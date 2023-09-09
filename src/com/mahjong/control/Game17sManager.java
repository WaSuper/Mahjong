package com.mahjong.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mahjong.common.MjCalcTool;
import com.mahjong.data.jpn.ScoreSystem;
import com.mahjong.item.ResultList;
import com.mahjong.model.MjAction;
import com.mahjong.model.MjDetail;
import com.mahjong.model.MjResult;
import com.mahjong.model.Player;

public class Game17sManager extends BaseManager {
	
	public Game17sManager() {
		super();
		FILE_INTERRUPT = "last_game_17step.tmp";
		mMainType = MainType_17s;
	}
	
	/**
	 * 设置玩家人数
	 * 
	 * @param count 玩家人数
	 * @param score 基础分值
	 * @param maxfeng 最大场风(从0开始计数)
	 */
	@Override
	public void setBaseData(int count, int score, int maxfeng) {
		this.mMemberCount = count;
		this.mBaseScore = score;
		this.mMaxFeng = maxfeng;
		Arrays.fill(mScores, score);
		Arrays.fill(mChickens, true);
		mJuCount = new int[maxfeng + 1];
		Arrays.fill(mJuCount, 0);
	}
		
	/**
	 * 分析流局满贯的分值变化
	 * 
	 * @param isManGuans
	 * @return
	 */
	@Override
	public int[] analysisLiujumanguan(boolean[] isManGuans, ResultList[] resultLists) {
		int[] changeScores = {0, 0, 0, 0};
		int[] indexes;
		int[] dealerChangs;
		switch (mMemberCount) {
		case 2:
			indexes = new int[] {0, 2};
			dealerChangs = new int[] {12000, -12000};
			break;
		case 3:
			indexes = new int[] {0, 1, 2};
			dealerChangs = new int[] {12000, -6000};
			break;
		case 4:
		default:
			indexes = new int[] {0, 1, 2, 3};
			dealerChangs = new int[] {12000, -4000};
			break;
		}
		for (int i = 0; i < isManGuans.length; i++) {
			if (isManGuans[i]) { // 不分庄家闲家，统一按庄家分数扣			
				for (int j : indexes) {
					if (j != i) {
						changeScores[j] += dealerChangs[1];
						resultLists[j].addBase(dealerChangs[1]);
					} else {
						changeScores[j] += dealerChangs[0];
						resultLists[j].addBase(dealerChangs[0]);
					}
				}
			}
		}		
		int[] tingpai = analysisTingpaiResult(resultLists);
		for (int i = 0; i < changeScores.length; i++) {
			changeScores[i] += tingpai[i];
		}
		return changeScores;
	}
	
	/**
	 * 自摸计算
	 * 
	 * @param winIndex
	 * @param fan
	 * @param fu
	 * @param juCount
	 * @param roundCount
	 * @param lizhiCount
	 * @param baoId
	 * @return
	 */
	@Override
	public int[] analysisZimo(int winIndex, int fan, int fu,
			int juCount, int roundCount, int lizhiCount, 
			boolean isBao, int baoId, ResultList[] resultLists) {
		// 17步无自摸
		int[] changeScores = {0, 0, 0, 0};
		return changeScores;
	}
	
	/**
	 * 自摸计算（精简）
	 * 
	 * @param winIndex
	 * @return
	 */
	@Override
	public int[] analysisZimo(int winIndex, ResultList[] resultLists) {
		return analysisZimo(winIndex, getResultFan(winIndex), getResultFu(winIndex), 
				getJuCount(), getRoundCount(), getLizhiCount(), 
				false, -1, resultLists);
	}
	
	
	/**
	 * 设置玩家自摸
	 * 
	 * @param changeScores
	 * @param winIndex
	 * @param baoId
	 */
	@Override
	public void setZimo(int[] changeScores, int winIndex, 
			String spectrum, int fan, int fu, int env, boolean isBao, int baoId) {
		for (int i = 0; i < 4; i++) {
			mScores[i] += changeScores[i];
		}
		MjAction action = MjAction.createZimoAction(getPlayer(winIndex).getUuid(), 
				spectrum, fan, fu, env, false, "",
				MjCalcTool.getSpecialYakusEnable2String());
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), getLizhiCount(), 
				changeScores[0], mScores[0], changeScores[1], mScores[1], 
				changeScores[2], mScores[2], changeScores[3], mScores[3], 
				"", "", action);
		addDetail(detail);
		mLizhiCount = 0; // 总立直数归零
		if (mChickens[winIndex]) { // 取消烧鸡状态
			mChickens[winIndex] = false;
		}
		if (checkPlayerFly()) {
			if (mListener != null) 
				mListener.onFinishByPlayerFly();
			return;
		}
		if (winIndex == getDealer()) { // 庄家听牌，连庄
			mRoundCount++; // 本场数+1
			continueRound();
		} else { // 庄家无听牌，流庄
			mRoundCount = 0; // 本场数归零
			nextRound();
		}
		saveStatesForTmp();
	}
	
	/**
	 * 设置玩家自摸（精简）
	 * 
	 * @param changeScores
	 * @param winIndex
	 */
	@Override
	public void setZimo(int[] changeScores, int winIndex) {
		setZimo(changeScores, winIndex, getResultSpectrum(winIndex), 
					getResultFan(winIndex), getResultFu(winIndex),
					getResultEnvironment(winIndex), false, -1);
	}
	
	/**
	 * 放铳计算
	 * 
	 * @param bombIndex
	 * @param winIndexs
	 * @param fan
	 * @param fu
	 * @param juCount
	 * @param roundCount
	 * @param lizhiCount
	 * @return
	 */
	public int[] analysisBomb(int bombIndex, int[] winIndexs, int[] fan, int[] fu,
			int juCount, int roundCount, int lizhiCount, boolean[] isBaos, int[] baoIds, 
			ResultList[] resultLists) {
		int[] changeScores = {0, 0, 0, 0};		
		for (int i = 0; i < winIndexs.length; i++) {
			int basePoint = ScoreSystem.GetBasePoint(fan[i], fu[i], mManguanUp);
			int index = winIndexs[i];
			if (index == bombIndex) continue; // 无此情况
			// 全部按点庄家付点数
			int playerPay = ScoreSystem.All_Pay_Dealer_Ronghe(basePoint);
			changeScores[index] += playerPay;
			resultLists[index].addBase(playerPay);
			// 一般情况
			changeScores[bombIndex] -= playerPay;	
			resultLists[bombIndex].addBase(-playerPay);
		}
		// 本场数点炮者付
		changeScores[bombIndex] -= (roundCount * 100 * (mMemberCount - 1));
		resultLists[bombIndex].addRound(-roundCount * 100 * (mMemberCount - 1));
		// 立直棒和本场数归属		
		int near;
		if (mLizhiBelong == 0) {
			near = (bombIndex + 1) % 4; // 离点炮者最近
		} else {
			near = getDealer(); // 离庄家最近
		}
		boolean isDone = false;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < winIndexs.length; j++) {
				if (winIndexs[j] == near) {
					changeScores[near] += (lizhiCount * 1000 + roundCount * 100 * (mMemberCount - 1));
					resultLists[near].addLizhi(lizhiCount * 1000).addRound(roundCount * 100 * (mMemberCount - 1));			
					isDone = true;
					break;
				}
			}
			if (isDone) break;
			near = (near + 1) % 4;
		}
		return changeScores;
	}
	
	/**
	 * 放铳计算（精简）
	 * 
	 * @param bombIndex
	 * @param winIndexs
	 * @return
	 */
	@Override
	public int[] analysisBomb(int bombIndex, int[] winIndexs, ResultList[] resultLists) {
		return analysisBomb(bombIndex, winIndexs, 
				getResultFans(winIndexs), getResultFus(winIndexs), 
				getJuCount(), getRoundCount(), getLizhiCount(),
				null, null, resultLists);
	}
	
	/**
	 * 设置玩家放铳
	 * 
	 * @param changeScores
	 * @param bombIndex
	 * @param winIndexs
	 * @param spectrums
	 * @param fans
	 * @param fus
	 * @param isBaos
	 * @param baoIndexs
	 */
	@Override
	public void setBomb(int[] changeScores, int bombIndex, int[] winIndexs, 
			String[] spectrums, int[] fans, int[] fus, int[] envs,
			boolean[] isBaos, int[] baoIndexs) {
		for (int i = 0; i < 4; i++) {
			mScores[i] += changeScores[i];
		}
		String[] winIds = new String[winIndexs.length];
		for (int i = 0; i < winIndexs.length; i++) {
			winIds[i] = getPlayer(winIndexs[i]).getUuid();
		}
		MjAction action = MjAction.createBombAction(getPlayer(bombIndex).getUuid(), 
				winIndexs.length, winIds, spectrums, fans, fus, envs, 0, null,
				MjCalcTool.getSpecialYakusEnable2String());
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), getLizhiCount(), 
				changeScores[0], mScores[0], changeScores[1], mScores[1], 
				changeScores[2], mScores[2], changeScores[3], mScores[3], 
				"", "", action);
		addDetail(detail);
		mLizhiCount = 0; // 总立直数归零
		boolean hasDealer = false;
		for (int i = 0; i < winIndexs.length; i++) {
			int winIndex = winIndexs[i];
			if (mChickens[winIndex]) { // 取消烧鸡状态
				mChickens[winIndex] = false;
			}
			if (winIndex == getDealer()) {
				hasDealer = true;
			}
		}		
		if (checkPlayerFly()) {
			if (mListener != null) 
				mListener.onFinishByPlayerFly();
			return;
		}
		if (hasDealer) { // 庄家听牌，连庄
			mRoundCount++; // 本场数+1
			continueRound();
		} else { // 庄家无听牌，流庄
			mRoundCount = 0; // 本场数归零
			nextRound();
		}
		saveStatesForTmp();
	}
	
	/**
	 * 设置玩家放铳（精简）
	 * 
	 * @param changeScores
	 * @param bombIndex
	 * @param winIndexs
	 */
	@Override
	public void setBomb(int[] changeScores, int bombIndex, int[] winIndexs) {
		setBomb(changeScores, bombIndex, winIndexs, 
				getResultSpectrums(winIndexs), 
				getResultFans(winIndexs), getResultFus(winIndexs), 
				getResultEnvironments(winIndexs), null, null);
	}
		
	/**
	 * 连庄
	 * 
	 */
	@Override
	public int continueRound() {
		int result = Result_None;
		for (int i = 0; i < 4; i++) {
			mSpectrums[i].resetByNewRound();
		}
		mDoraIndicaOut.clear();
		mDoraIndicaIn.clear();
		if (mFengCount > mMaxFeng && !isAllLower()) {
			result = Result_Finish_All;
			if (mListener != null) mListener.onFinishAll(); // 结束游戏
		} else {
			if (mFengCount == mMaxFeng && mJuCount[mFengCount] == (mMemberCount - 1)) {
				boolean isMax = true;
				for (int i = 0; i < mMemberCount - 1; i++) {
					if (mScores[i] > mScores[mMemberCount - 1]) {
						isMax = false;
						break;
					}
				}
				if (isMax && !mFinalWinnerUnlimited) {
					result = Result_Finish_All;
					if (mListener != null) mListener.onFinishAll(); // 结束游戏
				}
			}
		}
		return result;
	}
	
	/**
	 * 流庄
	 * 
	 */
	@Override
	public int nextRound() {
		int result = Result_None;
		for (int i = 0; i < 4; i++) {
			mSpectrums[i].resetByNewRound();
		}
		mDoraIndicaOut.clear();
		mDoraIndicaIn.clear();
		if (mJuCount[mFengCount] >= (mMemberCount - 1)) {
			mFengCount++; // 场风+1
			if (mFengCount > mMaxFeng) {
				// 结束游戏				
				mFengCount--; // 回退场风，不然会崩溃
				result = Result_Finish_All;
				if (mListener != null) mListener.onFinishAll();				
			}
		} else {
			if (mFengCount > mMaxFeng && !isAllLower()) {
				result = Result_Finish_All;
				if (mListener != null) mListener.onFinishAll(); // 结束游戏
			} else {
				mJuCount[mFengCount]++; // 局数+1
			}			
		}
		return result;
	}
	
	/**
	 * 判断是否符合南入\西入
	 * 
	 */
	@Override
	protected boolean isAllLower() {
		return false;
	}
	
	/**
	 * 复原到上一局状态
	 * 
	 * @return 立直状态
	 */
	@Override
	public int[] revokeLastRound() {
		MjDetail detail = removeLastDetail();
		int[] orgScores = detail.getOriginalScores();
		int lizhiCount = detail.getLizhiCount();
		int roundCount = detail.getRoundCount();
		int juCount = detail.getJuCount();
		
		mRoundCount = roundCount; // 复原本场数
		mLizhiCount = lizhiCount; // 复原立直数 
		for (int i = 0; i < 4; i++) { // 复原分值
			mScores[i] = orgScores[i];
		}
		mFengCount = juCount / mMemberCount; // 场风
		mJuCount[mFengCount] = juCount % mMemberCount;  // 复原庄家
		for (int i = mFengCount + 1; i < mJuCount.length; i++) { // 将大于当前场风的清零
			mJuCount[i] = 0;
		}
		for (int i = 0; i < 4; i++) { // 清空牌谱信息
			mSpectrums[i].resetByNewRound();
		}
		mDoraIndicaOut.clear(); // 清空宝牌指示牌
		mDoraIndicaIn.clear(); // 清空里宝牌指示牌
		// 复原玩家立直状态
		int[] lizhis = revokeLizhiState();
		// 设定玩家上局烧鸡状态
		Arrays.fill(mChickens, true);
		checkChickenState();
		
		saveStatesForTmp();
		
		return lizhis;
	}
	
	/**
	 * 复原玩家立直状态
	 * 
	 * @return 立直状态
	 */
	@Override
	protected int[] revokeLizhiState() {
		int[] lizhis = {0, 0, 0, 0};
		for (int i = mDetails.size() - 1; i >= 0; i--) {
			MjDetail tmpDetail = mDetails.get(i);
			if (!tmpDetail.isLizhi()) break;
			MjAction action = tmpDetail.getAction();
			int index = findPlayerOrgIndexById(action.id0);
			// 设定玩家上局立直状态
			lizhis[index] = action.tag1 > 0 ? 2 : 1;
			mSpectrums[index].mLiZhi = lizhis[index];
		}		
		return lizhis;
	}
	
	/**
	 * 临时保存当前所有操作
	 * 
	 */
	@Override
	public void saveStatesForTmp() {
		try {
			File tmpFile = new File(mTmpFileDir, FILE_INTERRUPT);
			if (!tmpFile.exists()) tmpFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile, false));
			JSONObject json = new JSONObject();
			json.put("save_type", TAG_INTERRUPT);
			json.put("member_count", mMemberCount);
			json.put("base_score", mBaseScore);
			json.put("scores", mScores[0] + "," + mScores[1] + "," + mScores[2] + "," + mScores[3]);
			json.put("max_feng", mMaxFeng);
			json.put("feng_count", mFengCount);
			String tmpJuCount = mJuCount[0] + "";
			for (int i = 1; i < mJuCount.length; i++) {
				tmpJuCount += ("," + mJuCount[i]);
			}
			json.put("ju_count", tmpJuCount);
			json.put("lizhi_count", mLizhiCount);
			json.put("round_count", mRoundCount);
			String playerString;
			switch (mMemberCount) {
			case 2:
				playerString = mPlayers[0].getUuid() + "," + mPlayers[2].getUuid();
				break;
			case 3:
				playerString = mPlayers[0].getUuid() + "," + mPlayers[1].getUuid() + "," 
						+ mPlayers[2].getUuid();
				break;
			case 4:
			default:
				playerString = mPlayers[0].getUuid() + "," + mPlayers[1].getUuid() + "," 
						+ mPlayers[2].getUuid() + "," + mPlayers[3].getUuid();
				break;
			}
			json.put("players", playerString);
			json.put("lizhi_belong", mLizhiBelong);
			json.put("ma_points", mMaPoints[0] + "," + mMaPoints[1] + "," + mMaPoints[2] + "," + mMaPoints[3]);
			json.put("start_time", mResult.getStartTime());
			json.put("note", note != null ? note : "");
			json.put("ret_point", mRetPoint);
			json.put("double_wind_4", isDoubleWind4);
			json.put("feng_type", mFengType);
			json.put("fanfu_type", mFanfuType);
			json.put("manguan_up", mManguanUp);
			json.put("no_fly", mNoFly);
			json.put("final_winner_unlimited", mFinalWinnerUnlimited);
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < mDetails.size(); i++) {
				MjDetail detail = mDetails.get(i);
				JSONObject object = new JSONObject();
				object.put("start_time", detail.getStartTime());
				object.put("log_time", detail.getLogTime());
				object.put("ju_count", detail.getJuCount());
				object.put("round_count", detail.getRoundCount());
				object.put("lizhi_count", detail.getLizhiCount());
				int[] changeScores = detail.getChangeScores();
				object.put("change_scores", changeScores[0] + "," + changeScores[1] 
						+ "," + changeScores[2] + "," + changeScores[3]);
				int[] finalScores = detail.getFinalScores();
				object.put("final_scores", finalScores[0] + "," + finalScores[1] 
						+ "," + finalScores[2] + "," + finalScores[3]);
				object.put("action_id", detail.getActionId());
				object.put("dora_out", detail.getDoraOut());
				object.put("dora_in", detail.getDoraIn());
				object.put("action_text", detail.getActionText());
				jsonArray.put(object);
			}
			json.put("details", jsonArray);
			bw.write(json.toString());
            bw.flush();
            bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 恢复上次中断信息
	 * 
	 * @return
	 */
	@Override
	public boolean restoreStatesForTmp() {
		File tmpFile = new File(mTmpFileDir, FILE_INTERRUPT);
		if (tmpFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(tmpFile));
				int len = -1;
		        char[] buffer = new char[1024];
		        StringBuffer sb = new StringBuffer();
		        while ((len = br.read(buffer)) != -1) {
		        	sb.append(buffer, 0, len);
		        }
				br.close();
		        JSONObject json = new JSONObject(sb.toString());
		        String save_type = json.optString("save_type");
		        if (save_type != null && save_type.equals(TAG_INTERRUPT)) {
		        	mMemberCount = json.getInt("member_count");
					mBaseScore = json.getInt("base_score");
					String scoreText = json.getString("scores");
					String[] scores = scoreText.split(",");
					if (scores.length == 4) {
						for (int i = 0; i < scores.length; i++) {
							mScores[i] = Integer.parseInt(scores[i]);
						}
					} else return false;
					mMaxFeng = json.getInt("max_feng");
					mFengCount = json.getInt("feng_count");
					String juText = json.getString("ju_count");
					String[] jus = juText.split(",");
					mJuCount = new int[jus.length];
					for (int i = 0; i < jus.length; i++) {
						mJuCount[i] = Integer.parseInt(jus[i]);
					}
					mLizhiCount = json.getInt("lizhi_count");
					mRoundCount = json.getInt("round_count");
					List<Player> pList = Player.getAllPlayer();
					Player[] mNPCs = Player.getNPCPlayers();
					String playerText = json.getString("players");
					String[] players = playerText.split(",");
					if (players.length == mMemberCount) {
						for (int i = 0; i < mMemberCount; i++) {
							String playerUuid = players[i];
							if (mMemberCount == 2 && i == 1) {
								i = 2;
								mPlayers[1] = Player.createNullPlayer();
								mPlayers[3] = Player.createNullPlayer();
							} else if (mMemberCount == 3 && i == 2) {
								mPlayers[3] = Player.createNullPlayer();
							}
							for (Player p : pList) {
								if (p.getUuid().equals(playerUuid)) {
									mPlayers[i] = p;
									break;
								}
							}
							for (Player p : mNPCs) {
								if (p.getUuid().equals(playerUuid)) {
									mPlayers[i] = p;
									break;
								}
							}
						}
					} else return false;
					mLizhiBelong = json.getInt("lizhi_belong");
					String maText = json.getString("ma_points");
					String[] mas = maText.split(",");
					if (mas.length == 4) {
						for (int i = 0; i < mas.length; i++) {
							mMaPoints[i] = Integer.parseInt(mas[i]);
						}
					} else return false;
					long result_time = json.getLong("start_time");
					note = json.optString("note", "");
					mRetPoint = json.optInt("ret_point", 5000);
					isDoubleWind4 = json.optBoolean("double_wind_4", false);
					mFengType = json.optInt("feng_type", 0);
					mFanfuType = json.optInt("fanfu_type", 0);
					mManguanUp = json.optBoolean("manguan_up", false);
					mNoFly = json.optBoolean("no_fly", false);
					mFinalWinnerUnlimited = json.optBoolean("final_winner_unlimited", false);
					JSONArray jsonArray = json.getJSONArray("details");
					mDetails.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = (JSONObject) jsonArray.get(i);
						long start_time = object.getLong("start_time");
						long log_time = object.getLong("log_time");
						int ju_count = object.getInt("ju_count");
						int round_count = object.getInt("round_count");
						int lizhi_count = object.getInt("lizhi_count");
						String changeText = object.getString("change_scores");
						String[] changes = changeText.split(",");
						int[] changeScores = new int[4];
						if (changes.length == 4) {
							for (int j = 0; j < changes.length; j++) {
								changeScores[j] = Integer.parseInt(changes[j]);
							}
						} else return false;
						String finalText = object.getString("final_scores");
						String[] finals = finalText.split(",");
						int[] finalScores = new int[4];
						if (finals.length == 4) {
							for (int j = 0; j < finals.length; j++) {
								finalScores[j] = Integer.parseInt(finals[j]);
							}
						} else return false;
						int action_id = object.getInt("action_id");
						String dora_out = object.getString("dora_out");
						String dora_in = object.getString("dora_in");
						String action_text = object.getString("action_text");
						MjDetail detail = new MjDetail(start_time, log_time, ju_count, round_count, lizhi_count, 
								changeScores[0], finalScores[0], changeScores[1], finalScores[1], 
								changeScores[2], finalScores[2], changeScores[3], finalScores[3], 
								dora_out, dora_in, action_id, action_text);
						mDetails.add(detail);
					}
					Collections.sort(mDetails, new Comparator<MjDetail>() {

						@Override
						public int compare(MjDetail d0, MjDetail d1) {
							if (d0.getLogTime() > d1.getLogTime()) {
								return 1;
							} else if  (d0.getLogTime() < d1.getLogTime()) {
								return -1;
							} else {
								return 0;
							}
						}
					});
					mResult = new MjResult(mMaxFeng, mBaseScore, mMaPoints, result_time, 
							getPlayer(0).getUuid(), getPlayer(0).getNickName(), 
							getPlayer(1).getUuid(), getPlayer(1).getNickName(), 
							getPlayer(2).getUuid(), getPlayer(2).getNickName(), 
							getPlayer(3).getUuid(), getPlayer(3).getNickName(),
							mRetPoint, mMemberCount, MainType_17s, getExtraData());
					mDoraIndicaOut.clear();
					mDoraIndicaIn.clear();
					for (int i = 0; i < 4; i++) {
						mSpectrums[i].reset();
					}
					revokeLizhiState();
					checkChickenState();
					return true;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}	
		return false;
	}
		
}

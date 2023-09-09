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

public class Game3pManager extends BaseManager {
			
	public Game3pManager() {
		super();
		FILE_INTERRUPT = "last_game_3p.tmp";
		mMainType = MainType_3p;
	}
	
	/**
	 * 设置玩家人数
	 * 
	 * @param count 玩家人数
	 * @param score 基础分值
	 * @param maxfeng 最大场风
	 */
	@Override
	public void setBaseData(int count, int score, int maxfeng) {
		this.mMemberCount = 3;
		this.mBaseScore = score;
		Arrays.fill(mScores, score);
		this.mMaxFeng = maxfeng;
		Arrays.fill(mChickens, true);
		this.mJuCount = new int[4];
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
		int[] dealerChanges;
		int[] playerChanges;
		if (mEnableZimoCut) { // 根据自摸损判断分值
			dealerChanges = new int[] {8000, -4000};
			playerChanges = new int[] {6000, -4000, -2000};
		} else {
			dealerChanges = new int[] {12000, -6000};
			playerChanges = new int[] {8000, -5000, -3000};
		}
		for (int i = 0; i < isManGuans.length; i++) {
			if (isManGuans[i]) {				
				if (getDealer() == i) { // 庄家满贯
					for (int j = 0; j < 3; j++) {
						if (j != i) {
							changeScores[j] += dealerChanges[1];
							resultLists[j].addBase(dealerChanges[1]);
						} else {
							changeScores[j] += dealerChanges[0];
							resultLists[j].addBase(dealerChanges[0]);
						}
					}
				} else { // 闲家满贯
					for (int j = 0; j < 3; j++) {
						if (j != i) {
							if (getDealer() == j) {
								changeScores[j] += playerChanges[1];
								resultLists[j].addBase(playerChanges[1]);
							} else {
								changeScores[j] += playerChanges[2];
								resultLists[j].addBase(playerChanges[2]);
							}							
						} else {
							changeScores[j] += playerChanges[0];
							resultLists[j].addBase(playerChanges[0]);
						}
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
		int[] changeScores = {0, 0, 0, 0};
		boolean isDealer = winIndex == juCount;
		int basePoint = ScoreSystem.GetBasePoint(fan, fu, mManguanUp);
		int yiman = 0;
		if (fan < 0) yiman = -fan;
		else if (fan >= 13) yiman = fan / 13;
		if (isDealer) { // 庄家自摸
			int playerPay = ScoreSystem.Player_Pay_Dealer_Zimo(basePoint);
			if (!mEnableZimoCut) { // 无自摸损，将第4人应付的分值平分给其他2人
				playerPay = ScoreSystem.GetPoint(playerPay, 1.5);
			}
			changeScores[winIndex] += (playerPay * 2 
					+ roundCount * 200 + lizhiCount * 1000);
			resultLists[winIndex].addBase(playerPay * 2)
					.addRound(roundCount * 200).addLizhi(lizhiCount * 1000);
			if (yiman > 0 && isBao && baoId >= 0) { // 役满有包牌
				if (yiman == 1) { // 普通役满：包牌者付全部（包括本场数）
					changeScores[baoId] -= (playerPay * 2 
							+ roundCount * 200);
					resultLists[baoId].addBaopai(-playerPay * 2).addRound(-roundCount * 200);
				} else { // 复合役满：包牌者付一个役满，剩余再平分，本场数两家付
					int yimanScore = ScoreSystem.Player_Pay_Dealer_Zimo(ScoreSystem.GetBasePoint(13, 0, mManguanUp));
					if (!mEnableZimoCut) { // 无自摸损，付3人份的分值
						yimanScore *= 3;
					} else { // 自摸损，付2人份的分值
						yimanScore *= 2;
					}
					changeScores[baoId] -= yimanScore;
					resultLists[baoId].addBaopai(-yimanScore);
					playerPay = (playerPay * 2 - yimanScore) / 2;
					for (int i = 0; i < 3; i++) {
						if (i == winIndex) { // 庄家赢家
							continue;
						} else { // 闲家
							changeScores[i] -= (playerPay + roundCount * 100);
							resultLists[i].addBase(-playerPay).addRound(-roundCount * 100);
						}
					}
				}				
			} else { // 一般情况
				for (int i = 0; i < 3; i++) {
					if (i == winIndex) { // 庄家赢家
						continue;
					} else { // 闲家
						changeScores[i] -= (playerPay + roundCount * 100);
						resultLists[i].addBase(-playerPay).addRound(-roundCount * 100);
					}
				}
			}
		} else { // 闲家自摸
			int dealerPay = ScoreSystem.Dealer_Pay_Player_Zimo(basePoint);
			int playerPay = ScoreSystem.Player_Pay_Player_Zimo(basePoint);
			if (!mEnableZimoCut) { // 无自摸损，将第4人应付的分值平分给其他2人
				int average = ScoreSystem.GetPoint(playerPay, 0.5);
				dealerPay += average;
				playerPay += average;
			}
			changeScores[winIndex] += (dealerPay + playerPay
					+ roundCount * 200 + lizhiCount * 1000);
			resultLists[winIndex].addBase(dealerPay + playerPay)
					.addRound(roundCount * 200).addLizhi(lizhiCount * 1000);
			if (yiman > 0 && isBao && baoId >= 0) { // 役满有包牌
				if (yiman == 1) { // 普通役满：包牌者付全部（包括本场数）
					changeScores[baoId] -= (dealerPay + playerPay
							+ roundCount * 200);
					resultLists[baoId].addBaopai(-dealerPay - playerPay)
							.addRound(-roundCount * 200);
				} else { // 复合役满：包牌者付一个役满，剩余的庄家付1/2，闲家付1/4，本场数两家付
					int tmpBaseScore = ScoreSystem.GetBasePoint(13, 0, mManguanUp);
					int tmpDealerPay = ScoreSystem.Dealer_Pay_Player_Zimo(tmpBaseScore);
					int tmpPlayerPay = ScoreSystem.Player_Pay_Player_Zimo(tmpBaseScore);
					int yimanScore;
					if (!mEnableZimoCut) { // 无自摸损
						yimanScore = tmpDealerPay + tmpPlayerPay * 2;
					} else { // 自摸损
						yimanScore = tmpDealerPay + tmpPlayerPay;
					}
					changeScores[baoId] -= yimanScore;
					resultLists[baoId].addBaopai(-yimanScore);
					int tmpPay = (dealerPay + playerPay - yimanScore);
					if (!mEnableZimoCut) { // 无自摸损
						tmpPay /= 8;
						dealerPay = tmpPay * 5;
						playerPay = tmpPay * 3;
					} else { // 自摸损
						tmpPay /= 3;
						dealerPay = tmpPay * 2;
						playerPay = tmpPay;
					}
					for (int i = 0; i < 3; i++) {
						if (i == winIndex) { // 闲家赢家
							continue;
						} else if (i == juCount) { // 庄家
							changeScores[i] -= (dealerPay + roundCount * 100);
							resultLists[i].addBase(dealerPay).addRound(-roundCount * 100);
						} else { // 闲家
							changeScores[i] -= (playerPay + roundCount * 100);
							resultLists[i].addBase(-playerPay).addRound(-roundCount * 100);
						}
					}
				}
			} else { // 一般情况
				for (int i = 0; i < 3; i++) {
					if (i == winIndex) { // 闲家赢家
						continue;
					} else if (i == juCount) { // 庄家
						changeScores[i] -= (dealerPay + roundCount * 100);
						resultLists[i].addBase(-dealerPay).addRound(-roundCount * 100);
					} else { // 闲家
						changeScores[i] -= (playerPay + roundCount * 100);
						resultLists[i].addBase(-playerPay).addRound(-roundCount * 100);
					}
				}	
			}
		}
		
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
				getEnableBaopai(winIndex), getBaopaiPlayer(winIndex), resultLists);
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
				spectrum, fan, fu, env, 
				isBao, (isBao && baoId >= 0) ? getPlayer(baoId).getUuid() : "",
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
					getResultEnvironment(winIndex),
					getEnableBaopai(winIndex), getBaopaiPlayer(winIndex));
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
	@Override
	public int[] analysisBomb(int bombIndex, int[] winIndexs, int[] fan, int[] fu,
			int juCount, int roundCount, int lizhiCount, boolean[] isBaos, int[] baoIds,
			ResultList[] resultLists) {
		int[] changeScores = {0, 0, 0, 0};		
		for (int i = 0; i < winIndexs.length; i++) {
			int basePoint = ScoreSystem.GetBasePoint(fan[i], fu[i], mManguanUp);
			int index = winIndexs[i];
			int yiman = 0;
			if (fan[i] < 0) yiman = -fan[i];
			else if (fan[i] >= 13) yiman = fan[i] / 13;
			if (index == bombIndex) continue; // 无此情况
			else if (index == juCount) { // 闲家放铳庄家
				int playerPay = ScoreSystem.All_Pay_Dealer_Ronghe(basePoint);
				changeScores[index] += playerPay;
				resultLists[index].addBase(playerPay);
				if (yiman > 0 && isBaos[i] && baoIds[i] >= 0 && baoIds[i] != bombIndex) { // 役满有包牌
					if (yiman == 1) { // 普通役满：包牌者付一半
						changeScores[baoIds[i]] -= (playerPay / 2); 
						changeScores[bombIndex] -= (playerPay / 2);
						resultLists[baoIds[i]].addBaopai(-playerPay / 2);
						resultLists[bombIndex].addBase(-playerPay / 2);
					} else { // 复合役满：包牌者付一个役满，剩余的点炮者付
						int yimanScore = ScoreSystem.All_Pay_Dealer_Ronghe(ScoreSystem.GetBasePoint(13, 0, mManguanUp));
						changeScores[baoIds[i]] -= yimanScore;
						changeScores[bombIndex] -= (playerPay - yimanScore);
						resultLists[baoIds[i]].addBaopai(-yimanScore);
						resultLists[bombIndex].addBase(-playerPay + yimanScore);
					}
				} else { // 一般情况
					changeScores[bombIndex] -= playerPay;	
					resultLists[bombIndex].addBase(-playerPay);	
				}
			} else { // 庄、闲家放铳闲家
				int pay = ScoreSystem.All_Pay_Player_Ronghe(basePoint);
				changeScores[index] += pay;
				resultLists[index].addBase(pay);
				if (yiman > 0 && isBaos[i] && baoIds[i] >= 0 && baoIds[i] != bombIndex) { // 役满有包牌
					if (yiman == 1) { // 普通役满：包牌者付一半
						changeScores[baoIds[i]] -= (pay / 2); 
						changeScores[bombIndex] -= (pay / 2);
						resultLists[baoIds[i]].addBaopai(-pay / 2);
						resultLists[bombIndex].addBase(-pay / 2);
					} else { // 复合役满：包牌者付一个役满，剩余的点炮者付
						int yimanScore = ScoreSystem.All_Pay_Player_Ronghe(ScoreSystem.GetBasePoint(13, 0, mManguanUp));
						changeScores[baoIds[i]] -= yimanScore;
						changeScores[bombIndex] -= (pay - yimanScore);
						resultLists[baoIds[i]].addBaopai(-yimanScore);
						resultLists[bombIndex].addBase(-pay + yimanScore);
					}
				} else { // 一般情况
					changeScores[bombIndex] -= pay;
					resultLists[bombIndex].addBase(-pay);
				}
			}
		}
		// 本场数点炮者付
		changeScores[bombIndex] -= (roundCount * 200);
		resultLists[bombIndex].addRound(-roundCount * 200);
		// 立直棒和本场数归属		
		int near;
		if (mLizhiBelong == 0) {
			near = (bombIndex + 1) % 3; // 离点炮者最近
		} else {
			near = getDealer(); // 离庄家最近
		}
		boolean isDone = false;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < winIndexs.length; j++) {
				if (winIndexs[j] == near) {
					changeScores[near] += (lizhiCount * 1000 + roundCount * 200);
					resultLists[near].addLizhi(lizhiCount * 1000).addRound(roundCount * 200);					
					isDone = true;
					break;
				}
			}
			if (isDone) break;
			near = (near + 1) % 3;
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
				getEnableBaopais(winIndexs), getBapaiPlayers(winIndexs),
				resultLists);
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
		String[] baoIds = new String[winIndexs.length];
		for (int i = 0; i < winIndexs.length; i++) {
			winIds[i] = getPlayer(winIndexs[i]).getUuid();	
			if (isBaos[i] && baoIndexs[i] >= 0) {
				baoIds[i] = getPlayer(baoIndexs[i]).getUuid();
			} else {
				baoIds[i] = "";
			}	
		}
		MjAction action = MjAction.createBombAction(getPlayer(bombIndex).getUuid(), 
				winIndexs.length, winIds, spectrums, fans, fus, envs, isBaos.length, baoIds,
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
				getResultEnvironments(winIndexs),
				getEnableBaopais(winIndexs), getBapaiPlayers(winIndexs));
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
			if (mFengCount == mMaxFeng && mJuCount[mFengCount] == 2) {
				boolean isMax = true;
				for (int i = 0; i < 2; i++) {
					if (mScores[i] > mScores[2]) {
						isMax = false;
						break;
					}
				}
				if (isEnterSW && isAllLower()) { // 继续南入\西入
					result = Result_Enter_S_W;
					if (mListener != null) mListener.onEnterSouthOrWest(mMaxFeng == 0);
				} else if (isMax && !mFinalWinnerUnlimited) {
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
		if (mJuCount[mFengCount] >= 2) {
			mFengCount++; // 场风+1
			if (mFengCount > mMaxFeng) {
				// 询问是否结束游戏				
				if (mMaxFeng < 2 && mFengCount == (mMaxFeng + 1)
						&& isEnterSW && isAllLower()) { // 继续南入\西入
					result = Result_Enter_S_W;
					if (mListener != null) mListener.onEnterSouthOrWest(mMaxFeng == 0);
				} else { // 结束游戏
					mFengCount--; // 回退场风，不然会崩溃
					result = Result_Finish_All;
					if (mListener != null) mListener.onFinishAll();
				}				
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
		boolean isAllLower = true;
		for (int i = 0; i < mScores.length; i++) {
			if (mScores[i] >= mBaseScore + 5000) {
				isAllLower = false;
				break;
			}
		}
		return isAllLower;
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
		mFengCount = juCount / 3; // 场风
		mJuCount[mFengCount] = juCount % 3;  // 复原庄家
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
			json.put("base_score", mBaseScore);
			json.put("scores", mScores[0] + "," + mScores[1] + "," + mScores[2] + "," + mScores[3]);
			json.put("max_feng", mMaxFeng);
			json.put("feng_count", mFengCount);
			json.put("ju_count", mJuCount[0] + "," + mJuCount[1] + "," + mJuCount[2] + "," + mJuCount[3]);
			json.put("lizhi_count", mLizhiCount);
			json.put("round_count", mRoundCount);
			json.put("players", mPlayers[0].getUuid() + "," + mPlayers[1].getUuid() + "," 
					+ mPlayers[2].getUuid());
			json.put("enter_sw", isEnterSW);
			json.put("lizhi_belong", mLizhiBelong);
			json.put("enable_fanfu", mEnableFanfu);
			json.put("ma_points", mMaPoints[0] + "," + mMaPoints[1] + "," + mMaPoints[2] + "," + mMaPoints[3]);
			json.put("start_time", mResult.getStartTime());
			json.put("note", note != null ? note : "");
			json.put("ret_point", mRetPoint);
			json.put("double_wind_4", isDoubleWind4);
			json.put("zimo_cut", mEnableZimoCut);
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
		        	mMemberCount = 3;
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
					if (jus.length == 4) {
						mJuCount = new int[4];
						for (int i = 0; i < jus.length; i++) {
							mJuCount[i] = Integer.parseInt(jus[i]);
						}						
					} else return false;
					mLizhiCount = json.getInt("lizhi_count");
					mRoundCount = json.getInt("round_count");
					List<Player> pList = Player.getAllPlayer();
					Player[] mNPCs = Player.getNPCPlayers();
					String playerText = json.getString("players");
					String[] players = playerText.split(",");
					if (players.length == 3) {
						for (int i = 0; i < players.length; i++) {
							String playerUuid = players[i];
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
					mPlayers[3] = Player.createNullPlayer(); // 创建空玩家，防止出错
					isEnterSW = json.getBoolean("enter_sw");
					mLizhiBelong = json.getInt("lizhi_belong");
					mEnableFanfu = json.getBoolean("enable_fanfu");
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
					mEnableZimoCut = json.optBoolean("zimo_cut", false);
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
							mRetPoint, mMemberCount, MainType_4p, getExtraData());
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

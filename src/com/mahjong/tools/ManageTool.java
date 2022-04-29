package com.mahjong.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.mahjong.common.MjCalcTool;
import com.mahjong.common.MjCard;
import com.mahjong.data.jpn.ScoreSystem;
import com.mahjong.item.ResultList;
import com.mahjong.model.MjAction;
import com.mahjong.model.MjDetail;
import com.mahjong.model.MjResult;
import com.mahjong.model.Player;

public class ManageTool {

	public static final String PLAYER_ITEM_POSITION = "PLAYER_ITEM_POSITION";
	public static final String PLAYER_IS_ZIMO = "IS_ZIMO";
	public static final String PLAYER_IS_BOMB = "PLAYER_IS_BOMB";
	public static final String PLAYER_ORIGINAL_INDEX = "PLAYER_ORIGINAL_INDEX";
	public static final String RESULT_FAN = "RESULT_FAN";
	public static final String RESULT_FU = "RESULT_FU";
	public static final String RESULT_BOMB_INDEX = "RESULT_BOMB_INDEX";
	
	public static final int Result_None 		= 0;
	public static final int Result_Finish_All 	= 1;
	public static final int Result_Player_Fly 	= 2;
	public static final int Result_Enter_S_W	= 3;
	
	private static ManageTool instance;

//	private int mLeftCount = -1; // 牌山剩余数量	
	private int mBaseScore = 25000; // 基本分
	private int[] mScores = {25000, 25000, 25000, 25000}; // 玩家分数
	private boolean[] mChickens = {true, true, true, true}; // 玩家烧鸡状态
	private int mMaxFeng = 1; // 最大场风（默认：半庄战）
	private int mFengCount = 0; // 场风计数：0-》东，1-》南，2-》西，3-》北
	private int[] mJuCount = {0, 0, 0, 0}; // 局数计数，对应各自场风，范围：0-3
	private int mLizhiCount = 0; // 场上累计立直棒数
	private int mRoundCount = 0; // 本场数
	private Player[] mPlayers = new Player[4]; // 4个玩家
	private SpectrumData[] mSpectrums = new SpectrumData[4]; // 4个玩家的牌谱
	private List<MjCard> mDoraIndicaOut = new ArrayList<MjCard>(); // 宝牌指示牌
	private List<MjCard> mDoraIndicaIn = new ArrayList<MjCard>(); // 里宝牌指示牌
	private Player[] mNPCs; // NPC玩家
	private boolean[] mNPCused; // NPC使用状态
	private MjResult mResult; // 最终结果
	private List<MjDetail> mDetails = new ArrayList<MjDetail>(); // 所有操作
	private OnManageToolListener mListener;
	private boolean isEnterSW = false; // 南入/西入
	private int mLizhiBelong = 0; // 多家和时立直棒归属
	private boolean mEnableFanfu; // 本场番缚
	private int[] mMaPoints = new int[4]; // 马点
	private String note; // 备注
	private int mRetPoint; // 返点-基础分
	
	private String mTmpFileDir; // 临时信息保存地址
	private static final String TAG_INTERRUPT = "interrupt";
	
	private ManageTool() {
		initData();
	}
	
	public static ManageTool getInstance() {
		if (instance == null) {
			instance = new ManageTool();
		}
		return instance;
	}
	
	/**
	 * 设置临时文件保存地址
	 * 
	 * @param dir
	 */
	public void init(String storeDir) {		
		this.mTmpFileDir = storeDir;
		Arrays.fill(mNPCused, false);
	}
	
	/**
	 * 获取备注信息
	 * 
	 * @return
	 */
	public String getNote() {
		return note != null ? note : "";
	}
	
	/**
	 * 设置备注信息
	 * 
	 * @param note
	 */
	public void setNote(String note) {
		this.note = note;
		saveStatesForTmp();
	}
	
	/**
	 * 获取基础分值
	 * 
	 * @return
	 */
	public int getBaseScore() {
		return mBaseScore;
	}
	
	/**
	 * 设置基础分值
	 * 
	 * @param score
	 */
	public void setBaseScore(int score) {
		mBaseScore = score;
		for (int i = 0; i < mScores.length; i++) {
			mScores[i] = score;
		}
	}
	
	/**
	 * 获取全部玩家的分值
	 * 
	 * @return
	 */
	public int[] getPlayerScores() {
		return mScores;
	}
	
	/**
	 * 获取玩家分值
	 * 
	 * @param index
	 * @return
	 */
	public int getPlayerScore(int index) {
		return mScores[index];
	}
	
	/**
	 * 设置马点
	 * 
	 * @param ma
	 */
	public void setMaPoint(int[] ma) {
		this.mMaPoints = ma;
	}
	
	/**
	 * 设置返点
	 * 
	 * @param ret
	 */
	public void setRetPoint(int ret) {
		this.mRetPoint = ret;
	}
	
	/**
	 * 设置是否南入\西入
	 * 
	 * @param enter
	 */
	public void setEnterSouthEast(boolean enter) {
		this.isEnterSW = enter;
	}
	
	/**
	 * 设置立直归属
	 * 
	 * @param belong
	 */
	public void setLiZhiBelong(int belong) {
		this.mLizhiBelong = belong;
	}
	
	/**
	 * 设置本场番缚
	 * 
	 * @param enable
	 */
	public void setEnableFanFu(boolean enable) {
		this.mEnableFanfu = enable;
	}
	
	/**
	 * 获取是否开启番缚
	 * 
	 * @return
	 */
	public boolean getEnableFanFu() {
		return mEnableFanfu;
	}
	
	/**
	 * 获取当前最大场风
	 * 
	 * @return
	 */
	public int getMaxFeng() {
		return mMaxFeng;
	}
	
	/**
	 * 设置当前最大场风
	 * 
	 * @param size
	 */
	public void setMaxFeng(int size) {
		this.mMaxFeng = size;
	}
	
	/**
	 * 获取场风
	 * 
	 * @return
	 */
	public int getFengCount() {
		return mFengCount;
	}
	
	/**
	 * 获取当前场风局数
	 * 
	 * @return
	 */
	public int getJuCount(){
		return mJuCount[mFengCount];
	}
	
	/**
	 * 获取总局数
	 * 
	 * @return
	 */
	public int getJuCountForAll() {
		return mFengCount * 4 + getJuCount();
	}
	
	/**
	 * 获取本场数
	 * 
	 * @return
	 */
	public int getRoundCount() {
		return mRoundCount;
	}
	
	/**
	 * 获取玩家烧鸡状态
	 * 
	 * @param index
	 * @return
	 */
	public boolean getChickenState(int index) {
		return mChickens[index];
	}
	
	/**
	 * 遍历所有操作，重设玩家烧鸡状态
	 * 
	 */
	public void checkChickenState() {
		int index;
		for (MjDetail detail : mDetails) {
			MjAction action = detail.getAction();
			switch (action.getActionID()) {
			case MjAction.ACTION_ZIMO:
				index = findPlayerOrgIndexById(action.id0);
				mChickens[index] = false;
				break;
			case MjAction.ACTION_BOMB:
				int count = action.tag1;
				index = findPlayerOrgIndexById(action.id0);
				mChickens[index] = false;
				if (count > 1) {
					index = findPlayerOrgIndexById(action.id1);
					mChickens[index] = false;
				}
				if (count > 2) {
					index = findPlayerOrgIndexById(action.id2);
					mChickens[index] = false;
				}
				break;
			default:
				break;
			}
			boolean isAllEnd = true;
			for (int i = 0; i < mChickens.length; i++) {
				if (mChickens[i]) {
					isAllEnd = false;
					break;
				}
			}
			if (isAllEnd) break;
		}
	}
	
	/**
	 * 获取立直棒数量
	 * 
	 * @return
	 */
	public int getLizhiCount() {
		return mLizhiCount;
	}
	
	/**
	 * 设置四个玩家立直状态(暂不用)
	 * 
	 * @param east
	 * @param south
	 * @param west
	 * @param north
	 */
	public void setPlayerLizhi(int east, int south, int west, int north) {
		mSpectrums[0].mLiZhi = east;
		mSpectrums[1].mLiZhi = south;
		mSpectrums[2].mLiZhi = west;
		mSpectrums[3].mLiZhi = north;
	}
	
	/**
	 * 设置单个玩家立直状态
	 * 
	 * @param index
	 * @param lizhi
	 * @param change
	 * @return
	 */
	public int setPlayerLizhi(int index, int lizhi) {
		mSpectrums[index].mLiZhi = lizhi;
		mLizhiCount += 1;
		mScores[index] -= 1000;
		int[] changeScores = {0, 0, 0, 0};
		changeScores[index] = -1000;
		MjAction action = MjAction.createLizhiAction(getPlayer(index).getUuid(), lizhi > 1);
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), getLizhiCount(), 
				changeScores[0], mScores[0], changeScores[1], mScores[1], 
				changeScores[2], mScores[2], changeScores[3], mScores[3], 
				"", "", action);
		addDetail(detail);
		saveStatesForTmp();
		return mLizhiCount;
	}
	
	/**
	 * 撤销玩家立直
	 * 
	 * @param index
	 * @return
	 */
	public int revokePlayerLizhi(int index) {
		mSpectrums[index].mLiZhi = 0;
		mLizhiCount -= 1;
		mScores[index] += 1000;
		removeLastDetail();
		saveStatesForTmp();
		return mLizhiCount;
	}
	
	/**
	 * 获取玩家立直状态
	 * 
	 * @param wind
	 * @return
	 */
	public int getPlayerLizhi(int wind) {
		return mSpectrums[wind].mLiZhi;
	}
	
	public int[] getPlayerLizhis() {
		return new int[] {mSpectrums[0].mLiZhi, mSpectrums[1].mLiZhi, 
				mSpectrums[2].mLiZhi, mSpectrums[3].mLiZhi};
	}
	
	/**
	 * 获取玩家是否立直
	 * 
	 * @param wind
	 * @return
	 */
	public boolean getPlayerIsLizhi(int wind) {
		return mSpectrums[wind].mLiZhi > 0;
	}
	
	/**
	 * 检测是否有玩家立直
	 * 
	 * @return
	 */
	public boolean checkHasLizhi() {
		for (int i = 0; i < mSpectrums.length; i++) {
			if (mSpectrums[i].mLiZhi > 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 设置玩家听牌状态
	 * 
	 * @param east
	 * @param south
	 * @param west
	 * @param north
	 */
	public void setPlayerTingPai(boolean east, boolean south, boolean west, boolean north) {
		mSpectrums[0].mTingPai = east;
		mSpectrums[1].mTingPai = south;
		mSpectrums[2].mTingPai = west;
		mSpectrums[3].mTingPai = north;
	}
	
	/**
	 * 获取玩家听牌状态
	 * 
	 * @param index
	 * @return
	 */
	public boolean getPlayerTingpai(int index) {
		return mSpectrums[index].mTingPai || mSpectrums[index].mLiZhi > 0;
	}
	
	public int[] analysisTingpaiResult(ResultList[] resultLists) {
		int count = 0;
		boolean[] ting = new boolean[4];
		for (int i = 0; i < 4; i++) {
			ting[i] = getPlayerTingpai(i);
			if (ting[i]) count++;
		}
		int[] change = new int[] {0, 0, 0, 0};
		if (count == 1) {
			for (int i = 0; i < 4; i++) {
				if (ting[i]) {
					change[i] += 3000;
					resultLists[i].addTingpai(3000);
				}
				else {
					change[i] -= 1000;
					resultLists[i].addTingpai(-1000);
				}
			}
		} else if (count == 2) {
			for (int i = 0; i < 4; i++) {
				if (ting[i]) {
					change[i] += 1500;
					resultLists[i].addTingpai(1500);
				}
				else {
					change[i] -= 1500;
					resultLists[i].addTingpai(-1500);
				}
			}			
		} else if (count == 3) {
			for (int i = 0; i < 4; i++) {
				if (ting[i]) {
					change[i] += 1000;
					resultLists[i].addTingpai(1000);
				}
				else {
					change[i] -= 3000;
					resultLists[i].addTingpai(-3000);
				}
			}	
		}
		return change;
	}
	
	/**
	 * 获取玩家牌谱
	 * 
	 * @param index
	 * @return
	 */
	public SpectrumData getSpectrumData(int index) {
		return mSpectrums[index];
	}
	
	/**
	 * 设置胡牌结果
	 * 
	 * @param index
	 * @param fan
	 * @param fu
	 */
	public void setResult(int index, int fan, int fu) {
		SpectrumData data = mSpectrums[index];
		data.mFan = fan;
		data.mFu = fu;
	}
	
	/**
	 * 设置胡牌结果
	 * 
	 * @param index
	 * @param fan
	 * @param fu
	 * @param spectrum
	 * @param env
	 */
	public void setResult(int index, int fan, int fu, String spectrum, int env) {
		SpectrumData data = mSpectrums[index];
		data.mFan = fan;
		data.mFu = fu;
		data.mSpectrum = spectrum;
		data.mEnv = env;
	}
	
	/**
	 * 获取玩家当局番数
	 * 
	 * @param index
	 * @return
	 */
	public int getResultFan(int index) {
		return mSpectrums[index].mFan;
	}
	
	/**
	 * 获取玩家当局番数数组
	 * 
	 * @param indexs
	 * @return
	 */
	public int[] getResultFans(int[] indexs) {
		int[] fan = new int[indexs.length];
		for (int i = 0; i < indexs.length; i++) {
			fan[i] = getResultFan(indexs[i]);
		}
		return fan;
	}
	
	/**
	 * 获取玩家当局符数
	 * 
	 * @param index
	 * @return
	 */
	public int getResultFu(int index) {
		return mSpectrums[index].mFu;
	}
	
	/**
	 * 获取玩家当局符数数组
	 * 
	 * @param indexs
	 * @return
	 */
	public int[] getResultFus(int[] indexs) {
		int[] fu = new int[indexs.length];
		for (int i = 0; i < indexs.length; i++) {
			fu[i] = getResultFu(indexs[i]);
		}
		return fu;
	}
	
	/**
	 * 获取玩家当局牌谱
	 * 
	 * @param index
	 * @return
	 */
	public String getResultSpectrum(int index) {
		return mSpectrums[index].mSpectrum;
	}
	
	/**
	 * 获取玩家当局牌谱数组
	 * 
	 * @param indexs
	 * @return
	 */
	public String[] getResultSpectrums(int[] indexs) {
		String[] spectrums = new String[indexs.length];
		for (int i = 0; i < indexs.length; i++) {
			spectrums[i] = getResultSpectrum(indexs[i]);
		}
		return spectrums;
	}
	
	/**
	 * 获取玩家当局环境变量
	 * 
	 * @param index
	 * @return
	 */
//	public int getResultEnvironment(int index, boolean isZimo) {
//		return mSpectrums[index].getEnvironment(isZimo, mFengCount, mJuCount[mFengCount]);
//	}
	public int getResultEnvironment(int index) {
		return mSpectrums[index].mEnv;
	}
	
	/**
	 * 获取玩家当局环境变量数组
	 * 
	 * @param index
	 * @return
	 */
//	public int[] getResultEnvironments(int[] indexs) {
//		int[] envs = new int[indexs.length];
//		for (int i = 0; i < indexs.length; i++) {
//			envs[i] = getResultEnvironment(indexs[i], false);
//		}
//		return envs;
//	}
	public int[] getResultEnvironments(int[] indexs) {
		int[] envs = new int[indexs.length];
		for (int i = 0; i < indexs.length; i++) {
			envs[i] = getResultEnvironment(indexs[i]);
		}
		return envs;
	}
	
	/**
	 * 获取对应包牌玩家
	 * 
	 * @param index
	 * @return
	 */
	public int getBaopaiPlayer(int index) {
		return mSpectrums[index].mBaopai;
	}
	
	/**
	 * 批量获取包牌玩家
	 * 
	 * @param indexs
	 * @return
	 */
	public int[] getBapaiPlayers(int[] indexs) {
		int[] bao = new int[indexs.length];
		for (int i = 0; i < bao.length; i++) {
			bao[i] = getBaopaiPlayer(indexs[i]);
		}
		return bao;
	}
	
	/**
	 * 设置对应包牌玩家
	 * 
	 * @param index
	 * @param baopaiId
	 */
	public void setBaopaiPlayer(int index, int baopaiId) {
		mSpectrums[index].mBaopai = baopaiId;
	}
	
	/**
	 * 获取是否开启玩家包牌
	 * 
	 * @param index
	 * @return
	 */
	public boolean getEnableBaopai(int index) {
		return mSpectrums[index].isEnableBaopai;
	}
	
	/**
	 * 批量获取是否开启玩家包牌
	 * 
	 * @param indexs
	 * @return
	 */
	public boolean[] getEnableBaopais(int[] indexs) {
		boolean[] enables = new boolean[indexs.length];
		for (int i = 0; i < indexs.length; i++) {
			enables[i] = getEnableBaopai(indexs[i]);
		}
		return enables;
	}
	
	/**
	 * 设置是否开启玩家包牌
	 * 
	 * @param index
	 * @param enable
	 */
	public void setEnableBaopai(int index, boolean enable) {
		mSpectrums[index].isEnableBaopai = enable;
	}
	
	/**
	 * 获取初始索引的玩家
	 * 
	 * @param index
	 * @return
	 */
	public Player getPlayer(int index) {
		return mPlayers[index];
	}
	
	/**
	 * 获取所有玩家
	 * 
	 * @return
	 */
	public Player[] getAllPlayer() {
		return mPlayers;
	}
	
	/**
	 * 根据主视觉与位置获取对应玩家初始索引
	 * 
	 * @param mainVision
	 * @param pos
	 * @return
	 */
	public int getPlayerIndexByPosition(int mainVision, int pos) {
		int index = 0;
		switch (pos) {
		case 0:
			index = mainVision;
			break;
		case 1:
			index = (1 + mainVision) % 4;
			break;
		case 2:
			index = (2 + mainVision) % 4;
			break;
		case 3:
			index = (3 + mainVision) % 4;
			break;
		default:
			break;
		}
		return index;
	}
	
	/**
	 * 获取初始索引的玩家在当前局数对应的风位
	 * 
	 * @param index
	 * @return
	 */
	public int getPlayerWind(int index) {
		int wind = (index + 4 - getDealer()) % 4;
		return wind;
	}
	
	/**
	 * 获取初始索引的玩家名字并带有风位
	 * 
	 * @param index
	 * @return
	 */	
	public String getPlayerNameWithWind(int index, String[] winds) {
		Player player = mPlayers[index];
		String name  = player.getNickName();
		int wind = getPlayerWind(index);
		name += ("(" + winds[wind] + ")");
		return name;
	}
	
	/**
	 * 设置四个玩家
	 * 
	 * @param east
	 * @param south
	 * @param west
	 * @param north
	 */
	public void setPlayers(Player east, Player south, Player west, Player north) {
		mPlayers[0] = createPlayerIfNull(east);
		mPlayers[1] = createPlayerIfNull(south);
		mPlayers[2] = createPlayerIfNull(west);
		mPlayers[3] = createPlayerIfNull(north);
	}
	
	public void changePlayer(int index, Player newPlayer) {
		Player oldPlayer = mPlayers[index];
		mPlayers[index] = newPlayer;
		mResult.changePlayer(index, newPlayer.getUuid(), newPlayer.getNickName());
		for (MjDetail detail : mDetails) {
			detail.changePlayer(oldPlayer.getUuid(), newPlayer.getUuid());
		}
		saveStatesForTmp();
	}
	
	/**
	 * 创建空玩家
	 * 
	 * @param player
	 * @return
	 */
	public Player createPlayerIfNull(Player player) {
		if (player != null) return player;
		int index;
		do {
			 index = (int)(Math.random() * Player.NPCsize);
		} while (mNPCused[index]);
		Player pc = mNPCs[index];
		mNPCused[index] = true;
		return pc;
	}
	
	/**
	 * 初始化数据
	 */
	public void initData() {
		createNPCs();
		resetData();
	}
	
	private void resetData() {
		mLizhiCount = 0;
		mRoundCount = 0;
//		mLeftCount = -1;
		for (int i = 0; i < mSpectrums.length; i++) {
			mSpectrums[i] = new SpectrumData(i);
			mSpectrums[i].reset();
		}
	}
	
	/**
	 * 初始化NPC玩家
	 */
	public void createNPCs() {
		mNPCs = Player.getNPCPlayers();
		mNPCused = new boolean[mNPCs.length];
		Arrays.fill(mNPCused, false);
	}
	
	/**
	 * 开始新游戏
	 * 
	 */
	public void startNewGame() {		
		mResult = new MjResult(mMaxFeng, mBaseScore, mMaPoints, 
				System.currentTimeMillis(), 
				getPlayer(0).getUuid(), getPlayer(0).getNickName(), 
				getPlayer(1).getUuid(), getPlayer(1).getNickName(), 
				getPlayer(2).getUuid(), getPlayer(2).getNickName(), 
				getPlayer(3).getUuid(), getPlayer(3).getNickName(),
				mRetPoint);
		mFengCount = 0;
		Arrays.fill(mJuCount, 0);
		mLizhiCount = 0;
		mRoundCount = 0;
		mDetails.clear();
		mDoraIndicaOut.clear();
		mDoraIndicaIn.clear();
		for (int i = 0; i < 4; i++) {
			mChickens[i] = true;
			mJuCount[i] = 0;
			mSpectrums[i].reset();
		}
	}
	
	/**
	 * 结束游戏
	 * 计算最终排名及得分
	 * 
	 */
	public void doGameFinish() {
		int[] ranks = new int[4];
		float[] mas = {0, 0, 0, 0};
		for (int i = 0; i < 4; i++) {
			ranks[i] = analysisPlayerRank(i, mScores);
			switch (ranks[i]) {
			case 1:
				mas[i] += (mRetPoint / 1000 * 4); // 头名赏：（返点 - 原点） / 1000 * 4; 
			case 2:				
			case 3:
			case 4: // 最终得点：（最终得分 - 返点） / 1000 + 顺位马
				double tmp = ((double)(mScores[i] - mBaseScore - mRetPoint)) / 1000 + mMaPoints[ranks[i] - 1];
				float point = new BigDecimal(tmp).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
				mas[i] += point;
				break;
			default:
				break;
			}
		}
		mResult.setEndGame(System.currentTimeMillis(), mScores, ranks, mas);
		mResult.setNote(note);
		if (mListener != null) {
			mListener.onResult(mPlayers, mScores, ranks, mas);
		}
		saveResult();
	}
	
	/**
	 * 保存结果到数据库
	 * 
	 */
	private void saveResult() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					ActiveAndroid.beginTransaction();
					mResult.save();
					for (MjDetail detail : mDetails) {
						detail.save();
					}
					ActiveAndroid.setTransactionSuccessful();
				} catch (Exception e) {
					String s = e.getMessage();
					System.out.println(s);
				} finally {
					ActiveAndroid.endTransaction();	
					clearStatesForTmp();
				}							
			}
		}).start();
	}
	
	/**
	 * 获取当前庄家玩家索引
	 * 
	 * @return
	 */
	public int getDealer() {
		return mJuCount[mFengCount];
	}
	
	/**
	 * 根据玩家id查找玩家原始风位
	 * 
	 * @param id
	 * @return
	 */
	public int findPlayerOrgIndexById(String id) {
		int index  = 0;
		for (int i = 0; i < mPlayers.length; i++) {
			if (mPlayers[i].getUuid().equals(id)) {
				index = i;
				break;
			}
		}		
		return index;
	}
	
	/**
	 * 计算全部玩家排名
	 * 
	 * @param orgScores
	 * @param changeScores
	 * @return
	 */
	public int[] analysisPlayerRanks(int[] orgScores, int[] changeScores) {
		int[] finalScores = new int[4];
		for (int i = 0; i < 4; i++) {
			finalScores[i] = orgScores[i] + changeScores[i]; 
		}
		return analysisPlayerRanks(finalScores);
	}
	
	/**
	 * 计算全部玩家排名
	 * 
	 * @param finalScores
	 * @return
	 */
	public int[] analysisPlayerRanks(int[] finalScores) {
		int[] ranks = {1, 2, 3, 4};
		for (int i = 0; i < 4; i++) {
			ranks[i] = analysisPlayerRank(i, finalScores);
		}
		return ranks;
	}
	
	/**
	 * 计算个人玩家排名(1-4)
	 * 
	 * @param index
	 * @param finalScores
	 * @return
	 */
	private int analysisPlayerRank(int index, int[] finalScores) {
		int rank = 4;
		int score = finalScores[index];
		for (int i = 0; i < 4; i++) {
			if (index == i) continue;
			if ((score > finalScores[i]) 
					|| (score == finalScores[i] && index < i)) {
				rank--;
			}
		}
		return rank;
		
	}
	
	/**
	 * 荒牌流局
	 * 
	 * @param changeScores
	 */
	public void setHuangpailiuju(int[] changeScores) {
		int tingCount = 0;
		List<String> ids = new ArrayList<String>();
		boolean isDealerTing = false;
		for (int i = 0; i < 4; i++) {
			mScores[i] += changeScores[i];
			if (getPlayerTingpai(i)) {
				tingCount++;
				ids.add(getPlayer(i).getUuid());
				if (!isDealerTing && i == getDealer()) isDealerTing = true;
			}
		}
		String[] tingIds = ids.toArray(new String[tingCount]);
		MjAction action = MjAction.createHPLJAction(tingCount, tingIds, 1);
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), getLizhiCount(), 
				changeScores[0], mScores[0], changeScores[1], mScores[1], 
				changeScores[2], mScores[2], changeScores[3], mScores[3], 
				"", "", action);
		addDetail(detail);
		if (checkPlayerFly()) {
			if (mListener != null) 
				mListener.onFinishByPlayerFly();
			return;
		}
		mRoundCount++; // 本场数+1
		if (isDealerTing) { // 庄家听牌，连庄
			continueRound();
		} else { // 庄家无听牌，流庄
			nextRound();
		}
		saveStatesForTmp();
	}
	
	/**
	 * 四风连打
	 * 
	 * @param wind
	 * @param isAddRound
	 */
	public void setSifenglianda(int wind, boolean isAddRound) {
		MjAction action = MjAction.createSFLDAction(wind, isAddRound ? 1 : 0);
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), getLizhiCount(), 
				0, mScores[0], 0, mScores[1], 
				0, mScores[2], 0, mScores[3], 
				"", "", action);
		addDetail(detail);
		if (isAddRound) mRoundCount++; // 本场数+1
		continueRound();
		saveStatesForTmp();
	}
	
	/**
	 * 四杠散了
	 * 
	 * @param gangCount
	 * @param gangIds
	 * @param isAddRound
	 */
	public void setSigangsanle(int gangCount, String[] gangIds, boolean isAddRound) {
		MjAction action = MjAction.createSGSLAction(gangCount, gangIds, isAddRound ? 1 : 0);
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), getLizhiCount(), 
				0, mScores[0], 0, mScores[1], 
				0, mScores[2], 0, mScores[3], 
				"", "", action);
		addDetail(detail);
		if (isAddRound) mRoundCount++; // 本场数+1
		continueRound();
		saveStatesForTmp();
	}
	
	/**
	 * 九牌九种
	 * 
	 * @param liuId
	 * @param isAddRound
	 */
	public void setJiupaijiuzhong(String liuId, boolean isAddRound) {
		MjAction action = MjAction.createJPJZAction(liuId, isAddRound ? 1 : 0);
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), getLizhiCount(), 
				0, mScores[0], 0, mScores[1], 
				0, mScores[2], 0, mScores[3], 
				"", "", action);
		addDetail(detail);
		if (isAddRound) mRoundCount++; // 本场数+1
		continueRound();
		saveStatesForTmp();
	}
	
	/**
	 * 四家立直
	 * 
	 * @param isAddRound
	 */
	public void setSijializhi(boolean isAddRound) {
		String[] lizhiIds = {mPlayers[0].getUuid(), mPlayers[1].getUuid(),
				mPlayers[2].getUuid(), mPlayers[3].getUuid()};
		MjAction action = MjAction.createSJLZAction(lizhiIds, isAddRound ? 1 : 0);
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), getLizhiCount(), 
				0, mScores[0], 0, mScores[1], 
				0, mScores[2], 0, mScores[3], 
				"", "", action);
		addDetail(detail);
		if (isAddRound) mRoundCount++; // 本场数+1
		continueRound();
		saveStatesForTmp();
	}
	
	/**
	 * 三家和了
	 * 
	 * @param heIds
	 * @param isAddRound
	 */
	public void setSanjiahele(String[] heIds, boolean isAddRound) {
		MjAction action = MjAction.createSJHLAction(heIds, isAddRound ? 1 : 0);
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), getLizhiCount(), 
				0, mScores[0], 0, mScores[1], 
				0, mScores[2], 0, mScores[3], 
				"", "", action);
		addDetail(detail);
		if (isAddRound) mRoundCount++; // 本场数+1
		continueRound();
		saveStatesForTmp();
	}
	
	/**
	 * 分析流局满贯的分值变化
	 * 
	 * @param isManGuans
	 * @return
	 */
	public int[] analysisLiujumanguan(boolean[] isManGuans, ResultList[] resultLists) {
		int[] changeScores = {0, 0, 0, 0};
		for (int i = 0; i < isManGuans.length; i++) {
			if (isManGuans[i]) {				
				if (getDealer() == i) { // 庄家满贯12000->闲家减4000
					for (int j = 0; j < changeScores.length; j++) {
						if (j != i) {
							changeScores[j] -= 4000;
							resultLists[j].addBase(-4000);
						} else {
							changeScores[j] += 12000;
							resultLists[j].addBase(12000);
						}
					}
				} else { // 闲家满贯8000->庄家减4000，闲家减2000
					for (int j = 0; j < changeScores.length; j++) {
						if (j != i) {
							if (getDealer() == j) {
								changeScores[j] -= 4000;
								resultLists[j].addBase(-4000);
							} else {
								changeScores[j] -= 2000;
								resultLists[j].addBase(-2000);
							}							
						} else {
							changeScores[j] += 8000;
							resultLists[j].addBase(8000);
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
	 * 流局满贯
	 * 
	 * @param changeScores
	 * @param isManGuans
	 * @param isDealerTing
	 */
	public void setLiujumanguan(int[] changeScores, boolean[] isManGuans, boolean isDealerTing) {
		int count = 0;
		List<String> ids = new ArrayList<String>();
		for (int i = 0; i < 4; i++) {
			mScores[i] += changeScores[i];
			if (isManGuans[i]) {
				count++;
				ids.add(getPlayer(i).getUuid());
			}
		}
		String[] mgIds = ids.toArray(new String[count]);
		MjAction action = MjAction.createLJMGAction(count, mgIds, 1);
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), getLizhiCount(), 
				changeScores[0], mScores[0], changeScores[1], mScores[1], 
				changeScores[2], mScores[2], changeScores[3], mScores[3], 
				"", "", action);
		addDetail(detail);
		if (checkPlayerFly()) {
			if (mListener != null) 
				mListener.onFinishByPlayerFly();
			return;
		}
		mRoundCount++; // 本场数+1
		if (isDealerTing) { // 庄家听牌，连庄
			continueRound();
		} else { // 庄家无听牌，流庄
			nextRound();
		}
		saveStatesForTmp();
	}
	
	/**
	 * 分数调整
	 * 
	 * @param changeScores
	 */
	public void setChangeScore(int[] changeScores) {
		for (int i = 0; i < 4; i++) {
			mScores[i] += changeScores[i];
		}
		MjAction action = MjAction.createChangeScoreAction();
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), getLizhiCount(), 
				changeScores[0], mScores[0], changeScores[1], mScores[1], 
				changeScores[2], mScores[2], changeScores[3], mScores[3], 
				"", "", action);
		addDetail(detail);
		if (checkPlayerFly()) {
			if (mListener != null) 
				mListener.onFinishByPlayerFly();
			return;
		}
		saveStatesForTmp();
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
	public int[] analysisZimo(int winIndex, int fan, int fu,
			int juCount, int roundCount, int lizhiCount, 
			boolean isBao, int baoId, ResultList[] resultLists) {
		int[] changeScores = {0, 0, 0, 0};
		boolean isDealer = winIndex == juCount;
		int basePoint = ScoreSystem.GetBasePoint(fan, fu);
		int yiman = 0;
		if (fan < 0) yiman = -fan;
		else if (fan >= 13) yiman = fan / 13;
		if (isDealer) { // 庄家自摸
			int playerPay = ScoreSystem.Player_Pay_Dealer_Zimo(basePoint);
			changeScores[winIndex] += (playerPay * 3 
					+ roundCount * 300 + lizhiCount * 1000);
			resultLists[winIndex].addBase(playerPay * 3)
					.addRound(roundCount * 300).addLizhi(lizhiCount * 1000);			
			if (yiman > 0 && isBao && baoId >= 0) { // 役满有包牌
				if (yiman == 1) { // 普通役满：包牌者付全部（包括本场数）
					changeScores[baoId] -= (playerPay * 3 
							+ roundCount * 300);
					resultLists[baoId].addBaopai(-playerPay * 3).addRound(-roundCount * 300);
				} else { // 复合役满：包牌者付一个役满，剩余再平分，本场数三家付
					int yimanScore = ScoreSystem.All_Pay_Dealer_Ronghe(ScoreSystem.GetBasePoint(13, 0));
					changeScores[baoId] -= yimanScore;
					resultLists[baoId].addBaopai(-yimanScore);
					playerPay = (playerPay * 3 - yimanScore) / 3;
					for (int i = 0; i < changeScores.length; i++) {
						if (i == winIndex) { // 庄家赢家
							continue;
						} else { // 闲家
							changeScores[i] -= (playerPay + roundCount * 100);
							resultLists[i].addBase(-playerPay).addRound(-roundCount * 100);
						}
					}
				}				
			} else { // 一般情况
				for (int i = 0; i < changeScores.length; i++) {
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
			changeScores[winIndex] += (dealerPay + playerPay * 2
					+ roundCount * 300 + lizhiCount * 1000);
			resultLists[winIndex].addBase(dealerPay + playerPay * 2)
					.addRound(roundCount * 300).addLizhi(lizhiCount * 1000);
			if (yiman > 0 && isBao && baoId >= 0) { // 役满有包牌
				if (yiman == 1) { // 普通役满：包牌者付全部（包括本场数）
					changeScores[baoId] -= (dealerPay + playerPay * 2
							+ roundCount * 300);
					resultLists[baoId].addBaopai(-dealerPay - playerPay * 2)
							.addRound(-roundCount * 300);
				} else { // 复合役满：包牌者付一个役满，剩余的庄家付1/2，闲家付1/4，本场数三家付
					int yimanScore = ScoreSystem.All_Pay_Player_Ronghe(ScoreSystem.GetBasePoint(13, 0));
					changeScores[baoId] -= yimanScore;
					resultLists[baoId].addBaopai(-yimanScore);
					playerPay = (dealerPay + playerPay * 2 - yimanScore) / 4;
					for (int i = 0; i < changeScores.length; i++) {
						if (i == winIndex) { // 闲家赢家
							continue;
						} else if (i == juCount) { // 庄家
							changeScores[i] -= (playerPay * 2 + roundCount * 100);
							resultLists[i].addBase(-playerPay * 2).addRound(-roundCount * 100);
						} else { // 闲家
							changeScores[i] -= (playerPay + roundCount * 100);
							resultLists[i].addBase(-playerPay).addRound(-roundCount * 100);
						}
					}
				}
			} else { // 一般情况
				for (int i = 0; i < changeScores.length; i++) {
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
		if (checkPlayerFly()) {
			if (mListener != null) 
				mListener.onFinishByPlayerFly();
			return;
		}
		mLizhiCount = 0; // 总立直数归零
		if (mChickens[winIndex]) { // 取消烧鸡状态
			mChickens[winIndex] = false;
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
	public int[] analysisBomb(int bombIndex, int[] winIndexs, int[] fan, int[] fu,
			int juCount, int roundCount, int lizhiCount, boolean[] isBaos, int[] baoIds,
			ResultList[] resultLists) {
		int[] changeScores = {0, 0, 0, 0};		
		for (int i = 0; i < winIndexs.length; i++) {
			int basePoint = ScoreSystem.GetBasePoint(fan[i], fu[i]);
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
						int yimanScore = ScoreSystem.All_Pay_Dealer_Ronghe(ScoreSystem.GetBasePoint(13, 0));
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
						int yimanScore = ScoreSystem.All_Pay_Player_Ronghe(ScoreSystem.GetBasePoint(13, 0));
						changeScores[baoIds[i]] -= yimanScore;
						changeScores[bombIndex] -= (pay - yimanScore);
						resultLists[baoIds[i]].addBaopai(-yimanScore);
						resultLists[bombIndex].addBaopai(-pay + yimanScore);
					}
				} else { // 一般情况
					changeScores[bombIndex] -= pay;
					resultLists[bombIndex].addBase(-pay);
				}				
			}
		}
		// 本场数点炮者付
		changeScores[bombIndex] -= (roundCount * 300);
		resultLists[bombIndex].addRound(-roundCount * 300);
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
					changeScores[near] += (lizhiCount * 1000 + roundCount * 300);
					resultLists[near].addLizhi(lizhiCount * 1000).addRound(roundCount * 300);					
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
		if (checkPlayerFly()) {
			if (mListener != null) 
				mListener.onFinishByPlayerFly();
			return;
		}
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
	public void setBomb(int[] changeScores, int bombIndex, int[] winIndexs) {
		setBomb(changeScores, bombIndex, winIndexs, 
				getResultSpectrums(winIndexs), 
				getResultFans(winIndexs), getResultFus(winIndexs), 
				getResultEnvironments(winIndexs),
				getEnableBaopais(winIndexs), getBapaiPlayers(winIndexs));
	}
	
	/**
	 * 检查是否有玩家起飞
	 * 
	 * @return
	 */
	public boolean checkPlayerFly() {
		for (int i = 0; i < mScores.length; i++) {
			if (mScores[i] < 0) {
				return true;
			}
		}		
		return false;
	}	
	
	/**
	 * 连庄
	 * 
	 */
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
			if (mFengCount == mMaxFeng && mJuCount[mFengCount] == 3) {
				boolean isMax = true;
				for (int i = 0; i < 3; i++) {
					if (mScores[i] > mScores[3]) {
						isMax = false;
						break;
					}
				}
				if (isMax && mListener != null) {
					result = Result_Finish_All;
					mListener.onFinishAll(); // 结束游戏
				}
			}
		}
		return result;
	}
	
	/**
	 * 流庄
	 * 
	 */
	public int nextRound() {
		int result = Result_None;
		for (int i = 0; i < 4; i++) {
			mSpectrums[i].resetByNewRound();
		}
		mDoraIndicaOut.clear();
		mDoraIndicaIn.clear();
		if (mJuCount[mFengCount] >= 3) {
			mFengCount++; // 场风+1
			if (mFengCount > mMaxFeng) {
				// 询问是否结束游戏				
				if (mMaxFeng < 2 && mFengCount == (mMaxFeng + 1)
						&& isEnterSW && isAllLower()) { // 继续南入\西入
					result = Result_Enter_S_W;
					if (mListener != null) mListener.onEnterSouthOrWest(mMaxFeng == 0);
				} else { // 结束游戏
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
	
	private boolean isAllLower() {
		boolean isAllLower = true;
		for (int i = 0; i < mScores.length; i++) {
			if (mScores[i] >= mBaseScore + 5000) {
				isAllLower = false;
				break;
			}
		}
		return isAllLower;
	}
	
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
		mFengCount = juCount / 4; // 场风
		mJuCount[mFengCount] = juCount % 4;  // 复原庄家
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
	 * @return
	 */
	private int[] revokeLizhiState() {
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
	 * 获取游戏结果
	 * 
	 * @return
	 */
	public MjResult getResult() {
		return mResult;
	}
	
	/**
	 * 获取全部操作
	 * 
	 * @return
	 */
	public List<MjDetail> getAllDetails() {
		return mDetails;
	}
	
	/**
	 * 将操作转为文本
	 * 
	 * @param context
	 * @param detail
	 * @param winds
	 * @return
	 */
	public String getDetailText(Context context, MjDetail detail, String[] winds) {
		String[] names = {mPlayers[0].getNickName(), mPlayers[1].getNickName(), 
				mPlayers[2].getNickName(), mPlayers[3].getNickName()};
		String[] ids = {mPlayers[0].getUuid(), mPlayers[1].getUuid(), 
				mPlayers[2].getUuid(), mPlayers[3].getUuid()};
		return MjDetail.getDetailText(context, detail, winds, names, ids);
	}
	
	/**
	 * 增加新操作
	 * 
	 * @param detail
	 * @return
	 */
	public boolean addDetail(MjDetail detail) {
		mDetails.add(detail);
		return true;
	}
	
	/**
	 * 撤销上一条操作
	 * 
	 * @return
	 */
	public MjDetail removeLastDetail() {
		if (mDetails.size() > 0) {
			MjDetail detail = mDetails.remove(mDetails.size() - 1);
			return detail;
		}	
		return null;
	}
	
	/**
	 * 判断是否有上一条操作
	 * 
	 * @return
	 */
	public boolean hasLastDetail() {
		if (mDetails.size() > 0) return true;
		return false;
	}
	
	/**
	 * 获取上一条操作
	 * 
	 * @return
	 */
	public MjDetail getLastDetail() {
		if (mDetails.size() > 0) {
			return mDetails.get(mDetails.size() - 1);
		}
		return null;
	}
	
	/**
	 * 检查上次中断情况
	 * 
	 * @return
	 */
	public boolean checkLastSaveStates() {
		File tmpFile = new File(mTmpFileDir, "last_game.tmp");
		if (tmpFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(tmpFile));
				int len = -1;
		        char[] buffer = new char[1024];
		        StringBuffer sb = new StringBuffer();
		        while ((len = br.read(buffer)) != -1) {
		        	sb.append(buffer, 0, len);
		        }
		        JSONObject json = new JSONObject(sb.toString());
		        String save_type = json.optString("save_type");
		        br.close();
		        if (save_type != null && save_type.equals(TAG_INTERRUPT)) {
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
	
	
	/**
	 * 临时保存当前所有操作
	 * 
	 */
	public void saveStatesForTmp() {
		try {
			File tmpFile = new File(mTmpFileDir, "last_game.tmp");
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
					+ mPlayers[2].getUuid() + "," + mPlayers[3].getUuid());
			json.put("enter_sw", isEnterSW);
			json.put("lizhi_belong", mLizhiBelong);
			json.put("enable_fanfu", mEnableFanfu);
			json.put("ma_points", mMaPoints[0] + "," + mMaPoints[1] + "," + mMaPoints[2] + "," + mMaPoints[3]);
			json.put("start_time", mResult.getStartTime());
			json.put("note", note != null ? note : "");
			json.put("ret_point", mRetPoint);
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
	public boolean restoreStatesForTmp() {
		File tmpFile = new File(mTmpFileDir, "last_game.tmp");
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
					if (players.length == 4) {
						for (int i = 0; i < players.length; i++) {
							for (Player p : pList) {
								if (p.getUuid().equals(players[i])) {
									mPlayers[i] = p;
									break;
								}
							}
							for (Player p : mNPCs) {
								if (p.getUuid().equals(players[i])) {
									mPlayers[i] = p;
									break;
								}
							}
						}
					} else return false;
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
					note = json.getString("note");
					mRetPoint = json.getInt("ret_point");
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
							mRetPoint);
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
	
	/**
	 * 清除缓存
	 * 
	 */
	public void clearStatesForTmp() {
		File tmpFile = new File(mTmpFileDir, "last_game.tmp");
		if (tmpFile.exists()) tmpFile.delete();	
	}
	
	/**
	 * 玩家个人信息
	 */
	public static class SpectrumData {
		
//		private static final MjWind[] mWinds = {MjWind.East, MjWind.South, MjWind.West, MjWind.North};
		
		public int mOrgIndex; // 原始索引
//		public List<MjCard> mDarkCards = new ArrayList<MjCard>();
//		public List<MjCardPairs> mBrightPairs = new ArrayList<MjCardPairs>();
//		public MjCard mWinCard = new MjCard();
		public boolean mTingPai; // 玩家听牌状态
		public int mLiZhi; // 玩家立直状态
//		public boolean mYiFa; // 环境：一发
//		public boolean mZiMo; // 环境：自摸
//		public boolean mFirstRound; // 环境：一巡无鸣牌
//		public boolean mFinalPick; // 环境：海底牌
//		public boolean mQiangGang; // 环境：抢杠
//		public boolean mLingShang; // 环境：岭上开花
		public int mFan; // 番数
		public int mFu; // 符数
		public String mSpectrum;
		public int mEnv;
		public boolean isEnableBaopai;
		public int mBaopai; // 包牌玩家
		
		public SpectrumData(int index) {
			mOrgIndex = index;
		}
		
//		public void setData(List<MjCard> darkCards,
//				List<MjCardPairs> brightCardPairs, MjCard winCard,
//				boolean lizhi, boolean doublelizhi, boolean yifa, boolean zimo,
//				boolean firstround, boolean finalpick, boolean qianggang, boolean lingshang) {
//			this.mWinCard.setData(winCard);
//			this.mDarkCards.clear();
//			for (MjCard c : darkCards) {
//				mDarkCards.add(new MjCard(c));
//			}
//			this.mBrightPairs.clear();
//			for (MjCardPairs p : brightCardPairs) {
//				mBrightPairs.add(new MjCardPairs(p));
//			}
//			if (doublelizhi) {
//				mLiZhi = 2;
//			} else if (lizhi) {
//				mLiZhi = 1;
//			} else {
//				mLiZhi = 0;
//			}
//			mYiFa = yifa;
//			mZiMo = zimo;
//			mFirstRound = firstround;
//			mFinalPick = finalpick;
//			mQiangGang = qianggang;
//			mLingShang = lingshang;
//		}
		
		public void setResult(int fan, int fu, String sperctrum, int env) {
			mFan = fan;
			mFu = fu;
			mSpectrum = sperctrum;
			mEnv = env;
		}
		
//		public int getEnvironment(boolean isZimo, int fengCount, int juCount) {
//			mZiMo = isZimo;
//			MjWind groundWind = mWinds[fengCount];
//			int wind = (mOrgIndex + 4 - juCount) % 4;
//			MjWind selfWind = mWinds[wind];
//			return MjCalcTool.convert2JpnEnvironment(mLiZhi == 1, mLiZhi == 2, 
//					mFirstRound, mFinalPick, mZiMo, mYiFa, mQiangGang, mLingShang, 
//					groundWind, selfWind, isMenQing());
//		}
//		
//		private boolean isMenQing() {
//			if (mLiZhi > 0) return true;
//			if (mWinCard.isBlank()) return false;
//			if (mBrightPairs == null || mBrightPairs.size() == 0) {
//				return true;
//			}
//			for (MjCardPairs pairs : mBrightPairs) {
//				if (pairs.getType() != MjPairType.ConcealedKong) {
//					return false;
//				}
//			}
//			return true;
//		}
		
		public void setEnableBaopai(boolean enable) {
			isEnableBaopai = enable;
		}
		
		public void setBaopaiPlayer(int index) {
			mBaopai = index;
		}
		
//		@Override
//		public String toString() {
//			if (mWinCard.isBlank()) {
//				return "";
//			}
//			return MjFanBean.createMjString(mDarkCards, mBrightPairs, mWinCard);
//		}
		
		public void reset() {
			resetByNewRound();
		}
		
		public void resetByNewRound() {
//			mDarkCards.clear();
//			for (int i = 0; i < 13; i++) {
//				mDarkCards.add(new MjCard());
//			}
//			mBrightPairs.clear();
//			mWinCard.reset();
			mTingPai = false;
			mLiZhi = 0;
//			mYiFa = false;
//			mZiMo = false;
//			mFirstRound = false;
//			mFinalPick = false;
//			mQiangGang = false;
//			mLingShang = false;
			mFan = 0;
			mFu = 0;
			mSpectrum = "";
			mEnv = 0;
			isEnableBaopai = false;
			mBaopai = -1;
		}
	}
	
	public void setOnManageToolListener(OnManageToolListener listener) {
		this.mListener = listener;
	}
	
	public interface OnManageToolListener {
		// 玩家起飞，游戏结束
		public void onFinishByPlayerFly();
		// 已完成所有局数，继续南入\西入
		public void onEnterSouthOrWest(boolean isNan);
		// 所有局数完成，游戏结束
		public void onFinishAll();
		// 
		public void onResult(Player[] players, int[] scores, int[] ranks, float[] mas);
	}
	
}

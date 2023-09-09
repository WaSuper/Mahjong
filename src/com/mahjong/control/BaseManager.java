package com.mahjong.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.mahjong.common.MjCard;
import com.mahjong.item.ResultList;
import com.mahjong.model.MjAction;
import com.mahjong.model.MjDetail;
import com.mahjong.model.MjResult;
import com.mahjong.model.Player;

public abstract class BaseManager {

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
	
	public static final String GAME_TYPE = "GAME_TYPE";
	public static final int MainType_4p 	= 0;
	public static final int MainType_3p 	= 1;
	public static final int MainType_17s 	= 2;
	
	protected int mMainType; // 游戏类型（0：四麻，1：三麻，2：17步）
	protected int mMemberCount; // 玩家人数
	protected int mBaseScore; // 基本分
	protected int[] mScores = {0, 0, 0, 0}; // 玩家分数
	protected boolean[] mChickens = {true, true, true, true}; // 玩家烧鸡状态
	protected int mMaxFeng = 1; // 最大场风
	protected int mFengCount = 0; // 场风计数
	protected int[] mJuCount; // 局数计数，对应各自场风
	protected int mLizhiCount = 0; // 场上累计立直棒数
	protected int mRoundCount = 0; // 本场数
	protected Player[] mPlayers = new Player[4]; // 4个玩家
	protected SpectrumData[] mSpectrums = new SpectrumData[4]; // 4个玩家的牌谱
	protected List<MjCard> mDoraIndicaOut = new ArrayList<MjCard>(); // 宝牌指示牌
	protected List<MjCard> mDoraIndicaIn = new ArrayList<MjCard>(); // 里宝牌指示牌
	protected Player[] mNPCs; // NPC玩家
	protected boolean[] mNPCused; // NPC使用状态
	protected MjResult mResult; // 最终结果
	protected List<MjDetail> mDetails = new ArrayList<MjDetail>(); // 所有操作
	protected OnManageToolListener mListener;
	protected boolean isEnterSW = false; // 南入/西入(17步不使用)
	protected int mLizhiBelong = 0; // 多家和时立直棒归属
	protected boolean mEnableFanfu = false; // 本场番缚(17步不使用)
	protected int[] mMaPoints = new int[4]; // 马点
	protected String note; // 备注
	protected int mRetPoint; // 返点-基础分
	protected boolean isDoubleWind4 = false; // 雀头双风+4符，否则+2符
	protected int mFengType = 0; // 17步场风类型：0->固定东风,1->轮流场风
	protected int mFanfuType = 0; // 17步番缚类型：0->五番番缚,1->满贯番缚,2->无番缚
	protected boolean mEnableZimoCut = false; // 3麻：自摸损
	protected boolean mManguanUp = false; // 切上满贯
	protected boolean mNoFly = false; // 无击飞
	protected boolean mFinalWinnerUnlimited = false; // 终局末位玩家第一时无限连庄
	
	protected String mTmpFileDir; // 临时信息保存地址
	protected static final String TAG_INTERRUPT = "interrupt";
	protected static String FILE_INTERRUPT;

	public BaseManager() {
		createNPCs();
		resetData();
	}
	
	/**
	 * 判断是否4麻玩法
	 * 
	 * @return
	 */
	public boolean is4pMahjong() {
		return mMainType == MainType_4p;
	}
	
	/**
	 * 判断是否3麻玩法
	 * 
	 * @return
	 */
	public boolean is3pMahjong() {
		return mMainType == MainType_3p;
	}
	
	/**
	 * 判断是否17步玩法
	 * 
	 * @return
	 */
	public boolean is17Step() {
		return mMainType == MainType_17s;
	}
	
	/**
	 * 重置数据
	 */
	private void resetData() {
		mLizhiCount = 0;
		mRoundCount = 0;
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
	 * 设置基础数据
	 * 
	 * @param count 玩家人数
	 * @param score 基础分值
	 * @param maxfeng 最大场风
	 */
	public abstract void setBaseData(int count, int score, int maxfeng);
	
	/**
	 * 获取游戏类型（0：四麻，1：三麻，2：17步）
	 * 
	 * @return
	 */
	public int getMainType() {
		return mMainType;
	}
	
	/**
	 * 将场上额外设定转为二进制数据
	 * 
	 * @return
	 */
	public int getExtraData() {
		int mExtraData = 1;
		if (isEnterSW) mExtraData |= 0x2;
		if (mLizhiBelong != 0) mExtraData |= 0x4;
		if (mEnableFanfu) mExtraData |= 0x8;
		if (isDoubleWind4) mExtraData |= 0x10;
		if (mFengType != 0) mExtraData |= 0x20;
		if (mFanfuType == 1) {
			mExtraData |= 0x40;
		} else if (mFanfuType == 2) {
			mExtraData |= 0x80;
		}
		if (mEnableZimoCut) mExtraData |= 0x100;
		if (mManguanUp) mExtraData |= 0x200;
		if (mNoFly) mExtraData |= 0x400;
		if (mFinalWinnerUnlimited) mExtraData |= 0x800;
		return mExtraData;
	}
	
	/**
	 * 将二进制数据转为场上额外设定
	 * 
	 * @param mExtraData
	 */
	public void setExtraData(int mExtraData) {
		if (mExtraData == 0) return;
		isEnterSW = ((mExtraData & 0x02) == 0x02);
		mLizhiBelong = ((mExtraData & 0x04) == 0x04) ? 1 : 0;
		mEnableFanfu = ((mExtraData & 0x08) == 0x08);
		isDoubleWind4 = ((mExtraData & 0x10) == 0x10);
		mFengType = ((mExtraData & 0x20) == 0x20) ? 1 : 0;
		mFanfuType = (mExtraData >> 6) & 0x3;
		mEnableZimoCut = ((mExtraData & 0x100) == 0x100);
		mManguanUp = ((mExtraData & 0x200) == 0x200);
		mNoFly = ((mExtraData & 0x400) == 0x400);
		mFinalWinnerUnlimited = ((mExtraData & 0x800) == 0x800);
	}
	
	/**
	 * 获取玩家人数
	 * 
	 * @return
	 */
	public int getMemberCount() {
		return mMemberCount;
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
	 * 获取全部玩家的分值
	 * 
	 * @return
	 */
	public int[] getPlayerScores() {
		return mScores;
	}
	
	/**
	 * 获取单个玩家分值
	 * 
	 * @param index
	 * @return
	 */
	public int getPlayerScore(int index) {
		return mScores[index];
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
	 * 获取场风（用于统计总场次）
	 * 
	 * @return
	 */
	public int getFengCount() {
		return mFengCount;
	}
	
	/**
	 * 获取场风（用于计算牌谱）
	 * 
	 * @return
	 */
	public int getGroundWind() {
		int windIndex = mFengCount;
		if (is17Step()) {
			if (mFengType == 0) {
				windIndex = 0;
			} else {
				switch (mMemberCount) {
				case 2:
					if (mFengCount % 2 == 0) windIndex = 0;
					else windIndex = 2;
					break;
				case 3:
					windIndex = mFengCount % 3;
					break;
				case 4:
					windIndex = mFengCount % 4;
					break;
				default:
					break;
				}
			}
		}
		return windIndex;
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
		return mFengCount * mMemberCount + getJuCount();
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
	 * 获取是否雀头双风+4符
	 * 
	 * @return
	 */
	public boolean getEnableDoubleWind4() {
		return isDoubleWind4;
	}
	
	/**
	 * 设置是否雀头双风+4符
	 * 
	 * @param enbale
	 */
	public void setEnableDoubleWind4(boolean enbale) {
		this.isDoubleWind4 = enbale;
	}
	
	/**
	 * 获取17步场风类型
	 * 
	 * @return
	 */
	public int getFengType() {
		return mFengType;
	}
	
	/**
	 * 设置17步场风类型
	 * 
	 * @param type
	 */
	public void setFengType(int type) {
		this.mFengType = type;
	}
	
	/**
	 * 获取17步番缚类型
	 * 
	 * @return
	 */
	public int getFanfuType() {
		return mFanfuType;
	}
	
	/**
	 * 设置17步番缚类型
	 * 
	 * @param type
	 */
	public void setFanfuType(int type) {
		this.mFanfuType = type;
	}
	
	/**
	 * 获取三麻是否自摸损
	 * 
	 * @return
	 */
	public boolean getEnableZimoCut() {
		return mEnableZimoCut;
	}
	
	/**
	 * 设置三麻是否自摸损
	 * 
	 * @param enable
	 */
	public void setEnableZimoCut(boolean enable) {
		this.mEnableZimoCut = enable;
	}
	
	/**
	 * 获取是否执行切上满贯
	 * 
	 * @return
	 */
	public boolean getEnableManguanUp() {
		return mManguanUp;
	}
	
	/**
	 * 设置是否执行切上满贯
	 * 
	 * @param enable
	 */
	public void setEnableManguanUp(boolean enable) {
		this.mManguanUp = enable;
	}
	
	/**
	 * 获取是否执行无击飞
	 * 
	 * @return
	 */
	public boolean getEnableNoFly() {
		return mNoFly;
	}
	
	/**
	 * 设置是否执行无击飞
	 * 
	 * @param enable
	 */
	public void setEnableNoFly(boolean enable) {
		this.mNoFly = enable;
	}
	
	/**
	 * 获取是否执行终局末位玩家第一时无限连庄
	 * 
	 * @return
	 */
	public boolean getEnableFinalWinnerUnlimited() {
		return mFinalWinnerUnlimited;
	}
	
	/**
	 * 设置是否执行终局末位玩家第一时无限连庄
	 * 
	 * @param enable
	 */
	public void setEnableFinalWinnerUnlimited(boolean enable) {
		this.mFinalWinnerUnlimited = enable;
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
	 * 获取单个玩家立直状态
	 * 
	 * @param wind
	 * @return
	 */
	public int getPlayerLizhi(int wind) {
		return mSpectrums[wind].mLiZhi;
	}
	
	/**
	 * 获取所有玩家立直状态
	 * 
	 * @param wind
	 * @return
	 */
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
	
	/**
	 * 分析听牌分值变化
	 * 
	 * @param resultLists
	 * @return
	 */
	public int[] analysisTingpaiResult(ResultList[] resultLists) {
		int count = 0;
		boolean[] ting = new boolean[4];
		for (int i = 0; i < 4; i++) {
			ting[i] = getPlayerTingpai(i);
			if (ting[i]) count++;
		}
		int[] change;
		switch (mMemberCount) {
		case 2:
			change = analysisTingpaiResultFor2P(resultLists, ting, count);
			break;
		case 3:
			change = analysisTingpaiResultFor3P(resultLists, ting, count);
			break;
		case 4:
		default:
			change = analysisTingpaiResultFor4P(resultLists, ting, count);
			break;
		}
		return change;
	}
	
	/**
	 * 分析四麻时听牌分值变化
	 * 
	 * @param resultLists
	 * @param ting
	 * @param count
	 * @return
	 */
	private int[] analysisTingpaiResultFor4P(ResultList[] resultLists, boolean[] ting, int count) {
		int[] change = new int[] {0, 0, 0, 0};
		int[] index = {0, 1, 2, 3};
		if (count == 1) {
			for (int i : index) {
				if (ting[i]) {
					change[i] += 3000;
					resultLists[i].addTingpai(3000);
				} else {
					change[i] -= 1000;
					resultLists[i].addTingpai(-1000);
				}
			}
		} else if (count == 2) {
			for (int i : index) {
				if (ting[i]) {
					change[i] += 1500;
					resultLists[i].addTingpai(1500);
				} else {
					change[i] -= 1500;
					resultLists[i].addTingpai(-1500);
				}
			}			
		} else if (count == 3) {
			for (int i : index) {
				if (ting[i]) {
					change[i] += 1000;
					resultLists[i].addTingpai(1000);
				} else {
					change[i] -= 3000;
					resultLists[i].addTingpai(-3000);
				}
			}	
		}
		return change;
	}
	
	/**
	 * 分析三麻时听牌分值变化
	 * 
	 * @param resultLists
	 * @param ting
	 * @param count
	 * @return
	 */
	private int[] analysisTingpaiResultFor3P(ResultList[] resultLists, boolean[] ting, int count) {
		int[] change = new int[] {0, 0, 0, 0};
		int[] index = {0, 1, 2};
		if (count == 1) {
			for (int i : index) {
				if (ting[i]) {
					change[i] += 2000;
					resultLists[i].addTingpai(2000);
				} else {
					change[i] -= 1000;
					resultLists[i].addTingpai(-1000);
				}
			}
		} else if (count == 2) {
			for (int i : index) {
				if (ting[i]) {
					change[i] += 1000;
					resultLists[i].addTingpai(1000);
				} else {
					change[i] -= 2000;
					resultLists[i].addTingpai(-2000);
				}
			}			
		}
		return change;
	}
	
	/**
	 * 分析二麻时听牌分值变化
	 * 
	 * @param resultLists
	 * @param ting
	 * @param count
	 * @return
	 */
	private int[] analysisTingpaiResultFor2P(ResultList[] resultLists, boolean[] ting, int count) {
		int[] change = new int[] {0, 0, 0, 0};
		int[] index = {0, 2};
		if (count == 1) {
			for (int i : index) {
				if (ting[i]) {
					change[i] += 1000;
					resultLists[i].addTingpai(1000);
				} else {
					change[i] -= 1000;
					resultLists[i].addTingpai(-1000);
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
	public int getResultEnvironment(int index) {
		return mSpectrums[index].mEnv;
	}
	
	/**
	 * 获取玩家当局环境变量数组
	 * 
	 * @param index
	 * @return
	 */
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
				mRetPoint, mMemberCount, mMainType, getExtraData());
		mFengCount = 0;
		Arrays.fill(mJuCount, 0);
		mLizhiCount = 0;
		mRoundCount = 0;
		mDetails.clear();
		mDoraIndicaOut.clear();
		mDoraIndicaIn.clear();
		Arrays.fill(mChickens, true);
		for (int i = 0; i < 4; i++) {
			mSpectrums[i].reset();
		}
	}
	
	/**
	 * 结束游戏
	 * 计算最终排名及得分
	 * 
	 */
	public void doGameFinish() {
		int[] ranks = {0, 0, 0, 0};
		float[] mas = {0, 0, 0, 0};
		int[] playerIndexes;
		switch (mMemberCount) {
		case 2:
			playerIndexes = new int[] {0, 2};
			break;
		case 3:
			playerIndexes = new int[] {0, 1, 2};
			break;
		case 4:
		default:
			playerIndexes = new int[] {0, 1, 2, 3};
			break;
		}
		for (int i : playerIndexes) {
			ranks[i] = analysisPlayerRank(i, mScores);
			switch (ranks[i]) {
			case 1:
				// 当终局时有立直棒在场上，应归一位者所有
				if (getLizhiCount() > 0) {
					setFinalLizhi(getLizhiCount(), i);
				}
				mas[i] += (mRetPoint / 1000 * mMemberCount); // 头名赏：（返点 - 原点） / 1000 * 人数; 
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
	protected void saveResult() {
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
		int[] ranks = {0, 0, 0, 0};
		for (int i = 0; i < 4; i++) {
			if (mMemberCount == 2 && (i == 1 || i == 3)) continue; // 2人时只计算东西风
			if (mMemberCount == 3 && i == 3) continue; // 3人时只计算东南西风
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
	protected int analysisPlayerRank(int index, int[] finalScores) {
		int score = finalScores[index];
		int rank = mMemberCount;
		int[] cmpIndexes;
		switch (mMemberCount) {
		case 2:
			cmpIndexes = new int[] {0, 2};
			break;
		case 3:
			cmpIndexes = new int[] {0, 1, 2};
			break;
		case 4:
		default:
			cmpIndexes = new int[] {0, 1, 2, 3};
			break;
		}
		for (int i : cmpIndexes) {
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
	 * 终局立直棒分配(流局终局时使用)
	 * 
	 * @param lizhiCount
	 * @param winIndex
	 */
	public void setFinalLizhi(int lizhiCount, int winIndex) {
		MjAction action = MjAction.createFinalLizhiAction(lizhiCount, mPlayers[winIndex].getUuid());
		int changeScores[] = {0, 0, 0, 0};
		for (int i = 0; i < changeScores.length; i++) {
			if (i == winIndex) {
				changeScores[i] = lizhiCount * 1000;
				mScores[i] += changeScores[i];
				break;
			}
		}
		MjDetail detail = new MjDetail(mResult.getStartTime(), System.currentTimeMillis(), 
				getJuCountForAll(), getRoundCount(), 0, 
				changeScores[0], mScores[0], changeScores[1], mScores[1], 
				changeScores[2], mScores[2], changeScores[3], mScores[3], 
				"", "", action);
		addDetail(detail);
		saveStatesForTmp();
	}
	
	/**
	 * 分析流局满贯的分值变化
	 * 
	 * @param isManGuans
	 * @return
	 */
	public abstract int[] analysisLiujumanguan(boolean[] isManGuans, ResultList[] resultLists);
	
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
	public abstract int[] analysisZimo(int winIndex, int fan, int fu,
			int juCount, int roundCount, int lizhiCount, 
			boolean isBao, int baoId, ResultList[] resultLists);
	
	/**
	 * 自摸计算（精简）
	 * 
	 * @param winIndex
	 * @return
	 */
	public abstract int[] analysisZimo(int winIndex, ResultList[] resultLists);
	
	/**
	 * 设置玩家自摸
	 * 
	 * @param changeScores
	 * @param winIndex
	 * @param baoId
	 */
	public abstract void setZimo(int[] changeScores, int winIndex, 
			String spectrum, int fan, int fu, int env, boolean isBao, int baoId);
	
	/**
	 * 设置玩家自摸（精简）
	 * 
	 * @param changeScores
	 * @param winIndex
	 */
	public abstract void setZimo(int[] changeScores, int winIndex);
	
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
	public abstract int[] analysisBomb(int bombIndex, int[] winIndexs, int[] fan, int[] fu,
			int juCount, int roundCount, int lizhiCount, boolean[] isBaos, int[] baoIds,
			ResultList[] resultLists);
	
	/**
	 * 放铳计算（精简）
	 * 
	 * @param bombIndex
	 * @param winIndexs
	 * @return
	 */
	public abstract int[] analysisBomb(int bombIndex, int[] winIndexs, ResultList[] resultLists);
	
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
	public abstract void setBomb(int[] changeScores, int bombIndex, int[] winIndexs, 
			String[] spectrums, int[] fans, int[] fus, int[] envs,
			boolean[] isBaos, int[] baoIndexs);
	
	/**
	 * 设置玩家放铳（精简）
	 * 
	 * @param changeScores
	 * @param bombIndex
	 * @param winIndexs
	 */
	public abstract void setBomb(int[] changeScores, int bombIndex, int[] winIndexs);
	
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
	 * 获取当前庄家玩家索引
	 * 
	 * @return
	 */
	public int getDealer() {
		switch (mMemberCount) {
		case 2:
			if (mJuCount[mFengCount] == 0) {
				return 0;
			} else {
				return 2;
			}
		case 3:
		case 4:
		default:
			return mJuCount[mFengCount];
		}
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
		int dealer = getDealer();
		int wind = (index + 4 - dealer) % 4;
		if (mMemberCount == 3) {
			if (dealer == 1 && index == 0) {
				wind = 2;
			} else if (dealer == 2) {
				if (index == 0) {
					wind = 1;
				} else if (index == 1) {
					wind = 2;
				}
			}
		}
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
	public void setPlayers(int member, Player east, Player south, Player west, Player north) {
		mMemberCount = member;
		mPlayers[0] = getNPCPlayerIfNull(east);
		if (member > 2) {
			mPlayers[1] = getNPCPlayerIfNull(south);
		} else {
			mPlayers[1] = Player.createNullPlayer();
		}
		mPlayers[2] = getNPCPlayerIfNull(west);
		if (member > 3) {
			mPlayers[3] = getNPCPlayerIfNull(north);
		} else {
			mPlayers[3] = Player.createNullPlayer();
		}
	}
	
	/**
	 * 中途更改玩家
	 * 
	 * @param index
	 * @param newPlayer
	 */
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
	 * 获取NPC玩家
	 * 
	 * @param player
	 * @return
	 */
	public Player getNPCPlayerIfNull(Player player) {
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
	 * 检查是否有玩家起飞
	 * 
	 * @return
	 */
	public boolean checkPlayerFly() {
		if (mNoFly) { // 无击飞模式，分值负数继续游戏
			return false;
		}
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
	public abstract int continueRound();
	
	/**
	 * 流庄
	 * 
	 */
	public abstract int nextRound();
	
	/**
	 * 判断是否符合南入\西入
	 * 
	 */
	protected abstract boolean isAllLower();
	
	/**
	 * 复原到上一局状态
	 * 
	 * @return 立直状态
	 */
	public abstract int[] revokeLastRound();
	
	/**
	 * 复原玩家立直状态
	 * 
	 * @return
	 */
	protected abstract int[] revokeLizhiState();
	
	/**
	 * 获取游戏结果
	 * 
	 * @return 立直状态
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
	 * 设置临时文件保存地址
	 * 
	 * @param dir
	 */
	public void initStoreDir(String storeDir) {		
		this.mTmpFileDir = storeDir;
	}
	
	/**
	 * 检查上次中断情况
	 * 
	 * @return
	 */
	public boolean checkLastSaveStates() {
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
	 * 清除缓存
	 * 
	 */
	public void clearStatesForTmp() {
		File tmpFile = new File(mTmpFileDir, FILE_INTERRUPT);
		if (tmpFile.exists()) tmpFile.delete();	
	}
	
	/**
	 * 临时保存当前所有操作
	 * 
	 */
	public abstract void saveStatesForTmp();
	
	/**
	 * 恢复上次中断信息
	 * 
	 * @return
	 */
	public abstract boolean restoreStatesForTmp();
	
	/**
	 * 玩家个人信息
	 */
	public static class SpectrumData {
		
		public int mOrgIndex; // 原始索引
		public boolean mTingPai; // 玩家听牌状态
		public int mLiZhi; // 玩家立直状态
		public int mFan; // 番数
		public int mFu; // 符数
		public String mSpectrum;
		public int mEnv;
		public boolean isEnableBaopai;
		public int mBaopai; // 包牌玩家
		
		public SpectrumData(int index) {
			mOrgIndex = index;
		}
		
		public void setResult(int fan, int fu, String sperctrum, int env) {
			mFan = fan;
			mFu = fu;
			mSpectrum = sperctrum;
			mEnv = env;
		}
		
		public void setEnableBaopai(boolean enable) {
			isEnableBaopai = enable;
		}
		
		public void setBaopaiPlayer(int index) {
			mBaopai = index;
		}
		
		public void reset() {
			resetByNewRound();
		}
		
		public void resetByNewRound() {
			mTingPai = false;
			mLiZhi = 0;
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
		// 显示结果
		public void onResult(Player[] players, int[] scores, int[] ranks, float[] mas);
	}
	
}

package com.mahjong.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mahjong.model.MjAction;
import com.mahjong.model.MjDetail;
import com.mahjong.model.MjResult;

public class AnalysisTool {

	private MjResult mResult;
	private List<MjDetail> mDetails;
	private Map<String, Integer> playerMap;
	private int[] lizhiCounts = {0, 0, 0, 0};
	private List<Integer> lizhiMvps = new ArrayList<Integer>();
	private int[] heCounts = {0, 0, 0, 0};
	private int[] heMaxs = {0, 0, 0, 0};
	private int[] heSums = {0, 0, 0, 0};
	private List<Integer> heMvps = new ArrayList<Integer>();
	private int[] bombCounts = {0, 0, 0, 0};
	private int[] bombMaxs = {0, 0, 0, 0};
	private int[] bombSums = {0, 0, 0, 0};
	private List<Integer> bombMvps = new ArrayList<Integer>();
	private int dataLen = 0;
	private int[][] mScores  = new int[4][1];
	private boolean[] chickens = {true, true, true, true};
	private boolean[] flys = {false, false, false, false};
	
	public AnalysisTool(MjResult result, List<MjDetail> details) {
		this.mResult = result;
		this.mDetails = details;
		this.playerMap = new HashMap<String, Integer>();				
		String[] ids= mResult.getIds();
		for (int i = 0; i < ids.length; i++) {
			playerMap.put(ids[i], i);
		}
		analysis();
	}
	
	public boolean[] getFlyPlayers() {
		return flys;
	}
	
	public boolean[] getChickens() {
		return chickens;
	}
	
	public int[] getLizhiCounts() {
		return lizhiCounts;
	}
	
	public List<Integer> getLizhiMvps() {
		return lizhiMvps;
	}
	
	public int[] getHeCounts() {
		return heCounts;
	}
	
	public List<Integer> getHeMvps() {
		return heMvps;
	}
	
	public int[] getBombCounts() {
		return bombCounts;
	}
	
	public List<Integer> getBombMvps() {
		return bombMvps;
	}
	
	public int getBaseScore() {
		return mResult.getBasePoint();
	}
	
	public int getDataLen() {
		return dataLen;
	}
	
	public int[] getScore1st() {
		int[] ranks = mResult.getRanks();
		for (int i = 0; i < ranks.length; i++) {
			if (ranks[i] == 1) {
				return mScores[i];
			}
		}
		return null;
	}
	
	public int[] getScore2nd() {
		int[] ranks = mResult.getRanks();
		for (int i = 0; i < ranks.length; i++) {
			if (ranks[i] == 2) {
				return mScores[i];
			}
		}
		return null;
	}
	
	public int[] getScore3rd() {
		int[] ranks = mResult.getRanks();
		for (int i = 0; i < ranks.length; i++) {
			if (ranks[i] == 3) {
				return mScores[i];
			}
		}
		return null;
	}
	
	public int[] getScore4th() {
		int[] ranks = mResult.getRanks();
		for (int i = 0; i < ranks.length; i++) {
			if (ranks[i] == 4) {
				return mScores[i];
			}
		}
		return null;
	}
	
	private void analysis() {
		Arrays.fill(lizhiCounts, 0);
		Arrays.fill(heCounts, 0);
		Arrays.fill(bombCounts, 0);
		Arrays.fill(heMaxs, 0);
		Arrays.fill(heSums, 0);
		Arrays.fill(bombMaxs, 0);
		Arrays.fill(bombSums, 0);
		Arrays.fill(flys, false);
		Arrays.fill(chickens, true);
		int lizhiMvpIndex = -1;
		lizhiMvps.clear();
		int heMvpIndex = -1;
		heMvps.clear();
		int bombMvpIndex = -1;
		bombMvps.clear();
		dataLen = 0;
		int tmpHeMax = 0;
		int tmpHeSum = 0;
		int tmpBombMax = 0;
		int tmpBombSum = 0;
		// 统计起飞玩家
		int[] finalScores = mResult.getPoints();
		for (int i = 0; i < finalScores.length; i++) {
			if (finalScores[i] < 0) {
				flys[i] = true;
			}
		}
		// 统计立直、和牌、放铳数量、烧鸡玩家
		if (mDetails != null) {
			dataLen = mDetails.size();
			for (MjDetail detail : mDetails) {				
				MjAction action = detail.getAction();
				int[] changeScores = detail.getChangeScores();
				Integer index;
				switch (action.getActionID()) {
				case MjAction.ACTION_LIZHI:
					index = playerMap.get(action.id0);
					if (index != null) {
						// 立直数量最大值
						lizhiMvpIndex = checkMvpIndex(index, lizhiMvpIndex, lizhiCounts);
					}					
					dataLen--;
					break;
				case MjAction.ACTION_ZIMO:
					index = playerMap.get(action.id0);
					if (index != null) {
						// 单次和牌最大值
						if (heMaxs[index] < changeScores[index]) heMaxs[index] = changeScores[index];
						if (tmpHeMax < heMaxs[index]) tmpHeMax = heMaxs[index];
						// 累计和牌最大值
						heSums[index] += changeScores[index];
						if (tmpHeSum < heSums[index]) tmpHeSum = heSums[index];
						// 和牌数量最大值
						heMvpIndex = checkMvpIndex(index, heMvpIndex, heCounts);
						// 检测烧鸡状态
						if (chickens[index]) chickens[index] = false;
					}
					break;
				case MjAction.ACTION_BOMB:
					index = playerMap.get(action.id3);
					if (index != null) {
						// 单次放铳最大值
						if (bombMaxs[index] > changeScores[index]) bombMaxs[index] = changeScores[index];
						if (tmpBombMax > bombMaxs[index]) tmpBombMax = bombMaxs[index];
						// 累计放铳最大值
						bombSums[index] += changeScores[index];
						if (tmpBombSum > bombSums[index]) tmpBombSum = bombSums[index];
						// 放铳数量最大值
						bombMvpIndex = checkMvpIndex(index, bombMvpIndex, bombCounts);
					}
					index = playerMap.get(action.id0);
					if (index != null) {
						// 单次和牌最大值
						if (heMaxs[index] < changeScores[index]) heMaxs[index] = changeScores[index];
						if (tmpHeMax < heMaxs[index]) tmpHeMax = heMaxs[index];
						// 累计和牌最大值
						heSums[index] += changeScores[index];
						if (tmpHeSum < heSums[index]) tmpHeSum = heSums[index];
						// 和牌数量最大值
						heMvpIndex = checkMvpIndex(index, heMvpIndex, heCounts);
						// 检测烧鸡状态
						if (chickens[index]) chickens[index] = false;
					}
					if (action.tag1 > 1) {
						index = playerMap.get(action.id1);
						if (index != null) {
							// 单次和牌最大值
							if (heMaxs[index] < changeScores[index]) heMaxs[index] = changeScores[index];
							if (tmpHeMax < heMaxs[index]) tmpHeMax = heMaxs[index];
							// 累计和牌最大值
							heSums[index] += changeScores[index];
							if (tmpHeSum < heSums[index]) tmpHeSum = heSums[index];
							// 和牌数量最大值
							heMvpIndex = checkMvpIndex(index, heMvpIndex, heCounts);
							// 检测烧鸡状态
							if (chickens[index]) chickens[index] = false;
						}
					}
					if (action.tag1 > 2) {
						index = playerMap.get(action.id2);
						if (index != null) {
							// 单次和牌最大值
							if (heMaxs[index] < changeScores[index]) heMaxs[index] = changeScores[index];
							if (tmpHeMax < heMaxs[index]) tmpHeMax = heMaxs[index];
							// 累计和牌最大值
							heSums[index] += changeScores[index];
							if (tmpHeSum < heSums[index]) tmpHeSum = heSums[index];
							// 和牌数量最大值
							heMvpIndex = checkMvpIndex(index, heMvpIndex, heCounts);
							// 检测烧鸡状态
							if (chickens[index]) chickens[index] = false;
						}
					}
					break;
				default:
					break;
				}
			}
		}
		// 计算MVP玩家
		if (lizhiMvpIndex >= 0) {
			int count = lizhiCounts[lizhiMvpIndex];
			for (int i = 0; i < lizhiCounts.length; i++) {
				if (lizhiCounts[i] == count) {
					lizhiMvps.add(i);
				}
			}
		}
		if (heMvpIndex >= 0) { // 计算优先级：数量>最大和牌点数>累计和牌点数
			// 统计最大和牌数量
			int count = heCounts[heMvpIndex];
			for (int i = 0; i < heCounts.length; i++) {
				if (heCounts[i] == count) {
					heMvps.add(i);
				}
			}
			// 统计最大和牌点数
			if (heMvps.size() > 1) {
				Iterator<Integer> iterator = heMvps.iterator();
				while (iterator.hasNext()) {
					int index = iterator.next();
					if (heMaxs[index] < tmpHeMax) {
						iterator.remove();
					}
				}
			}
			// 统计累计和牌点数
			if (heMvps.size() > 1) {
				Iterator<Integer> iterator = heMvps.iterator();
				while (iterator.hasNext()) {
					int index = iterator.next();
					if (heSums[index] < tmpHeSum) {
						iterator.remove();
					}
				}
			}
		}
		if (bombMvpIndex >= 0) { // 计算优先级：数量>最大放铳数>累计放铳数
			// 统计最大放铳数量
			int count = bombCounts[bombMvpIndex];
			for (int i = 0; i < bombCounts.length; i++) {
				if (bombCounts[i] == count) {
					bombMvps.add(i);
				}
			}
			// 统计最大放铳点数
			if (bombMvps.size() > 1) {
				Iterator<Integer> iterator = bombMvps.iterator();
				while (iterator.hasNext()) {
					int index = iterator.next();
					if (bombMaxs[index] > tmpBombMax) {
						iterator.remove();
					}
				}
			}
			// 统计累计放铳点数
			if (bombMvps.size() > 1) {
				Iterator<Integer> iterator = bombMvps.iterator();
				while (iterator.hasNext()) {
					int index = iterator.next();
					if (bombSums[index] > tmpBombSum) {
						iterator.remove();
					}
				}
			}
		}
		// 统计成绩
		if (mDetails != null) {
			if (dataLen > 0) {
				dataLen += 1;
				mScores = new int[4][dataLen];
				mScores[0][0] = getBaseScore();
				mScores[1][0] = getBaseScore();
				mScores[2][0] = getBaseScore();
				mScores[3][0] = getBaseScore();
				int index = 1;
				for (MjDetail detail : mDetails) {
					if (detail.getActionId() == MjAction.ACTION_LIZHI) {
						continue;
					}
					mScores[0][index] = detail.getFinalEast();
					mScores[1][index] = detail.getFinalSouth();
					mScores[2][index] = detail.getFinalWest();
					mScores[3][index] = detail.getFinalNorth();
					index++;
				}
			} else { // 特殊情况：直接终局
				dataLen = 2;
				mScores = new int[4][2];
				mScores[0][0] = getBaseScore();
				mScores[0][1] = mResult.getEastPoint();
				mScores[1][0] = getBaseScore();
				mScores[1][1] = mResult.getSouthPoint();
				mScores[2][0] = getBaseScore();
				mScores[2][1] = mResult.getWestPoint();
				mScores[3][0] = getBaseScore();
				mScores[3][1] = mResult.getNorthPoint();
			}
		}
		
		
	}
	
	private int checkMvpIndex(int index, int mvpIndex, int[] counts) {
		counts[index]++;
		if (mvpIndex < 0
				|| counts[index] > counts[mvpIndex]) {
			mvpIndex = index;
		}
		return mvpIndex;
	}
	
}

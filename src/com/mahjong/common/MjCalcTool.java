package com.mahjong.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mahjong.data.jpn.AgariIndex;
import com.mahjong.data.jpn.Game;
import com.mahjong.data.jpn.GroupState;
import com.mahjong.data.jpn.Groups;
import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.Groups.Junko;
import com.mahjong.data.jpn.Groups.Pung;
import com.mahjong.data.jpn.Groups.Gang;
import com.mahjong.data.jpn.GroupCollection;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Score;
import com.mahjong.data.jpn.ScoreSystem;
import com.mahjong.data.jpn.Tile;
import com.mahjong.data.jpn.TileCollection;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuValue;
import com.mahjong.item.YakuBean;
import com.mahjong.ui.MahjongSpectrum;

public class MjCalcTool {

	private static final MjWind[][] FuLouWind = {
		{MjWind.North, MjWind.East, MjWind.South, MjWind.West}, // left
		{MjWind.West, MjWind.North, MjWind.East, MjWind.South}, // center
		{MjWind.South, MjWind.West, MjWind.North, MjWind.East}, // right
	};
			
	private static Game mGame = new Game();
	
	public static class GameResult {
		
		public boolean canHupai = false;
		public boolean hasYaku = false;
		public List<Tile> allTiles;
		public List<Tile> handTiles;
		public Tile addedTile;
		public List<Group> fulouGroups;
		public List<Group[]> menqingGroups;
		public MahjongSpectrum spectrum;
		public boolean isLiZhi;
		public boolean isDoubleLiZhi;
		public boolean isZiMo;
		public boolean isYiFa; 
		public boolean isFirstRound;
		public boolean isFinalPick;
		public boolean isQiangGang;
		public boolean isLingshang;
		public MjWind groundWind;
		public MjWind selfWind;
		public MjWind addedWind;
		public int env;
		public Score score;
		
		public GameResult(MjWind groundWind, MjWind selfWind, MjWind addedWind) {
			this.groundWind = groundWind;
			this.selfWind = selfWind;
			this.addedWind = addedWind;			
		}
		
		public void setData(
				boolean isLiZhi, boolean isDoubleLiZhi, boolean isZiMo, boolean isYiFa, 
				boolean isFirstRound, boolean isFinalPick, boolean isQiangGang, boolean isLingshang) {			
			this.isLiZhi = isLiZhi;
			this.isDoubleLiZhi = isDoubleLiZhi;
			this.isZiMo = isZiMo;
			this.isYiFa = isYiFa;
			this.isFirstRound = isFirstRound;
			this.isFinalPick = isFinalPick;
			this.isQiangGang = isQiangGang;
			this.isLingshang = isLingshang;
		}
		
		public void setResult(boolean canHupai,
				List<Tile> allTiles, List<Tile> handTiles, Tile addedTile,
				List<Group> fulouGroups, List<Group[]> menqingGroups) {
			this.canHupai = canHupai;
			this.allTiles = allTiles;
			this.handTiles = handTiles;
			this.addedTile = addedTile;
			this.fulouGroups = fulouGroups;
			this.menqingGroups = menqingGroups;
			reCalcEnv();
		}
		
		public void setScore(Score score) {
			this.score = score;
			this.hasYaku = score.hasYaku();
		}
		
		public void reCalcDora(List<MjCard> indicators, List<MjCard> indicatorsIns) {
			if (canHupai) {
				calcDoras(allTiles, indicators, indicatorsIns, isLiZhi, isDoubleLiZhi);				
			}
		}
		
		public void reCalcEnv() {
			this.env = convert2JpnEnvironment(isLiZhi, isDoubleLiZhi, isFirstRound, isFinalPick, 
					isZiMo, isYiFa, isQiangGang, isLingshang, groundWind, selfWind, isMenQing(fulouGroups));
		}
	}

	public static class Game34Result {

		public int env; // 环境变量
		public int calcWay; // 计量方式：0：分值优先，1：番数优先
		public List<Tile> addedTiles; // 和牌（最高分时）
		public List<List<Tile>> handTiles; // 门清牌（最高分时）
		public List<Score> scores; // 存在多种结果时的分值
		public List<long[]> cmpNums; // 用于快速比较的数字，将14张牌转换成2个long数字
		public int[] doras; // 宝牌组
		public int[] doraIns; // 里宝牌组
		public Score maxScore; // 最大结果
		
		public int level; // 显示等级：1、一般情况；2、不听宝牌
		public Score level1Score; // 当显示2级结果时，不能超过的1级结果的上限
		public boolean isInit = false;
		
		public Game34Result(int level) {
			this.level = level;
		}
		
		public void init(int env, int calcWay, int[] doras, int[] doraIns) {
			this.env = env;
			this.calcWay = calcWay;
			this.addedTiles = new ArrayList<Tile>();
			this.handTiles = new ArrayList<List<Tile>>();
			this.scores = new ArrayList<Score>();
			this.cmpNums = new ArrayList<long[]>();
			this.doras = doras;
			this.doraIns = doraIns;
			this.maxScore = null;
			this.isInit = true;
		}
		
		public boolean isInit() {
			return isInit;
		}
		
		public void setLevel1Score(Score score) {
			this.level1Score = score;
		}
		
		public void reset() {
			isInit = false;
		}
		
		public void setData(int env, List<Tile> addedTiles, 
			List<List<Tile>> handTiles,  List<Score> scores) {
			this.env = env;
			this.addedTiles = addedTiles;
			this.handTiles = handTiles;
			this.scores = scores;
		}
		
		public MjCard getAddedCards(int index) {
			MjCard card = null;
			if (addedTiles.size() > index) {
				card = convertTile2MjCard(addedTiles.get(index));
			}
			return card;
		}
		
		public List<MjCard> getHandCards(int index) {
			List<MjCard> cardList = new ArrayList<MjCard>();
			if (handTiles.size() > index) {
				List<Tile> tileList = handTiles.get(index);
				for (int i = 0; i < tileList.size(); i++) {
					cardList.add(convertTile2MjCard(tileList.get(i)));
				}
			}
			return cardList;
		}
		
		public Score getScore(int index) {
			Score score = null;
			if (scores.size() > index) {
				score = scores.get(index);
			}
			return score;
		}
		
		public boolean hasYaku() {
			return (scores != null && scores.size() > 0);
		}
		
		public int size() {
			return scores != null ? scores.size() : 0;
		}
		
		public int getLevel() {
			return level;
		}
		
		public boolean isAllAddedTileDoras() {
			if (addedTiles == null || addedTiles.size() == 0) return false;
			int count = 0;
			for (int i = 0; i < doras.length; i++) {
				if (doras[i] < 0) count++;
			}
			if (count == doras.length) return false;
			for (Tile addedTile : addedTiles) {
				for (int dora : doras) {
					if (dora < 0) continue;
					if (dora != addedTile.Value()) return false;
				}
			}
			return true;
		}
		
		public void LogMaxScore(Tile tmpAddedTile, List<Tile> tmpHandTiles, 
				List<Group> allGroups) {
			List<Tile> tmpAllTiles = new ArrayList<Tile>(tmpHandTiles);
			tmpAllTiles.add(tmpAddedTile);
			List<IGroups> gList = new ArrayList<IGroups>();
			if (allGroups != null) gList.add(new GroupCollection(allGroups));
			ITiles tiles = new TileCollection(tmpAllTiles, tmpAllTiles, tmpAddedTile);
			Score tmpScore = mGame.getScore(tiles, gList, env);	
			if (tmpScore == null || !tmpScore.hasYaku()) {
				return;
			}
			if (level > 1) { // 当level大于1时，所有结果均需小于上级最大结果
				int levelCmp = calcWay == 0 ? ScoreSystem.compareForGame34(level1Score, tmpScore) :
					ScoreSystem.compare(level1Score, tmpScore);
				if (levelCmp <= 0) return;
			}
			long[] cmpNum = convert2CmpNums(tmpAllTiles, tmpAddedTile);
			if (maxScore == null) {
				maxScore = tmpScore;
				scores.add(tmpScore);
				addedTiles.add(tmpAddedTile);
				handTiles.add(tmpHandTiles);
				cmpNums.add(cmpNum);
			} else {
				int cmp = calcWay == 0 ? ScoreSystem.compareForGame34(maxScore, tmpScore) :
					ScoreSystem.compare(maxScore, tmpScore);
				if (cmp <= 0) {
					if (cmp != 0) {
						scores.clear();
						addedTiles.clear();
						handTiles.clear();
						cmpNums.clear();
					}
					int replaceIndex = -1;
					for (int i = 0; i < cmpNums.size(); i++) {
						long[] tmpNum = cmpNums.get(i);
						if (tmpNum[0] == cmpNum[0] && tmpNum[1] == cmpNum[1]) {
							// 就算是牌谱相同，但是不同的组合也会导致平和的缺失，此时要比较总番数来替换组合
							if (calcWay == 0 && tmpScore.FullYaku() == 0
									&& tmpScore.AllFanValue() > scores.get(i).AllFanValue()) {
								replaceIndex = i;
								break;
							}
							return;
						}
					}
					maxScore = tmpScore;
					if (replaceIndex >= 0) {
						scores.set(replaceIndex, tmpScore);
						addedTiles.set(replaceIndex, tmpAddedTile);
						handTiles.set(replaceIndex, tmpHandTiles);
						cmpNums.set(replaceIndex, cmpNum);
					} else {
						scores.add(tmpScore);
						addedTiles.add(tmpAddedTile);
						handTiles.add(tmpHandTiles);
						cmpNums.add(cmpNum);
					}
				}
			} 
		}
				
		private long[] convert2CmpNums(List<Tile> allTiles, Tile addedTile) {
			long[] cmpNum = {0, 0};
			int index = 0;
			for (int i = 0; i < 7; i++) {
				cmpNum[0] |= ((long)allTiles.get(i).Value() << index); 
				index += 5;
			}
			index = 0;
			for (int i = 7; i < 14; i++) {
				cmpNum[1] |= ((long)allTiles.get(i).Value() << index); 
				index += 5;
			}
			cmpNum[1] |= ((long)addedTile.Value() << index); 
			return cmpNum;
		}
		
	}
	
	/**
	 * 检测牌谱是否能胡牌
	 * 
	 * @param gameResult
	 * @param darkNums
	 * @param brightNums
	 * @param winNum
	 * @param indicators
	 * @param indicatorsIns
	 * @return
	 */
	public static boolean checkCanHupai(GameResult gameResult,
			List<MjCard> darkNums, List<MjCardPairs> brightNums, MjCard winNum,
			List<MjCard> indicators, List<MjCard> indicatorsIns) {
		List<Tile> allTiles = new ArrayList<Tile>();	// 所有牌
		List<Tile> handTiles = new ArrayList<Tile>();	// 门清牌
		// 添加和牌
		Tile addedTile = convertCard2JpnTile(winNum, 
				gameResult.isZiMo ? gameResult.selfWind : gameResult.addedWind,
				true);
		allTiles.add(addedTile);
		handTiles.add(addedTile);
		// 添加门清牌
		for (MjCard card : darkNums) {
			Tile tile = convertCard2JpnTile(card, gameResult.selfWind, true);
			allTiles.add(tile);
			handTiles.add(tile);
		}
		// 添加副露牌
		List<Group> fulouGroups = new ArrayList<Group>();
		for (MjCardPairs pairs : brightNums) {
			Group group = convertCardPair2JpnGroup(allTiles, pairs, gameResult.selfWind, true);
			fulouGroups.add(group);
		}
		// 计算宝牌
		calcDoras(allTiles, indicators, indicatorsIns, gameResult.isLiZhi, gameResult.isDoubleLiZhi);
		// 检测是否能胡牌
		List<Group[]> menqingGroups = new ArrayList<Group[]>();
		boolean result = AgariIndex.done(handTiles, addedTile.Value(), menqingGroups);	
		gameResult.setResult(result, allTiles, handTiles, addedTile, fulouGroups, menqingGroups);
		return result;
	}
	
	/**
	 * 检测牌谱是否有役
	 * 
	 * @param gameResult
	 * @return
	 */
	public static boolean calcToScore(GameResult gameResult) {
		List<IGroups> gList = new ArrayList<IGroups>();
		for (Group[] gs : gameResult.menqingGroups) {
			List<Group> allGroups = new ArrayList<Group>(gameResult.fulouGroups);
			for (Group g : gs) {
				allGroups.add(g);
			}
			gList.add(new GroupCollection(allGroups));
		}
		ITiles tiles = new TileCollection(gameResult.allTiles, gameResult.handTiles, gameResult.addedTile);
		Score score = mGame.getScore(tiles, gList, gameResult.env);
		gameResult.setScore(score);
		return score.hasYaku();
	}
	
	/**
	 * 计算牌谱结果并转为文字输出
	 * 
	 */
	public static String calcToResultText(List<MjCard> darkNums, List<MjCardPairs> brightNums, MjCard winNum,
			boolean isDealer, boolean isLiZhi, boolean isDoubleLiZhi,
			boolean isFirstRound, boolean isFinalPick, boolean isZiMo, 
			boolean isYiFa, boolean isQiangGang, boolean isLingshang,
			MjWind groundWind, MjWind selfWind, 
			int lizhiCount, int roundCount,
			List<MjCard> indicators, List<MjCard> indicatorsIns) {
		Score score = calcToResultScore(darkNums, brightNums, winNum, 
				isDealer, isLiZhi, isDoubleLiZhi, 
				isFirstRound, isFinalPick, isZiMo, isYiFa, isQiangGang, isLingshang, 
				groundWind, selfWind, lizhiCount, roundCount, indicators, indicatorsIns);
		if (score != null) {
			StringBuffer sBuffer = new StringBuffer();
			sBuffer.append(score.toString());
			if (score.hasYaku()) {
				int basePoint = ScoreSystem.GetBasePoint(score);
				String s;
				if (isDealer) {
					if (isZiMo) {
						s = "庄家自摸：闲家每人付" + ScoreSystem.GetPoint(basePoint, 2);
						if (roundCount > 0) s += ("+" + roundCount * 100);
						s += "点";
					} else {
						s = "庄家荣和：点炮者付" + ScoreSystem.GetPoint(basePoint, 6);
						if (roundCount > 0) s += ("+" + roundCount * 300);
						s += "点";
					}
				} else {
					if (isZiMo) {
						String s1 = "闲家每人付" + ScoreSystem.GetPoint(basePoint, 1);
						if (roundCount > 0) s1 += ("+" + roundCount * 100);
						s1 += "点,";
						String s2 = "庄家付" + ScoreSystem.GetPoint(basePoint, 2);
						if (roundCount > 0) s2 += ("+" + roundCount * 100);
						s2 += "点";
						s = "闲家自摸：" + s1 + s2;
					} else {
						s = "闲家荣和：点炮者付" + ScoreSystem.GetPoint(basePoint, 4);
						if (roundCount > 0) s += ("+" + roundCount * 300);
						s += "点";
					}
				}
				sBuffer.append("\n" + s);
				if (lizhiCount > 0) {
					sBuffer.append("\n\t\t+立直棒" + lizhiCount * 1000 + "点");
				}
				sBuffer.append("\n番种：");
				for (YakuValue value : score.YakuValues()) {
					sBuffer.append("\n\t\t" + value.toString());
				}
			}	
			return sBuffer.toString();
		} else {
			return "秘籍：混水摸鱼";
		}		
	}
	
	/**
	 * 计算牌谱结果
	 * 
	 */
	public static Score calcToResultScore(List<MjCard> darkNums, List<MjCardPairs> brightNums, MjCard winNum,
			boolean isDealer, boolean isLiZhi, boolean isDoubleLiZhi,
			boolean isFirstRound, boolean isFinalPick, boolean isZiMo, 
			boolean isYiFa, boolean isQiangGang, boolean isLingshang,
			MjWind groundWind, MjWind selfWind, 
			int lizhiCount, int roundCount,
			List<MjCard> indicators, List<MjCard> indicatorsIns) {
		List<Tile> allTiles = new ArrayList<Tile>();	// 所有牌
		List<Tile> handTiles = new ArrayList<Tile>();	// 门清牌
		// 添加和牌
		Tile addedTile = convertCard2JpnTile(winNum, isZiMo ? selfWind : ramdomWind(selfWind), true);
		allTiles.add(addedTile);
		handTiles.add(addedTile);
		// 添加门清牌
		for (MjCard card : darkNums) {
			Tile tile = convertCard2JpnTile(card, selfWind, true);
			allTiles.add(tile);
			handTiles.add(tile);
		}
		// 添加副露牌
		List<Group> fulouGroups = new ArrayList<Group>();
		for (MjCardPairs pairs : brightNums) {
			Group group = convertCardPair2JpnGroup(allTiles, pairs, selfWind, true);
			fulouGroups.add(group);
		}
		// 计算宝牌
		calcDoras(allTiles, indicators, indicatorsIns, isLiZhi, isDoubleLiZhi);
		// 检测是否能胡牌
		List<Group[]> menqingGroups = new ArrayList<Group[]>();
		boolean result = AgariIndex.done(handTiles, addedTile.Value(), menqingGroups);		
		if (result) {
			int env = convert2JpnEnvironment(isLiZhi, isDoubleLiZhi, isFirstRound, isFinalPick, 
					isZiMo, isYiFa, isQiangGang, isLingshang, groundWind, selfWind, isMenQing(fulouGroups));
			Score score = calcToScore(env, menqingGroups, fulouGroups, allTiles, handTiles, addedTile);
			return score;
		}
		return null;
	}
	
	/**
	 * 将确定能胡牌的组合计算结果
	 * 
	 */
	private static Score calcToScore(int env, 
			List<Group[]> menqingGroups, List<Group> fulouGroups,
			List<Tile> allTiles, List<Tile> handTiles, Tile addedTile) {
		List<IGroups> gList = new ArrayList<IGroups>();
		for (Group[] gs : menqingGroups) {
			List<Group> allGroups = new ArrayList<Group>(fulouGroups);
			for (Group g : gs) {
				allGroups.add(g);
			}
			gList.add(new GroupCollection(allGroups));
		}
		ITiles tiles = new TileCollection(allTiles, handTiles, addedTile);
		Score score = mGame.getScore(tiles, gList, env);
		return score;
	}
	
	/**
	 * 十七步算法：根据34张牌挑选出13张最优解牌谱
	 * --限定条件（确定：门清，可能：立直、一发、河底，无赤宝牌）
	 */
	public static boolean calcGame34ToResultScores(int calcWay,
			Game34Result game34Result, List<MjCard> cardNums,
			boolean isDealer, boolean isLiZhi, boolean isYiFa, boolean isFinalPick,
			MjWind groundWind, MjWind selfWind, 
			int lizhiCount, int roundCount,
			List<MjCard> indicators, List<MjCard> indicatorsIns) {
		// 用于计算的分值的元素
		int env = convert2JpnEnvironment(isLiZhi, false, false, isFinalPick,
				false, isYiFa, false, false, groundWind, selfWind, true); // 环境变量
		// 计算宝牌
		int[] doras = convertCard2TileDoras(indicators);
		// 计算里宝牌
		int[] doraIns = null;
		if (isLiZhi) {
			doraIns = convertCard2TileDoras(indicatorsIns);
		} else {
			doraIns = new int[indicatorsIns.size()];
			Arrays.fill(doraIns, -1);
		}
		game34Result.init(env, calcWay, doras, doraIns);
		// 记录所有牌，并分类
		Tile[] tileMap = new Tile[34]; // 记录所有存在的牌
		int[] tileCounts = new int[34];// 记录所有存在的牌对应的数量
        Arrays.fill(tileCounts, 0);
		List<Integer> pairTiles = new ArrayList<Integer>(); // 记录所有的对子
		List<Integer> pungTiles = new ArrayList<Integer>(); // 记录所有的刻子		
		long byteTile = 0; // 用二进制记录存在的牌
		for (MjCard card : cardNums) {
			Tile tile = convertCard2JpnTile(card, selfWind, false);
			tile.calDora(doras);
			tile.calInDora(doraIns);
			tileMap[tile.Value()] = tile;
			tileCounts[tile.Value()]++;
			byteTile |= (1L << tile.Value());
			if (tileCounts[tile.Value()] == 2) { // 对子
				pairTiles.add(tile.Value());
			} else if (tileCounts[tile.Value()] == 3) { // 刻子
				pungTiles.add(tile.Value());
			}
		}
		// 记录所有可用的点炮牌，找出所有的顺子、缺一张的顺子
		boolean[] canBombs = new boolean[34];
		Tile[] bombTiles = new Tile[34];
		Arrays.fill(canBombs, false);
		List<Integer> tileKeys = new ArrayList<Integer>(); // 记录所有存在的牌的索引
		List<Integer> junkoTiles = new ArrayList<Integer>(); // 记录所有的顺子
		List<Integer> junkoQbTiles = new ArrayList<Integer>(); // 记录所有缺边章的顺子，形如23缺1或4
		List<Integer> junkoQzTiles = new ArrayList<Integer>(); // 记录所有缺中章的顺子，形如13缺2
		long junkoByte = 0x3fe030180L; // 只含有8万、9万、8饼、9饼、8索、9索、东、南、西、北、白、发、中
		long junkoQbByte = 0x3fc020100L; // 只含有9万、9饼、9索、东、南、西、北、白、发、中
//		long junkoQzByte = junkoByte; // 只含有8万、9万、8饼、9饼、8索、9索、东、南、西、北、白、发、中
		for (int i = 0; i < tileCounts.length; i++) {
			if (tileCounts[i] < 4) {
				canBombs[i] = true;
				bombTiles[i] = new Tile(i, false, ramdomWind(selfWind));
				bombTiles[i].calDora(doras);
				bombTiles[i].calInDora(doraIns);
			}
			if (tileCounts[i] > 0) {
				tileKeys.add(i);
			}
			long tmpByte = 1L << i;
			if ((tmpByte & junkoByte) != tmpByte) {
				switch (tileCounts[i]) {
				case 4:
					if (tileCounts[i + 1] > 3 && tileCounts[i + 2] > 3) junkoTiles.add(i);
				case 3:
					if (tileCounts[i + 1] > 2 && tileCounts[i + 2] > 2) junkoTiles.add(i);
				case 2:
					if (tileCounts[i + 1] > 1 && tileCounts[i + 2] > 1) junkoTiles.add(i);
				case 1:
					if(tileCounts[i + 1] > 0 && tileCounts[i + 2] > 0) junkoTiles.add(i);
					if (tileCounts[i + 2] > 0) junkoQzTiles.add(i);
					break;
				case 0:
				default:
					break;
				}
			}
			if ((tmpByte & junkoQbByte) != tmpByte) {
				if (tileCounts[i] > 0 && tileCounts[i + 1] > 0) junkoQbTiles.add(i);
			}
		}
		// 判断特殊牌型（国士无双、七对子）
		// 1.国士无双
		calcGame34ForGuoShiWuShuang(game34Result, byteTile, tileMap, tileCounts, canBombs, bombTiles);
		// 2.七对子
		calcGame34ForQiDuiZi(game34Result, pairTiles, tileMap, tileKeys, canBombs, bombTiles);
		// 3.检测所有顺子、刻子的排列组合(单骑、听嵌章、双面听)
		calcGame34ForGroup(game34Result, junkoTiles, pungTiles, tileCounts, tileMap, tileKeys, 
				canBombs, bombTiles, pairTiles, junkoQbTiles, junkoQzTiles);
		return true;
	}
	
	/**
	 * 十七步算法：分析国士无双情况
	 * 
	 * @param game34Result
	 * @param byteTile
	 * @param tileMap
	 * @param tileCounts
	 * @param canBombs
	 * @param bombTiles
	 */
	private static void calcGame34ForGuoShiWuShuang(Game34Result game34Result,
			long byteTile, Tile[] tileMap, int[] tileCounts,
			boolean[] canBombs, Tile[] bombTiles) {
		long gswsByte = 0x3fc060301L; // 只含有1万、9万、1饼、9饼、1索、9索、东、南、西、北、白、发、中
		if ((gswsByte & byteTile) == gswsByte) { // 国士无双十三面
			List<Tile> tmpHandTiles = new ArrayList<Tile>();
			for (int yaojiu : JpnSetting.YaoJiu) { // 首先添加13张幺九牌
				tmpHandTiles.add(tileMap[yaojiu]);
			}			
			for (int yaojiu : JpnSetting.YaoJiu) { // 再遍历所有可用的点炮牌
				if (!canBombs[yaojiu]) continue;
				Tile tmpAddedTile = bombTiles[yaojiu];
				// 取分值最大的结果
				game34Result.LogMaxScore(tmpAddedTile, tmpHandTiles, null);
			}
		} else { // 国士无双缺一张
			for (int yaojiu : JpnSetting.YaoJiu) {
				long tmpGswsByte = gswsByte ^ (1L << yaojiu);
				if ((tmpGswsByte & byteTile) == tmpGswsByte) {
					// 判断是否存在2只及以上的幺九牌
					boolean isExistYaojiuAbove2 = false;
					List<Tile> yaojiuAbove2 = new ArrayList<Tile>();
					for (int yaojiu2 : JpnSetting.YaoJiu) {
						if (tileCounts[yaojiu2] >= 2) {
							isExistYaojiuAbove2 = true;
							yaojiuAbove2.add(tileMap[yaojiu2]);
						}
					}
					if (!isExistYaojiuAbove2) break; // 不存在则退出
					List<Tile> tmpHandTiles = new ArrayList<Tile>();
					for (int yaojiu2 : JpnSetting.YaoJiu) { 
						if (tileMap[yaojiu2] == null) continue; // 无此牌，则跳过
						long keyByte = 1L << yaojiu2;
						if ((keyByte & tmpGswsByte) != keyByte) continue; // 如果不是缺一张的幺九牌，则跳过
						tmpHandTiles.add(tileMap[yaojiu2]);
					}
					Tile tmpAddedTile = bombTiles[yaojiu]; // 点炮牌唯一
					for (Tile yaojiuTile : yaojiuAbove2) { // 遍历存在2张的幺九牌
						List<Tile> tmpHandTiles2 = new ArrayList<Tile>(tmpHandTiles);
						tmpHandTiles2.add(yaojiuTile);
						// 取分值最大的结果
						game34Result.LogMaxScore(tmpAddedTile, tmpHandTiles2, null);
					}
					break; // 符合其中一种，另外8种情况可以直接排除
				}
			}
		}
	}
	
	/**
	 * 十七步算法：分析七对子情况
	 * 
	 * @param game34Result
	 * @param pairTiles
	 * @param tileMap
	 * @param tileKeys
	 * @param canBombs
	 * @param bombTiles
	 */
	private static void calcGame34ForQiDuiZi(Game34Result game34Result,
			List<Integer> pairTiles, Tile[] tileMap, List<Integer> tileKeys,
			boolean[] canBombs, Tile[] bombTiles) {
		if (pairTiles.size() >= 6) {
			for (int i = 0; i < (1 << pairTiles.size()); i++) { // 2的n次方则为所有组合的次数
				int countOfbyte1 = CountOfByte1(i);
				if (countOfbyte1 == 6) { // 取所有6组不同对子的组合
					long tmpByte = 0;
					List<Tile> tmpHandTiles = new ArrayList<Tile>();
					for (int j = 0; j < pairTiles.size(); j++) {
			            if (((i >> j) & 1) == 1) { // 二进制位为1则加入
			            	int value = pairTiles.get(j);
			            	tmpByte |= (1L << value);
			            	tmpHandTiles.add(tileMap[value]);
			            	tmpHandTiles.add(tileMap[value]);
			            }
					}
					// 遍历所有可用的点炮牌
					for (int key : tileKeys) { 
						if (!canBombs[key]) continue; // 如果有4张相同的牌，则跳过
						long keyByte = 1L << key;
						if ((keyByte & tmpByte) == keyByte) continue; // 如果是6对组合中的值，则跳过
						List<Tile> tmpKeyHandTiles = new ArrayList<Tile>(tmpHandTiles);
						tmpKeyHandTiles.add(tileMap[key]);
						Tile tmpAddedTile = bombTiles[key];
						// 取分值最大的结果
						game34Result.LogMaxScore(tmpAddedTile, tmpKeyHandTiles, null);
					}
				}
			}			
		}
	}
	
	/**
	 * 十七步算法：分析所有顺子、刻子的排列组合(单骑、听嵌章、双面听)
	 * 
	 * @param game34Result
	 * @param junkoTiles
	 * @param pungTiles
	 * @param tileCounts
	 * @param tileMap
	 * @param tileKeys
	 * @param canBombs
	 * @param bombTiles
	 * @param pairTiles
	 * @param junkoQbTiles
	 * @param junkoQzTiles
	 */
	private static void calcGame34ForGroup(Game34Result game34Result,
			List<Integer> junkoTiles, List<Integer> pungTiles,
			int[] tileCounts, Tile[] tileMap, List<Integer> tileKeys,
			boolean[] canBombs, Tile[] bombTiles,
			List<Integer> pairTiles, List<Integer> junkoQbTiles, List<Integer> junkoQzTiles) {
		int junkoAndPungCount = junkoTiles.size() + pungTiles.size();
		if (junkoAndPungCount >= 3) {
			for (int i = 0; i < (1 << junkoAndPungCount); i++) { // 2的n次方则为所有组合的次数
				int countOfbyte1 = CountOfByte1(i);
				if (countOfbyte1 == 4 || countOfbyte1 == 3) { // 单骑找出4种组合， 听嵌章、双面听则找出3种
					boolean isOverload = false;
					int[] tmpTileCounts = tileCounts.clone();
					List<Tile> tmpHandTiles = new ArrayList<Tile>();
					List<Group> allGroups = new ArrayList<Group>();
					for (int j = 0; j < pungTiles.size(); j++) { // 从所有刻子中取组合
			            if (((i >> j) & 1) == 1) { // 二进制位为1则加入
			            	int value = pungTiles.get(j);
			            	if (tmpTileCounts[value] < 3) {isOverload = true; break;}
			            	tmpTileCounts[value] -= 3;
			            	tmpHandTiles.add(tileMap[value]);
			            	tmpHandTiles.add(tileMap[value]);
			            	tmpHandTiles.add(tileMap[value]);
			            	allGroups.add(new Groups.Pung(value, GroupState.MenQing));
			            }
					}
					if (isOverload) continue;
					for (int j = 0; j < junkoTiles.size(); j++) { // 从所有顺子中取组合
			            if (((i >> (j + pungTiles.size())) & 1) == 1) { // 二进制位为1则加入
			            	int value = junkoTiles.get(j);
			            	if (tmpTileCounts[value]-- <= 0) {isOverload = true; break;}
			            	tmpHandTiles.add(tileMap[value]);
			            	if (tmpTileCounts[value + 1]-- <= 0) {isOverload = true; break;}
			            	tmpHandTiles.add(tileMap[value + 1]);
			            	if (tmpTileCounts[value + 2]-- <= 0) {isOverload = true; break;}
			            	tmpHandTiles.add(tileMap[value + 2]);
			            	allGroups.add(new Groups.Junko(value, GroupState.MenQing));
			            }
					}
					if (isOverload) continue;
					if (countOfbyte1 == 4) { // 单骑
						// 遍历所有可用的点炮牌
						for (int key : tileKeys) {
							if (!canBombs[key]) continue; // 如果有4张相同的牌，则跳过
							if (tmpTileCounts[key] == 0) continue; // 如果是使用完的手牌，则跳过
							List<Group> tmpAllGroups = new ArrayList<Groups.Group>(allGroups);
							tmpAllGroups.add(new Groups.Pair(key, GroupState.HePai));
							List<Tile> tmp4BHandTiles = new ArrayList<Tile>(tmpHandTiles);
							tmp4BHandTiles.add(tileMap[key]);
							Tile tmpAddedTile = bombTiles[key];
							// 取分值最大的结果
							game34Result.LogMaxScore(tmpAddedTile, tmp4BHandTiles, tmpAllGroups);
						}
					} else if (countOfbyte1 == 3) { // 顺子听嵌章、顺子双面听、两对双面听
						for (int j = 0; j < pairTiles.size(); j++) { // 先取出1个对子
							int[] tmp2TileCounts = tmpTileCounts.clone();
							List<Tile> tmp2HandTiles = new ArrayList<Tile>(tmpHandTiles);
							List<Group> tmp2AllGroups = new ArrayList<Groups.Group>(allGroups);
							Tile tmpAddedTile;
							int value = pairTiles.get(j);
			            	if (tmp2TileCounts[value] < 2) continue;
			            	tmp2TileCounts[value] -= 2;
			            	tmp2HandTiles.add(tileMap[value]);
			            	tmp2HandTiles.add(tileMap[value]);
			            	tmp2AllGroups.add(new Groups.Pair(value, GroupState.MenQing));
			            	for (int k = 0; k < junkoQbTiles.size(); k++) { // 顺子双面听
			            		List<Tile> tmp3HandTiles = new ArrayList<Tile>(tmp2HandTiles);
								List<Group> tmp3AllGroups = new ArrayList<Groups.Group>(tmp2AllGroups);
			            		int QbValue = junkoQbTiles.get(k);
								if (tmp2TileCounts[QbValue] <= 0) continue;
								tmp3HandTiles.add(tileMap[QbValue]);
								if (tmp2TileCounts[QbValue + 1] <= 0) continue;
								tmp3HandTiles.add(tileMap[QbValue + 1]);
			            		int num = QbValue % 9;
								if (num == 0) { // 12听3
									if (!canBombs[QbValue + 2]) continue;
									tmpAddedTile = bombTiles[QbValue + 2];
									tmp3AllGroups.add(new Groups.Junko(QbValue, GroupState.HePai, 2));
									// 取分值最大的结果
									game34Result.LogMaxScore(tmpAddedTile, tmp3HandTiles, tmp3AllGroups);
								} else if (num == 8) { // 89听7
									if (!canBombs[QbValue - 1]) continue;
									tmpAddedTile = bombTiles[QbValue - 1];
									tmp3AllGroups.add(new Groups.Junko(QbValue - 1, GroupState.HePai, 0));
									// 取分值最大的结果
									game34Result.LogMaxScore(tmpAddedTile, tmp3HandTiles, tmp3AllGroups);
								} else { // 23听1、4
									if (canBombs[QbValue - 1]) {
										tmpAddedTile = bombTiles[QbValue - 1];
										List<Group> tmp4AllGroups = new ArrayList<Groups.Group>(tmp3AllGroups);
										tmp4AllGroups.add(new Groups.Junko(QbValue - 1, GroupState.HePai, 0));
										// 取分值最大的结果
										game34Result.LogMaxScore(tmpAddedTile, tmp3HandTiles, tmp4AllGroups);
									}
									if (canBombs[QbValue + 2]) {
										tmpAddedTile = bombTiles[QbValue + 2];
										List<Group> tmp4AllGroups = new ArrayList<Groups.Group>(tmp3AllGroups);
										tmp4AllGroups.add(new Groups.Junko(QbValue, GroupState.HePai, 2));
										// 取分值最大的结果
										game34Result.LogMaxScore(tmpAddedTile, tmp3HandTiles, tmp4AllGroups);
									}
								}
							}
			            	for (int k = 0; k < junkoQzTiles.size(); k++) { // 顺子听嵌章
			            		List<Tile> tmp3HandTiles = new ArrayList<Tile>(tmp2HandTiles);
								List<Group> tmp3AllGroups = new ArrayList<Groups.Group>(tmp2AllGroups);
			            		int QzValue = junkoQzTiles.get(k);
								if (tmp2TileCounts[QzValue] <= 0) continue;
								tmp3HandTiles.add(tileMap[QzValue]);
								if (tmp2TileCounts[QzValue + 2] <= 0) continue;
								tmp3HandTiles.add(tileMap[QzValue + 2]);
								if (!canBombs[QzValue + 1]) continue;
								tmpAddedTile = bombTiles[QzValue + 1];
								tmp3AllGroups.add(new Groups.Junko(QzValue, GroupState.HePai, 1));
								// 取分值最大的结果
								game34Result.LogMaxScore(tmpAddedTile, tmp3HandTiles, tmp3AllGroups);
			            	}
						}
						if (pairTiles.size() >= 2) { // 两对双面听
							for (int j = 0; j < (1 << pairTiles.size()); j++) { // 2的n次方则为所有组合的次数
								if (CountOfByte1(j) == 2) { // 取所有2组不同对子的组合
									List<Tile> tmp2HandTiles = new ArrayList<Tile>(tmpHandTiles);
									int[] tmpAddedKeys = new int[2];
									int index = 0;
									boolean canDone = true;
									for (int k = 0; k < pairTiles.size(); k++) {
							            if (((j >> k) & 1) == 1) { // 二进制位为1则加入
							            	int value = pairTiles.get(k);
							            	if (tmpTileCounts[value] < 2) {
							            		canDone = false;
							            		break;
							            	}
							            	tmp2HandTiles.add(tileMap[value]);
							            	tmp2HandTiles.add(tileMap[value]);
							            	tmpAddedKeys[index++] = value;
							            }
									}
									if (!canDone) continue;
									for (int key : tmpAddedKeys) {
										if (!canBombs[key]) continue; // 如果有4张相同的牌，则跳过
										List<Group> tmp2AllGroups = new ArrayList<Groups.Group>(allGroups);
										Tile tmpAddedTile = bombTiles[key];
										if (key == tmpAddedKeys[0]) {
											tmp2AllGroups.add(new Groups.Pung(tmpAddedKeys[0], GroupState.HePai));
											tmp2AllGroups.add(new Groups.Pair(tmpAddedKeys[1], GroupState.MenQing));
											game34Result.LogMaxScore(tmpAddedTile, tmp2HandTiles, tmp2AllGroups);
										} else if (key == tmpAddedKeys[1]) {
											tmp2AllGroups.add(new Groups.Pung(tmpAddedKeys[1], GroupState.HePai));
											tmp2AllGroups.add(new Groups.Pair(tmpAddedKeys[0], GroupState.MenQing));
											game34Result.LogMaxScore(tmpAddedTile, tmp2HandTiles, tmp2AllGroups);
										}
									}
								}
							}			
						}
					}
				} 
			}
		}
	}
	
	/**
	 * 计算二进制数中1的个数
	 * 
	 * @param n
	 * @return
	 */
	private static int CountOfByte1(int n) {
        int count = 0;
        while(n != 0){
        	count++;
            n = n & (n-1);
        }
        return count;
    }
	
	/**
	 * 计算宝牌数量
	 * 
	 * @param allTiles
	 * @param indicators
	 * @param indicatorsIns
	 * @param isLiZhi
	 * @param isDoubleLiZhi
	 */
	public static void calcDoras(List<Tile> allTiles, 
			List<MjCard> indicators, List<MjCard> indicatorsIns,
			boolean isLiZhi, boolean isDoubleLiZhi) {
		// 计算宝牌
		int[] doras = convertCard2TileDoras(indicators);
		// 计算里宝牌
		int[] doraIns = null;
		if (isLiZhi || isDoubleLiZhi) {
			doraIns = convertCard2TileDoras(indicatorsIns);
		} else {
			doraIns = new int[indicatorsIns.size()];
			Arrays.fill(doraIns, -1);
		}
		// 赋值所有牌的宝牌数量
		for (Tile tile : allTiles) {
			tile.calDora(doras);
			tile.calInDora(doraIns);
		}
	}
	
	/**
	 * 宝牌指示牌组转为宝牌组（计算用）
	 * 
	 * @param indicators
	 * @return
	 */
	public static int[] convertCard2TileDoras(List<MjCard> indicators) {
		int[] doras = new int[indicators.size()];
		for (int i = 0; i < indicators.size(); i++) {
			MjCard dora = indicators.get(i); 
			doras[i] = convertCard2TileDora(dora.num);
		}
		return doras;
	}
	
	/**
	 * 宝牌指示牌转为宝牌（计算用）
	 * 
	 * @param indicator
	 * @return
	 */
	public static int convertCard2TileDora(int indicator) {
		int dora = -1;
		if (indicator > MjSetting._face_down && indicator < MjSetting._man9) {
			dora = indicator - MjSetting._face_down + JpnSetting.MAN1;
		} else if (indicator == MjSetting._man9) {
			dora = JpnSetting.MAN1;
		} else if (indicator == MjSetting._man5r) {
			dora = JpnSetting.MAN6;
		} else if (indicator > MjSetting._man5r && indicator < MjSetting._pin9) {
			dora = indicator - MjSetting._man5r + JpnSetting.PIN1;
		} else if (indicator == MjSetting._pin9) {
			dora = JpnSetting.PIN1;
		} else if (indicator == MjSetting._pin5r) {
			dora = JpnSetting.PIN6;
		} else if (indicator > MjSetting._pin5r && indicator < MjSetting._bamboo9) {
			dora = indicator - MjSetting._pin5r + JpnSetting.SOU1;
		} else if (indicator == MjSetting._bamboo9) {
			dora = JpnSetting.SOU1;
		} else if (indicator == MjSetting._bamboo5r) {
			dora = JpnSetting.SOU6;
		} else if (indicator > MjSetting._bamboo5r && indicator < MjSetting._wind_north) {
			dora = indicator - MjSetting._bamboo5r + JpnSetting.TON;
		} else if (indicator == MjSetting._wind_north) {
			dora = JpnSetting.TON;
		} else if (indicator > MjSetting._wind_north && indicator < MjSetting._dragon_chun) {
			dora = indicator - MjSetting._wind_north + JpnSetting.HAK;
		} else if (indicator == MjSetting._dragon_chun) {
			dora = JpnSetting.HAK;
		}		
		return dora;
	}
	
	/**
	 * 宝牌指示牌组转为宝牌组（UI用）
	 * 
	 * @param indicators
	 * @return
	 */
	public static int[] convertCard2CardDoras(List<MjCard> indicators) {
		int[] doras = new int[indicators.size()];
		for (int i = 0; i < indicators.size(); i++) {
			MjCard dora = indicators.get(i); 
			doras[i] = convertCard2CardDora(dora.num);
		}
		return doras;
	}
	
	/**
	 * 宝牌指示牌转为宝牌（UI用）
	 * 
	 * @param indicator
	 * @return
	 */
	public static int convertCard2CardDora(int indicator) {
		int dora = -1;
		if (indicator > MjSetting._face_down && indicator < MjSetting._man9) {
			dora = indicator + 1;
		} else if (indicator == MjSetting._man9) {
			dora = MjSetting._man1;
		} else if (indicator == MjSetting._man5r) {
			dora = MjSetting._man6;
		} else if (indicator > MjSetting._man5r && indicator < MjSetting._pin9) {
			dora = indicator + 1;
		} else if (indicator == MjSetting._pin9) {
			dora = MjSetting._pin1;
		} else if (indicator == MjSetting._pin5r) {
			dora = MjSetting._pin6;
		} else if (indicator > MjSetting._pin5r && indicator < MjSetting._bamboo9) {
			dora = indicator + 1;
		} else if (indicator == MjSetting._bamboo9) {
			dora = MjSetting._bamboo1;
		} else if (indicator == MjSetting._bamboo5r) {
			dora = MjSetting._bamboo6;
		} else if (indicator > MjSetting._bamboo5r && indicator < MjSetting._wind_north) {
			dora = indicator + 1;
		} else if (indicator == MjSetting._wind_north) {
			dora = MjSetting._wind_east;
		} else if (indicator > MjSetting._wind_north && indicator < MjSetting._dragon_chun) {
			dora = indicator + 1;
		} else if (indicator == MjSetting._dragon_chun) {
			dora = MjSetting._dragon_haku;
		}		
		return dora;
	}
	
	/**
	 * 检查是否门前清状态
	 * 
	 * @param fulouGroups
	 * @return
	 */
	public static boolean isMenQing(List<Group> fulouGroups) {
		if (fulouGroups == null || fulouGroups.size() == 0) {
			return true;
		}
		for (Group group : fulouGroups) {
			if (group.state() == GroupState.FuLou) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 转化环境变量
	 * 
	 * @param isLiZhi
	 * @param isDoubleLiZhi
	 * @param isFirstRound
	 * @param isFinalPick
	 * @param isZiMo
	 * @param isYiFa
	 * @param isQiangGang
	 * @param isLingshang
	 * @param groundWind
	 * @param selfWind
	 * @param isMenQing
	 * @return
	 */
	public static int convert2JpnEnvironment(boolean isLiZhi, boolean isDoubleLiZhi,
			boolean isFirstRound, boolean isFinalPick, boolean isZiMo, 
			boolean isYiFa, boolean isQiangGang, boolean isLingshang,
			MjWind groundWind, MjWind selfWind, boolean isMenQing) {
		int env = 0;
		if (isLiZhi) env |= YakuEnvironment.LiZhi;
		if (isZiMo) env |= YakuEnvironment.ZiMo;
		if (isYiFa) env |= YakuEnvironment.YiFa;
		if (isDoubleLiZhi) env |= YakuEnvironment.DoubleLiZhi;
		if (isQiangGang) env |= YakuEnvironment.QiangGang;
		if (isFirstRound) env |= YakuEnvironment.FirstRound;
		if (isFinalPick) env |= YakuEnvironment.FinalPick;
		if (isLingshang) env |= YakuEnvironment.LingShangKaiHua;
		if (isMenQing) env |= YakuEnvironment.MenQianQing;
		switch (groundWind) {
		case East:
			env |= YakuEnvironment.GroundWindEast;
			break;
		case South:
			env |= YakuEnvironment.GroundWindSouth;
			break;
		case West:
			env |= YakuEnvironment.GroundWindWest;
			break;
		case North:
			env |= YakuEnvironment.GroundWindNorth;
			break;
		default:
			break;
		}
		switch (selfWind) {
		case East:
			env |= YakuEnvironment.SelfWindEast;
			break;
		case South:
			env |= YakuEnvironment.SelfWindSouth;
			break;
		case West:
			env |= YakuEnvironment.SelfWindWest;
			break;
		case North:
			env |= YakuEnvironment.SelfWindNorth;
			break;
		default:
			break;
		}
		return env;
	}
	
	/**
	 * 计算副露牌的风位
	 * 
	 * @param selfWind
	 * @param dir
	 * @return
	 */
	private static MjWind calWind(MjWind selfWind, int dir) {
		switch (selfWind) {
		case East:
			return FuLouWind[dir - MjDir.left][0];
		case South:
			return FuLouWind[dir - MjDir.left][1];
		case West:
			return FuLouWind[dir - MjDir.left][2];
		case North:
			return FuLouWind[dir - MjDir.left][3];
		default:
			return selfWind;
		}
	}
	
	/**
	 * 根据自己风位转化为其他任意风位
	 * 
	 * @param selfWind
	 * @return
	 */
	private static MjWind ramdomWind(MjWind selfWind) {
		switch (selfWind) {
		case East:
			return MjWind.South;
		case South:
			return MjWind.West;
		case West:
			return MjWind.North;
		case North:
			return MjWind.East;
		default:
			return MjWind.None;
		}
	}
	
	/**
	 * 将UI用组合装换为计算用组合
	 * 
	 * @param allTiles
	 * @param pairs
	 * @param selfWind
	 * @param enableRedDora
	 * @return
	 */
	private static Group convertCardPair2JpnGroup(List<Tile> allTiles, MjCardPairs pairs, 
			MjWind selfWind, boolean enableRedDora) {
		Group group = null;
		MjWind fulouWind = calWind(selfWind, pairs.mDir);
		boolean isFuLou = false;
		switch (pairs.mType) {
		case MjPairType.Sequence:	
			int min = 100;
			for (MjCard card : pairs.mCardList) {
				Tile tile;
				if (!isFuLou && card.dir == MjDir.horizontal) {
					tile = convertCard2JpnTile(card, fulouWind, enableRedDora);
					isFuLou = true;
				} else {
					tile = convertCard2JpnTile(card, selfWind, enableRedDora);
				}
				if (tile.Value() < min) min = tile.Value();
				allTiles.add(tile);
			}
			group = new Junko(min, GroupState.FuLou, fulouWind);
			break;
		case MjPairType.Triplet:
			for (MjCard card : pairs.mCardList) {
				Tile tile;
				if (!isFuLou && card.dir == MjDir.horizontal) {
					tile = convertCard2JpnTile(card, fulouWind, enableRedDora);
					isFuLou = true;
				} else {
					tile = convertCard2JpnTile(card, selfWind, enableRedDora);
				}
				allTiles.add(tile);
			}
			group = new Pung(allTiles.get(allTiles.size() - 1).Value(), GroupState.FuLou, fulouWind);
			break;
		case MjPairType.AdditionKong:
		case MjPairType.ExposedKong:
			for (MjCard card : pairs.mCardList) {
				Tile tile;
				if (!isFuLou && card.dir == MjDir.horizontal) {
					tile = convertCard2JpnTile(card, fulouWind, enableRedDora);
					isFuLou = true;
				} else {
					tile = convertCard2JpnTile(card, selfWind, enableRedDora);
				}
				allTiles.add(tile);
			}
			group = new Gang(allTiles.get(allTiles.size() - 1).Value(), GroupState.FuLou, fulouWind);
			break;
		case MjPairType.ConcealedKong:
			for (MjCard card : pairs.mCardList) {
				Tile tile = convertCard2JpnTile(card, selfWind, enableRedDora);
				allTiles.add(tile);
			}
			group = new Gang(allTiles.get(allTiles.size() - 1).Value(), GroupState.MenQing, selfWind);
			break;
		default:
			break;
		}			
		
		return group;
	}
	
	/**
	 * 将UI牌转换为计算牌
	 * 
	 * @param card
	 * @param wind
	 * @param enableRedDora
	 * @return
	 */
	private static Tile convertCard2JpnTile(MjCard card, MjWind wind, boolean enableRedDora) {
		int num = card.num;
		int value = 0;
		Tile tile;
		if (num >= MjSetting._man1 && num <= MjSetting._man5r) {
			if (num == MjSetting._man5r) {
				tile = new Tile(JpnSetting.MAN5, enableRedDora, wind);
			} else {
				value = num - MjSetting._man1 + JpnSetting.MAN;
				tile = new Tile(value, false, wind);
			}				
		} else if (num >= MjSetting._pin1 && num <= MjSetting._pin5r) {
			if (num == MjSetting._pin5r) {
				tile = new Tile(JpnSetting.PIN5, enableRedDora, wind);
			} else {
				value = num - MjSetting._pin1 + JpnSetting.PIN;
				tile = new Tile(value, false, wind);
			}				
		} else if (num >= MjSetting._bamboo1 && num <= MjSetting._bamboo5r) {
			if (num == MjSetting._bamboo5r) {
				tile = new Tile(JpnSetting.SOU5, enableRedDora, wind);
			} else {
				value = num - MjSetting._bamboo1 + JpnSetting.SOU;
				tile = new Tile(value, false, wind);
			}				
		} else {
			value = num - MjSetting._wind_east + JpnSetting.TON;
			tile = new Tile(value, false, wind);
		}
		return tile;
	}
	
	/**
	 * 将计算牌转换为UI牌
	 * 
	 * @param tile
	 * @return
	 */
	private static MjCard convertTile2MjCard(Tile tile) {
		MjCard card = new MjCard();
		int value = tile.Value();
		int num = 0;
		if (value >= JpnSetting.MAN && value < JpnSetting.PIN) {
			if (value == JpnSetting.MAN5 && tile.IsRedDora()) {
				num = MjSetting._man5r;
			} else {
				num = value - JpnSetting.MAN + MjSetting._man1;
			}
		} else if (value >= JpnSetting.PIN && value < JpnSetting.SOU) {
			if (value == JpnSetting.PIN5 && tile.IsRedDora()) {
				num = MjSetting._pin5r;
			} else {
				num = value - JpnSetting.PIN + MjSetting._pin1;
			}
		} else if (value >= JpnSetting.SOU && value < JpnSetting.TON) {
			if (value == JpnSetting.SOU5 && tile.IsRedDora()) {
				num = MjSetting._bamboo5r;
			} else {
				num = value - JpnSetting.SOU + MjSetting._bamboo1;
			}
		} else {
			num = value - JpnSetting.TON + MjSetting._wind_east;
		}
		card.setData(num);
		return card;
	}
	
	/**
	 * 获取所有特殊役
	 * 
	 * @return
	 */
	public static List<Yaku> getSpecialYakus() {
		List<Yaku> spYakus = new ArrayList<Yaku>();
		for (Yaku yaku : mGame.Yakus) {
			if (yaku.isAncientYaku()) {
				spYakus.add(yaku);
			}
		}		
		return spYakus;
	}
	
	/**
	 * 获取所有使用的特殊役的序列字符串
	 * 
	 * @return
	 */
	public static String getSpecialYakusEnable2String() {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		for (Yaku yaku : mGame.Yakus) {
			if (yaku.isAncientYaku()) {
				if (yaku.isCalculateYaku()) {
					if (!isFirst) {
						buffer.append(",");
					} else {
						isFirst = false;
					}
					buffer.append(yaku.orderIndex());					
				}
			}
		}	
		return buffer.toString();
	}
	
	/**
	 * 将特殊役按配置初始化
	 * 
	 * @param name2IndexMap
	 * @param mYakuList
	 */
	public static void setSpecialYakusEnable(
			Map<String, Integer> name2IndexMap, List<YakuBean> mYakuList) {
		for (Yaku yaku : mGame.Yakus) {
			if (yaku.isAncientYaku()) {
				Integer index = name2IndexMap.get(yaku.name());
				if (index != null) {
					yaku.setCalculateYaku(mYakuList.get(index).enable());
				}
			}
		}
	}
	
}

package com.mahjong.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mahjong.data.jpn.AgariIndex;
import com.mahjong.data.jpn.Game;
import com.mahjong.data.jpn.GroupState;
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
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuValue;
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
	
	public static boolean checkCanHupai(GameResult gameResult,
			List<MjCard> darkNums, List<MjCardPairs> brightNums, MjCard winNum,
			List<MjCard> indicators, List<MjCard> indicatorsIns) {
		List<Tile> allTiles = new ArrayList<Tile>();	// 所有牌
		List<Tile> handTiles = new ArrayList<Tile>();	// 门清牌
		// 添加和牌
		Tile addedTile = convert2JpnTile(winNum, gameResult.isZiMo ? gameResult.selfWind : gameResult.addedWind);
		allTiles.add(addedTile);
		handTiles.add(addedTile);
		// 添加门清牌
		for (MjCard card : darkNums) {
			Tile tile = convert2JpnTile(card, gameResult.selfWind);
			allTiles.add(tile);
			handTiles.add(tile);
		}
		// 添加副露牌
		List<Group> fulouGroups = new ArrayList<Group>();
		for (MjCardPairs pairs : brightNums) {
			Group group = convert2JpnGroup(allTiles, pairs, gameResult.selfWind);
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
		Tile addedTile = convert2JpnTile(winNum, isZiMo ? selfWind : ramdomWind(selfWind));
		allTiles.add(addedTile);
		handTiles.add(addedTile);
		// 添加门清牌
		for (MjCard card : darkNums) {
			Tile tile = convert2JpnTile(card, selfWind);
			allTiles.add(tile);
			handTiles.add(tile);
		}
		// 添加副露牌
		List<Group> fulouGroups = new ArrayList<Group>();
		for (MjCardPairs pairs : brightNums) {
			Group group = convert2JpnGroup(allTiles, pairs, selfWind);
			fulouGroups.add(group);
		}
		// 计算宝牌
		calcDoras(allTiles, indicators, indicatorsIns, isLiZhi, isDoubleLiZhi);
		// 检测是否能胡牌
		List<Group[]> menqingGroups = new ArrayList<Group[]>();
		boolean result = AgariIndex.done(handTiles, addedTile.Value(), menqingGroups);		
		if (result) {
			List<IGroups> gList = new ArrayList<IGroups>();
			for (Group[] gs : menqingGroups) {
				List<Group> allGroups = new ArrayList<Group>(fulouGroups);
				for (Group g : gs) {
					allGroups.add(g);
				}
				gList.add(new GroupCollection(allGroups));
			}
			ITiles tiles = new TileCollection(allTiles, handTiles, addedTile);
			int env = convert2JpnEnvironment(isLiZhi, isDoubleLiZhi, isFirstRound, isFinalPick, 
					isZiMo, isYiFa, isQiangGang, isLingshang, groundWind, selfWind, isMenQing(fulouGroups));
			Score score = mGame.getScore(tiles, gList, env);
			return score;
		}
		return null;
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
		int[] doras = new int[indicators.size()];
		for (int i = 0; i < indicators.size(); i++) {
			MjCard dora = indicators.get(i); 
			doras[i] = convertDora(dora.num);
		}
		// 计算里宝牌
		int[] doraIns = new int[indicatorsIns.size()];
		if (isLiZhi || isDoubleLiZhi) {
			for (int i = 0; i < indicatorsIns.size(); i++) {
				MjCard dora = indicatorsIns.get(i); 
				doraIns[i] = convertDora(dora.num);
			}			
		} else {
			Arrays.fill(doraIns, -1);
		}
		// 赋值所有牌的宝牌数量
		for (Tile tile : allTiles) {
			tile.calDora(doras);
			tile.calInDora(doraIns);
		}
	}
	
	/**
	 * 宝牌指示牌转为宝牌
	 * 
	 * @param indicator
	 * @return
	 */
	private static int convertDora(int indicator) {
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
	
	private static Group convert2JpnGroup(List<Tile> allTiles, MjCardPairs pairs, MjWind selfWind) {
		Group group = null;
		MjWind fulouWind = calWind(selfWind, pairs.mDir);
		boolean isFuLou = false;
		switch (pairs.mType) {
		case MjPairType.Sequence:	
			int min = 100;
			for (MjCard card : pairs.mCardList) {
				Tile tile;
				if (!isFuLou && card.dir == MjDir.horizontal) {
					tile = convert2JpnTile(card, fulouWind);
					isFuLou = true;
				} else {
					tile = convert2JpnTile(card, selfWind);
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
					tile = convert2JpnTile(card, fulouWind);
					isFuLou = true;
				} else {
					tile = convert2JpnTile(card, selfWind);
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
					tile = convert2JpnTile(card, fulouWind);
					isFuLou = true;
				} else {
					tile = convert2JpnTile(card, selfWind);
				}
				allTiles.add(tile);
			}
			group = new Gang(allTiles.get(allTiles.size() - 1).Value(), GroupState.FuLou, fulouWind);
			break;
		case MjPairType.ConcealedKong:
			for (MjCard card : pairs.mCardList) {
				Tile tile = convert2JpnTile(card, selfWind);
				allTiles.add(tile);
			}
			group = new Gang(allTiles.get(allTiles.size() - 1).Value(), GroupState.MenQing, selfWind);
			break;
		default:
			break;
		}			
		
		return group;
	}
	
	private static Tile convert2JpnTile(MjCard card, MjWind wind) {
		int num = card.num;
		int value = 0;
		Tile tile;
		if (num >= MjSetting._man1 && num <= MjSetting._man5r) {
			if (num == MjSetting._man5r) {
				tile = new Tile(JpnSetting.MAN5, true, wind);
			} else {
				value = num - MjSetting._man1 + JpnSetting.MAN;
				tile = new Tile(value, false, wind);
			}				
		} else if (num >= MjSetting._pin1 && num <= MjSetting._pin5r) {
			if (num == MjSetting._pin5r) {
				tile = new Tile(JpnSetting.PIN5, true, wind);
			} else {
				value = num - MjSetting._pin1 + JpnSetting.PIN;
				tile = new Tile(value, false, wind);
			}				
		} else if (num >= MjSetting._bamboo1 && num <= MjSetting._bamboo5r) {
			if (num == MjSetting._bamboo5r) {
				tile = new Tile(JpnSetting.SOU5, true, wind);
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
	
}

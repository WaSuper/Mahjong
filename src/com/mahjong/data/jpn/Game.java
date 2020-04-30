package com.mahjong.data.jpn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mahjong.data.jpn.Groups.Gang;
import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.Groups.Pair;
import com.mahjong.data.jpn.yaku.Yaku01_LiZhi;
import com.mahjong.data.jpn.yaku.Yaku02_YiFa;
import com.mahjong.data.jpn.yaku.Yaku03_ZiMo;
import com.mahjong.data.jpn.yaku.Yaku04_PingHe;
import com.mahjong.data.jpn.yaku.Yaku05_QiangGang;
import com.mahjong.data.jpn.yaku.Yaku06_YiPai;
import com.mahjong.data.jpn.yaku.Yaku07_DuanYaoJiu;
import com.mahjong.data.jpn.yaku.Yaku08_YiBeiKou;
import com.mahjong.data.jpn.yaku.Yaku09_LingShangKaiHua;
import com.mahjong.data.jpn.yaku.Yaku10_HaiDiLaoYue;
import com.mahjong.data.jpn.yaku.Yaku11_HeDiMoYu;
import com.mahjong.data.jpn.yaku.Yaku12_Dora;
import com.mahjong.data.jpn.yaku.Yaku13_InDora;
import com.mahjong.data.jpn.yaku.Yaku14_RedDora;
import com.mahjong.data.jpn.yaku.Yaku15_DoubleLiZhi;
import com.mahjong.data.jpn.yaku.Yaku16_QiDuiZi;
import com.mahjong.data.jpn.yaku.Yaku17_HunQuanDaiYao;
import com.mahjong.data.jpn.yaku.Yaku18_YiQiTongGuan;
import com.mahjong.data.jpn.yaku.Yaku19_SanSeTongShun;
import com.mahjong.data.jpn.yaku.Yaku20_SanSeTongKe;
import com.mahjong.data.jpn.yaku.Yaku21_SanGangZi;
import com.mahjong.data.jpn.yaku.Yaku22_DuiDuiHe;
import com.mahjong.data.jpn.yaku.Yaku23_SanAnKe;
import com.mahjong.data.jpn.yaku.Yaku24_XiaoSanYuan;
import com.mahjong.data.jpn.yaku.Yaku25_HunLaoTou;
import com.mahjong.data.jpn.yaku.Yaku26_ErBeiKou;
import com.mahjong.data.jpn.yaku.Yaku27_ChunQuanDaiYaoJiu;
import com.mahjong.data.jpn.yaku.Yaku28_HunYiSe;
import com.mahjong.data.jpn.yaku.Yaku29_QingYiSe;
import com.mahjong.data.jpn.yaku.Yaku50_TianDiRen;
import com.mahjong.data.jpn.yaku.Yaku51_DaCheLun;
import com.mahjong.data.jpn.yaku.Yaku52_GuoShuiWuShuang;
import com.mahjong.data.jpn.yaku.Yaku53_JiuLianBaiDeng;
import com.mahjong.data.jpn.yaku.Yaku54_SiAnKe;
import com.mahjong.data.jpn.yaku.Yaku55_DaSanYuan;
import com.mahjong.data.jpn.yaku.Yaku56_SiGangZi;
import com.mahjong.data.jpn.yaku.Yaku57_LvYiSe;
import com.mahjong.data.jpn.yaku.Yaku58_ZiYiSe;
import com.mahjong.data.jpn.yaku.Yaku59_QingLaoTou;
import com.mahjong.data.jpn.yaku.Yaku60_XiaoSixi;
import com.mahjong.data.jpn.yaku.Yaku61_DaSixi;

public class Game {

	private Yaku[] Yakus = {
			// 普通役
			new Yaku01_LiZhi(), new Yaku02_YiFa(), new Yaku03_ZiMo(), new Yaku04_PingHe(), new Yaku05_QiangGang(),
			new Yaku06_YiPai(), new Yaku07_DuanYaoJiu(), new Yaku08_YiBeiKou(), new Yaku09_LingShangKaiHua(), 
			new Yaku10_HaiDiLaoYue(), new Yaku11_HeDiMoYu(), new Yaku12_Dora(), new Yaku13_InDora(), new Yaku14_RedDora(), 
			new Yaku15_DoubleLiZhi(), new Yaku16_QiDuiZi(), new Yaku17_HunQuanDaiYao(), new Yaku18_YiQiTongGuan(), 
			new Yaku19_SanSeTongShun(), new Yaku20_SanSeTongKe(), new Yaku21_SanGangZi(), new Yaku22_DuiDuiHe(), 
			new Yaku23_SanAnKe(), new Yaku24_XiaoSanYuan(), new Yaku25_HunLaoTou(), new Yaku26_ErBeiKou(), 
			new Yaku27_ChunQuanDaiYaoJiu(), new Yaku28_HunYiSe(), new Yaku29_QingYiSe(), 
			// 役满役
			new Yaku50_TianDiRen(), new Yaku51_DaCheLun(), new Yaku52_GuoShuiWuShuang(), new Yaku53_JiuLianBaiDeng(),
			new Yaku54_SiAnKe(), new Yaku55_DaSanYuan(), new Yaku56_SiGangZi(), new Yaku57_LvYiSe(), new Yaku58_ZiYiSe(),
			new Yaku59_QingLaoTou(), new Yaku60_XiaoSixi(), new Yaku61_DaSixi()			
	};
	
	//private Yaku[] SpecialYakus = { new Yaku16_QiDuiZi(), new Yaku52_GuoShuiWuShuang()};
	
	//private Yaku[] ExtraYakus = { new Yaku30_LiuJuManGuan(), new Yaku62_BaLianZhuang(), new Yaku63_LeiJiYiMan() };
	
	public Score getScore(ITiles tiles, List<IGroups> gList, int env) {
		List<Score> scores = GetScores(tiles, gList, env);
		Score result = scores.get(0);
		for (int i = 1; i < scores.size(); i++) {
			Score tmpScore = scores.get(i);
			if (ScoreSystem.compare(result, tmpScore) < 0) result = tmpScore;
		}
		return result;
	}
	
	private List<Score> GetScores(ITiles tiles, List<IGroups> gList, int env) {
		boolean isMenQing = (env & YakuEnvironment.MenQianQing) != 0;
		Yaku[] allYakus = isMenQing ? Yakus : getNoMenQingYakus(Yakus);
		List<Yaku> envYakus = new ArrayList<Yaku>();
		List<Yaku> nonEnvYakus = new ArrayList<Yaku>();
		divideYakuForEnv(allYakus, envYakus, nonEnvYakus);
		List<YakuValue> tempResult = new ArrayList<YakuValue>();
		for (Yaku y : envYakus) {
			y.Test(tempResult, null, null, env);
		}

		int[] kindCountsFromTiles = tiles.KindCountsFromTiles();

		List<Score> results = new ArrayList<Score>();

		for (IGroups groups : gList) {
			int[] kindCounts = groups.KindCounts();
			int[] kindCountsWithoutPair = groups.KindCountsWithoutPair();
			
			int junkoCount = groups.JunkoCount();
			int pungCount = groups.PungCount();				
			List<YakuValue> result = new ArrayList<YakuValue>(tempResult);

			for (Yaku yaku : nonEnvYakus) {
				if (yaku.FilterTest(junkoCount, pungCount) 
						&& yaku.FilterTest(kindCountsFromTiles, kindCounts, kindCountsWithoutPair)) {
					yaku.Test(result, tiles, groups, env);
				}
			}
			
			tryRemoveNormalYakus(result);
			int fu = CalculateFu(result, groups, env);
			results.add(new Score(fu, result));
		}				


		// 形如11223344556677也算七对子，因此即使牌面能拆分成若干group也不能排除特殊役
		boolean succ = false;
		for (Yaku y : nonEnvYakus) {
			if (!y.FilterTest(0, 0)) continue;
			if (!y.FilterTest(kindCountsFromTiles, null, null)) continue;
			succ = (y.Test(tempResult, tiles, null, env) && y instanceof SpecialYaku) || succ;
		}
		if (succ) {
			tryRemoveNormalYakus(tempResult);
			int fu = CalculateFu(tempResult, null, env);
			results.add(new Score(fu, tempResult));
		}
		return results;
	}

	/**
	 * 计算符数，并进行进位处理
	 * 
	 * @param yakuValues 表示当前成立的所有役，可能存在hook符数计算的役
	 * @param groups
	 * @param env
	 * @return
	 */
	private int CalculateFu(List<YakuValue> yakuValues, IGroups groups, int env) {
		int fu = 20;
		for (YakuValue y : yakuValues) {
			if (y.getFullYaku() > 0) return 0;
			int hookFu = y.getSource().HookCalculateFu(groups, env);
			if (hookFu > 0) {
				return hookFu;
			}
		}

		// 所有从面子获得的符
		for (Group g : groups.AllGroups()) {
			fu += CalculateFu(g, env);
		}
		if ((env & YakuEnvironment.MenQianQing) != 0 && (env & YakuEnvironment.ZiMo) == 0) {
			fu += 10; // 门前清荣和
		}
		if ((env & YakuEnvironment.ZiMo) != 0) {
			fu += 2;
		}
		if (fu == 20 && groups.JunkoCount() == 4 
				&& (env & YakuEnvironment.MenQianQing) == 0 
				&& (env & YakuEnvironment.ZiMo) == 0) {
			fu = 30; // 非门前清平和形食和（30符）
		}
		return (fu + 9) / 10 * 10; //进位
	}		
	
	/**
	 * 计算group的符数
	 * 注意：该方法返回的符数未进行进位处理
	 * 
	 * @param group
	 * @param env
	 * @return
	 */
	private int CalculateFu(Group group, int env) {
		if (group instanceof Pair) {
			int fu = 0;
			if (group.value() >= JpnSetting.TON) {
				int val = group.value();
				int val1 = ((int)env >> 6) & 0xf; // 场风
				int val2 = ((int)env >> 10) & 0xf; // 自风
				if (val == val1) fu += 2;
				if (val == val2) fu += 2;
				if (fu == 0 && group.value() >= JpnSetting.HAK) fu += 2;
			}
			if (group.state() == GroupState.HePai) fu += 2; // 单骑
			return fu;
		} else if (group.isPung()) {
			int addedWind = 0x01;
			int selfWind = (((int)env >> 10) & 0xf);
			switch (group.wind()) {
			case East:
				addedWind = addedWind << 0;
				break;
			case South:
				addedWind = addedWind << 1;
				break;
			case West:
				addedWind = addedWind << 2;
				break;
			case North:
				addedWind = addedWind << 3;
				break;
			default:
				break;
			}
			boolean isClosed = group.state() == GroupState.MenQing
				|| (group.state() == GroupState.HePai && addedWind == selfWind);

			int fu = 2;
			if (group instanceof Gang) fu *= 4;
			if (isClosed) fu *= 2;
			if (group.hasYaoJiu()) fu *= 2;
			return fu;
		} else { //group is Junko
			if (group.state() == GroupState.HePai) {
				int value = group.value();
				switch (group.addedIndex()) {
				case 0: //边张
					if (value == 6 || value == 9 || value == 24) { // 789的顺子
						return 2;
					}
					break;
				case 1: //嵌张
					return 2;
				case 2: //边张
					if (value == 0 || value == 9 || value == 18) { // 123的顺子
						return 2;
					}
					break;
				default:
					break;
				}
			}
			return 0;
		}
	}		
	
	private Yaku[] getNoMenQingYakus(Yaku[] Yakus) {
		List<Yaku> mqYakus = new ArrayList<Yaku>();
		for (Yaku yaku : Yakus) {
			if ((yaku.flags() & YakuFlag.MenQianQing) == 0) {
				mqYakus.add(yaku);
			}
		}		
		Yaku[] result = new Yaku[mqYakus.size()];
		mqYakus.toArray(result);
		return result;
	}
	
	private void divideYakuForEnv(Yaku[] Yakus, List<Yaku> envYakus, List<Yaku> nonEnvYakus) {
		for (Yaku yaku : Yakus) {
			if ((yaku.flags() & YakuFlag.Environment) != 0) {
				envYakus.add(yaku);
			} else {
				nonEnvYakus.add(yaku);
			}
		}		
	}


	private void tryRemoveNormalYakus(List<YakuValue> result) {
		boolean isFullYaku = false;
		for (YakuValue yakuValue : result) {
			if (yakuValue.getFullYaku() > 0) {
				isFullYaku = true;
				break;
			}
		}
		if (isFullYaku) {
			for (Iterator<YakuValue> iterator = result.iterator(); iterator.hasNext();) {
				YakuValue yakuValue = (YakuValue) iterator.next();
				if (yakuValue.getFullYaku() == 0) {
					iterator.remove();
				}
			}
		}
	}
	
}

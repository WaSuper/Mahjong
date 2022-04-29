package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.Groups.Junko;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku35_JingTongHe extends Yaku {

	public Yaku35_JingTongHe() {
		mOrderIndex = 35;
		mFlags = YakuFlag.None;
		mName = "JingTongHe";
		mAncientYaku = true; // 镜同和为古役
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return junkoCount == 4;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return (kindCountsWithoutPair[0] == 2 && kindCountsWithoutPair[1] == 2) 
				|| (kindCountsWithoutPair[0] == 2 && kindCountsWithoutPair[2] == 2)
				|| (kindCountsWithoutPair[1] == 2 && kindCountsWithoutPair[2] == 2);
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int[] kinds = {-1, -1}; // 记录牌种类（万、饼、索其中2种）	
		int[] kind1Nums = {-1, -1}; // 记录其中第一种牌的2种顺子	
		int[] kind2Nums = {-1, -1}; // 记录其中第二种牌的2种顺子
		for (Junko junko : groups.JunkoList()) {
			int x = junko.value() / 9;
			int y = junko.value() % 9;
			if (kinds[0] == -1) {
				kinds[0] = x;
				kind1Nums[0] = y;
			} else {
				if (kinds[0] != x) {
					if (kinds[1] == -1) {
						kinds[1] = x;
						kind2Nums[0] = y;
					} else {
						kind2Nums[1] = y;
					}					
				} else {
					kind1Nums[1] = y;
				}
			}
		}
		if (kind1Nums[0] == kind1Nums[1] 
				|| kind2Nums[0] == kind2Nums[1]) { // 排除同一种类相同顺子
			return false;
		}
		if ((kind1Nums[0] == kind2Nums[0] && kind1Nums[1] == kind2Nums[1])
				|| (kind1Nums[0] == kind2Nums[1] && kind1Nums[1] == kind2Nums[0])) {
			if ((kind1Nums[0] == 0 && kind1Nums[1] == 6)
					|| (kind1Nums[0] == 6 && kind1Nums[1] == 0)) {
				int pair = groups.Pair().value() % 9;
				if (pair == 4 
						&& (kinds[0] * 9 + 4 != groups.Pair().value())
						&& (kinds[1] * 9 + 4 != groups.Pair().value())) {
					return false; // 排除三色双龙会，不能与镜同和同时存在
				}				
			}
			result.add(YakuValue.FromFanValue(this, "镜同和", (env & YakuEnvironment.MenQianQing) != 0 ? 2 : 1));
			return true;
		}
		return false;
	}

}

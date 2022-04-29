package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku36_ErTongKe extends Yaku {

	public Yaku36_ErTongKe() {
		mOrderIndex = 36;
		mFlags = YakuFlag.None;
		mName = "ErTongKe";
		mAncientYaku = true; // 镜同和为古役
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount == 4;
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
		int[] kind1Nums = {-1, -1}; // 记录其中第一种牌的2种刻子	
		int[] kind2Nums = {-1, -1}; // 记录其中第二种牌的2种刻子
		for (Group group : groups.PungList()) {
			int x = group.value() / 9;
			int y = group.value() % 9;
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
				|| kind2Nums[0] == kind2Nums[1]) { // 排除同一种类相同刻子
			return false;
		}
		if ((kind1Nums[0] == kind2Nums[0] && kind1Nums[1] == kind2Nums[1])
				|| (kind1Nums[0] == kind2Nums[1] && kind1Nums[1] == kind2Nums[0])) {
			result.add(YakuValue.FromFanValue(this, "二同刻", 2));
			return true;
		}
		return false;
	}

}

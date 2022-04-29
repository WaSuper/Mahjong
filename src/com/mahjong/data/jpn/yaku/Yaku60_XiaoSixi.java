package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku60_XiaoSixi extends Yaku {
	
	public Yaku60_XiaoSixi() {
		mOrderIndex = 60;
		mFlags = YakuFlag.YiMan;
		mName = "XiaoSixi";
	}
		
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount >= 3;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsWithoutPair[3] >= 3 && kindCounts[3] >= 4;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int count = 0;
		for (Group group : groups.PungList()) {
			if (group.value() >= JpnSetting.TON && group.value() <= JpnSetting.PEI) {
				count++;
			}
		}
		if (count != 3) return false;
		if (!(groups.Pair().value() >= JpnSetting.TON && groups.Pair().value() <= JpnSetting.PEI)) return false;
		result.add(YakuValue.FromFullYaku(this, "小四喜", 1));		
		return true;
	}

}

package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku61_DaSixi extends Yaku {
	
	public Yaku61_DaSixi() {
		mOrderIndex = 61;
		mFlags = YakuFlag.YiMan;
		mName = "DaSixi";
	}
		
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount == 4;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsWithoutPair[3] == 4;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		for (Group group : groups.PungList()) {
			if (group.value() < JpnSetting.TON || group.value() > JpnSetting.PEI) {
				return false;
			}
		}
		result.add(YakuValue.FromFullYaku(this, "大四喜", 2));		
		return true;
	}

}

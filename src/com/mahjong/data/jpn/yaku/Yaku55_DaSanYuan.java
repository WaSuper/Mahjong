package com.mahjong.data.jpn.yaku;

import java.util.Collection;
import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku55_DaSanYuan extends Yaku {

	public Yaku55_DaSanYuan() {
		mOrderIndex = 55;
		mFlags = YakuFlag.YiMan;
	}
		
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount >= 3;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsWithoutPair[3] >= 3;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int count = 0;
		for (Group g : groups.PungList()) {
			if (g.value() >= JpnSetting.HAK && g.value() <= JpnSetting.CHU) {
				count++;
			}
		}
		if (count < 3) return false;
		result.add(YakuValue.FromFullYaku(this, "大三元", 1));		
		return true;
	}

}

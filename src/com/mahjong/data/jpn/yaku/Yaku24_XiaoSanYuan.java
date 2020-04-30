package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;
import com.mahjong.data.jpn.Groups.Group;

public class Yaku24_XiaoSanYuan extends Yaku {

	public Yaku24_XiaoSanYuan() {
		mOrderIndex = 24;
		mFlags = YakuFlag.None;
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount >= 2;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCounts[3] >= 3 && kindCountsWithoutPair[3] >= 2;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if (groups.Pair().value() < JpnSetting.HAK 
				|| groups.Pair().value() > JpnSetting.CHU) return false;
		int count = 0;
		for (Group g : groups.PungList()) {
			if (g.value() >= JpnSetting.HAK && g.value() <= JpnSetting.CHU) {
				count++;
			}
		}
		if (count < 2) return false;
		result.add(YakuValue.FromFanValue(this, "小三元", 2));
		return true;
	}

}

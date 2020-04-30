package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku51_DaCheLun extends Yaku {

	public Yaku51_DaCheLun() {
		mOrderIndex = 51;
		mFlags = YakuFlag.YiMan | YakuFlag.MenQianQing;
	}
		
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsFromTiles[1] == 14;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int[] sorted = tiles.SortedTiles();
		for (int i = JpnSetting.PIN2; i < JpnSetting.PIN9; i++) {
			if (sorted[i] != 2) return false;
		}
		result.add(YakuValue.FromFullYaku(this, "大车轮", 1));
		return true;
	}

}

package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku72_DaZhuLin extends Yaku {

	public Yaku72_DaZhuLin() {
		mOrderIndex = 72;
		mFlags = YakuFlag.YiMan | YakuFlag.MenQianQing;
		mName = "DaZhuLin";
		mAncientYaku = true; // 大竹林为古役
	}
		
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsFromTiles[2] == 14;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int[] sorted = tiles.SortedTiles();
		for (int i = JpnSetting.SOU2; i < JpnSetting.SOU9; i++) {
			if (sorted[i] != 2) return false;
		}
		result.add(YakuValue.FromFullYaku(this, "大竹林", 1));
		return true;
	}

}

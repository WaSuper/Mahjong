package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku74_DaQiXing extends Yaku {

	public Yaku74_DaQiXing() {
		mOrderIndex = 74;
		mFlags = YakuFlag.YiMan | YakuFlag.MenQianQing;
		mName = "DaQiXing";
		mAncientYaku = true; // 大七星为古役
	}
		
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsFromTiles[3] == 14;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int[] sorted = tiles.SortedTiles();
		for (int i = JpnSetting.TON; i <= JpnSetting.CHU; i++) {
			if (sorted[i] != 2) return false;
		}
		result.add(YakuValue.FromFullYaku(this, "大七星", 2));
		return true;
	}

}

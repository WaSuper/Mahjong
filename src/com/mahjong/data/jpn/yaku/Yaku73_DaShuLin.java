package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku73_DaShuLin extends Yaku {

	public Yaku73_DaShuLin() {
		mOrderIndex = 73;
		mFlags = YakuFlag.YiMan | YakuFlag.MenQianQing;
		mName = "DaShuLin";
		mAncientYaku = true; // 大数隣为古役
	}
		
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsFromTiles[0] == 14;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int[] sorted = tiles.SortedTiles();
		for (int i = JpnSetting.MAN2; i < JpnSetting.MAN9; i++) {
			if (sorted[i] != 2) return false;
		}
		result.add(YakuValue.FromFullYaku(this, "大数隣", 1));
		return true;
	}

}

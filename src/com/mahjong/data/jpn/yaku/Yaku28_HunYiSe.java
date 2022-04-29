package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku28_HunYiSe extends Yaku {

	public Yaku28_HunYiSe() {
		mOrderIndex = 28;
		mFlags = YakuFlag.None;
		mName = "HunYiSe";
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		if (kindCountsFromTiles[3] == 0) return false;
		int sum = kindCountsFromTiles[0] + kindCountsFromTiles[1] + kindCountsFromTiles[2];
		return kindCountsFromTiles[0] == sum || kindCountsFromTiles[1] == sum || kindCountsFromTiles[2] == sum;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		result.add(YakuValue.FromFanValue(this, "混一色", (env & YakuEnvironment.MenQianQing) != 0 ? 3 : 2));
		return true;
	}

}

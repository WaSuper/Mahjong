package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku58_ZiYiSe extends Yaku {
	
	public Yaku58_ZiYiSe() {
		mOrderIndex = 58;
		mFlags = YakuFlag.YiMan;
		mName = "ZiYiSe";
	}
		
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount == 4 || junkoCount + pungCount == 0; // 或者大七星
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsFromTiles[0] + kindCountsFromTiles[1] + kindCountsFromTiles[2] == 0;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		result.add(YakuValue.FromFullYaku(this, "字一色", 1));		
		return true;
	}

}

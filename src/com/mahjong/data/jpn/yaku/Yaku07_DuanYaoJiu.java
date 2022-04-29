package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Tile;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku07_DuanYaoJiu extends Yaku {

	public Yaku07_DuanYaoJiu() {
		mOrderIndex = 7;
		mFlags = YakuFlag.None;
		mName = "DuanYaoJiu";
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsFromTiles[3] == 0;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		for (Tile t : tiles.AllTiles()) {
			if (t.IsYaoJiu()) return false;
		}
		result.add(YakuValue.FromFanValue(this, "断幺九", 1));
		return true;
	}

}

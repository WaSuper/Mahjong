package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Tile;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku25_HunLaoTou extends Yaku {

	public Yaku25_HunLaoTou() {
		mOrderIndex = 25;
		mFlags = YakuFlag.None;
		mName = "HunLaoTou";
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount == 4 || junkoCount + pungCount == 0; // 也可以是七对子
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsFromTiles[0] + kindCountsFromTiles[1] + kindCountsFromTiles[2] > 0 && kindCountsFromTiles[3] > 0;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		for (Tile tile : tiles.AllTiles()) {
			if (!tile.IsYaoJiu()) return false;
		}
		result.add(YakuValue.FromFanValue(this, "混老头", 2));
		return true;
	}

}

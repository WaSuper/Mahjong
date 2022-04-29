package com.mahjong.data.jpn.yaku;

import java.util.Collection;
import java.util.List;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.SpecialYaku;
import com.mahjong.data.jpn.Tile;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku16_QiDuiZi extends SpecialYaku {

	public Yaku16_QiDuiZi() {
		mOrderIndex = 16;
		mFlags = YakuFlag.MenQianQing;
		mName = "QiDuiZi";
	}
	
	@Override
	protected int HookCalculateFu(IGroups groups, int env) {
		return 25;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles) {
		return kindCountsFromTiles[0] % 2 
				+ kindCountsFromTiles[1] % 2 
				+ kindCountsFromTiles[2] % 2 
				+ kindCountsFromTiles[3] % 2 == 0;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if (!TestRon(tiles, env)) return false;
		result.add(YakuValue.FromFanValue(this, "七对子", 2));
		return true;
	}

	@Override
	protected void Syanten(ITiles tiles, Integer result) {
		int c1 = 0, c2 = 0;
		int[] sorted = tiles.SortedTiles();
		for (int i = 0; i < 34; i++) {
			if (sorted[i] >= 1) c1++; else continue;
			if (sorted[i] >= 2) c2++;
		}

		int num = 6 - c2;
		if (c1 < 7) num += 7 - c1;
		if (num < result) result = num;
	}

	@Override
	protected boolean TestRon(ITiles tiles, int env) {
		List<Tile> tList = tiles.HandTiles();
		if (tList.size() != 14) return false;
		int prev = -1;
		for (int i = 0; i < 14; i += 2) {
			int current = tList.get(i).Value();
			int next = tList.get(i + 1).Value();
			if (prev == current) return false; // 不允许相同的将
			if (current != next) return false;
			prev = current;
		}
		return true;
	}

}

package com.mahjong.data.jpn.yaku;

import java.util.Collection;
import java.util.List;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Tile;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku53_JiuLianBaiDeng extends Yaku {

	public Yaku53_JiuLianBaiDeng() {
		mOrderIndex = 53;
		mFlags = YakuFlag.YiMan | YakuFlag.MenQianQing;
	}
		
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return junkoCount >= 2 && pungCount >= 1;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		if (kindCounts[3] > 0) return false;
		int sum = kindCounts[0] + kindCounts[1] + kindCounts[2];
		return kindCounts[0] == sum || kindCounts[1] == sum || kindCounts[2] == sum;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int[] counts = { -3, -1, -1, -1, -1, -1, -1, -1, -3 };
		List<Tile> tList = tiles.AllTiles();
		for (int i = 0; i < tList.size(); i++) {
			counts[tList.get(i).Value() % 9]++;
		}
		int val = 0, extraNumber = 0;
		for (int i = 0; i < 9; i++) {
			val |= counts[i];
			if (counts[i] == 1) extraNumber = i;
		}

		if (val != 1) return false;
		int extraTile = tiles.Added().Value() % 9;
		if (extraTile == extraNumber) {
			result.add(YakuValue.FromFullYaku(this, "纯正九莲宝灯", 2));
		} else {
			if ((env & YakuEnvironment.FirstRound) != 0
					&& (env & YakuEnvironment.ZiMo) != 0
					&& (env & YakuEnvironment.SelfWindEast) != 0) {
				result.add(YakuValue.FromFullYaku(this, "纯正九莲宝灯", 2));
			} else {
				result.add(YakuValue.FromFullYaku(this, "九莲宝灯", 1));
			}
		}
		return true;
	}

}

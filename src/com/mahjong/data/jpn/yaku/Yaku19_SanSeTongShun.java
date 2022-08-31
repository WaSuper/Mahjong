package com.mahjong.data.jpn.yaku;

import java.util.Arrays;
import java.util.Collection;

import com.mahjong.data.jpn.Groups.Junko;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku19_SanSeTongShun extends Yaku {

	public Yaku19_SanSeTongShun() {
		mOrderIndex = 19;
		mFlags = YakuFlag.None;
		mName = "SanSeTongShun";
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return junkoCount >= 3;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsWithoutPair[0] > 0 && kindCountsWithoutPair[1] > 0 && kindCountsWithoutPair[2] > 0;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		boolean[][] flags = new boolean[3][7];		
		Arrays.fill(flags[0], false);	
		Arrays.fill(flags[1], false);	
		Arrays.fill(flags[2], false);
		for (Junko junko : groups.JunkoList()) {
			int x = junko.value() / 9;
			if (x >= 3) continue;
			int y = junko.value() % 9;
			if (y >= 7) continue;
			flags[x][y] = true;
		}
		for (int i = 0; i < 7; i++) {
			if (flags[0][i] && flags[1][i] && flags[2][i]) {
				result.add(YakuValue.FromFanValue(this, "三色同顺", (env & YakuEnvironment.MenQianQing) != 0 ? 2 : 1));
				return true;
			}
		}
		return false;
	}

}

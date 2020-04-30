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

public class Yaku18_YiQiTongGuan extends Yaku {

	public Yaku18_YiQiTongGuan() {
		mOrderIndex = 18;
		mFlags = YakuFlag.None;
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return junkoCount >= 3;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsWithoutPair[0] >= 3 || kindCountsWithoutPair[1] >= 3 || kindCountsWithoutPair[2] >= 3;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		boolean[][] flags = new boolean[3][7];		
		Arrays.fill(flags[0], false);
		Arrays.fill(flags[1], false);
		Arrays.fill(flags[2], false);
		for (Junko junko : groups.JunkoList()) {
			int x = junko.value() / 9;
			int y = junko.value() % 9;
			flags[x][y] = true;
		}
		for (int i = 0; i < 3; i++) {
			if (flags[i][0] && flags[i][3] && flags[i][6]) {
				result.add(YakuValue.FromFanValue(this, "一气通贯", (env & YakuEnvironment.MenQianQing) != 0 ? 2 : 1));
				return true;
			}
		}
		return false;
	}

}

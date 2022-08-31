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

public class Yaku33_SanSeTongGuan extends Yaku {

	public Yaku33_SanSeTongGuan() {
		mOrderIndex = 33;
		mFlags = YakuFlag.None;
		mName = "SanSeTongGuan";
		mAncientYaku = true; // 三色通贯为古役
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
		if ((flags[0][0] && flags[1][3] && flags[2][6])
				|| (flags[0][0] && flags[2][3] && flags[1][6])
				|| (flags[1][0] && flags[0][3] && flags[2][6])
				|| (flags[1][0] && flags[2][3] && flags[0][6])
				|| (flags[2][0] && flags[0][3] && flags[1][6])
				|| (flags[2][0] && flags[1][3] && flags[0][6])) {
			result.add(YakuValue.FromFanValue(this, "三色通贯", (env & YakuEnvironment.MenQianQing) != 0 ? 2 : 1));
			return true;
		}
		return false;
	}

}

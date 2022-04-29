package com.mahjong.data.jpn.yaku;

import java.util.Arrays;
import java.util.Collection;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku34_SanSeLianKe extends Yaku {

	public Yaku34_SanSeLianKe() {
		mOrderIndex = 34;
		mFlags = YakuFlag.None;
		mName = "SanSeLianKe";
		mAncientYaku = true; // 三色连刻为古役
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount >= 3;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return (kindCountsWithoutPair[0] + kindCountsWithoutPair[1] + kindCountsWithoutPair[2]) >= 3;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		boolean[][] flags = new boolean[3][9];		
		Arrays.fill(flags[0], false);	
		Arrays.fill(flags[1], false);	
		Arrays.fill(flags[2], false);
		for (Group pung : groups.PungList()) {
			int x = pung.value() / 9;
			int y = pung.value() % 9;
			flags[x][y] = true;
		}
		for (int i = 0; i < 7; i++) {
			if ((flags[0][i] && flags[1][i+1] && flags[2][i+2])
					|| (flags[0][i] && flags[2][i+1] && flags[1][i+2])
					|| (flags[1][i] && flags[0][i+1] && flags[2][i+2])
					|| (flags[1][i] && flags[2][i+1] && flags[0][i+2])
					|| (flags[2][i] && flags[0][i+1] && flags[1][i+2])
					|| (flags[2][i] && flags[1][i+1] && flags[0][i+2])) {
				result.add(YakuValue.FromFanValue(this, "三色连刻", 2));
				return true;
			}
		}				
		return false;
	}

}

package com.mahjong.data.jpn.yaku;

import java.util.Arrays;
import java.util.Collection;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku20_SanSeTongKe extends Yaku {

	public Yaku20_SanSeTongKe() {
		mOrderIndex = 20;
		mFlags = YakuFlag.None;
		mName = "SanSeTongKe";
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount >= 3;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsWithoutPair[0] > 0 && kindCountsWithoutPair[1] > 0 && kindCountsWithoutPair[2] > 0;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int[] counts = new int[9];	
		Arrays.fill(counts, 0);
		for (Group g : groups.PungList()) {
			int x = g.value() % 9;
			counts[x]++;
		}
		for (int i = 0; i < 9; i++) {
			if (counts[i] == 3) {
				result.add(YakuValue.FromFanValue(this, "三色同刻", 2));
				return true;
			}
		}
		return false;
	}

}

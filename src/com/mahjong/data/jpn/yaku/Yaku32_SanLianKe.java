package com.mahjong.data.jpn.yaku;

import java.util.Arrays;
import java.util.Collection;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku32_SanLianKe extends Yaku {

	public Yaku32_SanLianKe() {
		mOrderIndex = 32;
		mFlags = YakuFlag.None;
		mName = "SanLianKe";
		mAncientYaku = true; // 三连刻为古役
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount >= 3;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsWithoutPair[0] >= 3 || kindCountsWithoutPair[1] >= 3 || kindCountsWithoutPair[2] >= 3;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		boolean[][] flags = new boolean[3][9];		
		Arrays.fill(flags[0], false);	
		Arrays.fill(flags[1], false);	
		Arrays.fill(flags[2], false);
		for (Group pung : groups.PungList()) {
			int x = pung.value() / 9;
			if (x >= 3) continue; // 不包含字牌
			int y = pung.value() % 9;
			flags[x][y] = true;
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 7; j++) {
				if (flags[i][j] && flags[i][j+1] && flags[i][j+2]) {
					result.add(YakuValue.FromFanValue(this, "三连刻", 2));
					return true;
				}
			}
		}				
		return false;
	}

}

package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku59_QingLaoTou extends Yaku {
	
	public Yaku59_QingLaoTou() {
		mOrderIndex = 59;
		mFlags = YakuFlag.YiMan;
		mName = "QingLaoTou";
	}
		
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount == 4;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCounts[3] == 0
				&& (kindCounts[0] >= 1 && kindCounts[0] <= 2)
				&& (kindCounts[1] >= 1 && kindCounts[1] <= 2)
				&& (kindCounts[2] >= 1 && kindCounts[2] <= 2);
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		for (Group group : groups.PungList()) {
			if (!group.hasYaoJiu()) return false;
		}
		result.add(YakuValue.FromFullYaku(this, "清老头", 1));		
		return true;
	}

}

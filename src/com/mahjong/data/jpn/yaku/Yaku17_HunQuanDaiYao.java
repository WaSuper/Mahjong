package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku17_HunQuanDaiYao extends Yaku {

	public Yaku17_HunQuanDaiYao() {
		mOrderIndex = 17;
		mFlags = YakuFlag.None;
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return junkoCount > 0; // 混全带幺九必须有顺子，否则就是混老头
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCounts[0] + kindCounts[1] + kindCounts[2] > 0 && kindCounts[3] > 0;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if (!groups.Pair().hasYaoJiu()) return false;
		for (Group g : groups.JunkoList()) {
			if (!g.hasYaoJiu()) return false;
		}
		for (Group g : groups.PungList()) {
			if (!g.hasYaoJiu()) return false;
		}
		result.add(YakuValue.FromFanValue(this, "混全带幺", (env & YakuEnvironment.MenQianQing) != 0 ? 2 : 1));
		return true;
	}

}

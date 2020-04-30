package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku27_ChunQuanDaiYaoJiu extends Yaku {

	public Yaku27_ChunQuanDaiYaoJiu() {
		mOrderIndex = 27;
		mFlags = YakuFlag.None;
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return junkoCount > 0; // 纯全带幺九必须有一个顺子，否则就是清老头
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCounts[3] == 0; // 不能有字牌
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		for (Group group : groups.AllGroups()) {
			if (!group.hasYaoJiu()) return false;
		}
		result.add(YakuValue.FromFanValue(this, "纯全带幺九", (env & YakuEnvironment.MenQianQing) != 0 ? 3 : 2));
		return true;
	}

}

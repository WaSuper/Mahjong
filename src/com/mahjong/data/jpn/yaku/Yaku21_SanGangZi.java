package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.Groups;
import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku21_SanGangZi extends Yaku {

	public Yaku21_SanGangZi() {
		mOrderIndex = 21;
		mFlags = YakuFlag.None;
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount >= 3;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int count = 0;
		for (Group g : groups.PungList()) {
			if (g instanceof Groups.Gang) {
				count++;
			}
		}
		if (count == 3) {
			result.add(YakuValue.FromFanValue(this, "三杠子", 2));
			return true;
		}
		return false;
	}

}

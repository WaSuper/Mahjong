package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku22_DuiDuiHe extends Yaku {

	public Yaku22_DuiDuiHe() {
		mOrderIndex = 22;
		mFlags = YakuFlag.None;
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount == 4;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		// 当役满发生时，该役会被移除
		result.add(YakuValue.FromFanValue(this, "对对和", 2));
		return true;
	}

}

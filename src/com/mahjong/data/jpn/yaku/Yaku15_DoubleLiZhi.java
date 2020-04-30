package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku15_DoubleLiZhi extends Yaku {

	public Yaku15_DoubleLiZhi() {
		mOrderIndex = 15;
		mFlags = YakuFlag.Environment | YakuFlag.MenQianQing;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if ((env & YakuEnvironment.DoubleLiZhi) == 0)  return false;
		result.add(YakuValue.FromFanValue(this, "双立直", 2));
		return true;
	}

}

package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku01_LiZhi extends Yaku {

	public Yaku01_LiZhi() {
		mOrderIndex = 1;
		mFlags = YakuFlag.Environment | YakuFlag.MenQianQing;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if ((env & YakuEnvironment.LiZhi) == 0)  return false;
		result.add(YakuValue.FromFanValue(this, "立直", 1));
		return true;
	}

}

package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku02_YiFa extends Yaku {

	public Yaku02_YiFa() {
		mOrderIndex = 2;
		mFlags = YakuFlag.Environment | YakuFlag.MenQianQing;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if ((env & YakuEnvironment.YiFa) == 0)  return false;
		result.add(YakuValue.FromFanValue(this, "一发", 1));
		return true;
	}

}

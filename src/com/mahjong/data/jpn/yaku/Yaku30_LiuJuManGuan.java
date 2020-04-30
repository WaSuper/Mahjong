package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku30_LiuJuManGuan extends Yaku {

	public Yaku30_LiuJuManGuan() {
		mOrderIndex = 30;
		mFlags = YakuFlag.None;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		result.add(YakuValue.FromFanValue(this, "流局满贯", 5));
		return true;
	}

}

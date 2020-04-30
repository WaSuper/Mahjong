package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku10_HaiDiLaoYue extends Yaku {

	public Yaku10_HaiDiLaoYue() {
		mOrderIndex = 10;
		mFlags = YakuFlag.Environment;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int cmp = YakuEnvironment.FinalPick | YakuEnvironment.ZiMo;
		if ((env & cmp) != cmp)  return false;
		result.add(YakuValue.FromFanValue(this, "海底捞月", 1));
		return true;
	}

}

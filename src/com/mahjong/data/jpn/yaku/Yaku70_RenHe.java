package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku70_RenHe extends Yaku {

	public Yaku70_RenHe() {
		mOrderIndex = 70;
		mFlags = YakuFlag.YiMan | YakuFlag.Environment | YakuFlag.MenQianQing;
		mName = "RenHe";
		mAncientYaku = true; // 人和为古役
	}
		
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if ((env & YakuEnvironment.FirstRound) == 0) return false;
		if ((env & YakuEnvironment.ZiMo) == 0) {
			result.add(YakuValue.FromFullYaku(this, "人和", 1));
		}
		return true;
	}

}

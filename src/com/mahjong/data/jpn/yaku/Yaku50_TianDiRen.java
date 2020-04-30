package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku50_TianDiRen extends Yaku {

	public Yaku50_TianDiRen() {
		mOrderIndex = 50;
		mFlags = YakuFlag.YiMan | YakuFlag.Environment | YakuFlag.MenQianQing;
	}
		
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if ((env & YakuEnvironment.FirstRound) == 0) return false;
		if ((env & YakuEnvironment.ZiMo) != 0) {
			if ((env & YakuEnvironment.SelfWindEast) != 0) {
				result.add(YakuValue.FromFullYaku(this, "天和", 1));
			} else {
				result.add(YakuValue.FromFullYaku(this, "地和", 1));
			}
		} else {
			result.add(YakuValue.FromFullYaku(this, "人和", 1));
		}
		return true;
	}

}

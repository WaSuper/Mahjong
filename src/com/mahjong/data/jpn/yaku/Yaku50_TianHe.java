package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku50_TianHe extends Yaku {

	public Yaku50_TianHe() {
		mOrderIndex = 50;
		mFlags = YakuFlag.YiMan | YakuFlag.Environment | YakuFlag.MenQianQing;
		mName = "TianHe";
	}
		
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if ((env & YakuEnvironment.FirstRound) == 0) return false;
		if ((env & YakuEnvironment.ZiMo) != 0) {
			if ((env & YakuEnvironment.SelfWindEast) != 0) {
				result.add(YakuValue.FromFullYaku(this, "天和", 1));
				mName = "TianHe";
			}
		} 
		return true;
	}

}

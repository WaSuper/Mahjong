package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku11_HeDiMoYu extends Yaku {

	public Yaku11_HeDiMoYu() {
		mOrderIndex = 11;
		mFlags = YakuFlag.Environment;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if ((env & YakuEnvironment.FinalPick ) == 0)  return false;
		if ((env & YakuEnvironment.ZiMo ) != 0)  return false;
		result.add(YakuValue.FromFanValue(this, "河底摸鱼", 1));
		return true;
	}

}

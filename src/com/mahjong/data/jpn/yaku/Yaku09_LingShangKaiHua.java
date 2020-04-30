package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku09_LingShangKaiHua extends Yaku {

	public Yaku09_LingShangKaiHua() {
		mOrderIndex = 9;
		mFlags = YakuFlag.Environment;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if ((env & YakuEnvironment.LingShangKaiHua) == 0)  return false;
		result.add(YakuValue.FromFanValue(this, "岭上开花", 1));
		return true;
	}

}

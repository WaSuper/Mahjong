package com.mahjong.data.jpn.yaku;

import java.util.Collection;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku38_NorthDora extends Yaku {

	public Yaku38_NorthDora() {
		mOrderIndex = 38;
		mFlags = YakuFlag.Dora;
		mName = "NorthDora";
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int doraNorth = (env & YakuEnvironment.DoraNorth) >> 18;
		if (doraNorth < 1 || doraNorth > 4) {
			return false;
		}
		result.add(YakuValue.FromFanValue(this, "拨北宝牌", 0, doraNorth));
		return true;
	}

}

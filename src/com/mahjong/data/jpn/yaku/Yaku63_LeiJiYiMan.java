package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku63_LeiJiYiMan extends Yaku {
	
	public Yaku63_LeiJiYiMan() {
		mOrderIndex = 63;
		mFlags = YakuFlag.YiMan;
		mName = "LeiJiYiMan";
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		result.add(YakuValue.FromFullYaku(this, "累计役满", 1));		
		return true;
	}

}

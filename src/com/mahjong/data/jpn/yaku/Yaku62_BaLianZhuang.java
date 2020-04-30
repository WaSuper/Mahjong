package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku62_BaLianZhuang extends Yaku {
	
	public Yaku62_BaLianZhuang() {
		mOrderIndex = 62;
		mFlags = YakuFlag.YiMan;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		result.add(YakuValue.FromFullYaku(this, "八连庄", 1));		
		return true;
	}

}

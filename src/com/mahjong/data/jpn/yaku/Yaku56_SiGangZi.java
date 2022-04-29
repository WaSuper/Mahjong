package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.Groups.Gang;
import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku56_SiGangZi extends Yaku {

	public Yaku56_SiGangZi() {
		mOrderIndex = 56;
		mFlags = YakuFlag.YiMan;
		mName = "SiGangZi";
	}
		
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount == 4;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		for (Group g : groups.PungList()) {
			if (!(g instanceof Gang)) {
				return false;
			}
		}
		result.add(YakuValue.FromFullYaku(this, "四杠子", 1));		
		return true;
	}

}

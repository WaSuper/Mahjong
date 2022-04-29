package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.GroupState;
import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku31_ShiErLuoTai extends Yaku {

	public Yaku31_ShiErLuoTai() {
		mOrderIndex = 31;
		mFlags = YakuFlag.None;
		mName = "ShiErLuoTai";
		mAncientYaku = true; // 十二落抬为古役
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return junkoCount + pungCount == 4;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if (groups.Pair().state() != GroupState.HePai) return false;
		for (Group g : groups.JunkoList()) {
			if (g.state() != GroupState.FuLou) return false;			
		}
		for (Group g : groups.PungList()) {
			if (g.state() != GroupState.FuLou) return false;			
		}
		result.add(YakuValue.FromFanValue(this, "十二落抬", 1));
		return true;
	}

}

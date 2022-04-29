package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku06_YiPai extends Yaku {

	public Yaku06_YiPai() {
		mOrderIndex = 6;
		mFlags = YakuFlag.None;
		mName = "YiPai";
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount > 0;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsWithoutPair[3] > 0;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int groundwind 	= (env >> 6)&0xf;
		int selfwind 	= (env >> 10)&0xf;			
		int cmp = 0x70 | groundwind | selfwind;
		boolean yes = false;
		for (Group g : groups.PungList()) {
			int value = g.value();
			if (value >= JpnSetting.TON && value <= JpnSetting.CHU) {
				value = 0x01 << (value - JpnSetting.TON);
				if ((value & cmp) != 0) {
					yes = true;
					if (value == groundwind)
						result.add(YakuValue.FromFanValue(this, "场风", 1));
					if (value == selfwind)
						result.add(YakuValue.FromFanValue(this, "自风", 1));
					else if (value == 0x10) 
						result.add(YakuValue.FromFanValue(this, "白", 1));
					else if (value == 0x20) 
						result.add(YakuValue.FromFanValue(this, "发", 1));
					else if (value == 0x40) 
						result.add(YakuValue.FromFanValue(this, "中", 1));
					
				}
			}			
		}
		return yes;
	}

}

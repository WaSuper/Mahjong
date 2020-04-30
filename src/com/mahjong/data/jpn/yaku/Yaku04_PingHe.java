package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.GroupState;
import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku04_PingHe extends Yaku {

	public Yaku04_PingHe() {
		mOrderIndex = 4;
		mFlags = YakuFlag.TingPai | YakuFlag.MenQianQing;
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return junkoCount == 4;
	}

	@Override
	protected int HookCalculateFu(IGroups groups, int env) {
		int fu = ((env & YakuEnvironment.ZiMo) != 0) ? 20 : 30;
		return fu;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if (groups.Pair().state() == GroupState.HePai) return false;
		for (Group g : groups.JunkoList()) {
			if (g.state() == GroupState.FuLou) return false;
			if (g.state() == GroupState.HePai) {
				int value = g.value();
				switch (g.addedIndex()) {
					case 0: //边张
						if (value == 6 || value == 9 || value == 24) { // 789的顺子
							return false;
						}
						break;
					case 1: //嵌张
						return false;
					case 2: //边张
						if (value == 0 || value == 9 || value == 18) { // 123的顺子
							return false;
						}
						break;
					default:
						break;
				}
			}
		}

		int pairValue = groups.Pair().value();
		if (pairValue >= JpnSetting.TON && pairValue <= JpnSetting.CHU) {
			int groundwind 	= (env >> 6)&0xf;
			int selfwind 	= (env >> 10)&0xf;			
			int cmp = 0x70 | groundwind | selfwind;
			pairValue = 0x1 << (pairValue - JpnSetting.TON); 
			if ((pairValue & cmp) != 0) return false;
		}
		result.add(YakuValue.FromFanValue(this, "平和", 1));
		return true;
	}

}

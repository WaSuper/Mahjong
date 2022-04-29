package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.GroupState;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;
import com.mahjong.data.jpn.Groups.Group;

public class Yaku23_SanAnKe extends Yaku {

	public Yaku23_SanAnKe() {
		mOrderIndex = 23;
		mFlags = YakuFlag.TingPai;
		mName = "SanAnKe";
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount >= 3;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		int count = 0;
		for (Group g : groups.PungList()) {
			if (g.state() == GroupState.FuLou) continue;
			//if (g.state() == GroupState.HePai && !SelfWindEquals(g.wind(), env)) continue;
			if (g.state() == GroupState.HePai && ((env & YakuEnvironment.ZiMo) == 0)) continue;
			count++;
		}
		if (count == 3)  {
			result.add(YakuValue.FromFanValue(this, "三暗刻", 2));
			return true;
		}		
		return false;
	}

}

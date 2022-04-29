package com.mahjong.data.jpn.yaku;

import java.util.Collection;
import com.mahjong.data.jpn.GroupState;
import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku54_SiAnKe extends Yaku {

	public Yaku54_SiAnKe() {
		mOrderIndex = 54;
		mFlags = YakuFlag.YiMan | YakuFlag.MenQianQing;
		mName = "SiAnKe";
	}
		
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return pungCount == 4;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		for (Group g : groups.PungList()) {
			if (g.state() == GroupState.FuLou) return false;
			if (g.state() == GroupState.HePai && (env & YakuEnvironment.ZiMo) == 0) return false;
		}

		if (groups.Pair().state() == GroupState.HePai) {
			result.add(YakuValue.FromFullYaku(this, "四暗刻单骑", 2));
			mName = "SiAnKeF1";
		} else {
			if ((env & YakuEnvironment.FirstRound) != 0
					&& (env & YakuEnvironment.ZiMo) != 0
					&& (env & YakuEnvironment.SelfWindEast) != 0) {
				result.add(YakuValue.FromFullYaku(this, "四暗刻单骑", 2));
				mName = "SiAnKeF1";
			} else {
				result.add(YakuValue.FromFullYaku(this, "四暗刻", 1));
			}
		}
		return true;
	}

}

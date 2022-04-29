package com.mahjong.data.jpn.yaku;

import java.util.Collection;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.Tile;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku57_LvYiSe extends Yaku {

	private static final long CheckFlags =
			(1L << JpnSetting.SOU2)
			| (1L << JpnSetting.SOU3)
			| (1L << JpnSetting.SOU4)
			| (1L << JpnSetting.SOU6)
			| (1L << JpnSetting.SOU8)
			| (1L << JpnSetting.HAT);
	
	public Yaku57_LvYiSe() {
		mOrderIndex = 57;
		mFlags = YakuFlag.YiMan;
		mName = "LvYiSe";
	}
		
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return junkoCount + pungCount > 0;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		if (kindCounts[3] > 1) return false;
		return kindCounts[0] == 0 && kindCounts[1] == 0 && kindCounts[2] >= 3;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		long flags = 0;
		for (Tile tile : tiles.AllTiles()) flags |= (1L << tile.Value());
		if ((flags | CheckFlags) != CheckFlags) return false;
		result.add(YakuValue.FromFullYaku(this, "绿一色", 1));		
		return true;
	}

}

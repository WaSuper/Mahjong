package com.mahjong.data.jpn.yaku;

import java.util.Collection;
import java.util.List;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.JpnSetting;
import com.mahjong.data.jpn.SpecialYaku;
import com.mahjong.data.jpn.Tile;
import com.mahjong.data.jpn.YakuEnvironment;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku52_GuoShuiWuShuang extends SpecialYaku {

	private static final long CheckFlags =
			(1L << JpnSetting.MAN1)
			| (1L << JpnSetting.MAN9)
			| (1L << JpnSetting.PIN1)
			| (1L << JpnSetting.PIN9)
			| (1L << JpnSetting.SOU1)
			| (1L << JpnSetting.SOU9)
			| (1L << JpnSetting.TON)
			| (1L << JpnSetting.NAN)
			| (1L << JpnSetting.SHA)
			| (1L << JpnSetting.PEI)
			| (1L << JpnSetting.HAK)
			| (1L << JpnSetting.HAT)
			| (1L << JpnSetting.CHU);
	
	public Yaku52_GuoShuiWuShuang() {
		mOrderIndex = 52;
		mFlags = YakuFlag.YiMan | YakuFlag.MenQianQing;
		mName = "GuoShuiWuShuang";
	}


	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles) {
		return kindCountsFromTiles[0] >= 2 
				&& kindCountsFromTiles[1] >= 2 
				&& kindCountsFromTiles[2] >= 2 
				&& kindCountsFromTiles[3] >= 7;
	}
		
	@Override
	protected void Syanten(ITiles tiles, Integer result) {
		int terminalCount = 0;
		int pairFlag = 0;

		int[] sorted = tiles.SortedTiles();
		for (int i = 0; i < 34; i++) {
			if (sorted[i] == 0) continue;
			if (i > JpnSetting.MAN1 && i < JpnSetting.MAN9
					|| i > JpnSetting.PIN1 && i < JpnSetting.PIN9 
					|| i > JpnSetting.SOU1 && i < JpnSetting.SOU9)
				continue;

			if (pairFlag == 0 && sorted[i] >= 2) {
				pairFlag = 1;
			}
			terminalCount++;
		}

		int num = 13 - pairFlag - terminalCount;
		if (num < result) result = num;
	}
	
	@Override
	protected boolean TestRon(ITiles tiles, int env) {
		List<Tile> HandTiles = tiles.HandTiles();
		if (HandTiles.size() != 14) return false;
		long flags = 0;
		for (int i = 0; i < HandTiles.size(); i++) flags |= 1L << HandTiles.get(i).Value();
		return flags == CheckFlags;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		if (!TestRon(tiles, env)) return false;

		int added = tiles.Added().Value();
		int[] sorted = tiles.SortedTiles();
		if (sorted[added] == 2) {
			result.add(YakuValue.FromFullYaku(this, "国士无双十三面", 2));
			mName = "GuoShuiWuShuangF13";
		} else {
			if ((env & YakuEnvironment.FirstRound) != 0
					&& (env & YakuEnvironment.ZiMo) != 0
					&& (env & YakuEnvironment.SelfWindEast) != 0) {
				result.add(YakuValue.FromFullYaku(this, "国士无双十三面", 2));
				mName = "GuoShuiWuShuangF13";
			} else {
				result.add(YakuValue.FromFullYaku(this, "国士无双", 1));
			}
		}
		return true;
	}

}

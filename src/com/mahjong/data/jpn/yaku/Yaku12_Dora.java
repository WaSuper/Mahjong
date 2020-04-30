package com.mahjong.data.jpn.yaku;

import java.util.Collection;
import java.util.List;

import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Tile;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku12_Dora extends Yaku {

	public Yaku12_Dora() {
		mOrderIndex = 12;
		mFlags = YakuFlag.Dora;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		List<Tile> tList = tiles.AllTiles();
		int dora = 0;
		for (Tile tile : tList) {
			dora += tile.Dora();
		}
		if (dora == 0) return false;
		result.add(YakuValue.FromFanValue(this, "宝牌", 0, dora));
		return true;
	}

}

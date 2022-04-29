package com.mahjong.data.jpn.yaku;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mahjong.data.jpn.Groups.Junko;
import com.mahjong.data.jpn.IGroups;
import com.mahjong.data.jpn.ITiles;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.data.jpn.YakuFlag;
import com.mahjong.data.jpn.YakuValue;

public class Yaku08_YiBeiKou extends Yaku {

	public Yaku08_YiBeiKou() {
		mOrderIndex = 8;
		mFlags = YakuFlag.MenQianQing;
		mName = "YiBeiKou";
	}
	
	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) {
		return junkoCount >= 2;
	}
	
	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts,
			int[] kindCountsWithoutPair) {
		return kindCountsWithoutPair[0] >= 2 || kindCountsWithoutPair[1] >= 2 || kindCountsWithoutPair[2] >= 2;
	}
	
	@Override
	protected boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env) {
		Junko[] junkos = groups.JunkoList();
		Map<Integer, Integer> jMap = new HashMap<Integer, Integer>();
		for (Junko j : junkos) {
			int value = j.value();
			Integer count = jMap.get(value);
			if (count == null) jMap.put(value, 1);
			else jMap.put(value, ++count);
		}
		//if (jMap.size() != junkos.length - 1) return false;
		switch (junkos.length) {
		case 2:
			if (jMap.size() != 1) return false;
			break;
		case 3:
			if (jMap.size() != 1 && jMap.size() != 2) return false;
			break;
		case 4:
			if (jMap.size() != 2 && jMap.size() != 3) return false;
			if (jMap.size() == 2) {
				for (int key : jMap.keySet()) {
					if (jMap.get(key) == 2) { // 此时为二杯口
						return false;
					}
				}
			}
			break;
		default:
			return false;
		}	
		result.add(YakuValue.FromFanValue(this, "一杯口", 1));
		return true;
	}

}

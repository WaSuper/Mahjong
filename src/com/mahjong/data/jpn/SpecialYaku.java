package com.mahjong.data.jpn;

/**
 * 无法通过Groups（雀头、顺子、刻子、杠子）判断的役。
 * 注：添加一个新的特殊役，您需要亲自实现求向听数的方法。
 */
public abstract class SpecialYaku extends Yaku {

	@Override
	protected boolean FilterTest(int junkoCount, int pungCount) { return junkoCount + pungCount == 0;}

	@Override
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts, int[] kindCountsWithoutPair) {
		return FilterTest(kindCountsFromTiles);
	}

	protected boolean FilterTest(int[] kindCountsFromTiles) { return true; }

	/**
	 * 求向听数
	 * @param tiles 要计算的牌集合
	 * @param result 通过此参数返回，同时此参数也表示其他役最小的向听数结果。如果该役的向听数比原先大，则不应该更新该值
	 * @return 返回-1，表示已和牌
	 */
	protected abstract void Syanten(ITiles tiles, Integer result);

	protected abstract boolean TestRon(ITiles tiles, int env);
	
}

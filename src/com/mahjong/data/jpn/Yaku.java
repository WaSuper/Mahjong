package com.mahjong.data.jpn;

import java.util.Collection;

import com.mahjong.common.MjWind;

/**
 * 所有役的基类
 */
public abstract class Yaku implements Comparable<Yaku> {

	/**
	 * 该役的顺序编号，影响显示顺序
	 */
	protected int mOrderIndex;
	/**
	 *  表示役的类型，参考YakuFlag
	 */
	protected int mFlags;
	
	public Yaku() {}
		
	public int orderIndex() {
		return mOrderIndex;
	}
		
	public int flags() {
		return mFlags;
	}
	
	/**
	 * 第一层过滤器，判断顺子数量和刻子数量。返回true则通过，false则不通过
	 * 对于特殊牌型，junkoCount和pungCount都等于0
	 * @param junkoCount 顺子数量
	 * @param pungCount 刻子数量
	 * @return
	 */
	protected boolean FilterTest(int junkoCount, int pungCount) { return true; }
	
	/**
	 * 第二层过滤器，判断牌种类（万[0],饼[1],索[2],字[3]）的数量。后2个参数所统计的对象是groups
	 * @param kindCountsFromTiles 对tiles的统计结果
	 * @param kindCounts 包含雀头的统计结果，换言之是5个group的统计结果
	 * @param kindCountsWithoutPair 不包含雀头，换言之是4个group的统计结果
	 * @return 
	 */
	protected boolean FilterTest(int[] kindCountsFromTiles, int[] kindCounts, int[] kindCountsWithoutPair) { return true; }
	
	/**
	 * 测试并将役名与番数添加至返回集合中。返回false表示不匹配该类负责的任何役
	 * @param result 可修改的返回集合
	 * @param tiles 当前手牌
	 * @param groups 包含雀头、顺子、刻子、杠子。如果是特殊役，该值为null
	 * @param env 环境flags
	 * @return
	 */
	protected abstract boolean Test(Collection<YakuValue> result, ITiles tiles, IGroups groups, int env);
	
	/**
	 * 拦截符数计算。若该方法返回大于0，则后续不会对符数进行任何计算
	 * @param groups
	 * @param env
	 * @return
	 */
	protected int HookCalculateFu(IGroups groups, int env)  { return -1; }

	
	@Override
	public int compareTo(Yaku other) {
		return this.mOrderIndex = other.mOrderIndex;
	}
	
	protected static boolean SelfWindEquals(MjWind wind, int env) {
		int val1 = ((int) env >> 10) & 0xf;
		switch (wind) {
		case East:
			if (val1 == 0x1) return true;
		case South:
			if (val1 == 0x2) return true;
		case West:
			if (val1 == 0x4) return true;
		case North:
			if (val1 == 0x8) return true;
		default:
			return false;
		}
	}
	
}

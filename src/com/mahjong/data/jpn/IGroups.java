package com.mahjong.data.jpn;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.Groups.Pair;
import com.mahjong.data.jpn.Groups.Junko;

public interface IGroups {

	Group[] AllGroups();
	/**
	 * 雀头
	 */
	Pair Pair();
	/**
	 * 所有顺子
	 */
	Junko[] JunkoList();
	/**
	 * 所有刻子和杠子
	 */
	Group[] PungList();
	/**
	 * 面子加雀头总数量
	 */	
	int Count();
	/**
	 * 顺子数量
	 */
	int JunkoCount();
	/**
	 * 刻子和杠子数量
	 */
	int PungCount();
	/**
	 * 5个group(包含雀头)的统计结果
	 */
	int[] KindCounts();
	/**
	 * 4个group(不包含雀头)的统计结果
	 */	
	int[] KindCountsWithoutPair();
	
}

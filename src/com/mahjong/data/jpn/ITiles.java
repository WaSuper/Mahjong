package com.mahjong.data.jpn;

import java.util.List;

public interface ITiles {

	/**
	 * 所有牌，包含Groups
	 */
	List<Tile> AllTiles();

	/**
	 * 只有未副露的手牌
	 */
	List<Tile> HandTiles();

	/**
	 * 荣和或自摸的牌
	 */
	Tile Added();
	
	/**
	 * 按顺序排好的长度为34的数组
	 */
	int[] SortedTiles();
	
	/**
	 * 万[0],饼[1],索[2],字[3]的数量
	 */
	int[] KindCountsFromTiles();

}

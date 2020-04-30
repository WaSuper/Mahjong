package com.mahjong.data.jpn;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TileCollection implements ITiles {

	private List<Tile> mAllTiles;
	private List<Tile> mHandTiles;
	private Tile mAddedTile;
	
	private int[] mSortedTiles;
	private int[] kindCountsFromTiles = {0, 0, 0, 0};
	
	public TileCollection(List<Tile> allTiles, List<Tile> handTiles, Tile added) {
		this.mAllTiles = allTiles;
		Collections.sort(mAllTiles);
		this.mHandTiles = handTiles;
		Collections.sort(mHandTiles);
		this.mAddedTile = added;
		for (Tile tile : mAllTiles) {
			kindCountsFromTiles[tile.SortedLevel()]++;
		}
	}
	
	@Override
	public List<Tile> AllTiles() {
		return mAllTiles;
	}

	@Override
	public List<Tile> HandTiles() {
		return mHandTiles;
	}

	@Override
	public Tile Added() {
		return mAddedTile;
	}

	@Override
	public int[] SortedTiles() {
		if (mSortedTiles == null) {
			mSortedTiles = new int[34];
			Arrays.fill(mSortedTiles, 0);
			for (Tile tile : mAllTiles) {
				mSortedTiles[tile.Value()]++;
			}
		}
		return mSortedTiles;
	}

	@Override
	public int[] KindCountsFromTiles() {
		return kindCountsFromTiles;
	}

	
	
}

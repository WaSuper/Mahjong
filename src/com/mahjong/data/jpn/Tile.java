package com.mahjong.data.jpn;

import com.mahjong.common.MjWind;

/**
 * 牌的基础信息，并包含ID、赤宝牌、宝牌、里宝牌、牌归属方、牌状态信息。
 */
public class Tile implements Comparable<Tile> {

	private int value;
	private boolean isRedDora = false;
	private MjWind owner = MjWind.None;
	private int Dora = 0;
	private int InDora = 0;

	public Tile(int value) {
		this(value, false);
	}

	public Tile(int value, boolean isRedDora) {
		this.value = value;
		this.isRedDora = isRedDora;
	}
	
	public Tile(int value, boolean isRedDora, MjWind owner) {
		this.value = value;
		this.isRedDora = isRedDora;
		this.owner = owner;
	}
	
	public int Value() {
		return value;
	}
	
	public int Dora() {
		return Dora;
	}
	
	public int InDora() {
		return InDora;
	}
	
	public MjWind Wind() {
		return owner;
	}
	
	public boolean IsYaoJiu() {
		if (value > JpnSetting.MAN1 && value < JpnSetting.MAN9
				|| value > JpnSetting.PIN1 && value < JpnSetting.PIN9 
				|| value > JpnSetting.SOU1 && value < JpnSetting.SOU9) {
			return false;
		}
		return true;
	}
	
	public void calDora(int[] doras) {
		Dora = 0;
		for (int d : doras) {
			if (d == value) Dora++;
		}
	}
	
	public void calInDora(int[] inDoras) {
		InDora = 0;
		for (int d : inDoras) {
			if (d == value) InDora++;
		}
	}
	
	public boolean IsRedDora() {
		return isRedDora;
	}
	
	public int SortedLevel() {
		if (value > JpnSetting.SOU9) {
			return 3;
		} else if (value > JpnSetting.PIN9) {
			return 2;
		} else if (value > JpnSetting.MAN9) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int compareTo(Tile other) {
		if (this.value > other.value) return 1;
		else if (this.value == other.value) return 0;
		else return -1;
	}
}

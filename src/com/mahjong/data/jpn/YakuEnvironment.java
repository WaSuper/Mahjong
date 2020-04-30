package com.mahjong.data.jpn;

public class YakuEnvironment {

	public static final int None 			= 0x00000000;	// 无
	public static final int LiZhi 			= 0x00000001;	// 立直
	public static final int ZiMo			= 0x00000002;	// 自摸
	public static final int YiFa			= 0x00000004;	// 一发
	public static final int DoubleLiZhi		= 0x00000008;	// 双立直
	public static final int QiangGang		= 0x00000010;	// 抢杠
	public static final int FirstRound		= 0x00000020;	// 第一巡（天和、地和、人和的可能性）
	public static final int GroundWindEast	= 0x00000040;	// 场风东
	public static final int GroundWindSouth	= 0x00000080;	// 场风南
	public static final int GroundWindWest	= 0x00000100;	// 场风西
	public static final int GroundWindNorth = 0x00000200;	// 场风北
	public static final int SelfWindEast	= 0x00000400;	// 自风东
	public static final int SelfWindSouth	= 0x00000800;	// 自风南
	public static final int SelfWindWest	= 0x00001000;	// 自风西
	public static final int SelfWindNorth	= 0x00002000;	// 自风北
	public static final int FinalPick		= 0x00004000;	// 海底或河底
	public static final int LingShangKaiHua	= 0x00008000;	// 岭上开花
	public static final int MenQianQing		= 0x00010000;	// 门前清
	
}

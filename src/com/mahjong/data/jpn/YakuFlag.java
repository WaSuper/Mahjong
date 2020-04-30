package com.mahjong.data.jpn;

public class YakuFlag {

	/**
	 * 没有特殊要求的役
	 */
	public static final int None = 0x00;	
	/**
	 * 指与牌面无关的役
	 */
	public static final int Environment = 0x01;	
	/**
	 * 指役满。如果某役满成立，所有一般役将失效
	 */
	public static final int YiMan = 0x02;	
	/**
	 * 指对听牌形式有要求的役，典型例子为“平和”、“三暗刻”
	 * 此标志位会将不同的听牌形式视为不同的groups，并分别进行Test
	 */
	public static final int TingPai = 0x04;	
	/**
	 * 只计算Dora的役
	 */
	public static final int Dora = 0x08;	
	/**
	 * 门前清的役
	 */
	public static final int MenQianQing = 0x10;
	
}

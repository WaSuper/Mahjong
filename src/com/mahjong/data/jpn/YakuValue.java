package com.mahjong.data.jpn;

public class YakuValue {

	/**
	 * 番的来源类型
	 */
	private Yaku mSource;
	/**
	 * 显示的名字
	 */
	private String mName;
	/**
	 * 番数，如果为役满则忽略此值
	 */
	private int mFanValue;
	/**
	 * 役满倍数，为0表示非役满
	 */
	private int mFullYaku;
	/**
	 * Dora产生的番数
	 */
	private int mDoraValue;
	
	private YakuValue(Yaku source, String name, int fanValue, int fullYaku, int dora) {
		mSource = source;
		mName = name;
		mFanValue = fanValue;
		mFullYaku = fullYaku;
		mDoraValue = dora;
	}
	
	/**
	 * 构建一个役满
	 * @param source
	 * @param name
	 * @param fullYaku
	 * @return
	 */
	public static YakuValue FromFullYaku(Yaku source, String name, int fullYaku) {
		return new YakuValue(source, name, 0, fullYaku, 0);
	}
	
	/**
	 * 构建一个普通役
	 * @param source
	 * @param name
	 * @param fanValue
	 * @return
	 */
	public static YakuValue FromFanValue(Yaku source, String name, int fanValue) {
		return new YakuValue(source, name, fanValue, 0, 0);
	}
	
	/**
	 * 构建一个普通役（带宝牌数量）
	 * @param source
	 * @param name
	 * @param fanValue
	 * @param dora
	 * @return
	 */
	public static YakuValue FromFanValue(Yaku source, String name, int fanValue, int dora) {
		return new YakuValue(source, name, fanValue, 0, dora);
	}
	
	/**
	 * 构建一个用于移除的键值
	 * @param name
	 * @return
	 */
	public static YakuValue BuildKey(String name) {
		return new YakuValue(null, name, 0, 0, 0);
	}
	
	public Yaku getSource() {
		return mSource;
	}
	
	public String getName() {
		return mName;
	}
	
	public int getFanValue() {
		return mFanValue;
	}
	
	public int getFullYaku() {
		return mFullYaku;
	}
	
	public int getDoraValue() {
		return mDoraValue;
	}
	
	@Override
	public String toString() {
		String s;
		if (mFullYaku != 0) {
			s = mName + "[" + mFullYaku + "倍役满]";
		} else if (mFanValue != 0) {
			s = mName + "[" + mFanValue + "]";
		} else {
			s = mName + "[" + mDoraValue + "]";
		}
		return s;
	}
	
	public String toValueString() {
		String s;
		if (mFullYaku != 0) {
			s = mFullYaku+ "";
		} else if (mFanValue != 0) {
			s = mFanValue + "";
		} else {
			s = mDoraValue + "";
		}
		return s;
	}
	
	@Override
	public int hashCode() {
		return mName.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof YakuValue) && (mName == ((YakuValue)o).mName);
	}
	
}

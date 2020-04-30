package com.mahjong.data.jpn;

import com.mahjong.common.MjWind;

//一般和牌形式可能的情况：
//      雀头	 顺子	刻子	暗杠	大明杠     加杠 
// 门清  	 √	 √	 √	 √
// 副露		 √	 √		 √	  √
// 和牌  	 √	 √	 √
/// <summary>
/// 表示雀头、顺子、刻子、杠子。并包含来源信息。
/// </summary>
public class Groups {
	
	public static abstract class Group {
		
		protected GroupState mState;	// 面子状态
		protected int mValue;			// 面子值
		protected boolean isPung; 		// 是否属于刻子
		protected MjWind mWind;			// 副露牌所属的玩家
		protected int mAddedIndex;		// 附加牌的位置，副露、荣和或自摸
		
		public Group() {}	
		public GroupState state() { return mState; }
		public int value() { return mValue; }
		public boolean isPung() { return isPung; }
		public MjWind wind() { return mWind; }
		public void setWind(MjWind wind) { this.mWind = wind; }
		public int addedIndex() { return mAddedIndex; }
		public abstract boolean hasYaoJiu();
		public int sortedLevel() {
			if (mValue > JpnSetting.SOU9) {
				return 3;
			} else if (mValue > JpnSetting.PIN9) {
				return 2;
			} else if (mValue > JpnSetting.MAN9) {
				return 1;
			} else {
				return 0;
			}
		}
		
	}
	
	/// <summary>
	/// 雀头
	/// </summary>
	public static class Pair extends Group {
		
		public Pair(int value, GroupState state) {
			this(value, state, MjWind.None);
		}
		
		public Pair(int value, GroupState state, MjWind wind) {
			this.mValue = value;
			this.mState = state;
			this.isPung = false;
			this.mAddedIndex = -1;
			this.mWind = wind;
		}
		
		@Override
		public boolean hasYaoJiu() {
			if (mValue > JpnSetting.MAN1 && mValue < JpnSetting.MAN9
					|| mValue > JpnSetting.PIN1 && mValue < JpnSetting.PIN9 
					|| mValue > JpnSetting.SOU1 && mValue < JpnSetting.SOU9) {
				return false;
			}
			return true;
		}
		
	}
	
	/// <summary>
	/// 顺子
	/// </summary>
	public static class Junko extends Group {
		
		public Junko(int value, GroupState state) {
			this(value, state, -1);
		}
		
		public Junko(int value, GroupState state, int addedIndex) {
			this.mValue = value;
			this.mState = state;
			this.isPung = false;
			this.mAddedIndex = addedIndex;
		}
		
		public Junko(int value, GroupState state, MjWind wind) {
			this(value, state);
			this.mWind = wind;
		}
		
		@Override
		public boolean hasYaoJiu() {			
			if (mValue == 0 || mValue == 6 
					|| mValue == 9 || mValue == 15 
					|| mValue == 18 || mValue ==24) {
				return true;
			}
			return false;
		}
	}
	
	/// <summary>
	/// 刻子
	/// </summary>
	public static class Pung extends Group {
				
		public Pung(int value, GroupState state) {
			this(value, state, MjWind.None);
		}
		
		public Pung(int value, GroupState state, MjWind wind) {
			this.mValue = value;
			this.mState = state;
			this.isPung = true;
			this.mAddedIndex = -1;
			this.mWind = wind;
		}
		
		@Override
		public boolean hasYaoJiu() {
			if (mValue > JpnSetting.MAN1 && mValue < JpnSetting.MAN9
					|| mValue > JpnSetting.PIN1 && mValue < JpnSetting.PIN9 
					|| mValue > JpnSetting.SOU1 && mValue < JpnSetting.SOU9) {
				return false;
			}
			return true;
		}
	}
	
	/// <summary>
	/// 杠子（状态为门清时为暗杠；副露则为大明杠或加杠）
	/// </summary>
	public static class Gang extends Group {

		public Gang(int value, GroupState state) {
			this(value, state, MjWind.None);
		}
		
		public Gang(int value, GroupState state, MjWind wind) {
			this.mValue = value;
			this.mState = state;
			this.isPung = true;
			this.mAddedIndex = -1;
			this.mWind = wind;
		}
		
		@Override
		public boolean hasYaoJiu() {
			if (mValue > JpnSetting.MAN1 && mValue < JpnSetting.MAN9
					|| mValue > JpnSetting.PIN1 && mValue < JpnSetting.PIN9 
					|| mValue > JpnSetting.SOU1 && mValue < JpnSetting.SOU9) {
				return false;
			}
			return true;
		}
		
	}
	
}

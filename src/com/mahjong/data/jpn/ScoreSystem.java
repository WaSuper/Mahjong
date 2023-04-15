package com.mahjong.data.jpn;

public class ScoreSystem {

	/**
	 * 计算基本点（自动计算）
	 * 设符数为m，翻数为n，基本点为a，根据式子a=m*2^(n+2)计算出a的数值。
	 * @param score 结果集
	 * @param manguanUp 切上满贯
	 * @return
	 */
	public static int GetBasePoint(Score score, boolean manguanUp) {
		if (score.FullYaku() > 0) {
			return 8000 * score.FullYaku();
		}

		int allFanValue = score.AllFanValue();
		if (allFanValue <= 4) {
			int fu = score.Fu();
			if (manguanUp 
					&& ((allFanValue == 4 && fu == 30) || (allFanValue == 3 && fu == 60))) {
				return 2000;
			} else {
				return Math.min(score.Fu() * (1 << allFanValue + 2), 2000);
			}
			
		} else if (allFanValue == 5) {
			return 2000;
		} else if (allFanValue >= 6 && allFanValue <= 7) {
			return 3000;
		} else if (allFanValue >= 8 && allFanValue <= 10) {
			return 4000;
		} else if (allFanValue >= 11 && allFanValue <= 12) {
			return 6000;
		} else {
			int beishu = allFanValue / 13;
			return 8000 * beishu;
		}
	}
	
	/**
	 * 计算基本点（手动输入）
	 * 设符数为m，翻数为n，基本点为a，根据式子a=m*2^(n+2)计算出a的数值。
	 * @param fan 番数
	 * @param fu 符数
	 * @param manguanUp 切上满贯
	 * @return
	 */
	public static int GetBasePoint(int fan, int fu, boolean manguanUp) {
		if (fan < 0) {
			return -fan * 8000;
		} else if (fan <= 4) {
			if (manguanUp 
					&& ((fan == 4 && fu == 30) || (fan == 3 && fu == 60))) {
				return 2000;
			} else {
				return Math.min(fu * (1 << fan + 2), 2000);
			}
		} else if (fan == 5) {
			return 2000;
		} else if (fan >= 6 && fan <= 7) {
			return 3000;
		} else if (fan >= 8 && fan <= 10) {
			return 4000;
		} else if (fan >= 11 && fan <= 12) {
			return 6000;
		} else {
			int beishu = fan / 13;
			return 8000 * beishu;
		}
	}

	/**
	 * 基本点乘以multiple，并按百进位。
	 * 
	 * @param basePoint
	 * @param multiple
	 * @return
	 */
	public static int GetPoint(int basePoint, int multiple) {
		int point = basePoint * multiple;
		return (point + 99) / 100 * 100;
	}
	
	/**
	 * 基本点乘以multiple，并按百进位。
	 * 
	 * @param basePoint
	 * @param multiple
	 * @return
	 */
	public static int GetPoint(int basePoint, double multiple) {
		int point = (int) (basePoint * multiple);
		return (point + 99) / 100 * 100;
	}
	
	/**
	 * 三位闲家各自支付庄家自摸点数
	 * 
	 * @param basePoint
	 * @return
	 */
	public static int Player_Pay_Dealer_Zimo(int basePoint) {
		return GetPoint(basePoint, 2);
	}
	
	/**
	 * 点炮者支付庄家荣和点数
	 * 
	 * @param basePoint
	 * @return
	 */
	public static int All_Pay_Dealer_Ronghe(int basePoint) {
		return GetPoint(basePoint, 6);
	}
	
	/**
	 * 两位闲家各自支付闲家自摸点数
	 * 
	 * @param basePoint
	 * @return
	 */
	public static int Player_Pay_Player_Zimo(int basePoint) {
		return GetPoint(basePoint, 1);
	}
	
	/**
	 * 庄家支付闲家自摸点数
	 * 
	 * @param basePoint
	 * @return
	 */
	public static int Dealer_Pay_Player_Zimo(int basePoint) {
		return GetPoint(basePoint, 2);
	}
	
	/**
	 * 点炮者支付闲家荣和点数
	 * 
	 * @param basePoint
	 * @return
	 */
	public static int All_Pay_Player_Ronghe(int basePoint) {
		return GetPoint(basePoint, 4);
	}

	public static int compare(Score x, Score y) {
		int cmp = GetBasePoint(x, false) - GetBasePoint(y, false);
//		System.out.println("x=" + GetBasePoint(x) + ", y=" + GetBasePoint(y));
		if (cmp != 0)
			return cmp;
		cmp = x.AllFanValue() - y.AllFanValue();
		if (cmp != 0)
			return cmp;
		cmp = x.Fu() - y.Fu();
		return cmp;
	}
	
	public static int compareForGame34(Score x, Score y) {
		int cmp = GetBasePoint(x, false) - GetBasePoint(y, false);
		return cmp;
	}

}

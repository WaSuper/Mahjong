package com.mahjong.data.jpn;

import java.util.List;

import com.mahjong.data.jpn.yaku.Yaku02_YiFa;
import com.mahjong.data.jpn.yaku.Yaku10_HaiDiLaoYue;
import com.mahjong.data.jpn.yaku.Yaku11_HeDiMoYu;

public class Score {

	private int Fu = 0;
	private int FanValue = 0;
	private int FullYaku = 0;
	private int DoraValue = 0;
	private YakuValue[] YakuValues;

	public Score(int fu, List<YakuValue> yakuValues) {
		Fu = fu;
		YakuValues = new YakuValue[yakuValues.size()];
		yakuValues.toArray(YakuValues);
		for (YakuValue value : YakuValues) {
			FanValue += value.getFanValue();
			FullYaku += value.getFullYaku();
			DoraValue += value.getDoraValue();
		}
	}
	
	public boolean hasYaku() {
		if (FanValue > 0 || FullYaku > 0) {
			return true;
		}
		return false;
	}
	
	public int Fu() {
		return Fu;
	}
	
	public int FanValue() {
		return FanValue;
	}
	
	public int FullYaku() {
		return FullYaku;
	}
	
	public int DoraValue() {
		return DoraValue;
	}

	public int AllFanValue() {
		return FanValue + DoraValue;
	}
	
	public YakuValue[] YakuValues() {
		return YakuValues;
	}
	
	public boolean checkFanfu(int roundCount) {
		if (roundCount < 5) {
			return true;
		}
		if (FullYaku > 0) {
			return true;
		} else {
			int fanfu = getFanfu(roundCount);
			if (FanValue >= fanfu) {
				int validYaku = getFanfuValidYaku();
				if (validYaku >= fanfu) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int getFanfu(int roundCount) {
		return (roundCount - 5) / 4 + 2;
	}
	
	public int getFanfuValidYaku() {
		int invalidYaku = 0;
		for (YakuValue yaku : YakuValues) {
			if (yaku.getSource() instanceof Yaku02_YiFa) {
				invalidYaku++;
			} else if (yaku.getSource() instanceof Yaku10_HaiDiLaoYue) {
				invalidYaku++;
			} else if (yaku.getSource() instanceof Yaku11_HeDiMoYu) {
				invalidYaku++;
			}
		}
		return FanValue - invalidYaku;
	}
	
	public String toFanString() {
		if (FullYaku > 0) {
			if (FullYaku == 1) {
				return "役满";
			} else {
				return FullYaku + "倍役满";
			}			
		} else if (FanValue > 0) {
			int allFanValue = AllFanValue();
			String result;
			if (allFanValue >= 13) {
				int beishu = allFanValue / 13;		
				if (beishu == 1) {
					result = "累计役满";
				} else {
					result = beishu + "倍累计役满";
				}
			} else if (allFanValue >= 11 && allFanValue <= 12) {
				result = "三倍满";
			} else if (allFanValue >= 8 && allFanValue <= 10) {
				result = "倍满";
			} else if (allFanValue >= 6 && allFanValue <= 7) {
				result = "跳满";
			} else if (allFanValue == 5 
					|| (allFanValue == 4 && Fu >= 40)
					|| (allFanValue == 3 && Fu >= 70)) {
				result = "满贯";
			} else {
				result = allFanValue + "番";
			}
			return result;
		} else {
			return "X";
		}
	}
	
	public String toFuString() {
		if (FullYaku > 0) {
			return "X";			
		} 
		return Fu + "符";
	}
	
	@Override
	public String toString() {
		if (FullYaku > 0) {
			if (FullYaku == 1) {
				return "役满";
			} else {
				return FullYaku + "倍役满";
			}			
		} else if (FanValue > 0) {
			int allFanValue = AllFanValue();
			String result = allFanValue + "番" + Fu + "符 ";
			if (allFanValue >= 13) {
				int beishu = allFanValue / 13;		
				if (beishu == 1) {
					result = "累计役满：" + result;
				} else {
					result = beishu + "倍累计役满：" + result;
				}
			} else if (allFanValue >= 11 && allFanValue <= 12) {
				result = "三倍满：" + result;
			} else if (allFanValue >= 8 && allFanValue <= 10) {
				result = "倍满：" + result;
			} else if (allFanValue >= 6 && allFanValue <= 7) {
				result = "跳满：" + result;
			} else if (allFanValue == 5 
					|| (allFanValue == 4 && Fu >= 40)
					|| (allFanValue == 3 && Fu >= 70)) {
				result = "满贯：" + result;
			}
			return result;
		} else {
			return "秘籍：混水摸鱼";
		}
	}
	
}

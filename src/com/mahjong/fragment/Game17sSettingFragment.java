package com.mahjong.fragment;

import java.util.ArrayList;
import com.mahjong.R;
import com.mahjong.common.Game17sSetting;
import com.mahjong.control.BaseManager;
import com.mahjong.control.ManagerTool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Game17sSettingFragment extends BaseSettingFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		defaultMember = 2;
		defaultBaseScore = 50000;
		defaultRet = 5000;
		initKeys();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	private void initKeys() {
		KEY_MA_POINT_4 = Game17sSetting.GAME17S_MA_POINT_4;
		KEY_MA_POINT_3 = Game17sSetting.GAME17S_MA_POINT_3;
		KEY_MA_POINT_2 = Game17sSetting.GAME17S_MA_POINT_2;
		KEY_MEMBER_COUNT = Game17sSetting.GAME17S_MEMBER_COUNT;
		KEY_BATTLE_COUNT = Game17sSetting.GAME17S_BATTLE_COUNT;
		KEY_BASE_POINT = Game17sSetting.GAME17S_BASE_POINT;
		KEY_FANFU = Game17sSetting.GAME17S_FANFU;
		KEY_LIZHI_BELONG = Game17sSetting.GAME17S_LIZHI_BELONG;
		KEY_RET_POINT = Game17sSetting.GAME17S_RET_POINT;
		KEY_LANDSCAPE_MODE = Game17sSetting.GAME17S_LANDSCAPE_MODE;
		KEY_DOUBLE_WIND_4 = Game17sSetting.GAME17S_DOUBLE_WIND_4;
		KEY_GROUND_WIND = Game17sSetting.GAME17S_GROUND_WIND;
		KEY_MANGUAN_UP = Game17sSetting.GAME17S_MANGUAN_UP;
		KEY_NO_FLY = Game17sSetting.GAME17S_NO_FLY;
	}

	@Override
	public void initGameStart() {
		BaseManager tool = ManagerTool.getInstance().getManager();
		tool.setBaseData(mMember, mBaseScore, mMaxFeng - 1); // 数字对应的局数应为减1
		tool.setLiZhiBelong(mLizhiBelong);
		// 将马点统一转为4位
		tool.setMaPoint(changeMaPointTo4());
		tool.setRetPoint(mRetPoint);
		tool.setEnableDoubleWind4(mDoubleWind4Box.isChecked());
		tool.setFengType(mGroundWind);
		tool.setFanfuType(mFanfu);
		tool.setEnableManguanUp(mManguanUpBox.isChecked());
		tool.setEnableNoFly(mNoFlyBox.isChecked());
	}
	
	@Override
	protected String battleCount2String(int count) {
		if (mBattleCountList == null) {
			mBattleCountList = new ArrayList<String>();
			for (int i = 1; i < 11; i++) {
				mBattleCountList.add(i + "");
			}
		}
		return String.valueOf(count);
	}
	
	@Override
	protected int battleString2Count(String battle) {
		return Integer.parseInt(battle);
	}
	
	@Override
	protected String fanfuList2String(int index) {
		if (mFanfuSelectList == null) {
			mFanfuSelectList = new ArrayList<String>();
			mFanfuSelectList.add(getResources().getString(R.string.game17s_FanFu_five));
			mFanfuSelectList.add(getResources().getString(R.string.game17s_FanFu_manguan));
			mFanfuSelectList.add(getResources().getString(R.string.game17s_FanFu_none));
		}
		String type = mFanfuSelectList.get(index);
		return type;
	}

	@Override
	protected String groundwindList2String(int index) {
		if (mGroundWindSelectList == null) {
			mGroundWindSelectList = new ArrayList<String>();
			mGroundWindSelectList.add(getResources().getString(R.string.game17s_groundwind_stay_east));
			mGroundWindSelectList.add(getResources().getString(R.string.game17s_groundwind_take_turns));
		}
		String type = mGroundWindSelectList.get(index);
		return type;
	}
	
}

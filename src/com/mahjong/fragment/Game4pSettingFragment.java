package com.mahjong.fragment;

import java.util.ArrayList;
import com.mahjong.R;
import com.mahjong.common.MjSetting;
import com.mahjong.control.BaseManager;
import com.mahjong.control.ManagerTool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Game4pSettingFragment extends BaseSettingFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		defaultMember = 4;
		defaultBaseScore = 25000;
		defaultRet = 5000;
		initKeys();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	private void initKeys() {
		KEY_MA_POINT_4 = MjSetting.MA_POINT;
		KEY_MEMBER_COUNT = MjSetting.MEMBER_COUNT;
		KEY_BATTLE_COUNT = MjSetting.BATTLE_COUNT;
		KEY_BASE_POINT = MjSetting.BASE_POINT;
		KEY_ENTER_SOUTNWEST = MjSetting.ENTER_SOUTNWEST;
		KEY_FANFU = MjSetting.FANFU;
		KEY_LIZHI_BELONG = MjSetting.LIZHI_BELONG;
		KEY_RET_POINT = MjSetting.RET_POINT;
		KEY_LANDSCAPE_MODE = MjSetting.LANDSCAPE_MODE;
		KEY_DOUBLE_WIND_4 = MjSetting.DOUBLE_WIND_4;
		KEY_MANGUAN_UP = MjSetting.MANGUAN_UP;
		KEY_NO_FLY = MjSetting.NO_FLY;
	}

	@Override
	public void initGameStart() {
		BaseManager tool = ManagerTool.getInstance().getManager();
		tool.setBaseData(4, mBaseScore, mMaxFeng);
		tool.setEnterSouthEast(mEnterSWBox.isChecked());
		tool.setLiZhiBelong(mLizhiBelong);
		tool.setEnableFanFu(mFanfuBox.isChecked());
		tool.setMaPoint(mMaPoints);
		tool.setRetPoint(mRetPoint);
		tool.setEnableDoubleWind4(mDoubleWind4Box.isChecked());
		tool.setEnableManguanUp(mManguanUpBox.isChecked());
		tool.setEnableNoFly(mNoFlyBox.isChecked());
	}
		
	@Override
	protected String battleCount2String(int count) {
		if (mBattleCountList == null) {
			mBattleCountList = new ArrayList<String>();
			mBattleCountList.add(getResources().getString(R.string.battle_one));
			mBattleCountList.add(getResources().getString(R.string.battle_two));
			mBattleCountList.add(getResources().getString(R.string.battle_three));
			mBattleCountList.add(getResources().getString(R.string.battle_four));
		}
		String battle = mBattleCountList.get(count);
		return battle;
	}
	
	@Override
	protected int battleString2Count(String battle) {
		int count = 1;
		if (battle.endsWith(mBattleCountList.get(0))) {
			count = 0;
		} else if (battle.endsWith(mBattleCountList.get(1))) {
			count = 1;
		} else if (battle.endsWith(mBattleCountList.get(2))) {
			count = 2;
		} else if (battle.endsWith(mBattleCountList.get(3))) {
			count = 3;
		} 
		return count;
	}
	
	@Override
	protected String fanfuList2String(int index) {
		return null;
	}

	@Override
	protected String groundwindList2String(int index) {
		return null;
	}
	
}

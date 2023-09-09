package com.mahjong.fragment;

import java.util.ArrayList;
import com.mahjong.R;
import com.mahjong.common.Game3pSetting;
import com.mahjong.control.BaseManager;
import com.mahjong.control.ManagerTool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Game3pSettingFragment extends BaseSettingFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		defaultMember = 3;
		defaultBaseScore = 35000;
		defaultRet = 5000;
		initKeys();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	private void initKeys() {
		KEY_MA_POINT_3 = Game3pSetting.GAME3P_MA_POINT;
		KEY_MEMBER_COUNT = Game3pSetting.GAME3P_MEMBER_COUNT;
		KEY_BATTLE_COUNT = Game3pSetting.GAME3P_BATTLE_COUNT;
		KEY_BASE_POINT = Game3pSetting.GAME3P_BASE_POINT;
		KEY_ENTER_SOUTNWEST = Game3pSetting.GAME3P_ENTER_SOUTNWEST;
		KEY_FANFU = Game3pSetting.GAME3P_FANFU;
		KEY_LIZHI_BELONG = Game3pSetting.GAME3P_LIZHI_BELONG;
		KEY_RET_POINT = Game3pSetting.GAME3P_RET_POINT;
		KEY_LANDSCAPE_MODE = Game3pSetting.GAME3P_LANDSCAPE_MODE;
		KEY_DOUBLE_WIND_4 = Game3pSetting.GAME3P_DOUBLE_WIND_4;
		KEY_MANGUAN_UP = Game3pSetting.GAME3P_MANGUAN_UP;
		KEY_NO_FLY = Game3pSetting.GAME3P_NO_FLY;
		KEY_SQUARE_MODE = Game3pSetting.GAME3P_SQUARE_MODE;
		KEY_FINAL_WINNER_UNLIMITED = Game3pSetting.GAME3P_FINAL_WINNER_UNLIMITED;
	}

	@Override
	public void initGameStart() {
		BaseManager tool = ManagerTool.getInstance().getManager();
		tool.setBaseData(3, mBaseScore, mMaxFeng);
		tool.setEnterSouthEast(mEnterSWBox.isChecked());
		tool.setLiZhiBelong(mLizhiBelong);
		tool.setEnableFanFu(mFanfuBox.isChecked());
		tool.setMaPoint(changeMaPointTo4());
		tool.setRetPoint(mRetPoint);
		tool.setEnableDoubleWind4(mDoubleWind4Box.isChecked());
		tool.setEnableZimoCut(mZimoCutBox.isChecked());
		tool.setEnableManguanUp(mManguanUpBox.isChecked());
		tool.setEnableNoFly(mNoFlyBox.isChecked());
		tool.setEnableFinalWinnerUnlimited(mFinalWinnerUnlimitedBox.isChecked());
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

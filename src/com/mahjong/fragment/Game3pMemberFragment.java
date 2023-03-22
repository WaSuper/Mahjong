package com.mahjong.fragment;

import com.mahjong.common.Game3pSetting;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Game3pMemberFragment extends BaseMemberFragment {
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mMemberCount = 3;
		KEY_MEMBER_COUNT = Game3pSetting.GAME3P_MEMBER_COUNT;
		KEY_EAST = Game3pSetting.GAME3P_PLAYER_EAST;
		KEY_SOUTH = Game3pSetting.GAME3P_PLAYER_SOUTH;
		KEY_WEST = Game3pSetting.GAME3P_PLAYER_WEST;
		KEY_NORTH = Game3pSetting.GAME3P_PLAYER_NORTH;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}

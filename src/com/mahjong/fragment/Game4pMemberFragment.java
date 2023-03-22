package com.mahjong.fragment;

import com.mahjong.common.MjSetting;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Game4pMemberFragment extends BaseMemberFragment {
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mMemberCount = 4;
		KEY_MEMBER_COUNT = MjSetting.MEMBER_COUNT;
		KEY_EAST = MjSetting.PLAYER_EAST;
		KEY_SOUTH = MjSetting.PLAYER_SOUTH;
		KEY_WEST = MjSetting.PLAYER_WEST;
		KEY_NORTH = MjSetting.PLAYER_NORTH;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}

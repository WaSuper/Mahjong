package com.mahjong.fragment;

import com.mahjong.common.Game17sSetting;
import com.mahjong.tools.ShareprefenceTool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class Game17sMemberFragment extends BaseMemberFragment 
			implements OnClickListener, OnLongClickListener {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mMemberCount = ShareprefenceTool.getInstance()
				.getInt(Game17sSetting.GAME17S_MEMBER_COUNT, mContext, 2);
		KEY_MEMBER_COUNT = Game17sSetting.GAME17S_MEMBER_COUNT;
		KEY_EAST = Game17sSetting.GAME17S_PLAYER_EAST;
		KEY_SOUTH = Game17sSetting.GAME17S_PLAYER_SOUTH;
		KEY_WEST = Game17sSetting.GAME17S_PLAYER_WEST;
		KEY_NORTH = Game17sSetting.GAME17S_PLAYER_NORTH;
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
}

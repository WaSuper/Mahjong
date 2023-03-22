package com.mahjong.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SettingGuideAdapter extends FragmentPagerAdapter {

	List<Fragment> mList;

	public SettingGuideAdapter(FragmentManager fm) {
		super(fm);
	}

	public SettingGuideAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.mList = list;
	}

	@Override
	public Fragment getItem(int pos) {
		return mList != null && mList.size() > pos ? mList.get(pos) : null;
	}

	@Override
	public int getCount() {
		return mList != null ? mList.size() : 0;
	}

}

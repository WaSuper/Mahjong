package com.mahjong.adapter;

import java.util.List;

import com.mahjong.R;
import com.mahjong.item.YakuBean;
import com.mahjong.tools.SpecialYakuTool;
import com.mahjong.tools.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

public class SpecialYakuCheckAdapter extends BaseAdapter {

	private Context mContext;
	private List<YakuBean> mList;
	
	public SpecialYakuCheckAdapter(Context context) {
		this.mContext = context;
		mList = SpecialYakuTool.getInstance().getYakuList();
	}
	
	public void clickItem(int position) {
		YakuBean bean = mList.get(position);
		bean.setEnable(!bean.enable());
		SpecialYakuTool.getInstance().saveSpecialYaku();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mList != null ? mList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mList != null ? mList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_checkbox2, null);
        }
		YakuBean yakuBean = mList.get(position);
		CheckBox cbox = ViewHolder.get(convertView, R.id.item_checkbox);
		cbox.setChecked(yakuBean.enable());
		cbox.setText(yakuBean.showName());
		return convertView;
	}

}

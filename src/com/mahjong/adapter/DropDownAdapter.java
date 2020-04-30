package com.mahjong.adapter;

import java.util.List;

import com.mahjong.R;
import com.mahjong.item.DropBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DropDownAdapter extends BaseAdapter {

	private List<DropBean> drops;
	private Context context;

	public DropDownAdapter(List<DropBean> drops, Context context) {
		this.drops = drops;
		this.context = context;
	}

	@Override
	public int getCount() {
		return drops != null ? drops.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return drops != null ? drops.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.dropdown_item, parent, false);
			holder.tv = (TextView) convertView
					.findViewById(R.id.dropdown_item_name);
			holder.tig = (ImageView) convertView
					.findViewById(R.id.dropdown_item_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DropBean dropBean = drops.get(position);
		holder.tv.setText(dropBean.getName());
		if (dropBean.isChoiced()) {
			holder.tig.setVisibility(View.VISIBLE);
		} else {
			holder.tig.setVisibility(View.GONE);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView tv;
		ImageView tig;
	}

}

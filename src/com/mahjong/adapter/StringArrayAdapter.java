package com.mahjong.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mahjong.R;
import com.mahjong.tools.ViewHolder;

public class StringArrayAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<String> mList;
	
	public StringArrayAdapter(Context context) {
		this.mContext = context;
	}
	
	public void setData(List<String> list) {
		this.mList = list;
		this.notifyDataSetChanged();
	}
	
	public int getCount() {
		return mList != null ? mList.size() : 0;
	}

	public Object getItem(int position) {
		return mList != null ? mList.get(position) : null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_text, null);
        }
		TextView text = ViewHolder.get(convertView, R.id.item_text);
		text.setText(mList.get(position));
		return convertView;
	}
}

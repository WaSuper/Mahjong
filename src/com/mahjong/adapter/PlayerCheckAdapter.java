package com.mahjong.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import com.mahjong.R;
import com.mahjong.model.Player;
import com.mahjong.tools.ViewHolder;

public class PlayerCheckAdapter extends BaseAdapter {

	private Context mContext;
	private List<Player> mList;
	private boolean[] isChecked;
	
	public PlayerCheckAdapter(Context context) {
		this.mContext = context;
	}
	
	public void setData(List<Player> list, Player[] curPlayers) {
		this.mList = list;
		if (list != null && list.size() != 0) {
			isChecked = new boolean[list.size()];		
			Arrays.fill(isChecked, false);
			for (Player curPlayer : curPlayers) {
				if (curPlayer != null) {
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getUuid().equals(curPlayer.getUuid())) {
							isChecked[i] = true;
							break;
						}
					}
				}
			}
		} else {
			isChecked = null;
		}
		this.notifyDataSetChanged();
	}
	
	public void setCheck(int position) {
		isChecked[position] = !isChecked[position];
		notifyDataSetChanged();
	}
	
	public List<Player> getCheckPlayers() {
		List<Player> checkPlayers = new ArrayList<Player>();
		if (isChecked != null) {
			for (int i = 0; i < isChecked.length; i++) {
				if (isChecked[i]) {
					checkPlayers.add(mList.get(i));
				}
			}
		}		
		return checkPlayers;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_checkbox, null);
        }
		CheckBox cbox = ViewHolder.get(convertView, R.id.item_checkbox);
		cbox.setText(mList.get(position).getNickName());
		cbox.setChecked(isChecked[position]);
		return convertView;
	}
	
}

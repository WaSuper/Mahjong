package com.mahjong.adapter;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mahjong.R;
import com.mahjong.model.Player;
import com.mahjong.tools.ViewHolder;

public class PlayerSimpleAdapter extends BaseAdapter {

	private Context mContext;
	private List<Player> mList;
	
	public PlayerSimpleAdapter(Context context) {
		this.mContext = context;
	}
	
	public void setData(List<Player> list) {
		this.mList = list;
		this.notifyDataSetChanged();
	}
		
	public static List<Player> getPlayersExceptUesd(String[] playerIds) {
		List<Player> list = Player.getAllPlayer();
		Iterator<Player> iterator = list.iterator();
		while (iterator.hasNext()) {
			Player player = iterator.next();
			for (String id : playerIds) {
				if (id != null && id.equals(player.getUuid())) {
					iterator.remove();
					break;
				}
			}
		}
		return list;
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
		text.setText(mList.get(position).getNickName());
		return convertView;
	}
	
}

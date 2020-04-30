package com.mahjong.adapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mahjong.R;
import com.mahjong.model.Character;
import com.mahjong.model.Player;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.ValueTool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayerManageAdapter extends BaseAdapter {

	private List<Player> players;
	private Map<Long, Character> characters = new HashMap<Long, Character>();
	private boolean[] searchList;
	private Context context;
//	private float radius;
	private DisplayImageOptions options;

	public PlayerManageAdapter(Context context, List<Player> players, List<Character> characters) {
		this.players = players;
		if (players != null) {
			searchList = new boolean[players.size()];
			Arrays.fill(searchList, false);
		} else {
			searchList = null;
		}
		if (characters != null) {			
			for (Character character : characters) {
				this.characters.put(character.getUuid(), character);
			}
		}
		this.context = context;
//		this.radius = ValueTool.dp2px(context, 10);
		this.options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.emo_unknown) //设置图片uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.emo_unknown) //设置图片加载或解码过程中发生错误显示的图片
			.displayer(new RoundedBitmapDisplayer(ValueTool.dp2px(context, 10))) //设置图片的圆角半径
			.build();
		
	}
	
	public void setData(List<Player> players) {
		this.players = players;
		if (players != null) {
			searchList = new boolean[players.size()];
			Arrays.fill(searchList, false);
		} else {
			searchList = null;
		}	
		this.notifyDataSetChanged();
	}

	public List<Player> getPlayers() {
		return players;
	}
	
	public Player getPlayer(int position) {
		return players != null ? players.get(position) : null;
	}
	
	public boolean removePlayer(int position) {
		Player player = players.remove(position);
		if (player != null) {
			notifyDataSetChanged();
			return true;
		}
		return false;
	}
	
	public int searchName(String text) {
		if (players == null || players.size() == 0
				|| text == null || text.equals("")) {
			return -1;
		}
		int index = -1;
		Arrays.fill(searchList, false);
		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			if (player.getName().contains(text) || player.getNickName().contains(text)) {
				if (index == -1) {
					index = i;
				} 
				searchList[i] = true;
			}
		}
		if (index != -1) {
			notifyDataSetChanged();
		}
		return index;
	}
	
	@Override
	public int getCount() {
		return players != null ? players.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return players != null ? players.get(position) : null;
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
					R.layout.item_player, parent, false);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.player_item_icon);
			holder.name = (TextView) convertView
					.findViewById(R.id.player_item_name);
			holder.sign = (TextView) convertView
					.findViewById(R.id.player_item_sign);
			holder.sex = (ImageView) convertView
					.findViewById(R.id.player_item_sex);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Player player = players.get(position);
		holder.name.setText(player.getNickName());
		if (searchList[position]) {
			holder.name.setBackgroundColor(Color.YELLOW);
			holder.name.setTextColor(context.getResources().getColor(R.color.light_blacktext));
		} else {
			holder.name.setBackgroundColor(Color.TRANSPARENT);
			holder.name.setTextColor(Color.WHITE);
		}
		holder.sign.setText(player.getSign());
		holder.sex.setImageResource(player.getSex() == 'M' ? R.drawable.sex_male : R.drawable.sex_female);
//		int imgId = HeadIconTool.String2Id(player.getIcon());
//		if (imgId != -1) {
//			Bitmap bitmap = ImageTool.createRoundConerImage(
//            		BitmapFactory.decodeResource(context.getResources(), imgId), radius);
//			holder.icon.setImageBitmap(bitmap);
//		}
//		else holder.icon.setImageResource(R.drawable.head_none);
		Character character = characters.get(player.getCharacterId());
		EmoticonTool.showEmoticon(character, holder.icon, options);
		return convertView;
	}

	private class ViewHolder {
		ImageView icon;
		TextView name;
		TextView sign;
		ImageView sex;
	}

}

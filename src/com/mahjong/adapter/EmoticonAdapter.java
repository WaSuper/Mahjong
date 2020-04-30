package com.mahjong.adapter;

import java.util.List;

import com.mahjong.R;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.ViewHolder;
import com.mahjong.model.Character;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EmoticonAdapter extends BaseAdapter {

	private Context mContext;
	private List<Character> mCharacters;
	private DisplayImageOptions options;
	
	private int mTextColor = Color.WHITE;
	
	public EmoticonAdapter(Context context) {
		this.mContext = context;
		this.options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.emo_unknown) //设置图片uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.emo_unknown) //设置图片加载或解码过程中发生错误显示的图片
			.build();
	}
	
	public void setData(List<Character> characters) {
		this.mCharacters = characters;
		this.notifyDataSetChanged();
	}
	
	public void setTextColor(int color) {
		mTextColor = color;
	}
	
	@Override
	public int getCount() {
		return mCharacters != null ? mCharacters.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mCharacters != null ? mCharacters.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_emoticon, null);
        }
		ImageView icon = ViewHolder.get(convertView, R.id.emoticon_item_icon);
		TextView name = ViewHolder.get(convertView, R.id.emoticon_item_name);
		Character character = mCharacters.get(position);
		EmoticonTool.showEmoticon(character, icon, options);
		name.setText(character != null ? character.getName() : mContext.getString(R.string.unknown));
		name.setTextColor(mTextColor);
		return convertView;
	}

}

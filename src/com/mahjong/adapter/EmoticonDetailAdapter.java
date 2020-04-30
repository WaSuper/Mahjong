package com.mahjong.adapter;

import java.util.List;

import com.mahjong.R;
import com.mahjong.model.CharacterIcon;
import com.mahjong.tools.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class EmoticonDetailAdapter extends BaseAdapter {

	private Context mContext;
	private int rank;
	private List<CharacterIcon> mIconList;
	private DisplayImageOptions options;
	
	public EmoticonDetailAdapter(Context context, int rank) {
		this.mContext = context;
		this.rank = rank;
		this.options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.emo_unknown) //设置图片uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.emo_unknown) //设置图片加载或解码过程中发生错误显示的图片
			.build();
	}
	
	public void setData(List<CharacterIcon> list) {
		this.mIconList = list;
		this.notifyDataSetChanged();
	}
	
	public List<CharacterIcon> getData() {
		return mIconList;
	}
	
	@Override
	public int getCount() {
		return mIconList != null ? mIconList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mIconList != null ? mIconList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_emoticon_detail, null);
        }
		ImageView icon = ViewHolder.get(convertView, R.id.emoticon_detail_rank_icon);
		CharacterIcon characterIcon = mIconList.get(position);
		if (characterIcon.getCharacterId() == -1) {
			switch (rank) {
			case 1:
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_1_1, icon);
				break;
			case 2:
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_2_1, icon);
				break;
			case 3:
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_3_1, icon);
				break;
			case 4:
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_4_1, icon);
				break;
			default:
				break;
			}
		} else {
			String path = characterIcon.getPath();
			if (path == null || path.isEmpty()) {
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_unknown, icon);
			} else {
				ImageLoader.getInstance().displayImage("file://" + path, icon, options);
			}
		}
		return convertView;
	}

}

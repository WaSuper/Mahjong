package com.mahjong.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.mahjong.R;
import com.mahjong.activity.jpn.RankListActivity.RankListData;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.ValueTool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RankListAdapter extends BaseAdapter {

	private Context mContext;
	
	private int mType;
	private List<RankListData> mRankListDatas;
	
	private DisplayImageOptions options;
	private DecimalFormat pointFormat = new DecimalFormat("##0.0");
	private DecimalFormat firstFormat = new DecimalFormat("##0.0%");
	
	public RankListAdapter(Context context) {
		this.mContext = context;
		this.options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.emo_unknown) //设置图片uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.emo_unknown) //设置图片加载或解码过程中发生错误显示的图片
		.displayer(new RoundedBitmapDisplayer(ValueTool.dp2px(context, 10))) //设置图片的圆角半径
		.build();
	}
	
	public void setData(int type, List<RankListData> datas) {
		this.mType = type;
		this.mRankListDatas = datas;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mRankListDatas != null ? mRankListDatas.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mRankListDatas != null ? mRankListDatas.get(position) : null;
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
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_ranklist, parent, false);
			holder.rank = (TextView) convertView
					.findViewById(R.id.ranklist_item_rank);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.ranklist_item_icon);
			holder.name = (TextView) convertView
					.findViewById(R.id.ranklist_item_name);
			holder.data = (TextView) convertView
					.findViewById(R.id.ranklist_item_data);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RankListData data = mRankListDatas.get(position);
		holder.rank.setText(data.rank + "");
		holder.name.setText(data.player.getNickName());
		EmoticonTool.showEmoticon(data.player, holder.icon, options);
		switch (mType) {
		case 0:
			holder.data.setText(mContext.getString(R.string.rank_point_data, 
					pointFormat.format(data.pointData)));
			break;
		case 1:
			holder.data.setText(mContext.getString(R.string.rank_first_data, 
					firstFormat.format((float)data.firstCount / (float)data.gameCount)));
			break;
		default:
			break;
		}
		return convertView;
	}
	
	private class ViewHolder {
		TextView rank;
		ImageView icon;
		TextView name;
		TextView data;
	}

}

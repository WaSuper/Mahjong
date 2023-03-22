package com.mahjong.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mahjong.R;
import com.mahjong.model.MjResult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MapointRankAdapter extends BaseAdapter {

	private Context mContext;
	private List<RankData> mRanks = new ArrayList<RankData>();
	private DecimalFormat dFormat = new DecimalFormat("##0.0");
	
	public MapointRankAdapter(List<MjResult> results, Context context) {
		mContext = context;
		analysisData(results);
	}
	
	private void analysisData(List<MjResult> results) {
		Map<String, RankData> playerMap = new HashMap<String, RankData>();
		for (MjResult result : results) {
			String[] ids = result.getIds();
			String[] names = result.getNames();
			float[] mas = result.getMas();
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				if (id == null || id.isEmpty()) {
					continue;
				}
				RankData data = playerMap.get(id);
				if (data == null) {
					data = new RankData(id, names[i]);			
					playerMap.put(id, data);
				}
				data.addMa(mas[i]);
			}
		}
		mRanks.clear();
		for (RankData checkData : playerMap.values()) {
			if (mRanks.isEmpty()) {
				mRanks.add(checkData);
			} else {
				boolean isAdd = false;
				for (int i = 0; i < mRanks.size(); i++) {
					RankData sortData = mRanks.get(i);
					if (sortData.ma <= checkData.ma) {
						mRanks.add(i, checkData);
						isAdd = true;
						break;
					}
				}
				if (!isAdd) {
					mRanks.add(checkData);
				}
			}
		}
		for (int i = 0; i < mRanks.size(); i++) {
			mRanks.get(i).setRank(i + 1);
		}
	}
	
	@Override
	public int getCount() {
		return mRanks != null ? mRanks.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mRanks != null ? mRanks.get(position) : null;
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
					R.layout.item_mapoint_rank, parent, false);
			holder.rankView = (TextView) convertView.findViewById(R.id.item_mapoint_rank);
			holder.playerView = (TextView) convertView.findViewById(R.id.item_mapoint_player);
			holder.maView = (TextView) convertView.findViewById(R.id.item_mapoint_ma);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RankData data = mRanks.get(position);
		holder.rankView.setText(data.rank + "");
		holder.playerView.setText(data.name);
		String ma = dFormat.format(data.ma);
		if (data.ma > 0) {
			holder.maView.setText("+" + ma);			
		} else if (data.ma == 0) {
			holder.maView.setText("±" + ma);				
		} else {
			holder.maView.setText(ma);
		}
		return convertView;
	}
	
	class ViewHolder {
		TextView rankView;
		TextView playerView;
		TextView maView;
	}

	class RankData {
		
		int rank;
		String id;
		String name;
		float ma;
		
		public RankData(String id, String name) {
			this.id = id;
			this.name = name;
			this.ma = 0;
		}
		
		public void addMa(float ma) {
			this.ma += ma;
		}
		
		public void setRank(int rank) {
			this.rank = rank;
		}
		
	}
	
}

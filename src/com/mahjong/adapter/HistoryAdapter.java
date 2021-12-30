package com.mahjong.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.mahjong.R;
import com.mahjong.model.MjResult;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter {

	private List<MjResult> mResults;
	private Context mContext;
	private String[] mTypes = new String[4];
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private boolean isBatchMode = false;
	private boolean[] mSelectList;
	
	private HistoryItemListener mListener;
	
	public HistoryAdapter(List<MjResult> results, Context context) {
		mResults = results;
		mContext = context;
		mTypes[0] = mContext.getString(R.string.battle_one);
		mTypes[1] = mContext.getString(R.string.battle_two);
		mTypes[2] = "";
		mTypes[3] = mContext.getString(R.string.battle_four);
		if (results != null && results.size() != 0) {
			mSelectList = new boolean[results.size()];
			Arrays.fill(mSelectList, false);
		} else {
			mSelectList = null;
		}
	}
	
	/**
	 * 设置新数据
	 * 
	 * @param results
	 */
	public void setData(List<MjResult> results) {
		this.mResults = results;
		if (results != null && results.size() != 0) {
			mSelectList = new boolean[results.size()];
			Arrays.fill(mSelectList, false);
		} else {
			mSelectList = null;
		}
		notifyDataSetChanged();
	}
	
	/**
	 * 设置批量模式
	 * 
	 * @param isBatchMode
	 */
	public void setBatchMode(boolean isBatchMode) {
		this.isBatchMode = isBatchMode;
		if (mSelectList != null) {
			Arrays.fill(mSelectList, false);
		}
		notifyDataSetChanged();
	}
	
	/**
	 * 选择单项
	 * 
	 * @param pos
	 * @return 是否全选状态
	 */
	public boolean selectItem(int pos) {
		mSelectList[pos] = !mSelectList[pos];
		notifyDataSetChanged();
		for (int i = 0; i < mSelectList.length; i++) {
			if (!mSelectList[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 全选或取消全选
	 * 
	 * @param isSelect
	 * @return 是否全选状态
	 */
	public boolean selectAll(boolean isSelect) {
		if (mSelectList != null) {
			if (isSelect) {
				Arrays.fill(mSelectList, true);	
				notifyDataSetChanged();
				return true;
			} else {
				Arrays.fill(mSelectList, false);
				notifyDataSetChanged();
				return false;
			}
		} else {
			return false;
		}		
	}
	
	/**
	 * 获取所有选择项
	 * 
	 * @return
	 */
	public List<MjResult> getSelectList() {
		List<MjResult> list = new ArrayList<MjResult>();
		if (isBatchMode) {
			for (int i = 0; i < mSelectList.length; i++) {
				if (mSelectList[i]) {
					list.add(mResults.get(i));
				}
			}
		}
		return list;
	}
	
	@Override
	public int getCount() {
		return mResults != null ? mResults.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mResults != null ? mResults.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_drag_history, parent, false);
			holder.titleView = (TextView) convertView.findViewById(R.id.mjresult_title);
			holder.typeView = (TextView) convertView.findViewById(R.id.mjresult_type);
			holder.startTimeView = (TextView) convertView.findViewById(R.id.mjresult_starttime);
			holder.endTimeView = (TextView) convertView.findViewById(R.id.mjresult_endtime);
			holder.playerViews = new TextView[4];
			holder.playerViews[0] = (TextView) convertView.findViewById(R.id.mjresult_player1);
			holder.playerViews[1] = (TextView) convertView.findViewById(R.id.mjresult_player2);
			holder.playerViews[2] = (TextView) convertView.findViewById(R.id.mjresult_player3);
			holder.playerViews[3] = (TextView) convertView.findViewById(R.id.mjresult_player4);
			holder.scoresViews = new TextView[4];
			holder.scoresViews[0] = (TextView) convertView.findViewById(R.id.mjresult_score1);
			holder.scoresViews[1] = (TextView) convertView.findViewById(R.id.mjresult_score2);
			holder.scoresViews[2] = (TextView) convertView.findViewById(R.id.mjresult_score3);
			holder.scoresViews[3] = (TextView) convertView.findViewById(R.id.mjresult_score4);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.mjresult_cbox);
			holder.noteView = (TextView) convertView.findViewById(R.id.drag_history_note);
			holder.noteView.setMovementMethod(ScrollingMovementMethod.getInstance());
			convertView.findViewById(R.id.drag_history_content_view).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					if (mListener != null) {
						mListener.onItemClick(position);
					}
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MjResult result = mResults.get(position);
		String title = result.getTitle();
		if (title == null || title.isEmpty()) {
			title = mContext.getString(R.string.result_title_none);
		}
		holder.titleView.setText(title);
		holder.titleView.setSelected(true);
		holder.typeView.setText(mTypes[result.getGameType()]);
		holder.startTimeView.setText(dateFormat.format(new Date(result.getStartTime())));
		holder.endTimeView.setText(dateFormat.format(new Date(result.getEndTime())));
		String[] names = result.getNames();
		int[] ranks = result.getRanks();
		int[] scores = result.getPoints();
		float[] mas = result.getMas();
		for (int i = 0; i < 4; i++) {
			int rank = ranks[i];
			holder.playerViews[rank - 1].setText(names[i]);
			String score = scores[i] + "(";
			if (mas[i] > 0) {
				score += "+" + mas[i];
			} else if (mas[i] == 0) {
				score += "±0";
			} else {
				score += mas[i];
			}
			score += ")";
			holder.scoresViews[rank - 1].setText(score);
		}		
		if (isBatchMode) {
			holder.checkBox.setVisibility(View.VISIBLE);
			holder.checkBox.setChecked(mSelectList[position]);
		} else {
			holder.checkBox.setVisibility(View.GONE);
		}
		holder.noteView.setText(result.getNote());
		return convertView;
	}
	
	class ViewHolder {
		TextView titleView;
		TextView typeView;
		TextView startTimeView;
		TextView endTimeView;
		TextView[] playerViews;
		TextView[] scoresViews;
		CheckBox checkBox;
		TextView noteView;
	}
	
	public void setOnItemClickListener(HistoryItemListener listener) {
		this.mListener = listener;
	}
	
	public interface HistoryItemListener {
		public void onItemClick(int position);
	}

}

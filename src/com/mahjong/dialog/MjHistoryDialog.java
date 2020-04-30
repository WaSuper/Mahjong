package com.mahjong.dialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.mahjong.R;
import com.mahjong.model.MjAction;
import com.mahjong.model.MjDetail;
import com.mahjong.model.Player;
import com.mahjong.tools.GetSize;
import com.mahjong.ui.CommonDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MjHistoryDialog extends CommonDialog {

	private ListView mListView;
	private HistoryAdapter mAdapter;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
	private String[] winds = new String[4];
	private String[] nums = new String[4];
	private String ju;
	private String round;
	private String[] mNames;
	private String[] mIds;
	
	public MjHistoryDialog(Context context, List<MjDetail> list, Player[] players) {
		super(context, R.style.MyDialogStyle, 0);
		String[] names = new String[4];
		String[] ids = new String[4];
		for (int i = 0; i < players.length; i++) {
			names[i] = players[i].getNickName();
			ids[i] = players[i].getUuid();
		}
		initData(context, list, names, ids);
	}
	
	public MjHistoryDialog(Context context, List<MjDetail> list, String[] players, String[] ids) {
		super(context, R.style.MyDialogStyle, 0);
		initData(context, list, players, ids);
	}
	
	private void initData(Context context, List<MjDetail> list, String[] names, String[] ids) {
		mNames = names;
		mIds = ids;
		addView(R.layout.layout_listview);
		setCanceledOnTouchOutside(true);
		titleTextView.setText(R.string.history);
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		mListView = (ListView) view.findViewById(R.id.listview);
		mAdapter = new HistoryAdapter(list);
		setItemCount(list.size(), GetSize.dip2px(mContext, 350));
		mListView.setAdapter(mAdapter);
		winds[0] = mContext.getString(R.string.east);
		winds[1] = mContext.getString(R.string.south);
		winds[2] = mContext.getString(R.string.west);
		winds[3] = mContext.getString(R.string.north);
		nums[0] = mContext.getString(R.string.one);
		nums[1] = mContext.getString(R.string.two);
		nums[2] = mContext.getString(R.string.three);
		nums[3] = mContext.getString(R.string.four);
		ju = mContext.getString(R.string.ju);
		round = mContext.getString(R.string.round);
	}
	
	private class HistoryAdapter extends BaseAdapter {

		List<MjDetail> mDetailList;
		
		public HistoryAdapter(List<MjDetail> list) {
			this.mDetailList = list;
		}
		
		@Override
		public int getCount() {
			return mDetailList != null ? mDetailList.size() : 0;
		}

		@Override
		public Object getItem(int pos) {
			return mDetailList != null ? mDetailList.get(pos) : null;
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_mjdetail, parent, false);
				holder.timeText = (TextView) convertView.findViewById(R.id.detail_time);
				holder.juText = (TextView) convertView.findViewById(R.id.detail_ju);
				holder.roundText = (TextView) convertView.findViewById(R.id.detail_round);
				holder.contentText = (TextView) convertView.findViewById(R.id.detail_content);
				holder.playerTexts = new TextView[4];
				holder.playerTexts[0] = (TextView) convertView.findViewById(R.id.detail_scores_player1);
				holder.playerTexts[1] = (TextView) convertView.findViewById(R.id.detail_scores_player2);
				holder.playerTexts[2] = (TextView) convertView.findViewById(R.id.detail_scores_player3);
				holder.playerTexts[3] = (TextView) convertView.findViewById(R.id.detail_scores_player4);
				holder.scoreTexts = new TextView[4];
				holder.scoreTexts[0] = (TextView) convertView.findViewById(R.id.detail_scores_score1);
				holder.scoreTexts[1] = (TextView) convertView.findViewById(R.id.detail_scores_score2);
				holder.scoreTexts[2] = (TextView) convertView.findViewById(R.id.detail_scores_score3);
				holder.scoreTexts[3] = (TextView) convertView.findViewById(R.id.detail_scores_score4);			
				holder.mScoreLayout = (RelativeLayout) convertView.findViewById(R.id.detail_for_scores);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MjDetail detail = mDetailList.get(position);
			Date date = new Date(detail.getLogTime());
			holder.timeText.setText(simpleDateFormat.format(date));
			int juWind = detail.getJuCount() / 4;
			int juCount = detail.getJuCount() % 4;
			holder.juText.setText(winds[juWind] + nums[juCount] + ju);
			holder.roundText.setText(detail.getRoundCount() + round);
			int[] changeScores = detail.getChangeScores();
			int[] finalScores = detail.getFinalScores();
			boolean isShowScore = false;
			String content = MjDetail.getDetailText(mContext, detail, winds, mNames, mIds);
			switch (detail.getActionId()) {
			case MjAction.ACTION_ZIMO:
			case MjAction.ACTION_BOMB:
			case MjAction.ACTION_HUANGPAILIUJU:
			case MjAction.ACTION_LIUJUMANGUAN:
			case MjAction.ACTION_CHANGESCORE:
				isShowScore = true;
				break;
			case MjAction.ACTION_LIZHI:
				int index = MjDetail.findPlayerOrgIndexById(mIds, detail.getAction().id0);
				content += (" " + changeScores[index] + "→" + finalScores[index]);
				break;
			default:
				break;
			}
			holder.contentText.setText(content);
			if (isShowScore) {
				holder.mScoreLayout.setVisibility(View.VISIBLE);
				for (int i = 0; i < 4; i++) {
					String playerWind = winds[(i + 4 - juCount) % 4];
					holder.playerTexts[i].setText("(" + playerWind + ")" + mNames[i] + ":");
					holder.scoreTexts[i].setText(changeScores[i] + "→" + finalScores[i]);
				}
			} else {
				holder.mScoreLayout.setVisibility(View.GONE);				
			}			
			return convertView;
		}
		
	}
	
	private class ViewHolder {
		TextView timeText;
		TextView juText;
		TextView roundText;
		TextView contentText;
		TextView[] playerTexts;
		TextView[] scoreTexts;
		RelativeLayout mScoreLayout;
	}
			
}

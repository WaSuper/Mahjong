package com.mahjong.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.mahjong.R;
import com.mahjong.item.MjFanBean;
import com.mahjong.model.MjAction;
import com.mahjong.model.MjDetail;
import com.mahjong.model.MjResult;
import com.mahjong.ui.MahjongSpectrum;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HistoryDetailAdapter extends BaseAdapter {

	private Context mContext;
	private MjResult mResult;
	private List<MjDetail> mDetailList;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
	private String[] mNames;
	private String[] mIds;
	
	private String[] winds = new String[4];
	private String[] nums = new String[4];
	private String ju;
	private String round;

	public HistoryDetailAdapter(Context context, MjResult result, List<MjDetail> list) {
		this.mContext = context;
		this.mResult = result;
		this.mDetailList = list;
		mNames = result.getNames();
		mIds = result.getIds();
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
			holder.timeText = (TextView) convertView
					.findViewById(R.id.detail_time);
			holder.timeText.setTextColor(Color.WHITE);
			holder.juText = (TextView) convertView.findViewById(R.id.detail_ju);
			holder.juText.setTextColor(Color.WHITE);
			holder.roundText = (TextView) convertView
					.findViewById(R.id.detail_round);
			holder.roundText.setTextColor(Color.WHITE);
			holder.contentText = (TextView) convertView
					.findViewById(R.id.detail_content);
			holder.contentText.setTextColor(Color.WHITE);
			holder.playerTexts = new TextView[4];
			holder.playerTexts[0] = (TextView) convertView
					.findViewById(R.id.detail_scores_player1);
			holder.playerTexts[1] = (TextView) convertView
					.findViewById(R.id.detail_scores_player2);
			holder.playerTexts[2] = (TextView) convertView
					.findViewById(R.id.detail_scores_player3);
			holder.playerTexts[3] = (TextView) convertView
					.findViewById(R.id.detail_scores_player4);
			holder.scoreTexts = new TextView[4];
			holder.scoreTexts[0] = (TextView) convertView
					.findViewById(R.id.detail_scores_score1);
			holder.scoreTexts[1] = (TextView) convertView
					.findViewById(R.id.detail_scores_score2);
			holder.scoreTexts[2] = (TextView) convertView
					.findViewById(R.id.detail_scores_score3);
			holder.scoreTexts[3] = (TextView) convertView
					.findViewById(R.id.detail_scores_score4);
			for (int i = 0; i < 4; i++) {
				holder.playerTexts[i].setTextColor(Color.WHITE);
				holder.scoreTexts[i].setTextColor(Color.WHITE);
			}
			holder.mScoreLayout = (RelativeLayout) convertView
					.findViewById(R.id.detail_for_scores);
			holder.mSpectrum1 = (MahjongSpectrum) convertView
					.findViewById(R.id.detail_mjspectrum1);
			holder.mSpectrum2 = (MahjongSpectrum) convertView
					.findViewById(R.id.detail_mjspectrum2);
			holder.mSpectrum3 = (MahjongSpectrum) convertView
					.findViewById(R.id.detail_mjspectrum3);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MjDetail detail = mDetailList.get(position);
		MjAction action = detail.getAction();
		Date date = new Date(detail.getLogTime());
		holder.timeText.setText(simpleDateFormat.format(date));
		int juWind = detail.getJuCount() / 4;
		int juCount = detail.getJuCount() % 4;
		holder.juText.setText(winds[juWind] + nums[juCount] + ju);
		holder.roundText.setText(detail.getRoundCount() + round);
		int[] changeScores = detail.getChangeScores();
		int[] finalScores = detail.getFinalScores();
		boolean isShowScore = false;
		holder.mSpectrum1.setVisibility(View.GONE);
		holder.mSpectrum2.setVisibility(View.GONE);
		holder.mSpectrum3.setVisibility(View.GONE);
		String content = MjDetail.getDetailText(mContext, detail, winds,
				mNames, mIds);
		switch (detail.getActionId()) {
		case MjAction.ACTION_ZIMO:
			isShowScore = true;
			showSpectrum(holder.mSpectrum1, action.spt0);
			break;
		case MjAction.ACTION_BOMB:
			isShowScore = true;
			if (action.tag1 > 0) showSpectrum(holder.mSpectrum1, action.spt0);
			if (action.tag1 > 1) showSpectrum(holder.mSpectrum2, action.spt1);
			if (action.tag1 > 2) showSpectrum(holder.mSpectrum3, action.spt2);
			break;
		case MjAction.ACTION_HUANGPAILIUJU:
		case MjAction.ACTION_LIUJUMANGUAN:
		case MjAction.ACTION_CHANGESCORE:
			isShowScore = true;
			break;
		case MjAction.ACTION_LIZHI:
			int index = MjDetail.findPlayerOrgIndexById(mIds, action.id0);
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
				holder.playerTexts[i].setText("(" + playerWind + ")"
						+ mNames[i] + ":");
				holder.scoreTexts[i].setText(changeScores[i] + "→"
						+ finalScores[i]);
			}
		} else {
			holder.mScoreLayout.setVisibility(View.GONE);
		}
		return convertView;
	}

	private void showSpectrum(MahjongSpectrum spectrum, String mjString) {
		MjFanBean bean = new MjFanBean(null, null, null, mjString);
		if (bean.canShowSpectrum()) {
			spectrum.setVisibility(View.VISIBLE);
			spectrum.setData(bean.getCardList(), 
					bean.getPairsList(), bean.getWinCard());
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
		MahjongSpectrum mSpectrum1;
		MahjongSpectrum mSpectrum2;
		MahjongSpectrum mSpectrum3;		
	}

}

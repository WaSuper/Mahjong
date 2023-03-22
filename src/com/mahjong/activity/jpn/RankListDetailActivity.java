package com.mahjong.activity.jpn;

import java.text.DecimalFormat;

import com.mahjong.R;
import com.mahjong.activity.BaseActivity;
import com.mahjong.control.BaseManager;
import com.mahjong.item.MjFanBean;
import com.mahjong.model.Player;
import com.mahjong.model.RankItem;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.ui.MahjongSpectrum;
import com.mahjong.ui.RankChart;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RankListDetailActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	
	private ImageView mBackView;
	private TextView mNameView;
	private ImageView mIconView;
	private TextView mFanView;
	private MahjongSpectrum mSpectrumView;
	private TextView mBattleCountView;
	private TextView mRank1View;
	private TextView mRank2View;
	private TextView mRank3View;
	private TextView mRank4View;
	private TextView mAverageRankView;
	private TextView mMaxBankerView;
	private TextView mPointView;
	private TextView mLizhiView;
	private TextView mHepaiView;
	private TextView mZimoView;
	private TextView mBombView;
	private TextView mFlyView;
	private TextView mChickenView;
	private RankChart mRankChart;
	
	private Player mPlayer;
	private RankItem mRankItem;
	private DecimalFormat rankFormat = new DecimalFormat("##0.0%");
	private DecimalFormat percentFormat = new DecimalFormat("##0.0%");
	
	private int mMainType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_rank_detail);
		mContext = this;
		mMainType = ShareprefenceTool.getInstance().getInt(BaseManager.GAME_TYPE, mContext, 0);
		String id = getIntent().getStringExtra(Player.Col_Uuid);
		if (id != null && !id.isEmpty()) {
			mPlayer = Player.getPlayer(id);
			if (mPlayer == null) {
				Player[] npcs = Player.getNPCPlayers();
				for (Player player : npcs) {
					if (player.getUuid().equals(id)) {
						mPlayer = player;
						break;
					}
				}
				if (mPlayer == null) {
					String name = getResources().getString(R.string.cannot_find_player, id);
					mPlayer = new Player(id, name, name, 'M', "", "");
				}
			}
			mRankItem = RankItem.getRankItem(id, mMainType);
		}		
		initUI();
		initData();
	}
	
	private void initUI() {
		mBackView = (ImageView) findViewById(R.id.rank_detail_back);
		mNameView = (TextView) findViewById(R.id.rank_detail_name);
		mIconView = (ImageView) findViewById(R.id.rank_detail_icon);
		mFanView = (TextView) findViewById(R.id.rank_detail_fan);
		mSpectrumView = (MahjongSpectrum) findViewById(R.id.rank_detail_spectrum);
		mBattleCountView = (TextView) findViewById(R.id.rank_detail_battle_count);
		mRank1View = (TextView) findViewById(R.id.rank_detail_rank_first);
		mRank2View = (TextView) findViewById(R.id.rank_detail_rank_second);
		mRank3View = (TextView) findViewById(R.id.rank_detail_rank_third);
		mRank4View = (TextView) findViewById(R.id.rank_detail_rank_forth);
		mAverageRankView = (TextView) findViewById(R.id.rank_detail_rank_average);
		mMaxBankerView = (TextView) findViewById(R.id.rank_detail_max_banker);
		mPointView = (TextView) findViewById(R.id.rank_detail_point);
		mLizhiView = (TextView) findViewById(R.id.rank_detail_lizhi);
		mHepaiView = (TextView) findViewById(R.id.rank_detail_hepai);
		mZimoView = (TextView) findViewById(R.id.rank_detail_zimo);
		mBombView = (TextView) findViewById(R.id.rank_detail_bomb);
		mFlyView = (TextView) findViewById(R.id.rank_detail_fly);
		mChickenView = (TextView) findViewById(R.id.rank_detail_chicken);
		mRankChart = (RankChart) findViewById(R.id.rank_detail_chart);
		
		mBackView.setOnClickListener(this);
	}
	
	private void initData() {
		if (mPlayer != null) {
			mNameView.setText(mPlayer.getNickName());
			EmoticonTool.showEmoticon(mPlayer, mIconView, EmoticonTool.mDefaultOptions);
		}
		if (mRankItem != null) {
			mFanView.setText(mRankItem.getFanString(mContext));
			MjFanBean fanBean = new MjFanBean("", "", "", mRankItem.getSpectrum());
			if (fanBean.canShowSpectrum()) {
				mSpectrumView.setData(fanBean.getCardList(), 
						fanBean.getPairsList(), fanBean.getWinCard());
			}
			mBattleCountView.setText(mRankItem.getBattleCount() + "");
			mRank1View.setText(rankFormat.format(mRankItem.getRank1Percent()));
			mRank2View.setText(rankFormat.format(mRankItem.getRank2Percent()));
			mRank3View.setText(rankFormat.format(mRankItem.getRank3Percent()));
			mRank4View.setText(rankFormat.format(mRankItem.getRank4Percent()));
			mAverageRankView.setText(new DecimalFormat("##0.00").format(mRankItem.getAverageRank()));
			mMaxBankerView.setText(mRankItem.getMaxBanker() + "");
			mPointView.setText(new DecimalFormat("##0.0").format(mRankItem.getTotalPoint()));
			mLizhiView.setText(percentFormat.format(mRankItem.getLizhiPercent()));
			mHepaiView.setText(percentFormat.format(mRankItem.getHepaiPercent()));
			mZimoView.setText(percentFormat.format(mRankItem.getZimoPercent()));
			mBombView.setText(percentFormat.format(mRankItem.getBombPercent()));
			mFlyView.setText(percentFormat.format(mRankItem.getFlyPercent()));
			mChickenView.setText(percentFormat.format(mRankItem.getChickenPercent()));
			mRankChart.setData(mRankItem.getRecentRanks(), 
					mRankItem.getRecentChickens(), mRankItem.getRecentFlys());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rank_detail_back:
			finish();
			break;
		default:
			break;
		}
	}
	
}

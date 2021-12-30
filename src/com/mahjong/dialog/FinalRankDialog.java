package com.mahjong.dialog;

import java.util.List;

import com.mahjong.R;
import com.mahjong.model.Player;
import com.mahjong.tools.AnalysisTool;
import com.mahjong.tools.AudioTool;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ValueTool;
import com.mahjong.ui.LineChart;
import com.mahjong.ui.MjWinTextView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class FinalRankDialog extends Dialog implements android.view.View.OnClickListener {	
	
	private static final String Show_WinText = "Show_WinText";
	
	private Context mContext;
	
	private ImageView mBackView;
	private ImageView[] mIconViews;
	private TextView[] mNameViews;
	private TextView[] mScoreViews;
	private TextView[] mMapointViews;
	private TextView[] mLizhiTexts;
	private ImageView[] mLizhiMvps;
	private TextView[] mHeTexts;
	private ImageView[] mHeMvps;
	private TextView[] mBombTexts;
	private ImageView[] mBombMvps;
	private LineChart mLineChart;
	private ImageView[] mChickenViews;
	private ImageView[] mFlyViews;
	private MjWinTextView mWinTextView;
	
	private OnCancelListener mListener;
	
	public FinalRankDialog(Context context) {
		super(context, R.style.MyDialogStyle);
		setContentView(R.layout.layout_show_rank);
		this.mContext = context;
		// 设置全屏
		int padding = ValueTool.dp2px(mContext, 10);
		Window win = this.getWindow();
		win.getDecorView().setPadding(padding, padding, padding, padding);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		win.setAttributes(lp);
		initUI();
	}
	
	private void initUI() {
		mBackView = (ImageView) findViewById(R.id.rank_back);
		mIconViews = new ImageView[4];
		mIconViews[0] = (ImageView) findViewById(R.id.rank_img1);
		mIconViews[1] = (ImageView) findViewById(R.id.rank_img2);
		mIconViews[2] = (ImageView) findViewById(R.id.rank_img3);
		mIconViews[3] = (ImageView) findViewById(R.id.rank_img4);
		mNameViews = new TextView[4];
		mNameViews[0] = (TextView) findViewById(R.id.rank_name1);
		mNameViews[1] = (TextView) findViewById(R.id.rank_name2);
		mNameViews[2] = (TextView) findViewById(R.id.rank_name3);
		mNameViews[3] = (TextView) findViewById(R.id.rank_name4);
		mScoreViews = new TextView[4];
		mScoreViews[0] = (TextView) findViewById(R.id.rank_score1);
		mScoreViews[1] = (TextView) findViewById(R.id.rank_score2);
		mScoreViews[2] = (TextView) findViewById(R.id.rank_score3);
		mScoreViews[3] = (TextView) findViewById(R.id.rank_score4);
		mMapointViews = new TextView[4];
		mMapointViews[0] = (TextView) findViewById(R.id.rank_mapoint1);
		mMapointViews[1] = (TextView) findViewById(R.id.rank_mapoint2);
		mMapointViews[2] = (TextView) findViewById(R.id.rank_mapoint3);
		mMapointViews[3] = (TextView) findViewById(R.id.rank_mapoint4);
		mLizhiTexts = new TextView[4];
		mLizhiTexts[0] = (TextView) findViewById(R.id.rank_lizhi1);
		mLizhiTexts[1] = (TextView) findViewById(R.id.rank_lizhi2);
		mLizhiTexts[2] = (TextView) findViewById(R.id.rank_lizhi3);
		mLizhiTexts[3] = (TextView) findViewById(R.id.rank_lizhi4);
		mLizhiMvps = new ImageView[4];
		mLizhiMvps[0] = (ImageView) findViewById(R.id.rank_lizhi1_mvp);
		mLizhiMvps[1] = (ImageView) findViewById(R.id.rank_lizhi2_mvp);
		mLizhiMvps[2] = (ImageView) findViewById(R.id.rank_lizhi3_mvp);
		mLizhiMvps[3] = (ImageView) findViewById(R.id.rank_lizhi4_mvp);		
		mHeTexts = new TextView[4];
		mHeTexts[0] = (TextView) findViewById(R.id.rank_he1);
		mHeTexts[1] = (TextView) findViewById(R.id.rank_he2);
		mHeTexts[2] = (TextView) findViewById(R.id.rank_he3);
		mHeTexts[3] = (TextView) findViewById(R.id.rank_he4);
		mHeMvps = new ImageView[4];
		mHeMvps[0] = (ImageView) findViewById(R.id.rank_he1_mvp);
		mHeMvps[1] = (ImageView) findViewById(R.id.rank_he2_mvp);
		mHeMvps[2] = (ImageView) findViewById(R.id.rank_he3_mvp);
		mHeMvps[3] = (ImageView) findViewById(R.id.rank_he4_mvp);
		mBombTexts = new TextView[4];
		mBombTexts[0] = (TextView) findViewById(R.id.rank_bomb1);
		mBombTexts[1] = (TextView) findViewById(R.id.rank_bomb2);
		mBombTexts[2] = (TextView) findViewById(R.id.rank_bomb3);
		mBombTexts[3] = (TextView) findViewById(R.id.rank_bomb4);
		mBombMvps = new ImageView[4];
		mBombMvps[0] = (ImageView) findViewById(R.id.rank_bomb1_mvp);
		mBombMvps[1] = (ImageView) findViewById(R.id.rank_bomb2_mvp);
		mBombMvps[2] = (ImageView) findViewById(R.id.rank_bomb3_mvp);
		mBombMvps[3] = (ImageView) findViewById(R.id.rank_bomb4_mvp);
		mChickenViews = new ImageView[4];
		mChickenViews[0] = (ImageView) findViewById(R.id.rank_chicken1);
		mChickenViews[1] = (ImageView) findViewById(R.id.rank_chicken2);
		mChickenViews[2] = (ImageView) findViewById(R.id.rank_chicken3);
		mChickenViews[3] = (ImageView) findViewById(R.id.rank_chicken4);
		mFlyViews = new ImageView[4];
		mFlyViews[0] = (ImageView) findViewById(R.id.rank_fly1);
		mFlyViews[1] = (ImageView) findViewById(R.id.rank_fly2);
		mFlyViews[2] = (ImageView) findViewById(R.id.rank_fly3);
		mFlyViews[3] = (ImageView) findViewById(R.id.rank_fly4);
		mLineChart = (LineChart) findViewById(R.id.rank_chart);
		mWinTextView = (MjWinTextView) findViewById(R.id.rank_wintext);
		
		mBackView.setOnClickListener(this);
		mIconViews[0].setOnClickListener(this);
		boolean isShowWinText = ShareprefenceTool.getInstance().getBoolean(Show_WinText, mContext, true);
		mWinTextView.setVisibility(isShowWinText ? View.VISIBLE : View.INVISIBLE);
	}

	public void setData(Player[] players, int[] scores, float[] mapoint, int[] ranks,
			AnalysisTool aTool, AudioTool audioTool) {
//		String[] names = new String[4];
//		for (int i = 0; i < names.length; i++) {
//			names[i] = players[i].getNickName();
//		}
//		setData(names, scores, mapoint, ranks, aTool);
//	}
//	
//	public void setData(String[] names, int[] scores, float[] mapoint, int[] ranks,
//			AnalysisTool aTool) {
		int[] lizhiCounts = aTool.getLizhiCounts();
		int[] heCounts = aTool.getHeCounts();
		int[] bombCounts = aTool.getBombCounts();
		boolean[] flys = aTool.getFlyPlayers();
		boolean[] chickens = aTool.getChickens();
		int[] imgIds = {R.drawable.rank1_frame, R.drawable.rank2_frame, 
				R.drawable.rank3_frame, R.drawable.rank4_frame};
		for (int i = 0; i < 4; i++) {
			int index = ranks[i] - 1;
			// 胜利宣言
			if (index == 0) {
				String sign = players[i].getSign();
				if (sign == null || sign.isEmpty()) {
					sign = mContext.getString(R.string.sign_none);
				}
				mWinTextView.setText(sign);
				if (audioTool != null) audioTool.playGameTop(i);
			}
			// 头像
			Bitmap iconBitmap = EmoticonTool.getEmoticonForRank(players[i], ranks[i]);
			Bitmap rankBitmap = BitmapFactory.decodeResource(mContext.getResources(), imgIds[index]);
			iconBitmap = ImageTool.createRankImage(iconBitmap, rankBitmap, 
					iconBitmap.getWidth(), iconBitmap.getWidth() * 4 / 9);
			mIconViews[index].setImageBitmap(iconBitmap);
			// 名字
			mNameViews[index].setText(players[i].getNickName());
			// 分数
			mScoreViews[index].setText(scores[i] + "");
			// 点数
			if (mapoint[i] > 0) {
				mMapointViews[index].setText("+" + mapoint[i]);
				mMapointViews[index].setTextColor(Color.parseColor("#00BFFF"));
			} else if (mapoint[i] == 0) {
				mMapointViews[index].setText("±0");
			} else {
				mMapointViews[index].setText("" + mapoint[i]);
				mMapointViews[index].setTextColor(Color.parseColor("#FF0000"));
			}
			// 立直数
			mLizhiTexts[index].setText(lizhiCounts[i] + "");
			// 和牌数
			mHeTexts[index].setText(heCounts[i] + "");
			// 点炮数
			mBombTexts[index].setText(bombCounts[i] + "");
			// 起飞标志
			if (flys[i]) mFlyViews[index].setVisibility(View.VISIBLE);
			// 烧鸡标志
			if (chickens[i]) mChickenViews[index].setVisibility(View.VISIBLE);
		}
		List<Integer> mvps = aTool.getLizhiMvps();
		for (int index : mvps) {
			mLizhiMvps[ranks[index] - 1].setVisibility(View.VISIBLE);
		}
		mvps = aTool.getHeMvps();
		for (int index : mvps) {
			mHeMvps[ranks[index] - 1].setVisibility(View.VISIBLE);
		}
		mvps = aTool.getBombMvps();
		for (int index : mvps) {
			mBombMvps[ranks[index] - 1].setVisibility(View.VISIBLE);
		}
		mLineChart.setData(aTool.getBaseScore(), aTool.getDataLen(),
				aTool.getScore1st(), aTool.getScore2nd(), 
				aTool.getScore3rd(), aTool.getScore4th());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rank_back:
			dismiss();
			break;
		case R.id.rank_img1:
			if (mWinTextView.getVisibility() == View.VISIBLE) {
				mWinTextView.setVisibility(View.INVISIBLE);
				ShareprefenceTool.getInstance().setBoolean(Show_WinText, false, mContext);
			} else {
				mWinTextView.setVisibility(View.VISIBLE);
				ShareprefenceTool.getInstance().setBoolean(Show_WinText, true, mContext);
			}
		default:
			break;
		}
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		if (mListener != null) mListener.onCancel();
	}
	
	@Override
	public void cancel() {
		super.cancel();
		if (mListener != null) mListener.onCancel();
	}
	
	public void setOnCancelListener(OnCancelListener listener) {
		mListener = listener;
	}
	
	public interface OnCancelListener {
		void onCancel();
	}
	
}

package com.mahjong.activity.jpn;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.adapter.StringArrayAdapter;
import com.mahjong.common.MjCalcTool;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;
import com.mahjong.common.MjWind;
import com.mahjong.data.jpn.Score;
import com.mahjong.tools.KeyBoardUtil;
import com.mahjong.tools.ToastTool;
import com.mahjong.tools.KeyBoardUtil.OnKeyBoardListener;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.MahjongBaseView.OnTouchEventListener;
import com.mahjong.ui.MahjongBaseView;
import com.mahjong.ui.MahjongDora;
import com.mahjong.ui.MahjongSpectrum;
import com.mahjong.ui.MjCalcTreeView;
import com.mahjong.ui.ext.CountPicker;
import com.mahjong.ui.ext.CountPicker.OnCountSelectedListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CalculateActivity extends Activity 
		implements OnClickListener, OnCheckedChangeListener {

	public static final int MjPairsCode = 101;
	
	private TextView mTitle;
	private ImageView mBack;
	private TextView mNote;
	private TextView mPoint;			// 基本分：25000
	private CheckBox mPlayerBox; 		// 庄家：true or 闲家：false
	private CheckBox mLizhiBox;			// 立直
	private CheckBox mDoubleLizhiBox;	// 双立直
	private CheckBox mFirstRoundBox;	// 一巡
	private CheckBox mFinalPickBox;		// 海底
	private CheckBox mZimoBox;			// 自摸
	private CheckBox mYifaBox;			// 一发
	private CheckBox mQianggangBox;		// 抢杠
	private CheckBox mLingshangBox;		// 岭上开花
	private MahjongSpectrum mjSpectrum;	// 牌谱
	private MahjongDora mjDoraIndicaOut;// 宝牌指示牌
	private MahjongDora mjDoraIndicaIn; // 里宝牌指示牌
	private ImageButton mStartBtn;		// 开始按钮
//	private TextView mResult;			// 输出结果
	private MjCalcTreeView mResult;		// 输出结果
	// 场风
	private LinearLayout mGroundWindLayout;
	private TextView mGroundWindText;	
	// 自风
	private LinearLayout mSelfWindLayout;
	private TextView mSelfWindText;		
	// 立直数
	private LinearLayout mBangCountLayout;
	private TextView mBangCountText;
	// 本场数
	private LinearLayout mRollCountLayout;
	private TextView mRollCountText;
	
	private KeyBoardUtil mKeyBoardUtil;
	
	private List<String> mWindList = new ArrayList<String>();
	private MjWind mGroundWind;
	private MjWind mSelfWind;
	
	private int mBangCount = 0;
	private int mRollCount = 0;
	private int mSelectedCount = 0;
	
//	private String mResultText = "";
	private Score mResultScore = null;
	
	private List<String> mPairsTypeList = new ArrayList<String>();
	private CommonDialog mPairsTypeDialog;
	
	private MahjongBaseView lastMahjongView;
	
	private Context mContext;
	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			int choice = msg.arg1;
			switch (choice) {
			case SHOW_RESULT:
//				mResult.setText(mResultText);
				mResult.setData(mResultScore, mRollCount, mBangCount, 
						mPlayerBox.isChecked(), mZimoBox.isChecked());
				if (mResultScore != null && mResultScore.hasYaku()) {
					mKeyBoardUtil.hide();
				}
				break;
			case SHOW_ERROR_TEXT:
				String text = (String) msg.obj;
				ToastTool.showToast(mContext, text);
				break;
			case SHOW_START:
				showStartItem();
				break;
			default:
				break;
			}
		}
	};
	
	private static final int SHOW_RESULT 	 = 1;
	private static final int SHOW_ERROR_TEXT = 2;
	private static final int SHOW_START 	 = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_calculate);
		mContext = this;
		initUI();
		initData();
		resetData();
	}

	private void initUI() {
		mTitle = (TextView) findViewById(R.id.calculate_title);
		mBack = (ImageView) findViewById(R.id.calculate_back);
		mNote = (TextView) findViewById(R.id.calculate_note);
		mPoint = (TextView) findViewById(R.id.calculate_point);
		mPlayerBox = (CheckBox) findViewById(R.id.calculate_dealer_player);
		mLizhiBox = (CheckBox) findViewById(R.id.calculate_lizhi);
		mDoubleLizhiBox = (CheckBox) findViewById(R.id.calculate_double_lizhi);
		mFirstRoundBox = (CheckBox) findViewById(R.id.calculate_first_round);
		mFinalPickBox = (CheckBox) findViewById(R.id.calculate_final_pick);
		mZimoBox = (CheckBox) findViewById(R.id.calculate_zimo);
		mYifaBox = (CheckBox) findViewById(R.id.calculate_yifa);
		mQianggangBox = (CheckBox) findViewById(R.id.calculate_qianggang);
		mLingshangBox = (CheckBox) findViewById(R.id.calculate_lingshangkaihua);
		mjSpectrum = (MahjongSpectrum) findViewById(R.id.calculate_mahjongspectrum);
		mjDoraIndicaOut = (MahjongDora) findViewById(R.id.calculate_mahjongdora_out);
		mjDoraIndicaIn = (MahjongDora) findViewById(R.id.calculate_mahjongdora_in);
		mStartBtn = (ImageButton) findViewById(R.id.calculate_start);
		mGroundWindLayout = (LinearLayout) findViewById(R.id.calculate_ll_groundwind);
		mGroundWindText = (TextView) findViewById(R.id.calculate_tv_groundwind);
		mSelfWindLayout = (LinearLayout) findViewById(R.id.calculate_ll_selfwind);
		mSelfWindText = (TextView) findViewById(R.id.calculate_tv_selfwind);
		mBangCountLayout = (LinearLayout) findViewById(R.id.calculate_ll_bangcount);
		mBangCountText = (TextView) findViewById(R.id.calculate_tv_bangcount);
		mRollCountLayout = (LinearLayout) findViewById(R.id.calculate_ll_rollcount);
		mRollCountText = (TextView) findViewById(R.id.calculate_tv_rollcount);		
		mKeyBoardUtil = new KeyBoardUtil(mContext, 
				((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
//		mResult = (TextView) findViewById(R.id.calculate_result);
		mResult = (MjCalcTreeView) findViewById(R.id.calculate_tree_result);
		
		mTitle.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mNote.setOnClickListener(this);
		mPoint.setText("25000");
		mPoint.setOnClickListener(this);
		mPlayerBox.setChecked(true);
		//mPlayerBox.setOnCheckedChangeListener(this);
		mLizhiBox.setChecked(false);
		mLizhiBox.setOnCheckedChangeListener(this);
		mDoubleLizhiBox.setChecked(false);
		mDoubleLizhiBox.setOnCheckedChangeListener(this);
		mFirstRoundBox.setChecked(false);
		mFirstRoundBox.setOnCheckedChangeListener(this);
		mFinalPickBox.setChecked(false);
		mFinalPickBox.setOnCheckedChangeListener(this);
		mZimoBox.setChecked(true);
		mZimoBox.setOnCheckedChangeListener(this);
		mYifaBox.setChecked(false);
		mYifaBox.setOnCheckedChangeListener(this);
		mQianggangBox.setChecked(false);
		mQianggangBox.setOnCheckedChangeListener(this);
		mLingshangBox.setChecked(false);
		mLingshangBox.setOnCheckedChangeListener(this);
		mjSpectrum.setOnTouchEventListener(mTouchListener);
		mjDoraIndicaOut.setOnTouchEventListener(mTouchListener);
		mjDoraIndicaIn.setOnTouchEventListener(mTouchListener);
		mGroundWindLayout.setOnClickListener(this);
		mStartBtn.setOnClickListener(this);
		mSelfWindLayout.setOnClickListener(this);
		mBangCountLayout.setOnClickListener(this);
		mRollCountLayout.setOnClickListener(this);
		mKeyBoardUtil.setOnKeyBoardListener(mKeyBoardListener);
	}
	
	private void initData() {
		mWindList.clear();
		mWindList.add(getString(R.string.east));
		mWindList.add(getString(R.string.south));
		mWindList.add(getString(R.string.west));
		mWindList.add(getString(R.string.north));
		
		mPairsTypeList.clear();
		mPairsTypeList.add(getString(R.string.triplet));
		mPairsTypeList.add(getString(R.string.sequence));
		mPairsTypeList.add(getString(R.string.addition_kong));
		mPairsTypeList.add(getString(R.string.exposed_kong));
		mPairsTypeList.add(getString(R.string.concealed_kong));		
	}
	
	private void resetData() {
		
		mPlayerBox.setChecked(true);
		mZimoBox.setChecked(false);
		mDoubleLizhiBox.setChecked(false);
		mFirstRoundBox.setChecked(false);
		mFinalPickBox.setChecked(false);
		mLizhiBox.setChecked(false);
		mYifaBox.setChecked(false);
		mQianggangBox.setChecked(false);
		mLingshangBox.setChecked(false);
		
		mGroundWind = MjWind.East;
		setWindText(mGroundWind, mGroundWindText);
		mSelfWind = MjWind.East;
		setWindText(mSelfWind, mSelfWindText);		
		
		mBangCount = 0;
		mBangCountText.setText("0");
		mRollCount = 0;
		mRollCountText.setText("0");
		
		mjSpectrum.reset();
		mjDoraIndicaIn.reset();
		mjDoraIndicaOut.reset();
		
//		mResultText = "";
//		mResult.setText(mResultText);
		mResultScore = null;
		mResult.setData(mResultScore, 0, 0, true, true);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				showStartItem();
			}
		}, 500);
	}
	
	private void showStartItem() {
		mjSpectrum.showStartItem();
		mKeyBoardUtil.show(mjSpectrum);
		lastMahjongView = mjSpectrum;
	}
	
	private String setWindText(MjWind wind, TextView textView) {
		switch (wind) {
		case East:
			textView.setText(getString(R.string.east));
			break;
		case South:
			textView.setText(getString(R.string.south));
			break;
		case West:
			textView.setText(getString(R.string.west));
			break;
		case North:
			textView.setText(getString(R.string.north));
			break;
		default:
			break;
		}
		return textView.toString();
	}
	
	private MjWind setWindText(String wind, boolean isGroundWind) {
		MjWind w = MjWind.East;
		if (wind == null || wind.equals("")) {
			return MjWind.None;
		} else if (wind == getString(R.string.east)) {
			w = MjWind.East;	
		} else if (wind == getString(R.string.south)) {
			w = MjWind.South;
		} else if (wind == getString(R.string.west)) {
			w = MjWind.West;
		} else if (wind == getString(R.string.north)) {
			w = MjWind.North;
		}
		if (isGroundWind) {
			mGroundWindText.setText(wind);
			mGroundWind = w;
		} else {
			mSelfWindText.setText(wind);
			mSelfWind = w;
			if (mSelfWind == MjWind.East) {
				mPlayerBox.setChecked(true);
			} else {
				mPlayerBox.setChecked(false);
			}
		}
		return w;
	}
	
	private void calculateStart() {
		Message msg = new Message();
		
		List<MjCard> mDarkNums = mjSpectrum.getDarkNums();
		List<MjCardPairs> mBrightNums = mjSpectrum.getBrightNums();
		MjCard mWinNum = mjSpectrum.getWinNum();
		for (int i = 0; i < mDarkNums.size(); i++) {
			if (mDarkNums.get(i).isBlank()) {
				msg.arg1 = SHOW_ERROR_TEXT;
				msg.obj = new String(getString(R.string.input_invalid));
				mHandler.sendMessage(msg);
				return;
			}
		}
		if (mjSpectrum.getWinNum().isBlank()) {
			msg.arg1 = SHOW_ERROR_TEXT;
			msg.obj = new String(getString(R.string.input_invalid));
			mHandler.sendMessage(msg);
			return;
		}
		List<MjCard> mDora = mjDoraIndicaOut.getDoraList();
		List<MjCard> mDoraIn = mjDoraIndicaIn.getDoraList();
//		mResultText = MjCalcTool.calcToResultText(mDarkNums, mBrightNums, mWinNum, 
//				mPlayerBox.isChecked(), mLizhiBox.isChecked(), mDoubleLizhiBox.isChecked(),
//				mFirstRoundBox.isChecked(), mFinalPickBox.isChecked(), mZimoBox.isChecked(),
//				mYifaBox.isChecked(), mQianggangBox.isChecked(), mLingshangBox.isChecked(),
//				mGroundWind, mSelfWind, mBangCount, mRollCount,
//				mDora, mDoraIn);
		mResultScore = MjCalcTool.calcToResultScore(mDarkNums, mBrightNums, mWinNum, 
				mPlayerBox.isChecked(), mLizhiBox.isChecked(), mDoubleLizhiBox.isChecked(),
				mFirstRoundBox.isChecked(), mFinalPickBox.isChecked(), mZimoBox.isChecked(),
				mYifaBox.isChecked(), mQianggangBox.isChecked(), mLingshangBox.isChecked(),
				mGroundWind, mSelfWind, mBangCount, mRollCount,
				mDora, mDoraIn);
		msg.arg1 = SHOW_RESULT;
		mHandler.sendMessage(msg);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.calculate_title:
			//resetData();
			break;
		case R.id.calculate_back:
			this.finish();
			break;
		case R.id.calculate_note:
			resetData();
			break;
		case R.id.calculate_point:
			break;
		case R.id.calculate_ll_groundwind:
			showWindDialog(getString(R.string.ground_wind_),true);
			break;
		case R.id.calculate_ll_selfwind:
			showWindDialog(getString(R.string.self_wind_), false);
			break;
		case R.id.calculate_ll_bangcount:
			showCountDialog(getString(R.string.lizhibang), true);
			break;
		case R.id.calculate_ll_rollcount:
			showCountDialog(getString(R.string.rollcount), false);
			break;
		case R.id.calculate_start:
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					calculateStart();
				}
			}).start();			
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.calculate_dealer_player:			
			break;
		case R.id.calculate_lizhi:
			if (isChecked && mDoubleLizhiBox.isChecked()) {
				mDoubleLizhiBox.setChecked(false);
			}
			checkDoraIn();
			break;
		case R.id.calculate_double_lizhi:
			if (isChecked && mLizhiBox.isChecked()) {
				mLizhiBox.setChecked(false);
			}
			checkDoraIn();
			break;
		case R.id.calculate_zimo:
			break;
		case R.id.calculate_yifa:
			if (!mLizhiBox.isChecked() && !mDoubleLizhiBox.isChecked()) {
				mYifaBox.setChecked(false);
			}
			break;
		case R.id.calculate_first_round:
			if (isChecked) {
				if (mFinalPickBox.isChecked()) mFinalPickBox.setChecked(false);
			}
			break;
		case R.id.calculate_final_pick:
			if (isChecked) {
				if (mFirstRoundBox.isChecked()) mFirstRoundBox.setChecked(false);
				if (mQianggangBox.isChecked()) mQianggangBox.setChecked(false);
				if (mLingshangBox.isChecked()) mLingshangBox.setChecked(false);
			}
			break;
		case R.id.calculate_qianggang:
			if (isChecked) {
				if (mFinalPickBox.isChecked()) mFinalPickBox.setChecked(false);
				if (mLingshangBox.isChecked()) mLingshangBox.setChecked(false);
			}
			break;
		case R.id.calculate_lingshangkaihua:
			if (isChecked) {
				if (mFinalPickBox.isChecked()) mFinalPickBox.setChecked(false);
				if (mQianggangBox.isChecked()) mQianggangBox.setChecked(false);
			}
			break;
		default:
			break;
		}
	}
	
	private void checkDoraIn() {
		if (mLizhiBox.isChecked() || mDoubleLizhiBox.isChecked()) {
			mjDoraIndicaIn.setVisibility(View.VISIBLE);
		} else {
			mjDoraIndicaIn.setVisibility(View.INVISIBLE);
		}
	}
	
	private void showWindDialog(String title, final boolean isGroundWind) {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(title);
		mDialog.ok.setText(getResources().getString(R.string.back));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		ListView listView = (ListView) mDialog.getContentView();
		StringArrayAdapter mAdapter = new StringArrayAdapter(mContext);
		listView.setAdapter(mAdapter);
		mAdapter.setData(mWindList);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String textString = mWindList.get(position);
				setWindText(textString, isGroundWind);
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
	private void showCountDialog(String title, final boolean isBang) {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.layout_countpicker);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(title);
		mDialog.ok.setText(getResources().getString(R.string.ok));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (isBang) {
					mBangCount = mSelectedCount;
					mBangCountText.setText(mSelectedCount + "");
				} else {
					mRollCount = mSelectedCount;
					mRollCountText.setText(mSelectedCount + "");
				}
				mDialog.dismiss();
			}
		});
		CountPicker picker = (CountPicker) mDialog.getContentView().findViewById(R.id.countpicker);
		picker.setCount(0, 10);
		if (isBang) {
			picker.setSelectedCount(mBangCount);
		} else {
			picker.setSelectedCount(mRollCount);
		}
		picker.setOnCountSelectedListener(new OnCountSelectedListener() {
			
			@Override
			public void onCountSelected(int count) {
				mSelectedCount = count;	
			}
		});		
		mDialog.show();
	}
	
	private void showPairsTypeDialog() {
		if (mPairsTypeDialog == null) {
			mPairsTypeDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
			mPairsTypeDialog.addView(R.layout.listview);
			mPairsTypeDialog.setCanceledOnTouchOutside(true);
			mPairsTypeDialog.titleTextView.setText(getString(R.string.please_choose_type));
			mPairsTypeDialog.ok.setText(getResources().getString(R.string.back));
			mPairsTypeDialog.ok.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {					
					mPairsTypeDialog.dismiss();
				}
			});
			ListView listView = (ListView) mPairsTypeDialog.getContentView();
			StringArrayAdapter mAdapter = new StringArrayAdapter(mContext);
			listView.setAdapter(mAdapter);
			mAdapter.setData(mPairsTypeList);
			listView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String type = mPairsTypeList.get(position);
					Intent intent = new Intent(CalculateActivity.this, PairsSelectActivity.class);
					intent.putExtra(PairsSelectActivity.MjPairsType, type);
					startActivityForResult(intent, MjPairsCode);
					mPairsTypeDialog.dismiss();
				}
			});
		}
		if (!mPairsTypeDialog.isShowing()) {
			mPairsTypeDialog.show();
		}		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == MjPairsCode) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					MjCardPairs pairs = bundle.getParcelable(PairsSelectActivity.MjPairsValue);
					if (pairs != null) {
						if (!mjSpectrum.addMjCardPairs(pairs)) {
							ToastTool.showToast(mContext, R.string.please_release_blank_card);
						}
					}
				}
			}
		}
	}
	
	private OnTouchEventListener mTouchListener = new OnTouchEventListener() {
		
		@Override
		public void onTouchPairs(MahjongBaseView view, MjCardPairs pairs) {
			if (lastMahjongView != null && lastMahjongView != view) {
				lastMahjongView.clearTouchItem();
			}
			lastMahjongView = view;
			mKeyBoardUtil.show(view);
		}
		
		@Override
		public void onTouchOne(MahjongBaseView view, MjCard card) {
			if (lastMahjongView != null && lastMahjongView != view) {
				lastMahjongView.clearTouchItem();
			}
			lastMahjongView = view;
			mKeyBoardUtil.show(view);		
		}
		
		@Override
		public void onTouchAdd(MahjongBaseView view) {
			showPairsTypeDialog();
		}
	};
	
	private OnKeyBoardListener mKeyBoardListener = new OnKeyBoardListener() {
		
		@Override
		public void onShow(MahjongBaseView view) {
			
		}
		
		@Override
		public void onHide(MahjongBaseView view) {
			if (view != null) view.clearTouchItem();
			lastMahjongView = null;
		}
		
		@Override
		public void onClick(MahjongBaseView view, int key) {
			if (view != null) view.addMjCard(key);
		}
		
		@Override
		public void onBackSpace(MahjongBaseView view) {
			if (view != null) view.removeTouchItem();
		}
		
		public void onLeft(MahjongBaseView view) {
			if (view != null) view.onPressLeft();
		}
		
		public void onRight(MahjongBaseView view) {
			if (view != null) view.onPressRight();
		}
		
	};
	
}

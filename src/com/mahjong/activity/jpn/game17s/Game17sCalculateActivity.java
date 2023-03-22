package com.mahjong.activity.jpn.game17s;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mahjong.R;
import com.mahjong.activity.BaseActivity;
import com.mahjong.adapter.SpecialYakuCheckAdapter;
import com.mahjong.adapter.StringArrayAdapter;
import com.mahjong.common.MjCalcTool;
import com.mahjong.common.MjCalcTool.Game17sResult;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;
import com.mahjong.common.MjSetting;
import com.mahjong.common.MjWind;
import com.mahjong.data.jpn.Score;
import com.mahjong.tools.GetSize;
import com.mahjong.tools.KeyBoardUtil;
import com.mahjong.tools.ToastTool;
import com.mahjong.tools.KeyBoardUtil.OnKeyBoardListener;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.Mahjong34CardView;
import com.mahjong.ui.MahjongBaseView;
import com.mahjong.ui.MahjongDora;
import com.mahjong.ui.MahjongSpectrum;
import com.mahjong.ui.MjCalcTreeView;
import com.mahjong.ui.MahjongBaseView.OnTouchEventListener;
import com.mahjong.ui.ext.CountPicker;
import com.mahjong.ui.ext.CountPicker.OnCountSelectedListener;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Game17sCalculateActivity extends BaseActivity 
		implements OnClickListener, OnCheckedChangeListener {

	private TextView mTitle;				// 标题
	private TextView mSpecialYaku;			// 特殊役
	private ImageView mBack;				// 返回
	private TextView mNote;					// 重置
	private TextView mPoint;				// 基本分：50000
	private CheckBox mPlayerBox; 			// 庄家：true or 闲家：false
	private CheckBox mLizhiBox;				// 立直
	private CheckBox mYifaBox;				// 一发
	private CheckBox mFinalPickBox;			// 海底
	private CheckBox mDoubleWind4Box;		// 雀头双风4符
	private Mahjong34CardView mj34CardView; // 牌谱 
	private MahjongDora mjDoraIndicaOut; 	// 宝牌指示牌
	private MahjongDora mjDoraIndicaIn; 	// 里宝牌指示牌
	private TextView mResultNumText; 		// 结果排序序号
	private MahjongSpectrum mj34Spectrum; 	// 输出牌谱
	private MjCalcTreeView mResult;			// 输出结果
	private ImageButton mStartBtn;			// 开始按钮
	private ImageView mRandomView;			// 随机设置牌谱
	private CheckBox mEscapeDoraBox;		// 不听宝牌
	// 计量方式
	private LinearLayout mCalcWayLayout;
	private TextView mCalcWayText;	
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
	
	private Game17sResult mGame17sResult = new Game17sResult(1);
	private Game17sResult mGame17sResultEscapeDora = new Game17sResult(2);
//	private Score mResultScore = null;
	private int mScoreIndex = -1;
	private Random mRandom = new Random(System.currentTimeMillis());
	private int mCalcWay = 0;
	private List<String> mCalcWayList = new ArrayList<String>();
	private boolean isShowEscapeDora = false;
	
	private MahjongBaseView lastMahjongView;
	
	private Context mContext;
	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			int choice = msg.arg1;
			switch (choice) {
			case SHOW_RESULT:
				int level = msg.arg2;
				Game17sResult game17sResult;
				if (level == 1) {
					game17sResult = mGame17sResult;
				} else {
					game17sResult = mGame17sResultEscapeDora;
				}
				Score mResultScore = mScoreIndex > -1 ? 
						game17sResult.getScore(mScoreIndex) : null;
				mResult.setData(mResultScore, mRollCount, mBangCount, 
						mPlayerBox.isChecked(), false);
				List<MjCard> cards = mScoreIndex > -1 ? 
						game17sResult.getHandCards(mScoreIndex) : null;
				MjCard addedCard = mScoreIndex > -1 ? 
						game17sResult.getAddedCards(mScoreIndex) : null;
				mj34Spectrum.setData(cards, null, addedCard);
				if (mScoreIndex > -1) {
					mResultNumText.setText((mScoreIndex + 1) + "/" + game17sResult.size());
				} else {
					mResultNumText.setText("0/0");
				}
				if (mResultScore != null && mResultScore.hasYaku()) {
					mKeyBoardUtil.hide();
				}
				mEscapeDoraBox.setVisibility(isShowEscapeDora ? View.VISIBLE : View.GONE);
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
		setContentView(R.layout.activity_game34_calculate);
		mContext = this;
		initUI();
		initData();
		resetData();
	}

	private void initUI() {	
		mTitle = (TextView) findViewById(R.id.game34_calculate_title);
		mSpecialYaku = (TextView) findViewById(R.id.game34_calculate_specialyaku);
		mBack = (ImageView) findViewById(R.id.game34_calculate_back);
		mNote = (TextView) findViewById(R.id.game34_calculate_note);
		mPoint = (TextView) findViewById(R.id.game34_calculate_point);
		mPlayerBox = (CheckBox) findViewById(R.id.game34_calculate_dealer_player);
		mLizhiBox = (CheckBox) findViewById(R.id.game34_calculate_lizhi);
		mYifaBox = (CheckBox) findViewById(R.id.game34_calculate_yifa);
		mFinalPickBox = (CheckBox) findViewById(R.id.game34_calculate_final_pick);
		mDoubleWind4Box = (CheckBox) findViewById(R.id.game34_calculate_doublewind4);
		mStartBtn = (ImageButton) findViewById(R.id.game34_calculate_start);
		mGroundWindLayout = (LinearLayout) findViewById(R.id.game34_calculate_ll_groundwind);
		mGroundWindText = (TextView) findViewById(R.id.game34_calculate_tv_groundwind);
		mSelfWindLayout = (LinearLayout) findViewById(R.id.game34_calculate_ll_selfwind);
		mSelfWindText = (TextView) findViewById(R.id.game34_calculate_tv_selfwind);
		mBangCountLayout = (LinearLayout) findViewById(R.id.game34_calculate_ll_bangcount);
		mBangCountText = (TextView) findViewById(R.id.game34_calculate_tv_bangcount);
		mRollCountLayout = (LinearLayout) findViewById(R.id.game34_calculate_ll_rollcount);
		mRollCountText = (TextView) findViewById(R.id.game34_calculate_tv_rollcount);	
		mRandomView = (ImageView) findViewById(R.id.game34_calculate_set_random);
		mResultNumText = (TextView) findViewById(R.id.game34_calculate_result_num);
		mCalcWayLayout = (LinearLayout) findViewById(R.id.game34_calculate_set_way);
		mCalcWayText = (TextView) findViewById(R.id.game34_calculate_tv_way);
		mEscapeDoraBox = (CheckBox) findViewById(R.id.game34_calculate_escape_dora);
		
		mj34CardView = (Mahjong34CardView) findViewById(R.id.game34_calculate_mahjong34cardview);
		mjDoraIndicaOut = (MahjongDora) findViewById(R.id.game34_calculate_mahjongdora_out);
		mjDoraIndicaIn = (MahjongDora) findViewById(R.id.game34_calculate_mahjongdora_in);
		mj34Spectrum = (MahjongSpectrum) findViewById(R.id.game34_calculate_mahjongspectrum);
		mKeyBoardUtil = new KeyBoardUtil(mContext, 
				((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
		mKeyBoardUtil.enableRedDora(false);
		mResult = (MjCalcTreeView) findViewById(R.id.game34_calculate_tree_result);
		
		mTitle.setOnClickListener(this);
		mSpecialYaku.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mNote.setOnClickListener(this);
		mPoint.setText("50000");
		mPoint.setOnClickListener(this);
		mPlayerBox.setChecked(true);
		mLizhiBox.setChecked(true);
		checkDoraIn();
		mLizhiBox.setOnCheckedChangeListener(this);
		mYifaBox.setChecked(false);
		mYifaBox.setOnCheckedChangeListener(this);
		mFinalPickBox.setChecked(false);
		mFinalPickBox.setOnCheckedChangeListener(this);
		mDoubleWind4Box.setChecked(false);
		mDoubleWind4Box.setOnCheckedChangeListener(this);
		mj34CardView.setOnTouchEventListener(mTouchListener);
		mjDoraIndicaOut.setOnTouchEventListener(mTouchListener);
		mjDoraIndicaIn.setOnTouchEventListener(mTouchListener);
		mGroundWindLayout.setOnClickListener(this);
		mStartBtn.setOnClickListener(this);
		mSelfWindLayout.setOnClickListener(this);
		mBangCountLayout.setOnClickListener(this);
		mRollCountLayout.setOnClickListener(this);
		mKeyBoardUtil.setOnKeyBoardListener(mKeyBoardListener);
		mRandomView.setOnClickListener(this);
		mj34Spectrum.setOnClickListener(this);
		mCalcWayLayout.setOnClickListener(this);
		mEscapeDoraBox.setChecked(false);
		mEscapeDoraBox.setVisibility(View.GONE);
		mEscapeDoraBox.setOnCheckedChangeListener(this);
	}
	
	private void initData() {
		mWindList.clear();
		mWindList.add(getString(R.string.east));
		mWindList.add(getString(R.string.south));
		mWindList.add(getString(R.string.west));
		mWindList.add(getString(R.string.north));
		mCalcWayList.add(getString(R.string.calc_way_value));
		mCalcWayList.add(getString(R.string.calc_way_fan));
	}
	
	private void resetData() {
		
		mPlayerBox.setChecked(true);
		mLizhiBox.setChecked(true);
		mYifaBox.setChecked(false);
		mFinalPickBox.setChecked(false);
		mDoubleWind4Box.setChecked(false);
		
		mGroundWind = MjWind.East;
		setWindText(mGroundWind, mGroundWindText);
		mSelfWind = MjWind.East;
		setWindText(mSelfWind, mSelfWindText);		
		
		mBangCount = 0;
		mBangCountText.setText("0");
		mRollCount = 0;
		mRollCountText.setText("0");
		
		mj34CardView.reset();
		mjDoraIndicaIn.reset();
		checkDoraIn();
		mjDoraIndicaOut.reset();
		mj34Spectrum.reset();
		mResultNumText.setText("0/0");
		mEscapeDoraBox.setChecked(false);
		mEscapeDoraBox.setVisibility(View.GONE);
		isShowEscapeDora = false;
		mScoreIndex = -1;
		
		mResult.setData(null, 0, 0, true, true);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				showStartItem();
			}
		}, 500);
	}
	
	private void showStartItem() {
		mj34CardView.showStartItem();
		mKeyBoardUtil.show(mj34CardView);
		lastMahjongView = mj34CardView;
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
	
	private void setCalcWayText(int position) {
		mCalcWay = position;
		mCalcWayText.setText(mCalcWayList.get(position));
	}
	
	private void calculateStart(Game17sResult result) {
		Message msg = new Message();
		
		List<MjCard> mDarkNums = mj34CardView.getAllCards();
		int[] numCounts = new int[38];
		for (MjCard card : mDarkNums) {
			if (card.isBlank()) {
				msg.arg1 = SHOW_ERROR_TEXT;
				msg.obj = new String(getString(R.string.input_invalid));
				mHandler.sendMessage(msg);
				return;
			} else {
				numCounts[card.num]++;
				if (numCounts[card.num] > 4) {
					msg.arg1 = SHOW_ERROR_TEXT;
					msg.obj = new String(getString(R.string.input_invalid));
					mHandler.sendMessage(msg);
					return;
				}
			}
		}
		List<MjCard> mDora = mjDoraIndicaOut.getDoraList();
		List<MjCard> mDoraIn = mjDoraIndicaIn.getDoraList();
		MjCalcTool.calcGame17sToResultScores(mCalcWay, result, mDarkNums,
				mPlayerBox.isChecked(), mLizhiBox.isChecked(), 
				mYifaBox.isChecked(), mFinalPickBox.isChecked(),
				mGroundWind, mSelfWind, mBangCount, mRollCount,
				mDora, mDoraIn, mDoubleWind4Box.isChecked(), 0);
		int[] doras = MjCalcTool.convertCard2CardDoras(mDora);
		mj34Spectrum.setMarkWinRect(doras);
		mScoreIndex = result.hasYaku() ? 0 : -1;
		if (result.getLevel() == 1) { // 当所有能听的牌都为宝牌时，显示降番按钮
			isShowEscapeDora = result.isAllAddedTileDoras();
		} else { // 当降番后依然只能听宝牌时，需要继续向下降番寻找
			while (result.isAllAddedTileDoras()) {
				result.setLevel1Score(result.getScore(0));
				MjCalcTool.calcGame17sToResultScores(mCalcWay, result, mDarkNums,
						mPlayerBox.isChecked(), mLizhiBox.isChecked(), 
						mYifaBox.isChecked(), mFinalPickBox.isChecked(),
						mGroundWind, mSelfWind, mBangCount, mRollCount,
						mDora, mDoraIn, mDoubleWind4Box.isChecked(), 0);
				mScoreIndex = result.hasYaku() ? 0 : -1;
			}
		}
		msg.arg1 = SHOW_RESULT;
		msg.arg2 = result.getLevel();
		mHandler.sendMessage(msg);
	}
	
	private void setRandomCardView() {
		List<Integer> allList = new ArrayList<Integer>();
		for (int i = MjSetting._man1; i <= MjSetting._dragon_chun; i++) {
			if (i == MjSetting._man5r || i == MjSetting._pin5r || i == MjSetting._bamboo5r) {
				continue;
			}
			for (int j = 0; j < 4; j++) {
				allList.add(i);
			}
		}
		List<MjCard> cardList = new ArrayList<MjCard>();
		for (int i = 0; i < 34; i++) {
			int num = allList.remove(mRandom.nextInt(allList.size()));
			cardList.add(new MjCard(num));
		}
		MjSetting.sortMjCardList(cardList);
		mj34CardView.setData(cardList);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.game34_calculate_title:
			//resetData();
			break;
		case R.id.game34_calculate_back:
			this.finish();
			break;
		case R.id.game34_calculate_note:
			resetData();
			break;
		case R.id.game34_calculate_point:
			break;
		case R.id.game34_calculate_ll_groundwind:
			showWindDialog(getString(R.string.ground_wind_),true);
			break;
		case R.id.game34_calculate_ll_selfwind:
			showWindDialog(getString(R.string.self_wind_), false);
			break;
		case R.id.game34_calculate_ll_bangcount:
			showCountDialog(getString(R.string.lizhibang), true);
			break;
		case R.id.game34_calculate_ll_rollcount:
			showCountDialog(getString(R.string.rollcount), false);
			break;
		case R.id.game34_calculate_start:
			if (isShowEscapeDora) {
				isShowEscapeDora = false;
				mEscapeDoraBox.setChecked(false);
				mEscapeDoraBox.setVisibility(View.GONE);
			}
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					calculateStart(mGame17sResult);
					mGame17sResultEscapeDora.reset();
				}
			}).start();			
			break;
		case R.id.game34_calculate_specialyaku:
			showSpecialYakuDialog();
			break;
		case R.id.game34_calculate_set_random:
			setRandomCardView();
			break;
		case R.id.game34_calculate_mahjongspectrum:
			if (isShowEscapeDora) {
				if (mEscapeDoraBox.isChecked()) {
					showMjSpectrumDialog(mGame17sResultEscapeDora);
				} else {
					showMjSpectrumDialog(mGame17sResult);
				}
			} else {
				showMjSpectrumDialog(mGame17sResult);
			}
			break;
		case R.id.game34_calculate_set_way:
			showCalcWayDialog();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.game34_calculate_dealer_player:			
			break;
		case R.id.game34_calculate_lizhi:
			if (!isChecked) {
				if (mYifaBox.isChecked()) mYifaBox.setChecked(false);
			}
			checkDoraIn();
			break;
		case R.id.game34_calculate_yifa:
			if (!mLizhiBox.isChecked()) {
				mYifaBox.setChecked(false);
			}
			break;
		case R.id.game34_calculate_final_pick:
			break;
		case R.id.game34_calculate_escape_dora:
			if (isShowEscapeDora) {
				mScoreIndex = 0;
				if (isChecked) {
					if (mGame17sResultEscapeDora.isInit()) {
						Message msg = new Message();
						msg.arg1 = SHOW_RESULT;
						msg.arg2 = mGame17sResultEscapeDora.getLevel();
						mHandler.sendMessage(msg);
					} else {
						mGame17sResultEscapeDora.setLevel1Score(mGame17sResult.getScore(0));
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								calculateStart(mGame17sResultEscapeDora);
							}
						}).start();
					}
				} else {
					Message msg = new Message();
					msg.arg1 = SHOW_RESULT;
					msg.arg2 = mGame17sResult.getLevel();
					mHandler.sendMessage(msg);
				}
			}
			break;
		case R.id.game34_calculate_doublewind4:
			break;
		default:
			break;
		}
	}
	
	private void checkDoraIn() {
		if (mLizhiBox.isChecked()) {
			mjDoraIndicaIn.setVisibility(View.VISIBLE);
		} else {
			mjDoraIndicaIn.setVisibility(View.INVISIBLE);
		}
	}
	
	private void showCalcWayDialog() {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getString(R.string.calc_way_));
		mDialog.ok.setText(getResources().getString(R.string.back));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		ListView listView = (ListView) mDialog.getContentView();
		StringArrayAdapter mAdapter = new StringArrayAdapter(mContext);
		listView.setAdapter(mAdapter);
		mAdapter.setData(mCalcWayList);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				setCalcWayText(position);
				mDialog.dismiss();
			}
		});
		mDialog.show();
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
	
	private void showSpecialYakuDialog() {
		final CommonDialog mDialog = new CommonDialog(this, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getResources().getString(R.string.special_yaku));
		mDialog.ok.setText(getResources().getString(R.string.ok));
		ListView listView = (ListView) mDialog.getContentView();
		final SpecialYakuCheckAdapter mAdapter = new SpecialYakuCheckAdapter(this);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAdapter.clickItem(position);
			}
		});
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mDialog.show();
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
	
	private void showMjSpectrumDialog(final Game17sResult result) {
		if (!result.hasYaku()) return;
		final CommonDialog mDialog = new CommonDialog(this, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getResources().getString(R.string.please_select));
		mDialog.ok.setText(getResources().getString(R.string.ok));
		ListView listView = (ListView) mDialog.getContentView();
		listView.setMotionEventSplittingEnabled(false);
		final MjSpectrumAdapter mAdapter = new MjSpectrumAdapter(this);
		mAdapter.setData(result);
		mAdapter.setDoraList(mj34Spectrum.getDoraList());
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mScoreIndex != position) {
					mScoreIndex = position;
					Message msg = new Message();
					msg.arg1 = SHOW_RESULT;
					msg.arg2 = result.getLevel();
					mHandler.sendMessage(msg);
				}
				mDialog.dismiss();
			}
		});
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mDialog.setItemCount(result.size(), GetSize.dip2px(mContext, 320));
		mDialog.show();
	}
	
	class MjSpectrumAdapter extends BaseAdapter {

		private Context mContext;
		private Game17sResult mGame17sResult;
		private int[] doraList;
		
		public MjSpectrumAdapter(Context context) {
			this.mContext = context;
		}
		
		public void setData(Game17sResult game17sResult) {
			this.mGame17sResult = game17sResult;
			notifyDataSetChanged();
		}
		
		public void setDoraList(int[] doras) {
			this.doraList = doras;
		}
		
		@Override
		public int getCount() {
			return mGame17sResult.hasYaku() ? mGame17sResult.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return mGame17sResult.hasYaku() ? mGame17sResult.getScore(position) : null;
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
	            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mjspactrum_simple, null);
	            holder.numText = (TextView) convertView.findViewById(R.id.item_mjss_num);
	            holder.fanText = (TextView) convertView.findViewById(R.id.item_mjss_fan);
	            holder.mjSpectrum = (MahjongSpectrum) convertView.findViewById(R.id.item_mjss_mjspectrum);
	            convertView.setTag(holder);
	        } else {
	        	holder = (ViewHolder) convertView.getTag();
	        }
			holder.numText.setText((position + 1) + "");
			holder.mjSpectrum.setData(mGame17sResult.getHandCards(position), null, 
					mGame17sResult.getAddedCards(position));
			holder.mjSpectrum.setMarkWinRect(doraList);
			Score score = mGame17sResult.getScore(position);
			if (score.FullYaku() > 0) {
				holder.fanText.setText("*" + score.FullYaku());
			} else {
				holder.fanText.setText(score.AllFanValue()+ "/" + score.Fu());
			}
			return convertView;
		}
		
		private class ViewHolder {
			TextView numText;
			TextView fanText;
			MahjongSpectrum mjSpectrum;
		}
		
	}
	
}

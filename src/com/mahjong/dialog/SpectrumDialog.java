package com.mahjong.dialog;

import java.util.List;

import com.mahjong.R;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;
import com.mahjong.common.MjDir;
import com.mahjong.common.MjPairType;
import com.mahjong.tools.KeyBoardUtil;
import com.mahjong.tools.ToastTool;
import com.mahjong.tools.KeyBoardUtil.OnKeyBoardListener;
import com.mahjong.ui.MahjongBaseView;
import com.mahjong.ui.MahjongPairs;
import com.mahjong.ui.MahjongSpectrum;
import com.mahjong.ui.RippleButton;
import com.mahjong.ui.MahjongBaseView.OnTouchEventListener;
import com.mahjong.ui.StereoView;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class SpectrumDialog extends Dialog 
		implements OnClickListener, OnCheckedChangeListener {

	private Context mContext;
	
	private StereoView mStereoView;
	// page mjsacrtrum
	private MahjongSpectrum mMjSpectrum;
	private ImageButton mMjBackBtn;
	private ImageButton mMjOkBtn;
	private RippleButton mMjAddBtn;
	private RippleButton mMjResetBtn;
	private CheckBox mLiZhiBox;
	private CheckBox mDoubleLiZhiBox;
	private CheckBox mYiFaBox;
	private CheckBox mZiMoBox;
	private CheckBox mFirstRoundBox;
	private CheckBox mFinalPickBox;
	private CheckBox mQiangGangBox;
	private CheckBox mLingShangBox;	
	// page pair setting
	private RippleButton mPairSetBackBtn;
	private RippleButton mPairSetNextBtn;
	private Button mPairSetTripletBtn;
	private Button mPairSetSequenceBtn;
	private Button mPairSetAdditionKongBtn;
	private Button mPairSetExposedKongBtn;
	private Button mPairSetConcealedKongBtn;
	// page pair select
	private MahjongPairs mMjPairs;
	private ImageView mLeftView;
	private ImageView mCenterView;
	private ImageView mRightView;
	private RippleButton mPairSelBackBtn;
	private RippleButton mPairSelNextBtn;	
	
	private KeyBoardUtil mKeyBoardUtil;
	private MahjongBaseView lastMahjongView;
	
	private OnSpectrumDialogListener mListener;
	
	private Handler mHandler = new Handler();
	
	private int index;
	private int mLizhiState = 0;
	
	public SpectrumDialog(Context context) {
		super(context, R.style.MyDialogStyle);
		setContentView(R.layout.dialog_mjspactrum_setting);
		this.mContext = context;
		initUI();
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mMjSpectrum.showStartItem();
				mKeyBoardUtil.show(mMjSpectrum);
				lastMahjongView = mMjSpectrum;
			}
		}, 500);
	}
	
	private void initUI() {
		mStereoView = (StereoView) findViewById(R.id.dialog_mjspactrum_stereoview);
		mStereoView.setCanTouchRoll(false);
		mMjSpectrum = (MahjongSpectrum) findViewById(R.id.dialog_page_mjspectrum);
		mMjSpectrum.setOnTouchEventListener(mTouchListener);
		mMjSpectrum.setTouchRectColor(mContext.getResources().getColor(R.color.main));
		mMjBackBtn = (ImageButton) findViewById(R.id.dialog_page_mj_back);
		mMjBackBtn.setOnClickListener(this);
		mMjOkBtn = (ImageButton) findViewById(R.id.dialog_page_mj_ok);
		mMjOkBtn.setOnClickListener(this);
		mMjAddBtn = (RippleButton) findViewById(R.id.dialog_page_mj_add);
		mMjAddBtn.setOnClickListener(this);
		mMjAddBtn.setVisibility(mMjSpectrum.canAddMjCardPairs() ? View.VISIBLE : View.GONE);
		mMjResetBtn = (RippleButton) findViewById(R.id.dialog_page_mj_reset);
		mMjResetBtn.setOnClickListener(this);
		mLiZhiBox = (CheckBox) findViewById(R.id.dialog_page_lizhi);
		mLiZhiBox.setOnCheckedChangeListener(this);
		mDoubleLiZhiBox = (CheckBox) findViewById(R.id.dialog_page_double_lizhi);
		mDoubleLiZhiBox.setOnCheckedChangeListener(this);
		mYiFaBox = (CheckBox) findViewById(R.id.dialog_page_yifa);
		mYiFaBox.setOnCheckedChangeListener(this);
		mZiMoBox = (CheckBox) findViewById(R.id.dialog_page_zimo);
//		mZiMoBox.setOnCheckedChangeListener(this);
		mZiMoBox.setClickable(false);
		mZiMoBox.setEnabled(false);
		mFirstRoundBox = (CheckBox) findViewById(R.id.dialog_page_first_round);
		mFirstRoundBox.setOnCheckedChangeListener(this);
		mFinalPickBox = (CheckBox) findViewById(R.id.dialog_page_final_pick);
		mFinalPickBox.setOnCheckedChangeListener(this);
		mQiangGangBox = (CheckBox) findViewById(R.id.dialog_page_qianggang);
		mQiangGangBox.setOnCheckedChangeListener(this);
		mLingShangBox = (CheckBox) findViewById(R.id.dialog_page_lingshangkaihua);
		mLingShangBox.setOnCheckedChangeListener(this);
		
		mPairSetBackBtn = (RippleButton) findViewById(R.id.dialog_page_pair_setting_back);
		mPairSetBackBtn.setOnClickListener(this);
		mPairSetNextBtn = (RippleButton) findViewById(R.id.dialog_page_pair_setting_next);
		mPairSetNextBtn.setOnClickListener(this);
		mPairSetTripletBtn = (Button) findViewById(R.id.dialog_page_pair_setting_triplet);
		mPairSetTripletBtn.setOnClickListener(this);
		mPairSetSequenceBtn = (Button) findViewById(R.id.dialog_page_pair_setting_sequence);
		mPairSetSequenceBtn.setOnClickListener(this);
		mPairSetAdditionKongBtn = (Button) findViewById(R.id.dialog_page_pair_setting_addition_kong);
		mPairSetAdditionKongBtn.setOnClickListener(this);
		mPairSetExposedKongBtn = (Button) findViewById(R.id.dialog_page_pair_setting_exposed_kong);
		mPairSetExposedKongBtn.setOnClickListener(this);
		mPairSetConcealedKongBtn = (Button) findViewById(R.id.dialog_page_pair_setting_concealed_kong);
		mPairSetConcealedKongBtn.setOnClickListener(this);
		
		mMjPairs = (MahjongPairs) findViewById(R.id.dialog_page_pair_select_pairs);
		mMjPairs.setOnTouchEventListener(mTouchListener);
		mMjPairs.setTouchRectColor(mContext.getResources().getColor(R.color.main));
		mLeftView = (ImageView) findViewById(R.id.dialog_page_pair_select_left);
		mLeftView.setOnClickListener(this);
		mCenterView = (ImageView) findViewById(R.id.dialog_page_pair_select_center);
		mCenterView.setOnClickListener(this);
		mRightView = (ImageView) findViewById(R.id.dialog_page_pair_select_right);
		mRightView.setOnClickListener(this);
		mPairSelBackBtn = (RippleButton) findViewById(R.id.dialog_page_pair_select_back);
		mPairSelBackBtn.setOnClickListener(this);
		mPairSelNextBtn = (RippleButton) findViewById(R.id.dialog_page_pair_select_ok);
		mPairSelNextBtn.setOnClickListener(this);
		
		mKeyBoardUtil = new KeyBoardUtil(mContext, 
				((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
		mKeyBoardUtil.setOnKeyBoardListener(mKeyBoardListener);
	}

	public void setData(int index, List<MjCard> darkCards,
			List<MjCardPairs> brightCardPairs, MjCard winCard) {
		this.index = index;
		mMjSpectrum.copy(darkCards, brightCardPairs, winCard);
		checkAddShow();
	}
	
	public void setEnvironment(int lizhiState, boolean yifa, boolean zimo,
			boolean firstround, boolean finalpick, boolean qianggang, boolean lingshang) {
		mLizhiState = lizhiState;
		if (mLizhiState == 1) mLiZhiBox.setChecked(true);
		if (mLizhiState == 2) mDoubleLiZhiBox.setChecked(true);
		mYiFaBox.setChecked(yifa);
		mZiMoBox.setChecked(zimo);
		mFirstRoundBox.setChecked(firstround);
		mFinalPickBox.setChecked(finalpick);
		mQiangGangBox.setChecked(qianggang);
		mLingShangBox.setChecked(lingshang);
	}
	
	private void checkAddShow() {
		mMjAddBtn.setVisibility(mMjSpectrum.canAddMjCardPairs() ? View.VISIBLE : View.GONE);
	}
	
	@Override
	public void show() {
		mStereoView.setStartScreen(1);
		super.show();
//		mStereoView.setItem(0);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		// page mjsacrtrum
		case R.id.dialog_page_mj_back:
			dismiss();
			break;
		case R.id.dialog_page_mj_ok:
			if (mListener != null) {
				mListener.onComplete(index, mMjSpectrum.getDarkNums(), 
						mMjSpectrum.getBrightNums(), mMjSpectrum.getWinNum(),
						mLiZhiBox.isChecked(), mDoubleLiZhiBox.isChecked(),
						mYiFaBox.isChecked(), mZiMoBox.isChecked(),
						mFirstRoundBox.isChecked(), mFinalPickBox.isChecked(),
						mQiangGangBox.isChecked(), mLingShangBox.isChecked());
			}
			dismiss();
			break;
		case R.id.dialog_page_mj_add:
			mStereoView.toNext();
			break;
		case R.id.dialog_page_mj_reset:
			mMjSpectrum.reset();
			checkAddShow();
			break;
		// page pair setting
		case R.id.dialog_page_pair_setting_back:
			mStereoView.toPre();
			break;
		case R.id.dialog_page_pair_setting_next:
			mStereoView.toNext();
			break;
		case R.id.dialog_page_pair_setting_triplet:
			mMjPairs.setPairsType(MjPairType.Triplet);
			mStereoView.toNext();
			break;
		case R.id.dialog_page_pair_setting_sequence:
			mMjPairs.setPairsType(MjPairType.Sequence);
			mStereoView.toNext();
			break;
		case R.id.dialog_page_pair_setting_addition_kong:
			mMjPairs.setPairsType(MjPairType.AdditionKong);
			mStereoView.toNext();
			break;
		case R.id.dialog_page_pair_setting_exposed_kong:
			mMjPairs.setPairsType(MjPairType.ExposedKong);
			mStereoView.toNext();
			break;
		case R.id.dialog_page_pair_setting_concealed_kong:
			mMjPairs.setPairsType(MjPairType.ConcealedKong);
			mStereoView.toNext();
			break;
		// page pair select
		case R.id.dialog_page_pair_select_left:
			mMjPairs.setDir(MjDir.left);
			break;
		case R.id.dialog_page_pair_select_center:
			mMjPairs.setDir(MjDir.center);
			break;
		case R.id.dialog_page_pair_select_right:
			mMjPairs.setDir(MjDir.right);
			break;
		case R.id.dialog_page_pair_select_back:
			mStereoView.toPre();
			break;
		case R.id.dialog_page_pair_select_ok:
			if (!mMjPairs.checkValid()) {
				ToastTool.showToast(mContext, R.string.input_invalid);
				return;
			}
			mStereoView.toNext();
			if (mMjSpectrum.addMjCardPairs(mMjPairs.createMjCardPairs())) {
				checkAddShow();
			} else {
				ToastTool.showToast(mContext, R.string.please_release_blank_card);
			}			
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.dialog_page_lizhi:
			if (mLizhiState > 0) {
				if (isChecked) {
					mLizhiState = 1;
					if (mDoubleLiZhiBox.isChecked()) {
						mDoubleLiZhiBox.setChecked(false);
					} 								
				} else {
					mLizhiState = 2;
					if (!mDoubleLiZhiBox.isChecked()) {
						mDoubleLiZhiBox.setChecked(true);
					}	
				}
			} else {
				mLiZhiBox.setChecked(false);
			}	
			break;
		case R.id.dialog_page_double_lizhi:
			if (mLizhiState > 0) {
				if (isChecked) {
					mLizhiState = 2;
					if (mLiZhiBox.isChecked()) {
						mLiZhiBox.setChecked(false);
					} 								
				} else {
					mLizhiState = 1;
					if (!mLiZhiBox.isChecked()) {
						mLiZhiBox.setChecked(true);
					}	
				}
			} else {
				mDoubleLiZhiBox.setChecked(false);
			}
			break;
		case R.id.dialog_page_zimo:
			break;
		case R.id.dialog_page_yifa:
			if (!mLiZhiBox.isChecked() && !mDoubleLiZhiBox.isChecked()) {
				mYiFaBox.setChecked(false);
			}
			break;
		case R.id.dialog_page_first_round:
			if (isChecked) {
				if (mFinalPickBox.isChecked()) mFinalPickBox.setChecked(false);
			}
			break;
		case R.id.dialog_page_final_pick:
			if (isChecked) {
				if (mFirstRoundBox.isChecked()) mFirstRoundBox.setChecked(false);
				if (mQiangGangBox.isChecked()) mQiangGangBox.setChecked(false);
				if (mLingShangBox.isChecked()) mLingShangBox.setChecked(false);
			}
			break;
		case R.id.dialog_page_qianggang:
			if (isChecked) {
				if (mFinalPickBox.isChecked()) mFinalPickBox.setChecked(false);
				if (mLingShangBox.isChecked()) mLingShangBox.setChecked(false);
			}
			break;
		case R.id.dialog_page_lingshangkaihua:
			if (isChecked) {
				if (mFinalPickBox.isChecked()) mFinalPickBox.setChecked(false);
				if (mQiangGangBox.isChecked()) mQiangGangBox.setChecked(false);
			}
			break;
		default:
			break;
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
			if (view != null) 
				if (view.addMjCard(key))
					checkAddShow();
		}
		
		@Override
		public void onBackSpace(MahjongBaseView view) {
			if (view != null) 
				if (view.removeTouchItem())
					checkAddShow();
		}
		
		@Override
		public void onLeft(MahjongBaseView view) {
			if (view != null) view.onPressLeft();
		}
		
		@Override
		public void onRight(MahjongBaseView view) {
			if (view != null) view.onPressRight();
		}
	};
	
	public void setOnSpectrumDialogListener(OnSpectrumDialogListener listener) {
		this.mListener = listener;
	}
	
	public interface OnSpectrumDialogListener {
		public void onComplete(int index, List<MjCard> darkCards, List<MjCardPairs> brightCardPairs, MjCard winCard,
				boolean lizhi, boolean doublelizhi, boolean yifa, boolean zimo,
				boolean firstround, boolean finalpick, boolean qianggang, boolean lingshang);
	}
	
	
}

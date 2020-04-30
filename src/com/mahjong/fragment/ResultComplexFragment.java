package com.mahjong.fragment;

import java.util.List;

import com.mahjong.R;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;
import com.mahjong.common.MjWind;
import com.mahjong.common.MjCalcTool.GameResult;
import com.mahjong.dialog.SpectrumDialog;
import com.mahjong.dialog.SpectrumDialog.OnSpectrumDialogListener;
import com.mahjong.tools.ManageTool;
import com.mahjong.ui.MahjongSpectrum;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ResultComplexFragment extends Fragment
		implements OnClickListener, OnCheckedChangeListener {

	private View mView;
	private Context mContext;

	private MahjongSpectrum mSpectrum;
	private CheckBox mLizhiBox;
	private CheckBox mWLizhiBox;
	private CheckBox mYifaBox;
	private CheckBox mZimoBox;
	private CheckBox mFirstRoundBox;
	private CheckBox mFinalPickBox;
	private CheckBox mQianggangBox;
	private CheckBox mLingshangBox;
	
	private SpectrumDialog mSpectrumDialog;
	private int mOrgIndex;
	private int mLizhiState = 0;
	private boolean isZimo = false;
	private int curPage;
	private int mAddedIndex;
	private GameResult mGameResult;
	
	private OnResultComplexListener mListener;
	
//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
//		super.onSaveInstanceState(savedInstanceState);
//		savedInstanceState.putInt("state", 1);
//		savedInstanceState.putInt("page", curPage);
//		savedInstanceState.putInt("index", mOrgIndex);
//		savedInstanceState.putBoolean("zimo", isZimo);
//		savedInstanceState.putInt("addedIndex", mAddedIndex);
//	}
//	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_result_set_spcetrum, container, false);
		mContext = getActivity();
//		if (savedInstanceState != null && savedInstanceState.getInt("state", 0) == 1) {
//			int page = savedInstanceState.getInt("page", 0);
//			int index = savedInstanceState.getInt("index", 0);
//			boolean zimo = savedInstanceState.getBoolean("zimo", false);
//			int addedIndex = savedInstanceState.getInt("addedIndex", 0);
//			setPlayerIndex(page, index, zimo, addedIndex);
//		}
		initUI();
		return mView;
	}

	private void initUI() {
		mSpectrum = (MahjongSpectrum) mView.findViewById(R.id.result_set_complex_mjspectrum);
		mLizhiBox = (CheckBox) mView.findViewById(R.id.result_set_complex_lizhi);
		mWLizhiBox = (CheckBox) mView.findViewById(R.id.result_set_complex_double_lizhi);
		mYifaBox = (CheckBox) mView.findViewById(R.id.result_set_complex_yifa);
		mZimoBox = (CheckBox) mView.findViewById(R.id.result_set_complex_zimo);
		mFirstRoundBox = (CheckBox) mView.findViewById(R.id.result_set_complex_first_round);
		mFinalPickBox = (CheckBox) mView.findViewById(R.id.result_set_complex_final_pick);
		mQianggangBox = (CheckBox) mView.findViewById(R.id.result_set_complex_qianggang);
		mLingshangBox = (CheckBox) mView.findViewById(R.id.result_set_complex_lingshangkaihua);
		
		mSpectrum.setOnClickListener(this);
		mLizhiBox.setOnCheckedChangeListener(this);
		mWLizhiBox.setOnCheckedChangeListener(this);
		mYifaBox.setOnCheckedChangeListener(this);
//		mZimoBox.setOnCheckedChangeListener(this);		
		mZimoBox.setClickable(false);
		mZimoBox.setEnabled(false);
		mFirstRoundBox.setOnCheckedChangeListener(this);
		mFinalPickBox.setOnCheckedChangeListener(this);
		mQianggangBox.setOnCheckedChangeListener(this);
		mLingshangBox.setOnCheckedChangeListener(this);
		
		if (mLizhiState > 0) {
			if (mLizhiState == 1) mLizhiBox.setChecked(true);
			else if (mLizhiState == 2) mWLizhiBox.setChecked(true);
		}
		mZimoBox.setChecked(isZimo);
		if (mGameResult != null) mGameResult.spectrum = mSpectrum;
//		System.out.println("initUI -> mGameResult" + (mGameResult != null ? "不为空" : "为空"));
	}

	public void setPlayerIndex(int page, int index, boolean zimo, int addedIndex) {
		curPage = page;
		mOrgIndex = index;
		mLizhiState = ManageTool.getInstance().getPlayerLizhi(mOrgIndex);
		isZimo = zimo;
		mAddedIndex = addedIndex;
		ManageTool tool = ManageTool.getInstance();
		MjWind groundWind = MjWind.None;
		switch (tool.getFengCount()) {
		case 0:
			groundWind = MjWind.East;
			break;
		case 1:
			groundWind = MjWind.South;
			break;
		case 2:
			groundWind = MjWind.West;
			break;
		case 3:
			groundWind = MjWind.North;
			break;
		default:
			break;
		}
		MjWind selfWind = MjWind.None;
		switch (tool.getPlayerWind(index)) {
		case 0:
			selfWind = MjWind.East;
			break;
		case 1:
			selfWind = MjWind.South;
			break;
		case 2:
			selfWind = MjWind.West;
			break;
		case 3:
			selfWind = MjWind.North;
			break;
		default:
			break;
		}
		MjWind addedWind = MjWind.None;
		if (!zimo) {
			switch (tool.getPlayerWind(addedIndex)) {
			case 0:
				addedWind = MjWind.East;
				break;
			case 1:
				addedWind = MjWind.South;
				break;
			case 2:
				addedWind = MjWind.West;
				break;
			case 3:
				addedWind = MjWind.North;
				break;
			default:
				break;
			}
		}
		mGameResult = new GameResult(groundWind, selfWind, addedWind);
		if (mSpectrum != null) mGameResult.spectrum = mSpectrum;
//		System.out.println("setPlayerIndex -> mSpectrum" + (mSpectrum != null ? "不为空" : "为空"));
//		System.out.println("setPlayerIndex -> mGameResult" + (mGameResult != null ? "不为空" : "为空"));
	}
	
	public GameResult getGameResult() {
		return mGameResult;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.result_set_complex_mjspectrum:
			showSpectrumDialog();
			break;
		default:
			break;
		}
	}
	
	private void showSpectrumDialog() {
		if (mSpectrumDialog == null) {
			mSpectrumDialog = new SpectrumDialog(mContext);
			Display display = getActivity().getWindowManager().getDefaultDisplay();
			LayoutParams params = mSpectrumDialog.getWindow().getAttributes();
			params.width = display.getWidth();
			mSpectrumDialog.getWindow().setAttributes(params);
			mSpectrumDialog.setOnSpectrumDialogListener(new OnSpectrumDialogListener() {
				
				@Override
				public void onComplete(int index, List<MjCard> darkCards,
						List<MjCardPairs> brightCardPairs, MjCard winCard
						, boolean lizhi,boolean doublelizhi, boolean yifa, boolean zimo,
						boolean firstround, boolean finalpick, boolean qianggang, boolean lingshang) {
					mSpectrum.copy(darkCards, brightCardPairs, winCard);
					mLizhiBox.setChecked(lizhi);
					mWLizhiBox.setChecked(doublelizhi);
					mYifaBox.setChecked(yifa);
					mZimoBox.setChecked(zimo);
					mFirstRoundBox.setChecked(firstround);
					mFinalPickBox.setChecked(finalpick);
					mQianggangBox.setChecked(qianggang);
					mLingshangBox.setChecked(lingshang);
					mGameResult.setData(lizhi, doublelizhi, zimo, yifa, 
							firstround, finalpick, qianggang, lingshang);
					if (mListener != null) {
						mListener.onResult(mGameResult, curPage, mOrgIndex);
					}
				}
			});
		}
		mSpectrumDialog.setData(mOrgIndex, mSpectrum.getDarkNums(), 
				mSpectrum.getBrightNums(), mSpectrum.getWinNum());
		mSpectrumDialog.setEnvironment(mLizhiState, mYifaBox.isChecked(), mZimoBox.isChecked(), 
				mFirstRoundBox.isChecked(), mFinalPickBox.isChecked(), 
				mQianggangBox.isChecked(), mLingshangBox.isChecked());
		mSpectrumDialog.show();
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.result_set_complex_lizhi:
			if (mLizhiState > 0) {
				if (isChecked) {
					mLizhiState = 1;
					if (mWLizhiBox.isChecked()) {
						mWLizhiBox.setChecked(false);
					} 								
				} else {
					mLizhiState = 2;
					if (!mWLizhiBox.isChecked()) {
						mWLizhiBox.setChecked(true);
					}	
				}
			} else {
				mLizhiBox.setChecked(false);
			}			
			mGameResult.isLiZhi = isChecked;
			break;
		case R.id.result_set_complex_double_lizhi:
			if (mLizhiState > 0) {
				if (isChecked) {
					mLizhiState = 2;
					if (mLizhiBox.isChecked()) {
						mLizhiBox.setChecked(false);
					} 								
				} else {
					mLizhiState = 1;
					if (!mLizhiBox.isChecked()) {
						mLizhiBox.setChecked(true);
					}	
				}
			} else {
				mWLizhiBox.setChecked(false);
			}			
			mGameResult.isDoubleLiZhi = isChecked;
			break;
		case R.id.result_set_complex_zimo:			
			mGameResult.isZiMo = isChecked;
			break;
		case R.id.result_set_complex_yifa:
			if (!mLizhiBox.isChecked() && !mWLizhiBox.isChecked()) {
				mYifaBox.setChecked(false);
			}			
			mGameResult.isYiFa = isChecked;
			break;
		case R.id.result_set_complex_first_round:
			if (isChecked) {
				if (mFinalPickBox.isChecked()) mFinalPickBox.setChecked(false);
			}			
			mGameResult.isFirstRound = isChecked;
			break;
		case R.id.result_set_complex_final_pick:
			if (isChecked) {
				if (mFirstRoundBox.isChecked()) mFirstRoundBox.setChecked(false);
				if (mQianggangBox.isChecked()) mQianggangBox.setChecked(false);
				if (mLingshangBox.isChecked()) mLingshangBox.setChecked(false);
			}			
			mGameResult.isFinalPick = isChecked;
			break;
		case R.id.result_set_complex_qianggang:
			if (isChecked) {
				if (mFinalPickBox.isChecked()) mFinalPickBox.setChecked(false);
				if (mLingshangBox.isChecked()) mLingshangBox.setChecked(false);
			}			
			mGameResult.isQiangGang = isChecked;
			break;
		case R.id.result_set_complex_lingshangkaihua:
			if (isChecked) {
				if (mFinalPickBox.isChecked()) mFinalPickBox.setChecked(false);
				if (mQianggangBox.isChecked()) mQianggangBox.setChecked(false);
			}			
			mGameResult.isLingshang = isChecked;
			break;
		default:
			break;
		}		
//		mGameResult.reCalcEnv();
	}

	public void setOnResultComplexListener(OnResultComplexListener listener) {
		this.mListener = listener;
	}
	
	public interface OnResultComplexListener {
		void onResult(GameResult gameResult, int page, int index);
		void onEnvironmentChange(GameResult gameResult, int page, int index);
	}
	
}

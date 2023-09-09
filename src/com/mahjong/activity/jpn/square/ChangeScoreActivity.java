package com.mahjong.activity.jpn.square;

import com.mahjong.R;
import com.mahjong.activity.BaseActivity;
import com.mahjong.activity.jpn.GameSimpleActivity;
import com.mahjong.activity.jpn.ResultShow;
import com.mahjong.activity.jpn.ResultShowForLand;
import com.mahjong.common.MjSetting;
import com.mahjong.control.BaseManager;
import com.mahjong.control.ManagerTool;
import com.mahjong.model.MjAction;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.RotateTextView2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ChangeScoreActivity extends BaseActivity implements OnClickListener {
	
	public static final String ChangeScores = "ChangeScores";
	
	private Context mContext;
	
	private int mMainVision; // 主视觉
	private int[] mIndexs; // 四个方位玩家对应的初始索引
	private int mSumScore = 0;
	private int[] mScores = {0, 0, 0, 0};
	private int[] mBeiShus = {100, 100, 100, 100};
	
	private TextView mResetBtn;
	private TextView mOkBtn;
	private TextView mCancelBtn;
	private TextView mSumText;
	
	private RotateTextView2[] mNameViews;
	private RotateTextView2[] mScoreViews;
	private RotateTextView2[] mMinusBtns;
	private RotateTextView2[] mPlusBtns;
	private RotateTextView2[] mBeiShuTexts;
	private RotateTextView2[] mTextMul10Btns;
	private RotateTextView2[] mTextDiv10Btns;
	
	private BaseManager mTool = ManagerTool.getInstance().getManager();
	
	private boolean landscapeMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		landscapeMode = ShareprefenceTool.getInstance().getBoolean(MjSetting.LANDSCAPE_MODE, this, false);
		setContentView(R.layout.activity_square_jpn_changescore);
		mContext = this;
		mMainVision = getIntent().getIntExtra(GameSimpleActivity.MAIN_VISION, 0);
		initUI();
	}
	
	private void initUI() {
		mResetBtn = (TextView) findViewById(R.id.changescore_square_btn_reset);
		mOkBtn = (TextView) findViewById(R.id.changescore_square_btn_ok);
		mCancelBtn = (TextView) findViewById(R.id.changescore_square_btn_cancel);
		mSumText = (TextView) findViewById(R.id.changescore_square_tv_sum);
		mNameViews = new RotateTextView2[4];
		mNameViews[0] = (RotateTextView2) findViewById(R.id.changescore_square_bottom_name);
		mNameViews[1] = (RotateTextView2) findViewById(R.id.changescore_square_right_name);
		mNameViews[2] = (RotateTextView2) findViewById(R.id.changescore_square_top_name);
		mNameViews[3] = (RotateTextView2) findViewById(R.id.changescore_square_left_name);
		mScoreViews = new RotateTextView2[4];
		mScoreViews[0] = (RotateTextView2) findViewById(R.id.changescore_square_bottom_score);
		mScoreViews[1] = (RotateTextView2) findViewById(R.id.changescore_square_right_score);
		mScoreViews[2] = (RotateTextView2) findViewById(R.id.changescore_square_top_score);
		mScoreViews[3] = (RotateTextView2) findViewById(R.id.changescore_square_left_score);
		mMinusBtns = new RotateTextView2[4];
		mMinusBtns[0] = (RotateTextView2) findViewById(R.id.changescore_square_bottom_minus);
		mMinusBtns[1] = (RotateTextView2) findViewById(R.id.changescore_square_right_minus);
		mMinusBtns[2] = (RotateTextView2) findViewById(R.id.changescore_square_top_minus);
		mMinusBtns[3] = (RotateTextView2) findViewById(R.id.changescore_square_left_minus);
		mPlusBtns = new RotateTextView2[4];
		mPlusBtns[0] = (RotateTextView2) findViewById(R.id.changescore_square_bottom_plus);
		mPlusBtns[1] = (RotateTextView2) findViewById(R.id.changescore_square_right_plus);
		mPlusBtns[2] = (RotateTextView2) findViewById(R.id.changescore_square_top_plus);
		mPlusBtns[3] = (RotateTextView2) findViewById(R.id.changescore_square_left_plus);
		mBeiShuTexts = new RotateTextView2[4];
		mBeiShuTexts[0] = (RotateTextView2) findViewById(R.id.changescore_square_bottom_num);
		mBeiShuTexts[1] = (RotateTextView2) findViewById(R.id.changescore_square_right_num);
		mBeiShuTexts[2] = (RotateTextView2) findViewById(R.id.changescore_square_top_num);
		mBeiShuTexts[3] = (RotateTextView2) findViewById(R.id.changescore_square_left_num);
		mTextMul10Btns = new RotateTextView2[4];
		mTextMul10Btns[0] = (RotateTextView2) findViewById(R.id.changescore_square_bottom_multiply10);
		mTextMul10Btns[1] = (RotateTextView2) findViewById(R.id.changescore_square_right_multiply10);
		mTextMul10Btns[2] = (RotateTextView2) findViewById(R.id.changescore_square_top_multiply10);
		mTextMul10Btns[3] = (RotateTextView2) findViewById(R.id.changescore_square_left_multiply10);
		mTextDiv10Btns = new RotateTextView2[4];
		mTextDiv10Btns[0] = (RotateTextView2) findViewById(R.id.changescore_square_bottom_division10);
		mTextDiv10Btns[1] = (RotateTextView2) findViewById(R.id.changescore_square_right_division10);
		mTextDiv10Btns[2] = (RotateTextView2) findViewById(R.id.changescore_square_top_division10);
		mTextDiv10Btns[3] = (RotateTextView2) findViewById(R.id.changescore_square_left_division10);
		
		mIndexs = new int[4];
		for (int i = 0; i < 4; i++) {
			mIndexs[i] = mTool.getPlayerIndexByPosition(mMainVision, i);
			mNameViews[i].setText(mTool.getPlayer(mIndexs[i]).getNickName());
			mMinusBtns[i].setOnClickListener(this);
			mPlusBtns[i].setOnClickListener(this);
			mTextMul10Btns[i].setOnClickListener(this);
			mTextDiv10Btns[i].setOnClickListener(this);
		}
		
		mResetBtn.setOnClickListener(this);
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		
		checkIsShowItem(ManagerTool.getInstance().getManager().getMemberCount());
	}
	
	/**
	 * 根据人数确定是否显示控件
	 * 
	 * @param memberCount
	 */
	private void checkIsShowItem(int memberCount) {
		int orgIndex = mMainVision;
		for (int i = 0; i < 4; i++) {
			if ((orgIndex == 1 && memberCount < 3)
					|| (orgIndex == 3 && memberCount < 4)) {
				mNameViews[i].setVisibility(View.INVISIBLE);
				mScoreViews[i].setVisibility(View.INVISIBLE);
				mPlusBtns[i].setVisibility(View.INVISIBLE);
				mMinusBtns[i].setVisibility(View.INVISIBLE);
				mTextMul10Btns[i].setVisibility(View.INVISIBLE);
				mTextDiv10Btns[i].setVisibility(View.INVISIBLE);
				mBeiShuTexts[i].setVisibility(View.INVISIBLE);
			} 
			orgIndex = (orgIndex + 1) % 4;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.changescore_square_btn_reset:
			resetAll();
			break;
		case R.id.changescore_square_btn_ok:
			if (mSumScore != 0) {
				ToastTool.showToast(mContext, R.string.total_not_zore);
				return;
			}
			Intent intent;
			if (landscapeMode) {
				intent = new Intent(ChangeScoreActivity.this, ResultShowForLand.class);
			} else {
				intent = new Intent(ChangeScoreActivity.this, ResultShow.class);
			}
			intent.putExtra(MjAction.Name, MjAction.ACTION_CHANGESCORE);
			intent.putExtra(GameSimpleActivity.MAIN_VISION, mMainVision);
			intent.putExtra(ChangeScores, mScores);
			startActivity(intent);
			finish();
			break;
		case R.id.changescore_square_btn_cancel:
			finish();
			break;
		case R.id.changescore_square_bottom_plus:
			changeScoreText(0, true);
			break;
		case R.id.changescore_square_right_plus:
			changeScoreText(1, true);
			break;
		case R.id.changescore_square_top_plus:
			changeScoreText(2, true);
			break;
		case R.id.changescore_square_left_plus:
			changeScoreText(3, true);
			break;
		case R.id.changescore_square_bottom_minus:
			changeScoreText(0, false);
			break;
		case R.id.changescore_square_right_minus:
			changeScoreText(1, false);
			break;
		case R.id.changescore_square_top_minus:
			changeScoreText(2, false);
			break;
		case R.id.changescore_square_left_minus:
			changeScoreText(3, false);
			break;
		case R.id.changescore_square_bottom_multiply10:
			changeBeiShuText(0, true);
			break;
		case R.id.changescore_square_right_multiply10:
			changeBeiShuText(1, true);
			break;
		case R.id.changescore_square_top_multiply10:
			changeBeiShuText(2, true);
			break;
		case R.id.changescore_square_left_multiply10:
			changeBeiShuText(3, true);
			break;
		case R.id.changescore_square_bottom_division10:
			changeBeiShuText(0, false);
			break;
		case R.id.changescore_square_right_division10:
			changeBeiShuText(1, false);
			break;
		case R.id.changescore_square_top_division10:
			changeBeiShuText(2, false);
			break;
		case R.id.changescore_square_left_division10:
			changeBeiShuText(3, false);
			break;
		default:
			break;
		}
	}
	
	private boolean changeScoreText(int pos, boolean isPlus) {
		if (isPlus) {
			mScores[pos] += mBeiShus[pos];
		} else {
			mScores[pos] -= mBeiShus[pos];
		}
		mScoreViews[pos].setText(mScores[pos]+ "");
		mSumScore = mScores[0] + mScores[1] + mScores[2] + mScores[3];
		mSumText.setText(mSumScore + "");
		return true;
	}
	
	private boolean changeBeiShuText(int pos, boolean isMultiply) {
		if (isMultiply) {
			if (mBeiShus[pos] >= 100000) {
				return false;
			}
			mBeiShus[pos] *= 10;
		} else {
			if (mBeiShus[pos] <= 100) {
				return false;
			}
			mBeiShus[pos] /= 10;
		}
		mBeiShuTexts[pos].setText("±" + mBeiShus[pos]);
		return true;
	}
	
	private void resetAll() {
		mSumScore = 0;
		mSumText.setText("0");
		for (int i = 0; i < 4; i++) {
			mScores[i] = 0;
			mScoreViews[i].setText("0");
			mBeiShus[i] = 100;
			mBeiShuTexts[i].setText("±100");
		}
	}
	
}

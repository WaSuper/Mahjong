package com.mahjong.activity.jpn;

import com.mahjong.R;
import com.mahjong.activity.BaseActivity;
import com.mahjong.common.MjSetting;
import com.mahjong.control.BaseManager;
import com.mahjong.control.ManagerTool;
import com.mahjong.model.MjAction;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.RotateButton;
import com.mahjong.ui.RotateTextView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ChangeScoreActivity extends BaseActivity implements OnClickListener {

	public static final String ChangeScores = "ChangeScores";
	
	private Context mContext;
	
	private int mMainVision; // 主视觉
	private int[] mIndexs; // 四个方位玩家对应的初始索引
	private int mSumScore = 0;
	private int[] mScores = {0, 0, 0, 0};
	
	private Button mResetBtn;
	private Button mOkBtn;
	private Button mCancelBtn;
	private TextView mSumText;
	private RotateTextView[] mNameViews;
	private RotateTextView[] mTagViews;
	private RotateButton[] mBottomMinusBtns;
	private RotateButton[] mBottomPlusBtns;
	private RotateTextView[] mBottomTexts;
	private RotateButton[] mRightMinusBtns;
	private RotateButton[] mRightPlusBtns;
	private RotateTextView[] mRightTexts;
	private RotateButton[] mTopMinusBtns;
	private RotateButton[] mTopPlusBtns;
	private RotateTextView[] mTopTexts;
	private RotateButton[] mLeftMinusBtns;
	private RotateButton[] mLeftPlusBtns;
	private RotateTextView[] mLeftTexts;
	private RotateTextView[] mTextx100Views;
	
	private BaseManager mTool = ManagerTool.getInstance().getManager();
	
	private boolean landscapeMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		landscapeMode = ShareprefenceTool.getInstance().getBoolean(MjSetting.LANDSCAPE_MODE, this, false);
		setContentView(R.layout.activity_jpn_changescore);
		mContext = this;
		mMainVision = getIntent().getIntExtra(GameSimpleActivity.MAIN_VISION, 0);
		initUI();
	}
	
	private void initUI() {
		mResetBtn = (Button) findViewById(R.id.changescore_btn_reset);
		mOkBtn = (Button) findViewById(R.id.changescore_btn_ok);
		mCancelBtn = (Button) findViewById(R.id.changescore_btn_cancel);
		mSumText = (TextView) findViewById(R.id.changescore_tv_sum);
		mNameViews = new RotateTextView[4];
		mNameViews[0] = (RotateTextView) findViewById(R.id.changescore_bottom_name);
		mNameViews[1] = (RotateTextView) findViewById(R.id.changescore_right_name);
		mNameViews[2] = (RotateTextView) findViewById(R.id.changescore_top_name);
		mNameViews[3] = (RotateTextView) findViewById(R.id.changescore_left_name);
		mTagViews = new RotateTextView[4];
		mTagViews[0] = (RotateTextView) findViewById(R.id.changescore_bottom_tv_tag);
		mTagViews[1] = (RotateTextView) findViewById(R.id.changescore_right_tv_tag);
		mTagViews[2] = (RotateTextView) findViewById(R.id.changescore_top_tv_tag);
		mTagViews[3] = (RotateTextView) findViewById(R.id.changescore_left_tv_tag);
		mBottomMinusBtns = new RotateButton[4];
		mBottomMinusBtns[0] = (RotateButton) findViewById(R.id.changescore_bottom_minus_1);
		mBottomMinusBtns[1] = (RotateButton) findViewById(R.id.changescore_bottom_minus_10);
		mBottomMinusBtns[2] = (RotateButton) findViewById(R.id.changescore_bottom_minus_100);
		mBottomMinusBtns[3] = (RotateButton) findViewById(R.id.changescore_bottom_minus_1000);
		mBottomPlusBtns = new RotateButton[4];
		mBottomPlusBtns[0] = (RotateButton) findViewById(R.id.changescore_bottom_plus_1);
		mBottomPlusBtns[1] = (RotateButton) findViewById(R.id.changescore_bottom_plus_10);
		mBottomPlusBtns[2] = (RotateButton) findViewById(R.id.changescore_bottom_plus_100);
		mBottomPlusBtns[3] = (RotateButton) findViewById(R.id.changescore_bottom_plus_1000);
		mBottomTexts = new RotateTextView[4];
		mBottomTexts[0] = (RotateTextView) findViewById(R.id.changescore_bottom_tv_1);
		mBottomTexts[1] = (RotateTextView) findViewById(R.id.changescore_bottom_tv_10);
		mBottomTexts[2] = (RotateTextView) findViewById(R.id.changescore_bottom_tv_100);
		mBottomTexts[3] = (RotateTextView) findViewById(R.id.changescore_bottom_tv_1000);
		mRightMinusBtns = new RotateButton[4];
		mRightMinusBtns[0] = (RotateButton) findViewById(R.id.changescore_right_minus_1);
		mRightMinusBtns[1] = (RotateButton) findViewById(R.id.changescore_right_minus_10);
		mRightMinusBtns[2] = (RotateButton) findViewById(R.id.changescore_right_minus_100);
		mRightMinusBtns[3] = (RotateButton) findViewById(R.id.changescore_right_minus_1000);
		mRightPlusBtns = new RotateButton[4];
		mRightPlusBtns[0] = (RotateButton) findViewById(R.id.changescore_right_plus_1);
		mRightPlusBtns[1] = (RotateButton) findViewById(R.id.changescore_right_plus_10);
		mRightPlusBtns[2] = (RotateButton) findViewById(R.id.changescore_right_plus_100);
		mRightPlusBtns[3] = (RotateButton) findViewById(R.id.changescore_right_plus_1000);
		mRightTexts = new RotateTextView[4];
		mRightTexts[0] = (RotateTextView) findViewById(R.id.changescore_right_tv_1);
		mRightTexts[1] = (RotateTextView) findViewById(R.id.changescore_right_tv_10);
		mRightTexts[2] = (RotateTextView) findViewById(R.id.changescore_right_tv_100);
		mRightTexts[3] = (RotateTextView) findViewById(R.id.changescore_right_tv_1000);
		mTopMinusBtns = new RotateButton[4];
		mTopMinusBtns[0] = (RotateButton) findViewById(R.id.changescore_top_minus_1);
		mTopMinusBtns[1] = (RotateButton) findViewById(R.id.changescore_top_minus_10);
		mTopMinusBtns[2] = (RotateButton) findViewById(R.id.changescore_top_minus_100);
		mTopMinusBtns[3] = (RotateButton) findViewById(R.id.changescore_top_minus_1000);
		mTopPlusBtns = new RotateButton[4];
		mTopPlusBtns[0] = (RotateButton) findViewById(R.id.changescore_top_plus_1);
		mTopPlusBtns[1] = (RotateButton) findViewById(R.id.changescore_top_plus_10);
		mTopPlusBtns[2] = (RotateButton) findViewById(R.id.changescore_top_plus_100);
		mTopPlusBtns[3] = (RotateButton) findViewById(R.id.changescore_top_plus_1000);
		mTopTexts = new RotateTextView[4];
		mTopTexts[0] = (RotateTextView) findViewById(R.id.changescore_top_tv_1);
		mTopTexts[1] = (RotateTextView) findViewById(R.id.changescore_top_tv_10);
		mTopTexts[2] = (RotateTextView) findViewById(R.id.changescore_top_tv_100);
		mTopTexts[3] = (RotateTextView) findViewById(R.id.changescore_top_tv_1000);
		mLeftMinusBtns = new RotateButton[4];
		mLeftMinusBtns[0] = (RotateButton) findViewById(R.id.changescore_left_minus_1);
		mLeftMinusBtns[1] = (RotateButton) findViewById(R.id.changescore_left_minus_10);
		mLeftMinusBtns[2] = (RotateButton) findViewById(R.id.changescore_left_minus_100);
		mLeftMinusBtns[3] = (RotateButton) findViewById(R.id.changescore_left_minus_1000);
		mLeftPlusBtns = new RotateButton[4];
		mLeftPlusBtns[0] = (RotateButton) findViewById(R.id.changescore_left_plus_1);
		mLeftPlusBtns[1] = (RotateButton) findViewById(R.id.changescore_left_plus_10);
		mLeftPlusBtns[2] = (RotateButton) findViewById(R.id.changescore_left_plus_100);
		mLeftPlusBtns[3] = (RotateButton) findViewById(R.id.changescore_left_plus_1000);
		mLeftTexts = new RotateTextView[4];
		mLeftTexts[0] = (RotateTextView) findViewById(R.id.changescore_left_tv_1);
		mLeftTexts[1] = (RotateTextView) findViewById(R.id.changescore_left_tv_10);
		mLeftTexts[2] = (RotateTextView) findViewById(R.id.changescore_left_tv_100);
		mLeftTexts[3] = (RotateTextView) findViewById(R.id.changescore_left_tv_1000);
		mTextx100Views = new RotateTextView[4];
		mTextx100Views[0] = (RotateTextView) findViewById(R.id.changescore_bottom_tv_x100);
		mTextx100Views[1] = (RotateTextView) findViewById(R.id.changescore_right_tv_x100);
		mTextx100Views[2] = (RotateTextView) findViewById(R.id.changescore_top_tv_x100);
		mTextx100Views[3] = (RotateTextView) findViewById(R.id.changescore_left_tv_x100);
		
		mIndexs = new int[4];
		for (int i = 0; i < 4; i++) {
			mIndexs[i] = mTool.getPlayerIndexByPosition(mMainVision, i);
			mNameViews[i].setText(mTool.getPlayer(mIndexs[i]).getNickName());
			mBottomMinusBtns[i].setOnClickListener(this);
			mBottomPlusBtns[i].setOnClickListener(this);
			mRightMinusBtns[i].setOnClickListener(this);
			mRightPlusBtns[i].setOnClickListener(this);
			mTopMinusBtns[i].setOnClickListener(this);
			mTopPlusBtns[i].setOnClickListener(this);
			mLeftMinusBtns[i].setOnClickListener(this);
			mLeftPlusBtns[i].setOnClickListener(this);
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
				mTagViews[i].setVisibility(View.INVISIBLE);
				mNameViews[i].setVisibility(View.INVISIBLE);
				mTextx100Views[i].setVisibility(View.INVISIBLE);
				hideItem(i);
			} 
			orgIndex = (orgIndex + 1) % 4;
		}
	}
	
	private void hideItem(int index) {
		switch (index) {
		case 0:
			for (int i = 0; i < 4; i++) {
				mBottomTexts[i].setVisibility(View.INVISIBLE);
				mBottomPlusBtns[i].setVisibility(View.INVISIBLE);
				mBottomMinusBtns[i].setVisibility(View.INVISIBLE);
			}
			break;
		case 1:
			for (int i = 0; i < 4; i++) {
				mRightTexts[i].setVisibility(View.INVISIBLE);
				mRightPlusBtns[i].setVisibility(View.INVISIBLE);
				mRightMinusBtns[i].setVisibility(View.INVISIBLE);
			}
			break;
		case 2:
			for (int i = 0; i < 4; i++) {
				mTopTexts[i].setVisibility(View.INVISIBLE);
				mTopPlusBtns[i].setVisibility(View.INVISIBLE);
				mTopMinusBtns[i].setVisibility(View.INVISIBLE);
			}
			break;
		case 3:
			for (int i = 0; i < 4; i++) {
				mLeftTexts[i].setVisibility(View.INVISIBLE);
				mLeftPlusBtns[i].setVisibility(View.INVISIBLE);
				mLeftMinusBtns[i].setVisibility(View.INVISIBLE);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.changescore_btn_reset:
			resetAll();
			break;
		case R.id.changescore_btn_ok:
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
			intent.putExtra(ChangeScores, 
					new int[] {mScores[0] * 100, mScores[1] * 100, mScores[2] * 100, mScores[3] * 100});
			startActivity(intent);
			finish();
			break;
		case R.id.changescore_btn_cancel:
			finish();
			break;
		case R.id.changescore_bottom_minus_1:
			changeByClick(0, -1, mBottomTexts);
			break;
		case R.id.changescore_bottom_minus_10:
			changeByClick(0, -10, mBottomTexts);
			break;
		case R.id.changescore_bottom_minus_100:
			changeByClick(0, -100, mBottomTexts);
			break;
		case R.id.changescore_bottom_minus_1000:
			changeByClick(0, -1000, mBottomTexts);
			break;
		case R.id.changescore_bottom_plus_1:
			changeByClick(0, 1, mBottomTexts);
			break;
		case R.id.changescore_bottom_plus_10:
			changeByClick(0, 10, mBottomTexts);
			break;
		case R.id.changescore_bottom_plus_100:
			changeByClick(0, 100, mBottomTexts);
			break;
		case R.id.changescore_bottom_plus_1000:
			changeByClick(0, 1000, mBottomTexts);
			break;
		case R.id.changescore_right_minus_1:
			changeByClick(1, -1, mRightTexts);
			break;
		case R.id.changescore_right_minus_10:
			changeByClick(1, -10, mRightTexts);
			break;
		case R.id.changescore_right_minus_100:
			changeByClick(1, -100, mRightTexts);
			break;
		case R.id.changescore_right_minus_1000:
			changeByClick(1, -1000, mRightTexts);
			break;
		case R.id.changescore_right_plus_1:
			changeByClick(1, 1, mRightTexts);
			break;
		case R.id.changescore_right_plus_10:
			changeByClick(1, 10, mRightTexts);
			break;
		case R.id.changescore_right_plus_100:
			changeByClick(1, 100, mRightTexts);
			break;
		case R.id.changescore_right_plus_1000:
			changeByClick(1, 1000, mRightTexts);
			break;
		case R.id.changescore_top_minus_1:
			changeByClick(2, -1, mTopTexts);
			break;
		case R.id.changescore_top_minus_10:
			changeByClick(2, -10, mTopTexts);
			break;
		case R.id.changescore_top_minus_100:
			changeByClick(2, -100, mTopTexts);
			break;
		case R.id.changescore_top_minus_1000:
			changeByClick(2, -1000, mTopTexts);
			break;
		case R.id.changescore_top_plus_1:
			changeByClick(2, 1, mTopTexts);
			break;
		case R.id.changescore_top_plus_10:
			changeByClick(2, 10, mTopTexts);
			break;
		case R.id.changescore_top_plus_100:
			changeByClick(2, 100, mTopTexts);
			break;
		case R.id.changescore_top_plus_1000:
			changeByClick(2, 1000, mTopTexts);
			break;
		case R.id.changescore_left_minus_1:
			changeByClick(3, -1, mLeftTexts);
			break;
		case R.id.changescore_left_minus_10:
			changeByClick(3, -10, mLeftTexts);
			break;
		case R.id.changescore_left_minus_100:
			changeByClick(3, -100, mLeftTexts);
			break;
		case R.id.changescore_left_minus_1000:
			changeByClick(3, -1000, mLeftTexts);
			break;
		case R.id.changescore_left_plus_1:
			changeByClick(3, 1, mLeftTexts);
			break;
		case R.id.changescore_left_plus_10:
			changeByClick(3, 10, mLeftTexts);
			break;
		case R.id.changescore_left_plus_100:
			changeByClick(3, 100, mLeftTexts);
			break;
		case R.id.changescore_left_plus_1000:
			changeByClick(3, 1000, mLeftTexts);
			break;
		default:
			break;
		}
	}
	
	private boolean changeByClick(int pos, int multiple, RotateTextView[] mTexts) {
		int count = mScores[pos];
		count += multiple;
		if (count > 9999 || count < -9999) {
			return false;
		}
		mScores[pos] = count;
		if (count >= 0) {
			mTagViews[pos].setText("+");
		} else {
			mTagViews[pos].setText("-");
			count = -count;
		}
		int num1000 = count / 1000;
		count = count % 1000;
		int num100 = count / 100;
		count = count % 100;
		int num10 = count / 10;
		int num1 = count % 10;
		mTexts[0].setText(num1 + "");
		mTexts[1].setText(num10 + "");
		mTexts[2].setText(num100 + "");
		mTexts[3].setText(num1000 + "");
		
		mSumScore = (mScores[0] + mScores[1] + mScores[2] + mScores[3]) * 100;
		mSumText.setText(mSumScore + "");
		
		return true;
	}
	
	private void resetAll() {
		mSumScore = 0;
		for (int i = 0; i < 4; i++) {
			mScores[i] = 0;
			mBottomTexts[i].setText("0");
			mRightTexts[i].setText("0");
			mTopTexts[i].setText("0");
			mLeftTexts[i].setText("0");
			mTagViews[i].setText("+");
		}
		mSumText.setText("0");
	}
	
}

package com.mahjong.activity.jpn;

import com.mahjong.R;
import com.mahjong.dialog.FanfuDialog;
import com.mahjong.dialog.FanfuDialog.OnFanfuListener;
import com.mahjong.model.MjAction;
import com.mahjong.tools.ManageTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.PlayerFuncItem;
import com.mahjong.ui.ext.TextPicker;
import com.mahjong.ui.ext.TextPicker.OnTextSelectedListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

public class ResultSetBombSimpleActivity extends Activity implements OnClickListener {

	private Context mContext;
	
	private CheckBox[] mPosBoxs = new CheckBox[3]; // 玩家选择，0：上家，2：对家，2：下家
	private LinearLayout[] mPosLayouts = new LinearLayout[3]; // 对应选择玩家的布局
	private Button[] mFanBtns = new Button[3]; // 玩家对应的番数
	private Button[] mFuBtns = new Button[3]; // 玩家对应的符数
	private Button mOkBtn;
	private Button mCancelBtn;
	
	private String[] mFanTexts = {"1番", "2番", "3番", "4番", "5番", "6番",
			 					  "7番", "8番", "9番", "10番", "11番", "12番", 
								  "役满", "2倍役满", "3倍役满", "4倍役满", "5倍役满", "6倍役满",};
	private String[] mFan1Texts = {"30符", "40符", "50符", "60符","70符", 
			"80符", "90符", "100符", "110符"};
	private String[] mFan2_4Texts = {"25符", "30符", "40符", "50符", "60符",
			"70符", "80符", "90符", "100符", "110符"};
	private String[] mFan5Texts = {"--"};
	
	private int[] mFanIndex = {0, 0, 0};
	private int[] mFuIndex = {0, 0, 0};
	private int mOrgPlayer; // 当前玩家初始索引
	private int mMainVision; // 主视觉
	private int mHuCount = 0; // 胡牌人数
	private int mCheckBoxIndex = 0; // 当前查询位置
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		// 设定方向
		Intent intent = getIntent();
		int pos = intent.getIntExtra(ManageTool.PLAYER_ITEM_POSITION, PlayerFuncItem.POS_BOTTOM);
		switch (pos) {
		case PlayerFuncItem.POS_LEFT:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case PlayerFuncItem.POS_RIGHT:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			break;
		case PlayerFuncItem.POS_TOP:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
			break;
		case PlayerFuncItem.POS_BOTTOM:
		default:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}
		setContentView(R.layout.activity_jpn_result_set_bomb_simple);
		mContext = this;
		mOrgPlayer = intent.getIntExtra(ManageTool.PLAYER_ORIGINAL_INDEX, 0);
		mMainVision = intent.getIntExtra(GameSimpleActivity.MAIN_VISION, 0);
		initUI();
	}
	
	private void initUI() {
		mPosBoxs[0] = (CheckBox) findViewById(R.id.result_set_bomb_simple_check_upper);
		mPosBoxs[1] = (CheckBox) findViewById(R.id.result_set_bomb_simple_check_oppositer);
		mPosBoxs[2] = (CheckBox) findViewById(R.id.result_set_bomb_simple_check_downer);
		mPosLayouts[0] = (LinearLayout) findViewById(R.id.result_set_bomb_simple_ll_upper);
		mPosLayouts[1] = (LinearLayout) findViewById(R.id.result_set_bomb_simple_ll_oppositer);
		mPosLayouts[2] = (LinearLayout) findViewById(R.id.result_set_bomb_simple_ll_downer);
		mFanBtns[0] = (Button) findViewById(R.id.result_set_bomb_simple_upper_fan);
		mFanBtns[1] = (Button) findViewById(R.id.result_set_bomb_simple_oppositer_fan);
		mFanBtns[2] = (Button) findViewById(R.id.result_set_bomb_simple_downer_fan);
		mFuBtns[0] = (Button) findViewById(R.id.result_set_bomb_simple_upper_fu);
		mFuBtns[1] = (Button) findViewById(R.id.result_set_bomb_simple_oppositer_fu);
		mFuBtns[2] = (Button) findViewById(R.id.result_set_bomb_simple_downer_fu);
		mOkBtn = (Button) findViewById(R.id.result_set_bomb_simple_ok);
		mCancelBtn = (Button) findViewById(R.id.result_set_bomb_simple_cancel);
		
		OnPlayerCheckedChangeListener mCheckedListener = new OnPlayerCheckedChangeListener();
		for (CheckBox checkBox : mPosBoxs) {
			checkBox.setOnCheckedChangeListener(mCheckedListener);
		}
		OnFanClickListener mFanClickListener = new OnFanClickListener();
		for (Button btn : mFanBtns) {
			btn.setOnClickListener(mFanClickListener);
		}
		OnFuClickListener mFuClickListener = new OnFuClickListener();
		for (Button btn : mFuBtns) {
			btn.setOnClickListener(mFuClickListener);
		}
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		mFanBtns[0].setText(mFanTexts[0]);
		mFanBtns[1].setText(mFanTexts[0]);		
		mFanBtns[2].setText(mFanTexts[0]);
		mFuBtns[0].setText(mFan1Texts[0]);
		mFuBtns[1].setText(mFan1Texts[0]);
		mFuBtns[2].setText(mFan1Texts[0]);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.result_set_bomb_simple_ok:
			mHuCount = 0;
			for (CheckBox checkBox : mPosBoxs) {
				if (checkBox.isChecked()) mHuCount++;
			}
			if (mHuCount == 0) {
				ToastTool.showToast(mContext, R.string.please_choose_hele_player);
				return;
			}
			boolean isEnableFanfu = ManageTool.getInstance().getEnableFanFu();
			int roundCount = ManageTool.getInstance().getRoundCount();
			if (isEnableFanfu && roundCount >= 5) {
				mCheckBoxIndex = 0;
				checkFanfu();				
			} else {
				sendData();
			}
			break;
		case R.id.result_set_bomb_simple_cancel:
			this.finish();
			break;
		default:
			break;
		}
	}
	
	private void checkFanfu() {
		ManageTool tool = ManageTool.getInstance();
		for (; mCheckBoxIndex < mPosBoxs.length; mCheckBoxIndex++) {
			if (mPosBoxs[mCheckBoxIndex].isChecked()) {
				int bombIndex = (mOrgPlayer + 3 - mCheckBoxIndex) % 4;
				FanfuDialog dialog = new FanfuDialog(mContext, 
						tool.getPlayer(bombIndex).getNickName(),
						tool.getRoundCount(), fanIndex2Num(mFanIndex[mCheckBoxIndex]));
				dialog.setOnFanfuListener(new OnFanfuListener() {
					
					@Override
					public void onSuccess() {
						if (mCheckBoxIndex < mPosBoxs.length) {
							mCheckBoxIndex++;
							checkFanfu();
						} 					
					}
				});
				dialog.show();
				break;
			}
		}
		if (mCheckBoxIndex >= mPosBoxs.length) {
			sendData();
		}
	}
	
	private void sendData() {
		ManageTool mTool = ManageTool.getInstance();
		Intent data = new Intent(ResultSetBombSimpleActivity.this, ResultShow.class);
		data.putExtra(GameSimpleActivity.MAIN_VISION, mMainVision);
		data.putExtra(MjAction.Name, MjAction.ACTION_BOMB);
		data.putExtra(ManageTool.PLAYER_ORIGINAL_INDEX, mOrgPlayer);
		int[] bombIndexs = new int[mHuCount];
		int index = 0;
		for (int i = 0; i < mPosBoxs.length; i++) {
			if (mPosBoxs[i].isChecked()) {
				int fan = fanIndex2Num(mFanIndex[i]);
				int fu = fuIndex2Num(mFanIndex[i], mFuIndex[i]);
				bombIndexs[index] = (mOrgPlayer + 3 - i) % 4;	
				mTool.setResult(bombIndexs[index], fan, fu);
				index++;
			}
		}
		data.putExtra(ManageTool.RESULT_BOMB_INDEX, bombIndexs);
		startActivity(data);
		this.finish();
	}
	
	private int fanIndex2Num(int index) {
		int num = 0;
		if (index >= 0 && index <= 12) {
			num = index + 1;
		} else if (index >= 13 && index <= 17) {
//			num = (index - 11) * 13;
			num = 11 - index;
		}		
		return num;
	}
	
	private int fuIndex2Num(int fanIndex, int fuIndex) {
		int num = 0;
		if (fanIndex == 0) {
			num = (fuIndex + 3) * 10;
		} else if (fanIndex > 0 && fanIndex < 4) {
			if (fuIndex == 0) {
				num = 25;
			} else {
				num = (fuIndex + 2) * 10;
			}
		} 	
		return num;
	}
	
	class OnPlayerCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton btn, boolean isCheck) {
			switch (btn.getId()) {
			case R.id.result_set_bomb_simple_check_upper:
				if (isCheck) mPosLayouts[0].setVisibility(View.VISIBLE);
				else mPosLayouts[0].setVisibility(View.GONE);
				break;
			case R.id.result_set_bomb_simple_check_oppositer:
				if (isCheck) mPosLayouts[1].setVisibility(View.VISIBLE);
				else mPosLayouts[1].setVisibility(View.GONE);
				break;
			case R.id.result_set_bomb_simple_check_downer:
				if (isCheck) mPosLayouts[2].setVisibility(View.VISIBLE);
				else mPosLayouts[2].setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
		
	}
	
	class OnFanClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.result_set_bomb_simple_upper_fan:
				showSelectDialog(getResources().getString(R.string.please_select_fan), true, 0);
				break;
			case R.id.result_set_bomb_simple_oppositer_fan:
				showSelectDialog(getResources().getString(R.string.please_select_fan), true, 1);				
				break;
			case R.id.result_set_bomb_simple_downer_fan:
				showSelectDialog(getResources().getString(R.string.please_select_fan), true, 2);				
				break;
			default:
				break;
			}
		}
		
	}
	
	class OnFuClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.result_set_bomb_simple_upper_fu:
				showSelectDialog(getResources().getString(R.string.please_select_fu), false, 0);
				break;
			case R.id.result_set_bomb_simple_oppositer_fu:
				showSelectDialog(getResources().getString(R.string.please_select_fu), false, 1);
				break;
			case R.id.result_set_bomb_simple_downer_fu:
				showSelectDialog(getResources().getString(R.string.please_select_fu), false, 2);
				break;
			default:
				break;
			}
		}
		
	}
	
	private void showSelectDialog(String title, final boolean isFan, final int index) {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.layout_textpicker);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(title);
		mDialog.ok.setText(getResources().getString(R.string.ok));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (isFan) {
					String lastText = mFuBtns[index].getText().toString();
					String curText = mFanTexts[mFanIndex[index]];
					if (!lastText.equals(curText)) {
						mFanBtns[index].setText(curText);
						mFuIndex[index] = 0;
						if (mFanIndex[index] == 0) {
							mFuBtns[index].setText(mFan1Texts[0]);
						} else if (mFanIndex[index] > 0 && mFanIndex[index] < 4) {
							mFuBtns[index].setText(mFan2_4Texts[0]);
						} else {
							mFuBtns[index].setText(mFan5Texts[0]);
						}	
					}					
				} else {
					if (mFanIndex[index] == 0) {
						mFuBtns[index].setText(mFan1Texts[mFuIndex[index]]);
					} else if (mFanIndex[index] > 0 && mFanIndex[index] < 4) {
						mFuBtns[index].setText(mFan2_4Texts[mFuIndex[index]]);
					} else {
						mFuBtns[index].setText(mFan5Texts[mFuIndex[index]]);
					}					
				}
				mDialog.dismiss();
			}
		});
		TextPicker picker = (TextPicker) mDialog.getContentView().findViewById(R.id.textpicker);
		if (isFan) {
			picker.setText(mFanTexts);
			picker.setCurrentPosition(mFanIndex[index]);
		} else {
			if (mFanIndex[index] == 0) {
				picker.setText(mFan1Texts);
			} else if (mFanIndex[index] > 0 && mFanIndex[index] < 4) {
				picker.setText(mFan2_4Texts);
			} else {
				picker.setText(mFan5Texts);
			}	
			picker.setCurrentPosition(mFuIndex[index]);
		}
		picker.setOnTextSelectedListener(new OnTextSelectedListener() {
			
			@Override
			public void onTextSelected(String text, int position) {
				if (isFan) {					
					mFanIndex[index] = position;
				} else {
					mFuIndex[index] = position;
				}
			}
		});
		mDialog.show();
	}
	
}

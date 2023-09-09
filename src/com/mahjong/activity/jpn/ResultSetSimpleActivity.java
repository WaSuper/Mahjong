package com.mahjong.activity.jpn;

import com.mahjong.R;
import com.mahjong.activity.BaseActivity;
import com.mahjong.common.MjSetting;
import com.mahjong.control.BaseManager;
import com.mahjong.control.ManagerTool;
import com.mahjong.dialog.FanfuDialog;
import com.mahjong.dialog.FanfuDialog.OnFanfuListener;
import com.mahjong.model.MjAction;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.FlowRadioGroup;
import com.mahjong.ui.FlowRadioGroup.OnCheckedChangeListener;
import com.mahjong.ui.ext.TextPicker;
import com.mahjong.ui.ext.TextPicker.OnTextSelectedListener;
import com.mahjong.ui.PlayerFuncItem;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultSetSimpleActivity extends BaseActivity implements OnClickListener {	
	
	private Context mContext;
	
	private RelativeLayout mZimoLayout;
	private RelativeLayout mRongheLayout;
	private Button mOpppsiterBtn;
	private Button mUpperBtn;
	private Button mDownerBtn;
	private Button mOkBtn;
	private Button mCancelBtn;
	// 普通模式使用
	private FlowRadioGroup mFanGroup;
//	private FlowRadioGroup mFuGroup;
	private RadioButton[] mFanButtons = new RadioButton[18];
	private TextView[] mFuButtons = new TextView[11];
	private OnFuCheckedChangeListener mFuCheckedChangeListener;
	// 正方模式使用
	private Button mFanBtn;
	private Button mFuBtn;
	private String[] mFanTexts = {
			"1番", "2番", "3番", "4番", "5番", "6番",
			"7番", "8番", "9番", "10番", "11番", "12番", 
			"役满", "2倍役满", "3倍役满", "4倍役满", "5倍役满", "6倍役满",};
	private String[] mFan1Texts = {"30符", "40符", "50符", "60符","70符", 
			"80符", "90符", "100符", "110符"};
	private String[] mFan2_4Texts = {"25符", "30符", "40符", "50符", "60符",
			"70符", "80符", "90符", "100符", "110符"};
	private String[] mFan5Texts = {"--"};
	private int mFanIndex = 0;
	private int mFuIndex = 0;
	
	private Drawable[] mRadioButtonDrawables = new Drawable[3];
	
	private boolean isZimo; // 判断自摸布局、荣和布局
	private int mOrgPlayer; // 当前玩家初始索引
	private int mPosition = 0;	// 点炮玩家位置：1：下家，2：对家，3：上家
	private int mMainVision; // 主视觉
	
	private int mFanCount = 1; // 番数
	private int mFuCount = 30; // 符数
	
	private boolean landscapeMode; // 横屏模式
	private boolean squareMode; // 正方模式
	
	private BaseManager mManager = ManagerTool.getInstance().getManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		// 设定方向
		Intent intent = getIntent();
		int pos = intent.getIntExtra(BaseManager.PLAYER_ITEM_POSITION, PlayerFuncItem.POS_BOTTOM);
		landscapeMode = ShareprefenceTool.getInstance().getBoolean(MjSetting.LANDSCAPE_MODE, this, false);
		int[] port_orientations = {ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
				ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE};
		int[] land_orientations = {ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, 
				ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT};
		if (landscapeMode) {
			setRequestedOrientation(land_orientations[pos]);
		} else {
			setRequestedOrientation(port_orientations[pos]);
		}
		squareMode = ShareprefenceTool.getInstance().getBoolean(MjSetting.SQUARE_MODE, this, false);
		if (squareMode) {
			setContentView(R.layout.activity_square_jpn_result_set_simple);
		} else {
			setContentView(R.layout.activity_jpn_result_set_simple);
		}
		mContext = this;
		// 设定内容
		isZimo = intent.getBooleanExtra(BaseManager.PLAYER_IS_ZIMO, false);
		mOrgPlayer = intent.getIntExtra(BaseManager.PLAYER_ORIGINAL_INDEX, 0);
		mMainVision = intent.getIntExtra(GameSimpleActivity.MAIN_VISION, 0);
		initUI();
//		if (!isZimo) setDianPaoPlayer(2); // 默认对家
		if (!squareMode) {
			mFanGroup.check(mFanButtons[0].getId()); // 默认1番
			showButtonChecked(mFuButtons[2], true); // 默认30符
		} else {
			mFanBtn.setText(mFanTexts[0]);
			mFuBtn.setText(mFan1Texts[0]);
		}
	}
	
	private void initUI() {
		mZimoLayout = (RelativeLayout) findViewById(R.id.result_set_simple_top_zimo);
		mRongheLayout = (RelativeLayout) findViewById(R.id.result_set_simple_top_ronghe);
		mOpppsiterBtn = (Button) findViewById(R.id.result_set_simple_oppositer);
		mUpperBtn = (Button) findViewById(R.id.result_set_simple_upper);
		mDownerBtn = (Button) findViewById(R.id.result_set_simple_downer);
		mOkBtn = (Button) findViewById(R.id.result_set_simple_ok);
		mCancelBtn = (Button) findViewById(R.id.result_set_simple_cancel);
		if (!squareMode) {
			mFanGroup = (FlowRadioGroup) findViewById(R.id.result_set_simple_group_fan);
//			mFuGroup = (FlowRadioGroup) findViewById(R.id.result_set_simple_group_fu);
			mFanButtons[0] = (RadioButton) findViewById(R.id.result_set_simple_fan1);
			mFanButtons[1] = (RadioButton) findViewById(R.id.result_set_simple_fan2);
			mFanButtons[2] = (RadioButton) findViewById(R.id.result_set_simple_fan3);
			mFanButtons[3] = (RadioButton) findViewById(R.id.result_set_simple_fan4);
			mFanButtons[4] = (RadioButton) findViewById(R.id.result_set_simple_fan5);
			mFanButtons[5] = (RadioButton) findViewById(R.id.result_set_simple_fan6);
			mFanButtons[6] = (RadioButton) findViewById(R.id.result_set_simple_fan7);
			mFanButtons[7] = (RadioButton) findViewById(R.id.result_set_simple_fan8);
			mFanButtons[8] = (RadioButton) findViewById(R.id.result_set_simple_fan9);
			mFanButtons[9] = (RadioButton) findViewById(R.id.result_set_simple_fan10);
			mFanButtons[10] = (RadioButton) findViewById(R.id.result_set_simple_fan11);
			mFanButtons[11] = (RadioButton) findViewById(R.id.result_set_simple_fan12);
			mFanButtons[12] = (RadioButton) findViewById(R.id.result_set_simple_yiman1);
			mFanButtons[13] = (RadioButton) findViewById(R.id.result_set_simple_yiman2);
			mFanButtons[14] = (RadioButton) findViewById(R.id.result_set_simple_yiman3);
			mFanButtons[15] = (RadioButton) findViewById(R.id.result_set_simple_yiman4);
			mFanButtons[16] = (RadioButton) findViewById(R.id.result_set_simple_yiman5);
			mFanButtons[17] = (RadioButton) findViewById(R.id.result_set_simple_yiman6);
			mFuButtons[0] = (TextView) findViewById(R.id.result_set_simple_fu20);
			mFuButtons[1] = (TextView) findViewById(R.id.result_set_simple_fu25);
			mFuButtons[2] = (TextView) findViewById(R.id.result_set_simple_fu30);
			mFuButtons[3] = (TextView) findViewById(R.id.result_set_simple_fu40);
			mFuButtons[4] = (TextView) findViewById(R.id.result_set_simple_fu50);
			mFuButtons[5] = (TextView) findViewById(R.id.result_set_simple_fu60);
			mFuButtons[6] = (TextView) findViewById(R.id.result_set_simple_fu70);
			mFuButtons[7] = (TextView) findViewById(R.id.result_set_simple_fu80);
			mFuButtons[8] = (TextView) findViewById(R.id.result_set_simple_fu90);
			mFuButtons[9] = (TextView) findViewById(R.id.result_set_simple_fu100);
			mFuButtons[10] = (TextView) findViewById(R.id.result_set_simple_fu110);
		} else {
			mFanBtn = (Button) findViewById(R.id.result_set_simple_fan);
			mFuBtn = (Button) findViewById(R.id.result_set_simple_fu);
		}
		
		if (isZimo) {
			mZimoLayout.setVisibility(View.VISIBLE);
			mRongheLayout.setVisibility(View.GONE);
		} else {
			mZimoLayout.setVisibility(View.GONE);
			mRongheLayout.setVisibility(View.VISIBLE);
			switch (mManager.getMemberCount()) {
			case 2:
				mUpperBtn.setVisibility(View.INVISIBLE);
				mDownerBtn.setVisibility(View.INVISIBLE);
				setDianPaoPlayer(2); // 2人时默认选对家
				break;
			case 3:
				mOpppsiterBtn.setVisibility(View.INVISIBLE);
				break;
			default:
				break;
			}
		}
		mOpppsiterBtn.setOnClickListener(this);
		mUpperBtn.setOnClickListener(this);
		mDownerBtn.setOnClickListener(this);
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		if (!squareMode) {
			mFanGroup.setOnCheckedChangeListener(new OnFanCheckedChangeListener());
			mFuCheckedChangeListener = new OnFuCheckedChangeListener();
			for (TextView btn : mFuButtons) {
				btn.setOnClickListener(mFuCheckedChangeListener);
			}
		} else {
			mFanBtn.setOnClickListener(this);
			mFuBtn.setOnClickListener(this);
		}
		
		mRadioButtonDrawables[0] = getResources().getDrawable(R.drawable.ic_checkbox_circle2_nor);
		mRadioButtonDrawables[1] = getResources().getDrawable(R.drawable.ic_checkbox_circle2_sel);
		mRadioButtonDrawables[2] = getResources().getDrawable(R.drawable.ic_checkbox_circle2_dark_nor);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.result_set_simple_oppositer:
			setDianPaoPlayer(2);
			break;
		case R.id.result_set_simple_upper:
			setDianPaoPlayer(3);
			break;
		case R.id.result_set_simple_downer:
			setDianPaoPlayer(1);
			break;
		case R.id.result_set_simple_ok:
			if (!isZimo && mPosition == 0) {
				ToastTool.showToast(mContext, R.string.player_bomb_no_choose);
				return;
			}
			if (mManager.is17Step()) {
				if (mManager.getFanfuType() == 2 || mFanCount < 0) {
					sendData();
				} else { // 检测非役满情况
					FanfuDialog dialog = new FanfuDialog(mContext, null, 
							mFanCount, mFuCount, mManager.getFanfuType());
					dialog.setOnFanfuListener(new OnFanfuListener() {
						
						@Override
						public void onSuccess() {
							sendData();
						}
					});
					dialog.show();
				}
			} else {
				boolean isEnableFanfu = mManager.getEnableFanFu();
				int roundCount = mManager.getRoundCount();
				if (isEnableFanfu && roundCount >= 5 && mFanCount > 0) { // 检测非役满情况
					FanfuDialog dialog = new FanfuDialog(mContext, null, roundCount, mFanCount);
					dialog.setOnFanfuListener(new OnFanfuListener() {
						
						@Override
						public void onSuccess() {
							sendData();
						}
					});
					dialog.show();
				} else {
					sendData();
				}
			}
			break;
		case R.id.result_set_simple_cancel:
			this.finish();
			break;			
		case R.id.result_set_simple_fan:
			showSelectDialog(getResources().getString(R.string.please_select_fan), true);
			break;		
		case R.id.result_set_simple_fu:
			showSelectDialog(getResources().getString(R.string.please_select_fu), false);
			break;
		default:
			break;
		}
	}
	
	private void sendData() {
		mManager.setResult(mOrgPlayer, mFanCount, mFuCount);
		Intent data;
		if (landscapeMode) {
			data = new Intent(ResultSetSimpleActivity.this, ResultShowForLand.class);
		} else {
			data = new Intent(ResultSetSimpleActivity.this, ResultShow.class);
		}
		data.putExtra(GameSimpleActivity.MAIN_VISION, mMainVision);
		if (isZimo) {
			data.putExtra(MjAction.Name, MjAction.ACTION_ZIMO);
			data.putExtra(BaseManager.PLAYER_ORIGINAL_INDEX, mOrgPlayer);
		} else {
			data.putExtra(MjAction.Name, MjAction.ACTION_BOMB);
			int index = (mOrgPlayer + mPosition) % 4;
			if (mManager.getMemberCount() == 3) {
				switch (mOrgPlayer) {
				case 0:
					if (mPosition == 3) index = 2;
					break;
				case 2:
					if (mPosition == 1) index = 0;
					break;
				default:
					break;
				}
			}
			data.putExtra(BaseManager.PLAYER_ORIGINAL_INDEX, index);
			data.putExtra(BaseManager.RESULT_BOMB_INDEX, new int[] {mOrgPlayer});
		}
		startActivity(data);
		this.finish();		
	}
	
	private void setDianPaoPlayer(int pos) {
		mPosition = pos;
		switch (mPosition) {
		case 1: // 下家
			mDownerBtn.setBackgroundResource(R.drawable.ic_btn_player_dir);
			mOpppsiterBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			mUpperBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			break;
		case 2: // 对家
			mOpppsiterBtn.setBackgroundResource(R.drawable.ic_btn_player_dir);
			mDownerBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			mUpperBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			break;
		case 3: // 上家
			mUpperBtn.setBackgroundResource(R.drawable.ic_btn_player_dir);
			mOpppsiterBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			mDownerBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			break;
		default:
			break;
		}
	}
	
	class OnFanCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(FlowRadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.result_set_simple_fan1:	
				mFanCount = 1;
				if (isZimo) {
					setFuButtonsEnable(new int[] {0, 1, 10}, new int[] {2, 3, 4, 5, 6, 7, 8, 9}, true, 2);
				} else {
					setFuButtonsEnable(new int[] {0, 1}, new int[] {2, 3, 4, 5, 6, 7, 8, 9, 10}, true, 2);
				}
				break;
			case R.id.result_set_simple_fan2:
				mFanCount = 2;
				if (isZimo) {
					setFuButtonsEnable(null, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, true, 0);
				} else {
					setFuButtonsEnable(new int[] {0}, new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, true, 1);
				}				
				break;
			case R.id.result_set_simple_fan3:	
				mFanCount = 3;		
				if (isZimo) {
					setFuButtonsEnable(null, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, true, 0);
				} else {
					setFuButtonsEnable(new int[] {0}, new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, true, 1);
				}
				break;
			case R.id.result_set_simple_fan4:	
				mFanCount = 4;			
				if (isZimo) {
					setFuButtonsEnable(null, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, true, 0);
				} else {
					setFuButtonsEnable(new int[] {0}, new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, true, 1);
				}
				break;
			case R.id.result_set_simple_fan5:
				mFanCount = 5;				
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);
				break;
			case R.id.result_set_simple_fan6:	
				mFanCount = 6;			
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);
				break;
			case R.id.result_set_simple_fan7:	
				mFanCount = 7;			
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);
				break;
			case R.id.result_set_simple_fan8:	
				mFanCount = 8;			
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);
				break;
			case R.id.result_set_simple_fan9:	
				mFanCount = 9;			
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);
				break;
			case R.id.result_set_simple_fan10:	
				mFanCount = 10;			
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);
				break;
			case R.id.result_set_simple_fan11:		
				mFanCount = 11;		
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);
				break;
			case R.id.result_set_simple_fan12:
				mFanCount = 12;				
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);
				break;
			case R.id.result_set_simple_yiman1:
//				mFanCount = 13;
				mFanCount = -1;				
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);
				break;
			case R.id.result_set_simple_yiman2:
//				mFanCount = 26;
				mFanCount = -2;				
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);
				break;
			case R.id.result_set_simple_yiman3:
//				mFanCount = 39;
				mFanCount = -3;			
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);	
				break;
			case R.id.result_set_simple_yiman4:
//				mFanCount = 52;
				mFanCount = -4;			
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);	
				break;
			case R.id.result_set_simple_yiman5:
//				mFanCount = 65;
				mFanCount = -5;		
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);		
				break;
			case R.id.result_set_simple_yiman6:
//				mFanCount = 78;
				mFanCount = -6;		
				setFuButtonsEnable(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, null, false, -1);		
				break;
			default:
				break;
			}
		}		
	}
	
	private void setFuButtonsEnable(int[] unables, int[] enables, 
			boolean isNeedChange, int defaultIndex) {	
		int curIndex = changeFu2Index(mFuCount);
		if (unables != null) {
			for (int i = 0; i < unables.length; i++) {
				int index = unables[i];
				TextView item = mFuButtons[index];
				if (item.isEnabled()) {
					showButtonEnable(item, false);
				}		
				if (isNeedChange) {
					if	(curIndex < 0 || index == curIndex) {
						item = mFuButtons[defaultIndex];
						showButtonChecked(item, true);
						curIndex = defaultIndex;		
						mFuCount = changeIndex2Fu(defaultIndex);
					}
				}				
			}
		}	
		if (!isNeedChange) {
			mFuCount = 0;
		}
		if (enables != null) {
			for (int i = 0; i < enables.length; i++) {
				int index = enables[i];
				TextView item = mFuButtons[index];
				if (!item.isEnabled()) {
					showButtonChecked(item, false);					
				}	
				if	(curIndex < 0) {
					item = mFuButtons[defaultIndex];
					showButtonChecked(item, true);
					curIndex = defaultIndex;
					mFuCount = changeIndex2Fu(defaultIndex);
				}
			}			
		}
	}
	
	private int changeFu2Index(int fu) {
		int index = -1;
		if (fu == 20) {
			index = 0;
		} else if (fu == 25) {
			index = 1;
		} else if (fu > 25) {
			index  = fu / 10 - 1;
		}
		return index;
	}
	
	private int changeIndex2Fu(int index) {
		int fu = 0;
		if (index > 1 || index < 11) {
			fu = (index + 1) * 10;
		} else if (index == 1) {
			fu = 25;
		} else if (index == 0) {
			fu = 20;
		}		
		return fu;
	}
	
	private void showButtonChecked(TextView item, boolean isChecked) {
		if (isChecked) {
			item.setEnabled(true);
			item.setTextColor(getResources().getColor(R.color.white));
			item.setCompoundDrawablesWithIntrinsicBounds(
					mRadioButtonDrawables[1], null, null, null);
		} else {
			item.setEnabled(true);
			item.setTextColor(getResources().getColor(R.color.white));
			item.setCompoundDrawablesWithIntrinsicBounds(
					mRadioButtonDrawables[0], null, null, null);
		}
	}
	
	private void showButtonEnable(TextView item, boolean enable) {
		if (enable) {
			item.setEnabled(true);
			item.setTextColor(getResources().getColor(R.color.white));
			item.setCompoundDrawablesWithIntrinsicBounds(
					mRadioButtonDrawables[0], null, null, null);
		} else {
			item.setEnabled(false);
			item.setTextColor(getResources().getColor(R.color.grey));
			item.setCompoundDrawablesWithIntrinsicBounds(
					mRadioButtonDrawables[2], null, null, null);
		}
	}
	
	class OnFuCheckedChangeListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			int lastFu = mFuCount;
			switch (view.getId()) {
			case R.id.result_set_simple_fu20:
				mFuCount = 20;
				break;
			case R.id.result_set_simple_fu25:
				mFuCount = 25;
				break;
			case R.id.result_set_simple_fu30:
				mFuCount = 30;
				break;
			case R.id.result_set_simple_fu40:
				mFuCount = 40;
				break;
			case R.id.result_set_simple_fu50:
				mFuCount = 50;
				break;
			case R.id.result_set_simple_fu60:
				mFuCount = 60;
				break;
			case R.id.result_set_simple_fu70:
				mFuCount = 70;
				break;
			case R.id.result_set_simple_fu80:
				mFuCount = 80;
				break;
			case R.id.result_set_simple_fu90:
				mFuCount = 90;
				break;
			case R.id.result_set_simple_fu100:
				mFuCount = 100;
				break;
			case R.id.result_set_simple_fu110:
				mFuCount = 110;
				break;
			default:
				break;
			}	
			try {
				if (lastFu != mFuCount) {
					if (lastFu > 0) {
						showButtonChecked(mFuButtons[changeFu2Index(lastFu)], false);
					}
					showButtonChecked(mFuButtons[changeFu2Index(mFuCount)], true);
				}
			} catch (Exception e) {
				
			}			
		}
		
	}
	
	/**
	 * 正方模式下的番数符数选择框
	 * 
	 * @param title
	 * @param isFan
	 */
	private void showSelectDialog(String title, final boolean isFan) {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.layout_textpicker);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(title);
		mDialog.ok.setText(getResources().getString(R.string.ok));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (isFan) {
					String lastText = mFanBtn.getText().toString();
					String curText = mFanTexts[mFanIndex];
					if (!lastText.equals(curText)) {
						mFanBtn.setText(curText);
						mFuIndex = 0;
						if (mFanIndex == 0) {
							mFuBtn.setText(mFan1Texts[0]);
						} else if (mFanIndex > 0 && mFanIndex < 4) {
							mFuBtn.setText(mFan2_4Texts[0]);
						} else {
							mFuBtn.setText(mFan5Texts[0]);
						}	
					}					
				} else {
					if (mFanIndex == 0) {
						mFuBtn.setText(mFan1Texts[mFuIndex]);
					} else if (mFanIndex > 0 && mFanIndex < 4) {
						mFuBtn.setText(mFan2_4Texts[mFuIndex]);
					} else {
						mFuBtn.setText(mFan5Texts[mFuIndex]);
					}					
				}
				mFanCount = fanIndex2Num(mFanIndex);
				mFuCount = fuIndex2Num(mFanIndex, mFuIndex);
				mDialog.dismiss();
			}
		});
		TextPicker picker = (TextPicker) mDialog.getContentView().findViewById(R.id.textpicker);
		if (isFan) {
			picker.setText(mFanTexts);
			picker.setCurrentPosition(mFanIndex);
		} else {
			if (mFanIndex == 0) {
				picker.setText(mFan1Texts);
			} else if (mFanIndex > 0 && mFanIndex < 4) {
				picker.setText(mFan2_4Texts);
			} else {
				picker.setText(mFan5Texts);
			}	
			picker.setCurrentPosition(mFuIndex);
		}
		picker.setOnTextSelectedListener(new OnTextSelectedListener() {
			
			@Override
			public void onTextSelected(String text, int position) {
				if (isFan) {					
					mFanIndex = position;
				} else {
					mFuIndex = position;
				}
			}
		});
		mDialog.show();
	}
	
	/**
	 * 正方模式下的索引转番数
	 * 
	 * @param index
	 * @return
	 */
	private int fanIndex2Num(int index) {
		int num = 0;
		if (index >= 0 && index <= 11) {
			num = index + 1;
		} else if (index >= 12 && index <= 17) {
//			num = (index - 11) * 13;
			num = 11 - index;
		}		
		return num;
	}
	
	/**
	 * 正方模式下的索引转符数
	 * 
	 * @param fanIndex
	 * @param fuIndex
	 * @return
	 */
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
	
}

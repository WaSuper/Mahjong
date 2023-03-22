package com.mahjong.fragment;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.activity.jpn.SettingActivity;
import com.mahjong.adapter.SpecialYakuCheckAdapter;
import com.mahjong.adapter.StringArrayAdapter;
import com.mahjong.control.BaseManager;
import com.mahjong.control.ManagerTool;
import com.mahjong.dialog.MaPointDialog;
import com.mahjong.dialog.MaPointDialog.MaPointListener;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ValueTool;
import com.mahjong.ui.CommonDialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public abstract class BaseSettingFragment extends Fragment 
	implements OnClickListener, OnCheckedChangeListener {
	
	protected View mView;
	protected Context mContext;

	protected TextView mMemberCount;
	protected TextView mBattleCount;
	protected TextView mGroundWindText; // 17step
	protected TextView mBasePointText;
	protected TextView mEnterSWText;
	protected CheckBox mEnterSWBox;
	protected TextView mFanfuText; // 4p、3p / 17step
	protected CheckBox mFanfuBox; // 4p、3p
	protected TextView mFanfuItemText; // 17step
	protected TextView mLizhiBelongText;
	protected TextView mLizhiBelongChoice;
	protected TextView mMaPointText;
	protected TextView mRetPointText;
	protected RelativeLayout mSpYakuLayout;
	protected CheckBox mLandscapeMode;	
	protected CheckBox mDoubleWind4;
	protected CheckBox mZimoCutBox; // 3p
	
	protected List<String> mBattleCountList;
	protected int mMember;
	protected int defaultMember;
	protected int mMaxFeng;
	protected int mBaseScore;
	protected int defaultBaseScore;
	protected int mLizhiBelong;
	protected int[] mMaPoints;
	protected final int[] defaultMa4 = {15, 5, -5, -15};
	protected final int[] defaultMa3 = {15, 0, -15};
	protected final int[] defaultMa2 = {15, -15};
	protected MaPointDialog mMaPointDialog;
	protected int mRetPoint;
	protected int defaultRet;
	protected int mGroundWind; // 17step
	protected List<String> mGroundWindSelectList; // 17step
	protected int mFanfu; // 17step
	protected List<String> mFanfuSelectList; // 17step
	
	protected BaseManager mManager = ManagerTool.getInstance().getManager();
	
	protected String KEY_MA_POINT_4;
	protected String KEY_MA_POINT_3;
	protected String KEY_MA_POINT_2;
	protected String KEY_MEMBER_COUNT;
	protected String KEY_BATTLE_COUNT;
	protected String KEY_BASE_POINT;
	protected String KEY_ENTER_SOUTNWEST; // 4p、3p
	protected String KEY_FANFU;
	protected String KEY_LIZHI_BELONG;
	protected String KEY_RET_POINT;
	protected String KEY_LANDSCAPE_MODE;
	protected String KEY_DOUBLE_WIND_4;
	protected String KEY_GROUND_WIND; // 17step
	protected String KEY_ZIMO_CUT; // 3p
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_setting, container,false);  
		initUI();
		return mView;
	}
	
	/**
	 * 进入游戏前调用
	 */
	public abstract void initGameStart();
	
	private void initUI() {
		mMemberCount = (TextView) mView.findViewById(R.id.setting_member_count);
		mBattleCount = (TextView) mView.findViewById(R.id.setting_battle_count);
		mBasePointText = (TextView) mView.findViewById(R.id.setting_basepoint);
		mEnterSWText = (TextView) mView.findViewById(R.id.setting_tv_enter_sw); // 4p、3p
		mEnterSWBox = (CheckBox) mView.findViewById(R.id.setting_cb_enter_sw); // 4p、3p
		mLizhiBelongText = (TextView) mView.findViewById(R.id.setting_lizhi_belong_content);
		mLizhiBelongChoice = (TextView) mView.findViewById(R.id.setting_lizhi_belong);
		mMaPointText = (TextView) mView.findViewById(R.id.setting_mapoint);
		mRetPointText = (TextView) mView.findViewById(R.id.setting_retpoint);
		mSpYakuLayout = (RelativeLayout) mView.findViewById(R.id.setting_rl_special_yaku);
		mLandscapeMode = (CheckBox) mView.findViewById(R.id.setting_cb_landscape_mode);
		mDoubleWind4 =  (CheckBox) mView.findViewById(R.id.setting_cb_dwind4);
		mGroundWindText = (TextView) mView.findViewById(R.id.setting_tv_groundwind); // 17step
		mFanfuBox = (CheckBox) mView.findViewById(R.id.setting_cb_fanfu); // 4p、3p
		mFanfuItemText = (TextView) mView.findViewById(R.id.setting_game17s_tv_fanfu_item); // 17step
		mZimoCutBox = (CheckBox) mView.findViewById(R.id.setting_cb_zimocut); // 3p
		if (mManager.is17Step()) {
			mView.findViewById(R.id.setting_rl_enter_sw).setVisibility(View.GONE);
			mView.findViewById(R.id.setting_rl_fanfu).setVisibility(View.GONE);
			mView.findViewById(R.id.setting_rl_groundwind).setVisibility(View.VISIBLE);
			mView.findViewById(R.id.setting_game17s_rl_fanfu).setVisibility(View.VISIBLE);
			mFanfuText = (TextView) mView.findViewById(R.id.setting_game17s_tv_fanfu);
		} else {
			mFanfuText = (TextView) mView.findViewById(R.id.setting_tv_fanfu);
			if (mManager.is3pMahjong()) {
				mView.findViewById(R.id.setting_rl_zimocut).setVisibility(View.VISIBLE);
			}
		}
		
		mMemberCount.setOnClickListener(this);
		mBattleCount.setOnClickListener(this);
		mFanfuBox.setOnCheckedChangeListener(this);
		mBasePointText.setOnClickListener(this);
		mEnterSWText.setOnClickListener(this);
		mEnterSWBox.setOnCheckedChangeListener(this);
		mLizhiBelongText.setOnClickListener(this);
		mLizhiBelongChoice.setOnClickListener(this);
		mMaPointText.setOnClickListener(this);
		mRetPointText.setOnClickListener(this);
		mSpYakuLayout.setOnClickListener(this);
		mLandscapeMode.setOnCheckedChangeListener(this);
		mDoubleWind4.setOnCheckedChangeListener(this);
		mGroundWindText.setOnClickListener(this);
		mFanfuItemText.setOnClickListener(this);
		mFanfuText.setOnClickListener(this);
		mZimoCutBox.setOnCheckedChangeListener(this);
		
		ShareprefenceTool tool = ShareprefenceTool.getInstance();
		mMember = tool.getInt(KEY_MEMBER_COUNT, mContext, defaultMember);
		mMemberCount.setText(mMember + "");
		
		mMaxFeng = tool.getInt(KEY_BATTLE_COUNT, mContext, 1);
		mBattleCount.setText(battleCount2String(mMaxFeng));
		
		mBaseScore = tool.getInt(KEY_BASE_POINT, mContext, defaultBaseScore);
		mBasePointText.setText(mBaseScore + "");
		
		mLizhiBelong = tool.getInt(KEY_LIZHI_BELONG, mContext, 0);
		if (mLizhiBelong == 0) {
			mLizhiBelongChoice.setText(mContext.getString(R.string.bomb));
		} else {
			mLizhiBelongChoice.setText(mContext.getString(R.string.dealer));
		}
		
		initMaPoints();
		
		mRetPoint = tool.getInt(KEY_RET_POINT, mContext, defaultRet);
		mRetPointText.setText(mRetPoint + "");
		
		boolean landscapeMode = tool.getBoolean(KEY_LANDSCAPE_MODE, mContext, false);
		mLandscapeMode.setChecked(landscapeMode);
		
		boolean doubleWind4 = tool.getBoolean(KEY_DOUBLE_WIND_4, mContext, false);
		mDoubleWind4.setChecked(doubleWind4);
		
		if (mManager.is17Step()) {
			mGroundWind = tool.getInt(KEY_GROUND_WIND, mContext, 0);
			mGroundWindText.setText(groundwindList2String(mGroundWind));
			
			mFanfu = tool.getInt(KEY_FANFU, mContext, 0);
			mFanfuItemText.setText(fanfuList2String(mFanfu));
		} else {
			boolean isEnterSW = tool.getBoolean(KEY_ENTER_SOUTNWEST, mContext, false);
			mEnterSWBox.setChecked(isEnterSW);
			
			boolean isFanfu = tool.getBoolean(KEY_FANFU, mContext, false);
			mFanfuBox.setChecked(isFanfu);
			
			if (mManager.is3pMahjong()) {
				boolean zimoCut = tool.getBoolean(KEY_ZIMO_CUT, mContext, false);
				mZimoCutBox.setChecked(zimoCut);
			}
		}
	}
	
	protected void initMaPoints() {
		switch (mMember) {
		case 2:
			mMaPoints = ShareprefenceTool.getInstance().getIntArray(KEY_MA_POINT_2, mContext);
			if (mMaPoints == null || mMaPoints.length != 2) {
				mMaPoints = defaultMa2;
			}
			break;
		case 3:
			mMaPoints = ShareprefenceTool.getInstance().getIntArray(KEY_MA_POINT_3, mContext);
			if (mMaPoints == null || mMaPoints.length != 3) {
				mMaPoints = defaultMa3;
			}
			break;
		case 4:
			mMaPoints = ShareprefenceTool.getInstance().getIntArray(KEY_MA_POINT_4, mContext);
			if (mMaPoints == null || mMaPoints.length != 4) {
				mMaPoints = defaultMa4;
			}
			break;
		default:
			break;
		}
		String text = mMaPoints[0] + "";
		for (int i = 1; i < mMaPoints.length; i++) {
			text += "," + mMaPoints[i];
		}
		mMaPointText.setText(text);
	}
	
	protected void setMaPoints(int[] mas) {
		mMaPoints = mas;
		switch (mMember) {
		case 2:
			ShareprefenceTool.getInstance().setIntArray(KEY_MA_POINT_2, mMaPoints, mContext);
			break;
		case 3:
			ShareprefenceTool.getInstance().setIntArray(KEY_MA_POINT_3, mMaPoints, mContext);
			break;
		case 4:
			ShareprefenceTool.getInstance().setIntArray(KEY_MA_POINT_4, mMaPoints, mContext);
			break;
		default:
			break;
		}
		String text = mMaPoints[0] + "";
		for (int i = 1; i < mMaPoints.length; i++) {
			text += "," + mMaPoints[i];
		}
		mMaPointText.setText(text);
	}
	
	protected int[] changeMaPointTo4() {
		int[] mas = {0, 0, 0, 0};
		for (int i = 0; i < mMaPoints.length; i++) {
			mas[i] = mMaPoints[i];
		}
		return mas;
	}
	
	protected abstract String battleCount2String(int count);
	
	protected abstract int battleString2Count(String battle);
	
	protected abstract String fanfuList2String(int index);
	
	protected abstract String groundwindList2String(int index);

	@Override
	public void onClick(View v) {
		List<String> mList = new ArrayList<String>();
		switch (v.getId()) {
		case R.id.setting_member_count:
			if (mManager.is17Step()) {
				mList.add("2");
				mList.add("3");
				mList.add("4");
			} else if (mManager.is3pMahjong()) {
				mList.add("3");
			} else if (mManager.is4pMahjong()) {
				mList.add("4");
			}
			showListDialog(getResources().getString(R.string.member_count), mList);
			break;
		case R.id.setting_battle_count:
			showListDialog(getResources().getString(R.string.battle_count), mBattleCountList);
			break;
		case R.id.setting_tv_groundwind:
			showListDialog(getResources().getString(R.string.windground), mGroundWindSelectList);
			break;
		case R.id.setting_basepoint:
			if (mManager.is17Step()) {
				showBasePointDialog(50000, 100000, 150000);
			} else if (mManager.is3pMahjong()) {
				showBasePointDialog(35000, 50000, 100000);
			} else if (mManager.is4pMahjong()) {
				showBasePointDialog(25000, 40000, 100000);
			}
			break;
		case R.id.setting_tv_enter_sw:
			showTextDialog(getResources().getString(R.string.enter_south_or_west), 
					getResources().getString(R.string.enter_south_or_west_content));
			break;
		case R.id.setting_tv_fanfu:
			showTextDialog(getResources().getString(R.string.FanFu), 
					getResources().getString(R.string.FanFu_content));
			break;
		case R.id.setting_lizhi_belong_content:
			showTextDialog(getResources().getString(R.string.lizhi_belong_to), 
					getResources().getString(R.string.lizhi_belong_to_content));
			break;
		case R.id.setting_lizhi_belong:
			mList.add(getString(R.string.bomb));
			mList.add(getString(R.string.dealer));
			showListDialog(getResources().getString(R.string.lizhi_belong_to), mList);
			break;
		case R.id.setting_mapoint:
			showMaPointDialog();
			break;
		case R.id.setting_retpoint:
			showRetPointDialog();
			break;
		case R.id.setting_rl_special_yaku:
			showSpecialYakuDialog(getResources().getString(R.string.special_yaku)); 
			break;
		case R.id.setting_game17s_tv_fanfu:
			showTextDialog(getResources().getString(R.string.FanFu), 
					getResources().getString(R.string.game17s_FanFu_content));
			break;
		case R.id.setting_game17s_tv_fanfu_item:
			showListDialog(getResources().getString(R.string.game17s_FanFu), mFanfuSelectList);
			break;
		default:
			break;
		}
	}
	
	protected void showTextDialog(String title, String content) {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.item_text);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(title);
		mDialog.ok.setText(getResources().getString(R.string.back));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		TextView textView = (TextView) mDialog.getContentView().findViewById(R.id.item_text);
		textView.setText(content);
		textView.setPadding(ValueTool.dp2px(mContext, 10), 0, ValueTool.dp2px(mContext, 10), 0);
		textView.setTextSize(16);
		mDialog.show();
	}
	
	protected void showListDialog(String title, final List<String> list) {
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
		mAdapter.setData(list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String textString = list.get(position);
				if (mDialog.titleTextView.getText().equals(
						getResources().getString(R.string.member_count))) {
					int tmpMember = Integer.parseInt(textString);
					if (tmpMember != mMember) {
						mMember = tmpMember;
						mMemberCount.setText(textString);
						ShareprefenceTool.getInstance().setInt(
								KEY_MEMBER_COUNT, mMember, mContext);
						initMaPoints();
						Intent intent = new Intent(SettingActivity.SETTING_MEMBER);
						mContext.sendBroadcast(intent);
					}
				} else if (mDialog.titleTextView.getText().equals(
						getResources().getString(R.string.battle_count))) {
					mBattleCount.setText(textString);
					mMaxFeng = battleString2Count(textString);
					ShareprefenceTool.getInstance().setInt(
							KEY_BATTLE_COUNT, mMaxFeng, mContext);
				}  else if (mDialog.titleTextView.getText().equals(
						getResources().getString(R.string.windground))) {
					mGroundWind = position;
					if (mGroundWind == 0) {
						mGroundWindText.setText(mContext.getString(R.string.game17s_groundwind_stay_east));
					} else {
						mGroundWindText.setText(mContext.getString(R.string.game17s_groundwind_take_turns));
					}
					ShareprefenceTool.getInstance().setInt(
							KEY_GROUND_WIND, mGroundWind, mContext);
				} else if (mDialog.titleTextView.getText().equals(
						getResources().getString(R.string.lizhi_belong_to))) {
					mLizhiBelong = position;
					if (mLizhiBelong == 0) {
						mLizhiBelongChoice.setText(mContext.getString(R.string.bomb));
					} else {
						mLizhiBelongChoice.setText(mContext.getString(R.string.dealer));
					}
					ShareprefenceTool.getInstance().setInt(
							KEY_LIZHI_BELONG, mLizhiBelong, mContext);
				} else if (mDialog.titleTextView.getText().equals(
						getResources().getString(R.string.game17s_FanFu))) {
					mFanfu = position;
					switch (mFanfu) {
					case 0:
						mFanfuItemText.setText(mContext.getString(R.string.game17s_FanFu_five));
						break;
					case 1:
						mFanfuItemText.setText(mContext.getString(R.string.game17s_FanFu_manguan));
						break;
					case 2:
						mFanfuItemText.setText(mContext.getString(R.string.game17s_FanFu_none));
						break;
					default:
						break;
					}
					ShareprefenceTool.getInstance().setInt(
							KEY_FANFU, mFanfu, mContext);
				}
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}

	protected void showBasePointDialog(final int set1, final int set2, final int set3) {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		mDialog.addView(R.layout.layout_basepoint);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.titleTextView.setText(mContext.getString(R.string.base_point));
		View contentView = mDialog.getContentView();
		Button set1Btn = (Button) contentView.findViewById(R.id.basepoint_set1);
		set1Btn.setText(set1 + "");
		Button set2Btn = (Button) contentView.findViewById(R.id.basepoint_set2);
		set2Btn.setText(set2 + "");
		Button set3Btn = (Button) contentView.findViewById(R.id.basepoint_set3);
		set3Btn.setText(set3 + "");
		Button plus1000Btn = (Button) contentView.findViewById(R.id.basepoint_plus1000);
		Button minus1000Btn = (Button) contentView.findViewById(R.id.basepoint_minus1000);
		final EditText pointText = (EditText) contentView.findViewById(R.id.basepoint_point);
		pointText.setText(mBaseScore + "");
		defaultBaseScore = mBaseScore;
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.basepoint_set1:
					defaultBaseScore = set1;
					pointText.setText(set1 + "");
					break;
				case R.id.basepoint_set2:
					defaultBaseScore = set2;
					pointText.setText(set2 + "");
					break;
				case R.id.basepoint_set3:
					defaultBaseScore = set3;
					pointText.setText(set3 + "");
					break;
				case R.id.basepoint_plus1000:
					defaultBaseScore += 1000;
					pointText.setText(defaultBaseScore + "");
					break;
				case R.id.basepoint_minus1000:
					defaultBaseScore -= 1000;
					if (defaultBaseScore < 0) defaultBaseScore = 0;
					pointText.setText(defaultBaseScore + "");
					break;
				default:
					break;
				}				
			}
		};
		set1Btn.setOnClickListener(listener);
		set2Btn.setOnClickListener(listener);
		set3Btn.setOnClickListener(listener);
		plus1000Btn.setOnClickListener(listener);
		minus1000Btn.setOnClickListener(listener);
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mBaseScore = defaultBaseScore;
				mBasePointText.setText(mBaseScore + "");
				ShareprefenceTool.getInstance().setInt(KEY_BASE_POINT, mBaseScore, mContext);
				mDialog.dismiss();
			}
		});
		mDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
	private void showMaPointDialog() {
		if (mMaPointDialog == null) {
			mMaPointDialog = new MaPointDialog(mContext);
			mMaPointDialog.setOnMaPointListener(new MaPointListener() {
				
				@Override
				public void OnMaPointChange(int[] mas) {
					setMaPoints(mas);
				}
			});
		}
		mMaPointDialog.show(mMaPoints);
	}
	
	private void showRetPointDialog() {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		mDialog.addView(R.layout.layout_retpoint);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.titleTextView.setText(mContext.getString(R.string.ret_point));
		View contentView = mDialog.getContentView();
		Button plus1000Btn = (Button) contentView.findViewById(R.id.retpoint_plus1000);
		Button minus1000Btn = (Button) contentView.findViewById(R.id.retpoint_minus1000);
		final EditText pointText = (EditText) contentView.findViewById(R.id.retpoint_point);
		pointText.setText(mRetPoint + "");
		defaultRet = mRetPoint;
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.retpoint_plus1000:
					defaultRet += 1000;
					pointText.setText(defaultRet + "");
					break;
				case R.id.retpoint_minus1000:
					defaultRet -= 1000;
					if (defaultRet < 0) defaultRet = 0;
					pointText.setText(defaultRet + "");
					break;
				default:
					break;
				}				
			}
		};
		plus1000Btn.setOnClickListener(listener);
		minus1000Btn.setOnClickListener(listener);
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mRetPoint = defaultRet;
				mRetPointText.setText(mRetPoint + "");
				ShareprefenceTool.getInstance().setInt(KEY_RET_POINT, mRetPoint, mContext);
				mDialog.dismiss();
			}
		});
		mDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}

	private void showSpecialYakuDialog(String title) {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(title);
		mDialog.ok.setText(getResources().getString(R.string.ok));
		ListView listView = (ListView) mDialog.getContentView();
		final SpecialYakuCheckAdapter mAdapter = new SpecialYakuCheckAdapter(mContext);
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
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.setting_cb_fanfu:
			ShareprefenceTool.getInstance().setBoolean(
					KEY_FANFU, isChecked, mContext);
			break;
		case R.id.setting_cb_enter_sw:
			ShareprefenceTool.getInstance().setBoolean(
					KEY_ENTER_SOUTNWEST, isChecked, mContext);
			break;
		case R.id.setting_cb_landscape_mode:
			ShareprefenceTool.getInstance().setBoolean(
					KEY_LANDSCAPE_MODE, isChecked, mContext);
			break;
		case R.id.setting_cb_dwind4:
			ShareprefenceTool.getInstance().setBoolean(
					KEY_DOUBLE_WIND_4, isChecked, mContext);
			break;
		case R.id.setting_cb_zimocut:
			ShareprefenceTool.getInstance().setBoolean(
					KEY_ZIMO_CUT, isChecked, mContext);
			break;
		default:
			break;
		}
	}
}

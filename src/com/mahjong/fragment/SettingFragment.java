﻿package com.mahjong.fragment;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.adapter.StringArrayAdapter;
import com.mahjong.common.MjSetting;
import com.mahjong.tools.ManageTool;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ValueTool;
import com.mahjong.ui.CommonDialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class SettingFragment extends Fragment 
		implements OnClickListener, OnCheckedChangeListener {

	private View mView;
	private Context mContext;
	
	private TextView mMemberCount;
	private TextView mBattleCount;
	private TextView mBasePoint;
	private TextView mEnterSWText;
	private CheckBox mEnterSWBox;
	private TextView mSpYakuText;
	private TextView mFanfuText;
	private CheckBox mFanfuBox;
	private TextView mLizhiBelongText;
	private TextView mLizhiBelongChoice;
	private TextView mMaPointText;
	
	private List<String> mBattleCountList;
	private int mMember;
	private int mMaxFeng;
	private int mBaseScore;
	private int mLizhiBelong;
	private int[] mMa;
	private int[] defaultMa = {15, 5, -5, -15};
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_setting, container,false);  
		mContext = getActivity();
		initUI();
		return mView;
	}
	
	private void initUI() {
		mMemberCount = (TextView) mView.findViewById(R.id.setting_member_count);
		mBattleCount = (TextView) mView.findViewById(R.id.setting_battle_count);
		mSpYakuText = (TextView) mView.findViewById(R.id.setting_tv_special_yaku);
		mFanfuText = (TextView) mView.findViewById(R.id.setting_tv_fanfu);
		mFanfuBox = (CheckBox) mView.findViewById(R.id.setting_cb_fanfu);
		mBasePoint = (TextView) mView.findViewById(R.id.setting_basepoint);
		mEnterSWText = (TextView) mView.findViewById(R.id.setting_tv_enter_sw);
		mEnterSWBox =  (CheckBox) mView.findViewById(R.id.setting_cb_enter_sw);
		mLizhiBelongText = (TextView) mView.findViewById(R.id.setting_lizhi_belong_content);
		mLizhiBelongChoice = (TextView) mView.findViewById(R.id.setting_lizhi_belong);
		mMaPointText = (TextView) mView.findViewById(R.id.setting_mapoint);
		
		mMemberCount.setOnClickListener(this);
		mBattleCount.setOnClickListener(this);
		mSpYakuText.setOnClickListener(this);
		mFanfuText.setOnClickListener(this);
		mFanfuBox.setOnCheckedChangeListener(this);
		mBasePoint.setOnClickListener(this);
		mEnterSWText.setOnClickListener(this);
		mEnterSWBox.setOnCheckedChangeListener(this);
		mLizhiBelongText.setOnClickListener(this);
		mLizhiBelongChoice.setOnClickListener(this);
		mMaPointText.setOnClickListener(this);
		
		ShareprefenceTool tool = ShareprefenceTool.getInstance();
		mMember = tool.getInt(MjSetting.MEMBER_COUNT, mContext, 4);
		mMemberCount.setText(mMember + "");
		
		mMaxFeng = tool.getInt(MjSetting.BATTLE_COUNT, mContext, 1);
		mBattleCount.setText(battleCount2String(mMaxFeng));
		
		mBaseScore = tool.getInt(MjSetting.BASE_POINT, mContext, 25000);
		mBasePoint.setText(mBaseScore + "");
		
		boolean isEnterSW = tool.getBoolean(MjSetting.ENTER_SOUTNWEST, mContext, false);
		mEnterSWBox.setChecked(isEnterSW);
				
		boolean isFanfu = tool.getBoolean(MjSetting.FANFU, mContext, false);
		mFanfuBox.setChecked(isFanfu);
		
		mLizhiBelong = tool.getInt(MjSetting.LIZHI_BELONG, mContext, 0);
		if (mLizhiBelong == 0) {
			mLizhiBelongChoice.setText(mContext.getString(R.string.bomb));
		} else {
			mLizhiBelongChoice.setText(mContext.getString(R.string.dealer));
		}
		
		mMa = tool.getIntArray(MjSetting.MA_POINT, mContext);
		if (mMa == null || mMa.length != 4) {
			mMa = defaultMa;
		}
		mMaPointText.setText(mMa[0] + "," + mMa[1] + "," + mMa[2] + "," + mMa[3]);
	}

	public void initGameStart() {
		ManageTool tool = ManageTool.getInstance();
		tool.setMaxFeng(mMaxFeng);
		tool.setBaseScore(mBaseScore);
		tool.setEnterSouthEast(mEnterSWBox.isChecked());
		tool.setLiZhiBelong(mLizhiBelong);
		tool.setEnableFanFu(mFanfuBox.isChecked());
		tool.setMaPoint(mMa);
	}
	
	private String battleCount2String(int count) {
		if (mBattleCountList == null) {
			mBattleCountList = new ArrayList<String>();
			mBattleCountList.add(getResources().getString(R.string.battle_one));
			mBattleCountList.add(getResources().getString(R.string.battle_two));
			mBattleCountList.add(getResources().getString(R.string.battle_four));
		}
		if (count == 3) count--;
		String battle = mBattleCountList.get(count);
		return battle;
	}
	
	private int battleString2Count(String battle) {
		int count = 1;
		if (battle.endsWith(mBattleCountList.get(0))) {
			count = 0;
		} else if (battle.endsWith(mBattleCountList.get(1))) {
			count = 1;
		} else if (battle.endsWith(mBattleCountList.get(2))) {
			count = 3;
		} 
		return count;
	}
	
	public void onClick(View v) {
		List<String> mList = new ArrayList<String>();
		switch (v.getId()) {
		case R.id.setting_member_count:
			//mList.add("3");
			mList.add("4");
			showListDialog(getResources().getString(R.string.member_count), mList);
			break;
		case R.id.setting_battle_count:
			showListDialog(getResources().getString(R.string.battle_count), mBattleCountList);
			break;
		case R.id.setting_basepoint:
			mList.add("25000");
			mList.add("40000");
			mList.add("100000");
			showListDialog(getResources().getString(R.string.base_point), mList);
			break;
		case R.id.setting_tv_special_yaku:
			showTextDialog(getResources().getString(R.string.special_yaku), 
					getResources().getString(R.string.no_support));
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
		default:
			break;
		}
	}
	
	private void showTextDialog(String title, String content) {
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
	
	private void showListDialog(String title, final List<String> list) {
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
					mMember = Integer.parseInt(textString);
					mMemberCount.setText(textString);
					ShareprefenceTool.getInstance().setInt(
							MjSetting.MEMBER_COUNT, mMember, mContext);
				} else if (mDialog.titleTextView.getText().equals(
						getResources().getString(R.string.battle_count))) {
					mBattleCount.setText(textString);
					mMaxFeng = battleString2Count(textString);
					ShareprefenceTool.getInstance().setInt(
							MjSetting.BATTLE_COUNT, mMaxFeng, mContext);
				} else if (mDialog.titleTextView.getText().equals(
						getResources().getString(R.string.base_point))) {
					mBaseScore = Integer.parseInt(textString);
					mBasePoint.setText(textString);
					ShareprefenceTool.getInstance().setInt(
							MjSetting.BASE_POINT, mBaseScore, mContext);					
				} else if (mDialog.titleTextView.getText().equals(
						getResources().getString(R.string.lizhi_belong_to))) {
					mLizhiBelong = position;
					if (mLizhiBelong == 0) {
						mLizhiBelongChoice.setText(mContext.getString(R.string.bomb));
					} else {
						mLizhiBelongChoice.setText(mContext.getString(R.string.dealer));
					}
				}
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
	private void showMaPointDialog() {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		mDialog.addView(R.layout.layout_mapoint);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.titleTextView.setText(mContext.getString(R.string.ma_point));
		final Button[] plus5Btns = new Button[4];
		final Button[] plus1Btns = new Button[4];
		final Button[] minus1Btns = new Button[4];
		final Button[] minus5Btns = new Button[4];
		final EditText[] maTexts = new EditText[4];
		View contentView = mDialog.getContentView();
		plus5Btns[0] = (Button) contentView.findViewById(R.id.mapoint_line1_plus5);
		plus5Btns[1] = (Button) contentView.findViewById(R.id.mapoint_line2_plus5);
		plus5Btns[2] = (Button) contentView.findViewById(R.id.mapoint_line3_plus5);
		plus5Btns[3] = (Button) contentView.findViewById(R.id.mapoint_line4_plus5);
		plus1Btns[0] = (Button) contentView.findViewById(R.id.mapoint_line1_plus1);
		plus1Btns[1] = (Button) contentView.findViewById(R.id.mapoint_line2_plus1);
		plus1Btns[2] = (Button) contentView.findViewById(R.id.mapoint_line3_plus1);
		plus1Btns[3] = (Button) contentView.findViewById(R.id.mapoint_line4_plus1);
		minus1Btns[0] = (Button) contentView.findViewById(R.id.mapoint_line1_minus1);
		minus1Btns[1] = (Button) contentView.findViewById(R.id.mapoint_line2_minus1);
		minus1Btns[2] = (Button) contentView.findViewById(R.id.mapoint_line3_minus1);
		minus1Btns[3] = (Button) contentView.findViewById(R.id.mapoint_line4_minus1);
		minus5Btns[0] = (Button) contentView.findViewById(R.id.mapoint_line1_minus5);
		minus5Btns[1] = (Button) contentView.findViewById(R.id.mapoint_line2_minus5);
		minus5Btns[2] = (Button) contentView.findViewById(R.id.mapoint_line3_minus5);
		minus5Btns[3] = (Button) contentView.findViewById(R.id.mapoint_line4_minus5);
		maTexts[0] = (EditText) contentView.findViewById(R.id.mapoint_line1_point);
		maTexts[1] = (EditText) contentView.findViewById(R.id.mapoint_line2_point);
		maTexts[2] = (EditText) contentView.findViewById(R.id.mapoint_line3_point);
		maTexts[3] = (EditText) contentView.findViewById(R.id.mapoint_line4_point);
		final TextView maInvalid = (TextView) contentView.findViewById(R.id.mapoint_invalid);
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.mapoint_line1_plus5:		
					defaultMa[0] += 5;
					maTexts[0].setText(defaultMa[0] + "");
					checkMaPointValid(defaultMa, maInvalid);
					break;
				case R.id.mapoint_line1_plus1:
					defaultMa[0] += 1;
					maTexts[0].setText(defaultMa[0] + "");
					checkMaPointValid(defaultMa, maInvalid);
					break;
				case R.id.mapoint_line1_minus1:
					if (defaultMa[0] >= 1) {
						defaultMa[0] -= 1;
						maTexts[0].setText(defaultMa[0] + "");
						checkMaPointValid(defaultMa, maInvalid);
					}
					break;
				case R.id.mapoint_line1_minus5:
					if (defaultMa[0] >= 5) {
						defaultMa[0] -= 5;
						maTexts[0].setText(defaultMa[0] + "");
						checkMaPointValid(defaultMa, maInvalid);
					}
					break;
				case R.id.mapoint_line2_plus5:	
					defaultMa[1] += 5;
					maTexts[1].setText(defaultMa[1] + "");	
					checkMaPointValid(defaultMa, maInvalid);				
					break;
				case R.id.mapoint_line2_plus1:
					defaultMa[1] += 1;
					maTexts[1].setText(defaultMa[1] + "");	
					checkMaPointValid(defaultMa, maInvalid);
					break;
				case R.id.mapoint_line2_minus1:
					if (defaultMa[1] >= 1) {
						defaultMa[1] -= 1;
						maTexts[1].setText(defaultMa[1] + "");	
						checkMaPointValid(defaultMa, maInvalid);
					}	
					break;
				case R.id.mapoint_line2_minus5:
					if (defaultMa[1] >= 5) {
						defaultMa[1] -= 5;
						maTexts[1].setText(defaultMa[1] + "");	
						checkMaPointValid(defaultMa, maInvalid);
					}
					break;
				case R.id.mapoint_line3_plus5:		
					if (defaultMa[2] <= -5) {
						defaultMa[2] += 5;
						maTexts[2].setText(defaultMa[2] + "");
						checkMaPointValid(defaultMa, maInvalid);	
					}			
					break;
				case R.id.mapoint_line3_plus1:
					if (defaultMa[2] <= -1) {
						defaultMa[2] += 1;
						maTexts[2].setText(defaultMa[2] + "");
						checkMaPointValid(defaultMa, maInvalid);
					}
					break;
				case R.id.mapoint_line3_minus1:
					defaultMa[2] -= 1;
					maTexts[2].setText(defaultMa[2] + "");
					checkMaPointValid(defaultMa, maInvalid);
					break;
				case R.id.mapoint_line3_minus5:
					defaultMa[2] -= 5;
					maTexts[2].setText(defaultMa[2] + "");
					checkMaPointValid(defaultMa, maInvalid);
					break;
				case R.id.mapoint_line4_plus5:	
					if (defaultMa[3] <= -5) {
						defaultMa[3] += 5;
						maTexts[3].setText(defaultMa[3] + "");	
						checkMaPointValid(defaultMa, maInvalid);
					}			
					break;
				case R.id.mapoint_line4_plus1:
					if (defaultMa[3] <= -1) {
						defaultMa[3] += 1;
						maTexts[3].setText(defaultMa[3] + "");	
						checkMaPointValid(defaultMa, maInvalid);
					}
					break;
				case R.id.mapoint_line4_minus1:
					defaultMa[3] -= 1;
					maTexts[3].setText(defaultMa[3] + "");	
					checkMaPointValid(defaultMa, maInvalid);
					break;
				case R.id.mapoint_line4_minus5:
					defaultMa[3] -= 5;
					maTexts[3].setText(defaultMa[3] + "");	
					checkMaPointValid(defaultMa, maInvalid);
					break;
				default:
					break;
				}
			}
		};
		defaultMa = mMa;
		for (int i = 0; i < 4; i++) {
			plus5Btns[i].setOnClickListener(listener);
			plus1Btns[i].setOnClickListener(listener);
			minus1Btns[i].setOnClickListener(listener);
			minus5Btns[i].setOnClickListener(listener);
			maTexts[i].setText(defaultMa[i] + "");
		}
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (defaultMa[0] + defaultMa[1] + defaultMa[2]+ defaultMa[3] != 0) {
					return;
				}
				mMa = defaultMa;
				ShareprefenceTool.getInstance().setIntArray(MjSetting.MA_POINT, mMa, mContext);
				mMaPointText.setText(mMa[0] + "," + mMa[1] + "," + mMa[2] + "," + mMa[3]);
				mDialog.dismiss();				
			}
		});
		mDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
	private boolean checkMaPointValid(int[] ma, TextView invalidText) {
		boolean result = ma[0] + ma[1] + ma[2] + ma[3] == 0;
		if (result) {
			invalidText.setVisibility(View.INVISIBLE);
		} else {
			invalidText.setVisibility(View.VISIBLE);
		}
		return result;
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.setting_cb_fanfu:
			ShareprefenceTool.getInstance().setBoolean(
					MjSetting.FANFU, isChecked, mContext);
			break;
		case R.id.setting_cb_enter_sw:
			ShareprefenceTool.getInstance().setBoolean(
					MjSetting.ENTER_SOUTNWEST, isChecked, mContext);
			break;
		default:
			break;
		}
		
	}
	
}

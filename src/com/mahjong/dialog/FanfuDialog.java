package com.mahjong.dialog;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahjong.R;
import com.mahjong.ui.CommonDialog;

public class FanfuDialog extends CommonDialog implements android.view.View.OnClickListener, OnCheckedChangeListener {

	private ImageView mMinusBtn;
	private ImageView mPlusBtn;
	private TextView mDoraTitleText;
	private TextView mDoraText;
	private CheckBox mYifaBox;
	private CheckBox mHaidiBox;
	private CheckBox mHediBox;
	private TextView mErrorText;
	
	private int round_count;
	private int fan;
	private int dora_count = 0;
	
	private boolean is17step;
	private int fu;
	private int type;
	
	private OnFanfuListener mListener;
	
	public FanfuDialog(Context context, String title, int roundCount, int fan) {
		super(context, R.style.MyDialogStyle);
		addView(R.layout.layout_fanfu_check);
		if (title == null) {
			titleTextView.setText(mContext.getString(R.string.FanFu));
		} else {
			titleTextView.setText(title);
		}
		mMinusBtn = (ImageView) view.findViewById(R.id.fanfu_dora_minus);
		mPlusBtn = (ImageView) view.findViewById(R.id.fanfu_dora_plus);
		mDoraText = (TextView) view.findViewById(R.id.fanfu_dora_count);
		mYifaBox = (CheckBox) view.findViewById(R.id.fanfu_cb_yifa);
		mHaidiBox = (CheckBox) view.findViewById(R.id.fanfu_cb_haidilaoyue);
		mHediBox = (CheckBox) view.findViewById(R.id.fanfu_cb_hedimoyu);
		mErrorText = (TextView) view.findViewById(R.id.fanfu_error);
		
		this.is17step = false;
		this.round_count = roundCount;
		this.fan = fan;
		
		mMinusBtn.setOnClickListener(this);
		mPlusBtn.setOnClickListener(this);
		mYifaBox .setOnCheckedChangeListener(this);
		mHaidiBox .setOnCheckedChangeListener(this);
		mHediBox .setOnCheckedChangeListener(this);
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}
	
	public FanfuDialog(Context context, String title, int fan, int fu, int type) {
		super(context, R.style.MyDialogStyle);
		addView(R.layout.layout_fanfu_check);
		if (title == null) {
			titleTextView.setText(mContext.getString(R.string.FanFu));
		} else {
			titleTextView.setText(title);
		}
		mMinusBtn = (ImageView) view.findViewById(R.id.fanfu_dora_minus);
		mPlusBtn = (ImageView) view.findViewById(R.id.fanfu_dora_plus);
		mDoraTitleText = (TextView) view.findViewById(R.id.fanfu_item_dora);
		mDoraTitleText.setText(R.string.dora_in);
		mDoraText = (TextView) view.findViewById(R.id.fanfu_dora_count);
		mYifaBox = (CheckBox) view.findViewById(R.id.fanfu_cb_yifa);
		mYifaBox.setVisibility(View.GONE);
		mHaidiBox = (CheckBox) view.findViewById(R.id.fanfu_cb_haidilaoyue);
		mHaidiBox.setVisibility(View.GONE);
		mHediBox = (CheckBox) view.findViewById(R.id.fanfu_cb_hedimoyu);
		mHediBox.setVisibility(View.GONE);
		mErrorText = (TextView) view.findViewById(R.id.fanfu_error);
		
		this.is17step = true;
		this.fan = fan;
		this.fu = fu;
		this.type = type;
		
		mMinusBtn.setOnClickListener(this);
		mPlusBtn.setOnClickListener(this);
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.fanfu_dora_minus:
			if (dora_count > 0) {
				dora_count--;
				mDoraText.setText(dora_count + "");
			}
			break;
		case R.id.fanfu_dora_plus:
			dora_count++;
			mDoraText.setText(dora_count + "");
			break;
		case R.id.l_ok:
			if (is17step) {
				if (check17StepFanfu()) {
					if (mListener != null) mListener.onSuccess();
					dismiss();
				}
			} else {
				if (checkFanfu()) {
					if (mListener != null) mListener.onSuccess();
					dismiss();
				}
			}
			break;
		case R.id.l_cancel:
			dismiss();
			break;
		default:
			break;
		}
	}
	
	private boolean check17StepFanfu() {
		int count = dora_count;
		if (fan > 0 && count > fan) {
			mErrorText.setText(R.string.fanfu_wrong);
			mErrorText.setVisibility(View.VISIBLE);
			return false;
		}
		String fanfuText = mContext.getString(R.string.game17s_FanFu_none);
		switch (type) {
		case 0: // 五番番缚
			if (fan < 0) {
				return true;
			} else {
				if ((fan - count) >= 5) {
					return true;
				}
			}
			fanfuText = mContext.getString(R.string.game17s_FanFu_five);
			break;
		case 1: // 满贯番缚
			if (fan < 0) {
				return true;
			} else {
				int validYaku = fan - count;
				if (validYaku >= 5) {
					return true;
				} else if (validYaku == 4 && fu >= 40) {
					return true;
				} else if (validYaku == 3 && fu >= 70) {
					return true;
				}
			}
			fanfuText = mContext.getString(R.string.game17s_FanFu_manguan);
			break;
		case 2: // 无番缚
		default:
			return true;
		}		
		String error = String.format(mContext.getString(R.string.game17s_fanfu_invalid), fanfuText, fan - count); 
		mErrorText.setText(error);
		mErrorText.setVisibility(View.VISIBLE);
		return false;
	}
	
	private boolean checkFanfu() {
		int count = dora_count;
		if (mYifaBox.isChecked()) count++;
		if (mHaidiBox.isChecked()) count++;
		if (mHediBox.isChecked()) count++;
		if (fan > 0 && count > fan) {
			mErrorText.setText(R.string.fanfu_wrong);
			mErrorText.setVisibility(View.VISIBLE);
			return false;
		}
		if (round_count >= 5) {
			int fanfu = (round_count - 5) / 4 + 2;
			if (fan < fanfu || fan - count < fanfu) {
				String error = String.format(mContext.getString(R.string.fanfu_invalid), fanfu, fan - count); 
				mErrorText.setText(error);
				mErrorText.setVisibility(View.VISIBLE);
				return false;
			} 
		}		
		return true;
	}

	@Override
	public void onCheckedChanged(CompoundButton cbox, boolean checked) {
		switch (cbox.getId()) {
		case R.id.fanfu_cb_yifa:			
			break;
		case R.id.fanfu_cb_haidilaoyue:
			if (checked && mHediBox.isChecked()) {
				mHediBox.setChecked(false);
			}
			break;
		case R.id.fanfu_cb_hedimoyu:
			if (checked && mHaidiBox.isChecked()) {
				mHaidiBox.setChecked(false);
			}
			break;			
		default:
			break;
		}
	}
	
	public void setOnFanfuListener(OnFanfuListener listener) {
		mListener = listener;
	}
	
	public interface OnFanfuListener {
		void onSuccess();
	}

}

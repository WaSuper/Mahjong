package com.mahjong.tools;

import com.mahjong.R;
import com.mahjong.common.MjSetting;
import com.mahjong.ui.MahjongBaseView;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class KeyBoardUtil implements OnClickListener {

	private Context mContext;
	private LinearLayout mKeyBoard;
	private ImageButton mBtnHide;
	private ImageButton mBtnLeft;
	private ImageButton mBtnRight;
	private ImageButton mBtnBackSpace;
	
	private ImageButton mBtnMan1, mBtnMan2, mBtnMan3, mBtnMan4, mBtnMan5, 
				mBtnMan6, mBtnMan7, mBtnMan8, mBtnMan9, mBtnMan5r;
	private ImageButton mBtnPin1, mBtnPin2, mBtnPin3, mBtnPin4, mBtnPin5,
				mBtnPin6, mBtnPin7, mBtnPin8, mBtnPin9, mBtnPin5r;
	private ImageButton mBtnBamboo1, mBtnBamboo2, mBtnBamboo3, mBtnBamboo4, mBtnBamboo5,
				mBtnBamboo6, mBtnBamboo7, mBtnBamboo8, mBtnBamboo9, mBtnBamboo5r;
	private ImageButton mBtnWindEast, mBtnWindSouth, mBtnWindWest, mBtnWindNorth;
	private ImageButton mBtnDragonHaku, mBtnDragonGreen, mBtnDragonChun;
	
	private OnKeyBoardListener mListener;
	private MahjongBaseView mMjBaseView;
	
	public KeyBoardUtil(Context context, View view) {
		this.mContext = context;
		this.mKeyBoard = (LinearLayout) view.findViewById(R.id.keyboard);
		initUI();
	}
	
	private void initUI () {
		mBtnHide = (ImageButton) mKeyBoard.findViewById(R.id.keyboard_hide);
		mBtnLeft = (ImageButton) mKeyBoard.findViewById(R.id.keyboard_left);
		mBtnRight = (ImageButton) mKeyBoard.findViewById(R.id.keyboard_right);
		mBtnBackSpace = (ImageButton) mKeyBoard.findViewById(R.id.keyboard_backspace);
		mBtnMan1 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_man1);
		mBtnMan2 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_man2);
		mBtnMan3 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_man3);
		mBtnMan4 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_man4);
		mBtnMan5 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_man5);
		mBtnMan6 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_man6);
		mBtnMan7 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_man7);
		mBtnMan8 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_man8);
		mBtnMan9 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_man9);
		mBtnMan5r =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_man5r);
		mBtnPin1 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_pin1);
		mBtnPin2 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_pin2);
		mBtnPin3 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_pin3);
		mBtnPin4 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_pin4);
		mBtnPin5 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_pin5);
		mBtnPin6 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_pin6);
		mBtnPin7 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_pin7);
		mBtnPin8 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_pin8);
		mBtnPin9 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_pin9);
		mBtnPin5r =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_pin5r);
		mBtnBamboo1 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_bamboo1);
		mBtnBamboo2 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_bamboo2);
		mBtnBamboo3 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_bamboo3);
		mBtnBamboo4 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_bamboo4);
		mBtnBamboo5 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_bamboo5);
		mBtnBamboo6 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_bamboo6);
		mBtnBamboo7 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_bamboo7);
		mBtnBamboo8 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_bamboo8);
		mBtnBamboo9 =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_bamboo9);
		mBtnBamboo5r =  (ImageButton) mKeyBoard.findViewById(R.id.keyboard_bamboo5r);
		mBtnWindEast = (ImageButton) mKeyBoard.findViewById(R.id.keyboard_wind_east);
		mBtnWindSouth = (ImageButton) mKeyBoard.findViewById(R.id.keyboard_wind_south);
		mBtnWindWest = (ImageButton) mKeyBoard.findViewById(R.id.keyboard_wind_west);
		mBtnWindNorth = (ImageButton) mKeyBoard.findViewById(R.id.keyboard_wind_north);
		mBtnDragonHaku = (ImageButton) mKeyBoard.findViewById(R.id.keyboard_dragon_haku);
		mBtnDragonGreen = (ImageButton) mKeyBoard.findViewById(R.id.keyboard_dragon_green);
		mBtnDragonChun = (ImageButton) mKeyBoard.findViewById(R.id.keyboard_dragon_chun);
		
		mBtnHide.setOnClickListener(this);
		mBtnLeft.setOnClickListener(this);
		mBtnRight.setOnClickListener(this);
		mBtnBackSpace.setOnClickListener(this);
		mBtnMan1.setOnClickListener(this);
		mBtnMan2.setOnClickListener(this);
		mBtnMan3.setOnClickListener(this);
		mBtnMan4.setOnClickListener(this);
		mBtnMan5.setOnClickListener(this);
		mBtnMan6.setOnClickListener(this);
		mBtnMan7.setOnClickListener(this);
		mBtnMan8.setOnClickListener(this);
		mBtnMan9.setOnClickListener(this);
		mBtnMan5r.setOnClickListener(this);
		mBtnPin1.setOnClickListener(this);
		mBtnPin2.setOnClickListener(this);
		mBtnPin3.setOnClickListener(this);
		mBtnPin4.setOnClickListener(this);
		mBtnPin5.setOnClickListener(this);
		mBtnPin6.setOnClickListener(this);
		mBtnPin7.setOnClickListener(this);
		mBtnPin8.setOnClickListener(this);
		mBtnPin9.setOnClickListener(this);
		mBtnPin5r.setOnClickListener(this);
		mBtnBamboo1.setOnClickListener(this);
		mBtnBamboo2.setOnClickListener(this);
		mBtnBamboo3.setOnClickListener(this);
		mBtnBamboo4.setOnClickListener(this);
		mBtnBamboo5.setOnClickListener(this);
		mBtnBamboo6.setOnClickListener(this);
		mBtnBamboo7.setOnClickListener(this);
		mBtnBamboo8.setOnClickListener(this);
		mBtnBamboo9.setOnClickListener(this);
		mBtnBamboo5r.setOnClickListener(this);
		mBtnWindEast.setOnClickListener(this);
		mBtnWindSouth.setOnClickListener(this);
		mBtnWindWest.setOnClickListener(this);
		mBtnWindNorth.setOnClickListener(this);
		mBtnDragonHaku.setOnClickListener(this);
		mBtnDragonGreen.setOnClickListener(this);
		mBtnDragonChun.setOnClickListener(this);
	}
	
	public void enableRedDora(boolean enable) {
		if (!enable) {
			mBtnMan5r.setVisibility(View.GONE);
			mBtnPin5r.setVisibility(View.GONE);
			mBtnBamboo5r.setVisibility(View.GONE);
		} else {
			mBtnMan5r.setVisibility(View.VISIBLE);
			mBtnPin5r.setVisibility(View.VISIBLE);
			mBtnBamboo5r.setVisibility(View.VISIBLE);
		}
	}
	
	public void show(MahjongBaseView baseView) {
		this.mMjBaseView = baseView;
		if (!mKeyBoard.isShown()) {
			Animation mShowAnim = AnimationUtils.loadAnimation(mContext, R.anim.up_in);
			mKeyBoard.setVisibility(View.VISIBLE);
			mKeyBoard.setAnimation(mShowAnim);
			if (mListener != null) mListener.onShow(mMjBaseView);
		}	
	}
	
	public void hide() {
		if (mKeyBoard.isShown()) {
			Animation mShowAnim = AnimationUtils.loadAnimation(mContext, R.anim.down_out);
			mKeyBoard.setVisibility(View.GONE);
			mKeyBoard.setAnimation(mShowAnim);
			if (mListener != null) mListener.onHide(mMjBaseView);
		}	
		this.mMjBaseView = null;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.keyboard_hide:
			hide();
			break;
		case R.id.keyboard_left:
			if (mListener != null) mListener.onLeft(mMjBaseView);
			break;
		case R.id.keyboard_right:
			if (mListener != null) mListener.onRight(mMjBaseView);
			break;
		case R.id.keyboard_backspace:
			if (mListener != null) mListener.onBackSpace(mMjBaseView);
			break;
		case R.id.keyboard_man1:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._man1);
			break;
		case R.id.keyboard_man2:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._man2);
			break;
		case R.id.keyboard_man3:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._man3);
			break;
		case R.id.keyboard_man4:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._man4);
			break;
		case R.id.keyboard_man5:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._man5);
			break;
		case R.id.keyboard_man6:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._man6);
			break;
		case R.id.keyboard_man7:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._man7);
			break;
		case R.id.keyboard_man8:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._man8);
			break;
		case R.id.keyboard_man9:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._man9);
			break;
		case R.id.keyboard_man5r:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._man5r);
			break;
		case R.id.keyboard_pin1:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._pin1);
			break;
		case R.id.keyboard_pin2:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._pin2);
			break;
		case R.id.keyboard_pin3:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._pin3);
			break;
		case R.id.keyboard_pin4:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._pin4);
			break;
		case R.id.keyboard_pin5:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._pin5);
			break;
		case R.id.keyboard_pin6:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._pin6);
			break;
		case R.id.keyboard_pin7:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._pin7);
			break;
		case R.id.keyboard_pin8:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._pin8);
			break;
		case R.id.keyboard_pin9:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._pin9);
			break;
		case R.id.keyboard_pin5r:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._pin5r);
			break;
		case R.id.keyboard_bamboo1:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._bamboo1);
			break;
		case R.id.keyboard_bamboo2:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._bamboo2);
			break;
		case R.id.keyboard_bamboo3:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._bamboo3);
			break;
		case R.id.keyboard_bamboo4:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._bamboo4);
			break;
		case R.id.keyboard_bamboo5:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._bamboo5);
			break;
		case R.id.keyboard_bamboo6:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._bamboo6);
			break;
		case R.id.keyboard_bamboo7:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._bamboo7);
			break;
		case R.id.keyboard_bamboo8:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._bamboo8);
			break;
		case R.id.keyboard_bamboo9:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._bamboo9);
			break;
		case R.id.keyboard_bamboo5r:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._bamboo5r);
			break;
		case R.id.keyboard_wind_east:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._wind_east);
			break;
		case R.id.keyboard_wind_south:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._wind_south);
			break;
		case R.id.keyboard_wind_west:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._wind_west);
			break;
		case R.id.keyboard_wind_north:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._wind_north);
			break;
		case R.id.keyboard_dragon_haku:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._dragon_haku);
			break;
		case R.id.keyboard_dragon_green:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._dragon_green);
			break;
		case R.id.keyboard_dragon_chun:
			if (mListener != null) mListener.onClick(mMjBaseView, MjSetting._dragon_chun);
			break;
		default:
			break;
		}
	}
	
	public void setOnKeyBoardListener(OnKeyBoardListener onKeyBoardListener) {
		this.mListener = onKeyBoardListener;
	}
	
	public interface OnKeyBoardListener {
		void onClick(MahjongBaseView view, int key);
		void onBackSpace(MahjongBaseView view);
		void onShow(MahjongBaseView view);
		void onHide(MahjongBaseView view);
		void onLeft(MahjongBaseView view);
		void onRight(MahjongBaseView view);
	}
	
}

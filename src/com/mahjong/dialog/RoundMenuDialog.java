package com.mahjong.dialog;

import com.mahjong.R;
import com.mahjong.ui.RoundMenuView;
import com.mahjong.ui.RoundMenuView.RoundMenu;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;;

public class RoundMenuDialog extends Dialog {

	private Context mContext;
	
	private RoundMenuView mMenuView;
	
	private OnRoundMenuDialogListener mListener;
	private int curDir = 0;
	private RoundMenu firstMenu;
		
	public RoundMenuDialog(Context context) {
		super(context, R.style.MyDialogStyle);
		setContentView(R.layout.dialog_direction_setting);
		this.mContext = context;
		initUI();
	}
	
	private void initUI() {
		int greyColor = mContext.getResources().getColor(R.color.grey);
		int whiteColor = Color.WHITE;
		Bitmap rightBitmap= BitmapFactory.decodeResource(mContext.getResources(), R.drawable.roundview_right);
		Bitmap coreBitmap= BitmapFactory.decodeResource(mContext.getResources(), R.drawable.roundview_ok);
		
		mMenuView = (RoundMenuView) findViewById(R.id.roundmenuview);
		RoundMenuView.RoundMenu roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = greyColor;
        roundMenu.strokeColor = greyColor;
        roundMenu.solidColor = whiteColor;
        roundMenu.icon = rightBitmap;
        roundMenu.onClickListener = new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
//            	ToastTool.showToast(mContext, "下");
            	curDir = 0;
            }
        };
        mMenuView.addRoundMenu(roundMenu);
        firstMenu = roundMenu;
        mMenuView.setCurRoundMenu(firstMenu);
 
        roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = greyColor;
        roundMenu.strokeColor = greyColor;
        roundMenu.solidColor = whiteColor;
        roundMenu.icon = rightBitmap;
        roundMenu.onClickListener = new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
//            	ToastTool.showToast(mContext, "左");
            	curDir = 3;
            }
        };
        mMenuView.addRoundMenu(roundMenu);
 
        roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = greyColor;
        roundMenu.strokeColor = greyColor;
        roundMenu.solidColor = whiteColor;
        roundMenu.icon = rightBitmap;
        roundMenu.onClickListener=new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
//            	ToastTool.showToast(mContext, "上");
            	curDir = 2;
            }
        };
        mMenuView.addRoundMenu(roundMenu);
 
        roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = greyColor;
        roundMenu.strokeColor = greyColor;
        roundMenu.solidColor = whiteColor;
        roundMenu.icon = rightBitmap;
        roundMenu.onClickListener=new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ToastTool.showToast(mContext, "右");
            	  curDir = 1;
            }
        };
        mMenuView.addRoundMenu(roundMenu);
 
        mMenuView.setCoreMenu(whiteColor, greyColor, greyColor
                , 1, 0.43, coreBitmap, new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    	ToastTool.showToast(mContext, "点击了中心圆圈");
                    	if (mListener != null) {
							mListener.onFinish(curDir);
						}
                    	dismiss();
                    }
                });
	}
	
	public void resetDir() {
		curDir = 0;
		mMenuView.setCurRoundMenu(firstMenu);
	}
	
	public void setOnRoundMenuDialogListener(OnRoundMenuDialogListener listener) {
		this.mListener = listener;
	}
	
	public interface OnRoundMenuDialogListener {
		public void onFinish(int dir);
	}
	
}

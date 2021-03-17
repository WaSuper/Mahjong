package com.mahjong.dialog;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mahjong.R;
import com.mahjong.ui.CommonDialog;

public class ProgressDialog extends CommonDialog {

	private ImageView mImgView;
	private ProgressBar mProgressBar;
	private AnimationDrawable animationDrawable;
	
	private int mMaxCount;
	
	public ProgressDialog(Context context) {
		super(context, R.style.MyDialogStyle, true);
		addView(R.layout.layout_progress);
		titleTextView.setText(mContext.getString(R.string.please_wait));
		mImgView = (ImageView) view.findViewById(R.id.progress_img);
		mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		
		animationDrawable = (AnimationDrawable) mImgView.getBackground();
		setCanceledOnTouchOutside(false);
	}
	
	public void setStart(int count) {
		animationDrawable.start();
		mMaxCount = count;
		mProgressBar.setMax(count);
		mProgressBar.setProgress(0);
		show();
	}
	
	public void setProgress(int progress) {
		mProgressBar.setProgress(progress);
		if (progress == mMaxCount) {
			setEnd();
		}
	}
	
	public void setEnd() {
		animationDrawable.stop();
		dismiss();
	}

}

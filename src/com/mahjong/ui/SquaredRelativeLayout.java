package com.mahjong.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquaredRelativeLayout extends RelativeLayout {
	
	
	public SquaredRelativeLayout(Context context) {
		this(context, null);
	}
	
	public SquaredRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SquaredRelativeLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if(width > height) {
             super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        } else {
             super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
    }
}

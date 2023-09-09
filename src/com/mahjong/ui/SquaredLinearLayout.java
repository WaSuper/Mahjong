package com.mahjong.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquaredLinearLayout extends LinearLayout {

	public SquaredLinearLayout(Context context) {
		this(context, null);
	}
	
	public SquaredLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SquaredLinearLayout(Context context, AttributeSet attrs,
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

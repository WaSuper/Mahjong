package com.mahjong.ui;

import com.mahjong.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class RotateTextView extends TextView {

    private int degree;
    private int transX;
    private int transY;

    public RotateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RotateTextView);
        degree = ta.getInteger(R.styleable.RotateTextView_rotateAngle, 0);
        transX = ta.getDimensionPixelSize(R.styleable.RotateTextView_transX, 0);
        transY = ta.getDimensionPixelSize(R.styleable.RotateTextView_transY, 0);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(degree, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        canvas.translate(transX, transY);
        super.onDraw(canvas);
        canvas.restore();
    }
	
}

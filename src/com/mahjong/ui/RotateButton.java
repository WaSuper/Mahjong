package com.mahjong.ui;

import com.mahjong.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

public class RotateButton extends Button {

	private static final int[] textDegree = new int[]{R.attr.rotateAngle, R.attr.transX, R.attr.transY};
    private int degree;
    private int transX;
    private int transY;

    public RotateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, textDegree);
        degree = ta.getInteger(0, 0);
        transX = ta.getDimensionPixelSize(1, 0);
        transY = ta.getDimensionPixelSize(2, 0);
        ta.recycle();
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

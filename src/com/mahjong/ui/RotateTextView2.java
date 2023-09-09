package com.mahjong.ui;

import com.mahjong.R;
import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;

public class RotateTextView2 extends View {

	private static final int left = 3;
	private static final int right = 1;
	private static final int center = 4;
	
	private int angle;
    private int transX;
    private int transY;
    private String text;
    private int textSize;
    private int textColor;
    private int position;
	
	/**
	 * 文字画刷
	 */
	private Paint mTextPaint;
	FontMetrics mFontMetrics;
	float mTextPadding;
	
	public RotateTextView2(Context context) {
		this(context, null);
	}
	
	public RotateTextView2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RotateTextView2(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttrs(context, attrs);
		initPaint();
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RotateTextView2);
        angle = ta.getInteger(R.styleable.RotateTextView2_rotateAngle, 0);
        transX = ta.getDimensionPixelSize(R.styleable.RotateTextView2_transX, 0);
        transY = ta.getDimensionPixelSize(R.styleable.RotateTextView2_transY, 0);
        text = ta.getString(R.styleable.RotateTextView2_exText) != null ? 
        		ta.getString(R.styleable.RotateTextView2_exText) : "default";
        textSize = ta.getInteger(R.styleable.RotateTextView2_exTextSize, 16);
        textColor = ta.getColor(R.styleable.RotateTextView2_exTextColor, Color.WHITE);
        position = ta.getInteger(R.styleable.RotateTextView2_position, center);
        ta.recycle();
	}
	
	private void initPaint() {
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mTextPaint.setStyle(Paint.Style.STROKE);
		mTextPaint.setTextAlign(Paint.Align.RIGHT);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), textSize));
		mTextPaint.setColor(textColor);
		mFontMetrics = mTextPaint.getFontMetrics();	
		mTextPadding = (mFontMetrics.top + mFontMetrics.bottom) / 2;
	}
	
	public void setText(String text) {
		this.text = text == null ? "" : text;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		canvas.rotate(angle, centerX, centerY);
		canvas.translate(transX, transY);
		switch (position) {
		case left:
			switch (angle) {
			case 0:
				mTextPaint.setTextAlign(Paint.Align.LEFT);
				canvas.drawText(text, 0, centerY - mTextPadding, mTextPaint);
				break;
			case 180:
				mTextPaint.setTextAlign(Paint.Align.RIGHT);
				canvas.drawText(text, getWidth(), centerY - mTextPadding, mTextPaint);
				break;
			case 90:
				mTextPaint.setTextAlign(Paint.Align.LEFT);
				canvas.drawText(text, centerX - centerY, centerY - mTextPadding, mTextPaint);
				break;
			case -90:
				mTextPaint.setTextAlign(Paint.Align.RIGHT);
				canvas.drawText(text, centerX + centerY, centerY - mTextPadding, mTextPaint);
				break;
			default:
				break;
			}
			break;
		case right:
			switch (angle) {
			case 0:
				mTextPaint.setTextAlign(Paint.Align.RIGHT);
				canvas.drawText(text, getWidth(), centerY - mTextPadding, mTextPaint);
				break;
			case 180:
				mTextPaint.setTextAlign(Paint.Align.LEFT);
				canvas.drawText(text, 0, centerY - mTextPadding, mTextPaint);
				break;
			case 90:
				mTextPaint.setTextAlign(Paint.Align.RIGHT);
				canvas.drawText(text, centerX + centerY, centerY - mTextPadding, mTextPaint);
				break;
			case -90:
				mTextPaint.setTextAlign(Paint.Align.LEFT);
				canvas.drawText(text, centerX - centerY, centerY - mTextPadding, mTextPaint);
				break;
			default:
				break;
			}
			break;
		case center:
		default:
			mTextPaint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(text, centerX, centerY - mTextPadding, mTextPaint);
			break;
		}
		canvas.translate(-transX, -transY);
		canvas.rotate(-angle, centerX, centerY);
	}
	
}

package com.mahjong.ui;

import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LineChart extends View {

	/**
	 * 基本画刷
	 */
	private Paint mPaint;
	
	private int mBaseScore = -1;
	private int mLen = 0;
	private int[] mScore1st;
	private int[] mScore2nd;
	private int[] mScore3rd;
	private int[] mScore4th;
	private int mMaxScore;
	private int mMinScore;
	
	public LineChart(Context context) {
		this(context, null);
	}
	
	public LineChart(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint();
//		testData();
	}
	
	/**
	 * 初始化画刷
	 */
	private void initPaint() {        
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(2);
		mPaint.setTextAlign(Paint.Align.LEFT);
		mPaint.setTextSize(ValueTool.sp2px(getContext(), 14));
	}
	
	public void setData(int baseScore, int len,
			int[] score1st, int[] score2nd, int[] score3rd, int[] score4th) {
		mBaseScore = baseScore;
		mLen = len;
		mScore1st = score1st;
		mScore2nd = score2nd;
		mScore3rd = score3rd;
		mScore4th = score4th;
		mMaxScore = baseScore + 5000;
		mMinScore = baseScore - 5000;
		for (int i = 0; i < len; i++) {
			if (score1st[i] > mMaxScore) mMaxScore = score1st[i];
			if (score1st[i] < mMinScore) mMinScore = score1st[i];
			if (score2nd[i] > mMaxScore) mMaxScore = score2nd[i];
			if (score2nd[i] < mMinScore) mMinScore = score2nd[i];
			if (score3rd[i] > mMaxScore) mMaxScore = score3rd[i];
			if (score3rd[i] < mMinScore) mMinScore = score3rd[i];
			if (score4th[i] > mMaxScore) mMaxScore = score4th[i];
			if (score4th[i] < mMinScore) mMinScore = score4th[i];
		}
		invalidate();
	}
	
	private void testData() {
		mBaseScore = 25000;
		mLen = 4;
		mScore1st = new int[mLen];
		mScore1st[0] = 25000;
		mScore1st[1] = 23500;
		mScore1st[2] = 22100;
		mScore1st[3] = 54100;
		mScore2nd = new int[mLen];
		mScore2nd[0] = 25000;
		mScore2nd[1] = 25000;
		mScore2nd[2] = 30500;
		mScore2nd[3] = 30500;
		mScore3rd = new int[mLen];
		mScore3rd[0] = 25000;
		mScore3rd[1] = 25000;
		mScore3rd[2] = 23600;
		mScore3rd[3] = 23600;
		mScore4th = new int[mLen];
		mScore4th[0] = 26500;
		mScore4th[1] = 26500;
		mScore4th[2] = 23800;
		mScore4th[3] = 8200;
		mMaxScore = 54100;
		mMinScore = 8200;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBaseScore == -1) return;
		int height = getHeight();
		int width = getWidth();
		int paddingleft = ValueTool.dp2px(getContext(), 10);
		int paddingTop = ValueTool.dp2px(getContext(), 5);
		int paddingBottom = ValueTool.dp2px(getContext(), 20);
		// 画边框
		mPaint.setColor(Color.parseColor("#a9a9a9"));
		canvas.drawLine(paddingleft, paddingTop, paddingleft, height - paddingBottom, mPaint);
		canvas.drawLine(paddingleft, height - paddingBottom, width - paddingleft, height - paddingBottom, mPaint);
		// 画折线
		float yLen = height - paddingTop - paddingBottom;
		double yScore = mMaxScore - mMinScore;
		float xLen = width - paddingleft * 2;
		float xSpace = (float) (xLen / (mLen - 1));
		float startX = paddingleft;
		for (int i = 0; i < mLen - 1; i++) {
			// 1位
			mPaint.setColor(Color.parseColor("#ffb308"));
			float y1 = (float) ((double)(mMaxScore - mScore1st[i]) / yScore * yLen) + paddingTop;
			float y2 = (float) ((double)(mMaxScore - mScore1st[i + 1]) / yScore * yLen) + paddingTop;
			canvas.drawLine(startX, y1, startX + xSpace, y2, mPaint);
			if (i != 0) canvas.drawCircle(startX, y1, 4, mPaint);
			// 2位
			mPaint.setColor(Color.parseColor("#4b648c"));
			y1 = (float) ((double)(mMaxScore - mScore2nd[i]) / yScore * yLen) + paddingTop;
			y2 = (float) ((double)(mMaxScore - mScore2nd[i + 1]) / yScore * yLen) + paddingTop;
			canvas.drawLine(startX, y1, startX + xSpace, y2, mPaint);
			if (i != 0) canvas.drawCircle(startX, y1, 4, mPaint);
			// 3位
			mPaint.setColor(Color.parseColor("#a26628"));
			y1 = (float) ((double)(mMaxScore - mScore3rd[i]) / yScore * yLen) + paddingTop;
			y2 = (float) ((double)(mMaxScore - mScore3rd[i + 1]) / yScore * yLen) + paddingTop;
			canvas.drawLine(startX, y1, startX + xSpace, y2, mPaint);
			if (i != 0) canvas.drawCircle(startX, y1, 4, mPaint);
			// 4位
			mPaint.setColor(Color.parseColor("#cacaca"));
			y1 = (float) ((double)(mMaxScore - mScore4th[i]) / yScore * yLen) + paddingTop;
			y2 = (float) ((double)(mMaxScore - mScore4th[i + 1]) / yScore * yLen) + paddingTop;
			canvas.drawLine(startX, y1, startX + xSpace, y2, mPaint);
			if (i != 0) canvas.drawCircle(startX, y1, 4, mPaint);
			// nextLine
			startX += xSpace;
		}		
		// 画标签
		startX = paddingleft;
		float startY = height - paddingBottom / 3;
		float centerY = height - paddingBottom / 2;
		float padding1 = ValueTool.dp2px(getContext(), 26);
		float padding2 = ValueTool.dp2px(getContext(), 14);
		mPaint.setColor(Color.parseColor("#ffb308"));
		canvas.drawText("1位", startX, startY, mPaint);
		startX += padding1;
		canvas.drawCircle(startX, centerY, 7, mPaint);
		startX += padding2;
		mPaint.setColor(Color.parseColor("#4b648c"));
		canvas.drawText("2位", startX, startY, mPaint);
		startX += padding1;
		canvas.drawCircle(startX, centerY, 7, mPaint);
		startX += padding2;
		mPaint.setColor(Color.parseColor("#a26628"));
		canvas.drawText("3位", startX, startY, mPaint);
		startX += padding1;
		canvas.drawCircle(startX, centerY, 7, mPaint);
		startX += padding2;
		mPaint.setColor(Color.parseColor("#cacaca"));
		canvas.drawText("4位", startX, startY, mPaint);
		startX += padding1;
		canvas.drawCircle(startX, centerY, 7, mPaint);
	}

}

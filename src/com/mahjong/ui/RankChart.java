package com.mahjong.ui;

import java.util.Arrays;

import com.mahjong.R;
import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RankChart extends View {

	/**
	 * 基本画刷
	 */
	private Paint mPaint;
	/**
	 * 虚线画刷
	 */
	private Paint mDashLinePaint;
	/**
	 * 文字画刷
	 */
	private Paint mTextPaint;
	/**
	 * 图片画刷
	 */
	private Paint mBitmapPaint;
	
	private int mMaxRank = 10;
	private int[] mRanks = { 1, 3, 2, 4 };
	private boolean[] mChickens = { false, true, true, false};
	private boolean[] mFlys = { true, false, true, false };
	private int[] mRankColors = { R.color.rank1, R.color.rank2, R.color.rank3, R.color.rank4 };
	private Bitmap mChickenBitmap;
	private Bitmap mFlyBitmap;
	private Bitmap mChickenFlyBitmap;
	
	public RankChart(Context context) {
		this(context, null);
	}
	
	public RankChart(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RankChart(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint();
	}
	
	/**
	 * 初始化画刷
	 */
	private void initPaint() {        
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(8);
		mPaint.setColor(Color.WHITE);
		
		mDashLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mDashLinePaint.setColor(Color.parseColor("#80C5C9CC"));
        // 需要加上这句，否则画不出东西
		mDashLinePaint.setStyle(Paint.Style.STROKE);
		mDashLinePaint.setStrokeWidth(3);
		mDashLinePaint.setPathEffect(new DashPathEffect(new float[] {15, 5}, 0));
		
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStrokeWidth(2);
		mTextPaint.setTextAlign(Paint.Align.LEFT);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 16));
		mTextPaint.setColor(Color.WHITE);
		

		mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mBitmapPaint.setStyle(Paint.Style.FILL);
		mBitmapPaint.setAntiAlias(true);
		
		mChickenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mj_chicken);
		mFlyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mj_fly);
		mChickenFlyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mj_chicken_fly);
	}
	
	public void setData(int[] ranks, boolean[] chickens, boolean[] flys) {
		if (ranks != null) {
			if (ranks.length > mMaxRank) {
				mRanks = new int[mMaxRank];
				mChickens = new boolean[mMaxRank];
				mFlys = new boolean[mMaxRank];
				System.arraycopy(ranks, ranks.length - mMaxRank, mRanks, 0, mMaxRank);
				if (chickens == null || chickens.length != ranks.length) {
					Arrays.fill(mChickens, false);
				} else {
					System.arraycopy(chickens, chickens.length - mMaxRank, mChickens, 0, mMaxRank);
				}
				if (flys == null || flys.length != ranks.length) {
					Arrays.fill(mFlys, false);
				} else {
					System.arraycopy(flys, flys.length - mMaxRank, mFlys, 0, mMaxRank);
				}
			} else {
				mRanks = ranks;
				if (chickens == null || chickens.length != ranks.length) {
					mChickens = new boolean[ranks.length];
					Arrays.fill(mChickens, false);
				} else {
					mChickens = chickens;
				}
				if (flys == null || flys.length != ranks.length) {
					mFlys = new boolean[ranks.length];
					Arrays.fill(mFlys, false);
				} else {
					mFlys = flys;
				}
			}
		}
		invalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		Rect rect = new Rect();
//		mTextPaint.getTextBounds("1", 0, "1".length(), rect);
//		int height = rect.height() * 6;  
//		int height = ValueTool.dp2px(getContext(), 100);
//		setMeasuredDimension(widthMeasureSpec, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getHeight() / 6;
		int left = ValueTool.dp2px(getContext(), 10);
		// 画文字
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		float top = (height * 2 - fontMetrics.top - fontMetrics.bottom) / 2;
		canvas.drawText("一位", left, top, mTextPaint);
		canvas.drawText("二位", left, top + height * 5 / 4, mTextPaint);
		canvas.drawText("三位", left, top + height * 10 / 4, mTextPaint);
		canvas.drawText("四位", left, top + height * 15 / 4, mTextPaint);
		// 画虚线
		int right = getWidth() - left;
		left = left * 6;
		int[] lineY = {height, height * 9 / 4, height * 7 / 2, height * 19 / 4};
		Path linePath = new Path();
		linePath.moveTo(left, lineY[0]);
		linePath.lineTo(right, lineY[0]);
		canvas.drawPath(linePath, mDashLinePaint);
		linePath.reset();
		linePath.moveTo(left, lineY[1]);
		linePath.lineTo(right, lineY[1]);
		canvas.drawPath(linePath, mDashLinePaint);
		linePath.reset();
		linePath.moveTo(left, lineY[2]);
		linePath.lineTo(right, lineY[2]);
		canvas.drawPath(linePath, mDashLinePaint);
		linePath.reset();
		linePath.moveTo(left, lineY[3]);
		linePath.lineTo(right, lineY[3]);
		canvas.drawPath(linePath, mDashLinePaint);
		// 画排位
		int rankWidth = right - left;
		int pointWidth = rankWidth / 10;
		left = left + pointWidth / 2;
		int len = ValueTool.dp2px(getContext(), 12);
		for (int i = 0; i < mRanks.length; i++) {
			if (mRanks[i] > 0 && mRanks[i] < 5) {
				if (i > 0 && mRanks[i - 1] > 0 && mRanks[i - 1] < 5) {
					mPaint.setColor(Color.parseColor("#80C5C9CC"));
					canvas.drawLine(left - pointWidth, lineY[mRanks[i - 1] - 1], 
							left, lineY[mRanks[i] - 1], mPaint);
				}
				RectF bitmapRectF = new RectF(left - len, lineY[mRanks[i] - 1] - len, 
						left + len, lineY[mRanks[i] - 1] + len);
				if (!mChickens[i] && !mFlys[i]) {
					mPaint.setColor(getResources().getColor(mRankColors[mRanks[i] - 1]));
					canvas.drawCircle(left, lineY[mRanks[i] - 1], len / 2, mPaint);
				} else if (mChickens[i] && mFlys[i]) {
					canvas.drawBitmap(mChickenFlyBitmap, null, bitmapRectF, mBitmapPaint);
				} else if (mChickens[i]) {
					canvas.drawBitmap(mChickenBitmap, null, bitmapRectF, mBitmapPaint);
				}  else if (mFlys[i]) {
					canvas.drawBitmap(mFlyBitmap, null, bitmapRectF, mBitmapPaint);
				} 
				
			}				
			left += pointWidth;
		}
	}

}

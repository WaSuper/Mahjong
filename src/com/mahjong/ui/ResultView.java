package com.mahjong.ui;

import com.mahjong.R;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ResultView extends View {

	public static final int POS_BOTTOM 	= 0;
	public static final int POS_RIGHT 	= 1;
	public static final int POS_TOP 	= 2;
	public static final int POS_LEFT 	= 3;
	
	/**
	 * 基本画刷
	 */
	private Paint mPaint;
	/**
	 * 文字画刷
	 */
	private Paint mTextPaint;
	
	private int mBackgroundId = R.drawable.bg_result_change; // 背景图片
	private int mPosition; // 方位
	private float mRotateAngle; // 旋转角度
	private int mOrgPoint = 25000; // 原始分值
	private int mChangePoint = 0; // 变化分值
	private int mRank = 1; // 排位
	private int mLizhiPoint = 0; // 立直分值
	private int mRoundPoint = 0; // 本场分值
	
	public ResultView(Context context) {
		this(context, null);
	}
	
	public ResultView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ResultView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ResultView);
		mPosition = ta.getInt(R.styleable.ResultView_position, 0);
        ta.recycle();
		initPaint();
	}

	/**
	 * 初始化画刷
	 */
	private void initPaint() {        
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mPaint.setStyle(Paint.Style.FILL);
		
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTextAlign(Paint.Align.LEFT);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 18));
	}

	public void setData(int orgPoint, int changePoint, int rank,
			int lizhi, int round) {
		this.mOrgPoint = orgPoint;
		this.mChangePoint = changePoint;
		this.mRank = rank;
		this.mLizhiPoint = lizhi;
		this.mRoundPoint = round;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int w, h;
		if (getWidth() > getHeight()) {
			w = getWidth();
			h = getHeight();
		} else {
			w = getHeight();
			h = getWidth();
		}
		int marginTop = ValueTool.dp2px(getContext(), 42);
		int marginLeft = ValueTool.dp2px(getContext(), 22);
		int marginBottom = ValueTool.dp2px(getContext(), 12);
		int marginLeft2 = ValueTool.dp2px(getContext(), 25);
		int marginTop2 = ValueTool.dp2px(getContext(), 48);
		int marginRight = ValueTool.dp2px(getContext(), 40);
		int marginLeft4 = ValueTool.dp2px(getContext(), 10);
		int marginTop4 = ValueTool.dp2px(getContext(), 20);
		float left = 0, top = 0;
		float left2 = 0, top2 = 0;
		float left3 = 0, top3 = 0;
		float left4 = 0, top4 = 0;
		switch (mPosition) {
		case POS_TOP:
			mRotateAngle = 180;
			left = getWidth() - marginLeft;
			top = getHeight() - marginTop;
			left2  = getWidth() - marginLeft2;
			top2 = marginBottom;
			left3  = marginRight;
			top3 = getHeight() - marginTop2;
			left4 = getWidth() - marginLeft4;
			top4 = getHeight() - marginTop4;
			break;
		case POS_LEFT:
			mRotateAngle = 90;
			left = getWidth() - marginTop;
			top = marginLeft;
			left2 = marginBottom;
			top2 = marginLeft2;
			left3 = getWidth() - marginTop2;
			top3 = getHeight() - marginRight;
			left4 = getWidth() - marginTop4;
			top4 = marginLeft4;
			break;
		case POS_BOTTOM:
			mRotateAngle = 0;
			left = marginLeft;
			top = marginTop;
			left2 = marginLeft2;
			top2 = getHeight() - marginBottom;
			left3 = getWidth() - marginRight;
			top3 = marginTop2;
			left4 = marginLeft4;
			top4 = marginTop4;
			break;
		case POS_RIGHT:
			mRotateAngle = -90;
			left = marginTop;
			top = getHeight() - marginLeft;
			left2 = getWidth() - marginBottom;
			top2 = getHeight() - marginLeft2;
			left3 = marginTop2;
			top3 = marginRight;
			left4 = marginTop4;
			top4 = getHeight() - marginLeft4;
			break;
		default:
			break;
		}	
		// 画背景	
		Bitmap mBgBitmap = ImageTool.resizeBitmap(
				BitmapFactory.decodeResource(getResources(), mBackgroundId),
				w, h);
		mBgBitmap = ImageTool.rotateTo(mRotateAngle, mBgBitmap);	
		canvas.drawBitmap(mBgBitmap, (getWidth() - mBgBitmap.getWidth()) / 2, 
				(getHeight() - mBgBitmap.getHeight()) / 2, mPaint);
		// 画原本分值
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 18));
		canvas.rotate(mRotateAngle, left, top);
		mTextPaint.setColor(Color.WHITE);
		canvas.drawText(mOrgPoint + "", left, top, mTextPaint);		
		canvas.rotate(-mRotateAngle, left, top);
		// 画变化分值
		float changeAngle = 5;
		canvas.rotate(mRotateAngle - changeAngle, left2, top2);
		String changeText;
		if (mChangePoint > 0) {
			mTextPaint.setColor(Color.parseColor("#00BFFF"));
			changeText = "+" + mChangePoint;
		} else if (mChangePoint < 0) {
			mTextPaint.setColor(Color.RED);
			changeText = "-" + (-mChangePoint);
		} else {
			mTextPaint.setColor(Color.WHITE);
			changeText = "±0";
		}
		canvas.drawText(changeText, left2, top2, mTextPaint);		
		canvas.rotate(-mRotateAngle + changeAngle, left2, top2);
		// 画排位
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 24));
		canvas.rotate(mRotateAngle, left3, top3);
		mTextPaint.setColor(Color.YELLOW);
		canvas.drawText(mRank + "", left3, top3, mTextPaint);		
		canvas.rotate(-mRotateAngle, left3, top3);
		// 画立直分值、本场分值
		if (mLizhiPoint != 0 || mRoundPoint != 0) {
			mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 12));
			StringBuffer sBuffer = new StringBuffer();
			if (mLizhiPoint != 0) {
				sBuffer.append(getResources().getString(R.string.lizhi) + "：");
				sBuffer.append(mLizhiPoint + "   ");
			}
			if (mRoundPoint != 0) {
				sBuffer.append(getResources().getString(R.string.round) + "：");
				sBuffer.append(mRoundPoint);
			}			
			canvas.rotate(mRotateAngle, left4, top4);
			mTextPaint.setColor(Color.WHITE);
			canvas.drawText(sBuffer.toString(), left4, top4, mTextPaint);		
			canvas.rotate(-mRotateAngle, left4, top4);
		}		
	}
	
}

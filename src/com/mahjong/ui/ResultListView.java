package com.mahjong.ui;

import com.mahjong.R;
import com.mahjong.item.ResultList;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class ResultListView extends View {

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
	private TextPaint mTextPaint;
	
	private int mBackgroundId = R.drawable.star_bg; // 背景图片
	private int mPosition; // 方位
	private float mRotateAngle; // 旋转角度
	private ResultList mResultList; // 分数清单
	
	public ResultListView(Context context) {
		this(context, null);
	}
	
	public ResultListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ResultListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ResultListView);
		mPosition = ta.getInt(R.styleable.ResultListView_position, 0);
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
		
		mTextPaint = new TextPaint();
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 12));
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextAlign(Paint.Align.LEFT);
	}

	public void setData(ResultList resultList) {
		this.mResultList = resultList;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mResultList != null && mResultList.size() > 0) {
			int w, h;
			if (getWidth() >= getHeight()) {
				w = getWidth();
				h = getHeight();
			} else {
				w = getHeight();
				h = getWidth();
			}
			int marginTop = ValueTool.dp2px(getContext(), 50);
			int marginLeft = ValueTool.dp2px(getContext(), 20);
			float left = 0, top = 0;
			switch (mPosition) {
			case POS_TOP:
				mRotateAngle = 180;
				left = getWidth() - marginLeft;
				top = getHeight() - marginTop;
				break;
			case POS_LEFT:
				mRotateAngle = 90;
				left = getWidth() - marginTop;
				top = marginLeft;
				break;
			case POS_BOTTOM:
				mRotateAngle = 0;
				left = marginLeft;
				top = marginTop;
				break;
			case POS_RIGHT:
				mRotateAngle = -90;
				left = marginTop;
				top = getHeight() - marginLeft;
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
			// 画分数清单		
			float changeAngle = 25;
			canvas.save();
			canvas.rotate(mRotateAngle - changeAngle, left, top);
			canvas.translate(left, top);
			StaticLayout slayout = new StaticLayout(mResultList.getResultString(getContext()), 
					mTextPaint, 300, Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
			slayout.draw(canvas);
			canvas.restore();
		}
		
	}
	
}

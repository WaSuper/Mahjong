package com.mahjong.ui;

import com.mahjong.R;
import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MjDiceResultView extends View {

	private Context mContext;
	
	/**
	 * 基本画刷
	 */
	private Paint mPaint;
	/**
	 * 牌谱画刷
	 */
	private Paint mBitmapPaint;
	/**
	 * 骰子合计点数
	 */
	private int mPoint = 0;
	/**
	 * 4个麻将图片
	 */
	private Bitmap mImgBackH;
	private Bitmap mImgFaceH;
	private Bitmap mImgBackV;
	private Bitmap mImgFaceV;
	/**
	 * 旋转角度
	 */
	private int angle = 0;
	/**
	 * 发生位置变化
	 */
	private boolean isAction = false;		
	/**
	 * 是否使用三麻模式
	 * 三麻模式下，只使用3个方位，数量18*2*3=108
	 * 非三麻模式下，使用4个方位，数量17*2*4=136
	 */
	private boolean isMj3P = false;
	
	public MjDiceResultView(Context context) {
		this(context, null);
	}
	
	public MjDiceResultView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MjDiceResultView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		initPaint();
	}
	
	private void initPaint() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setColor(Color.parseColor("#ffffff"));
		mPaint.setTextSize(ValueTool.sp2px(mContext, 16));
		
		mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mBitmapPaint.setStyle(Paint.Style.FILL);
		mBitmapPaint.setTextAlign(Paint.Align.CENTER);	
		
		mImgBackH = BitmapFactory.decodeResource(getResources(), R.drawable.tile_h_back);
		mImgFaceH = BitmapFactory.decodeResource(getResources(), R.drawable.tile_h_face);
		mImgBackV = BitmapFactory.decodeResource(getResources(), R.drawable.tile_v_back);
		mImgFaceV = BitmapFactory.decodeResource(getResources(), R.drawable.tile_v_face);
	}
	
	/**
	 * 根据庄家设置旋转角度
	 * 
	 * @param angle 0：下方，-90：右方，180：上方，90：左方
	 */
	public void setAngle(int angle) {
		this.angle = angle;
	}
	
	public void setPoint(int point) {
		this.mPoint = point;
		isAction = false;
		invalidate();
	}
	
	public void showAction() {
		isAction = true;
		invalidate();
	}
	
	public void setMahjng3P(boolean is3P) {
		isMj3P = is3P;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
        canvas.rotate(angle, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
		super.onDraw(canvas);
		if (mPoint < 2 || mPoint > 12) return;
		int len = Math.min(getWidth(), getHeight());
		boolean isLandscape = getWidth() > getHeight();
		float hHeight = (float)len / 20;
		float hWidth = hHeight * 50 / 42;
		float hPadding = hHeight * 32 / 42;
		float vWidth = (float)len / 20;
		float vHeight = vWidth * 54 / 38;
		float vPadding = vWidth * 16 / 54;
		int maxPoint = isMj3P ? 18 : 17;
		if (isMj3P) { // 三麻模式
			switch (angle) {
			case 0: // 东风位
				if (mPoint == 1 || mPoint == 4 || mPoint == 7 || mPoint == 10) { // 下方
					onDrawBottom(canvas, len, isLandscape, maxPoint,
							hWidth, hHeight, hPadding, 
							vWidth, vHeight, vPadding);
				} else if (mPoint == 2 || mPoint == 5 || mPoint == 8 || mPoint == 11) { // 右方
					onDrawRight(canvas, len, isLandscape, maxPoint,
							hWidth, hHeight, hPadding, 
							vWidth, vHeight, vPadding);
				} else if (mPoint == 3 || mPoint == 6 || mPoint == 9 || mPoint == 12) { // 上方
					onDrawTop(canvas, len, isLandscape, maxPoint,
							hWidth, hHeight, hPadding, 
							vWidth, vHeight, vPadding);
				}
				break;
			case -90: // 南风位
				if (mPoint == 1 || mPoint == 4 || mPoint == 7 || mPoint == 10) { // 下方
					onDrawBottom(canvas, len, isLandscape, maxPoint,
							hWidth, hHeight, hPadding, 
							vWidth, vHeight, vPadding);
				} else if (mPoint == 2 || mPoint == 5 || mPoint == 8 || mPoint == 11) { // 右方
					onDrawRight(canvas, len, isLandscape, maxPoint,
							hWidth, hHeight, hPadding, 
							vWidth, vHeight, vPadding);
				} else if (mPoint == 3 || mPoint == 6 || mPoint == 9 || mPoint == 12) { // 左方
					onDrawLeft(canvas, len, isLandscape, maxPoint,
							hWidth, hHeight, hPadding, 
							vWidth, vHeight, vPadding);
				}
				break;
			case 180: // 西风位
				if (mPoint == 1 || mPoint == 4 || mPoint == 7 || mPoint == 10) { // 下方
					onDrawBottom(canvas, len, isLandscape, maxPoint,
							hWidth, hHeight, hPadding, 
							vWidth, vHeight, vPadding);
				} else if (mPoint == 2 || mPoint == 5 || mPoint == 8 || mPoint == 11) { // 上方
					onDrawTop(canvas, len, isLandscape, maxPoint,
							hWidth, hHeight, hPadding, 
							vWidth, vHeight, vPadding);
				} else if (mPoint == 3 || mPoint == 6 || mPoint == 9 || mPoint == 12) { // 左方
					onDrawLeft(canvas, len, isLandscape, maxPoint,
							hWidth, hHeight, hPadding, 
							vWidth, vHeight, vPadding);
				}
				break;
			default:
				break;
			}
		} else { // 四麻模式
			if (mPoint == 2 || mPoint == 6 || mPoint == 10) { // 右方
				onDrawRight(canvas, len, isLandscape, maxPoint,
						hWidth, hHeight, hPadding, 
						vWidth, vHeight, vPadding);
			} else if (mPoint == 3 || mPoint == 7 || mPoint == 11) { // 上方
				onDrawTop(canvas, len, isLandscape, maxPoint,
						hWidth, hHeight, hPadding, 
						vWidth, vHeight, vPadding);
			} else if (mPoint == 4 || mPoint == 8 || mPoint == 12) { // 左方
				onDrawLeft(canvas, len, isLandscape, maxPoint,
						hWidth, hHeight, hPadding, 
						vWidth, vHeight, vPadding);
			} else if (mPoint == 1 || mPoint == 5 || mPoint == 9) { // 下方
				onDrawBottom(canvas, len, isLandscape, maxPoint,
						hWidth, hHeight, hPadding, 
						vWidth, vHeight, vPadding);
			}
		}
		canvas.restore();
	}

	private void onDrawRight(Canvas canvas, int len, 
			boolean isLandscape, int maxPoint,
			float hWidth, float hHeight, float hPadding,
			float vWidth, float vHeight, float vPadding) {
		float right;
		float top;
		if (isLandscape) {
			top = ValueTool.dp2px(mContext, 20);
			right = (getWidth() + len) / 2 - hWidth;
		} else {
			right = getWidth() - ValueTool.dp2px(mContext, 20);
			top = (getHeight() - len) / 2 + hHeight * 3;
		}
		if (isAction) canvas.drawText(mPoint + "", right - hWidth * 3 / 2, top + hHeight, mPaint);
		for (int i = 0; i < mPoint; i++) {
			if (isAction && i == mPoint - 1) { // 添加叠加效果
				canvas.drawBitmap(mImgBackH, null, 
						new RectF(right - hWidth, top + hHeight * 12 / 42, 
								right, top + hHeight * 54 / 42), mBitmapPaint);
			}
			if (isAction && i == mPoint - 3) { // 显示宝牌指示牌
				canvas.drawBitmap(mImgFaceH, null, 
						new RectF(right - hWidth, top, right, top + hHeight), mBitmapPaint);
			} else {
				canvas.drawBitmap(mImgBackH, null, 
						new RectF(right - hWidth, top, right, top + hHeight), mBitmapPaint);
			}				
			top += hPadding;
		}
		if (isAction) top += hPadding * 3 / 2;
		for (int i = mPoint; i < maxPoint; i++) {
			if (i == maxPoint - 1) { // 添加叠加效果
				canvas.drawBitmap(mImgBackH, null, 
						new RectF(right - hWidth, top + hHeight * 12 / 42, 
								right, top + hHeight * 54 / 42), mBitmapPaint);
			}
			canvas.drawBitmap(mImgBackH, null, 
					new RectF(right - hWidth, top, right, top + hHeight), mBitmapPaint);
			top += hPadding;
		}
		if (isAction) canvas.drawText(maxPoint - mPoint + "", right - hWidth * 3 / 2, top, mPaint);
		if (mPoint == 2) {
			if (isMj3P && angle == -90) {
				float left;
				if (isLandscape) {
					top = ValueTool.dp2px(mContext, 20);
					left = getWidth() - right;
				} else {
					left = ValueTool.dp2px(mContext, 20);
					top = (getHeight() - len) / 2 + hHeight * 3;
				}
				top += hHeight * 12 / 42;
				canvas.drawBitmap(mImgBackH, null, 
						new RectF(left, top, left + hWidth, top + hHeight), mBitmapPaint);
				top -= hHeight * 12 / 42;
				if (isAction) {
					canvas.drawBitmap(mImgFaceH, null, 
							new RectF(left, top, left + hWidth, top + hHeight), mBitmapPaint);
				} else {
					canvas.drawBitmap(mImgBackH, null, 
							new RectF(left, top, left + hWidth, top + hHeight), mBitmapPaint);
				}
			} else {
				if (isLandscape) {
					top = ValueTool.dp2px(mContext, 10);
					right = (getWidth() + len) / 2 - hWidth * 3;
				} else {
					right = getWidth() - ValueTool.dp2px(mContext, 20) - hWidth - vWidth;
					top = (getHeight() - len) / 2 + hHeight * 70 / 54;
				}
				canvas.drawBitmap(mImgBackV, null, 
						new RectF(right - vWidth, top, right, top + vHeight), mBitmapPaint);
				top -= hHeight * 16 / 54;
				if (isAction) {
					canvas.drawBitmap(mImgFaceV, null, 
							new RectF(right - vWidth, top, right, top + vHeight), mBitmapPaint);
				} else {
					canvas.drawBitmap(mImgBackV, null, 
							new RectF(right - vWidth, top, right, top + vHeight), mBitmapPaint);
				}
			}
		}
	}
	
	private void onDrawTop(Canvas canvas, int len, 
			boolean isLandscape, int maxPoint,
			float hWidth, float hHeight, float hPadding,
			float vWidth, float vHeight, float vPadding) {
		float left;
		float top;
		if (isLandscape) {
			top = ValueTool.dp2px(mContext, 20);
			left = (getWidth() - len) / 2 + vWidth;
		} else {
			left = vWidth;
			top = (getHeight() - len) / 2 + hHeight;
		}
		if (isAction) canvas.drawText(mPoint + "", left + vWidth / 2, top + vHeight * 2, mPaint);
		for (int i = 0; i < mPoint; i++) {
			top += vPadding; 
			// 绘制底层叠放
			canvas.drawBitmap(mImgBackV, null, 
					new RectF(left, top, left + vWidth, top + vHeight), mBitmapPaint);
			top -= vPadding;
			if (isAction && i == mPoint - 3) { // 显示宝牌指示牌
				canvas.drawBitmap(mImgFaceV, null, 
						new RectF(left, top, left + vWidth, top + vHeight), mBitmapPaint);
			} else {
				canvas.drawBitmap(mImgBackV, null, 
						new RectF(left, top, left + vWidth, top + vHeight), mBitmapPaint);
			}				
			left += vWidth;
		}
		if (isAction) left += vWidth;
		for (int i = mPoint; i < maxPoint; i++) {
			top += vPadding; 
			// 绘制底层叠放
			canvas.drawBitmap(mImgBackV, null, 
					new RectF(left, top, left + vWidth, top + vHeight), mBitmapPaint);
			top -= vPadding;
			canvas.drawBitmap(mImgBackV, null, 
					new RectF(left, top, left + vWidth, top + vHeight), mBitmapPaint);
			left += vWidth;
		}
		if (isAction) canvas.drawText(maxPoint - mPoint + "", left - vWidth / 2, top + vHeight * 2, mPaint);
		if (mPoint == 2) {
			if (isMj3P && angle == 180) {
				if (isLandscape) {
					top = ValueTool.dp2px(mContext, 20) + vHeight * 2;
					left = (getWidth() - len) / 2;
				} else {
					left = ValueTool.dp2px(mContext, 10);
					top = (getHeight() - len) / 2 + hHeight + vHeight * 2;
				}
				top += hHeight * 12 / 42;
				canvas.drawBitmap(mImgBackH, null, 
						new RectF(left, top, left + hWidth, top + hHeight), mBitmapPaint);
				top -= hHeight * 12 / 42;
				if (isAction) {
					canvas.drawBitmap(mImgFaceH, null, 
							new RectF(left, top, left + hWidth, top + hHeight), mBitmapPaint);
				} else {
					canvas.drawBitmap(mImgBackH, null, 
							new RectF(left, top, left + hWidth, top + hHeight), mBitmapPaint);
				}
			}
		}
	}
	
	private void onDrawLeft(Canvas canvas, int len, 
			boolean isLandscape, int maxPoint,
			float hWidth, float hHeight, float hPadding,
			float vWidth, float vHeight, float vPadding) {
		float left;
		float top;
		if (isLandscape) {
			top = ValueTool.dp2px(mContext, 20);
			left = (getWidth() - len) / 2 + hWidth;
		} else {
			left = ValueTool.dp2px(mContext, 20);
			top = (getHeight() - len) / 2 + hHeight * 3;
		}
		if (isAction) canvas.drawText(maxPoint - mPoint + "", left + hWidth * 3 / 2, top + hHeight, mPaint);
		if (!isAction) top += hPadding;
		for (int i = mPoint; i < maxPoint; i++) {
			if (isAction && i == maxPoint - 1) { // 添加叠加效果
				canvas.drawBitmap(mImgBackH, null, 
						new RectF(left, top + hHeight * 12 / 42, 
								left + hWidth, top + hHeight * 54 / 42), mBitmapPaint);
			}
			canvas.drawBitmap(mImgBackH, null, 
					new RectF(left, top, left + hWidth, top + hHeight), mBitmapPaint);				
			top += hPadding;
		}
		if (isAction) top += hPadding * 3 / 2;
		for (int i = 0; i < mPoint; i++) {
			if (i == mPoint - 1) { // 添加叠加效果
				canvas.drawBitmap(mImgBackH, null, 
						new RectF(left, top + hHeight * 12 / 42, 
								left + hWidth, top + hHeight * 54 / 42), mBitmapPaint);
			}
			if (isAction && i == 2) { // 显示宝牌指示牌
				canvas.drawBitmap(mImgFaceH, null, 
						new RectF(left, top, left + hWidth, top + hHeight), mBitmapPaint);
			} else {
				canvas.drawBitmap(mImgBackH, null, 
						new RectF(left, top, left + hWidth, top + hHeight), mBitmapPaint);
			}				
			top += hPadding;
		}
		if (isAction) canvas.drawText(mPoint + "", left + hWidth * 3 / 2, top, mPaint);
	}
	
	private void onDrawBottom(Canvas canvas, int len, 
			boolean isLandscape, int maxPoint,
			float hWidth, float hHeight, float hPadding,
			float vWidth, float vHeight, float vPadding) {
		float right;
		float bottom;
		if (isLandscape) {
			bottom = getHeight() - ValueTool.dp2px(mContext, 20);
			right = (getWidth() + len) / 2 - vWidth;
		} else {
			right = getWidth() - vWidth;
			bottom = (getHeight() + len) / 2 - hHeight;
		}
		if (isAction) canvas.drawText(mPoint + "", right - vWidth / 2, bottom - vHeight * 3 / 2, mPaint);
		for (int i = 0; i < mPoint; i++) {
			// 绘制底层叠放
			canvas.drawBitmap(mImgBackV, null, 
					new RectF(right - vWidth, bottom - vHeight, right, bottom), mBitmapPaint);
			bottom -= vPadding;
			if (isAction && i == mPoint - 3) { // 显示宝牌指示牌
				canvas.drawBitmap(mImgFaceV, null, 
						new RectF(right - vWidth, bottom - vHeight, right, bottom), mBitmapPaint);
			} else {
				canvas.drawBitmap(mImgBackV, null, 
						new RectF(right - vWidth, bottom - vHeight, right, bottom), mBitmapPaint);
			}				
			bottom += vPadding; 
			right -= vWidth;
		}
		if (isAction) right -= vWidth;
		for (int i = mPoint; i < maxPoint; i++) {
			// 绘制底层叠放
			canvas.drawBitmap(mImgBackV, null, 
					new RectF(right - vWidth, bottom - vHeight, right, bottom), mBitmapPaint);
			bottom -= vPadding;
			canvas.drawBitmap(mImgBackV, null, 
					new RectF(right - vWidth, bottom - vHeight, right, bottom), mBitmapPaint);
			bottom += vPadding; 
			right -= vWidth;
		}			
		if (isAction) canvas.drawText(maxPoint - mPoint + "", right + vWidth / 2, bottom - vHeight * 3 / 2 , mPaint);
	}
	
}

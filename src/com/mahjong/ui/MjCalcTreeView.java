package com.mahjong.ui;

import com.mahjong.R;
import com.mahjong.data.jpn.Score;
import com.mahjong.data.jpn.ScoreSystem;
import com.mahjong.data.jpn.YakuValue;
import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class MjCalcTreeView extends View {

	private int Color_Blue = Color.parseColor("#6eafff");
	private int Color_Black = Color.parseColor("#283343");
	
	/**
	 * 基本画刷
	 */
	private Paint mPaint;
	/**
	 * 文字画刷
	 */
	private Paint mTextPaint;
	
	/**
	 *  显示数据
	 */
	private Score mScore;
	private int mRoundCount;
	private int mLizhiCount;
	private boolean isDealer;
	private boolean isZiMo;	
	int mMaxCount = 0;			// 总番数条目
	int mInvalidateCount = 0;	// 当前刷新的番数条目（实现逐个显示）

	float dp2, dp3, dp4, dp5, dp10;
	float sin30, cos30, sin45, cos45;
	
	public MjCalcTreeView(Context context) {
		this(context, null);
	}
	
	public MjCalcTreeView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MjCalcTreeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint();
		initData();
	}

	/**
	 * 初始化画刷
	 */
	private void initPaint() {        
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mPaint.setStyle(Paint.Style.STROKE);
		
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setColor(Color.WHITE);
	}
	
	private void initData() {
		dp2 = ValueTool.dp2px(getContext(), 2);
		dp3 = ValueTool.dp2px(getContext(), 3);
		dp4 = ValueTool.dp2px(getContext(), 4);
		dp5 = ValueTool.dp2px(getContext(), 5);
		dp10 = ValueTool.dp2px(getContext(), 10);
		sin30 = (float) Math.sin(30 * Math.PI / 180);
		cos30 = (float) Math.cos(30 * Math.PI / 180);
		sin45 = (float) Math.sin(45 * Math.PI / 180);
		cos45 = sin45;		
	}
	
	public void setData(Score score, int roundCount, int lizhiCount,
			boolean isDealer, boolean isZiMo) {
		this.mScore = score;
		this.mRoundCount = roundCount;
		this.mLizhiCount = lizhiCount;
		this.isDealer = isDealer;
		this.isZiMo = isZiMo;
		this.mMaxCount = 0;
		if (mScore != null && mScore.hasYaku()) {
			mMaxCount = mScore.YakuValues().length;
		}
		this.mInvalidateCount = 0;
		this.requestLayout();
		this.invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = (int) (dp10 * 12 + width / 5);
		int extra = 0;
		if (mMaxCount > 3) {
			int count = (int) Math.ceil((double)mMaxCount / 3) - 1;
			float len = dp10 * 5 +  width / 12;
			extra = (int) (count * len);
			if (mMaxCount % 3 == 1) {
				extra -= (dp10 +  width / 12);
			}
		}
		height += extra;
		setMeasuredDimension(width, height); 
//		System.out.println("MjCalcTreeView onMeasure!!!!!!!");
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth() - getPaddingLeft() - getPaddingRight();
		float fanRadius = (float) width / 10;
		float yakuRadius = (float) width / 24;
		
		float centerX = dp10 * 3 + fanRadius;
		float centerY = dp10 * 3 + fanRadius;
		// 绘制番数
		onDrawFan(canvas, centerX, centerY, fanRadius);
		// 绘制符数
		onDrawFu(canvas, centerX, centerY, fanRadius);
		// 绘制结果文字
		onDrawResultText(canvas, centerX, centerY, fanRadius);		
		// 绘制役种
		if (mMaxCount > 0) {
			onDrawYaku(canvas, centerX, centerY, fanRadius, yakuRadius);
		}
	}
	
	/**
	 * 画番数部分
	 * 
	 * @param canvas
	 * @param centerX
	 * @param centerY
	 * @param fanRadius
	 */
	private void onDrawFan(Canvas canvas, float centerX, float centerY, float fanRadius) {
		// 先画内圆
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color_Blue);
		mPaint.setStrokeWidth(dp4);
		canvas.drawCircle(centerX, centerY, fanRadius, mPaint);
		// 再画外弧
		mPaint.setColor(Color_Black);
		mPaint.setStrokeWidth(dp2);
		canvas.drawArc(centerX - fanRadius - dp10, centerY - fanRadius - dp10, 
				centerX + fanRadius + dp10, centerY + fanRadius + dp10, 
				-60f, 210f, false, mPaint);
		// 画右上小黑点
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeWidth(dp2);
		canvas.drawCircle(centerX + (fanRadius + dp10) * sin30, 
				centerY - (fanRadius + dp10) * cos30, 
				dp4, mPaint);
		// 画左下小黑点
		canvas.drawCircle(centerX - (fanRadius + dp10) * cos30, 
				centerY + (fanRadius + dp10) * sin30, 
				dp4, mPaint);
        // 画文字
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 20));
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float baseLineY = (centerY * 2 - fontMetrics.top - fontMetrics.bottom) / 2;
        String text = "X";
        if (mScore != null && mScore.hasYaku()) {
			text = mScore.toFanString();
		}
		canvas.drawText(text, centerX, baseLineY, mTextPaint);
	}
	
	/**
	 * 画符数部分
	 * 
	 * @param canvas
	 * @param centerX
	 * @param centerY
	 * @param fanRadius
	 */
	private void onDrawFu(Canvas canvas, float centerX, float centerY, float fanRadius) {
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color_Black);
		mPaint.setStrokeWidth(dp2);
		float tmpW = (fanRadius + dp10) * sin30;
		float tmpH = (fanRadius + dp10) * cos30;
		float tmpLen = dp10 * 1.5f;
		// 画连线：点线点
		canvas.drawCircle(centerX - tmpW, centerY + tmpH, dp4 + dp2, mPaint);
		canvas.drawLine(centerX - tmpW, centerY + tmpH, 
				centerX - tmpW - tmpLen * sin30, centerY + tmpH + tmpLen * cos30, mPaint);
		canvas.drawCircle(centerX - tmpW - tmpLen * sin30, 
				centerY + tmpH + tmpLen * cos30, dp4, mPaint);
		// 画外弧两点
		float tmpX = centerX - (fanRadius + dp10 * 5f) * sin30;
		float tmpY = centerY + (fanRadius + dp10 * 5f) * cos30;
		tmpLen = dp10 * 2.5f;
		canvas.drawCircle(tmpX - tmpLen * sin30, tmpY - tmpLen * cos30, dp4, mPaint);
		canvas.drawCircle(tmpX + tmpLen * cos45, tmpY + tmpLen * sin45, dp4, mPaint);
		// 画外弧
		mPaint.setStyle(Paint.Style.STROKE);
		canvas.drawArc(tmpX - tmpLen, tmpY - tmpLen, tmpX + tmpLen, tmpY + tmpLen, 
				-120, 165, false, mPaint);
		// 画内圆
		mPaint.setColor(Color_Blue);
		mPaint.setStrokeWidth(dp3);
		canvas.drawCircle(tmpX, tmpY, dp10 * 1.8f, mPaint);
		// 画文字
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 13));
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		float baseLineY = (tmpY * 2 - fontMetrics.top - fontMetrics.bottom) / 2;
		String text = "X";
        if (mScore != null && mScore.hasYaku()) {
			text = mScore.toFuString();
		}
		canvas.drawText(text, tmpX, baseLineY, mTextPaint);		
	}
	
	/**
	 * 画文字结果部分
	 * 
	 * @param canvas
	 * @param centerX
	 * @param centerY
	 * @param fanRadius
	 */
	private void onDrawResultText(Canvas canvas, float centerX, float centerY, float fanRadius) {
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color_Black);
		mPaint.setStrokeWidth(dp2);
		float tmpW = (fanRadius + dp10) * cos30;
		float tmpH = (fanRadius + dp10) * sin30;
		float tmpLen = dp10 * 2f;
		// 画连线：点线点
		canvas.drawCircle(centerX + tmpW, centerY - tmpH, dp4 + dp2, mPaint);
		float tmpX = centerX + tmpW + tmpLen * cos30;
		float tmpY = centerY - tmpH - tmpLen * sin30;
		canvas.drawLine(centerX + tmpW, centerY - tmpH, tmpX, tmpY, mPaint);
		canvas.drawCircle(tmpX, tmpY, dp4, mPaint);
		// 画外框
		mPaint.setStrokeWidth(dp2);
		tmpLen = dp10 * 1.5f;
		float dp5 = dp10 / 2;
		canvas.drawCircle(tmpX + dp5, tmpY - tmpLen - dp5, dp4, mPaint);
		canvas.drawLine(tmpX, tmpY - tmpLen, tmpX + dp5, tmpY - tmpLen - dp5, mPaint);
		canvas.drawLine(tmpX, tmpY - tmpLen, tmpX, tmpY + tmpLen, mPaint);
		canvas.drawLine(tmpX, tmpY + tmpLen, tmpX + dp5, tmpY + tmpLen + dp5, mPaint);
		canvas.drawLine(tmpX + dp5, tmpY + tmpLen + dp5, 
				tmpX + fanRadius * 5, tmpY + tmpLen + dp5, mPaint);
		canvas.drawCircle(tmpX + fanRadius * 5, tmpY + tmpLen + dp5, dp4, mPaint);
		// 画内框
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color_Blue);
		mPaint.setStrokeWidth(dp3);
		tmpLen = dp10;
//		Path innerPath = new Path();
//		innerPath.moveTo(tmpX + dp5, tmpY - tmpLen - dp5);
//		innerPath.lineTo(tmpX + dp5, tmpY + tmpLen);
//		innerPath.lineTo(tmpX + dp10, tmpY + tmpLen + dp5);
//		innerPath.lineTo(tmpX + fanRadius * 5f - dp5, tmpY + tmpLen + dp5);
//		innerPath.lineTo(tmpX + fanRadius * 5f, tmpY + tmpLen);
//		innerPath.lineTo(tmpX + fanRadius * 5f, tmpY - tmpLen - dp5);
//		innerPath.lineTo(tmpX + fanRadius * 5f - dp5, tmpY - tmpLen - dp10);
//		innerPath.lineTo(tmpX + dp10, tmpY - tmpLen - dp10);		
//		innerPath.close();
//		canvas.drawPath(innerPath, mPaint);
		canvas.drawLine(tmpX + dp10, tmpY - tmpLen - dp10, tmpX + dp5, tmpY - tmpLen - dp5, mPaint);
		canvas.drawLine(tmpX + dp5, tmpY - tmpLen - dp5, tmpX + dp5, tmpY + tmpLen, mPaint);
		canvas.drawLine(tmpX + dp5, tmpY + tmpLen, tmpX + dp10, tmpY + tmpLen + dp5, mPaint);
		canvas.drawLine(tmpX + dp10, tmpY + tmpLen + dp5, tmpX + fanRadius * 5f - dp5, tmpY + tmpLen + dp5, mPaint);
		// 画文字
		mTextPaint.setTextAlign(Paint.Align.LEFT);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 11));
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		float baseLineY = (tmpY * 2 - fontMetrics.top - fontMetrics.bottom) / 2;
		String textUp = getContext().getString(R.string.result_none);
		String textDown = "";
		if (mScore != null && mScore.hasYaku()) {
			int basePoint = ScoreSystem.GetBasePoint(mScore);
			if (isDealer) {
				if (isZiMo) {
					int point = ScoreSystem.GetPoint(basePoint, 2) + mRoundCount * 100;
					textUp = "庄家自摸<" + (point * 3 + mLizhiCount * 1000) + ">";
					textDown = "闲家付：" + point + "点";
				} else {
					int point = ScoreSystem.GetPoint(basePoint, 6) + mRoundCount * 300;
					textUp = "庄家荣和<" + (point + mLizhiCount * 1000) + ">";
					textDown = "点炮者付：" + point + "点";
				}
			} else {
				if (isZiMo) {
					int pointP = ScoreSystem.GetPoint(basePoint, 1) + mRoundCount * 100;
					int pointD = ScoreSystem.GetPoint(basePoint, 2) + mRoundCount * 100;
					textUp = "闲家自摸<" + (pointP * 2 + pointD + mLizhiCount * 1000) + ">";
					textDown = "庄家付:" + pointD + "点，闲家付：" + pointP + "点";
				} else {
					int point = ScoreSystem.GetPoint(basePoint, 4) + mRoundCount * 300;
					textUp = "闲家荣和<" + (point + mLizhiCount * 1000) + ">";
					textDown = "点炮者付：" + point + "点";
				}
			}
		}
		canvas.drawText(textUp, tmpX + dp10, baseLineY - tmpLen, mTextPaint);
		canvas.drawText(textDown, tmpX + dp10, baseLineY + tmpLen - dp5, mTextPaint);
	}
	
	/**
	 * 画番数部分
	 * 
	 * @param canvas
	 * @param centerX
	 * @param centerY
	 * @param fanRadius
	 * @param yakuRadius
	 */
	private void onDrawYaku(Canvas canvas, float centerX, float centerY, float fanRadius, float yakuRadius) {
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color_Black);
		mPaint.setStrokeWidth(dp2);
		float tmpW = (fanRadius + dp10) * cos30;
		float tmpH = (fanRadius + dp10) * sin30;
		float tmpLen = fanRadius * 2;
		float sin15 = (float) Math.sin(15 * Math.PI / 180);
		float cos15 = (float) Math.cos(15 * Math.PI / 180);
		// 画连线：点线点
		canvas.drawCircle(centerX + tmpW, centerY + tmpH, dp4 + dp2, mPaint);
		float tmpX = centerX + tmpW + tmpLen * cos15;
		float tmpY = centerY + tmpH + tmpLen * sin15;
		canvas.drawLine(centerX + tmpW, centerY + tmpH, tmpX, tmpY, mPaint);
		canvas.drawCircle(tmpX, tmpY, dp4, mPaint);
		// 画明细番
		tmpX += (yakuRadius + dp5) * cos15;
		tmpY += (yakuRadius + dp5) * sin15;
		YakuValue[] yakuValues = mScore.YakuValues();
		for (int count = 0; count < mMaxCount;) {
			// 画连线
			if (count != 0) {
				mPaint.setColor(Color_Black);
				mPaint.setStyle(Paint.Style.FILL);
				mPaint.setStrokeWidth(dp2);
				PointF point1 = new PointF(tmpX - (yakuRadius + dp5) * sin45, tmpY - (yakuRadius + dp5) * cos45);
				PointF point2 = new PointF(tmpX - (yakuRadius + dp5 * 5) * sin45, tmpY - (yakuRadius + dp5 * 5) * cos45);
				canvas.drawCircle(point1.x, point1.y, dp4, mPaint);
				canvas.drawLine(point1.x, point1.y, point2.x, point2.y, mPaint);
				canvas.drawCircle(point2.x, point2.y, dp4, mPaint);				
			}
			// 先画中心番
			YakuValue yakuValue = yakuValues[count];
			mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 14));
			Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
			PointF pointC = new PointF(tmpX, tmpY);
			mPaint.setColor(Color_Black);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(dp2);
			canvas.drawArc(pointC.x - yakuRadius - dp5, pointC.y - yakuRadius - dp5, 
					pointC.x + yakuRadius + dp5, pointC.y + yakuRadius + dp5, 
					30, 220, false, mPaint);
			mPaint.setColor(Color_Blue);
			mPaint.setStrokeWidth(dp3);
			canvas.drawCircle(pointC.x, pointC.y, yakuRadius, mPaint);
			mTextPaint.setTextAlign(Paint.Align.CENTER);
			float baseLineY = (pointC.y * 2 - fontMetrics.top - fontMetrics.bottom) / 2;
			canvas.drawText(yakuValue.toValueString(), pointC.x, baseLineY, mTextPaint);
			mTextPaint.setTextAlign(Paint.Align.LEFT);
			canvas.drawText(yakuValue.getName(), pointC.x + yakuRadius + dp5, baseLineY, mTextPaint);
			canvas.drawLine(pointC.x + yakuRadius + dp5, pointC.y - yakuRadius + dp5, 
					pointC.x + yakuRadius + dp10 * 3, pointC.y - yakuRadius + dp5, mPaint);
			canvas.drawLine(pointC.x + yakuRadius + dp5, pointC.y + yakuRadius - dp5, 
					pointC.x + yakuRadius + dp10 * 5, pointC.y + yakuRadius - dp5, mPaint);
			count++;
			if (mInvalidateCount < count) { // 延迟显示
				mInvalidateCount++;  
				if (mInvalidateCount < mMaxCount) {
					postInvalidateDelayed(150);
//					System.out.println("MjCalcTreeView postInvalidateDelayed!!!!!!!");
				}
				break;
			}
			if (count >= mMaxCount) break;
			// 再画右侧番
			yakuValue = yakuValues[count];
			mPaint.setColor(Color_Black);
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setStrokeWidth(dp2);
			PointF point1 = new PointF(tmpX + (yakuRadius + dp5) * sin45, tmpY + (yakuRadius + dp5) * cos45);
			PointF point2 = new PointF(tmpX + (yakuRadius + dp5 * 5) * sin45, tmpY + (yakuRadius + dp5 * 5) * cos45);
			PointF pointR = new PointF(tmpX + (yakuRadius * 2 + dp5 * 6) * sin45, 
					tmpY + (yakuRadius * 2 + dp5 * 6) * cos45);
			canvas.drawCircle(point1.x, point1.y, dp4, mPaint);
			canvas.drawLine(point1.x, point1.y, point2.x, point2.y, mPaint);
			canvas.drawCircle(point2.x, point2.y, dp4, mPaint);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawArc(pointR.x - yakuRadius - dp5, pointR.y - yakuRadius - dp5, 
					pointR.x + yakuRadius + dp5, pointR.y + yakuRadius + dp5, 
					30, 220, false, mPaint);
			mPaint.setColor(Color_Blue);
			mPaint.setStrokeWidth(dp3);
			canvas.drawCircle(pointR.x, pointR.y, yakuRadius, mPaint);
			mTextPaint.setTextAlign(Paint.Align.CENTER);
			baseLineY = (pointR.y * 2 - fontMetrics.top - fontMetrics.bottom) / 2;
			canvas.drawText(yakuValue.toValueString(), pointR.x, baseLineY, mTextPaint);
			mTextPaint.setTextAlign(Paint.Align.LEFT);
			canvas.drawText(yakuValue.getName(), pointR.x + yakuRadius + dp5, baseLineY, mTextPaint);
			canvas.drawLine(pointR.x + yakuRadius + dp5, pointR.y - yakuRadius + dp5, 
					pointR.x + yakuRadius + dp10 * 3, pointR.y - yakuRadius + dp5, mPaint);
			canvas.drawLine(pointR.x + yakuRadius + dp5, pointR.y + yakuRadius - dp5, 
					pointR.x + yakuRadius + dp10 * 5, pointR.y + yakuRadius - dp5, mPaint);
			count++;
			if (mInvalidateCount < count) { // 延迟显示
				mInvalidateCount++; 
				if (mInvalidateCount < mMaxCount) {
					postInvalidateDelayed(150);
//					System.out.println("MjCalcTreeView postInvalidateDelayed!!!!!!!");
				}
				break;
			}
			if (count >= mMaxCount) break;
			// 再画左侧番
			yakuValue = yakuValues[count];
			mPaint.setColor(Color_Black);
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setStrokeWidth(dp2);
			point1 = new PointF(tmpX - (yakuRadius + dp5) * sin45, tmpY + (yakuRadius + dp5) * cos45);
			point2 = new PointF(tmpX - (yakuRadius + dp5 * 5) * sin45, tmpY + (yakuRadius + dp5 * 5) * cos45);
			PointF pointL = new PointF(tmpX - (yakuRadius * 2 + dp5 * 6) * sin45, 
					tmpY + (yakuRadius * 2 + dp5 * 6) * cos45);
			canvas.drawCircle(point1.x, point1.y, dp4, mPaint);
			canvas.drawLine(point1.x, point1.y, point2.x, point2.y, mPaint);
			canvas.drawCircle(point2.x, point2.y, dp4, mPaint);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawArc(pointL.x - yakuRadius - dp5, pointL.y - yakuRadius - dp5, 
					pointL.x + yakuRadius + dp5, pointL.y + yakuRadius + dp5, 
					-70, 220, false, mPaint);
			mPaint.setColor(Color_Blue);
			mPaint.setStrokeWidth(dp3);
			canvas.drawCircle(pointL.x, pointL.y, yakuRadius, mPaint);
			mTextPaint.setTextAlign(Paint.Align.CENTER);
			baseLineY = (pointL.y * 2 - fontMetrics.top - fontMetrics.bottom) / 2;
			canvas.drawText(yakuValue.toValueString(), pointL.x, baseLineY, mTextPaint);
			mTextPaint.setTextAlign(Paint.Align.RIGHT);
			canvas.drawText(yakuValue.getName(), pointL.x - yakuRadius - dp5, baseLineY, mTextPaint);
			canvas.drawLine(pointL.x - yakuRadius - dp5, pointL.y - yakuRadius + dp5, 
					pointL.x - yakuRadius - dp10 * 3, pointL.y - yakuRadius + dp5, mPaint);
			canvas.drawLine(pointL.x - yakuRadius - dp5, pointL.y + yakuRadius - dp5, 
					pointL.x - yakuRadius - dp10 * 5, pointL.y + yakuRadius - dp5, mPaint);
			count++;
			if (mInvalidateCount < count) { // 延迟显示
				mInvalidateCount++;  
				if (mInvalidateCount < mMaxCount) {
					postInvalidateDelayed(150);
//					System.out.println("MjCalcTreeView postInvalidateDelayed!!!!!!!");
				}
				break;
			}
			// 移动中心点
			tmpX = pointL.x + (yakuRadius * 2 + dp5 * 6) * sin45;
			tmpY = pointL.y + (yakuRadius * 2 + dp5 * 6) * cos45;
		}		
	}
	
}

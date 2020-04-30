package com.mahjong.anime;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class SwingAnimation extends Animation {

	private float mMiddleDegrees;
	private float mLeftDegrees;
	private float mRightDegrees;
	private int mPivotXType;
	private float mPivotXValue;
	private int mPivotYType;
	private float mPivotYValue;
	private float mPivotX;
	private float mPivotY;
	
	/**
	 * 钟摆动画
	 * 坐标类型有三种：ABSOLUTE 绝对坐标，RELATIVE_TO_SELF 相对自身的坐标，RELATIVE_TO_PARENT 相对上级视图的坐标
	 * 
	 * @param middleDegrees 中间度数
	 * @param leftDegrees 摆到左侧的度数
	 * @param rightDegrees 摆到右侧的度数
	 * @param pivotXType 圆心X坐标类型
	 * @param pivotXValue 圆心X坐标相对比例（为0时表示左边顶点，为1表示右边顶点，为0.5表示水平中心点）
	 * @param pivotYType 圆心Y坐标类型
	 * @param pivotYValue 圆心Y坐标相对比例（为0时表示上边顶点，为1表示下边顶点，为0.5表示垂直中心点）
	 */
	public SwingAnimation(float middleDegrees, float leftDegrees, float rightDegrees,
			int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
		this.mMiddleDegrees = middleDegrees;
		this.mLeftDegrees = leftDegrees;
		this.mRightDegrees = rightDegrees;
		this.mPivotXType = pivotXType;
		this.mPivotXValue = pivotXValue;
		this.mPivotYType = pivotYType;
		this.mPivotYValue = pivotYValue;
	}
	
	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mPivotX = resolveSize(mPivotXType, mPivotXValue, width, parentWidth);
		mPivotY = resolveSize(mPivotYType, mPivotYValue, height, parentHeight);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float degrees;

		float leftPos = (float) (1.0 / 4.0);
		float rightPos = (float) (3.0 / 4.0);

		if (interpolatedTime <= leftPos) {
			degrees = mMiddleDegrees + ((mLeftDegrees - mMiddleDegrees) * interpolatedTime * 4);
		} else if (interpolatedTime > leftPos && interpolatedTime < rightPos) {
			degrees = mLeftDegrees + ((mRightDegrees - mLeftDegrees) * (interpolatedTime - leftPos) * 2);
		} else {
			degrees = mRightDegrees + ((mMiddleDegrees - mRightDegrees) * (interpolatedTime - rightPos) * 4);
		}

		float scale = getScaleFactor();
		if (mPivotX == 0.0f && mPivotY == 0.0f) {
			t.getMatrix().setRotate(degrees);
		} else {
			t.getMatrix().setRotate(degrees, mPivotX * scale, mPivotY * scale);
		}
	}

}

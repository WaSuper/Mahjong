package com.mahjong.ui;

import com.mahjong.R;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.ValueTool;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MjPanelView extends View {

	private static final String FrameColor = "#349abe";
	private static final String BackgroundColor = "#401E90FF"; // 蓝色
	private static final String SelectColor = "#4000FFFF"; // 绿色
	private static final String FrameFanfu5Color = "#FFFF00"; // 黄色
	private static final String BackgroundFanfu5Color = "#A0CDCD00"; // 
	private static final String SelectFanfu5Color = "#FFFF00"; // 
	private static final String FrameFanfu9Color = "#FF0000"; // 红色
	private static final String BackgroundFanfu9Color = "#A0CD3700"; // 
	private static final String SelectFanfu9Color = "#FF0000"; // 
	private static final String FrameFanfu13Color = "#000000"; // 黑色
	private static final String BackgroundFanfu13Color = "#A01C1C1C"; // 
	private static final String SelectFanfu13Color = "#000000"; // 
	
	enum PathType {
		None, LeftTop, LeftBottom, RightTop, RightBottom, 
		Center, Left, Top, Right, Bottom
	}
	
	/**
	 * 基本画刷
	 */
	private Paint mPaint;
	/**
	 * 图片画刷
	 */
	private Paint mBitmapPaint;
	/**
	 * 背景画刷
	 */
	private Paint mBgPaint;
	/**
	 * 文字画刷
	 */
	private Paint mTextPaint;
	
	private int mFengCount = 0; // 场风计数：0-》东，1-》南，2-》西，3-》北
	private int mJuCount = 0; // 局数计数，对应各自场风，范围：0-3
	private int mLizhiCount = 0; // 立直累计数
	private int mRoundCount = 0; // 本场数
	private int[] mScores = {25000, 25000, 25000, 25000}; // 玩家分数
	private int[] mLizhis = {0, 0, 0, 0}; // 玩家立直状态：0-》无，1-》立直，2-》双立直
	
	private int mCurPlayer = 0; // 当前玩家
	private int mBaseScore = 25000; // 基本分
	//private int[] mPlayer2Index = {0, 1, 2, 3}; // 玩家对应的索引
	private String[] mBtn2String = new String[4]; // 四个按钮对应的文本(0:帮助，1：退出，2：历史，3：撤销)
	
	private int mMemberCount = 4; // 玩家人数
	
	private Bitmap mImgLizhiH; // 水平立直棒
	private Bitmap mImgLizhiV; // 竖直立直棒
	private Bitmap mImgLizhiCount; // 立直棒计数
	private Bitmap mImgDice; // 骰子背景
	private Bitmap[] mImgWinds; // 文字：东、南、西、北
	private Bitmap[] mImgJuNums; // 文字：一、二、三、四
	private Bitmap[] mImgRoundNums; // 0-9、x文字
	private Bitmap mImgJu; // 文字：局
	private Bitmap mImgBen; // 文字：本
	private Bitmap mImgChang; // 文字：场
	
	private Region mLeftTopRegion;		// 左上按钮
	private Region mLeftBottomRegion;	// 左下按钮
	private Region mRightTopRegion;		// 右上按钮
	private Region mRightBottomRegion;	// 右下按钮
	private RectF mCenterRectF;			// 中心区域
	private RectF mBottomPointRectF;	// 下方分值区
	private RectF mRightPointRectF;		// 右方分值区
	private RectF mTopPointRectF;		// 上方分值区
	private RectF mLeftPointRectF;		// 左方分值区
	
	private PathType mSelectPath = PathType.None;	// 当前点击区域
	private boolean mPointMode = false;	// 是否显示分差
	
	private OnMjPanelViewListener mListener;
	
	private boolean is17Step = false; // 当玩法为17步时，场风显示不同
	private int mFengType = 0; // 17步场风类型：0->固定东风,1->轮流场风
	
	private int mFrameColor;
	private int mBackgroundColor;
	private int mSelectColor;
	
	public MjPanelView(Context context) {
		this(context, null);
	}
	
	public MjPanelView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MjPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
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
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setColor(Color.parseColor(FrameColor));
		mPaint.setTextSize(ValueTool.sp2px(getContext(), 14));
		mPaint.setStrokeWidth(5);
		
		mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mBitmapPaint.setStyle(Paint.Style.FILL);
		mBitmapPaint.setTextAlign(Paint.Align.CENTER);
		
		mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mBgPaint.setStyle(Paint.Style.FILL);
		mBgPaint.setTextAlign(Paint.Align.CENTER);
		mBgPaint.setColor(Color.parseColor(BackgroundColor));
		
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mTextPaint.setStyle(Paint.Style.STROKE);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 14));
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		mBtn2String[0] = getResources().getString(R.string.help);
		mBtn2String[1] = getResources().getString(R.string.exit);
		mBtn2String[2] = getResources().getString(R.string.history);
		mBtn2String[3] = getResources().getString(R.string.revoke);
		
//		mImgLizhiH = BitmapFactory.decodeResource(getResources(), R.drawable.mj_lizhi);
//		mImgLizhiV = BitmapFactory.decodeResource(getResources(), R.drawable.mj_lizhi);
//		mImgLizhiV = ImageTool.rotateTo(90, mImgLizhiV);
		mImgLizhiCount = BitmapFactory.decodeResource(getResources(), R.drawable.mj_bang);
		mImgDice = BitmapFactory.decodeResource(getResources(), R.drawable.bg_dice);
		Bitmap windBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text_ju_wind);
		mImgWinds = ImageTool.split(windBitmap, 4, 1);
		releaseBitmap(windBitmap);
		Bitmap juNumBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text_ju_number);
		mImgJuNums = ImageTool.split(juNumBitmap, 4, 1);
		releaseBitmap(juNumBitmap);
		Bitmap roundNumBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text_count_number);
		mImgRoundNums = ImageTool.split(roundNumBitmap, 11, 1);
		releaseBitmap(roundNumBitmap);
		Bitmap extraBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text_extra);
		Bitmap[] extras = ImageTool.split(extraBitmap, 3, 1);
		releaseBitmap(extraBitmap);
		mImgJu = extras[0];
		mImgBen = extras[1];
		mImgChang = extras[2];
	}
	
	private void releaseBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}
	
	public void setCurPlayer(int index) {
		index = index % 4;
		this.mCurPlayer = index;
//		this.mPlayer2Index[0] = index;
//		this.mPlayer2Index[1] = (index + 1) % 4;
//		this.mPlayer2Index[2] = (index + 2) % 4;
//		this.mPlayer2Index[3] = (index + 3) % 4;
		invalidate();
	}
	
	public void setBaseData(int score, int member, boolean initAll) {
		mBaseScore = score;
		mMemberCount = member;
		if (initAll) {
			for (int i = 0; i < mScores.length; i++) {
				mScores[i] = mBaseScore;
			}
		}
		invalidate();
	}
	
	public void setPlayerScore(int[] scores) {
		mScores = scores;
		invalidate();
	}
	
	public void setPlayerLizhi(int index, int lizhi, int lizhiCount) {
		mLizhis[index] = lizhi;
		mLizhiCount = lizhiCount;
		invalidate();
	}
	
	public void setPlayersLizhi(int[] lizhis, int lizhiCount) {
		mLizhis = lizhis;
		mLizhiCount = lizhiCount;
	}
	
	public void setCounter(int feng, int ju, int lizhi, int round, boolean isResetLizhiState) {
		this.mFengCount = feng;
		this.mJuCount = ju;
		this.mLizhiCount = lizhi;
		this.mRoundCount = round;
		if (isResetLizhiState) {
			for (int i = 0; i < mLizhis.length; i++) {
				mLizhis[i] = 0;
			}
		}
		invalidate();
	}
	
	public void set17StepPanel(boolean is17Step, int fengType) {
		this.is17Step = is17Step;
		this.mFengType = fengType;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int width = getWidth();
		int height = getHeight();		
		float wPadding = 0, hPadding = 0, cLen = width / 3;
		if (width != height) {
			if (width > height) {
				wPadding = (width - height) / 2;
				cLen = height / 3;
			} else {
				hPadding = (height - width) / 2;
			}
		}
		int lizhiPadding = ValueTool.dp2px(getContext(), 8); // 立直框高度
		int lizhiCorner = ValueTool.dp2px(getContext(), 4); // 立直框角长度
		int pointPadding = ValueTool.dp2px(getContext(), 16); // 点数高度
		
		float leftP1 = wPadding, leftP2 = leftP1 + lizhiPadding, leftP3 = leftP2 + pointPadding, leftP4 = wPadding + cLen;
		float rightP1 = width - leftP1, rightP2 = width - leftP2, rightP3 = width - leftP3, rightP4 = width - leftP4;
		float topP1 = hPadding, topP2 = topP1 + lizhiPadding, topP3 = topP2 + pointPadding, topP4 = hPadding + cLen;
		float bottomP1 = height - topP1, bottomP2 = height - topP2, bottomP3 = height - topP3, bottomP4 = height - topP4;
		
		if (mRoundCount < 5) {
			mFrameColor = Color.parseColor(FrameColor);
			mBackgroundColor = Color.parseColor(BackgroundColor);
			mSelectColor = Color.parseColor(SelectColor);
		} else if (mRoundCount < 9) {
			mFrameColor = Color.parseColor(FrameFanfu5Color);
			mBackgroundColor = Color.parseColor(BackgroundFanfu5Color);
			mSelectColor = Color.parseColor(SelectFanfu5Color);
		} else if (mRoundCount < 13) {
			mFrameColor = Color.parseColor(FrameFanfu9Color);
			mBackgroundColor = Color.parseColor(BackgroundFanfu9Color);
			mSelectColor = Color.parseColor(SelectFanfu9Color);
		} else {
			mFrameColor = Color.parseColor(FrameFanfu13Color);
			mBackgroundColor = Color.parseColor(BackgroundFanfu13Color);
			mSelectColor = Color.parseColor(SelectFanfu13Color);
		}
		mPaint.setColor(mFrameColor);
		mBgPaint.setColor(mBackgroundColor);
		/*************************************************************************/
		/********************************* 绘制边框 *********************************/
		/*************************************************************************/
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		MaskFilter lastMaskFilter = mPaint.getMaskFilter();
		mPaint.setMaskFilter(new BlurMaskFilter(10, Blur.SOLID));
		Path framePath = new Path();
		framePath.moveTo(leftP1, topP4 + lizhiCorner);
		framePath.lineTo(leftP1 + lizhiCorner, topP4);
		framePath.lineTo(leftP3, topP4);
		framePath.lineTo(leftP4, topP3);
		framePath.lineTo(leftP4, topP1 + lizhiCorner);
		framePath.lineTo(leftP4 + lizhiCorner, topP1);
		framePath.lineTo(rightP4 - lizhiCorner, topP1);
		framePath.lineTo(rightP4, topP1 + lizhiCorner);
		framePath.lineTo(rightP4, topP3);
		framePath.lineTo(rightP3, topP4);
		framePath.lineTo(rightP1 - lizhiCorner, topP4);
		framePath.lineTo(rightP1, topP4 + lizhiCorner);
		framePath.lineTo(rightP1, bottomP4 - lizhiCorner);
		framePath.lineTo(rightP1 - lizhiCorner, bottomP4);
		framePath.lineTo(rightP3, bottomP4);
		framePath.lineTo(rightP4, bottomP3);
		framePath.lineTo(rightP4, bottomP1 - lizhiCorner);
		framePath.lineTo(rightP4 - lizhiCorner, bottomP1);
		framePath.lineTo(leftP4 + lizhiCorner, bottomP1);
		framePath.lineTo(leftP4, bottomP1 - lizhiCorner);
		framePath.lineTo(leftP4, bottomP3);
		framePath.lineTo(leftP3, bottomP4);
		framePath.lineTo(leftP1 + lizhiCorner, bottomP4);
		framePath.lineTo(leftP1, bottomP4 - lizhiCorner);		
		framePath.close();
		canvas.drawPath(framePath, mPaint);
		canvas.drawLine(leftP2, topP4, leftP2, bottomP4, mPaint);
		canvas.drawLine(rightP2, topP4, rightP2, bottomP4, mPaint);
		canvas.drawLine(leftP4, topP2, rightP4, topP2, mPaint);
		canvas.drawLine(leftP4, bottomP2, rightP4, bottomP2, mPaint);
		mPaint.setMaskFilter(lastMaskFilter);
		/*************************************************************************/
		/******************************** 绘制四个按钮 ********************************/
		/*************************************************************************/
		float buttonPadding = ValueTool.dp2px(getContext(), 5); // 按钮与边框间距
		float buttonPadding2 = ValueTool.dp2px(getContext(), 2); // 按钮与边框间距
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 14));
		float centerX, centerY;
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();	
		float textPadding = (fontMetrics.top + fontMetrics.bottom) / 2;
		// 左上按钮
		Path leftTopPath = new Path();
		leftTopPath.moveTo(leftP2, topP4 - buttonPadding);
		leftTopPath.lineTo(leftP3 - buttonPadding2, topP4 - buttonPadding);
		leftTopPath.lineTo(leftP4 - buttonPadding, topP3 - buttonPadding2);
		leftTopPath.lineTo(leftP4 - buttonPadding, topP2);
		leftTopPath.lineTo(leftP4 - buttonPadding - pointPadding, topP2);
		leftTopPath.lineTo(leftP2, topP4 - buttonPadding - pointPadding);
		leftTopPath.close();
		canvas.drawPath(leftTopPath, mPaint);
		if (mSelectPath == PathType.LeftTop) {
			mBgPaint.setColor(mSelectColor);
		}
		canvas.drawPath(leftTopPath, mBgPaint);
		mBgPaint.setColor(mBackgroundColor);
		mLeftTopRegion = path2Region(leftTopPath);
		centerX = (leftP2 + leftP4 - buttonPadding) / 2;
		centerY = (topP2 + topP4 - buttonPadding) / 2;
		canvas.rotate(-45, centerX, centerY);
		canvas.drawText(mBtn2String[0], centerX, centerY - textPadding, mTextPaint);
		canvas.rotate(45, centerX, centerY);
		// 左下按钮
		Path leftBottomPath = new Path();
		leftBottomPath.moveTo(leftP2, bottomP4 + buttonPadding);
		leftBottomPath.lineTo(leftP3 - buttonPadding2, bottomP4 + buttonPadding);
		leftBottomPath.lineTo(leftP4 - buttonPadding, bottomP3 + buttonPadding2);
		leftBottomPath.lineTo(leftP4 - buttonPadding, bottomP2);
		leftBottomPath.lineTo(leftP4 - buttonPadding - pointPadding, bottomP2);
		leftBottomPath.lineTo(leftP2, bottomP4 + buttonPadding + pointPadding);
		leftBottomPath.close();
		canvas.drawPath(leftBottomPath, mPaint);
		if (mSelectPath == PathType.LeftBottom) {
			mBgPaint.setColor(mSelectColor);
		}
		canvas.drawPath(leftBottomPath, mBgPaint);
		mBgPaint.setColor(mBackgroundColor);
		mLeftBottomRegion = path2Region(leftBottomPath);
		centerX = (leftP2 + leftP4 - buttonPadding) / 2;
		centerY = (bottomP2 + bottomP4 + buttonPadding) / 2;
		canvas.rotate(-135, centerX, centerY);
		canvas.drawText(mBtn2String[3], centerX, centerY - textPadding, mTextPaint);
		canvas.rotate(135, centerX, centerY);
		// 右上按钮
		Path rightTopPath = new Path();
		rightTopPath.moveTo(rightP2, topP4 - buttonPadding);
		rightTopPath.lineTo(rightP3 + buttonPadding2, topP4 - buttonPadding);
		rightTopPath.lineTo(rightP4 + buttonPadding, topP3 - buttonPadding2);
		rightTopPath.lineTo(rightP4 + buttonPadding, topP2);
		rightTopPath.lineTo(rightP4 + buttonPadding + pointPadding, topP2);
		rightTopPath.lineTo(rightP2, topP4 - buttonPadding - pointPadding);
		rightTopPath.close();
		canvas.drawPath(rightTopPath, mPaint);
		if (mSelectPath == PathType.RightTop) {
			mBgPaint.setColor(mSelectColor);
		}
		canvas.drawPath(rightTopPath, mBgPaint);
		mBgPaint.setColor(mBackgroundColor);
		mRightTopRegion = path2Region(rightTopPath);
		centerX = (rightP2 + rightP4 + buttonPadding) / 2;
		centerY = (topP2 + topP4 - buttonPadding) / 2;
		canvas.rotate(45, centerX, centerY);
		canvas.drawText(mBtn2String[1], centerX, centerY - textPadding, mTextPaint);
		canvas.rotate(-45, centerX, centerY);
		// 右下按钮
		Path rightBottomPath = new Path();
		rightBottomPath.moveTo(rightP2, bottomP4 + buttonPadding);
		rightBottomPath.lineTo(rightP3 + buttonPadding2, bottomP4 + buttonPadding);
		rightBottomPath.lineTo(rightP4 + buttonPadding, bottomP3 + buttonPadding2);
		rightBottomPath.lineTo(rightP4 + buttonPadding, bottomP2);
		rightBottomPath.lineTo(rightP4 + buttonPadding + pointPadding, bottomP2);
		rightBottomPath.lineTo(rightP2, bottomP4 + buttonPadding + pointPadding);
		rightBottomPath.close();
		canvas.drawPath(rightBottomPath, mPaint);
		if (mSelectPath == PathType.RightBottom) {
			mBgPaint.setColor(mSelectColor);
		}
		canvas.drawPath(rightBottomPath, mBgPaint);
		mBgPaint.setColor(mBackgroundColor);
		mRightBottomRegion = path2Region(rightBottomPath);
		centerX = (rightP2 + rightP4 + buttonPadding) / 2;
		centerY = (bottomP2 + bottomP4 + buttonPadding) / 2;
		canvas.rotate(135, centerX, centerY);
		canvas.drawText(mBtn2String[2], centerX, centerY - textPadding, mTextPaint);
		canvas.rotate(-135, centerX, centerY);
		/*************************************************************************/
		/****************************** 绘制分值和立直棒 *******************************/
		/*************************************************************************/
		int index;
		String score;
		boolean isLizhi;
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 15));
		fontMetrics = mTextPaint.getFontMetrics();	
		textPadding = (fontMetrics.top + fontMetrics.bottom) / 2;
		// 下方
		index = mCurPlayer;
		if (checkDrawScoreAndLizhi(index)) {
			score = getPointText(index);
			isLizhi = mLizhis[index] > 0;
			centerX = (leftP4 + rightP4) / 2;
			centerY = (bottomP2 + bottomP3) / 2;
			canvas.drawText(score, centerX, centerY - textPadding, mTextPaint);
			if (isLizhi) {
				RectF bottomRectF = new RectF(leftP4 + lizhiCorner, bottomP2, 
						rightP4 - lizhiCorner, bottomP1);
				if (mImgLizhiH == null || mImgLizhiH.isRecycled()) {
					mImgLizhiH = BitmapFactory.decodeResource(getResources(), R.drawable.mj_lizhi);
					mImgLizhiH = ImageTool.scaleTo(mImgLizhiH, bottomRectF.width(), bottomRectF.height());
				}
				canvas.drawBitmap(mImgLizhiH, null, bottomRectF, mBitmapPaint);
			}
		}
		mBottomPointRectF = new RectF(leftP4, bottomP3, rightP4, bottomP1);
		// 右方 
		index = (index + 1) % 4;
		if (checkDrawScoreAndLizhi(index)) {
			score = getPointText(index);
			isLizhi = mLizhis[index] > 0;
			centerX = (rightP2 + rightP3) / 2;
			centerY = (topP4 + bottomP4) / 2;
			canvas.rotate(-90, centerX, centerY);
			canvas.drawText(score, centerX, centerY - textPadding, mTextPaint);		
			canvas.rotate(90, centerX, centerY);
			if (isLizhi) {
				RectF rightRectF = new RectF(rightP2, topP4 + lizhiCorner, 
						rightP1, bottomP4 - lizhiCorner);
				if (mImgLizhiV == null || mImgLizhiV.isRecycled()) {
					mImgLizhiV = BitmapFactory.decodeResource(getResources(), R.drawable.mj_lizhi);
					mImgLizhiV = ImageTool.scaleTo(mImgLizhiV, rightRectF.width(), rightRectF.height());
					mImgLizhiV = ImageTool.rotateTo(90, mImgLizhiV);
				}
				canvas.drawBitmap(mImgLizhiV, null, rightRectF, mBitmapPaint);
			}
		}
		mRightPointRectF = new RectF(rightP3, topP4, rightP1, bottomP4);
		// 上方
		index = (index + 1) % 4;
		if (checkDrawScoreAndLizhi(index)) {
			score = getPointText(index);
			isLizhi = mLizhis[index] > 0;
			centerX = (leftP4 + rightP4) / 2;
			centerY = (topP2 + topP3) / 2;
			canvas.rotate(180, centerX, centerY);
			canvas.drawText(score, centerX, centerY - textPadding, mTextPaint);		
			canvas.rotate(-180, centerX, centerY);
			if (isLizhi) {
				RectF topRectF = new RectF(leftP4 + lizhiCorner, topP1, 
						rightP4 - lizhiCorner, topP2);
				if (mImgLizhiH == null || mImgLizhiH.isRecycled()) {
					mImgLizhiH = BitmapFactory.decodeResource(getResources(), R.drawable.mj_lizhi);
					mImgLizhiH = ImageTool.scaleTo(mImgLizhiH, topRectF.width(), topRectF.height());
				}
				canvas.drawBitmap(mImgLizhiH, null, topRectF, mBitmapPaint);
			}
		}
		mTopPointRectF = new RectF(leftP4, topP1, rightP4, topP3);
		// 左方
		index = (index + 1) % 4;
		if (checkDrawScoreAndLizhi(index)) {
			score = getPointText(index);
			isLizhi = mLizhis[index] > 0;
			centerX = (leftP2 + leftP3) / 2;
			centerY = (topP4 + bottomP4) / 2;
			canvas.rotate(90, centerX, centerY);
			canvas.drawText(score, centerX, centerY - textPadding, mTextPaint);		
			canvas.rotate(-90, centerX, centerY);
			if (isLizhi) {
				RectF leftRectF = new RectF(leftP1, topP4 + lizhiCorner, 
						leftP2, bottomP4 - lizhiCorner);
				if (mImgLizhiV == null || mImgLizhiV.isRecycled()) {
					mImgLizhiV = BitmapFactory.decodeResource(getResources(), R.drawable.mj_lizhi);
					mImgLizhiV = ImageTool.scaleTo(mImgLizhiV, leftRectF.width(), leftRectF.height());
					mImgLizhiV = ImageTool.rotateTo(90, mImgLizhiV);
				}
				canvas.drawBitmap(mImgLizhiV, null, leftRectF, mBitmapPaint);
			}
		}
		mLeftPointRectF = new RectF(leftP1, topP4, leftP3, bottomP4);
		/*************************************************************************/
		/**************************** 绘制局数、本场、立直数 *****************************/
		/*************************************************************************/
		float centerPadding = (leftP4 - leftP3) / 2; // 中心边框与外层边框间距
		float centerCorner = ValueTool.dp2px(getContext(), 15);
		// 画中心框
		Path centerPath = new Path();
		centerPath.moveTo(leftP3 + centerPadding, topP3 + centerPadding + centerCorner);
		centerPath.lineTo(leftP3 + centerPadding + centerCorner, topP3 + centerPadding);
		centerPath.lineTo(rightP3 - centerPadding - centerCorner, topP3 + centerPadding);
		centerPath.lineTo(rightP3 - centerPadding, topP3 + centerPadding + centerCorner);
		centerPath.lineTo(rightP3 - centerPadding, bottomP3 - centerPadding - centerCorner);
		centerPath.lineTo(rightP3 - centerPadding - centerCorner, bottomP3 - centerPadding);
		centerPath.lineTo(leftP3 + centerPadding + centerCorner, bottomP3 - centerPadding);
		centerPath.lineTo(leftP3 + centerPadding, bottomP3 - centerPadding - centerCorner);
		centerPath.close();
		canvas.drawPath(centerPath, mPaint);
		// 画骰子背景
		mBitmapPaint.setAlpha(0x40);
		mCenterRectF = new RectF(leftP3 + centerPadding, topP3 + centerPadding, 
				rightP3 - centerPadding, bottomP3 - centerPadding);
		canvas.drawBitmap(mImgDice, null, mCenterRectF, mBitmapPaint);
		mBitmapPaint.setAlpha(0xff);
		// 画局数
//		float margin = ValueTool.dp2px(getContext(), 10);
//		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 22));
//		fontMetrics = mTextPaint.getFontMetrics();	
//		textPadding = (fontMetrics.top + fontMetrics.bottom) / 2;
//		float cHeight = bottomP3 - topP3 - centerPadding * 2;
//		centerX = getWidth() / 2;
//		centerY = getHeight() / 2 - ValueTool.dp2px(getContext(), 25);
//		canvas.drawText(getJuText(), centerX, centerY - textPadding, mTextPaint);
		
		float centerW = rightP3 - leftP3 - centerPadding * 2;
		float centerH = bottomP3 - topP3 - centerPadding * 2;
		float marginTop = centerH / 10;
		float left = leftP3 + centerPadding;
		float top = topP3 + centerPadding + marginTop;
		float rectLen = centerW / 3;
		RectF windRectF = new RectF(left, top, left + rectLen, top + rectLen);
		RectF juNumRectF =  new RectF(left + rectLen, top, left + rectLen * 2, top + rectLen);
		canvas.drawBitmap(mImgJuNums[mJuCount], null, juNumRectF, mBitmapPaint);
		RectF juTextRectF = new RectF(left + rectLen * 2, top, left + rectLen * 3, top + rectLen);
		canvas.drawBitmap(mImgJu, null, juTextRectF, mBitmapPaint);
		if (is17Step) {
			int tmpIndex = 0;
			if (mFengType == 0) {
				tmpIndex = 0;
			} else {
				switch (mMemberCount) {
				case 2:
					if (mFengCount % 2 == 0) tmpIndex = 0;
					else tmpIndex = 2;
					break;
				case 3:
					tmpIndex = mFengCount % 3;
					break;
				case 4:
					tmpIndex = mFengCount % 4;
					break;
				default:
					break;
				}
			}
			canvas.drawBitmap(mImgWinds[tmpIndex], null, windRectF, mBitmapPaint);
			// 绘制总场风次数
			RectF windExtraRectF1 = new RectF(left + rectLen * 2 / 3, top - rectLen / 3, 
					left + rectLen * 4 / 3, top + rectLen / 3);
			RectF windExtraRectF2 = new RectF(left + rectLen * 4 / 3, top - rectLen / 3, 
					left + rectLen * 2, top + rectLen / 3);
			if (mFengCount < 9) {
				canvas.drawBitmap(mImgRoundNums[mFengCount + 1], null, windExtraRectF1, mBitmapPaint);
			} else if (mFengCount < 99) {
				int highNum = (mFengCount + 1) / 10;
				canvas.drawBitmap(mImgRoundNums[highNum], null, windExtraRectF1, mBitmapPaint);
				int lowNum = (mFengCount + 1) % 10;
				canvas.drawBitmap(mImgRoundNums[lowNum], null, windExtraRectF2, mBitmapPaint);
			} else {
				canvas.drawBitmap(mImgRoundNums[9], null, windExtraRectF1, mBitmapPaint);
				canvas.drawBitmap(mImgRoundNums[9], null, windExtraRectF2, mBitmapPaint);
			}
		} else {
			canvas.drawBitmap(mImgWinds[mFengCount], null, windRectF, mBitmapPaint);
		}
		
		// 画本场数
//		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 17));
//		mTextPaint.setTextAlign(Paint.Align.CENTER);
//		fontMetrics = mTextPaint.getFontMetrics();	
//		textPadding = (fontMetrics.top + fontMetrics.bottom) / 2;
//		centerY = getHeight() / 2;
//		canvas.drawText("本", centerX, centerY - textPadding, mTextPaint);
//		mTextPaint.setTextAlign(Paint.Align.LEFT);
//		canvas.drawText("场", centerX + margin, centerY - textPadding, mTextPaint);
//		mTextPaint.setTextAlign(Paint.Align.RIGHT);
//		canvas.drawText("" + mRoundCount, centerX - margin, centerY - textPadding, mTextPaint);
		
		rectLen = centerW / 4;
		float numWidth = rectLen * 55 / 70;
		top += centerH / 3;
		left += (centerW - rectLen) / 2;
		RectF benTextRectF = new RectF(left, top, left + rectLen, top + rectLen);
		canvas.drawBitmap(mImgBen, null, benTextRectF, mBitmapPaint);
		RectF changTextRectF = new RectF(left + rectLen, top, left + rectLen * 2, top + rectLen);
		canvas.drawBitmap(mImgChang, null, changTextRectF, mBitmapPaint);
		if (mRoundCount > 9) {
			int hNum = mRoundCount / 10;
			if (mRoundCount > 99) hNum = 9;
			RectF hNumRectF = new RectF(left - numWidth * 2, top, left - numWidth, top + rectLen);
			canvas.drawBitmap(mImgRoundNums[hNum], null, hNumRectF, mBitmapPaint);
		}
		int lNum = mRoundCount % 10;
		if (mRoundCount > 99) lNum = 9;
		RectF lNumRectF = new RectF(left - numWidth, top, left, top + rectLen);
		canvas.drawBitmap(mImgRoundNums[lNum], null, lNumRectF, mBitmapPaint);
		
		// 画立直数
//		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 14));
//		fontMetrics = mTextPaint.getFontMetrics();	
//		mTextPaint.setTextAlign(Paint.Align.CENTER);
//		textPadding = (fontMetrics.top + fontMetrics.bottom) / 2;
//		centerY += cHeight / 3;
//		canvas.drawText("x", centerX, centerY, mTextPaint);
//		mTextPaint.setTextAlign(Paint.Align.LEFT);
//		canvas.drawText("" + mLizhiCount, centerX + margin, centerY, mTextPaint);
//		float lizhiHeight = ValueTool.dp2px(getContext(), 12);
//		float lizhiWidth = lizhiHeight / 2.8f;
//		canvas.drawBitmap(mImgLizhiCount, null, 
//				new RectF(centerX - margin - lizhiWidth, centerY - lizhiHeight, 
//						centerX - margin, centerY), mBitmapPaint);
		
		top += rectLen * 5 / 4;
		rectLen = centerW / 5;
		numWidth = rectLen * 55 / 70;
		left = leftP3 + centerPadding + (centerW - rectLen) / 2;
		RectF xtextRectF = new RectF(left, top, left + numWidth, top + rectLen);
		canvas.drawBitmap(mImgRoundNums[10], null, xtextRectF, mBitmapPaint);
		float lizhiHeight = rectLen * 0.9f;
		float lizhiWidth = lizhiHeight / 2.8f;
		canvas.drawBitmap(mImgLizhiCount, null, 
				new RectF(left - lizhiWidth * 1.5f, top, left - lizhiWidth * 0.5f, top + lizhiHeight), mBitmapPaint);
		left += numWidth;
		if (mLizhiCount > 9) {
			int hNum = mLizhiCount / 10;
			if (mLizhiCount > 99) hNum = 9;
			RectF hLizhiRectF = new RectF(left, top, left + numWidth, top + rectLen);
			canvas.drawBitmap(mImgRoundNums[hNum], null, hLizhiRectF, mBitmapPaint);
			left += numWidth;
		}
		lNum = mLizhiCount % 10;
		if (mLizhiCount > 99) lNum = 9;
		RectF lLizhiRectF = new RectF(left, top, left + numWidth, top + rectLen);
		canvas.drawBitmap(mImgRoundNums[lNum], null, lLizhiRectF, mBitmapPaint);
	}
	
	private boolean checkDrawScoreAndLizhi(int index) {
		if ((index == 1 && mMemberCount < 3)
				|| (index == 3 && mMemberCount < 4)) {
			return false;
		}
		return true;
	}
	
	private String getPointText(int index) {
		String text = "";
		if (mPointMode) {
			switch (mSelectPath) {
			case Bottom:
				text += (mScores[index] - mScores[mCurPlayer]);
				break;
			case Right:
				text += (mScores[index] - mScores[(mCurPlayer + 1) % 4]);
				break;
			case Top:
				text += (mScores[index] - mScores[(mCurPlayer + 2) % 4]);
				break;
			case Left:
				text += (mScores[index] - mScores[(mCurPlayer + 3) % 4]);
				break;
			default:
				break;
			}
		} else {
			text += mScores[index];
		}
		return text;
	}
	
//	private String getJuText() {
//		String[] feng = {"东", "南", "西", "北"};
//		String[] ju = {"一", "二", "三", "四"};		
//		return feng[mFengCount] + ju[mJuCount] + "局";
//	}
	
	/**
	 * 路径转区域
	 * 
	 * @param path
	 * @return
	 */
	private Region path2Region(Path path) {
		//构造一个区域对象，左闭右开的。  
        RectF r = new RectF();  
        //计算控制点的边界  
        path.computeBounds(r, true);  
        //设置区域路径和剪辑描述的区域  
        Region region = new Region();
        region.setPath(path, new Region((int)r.left,(int)r.top,(int)r.right,(int)r.bottom));
        return region;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
        float y = event.getY();
        PathType type;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			type = checkTouchItem((int)x, (int)y);
			if (type != PathType.None && type != mSelectPath) {
				mSelectPath = type;
				invalidate();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			type = checkTouchItem((int)x, (int)y);
			if (type != PathType.None && type == mSelectPath) {
				doTouchItem(type);
			}
			mSelectPath = PathType.None;
			mPointMode = false;
			invalidate();
			break;
			default:
				break;
		}
		return true;
	}
		
	private PathType checkTouchItem(int x, int y) {
		if (mLeftTopRegion.contains(x, y)) {
			return PathType.LeftTop;
		} else if (mLeftBottomRegion.contains(x, y)) {
			return PathType.LeftBottom;
		} else if (mRightTopRegion.contains(x, y)) {
			return PathType.RightTop;
		} else if (mRightBottomRegion.contains(x, y)) {
			return PathType.RightBottom;
		} else if (mCenterRectF.contains(x, y)) {
			return PathType.Center;
		} else if (mBottomPointRectF.contains(x, y)) {
			mPointMode = true;
			return PathType.Bottom;
		} else if (mRightPointRectF.contains(x, y)) {
			mPointMode = true;
			return PathType.Right;
		} else if (mTopPointRectF.contains(x, y)) {
			mPointMode = true;
			return PathType.Top;
		} else if (mLeftPointRectF.contains(x, y)) {
			mPointMode = true;
			return PathType.Left;
		}
		return PathType.None;
	}
	
	private void doTouchItem(PathType type) {
		switch (type) {
		case LeftTop:
			if (mListener != null) mListener.onClickHelp();
			break;
		case RightTop:
			if (mListener != null) mListener.onClickEixt();
			break;
		case RightBottom:
			if (mListener != null) mListener.onClickHistory();
			break;
		case LeftBottom:
			if (mListener != null) mListener.onClickRevoke();
			break;
		case Center:
			if (mListener != null) mListener.onClickCenter();
			break;
		case Bottom:
			if (mListener != null) mListener.onClickPoint(0);
			break;
		case Right:
			if (mListener != null) mListener.onClickPoint(1);
			break;
		case Top:
			if (mListener != null) mListener.onClickPoint(2);
			break;
		case Left:
			if (mListener != null) mListener.onClickPoint(3);
			break;
		default:
			break;
		}
	}
		
	public void setOnMjPanelViewListener(OnMjPanelViewListener listener) {
		this.mListener = listener;
	}
		
	public interface OnMjPanelViewListener {
		public void onClickEixt();
		public void onClickHelp();
		public void onClickHistory();
		public void onClickRevoke();
		public void onClickCenter();
		public void onClickPoint(int pos);
	}
}

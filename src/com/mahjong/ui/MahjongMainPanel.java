package com.mahjong.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mahjong.R;
import com.mahjong.model.Player;
import com.mahjong.tools.HeadIconTool;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class MahjongMainPanel extends View {
	
	private static final float mMaxCameraRotate = 15; // z轴旋转角度
	private static final float tableScaleMult = 1.25f; // 桌子相对屏幕宽度放大倍数
	private float norScaleMult; // 图片实际放大倍数
	private static final int icon_none = R.drawable.head_none; // 默认头像
	
	/**
	 * 基本画刷
	 */
	private Paint mPaint;
	/**
	 * 背景画刷
	 */
	private Paint mBitmapPaint;
	
	private int mFengCount = 0; // 场风计数：0-》东，1-》南，2-》西，3-》北
	private int[] mJuCount = {0, 0, 0, 0}; // 局数计数，对应各自场风，范围：0-3
	private Player[] mPlayers = new Player[4]; // 4个玩家
	private int[] mScores = {25000, 25000, 25000, 25000}; // 玩家分数
	private boolean[] mTingPais = {false, false, false, false}; // 玩家听牌状态
	private int[] mLizhis = {0, 0, 0, 0}; // 玩家立直状态
	private boolean[] mChickens = {true, true, true, true}; // 玩家烧鸡状态

	private int mCurPlayer = 0; // 当前玩家
	private int mBaseScore = 25000; // 基本分
	private int mLeftCount = -1; // 牌山剩余数量	
	private RectF[] mPlayerRectFs = new RectF[4]; // 玩家头像位置
	private RectF[] mTingPaiRectFs = new RectF[4]; // 听牌位置
	private RectF[] mLizhiRectFs = new RectF[4]; // 立直位置
	private RectF[] mSpectrumRectFs = new RectF[3]; // 牌谱位置：左、上、右
	
	private boolean isInit = false;	
	/* 图片 */
	private Bitmap mImgTable; // 桌子
	private Bitmap mImgIndicator; // 指示盘
	private Bitmap[] mImgFeng; // 东 南 西 北
	private Bitmap[] mImgChang; // 东 南 西 北 局
	private Bitmap[] mImgJu; // 1 2 3 4
	private Bitmap mImgLeft; // 余
	private Bitmap[] mImgLeftCount; // 余数量
	private Bitmap[][] mImgNum; // 红：- + 0 1 2 3 4 5 6 7 8 9
								// 蓝：- + 0 1 2 3 4 5 6 7 8 9
								// 黄：- + 0 1 2 3 4 5 6 7 8 9
	private Bitmap mImgLizhi; // 立直棒
	private Bitmap mImgLeader; // 东风定位
	private Bitmap mImgChicken; // 烧鸡
	private Bitmap[] mImgBtnLiZhi = new Bitmap[3]; // 立直按钮：0-》nor，1-》sel，2-》double
	private Bitmap[] mImgBtnTingPai = new Bitmap[2]; // 听牌按钮：0-》nor，1-》sel
	private Bitmap[] mImgSampleSpectrum = new Bitmap[3]; // 牌谱：左、上、右
	
	/* 用于旋转图片 */
    private Matrix mMatrix;
    private Camera mCamera;
    /* Camera绕X轴旋转的角度 */
    private float mCameraRotateX;
    /* Camera绕Y轴旋转的角度 */
    private float mCameraRotateY;
    
    /* 监听动作 */
    private MahjongMainPanelListener mListener;
    
    /* 监听长按 */
    private static final int TOUCH_SLOP = 20;
    private RectF indicatorRect;
    private boolean isMove;
    private float lastX;
    private float lastY;
    private Runnable mLongClickRunnable = new Runnable() {
		
		@Override
		public void run() {
			performLongClick();
		}
	};

	public MahjongMainPanel(Context context) {
		this(context, null);
	}
	
	public MahjongMainPanel(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MahjongMainPanel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint();
	}
	
	/**
	 * 初始化画刷
	 */
	private void initPaint() {
		mMatrix = new Matrix();
        mCamera = new Camera();
        
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setColor(Color.parseColor("#ffffff"));
		mPaint.setTextSize(ValueTool.sp2px(getContext(), 14));
		
		mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mBitmapPaint.setStyle(Paint.Style.FILL);
		mBitmapPaint.setTextAlign(Paint.Align.CENTER);
	}
	
	/**
	 * 初始化图片素材
	 */
	private void initImage() {
		mImgTable = BitmapFactory.decodeResource(getResources(), R.drawable.mj_table_bg);
		mImgIndicator = BitmapFactory.decodeResource(getResources(), R.drawable.mj_indicator_bg);
		Bitmap fengBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mj_feng);
		mImgFeng = ImageTool.split(fengBitmap, 4, 1);
		Bitmap changBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mj_chang);
		mImgChang = ImageTool.split(changBitmap, 5, 1);
		Bitmap juBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mj_ju);
		mImgJu = ImageTool.split(juBitmap, 4, 1);
		mImgLeft = BitmapFactory.decodeResource(getResources(), R.drawable.mj_left);
		Bitmap leftCount = BitmapFactory.decodeResource(getResources(), R.drawable.mj_num_left);
		mImgLeftCount = ImageTool.split(leftCount, 10, 1);
		Bitmap numBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mj_score);
		mImgNum = ImageTool.split2(numBitmap, 12, 3);
		mImgLizhi = BitmapFactory.decodeResource(getResources(), R.drawable.mj_lizhi);
		mImgLeader = BitmapFactory.decodeResource(getResources(), R.drawable.mj_firstplayer);
		mImgChicken = BitmapFactory.decodeResource(getResources(), R.drawable.mj_chicken);
		mImgBtnLiZhi[0] = BitmapFactory.decodeResource(getResources(), R.drawable.mj_lizhi_nor);
		mImgBtnLiZhi[1] = BitmapFactory.decodeResource(getResources(), R.drawable.mj_lizhi_sel);
		mImgBtnLiZhi[2] = BitmapFactory.decodeResource(getResources(), R.drawable.mj_lizhi_double_sel);
		mImgBtnTingPai[0] = BitmapFactory.decodeResource(getResources(), R.drawable.mj_tingpai_nor);
		mImgBtnTingPai[1] = BitmapFactory.decodeResource(getResources(), R.drawable.mj_tingpai_sel);
		mImgSampleSpectrum[0] = BitmapFactory.decodeResource(getResources(), R.drawable.mj_sample_left);
		mImgSampleSpectrum[1] = BitmapFactory.decodeResource(getResources(), R.drawable.mj_sample_top);
		mImgSampleSpectrum[2] = BitmapFactory.decodeResource(getResources(), R.drawable.mj_sample_right);
	}

	/**
	 * 重设参数值
	 */
	public void reset() {
		mFengCount = 0;
		Arrays.fill(mJuCount, 0);
		mCurPlayer = 0;
		mLeftCount = -1;
		for (int i = 0; i < mScores.length; i++) {
			mScores[i] = mBaseScore;
		}
		for (int i = 0; i < mLizhis.length; i++) {
			mLizhis[i] = 0;
		}
	}
	
	public void setPlayers(Player east, Player south, Player west, Player north) {
		mPlayers[0] = east;
		mPlayers[1] = south;
		mPlayers[2] = west;
		mPlayers[3] = north;
	}
	
	public void setCurrentPlayer(int wind) {
		mCurPlayer = wind % 4;
		invalidate();
	}
	
	public int getCurrentPlayer() {
		return mCurPlayer;
	}
	
	public void setBaseScore(int score) {
		mBaseScore = score;
		for (int i = 0; i < mScores.length; i++) {
			mScores[i] = score;
		}
		invalidate();
	}
	
	public void setPlayerScores(int east, int south, int west, int north) {
		mScores[0] = east;
		mScores[1] = south;
		mScores[2] = west;
		mScores[3] = north;
		invalidate();
	}
	
	public void setPlayerLiZhi(int wind, boolean lizhi, boolean doubleLizhi) {
		wind = wind % 4;
		if (doubleLizhi) {
			mLizhis[wind] = 2;
		} else if (lizhi) {
			mLizhis[wind] = 1;
		} else {
			mLizhis[wind] = 0;
		}
		invalidate();
	}
	
	public void setPlayerLizhi(int wind) {
		wind = wind % 4;
		mLizhis[wind] = (mLizhis[wind] + 1) % 3;
		if (mListener != null) {
			mListener.onLiZhiState(mLizhis[0], mLizhis[1], mLizhis[2], mLizhis[3]);
		}
		invalidate();
	}
	
	public void setPlayerTingPai(int wind) {
		wind = wind % 4;
		mTingPais[wind] = !mTingPais[wind];
		if (mListener != null) {
			mListener.onTingPaiState(mTingPais[0], mTingPais[1], mTingPais[2], mTingPais[3]);
		}
		invalidate();
	}
	
	/**
	 * 开始绘制
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (!isInit) { initImage(); isInit = true; }
		
		// 画布翻转
		getCameraRotate(getWidth() / 2, getHeight() / 2  - getWidth() / 2, getWidth() / 2);
		setCameraRotate(canvas);
		
		// 画桌子
		RectF tableRect = onDrawTable(canvas);
		// 画指示盘
		indicatorRect = onDrawIndicatorBg(canvas, tableRect);
		// 画牌谱
		onDrawSpectrum(canvas, indicatorRect);
		// 画风位
		onDrawFeng(canvas, indicatorRect);
		// 画中间文字
		onDrawCenterText(canvas);
		// 画分数和立直棒
		onDrawScoreAndLizhi(canvas, indicatorRect);
		
		// 画布回滚
		resetCameraRotate(canvas);
		
		// 绘制玩家
		onDrawPlayer(canvas);
	}
	
	/**
	 * 绘制玩家
	 * 
	 * @param canvas
	 */
	private void onDrawPlayer(Canvas canvas) {
		float iconWidth = ValueTool.dp2px(getContext(), 42f) * norScaleMult;
		float iconHeight = ValueTool.dp2px(getContext(), 42f) * norScaleMult;
		float iconMargin = ValueTool.dp2px(getContext(), 18f) * norScaleMult;
		float btnWidth = ValueTool.dp2px(getContext(), 10f) * norScaleMult;
		float btnHeight = ValueTool.dp2px(getContext(), 23.5f) * norScaleMult;
		float textMargin = ValueTool.dp2px(getContext(), 10f) * norScaleMult;
		// 下方
		int index = mCurPlayer;
		Player bottomPlayer = mPlayers[index];		
		int icon = bottomPlayer != null ? HeadIconTool.String2Id(bottomPlayer.getIcon()) : -1;
		if (icon == -1) icon = icon_none;
		Bitmap bottomBitmap = BitmapFactory.decodeResource(getResources(), icon);
		RectF bottomRectF = new RectF(iconMargin * 3, getHeight() - iconMargin - iconHeight,
				iconMargin * 3 + iconWidth, getHeight() - iconMargin);
		canvas.drawBitmap(bottomBitmap, null, bottomRectF, mBitmapPaint);
		onDrawPlayerDetail(canvas, bottomPlayer, index, bottomRectF, textMargin, btnWidth, btnHeight);
		// 右方
		index = (index + 1) % 4;
		Player rightPlayer = mPlayers[index];
		icon = rightPlayer != null ? HeadIconTool.String2Id(rightPlayer.getIcon()) : -1;
		if (icon == -1) icon = icon_none;
		Bitmap rightBitmap = BitmapFactory.decodeResource(getResources(), icon);
		RectF rightRectF = new RectF(getWidth() - iconMargin - iconWidth, getHeight() / 2 - iconHeight * 2.7f,
				getWidth() - iconMargin, getHeight() / 2 - iconHeight * 1.7f);
		canvas.drawBitmap(rightBitmap, null, rightRectF, mBitmapPaint);
		onDrawPlayerDetail(canvas, rightPlayer, index, rightRectF, textMargin, btnWidth, btnHeight);
		// 上方
		index = (index + 1) % 4;
		Player topPlayer = mPlayers[index];		
		icon = topPlayer != null ? HeadIconTool.String2Id(topPlayer.getIcon()) : -1;
		if (icon == -1) icon = icon_none;
		Bitmap topBitmap = BitmapFactory.decodeResource(getResources(), icon);
		RectF topRectF = new RectF(getWidth() - iconMargin * 3 - iconWidth, getHeight() / 2 - iconHeight * 4f,
				getWidth() - iconMargin * 3, getHeight() / 2 - iconHeight * 3f);
		canvas.drawBitmap(topBitmap, null, topRectF, mBitmapPaint);
		onDrawPlayerDetail(canvas, topPlayer, index, topRectF, textMargin, btnWidth, btnHeight);
		// 左方
		index = (index + 1) % 4;
		Player leftPlayer = mPlayers[index];
		icon = leftPlayer != null ? HeadIconTool.String2Id(leftPlayer.getIcon()) : -1;
		if (icon == -1) icon = icon_none;
		Bitmap leftBitmap = BitmapFactory.decodeResource(getResources(), icon);
		RectF leftRectF = new RectF(iconMargin, getHeight() / 2 - iconHeight * 2.7f ,
				iconMargin + iconWidth, getHeight() / 2 - iconHeight * 1.7f);
		canvas.drawBitmap(leftBitmap, null, leftRectF, mBitmapPaint);
		onDrawPlayerDetail(canvas, leftPlayer, index, leftRectF, textMargin, btnWidth, btnHeight);
		
		float lHeight = ValueTool.dp2px(getContext(), 140f) * norScaleMult;
		mSpectrumRectFs[0] = new RectF(leftRectF.left, leftRectF.bottom + textMargin * 2, 
				leftRectF.right, leftRectF.bottom + lHeight);
		mSpectrumRectFs[1] = new RectF(leftRectF.right + btnWidth, leftRectF.top, 
				rightRectF.left - btnWidth, leftRectF.bottom);
		mSpectrumRectFs[2] = new RectF(rightRectF.left, rightRectF.bottom + textMargin * 2, 
				rightRectF.right, rightRectF.bottom + lHeight);
	}
	
	/**
	 * 绘制玩家细节
	 * 
	 * @param canvas
	 * @param player
	 * @param index
	 * @param parent
	 */
	private void onDrawPlayerDetail(Canvas canvas, Player player, int index, RectF parent,
			float textMargin, float btnWidth, float btnHeight) {
		mPlayerRectFs[index] = parent;
		// 画名字
		canvas.drawText(player != null ? player.getNickName() : getResources().getString(R.string.pc), 
				parent.centerX(), parent.bottom + textMargin, mPaint);
		// 画听牌
		mTingPaiRectFs[index] = new RectF(parent.left - btnWidth, parent.centerY() - btnHeight / 2,
				parent.left, parent.centerY() + btnHeight / 2);
		canvas.drawBitmap(mTingPais[index] ? mImgBtnTingPai[1] : mImgBtnTingPai[0], 
				null, mTingPaiRectFs[index], mBitmapPaint);
		// 画立直
		mLizhiRectFs[index] = new RectF(parent.right, parent.centerY() - btnHeight / 2,
				parent.right + btnWidth, parent.centerY() + btnHeight / 2);
		canvas.drawBitmap(mImgBtnLiZhi[mLizhis[index]], 
				null, mLizhiRectFs[index], mBitmapPaint);
		// 画烧鸡
		if (mChickens[index]) {
			float len = parent.width() / 6;
			RectF chickenRectF = new RectF(parent.left - len, parent.bottom - len,
					parent.left + len, parent.bottom + len);
			canvas.drawBitmap(mImgChicken, null, chickenRectF, mBitmapPaint);
		}
		// 当前玩家为初始东风
		if (index == 0) {
			float width = parent.width() / 5;
			float height = width * mImgLeader.getHeight() / mImgLeader.getWidth();
			RectF leadRectF = new RectF(parent.left - width / 2, parent.top - height,
					parent.left + width / 2, parent.top);
			canvas.drawBitmap(mImgLeader, null, leadRectF, mBitmapPaint);
		}
	}
	
	/**
	 * 画桌子
	 * 
	 * @param canvas
	 */
	private RectF onDrawTable(Canvas canvas) {
		RectF tableRect = new RectF();
		tableRect.left = (1 - tableScaleMult) * getWidth() / 2;
		tableRect.top = (getHeight() - getWidth() * tableScaleMult) / 2;
		tableRect.right = (1 + tableScaleMult) * getWidth() / 2;
		tableRect.bottom = tableRect.top + getWidth() * tableScaleMult;
		canvas.drawBitmap(mImgTable, null, tableRect, mBitmapPaint);
		norScaleMult = getWidth() * tableScaleMult / mImgTable.getWidth();
		return tableRect;
	}
	
	/**
	 * 画指示盘背景
	 * 
	 * @param canvas
	 */
	private RectF onDrawIndicatorBg(Canvas canvas, RectF tableRect) {
		RectF indicatorRect = new RectF();
		float indicatorLen = mImgIndicator.getWidth() * norScaleMult;
		indicatorRect.left = (getWidth() - indicatorLen) / 2;
		indicatorRect.top = (getHeight() - indicatorLen) / 2;;
		indicatorRect.right = (getWidth() + indicatorLen) / 2;
		indicatorRect.bottom = indicatorRect.top + indicatorLen;
		canvas.drawBitmap(mImgIndicator, null, indicatorRect, mBitmapPaint);
		return indicatorRect;
	}
	
	/**
	 * 画牌谱
	 * 
	 * @param canvas
	 * @param indicatorRect
	 */
	private void onDrawSpectrum(Canvas canvas, RectF indicatorRect) {
		float marginCenter = ValueTool.dp2px(getContext(), 50f) * norScaleMult;
		float lHeight = ValueTool.dp2px(getContext(), 120f) * norScaleMult;
		float lWidth = lHeight * mImgSampleSpectrum[0].getWidth() / mImgSampleSpectrum[0].getHeight();
		float tWidth = ValueTool.dp2px(getContext(), 135f) * norScaleMult;
		float tHeight = tWidth * mImgSampleSpectrum[1].getHeight() / mImgSampleSpectrum[1].getWidth();
		RectF leftRectF = new RectF(indicatorRect.left - marginCenter - lWidth, indicatorRect.centerY() - lHeight / 2,
				indicatorRect.left - marginCenter, indicatorRect.centerY() + lHeight / 2);		
		canvas.drawBitmap(mImgSampleSpectrum[0], null, leftRectF, mBitmapPaint);
		RectF topRectF = new RectF(indicatorRect.centerX() - tWidth / 2, indicatorRect.top - marginCenter - tHeight,
				indicatorRect.centerX() + tWidth / 2, indicatorRect.top - marginCenter);
		canvas.drawBitmap(mImgSampleSpectrum[1], null, topRectF, mBitmapPaint);
		RectF rightRectF = new RectF(indicatorRect.right + marginCenter, indicatorRect.centerY() - lHeight / 2,
				indicatorRect.right + marginCenter + lWidth, indicatorRect.centerY() + lHeight / 2);
		canvas.drawBitmap(mImgSampleSpectrum[2], null, rightRectF, mBitmapPaint);
	}
	
	/**
	 * 画东南西北4个风位
	 * 
	 * @param canvas
	 * @param indicatorRect
	 */
	private void onDrawFeng(Canvas canvas, RectF indicatorRect) {
		int index = (4 - mCurPlayer + mJuCount[mFengCount]) % 4;
		float len = mImgFeng[0].getWidth() * norScaleMult;
		float padding = ValueTool.dp2px(getContext(), 4.3f) * norScaleMult;
		float paddingTop = ValueTool.dp2px(getContext(), 5.2f) * norScaleMult;
		RectF leftBottom = new RectF(indicatorRect.left + padding, indicatorRect.bottom - padding - len,
				indicatorRect.left + padding + len, indicatorRect.bottom - padding);
		RectF rightBottom = new RectF(indicatorRect.right - padding - len, indicatorRect.bottom - padding - len,
				indicatorRect.right - padding, indicatorRect.bottom - padding);
		RectF rightTop = new RectF(indicatorRect.right - padding - len, indicatorRect.top + paddingTop,
				indicatorRect.right - padding, indicatorRect.top + paddingTop + len);
		RectF leftTop = new RectF(indicatorRect.left + padding, indicatorRect.top + paddingTop,
				indicatorRect.left + padding + len, indicatorRect.top + paddingTop + len);
		
		Bitmap tmp;
		// 风位 1 局
		int imgIndex = (4 - index) % 4;
		mMatrix.reset();
		canvas.drawBitmap(mImgFeng[imgIndex], null, leftBottom, mBitmapPaint);
		// 风位 2 局
		imgIndex = (imgIndex + 1) % 4;
		mMatrix.postRotate(-90);
		tmp = Bitmap.createBitmap(mImgFeng[imgIndex], 0, 0, 
				mImgFeng[imgIndex].getWidth(), mImgFeng[imgIndex].getHeight(), mMatrix, true); // 向左旋转90度
		canvas.drawBitmap(tmp, null, rightBottom, mBitmapPaint);
		// 风位 3 局
		imgIndex = (imgIndex + 1) % 4;
		mMatrix.postRotate(-90);
		tmp = Bitmap.createBitmap(mImgFeng[imgIndex], 0, 0, 
				mImgFeng[imgIndex].getWidth(), mImgFeng[imgIndex].getHeight(), mMatrix, true); // 向左旋转180度
		canvas.drawBitmap(tmp, null, rightTop, mBitmapPaint);
		// 风位4 局
		imgIndex = (imgIndex + 1) % 4;
		mMatrix.postRotate(-90);
		tmp = Bitmap.createBitmap(mImgFeng[imgIndex], 0, 0, 
				mImgFeng[imgIndex].getWidth(), mImgFeng[imgIndex].getHeight(), mMatrix, true); // 向左旋转270度
		canvas.drawBitmap(tmp, null, leftTop, mBitmapPaint);
	}
	
	/**
	 * 画中间文字
	 * 
	 * @param canvas
	 */
	private void onDrawCenterText(Canvas canvas) {
		float aboveScaleMult = norScaleMult - 0.1f;
		
		float abovePadding = ValueTool.dp2px(getContext(), 0.9f) * norScaleMult;;
		float numWidth = mImgJu[0].getWidth() * aboveScaleMult;
		float numHeight = mImgJu[0].getHeight() * aboveScaleMult;
		RectF numRectF = new RectF((getWidth() - numWidth) / 2, getHeight() / 2 - abovePadding - numHeight, 
				(getWidth() + numWidth) / 2, getHeight() / 2 - abovePadding);
		canvas.drawBitmap(mImgJu[mJuCount[mFengCount]], null, numRectF, mBitmapPaint);
		
		float aboveTextWidth = mImgChang[0].getWidth() * aboveScaleMult;
		float aboveTextHeight = mImgChang[0].getHeight() * aboveScaleMult;
		RectF fengRectF = new RectF(numRectF.left - aboveTextWidth, getHeight() / 2 - abovePadding - aboveTextHeight,
				numRectF.left, getHeight() / 2 - abovePadding);
		canvas.drawBitmap(mImgChang[mFengCount], null, fengRectF, mBitmapPaint);
		RectF changRectF = new RectF(numRectF.right, getHeight() / 2 - abovePadding - aboveTextHeight,
				numRectF.right + aboveTextWidth, getHeight() / 2 - abovePadding);
		canvas.drawBitmap(mImgChang[4], null, changRectF, mBitmapPaint);
		
		float belowScaleMult = norScaleMult * 1.1f;
		
		float belowPadding = ValueTool.dp2px(getContext(), 4f) * norScaleMult;
		float belowTextWidth = mImgLeft.getWidth() * belowScaleMult;
		float belowTextHeight = mImgLeft.getHeight() * belowScaleMult;
		RectF leftRectF = new RectF(numRectF.left - belowTextWidth, getHeight() / 2 + belowPadding,
				numRectF.left, getHeight() / 2 + belowPadding + belowTextHeight);
		canvas.drawBitmap(mImgLeft, null, leftRectF, mBitmapPaint);
		
		float belowNumWidth = mImgNum[0][0].getWidth() * belowScaleMult;
		float belowNumHeight = mImgNum[0][0].getHeight() * belowScaleMult;
		RectF sNumRectF = new RectF(leftRectF.right + 4, leftRectF.bottom - belowNumHeight,
				leftRectF.right + 4 + belowNumWidth, leftRectF.bottom);
		RectF gNumRectF = new RectF(sNumRectF.right + 2, leftRectF.bottom - belowNumHeight,
				sNumRectF.right + 2 + belowNumWidth, leftRectF.bottom);
		Bitmap sBitmap, gBitmap;
		if (mLeftCount < 0) {
			sBitmap = mImgNum[1][0];
			gBitmap = mImgNum[1][0];
		} else {
			sBitmap = mImgLeftCount[mLeftCount / 10];
			gBitmap = mImgLeftCount[mLeftCount % 10];
		}
		canvas.drawBitmap(sBitmap, null, sNumRectF, mBitmapPaint);
		canvas.drawBitmap(gBitmap, null, gNumRectF, mBitmapPaint);
	}
	
	/**
	 * 画分数和立直棒
	 * 
	 * @param canvas
	 */	
	private void onDrawScoreAndLizhi(Canvas canvas, RectF indicatorRect) {
		float scale = norScaleMult - 0.1f;
		float marginBottom = ValueTool.dp2px(getContext(), 19f) * norScaleMult;
		float marginTop = marginBottom;
		float marginBottom2 = ValueTool.dp2px(getContext(), 6.2f) * norScaleMult;
		float marginTop2 = ValueTool.dp2px(getContext(), 7f) * norScaleMult;
		float numWidth = mImgNum[2][0].getWidth() * scale;
		float numHeight = mImgNum[2][0].getHeight() * scale;
		float lizhiWidth = mImgLizhi.getWidth() * (scale - 0.2f);
		float lizhiHeight = mImgLizhi.getHeight() * (scale - 0.2f);
		// 画下方
		mMatrix.reset();
		List<Bitmap> bottomList = calcNum2Bitmap(mScores[mCurPlayer]);
		Bitmap bottombBitmap = ImageTool.merge2H(bottomList, mBitmapPaint);
		RectF bottomRectF = new RectF(
				(getWidth() - numWidth * bottomList.size()) / 2, 
				indicatorRect.bottom - marginBottom - numHeight,
				(getWidth() + numWidth * bottomList.size()) / 2, 
				indicatorRect.bottom - marginBottom);
		canvas.drawBitmap(bottombBitmap, null, bottomRectF, mBitmapPaint);
		if (mLizhis[mCurPlayer] != 0) {
			RectF bottomRectF2 = new RectF(
					(getWidth() - lizhiWidth) / 2, 
					indicatorRect.bottom - marginBottom2 - lizhiHeight,
					(getWidth() + lizhiWidth) / 2, 
					indicatorRect.bottom - marginBottom2);
			canvas.drawBitmap(mImgLizhi, null, bottomRectF2, mBitmapPaint);
		}		
		// 画右方
		mMatrix.postRotate(-90);
		List<Bitmap> rightList = calcNum2Bitmap(mScores[(mCurPlayer + 1) % 4]);
		Bitmap rightBitmap = ImageTool.merge2H(rightList, mBitmapPaint);
		rightBitmap = Bitmap.createBitmap(rightBitmap, 0, 0, 
				rightBitmap.getWidth(), rightBitmap.getHeight(), mMatrix, true); 
		RectF rightRectF = new RectF(
				indicatorRect.right - marginBottom - numHeight,
				(getHeight() - numWidth * rightList.size()) / 2,
				indicatorRect.right - marginBottom,
				(getHeight() + numWidth * rightList.size()) / 2);
		canvas.drawBitmap(rightBitmap, null, rightRectF, mBitmapPaint);
		if (mLizhis[(mCurPlayer + 1) % 4] != 0) {
			Bitmap tmp = Bitmap.createBitmap(mImgLizhi, 0, 0, 
					mImgLizhi.getWidth(), mImgLizhi.getHeight(), mMatrix, true);
			RectF rightRectF2 = new RectF(
					indicatorRect.right - marginBottom2 - lizhiHeight, 
					(getHeight() - lizhiWidth) / 2,
					indicatorRect.right - marginBottom2, 
					(getHeight() + lizhiWidth) / 2);
			canvas.drawBitmap(tmp, null, rightRectF2, mBitmapPaint);
		}
		// 画上方
		mMatrix.postRotate(-90);
		List<Bitmap> topList = calcNum2Bitmap(mScores[(mCurPlayer + 2) % 4]);
		Bitmap topBitmap = ImageTool.merge2H(topList, mBitmapPaint);
		topBitmap = Bitmap.createBitmap(topBitmap, 0, 0, 
				topBitmap.getWidth(), topBitmap.getHeight(), mMatrix, true); 
		RectF topRectF = new RectF(
				(getWidth() - numWidth * topList.size()) / 2,
				indicatorRect.top + marginTop,
				(getWidth() + numWidth * topList.size()) / 2,
				indicatorRect.top + marginTop + numHeight);
		canvas.drawBitmap(topBitmap, null, topRectF, mBitmapPaint);
		if (mLizhis[(mCurPlayer + 2) % 4] != 0) {
			RectF topRectF2 = new RectF(
					(getWidth() - lizhiWidth) / 2, 
					indicatorRect.top + marginTop2,
					(getWidth() + lizhiWidth) / 2, 
					indicatorRect.top + marginTop2 + lizhiHeight);
			canvas.drawBitmap(mImgLizhi, null, topRectF2, mBitmapPaint);
		}
		// 画左方
		mMatrix.postRotate(-90);
		List<Bitmap> leftList = calcNum2Bitmap(mScores[(mCurPlayer + 3) % 4]);
		Bitmap leftBitmap = ImageTool.merge2H(leftList, mBitmapPaint);
		leftBitmap = Bitmap.createBitmap(leftBitmap, 0, 0, 
				leftBitmap.getWidth(), leftBitmap.getHeight(), mMatrix, true); 
		RectF leftRectF = new RectF(
				indicatorRect.left + marginBottom,
				(getHeight() - numWidth * leftList.size()) / 2,
				indicatorRect.left + marginBottom + numHeight,
				(getHeight() + numWidth * leftList.size()) / 2);
		canvas.drawBitmap(leftBitmap, null, leftRectF, mBitmapPaint);
		if (mLizhis[(mCurPlayer + 3) % 4] != 0) {
			Bitmap tmp = Bitmap.createBitmap(mImgLizhi, 0, 0, 
					mImgLizhi.getWidth(), mImgLizhi.getHeight(), mMatrix, true);
			RectF leftRectF2 = new RectF(
					indicatorRect.left + marginBottom2, 
					(getHeight() - lizhiWidth) / 2,
					indicatorRect.left + marginBottom2 + lizhiHeight, 
					(getHeight() + lizhiWidth) / 2);
			canvas.drawBitmap(tmp, null, leftRectF2, mBitmapPaint);
		}
	}
	
	/**
	 * 将数字转换为图片
	 * 
	 * @param num
	 * @return
	 */
	private List<Bitmap> calcNum2Bitmap(int num) {
		List<Bitmap> list = new ArrayList<Bitmap>();
		int divide, remain = num;
		int divisor = 100000;
		boolean isZero = true;
		if (remain < 0) {
			remain = -remain;
			list.add(mImgNum[2][0]);
		}
		while (divisor > 0) {
			divide = remain / divisor;
			remain = remain % divisor;
			divisor /= 10;
			if (isZero && divide == 0) continue;
			else {
				isZero = false;
				list.add(mImgNum[2][divide + 2]);
			}
		}
		return list;
	}
	
	/**
	 * 旋转画布
	 * 
	 * @param mCanvas
	 */
	private void setCameraRotate(Canvas mCanvas) {
        mMatrix.reset();
        mCamera.save();
        mCamera.rotateX(mCameraRotateX);//绕x轴旋转
        mCamera.rotateY(mCameraRotateY);//绕y轴旋转
        mCamera.getMatrix(mMatrix);//计算对于当前变换的矩阵，并将其复制到传入的mMatrix中
        mCamera.restore();
       /**
         * Camera默认位于视图的左上角，故生成的矩阵默认也是以其左上角为旋转中心，
         * 所以在动作之前调用preTranslate将mMatrix向左移动getWidth()/2个长度，
         * 向上移动getHeight()/2个长度，
         * 使旋转中心位于矩阵的中心位置，动作之后再post回到原位
         */
        mMatrix.preTranslate(-getWidth() / 2, -getHeight() / 2);
        mMatrix.postTranslate(getWidth() / 2, getHeight() / 2);
        mCanvas.concat(mMatrix);//将mMatrix与canvas中当前的Matrix相关联
    }
	
	private void resetCameraRotate(Canvas mCanvas) {
		mMatrix.reset();
        mCamera.save();
        mCamera.rotateX(-mCameraRotateX);//绕x轴旋转
        mCamera.rotateY(-mCameraRotateY);//绕y轴旋转
		mCamera.getMatrix(mMatrix);
		mCamera.restore();
        mMatrix.preTranslate(-getWidth() / 2, -getHeight() / 2);
        mMatrix.postTranslate(getWidth() / 2, getHeight() / 2);
		mCanvas.concat(mMatrix);//将mMatrix与canvas中当前的Matrix相关联
	}
	
	/**
	 * 获取旋转点
	 * 
	 * @param x
	 * @param y
	 * @param mMaxRadius
	 */
	private void getCameraRotate(float x, float y, float mMaxRadius) {
        float rotateX = -(y - getHeight() / 2);
        float rotateY = (x - getWidth() / 2);
        /**
         *为什么旋转角度要这样计算：
         * 当Camera.rotateX(x)的x为正时，图像围绕X轴，上半部分向里下半部分向外，进行旋转，
         * 也就是手指触摸点要往上移。这个x就会与event.getY()的值有关，x越大，绕X轴旋转角度越大，
         * 以圆心为基准，手指往上移动，event.getY() - getHeight() / 2的值为负，
         * 故 float rotateX = -(event.getY() - getHeight() / 2)
         * 同理，
         * 当Camera.rotateY(y)的y为正时，图像围绕Y轴，右半部分向里左半部分向外，进行旋转，
         * 也就是手指触摸点要往右移。这个y就会与event.getX()的值有关，y越大，绕Y轴旋转角度越大，
         * 以圆心为基准，手指往右移动，event.getX() - getWidth() / 2的值为正，
         * 故 float rotateY = event.getX() - getWidth() / 2
         */

        /**
         * 此时得到的rotateX、rotateY 其实是以圆心为基准，手指移动的距离，
         * 这个值很大，不能用来作为旋转的角度，
         * 所以还需要继续处理
         */

        //求出移动距离与半径之比。mMaxRadius为最大半径
        float percentX = rotateX / mMaxRadius;
        float percentY = rotateY / mMaxRadius;

        if (percentX > 1) {
            percentX = 1;
        } else if (percentX < -1) {
            percentX = -1;
        }

        if (percentY > 1) {
            percentY = 1;
        } else if (percentY < -1) {
            percentY = -1;
        }

        //将最终的旋转角度控制在一定的范围内，这里mMaxCameraRotate的值为15，效果比较好
        mCameraRotateX = percentX * mMaxCameraRotate;
        mCameraRotateY = percentY * mMaxCameraRotate;
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
        float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:	
			if (checkTouchItem(x, y, true)) {
				//invalidate();
			} else {
				isMove = false;
				lastX = x;
				lastY = y;
				if (indicatorRect.contains(x, y)) {
					postDelayed(mLongClickRunnable, ViewConfiguration.getLongPressTimeout());
				}				
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (checkTouchItem(x, y, false)) {
				//invalidate();
			} else if (!isMove) {
				if (Math.abs(lastX - x) > TOUCH_SLOP || Math.abs(lastY - y) > TOUCH_SLOP){
					//移动超过了阈值，表示移动了
	                isMove = true;
	                //移除runnable
	                removeCallbacks(mLongClickRunnable); 
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			removeCallbacks(mLongClickRunnable); 
			break;
		default:
			break;
		}
		return true;
	}
	
	private boolean checkTouchItem(float x, float y, boolean isDown) {
		if (isDown) {
			for (int i = 0; i < mLizhiRectFs.length; i++) {
				if (x >= mLizhiRectFs[i].left && x <= mLizhiRectFs[i].right + 10
						&& y >= mLizhiRectFs[i].top - 10 && y <= mLizhiRectFs[i].bottom + 10) {
					setPlayerLizhi(i);
					return true;
				}			
			}
			for (int i = 0; i < mTingPaiRectFs.length; i++) {
				if (x >= mTingPaiRectFs[i].left - 10 && x <= mTingPaiRectFs[i].right
						&& y >= mTingPaiRectFs[i].top - 10 && y <= mTingPaiRectFs[i].bottom + 10) {
					setPlayerTingPai(i);
					return true;
				}		
			}
			for (int i = mSpectrumRectFs.length - 1; i >= 0; i--) {
				if (mSpectrumRectFs[i].contains(x, y)) {
					if (mListener != null) {
						mListener.onClickSpectrum(((3 - i + mCurPlayer) % 4 + (4 - mJuCount[mFengCount])) % 4);
					}
					return true;
				}
			}
		}		
		return false;
	}
	
	public void setOnMahjongMainPanelListener(MahjongMainPanelListener listener) {
		this.mListener = listener;
	}
	
	public interface MahjongMainPanelListener {
		public void onLiZhiState(int east, int south, int west, int north);
		public void onTingPaiState(boolean east, boolean south, boolean west, boolean north);
		public void onClickSpectrum(int index);
	}
	
}

package com.mahjong.ui;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjSetting;
import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class MahjongMainDora extends MahjongBaseView {

	private static final float MaxWidthMult = 6.5f;
	private static final float MaxHeightMult = 2.5f;
	
	/**
	 * 是否里宝牌
	 */
	private boolean doraIn;
	/**
	 * 单张牌宽度
	 */
	private int mjWidth;
	/**
	 * 单张牌高度
	 */
	private int mjHeight;
	/**
	 * 左右预留边距
	 */
	private int mjPadding;
	/**
	 * 基本画刷
	 */
	private Paint mPaint;
	/**
	 * 牌谱画刷
	 */
	private Paint mBitmapPaint;
	/**
	 * 牌谱
	 */	
	private List<MjCard> mCards = new ArrayList<MjCard>();
	private List<MjCard> mCardIns = new ArrayList<MjCard>();
	/**
	 * 牌数
	 */
	private int MaxSize = 5;
	/**
	 * 立直数
	 */
	private int mLizhiCount = 0;
	/**
	 * 本场数
	 */
	private int mRoundCount = 0;
	
	/* 监听动作 */
	private MahjongMainDoraListener mListener;
	
	public MahjongMainDora(Context context) {
		this(context, null);
	}
	
	public MahjongMainDora(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MahjongMainDora(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.MahjongDora);
		touchMode = a.getBoolean(R.styleable.MahjongDora_touchMode, true);
		doraIn = a.getBoolean(R.styleable.MahjongDora_doraIn, false);;
		a.recycle();
		initPaint();
		initData();
	}
	
	private void initPaint() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setTextAlign(Paint.Align.LEFT);
		mPaint.setTextSize(ValueTool.sp2px(getContext(), 13));
		mPaint.setColor(Color.WHITE);
		
		mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mBitmapPaint.setStyle(Paint.Style.FILL);
		mBitmapPaint.setTextAlign(Paint.Align.CENTER);
		
	}
	
	private void initData() {
		for (int i = 0; i < MaxSize; i++) {
			mCards.add(new MjCard());
			mCardIns.add(new MjCard());
		}
	}
	
	public void setTouchMode(boolean touchMode) {
		this.touchMode = touchMode;
	}
	
	public List<MjCard> getDoraList() {
		return mCards;
	}
	
	public List<MjCard> getDoraInList() {
		return mCardIns;
	}
	
	public void setDoraList(List<MjCard> list) {
		for (int i = 0; i < MaxSize; i++) {
			mCards.get(i).num = list.get(i).num;
		}
		invalidate();
	}
	
	public void setDoraInList(List<MjCard> list) {
		for (int i = 0; i < MaxSize; i++) {
			mCardIns.get(i).num = list.get(i).num;
		}
		invalidate();
	}
	
	public void setDoarIn(boolean doraIn) {
		if (this.doraIn != doraIn) {
			this.doraIn = doraIn;
			invalidate();
		}		
	}
	
	public boolean getDoraIn() {
		return doraIn;
	}
	
	public void setLizhiAndRound(int lizhi, int round) {
		this.mLizhiCount = lizhi;
		this.mRoundCount = round;
		this.invalidate();
	}
	
	@Override
	public void reset() {
		mLizhiCount = 0;
		mRoundCount = 0;
		for (int i = 0; i < mCards.size(); i++) {
			mCards.get(i).setBlank();
		}	
		for (int i = 0; i < mCardIns.size(); i++) {
			mCardIns.get(i).setBlank();
		}
		clearTouchItem();
		invalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int widthSize = dm.widthPixels > dm.heightPixels ? dm.heightPixels : dm.widthPixels;
        
        mjWidth = widthSize / 19;
		mjHeight = 128 * mjWidth / 82;
		mjPadding = (int) (mjWidth * (MaxWidthMult - 5) / 2);
		
		int width = (int) (mjWidth * MaxWidthMult), height = (int) (mjHeight * MaxHeightMult);
        setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int cTop = getHeight() / 2 - mjHeight * 6 / 7;
		int leftPoint = mjPadding;	
		// 画宝牌指示牌
		if (doraIn) {			
			onDrawCardIns(canvas, cTop + mjHeight / 4, leftPoint + mjWidth / 3);	
		}
		// 画宝牌
		onDrawCards(canvas, cTop, leftPoint);
		// 画立直数和本场数
		onDrawLizhiAndRound(canvas, cTop + mjHeight * 4 / 3, leftPoint);
		// 画边框
		onDrawFrame(canvas);
	}
	
	private int onDrawCards(Canvas canvas, int cTop, int leftPoint) {
		for (int i = 0; i < mCards.size(); i++) {
			MjCard card = mCards.get(i);
			Bitmap dBitmap = BitmapFactory.decodeResource(getResources(), 
					MjSetting.getMahjongBitmapRes(card.num));
			card.setRect(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
			canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
			leftPoint += mjWidth;
		}
		return leftPoint;
	}
	
	private int onDrawCardIns(Canvas canvas, int cTop, int leftPoint) {
		for (int i = 0; i < mCardIns.size(); i++) {
			MjCard card = mCardIns.get(i);
			Bitmap dBitmap = BitmapFactory.decodeResource(getResources(), 
					MjSetting.getMahjongBitmapRes(card.num));
			card.setRect(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
			canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
			leftPoint += mjWidth;
		}
		return leftPoint;
	}
	
	private void onDrawFrame(Canvas canvas) {
		mPaint.setColor(Color.parseColor("#61a8ff"));
		mPaint.setStrokeWidth(3.0f);
		Path pathOut = new Path();
		pathOut.moveTo(mjWidth / 2, 3);
		pathOut.lineTo(getWidth() - mjWidth / 2, 3);
		pathOut.lineTo(getWidth() - 3, mjHeight / 3);
		pathOut.lineTo(getWidth() - 3, getHeight() - mjHeight / 3);
		pathOut.lineTo(getWidth() - mjWidth / 2, getHeight() - 3);
		pathOut.lineTo(mjWidth / 2, getHeight() - 3);
		pathOut.lineTo(3, getHeight() - mjHeight / 3);
		pathOut.lineTo(3, mjHeight / 3);
		pathOut.close();
		canvas.drawPath(pathOut, mPaint);
		if (doraIn) {
			mPaint.setColor(Color.parseColor("#000000"));
		} else {
			mPaint.setColor(Color.parseColor("#ffffff"));
		}
		mPaint.setStrokeWidth(1.5f);
		Path pathIn = new Path();
		pathIn.moveTo(mjWidth / 2, 6);
		pathIn.lineTo(getWidth() - mjWidth / 2, 6);
		pathIn.lineTo(getWidth() - 6, mjHeight / 3);
		pathIn.lineTo(getWidth() - 6, getHeight() - mjHeight / 3);
		pathIn.lineTo(getWidth() - mjWidth / 2, getHeight() - 6);
		pathIn.lineTo(mjWidth / 2, getHeight() - 6);
		pathIn.lineTo(6, getHeight() - mjHeight / 3);
		pathIn.lineTo(6, mjHeight / 3);
		pathIn.close();
		canvas.drawPath(pathIn, mPaint);
	}
	
	private void onDrawLizhiAndRound(Canvas canvas, int cTop, int leftPoint) {
		mPaint.setColor(Color.WHITE);
		float width = mjWidth / 3.2f;
		float height = mjWidth / 3.2f * 2.8f;
		leftPoint += mjWidth / 2;
		
		Bitmap lizhiBitmap = BitmapFactory.decodeResource(getResources(), 
				R.drawable.mj_bang);
		RectF lizhiRectF = new RectF(leftPoint, cTop, leftPoint + width, cTop + height);
		canvas.drawBitmap(lizhiBitmap, null, lizhiRectF, mBitmapPaint);
		canvas.drawText(" x " + mLizhiCount, leftPoint + width, cTop + mjHeight / 2, mPaint);
		
		leftPoint = getWidth() / 2 + mjWidth / 2;
		Bitmap roundBitmap = BitmapFactory.decodeResource(getResources(), 
				R.drawable.mj_roll);
		RectF roundRectF = new RectF(leftPoint, cTop, leftPoint + width, cTop + height);
		canvas.drawBitmap(roundBitmap, null, roundRectF, mBitmapPaint);
		canvas.drawText(" x " + mRoundCount, leftPoint + width, cTop + mjHeight / 2, mPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:	

			break;
		case MotionEvent.ACTION_MOVE:
			
			break;
		case MotionEvent.ACTION_UP:
			checkTouchItem(event.getX(), event.getY());
			break;
		default:
			break;
		}
		return true;
	}
		
	private boolean checkTouchItem(float x, float y) {
		float cTop = getHeight() / 2 - mjHeight * 6 / 7;
		float left = mjPadding - mjWidth / 3;
		float right = getWidth() - mjPadding + mjWidth / 3;
		if (y >= cTop && y <= cTop + mjHeight * 5 / 4
				&& x >= left && x <= right) {
			if (mListener != null) mListener.onClickDora(mCards, mCardIns);
			return true;
		}
		cTop += mjHeight * 4 / 3;
		float bottom = getHeight() - 10;
		if (y >= cTop && y <= bottom) {
			if (x >= left && x < getWidth() / 2) {
				if (mListener != null) mListener.onClickLiZhi(mLizhiCount);
				return true;
			}
			if (x > getWidth() / 2 && x <= right) {
				if (mListener != null) mListener.onClickRound(mRoundCount);
				return true;
			}
		}
		return false;
	}
		
	public void setOnMahjongMainDoraListener(MahjongMainDoraListener listener) {
		this.mListener = listener;
	}
	
	public interface MahjongMainDoraListener {
		public void onClickDora(List<MjCard> doras, List<MjCard> doraIns);
		public void onClickLiZhi(int lizhi);
		public void onClickRound(int round);
	}
	
}

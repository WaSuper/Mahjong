package com.mahjong.ui;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjSetting;

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

public class MahjongDora extends MahjongBaseView {

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
	/**
	 * 牌数
	 */
	private int MaxSize = 5;
	/**
	 * 点击区域
	 */
	private RectF mTouchRect = null;
	/**
	 * 点击单牌
	 */
	private MjCard mTouchCard = null;
	
	public MahjongDora(Context context) {
		this(context, null);
	}
	
	public MahjongDora(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MahjongDora(Context context, AttributeSet attrs, int defStyleAttr) {
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
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setColor(Color.parseColor("#a0ffffff"));
		
		mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mBitmapPaint.setStyle(Paint.Style.FILL);
		mBitmapPaint.setTextAlign(Paint.Align.CENTER);
		
	}
	
	private void initData() {
		for (int i = 0; i < MaxSize; i++) {
			mCards.add(new MjCard());
		}
	}
	
	public void setTouchMode(boolean touchMode) {
		this.touchMode = touchMode;
	}
	
	public List<MjCard> getDoraList() {
		return mCards;
	}
	
	public void setDoraList(List<MjCard> doras) {
		if (doras != null && doras.size() == MaxSize) {
			for (int i = 0; i < MaxSize; i++) {
				mCards.get(i).num = doras.get(i).num;
			}
			invalidate();
		}		
	}
	
	public void setDoarIn(boolean doraIn) {
		this.doraIn = doraIn;
	}
	
	public boolean getDoraIn() {
		return doraIn;
	}
	
	public int size() {
		for (int i = 0; i < mCards.size(); i++) {
			if (mCards.get(i).isBlank()) {
				return i;
			}
		}
		return MaxSize;
	}
	
	public void showStartItem() {
		for (MjCard card : mCards) {
			if (card.isBlank()) {
				setTouchCard(card);
				invalidate();
				return;
			}
		}
		setTouchCard(mCards.get(0));
		invalidate();
	}
	
	@Override
	public void reset() {
		for (int i = 0; i < mCards.size(); i++) {
			mCards.get(i).setBlank();
		}		
		clearTouchItem();
		invalidate();
	}
	
	@Override
	public boolean addMjCard(int num) {
		if (mTouchCard != null) {
			return addMjCard(mTouchCard, num);
		}
		return false;
	}
	
	@Override
	public boolean addMjCard(MjCard card, int num) {
		if (card.isBlank()) {
			for (int i = 0; i < mCards.size(); i++) {
				MjCard mFirstBlank = mCards.get(i);
				if (mFirstBlank.isBlank()) {
					mFirstBlank.num = num;
					if (i == mCards.size() - 1) {
						setTouchCard(mFirstBlank);
					} else {
						setTouchCard(mCards.get(i + 1));
					}
					break;
				}
			}
		} else {
			card.num = num;
		}
		invalidate();
		return true;
	}
	
	@Override
	public boolean removeMjCard() {
		if (mTouchCard != null) {
			return removeMjCard(mTouchCard);
		}
		return false;
	}
	
	@Override
	public boolean removeMjCard(MjCard card) {
		if (!card.isBlank()) {
			for (int i = mCards.size() - 1; i >= 0; i--) {
				MjCard mFirstUnBlank = mCards.get(i);
				if (!mFirstUnBlank.isBlank()) {
					if (i == 0 || mFirstUnBlank.id == card.id) {
						card.setBlank();
					} else {
						boolean start = false;
						for (int j = 0; j < i; j++) {
							MjCard tmpFront = mCards.get(j);
							if (!start && tmpFront.id == card.id) {
								start = true;
							}
							if (start) {
								MjCard tmpBehind = mCards.get(j + 1);
								tmpFront.num = tmpBehind.num;
								if (j + 1 == i) {
									tmpBehind.setBlank();
									setTouchCard(tmpBehind);
								}
							}							
						}
					}
					break;
				}
			}
			invalidate();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean removeTouchItem() {
		return removeMjCard();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int widthSize = dm.widthPixels;
        int minLen = dm.widthPixels > dm.heightPixels ? dm.heightPixels : dm.widthPixels;
        if (widthSize > minLen) {
        	mjWidth = widthSize / 26;
		} else {
			mjWidth = widthSize / 19;
		} 
		mjHeight = 128 * mjWidth / 82;
		mjPadding = mjWidth / 2;
		
		int width = mjWidth * 6, height = mjHeight * 2;
        setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int cTop = (getHeight() - mjHeight) / 2;
		int leftPoint = mjPadding;
		// 画暗牌
		leftPoint = onDrawCards(canvas, cTop, leftPoint);	
		// 画边框
		onDrawFrame(canvas);
		// 画操作符
		if (touchMode) {
			onDrawTouchRect(canvas);
		}
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
	
	private void onDrawFrame(Canvas canvas) {
		mPaint.setColor(Color.parseColor("#61a8ff"));
		mPaint.setStrokeWidth(3.0f);
		Path pathOut = new Path();
		pathOut.moveTo(mjWidth / 2, 3);
		pathOut.lineTo(mjWidth * 6 - mjWidth / 2, 3);
		pathOut.lineTo(mjWidth * 6 - 3, mjHeight / 3);
		pathOut.lineTo(mjWidth * 6 - 3, mjHeight * 5 / 3);
		pathOut.lineTo(mjWidth * 6 - mjWidth / 2, mjHeight * 2 - 3);
		pathOut.lineTo(mjWidth / 2, mjHeight * 2 - 3);
		pathOut.lineTo(3, mjHeight * 5 / 3);
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
		pathIn.lineTo(mjWidth * 6 - mjWidth / 2, 6);
		pathIn.lineTo(mjWidth * 6 - 6, mjHeight / 3);
		pathIn.lineTo(mjWidth * 6 - 6, mjHeight * 5 / 3);
		pathIn.lineTo(mjWidth * 6 - mjWidth / 2, mjHeight * 2 - 6);
		pathIn.lineTo(mjWidth / 2, mjHeight * 2 - 6);
		pathIn.lineTo(6, mjHeight * 5 / 3);
		pathIn.lineTo(6, mjHeight / 3);
		pathIn.close();
		canvas.drawPath(pathIn, mPaint);
	}
		
	private void onDrawTouchRect(Canvas canvas) {
		mPaint.setColor(Color.parseColor("#a0ffffff"));
		mPaint.setStrokeWidth(1.0f);
		if (mTouchRect != null) {
			float padding = 3;
			float length = mjWidth / 3;
			float height = length * sin(60);
			// 上三角形
			Path topPath = new Path();
			topPath.moveTo(mTouchRect.centerX(), mTouchRect.top - padding);
			topPath.lineTo(mTouchRect.left + length, mTouchRect.top - padding - height);
			topPath.lineTo(mTouchRect.left + length * 2, mTouchRect.top - padding - height);
			topPath.close();
			canvas.drawPath(topPath, mPaint);
			// 下三角形
			Path bottomPath = new Path();
			bottomPath.moveTo(mTouchRect.centerX(), mTouchRect.bottom + 3);
			bottomPath.lineTo(mTouchRect.left + length, mTouchRect.bottom + padding + height);
			bottomPath.lineTo(mTouchRect.left + length * 2, mTouchRect.bottom + padding + height);
			bottomPath.close();
			canvas.drawPath(bottomPath, mPaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:	
			if (touchMode) {
				if (checkTouchItem(event.getX(), event.getY(), true))
					invalidate();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (touchMode) {
				if (checkTouchItem(event.getX(), event.getY(), false))
					invalidate();
			}			
			break;
		case MotionEvent.ACTION_UP:
			if (touchMode) {
				invalidate();
			}
			break;
		default:
			break;
		}
		return true;
	}
		
	private boolean checkTouchItem(float x, float y, boolean isDown) {
		for (MjCard card : mCards) {
			if (card.checkInRect(x, y)) {
				setTouchCard(card);
				if (touchMode && mListener != null) {
					mListener.onTouchOne(this, card);
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void clearTouchItem() {
		this.mTouchRect = null;
		invalidate();
	}
	
	@Override
	public void onPressLeft() {
		if (mTouchRect != null) {
			if (mTouchCard != null) {
				if (mTouchCard.id == mCards.get(0).id) {
					setTouchCard(mCards.get(mCards.size() - 1));
				} else {
					for (int i = 1; i < mCards.size(); i++) {
						if (mTouchCard.id == mCards.get(i).id) {
							setTouchCard(mCards.get(i - 1));
							break;
						}
					}
				}		
			} 
		} else {
			setTouchCard(mCards.get(0));
		}
		invalidate();	
	}
	
	@Override
	public void onPressRight() {
		if (mTouchRect != null) {
			if (mTouchCard != null) {
				if (mTouchCard.id == mCards.get(mCards.size() - 1).id) {
					setTouchCard(mCards.get(0));
				} else {
					for (int i = 0; i < mCards.size() - 1; i++) {
						if (mTouchCard.id == mCards.get(i).id) {
							setTouchCard(mCards.get(i + 1));
							break;
						}
					}
				}
			}
		} else {
			setTouchCard(mCards.get(mCards.size() - 1));
		}
		invalidate();
	}
	
	private void setTouchCard(MjCard card) {
		mTouchCard = card;
		mTouchRect = mTouchCard.rect;
	}
		
}

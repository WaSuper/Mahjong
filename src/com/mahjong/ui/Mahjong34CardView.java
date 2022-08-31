package com.mahjong.ui;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjSetting;
import com.mahjong.item.MjFanBean;

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

public class Mahjong34CardView extends MahjongBaseView {

	/**
	 * 牌谱显示行数：三行 或 双行
	 */
	private boolean threeLine;
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
	 * 未明牌牌谱
	 */	
	private List<MjCard> mCards = new ArrayList<MjCard>();
	/**
	 * 点击区域
	 */
	private RectF mTouchRect = null;
	/**
	 * 点击单牌
	 */
	private MjCard mTouchCard = null;
	
	public Mahjong34CardView(Context context) {
		this(context, null);
	}
	
	public Mahjong34CardView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public Mahjong34CardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.MahjongSpectrum);
		threeLine = a.getBoolean(R.styleable.Mahjong34CardView_threeLine, false);
		touchMode = a.getBoolean(R.styleable.Mahjong34CardView_touchMode, false);
		a.recycle();
		initPaint();
		reset();
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
	
	public void setTouchRectColor(int color) {
		mPaint.setColor(color);
	}
	
	public void setThreeLine(boolean threeLine) {
		this.threeLine = threeLine;
	}
	
	public void setTouchMode(boolean touchMode) {
		this.touchMode = touchMode;
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
		mCards.clear();
		for (int i = 0; i < 34; i++) {
			mCards.add(new MjCard());
		}
		clearTouchItem();
		invalidate();
	}
	
	@Override
	public String toString() {
		return MjFanBean.createMjString(mCards, null, null);
	}
	
	public boolean checkIsComplete() {
		for (int i = 0; i < mCards.size(); i++) {
			if (mCards.get(i).isBlank()) {
				return false;
			}
		}
		return true;
	}
	
	public List<MjCard> getAllCards() {
		return mCards;
	}
	
	public void copy(List<MjCard> cards) {
		this.mCards.clear();
		for (MjCard c : cards) {
			mCards.add(new MjCard(c));
		}
		clearTouchItem();
		invalidate();
	}
	
	public void setData(List<MjCard> cards) {
		this.mCards = cards;
		clearTouchItem();
		invalidate();
	}	
	
	public void sortMjCards() {
		MjSetting.sortMjCardList(mCards);		
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
						setTouchCard(mCards.get(i));
					} else {
						setTouchCard(mCards.get(i + 1));
					}
					sortMjCards();
					invalidate();
					break;
				}
			}
		} else {
			card.num = num;
			int id = card.id;
			sortMjCards();
			for (int i = 0; i < mCards.size(); i++) {
				MjCard tmp = mCards.get(i);
				if (tmp.id == id) {
					setTouchCard(tmp);
					break;
				}
			}
			invalidate();
		}		
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
			for (int i = 0; i < mCards.size(); i++) {
				MjCard frontCard = mCards.get(i);
				if (frontCard.id == card.id) {
					for (int j = mCards.size() - 1; j >= i; j--) {
						MjCard behindCard = mCards.get(j);
						if (!behindCard.isBlank()) {
							if (behindCard.id != frontCard.id) {
								int tmpId = frontCard.id;
								int tmpNum = frontCard.num;
								frontCard.id = behindCard.id;
								frontCard.num = behindCard.num;
								behindCard.id = tmpId;
								behindCard.num = tmpNum;
							}
							behindCard.setBlank();
							break;
						}
					}
					break;
				}
			}
			sortMjCards();
			for (int i = 0; i < mCards.size(); i++) {
				MjCard tmp = mCards.get(i);
				if (tmp.isBlank()) {
					setTouchCard(tmp);
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
		if (mTouchRect != null) {
			if (mTouchCard != null) {
				return removeMjCard(mTouchCard);
			}
		}
		return false;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec); 
        //int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec); 
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int minLen = dm.widthPixels > dm.heightPixels ? dm.heightPixels : dm.widthPixels;
        if (widthSize > minLen) {
        	mjWidth = widthSize / 26;
		} else {
			mjWidth = widthSize / 19;
		}        
		mjHeight = 128 * mjWidth / 82;
		mjPadding = mjWidth / 2;
		
		int width = widthSize, height = threeLine ? mjWidth * 7 : mjWidth * 5;
        setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int cTop = mjHeight / 2;
		int leftPoint = mjPadding;
		// 画暗牌
		leftPoint = onDrawDrakNums(canvas, cTop, leftPoint);
		// 画操作符
		if (touchMode) {
			onDrawTouchRect(canvas);
		}
	}
	
	private int onDrawDrakNums(Canvas canvas, int cTop, int leftPoint) {
		if (mCards == null) {
			return leftPoint;
		}
		int[] lineCounts;
		if (threeLine) {
			lineCounts = new int[] {12, 10, 12};
		} else {
			lineCounts = new int[] {17, 17};
		}
		int row = 0, column = 0;
		int margin = (getWidth() - mjPadding * 2 - mjWidth * lineCounts[0]) / (lineCounts[0] - 1);
		for (MjCard card : mCards) {
			Bitmap dBitmap = BitmapFactory.decodeResource(getResources(), 
					MjSetting.getMahjongBitmapRes(card.num));
			card.setRect(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
			canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
			column++;
			if (column < lineCounts[row]) {
				leftPoint += margin + mjWidth;
			} else {
				column = 0;
				row++;
				if (row < lineCounts.length) {
					if (threeLine && row == 1) {
						leftPoint = mjPadding + margin + mjWidth;
					} else {
						leftPoint = mjPadding;
					}
					cTop += mjHeight + mjPadding;
				} else {
					break;
				}
			}
			
		}
		return leftPoint;
	}
		
	private void onDrawTouchRect(Canvas canvas) {
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
			performClick();
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
		this.mTouchCard = null;
		this.mTouchRect = null;
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
					for (int i = mCards.size() - 1; i > 0; i--) {
						MjCard card = mCards.get(i);
						if (mTouchCard.id == card.id) {
							setTouchCard(mCards.get(i - 1));
							break;
						}
					}
				}
				invalidate();			
			}
		} else {
			setTouchCard(mCards.get(0));
			invalidate();
		}
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
				invalidate();
			}
		} else {
			setTouchCard(mCards.get(0));
			invalidate();
		}
	}
	
	private void setTouchCard(MjCard card) {
		mTouchCard = card;
		mTouchRect = mTouchCard.rect;
	}

	
}

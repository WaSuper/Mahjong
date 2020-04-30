package com.mahjong.ui;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;
import com.mahjong.common.MjDir;
import com.mahjong.common.MjPairType;
import com.mahjong.common.MjSetting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class MahjongPairs extends MahjongBaseView {

	/**
	 * 使能触摸响应
	 */
	private boolean touchMode;
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
	 * 牌谱类型
	 */	
	private int mPairsType;
	/**
	 * 牌谱方向
	 */	
	private int mDir;
	/**
	 * 牌谱
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
	
	public MahjongPairs(Context context) {
		this(context, null);
	}
	
	public MahjongPairs(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MahjongPairs(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		touchMode = true;
		initPaint();
		setPairsType(MjPairType.Triplet, MjDir.left);
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
	
	public void setTouchMode(boolean touchMode) {
		this.touchMode = touchMode;
	}
	
	@Override
	public void reset() {
		for (int i = 0; i < mCards.size(); i++) {
			mCards.get(i).setBlank();
		}		
		clearTouchItem();
		invalidate();
	}
	
	public boolean checkValid() {
		switch (mPairsType) {
		case MjPairType.Sequence:
			int[] values = new int[3];
			int min = 0;
			for (int i = 0; i < mCards.size(); i++) {
				MjCard card = mCards.get(i);
				if (card.isBlank() || card.num < 1 || card.num > 30) return false;
				values[i] = card.num % 10 == 0 ? card.num - 5 : card.num;
				if (i != 0) {
					if ((values[i] / 10) != (values[i - 1] / 10)) {
						return false;
					} else {
						if (values[i] < values[min]) {
							min = i;
						}
					}					
				}
			}			
			switch (min) {
			case 0:
				if (!((values[1] == values[0] + 1 && values[2] == values[0] + 2)
						|| (values[1] == values[0] + 2 && values[2] == values[0] + 1))) {
					return false;
				}
				break;
			case 1:
				if (!((values[0] == values[1] + 1 && values[2] == values[1] + 2)
						|| (values[0] == values[1] + 2 && values[2] == values[1] + 1))) {
					return false;
				}
				break;
			case 2:
				if (!((values[0] == values[2] + 1 && values[1] == values[2] + 2)
						|| (values[0] == values[1] + 2 && values[1] == values[1] + 1))) {
					return false;
				}
				break;
			default:
				break;
			}
			break;
		case MjPairType.Triplet:
		case MjPairType.AdditionKong:
		case MjPairType.ExposedKong:
		case MjPairType.ConcealedKong:
			int pre = -1;
			for (int i = 0; i < mCards.size(); i++) {
				MjCard card = mCards.get(i);
				if (card.isBlank()) return false;
				int value = card.num % 10 == 0 ? card.num - 5 : card.num;
				if (i != 0 && value != pre) {
					return false;
				}
				pre = value;
			}
			break;
		default:
			break;
		}
		
		return true;
	}
	
	public MjCardPairs createMjCardPairs() {
		MjCardPairs pairs = new MjCardPairs(mPairsType, mDir, mCards);
		return pairs;
	}
	
	public void setDir(int dir) {
		setPairsType(mPairsType, dir);
	}
	
	public int getDir() {
		return mDir;
	}
	
	public void setPairsType(int type) {
		setPairsType(type, mDir);
	}
	
	public void setPairsType(int type, int dir) {
		this.mPairsType = type;
		this.mDir = dir;
		this.mCards.clear();
		switch (mPairsType) {
		case MjPairType.Triplet:
		case MjPairType.Sequence:
			switch (mDir) {
			case MjDir.left:
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				mCards.add(new MjCard());
				mCards.add(new MjCard());
				break;
			case MjDir.center:
				mCards.add(new MjCard());
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				mCards.add(new MjCard());
				break;
			case MjDir.right:
				mCards.add(new MjCard());
				mCards.add(new MjCard());
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				break;
			default:
				break;
			}
			break;
		case MjPairType.AdditionKong:
			switch (mDir) {
			case MjDir.left:
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				mCards.add(new MjCard());
				mCards.add(new MjCard());
				break;
			case MjDir.center:
				mCards.add(new MjCard());
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				mCards.add(new MjCard());
				break;
			case MjDir.right:
				mCards.add(new MjCard());
				mCards.add(new MjCard());
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				break;
			default:
				break;
			}
			break;
		case MjPairType.ExposedKong:
			switch (mDir) {
			case MjDir.left:
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				mCards.add(new MjCard());
				mCards.add(new MjCard());
				mCards.add(new MjCard());
				break;
			case MjDir.center:
				mCards.add(new MjCard());
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				mCards.add(new MjCard());
				mCards.add(new MjCard());
				break;
			case MjDir.right:
				mCards.add(new MjCard());
				mCards.add(new MjCard());
				mCards.add(new MjCard());
				mCards.add(new MjCard(MjSetting._face_down, MjDir.horizontal));
				break;
			default:
				break;
			}
			break;
		case MjPairType.ConcealedKong:
			for (int i = 0; i < 4; i++) {
				mCards.add(new MjCard());
			}
			break;
		default:
			break;
		}
		mTouchCard = null;
		mTouchRect = null;
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
		switch (mPairsType) {
		case MjPairType.Sequence:
			card.setData(num);
			break;
		case MjPairType.Triplet:
		case MjPairType.AdditionKong:
		case MjPairType.ExposedKong:
		case MjPairType.ConcealedKong:
			card.setData(num);
//			for (int i = 0; i < mCards.size(); i++) {
//				mCards.get(i).setData(num);
//			}
			break;
		default:
			break;
		}
		if (card.id == mCards.get(mCards.size() - 1).id) {
			setTouchCard(mCards.get(0));
		} else {
			for (int i = 0; i < mCards.size() - 1; i++) {
				if (card.id == mCards.get(i).id) {
					setTouchCard(mCards.get(i + 1));
					break;
				}
			}
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
		switch (mPairsType) {
		case MjPairType.Sequence:
			card.setBlank();
			break;
		case MjPairType.Triplet:
		case MjPairType.AdditionKong:
		case MjPairType.ExposedKong:
		case MjPairType.ConcealedKong:
			for (int i = 0; i < mCards.size(); i++) {
				mCards.get(i).setBlank();
			}
			break;
		default:
			break;
		}	
		invalidate();
		return true;
	}
	
	@Override
	public boolean removeTouchItem() {
		return removeMjCard();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec); 
        //int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int minLen = dm.widthPixels > dm.heightPixels ? dm.heightPixels : dm.widthPixels;
        if (widthSize > minLen) {
        	mjWidth = widthSize / 16;
        } else {
        	mjWidth = widthSize / 8;
		}
		mjHeight = 128 * mjWidth / 82;
		mjPadding = mjWidth / 4;
		
		int width = widthSize, height = mjWidth * 3;
        setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int cTop = getHeight() / 2 + mjWidth - mjHeight;
		int leftPoint = calculateLeftPoint();
		// 画暗牌
		leftPoint = onDrawCards(canvas, cTop, leftPoint);		
		// 画操作符
		if (touchMode) {
			onDrawTouchRect(canvas);
		}
	}
	
	private int calculateLeftPoint() {
		int leftPoint = mjPadding;
		switch (mPairsType) {
		case MjPairType.Triplet:
		case MjPairType.Sequence:
		case MjPairType.AdditionKong:
			leftPoint = (getWidth() - 2 * mjWidth - mjHeight) / 2;
			break;
		case MjPairType.ExposedKong:
			leftPoint = (getWidth() - 3 * mjWidth - mjHeight) / 2;
			break;
		case MjPairType.ConcealedKong:
			leftPoint = (getWidth() - 4 * mjWidth) / 2;
			break;
		default:
			break;
		}
		return leftPoint;
	}
	
	private int onDrawCards(Canvas canvas, int cTop, int leftPoint) {
		Matrix matrix = new Matrix();
		boolean lastTag = false;
		for (int i = 0; i < mCards.size(); i++) {
			MjCard card = mCards.get(i);
			Bitmap dBitmap = BitmapFactory.decodeResource(getResources(), 
					MjSetting.getMahjongBitmapRes(card.num));
			switch (mPairsType) {
			case MjPairType.Triplet:
			case MjPairType.Sequence:
			case MjPairType.AdditionKong:
			case MjPairType.ExposedKong:
				if (card.dir == MjDir.vertical) {
					card.setRect(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
					canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
					leftPoint += mjWidth;
				} else {
					matrix.reset();
					matrix.postRotate(-90f);
					dBitmap = Bitmap.createBitmap(dBitmap, 0, 0, 
							dBitmap.getWidth(), dBitmap.getHeight(), matrix, true); // 向左旋转90度
					if (!lastTag) {
						lastTag = true;
						card.setRect(leftPoint, cTop + mjHeight - mjWidth, 
										leftPoint + mjHeight, cTop + mjHeight);
						canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
					} else {
						lastTag = false;
						leftPoint -= mjHeight;
						card.setRect(leftPoint, cTop + mjHeight - mjWidth - mjWidth, 
										leftPoint + mjHeight, cTop + mjHeight - mjWidth);
						canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
					}					
					leftPoint += mjHeight;
				}
				break;
			case MjPairType.ConcealedKong:
//				if (i == 0 || i == mCards.size() - 1) {
//					dBitmap = BitmapFactory.decodeResource(getResources(), 
//							MjSetting.getMahjongBitmapRes(MjSetting._face_down));
//				}
				card.setRect(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
				canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
				leftPoint += mjWidth;
				break;
			default:
				break;
			}
		}			
		//pairs.calculateRect();	
		return leftPoint;
	}
		
	private void onDrawTouchRect(Canvas canvas) {
		if (mTouchRect != null) {
			float padding = 5;
			float length = mjWidth / 5;
			int angle = 15;
			float height1 = length * sin(angle);
			float height2 = length * cos(30 - angle);
			// 左上三角形
			Path path_l_t = new Path();
			path_l_t.moveTo(mTouchRect.left, mTouchRect.top - padding - height1);
			path_l_t.lineTo(mTouchRect.left + height2, mTouchRect.top - padding);
			path_l_t.lineTo(mTouchRect.left + height2 - height1, mTouchRect.top - padding - height2);
			path_l_t.close();
			canvas.drawPath(path_l_t, mPaint);
			// 右上三角形
			Path path_r_t = new Path();
			path_r_t.moveTo(mTouchRect.right, mTouchRect.top - padding - height1);
			path_r_t.lineTo(mTouchRect.right - height2, mTouchRect.top - padding);
			path_r_t.lineTo(mTouchRect.right - height2 + height1, mTouchRect.top - padding - height2);
			path_r_t.close();
			canvas.drawPath(path_r_t, mPaint);
			// 左下三角形
			Path path_l_b = new Path();
			path_l_b.moveTo(mTouchRect.left, mTouchRect.bottom + padding + height1);
			path_l_b.lineTo(mTouchRect.left + height2, mTouchRect.bottom + padding);
			path_l_b.lineTo(mTouchRect.left + height2 - height1, mTouchRect.bottom + padding + height2);
			path_l_b.close();
			canvas.drawPath(path_l_b, mPaint);
			// 右下三角形
			Path path_r_b = new Path();
			path_r_b.moveTo(mTouchRect.right, mTouchRect.bottom + padding + height1);
			path_r_b.lineTo(mTouchRect.right - height2, mTouchRect.bottom + padding);
			path_r_b.lineTo(mTouchRect.right - height2 + height1, mTouchRect.bottom + padding + height2);
			path_r_b.close();
			canvas.drawPath(path_r_b, mPaint);
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

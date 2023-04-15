package com.mahjong.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mahjong.R;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;
import com.mahjong.common.MjDir;
import com.mahjong.common.MjPairType;
import com.mahjong.common.MjSetting;
import com.mahjong.item.MjFanBean;

import android.content.Context;
import android.content.res.TypedArray;
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

public class MahjongSpectrum extends MahjongBaseView {

	/**
	 * 牌谱显示行数：单行 或 双行
	 */
	private boolean singleLine;
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
	private List<MjCard> mDarkNums = new ArrayList<MjCard>();
	/**
	 * 已明牌牌谱对
	 */
	private List<MjCardPairs> mBrightNums = new ArrayList<MjCardPairs>();
	/**
	 * 听牌
	 */
	private MjCard mWinNum = new MjCard();
	/**
	 * 点击区域
	 */
	private RectF mTouchRect = null;
	/**
	 * 点击单牌
	 */
	private MjCard mTouchCard = null;
	/**
	 * 点击牌对
	 */
	private MjCardPairs mTouchCardPairs = null;
	/**
	 * 添加明牌区域
	 */
	private RectF mAddRect = null;
	/**
	 * 是否显示添加区域
	 */
	private boolean isShowAddRect;
	/**
	 * 是否标记添加区域
	 */
	private boolean isMarkWinRect;
	/**
	 * 宝牌列表
	 */
	private int[] mDoraList;
	/**
	 * 是否使能控件
	 */
	private boolean enable;
	/**
	 * 是否显示听牌
	 */
	private boolean isShowWinNum;
	
	public MahjongSpectrum(Context context) {
		this(context, null);
	}
	
	public MahjongSpectrum(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MahjongSpectrum(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.MahjongSpectrum);
		singleLine = a.getBoolean(R.styleable.MahjongSpectrum_singleLine, true);
		touchMode = a.getBoolean(R.styleable.MahjongSpectrum_touchMode, false);
		isShowAddRect = a.getBoolean(R.styleable.MahjongSpectrum_showAddRect, true);
		enable = a.getBoolean(R.styleable.MahjongSpectrum_enable, true);
		isMarkWinRect = false;
		isShowWinNum = true;
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
	
	public void setMarkWinRect(int[] doraList) {
		this.isMarkWinRect = true;
		mDoraList = doraList;
	}
	
	public int[] getDoraList() {
		return mDoraList;
	}
	
	public void setTouchRectColor(int color) {
		mPaint.setColor(color);
	}
	
	public void setSingleLine(boolean singleLine) {
		this.singleLine = singleLine;
	}
	
	public void setTouchMode(boolean touchMode) {
		this.touchMode = touchMode;
	}
	
	public void showStartItem() {
		for (MjCard card : mDarkNums) {
			if (card.isBlank()) {
				setTouchCard(card);
				invalidate();
				return;
			}
		}
		if (mWinNum.isBlank()) {
			setTouchCard(mWinNum);
			invalidate();
			return;
		}
		setTouchCard(mDarkNums.get(0));
		invalidate();
	}
	
	@Override
	public void reset() {
		mWinNum.reset();
		mDarkNums.clear();
		mBrightNums.clear();
		for (int i = 0; i < 13; i++) {
			mDarkNums.add(new MjCard());
		}
		clearTouchItem();
		invalidate();
	}
	
	@Override
	public String toString() {
		return MjFanBean.createMjString(mDarkNums, mBrightNums, mWinNum);
	}
	
	public boolean checkIsComplete() {
		if (mWinNum.isBlank()) {
			return false;
		}
		for (int i = 0; i < mDarkNums.size(); i++) {
			if (mDarkNums.get(i).isBlank()) {
				return false;
			}
		}
		for (int i = 0; i < mBrightNums.size(); i++) {
			if (mBrightNums.get(i).isBlank()) {
				return false;
			}
		}
		return true;
	}
	
	public List<MjCard> getDarkNums() {
		return mDarkNums;
	}
	
	public List<MjCardPairs> getBrightNums() {
		return mBrightNums;
	}
	
	public MjCard getWinNum() {
		return mWinNum;
	}
	
	public void copy(List<MjCard> cards, List<MjCardPairs> pairs, MjCard winCard) {
		if (winCard != null) {
			mWinNum.setData(winCard);
		} else {
			mWinNum.reset();
		}
		mDarkNums.clear();
		mBrightNums.clear();
		if (cards == null && pairs == null) {
			for (int i = 0; i < 13; i++) {
				mDarkNums.add(new MjCard());
			}
		} else {
			if (cards != null) {
				for (MjCard c : cards) {
					mDarkNums.add(new MjCard(c));
				}
			}
			if (pairs != null) {
				for (MjCardPairs p : pairs) {
					mBrightNums.add(new MjCardPairs(p));
				}	
			}
		}
		clearTouchItem();
		invalidate();
	}	
	
	public void copy(List<MjCard> cards, List<MjCardPairs> pairs, MjCard winCard, boolean isShowWinCard) {
		isShowWinNum = isShowWinCard;
		copy(cards, pairs, winCard);
	}
	
	public void setData(List<MjCard> cards, List<MjCardPairs> pairs, MjCard winCard) {
		if (cards == null) {
			mDarkNums.clear();
			for (int i = 0; i < 13; i++) {
				mDarkNums.add(new MjCard());
			}
		} else {
			mDarkNums = cards;
		}
		if (winCard == null) {
			mWinNum.reset();
		} else {
			this.mWinNum = winCard;
		}
		if (pairs == null) {
			mBrightNums.clear();
		} else {
			mBrightNums = pairs;
		}
		clearTouchItem();
		invalidate();
	}
	
	public void sortDrakMjCards() {
		MjSetting.sortMjCardList(mDarkNums);		
	}
	
	public boolean setWinMjCard(int num) {
		mWinNum.num = num;
		invalidate();
		return true;
	}
	
	public boolean resetWinMjCard() {
		mWinNum.num = MjSetting._face_down;
		invalidate();
		return true;
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
			if (card.id == mWinNum.id) {
				return setWinMjCard(num);
			}
			for (int i = 0; i < mDarkNums.size(); i++) {
				MjCard mFirstBlank = mDarkNums.get(i);
				if (mFirstBlank.isBlank()) {
					mFirstBlank.num = num;
					if (i == mDarkNums.size() - 1) {
//						setTouchCard(mFirstBlank);
						setTouchCard(mWinNum);
					} else {
						setTouchCard(mDarkNums.get(i + 1));
					}
					sortDrakMjCards();
					invalidate();
					break;
				}
			}
		} else {
			card.num = num;
			int id = card.id;
			sortDrakMjCards();
			for (int i = 0; i < mDarkNums.size(); i++) {
				MjCard tmp = mDarkNums.get(i);
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
			if (card.id == mWinNum.id) {
				return resetWinMjCard();
			}
			for (int i = 0; i < mDarkNums.size(); i++) {
				MjCard frontCard = mDarkNums.get(i);
				if (frontCard.id == card.id) {
					for (int j = mDarkNums.size() - 1; j >= i; j--) {
						MjCard behindCard = mDarkNums.get(j);
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
			sortDrakMjCards();
			for (int i = 0; i < mDarkNums.size(); i++) {
				MjCard tmp = mDarkNums.get(i);
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
	
	public boolean canAddMjCardPairs() {
		if (mBrightNums.size() < 4) {
			int blankCount = 0;
			for (int i = mDarkNums.size() - 1; i >= 0; i--) {
				if (mDarkNums.get(i).isBlank()) {
					blankCount++;
				} else {
					break;
				}
			}
			if (blankCount >= 3) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean addMjCardPairs(MjCardPairs pairs) {
		boolean canAdd = canAddMjCardPairs();
		if (canAdd) {
			mBrightNums.add(pairs);
			mDarkNums.remove(mDarkNums.size() - 1);
			mDarkNums.remove(mDarkNums.size() - 1);
			mDarkNums.remove(mDarkNums.size() - 1);
			invalidate();
		}		
		return canAdd;
	}
	
	@Override
	public boolean removeMjCardPairs() {
		if (mTouchCardPairs != null) {
			return removeMjCardPairs(mTouchCardPairs);
		}
		return false;
	}
	
	@Override
	public boolean removeMjCardPairs(MjCardPairs pairs) {
		for (Iterator<MjCardPairs> iterator = mBrightNums.iterator(); iterator.hasNext();) {
			MjCardPairs m = iterator.next();
			if (m.id == pairs.id) {
				iterator.remove();
				for (int i = 0; i < 3; i++) {
					mDarkNums.add(new MjCard());
				}
				break;
			}
		}
		mTouchCardPairs = null;
		mTouchRect = null;
		invalidate();				
		return true;
	}
	
	@Override
	public boolean removeTouchItem() {
		if (mTouchRect != null) {
			if (mTouchCard != null) {
				return removeMjCard(mTouchCard);
			}
			if (mTouchCardPairs != null) {
				return removeMjCardPairs(mTouchCardPairs);
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
		
		int width = widthSize, height = singleLine ? mjWidth * 3 : mjWidth * 6;
        setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int cTop, cTopUp, CTopDown;
		if (singleLine) {
			cTop = getHeight() / 2 + mjWidth - mjHeight;
			cTopUp = CTopDown = 0;
		} else {
			cTop = getHeight() / 2 - mjWidth;
			cTopUp = getHeight() / 4 + mjWidth - mjHeight;
			CTopDown = getHeight() * 3 / 4 + mjWidth - mjHeight;
		}
		int leftPoint = mjPadding;
		// 画暗牌
		leftPoint = onDrawDrakNums(canvas, cTop, leftPoint);
		// 画明牌		
		if (leftPoint != mjPadding) leftPoint += (mjWidth / 2);
		if (singleLine) {
			leftPoint = onDrawBrightNums(canvas , cTop, leftPoint);
		} else {
			leftPoint = onDrawBrightNums(canvas , cTop, cTopUp, CTopDown, leftPoint);
		}		
		// 画胡牌
		leftPoint += (mjWidth / 2);
		leftPoint = onDrawWinNum(canvas, cTop, leftPoint);
		// 画添加按钮
		if (touchMode && isShowAddRect && mBrightNums.size() < 4) {
			leftPoint += mjWidth;
			onDrawAddIcon(canvas, cTop, leftPoint);
		} else {
			mAddRect = null;
		}
		// 画操作符
		if (touchMode) {
			onDrawTouchRect(canvas);
		}
	}
	
	private int onDrawDrakNums(Canvas canvas, int cTop, int leftPoint) {
		if (mDarkNums == null) {
			return leftPoint;
		}
		for (MjCard card : mDarkNums) {
			Bitmap dBitmap = BitmapFactory.decodeResource(getResources(), 
					MjSetting.getMahjongBitmapRes(card.num));
			card.setRect(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
			canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
			leftPoint += mjWidth;
		}
		return leftPoint;
	}
	
	private int onDrawBrightNums(Canvas canvas, int cTop, int leftPoint) {
		if (mBrightNums == null) {
			return leftPoint;
		}
		Matrix matrix = new Matrix();
		for (MjCardPairs pairs : mBrightNums) {
			boolean lastTag = false;
			for (int i = 0; i < pairs.mCardList.size(); i++) {
				MjCard card = pairs.mCardList.get(i);
				Bitmap dBitmap = BitmapFactory.decodeResource(getResources(), 
						MjSetting.getMahjongBitmapRes(card.num));
				switch (pairs.mType) {
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
					if (i == 0 || i == pairs.mCardList.size() - 1) {
						dBitmap = BitmapFactory.decodeResource(getResources(), 
								MjSetting.getMahjongBitmapRes(MjSetting._face_down));
					}
					card.setRect(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
					canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
					leftPoint += mjWidth;
					break;
				default:
					card.setRect(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
					canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
					leftPoint += mjWidth;
					break;
				}
			}			
			leftPoint += (mjWidth / 8);
			pairs.calculateRect();
		}
		leftPoint -= (mjWidth / 8);
		return leftPoint;
	}
	

	private int onDrawBrightNums(Canvas canvas, int cTop, int cTopUp, int cTopDown, int leftPoint) {
		if (mBrightNums == null) {
			return leftPoint;
		}
		int copyLeftPoint = leftPoint;
		int rightPoint = 0;
		Matrix matrix = new Matrix();
		int changeSize = -1;
		if (mBrightNums.size() > 2) { // 大于2个时分2行
			cTop = cTopUp;
			changeSize = 2;
		} else if (mBrightNums.size() == 2) { // 等于2个且有1个大明杠或暗杠时分2行
			if (mBrightNums.get(0).mType == MjPairType.ExposedKong 
					|| mBrightNums.get(0).mType == MjPairType.ConcealedKong
					|| mBrightNums.get(1).mType == MjPairType.ExposedKong
					|| mBrightNums.get(1).mType == MjPairType.ConcealedKong) {
				cTop = cTopUp;
				changeSize = 1;
			}
		}
		for (int m = 0; m < mBrightNums.size(); m++) {
			MjCardPairs pairs = mBrightNums.get(m);
			if (m == changeSize) {
				cTop = cTopDown;
				rightPoint = leftPoint;
				leftPoint = copyLeftPoint;
			}
			boolean lastTag = false;
			for (int i = 0; i < pairs.mCardList.size(); i++) {
				MjCard card = pairs.mCardList.get(i);
				Bitmap dBitmap = BitmapFactory.decodeResource(getResources(), 
						MjSetting.getMahjongBitmapRes(card.num));
				switch (pairs.mType) {
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
					if (i == 0 || i == pairs.mCardList.size() - 1) {
						dBitmap = BitmapFactory.decodeResource(getResources(), 
								MjSetting.getMahjongBitmapRes(MjSetting._face_down));
					}
					card.setRect(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
					canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
					leftPoint += mjWidth;
					break;
				default:
					card.setRect(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
					canvas.drawBitmap(dBitmap, null, card.rect, mBitmapPaint);
					leftPoint += mjWidth;
					break;
				}
			}			
			leftPoint += (mjWidth / 4);
			pairs.calculateRect();
		}
		if (rightPoint > leftPoint) leftPoint = rightPoint;
		leftPoint -= (mjWidth / 4);
		return leftPoint;
	}
	
	private int onDrawWinNum(Canvas canvas, int cTop, int leftPoint) {
		if (mWinNum == null || !isShowWinNum) {
			return leftPoint;
		}
		Bitmap winBitmap = BitmapFactory.decodeResource(getResources(), 
				MjSetting.getMahjongBitmapRes(mWinNum.num));
		mWinNum.setRect(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
		if (isMarkWinRect && mDoraList != null) {
			for (int dora : mDoraList) {
				if (dora > 0 && dora == mWinNum.num) {
					mPaint.setColor(Color.parseColor("#a0ff3030"));
					mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
					canvas.drawRect(leftPoint - 5, cTop - 5, 
							leftPoint + mjWidth + 5, cTop + mjHeight + 5, mPaint);
					mPaint.setColor(Color.parseColor("#a0ffffff"));
					mPaint.setStyle(Paint.Style.STROKE);
					break;
				}
			}
		}
		canvas.drawBitmap(winBitmap, null, mWinNum.rect, mBitmapPaint);
		return leftPoint + mjWidth;
	}
	
	private int onDrawAddIcon(Canvas canvas, int cTop, int leftPoint) {
		Bitmap addBitmap = BitmapFactory.decodeResource(getResources(), 
				R.drawable.b_add);
		mAddRect = new RectF(leftPoint, cTop, leftPoint + mjWidth, cTop + mjHeight);
		canvas.drawBitmap(addBitmap, null, mAddRect, mBitmapPaint);
		return leftPoint + mjWidth;
	}
	
	private void onDrawTouchRect(Canvas canvas) {
		if (mTouchRect != null) {
			float padding = 3;
			float length = mjWidth / 3;
			if (mTouchRect.width() <= mjWidth) {
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
			} else {
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
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!enable) return false;
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
		for (MjCard card : mDarkNums) {
			if (card.checkInRect(x, y)) {
				setTouchCard(card);
				if (touchMode && mListener != null) {
					mListener.onTouchOne(this, card);
				}
				return true;
			}
		}
		for (MjCardPairs pairs : mBrightNums) {
			if (pairs.checkInRect(x, y)) {
				setTouchCardPairs(pairs);
				if (touchMode && mListener != null) {
					mListener.onTouchPairs(this, pairs);
				}
				return true;
			}
		}
		if (mWinNum.checkInRect(x, y)) {
			setTouchCard(mWinNum);
			if (touchMode && mListener != null) {
				mListener.onTouchOne(this, mWinNum);
			}
			return true;
		}		
		this.mTouchCard = null;
		this.mTouchCardPairs = null;
		this.mTouchRect = null;
		if (isDown && mAddRect != null && mAddRect.contains(x, y)) {
			if (mListener != null) {
				mListener.onTouchAdd(this);
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
				if (mTouchCard.id == mWinNum.id) {
					if (mBrightNums.size() == 0) {
						setTouchCard(mDarkNums.get(mDarkNums.size() - 1));
					} else {
						setTouchCardPairs(mBrightNums.get(mBrightNums.size() - 1));
					}
				} else if (mTouchCard.id == mDarkNums.get(0).id) {
					setTouchCard(mWinNum);
				} else {
					for (int i = mDarkNums.size() - 1; i > 0; i--) {
						MjCard card = mDarkNums.get(i);
						if (mTouchCard.id == card.id) {
							setTouchCard(mDarkNums.get(i - 1));
							break;
						}
					}
				}
				invalidate();			
			} else if (mTouchCardPairs != null) {
				if (mTouchCardPairs.id == mBrightNums.get(0).id) {
					setTouchCard(mDarkNums.get(mDarkNums.size() - 1));
				} else {
					for (int i = mBrightNums.size() - 1; i > 0; i--) {
						MjCardPairs pairs = mBrightNums.get(i);
						if (mTouchCardPairs.id == pairs.id) {
							setTouchCardPairs(mBrightNums.get(i - 1));
							break;
						}
					}
				}
				invalidate();
			}
		} else {
			setTouchCard(mDarkNums.get(0));
			invalidate();
		}
	}
	
	@Override
	public void onPressRight() {
		if (mTouchRect != null) {
			if (mTouchCard != null) {
				if (mTouchCard.id == mWinNum.id) {
					setTouchCard(mDarkNums.get(0));
				} else if (mTouchCard.id == mDarkNums.get(mDarkNums.size() - 1).id) {
					if (mBrightNums.size() == 0) {
						setTouchCard(mWinNum);
					} else {
						setTouchCardPairs(mBrightNums.get(0));
					}
				} else {
					for (int i = 0; i < mDarkNums.size() - 1; i++) {
						if (mTouchCard.id == mDarkNums.get(i).id) {
							setTouchCard(mDarkNums.get(i + 1));
							break;
						}
					}
				}
				invalidate();
			} else if (mTouchCardPairs != null) {
				if (mTouchCardPairs.id == mBrightNums.get(mBrightNums.size() - 1).id) {
					setTouchCard(mWinNum);
				} else {
					for (int i = 0; i < mBrightNums.size() - 1; i++) {
						if (mTouchCardPairs.id == mBrightNums.get(i).id) {
							setTouchCardPairs(mBrightNums.get(i + 1));
							break;
						}
					}
				}
				invalidate();
			}
		} else {
			setTouchCard(mWinNum);
			invalidate();
		}
	}
	
	private void setTouchCard(MjCard card) {
		mTouchCard = card;
		mTouchCardPairs = null;
		mTouchRect = mTouchCard.rect;
	}
	
	private void setTouchCardPairs(MjCardPairs pairs) {
		mTouchCard = null;
		mTouchCardPairs = pairs;
		mTouchRect = mTouchCardPairs.mRect;
	}

	
}

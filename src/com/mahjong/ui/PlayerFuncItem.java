package com.mahjong.ui;

import com.mahjong.R;
import com.mahjong.model.Player;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PlayerFuncItem extends View {

	private static final int icon_none = R.drawable.emo_0_1; // 默认头像
	private static final String FrameColor = "#349abe";
	private static final String BackgroudColor = "#401E90FF"; // 蓝色
	private static final String SelectColor = "#4000FFFF"; // 绿色
	private static final String DealerColor = "#A0EE0000"; // 红色	
	private static final String LizhiColor = "#FFC600"; // 立直背景色
	private static final String DoubleLizhiColor = "#A0EE0000"; // 双立直背景色
	
	public static final int POS_BOTTOM 	= 0;
	public static final int POS_RIGHT 	= 1;
	public static final int POS_TOP 	= 2;
	public static final int POS_LEFT 	= 3;
	
	enum PathType {
		None, Zimo, Ronghe, Lizhi, Feng, Icon, Bomb
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
	
	private Player mPlayer;	// 玩家
	private int mOrgIndex = 0;	// 玩家初始顺位（0：东，1：南，2：西，3：北）
	private int mCurIndex = 0;	// 玩家当前顺位（0：东，1：南，2：西，3：北）
	private int mPosition;	// 控件方位
	private float mRotateAngle = 0; // 旋转角度
	private int mLizhiState = 0; // 玩家是否立直：0-》无，1-》立直，2-》双立直
	private boolean mChicken = true; // 玩家是否烧鸡
	private PathType mSelectPath = PathType.None;
	private int rank = 0; // 玩家排位（1-4）
	
	private Bitmap mIcon;		// 玩家头像
	private Bitmap mImgLeader; 	// 东风定位
	private Bitmap mImgChicken; // 烧鸡
	private Bitmap mImgBomb;	// 点炮
	
	private RectF mIconRectF;
	private Region mIconRegion;		// 头像区域
	private Region mZimoRegion;		// 自摸区域
	private Region mRongheRegion;	// 荣和区域
	private Region mFengRegion;		// 风向区域
	private Region mLizhiRegion;	// 立直区域
	private RectF mBombRect;		// 点炮区域
	
	private OnPlayerFuncItemListener mListener;

	public PlayerFuncItem(Context context) {
		this(context, null);
	}

	public PlayerFuncItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PlayerFuncItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PlayerFuncItem);
		mPosition = a.getInt(R.styleable.PlayerFuncItem_position, POS_BOTTOM);
		a.recycle();
		initPaint();
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
		
		mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mBitmapPaint.setStyle(Paint.Style.FILL);
		mBitmapPaint.setTextAlign(Paint.Align.CENTER);
		
		mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mBgPaint.setStyle(Paint.Style.FILL);
		mBgPaint.setTextAlign(Paint.Align.CENTER);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 14));
	}
	
	/**
	 * 获取控件方位
	 * 
	 * @return
	 */
	public int getItemPosition() {
		return mPosition;
	}
	
	public void setPlayer(Player player, int orgIndex, int curIndex) {
		this.mPlayer = player;
		this.mOrgIndex = orgIndex;
		this.mCurIndex = curIndex;
		mIcon = null;
	}
	
	public void resetPlayer(Player player) {
		mPlayer = player;
		mIcon = null;
		invalidate();
	}
	
	public Player getPlayer() {
		return mPlayer;
	}
	
	/**
	 * 获取玩家原始风向
	 * 
	 * @return
	 */
	public int getOriginalIndex() {
		return mOrgIndex;
	}
	
	/**
	 * 设置玩家当前风向
	 * 
	 * @param index
	 */
	public void setCurrentIndex(int index) {
		this.mCurIndex = index;
		invalidate();
	}
	
	public void setLizhiState(int lizhi) {
		this.mLizhiState = lizhi;
		invalidate();
	}
	
	public void setChickenState(boolean isChicken) {
		this.mChicken = isChicken;
		invalidate();
	}
	
	/**
	 * 设置玩家排名
	 * 
	 * @param rank
	 */
	public void setPlayerRank(int rank) {
		this.rank = rank;
		this.mIcon = null;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int width = getWidth() - getPaddingLeft() - getPaddingRight();
		int height = getHeight() - getPaddingBottom() - getPaddingTop();
		float marginLen = width > height ? height / 4 : width / 4;
		
		switch (mPosition) {
		case POS_TOP:
			mRotateAngle = 180;
			break;
		case POS_LEFT:
			mRotateAngle = 90;
			break;
		case POS_BOTTOM:
			mRotateAngle = 0;
			break;
		case POS_RIGHT:
			mRotateAngle = -90;
			break;
		default:
			break;
		}		
				
		// 绘制边框
		onDrawLines(canvas, width, height, marginLen);
		// 绘制按钮
		onDrawBottons(canvas, width, height, marginLen);
		// 绘制头像
		onDrawPlayerIcon(canvas, width, height);
				
	}
	
	/**
	 * 绘制边框
	 * 
	 * @param canvas
	 * @param width
	 * @param height
	 * @param marginLen
	 */
	protected void onDrawLines(Canvas canvas, int width, int height, float marginLen) {
		if (width < height) {
			int tmp = width;
			width = height;
			height = tmp;
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas newCanvas = new Canvas(bitmap);
		
		mPaint.setStrokeWidth(3);
		PointF leftBottomPoint1 = new PointF(0, height);
		PointF leftBottomPoint2 = new PointF(width / 2 - height - marginLen, leftBottomPoint1.y);
		PointF leftBottomPoint3 = new PointF(leftBottomPoint2.x + marginLen, leftBottomPoint2.y);
		PointF leftTopPoint1 = new PointF(width / 2 - height, height * 2 / 3);
		PointF leftTopPoint2 = new PointF(leftTopPoint1.x + marginLen, leftTopPoint1.y);
		PointF rightTopPoint1 = new PointF(width / 2 + height, leftTopPoint1.y);
		PointF rightTopPoint2 = new PointF(rightTopPoint1.x - marginLen, leftTopPoint1.y);
		PointF rightBottomPoint1 = new PointF(width, height);
		PointF rightBottomPoint2 = new PointF(width / 2 + height + marginLen, rightBottomPoint1.y);
		PointF rightBottomPoint3 = new PointF(rightBottomPoint2.x - marginLen, rightBottomPoint1.y);
		newCanvas.drawLine(leftTopPoint1.x, leftTopPoint1.y, 
				rightTopPoint1.x, rightTopPoint1.y, mPaint); 		// 中间横线
		newCanvas.drawLine(leftBottomPoint1.x, leftBottomPoint1.y, 
				leftBottomPoint3.x, leftBottomPoint3.y, mPaint); 	// 左横线
		newCanvas.drawLine(leftBottomPoint2.x, leftBottomPoint2.y, 
				leftTopPoint1.x, leftTopPoint1.y, mPaint); 			// 左一斜线
		newCanvas.drawLine(leftBottomPoint3.x, leftBottomPoint3.y, 
				leftTopPoint2.x, leftTopPoint2.y, mPaint);			// 左二斜线
		newCanvas.drawLine(rightBottomPoint3.x, rightBottomPoint3.y, 
				rightBottomPoint1.x, rightBottomPoint1.y, mPaint);  // 右横线
		newCanvas.drawLine(rightTopPoint1.x, rightTopPoint1.y, 
				rightBottomPoint2.x, rightBottomPoint2.y, mPaint); 	// 右一斜线
		newCanvas.drawLine(rightBottomPoint3.x, rightBottomPoint3.y, 
				rightTopPoint2.x, rightTopPoint2.y, mPaint);		// 右二斜线
		Path path1 = new Path();
		path1.moveTo(leftBottomPoint2.x, leftBottomPoint2.y);
		path1.lineTo(leftTopPoint1.x, leftTopPoint1.y);
		path1.lineTo(leftTopPoint2.x, leftTopPoint2.y);
		path1.lineTo(leftBottomPoint3.x, leftBottomPoint3.y);
		path1.close();
		newCanvas.drawPath(path1, mBgPaint); // 左菱形区域
		Path path2 = new Path();
		path2.moveTo(rightBottomPoint2.x, rightBottomPoint2.y);
		path2.lineTo(rightTopPoint1.x, rightTopPoint1.y);
		path2.lineTo(rightTopPoint2.x, rightTopPoint2.y);
		path2.lineTo(rightBottomPoint3.x, rightBottomPoint3.y);
		path2.close();
		newCanvas.drawPath(path2, mBgPaint); // 右菱形区域
		// 画名字
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 14));
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        int baseLineY = (int) ((leftTopPoint1.y + leftBottomPoint1.y - fontMetrics.top - fontMetrics.bottom) / 2); //基线中间点的y轴计算公式
        newCanvas.drawText(mPlayer != null ? mPlayer.getNickName() : getResources().getString(R.string.pc), 
				width / 2, baseLineY, mTextPaint);		
		// 画烧鸡
		if (mChicken) {
			if (mImgChicken == null) {
				mImgChicken = BitmapFactory.decodeResource(getResources(), R.drawable.mj_chicken);
			}
			float len = (leftBottomPoint1.y - leftTopPoint1.y) * 5 / 6;
			RectF chickenRectF = new RectF(leftTopPoint2.x, leftBottomPoint3.y - len,
					leftTopPoint2.x + len, leftBottomPoint3.y);
			newCanvas.drawBitmap(mImgChicken, null, chickenRectF, mBitmapPaint);
		}	
		
		bitmap = ImageTool.rotateTo(mRotateAngle, bitmap);
		canvas.drawBitmap(bitmap, getPaddingLeft(), getPaddingTop(), mBitmapPaint);		
	}
	
	/**
	 * 绘制按钮
	 * 
	 * @param canvas
	 * @param width
	 * @param height
	 * @param marginLen
	 */
	protected void onDrawBottons(Canvas canvas, int width, int height, float marginLen) {
		switch (mPosition) {
		case POS_TOP:
			onDrawBottonsTop(canvas, width, height, marginLen);
			break;
		case POS_LEFT:
			onDrawBottonsLeft(canvas, width, height, marginLen);
			break;
		case POS_BOTTOM:
			onDrawBottonsBottom(canvas, width, height, marginLen);
			break;
		case POS_RIGHT:
			onDrawBottonsRight(canvas, width, height, marginLen);
			break;
		default:
			break;
		}	
	}
	
	private void onDrawBottonsBottom(Canvas canvas, int width, int height, float marginLen) {
		mBgPaint.setStrokeWidth(3);
		float diffLen = ValueTool.dp2px(getContext(), 8);
		// 下层属性
		float sLen = height * 4 / 9;
		float byTop = height * 2 / 3 + getPaddingTop();
		float byBottom = height + getPaddingTop() - 10;
		float marginLen2 = (byBottom - byTop) * marginLen / (height / 3);
		float extendHeight = height / 6;
		// 画自摸
		float bxlRight = getWidth() / 2 - height - marginLen / 2;
		float bxlLeft = bxlRight - sLen - diffLen;
		Path mZimoPath = new Path();
		mZimoPath.moveTo(bxlLeft, byTop - extendHeight);
		mZimoPath.lineTo(bxlRight, byTop - extendHeight);
		mZimoPath.lineTo(bxlRight, byBottom - extendHeight - diffLen / 2);
		mZimoPath.lineTo(bxlRight - marginLen / 2 - diffLen / 2, byBottom);
		mZimoPath.lineTo(bxlLeft - marginLen2, byBottom);
		mZimoPath.lineTo(bxlLeft - marginLen2, byBottom - marginLen2);
		mZimoPath.close();
		mZimoRegion = path2Region(mZimoPath);
		if (mSelectPath == PathType.Zimo) { // 点击自摸
			mBgPaint.setColor(Color.parseColor(SelectColor));
		}
		canvas.drawPath(mZimoPath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mZimoPath, mPaint);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 16));
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		canvas.drawText(getResources().getString(R.string.zimo), 
				(bxlLeft - marginLen2 + bxlRight) / 2, 
				(byTop - extendHeight + byBottom - fontMetrics.top - fontMetrics.bottom) / 2, mTextPaint);
		// 画荣和
		float bxrLeft = getWidth() / 2 + height + marginLen - diffLen;
		float bxrRight = bxrLeft + sLen + diffLen;
		Path mRonghePath = new Path();
		mRonghePath.moveTo(bxrRight, byTop - extendHeight);
		mRonghePath.lineTo(bxrLeft, byTop - extendHeight);
		mRonghePath.lineTo(bxrLeft, byBottom - extendHeight - diffLen / 2);
		mRonghePath.lineTo(bxrLeft + marginLen / 2 + diffLen / 2, byBottom);
		mRonghePath.lineTo(bxrRight + marginLen2, byBottom);
		mRonghePath.lineTo(bxrRight + marginLen2, byBottom - marginLen2);
		mRonghePath.close();
		mRongheRegion = path2Region(mRonghePath);
		if (mSelectPath == PathType.Ronghe) { // 点击荣和
			mBgPaint.setColor(Color.parseColor(SelectColor));
		}
		canvas.drawPath(mRonghePath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mRonghePath, mPaint);
		canvas.drawText(getResources().getString(R.string.ronghe), 
				(bxrLeft + marginLen2 + bxrRight) / 2, 
				(byTop - extendHeight + byBottom - fontMetrics.top - fontMetrics.bottom) / 2, mTextPaint);
		// 上层属性
		sLen = height / 3;
		float tyBottom = height * 2 / 3 + getPaddingTop() - 10;
		float tyTop = tyBottom - height / 3;
		// 画风位
		float txlRight = getWidth() / 2 - height / 3 - diffLen;
		float txlLeft = txlRight - diffLen - sLen ;
		Path mFengPath = new Path();
		mFengPath.moveTo(txlLeft, tyTop + diffLen);
		mFengPath.lineTo(txlLeft + diffLen, tyTop);
		mFengPath.lineTo(txlRight, tyTop);
		mFengPath.lineTo(txlRight, tyBottom);
		mFengPath.lineTo(txlLeft, tyBottom);
		mFengPath.close();
		mFengRegion = path2Region(mFengPath);
		String fengText;
		switch (mCurIndex) {
		case 0:
			fengText = getResources().getString(R.string.east);
			mBgPaint.setColor(Color.parseColor(DealerColor));
			mTextPaint.setColor(Color.BLACK);
			break;
		case 1:
			fengText = getResources().getString(R.string.south);
			break;
		case 2:
			fengText = getResources().getString(R.string.west);
			break;
		case 3:
			fengText = getResources().getString(R.string.north);
			break;
		default:
			fengText = getResources().getString(R.string.east);
			break;
		}
		canvas.drawPath(mFengPath, mBgPaint);
		canvas.drawPath(mFengPath, mPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 15));
		fontMetrics = mTextPaint.getFontMetrics();
		canvas.drawText(fengText, (txlLeft + diffLen / 2 + txlRight) / 2, 
				(tyTop + tyBottom - fontMetrics.top - fontMetrics.bottom) / 2, mTextPaint);
		mTextPaint.setColor(Color.WHITE);
		if (mOrgIndex == 0) { // 如果是初始东，画定位
			if (mImgLeader == null) {
				mImgLeader = BitmapFactory.decodeResource(getResources(), R.drawable.mj_firstplayer);
			}
			float h = tyTop - getPaddingTop();
			float w = h * mImgLeader.getWidth() / mImgLeader.getHeight();
			RectF leadRectF = new RectF(txlLeft - w / 2, getPaddingTop(), txlLeft + w / 2, tyTop);
			canvas.drawBitmap(mImgLeader, null, leadRectF, mBitmapPaint);
		}
		// 画立直
		float txrLeft= getWidth() / 2 + height / 3 + diffLen;
		float txrRight = txrLeft + diffLen + sLen;
		Path mLizhiPath = new Path();
		mLizhiPath.moveTo(txrRight, tyTop + diffLen);
		mLizhiPath.lineTo(txrRight - diffLen, tyTop);
		mLizhiPath.lineTo(txrLeft, tyTop);
		mLizhiPath.lineTo(txrLeft, tyBottom);
		mLizhiPath.lineTo(txrRight, tyBottom);
		mLizhiPath.close();
		mLizhiRegion = path2Region(mLizhiPath);
		if (mSelectPath == PathType.Lizhi && mLizhiState == 0) { // 点击立直
			mBgPaint.setColor(Color.parseColor(SelectColor));
		} else if (mLizhiState == 1) {
			mBgPaint.setColor(Color.parseColor(LizhiColor));
		} else if (mLizhiState == 2) {
			mBgPaint.setColor(Color.parseColor(DoubleLizhiColor));
		}
		canvas.drawPath(mLizhiPath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mLizhiPath, mPaint);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 12));
		fontMetrics = mTextPaint.getFontMetrics();
		String lizhiText = getResources().getString(R.string.lizhi);
//		if (mLizhiState > 0) {
//			lizhiText = getResources().getString(R.string.revoke);
//		} else {
//			lizhiText = getResources().getString(R.string.lizhi);
//		}
		canvas.drawText(lizhiText, (txrLeft + txrRight) / 2, 
				(tyTop + tyBottom - fontMetrics.top - fontMetrics.bottom) / 2, mTextPaint);
		// 画点炮
		if (mImgBomb == null) {
			mImgBomb = BitmapFactory.decodeResource(getResources(), R.drawable.mj_bomb);
			mImgBomb = ImageTool.rotateTo(mRotateAngle, mImgBomb);
		}
		float left = getWidth() / 2 + height + marginLen;
		mBombRect = new RectF(left, tyTop - extendHeight, left + tyBottom - tyTop, tyBottom - extendHeight);
		canvas.drawBitmap(mImgBomb, null, mBombRect, mBitmapPaint);		
	}
	
	private void onDrawBottonsTop(Canvas canvas, int width, int height, float marginLen) {
		mBgPaint.setStrokeWidth(3);
		float diffLen = ValueTool.dp2px(getContext(), 8);
		float centerX, centerY;
		// 上层属性
		float sLen = height * 4 / 9;
		float byTop = getPaddingTop() + 10;
		float byBottom = getPaddingTop() + height / 3;
		float marginLen2 = (byBottom - byTop) * marginLen / (height / 3);
		float extendHeight = height / 6;
		// 画自摸
		float bxlLeft = getWidth() / 2 + height + marginLen / 2;
		float bxlRight = bxlLeft + sLen + diffLen;
		Path mZimoPath = new Path();
		mZimoPath.moveTo(bxlLeft + marginLen / 2 + diffLen / 2, byTop);
		mZimoPath.lineTo(bxlRight + marginLen2, byTop);
		mZimoPath.lineTo(bxlRight + marginLen2, byTop + marginLen2);
		mZimoPath.lineTo(bxlRight, byBottom + extendHeight);
		mZimoPath.lineTo(bxlLeft, byBottom + extendHeight);
		mZimoPath.lineTo(bxlLeft, byTop + extendHeight + diffLen / 2);
		mZimoPath.close();
		mZimoRegion = path2Region(mZimoPath);
		if (mSelectPath == PathType.Zimo) { // 点击自摸
			mBgPaint.setColor(Color.parseColor(SelectColor));
		}
		canvas.drawPath(mZimoPath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mZimoPath, mPaint);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 16));
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();	
		centerX = (bxlLeft + marginLen2 + bxlRight) / 2;
		centerY = (byTop + byBottom + extendHeight) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(getResources().getString(R.string.zimo), 
				centerX, centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		// 画荣和
		float bxrRight = getWidth() / 2 - height - marginLen + diffLen;
		float bxrLeft = bxrRight - sLen - diffLen;
		Path mRonghePath = new Path();
		mRonghePath.moveTo(bxrRight - marginLen / 2 - diffLen / 2, byTop);
		mRonghePath.lineTo(bxrLeft - marginLen2, byTop);
		mRonghePath.lineTo(bxrLeft - marginLen2, byTop + marginLen2);
		mRonghePath.lineTo(bxrLeft, byBottom + extendHeight);
		mRonghePath.lineTo(bxrRight, byBottom + extendHeight);
		mRonghePath.lineTo(bxrRight, byTop + extendHeight + diffLen / 2);
		mRonghePath.close();
		mRongheRegion = path2Region(mRonghePath);
		if (mSelectPath == PathType.Ronghe) { // 点击荣和
			mBgPaint.setColor(Color.parseColor(SelectColor));
		}
		canvas.drawPath(mRonghePath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mRonghePath, mPaint);
		centerX = (bxrLeft - marginLen2 + bxrRight) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(getResources().getString(R.string.ronghe), 
				centerX, centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		// 下层属性
		sLen = height / 3;
		float tyTop = getPaddingTop() + height / 3 + 10;
		float tyBottom = tyTop + height / 3;
		// 画风位
		float txlLeft = getWidth() / 2 + height / 3 + diffLen;
		float txlRight = txlLeft + diffLen + sLen ;
		Path mFengPath = new Path();
		mFengPath.moveTo(txlLeft, tyBottom);
		mFengPath.lineTo(txlLeft, tyTop);
		mFengPath.lineTo(txlRight, tyTop);
		mFengPath.lineTo(txlRight, tyBottom - diffLen);
		mFengPath.lineTo(txlRight - diffLen, tyBottom);
		mFengPath.close();
		mFengRegion = path2Region(mFengPath);
		String fengText;
		switch (mCurIndex) {
		case 0:
			fengText = getResources().getString(R.string.east);
			mBgPaint.setColor(Color.parseColor(DealerColor));
			mTextPaint.setColor(Color.BLACK);
			break;
		case 1:
			fengText = getResources().getString(R.string.south);
			break;
		case 2:
			fengText = getResources().getString(R.string.west);
			break;
		case 3:
			fengText = getResources().getString(R.string.north);
			break;
		default:
			fengText = getResources().getString(R.string.east);
			break;
		}
		canvas.drawPath(mFengPath, mBgPaint);
		canvas.drawPath(mFengPath, mPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 15));
		fontMetrics = mTextPaint.getFontMetrics();
		centerX = (txlLeft + txlRight) / 2;
		centerY = (tyTop + tyBottom) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(fengText, centerX, 
				centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		mTextPaint.setColor(Color.WHITE);
		if (mOrgIndex == 0) { // 如果是初始东，画定位
			if (mImgLeader == null) {
				mImgLeader = BitmapFactory.decodeResource(getResources(), R.drawable.mj_firstplayer);
				mImgLeader = ImageTool.rotateTo(mRotateAngle, mImgLeader);
			}
			float h = getPaddingTop() + height - tyBottom;
			float w = h * mImgLeader.getWidth() / mImgLeader.getHeight();
			RectF leadRectF = new RectF(txlRight - w / 2, tyBottom, txlRight + w / 2, getPaddingTop() + height);
			canvas.drawBitmap(mImgLeader, null, leadRectF, mBitmapPaint);
		}
		// 画立直
		float txrRight = getWidth() / 2 - height / 3 - diffLen;
		float txrLeft = txrRight - diffLen - sLen;
		Path mLizhiPath = new Path();
		mLizhiPath.moveTo(txrRight, tyTop);
		mLizhiPath.lineTo(txrRight, tyBottom);
		mLizhiPath.lineTo(txrLeft + diffLen, tyBottom);
		mLizhiPath.lineTo(txrLeft, tyBottom - diffLen);
		mLizhiPath.lineTo(txrLeft, tyTop);
		mLizhiPath.close();
		mLizhiRegion = path2Region(mLizhiPath);
		if (mSelectPath == PathType.Lizhi && mLizhiState == 0) { // 点击立直
			mBgPaint.setColor(Color.parseColor(SelectColor));
		} else if (mLizhiState == 1) {
			mBgPaint.setColor(Color.parseColor(LizhiColor));
		} else if (mLizhiState == 2) {
			mBgPaint.setColor(Color.parseColor(DoubleLizhiColor));
		}
		canvas.drawPath(mLizhiPath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mLizhiPath, mPaint);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 12));
		fontMetrics = mTextPaint.getFontMetrics();
		String lizhiText = getResources().getString(R.string.lizhi);;
//		if (mLizhiState > 0) {
//			lizhiText = getResources().getString(R.string.revoke);
//		} else {
//			lizhiText = getResources().getString(R.string.lizhi);
//		}
		centerX = (txrLeft + txrRight) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(lizhiText, centerX, 
				centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		// 画点炮
		if (mImgBomb == null) {
			mImgBomb = BitmapFactory.decodeResource(getResources(), R.drawable.mj_bomb);
			mImgBomb = ImageTool.rotateTo(mRotateAngle, mImgBomb);
		}
		float right = getWidth() / 2 - height - marginLen;
		mBombRect = new RectF(right - tyBottom + tyTop, tyTop + extendHeight, right, tyBottom + extendHeight);
		canvas.drawBitmap(mImgBomb, null, mBombRect, mBitmapPaint);
	}
	
	private void onDrawBottonsLeft(Canvas canvas, int width, int height, float marginLen) {
		mBgPaint.setStrokeWidth(3);
		float diffLen = ValueTool.dp2px(getContext(), 8);
		float centerX, centerY;
		// 左层属性
		float sLen = width * 4 / 9;
		float lxLeft = getPaddingLeft() + 10;
		float lxRight = getPaddingLeft() + width / 3;
		float extendHeight = width / 6;
		// 画自摸
		float lytBottom = getHeight() / 2 - width - marginLen / 2; 
		float lytTop = lytBottom - sLen - diffLen;
		float marginLen2 = (lxRight - lxLeft) * marginLen / (width / 3);
		Path mZimoPath = new Path();
		mZimoPath.moveTo(lxLeft, lytBottom - marginLen / 2 - diffLen / 2);
		mZimoPath.lineTo(lxLeft, lytTop - marginLen2);
		mZimoPath.lineTo(lxLeft + marginLen2, lytTop - marginLen2);
		mZimoPath.lineTo(lxRight + extendHeight, lytTop);
		mZimoPath.lineTo(lxRight + extendHeight, lytBottom);
		mZimoPath.lineTo(lxLeft + extendHeight + diffLen / 2, lytBottom);
		mZimoPath.close();
		
		mZimoRegion = path2Region(mZimoPath);
		if (mSelectPath == PathType.Zimo) { // 点击自摸
			mBgPaint.setColor(Color.parseColor(SelectColor));
		}
		canvas.drawPath(mZimoPath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mZimoPath, mPaint);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 16));
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();	
		centerX = (lxLeft + lxRight + extendHeight) / 2;
		centerY = (lytTop - marginLen2 + lytBottom) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(getResources().getString(R.string.zimo), 
				centerX, centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		// 画荣和
		float lybTop = getHeight() / 2 + width + marginLen - diffLen;
		float lybBottom = lybTop + sLen + diffLen;
		Path mRonghePath = new Path();
		mRonghePath.moveTo(lxLeft, lybTop + marginLen / 2 + diffLen / 2);
		mRonghePath.lineTo(lxLeft + extendHeight + diffLen / 2, lybTop);
		mRonghePath.lineTo(lxRight + extendHeight, lybTop);
		mRonghePath.lineTo(lxRight + extendHeight, lybBottom);
		mRonghePath.lineTo(lxLeft + marginLen2, lybBottom + marginLen2);
		mRonghePath.lineTo(lxLeft, lybBottom + marginLen2);
		mRonghePath.close();
		mRongheRegion = path2Region(mRonghePath);
		if (mSelectPath == PathType.Ronghe) { // 点击荣和
			mBgPaint.setColor(Color.parseColor(SelectColor));
		}
		canvas.drawPath(mRonghePath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mRonghePath, mPaint);
		centerY = (lybTop + marginLen2 + lybBottom) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(getResources().getString(R.string.ronghe), 
				centerX, centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		// 右层属性
		sLen = width / 3;
		float rxLeft = getPaddingLeft() + width / 3 + 10;
		float rxRight = rxLeft + width / 3;
		// 画风位
		float rytBottom = getHeight() / 2 - width / 3 - diffLen;
		float rytTop = rytBottom - sLen - diffLen;
		Path mFengPath = new Path();
		mFengPath.moveTo(rxLeft, rytTop);
		mFengPath.lineTo(rxRight - diffLen, rytTop);
		mFengPath.lineTo(rxRight, rytTop + diffLen);
		mFengPath.lineTo(rxRight, rytBottom);
		mFengPath.lineTo(rxLeft, rytBottom);
		mFengPath.close();
		mFengRegion = path2Region(mFengPath);
		String fengText;
		switch (mCurIndex) {
		case 0:
			fengText = getResources().getString(R.string.east);
			mBgPaint.setColor(Color.parseColor(DealerColor));
			mTextPaint.setColor(Color.BLACK);
			break;
		case 1:
			fengText = getResources().getString(R.string.south);
			break;
		case 2:
			fengText = getResources().getString(R.string.west);
			break;
		case 3:
			fengText = getResources().getString(R.string.north);
			break;
		default:
			fengText = getResources().getString(R.string.east);
			break;
		}
		canvas.drawPath(mFengPath, mBgPaint);
		canvas.drawPath(mFengPath, mPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 15));
		fontMetrics = mTextPaint.getFontMetrics();
		centerX = (rxLeft + rxRight) / 2;
		centerY = (rytTop + diffLen / 2 + rytBottom) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(fengText, centerX, 
				centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		mTextPaint.setColor(Color.WHITE);
		if (mOrgIndex == 0) { // 如果是初始东，画定位
			if (mImgLeader == null) {
				mImgLeader = BitmapFactory.decodeResource(getResources(), R.drawable.mj_firstplayer);
				mImgLeader = ImageTool.rotateTo(mRotateAngle, mImgLeader);
			}
			float h = getPaddingLeft() + width - rxRight;
			float w = h * mImgLeader.getHeight() / mImgLeader.getWidth();
			RectF leadRectF = new RectF(rxRight, rytTop - w / 2, rxRight + h, rytTop + w / 2);
			canvas.drawBitmap(mImgLeader, null, leadRectF, mBitmapPaint);
		}
		// 画立直
		float rybTop = getHeight() / 2 + width / 3 + diffLen;
		float rybBottom = rybTop + sLen + diffLen;
		Path mLizhiPath = new Path();
		mLizhiPath.moveTo(rxLeft, rybTop);
		mLizhiPath.lineTo(rxRight, rybTop);
		mLizhiPath.lineTo(rxRight, rybBottom - diffLen);
		mLizhiPath.lineTo(rxRight - diffLen, rybBottom);
		mLizhiPath.lineTo(rxLeft, rybBottom);
		mLizhiPath.close();
		mLizhiRegion = path2Region(mLizhiPath);
		if (mSelectPath == PathType.Lizhi && mLizhiState == 0) { // 点击立直
			mBgPaint.setColor(Color.parseColor(SelectColor));
		} else if (mLizhiState == 1) {
			mBgPaint.setColor(Color.parseColor(LizhiColor));
		} else if (mLizhiState == 2) {
			mBgPaint.setColor(Color.parseColor(DoubleLizhiColor));
		}
		canvas.drawPath(mLizhiPath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mLizhiPath, mPaint);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 12));
		fontMetrics = mTextPaint.getFontMetrics();
		String lizhiText = getResources().getString(R.string.lizhi);
//		if (mLizhiState > 0) {
//			lizhiText = getResources().getString(R.string.revoke);
//		} else {
//			lizhiText = getResources().getString(R.string.lizhi);
//		}
		centerY = (rybTop + rybBottom) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(lizhiText, centerX, 
				centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		// 画点炮
		if (mImgBomb == null) {
			mImgBomb = BitmapFactory.decodeResource(getResources(), R.drawable.mj_bomb);
			mImgBomb = ImageTool.rotateTo(mRotateAngle, mImgBomb);
		}
		float top = getHeight() / 2 + width + marginLen;
		mBombRect = new RectF(rxLeft + extendHeight, top, rxRight + extendHeight, top + rxRight - rxLeft);
		canvas.drawBitmap(mImgBomb, null, mBombRect, mBitmapPaint);
	}
	
	private void onDrawBottonsRight(Canvas canvas, int width, int height, float marginLen) {
		mBgPaint.setStrokeWidth(3);
		float diffLen = ValueTool.dp2px(getContext(), 8);
		float centerX, centerY;
		// 右层属性
		float sLen = width * 4 / 9;
		float lxRight = getPaddingLeft() + width - 10;
		float lxLeft = getPaddingLeft()  + width * 2 / 3;
		float extendHeight = width / 6;
		// 画自摸
		float lytTop = getHeight() / 2 + width + marginLen / 2; 
		float lytBottom = lytTop + sLen + diffLen;
		float marginLen2 = (lxRight - lxLeft) * marginLen / (width / 3);
		Path mZimoPath = new Path();
		mZimoPath.moveTo(lxLeft - extendHeight, lytBottom);
		mZimoPath.lineTo(lxLeft - extendHeight, lytTop);
		mZimoPath.lineTo(lxRight - extendHeight - diffLen / 2, lytTop);
		mZimoPath.lineTo(lxRight, lytTop + marginLen / 2 + diffLen / 2);
		mZimoPath.lineTo(lxRight, lytBottom + marginLen2);
		mZimoPath.lineTo(lxRight - marginLen2, lytBottom + marginLen2);
		mZimoPath.close();
		
		mZimoRegion = path2Region(mZimoPath);
		if (mSelectPath == PathType.Zimo) { // 点击自摸
			mBgPaint.setColor(Color.parseColor(SelectColor));
		}
		canvas.drawPath(mZimoPath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mZimoPath, mPaint);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 16));
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();	
		centerX = (lxLeft + lxRight - extendHeight) / 2;
		centerY = (lytTop + marginLen2 + lytBottom) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(getResources().getString(R.string.zimo), 
				centerX, centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		// 画荣和
		float lybBottom = getHeight() / 2 - width - marginLen + diffLen;
		float lybTop = lybBottom - sLen - diffLen;
		Path mRonghePath = new Path();
		mRonghePath.moveTo(lxLeft - extendHeight, lybTop);
		mRonghePath.lineTo(lxRight - marginLen2, lybTop - marginLen2);
		mRonghePath.lineTo(lxRight, lybTop - marginLen2);
		mRonghePath.lineTo(lxRight, lybBottom - marginLen / 2 - diffLen / 2);
		mRonghePath.lineTo(lxRight - extendHeight - diffLen / 2, lybBottom);
		mRonghePath.lineTo(lxLeft - extendHeight, lybBottom);
		mRonghePath.close();
		mRongheRegion = path2Region(mRonghePath);
		if (mSelectPath == PathType.Ronghe) { // 点击荣和
			mBgPaint.setColor(Color.parseColor(SelectColor));
		}
		canvas.drawPath(mRonghePath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mRonghePath, mPaint);
		centerY = (lybTop - marginLen2 + lybBottom) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(getResources().getString(R.string.ronghe), 
				centerX, centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		// 左层属性
		sLen = width / 3;
		float rxRight = getPaddingLeft() + width * 2 / 3 - 10;
		float rxLeft = rxRight - width / 3;
		// 画风位
		float rytTop = getHeight() / 2 + width / 3 + diffLen;
		float rytBottom = rytTop + sLen + diffLen;
		Path mFengPath = new Path();
		mFengPath.moveTo(rxLeft, rytTop);
		mFengPath.lineTo(rxRight, rytTop);
		mFengPath.lineTo(rxRight, rytBottom);
		mFengPath.lineTo(rxLeft + diffLen, rytBottom);
		mFengPath.lineTo(rxLeft, rytBottom - diffLen);
		mFengPath.close();
		mFengRegion = path2Region(mFengPath);
		String fengText;
		switch (mCurIndex) {
		case 0:
			fengText = getResources().getString(R.string.east);
			mBgPaint.setColor(Color.parseColor(DealerColor));
			mTextPaint.setColor(Color.BLACK);
			break;
		case 1:
			fengText = getResources().getString(R.string.south);
			break;
		case 2:
			fengText = getResources().getString(R.string.west);
			break;
		case 3:
			fengText = getResources().getString(R.string.north);
			break;
		default:
			fengText = getResources().getString(R.string.east);
			break;
		}
		canvas.drawPath(mFengPath, mBgPaint);
		canvas.drawPath(mFengPath, mPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 15));
		fontMetrics = mTextPaint.getFontMetrics();
		centerX = (rxLeft + rxRight) / 2;
		centerY = (rytTop + rytBottom) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(fengText, centerX, 
				centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		mTextPaint.setColor(Color.WHITE);
		if (mOrgIndex == 0) { // 如果是初始东，画定位
			if (mImgLeader == null) {
				mImgLeader = BitmapFactory.decodeResource(getResources(), R.drawable.mj_firstplayer);
				mImgLeader = ImageTool.rotateTo(mRotateAngle, mImgLeader);
			}
			float h = rxLeft - getPaddingLeft();
			float w = h * mImgLeader.getHeight() / mImgLeader.getWidth();
			RectF leadRectF = new RectF(getPaddingLeft(), rytBottom - w / 2, getPaddingLeft() + h, rytBottom + w / 2);
			canvas.drawBitmap(mImgLeader, null, leadRectF, mBitmapPaint);
		}
		// 画立直
		float rybBottom = getHeight() / 2 - width / 3 - diffLen;
		float rybTop = rybBottom - sLen - diffLen;
		Path mLizhiPath = new Path();
		mLizhiPath.moveTo(rxLeft, rybTop + diffLen);
		mLizhiPath.lineTo(rxLeft + diffLen, rybTop);
		mLizhiPath.lineTo(rxRight, rybTop);
		mLizhiPath.lineTo(rxRight, rybBottom);
		mLizhiPath.lineTo(rxLeft, rybBottom);
		mLizhiPath.close();
		mLizhiRegion = path2Region(mLizhiPath);
		if (mSelectPath == PathType.Lizhi && mLizhiState == 0) { // 点击立直
			mBgPaint.setColor(Color.parseColor(SelectColor));
		} else if (mLizhiState == 1) {
			mBgPaint.setColor(Color.parseColor(LizhiColor));
		} else if (mLizhiState == 2) {
			mBgPaint.setColor(Color.parseColor(DoubleLizhiColor));
		}
		canvas.drawPath(mLizhiPath, mBgPaint);
		mBgPaint.setColor(Color.parseColor(BackgroudColor));
		canvas.drawPath(mLizhiPath, mPaint);
		mTextPaint.setTextSize(ValueTool.sp2px(getContext(), 12));
		fontMetrics = mTextPaint.getFontMetrics();
		String lizhiText = getResources().getString(R.string.lizhi);
//		if (mLizhiState > 0) {
//			lizhiText = getResources().getString(R.string.revoke);
//		} else {
//			lizhiText = getResources().getString(R.string.lizhi);
//		}
		centerY = (rybTop + rybBottom) / 2;
		canvas.rotate(mRotateAngle, centerX, centerY);
		canvas.drawText(lizhiText, centerX, 
				centerY - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
		canvas.rotate(-mRotateAngle, centerX, centerY);
		// 画点炮
		if (mImgBomb == null) {
			mImgBomb = BitmapFactory.decodeResource(getResources(), R.drawable.mj_bomb);
			mImgBomb = ImageTool.rotateTo(mRotateAngle, mImgBomb);
		}
		float bottom = getHeight() / 2 - width - marginLen;
		mBombRect = new RectF(rxLeft - extendHeight, bottom - rxRight + rxLeft, rxRight - extendHeight, bottom);
		canvas.drawBitmap(mImgBomb, null, mBombRect, mBitmapPaint);
	}
	
	/**
	 * 绘制头像
	 * 
	 * @param canvas
	 * @param width
	 * @param height
	 */
	protected void onDrawPlayerIcon(Canvas canvas, int width, int height) {
		if (mIcon == null) mIcon = createPlayerIcon();
		Path mFramePath = null;
		switch (mPosition) {
		case POS_TOP:
			mFramePath = calculateIconPathTop(width, height);
			break;
		case POS_LEFT:
			mFramePath = calculateIconPathLeft(width, height);
			break;
		case POS_BOTTOM:
			mFramePath = calculateIconPathBottom(width, height);
			break;
		case POS_RIGHT:
			mFramePath = calculateIconPathRight(width, height);
			break;
		default:
			break;
		}		
		mIconRegion = path2Region(mFramePath);
		mPaint.setStrokeWidth(3f);
		canvas.drawPath(mFramePath, mPaint);
						
		canvas.save();
		canvas.clipPath(mFramePath);		
		canvas.drawBitmap(mIcon, null, mIconRectF, mBitmapPaint);		
		canvas.restore();
	}
	
	private Path calculateIconPathBottom(int width, int height) {
		float len = height * 2 / 3;
		float top = getPaddingTop();
		float bottom = top + len;
		float left = getWidth() / 2 - height / 3;
		float right = left + len;
		float cornerLen = len / 5;
		mIconRectF = new RectF(left, top, right, bottom);
		Path mFramePath = new Path();
		mFramePath.moveTo(left + cornerLen, top);
		mFramePath.lineTo(right - cornerLen, top);
		mFramePath.lineTo(right, top + cornerLen);
		mFramePath.lineTo(right, bottom);
		mFramePath.lineTo(left, bottom);
		mFramePath.lineTo(left, top + cornerLen);
		mFramePath.close();
		return mFramePath;
	}
	
	private Path calculateIconPathTop(int width, int height) {
		float len = height * 2 / 3;
		float bottom = height + getPaddingTop();
		float top = bottom - len;
		float left = getWidth() / 2 - height / 3;
		float right = left + len;
		float cornerLen = len / 5;
		mIconRectF = new RectF(left, top, right, bottom);
		Path mFramePath = new Path();
		mFramePath.moveTo(left, top);
		mFramePath.lineTo(right, top);
		mFramePath.lineTo(right, bottom - cornerLen);
		mFramePath.lineTo(right - cornerLen, bottom);
		mFramePath.lineTo(left + cornerLen, bottom);
		mFramePath.lineTo(left, bottom - cornerLen);
		mFramePath.close();
		return mFramePath;
	}
	
	private Path calculateIconPathLeft(int width, int height) {
		float len = width * 2 / 3;
		float top = getHeight() / 2 - width / 3;
		float bottom = top + len;
		float right = getPaddingLeft() + width;
		float left = right - len;
		float cornerLen = len / 5;
		mIconRectF = new RectF(left, top, right, bottom);
		Path mFramePath = new Path();
		mFramePath.moveTo(left, top);
		mFramePath.lineTo(right - cornerLen, top);
		mFramePath.lineTo(right, top + cornerLen);
		mFramePath.lineTo(right, bottom - cornerLen);
		mFramePath.lineTo(right - cornerLen, bottom);
		mFramePath.lineTo(left, bottom);
		mFramePath.close();
		return mFramePath;
	}
	
	private Path calculateIconPathRight(int width, int height) {
		float len = width * 2 / 3;
		float top = getHeight() / 2 - width / 3;
		float bottom = top + len;
		float left = getPaddingLeft();
		float right = left + len;
		float cornerLen = len / 5;
		mIconRectF = new RectF(left, top, right, bottom);
		Path mFramePath = new Path();
		mFramePath.moveTo(left, top + cornerLen);
		mFramePath.lineTo(left + cornerLen, top);
		mFramePath.lineTo(right, top);
		mFramePath.lineTo(right, bottom);
		mFramePath.lineTo(left + cornerLen, bottom);
		mFramePath.lineTo(left, bottom - cornerLen);
		mFramePath.close();
		return mFramePath;
	}
	
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
	
	/**
	 * 绘制区域
	 * 
	 * @param canvas
	 * @param region
	 * @param paint
	 */
//	private void onDrawRegion(Canvas canvas, Region region, Paint paint) {
//		RegionIterator iterator = new RegionIterator(region);
//		Rect rect = new Rect();
//		while (iterator.next(rect)) {
//		    canvas.drawRect(rect, paint);
//		}		
//	}
	
	/**
	 * 创建玩家头像
	 * 
	 * @return
	 */
	private Bitmap createPlayerIcon() {
//		int iconId = mPlayer != null ? HeadIconTool.String2Id(mPlayer.getIcon()) : -1;
//		if (iconId == -1) iconId = icon_none;
		Bitmap bitmap;
		if (mPlayer != null) {
			bitmap = EmoticonTool.getEmoticonForRank(mPlayer, rank);
		} else {
			bitmap = BitmapFactory.decodeResource(getResources(), icon_none);
		}
		return ImageTool.rotateTo(mRotateAngle, bitmap);
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
			invalidate();
			break;
			default:
				break;
		}
		return true;
	}
	
	private PathType checkTouchItem(int x, int y) {
		if (mIconRegion.contains(x, y)) {
			return PathType.Icon;
		} else if (mZimoRegion.contains(x, y)) {
			return PathType.Zimo;
		} else if (mRongheRegion.contains(x, y)) {
			return PathType.Ronghe;
		} else if (mFengRegion.contains(x, y)) {
			return PathType.Feng;
		} else if (mLizhiRegion.contains(x, y)) {
			return PathType.Lizhi;
		} else if (mBombRect.contains(x, y)) {
			return PathType.Bomb;
		}
		return PathType.None;
	}
	
	private void doTouchItem(PathType type) {
		switch (type) {
		case Icon:			
			if (mListener != null) mListener.onClickIcon(mPlayer, mOrgIndex, mPosition);
			break;
		case Zimo:			
			if (mListener != null) mListener.onClickZimo(mPlayer, mOrgIndex, mPosition);
			break;
		case Ronghe:			
			if (mListener != null) mListener.onClickRonghe(mPlayer, mOrgIndex, mPosition);
			break;
		case Feng:			
			if (mListener != null) mListener.onClickFeng(mPlayer, mOrgIndex, mPosition);
			break;
		case Lizhi:			
			if (mListener != null) {
				if (mLizhiState > 0) return;
				mLizhiState = mListener.onClickLizhi(mPlayer, mOrgIndex, mPosition, mLizhiState);
			}
			break;
		case Bomb:
			if (mListener != null) mListener.onClickBomb(mPlayer, mOrgIndex, mPosition);
		default:
			break;
		}
	}
	
	public void setOnPlayerFuncItemListener(OnPlayerFuncItemListener listener) {
		this.mListener = listener;
	}
	
	public interface OnPlayerFuncItemListener {
		public void onClickIcon(Player player, int orgIndex, int pos);
		public void onClickZimo(Player player, int orgIndex, int pos);
		public void onClickRonghe(Player player, int orgIndex, int pos);
		public void onClickFeng(Player player, int orgIndex, int pos);
		public int onClickLizhi(Player player, int orgIndex, int pos, int lizhi);
		public void onClickBomb(Player player, int orgIndex, int pos);
	}
	
}

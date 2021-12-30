package com.mahjong.tools;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;

public class ImageTool {

	public static Bitmap[] split(Bitmap bitmap, int xPiece, int yPiece) {
		Bitmap[] pieces = new Bitmap[xPiece * yPiece];
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int pieceWidth = width / xPiece;
		int pieceHeight = height / yPiece;
		for (int i = 0; i < yPiece; i++) {
			for (int j = 0; j < xPiece; j++) {
				int index = j + i * xPiece;
				int xValue = j * pieceWidth;
				int yValue = i * pieceHeight;
				Bitmap tmp = Bitmap.createBitmap(bitmap, xValue, yValue,
						pieceWidth, pieceHeight);
				pieces[index] = tmp;
			}
		}
		return pieces;
	}

	public static Bitmap[][] split2(Bitmap bitmap, int xPiece, int yPiece) {
		Bitmap[][] pieces = new Bitmap[yPiece][xPiece];
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int pieceWidth = width / xPiece;
		int pieceHeight = height / yPiece;
		for (int i = 0; i < yPiece; i++) {
			for (int j = 0; j < xPiece; j++) {
				int xValue = j * pieceWidth;
				int yValue = i * pieceHeight;
				Bitmap tmp = Bitmap.createBitmap(bitmap, xValue, yValue,
						pieceWidth, pieceHeight);
				pieces[i][j] = tmp;
			}
		}
		return pieces;
	}

	public static Bitmap merge2H(List<Bitmap> orgs, Paint paint) {
		Bitmap dst = Bitmap.createBitmap(orgs.get(0).getWidth() * orgs.size(),
				orgs.get(0).getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(dst);
		for (int i = 0; i < orgs.size(); i++) {
			canvas.drawBitmap(orgs.get(i), i * orgs.get(i).getWidth(), 0, paint);
		}
		return dst;
	}

	public static Bitmap scaleTo(Bitmap org, float newWidth, float newHeight) {
		// 获得图片的宽高
		int width = org.getWidth();
		int height = org.getHeight();
		// 计算缩放比例
		float scaleWidth = newWidth / width;
		float scaleHeight = newHeight / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		return Bitmap.createBitmap(org, 0, 0, width, height, matrix, true);
	}

	public static Bitmap rotateTo(float angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (rotateBitmap != bitmap && bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		return rotateBitmap;
	}

	/**
	 * resize图片，以 fitcenter模式填充到指定宽高
	 * 
	 * @param src
	 * @param destWidth
	 * @param destHeight
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap src, int destW, int destH) {
		if (src == null || destW == 0 || destH == 0) {
			return null;
		}		
		int orgW = src.getWidth(); // 图片宽度		
		int orgH = src.getHeight(); // 图片高度	
		
		Bitmap desBitmap;
		float tmpH = ((float)destW / (float)orgW) * orgH;
		if (tmpH < destH) {
			desBitmap = scaleTo(src, destW, tmpH);
		} else {
			float tmpW = ((float)destH / (float)orgH) * orgW;
			desBitmap = scaleTo(src, tmpW, destH);
		}		
		return desBitmap;
	}
	
	/**
	 * 根据原图和边长绘制圆形图片
	 * 
	 * @param source
	 * @param min
	 * @return
	 */
	public static Bitmap createCircleImage(Bitmap source, int min)
	{
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		// 产生一个同样大小的画布
		Canvas canvas = new Canvas(target);
		// 首先绘制圆形
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		// 使用SRC_IN
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// 绘制图片
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}
	
	/**
	 * 绘制玩家等级图标
	 * 
	 * @param icon
	 * @param rank
	 * @param min
	 * @return
	 */
	public static Bitmap createRankImage(Bitmap icon, Bitmap rank, int min, float radius)
	{
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		// 产生一个同样大小的画布
		Canvas canvas = new Canvas(target);
		Xfermode orgMode = paint.getXfermode();
		// 首先绘制玩家图标
//		canvas.drawCircle(min / 2, min / 2, radius, paint); // 圆形
		canvas.drawRect(min / 2 - radius, min / 2 - radius, min / 2 + radius, min / 2 + radius, paint); // 方框
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(icon, 0, 0, paint);
		// 绘制等级图标
		paint.setXfermode(orgMode);
		paint.setAlpha(220);
		canvas.drawBitmap(rank, null, new RectF(0, 0, min, min), paint);
		return target;
	}
 
	/**
	 * 根据原图添加圆角
	 * 
	 * @param source
	 * @return
	 */
	public static Bitmap createRoundConerImage(Bitmap source, float radius)
	{
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
		canvas.drawRoundRect(rect, radius, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

}

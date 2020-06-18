package com.mahjong.ui;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class MjWinTextView extends View {
	
	/**
	 * 基本画刷
	 */
	private Paint mPaint;
	/**
	 * 图片画刷
	 */
	private Paint mBitmapPaint;
	
	private boolean isCalculate = false;
	private int mDegrees = -15;
	private int mTextColor = Color.parseColor("#FFD700");
	private String mTextString;
	private int mTextLine = 0;
	private int[][] LineLens = new int[3][3];
	private List<String> mStringList = new ArrayList<String>();
	
	public MjWinTextView(Context context) {
		this(context, null);
	}
	
	public MjWinTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MjWinTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
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
		mPaint.setColor(mTextColor);
		mPaint.setTextSize(ValueTool.sp2px(getContext(), 18));
		
		mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mBitmapPaint.setStyle(Paint.Style.FILL);
		mBitmapPaint.setTextAlign(Paint.Align.CENTER);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		LineLens[0][0] = LineLens[0][2] = width / 4;
		LineLens[0][1] = width - LineLens[0][0] - LineLens[0][2];
		LineLens[1][0] = width / 5;
		LineLens[1][2] = width / 5;
		LineLens[1][1] = width - LineLens[1][0] - LineLens[1][2];
		LineLens[2][0] = width / 3;
		LineLens[2][2] = width / 5;
		LineLens[2][1] = width - LineLens[2][0] - LineLens[2][2];
//		System.out.println("tag-n LineLens[0] ->" + LineLens[0][0] + "," + LineLens[0][1] + "," + LineLens[0][2]);
//		System.out.println("tag-n LineLens[1] ->" + LineLens[1][0] + "," + LineLens[1][1] + "," + LineLens[1][2]);
//		System.out.println("tag-n LineLens[2] ->" + LineLens[2][0] + "," + LineLens[2][1] + "," + LineLens[2][2]);
	}
	
	public int getmDegrees() {
        return mDegrees;
    }

    public void setmDegrees(int mDegrees) {
        this.mDegrees = mDegrees;
        invalidate();
    }
    
    public void setText(String text) {
    	this.mTextString = text;
    	this.isCalculate = false;
        invalidate();
    	   	
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        super.onDraw(canvas);
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dialog_win), 
        		null, new Rect(0, 0, getWidth(), getHeight()), mBitmapPaint);
        canvas.rotate(mDegrees, getWidth() / 2f, getHeight() / 2f);
        if (!isCalculate) {
        	int width = getTextWidth(mPaint, mTextString);
//    		System.out.println("tag-n width ->" + width);
        	if (width == 0) {
        		mStringList.clear();
        		mTextLine = 0;
    		} else {
    			divideText(mPaint, mTextString, width);
    		} 
        	isCalculate = true;
		}
        Rect rect = new Rect();
        int textHeight = 0;
        if (mTextLine > 0) {
        	mPaint.getTextBounds(mTextString, 0, mTextString.length(), rect);
            textHeight = rect.height();
		}        
        switch (mTextLine) {
		case 0:			
			break;
		case 1:
			canvas.drawText(mStringList.get(0), LineLens[1][0] + LineLens[1][1] / 2, getHeight() / 2, mPaint);
			break;
		case 2:
			canvas.drawText(mStringList.get(0), LineLens[0][0] + LineLens[0][1] / 2, getHeight() / 2 - textHeight, mPaint);
			canvas.drawText(mStringList.get(1), LineLens[1][0] + LineLens[1][1] / 2, getHeight() / 2, mPaint);
			break;
		case 3:
			canvas.drawText(mStringList.get(0), LineLens[0][0] + LineLens[0][1] / 2, getHeight() / 2 - textHeight, mPaint);
			canvas.drawText(mStringList.get(1), LineLens[1][0] + LineLens[1][1] / 2, getHeight() / 2, mPaint);
			canvas.drawText(mStringList.get(2), LineLens[2][0] + LineLens[2][1] / 2, getHeight() / 2 + textHeight, mPaint);
			break;
		case 4:
			canvas.drawText(mStringList.get(0), LineLens[0][0] + LineLens[0][1] / 2, getHeight() / 2 - textHeight, mPaint);
			canvas.drawText(mStringList.get(1), LineLens[1][0] + LineLens[1][1] / 2, getHeight() / 2, mPaint);
			canvas.drawText(mStringList.get(2), LineLens[2][0] + LineLens[2][1] / 2, getHeight() / 2 + textHeight, mPaint);
			canvas.drawText(mStringList.get(3), LineLens[2][0] + LineLens[2][1] * 3 / 4, getHeight() / 2 + textHeight * 2, mPaint);
			break;
		default:
			break;
		}
        canvas.restore();
    }
    
    // 精确计算文字的宽度
    private int getTextWidth(Paint paint, String str) {
        float iSum = 0;
        if (str != null && !str.equals("")) {
               int len = str.length();
               float widths[] = new float[len];
               paint.getTextWidths(str, widths);
               for (int i = 0; i < len; i++) {
                    iSum += Math.ceil(widths[i]);
               }
        }
        return (int)iSum;
    }
    
    private void divideText(Paint paint, String str, int width) {
    	mStringList.clear();
    	
    	float iSum = 0;
    	int len = str.length();
        float widths[] = new float[len];
        paint.getTextWidths(str, widths);
        
    	if (width > 0 && width <= LineLens[1][1]) {
			mTextLine = 1;
			mStringList.add(mTextString);
		} else if (width > LineLens[1][1] 
				&& width <= LineLens[0][1] + LineLens[1][1]) {
			mTextLine = 2;
			for (int i = 0; i < len; i++) {
                iSum += Math.ceil(widths[i]);
                if (iSum > LineLens[0][1]) {
					mStringList.add(str.substring(0, i));
					mStringList.add(str.substring(i, len));
					break;
				}
           }
		} else if (width > LineLens[0][1] + LineLens[1][1] 
				&& width <= LineLens[0][1] + LineLens[1][1] + LineLens[2][1]) {
			mTextLine = 3;
			int firstIndex = -1;
			for (int i = 0; i < len; i++) {
                iSum += Math.ceil(widths[i]);
                if (firstIndex == -1 && iSum > LineLens[0][1]) {
					mStringList.add(str.substring(0, i));
					firstIndex = i;
				} else if (iSum > LineLens[0][1] + LineLens[1][1]) {
					mStringList.add(str.substring(firstIndex, i));
					mStringList.add(str.substring(i, len));
					break;
				}
           }
		} else if (width > LineLens[0][1] + LineLens[1][1] + LineLens[2][1]) {
			mTextLine = 4;
			int firstIndex = -1, secondIndex = -1;
			for (int i = 0; i < len; i++) {
                iSum += Math.ceil(widths[i]);
                if (firstIndex == -1 && iSum > LineLens[0][1]) {
					mStringList.add(str.substring(0, i));
					firstIndex = i;
				} else if (secondIndex == -1 && iSum > LineLens[0][1] + LineLens[1][1]) {
					mStringList.add(str.substring(firstIndex, i));
					secondIndex = i;
				} else if (iSum > LineLens[0][1] + LineLens[1][1] + LineLens[2][1]) {
					mStringList.add(str.substring(secondIndex, i));
					mStringList.add("......");
					break;
				}
           }
		}
//		System.out.println("tag-n mTextLine ->" + mTextLine);
    }

}

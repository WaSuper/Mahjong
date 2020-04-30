package com.mahjong.ui.ext;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.mahjong.R;
import com.mahjong.ui.WheelPicker;

public class CountPicker extends WheelPicker<Integer> {

	private int mStartCount, mEndCount;
	private int mSelectedCount;
	private OnCountSelectedListener mOnCountSelectedListener;

	public CountPicker(Context context) {
		this(context, null);
	}

	public CountPicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CountPicker(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttrs(context, attrs);
        setItemMaximumWidthText("00");
        updateCount();
        setSelectedCount(mSelectedCount, false);
        setOnWheelChangeListener(new OnWheelChangeListener<Integer>() {
            @Override
            public void onWheelSelected(Integer item, int position) {
            	mSelectedCount = item;
                if (mOnCountSelectedListener != null) {
                    mOnCountSelectedListener.onCountSelected(item);
                }
            }
        });
	}
	
	private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        mSelectedCount = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CountPicker);
        mStartCount = a.getInteger(R.styleable.CountPicker_startCount, 0);
        mEndCount = a.getInteger(R.styleable.CountPicker_endCount, 10);
        a.recycle();

    }

    private void updateCount() {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = mStartCount; i <= mEndCount; i++) {
            list.add(i);
        }
        setDataList(list);
    }

    public void setStartCount(int startCount) {
        mStartCount = startCount;
        updateCount();
        if (mStartCount > mSelectedCount) {
            setSelectedCount(mStartCount, false);
        } else {
            setSelectedCount(mSelectedCount, false);
        }
    }

    public void setEndCount(int endCount) {
        mEndCount = endCount;
        updateCount();
        if (mSelectedCount > endCount) {
            setSelectedCount(mEndCount, false);
        } else {
            setSelectedCount(mSelectedCount, false);
        }
    }

    public void setCount(int startCount, int endCount) {
        setStartCount(startCount);
        setEndCount(endCount);
    }

    public void setSelectedCount(int selectedCount) {
        setSelectedCount(selectedCount, true);
    }

    public void setSelectedCount(int selectedCount, boolean smoothScroll) {
        setCurrentPosition(selectedCount - mStartCount, smoothScroll);
    }

    public int getSelectedCount() {
    	return mSelectedCount;
    }

	public void setOnCountSelectedListener(
			OnCountSelectedListener onCountSelectedListener) {
		mOnCountSelectedListener = onCountSelectedListener;
	}

	public interface OnCountSelectedListener {
		void onCountSelected(int count);
	}

}

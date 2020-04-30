package com.mahjong.ui.ext;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.mahjong.ui.WheelPicker;

public class TextPicker extends WheelPicker<String> {

	private String[] mTexts;
	private String mSelectedText;
	private OnTextSelectedListener mOnTextSelectedListener;

	public TextPicker(Context context) {
		this(context, null);
	}

	public TextPicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextPicker(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttrs(context, attrs);
        setItemMaximumWidthText("0000000");
        setOnWheelChangeListener(new OnWheelChangeListener<String>() {
            @Override
            public void onWheelSelected(String item, int position) {
            	mSelectedText = item;
                if (mOnTextSelectedListener != null) {
                    mOnTextSelectedListener.onTextSelected(item, position);
                }
            }
        });
	}
	
	private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        
        setNullText();

    }

    public void setNullText() {
    	mTexts = new String[1];
    	mTexts[0] = "--";
		updateText();
        setCurrentPosition(0, false);
    }
    
    public void setText(String[] contents) {
    	mTexts = contents;
    	updateText();
    	setCurrentPosition(getCurrentPosition());
    }

    public String getSelectedText() {
    	return mSelectedText;
    }

    private void updateText() {
        List<String> list = new ArrayList<String>();
        for (String s : mTexts) {
            list.add(s);
        }
        setDataList(list);
    }

	public void setOnTextSelectedListener(
			OnTextSelectedListener onTextSelectedListener) {
		mOnTextSelectedListener = onTextSelectedListener;
	}

	public interface OnTextSelectedListener {
		void onTextSelected(String text, int position);
	}

}

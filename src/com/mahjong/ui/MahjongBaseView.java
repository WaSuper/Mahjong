package com.mahjong.ui;

import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MahjongBaseView extends View {
	
	/**
	 * 使能触摸响应
	 */
	protected boolean touchMode;
	/**
	 * 监听事件
	 */
	protected OnTouchEventListener mListener;
	
	public MahjongBaseView(Context context) {
		this(context, null);
	}
	
	public MahjongBaseView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MahjongBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void reset() {}
	
	public boolean addMjCard(int num) { return false; }
	
	public boolean addMjCard(MjCard card, int num) { return false; }
	
	public boolean removeMjCard() { return false; }
	
	public boolean removeMjCard(MjCard card) { return false; }
	
	public boolean addMjCardPairs(MjCardPairs pairs) { return false; }
	
	public boolean removeMjCardPairs() { return false; }
	
	public boolean removeMjCardPairs(MjCardPairs pairs) { return false; }
	
	public boolean removeTouchItem() { return false; }
	
	public void clearTouchItem() {}
	
	public void onPressLeft() {}
	
	public void onPressRight() {}
	
	public float sin(int angle){
        return (float) Math.sin(angle * Math.PI/180);
    }
 
	public float cos(int angle){
        return (float) Math.cos(angle * Math.PI/180);
    }
    
    public void setOnTouchEventListener(OnTouchEventListener listener) {
    	this.mListener = listener;
    }
    
    public interface OnTouchEventListener {
    	void onTouchAdd(MahjongBaseView view);
    	void onTouchOne(MahjongBaseView view, MjCard card);
    	void onTouchPairs(MahjongBaseView view, MjCardPairs pairs);
    }
    
}

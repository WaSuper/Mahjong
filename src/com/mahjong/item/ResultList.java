package com.mahjong.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mahjong.R;

import android.content.Context;

public class ResultList {

	public static final int Type_Base 	= 1;
	public static final int Type_Baopai = 2;
	public static final int Type_Ting 	= 3;
	public static final int Type_Round 	= 4;
	public static final int Type_Lizhi 	= 5;
	public static final int Type_Modify = 6;
		
	private int changeScore = 0;
	private List<ResultItem> mItems = new ArrayList<ResultItem>();
	private boolean isSorted = false;
	
	public ResultList() {
		
	}
	
	public int size() {
		return mItems.size();
	}
	
	public ResultList add(int type, int changeScore) {
		this.changeScore += changeScore;
		mItems.add(new ResultItem(type, changeScore));
		return this;
	}
	
	public ResultList addBase(int changeScore) {
		return add(Type_Base, changeScore);
	}
	
	public ResultList addBaopai(int changeScore) {
		return add(Type_Baopai, changeScore);
	}
	
	public ResultList addTingpai(int changeScore) {
		return add(Type_Ting, changeScore);
	}
	
	public ResultList addRound(int changeScore) {
		if (changeScore == 0) {
			return this;
		}
		return add(Type_Round, changeScore);
	}
	
	public ResultList addLizhi(int changeScore) {
		if (changeScore == 0) {
			return this;
		}
		return add(Type_Lizhi, changeScore);
	}
		
	public ResultList addModify(int changeScore) {
		return add(Type_Modify, changeScore);
	}
	
	public int getChangeScores() {
		return changeScore;
	}
	
	public List<ResultItem> getResultList() {
		sortList();
		return mItems;
	}
	
	public String getResultString(Context context) {
		sortList();
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < mItems.size(); i++) {
			ResultItem item = mItems.get(i);
			switch (item.type) {
			case Type_Base:
				sBuffer.append(context.getString(R.string.base));
				break;
			case Type_Baopai:
				sBuffer.append(context.getString(R.string.baopai));
				break;
			case Type_Ting:
				sBuffer.append(context.getString(R.string.tingpai));
				break;
			case Type_Round:
				sBuffer.append(context.getString(R.string.round));
				break;
			case Type_Lizhi:
				sBuffer.append(context.getString(R.string.lizhi));
				 break;
			case Type_Modify:
				sBuffer.append(context.getString(R.string.edit));
				break;
			default:
				break;
			}
			sBuffer.append(":");
			sBuffer.append(item.changeScore);
			if (i != mItems.size() - 1) {
				sBuffer.append("\n");
			}
		}
		return sBuffer.toString();
	}
	
	private void sortList() {
		if (isSorted) {
			Collections.sort(mItems, new Comparator<ResultItem>() {

				@Override
				public int compare(ResultItem r0, ResultItem r1) {
					return r0.type - r1.type;
				}
			});
			isSorted = true;
		}
	}
	
	public static class ResultItem {
		
		int type;
		int changeScore;
		
		public ResultItem(int type, int changeScore) {
			this.type = type;
			this.changeScore = changeScore;
		}
		
	}
	
}

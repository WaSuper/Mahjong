package com.mahjong.common;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

public class MjCardPairs implements Parcelable {

	public int id;
	public int mType;
	public int mDir;
	public List<MjCard> mCardList = new ArrayList<MjCard>();	
	public RectF mRect;
	
	public MjCardPairs() {
		this.id = MjSetting.buildUuid();
	}
	
	public MjCardPairs(int type) {
		this();
		this.mType = type;
		this.mDir = MjDir.left;
	}
	
	public MjCardPairs(int type, int dir) {
		this();
		this.mType = type;
		this.mDir = dir;
	}
	
	public MjCardPairs(int type, int dir, List<MjCard> cards) {
		this();
		this.mType = type;
		this.mDir = dir;
		this.mCardList.clear();
		for (MjCard card : cards) {
			this.mCardList.add(new MjCard(card));
		}
	}
	
	public MjCardPairs(MjCardPairs pairs) {
		this(pairs.mType, pairs.mDir, pairs.mCardList);
	}
	
	public void setType(int type) {
		this.mType = type;
	}
	
	public int getType() {
		return mType;
	}
	
	public void setDir(int dir) {
		this.mDir = dir;
	}
	
	public int getDir() {
		return mDir;
	}
	
	public void addCard(MjCard card) {
		this.mCardList.add(card);
	}
	
	public void addCard(int num, int dir) {
		this.mCardList.add(new MjCard(num, dir));
	}
	
	public void reset() {
		mCardList.clear();
		mRect = null;
	}
	
	public boolean isBlank() {
		for (int i = 0; i < mCardList.size(); i++) {
			if (mCardList.get(i).isBlank()) {
				return true;
			}
		}
		return false;
	}
	
	public void setRect(int top, int left, int right, int bottom) {
		this.mRect = new RectF(top, left, right, bottom);
	}
	
	public void calculateRect() {
		boolean isFirst = true;
		float top = -1, left = -1, right = -1, bottom = -1;
		for (int i = 0; i < mCardList.size(); i++) {
			RectF rectF = mCardList.get(i).rect;
			if (isFirst) {
				isFirst = false;
				top = rectF.top;
				left = rectF.left;
				right = rectF.right;
				bottom = rectF.bottom;
			}
			else {
				if (rectF.top < top) top = rectF.top;
				if (rectF.left < left) left = rectF.left;
				if (rectF.right > right) right = rectF.right;
				if (rectF.bottom > bottom) bottom = rectF.bottom;
			}
		}
		this.mRect = new RectF(left, top, right, bottom);
	}
	
	public boolean checkInRect(float x, float y) {
		if (x > mRect.left && x < mRect.right
				&& y > mRect.top && y < mRect.bottom) {
			return true;
		}
		return false;
	}

	protected MjCardPairs(Parcel parcel) {
		id = parcel.readInt();
		mType = parcel.readInt();
		mDir = parcel.readInt();
		parcel.readTypedList(mCardList, MjCard.CREATOR);
	}
	
	public static final Parcelable.Creator<MjCardPairs> CREATOR = new Parcelable.Creator<MjCardPairs>() {

		@Override
		public MjCardPairs createFromParcel(Parcel parcel) {
			return new MjCardPairs(parcel);
		}

		@Override
		public MjCardPairs[] newArray(int size) {
			return new MjCardPairs[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(id);
		parcel.writeInt(mType);
		parcel.writeInt(mDir);
		parcel.writeTypedList(mCardList);
	}
	
}

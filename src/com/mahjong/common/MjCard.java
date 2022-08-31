package com.mahjong.common;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

public class MjCard implements Parcelable {
	
	public int id;
	public int num;
	public int dir;
	public RectF rect;
	
	public MjCard() {
		this(0, MjDir.vertical);
	}
	
	public MjCard(int num) {
		this(num, MjDir.vertical);
	}
	
	public MjCard(int num, int dir) {
		this.id = MjSetting.buildUuid();
		this.num = num;
		this.dir = dir;
	}
	
	public MjCard(MjCard card) {
		this.id = card.id;
		this.num = card.num;
		this.dir = card.dir;
	}
	
	public void setBlank() {
		num = MjSetting._face_down;
	}
	
	public void reset() {
		num = MjSetting._face_down;
		dir = MjDir.vertical;
		rect = null;
	}
	
	public void setData(int num) {
		this.num = num;
	}
	
	public void setData(int num, int dir) {
		this.num = num;
		this.dir = dir;
	}
	
	public void setData(MjCard card) {
		this.num = card.num;
		this.dir = card.dir;
	}
	
	public void setRect(int top, int left, int right, int bottom) {
		this.rect = new RectF(top, left, right, bottom);
	}
	
	public boolean checkInRect(float x, float y) {
		if (x > rect.left && x < rect.right
				&& y > rect.top && y < rect.bottom) {
			return true;
		}
		return false;
	}
	
	public boolean isBlank() {
		if (num == MjSetting._face_down) {
			return true;
		}
		return false;
	}
	
	protected MjCard(Parcel parcel) {
		id = parcel.readInt();
		num = parcel.readInt();
		dir = parcel.readInt();
	}
	
	public static final Parcelable.Creator<MjCard> CREATOR = new Creator<MjCard>() {

		@Override
		public MjCard createFromParcel(Parcel parcel) {
			return new MjCard(parcel);
		}

		@Override
		public MjCard[] newArray(int size) {
			return new MjCard[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(id);
		parcel.writeInt(num);
		parcel.writeInt(dir);
	}
	
}

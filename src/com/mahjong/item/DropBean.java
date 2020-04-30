package com.mahjong.item;

public class DropBean {

	private String name;
	private int type;
	private boolean checked;
	
	public DropBean() {
		
	}
	
	public DropBean(String name, int type) {
		this.name = name;
		this.type = type;
		this.checked = false;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public void setChoiced(boolean checked) {
		this.checked = checked;
	}
	
	public boolean isChoiced() {
		return checked;
	}
	
}

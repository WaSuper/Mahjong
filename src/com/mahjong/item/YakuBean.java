package com.mahjong.item;

public class YakuBean {
	
	private String name;
	private String showName;
	private boolean enable;
	
	public YakuBean(String name, boolean enable) {
		this.name = name;
		this.enable = enable;
	}
	
	public YakuBean(String name, String showName, boolean enable) {
		this.name = name;
		this.showName = showName;
		this.enable = enable;
	}
	
	public String name() {
		return name;
	}
	
	public String showName() {
		return showName;
	}
	
	public void setShowName(String showName) {
		this.showName = showName;
	}
	
	public boolean enable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
}

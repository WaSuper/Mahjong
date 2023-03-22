package com.mahjong.control;

public class ManagerTool {

	public static ManagerTool instance;
	
	private BaseManager mManager;
	
	public static ManagerTool getInstance() {
		if (instance == null) {
			instance = new ManagerTool();
		}
		return instance;
	}
	
	public void init(int type, String storeDir) {
		switch (type) {
		case BaseManager.MainType_4p:
			mManager = new Game4pManager();
			break;
		case BaseManager.MainType_3p:
			mManager = new Game3pManager();
			break;
		case BaseManager.MainType_17s:
			mManager = new Game17sManager();
			break;
		default:
			break;
		}
		mManager.initStoreDir(storeDir);
	}
	
	public BaseManager getManager() {
		return mManager;
	}
	
}

package com.mahjong.tools;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

public class LightTool {

	private Activity mActivity;
	
	// 最大的屏幕亮度       
	private float maxLight;	
	// 当前的亮度     
	private float currentLight;	
	// 用来控制屏幕亮度     
	private Handler lightHandler;
	// 60秒时间不点击屏幕，屏幕变暗    
	private long delayTime = 60 * 1000L;
	
	private static int minLight = 10;

	
	public LightTool(Activity activity) {
		mActivity = activity;
		mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		lightHandler = new Handler(Looper.getMainLooper()); 
		maxLight = getLightness();
	}
	
	public void onDestroy() {
		stopSleepTask();
	}
	
	public boolean dispatchTouchEvent(boolean result) {
		if (currentLight == minLight) {
			startSleepTask();
			return false;
		}
		startSleepTask();		
		return result;
	}
	
	
	/**     * 获取亮度     */
	private float getLightness() {
		WindowManager.LayoutParams localLayoutParams = mActivity.getWindow().getAttributes();
		return localLayoutParams.screenBrightness; 
	}
	
	/**     * 设置亮度      */
	private void setLightness(int light) {
		currentLight = light;
		WindowManager.LayoutParams localLayoutParams = mActivity.getWindow().getAttributes();
		localLayoutParams.screenBrightness = (light / 255.0F);
		mActivity.getWindow().setAttributes(localLayoutParams);
	}	
	
	/**     * 开启休眠任务     */
	public void startSleepTask() {
		setLightness((int) maxLight);
		stopSleepTask();
		lightHandler.postDelayed(sleepWindowTask, delayTime);
	}
	
	/**     * 结束休眠任务     */
	public void stopSleepTask() {
		lightHandler.removeCallbacks(sleepWindowTask);
	}
	
	/**     * 休眠任务     */	
	Runnable sleepWindowTask = new Runnable() {
		
		@Override
		public void run() {
			setLightness(minLight);
		}
	};

}

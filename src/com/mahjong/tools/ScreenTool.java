package com.mahjong.tools;

import java.lang.reflect.Method;

import android.app.Activity;

public class ScreenTool {

	public static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
 }
	
}

package com.mahjong.activity;

import com.mahjong.tools.DisplayUtil;
import com.mahjong.tools.WindowInsetTool;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class BaseActivity extends Activity {

	private float fontScale = 1f;

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        return DisplayUtil.getResources(this,resources,fontScale);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(DisplayUtil.attachBaseContext(newBase,fontScale));
    }
    
    public void setFontScale(float fontScale) {
        this.fontScale = fontScale;
        DisplayUtil.recreate(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        applyWindowSafeArea();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        applyWindowSafeArea();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        applyWindowSafeArea();
    }

    protected boolean shouldApplyWindowSafeArea() {
        return true;
    }

    private void applyWindowSafeArea() {
        if (shouldApplyWindowSafeArea()) {
            WindowInsetTool.applyTopSafeArea(this);
        }
    }
	
}

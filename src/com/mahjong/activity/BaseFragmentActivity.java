package com.mahjong.activity;

import com.mahjong.tools.DisplayUtil;
import com.mahjong.tools.WindowInsetTool;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class BaseFragmentActivity extends FragmentActivity {

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

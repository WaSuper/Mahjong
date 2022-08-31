package com.mahjong.activity;

import com.mahjong.tools.DisplayUtil;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;

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
	
}

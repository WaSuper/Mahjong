package com.mahjong.activity;

import com.mahjong.R;
import com.mahjong.anime.SwingAnimation;
import com.mahjong.tools.FileTools;
import com.mahjong.tools.PackageUtils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutActivity extends BaseActivity implements OnClickListener {

	private ImageView backView;
	private TextView versionView;
	private RelativeLayout mFunctionLayout;
	private RelativeLayout mFeedbackLayout;
	private RelativeLayout mUpgradeLayout;
	private ImageView mAppView;
	private SwingAnimation mSwingAnimation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initUI();
	}
	
	private void initUI() {
		backView = (ImageView) findViewById(R.id.about_back);
		versionView = (TextView) findViewById(R.id.about_tv_version);
		mFunctionLayout = (RelativeLayout) findViewById(R.id.about_rl_function_introduce);
		mFeedbackLayout = (RelativeLayout) findViewById(R.id.about_rl_feedback);
		mUpgradeLayout = (RelativeLayout) findViewById(R.id.about_rl_upgrade);
		mAppView = (ImageView) findViewById(R.id.about_img_app);
		
		backView.setOnClickListener(this);
		mFunctionLayout.setOnClickListener(this);
		mFeedbackLayout.setOnClickListener(this);
		mUpgradeLayout.setOnClickListener(this);
		
		versionView.setText(getString(R.string.version, PackageUtils.getVersionName(this)));
		
		mSwingAnimation = new SwingAnimation(
                0f, 30f, -30f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f);
        mSwingAnimation.setDuration(3000);     //动画持续时间
        mSwingAnimation.setInterpolator(new LinearInterpolator());
        mSwingAnimation.setRepeatCount(Animation.INFINITE);  //动画重播次数
        mSwingAnimation.setFillAfter(false);  //是否保持动画结束画面
        mSwingAnimation.setStartOffset(0);   //动画播放延迟
        mAppView.setAnimation(mSwingAnimation);
        mAppView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.about_back:
			finish();
			break;
		case R.id.about_img_app:
			FileTools.getDatabaseFiles(this);
			break;
		case R.id.about_rl_function_introduce:
			break;
		case R.id.about_rl_feedback:
			break;
		case R.id.about_rl_upgrade:
			break;
		default:
			break;
		}
	}
}

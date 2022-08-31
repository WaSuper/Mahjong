package com.mahjong.activity.jpn;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.activity.BaseFragmentActivity;
import com.mahjong.fragment.MemberFragment;
import com.mahjong.fragment.SettingFragment;
import com.mahjong.tools.ManageTool;
import com.mahjong.ui.CommonDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingActivity extends BaseFragmentActivity implements
		OnClickListener {

	private ImageView mBack;
	private Button mStartGame;
	private ViewPager mGuide;
	
	private ImageView mPointMenber;
	private ImageView mPointSetting;
	private int mCurPage = 0;
	
	private MemberFragment mMemberFragment;
	private SettingFragment mSettingFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_setting);
		// 设置临时保存地址
		ManageTool.getInstance().init(getFilesDir().toString());
		initUI();
		checkGameRestore();
	}

	@SuppressWarnings("deprecation")
	private void initUI() {
		mBack = (ImageView) findViewById(R.id.mahjong_back);
		mStartGame = (Button) findViewById(R.id.mahjong_start_game);
		mGuide = (ViewPager) findViewById(R.id.mahjong_guide);
		mPointMenber = (ImageView) findViewById(R.id.mahjong_point1);
		mPointSetting = (ImageView) findViewById(R.id.mahjong_point2);

		mBack.setOnClickListener(this);
		mStartGame.setOnClickListener(this);

		List<Fragment> mFragmentList = new ArrayList<Fragment>();
		mMemberFragment = new MemberFragment();
		mSettingFragment = new SettingFragment();
		mFragmentList.add(mMemberFragment);
		mFragmentList.add(mSettingFragment);
		mGuide.setAdapter(new GuideAdapter(getSupportFragmentManager(),
				mFragmentList));
		mGuide.setOnPageChangeListener(new GuideListener());
		mGuide.setCurrentItem(mCurPage);		
	}
	
	private void checkGameRestore() {
		if (ManageTool.getInstance().checkLastSaveStates()) {
			final CommonDialog dialog = new CommonDialog(this, R.style.MyDialogStyle);
			dialog.titleTextView.setText(R.string.tip);
			dialog.addView(R.layout.item_text);
			TextView text = (TextView) dialog.getContentView().findViewById(R.id.item_text);
			text.setText(R.string.last_game_interrupt);
			dialog.ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {					
					dialog.dismiss();			
					if (ManageTool.getInstance().restoreStatesForTmp()) {
						startActivity(new Intent(SettingActivity.this, GameSimpleActivity.class));
						finish();
					} else {
						showTextDialog(R.string.tip, R.string.restore_data_fail);
					}
				}
			});
			dialog.cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}

	private void showTextDialog(int title, int content) {
		final CommonDialog dialog = new CommonDialog(this, R.style.MyDialogStyle, 0);
		dialog.setCanceledOnTouchOutside(true);
		dialog.titleTextView.setText(title);
		dialog.addView(R.layout.item_text);
		TextView text = (TextView) dialog.getContentView().findViewById(R.id.item_text);
		text.setText(content);
		dialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {					
				dialog.dismiss();
			}
		});
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mahjong_back: // 返回
			this.finish();
			break;
		case R.id.mahjong_start_game:
			checkStartGame();
			break;
		default:
			break;
		}
	}
	
	private void checkStartGame() {
		mMemberFragment.initGameStart();
		mSettingFragment.initGameStart();
		ManageTool.getInstance().startNewGame();
		startActivity(new Intent(SettingActivity.this, GameSimpleActivity.class));
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	class GuideAdapter extends FragmentPagerAdapter {

		List<Fragment> mList;

		public GuideAdapter(FragmentManager fm) {
			super(fm);
		}

		public GuideAdapter(FragmentManager fm, List<Fragment> list) {
			super(fm);
			this.mList = list;
		}

		@Override
		public Fragment getItem(int pos) {
			return mList != null && mList.size() > pos ? mList.get(pos) : null;
		}

		@Override
		public int getCount() {
			return mList != null ? mList.size() : 0;
		}

	}

	class GuideListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int state) {
			switch (state) {
			case ViewPager.SCROLL_STATE_IDLE: // 空闲状态
				break;

			case ViewPager.SCROLL_STATE_DRAGGING: // 正在滑动
				break;

			case ViewPager.SCROLL_STATE_SETTLING: // 自然沉降
				break;

			}
		}

		public void onPageScrolled(int pos, float percent, int arg2) {

		}

		public void onPageSelected(int pos) {
			mCurPage = pos;
			if (pos == 0) {
				mPointMenber.setBackgroundResource(R.drawable.ic_indicator_sel);
				mPointSetting.setBackgroundResource(R.drawable.ic_indicator_nor);
			} else {
				mPointMenber.setBackgroundResource(R.drawable.ic_indicator_nor);
				mPointSetting.setBackgroundResource(R.drawable.ic_indicator_sel);
			}
		}

	}

}

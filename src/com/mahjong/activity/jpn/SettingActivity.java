package com.mahjong.activity.jpn;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.activity.BaseFragmentActivity;
import com.mahjong.adapter.SettingGuideAdapter;
import com.mahjong.control.BaseManager;
import com.mahjong.control.ManagerTool;
import com.mahjong.fragment.BaseMemberFragment;
import com.mahjong.fragment.BaseSettingFragment;
import com.mahjong.fragment.Game17sMemberFragment;
import com.mahjong.fragment.Game17sSettingFragment;
import com.mahjong.fragment.Game3pMemberFragment;
import com.mahjong.fragment.Game3pSettingFragment;
import com.mahjong.fragment.Game4pMemberFragment;
import com.mahjong.fragment.Game4pSettingFragment;
import com.mahjong.ui.CommonDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingActivity extends BaseFragmentActivity implements
		OnClickListener {
	
	public static final String SETTING_MEMBER = "SETTING_MEMBER";

	private ImageView mBack;
	private TextView mTitle;
	private Button mStartGame;
	private ViewPager mGuide;
	
	private ImageView mPointMember;
	private ImageView mPointSetting;
	private int mCurPage = 0;
	
	private BaseMemberFragment mMemberFragment;
	private BaseSettingFragment mSettingFragment;

	private SettingReceiver mSettingReceiver;
	
	private BaseManager mManager = ManagerTool.getInstance().getManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_setting);
		initUI();
		checkGameRestore();
		registerReceiver();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
	}
	
	private void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter(SETTING_MEMBER);
		mSettingReceiver = new SettingReceiver();
		registerReceiver(mSettingReceiver, intentFilter);
	}
	
	private void unregisterReceiver() {
		unregisterReceiver(mSettingReceiver);
	}

	@SuppressWarnings("deprecation")
	private void initUI() {
		mBack = (ImageView) findViewById(R.id.mahjong_back);
		mTitle = (TextView) findViewById(R.id.mahjong_tv_title);
		mStartGame = (Button) findViewById(R.id.mahjong_start_game);
		mGuide = (ViewPager) findViewById(R.id.mahjong_guide);
		mPointMember = (ImageView) findViewById(R.id.mahjong_point1);
		mPointSetting = (ImageView) findViewById(R.id.mahjong_point2);

		mBack.setOnClickListener(this);
		mStartGame.setOnClickListener(this);
		
		if (mManager.is4pMahjong()) {
			mTitle.setText(R.string.game4p);
			mMemberFragment = new Game4pMemberFragment();
			mSettingFragment = new Game4pSettingFragment();
		} else if (mManager.is3pMahjong()) {
			mTitle.setText(R.string.game3p);
			mMemberFragment = new Game3pMemberFragment();
			mSettingFragment = new Game3pSettingFragment();
		} else if (mManager.is17Step()) {
			mTitle.setText(R.string.game17s);
			mMemberFragment = new Game17sMemberFragment();
			mSettingFragment = new Game17sSettingFragment();
		}

		List<Fragment> mFragmentList = new ArrayList<Fragment>();
		mFragmentList.add(mMemberFragment);
		mFragmentList.add(mSettingFragment);
		mGuide.setAdapter(new SettingGuideAdapter(getSupportFragmentManager(),
				mFragmentList));
		mGuide.setOnPageChangeListener(new GuideListener());
		mGuide.setCurrentItem(mCurPage);		
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
	
	private void checkGameRestore() {
		if (mManager.checkLastSaveStates()) {
			final CommonDialog dialog = new CommonDialog(this, R.style.MyDialogStyle);
			dialog.titleTextView.setText(R.string.tip);
			dialog.addView(R.layout.item_text);
			TextView text = (TextView) dialog.getContentView().findViewById(R.id.item_text);
			text.setText(R.string.last_game_interrupt);
			dialog.ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {					
					dialog.dismiss();			
					if (mManager.restoreStatesForTmp()) {
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
	
	private void checkStartGame() {
		mMemberFragment.initGameStart();
		mSettingFragment.initGameStart();
		mManager.startNewGame();
		startActivity(new Intent(SettingActivity.this, GameSimpleActivity.class));
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
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
				mPointMember.setBackgroundResource(R.drawable.ic_indicator_sel);
				mPointSetting.setBackgroundResource(R.drawable.ic_indicator_nor);
			} else {
				mPointMember.setBackgroundResource(R.drawable.ic_indicator_nor);
				mPointSetting.setBackgroundResource(R.drawable.ic_indicator_sel);
			}
		}

	}
	
	public class SettingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mMemberFragment.initData(true);
		}
		
	}

}

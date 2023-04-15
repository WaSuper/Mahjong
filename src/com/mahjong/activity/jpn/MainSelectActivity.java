package com.mahjong.activity.jpn;

import com.mahjong.R;
import com.mahjong.activity.AboutActivity;
import com.mahjong.activity.BaseActivity;
import com.mahjong.activity.EmoticonActivity;
import com.mahjong.activity.PlayerManageActivity;
import com.mahjong.activity.SoundEffectActivity;
import com.mahjong.activity.jpn.CalculateActivity;
import com.mahjong.activity.jpn.SettingActivity;
import com.mahjong.activity.jpn.game17s.Game17sCalculateActivity;
import com.mahjong.control.BaseManager;
import com.mahjong.control.ManagerTool;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.SpecialYakuTool;
import com.mahjong.tools.ValueTool;
import com.mahjong.ui.CommonDialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainSelectActivity extends BaseActivity implements OnClickListener {

	private static final String GAME_TYPE 	= BaseManager.GAME_TYPE;
	private static final int GameType4P 	= BaseManager.MainType_4p;
	private static final int GameType3P 	= BaseManager.MainType_3p;
	private static final int GameType17Step = BaseManager.MainType_17s;
	
	private ImageButton btnBattleEntrance;
	private ImageButton btnBattleRecord;
	private ImageButton btnScoreCalc;
	private ImageButton btnRankingList;
	private ImageButton btnPlayerManage;
	private ImageButton btnEmoticon;
	private ImageButton btnSoundEffect;
//	private ImageButton btnWlanBattle;	
	private TextView btnGame4P;		
	private TextView btnGame3P;		
	private TextView btnGame17Step;	
	private Button btnFanType;
	private Button btnPointForm;
	private TextView aboutView;
	
	private int mGameType = -1;
	private int animeCounter = 0;
	private boolean isInit = false;
	
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_function_select);
		initUI();
		initData();
	}

	private void initData() {
		// 初始化特殊役保存文件地址
		SpecialYakuTool.getInstance().init(this, getFilesDir().toString());
		// 初始化游戏类型
		setGameType(ShareprefenceTool.getInstance().getInt(GAME_TYPE, this, GameType4P));
	}
	
	private void initUI() {
		btnBattleEntrance = (ImageButton) findViewById(R.id.jpn_select_btn_battle_entrance);
		btnBattleRecord = (ImageButton) findViewById(R.id.jpn_select_btn_battle_record);
		btnScoreCalc = (ImageButton) findViewById(R.id.jpn_select_btn_score_calc);
		btnRankingList = (ImageButton) findViewById(R.id.jpn_select_btn_ranking_list);
		btnPlayerManage = (ImageButton) findViewById(R.id.jpn_select_btn_player_manage);
		btnEmoticon = (ImageButton) findViewById(R.id.jpn_select_btn_emoticon);
		btnSoundEffect = (ImageButton) findViewById(R.id.jpn_select_btn_sound_effect);
//		btnWlanBattle = (ImageButton) findViewById(R.id.jpn_select_btn_wlan_battle);
		btnGame4P = (TextView) findViewById(R.id.jpn_select_main_4p);
		btnGame3P = (TextView) findViewById(R.id.jpn_select_main_3p);
		btnGame17Step = (TextView) findViewById(R.id.jpn_select_main_17step);
		btnFanType = (Button) findViewById(R.id.jpn_select_btn_fan_type);
		btnPointForm = (Button) findViewById(R.id.jpn_select_btn_point_form);
		aboutView = (TextView) findViewById(R.id.jpn_select_about);
		
		btnBattleEntrance.setOnClickListener(this);
		btnBattleRecord.setOnClickListener(this);
		btnScoreCalc.setOnClickListener(this);
		btnRankingList.setOnClickListener(this);
		btnPlayerManage.setOnClickListener(this);
//		btnWlanBattle.setOnClickListener(this);
		btnEmoticon.setOnClickListener(this);
		btnSoundEffect.setOnClickListener(this);
		btnGame4P.setOnClickListener(this);
		btnGame3P.setOnClickListener(this);
		btnGame17Step.setOnClickListener(this);
		btnFanType.setOnClickListener(this);
		btnPointForm.setOnClickListener(this);
		aboutView.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.jpn_select_btn_battle_entrance:
			// 初始化并设置临时保存地址
			ManagerTool.getInstance().init(mGameType, getFilesDir().toString());
			startActivity(new Intent(MainSelectActivity.this, SettingActivity.class));
			break;
		case R.id.jpn_select_btn_battle_record:
			startActivity(new Intent(MainSelectActivity.this, HistoryActivity.class));
			break;
		case R.id.jpn_select_btn_score_calc:
			switch (mGameType) {
			case GameType4P:
				startActivity(new Intent(MainSelectActivity.this, CalculateActivity.class));
				break;
			case GameType3P:
				startActivity(new Intent(MainSelectActivity.this, CalculateActivity.class));
				break;
			case GameType17Step:
				startActivity(new Intent(MainSelectActivity.this, Game17sCalculateActivity.class));
				break;
			default:
				break;
			}
			break;
		case R.id.jpn_select_btn_ranking_list:
			startActivity(new Intent(MainSelectActivity.this, RankListActivity.class));
			break;
		case R.id.jpn_select_btn_player_manage:
			startActivity(new Intent(MainSelectActivity.this, PlayerManageActivity.class));
			break;
		case R.id.jpn_select_btn_emoticon:
			startActivity(new Intent(MainSelectActivity.this, EmoticonActivity.class));
			break;
		case R.id.jpn_select_btn_sound_effect:
			startActivity(new Intent(MainSelectActivity.this, SoundEffectActivity.class));
			break;
//		case R.id.jpn_select_btn_wlan_battle:
//			ToastTool.showToast(this, R.string.no_support);
//			break;
		case R.id.jpn_select_main_4p:
			setGameType(GameType4P);
			break;
		case R.id.jpn_select_main_3p:
			setGameType(GameType3P);
			break;
		case R.id.jpn_select_main_17step:
			setGameType(GameType17Step);
			break;
		case R.id.jpn_select_btn_fan_type:
			startActivity(new Intent(MainSelectActivity.this, FanTypeActivity.class));
			break;
		case R.id.jpn_select_btn_point_form:
			startActivity(new Intent(MainSelectActivity.this, PointFormActivity.class));
			break;
		case R.id.jpn_select_about:
			startActivity(new Intent(MainSelectActivity.this, AboutActivity.class));
			break;
		default:
			break;
		}
	}

	private void setGameType(int type) {
		if (mGameType == type) {
			if (mGameType == GameType17Step) {
				show17StepInformation();
			}
			return;
		}
		mGameType = type;
		if (!isInit) {
			mHandler.post(mAnimeRunnable);
			isInit = true;
		} else {
			showGameTypeView(type);
			ShareprefenceTool.getInstance().setInt(GAME_TYPE, mGameType, this);
		}
	}
	
	private Runnable mAnimeRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (animeCounter != mGameType + 6) {
				showGameTypeView(animeCounter % 3);
				animeCounter++;
				mHandler.postDelayed(mAnimeRunnable, 150);
			} else {
				showGameTypeView(mGameType);
			}
		}
	};
	
	private void showGameTypeView(int type) {
		switch (type) {
		case GameType4P:
			btnGame4P.setBackgroundResource(R.drawable.main_choose_light);
			btnGame3P.setBackgroundResource(R.drawable.main_choose_dark);
			btnGame17Step.setBackgroundResource(R.drawable.main_choose_dark);
			break;
		case GameType3P:
			btnGame4P.setBackgroundResource(R.drawable.main_choose_dark);
			btnGame3P.setBackgroundResource(R.drawable.main_choose_light);
			btnGame17Step.setBackgroundResource(R.drawable.main_choose_dark);
			break;
		case GameType17Step:
			btnGame4P.setBackgroundResource(R.drawable.main_choose_dark);
			btnGame3P.setBackgroundResource(R.drawable.main_choose_dark);
			btnGame17Step.setBackgroundResource(R.drawable.main_choose_light);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
	
	private void show17StepInformation() {
		final CommonDialog infoDialog = new CommonDialog(this, R.style.MyDialogStyle, 0);
		infoDialog.addView(R.layout.item_text);
		infoDialog.setCanceledOnTouchOutside(true);
		infoDialog.titleTextView.setText(getString(R.string.game17s));
		TextView textView = (TextView) infoDialog.getContentView().findViewById(R.id.item_text);
		textView.setText(getString(R.string.game17s_explain));
		textView.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		textView.setTextSize(ValueTool.sp2px(this, 6));
		infoDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {	
				infoDialog.dismiss();
			}
		});
		infoDialog.show();
	}

}

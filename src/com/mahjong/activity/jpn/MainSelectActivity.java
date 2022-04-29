package com.mahjong.activity.jpn;

import com.mahjong.R;
import com.mahjong.activity.AboutActivity;
import com.mahjong.activity.EmoticonActivity;
import com.mahjong.activity.PlayerManageActivity;
import com.mahjong.activity.SoundEffectActivity;
import com.mahjong.activity.jpn.CalculateActivity;
import com.mahjong.activity.jpn.SettingActivity;
import com.mahjong.tools.SpecialYakuTool;
import com.mahjong.tools.ToastTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainSelectActivity extends Activity implements OnClickListener {

	private ImageButton btnBattleEntrance;
	private ImageButton btnBattleRecord;
	private ImageButton btnScoreCalc;
	private ImageButton btnRankingList;
	private ImageButton btnPlayerManage;
	private ImageButton btnEmoticon;
	private ImageButton btnSoundEffect;
	private ImageButton btnWlanBattle;	
	private Button btnFanType;
	private Button btnPointForm;
	private TextView aboutView;

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
	}
	
	private void initUI() {
		btnBattleEntrance = (ImageButton) findViewById(R.id.jpn_select_btn_battle_entrance);
		btnBattleRecord = (ImageButton) findViewById(R.id.jpn_select_btn_battle_record);
		btnScoreCalc = (ImageButton) findViewById(R.id.jpn_select_btn_score_calc);
		btnRankingList = (ImageButton) findViewById(R.id.jpn_select_btn_ranking_list);
		btnPlayerManage = (ImageButton) findViewById(R.id.jpn_select_btn_player_manage);
		btnEmoticon = (ImageButton) findViewById(R.id.jpn_select_btn_emoticon);
		btnSoundEffect = (ImageButton) findViewById(R.id.jpn_select_btn_sound_effect);
		btnWlanBattle = (ImageButton) findViewById(R.id.jpn_select_btn_wlan_battle);
		btnFanType = (Button) findViewById(R.id.jpn_select_btn_fan_type);
		btnPointForm = (Button) findViewById(R.id.jpn_select_btn_point_form);
		aboutView = (TextView) findViewById(R.id.jpn_select_about);
		
		btnBattleEntrance.setOnClickListener(this);
		btnBattleRecord.setOnClickListener(this);
		btnScoreCalc.setOnClickListener(this);
		btnRankingList.setOnClickListener(this);
		btnPlayerManage.setOnClickListener(this);
		btnWlanBattle.setOnClickListener(this);
		btnEmoticon.setOnClickListener(this);
		btnSoundEffect.setOnClickListener(this);
		btnFanType.setOnClickListener(this);
		btnPointForm.setOnClickListener(this);
		aboutView.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.jpn_select_btn_battle_entrance:
			startActivity(new Intent(MainSelectActivity.this, SettingActivity.class));
			break;
		case R.id.jpn_select_btn_battle_record:
			startActivity(new Intent(MainSelectActivity.this, HistoryActivity.class));
			break;
		case R.id.jpn_select_btn_score_calc:
			startActivity(new Intent(MainSelectActivity.this, CalculateActivity.class));
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
		case R.id.jpn_select_btn_wlan_battle:
			ToastTool.showToast(this, R.string.no_support);
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

}

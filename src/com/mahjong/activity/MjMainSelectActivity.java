package com.mahjong.activity;

import com.mahjong.R;
import com.mahjong.activity.jpn.CalculateActivity;
import com.mahjong.activity.jpn.SettingActivity;
import com.mahjong.tools.ToastTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MjMainSelectActivity extends Activity implements OnClickListener {

	Button btnJpn;
	Button btnInter;
	Button btnCant;
	Button btnTaiwan;
	
	ImageView calJpn;
	ImageView calInter;
	ImageView calCant;
	ImageView calTaiwan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_select);
		initUI();
	}

	private void initUI() {
		btnJpn = (Button) findViewById(R.id.selcet_btn_jpn);
		btnInter = (Button) findViewById(R.id.selcet_btn_inter);
		btnCant = (Button) findViewById(R.id.selcet_btn_cant);
		btnTaiwan = (Button) findViewById(R.id.selcet_btn_taiwan);

		calJpn = (ImageView) findViewById(R.id.select_cal_jpn);
		calInter = (ImageView) findViewById(R.id.select_cal_inter);
		calCant = (ImageView) findViewById(R.id.select_cal_cant);
		calTaiwan = (ImageView) findViewById(R.id.select_cal_taiwan);
		
		btnJpn.setOnClickListener(this);
		btnInter.setOnClickListener(this);
		btnCant.setOnClickListener(this);
		btnTaiwan.setOnClickListener(this);
		calJpn.setOnClickListener(this);
		calInter.setOnClickListener(this);
		calCant.setOnClickListener(this);
		calTaiwan.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.selcet_btn_jpn:
			startActivity(new Intent(MjMainSelectActivity.this, SettingActivity.class));
			break;
		case R.id.selcet_btn_inter:
			ToastTool.showToast(this, R.string.no_support);
			break;
		case R.id.selcet_btn_cant:
			ToastTool.showToast(this, R.string.no_support);
			break;
		case R.id.selcet_btn_taiwan:
			ToastTool.showToast(this, R.string.no_support);
			break;
		case R.id.select_cal_jpn:
			startActivity(new Intent(MjMainSelectActivity.this, CalculateActivity.class));
			break;
		case R.id.select_cal_inter:
			break;
		case R.id.select_cal_cant:
			break;
		case R.id.select_cal_taiwan:
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

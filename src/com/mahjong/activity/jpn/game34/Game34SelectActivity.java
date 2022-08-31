package com.mahjong.activity.jpn.game34;

import com.mahjong.R;
import com.mahjong.tools.ToastTool;
import com.mahjong.tools.ValueTool;
import com.mahjong.ui.CommonDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Game34SelectActivity extends Activity implements OnClickListener {

	
	private ImageView mBackView;
	private ImageView mInfoView;
	private ImageButton btnBattleEntrance;
	private ImageButton btnBattleRecord;
	private ImageButton btnScoreCalc;
	private ImageButton btnRankingList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game34_function_select);
		initUI();
	}
	
	private void initUI() {
		mBackView = (ImageView) findViewById(R.id.game34_select_back);
		mInfoView = (ImageView) findViewById(R.id.game34_info);
		btnBattleEntrance = (ImageButton) findViewById(R.id.game34_select_btn_battle_entrance);
		btnBattleRecord = (ImageButton) findViewById(R.id.game34_select_btn_battle_record);
		btnScoreCalc = (ImageButton) findViewById(R.id.game34_select_btn_score_calc);
		btnRankingList = (ImageButton) findViewById(R.id.game34_select_btn_ranking_list);
		
		mBackView.setOnClickListener(this);
		mInfoView.setOnClickListener(this);
		btnBattleEntrance.setOnClickListener(this);
		btnBattleRecord.setOnClickListener(this);
		btnScoreCalc.setOnClickListener(this);
		btnRankingList.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.game34_select_back:
			finish();
			break;
		case R.id.game34_info:
			showInformation();
			break;
		case R.id.game34_select_btn_battle_entrance:
			ToastTool.showToast(this, R.string.no_support);
			break;
		case R.id.game34_select_btn_battle_record:
			ToastTool.showToast(this, R.string.no_support);
			break;
		case R.id.game34_select_btn_score_calc:
			startActivity(new Intent(Game34SelectActivity.this, Game34CalculateActivity.class));
			break;
		case R.id.game34_select_btn_ranking_list:
			ToastTool.showToast(this, R.string.no_support);
			break;
		default:
			break;
		}
	}
	
	private void showInformation() {
		final CommonDialog infoDialog = new CommonDialog(this, R.style.MyDialogStyle, 0);
		infoDialog.addView(R.layout.item_text);
		infoDialog.setCanceledOnTouchOutside(true);
		infoDialog.titleTextView.setText(getString(R.string.game34));
		TextView textView = (TextView) infoDialog.getContentView().findViewById(R.id.item_text);
		textView.setText(getString(R.string.game34_explain));
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

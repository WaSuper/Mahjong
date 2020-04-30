package com.mahjong.activity;

import java.io.File;
import java.util.List;

import com.mahjong.R;
import com.mahjong.activity.jpn.GameSimpleActivity;
import com.mahjong.model.AudioItem;
import com.mahjong.model.Player;
import com.mahjong.tools.AudioTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayerSettingActivity extends Activity 
		implements OnClickListener, OnCheckedChangeListener {

	private ImageView mBackView;
	private CheckBox[] mAudioBoxs;
	private TextView[] mAudioDirViews;
	
	private String mPlayerId;
	private List<AudioItem> mAudioList;
	
	private boolean isChange = false;
	private String mLastSelectPath = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_setting);
		mPlayerId = getIntent().getStringExtra(Player.Col_Uuid);
		initUI();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isChange) {
			Intent intent = new Intent(GameSimpleActivity.BROADCAST_RESULT);
			intent.putExtra(GameSimpleActivity.BROADCAST_RESULT, 
					GameSimpleActivity.REQUEST_CODE_PLAYER_SETTING);
			intent.putExtra(Player.Col_Uuid, mPlayerId);
			sendBroadcast(intent);
		}		
	}
	
	private void initUI() {
		mBackView = (ImageView) findViewById(R.id.player_setting_back);
		mAudioBoxs = new CheckBox[4];
		mAudioBoxs[0] = (CheckBox) findViewById(R.id.audio_lizhi_bgm_check);
		mAudioBoxs[1] = (CheckBox) findViewById(R.id.audio_lizhi_check);
		mAudioBoxs[2] = (CheckBox) findViewById(R.id.audio_zimo_check);
		mAudioBoxs[3] = (CheckBox) findViewById(R.id.audio_ronghe_check);
		mAudioDirViews = new TextView[4];
		mAudioDirViews[0] = (TextView) findViewById(R.id.audio_lizhi_bgm_dir);
		mAudioDirViews[1] = (TextView) findViewById(R.id.audio_lizhi_dir);
		mAudioDirViews[2] = (TextView) findViewById(R.id.audio_zimo_dir);
		mAudioDirViews[3] = (TextView) findViewById(R.id.audio_ronghe_dir);
		
		mAudioList = AudioItem.loadItemsById(mPlayerId);
		if (mAudioList != null && mAudioList.size() > 0) {
			for (AudioItem audioItem : mAudioList) {
				int index = audioItem.getType() - 1;
				mAudioBoxs[index].setChecked(audioItem.getEnable());
				showFilePath(mAudioDirViews[index], audioItem.getFilePath());
			}
		}
		
		mBackView.setOnClickListener(this);
		for (CheckBox c : mAudioBoxs) {
			c.setOnCheckedChangeListener(this);
		}
		for (TextView t : mAudioDirViews) {
			t.setOnClickListener(this);
		}
	}
	
	private void showFilePath(TextView textView, String path) {
		if (path == null || path.isEmpty()) {
			textView.setText(R.string.click_to_select);
		} else {
			textView.setText(path);
		}
	}
	
	private AudioItem getAudioItem(int type) {
		if (mAudioList != null && mAudioList.size() > 0) {
			for (AudioItem audioItem : mAudioList) {
				if (audioItem.getType() == type) {
					return audioItem;
				}
			}
		}
		AudioItem item = new AudioItem(mPlayerId, type, "", false);	
		mAudioList.add(item);
		return item;		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String path = data.getStringExtra(FileActivity.FileDir);
			mLastSelectPath = new File(path).getParent();
			if (path != null && path != "") {
				switch (requestCode) {
				case AudioTool.Type_Lizhi_BGM:
				case AudioTool.Type_Lizhi:
				case AudioTool.Type_Zimo:
				case AudioTool.Type_Ronghe:
					mAudioDirViews[requestCode - 1].setText(path);
					AudioItem item = getAudioItem(requestCode);
					item.setFilePath(path);
					item.save();
					isChange = true;
					break;
				default:
					break;
				}
			}			
		} else if (resultCode == RESULT_CANCELED) {
			mLastSelectPath = "";
		}
	}
	
	@Override
	public void onClick(View view) {
		Intent intent = new Intent(PlayerSettingActivity.this, FileActivity.class);
		intent.putExtra(FileActivity.FileType, FileActivity.File_Music_Only);
		intent.putExtra(FileActivity.FileDir, mLastSelectPath);
		switch (view.getId()) {
		case R.id.player_setting_back:
			this.finish();
			break;
		case R.id.audio_lizhi_bgm_dir:
			startActivityForResult(intent, AudioTool.Type_Lizhi_BGM);
			break;
		case R.id.audio_lizhi_dir:
			startActivityForResult(intent, AudioTool.Type_Lizhi);
			break;
		case R.id.audio_zimo_dir:
			startActivityForResult(intent, AudioTool.Type_Zimo);
			break;
		case R.id.audio_ronghe_dir:
			startActivityForResult(intent, AudioTool.Type_Ronghe);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton cbox, boolean checked) {
		int type = 0;
		switch (cbox.getId()) {
		case R.id.audio_lizhi_bgm_check:
			type = AudioTool.Type_Lizhi_BGM;
			break;
		case R.id.audio_lizhi_check:
			type = AudioTool.Type_Lizhi;
			break;
		case R.id.audio_zimo_check:
			type = AudioTool.Type_Zimo;
			break;
		case R.id.audio_ronghe_check:
			type = AudioTool.Type_Ronghe;
			break;
		default:
			break;
		}
		switch (type) {
		case AudioTool.Type_Lizhi_BGM:
		case AudioTool.Type_Lizhi:
		case AudioTool.Type_Zimo:
		case AudioTool.Type_Ronghe:
			AudioItem item = getAudioItem(type);
			item.setEnable(checked);
			item.save();
			isChange = true;
			break;
		default:
			break;
		}
	}
	
	
}

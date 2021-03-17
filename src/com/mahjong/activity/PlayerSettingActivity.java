package com.mahjong.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.activity.jpn.GameSimpleActivity;
import com.mahjong.adapter.StringArrayAdapter;
import com.mahjong.model.AudioItem;
import com.mahjong.model.Player;
import com.mahjong.tools.AudioTool;
import com.mahjong.tools.FileTools;
import com.mahjong.tools.PopWinDownUtil;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.ui.CommonDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PlayerSettingActivity extends Activity 
		implements OnClickListener, OnCheckedChangeListener {

	private LinearLayout mMainLinearLayout;
	private ImageView mBackView;
	private CheckBox[] mAudioBoxs;
	private TextView[] mAudioDirViews;
	
	private String mPlayerId;
	private List<AudioItem> mAudioList;
	
	private boolean isChange = false;
	private String mLastSelectPath;
	
	private boolean isGlobalSetting = false; // Y：全局设定；N：个人设定
	
	private PopWinDownUtil popWinDownUtil;
	private int mAudioType = 0;
	
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_setting);
		mContext = this;
		mPlayerId = getIntent().getStringExtra(Player.Col_Uuid);
		if (mPlayerId == null || mPlayerId.isEmpty()) isGlobalSetting = true;
		mLastSelectPath = ShareprefenceTool.getInstance()
				.getString(FileActivity.LastSecletPath, this, "");
		initUI();
		initPopUpWin();
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
		mMainLinearLayout = (LinearLayout) findViewById(R.id.player_setting_ll_main);
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
		
		if (isGlobalSetting) {
			for (CheckBox cBox : mAudioBoxs) {
				cBox.setVisibility(View.GONE);
			}
		} else {
			mAudioList = AudioItem.loadItemsById(mPlayerId);
			if (mAudioList != null && mAudioList.size() > 0) {
				for (AudioItem audioItem : mAudioList) {
					int index = audioItem.getType() - 1;
					mAudioBoxs[index].setChecked(audioItem.getEnable());
					showFilePath(mAudioDirViews[index], audioItem.getFilePath());
				}
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
			String name = FileTools.getFileNameNoEx(path);
			textView.setText(name);
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
		if (!isGlobalSetting) {
			if (resultCode == RESULT_OK) {
				String path = data.getStringExtra(FileActivity.FileDir);
				setAudio(requestCode, path);
			} else if (resultCode == RESULT_CANCELED) {
				//mLastSelectPath = "";
			}
		}		
	}
	
	private void setAudio(int type, String path) {
		if (path != null && path != "") {
			mLastSelectPath = new File(path).getParent();
			switch (type) {
			case AudioTool.Type_Lizhi_BGM:
			case AudioTool.Type_Lizhi:
			case AudioTool.Type_Zimo:
			case AudioTool.Type_Ronghe:
				//mAudioDirViews[requestCode - 1].setText(path);
				showFilePath(mAudioDirViews[type - 1], path);						
				AudioItem item = getAudioItem(type);
				item.setFilePath(path);
				item.save();
				ShareprefenceTool.getInstance().setString(
						FileActivity.LastSecletPath, mLastSelectPath, this);
				AudioTool.setAudioHistory(mContext, type, path);
				isChange = true;
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public void onClick(View view) {
		Intent intent = new Intent(PlayerSettingActivity.this, FileActivity.class);
		intent.putExtra(FileActivity.FileType, FileActivity.File_Music_Only);
		intent.putExtra(FileActivity.FileDir, mLastSelectPath);
		intent.putExtra(FileActivity.GlobalSetting, isGlobalSetting);
		switch (view.getId()) {
		case R.id.player_setting_back:
			this.finish();
			break;
		case R.id.audio_lizhi_bgm_dir:
			mAudioType = AudioTool.Type_Lizhi_BGM;
			if (isGlobalSetting) {
				intent.putExtra(FileActivity.AudioType, mAudioType);
				startActivityForResult(intent, mAudioType);
			} else {
				popWinDownUtil.showAsPopUpFromBottom();
			}			
			break;
		case R.id.audio_lizhi_dir:
			mAudioType = AudioTool.Type_Lizhi;
			if (isGlobalSetting) {
				intent.putExtra(FileActivity.AudioType, mAudioType);
				startActivityForResult(intent, mAudioType);
			} else {
				popWinDownUtil.showAsPopUpFromBottom();
			}
			break;
		case R.id.audio_zimo_dir:
			mAudioType = AudioTool.Type_Zimo;
			if (isGlobalSetting) {
				intent.putExtra(FileActivity.AudioType, mAudioType);
				startActivityForResult(intent, mAudioType);
			} else {
				popWinDownUtil.showAsPopUpFromBottom();
			}
			break;
		case R.id.audio_ronghe_dir:
			mAudioType = AudioTool.Type_Ronghe;
			if (isGlobalSetting) {
				intent.putExtra(FileActivity.AudioType, mAudioType);
				startActivityForResult(intent, mAudioType);
			} else {
				popWinDownUtil.showAsPopUpFromBottom();
			}
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
	
	private void initPopUpWin() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dropdown_file_select, null);
		view.findViewById(R.id.dropdown_file_music_history).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						popWinDownUtil.hide();
						showAudioHistoryDialog(mAudioType);
					}
				});
		view.findViewById(R.id.dropdown_file_all).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						popWinDownUtil.hide();
						Intent intent = new Intent(PlayerSettingActivity.this, FileActivity.class);
						intent.putExtra(FileActivity.FileType, FileActivity.File_Music_Only);
						intent.putExtra(FileActivity.FileDir, mLastSelectPath);
						intent.putExtra(FileActivity.GlobalSetting, isGlobalSetting);
						switch (mAudioType) {
						case AudioTool.Type_Lizhi_BGM:
						case AudioTool.Type_Lizhi:
						case AudioTool.Type_Zimo:
						case AudioTool.Type_Ronghe:
							intent.putExtra(FileActivity.AudioType, mAudioType);
							startActivityForResult(intent, mAudioType);
							break;
						default:
							break;
						}
					}
				});
		popWinDownUtil = new PopWinDownUtil(mContext, view, mMainLinearLayout);
	}
	
	private void showAudioHistoryDialog(final int type) {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, true);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getString(R.string.please_select));
		ListView listView = (ListView) mDialog.getContentView();
		StringArrayAdapter adapter = new StringArrayAdapter(mContext);
		listView.setAdapter(adapter);
		final List<String> historyList = AudioTool.getAudioHistory(mContext, type);
		List<String> nameList = new ArrayList<String>();
		for (int i = 0; i < historyList.size(); i++) {
			nameList.add(FileTools.getFileNameNoEx(historyList.get(i)));
		}
		adapter.setData(nameList);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String path = historyList.get(position);
				setAudio(type, path);
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
}

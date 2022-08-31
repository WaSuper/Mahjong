package com.mahjong.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.activity.jpn.GameSimpleActivity;
import com.mahjong.adapter.PlayerSimpleAdapter;
import com.mahjong.adapter.StringArrayAdapter;
import com.mahjong.dialog.SoundBoxSelectDialog;
import com.mahjong.dialog.SoundBoxSelectDialog.OnSoundBoxSelectListener;
import com.mahjong.model.AudioItem;
import com.mahjong.model.Player;
import com.mahjong.model.SoundBox;
import com.mahjong.tools.AudioTool;
import com.mahjong.tools.FileTools;
import com.mahjong.tools.PopWinDownUtil;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.CommonDialog;

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

public class PlayerSettingActivity extends BaseActivity 
		implements OnClickListener, OnCheckedChangeListener {

	private LinearLayout mMainLinearLayout;
	private ImageView mBackView;
	private CheckBox mLizhiBgmBox;
	private CheckBox mSoundEffectBox;
	private TextView mSoundEffectText;
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
		mLizhiBgmBox = (CheckBox) findViewById(R.id.audio_lizhi_bgm_check);
		mSoundEffectBox = (CheckBox) findViewById(R.id.audio_sound_effect_check);
		mSoundEffectText = (TextView) findViewById(R.id.audio_sound_effect_dir);
		mAudioDirViews = new TextView[6];
		mAudioDirViews[0] = (TextView) findViewById(R.id.audio_lizhi_bgm_dir);
		mAudioDirViews[1] = (TextView) findViewById(R.id.audio_lizhi_dir);
		mAudioDirViews[2] = (TextView) findViewById(R.id.audio_dlizhi_dir);
		mAudioDirViews[3] = (TextView) findViewById(R.id.audio_zimo_dir);
		mAudioDirViews[4] = (TextView) findViewById(R.id.audio_ronghe_dir);
		mAudioDirViews[5] = (TextView) findViewById(R.id.audio_gametop_dir);
		
		if (isGlobalSetting) { // 全局设置
			mLizhiBgmBox.setVisibility(View.GONE);
			mSoundEffectBox.setVisibility(View.GONE);
		} else {
			mAudioList = AudioItem.loadItemsById(mPlayerId);
			if (mAudioList != null && mAudioList.size() > 0) {
				for (AudioItem audioItem : mAudioList) {
					if (audioItem.getType() == AudioTool.Type_SoundBox) {
						mSoundEffectBox.setChecked(audioItem.getEnable());
						if (audioItem.getFilePath() == null || audioItem.getFilePath().isEmpty()) {
							mSoundEffectText.setText(R.string.click_to_select);
						} else {
							mSoundEffectText.setText(audioItem.getFilePath());
						}						
						setSoundBoxList(audioItem.getSoundBoxId());
					} else if (audioItem.getType() == AudioTool.Type_Lizhi_BGM) {
						mLizhiBgmBox.setChecked(audioItem.getEnable());
						showFilePath(mAudioDirViews[0], audioItem.getFilePath(), true);
					}
				}
			}
		}		
		
		mBackView.setOnClickListener(this);
		mLizhiBgmBox.setOnCheckedChangeListener(this);
		mAudioDirViews[0].setOnClickListener(this);
		mSoundEffectBox.setOnCheckedChangeListener(this);
		mSoundEffectText.setOnClickListener(this);		
	}
	
	private void showFilePath(TextView textView, String path, boolean isBgm) {
		if (path == null || path.isEmpty()) {
			if (isBgm) {
				textView.setText(R.string.click_to_select);
			} else {
				textView.setText(R.string.no_setting);
			}			
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
			AudioItem item = getAudioItem(type);
			switch (type) {
			case AudioTool.Type_Lizhi_BGM:
				showFilePath(mAudioDirViews[0], path, true);
				item.setFilePath(path);
				item.save();
				ShareprefenceTool.getInstance().setString(
						FileActivity.LastSecletPath, mLastSelectPath, this);
				AudioTool.setAudioHistory(mContext, type, path);
				isChange = true;
				break;
			case AudioTool.Type_SoundBox:
				mSoundEffectText.setText(path);		
				item.setFilePath(path);		
				item.save();	
				isChange = true;
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.player_setting_back:
			this.finish();
			break;
		case R.id.audio_lizhi_bgm_dir:
			doClickItem(AudioTool.Type_Lizhi_BGM, false);
			break;
		case R.id.audio_sound_effect_dir:
			showSoundBoxSelectDialog();
			break;
		default:
			break;
		}
	}
	
	private void doClickItem(int type, boolean isDriect2Activity) {
		Intent intent = new Intent(PlayerSettingActivity.this, FileActivity.class);
		intent.putExtra(FileActivity.FileType, FileActivity.File_Music_Only);
		intent.putExtra(FileActivity.FileDir, mLastSelectPath);
		intent.putExtra(FileActivity.GlobalSetting, isGlobalSetting);
		mAudioType = type;
		if (isGlobalSetting || isDriect2Activity) {
			intent.putExtra(FileActivity.AudioType, mAudioType);
			startActivityForResult(intent, mAudioType);
		} else {
			popWinDownUtil.showAsPopUpFromBottom();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton cbox, boolean checked) {
		int type = 0;
		switch (cbox.getId()) {
		case R.id.audio_lizhi_bgm_check:
			type = AudioTool.Type_Lizhi_BGM;
			break;
		case R.id.audio_sound_effect_check:
			type = AudioTool.Type_SoundBox;
			break;
		default:
			break;
		}
		switch (type) {
		case AudioTool.Type_Lizhi_BGM:
		case AudioTool.Type_SoundBox:
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
						doClickItem(mAudioType, true);
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
	
	private void showSoundBoxSelectDialog() {
		SoundBoxSelectDialog mDialog = new SoundBoxSelectDialog(mContext);
		mDialog.setOnSoundBoxSelectListener(new OnSoundBoxSelectListener() {
			
			@Override
			public void onSelect(SoundBox soundbox) {
				if (isGlobalSetting) {
					showPlayerSelectDialog(soundbox);
				} else {
					AudioItem item = getAudioItem(AudioTool.Type_SoundBox);
					mSoundEffectText.setText(soundbox.getName());		
					item.setFilePath(soundbox.getName());		
					item.setSoundBoxId(soundbox.getUuid());
					item.save();	
					setSoundBoxList(soundbox.getUuid());
					isChange = true;
				}				
			}
		});
		mDialog.show();
	}
	
	private void setSoundBoxList(long soundboxId) {
		List<AudioItem> soundList = AudioItem.loadItemsBySoundBoxId(soundboxId);
		if (soundList != null && soundList.size() > 0) {
			for (AudioItem soundItem : soundList) {
				if (soundItem.getType() == AudioTool.Type_SoundBox) continue;
				int index = soundItem.getType() - 1;
				showFilePath(mAudioDirViews[index], soundItem.getFilePath(), false);
			}
		}
	}
    
    private void showPlayerSelectDialog(final SoundBox soundbox) {
    	final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getString(R.string.please_choose_player));
		mDialog.ok.setText(getResources().getString(R.string.ok));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		ListView listView = (ListView) mDialog.getContentView();
		PlayerSimpleAdapter mAdapter = new PlayerSimpleAdapter(mContext);
		listView.setAdapter(mAdapter);
		final List<Player> list = Player.getAllPlayer();
		mAdapter.setData(list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Player player = list.get(position);
				AudioItem audioItem = AudioItem.loadItemByIdAndType(
						player.getUuid(), AudioTool.Type_SoundBox);
				if (audioItem != null) {
					audioItem.setSoundBoxId(soundbox.getUuid());
					audioItem.setFilePath(soundbox.getName());
					audioItem.save();
				} else {
					audioItem = new AudioItem(player.getUuid(), soundbox.getUuid(), 
							AudioTool.Type_SoundBox, soundbox.getName(), false);
					audioItem.save();
				}
				ToastTool.showToast(mContext, R.string.success);
				mDialog.dismiss();
			}
			
		});
		mDialog.show();
    }
	
}

package com.mahjong.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.adapter.StringArrayAdapter;
import com.mahjong.model.AudioItem;
import com.mahjong.model.SoundBox;
import com.mahjong.tools.AudioTool;
import com.mahjong.tools.FileTools;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.PopWinDownUtil;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.CommonDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SoundEffectDetailActivity extends BaseActivity
		implements OnClickListener {

	public static final int Request_SoundEffect_Icon = 0x6001;
	public static final int Request_SoundEffect_Path = 0x6002;

	private Context mContext;

	private LinearLayout mMainLinearLayout;
	private ImageView mIconInnerView;
	private ImageView mIconOuterView;
	private TextView mNameView;
	private ImageView mCloseView;
	private TextView[] mAudioDirViews;
	
	private SoundBox mSoundBox;
	private List<AudioItem> mAudioList;
	private String mLastSelectPath = "";
	
	private PopWinDownUtil popWinDownUtil;
	private int mAudioType = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soundeffect_detail);
		mContext = this;
		// get data
		long uuid = getIntent().getLongExtra(SoundBox.Col_Uuid, -1);
		mSoundBox = SoundBox.getSoundBox(uuid);
		mLastSelectPath = ShareprefenceTool.getInstance()
				.getString(FileActivity.LastSecletPath, this, "");
		// init ui
		initUI();
		initPopUpWin();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String path = data.getStringExtra(FileActivity.FileDir);
			mLastSelectPath = new File(path).getParent();
			switch (requestCode) {
			case Request_SoundEffect_Icon:
				showIcon(path);
				SoundBox.modifySoundBoxIcon(mSoundBox, path);
				setResult(RESULT_OK);
				break;
			case Request_SoundEffect_Path:
				int type = data.getIntExtra(FileActivity.AudioType, 0);
				setAudio(type, path);			
				break;
			default:
				break;
			}
		} else if (resultCode == RESULT_CANCELED) {
			//mLastSelectPath = "";
		}
	}
	
	private void setAudio(int type, String path) {
		if (path != null && path != "") {
			mLastSelectPath = new File(path).getParent();
			switch (type) {
			case AudioTool.Type_Lizhi:
			case AudioTool.Type_DoubleLizhi:
			case AudioTool.Type_Zimo:
			case AudioTool.Type_Ronghe:
			case AudioTool.Type_GameTop:
				//mAudioDirViews[requestCode - 1].setText(path);
				showFilePath(mAudioDirViews[type - 2], path);						
				AudioItem item = getAudioItem(type);
				item.setFilePath(path);
				item.save();
				ShareprefenceTool.getInstance().setString(
						FileActivity.LastSecletPath, mLastSelectPath, this);
				AudioTool.setAudioHistory(mContext, type, path);
				break;
			default:
				break;
			}
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
		AudioItem item = new AudioItem(mSoundBox.getUuid(), type, "", false);	
		mAudioList.add(item);
		return item;		
	}

	private void initUI() {
		mMainLinearLayout = (LinearLayout) findViewById(R.id.soundeffect_detail_ll_main);
		mIconInnerView = (ImageView) findViewById(R.id.soundeffect_detail_icon_inner);
		mIconOuterView = (ImageView) findViewById(R.id.soundeffect_detail_icon_outer);
		mNameView = (TextView) findViewById(R.id.soundeffect_detail_name);
		mCloseView = (ImageView) findViewById(R.id.soundeffect_detail_close);
		mAudioDirViews = new TextView[5];
		mAudioDirViews[0] = (TextView) findViewById(R.id.soundeffect_detail_lizhi_dir);
		mAudioDirViews[1] = (TextView) findViewById(R.id.soundeffect_detail_dlizhi_dir);
		mAudioDirViews[2] = (TextView) findViewById(R.id.soundeffect_detail_zimo_dir);
		mAudioDirViews[3] = (TextView) findViewById(R.id.soundeffect_detail_ronghe_dir);
		mAudioDirViews[4] = (TextView) findViewById(R.id.soundeffect_detail_gametop_dir);
		
		mIconOuterView.setOnClickListener(this);
		mNameView.setOnClickListener(this);
		mCloseView.setOnClickListener(this);
		for (TextView textView : mAudioDirViews) {
			textView.setOnClickListener(this);
		}
		
		mAudioList = AudioItem.loadItemsBySoundBoxId(mSoundBox.getUuid());
		if (mAudioList != null && mAudioList.size() > 0) {
			for (AudioItem audioItem : mAudioList) {
				if (audioItem.getType() == AudioTool.Type_SoundBox) continue;
				int index = audioItem.getType() - 2;
				showFilePath(mAudioDirViews[index], audioItem.getFilePath());
			}
		}
		
		mNameView.setText(mSoundBox.getName());
		showIcon(mSoundBox.getDefaultIcon());
	}
	
	private void showIcon(String path) {
		ImageLoader.getInstance().loadImage("file://" + path,
				new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						
					}
					
					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						mIconInnerView.setImageBitmap(null);
						mIconOuterView.setImageResource(R.drawable.soundbox_default);
					}
					
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						loadedImage = ImageTool.createCircleImage(loadedImage, loadedImage.getWidth());
						mIconInnerView.setImageBitmap(loadedImage);
						mIconOuterView.setImageResource(R.drawable.soundbox_bg);
					}
					
					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						mIconInnerView.setImageBitmap(null);
						mIconOuterView.setImageResource(R.drawable.soundbox_default);
					}
				});		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.soundeffect_detail_icon_outer:
			onClickIcon();
			break;
		case R.id.soundeffect_detail_name:
			editSoundBox();
			break;
		case R.id.soundeffect_detail_close:
			finish();
			break;
		case R.id.soundeffect_detail_lizhi_dir:
			mAudioType = AudioTool.Type_Lizhi;
			popWinDownUtil.showAsPopUpFromBottom();
			break;
		case R.id.soundeffect_detail_dlizhi_dir:
			mAudioType = AudioTool.Type_DoubleLizhi;
			popWinDownUtil.showAsPopUpFromBottom();
			break;
		case R.id.soundeffect_detail_zimo_dir:
			mAudioType = AudioTool.Type_Zimo;
			popWinDownUtil.showAsPopUpFromBottom();
			break;
		case R.id.soundeffect_detail_ronghe_dir:
			mAudioType = AudioTool.Type_Ronghe;
			popWinDownUtil.showAsPopUpFromBottom();
			break;
		case R.id.soundeffect_detail_gametop_dir:
			mAudioType = AudioTool.Type_GameTop;
			popWinDownUtil.showAsPopUpFromBottom();
			break;
		default:
			break;
		}
	}
	
	private void onClickItem(int type) {
		if (mSoundBox.getUuid() < 0) return;
		Intent intent = new Intent(SoundEffectDetailActivity.this, FileActivity.class);
		intent.putExtra(FileActivity.FileType, FileActivity.File_Music_Only);
		intent.putExtra(FileActivity.AudioType, type);
		intent.putExtra(FileActivity.FileDir, mLastSelectPath);
		intent.putExtra(FileActivity.GlobalSetting, false);
		startActivityForResult(intent, Request_SoundEffect_Path);
	}
	
	private void onClickIcon() {
		if (mSoundBox.getUuid() < 0) return;
		Intent intent = new Intent(SoundEffectDetailActivity.this, FileActivity.class);
		intent.putExtra(FileActivity.FileType, FileActivity.File_Picture_Only);
		intent.putExtra(FileActivity.FileDir, mLastSelectPath);
		startActivityForResult(intent, Request_SoundEffect_Icon);
	}
	
	private void editSoundBox() {
		if (mSoundBox.getUuid() < 0) return;
		final CommonDialog editDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		editDialog.addView(R.layout.item_edittext);
		editDialog.setCanceledOnTouchOutside(true);
		editDialog.titleTextView.setText(getString(R.string.sound_effect));
		final EditText editText = (EditText) editDialog.getContentView().findViewById(R.id.edittext);
		editText.setText(mSoundBox.getName());
		editDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String text = editText.getText().toString().trim();
				if (text.isEmpty()) {
					ToastTool.showToast(mContext, R.string.no_data);
					return;
				}
				boolean result = SoundBox.modifySoundBoxName(mSoundBox, text);
				if (result) {
					mNameView.setText(text);
					editDialog.dismiss();
					setResult(RESULT_OK);
				} else {
					ToastTool.showToast(mContext, R.string.modify_fail);
				}				
			}
		});
		editDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				editDialog.dismiss();
			}
		});
		editDialog.show();
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
						onClickItem(mAudioType);
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

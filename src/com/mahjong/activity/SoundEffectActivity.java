package com.mahjong.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.mahjong.R;
import com.mahjong.adapter.SoundEffectAdapter;
import com.mahjong.model.AudioItem;
import com.mahjong.model.Character;
import com.mahjong.model.SoundBox;
import com.mahjong.tools.AudioTool;
import com.mahjong.tools.FileTools;
import com.mahjong.tools.ToastTool;
import com.mahjong.tools.ValueTool;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.fab.FloatingActionButton;
import com.mahjong.ui.fab.FloatingActionsMenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class SoundEffectActivity extends Activity 
	implements OnClickListener, OnItemClickListener, OnItemLongClickListener {

	public static final int Request_SoundBox_Update = 0x5001;
	public static final int Request_SoundBox_Import = 0x5002;
	
	private Context mContext;
	
	private ImageView mBackView;
	private ImageView mInfoView;
	private ListView mListView;
	private SoundEffectAdapter mAdapter;
	private FloatingActionsMenu mMenu;
	private FloatingActionButton mActionCreate;
	private FloatingActionButton mActionImport;
	
	private List<SoundBox> mSoundBoxList;
	private String mLastSelectPath = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soundeffect);
		mContext = this;
		initUI();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Request_SoundBox_Update:
				mSoundBoxList = SoundBox.getAllSoundBoxs();
				mAdapter.setData(mSoundBoxList);
				break;
			case Request_SoundBox_Import:
				mLastSelectPath = data.getStringExtra(FileActivity.FileDir);
				String nameStrings = data.getStringExtra(FileActivity.FileSelects);
				importSoundBoxList(mLastSelectPath, nameStrings);
				break;
			default:
				break;
			}
		}
	}

	private void initUI() {
		mBackView = (ImageView) findViewById(R.id.soundeffect_back);
		mInfoView = (ImageView) findViewById(R.id.soundeffect_info);
		mMenu = (FloatingActionsMenu) findViewById(R.id.soundeffect_multiple_actions);
		mActionCreate = (FloatingActionButton) findViewById(R.id.soundeffect_action_create);
		mActionImport = (FloatingActionButton) findViewById(R.id.soundeffect_action_import);
		mListView = (ListView) findViewById(R.id.soundeffect_listview);
		mAdapter = new SoundEffectAdapter(mContext);
		mSoundBoxList = SoundBox.getAllSoundBoxs();
		mAdapter.setData(mSoundBoxList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);

		mBackView.setOnClickListener(this);
		mInfoView.setOnClickListener(this);
		mActionCreate.setOnClickListener(this);
		mActionImport.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.soundeffect_back:
			finish();
			break;
		case R.id.soundeffect_action_create:
			createSoundBox();
			mMenu.collapse();
			break;
		case R.id.soundeffect_action_import:
			Intent intent = new Intent(SoundEffectActivity.this, FileActivity.class);
			intent.putExtra(FileActivity.FileType, FileActivity.File_Music_Only);
			intent.putExtra(FileActivity.FileDir, mLastSelectPath);
			intent.putExtra(FileActivity.FileShowBottom, true);
			startActivityForResult(intent, Request_SoundBox_Import);
			mMenu.collapse();
			break;			
		case R.id.soundeffect_info:
			showInformation();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SoundBox soundbox = mSoundBoxList.get(position);
		Intent intent = new Intent(SoundEffectActivity.this, SoundEffectDetailActivity.class);
		intent.putExtra(Character.Col_Uuid, soundbox.getUuid());
		startActivityForResult(intent, Request_SoundBox_Update);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		deleteSoundBox(position);
		return true;
	}
	
	private void createSoundBox() {
		final CommonDialog createDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		createDialog.addView(R.layout.item_edittext);
		createDialog.setCanceledOnTouchOutside(true);
		createDialog.titleTextView.setText(getString(R.string.sound_effect));
		final EditText editText = (EditText) createDialog.getContentView().findViewById(R.id.edittext);
		editText.setText("");
		createDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String text = editText.getText().toString().trim();
				if (text.isEmpty()) {
					ToastTool.showToast(mContext, R.string.no_data);
					return;
				}
				int index = 0;
				if (mSoundBoxList.size() > 0) {
					index = mSoundBoxList.get(mSoundBoxList.size() - 1).getIndex() + 1;
				}
				SoundBox soundbox = SoundBox.createSoundBox(text, null, index);
				if (soundbox != null) {
					mSoundBoxList.add(soundbox);
					mAdapter.notifyDataSetChanged();
				} else {
					ToastTool.showToast(mContext, R.string.add_log_fail);
				}				
				createDialog.dismiss();
			}
		});
		createDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				createDialog.dismiss();
			}
		});
		createDialog.show();
	}
	
	private void deleteSoundBox(final int position) {
		final CommonDialog deleteDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		deleteDialog.addView(R.layout.item_text);
		deleteDialog.setCanceledOnTouchOutside(true);
		deleteDialog.titleTextView.setText(getString(R.string.tip));
		TextView textView = (TextView) deleteDialog.getContentView().findViewById(R.id.item_text);
		textView.setText(getString(R.string.ensure_delete_soundbox));
		deleteDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {			
				boolean result = SoundBox.deleteSoundBox(mSoundBoxList.get(position));
				if (result) {
					mSoundBoxList.remove(position);
					ToastTool.showToast(mContext, R.string.delete_success);
					mAdapter.notifyDataSetChanged();
				} else {
					ToastTool.showToast(mContext, R.string.delete_fail);
				}		
				deleteDialog.dismiss();		
			}
		});
		deleteDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				deleteDialog.dismiss();
			}
		});
		deleteDialog.show();
	}
	
	private void importSoundBoxList(String path, String nameStrings) {
		File parentFile = new File(path);
		if (!parentFile.exists()) {
			ToastTool.showToast(mContext, R.string.import_fail);
			return;
		}
		File[] list = parentFile.listFiles();
		if (list == null || list.length == 0) {
			ToastTool.showToast(mContext, R.string.import_fail);
			return;
		}
		
		String regularEx = "#!#";
		String[] names = nameStrings.split(regularEx);

		ActiveAndroid.beginTransaction();
		int count = 0;
		for (String name : names) {
			//System.out.println("tag-s " + path + "/" + name);
			boolean isExist = false;
			for (SoundBox soundbox :mSoundBoxList) {
				if (soundbox.getName().equals(name)) {
					isExist = true;
					break;
				}
			}
			if (!isExist && importSoundBox(path + "/" + name)) {
				count++;
			}
		}		
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();		
		
		if (count > 0) {
			mAdapter.notifyDataSetChanged();
			ToastTool.showToast(mContext, getString(R.string.import_soundbox_success, count));			
		} else {
			ToastTool.showToast(mContext, R.string.import_fail);
		}
	}
	
	private boolean importSoundBox(String path) {
		File parentFile = new File(path);
		if (!parentFile.exists()) {
			return false;
		}
		File[] list = parentFile.listFiles();
		if (list == null || list.length == 0) {
			return false;
		}
		int index = 0;
		if (mSoundBoxList.size() > 0) {
			index = mSoundBoxList.get(mSoundBoxList.size() - 1).getIndex() + 1;
		}
		SoundBox newSoundBox = new SoundBox(System.currentTimeMillis(), 
				parentFile.getName(), null, index);
		List<AudioItem> audioList = new ArrayList<AudioItem>();
		boolean isIconFound = false;
		for (File file : list) {
			if (file.isDirectory()) continue;
			String extp = FileTools.getExtension(file.getName()); // 选择文件类型
			if (extp == null || extp.isEmpty()) continue;
			// 先找到封面图
			if (!isIconFound) {
				for (String s : FileTools.Picture_SupportTypeArray) {
					if (extp.equalsIgnoreCase(s)) {
						String name = FileTools.getFileNameNoEx(file.getName()); // 截取名字
						if (name.endsWith("_0_1")) { 
							newSoundBox.setDefaultIcon(file.getAbsolutePath());
							isIconFound = true;
							break;
						}
					}
				}
				if (isIconFound) continue;
			}		
			// 再找到音频
			for (String s : FileTools.Music_SupportTypeArray) {                
                if (extp.equalsIgnoreCase(s)) {
                	String name = FileTools.getFileNameNoEx(file.getName()); // 截取名字
                	if (name.endsWith("_act_rich")) { // 立直音效
						AudioItem audio = new AudioItem(newSoundBox.getUuid(), 
								AudioTool.Type_Lizhi, file.getAbsolutePath(), true);
						audioList.add(audio);
					} else if (name.endsWith("_act_drich")) { // 双立直音效
						AudioItem audio = new AudioItem(newSoundBox.getUuid(), 
								AudioTool.Type_DoubleLizhi, file.getAbsolutePath(), true);	
						audioList.add(audio);					
					} else if (name.endsWith("_act_tumo")) { // 自摸音效
						AudioItem audio = new AudioItem(newSoundBox.getUuid(), 
								AudioTool.Type_Zimo, file.getAbsolutePath(), true);	
						audioList.add(audio);
					} else if (name.endsWith("_act_ron")) { // 荣和音效
						AudioItem audio = new AudioItem(newSoundBox.getUuid(), 
								AudioTool.Type_Ronghe, file.getAbsolutePath(), true);	
						audioList.add(audio);
					} else if (name.endsWith("_game_top")) { // 第一位音效
						AudioItem audio = new AudioItem(newSoundBox.getUuid(), 
								AudioTool.Type_GameTop, file.getAbsolutePath(), true);
						audioList.add(audio);	
					}
                    break;
                }
            }
		}
		if (audioList.size() == 0) {
			return false;
		}
		boolean result = false;
		try {
			newSoundBox.save();
			for (AudioItem audio : audioList) {
				audio.save();
			}
			result = true;
		} catch (Exception e) {
			Log.e("SoundEffectActivity", e.getMessage());
		}
		if (result) {
			mSoundBoxList.add(newSoundBox);
			return true;
		} else {
			return false;
		}				
	}
	
	private void showInformation() {
		final CommonDialog infoDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		infoDialog.addView(R.layout.item_text);
		infoDialog.setCanceledOnTouchOutside(true);
		infoDialog.titleTextView.setText(getString(R.string.sound_effect_import_title));
		TextView textView = (TextView) infoDialog.getContentView().findViewById(R.id.item_text);
		textView.setText(getString(R.string.sound_effect_import_information));
		textView.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		textView.setTextSize(ValueTool.sp2px(mContext, 6));
		infoDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {	
				infoDialog.dismiss();
			}
		});
		infoDialog.show();
	}
	
}

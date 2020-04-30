package com.mahjong.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.mahjong.R;
import com.mahjong.adapter.EmoticonAdapter;
import com.mahjong.model.Character;
import com.mahjong.model.CharacterIcon;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class EmoticonActivity extends Activity 
		implements OnClickListener, OnItemClickListener, OnItemLongClickListener {

	public static final int Request_Emoticon_Update = 0x3001;
	public static final int Request_Emoticon_Import = 0x3002;
	
	private Context mContext;
	
	private ImageView mBackView;
	private ImageView mInfoView;
	private ListView mListView;
	private EmoticonAdapter mAdapter;	
	private FloatingActionsMenu mMenu;
	private FloatingActionButton mActionCreate;
	private FloatingActionButton mActionImport;
	
	private List<Character> mCharacterList;
	private String mLastSelectPath = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emoticon);
		mContext = this;
		initUI();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Request_Emoticon_Update:
				mCharacterList = Character.getAllCharacters(this);
				mAdapter.setData(mCharacterList);
				break;
			case Request_Emoticon_Import:
				mLastSelectPath = data.getStringExtra(FileActivity.FileDir);
				importCharacter(mLastSelectPath);
				break;
			default:
				break;
			}
		}
	}
	
	private void initUI() {
		mBackView = (ImageView) findViewById(R.id.emoticon_back);
		mInfoView = (ImageView) findViewById(R.id.emoticon_info);
		mMenu = (FloatingActionsMenu) findViewById(R.id.emoticon_multiple_actions);
		mActionCreate = (FloatingActionButton) findViewById(R.id.emoticon_action_create);
		mActionImport = (FloatingActionButton) findViewById(R.id.emoticon_action_import);
		mListView = (ListView) findViewById(R.id.emoticon_listview);
		mAdapter = new EmoticonAdapter(this);
		mCharacterList = Character.getAllCharacters(this);
		mAdapter.setData(mCharacterList);
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
		case R.id.emoticon_back:
			finish();
			break;
		case R.id.emoticon_action_create:
			createCharacter();
			mMenu.collapse();
			break;
		case R.id.emoticon_action_import:
			Intent intent = new Intent(EmoticonActivity.this, FileActivity.class);
			intent.putExtra(FileActivity.FileType, FileActivity.File_Picture_Only);
			intent.putExtra(FileActivity.FileDir, mLastSelectPath);
			intent.putExtra(FileActivity.FileShowBottom, true);
			startActivityForResult(intent, Request_Emoticon_Import);
			mMenu.collapse();
			break;			
		case R.id.emoticon_info:
			showInformation();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Character character = mCharacterList.get(position);
		Intent intent = new Intent(EmoticonActivity.this, EmoticonDetailActivity.class);
		intent.putExtra(Character.Col_Uuid, character.getUuid());
		startActivityForResult(intent, Request_Emoticon_Update);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 0) return true;
		deleteCharacter(position);
		return true;
	}
	
	private void createCharacter() {
		final CommonDialog createDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		createDialog.addView(R.layout.item_edittext);
		createDialog.setCanceledOnTouchOutside(true);
		createDialog.titleTextView.setText(getString(R.string.emoticon));
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
				Character character = Character.createCharacter(
						mCharacterList.get(mCharacterList.size() - 1), text, null);
				if (character != null) {
					mCharacterList.add(character);
					mAdapter.notifyDataSetChanged();
					createDialog.dismiss();
				} else {
					ToastTool.showToast(mContext, R.string.add_log_fail);
				}				
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
	
	private void deleteCharacter(final int position) {
		final CommonDialog deleteDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		deleteDialog.addView(R.layout.item_text);
		deleteDialog.setCanceledOnTouchOutside(true);
		deleteDialog.titleTextView.setText(getString(R.string.tip));
		TextView textView = (TextView) deleteDialog.getContentView().findViewById(R.id.item_text);
		textView.setText(getString(R.string.ensure_delete_emoticon_package));
		deleteDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {			
				boolean result = Character.deleteCharacter(mCharacterList.get(position));
				if (result) {
					mCharacterList.remove(position);
					ToastTool.showToast(mContext, R.string.delete_success);
					mAdapter.notifyDataSetChanged();
					deleteDialog.dismiss();
				} else {
					ToastTool.showToast(mContext, R.string.delete_fail);
				}				
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
	
	private void importCharacter(String path) {
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
		Character newCharacter = new Character(System.currentTimeMillis(), 
				parentFile.getName(), null, 
				mCharacterList.get(mCharacterList.size() - 1).getIndex() + 1);
		List<CharacterIcon> rankList1 = new ArrayList<CharacterIcon>();
		List<CharacterIcon> rankList2 = new ArrayList<CharacterIcon>();
		List<CharacterIcon> rankList3 = new ArrayList<CharacterIcon>();
		List<CharacterIcon> rankList4 = new ArrayList<CharacterIcon>();
		List<List<CharacterIcon>> rankLists = new ArrayList<List<CharacterIcon>>();
		rankLists.add(rankList1);
		rankLists.add(rankList2);
		rankLists.add(rankList3);
		rankLists.add(rankList4);
		for (File file : list) {
			if (file.isDirectory()) continue;
			for (String s : FileTools.Picture_SupportTypeArray) {
                String extp = FileTools.getExtension(file.getName()); // 选择图片类型
                if (extp != null && extp.equalsIgnoreCase(s)) {
                	String name = FileTools.getFileNameNoEx(file.getName()); // 截取名字
                	String[] nameSplits = name.split("_"); // 命名规则：emo_0_1
                	if (nameSplits.length > 2) {
                		try {
    						String rankString = nameSplits[nameSplits.length - 2];
    						String numString  = nameSplits[nameSplits.length - 1];
    						int rank = Integer.parseInt(rankString);
    						int num = Integer.parseInt(numString);	
    						if (rank == 0) { // 封面图
								newCharacter.setDefaultIcon(file.getAbsolutePath());
							} else if (rank > 0 && rank < 5) { // 排位图
								CharacterIcon icon = new CharacterIcon(
										newCharacter.getUuid(), file.getAbsolutePath(), rank, num, name);
								rankLists.get(rank - 1).add(icon);
							}
						} catch (Exception e) {
							
						}
					}
                    break;
                }
            }
		}
		Comparator<CharacterIcon> comparator = new Comparator<CharacterIcon>() {

			@Override
			public int compare(CharacterIcon c0, CharacterIcon c1) {
				return c0.getIndex() - c1.getIndex();
			}
		};
		for (List<CharacterIcon> icons : rankLists) {
			Collections.sort(icons, comparator);
		}
		boolean result = false;
		ActiveAndroid.beginTransaction();
		try {
			newCharacter.save();
			for (List<CharacterIcon> icons : rankLists) {
				for (int i = 0; i < icons.size(); i++) {
					icons.get(i).setIndex(i + 1);
					icons.get(i).save();
				}
			}
			ActiveAndroid.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			Log.e("EmoticonActivity", e.getMessage());
		} finally {
			ActiveAndroid.endTransaction();
		}
		if (result) {
			mCharacterList.add(newCharacter);
			mAdapter.notifyDataSetChanged();
			ToastTool.showToast(mContext, R.string.import_success);			
		} else {
			ToastTool.showToast(mContext, R.string.import_fail);
		}
	}
	
	private void showInformation() {
		final CommonDialog infoDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		infoDialog.addView(R.layout.item_text);
		infoDialog.setCanceledOnTouchOutside(true);
		infoDialog.titleTextView.setText(getString(R.string.emoticon_import_title));
		TextView textView = (TextView) infoDialog.getContentView().findViewById(R.id.item_text);
		textView.setText(getString(R.string.emoticon_import_information));
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

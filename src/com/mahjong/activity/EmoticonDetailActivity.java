package com.mahjong.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.adapter.EmoticonDetailAdapter;
import com.mahjong.model.Character;
import com.mahjong.model.CharacterIcon;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.CommonDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class EmoticonDetailActivity extends BaseActivity 
		implements OnClickListener {

	public static final int Request_Emoticon_Icon = 0x4001;
	public static final int Request_Emoticon_Path = 0x4002;
	
	private Context mContext;
	
	private ImageView mIconView;
	private TextView mNameView;
	private ImageView mCloseView;
	private ImageView[] mAddViews;
	private ListView[] mRankListViews;
	private EmoticonDetailAdapter[] mRankAdapters;
	
	private Character mCharacter;
	private int mCurrentRank = 0;
	private String mLastSelectPath = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emoticon_detail);
		mContext = this;
		// get data
		long uuid = getIntent().getLongExtra(Character.Col_Uuid, -1);
		mCharacter = Character.getCharacter(mContext, uuid);
		// init ui
		initUI();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String path = data.getStringExtra(FileActivity.FileDir);
			mLastSelectPath = new File(path).getParent();
			switch (requestCode) {
			case Request_Emoticon_Icon:
				ImageLoader.getInstance().displayImage("file://" + path, mIconView);
				Character.modifyCharacterIcon(mCharacter, path);	
				setResult(RESULT_OK);
				break;
			case Request_Emoticon_Path:
				EmoticonDetailAdapter adapter = mRankAdapters[mCurrentRank - 1];
				if (adapter != null) {
					boolean isExist = false;
					for (CharacterIcon icon : mRankAdapters[mCurrentRank - 1].getData()) {
						if (icon.getPath().equals(path)) {
							isExist = true;
							break;
						}
					}
					if (!isExist) {
						List<CharacterIcon> rankList = CharacterIcon.addCharacterIcon(
								mCharacter.getUuid(), adapter.getData(), mCurrentRank, path);
						adapter.setData(rankList);
					} else {
						ToastTool.showToast(mContext, R.string.input_icon_exist);
					}
				}				
				break;
			default:
				break;
			}
		} else if (resultCode == RESULT_CANCELED) {
			//mLastSelectPath = "";
		}
	}
	
	private void initUI() {
		mIconView = (ImageView) findViewById(R.id.emoticon_detail_icon);
		mNameView = (TextView) findViewById(R.id.emoticon_detail_name);
		mCloseView = (ImageView) findViewById(R.id.emoticon_detail_close);
		mAddViews = new ImageView[4];
		mAddViews[0] = (ImageView) findViewById(R.id.emoticon_detail_add1);
		mAddViews[1] = (ImageView) findViewById(R.id.emoticon_detail_add2);
		mAddViews[2] = (ImageView) findViewById(R.id.emoticon_detail_add3);
		mAddViews[3] = (ImageView) findViewById(R.id.emoticon_detail_add4);
		mRankListViews = new ListView[4];
		mRankListViews[0] = (ListView) findViewById(R.id.emoticon_detail_list1);
		mRankListViews[1] = (ListView) findViewById(R.id.emoticon_detail_list2);
		mRankListViews[2] = (ListView) findViewById(R.id.emoticon_detail_list3);
		mRankListViews[3] = (ListView) findViewById(R.id.emoticon_detail_list4);
		mRankAdapters = new EmoticonDetailAdapter[4];
		
		mIconView.setOnClickListener(this);
		mNameView.setOnClickListener(this);
		mCloseView.setOnClickListener(this);
		for (int i = 0; i < 4; i++) {
			mAddViews[i].setOnClickListener(this);
			mRankAdapters[i] = new EmoticonDetailAdapter(mContext, i + 1);
			mRankListViews[i].setAdapter(mRankAdapters[i]);
			mRankListViews[i].setOnItemClickListener(new onEmoticonItemClick(i + 1));
			mRankListViews[i].setOnItemLongClickListener(new onEmoticonItemLongClick(i + 1));
		}
		
		mNameView.setText(mCharacter.getName());
		EmoticonTool.showEmoticon(mCharacter, mIconView, null);
		if (mCharacter.getUuid() == -1) {
			showDefaultIcon();
		} else if (mCharacter.getUuid() > 0) {		
			showCharacterIcon();
		}
	}
	
	private void showDefaultIcon() {		
		for (int i = 0; i < 4; i++) {
			List<CharacterIcon> mIconList = new ArrayList<CharacterIcon>();
			mIconList.add(new CharacterIcon(-1, null, i + 1, 0, null));
			mRankAdapters[i].setData(mIconList);
		}
	}
	
	private void showCharacterIcon() {
		List<List<CharacterIcon>> rankLists = CharacterIcon.getAllCharacterIcons(mCharacter.getUuid());
		for (int i = 0; i < 4; i++) {
			mRankAdapters[i].setData(rankLists.get(i));
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.emoticon_detail_close:
			finish();
			break;
		case R.id.emoticon_detail_icon:
			onClickIcon();
			break;
		case R.id.emoticon_detail_name:
			editCharacter();
			break;
		case R.id.emoticon_detail_add1:
			onClickAdd(1);
			break;
		case R.id.emoticon_detail_add2:
			onClickAdd(2);
			break;
		case R.id.emoticon_detail_add3:
			onClickAdd(3);
			break;
		case R.id.emoticon_detail_add4:
			onClickAdd(4);
			break;
		default:
			break;
		}
	}
	
	private void onClickIcon() {
		if (mCharacter.getUuid() < 0) return;
		Intent intent = new Intent(EmoticonDetailActivity.this, FileActivity.class);
		intent.putExtra(FileActivity.FileType, FileActivity.File_Picture_Only);
		intent.putExtra(FileActivity.FileDir, mLastSelectPath);
		startActivityForResult(intent, Request_Emoticon_Icon);
	}
	
	private void onClickAdd(int rank) {
		if (mCharacter.getUuid() < 0) return;
		mCurrentRank = rank;
		Intent intent = new Intent(EmoticonDetailActivity.this, FileActivity.class);
		intent.putExtra(FileActivity.FileType, FileActivity.File_Picture_Only);
		intent.putExtra(FileActivity.FileDir, mLastSelectPath);
		startActivityForResult(intent, Request_Emoticon_Path);
	}
	
	private void editCharacter() {
		if (mCharacter.getUuid() < 0) return;
		final CommonDialog editDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		editDialog.addView(R.layout.item_edittext);
		editDialog.setCanceledOnTouchOutside(true);
		editDialog.titleTextView.setText(getString(R.string.emoticon));
		final EditText editText = (EditText) editDialog.getContentView().findViewById(R.id.edittext);
		editText.setText(mCharacter.getName());
		editDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String text = editText.getText().toString().trim();
				if (text.isEmpty()) {
					ToastTool.showToast(mContext, R.string.no_data);
					return;
				}
				boolean result = Character.modifyCharacterName(mCharacter, text);
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

	class onEmoticonItemClick implements OnItemClickListener {
		
		private int rank;
		
		public onEmoticonItemClick(int rank) {
			this.rank = rank;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mCharacter.getUuid() < 0) return;
		}
		
	}
	
	
	class onEmoticonItemLongClick implements OnItemLongClickListener {
		
		private int rank;
		
		public onEmoticonItemLongClick(int rank) {
			this.rank = rank;
		}
		
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			if (mCharacter.getUuid() < 0) return false;
			EmoticonDetailAdapter adapter = mRankAdapters[rank - 1];
			deleteCharacterIcon(adapter, position);			
			return true;
		}
		
	}
	
	private void deleteCharacterIcon(final EmoticonDetailAdapter adapter, final int position) {
		final CommonDialog deleteDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		deleteDialog.addView(R.layout.item_text);
		deleteDialog.setCanceledOnTouchOutside(true);
		deleteDialog.titleTextView.setText(getString(R.string.tip));
		TextView textView = (TextView) deleteDialog.getContentView().findViewById(R.id.item_text);
		textView.setText(getString(R.string.ensure_delete_emoticon));
		deleteDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				List<CharacterIcon> iconList = adapter.getData();
				CharacterIcon icon = iconList.get(position);				
				boolean result = CharacterIcon.deleteCharacterIcon(icon);
				if (result) {
					iconList.remove(position);
					ToastTool.showToast(mContext, R.string.delete_success);
					adapter.notifyDataSetChanged();
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
	
}

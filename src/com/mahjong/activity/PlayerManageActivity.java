package com.mahjong.activity;

import java.io.File;
import java.util.List;

import com.activeandroid.query.Delete;
import com.mahjong.R;
import com.mahjong.adapter.PlayerManageAdapter;
import com.mahjong.model.AudioItem;
import com.mahjong.model.Character;
import com.mahjong.model.Player;
import com.mahjong.tools.ExcelUtils;
import com.mahjong.tools.ToastTool;
import com.mahjong.tools.ValueTool;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.fab.FloatingActionButton;
import com.mahjong.ui.fab.FloatingActionsMenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PlayerManageActivity extends Activity 
		implements OnClickListener, OnItemClickListener, OnItemLongClickListener {

	public static final int REQUEST_CREATEPLAYER 	= 0x1001;
	public static final int REQUEST_EDITPLAYER 		= 0x1002;
	public static final int REQUEST_PLAYER_IMPORT 	= 0x1003;
	public static final String SELECT_PLAYER_UUID = "SELECT_PLAYER_UUID";
	
	private Context mContext;
	
	private ImageView mBack;
	private FloatingActionsMenu mMenu;
	private FloatingActionButton mActionCreate;
	private FloatingActionButton mActionSearch;
	private FloatingActionButton mActionExport;
	private FloatingActionButton mActionImport;
	private ListView mPlayerList;
	private PlayerManageAdapter mPlayerAdapter;
	private ImageView mSetting;
	
	private String mLastSelectPath = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_manage);
		mContext = this;
		initUI();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_PLAYER_IMPORT:
				String path = data.getStringExtra(FileActivity.FileDir);
				mLastSelectPath = new File(path).getParent();
				int count = ExcelUtils.readExcelToPlayer(path);
				if (count > 0) {
					ToastTool.showToast(mContext, getString(R.string.import_player_success, count));
				} else {
					ToastTool.showToast(mContext, R.string.import_fail);
				}
			case REQUEST_CREATEPLAYER:
			case REQUEST_EDITPLAYER:
				List<Player> list = Player.getAllPlayer();
				mPlayerAdapter.setData(list);
				break;
			default:
				break;
			}
		}		
	}
	
	private void initUI() {
		mBack = (ImageView) findViewById(R.id.player_back);
		mMenu = (FloatingActionsMenu) findViewById(R.id.player_multiple_actions);
		mActionCreate = (FloatingActionButton) findViewById(R.id.player_action_create);
		mActionSearch = (FloatingActionButton) findViewById(R.id.player_action_search);
		mActionExport = (FloatingActionButton) findViewById(R.id.player_action_export);
		mActionImport = (FloatingActionButton) findViewById(R.id.player_action_import);
		mPlayerList = (ListView) findViewById(R.id.player_listview);
		mSetting = (ImageView) findViewById(R.id.player_setting);
		
		List<Player> players = Player.getAllPlayer();
		List<Character> characters = Character.getAllCharacters(mContext);
		mPlayerAdapter = new PlayerManageAdapter(this, players, characters);
		mPlayerList.setAdapter(mPlayerAdapter);
		mPlayerList.setOnItemClickListener(this);
		mPlayerList.setOnItemLongClickListener(this);
		
		mBack.setOnClickListener(this);
		mActionCreate.setOnClickListener(this);
		mActionSearch.setOnClickListener(this);
		mActionExport.setOnClickListener(this);
		mActionImport.setOnClickListener(this);
		mSetting.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.player_back:
			this.finish();
			break;
		case R.id.player_action_create:
			startActivityForResult(new Intent(PlayerManageActivity.this, CreatePlayerActivity.class), 
					REQUEST_CREATEPLAYER);
			mMenu.collapse();
			break;
		case R.id.player_action_search:
			showSearchDialog();
			mMenu.collapse();
			break;
		case R.id.player_action_export:
			showExportDiaolg();
			mMenu.collapse();
			break;
		case R.id.player_action_import:
			Intent intent = new Intent(PlayerManageActivity.this, FileActivity.class);
			intent.putExtra(FileActivity.FileType, FileActivity.File_Excel_Only);
			intent.putExtra(FileActivity.FileDir, mLastSelectPath);
			startActivityForResult(intent, REQUEST_PLAYER_IMPORT);
			mMenu.collapse();
			break;
		case R.id.player_setting:
			Intent settingIntent = new Intent(PlayerManageActivity.this, PlayerSettingActivity.class);
			settingIntent.putExtra(Player.Col_Uuid, "");
			startActivity(settingIntent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		Player player = mPlayerAdapter.getPlayer(position);
		Intent intent = new Intent(PlayerManageActivity.this, EditPlayerActivity.class);
		intent.putExtra(SELECT_PLAYER_UUID, player.getUuid());
		startActivityForResult(intent, REQUEST_EDITPLAYER);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		showDeleteDialog(position);
		return true;
	}
	
	private void showSearchDialog() {
		final CommonDialog mDialog = new CommonDialog(this, R.style.MyDialogStyle, 0);
		final EditText editText = new EditText(this);
		mDialog.addView(editText);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getResources().getString(R.string.search));
		mDialog.ok.setText(getResources().getString(R.string.ok));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				int index = mPlayerAdapter.searchName(editText.getText().toString().trim());
				if (index != -1) {
					mPlayerList.smoothScrollToPosition(index);
				} else {
					ToastTool.showToast(mContext, R.string.name_unfound);
				}
				mDialog.dismiss();
			}
		});
		editText.setTextColor(getResources().getColor(R.color.light_blacktext));
		editText.setTextSize(15.0f);
		editText.setHint(R.string.please_input_name_nickname);
		editText.setBackgroundColor(Color.TRANSPARENT);
		mDialog.show();
	}
	
	private void showDeleteDialog(final int position) {
		final Player player = mPlayerAdapter.getPlayer(position);
		final CommonDialog mDialog = new CommonDialog(this, R.style.MyDialogStyle);
		TextView textView = new TextView(this);
		mDialog.addView(textView);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getResources().getString(R.string.tip));
		mDialog.ok.setText(getResources().getString(R.string.ok));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				try {
					new Delete().from(Player.class)
						.where(Player.Col_Uuid + "=?", player.getUuid())
						.execute();
					new Delete().from(AudioItem.class)
						.where(AudioItem.Col_PlayerId + "=?", player.getUuid())
						.execute();
					mPlayerAdapter.removePlayer(position);
					mDialog.dismiss();
				} catch (Exception e) {
					ToastTool.showToast(mContext, R.string.delete_fail);
				}				
				
			}
		});
		mDialog.cancel.setText(getResources().getString(R.string.cancel));
		mDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		textView.setText(getResources().getString(R.string.ensure_delete_player, player.getNickName()));
		textView.setTextColor(getResources().getColor(R.color.light_blacktext));
		textView.setTextSize(18.0f);
		textView.setBackgroundColor(Color.TRANSPARENT);
		textView.setSingleLine(false);
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
		lp.leftMargin = 80;
		textView.setLayoutParams(lp);
		mDialog.show();
	}
	
	private void showExportDiaolg() {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		mDialog.addView(R.layout.item_text);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getString(R.string.tip));
		TextView textView = (TextView) mDialog.getContentView().findViewById(R.id.item_text);
		textView.setText(getString(R.string.player_export_information));
		textView.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		textView.setTextSize(ValueTool.sp2px(mContext, 6));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {	
				String fileName = "player";
				String filePath = Environment.getExternalStorageDirectory() + "/Mahjong";
				if (ExcelUtils.createExcelFromPlayer(filePath, fileName, 
						Player.getAllPlayer(), AudioItem.getAllAudioItems())) {
			        ToastTool.showToast(mContext, mContext.getString(R.string.export_success, filePath + "/" + fileName + ".xls"));				
				} else {
					ToastTool.showToast(mContext, mContext.getString(R.string.export_fail));	
				}
				mDialog.dismiss();
			}
		});
		mDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {	
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
}

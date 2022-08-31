package com.mahjong.activity;

import com.mahjong.R;
import com.mahjong.dialog.CharacterSelectDialog;
import com.mahjong.dialog.CharacterSelectDialog.OnCharacterSelectListener;
import com.mahjong.model.Character;
import com.mahjong.model.Player;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.ImageTool;
import com.mahjong.ui.CommonDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditPlayerActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	
	private ImageView mBack;
	private Button mOK;
	private ImageView mSetting;
	
	private EditText mId;
	private EditText mName;
	private EditText mNickName;
	private TextView mSex;
	private EditText mSign;
	private ImageView mIcon;
	
	private Player player;
	private Character mCharacter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editplayer);
		mContext = this;
		Intent intent = getIntent();
		String uuid = intent.getStringExtra(PlayerManageActivity.SELECT_PLAYER_UUID);
		player = Player.getPlayer(uuid);
		mCharacter = Character.getCharacter(mContext, player.getCharacterId());
		initUI();
	}
	
	private void initUI() {
		mBack = (ImageView) findViewById(R.id.editplayer_back);
		mOK = (Button) findViewById(R.id.editplayer_ok);
		mSetting = (ImageView) findViewById(R.id.editplayer_setting);
		mId = (EditText) findViewById(R.id.editplayer_edit_id);
		mName = (EditText) findViewById(R.id.editplayer_edit_name);
		mNickName = (EditText) findViewById(R.id.editplayer_edit_nickname);
		mSex = (TextView) findViewById(R.id.editplayer_tv_sex);
		mSign = (EditText) findViewById(R.id.editplayer_edit_sign);
		mIcon = (ImageView) findViewById(R.id.editplayer_icon);
		
		if (player != null) {
			mId.setText(player.getUuid());
			mName.setText(player.getName());
			mNickName.setText(player.getNickName());
			mSex.setText(player.getSex() == 'M' ? getString(R.string.male) : getString(R.string.female));
			mSign.setText(player.getSign());
//			int imgId = HeadIconTool.String2Id(player.getIcon());
//			if (imgId != -1) {
//				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgId);
//				bitmap = ImageTool.createCircleImage(bitmap, bitmap.getWidth());
//				mIcon.setImageBitmap(bitmap);		
//			}
			Bitmap bitmap = EmoticonTool.getEmoticon(mCharacter);
			bitmap = ImageTool.createCircleImage(bitmap, bitmap.getWidth());
			mIcon.setImageBitmap(bitmap);
		}
		
		mBack.setOnClickListener(this);
		mSetting.setOnClickListener(this);
		mOK.setOnClickListener(this);
		mSex.setOnClickListener(this);
		mIcon.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == RESULT_OK) {
//			switch (requestCode) {
//			case HeadIconSelectActivity.REQUEST_ICON:
//				int folder = data.getIntExtra(HeadIconSelectActivity.ID_FOLDER, -1);
//				int icon = data.getIntExtra(HeadIconSelectActivity.ID_ICON, -1);
//				if (icon != -1) {
//					Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icon);
//					bitmap = ImageTool.createCircleImage(bitmap, bitmap.getWidth());
//					mIcon.setImageBitmap(bitmap);	
//					player.setIcon(HeadIconTool.Id2String(folder, icon));
//				}
//				break;
//			default:
//				break;
//			}
//		}
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editplayer_back:
			this.finish();
			break;
		case R.id.editplayer_setting:
			Intent settingIntent = new Intent(EditPlayerActivity.this, PlayerSettingActivity.class);
			settingIntent.putExtra(Player.Col_Uuid, player.getUuid());
			startActivity(settingIntent);
			break;
		case R.id.editplayer_ok:
			checkEditPlayer();
			break;
		case R.id.editplayer_tv_sex:
			showSexDialog();
			break;		
		case R.id.editplayer_icon:
//			startActivityForResult(new Intent(EditPlayerActivity.this, HeadIconSelectActivity.class),
//					HeadIconSelectActivity.REQUEST_ICON);
			showCharacterSelectDialog();
			break;
		default:
			break;
		}
	}
	
	private void checkEditPlayer() {
		String name = mName.getText().toString().trim();
		if (name.equals("")) {
			showErrorDialog(getString(R.string.please_input_name));
			return;
		}
		player.setName(name);
		String nickname = mNickName.getText().toString().trim();
		if (nickname.equals("")) {
			showErrorDialog(getString(R.string.please_input_nickname));
			return;
		}
		player.setNickName(nickname);
		String sign = mSign.getText().toString().trim();
		if (sign.equals("")) {
			showErrorDialog(getString(R.string.please_input_sign));
			return;
		}
		player.setSign(sign);
		String sexString = mSex.getText().toString();
		char sex = sexString.equals(getString(R.string.male)) ? 'M' : 'F';
		player.setSex(sex);
		player.setCharacter(mCharacter);
		player.save();		
		setResult(RESULT_OK);
		this.finish();
	}
	
	private void showSexDialog() {
		final CommonDialog mDialog = new CommonDialog(this, R.style.MyDialogStyle, true);
		mDialog.addView(R.layout.item_sex);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getString(R.string.please_choose_sex));
		ImageView manImage = (ImageView) mDialog.getContentView().findViewById(R.id.sex_man);
		ImageView womanImage = (ImageView) mDialog.getContentView().findViewById(R.id.sex_woman);
		manImage.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mSex.setText(getString(R.string.male));
				mDialog.dismiss();
			}
		});
		womanImage.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mSex.setText(getString(R.string.female));
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
	private void showErrorDialog(String error) {
		final CommonDialog mDialog = new CommonDialog(this, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.item_text);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getString(R.string.tip));
		mDialog.ok.setText(getResources().getString(R.string.back));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		RelativeLayout relativeLayout = (RelativeLayout) mDialog.getContentView();
		TextView textView = (TextView) relativeLayout.findViewById(R.id.item_text);
		textView.setText(error);
		mDialog.show();
	}
	
	private void showCharacterSelectDialog() {
		CharacterSelectDialog mDialog = new CharacterSelectDialog(mContext);
		mDialog.setOnCharacterSelectListener(new OnCharacterSelectListener() {
			
			@Override
			public void onSelect(Character character) {
				mCharacter = character;
				Bitmap bitmap = EmoticonTool.getEmoticon(mCharacter);
				bitmap = ImageTool.createCircleImage(bitmap, bitmap.getWidth());
				mIcon.setImageBitmap(bitmap);				
			}
		});
		mDialog.show();
	}
	
}

package com.mahjong.activity;

import java.util.List;
import java.util.regex.Pattern;

import com.activeandroid.query.Select;
import com.mahjong.R;
import com.mahjong.dialog.CharacterSelectDialog;
import com.mahjong.dialog.CharacterSelectDialog.OnCharacterSelectListener;
import com.mahjong.model.Character;
import com.mahjong.model.Player;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.ImageTool;
import com.mahjong.ui.CommonDialog;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CreatePlayerActivity extends Activity implements OnClickListener {

	private Context mContext;
	
	private ImageView mBack;
	private Button mOK;
	
	private EditText mId;
	private EditText mName;
	private EditText mNickName;
	private TextView mSex;
	private EditText mSign;
	private ImageView mIcon;
	
//	private String iconString = "";
	private Character mCharacter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createplayer);
		mContext = this;
		mCharacter = Character.getCharacter(mContext, -1);
		initUI();
	}
	
	private void initUI() {
		mBack = (ImageView) findViewById(R.id.createplayer_back);
		mOK = (Button) findViewById(R.id.createplayer_ok);
		mId = (EditText) findViewById(R.id.createplayer_edit_id);
		mName = (EditText) findViewById(R.id.createplayer_edit_name);
		mNickName = (EditText) findViewById(R.id.createplayer_edit_nickname);
		mSex = (TextView) findViewById(R.id.createplayer_tv_sex);
		mSign = (EditText) findViewById(R.id.createplayer_edit_sign);
		mIcon = (ImageView) findViewById(R.id.createplayer_icon);
		
		mBack.setOnClickListener(this);
		mOK.setOnClickListener(this);
		mSex.setOnClickListener(this);
		mIcon.setOnClickListener(this);
		
		InputFilter filter = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
                    Spanned dest, int dstart, int dend) {
				String regex = "^[\u4E00-\u9FA5]+$";
	            boolean isChinese = Pattern.matches(regex, source.toString());
	            if (isChinese) {
	                return "";
	            }
	            for (int i = start; i < end; i++) {
	                if (!java.lang.Character.isLetterOrDigit(source.charAt(i))) {
	                    return "";
	                }
	            }
				return null;
			}
			
		};
		mId.setFilters(new InputFilter[] {filter, new InputFilter.LengthFilter(10)});
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
//					iconString = HeadIconTool.Id2String(folder, icon);
//				}
//				break;
//			default:
//				break;
//			}
//		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.createplayer_back:
			this.finish();
			break;
		case R.id.createplayer_ok:
			checkCreatePlayer();
			break;
		case R.id.createplayer_tv_sex:
			showSexDialog();
			break;		
		case R.id.createplayer_icon:
//			startActivityForResult(new Intent(CreatePlayerActivity.this, HeadIconSelectActivity.class),
//					HeadIconSelectActivity.REQUEST_ICON);
			showCharacterSelectDialog();
			break;
		default:
			break;
		}
	}
	
	private void checkCreatePlayer() {
		String uuid = mId.getText().toString().trim();
		if (uuid.equals("")) {
			showErrorDialog(getString(R.string.please_input_id));
			return;
		} else {
			List<Player> players = new Select()
					.from(Player.class).where("Uuid=?", uuid).execute();
			if (players != null && players.size() > 0) {
				showErrorDialog(getString(R.string.input_id_exist));
				return;
			}
		}
		String name = mName.getText().toString().trim();
		if (name.equals("")) {
			showErrorDialog(getString(R.string.please_input_name));
			return;
		}
		String nickname = mNickName.getText().toString().trim();
		if (nickname.equals("")) {
			showErrorDialog(getString(R.string.please_input_nickname));
			return;
		}
		String sign = mSign.getText().toString().trim();
		if (sign.equals("")) {
			showErrorDialog(getString(R.string.please_input_sign));
			return;
		}
		
		String sexString = mSex.getText().toString();
		char sex = sexString.equals(getString(R.string.male)) ? 'M' : 'F';
		Player player = new Player(uuid, name, nickname, sex, sign, 
				mCharacter.getDefaultIcon(), mCharacter.getUuid(), -1);
		player.save();
		
		setResult(RESULT_OK);
		this.finish();
	}
	
	private void showSexDialog() {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, true);
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
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
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

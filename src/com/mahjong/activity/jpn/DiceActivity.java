package com.mahjong.activity.jpn;

import java.util.Random;

import com.mahjong.R;
import com.mahjong.activity.BaseActivity;
import com.mahjong.tools.LightTool;
import com.mahjong.tools.ManageTool;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.ui.MjDiceResultView;
import com.mahjong.ui.PlayerFuncItem;
import com.mahjong.ui.RippleLayout;
import com.mahjong.ui.RippleLayout.OnRippleCompleteListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

public class DiceActivity extends BaseActivity 
		implements OnClickListener, OnRippleCompleteListener, OnCheckedChangeListener {

	private static final String ENABLE_DICE_SOUND = "ENABLE_DICE_SOUND";
	
	private int mDealerPos;
	
	private RippleLayout[] mRollView;
	private RippleLayout[] mOkView;
	private ImageView mDice1View;
	private ImageView mDice2View;
	private ImageView[] mLoactionViews;
	private MjDiceResultView mResultView;
	private CheckBox mSoundBox;
	
	private Handler mDiceHandler = new Handler();
	private boolean mDiceState = false;
	private int mDiceCounter = 0;
	private int[] mDiceNums = {0, 0};
	private Bitmap[] mDiceBitmaps = new Bitmap[6];
	private Random random = new Random(System.currentTimeMillis());
	
	private SoundPool soundPool;
	private int soundId;
	
	private LightTool mLightTool; // 控制屏幕亮度
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_dice);
		mLightTool = new LightTool(this);
		initUI();
		initSoundEffect();
		boolean enableSound = ShareprefenceTool.getInstance().getBoolean(ENABLE_DICE_SOUND, this, true);
		mSoundBox.setChecked(enableSound);
		// 设定方向
		Intent intent = getIntent();
		mDealerPos = intent.getIntExtra(ManageTool.PLAYER_ITEM_POSITION, PlayerFuncItem.POS_BOTTOM);	
		switch (mDealerPos) {
		case PlayerFuncItem.POS_BOTTOM:
			mResultView.setAngle(0);
			mLoactionViews[0].setVisibility(View.VISIBLE);
			break;
		case PlayerFuncItem.POS_RIGHT:
			mResultView.setAngle(-90);
			mDice1View.setRotation(-90);
			mDice2View.setRotation(-90);
			mLoactionViews[1].setVisibility(View.VISIBLE);
			break;
		case PlayerFuncItem.POS_TOP:
			mResultView.setAngle(180);	
			mDice1View.setRotation(180);
			mDice2View.setRotation(180);
			mLoactionViews[2].setVisibility(View.VISIBLE);
			break;
		case PlayerFuncItem.POS_LEFT:
			mResultView.setAngle(90);	
			mDice1View.setRotation(90);
			mDice2View.setRotation(90);	
			mLoactionViews[3].setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		soundPool.unload(soundId);
		soundPool.release();
	}

	private void initUI() {
		mRollView = new RippleLayout[2];
		mRollView[0] = (RippleLayout) findViewById(R.id.dice_rv_roll1);
		mRollView[1] = (RippleLayout) findViewById(R.id.dice_rv_roll2);
//		mRollView[0].setOnRippleCompleteListener(this);
//		mRollView[1].setOnRippleCompleteListener(this);
		mRollView[0].setOnClickListener(this);
		mRollView[1].setOnClickListener(this);
		mOkView = new RippleLayout[2];
		mOkView[0] = (RippleLayout) findViewById(R.id.dice_rv_ok1);
		mOkView[1] = (RippleLayout) findViewById(R.id.dice_rv_ok2);
//		mOkView[0].setOnRippleCompleteListener(this);
//		mOkView[1].setOnRippleCompleteListener(this);
		mOkView[0].setOnClickListener(this);
		mOkView[1].setOnClickListener(this);
		mDice1View = (ImageView) findViewById(R.id.dice_img1);
		mDice2View = (ImageView) findViewById(R.id.dice_img2);
		mResultView = (MjDiceResultView) findViewById(R.id.dice_result);
		mSoundBox = (CheckBox) findViewById(R.id.dice_sound);
		mSoundBox.setOnCheckedChangeListener(this);
		mLoactionViews = new ImageView[4];
		mLoactionViews[0] = (ImageView) findViewById(R.id.dice_location_bottom);
		mLoactionViews[1] = (ImageView) findViewById(R.id.dice_location_right);
		mLoactionViews[2] = (ImageView) findViewById(R.id.dice_location_top);
		mLoactionViews[3] = (ImageView) findViewById(R.id.dice_location_left);
		
		mDiceBitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.game_dice1);
		mDiceBitmaps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.game_dice2);
		mDiceBitmaps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.game_dice3);
		mDiceBitmaps[3] = BitmapFactory.decodeResource(getResources(), R.drawable.game_dice4);
		mDiceBitmaps[4] = BitmapFactory.decodeResource(getResources(), R.drawable.game_dice5);
		mDiceBitmaps[5] = BitmapFactory.decodeResource(getResources(), R.drawable.game_dice6);
	}

    private void initSoundEffect() {
        // 5.0 及 之后
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = null;
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(4)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else { // 5.0 以前
            soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);  // 创建SoundPool
        }
        soundId = soundPool.load(this, R.raw.roll_dice, 1);//加载资源，得到soundId
    }

	@Override
	public void onCheckedChanged(CompoundButton btn, boolean checked) {
		ShareprefenceTool.getInstance().setBoolean(ENABLE_DICE_SOUND, checked, this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dice_rv_roll1:
		case R.id.dice_rv_roll2:
			if (!mDiceState) {
				mDiceHandler.removeCallbacks(mActionRunnable);
				mDiceHandler.post(mDiceRunnable);
				mDiceState = true;
				mDiceCounter = 0;
				if (mSoundBox.isChecked()) {
					soundPool.play(soundId,1,1,1,0,1);
				}
			}
			break;
		case R.id.dice_rv_ok1:
		case R.id.dice_rv_ok2:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onComplete(RippleLayout rippleView) {
		switch (rippleView.getId()) {
		case R.id.dice_rv_roll1:
		case R.id.dice_rv_roll2:
			if (!mDiceState) {
				mDiceHandler.removeCallbacks(mActionRunnable);
				mDiceHandler.post(mDiceRunnable);
				mDiceState = true;
				mDiceCounter = 0;
				if (mSoundBox.isChecked()) {
					soundPool.play(soundId,1,1,1,0,1);
				}				
			}			
			break;
		case R.id.dice_rv_ok1:
		case R.id.dice_rv_ok2:
			finish();
			break;
		default:
			break;
		}
	}
	
	private Runnable mDiceRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (mDiceCounter >= 24) {
				mDiceState = false;
				mDiceCounter = 0;
				mResultView.setPoint(mDiceNums[0] + mDiceNums[1] + 2);
				mDiceHandler.postDelayed(mActionRunnable, 400);
			} else {
				int tmp = random.nextInt(6);
				if (tmp == mDiceNums[0]) {
					mDiceNums[0] = (tmp + 1) % 6;
				} else {
					mDiceNums[0] = tmp;
				}
				tmp = random.nextInt(6);
				if (tmp == mDiceNums[1]) {
					mDiceNums[1] = (tmp + 1) % 6;
				} else {
					mDiceNums[1] = tmp;
				}
				mDice1View.setImageBitmap(mDiceBitmaps[mDiceNums[0]]);
				mDice2View.setImageBitmap(mDiceBitmaps[mDiceNums[1]]);
				mDiceCounter++;
				mDiceHandler.postDelayed(mDiceRunnable, 50);
			}
		}
	};
	
	private Runnable mActionRunnable = new Runnable() {
		
		@Override
		public void run() {
			mResultView.showAction();
		}
	};
	
}

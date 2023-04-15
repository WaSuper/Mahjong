package com.mahjong.activity;

import java.util.Random;

import com.mahjong.R;
import com.mahjong.activity.jpn.MainSelectActivity;
import com.mahjong.tools.GetSize;
import com.mahjong.tools.ImageTool;
import com.mahjong.ui.CommonDialog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class StartActivity extends BaseActivity 
		implements OnRequestPermissionsResultCallback {

	private ImageView mBgView;
	private ImageView mTextWu;
	private ImageView mTextXian;
	private ImageView mTextLi;
	private ImageView mTextZhi;
	private ImageView mLizhiBang1;
	private ImageView mLizhiBang2;
	private ImageView mLizhiBang3;
	private ImageView mLizhiBang4;
	private ImageView mLizhiBang5;
	
	private int[] lizhiIDs = {
			R.drawable.liqi_21catball, R.drawable.liqi_21chunjie, R.drawable.liqi_21cyber,
			R.drawable.liqi_21magic, R.drawable.liqi_21zhounian, R.drawable.liqi_22chunjie,
			R.drawable.liqi_22rpg, R.drawable.liqi_22summer_0, R.drawable.liqi_22winter_0,
			R.drawable.liqi_23chunjie_0, R.drawable.liqi_2211saki_0, R.drawable.liqi_akagi,
			R.drawable.liqi_baozhu_0, R.drawable.liqi_bone_0, R.drawable.liqi_chocolate_0,
			R.drawable.liqi_duane_0, R.drawable.liqi_evil_0, R.drawable.liqi_fish_0,
			R.drawable.liqi_hl, R.drawable.liqi_huiye, R.drawable.liqi_jade_0,
			R.drawable.liqi_jiaozi_0, R.drawable.liqi_jinlongyu, R.drawable.liqi_kuangdu_0,
			R.drawable.liqi_mofabang, R.drawable.liqi_music_0, R.drawable.liqi_red_0,
			R.drawable.liqi_rich_0, R.drawable.liqi_saki, R.drawable.liqi_scallion_0,
			R.drawable.liqi_xhzy, R.drawable.liqi_xiaoemofanbian, R.drawable.liqi_xuegao_0,
			R.drawable.liqi_xzw22121, R.drawable.liqi_xzw22122, R.drawable.liqi_xzw22123,
			R.drawable.liqi_xzw22124, R.drawable.liqi_yijitiantong};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		initUI();
		if (Build.VERSION.SDK_INT >= 23) { // above Android 6.0
			requestPower();
		} else {
			goToMain();
		}
	}
	
	private void initUI() {
		mBgView = (ImageView) findViewById(R.id.start_bg);
		mTextWu = (ImageView) findViewById(R.id.start_text_wu);
		mTextXian = (ImageView) findViewById(R.id.start_text_xian);
		mTextLi = (ImageView) findViewById(R.id.start_text_li);
		mTextZhi = (ImageView) findViewById(R.id.start_text_zhi);
		mLizhiBang1 = (ImageView) findViewById(R.id.start_lzb_1);
		mLizhiBang2 = (ImageView) findViewById(R.id.start_lzb_2);
		mLizhiBang3 = (ImageView) findViewById(R.id.start_lzb_3);
		mLizhiBang4 = (ImageView) findViewById(R.id.start_lzb_4);
		mLizhiBang5 = (ImageView) findViewById(R.id.start_lzb_5);
		
		Bitmap bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_ubw);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		bgBitmap = ImageTool.resizeBitmapEnd(bgBitmap, metrics.widthPixels, metrics.heightPixels);
		mBgView.setImageBitmap(bgBitmap);
		
		Bitmap textBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text_unlimited_lizhi);
		Bitmap[] splitBitmaps = ImageTool.split(textBitmap, 4, 1);
		mTextWu.setImageBitmap(splitBitmaps[0]);
		mTextXian.setImageBitmap(splitBitmaps[1]);
		mTextLi.setImageBitmap(splitBitmaps[2]);
		mTextZhi.setImageBitmap(splitBitmaps[3]);
		
		Random random = new Random(System.currentTimeMillis());
		mLizhiBang1.setImageResource(lizhiIDs[random.nextInt(lizhiIDs.length)]);
		mLizhiBang2.setImageResource(lizhiIDs[random.nextInt(lizhiIDs.length)]);
		mLizhiBang3.setImageResource(lizhiIDs[random.nextInt(lizhiIDs.length)]);
		mLizhiBang4.setImageResource(lizhiIDs[random.nextInt(lizhiIDs.length)]);
		mLizhiBang5.setImageResource(lizhiIDs[random.nextInt(lizhiIDs.length)]);
		
	}

	private void moveLizhiBangs(int winW, int winH) {
		int[] pos1 = new int[2];
		int[] pos2 = new int[2];
		int[] pos3 = new int[2];
		int[] pos4 = new int[2];
		int[] pos5 = new int[2];
		mLizhiBang1.getLocationInWindow(pos1);
		mLizhiBang2.getLocationInWindow(pos2);
		mLizhiBang3.getLocationInWindow(pos3);
		mLizhiBang4.getLocationInWindow(pos4);
		mLizhiBang5.getLocationInWindow(pos5);
		
		int dp5 = GetSize.dip2px(this, 5);
		final int tmpW1 = dp5 * 6;
		final int tmpH1 = winH - dp5 - mLizhiBang1.getHeight() - pos1[1];
		final double angle1 = 180 * Math.atan2(tmpW1, tmpH1) / Math.PI;
		final int tmpW2 = pos4[0] + pos1[0] - dp5 * 15;
		final double angle2 = 180 * Math.atan2(tmpW2, tmpH1) / Math.PI;
		final int tmpH2 = winH - dp5 - mLizhiBang2.getHeight() - pos2[1];
		final int tmpH3 = winH - dp5 - mLizhiBang3.getHeight() - pos3[1];
		
		int rotateTime = 500;
		
		Animation rotateAnim1 = new RotateAnimation(0, (float) -angle1, 
				mLizhiBang1.getWidth() / 2, mLizhiBang1.getHeight() / 2);
		rotateAnim1.setFillAfter(true);
		rotateAnim1.setDuration(rotateTime);
		mLizhiBang1.startAnimation(rotateAnim1);
		
		Animation rotateAnim2 = new RotateAnimation(0, (float) -angle2, 
				mLizhiBang2.getWidth() / 2, mLizhiBang2.getHeight() / 2);
		rotateAnim2.setFillAfter(true);
		rotateAnim2.setDuration(rotateTime);
		mLizhiBang2.startAnimation(rotateAnim2);
		
		Animation rotateAnim5 = new RotateAnimation(0, (float) angle1, 
				mLizhiBang5.getWidth() / 2, mLizhiBang5.getHeight() / 2);
		rotateAnim5.setFillAfter(true);
		rotateAnim5.setDuration(rotateTime);
		mLizhiBang5.startAnimation(rotateAnim5);
		
		Animation rotateAnim4 = new RotateAnimation(0, (float) angle2, 
				mLizhiBang4.getWidth() / 2, mLizhiBang4.getHeight() / 2);
		rotateAnim4.setFillAfter(true);
		rotateAnim4.setDuration(rotateTime);
		mLizhiBang4.startAnimation(rotateAnim4);
		
		rotateAnim1.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation anim) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation anim) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation anim) {
				int moveTime = 500;
				
				Animation moveAnim3 = new TranslateAnimation(0f, 0f, 0f, tmpH3);
				moveAnim3.setFillAfter(true);
				moveAnim3.setDuration(moveTime);
				moveAnim3.setInterpolator(new AccelerateInterpolator());
				mLizhiBang3.startAnimation(moveAnim3);
				moveAnim3.setAnimationListener(mEndListener);
				
				mLizhiBang1.setRotation((float) -angle1);
				Animation moveAnim1 = new TranslateAnimation(0f, tmpW1, 0f, tmpH1);
				moveAnim1.setFillAfter(true);
				moveAnim1.setDuration(moveTime);
				moveAnim1.setInterpolator(new AccelerateInterpolator());
				mLizhiBang1.startAnimation(moveAnim1);
				
				mLizhiBang2.setRotation((float) -angle2);
				Animation moveAnim2 = new TranslateAnimation(0f, tmpW2, 0f, tmpH2);
				moveAnim2.setFillAfter(true);
				moveAnim2.setDuration(moveTime);
				moveAnim2.setInterpolator(new AccelerateInterpolator());
				mLizhiBang2.startAnimation(moveAnim2);

				mLizhiBang5.setRotation((float) angle1);
				Animation moveAnim5 = new TranslateAnimation(0f, -tmpW1, 0f, tmpH1);
				moveAnim5.setFillAfter(true);
				moveAnim5.setDuration(moveTime);
				moveAnim5.setInterpolator(new AccelerateInterpolator());
				mLizhiBang5.startAnimation(moveAnim5);
				
				mLizhiBang4.setRotation((float) angle2);
				Animation moveAnim4 = new TranslateAnimation(0f, -tmpW2, 0f, tmpH2);
				moveAnim4.setFillAfter(true);
				moveAnim4.setDuration(moveTime);
				moveAnim4.setInterpolator(new AccelerateInterpolator());
				mLizhiBang4.startAnimation(moveAnim4);
			}
		});
				
	}
	
	private AnimationListener mEndListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation anim) {
			
		}
		
		@Override
		public void onAnimationRepeat(Animation anim) {
			
		}
		
		@Override
		public void onAnimationEnd(Animation anim) {
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					startActivity(new Intent(StartActivity.this,
							MainSelectActivity.class));
					StartActivity.this.finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			}, 300);
			
		}
	};
	
	private void goToMain() {
		mLizhiBang1.post(new Runnable() {
			
			@Override
			public void run() {
				DisplayMetrics metrics = getResources().getDisplayMetrics();
				moveLizhiBangs(metrics.widthPixels, metrics.heightPixels);
			}
		});
	}

	private void requestPower() {
		// checkSelfPermission 判断是否已经申请了此权限
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			// 如果应用之前请求过此权限但用户拒绝了请求，shouldShowRequestPermissionRationale将返回true。
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				showFailDialog();
			} else {
				showRequestDialog();
			}
		} else {
			goToMain();
		}
	}

	private void showRequestDialog() {
		final CommonDialog dialog = new CommonDialog(this, R.style.MyDialogStyle, 0);		
		dialog.addView(R.layout.item_text);
		dialog.titleTextView.setText(R.string.tip);
		TextView content = (TextView) dialog.getContentView().findViewById(R.id.item_text);
		content.setText(R.string.storage_explain);
		dialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActivityCompat.requestPermissions(StartActivity.this,
						new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, }, 1);
				dialog.dismiss();
			}
		});
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private void showFailDialog() {
		final CommonDialog dialog = new CommonDialog(this, R.style.MyDialogStyle, 0);		
		dialog.addView(R.layout.item_text);
		dialog.titleTextView.setText(R.string.tip);
		TextView content = (TextView) dialog.getContentView().findViewById(R.id.item_text);
		content.setText(R.string.request_permission_fail);
		dialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();
			}
		});
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == 1) {
		      for (int i = 0; i < permissions.length; i++) {
		          if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
		              goToMain();
		          } else {
		              showFailDialog();
		          }
		      }
		   }
	}
	
	

}

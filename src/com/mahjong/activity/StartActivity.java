package com.mahjong.activity;

import com.mahjong.R;
import com.mahjong.activity.jpn.MainSelectActivity;
import com.mahjong.ui.CommonDialog;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class StartActivity extends Activity implements OnRequestPermissionsResultCallback {

	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		if (Build.VERSION.SDK_INT >= 23) { // above Android 6.0
			requestPower();
		} else {
			goToMain(1000);
		}
	}

	private void goToMain(long time) {
		handler.postDelayed(new Runnable() {

			public void run() {
				startActivity(new Intent(StartActivity.this,
						MainSelectActivity.class));
				StartActivity.this.finish();
			}
		}, time);
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
				ActivityCompat.requestPermissions(this,
						new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, }, 1);
			}
		} else {
			goToMain(1000);
		}
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
		              goToMain(0);
		          } else {
		              showFailDialog();
		          }
		      }
		   }
	}
	
	

}

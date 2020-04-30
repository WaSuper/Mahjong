package com.mahjong.tools;

import com.mahjong.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class PopWinDownUtil {

	private Context context;
	private View contentView;
	private View relayView;
	private PopupWindow popupWindow;

	public PopWinDownUtil(Context context, View contentView, View relayView) {
		this.context = context;
		this.contentView = contentView;
		this.relayView = relayView;
		init();
	}

	public void init() {
		// 内容，高度，宽度
		popupWindow = new PopupWindow(contentView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		// 动画效果
		popupWindow.setAnimationStyle(R.style.AnimationTopFade);
		// 菜单背景色
		ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.setOutsideTouchable(true);
		// 关闭事件
		popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		popupWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				if (onDismissListener != null) {
					onDismissListener.onDismiss();
				}
			}
		});
	}

	public void showAsPopUp() {
		popupWindow.setAnimationStyle(R.style.AnimationBottomFade);  //设置动画
		// 获得位置 这里的v是目标控件，就是你要放在这个v的上面还是下面
		int[] location = new int[2];
		relayView.getLocationOnScreen(location);
		//这里定义在上方
		popupWindow.getContentView().measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int height = popupWindow.getContentView().getMeasuredHeight();
		if (height > 1000) height = 1000;
		popupWindow.showAtLocation(relayView, Gravity.NO_GRAVITY, 
				location[0], location[1] - height); 
	}
	
	public void showAsDropDown() {
		// 显示位置
		popupWindow.showAsDropDown(relayView);
	}

	public void hide() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	private OnDismissListener onDismissListener;

	public void setOnDismissListener(OnDismissListener onDismissListener) {
		this.onDismissListener = onDismissListener;
	}

	public interface OnDismissListener {
		void onDismiss();
	}

	public boolean isShowing() {
		return popupWindow.isShowing();
	}

}

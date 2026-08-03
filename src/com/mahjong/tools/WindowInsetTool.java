package com.mahjong.tools;

import com.mahjong.R;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

public class WindowInsetTool {

	private WindowInsetTool() {
	}

	public static void applyTopSafeArea(Activity activity) {
		if (activity == null) {
			return;
		}
		ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
		if (contentView == null || contentView.getChildCount() == 0) {
			return;
		}
		final View rootView = contentView.getChildAt(0);
		if (rootView == null) {
			return;
		}
		rootView.post(new Runnable() {
			@Override
			public void run() {
				applyTopSafeArea(rootView);
			}
		});
	}

	private static void applyTopSafeArea(View rootView) {
		Object tag = rootView.getTag(R.id.window_safe_padding);
		int[] originPadding;
		if (tag instanceof int[]) {
			originPadding = (int[]) tag;
		} else {
			originPadding = new int[] {
					rootView.getPaddingLeft(),
					rootView.getPaddingTop(),
					rootView.getPaddingRight(),
					rootView.getPaddingBottom()
			};
			rootView.setTag(R.id.window_safe_padding, originPadding);
		}

		Rect visibleFrame = new Rect();
		rootView.getWindowVisibleDisplayFrame(visibleFrame);

		int[] location = new int[2];
		rootView.getLocationOnScreen(location);

		int topInset = Math.max(0, visibleFrame.top - location[1]);
		int paddingTop = originPadding[1] + topInset;
		if (rootView.getPaddingTop() == paddingTop
				&& rootView.getPaddingLeft() == originPadding[0]
				&& rootView.getPaddingRight() == originPadding[2]
				&& rootView.getPaddingBottom() == originPadding[3]) {
			return;
		}

		// Keep the page content below the system status area on newer Android versions.
		rootView.setPadding(originPadding[0], paddingTop, originPadding[2], originPadding[3]);
	}
}

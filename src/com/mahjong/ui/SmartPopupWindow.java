package com.mahjong.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.PopupWindowCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * 可以在任意位置显示的PopupWindow
 * 用法：
 SmartPopupWindow popupWindow= SmartPopupWindow.Builder
 .build(Activity.this, view)
 .setAlpha(0.4f)                   //背景灰度     默认全透明
 .setOutsideTouchDismiss(false)    //点击外部消失  默认true（消失）
 .createPopupWindow();
 popupWindow.showAtAnchorView(view, VerticalPosition.ABOVE, HorizontalPosition.CENTER);
 */
public class SmartPopupWindow extends PopupWindow {

    private int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    private float mAlpha = 1f; //背景灰度  0-1  1表示全透明
    private Context mContext;
    private View mContentView;
    private boolean isTouchOutsideDismiss = true;   //点击外部消失
    private int mAnimationStyle = -1;

    //下面的几个变量只是位置处理外部点击事件（6.0以上）
    //是否只是获取宽高
    //getViewTreeObserver监听时
    private boolean isOnlyGetWH = true;
    private View mAnchorView;
    @VerticalPosition
    private int mVerticalGravity = VerticalPosition.BELOW;
    @HorizontalPosition
    private int mHorizontalGravity = HorizontalPosition.LEFT;
    private int mOffsetX;
    private int mOffsetY;

    public SmartPopupWindow(Context context) {
        this(context, null);
    }

    public SmartPopupWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void init() {
        setContentView(mContentView);
        setHeight(mHeight);
        setWidth(mWidth);
        touchOutsideDismiss(isTouchOutsideDismiss);
        if (mAnimationStyle != -1) {
            setAnimationStyle(mAnimationStyle);
        }
    }

    private void touchOutsideDismiss(boolean touchOutsideDismiss) {
        if (!touchOutsideDismiss) {
            setFocusable(true);
            setOutsideTouchable(false);
            setBackgroundDrawable(null);

            getContentView().setFocusable(true);
            getContentView().setFocusableInTouchMode(true);
            getContentView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dismiss();
                        return true;
                    }
                    return false;
                }
            });
            //在Android 6.0以上 ，只能通过拦截事件来解决
            setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int x = (int) event.getX();
                    final int y = (int) event.getY();

                    if ((event.getAction() == MotionEvent.ACTION_DOWN)
                            && ((x < 0) || (x >= mWidth) || (y < 0) || (y >= mHeight))) {
                        //outside
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        //outside
                        return true;
                    }
                    return false;
                }
            });
        } else {
            setFocusable(true);
            setOutsideTouchable(true);
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        isOnlyGetWH = true;
        mAnchorView = parent;
        mOffsetX = x;
        mOffsetY = y;
        addGlobalLayoutListener(getContentView());
        super.showAtLocation(parent, gravity, x, y);
    }

    public void showAtAnchorView(@NonNull View anchorView, @VerticalPosition int verticalPos, @HorizontalPosition int horizontalPos) {
        showAtAnchorView(anchorView, verticalPos, horizontalPos, true);
    }

    public void showAtAnchorView(@NonNull View anchorView, @VerticalPosition int verticalPos, @HorizontalPosition int horizontalPos, boolean fitInScreen) {
        showAtAnchorView(anchorView, verticalPos, horizontalPos, 0, 0, fitInScreen);
    }
    public void showAtAnchorView(@NonNull View anchorView, @VerticalPosition int verticalPos, @HorizontalPosition int horizontalPos, int x, int y) {
        showAtAnchorView(anchorView, verticalPos, horizontalPos, x, y, true);
    }

    public void showAtAnchorView(@NonNull View anchorView, @VerticalPosition int verticalPos, @HorizontalPosition int horizontalPos, int x, int y, boolean fitInScreen) {
        isOnlyGetWH = false;
        mAnchorView = anchorView;
        mOffsetX = x;
        mOffsetY = y;
        mVerticalGravity = verticalPos;
        mHorizontalGravity = horizontalPos;
        showBackgroundAnimator();
        final View contentView = getContentView();
        addGlobalLayoutListener(contentView);
        setClippingEnabled(fitInScreen);
        contentView.measure(makeDropDownMeasureSpec(getWidth()), makeDropDownMeasureSpec(getHeight()));
        final int measuredW = contentView.getMeasuredWidth();
        final int measuredH = contentView.getMeasuredHeight();
        if (!fitInScreen) {
            final int[] anchorLocation = new int[2];
            anchorView.getLocationInWindow(anchorLocation);
            x += anchorLocation[0];
            y += anchorLocation[1] + anchorView.getHeight();
        }
        y = calculateY(anchorView, verticalPos, measuredH, y);
        x = calculateX(anchorView, horizontalPos, measuredW, x);
        if (fitInScreen) {
            PopupWindowCompat.showAsDropDown(this, anchorView, x, y, Gravity.NO_GRAVITY);
        } else {
            showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y);
        }
    }

    /**
     * 根据垂直gravity计算y偏移
     */
    private int calculateY(View anchor, int verticalGravity, int measuredH, int y) {
        switch (verticalGravity) {
            case VerticalPosition.ABOVE:
                y -= measuredH + anchor.getHeight();
                break;
            case VerticalPosition.ALIGN_BOTTOM:
                y -= measuredH;
                break;
            case VerticalPosition.CENTER:
                y -= anchor.getHeight() / 2 + measuredH / 2;
                break;
            case VerticalPosition.ALIGN_TOP:
                y -= anchor.getHeight();
                break;
            case VerticalPosition.BELOW:
                // Default position.
                break;
        }

        return y;
    }

    /**
     * 根据水平gravity计算x偏移
     */
    private int calculateX(View anchor, int horizontalGravity, int measuredW, int x) {
        switch (horizontalGravity) {
            case HorizontalPosition.LEFT:
                x -= measuredW;
                break;
            case HorizontalPosition.ALIGN_RIGHT:
                x -= measuredW - anchor.getWidth();
                break;
            case HorizontalPosition.CENTER:
                x += anchor.getWidth() / 2 - measuredW / 2;
                break;
            case HorizontalPosition.ALIGN_LEFT:
                // Default position.
                break;
            case HorizontalPosition.RIGHT:
                x += anchor.getWidth();
                break;
        }

        return x;
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), getDropDownMeasureSpecMode(measureSpec));
    }

    private static int getDropDownMeasureSpecMode(int measureSpec) {
        switch (measureSpec) {
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                return View.MeasureSpec.UNSPECIFIED;
            default:
                return View.MeasureSpec.EXACTLY;
        }
    }

    //监听器，用于PopupWindow弹出时获取准确的宽高
    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mWidth = getContentView().getWidth();
            mHeight = getContentView().getHeight();
            //只获取宽高时，不执行更新操作
            if (isOnlyGetWH) {
                removeGlobalLayoutListener();
                return;
            }
            updateLocation(mWidth, mHeight, mAnchorView, mVerticalGravity, mHorizontalGravity, mOffsetX, mOffsetY);
            removeGlobalLayoutListener();
        }
    };

    private void updateLocation(int width, int height, @NonNull View anchor,
                                @VerticalPosition final int verticalGravity,
                                @HorizontalPosition int horizontalGravity,
                                int x, int y) {
        x = calculateX(anchor, horizontalGravity, width, x);
        y = calculateY(anchor, verticalGravity, height, y);
        update(anchor, x, y, width, height);
    }

    private void removeGlobalLayoutListener() {
        if (getContentView() != null) {
            if (Build.VERSION.SDK_INT >= 16) {
                getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
            } else {
                getContentView().getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
            }
        }
    }

    private void addGlobalLayoutListener(View contentView) {
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dismissBackgroundAnimator();
        removeGlobalLayoutListener();
    }

    /**
     * 窗口显示，窗口背景透明度渐变动画
     */
    private void showBackgroundAnimator() {
        if (mAlpha >= 1f) return;
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, mAlpha);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (Float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(360);
        animator.start();
    }

    /**
     * 窗口隐藏，窗口背景透明度渐变动画
     */
    private void dismissBackgroundAnimator() {
        if (mAlpha >= 1f) return;
        ValueAnimator animator = ValueAnimator.ofFloat(mAlpha, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (Float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(360);
        animator.start();
    }

    /**
     * 控制窗口背景的不透明度
     */
    private void setWindowBackgroundAlpha(float alpha) {
        if (mContext == null) return;
        if (mContext instanceof Activity) {
            Window window = ((Activity) mContext).getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.alpha = alpha;
            window.setAttributes(layoutParams);
        }
    }

    public static class Builder {
        private SmartPopupWindow mWindow;

        private Builder(Activity activity, View view) {
            mWindow = new SmartPopupWindow(activity);
            mWindow.mContext = activity;
            mWindow.mContentView = view;
        }

        public static Builder build(Activity activity, View view) {
            return new Builder(activity, view);
        }

        public Builder setSize(int width, int height) {
            mWindow.mWidth = width;
            mWindow.mHeight = height;
            return this;
        }

        public Builder setAnimationStyle(int animationStyle) {
            mWindow.mAnimationStyle = animationStyle;
            return this;
        }

        public Builder setAlpha(float alpha) {
            mWindow.mAlpha = alpha;
            return this;
        }

        public Builder setOutsideTouchDismiss(boolean dismiss) {
            mWindow.isTouchOutsideDismiss = dismiss;
            return this;
        }

        /**
         * 创建PopupWindow
         * @return
         */
        public SmartPopupWindow createPopupWindow() {
            mWindow.init();
            return mWindow;
        }
    }
}


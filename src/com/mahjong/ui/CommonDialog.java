package com.mahjong.ui;

import com.mahjong.R;
import com.mahjong.anime.Rotate3dAnimation;
import com.mahjong.tools.GetSize;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommonDialog extends Dialog {
	
	public Context mContext;
	private RelativeLayout container;
	private LinearLayout contentRelativeLayout;
	public TextView ok, cancel, titleTextView;
	private Handler handler = new Handler();
	public View view;
	private int itemCount = -1;
	private int maxHeight;
	public View buttonContainerView;
	
	private onDialogListener mDialogListener;

	public CommonDialog(Context context) {
		super(context);

		mContext = context;
		initUI();

	}
	
	public CommonDialog(Context context,boolean isone) {

		super(context);
		mContext = context;
		initUI_oneButton();
	}


	public CommonDialog(Context context, int mydialogstyle) {

		super(context, mydialogstyle);
		mContext = context;
		initUI();
	}

	public CommonDialog(Context context, int mydialogstyle, int Style) {

		super(context, mydialogstyle);
		mContext = context;
		if(Style==100){
			initUI_changeableSize_oneButton();
		}else{
			initUI_oneButton();
		}
	}

	public CommonDialog(Context context, int mydialogstyle, boolean flag) {

		super(context, mydialogstyle);
		mContext = context;
		if(flag){
			initUI_noButton();
		}else{
			initUI_changeableSize_noButton();
		}
	}

	public void setItemCount(int count, int maxHeight) {
		this.itemCount = count;
		this.maxHeight = maxHeight;
	}

	public void addView(int id) {
		if (mContext != null) {
			LayoutInflater layoutInflater = LayoutInflater.from(mContext);
			view = layoutInflater.inflate(id, null);

			adjust_view_size(contentRelativeLayout);
		}

	}

	private void adjust_view_size(final View mView) {

		final ViewTreeObserver vto = mView.getViewTreeObserver();

		vto.addOnPreDrawListener(new OnPreDrawListener() {

			public boolean onPreDraw() {
				mView.getViewTreeObserver().removeOnPreDrawListener(this);

				handler.post(new Runnable() {

					public void run() {

						LayoutParams layoutParams = mView.getLayoutParams();

						if (itemCount > 2) {
							layoutParams.height = maxHeight;
							mView.setLayoutParams(layoutParams);
						}
						if (view != null)
							((ViewGroup) mView).addView(view);

					}
				});
				return true;
			}
		});

	}

	public void addView(View view) {

		contentRelativeLayout.addView(view);
		adjust_view_size(contentRelativeLayout);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	public void cancel() {
		super.cancel();
		if (mDialogListener != null) {
			mDialogListener.cancelDialog();
		}
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		if (mDialogListener != null) {
			mDialogListener.cancelDialog();
		}
	}
	
	void initUI() {
		setContentView(R.layout.dialog_twobutton);

		container = (RelativeLayout) findViewById(R.id.dialog_container);
		contentRelativeLayout = (LinearLayout) findViewById(R.id.contentlayout);
		titleTextView = (TextView) findViewById(R.id.title);
		ok = (TextView) findViewById(R.id.l_ok);
		cancel = (TextView) findViewById(R.id.l_cancel);
		buttonContainerView = findViewById(R.id.container_button);
	}

	void initUI_oneButton() {
		setContentView(R.layout.dialog_onebutton);

		container = (RelativeLayout) findViewById(R.id.dialog_container);
		contentRelativeLayout = (LinearLayout) findViewById(R.id.contentlayout);
		titleTextView = (TextView) findViewById(R.id.title);
		ok = (TextView) findViewById(R.id.cancel);
	}

	void initUI_noButton() {
		setContentView(R.layout.dialog_nobutton);

		container = (RelativeLayout) findViewById(R.id.dialog_container);
		contentRelativeLayout = (LinearLayout) findViewById(R.id.contentlayout);
		titleTextView = (TextView) findViewById(R.id.title);
	}
	
	void initUI_changeableSize_oneButton(){
		setContentView(R.layout.dialog_onebutton_sizeable);

		container = (RelativeLayout) findViewById(R.id.dialog_container);
		contentRelativeLayout = (LinearLayout) findViewById(R.id.contentlayout);
		titleTextView = (TextView) findViewById(R.id.title);
		ok = (TextView) findViewById(R.id.cancel);
	}
	
	void initUI_changeableSize_noButton(){
		setContentView(R.layout.dialog_nobutton_sizeable);
		
		container = (RelativeLayout) findViewById(R.id.dialog_container);
		contentRelativeLayout = (LinearLayout) findViewById(R.id.contentlayout);
		titleTextView = (TextView) findViewById(R.id.title);
	}

	public View getContentView() {
		return view;
	}
	
	public void setOnCancelDialogListener(onDialogListener dialogListener) {
		this.mDialogListener = dialogListener;
	}
	
	public interface onDialogListener {		
		public void cancelDialog();
	}
	
	
	/******************************************* 3D旋转动画 ********************************************/
	private int centerX;
    private int centerY;
    private int depthZ = 700;//修改此处可以改变距离来达到你满意的效果
    private int duration = 300;//动画时间
    private Rotate3dAnimation openAnimation;
    private Rotate3dAnimation closeAnimation;
    private OnAnimationlistener mAnimationlistener;
    
    public void openAnimation() {
    	centerX = container.getWidth() / 2;
        centerY = container.getHeight() / 2;
        if (openAnimation == null) {
        	initOpenAnim();
        }
        //用作判断当前点击事件发生时动画是否正在执行
        if (openAnimation.hasStarted() && !openAnimation.hasEnded()) {
            return;
        }
        container.startAnimation(openAnimation);
    }
    
    public void closeAnimation() {
    	centerX = container.getWidth() / 2;
        centerY = container.getHeight() / 2;
        if (closeAnimation == null) {
			initCloseAnim();
		}
        //用作判断当前点击事件发生时动画是否正在执行
        if (closeAnimation.hasStarted() && !closeAnimation.hasEnded()) {
            return;
        }
        container.startAnimation(closeAnimation);
    }

    /**
     *注意旋转角度
     */
    private void initOpenAnim() {
        //从0到90度，顺时针旋转视图，此时reverse参数为true，达到90度时动画结束时视图变得不可见，
        openAnimation = new Rotate3dAnimation(0, 90, centerX, centerY, depthZ, true);
        openAnimation.setDuration(duration);
        openAnimation.setFillAfter(true);
        openAnimation.setInterpolator(new AccelerateInterpolator());
        openAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //从270到360度，顺时针旋转视图，此时reverse参数为false，达到360度动画结束时视图变得可见
            	if (mAnimationlistener != null) mAnimationlistener.onOpenEnd();
                Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(270, 360, centerX, centerY, depthZ, false);
                rotateAnimation.setDuration(duration);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                container.startAnimation(rotateAnimation);
            }
        });
    }


    private void initCloseAnim() {
        closeAnimation = new Rotate3dAnimation(360, 270, centerX, centerY, depthZ, true);
        closeAnimation.setDuration(duration);
        closeAnimation.setFillAfter(true);
        closeAnimation.setInterpolator(new AccelerateInterpolator());
        closeAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            	if (mAnimationlistener != null) mAnimationlistener.onCloseEnd();
                Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(90, 0, centerX, centerY, depthZ, false);
                rotateAnimation.setDuration(duration);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                container.startAnimation(rotateAnimation);
            }
        });
    }
    
    public void setOnAnimationListener(OnAnimationlistener listener) {
    	this.mAnimationlistener = listener;
    }
    
    public interface OnAnimationlistener {
    	public void onOpenEnd();
    	public void onCloseEnd();
    }

}

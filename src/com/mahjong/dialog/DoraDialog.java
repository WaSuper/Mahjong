package com.mahjong.dialog;

import java.util.List;

import com.mahjong.R;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;
import com.mahjong.tools.KeyBoardUtil;
import com.mahjong.tools.KeyBoardUtil.OnKeyBoardListener;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.MahjongBaseView;
import com.mahjong.ui.MahjongDora;
import com.mahjong.ui.MahjongBaseView.OnTouchEventListener;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class DoraDialog extends Dialog implements OnClickListener {

	private Context mContext;
	
	public Button mCancel;
	public Button mReset;
	public Button mOk;	
	private MahjongDora mOutDora;
	private MahjongDora mInDora;
	private KeyBoardUtil mKeyBoardUtil;
	
	private boolean hasInDoras;
	private MahjongBaseView lastMahjongView;
	private OnDoraDialogListener mListener;
	
	public DoraDialog(Context context) {
		super(context, R.style.MyDialogStyle);
		setContentView(R.layout.dialog_dora_setting);
		this.mContext = context;
		initUI();
	}
	
	private void initUI() {
		mCancel = (Button) findViewById(R.id.dialog_dora_back);
		mCancel.setOnClickListener(this);
		mReset = (Button) findViewById(R.id.dialog_dora_reset);
		mReset.setOnClickListener(this);
		mOk = (Button) findViewById(R.id.dialog_dora_ok);
		mOk.setOnClickListener(this);		
		
		mOutDora = (MahjongDora) findViewById(R.id.dialog_dora_out);
		mOutDora.setOnTouchEventListener(mTouchListener);
		mInDora = (MahjongDora) findViewById(R.id.dialog_dora_in);
		mInDora.setOnTouchEventListener(mTouchListener);
		mKeyBoardUtil = new KeyBoardUtil(mContext, 
				((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
		mKeyBoardUtil.setOnKeyBoardListener(mKeyBoardListener);
		
		mOutDora.showStartItem();
		lastMahjongView = mOutDora;
	}

	public void setDoraIndicator(boolean isIn,
			List<MjCard> doras, List<MjCard> doraIns) {
		mInDora.setVisibility((hasInDoras = isIn) ? View.VISIBLE : View.GONE);	
		mOutDora.setDoraList(doras);
		mInDora.setDoraList(doraIns);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.dialog_dora_back:
			dismiss();
			break;
		case R.id.dialog_dora_reset:
			mOutDora.reset();
			mInDora.reset();
			break;
		case R.id.dialog_dora_ok:
			checkFinish();
			break;
		default:
			break;
		}
	}

	private void checkFinish() {
		if (hasInDoras) {
			if (mOutDora.size() != mInDora.size()) {
				ToastTool.showToast(mContext, R.string.dora_size_not_match);
				return;
			}
		}
		if (mListener != null) {
			mListener.onFinish(mOutDora.getDoraList(), mInDora.getDoraList());
		}
		dismiss();
	}
	
	private OnTouchEventListener mTouchListener = new OnTouchEventListener() {
		
		@Override
		public void onTouchPairs(MahjongBaseView view, MjCardPairs pairs) {
			
		}
		
		@Override
		public void onTouchOne(MahjongBaseView view, MjCard card) {
			if (lastMahjongView != null && lastMahjongView != view) {
				lastMahjongView.clearTouchItem();
			}
			lastMahjongView = view;
			mKeyBoardUtil.show(view);		
		}
		
		@Override
		public void onTouchAdd(MahjongBaseView view) {
			
		}
	};
	
	private OnKeyBoardListener mKeyBoardListener = new OnKeyBoardListener() {
		
		@Override
		public void onShow(MahjongBaseView view) {
			
		}
		
		@Override
		public void onHide(MahjongBaseView view) {
			if (view != null) view.clearTouchItem();
			lastMahjongView = null;
		}
		
		@Override
		public void onClick(MahjongBaseView view, int key) {
			if (view != null) view.addMjCard(key);
		}
		
		@Override
		public void onBackSpace(MahjongBaseView view) {
			if (view != null) view.removeTouchItem();
		}
		
		@Override
		public void onLeft(MahjongBaseView view) {
			if (view != null) view.onPressLeft();
		}
		
		@Override
		public void onRight(MahjongBaseView view) {
			if (view != null) view.onPressRight();
		}
	};
	
	public void setOnDoraDialogListener(OnDoraDialogListener listener) {
		mListener = listener;
	}
	
	public interface OnDoraDialogListener {
		public void onFinish(List<MjCard> doras, List<MjCard> doraIns);
	}
	
}

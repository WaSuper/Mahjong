package com.mahjong.activity.jpn;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.activity.BaseActivity;
import com.mahjong.adapter.DropDownAdapter;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;
import com.mahjong.common.MjDir;
import com.mahjong.common.MjPairType;
import com.mahjong.item.DropBean;
import com.mahjong.tools.KeyBoardUtil;
import com.mahjong.tools.PopWinDownUtil;
import com.mahjong.tools.KeyBoardUtil.OnKeyBoardListener;
import com.mahjong.tools.PopWinDownUtil.OnDismissListener;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.MahjongBaseView.OnTouchEventListener;
import com.mahjong.ui.MahjongBaseView;
import com.mahjong.ui.MahjongPairs;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PairsSelectActivity extends BaseActivity
		implements OnClickListener, OnItemClickListener, OnDismissListener {

	public static final String MjPairsType = "MjPairsType";
	public static final String MjPairsValue = "MjPairsValue";
	
	private Context mContext;
	
	private TextView mTitle;
	private ImageView mBack;
	private ImageView mMore;
	private TextView mOK;
	private ImageView mDirLeft;
	private ImageView mDirCenter;
	private ImageView mDirRight;
	private MahjongPairs mMahjongPairs;
	
	private KeyBoardUtil mKeyBoardUtil;
	private PopWinDownUtil popWinDownUtil;
	
	private DropDownAdapter mDropDownAdapter;
	
	private int mSelectPosition = 0;
	private List<DropBean> mSelectList = new ArrayList<DropBean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_pairs_select);
		mContext = this;
		initUI();
		initData();
		initPopUpWin();
	}
	
	private void initUI() {
		mTitle = (TextView) findViewById(R.id.pairs_title);
		mBack = (ImageView) findViewById(R.id.pairs_back);
		mMore = (ImageView) findViewById(R.id.pairs_title_more);
		mOK = (TextView) findViewById(R.id.pairs_ok);
		mDirLeft = (ImageView) findViewById(R.id.pairs_dir_left);
		mDirCenter = (ImageView) findViewById(R.id.pairs_dir_center);
		mDirRight = (ImageView) findViewById(R.id.pairs_dir_right);
		mMahjongPairs = (MahjongPairs) findViewById(R.id.pairs_mahjongpairs);
		mKeyBoardUtil = new KeyBoardUtil(mContext, 
				((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
		
		mTitle.setOnClickListener(this);
		mMore.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mOK.setOnClickListener(this);
		mDirLeft.setOnClickListener(this);
		mDirCenter.setOnClickListener(this);
		mDirRight.setOnClickListener(this);
		mMahjongPairs.setOnTouchEventListener(onTouchEventListener);
		mKeyBoardUtil.setOnKeyBoardListener(onKeyBoardListener);
	}
	
	private void initData() {
		mSelectList.clear();
		mSelectList.add(new DropBean(getString(R.string.triplet), MjPairType.Triplet));
		mSelectList.add(new DropBean(getString(R.string.sequence), MjPairType.Sequence));
		mSelectList.add(new DropBean(getString(R.string.addition_kong), MjPairType.AdditionKong));
		mSelectList.add(new DropBean(getString(R.string.exposed_kong), MjPairType.ExposedKong));
		mSelectList.add(new DropBean(getString(R.string.concealed_kong), MjPairType.ConcealedKong));
		
		Intent intent = getIntent();
		String type = intent.getStringExtra(MjPairsType);
		setMjPairsType(type);
	}

	private void setMjPairsType(String type) {
		if (type != null) {
			for (int i = 0; i< mSelectList.size(); i++) {
				DropBean dropBean = mSelectList.get(i);
				if (type.endsWith(dropBean.getName())) {
					mSelectPosition = i;
					dropBean.setChoiced(true);
					mTitle.setText(dropBean.getName());
					mMahjongPairs.setPairsType(dropBean.getType(), mMahjongPairs.getDir());
					break;
				}
			}
		}
	}
	
	private void initPopUpWin() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dropdown_content, null);
		view.findViewById(R.id.dropdown_content).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						popWinDownUtil.hide();
					}
				});
		ListView listView = (ListView) view.findViewById(R.id.dropdown_listview);
		listView.setOnItemClickListener(this);
		mDropDownAdapter = new DropDownAdapter(mSelectList, mContext);
		listView.setAdapter(mDropDownAdapter);
		popWinDownUtil = new PopWinDownUtil(mContext, view, mTitle);
		popWinDownUtil.setOnDismissListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mSelectPosition == position) {
			return;
		}
		mSelectList.get(mSelectPosition).setChoiced(false);
		DropBean newDropBean = mSelectList.get(position);
		newDropBean.setChoiced(true);
		mTitle.setText(newDropBean.getName());
		mMahjongPairs.setPairsType(newDropBean.getType(), mMahjongPairs.getDir());
		mDropDownAdapter.notifyDataSetChanged();
		mSelectPosition = position;
		popWinDownUtil.hide();
	}

	@Override
	public void onDismiss() {
		mMore.setImageResource(R.drawable.selector_btn_bottom_arrow);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.pairs_back:
			this.finish();
			break;
		case R.id.pairs_title:
		case R.id.pairs_title_more:
			popWinDownUtil.showAsDropDown();
			mMore.setImageResource(R.drawable.selector_btn_top_arrow);
			break;
		case R.id.pairs_ok:
			if (!mMahjongPairs.checkValid()) {
				ToastTool.showToast(mContext, R.string.input_invalid);
				return;
			}
			Intent intent = getIntent();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MjPairsValue, mMahjongPairs.createMjCardPairs());
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			this.finish();
			break;
		case R.id.pairs_dir_left:
			mMahjongPairs.setDir(MjDir.left);
			break;
		case R.id.pairs_dir_center:
			mMahjongPairs.setDir(MjDir.center);
			break;
		case R.id.pairs_dir_right:
			mMahjongPairs.setDir(MjDir.right);
			break;
		default:
			break;
		}
	}
	
	private OnTouchEventListener onTouchEventListener = new OnTouchEventListener() {
		
		@Override
		public void onTouchAdd(MahjongBaseView view) {
			
		}

		@Override
		public void onTouchOne(MahjongBaseView view, MjCard card) {
			mKeyBoardUtil.show(view);
		}

		@Override
		public void onTouchPairs(MahjongBaseView view, MjCardPairs pairs) {
			
		}
	};
	
	private OnKeyBoardListener onKeyBoardListener = new OnKeyBoardListener() {
		
		@Override
		public void onShow(MahjongBaseView view) {
			
		}
		
		@Override
		public void onHide(MahjongBaseView view) {
			mMahjongPairs.clearTouchItem();
		}
		
		@Override
		public void onLeft(MahjongBaseView view) {
			mMahjongPairs.onPressLeft();
		}
		
		@Override
		public void onRight(MahjongBaseView view) {
			mMahjongPairs.onPressRight();
		}
		
		@Override
		public void onClick(MahjongBaseView view, int key) {
			mMahjongPairs.addMjCard(key);
		}
		
		@Override
		public void onBackSpace(MahjongBaseView view) {
			mMahjongPairs.removeMjCard();
		}
	};
	
}

package com.mahjong.dialog;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mahjong.R;
import com.mahjong.adapter.SoundEffectAdapter;
import com.mahjong.model.SoundBox;
import com.mahjong.tools.GetSize;
import com.mahjong.ui.CommonDialog;

public class SoundBoxSelectDialog extends CommonDialog implements OnItemClickListener {

	private ListView mListView;
	private SoundEffectAdapter mAdapter;
	
	private List<SoundBox> mSoundBoxList;
	private OnSoundBoxSelectListener mListener;
	
	public SoundBoxSelectDialog(Context context) {
		super(context, R.style.MyDialogStyle, 0);
		initUI(context);
	}

	private void initUI(Context context) {
		addView(R.layout.layout_listview);
		setCanceledOnTouchOutside(true);
		titleTextView.setText(R.string.please_select_soundbox);
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		mListView = (ListView) view.findViewById(R.id.listview);
		mAdapter = new SoundEffectAdapter(context);
		mSoundBoxList = SoundBox.getAllSoundBoxs();
		mAdapter.setData(mSoundBoxList);
		mAdapter.setTextColor(Color.BLACK);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		setItemCount(mSoundBoxList.size(), GetSize.dip2px(mContext, 260));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mListener != null) mListener.onSelect(mSoundBoxList.get(position));
		dismiss();
	}
	
	public void setOnSoundBoxSelectListener(OnSoundBoxSelectListener listener) {
		this.mListener = listener;
	}
	
	public interface OnSoundBoxSelectListener {
		void onSelect(SoundBox soundbox);
	}
	
}

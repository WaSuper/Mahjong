package com.mahjong.dialog;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mahjong.R;
import com.mahjong.adapter.EmoticonAdapter;
import com.mahjong.model.Character;
import com.mahjong.tools.GetSize;
import com.mahjong.ui.CommonDialog;

public class CharacterSelectDialog extends CommonDialog implements OnItemClickListener {

	private ListView mListView;
	private EmoticonAdapter mAdapter;
	
	private List<Character> mCharacterList;
	private OnCharacterSelectListener mListener;
	
	public CharacterSelectDialog(Context context) {
		super(context, R.style.MyDialogStyle, 0);
		initUI(context);
	}

	private void initUI(Context context) {
		addView(R.layout.layout_listview);
		setCanceledOnTouchOutside(true);
		titleTextView.setText(R.string.please_select_character);
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		mListView = (ListView) view.findViewById(R.id.listview);
		mAdapter = new EmoticonAdapter(context);
		mCharacterList = Character.getAllCharacters(context);
		mAdapter.setData(mCharacterList);
		mAdapter.setTextColor(Color.BLACK);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		setItemCount(mCharacterList.size(), GetSize.dip2px(mContext, 260));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mListener != null) mListener.onSelect(mCharacterList.get(position));
		dismiss();
	}
	
	public void setOnCharacterSelectListener(OnCharacterSelectListener listener) {
		this.mListener = listener;
	}
	
	public interface OnCharacterSelectListener {
		void onSelect(Character character);
	}
	
}

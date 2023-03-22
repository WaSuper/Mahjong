package com.mahjong.dialog;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahjong.R;
import com.mahjong.ui.CommonDialog;

public class MaPointDialog extends CommonDialog implements OnClickListener {

	private TextView[] titleTexts = new TextView[4];
	private LinearLayout[] lineLayouts = new LinearLayout[4]; 
	private Button[] plus5Btns = new Button[4];
	private Button[] plus1Btns = new Button[4];
	private Button[] minus1Btns = new Button[4];
	private Button[] minus5Btns = new Button[4];
	private EditText[] maTexts = new EditText[4];
	private TextView maInvalid;
	
	private int[] defaultMa;
	
	private MaPointListener mListener;
	
	public MaPointDialog(Context context) {
		super(context, R.style.MyDialogStyle);
		initUI();
	}
	
	private void initUI() {
		addView(R.layout.layout_mapoint);
		setCanceledOnTouchOutside(false);
		titleTextView.setText(mContext.getString(R.string.ma_point));
		View contentView = getContentView();
		titleTexts[0] = (TextView) contentView.findViewById(R.id.mapoint_line1_title1);
		titleTexts[1] = (TextView) contentView.findViewById(R.id.mapoint_line2_title2);
		titleTexts[2] = (TextView) contentView.findViewById(R.id.mapoint_line3_title3);
		titleTexts[3] = (TextView) contentView.findViewById(R.id.mapoint_line4_title4);
		lineLayouts[0] = (LinearLayout) contentView.findViewById(R.id.mapoint_line1_layout1);
		lineLayouts[1] = (LinearLayout) contentView.findViewById(R.id.mapoint_line2_layout2);
		lineLayouts[2] = (LinearLayout) contentView.findViewById(R.id.mapoint_line3_layout3);
		lineLayouts[3] = (LinearLayout) contentView.findViewById(R.id.mapoint_line4_layout4);
		plus5Btns[0] = (Button) contentView.findViewById(R.id.mapoint_line1_plus5);
		plus5Btns[1] = (Button) contentView.findViewById(R.id.mapoint_line2_plus5);
		plus5Btns[2] = (Button) contentView.findViewById(R.id.mapoint_line3_plus5);
		plus5Btns[3] = (Button) contentView.findViewById(R.id.mapoint_line4_plus5);
		plus1Btns[0] = (Button) contentView.findViewById(R.id.mapoint_line1_plus1);
		plus1Btns[1] = (Button) contentView.findViewById(R.id.mapoint_line2_plus1);
		plus1Btns[2] = (Button) contentView.findViewById(R.id.mapoint_line3_plus1);
		plus1Btns[3] = (Button) contentView.findViewById(R.id.mapoint_line4_plus1);
		minus1Btns[0] = (Button) contentView.findViewById(R.id.mapoint_line1_minus1);
		minus1Btns[1] = (Button) contentView.findViewById(R.id.mapoint_line2_minus1);
		minus1Btns[2] = (Button) contentView.findViewById(R.id.mapoint_line3_minus1);
		minus1Btns[3] = (Button) contentView.findViewById(R.id.mapoint_line4_minus1);
		minus5Btns[0] = (Button) contentView.findViewById(R.id.mapoint_line1_minus5);
		minus5Btns[1] = (Button) contentView.findViewById(R.id.mapoint_line2_minus5);
		minus5Btns[2] = (Button) contentView.findViewById(R.id.mapoint_line3_minus5);
		minus5Btns[3] = (Button) contentView.findViewById(R.id.mapoint_line4_minus5);
		maTexts[0] = (EditText) contentView.findViewById(R.id.mapoint_line1_point);
		maTexts[1] = (EditText) contentView.findViewById(R.id.mapoint_line2_point);
		maTexts[2] = (EditText) contentView.findViewById(R.id.mapoint_line3_point);
		maTexts[3] = (EditText) contentView.findViewById(R.id.mapoint_line4_point);
		maInvalid = (TextView) contentView.findViewById(R.id.mapoint_invalid);
		for (int i = 0; i < 4; i++) {
			plus5Btns[i].setOnClickListener(this);
			plus1Btns[i].setOnClickListener(this);
			minus1Btns[i].setOnClickListener(this);
			minus5Btns[i].setOnClickListener(this);
		}
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	public void show(int[] mas) {
		this.defaultMa = mas;
		maTexts[0].setText(defaultMa[0] + "");
		for (int i = 1; i < 4; i++) {
			if (defaultMa.length > i) {
				titleTexts[i].setVisibility(View.VISIBLE);
				lineLayouts[i].setVisibility(View.VISIBLE);
				maTexts[i].setText(defaultMa[i] + "");
			} else {
				titleTexts[i].setVisibility(View.GONE);
				lineLayouts[i].setVisibility(View.GONE);
			}
		}
		show();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.l_ok:
			int sum = 0;
			for (int i = 0; i < defaultMa.length; i++) {
				sum += defaultMa[i];
			}
			if (sum != 0) return;
			if (mListener != null) mListener.OnMaPointChange(defaultMa);
			dismiss();
			break;
		case R.id.l_cancel:
			dismiss();
			break;
		case R.id.mapoint_line1_plus5:		
			defaultMa[0] += 5;
			maTexts[0].setText(defaultMa[0] + "");
			checkMaPointValid(defaultMa, maInvalid);
			break;
		case R.id.mapoint_line1_plus1:
			defaultMa[0] += 1;
			maTexts[0].setText(defaultMa[0] + "");
			checkMaPointValid(defaultMa, maInvalid);
			break;
		case R.id.mapoint_line1_minus1:
			if (defaultMa[0] >= 1) {
				defaultMa[0] -= 1;
				maTexts[0].setText(defaultMa[0] + "");
				checkMaPointValid(defaultMa, maInvalid);
			}
			break;
		case R.id.mapoint_line1_minus5:
			if (defaultMa[0] >= 5) {
				defaultMa[0] -= 5;
				maTexts[0].setText(defaultMa[0] + "");
				checkMaPointValid(defaultMa, maInvalid);
			}
			break;
		case R.id.mapoint_line2_plus5:	
			defaultMa[1] += 5;
			maTexts[1].setText(defaultMa[1] + "");	
			checkMaPointValid(defaultMa, maInvalid);				
			break;
		case R.id.mapoint_line2_plus1:
			defaultMa[1] += 1;
			maTexts[1].setText(defaultMa[1] + "");	
			checkMaPointValid(defaultMa, maInvalid);
			break;
		case R.id.mapoint_line2_minus1:
			if (defaultMa[1] >= 1) {
				defaultMa[1] -= 1;
				maTexts[1].setText(defaultMa[1] + "");	
				checkMaPointValid(defaultMa, maInvalid);
			}	
			break;
		case R.id.mapoint_line2_minus5:
			if (defaultMa[1] >= 5) {
				defaultMa[1] -= 5;
				maTexts[1].setText(defaultMa[1] + "");	
				checkMaPointValid(defaultMa, maInvalid);
			}
			break;
		case R.id.mapoint_line3_plus5:		
			if (defaultMa[2] <= -5) {
				defaultMa[2] += 5;
				maTexts[2].setText(defaultMa[2] + "");
				checkMaPointValid(defaultMa, maInvalid);	
			}			
			break;
		case R.id.mapoint_line3_plus1:
			if (defaultMa[2] <= -1) {
				defaultMa[2] += 1;
				maTexts[2].setText(defaultMa[2] + "");
				checkMaPointValid(defaultMa, maInvalid);
			}
			break;
		case R.id.mapoint_line3_minus1:
			defaultMa[2] -= 1;
			maTexts[2].setText(defaultMa[2] + "");
			checkMaPointValid(defaultMa, maInvalid);
			break;
		case R.id.mapoint_line3_minus5:
			defaultMa[2] -= 5;
			maTexts[2].setText(defaultMa[2] + "");
			checkMaPointValid(defaultMa, maInvalid);
			break;
		case R.id.mapoint_line4_plus5:	
			if (defaultMa[3] <= -5) {
				defaultMa[3] += 5;
				maTexts[3].setText(defaultMa[3] + "");	
				checkMaPointValid(defaultMa, maInvalid);
			}			
			break;
		case R.id.mapoint_line4_plus1:
			if (defaultMa[3] <= -1) {
				defaultMa[3] += 1;
				maTexts[3].setText(defaultMa[3] + "");	
				checkMaPointValid(defaultMa, maInvalid);
			}
			break;
		case R.id.mapoint_line4_minus1:
			defaultMa[3] -= 1;
			maTexts[3].setText(defaultMa[3] + "");	
			checkMaPointValid(defaultMa, maInvalid);
			break;
		case R.id.mapoint_line4_minus5:
			defaultMa[3] -= 5;
			maTexts[3].setText(defaultMa[3] + "");	
			checkMaPointValid(defaultMa, maInvalid);
			break;
		default:
			break;
		}
	}
	
	private boolean checkMaPointValid(int[] ma, TextView invalidText) {
		int sum = 0;
		for (int i = 0; i < ma.length; i++) {
			sum += ma[i];
		}
		boolean result = sum == 0;
		if (result) {
			invalidText.setVisibility(View.INVISIBLE);
		} else {
			invalidText.setVisibility(View.VISIBLE);
		}
		return result;
	}
	
	public void setOnMaPointListener(MaPointListener listener) {
		this.mListener = listener;
	}
	
	public interface MaPointListener {
		public void OnMaPointChange(int[] mas);
	}

}

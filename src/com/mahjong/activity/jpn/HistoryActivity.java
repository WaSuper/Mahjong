package com.mahjong.activity.jpn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.mahjong.R;
import com.mahjong.activity.FileActivity;
import com.mahjong.adapter.HistoryAdapter;
import com.mahjong.adapter.HistoryAdapter.HistoryItemListener;
import com.mahjong.adapter.MapointRankAdapter;
import com.mahjong.model.MjDetail;
import com.mahjong.model.MjResult;
import com.mahjong.model.RankItem;
import com.mahjong.tools.DateFormatUtils;
import com.mahjong.tools.ExcelUtils;
import com.mahjong.tools.FileFitVersionTool;
import com.mahjong.tools.FileTools;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.CustomDatePicker;
import com.mahjong.ui.CustomDatePicker.Callback;
import com.mahjong.ui.HorizontalPosition;
import com.mahjong.ui.SmartPopupWindow;
import com.mahjong.ui.VerticalPosition;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

public class HistoryActivity extends Activity
		implements OnClickListener, OnItemClickListener, OnItemLongClickListener, OnCheckedChangeListener {

	public final static int Data_Result_Done = 9001;
	
	private Context mContext;
	private List<MjResult> mResultList;
	
	private ImageView mBack;
	private ImageView mMore;
	private Button mStartDateBtn;
	private Button mEndDateBtn;
	private TextView mCountText;
	private ListView mHistoryList;
	private HistoryAdapter mAdapter;
	private SmartPopupWindow popupWindow;
	
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
	private CustomDatePicker mStartDatePicker;
	private CustomDatePicker mEndDatePicker;
	private long mStartDate;
	private long mEndDate;
	
	private boolean isBatchMode = false;
	private int mSelectId;
	private LinearLayout mBottomLayout;
	private CheckBox mSelAllBox;
	private TextView mSelActionView;
	private TextView mOkView;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_history);
		mContext = this;
		initUI();
		FileTools.getInstance().setFitVersionTool(new FileFitVersionTool(this, new Handler()));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case FileActivity.File_Excel_Only:
				String filePath = data.getStringExtra(FileActivity.FileDir);
				int count = ExcelUtils.readExcelToResult(filePath);
				if (count > 0) {
					ToastTool.showToast(mContext, mContext.getString(R.string.add_log_success, count));
					searchResultList(true);
					ShareprefenceTool.getInstance().setBoolean(RankItem.IS_NEED_UPDATE, true, mContext);
				} else {
					ToastTool.showToast(mContext, R.string.add_log_fail);
				}
				break;
			case Data_Result_Done:
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	}
	
	private void initUI() {
		mBack = (ImageView) findViewById(R.id.history_back);
		mMore = (ImageView) findViewById(R.id.history_more);
		mStartDateBtn = (Button) findViewById(R.id.history_date_start);
		mEndDateBtn = (Button) findViewById(R.id.history_date_end);
		mCountText = (TextView) findViewById(R.id.history_count);
		mHistoryList = (ListView) findViewById(R.id.history_listview);		
		
		mBack.setOnClickListener(this);
		mMore.setOnClickListener(this);
		mStartDateBtn.setOnClickListener(this);
		mEndDateBtn.setOnClickListener(this);
		
		long newTime = System.currentTimeMillis();
		String now = mDateFormat.format(new Date(newTime));
		mStartDate = DateFormatUtils.str2Long(now + " 00:00", true);
		mEndDate = DateFormatUtils.str2Long(now + " 23:59", true);;
		mStartDateBtn.setText(now);
		mEndDateBtn.setText(now);
		long start = DateFormatUtils.str2Long("2010-01-01", false);
		mStartDatePicker = new CustomDatePicker(mContext, new Callback() {
			
			@Override
			public void onTimeSelected(long timestamp) {
				String text = mDateFormat.format(new Date(timestamp));
				mStartDateBtn.setText(text);
				mStartDate = DateFormatUtils.str2Long(text + " 00:00", true);
				searchResultList(true);
			}
		}, start, newTime);
		mStartDatePicker.setCanShowPreciseTime(false);
		mStartDatePicker.setScrollLoop(false);
		mEndDatePicker = new CustomDatePicker(mContext, new Callback() {
			
			@Override
			public void onTimeSelected(long timestamp) {
				String text = mDateFormat.format(new Date(timestamp));
				mEndDateBtn.setText(text);	
				mEndDate = DateFormatUtils.str2Long(text + " 23:59", true);		
				searchResultList(true);
			}
		}, start, newTime);
		mEndDatePicker.setCanShowPreciseTime(false);
		mEndDatePicker.setScrollLoop(false);
			
		searchResultList(false);
		mAdapter = new HistoryAdapter(mResultList, mContext);
		mHistoryList.setAdapter(mAdapter);
		mHistoryList.setOnItemClickListener(this);
		mHistoryList.setOnItemLongClickListener(this);
		mAdapter.setOnItemClickListener(new HistoryItemListener() {
			
			@Override
			public void onItemClick(int position) {
				itemClick(position);
			}
		});
		
		mBottomLayout = (LinearLayout) findViewById(R.id.history_ll_batch);
		mSelAllBox = (CheckBox) findViewById(R.id.history_cb_select_all);
		mSelAllBox.setOnCheckedChangeListener(this);
		mSelActionView = (TextView) findViewById(R.id.history_tv_action);
		mOkView = (TextView) findViewById(R.id.history_tv_ok);
		mOkView.setOnClickListener(this);
	}

	private void searchResultList(boolean isReset) {
		mResultList = new Select().from(MjResult.class)
				.where(MjResult.Col_StartTime + ">=? AND " + MjResult.Col_StartTime + "<=?", mStartDate, mEndDate)
				.orderBy(MjResult.Col_StartTime + " DESC")
				.execute();
		mCountText.setText(String.format(getString(R.string.log_count), 
				mResultList != null ? mResultList.size() : 0));
		if (isReset) {
			mAdapter.setData(mResultList);
		}
	}
	
	private void showAllResult() {
		mResultList = new Select().from(MjResult.class)
				.orderBy(MjResult.Col_StartTime + " DESC")
				.execute();
		mCountText.setText(String.format(getString(R.string.log_count), 
				mResultList != null ? mResultList.size() : 0));
		mAdapter.setData(mResultList);
		if (mResultList != null && mResultList.size() > 0) {
			mStartDate = mResultList.get(mResultList.size() - 1).getStartTime();
			String startText = mDateFormat.format(new Date(mStartDate));
			mStartDateBtn.setText(startText);
			mStartDate = DateFormatUtils.str2Long(startText + " 00:00", true);
			mEndDate = mResultList.get(0).getStartTime();
			String endText = mDateFormat.format(new Date(mEndDate));
			mEndDateBtn.setText(endText);
			mEndDate = DateFormatUtils.str2Long(endText + " 23:59", true);
		} else {
			long time = System.currentTimeMillis();
			String timeText = mDateFormat.format(new Date(time));
			mStartDateBtn.setText(timeText);
			mStartDate = DateFormatUtils.str2Long(timeText + " 00:00", true);
			mEndDate = DateFormatUtils.str2Long(timeText + " 23:59", true);
			mEndDateBtn.setText(timeText);
		}
	}
	
	@Override
	public void onBackPressed() {
		onBack();
	}
	
	private void onBack() {
		if (isBatchMode) {
			closeBatchMode();
		} else {
			finish();
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.history_back:
			onBack();
			break;
		case R.id.history_more:
			showPopupWindows();
			break;
		case R.id.history_date_start:
			mStartDatePicker.show(mStartDateBtn.getText().toString());
			break;
		case R.id.history_date_end:
			mEndDatePicker.show(mEndDateBtn.getText().toString());
			break;
		case R.id.his_act_copy_to_clipboard:
			showBatchMode(view.getId(), mContext.getString(R.string.copy_to_clipboard));
			popupWindow.dismiss();
			break;
		case R.id.his_act_export_for_excel:
			showBatchMode(view.getId(), mContext.getString(R.string.export_for_excel));
			popupWindow.dismiss();
			break;
		case R.id.his_act_import_from_excel:
			Intent intent = new Intent(HistoryActivity.this, FileActivity.class);
			intent.putExtra(FileActivity.FileType, FileActivity.File_Excel_Only);
			startActivityForResult(intent, FileActivity.File_Excel_Only);
			popupWindow.dismiss();
			break;
		case R.id.his_act_show_all:
			showAllResult();
			popupWindow.dismiss();
			break;
		case R.id.his_act_statistics:
			showBatchMode(view.getId(), mContext.getString(R.string.statistics));
			popupWindow.dismiss();
			break;
		case R.id.history_tv_ok:
			doneBatchMode();
			break;
		default:
			break;
		}
	}	
	
	private void showBatchMode(int id, String text) {
		isBatchMode = true;
		mSelectId = id;
		mBottomLayout.setVisibility(View.VISIBLE);
		mSelAllBox.setChecked(false);
		mSelActionView.setText(text);
		mAdapter.setBatchMode(true);
	}
	
	private void closeBatchMode() {
		mBottomLayout.setVisibility(View.GONE);
		isBatchMode = false;
		mAdapter.setBatchMode(false);
	}
	
	private void doneBatchMode() {
		List<MjResult> list = mAdapter.getSelectList();
		if (list == null || list.size() == 0) {
			ToastTool.showToast(mContext, R.string.no_data);
			return;
		}
		switch (mSelectId) {
		case R.id.his_act_copy_to_clipboard:			
			copyToClickboard(list);
			break;
		case R.id.his_act_export_for_excel:
			String fileName = "mj_" + String.valueOf(System.currentTimeMillis());
			String filePath = Environment.getExternalStorageDirectory() + "/Mahjong";
			if (ExcelUtils.createExcelFromResult(filePath, fileName, list)) {
		        ToastTool.showToast(mContext, mContext.getString(R.string.export_success, filePath + "/" + fileName + ".xls"));				
			} else {
				ToastTool.showToast(mContext, mContext.getString(R.string.export_fail));	
			}
			break;
		case R.id.his_act_statistics:
			showRankdialog(list);
			break;
		default:
			break;
		}
		closeBatchMode();
	}
	
	private void showPopupWindows() {
		if (popupWindow == null) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.layout_history_action, null);
			popupWindow = SmartPopupWindow.Builder
					.build(this, view)
					.setAlpha(0.8f)
					.createPopupWindow();
			Button[] actions = new Button[5];
			actions[0] = (Button) view.findViewById(R.id.his_act_copy_to_clipboard);
			actions[1] = (Button) view.findViewById(R.id.his_act_export_for_excel);
			actions[2] = (Button) view.findViewById(R.id.his_act_import_from_excel);
			actions[3] = (Button) view.findViewById(R.id.his_act_show_all);
			actions[4] = (Button) view.findViewById(R.id.his_act_statistics);
			for (int i = 0; i < actions.length; i++) {
				actions[i].setOnClickListener(this);
			}			
		}
		popupWindow.showAtAnchorView(mMore, VerticalPosition.BELOW, HorizontalPosition.RIGHT);
	}

	private void copyToClickboard(List<MjResult> list) {
		//获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String battleCount = mContext.getString(R.string.battle_count) + "：";
        String startTime = mContext.getString(R.string.start_time) + "：";
        String endTime = mContext.getString(R.string.end_time) + "：";
        String[] rankStrings = new String[4];
        rankStrings[0] = mContext.getString(R.string.rank1);
        rankStrings[1] = mContext.getString(R.string.rank2);
        rankStrings[2] = mContext.getString(R.string.rank3);
        rankStrings[3] = mContext.getString(R.string.rank4);
        String point = mContext.getString(R.string.point);
        String divider = "----------------------------------------\n";
        String[] types = new String[4];
        types[0] = mContext.getString(R.string.battle_one);
        types[1] = mContext.getString(R.string.battle_two);
        types[2] = "";
        types[3] = mContext.getString(R.string.battle_four);
        StringBuffer buffer = new StringBuffer();
        for (MjResult result : list) {
        	String title = result.getTitle();
        	if (title != null && !title.isEmpty()) { // database ver.2
        		buffer.append(getString(R.string.title) + "：");
				buffer.append(title + "\n");
			}
			buffer.append(battleCount);
			buffer.append(types[result.getGameType()] + "\n");
			buffer.append(startTime);
			buffer.append(dateFormat.format(new Date(result.getStartTime())) + "\n");
			buffer.append(endTime);
			buffer.append(dateFormat.format(new Date(result.getEndTime())) + "\n");
			String[] names = result.getNames();
			int[] ranks = result.getRanks();
			int[] scores = result.getPoints();
			float[] mas = result.getMas();
			String[] ranklines = new String[4];
			for (int i = 0; i < 4; i++) {
				int rank = ranks[i];
				ranklines[rank - 1] = rankStrings[rank - 1] + names[i] + "\t\t" + point;
				String score = scores[i] + "(";
				if (mas[i] > 0) {
					score += "+" + mas[i];
				} else if (mas[i] == 0) {
					score += "±0";
				} else {
					score += mas[i];
				}
				score += ")";
				ranklines[rank - 1] += score + "\n";
			}		
			for (int i = 0; i < ranklines.length; i++) {
				buffer.append(ranklines[i]);
			}
			if (result.getStartTime() != list.get(list.size() - 1).getStartTime()) {
				buffer.append(divider);				
			}
		}
        ClipData mClipData = ClipData.newPlainText("Mahjong", buffer.toString());
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastTool.showToast(mContext, R.string.success);
	}
	
	private void showRankdialog(List<MjResult> list) {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(R.string.statistics);
		ListView listView = (ListView) mDialog.getContentView();
		MapointRankAdapter mAdapter = new MapointRankAdapter(list, mContext);
		listView.setAdapter(mAdapter);
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		itemClick(position);
	}
	
	private void itemClick(int position) {
		if (isBatchMode) {
			mAdapter.selectItem(position);
		} else {
			MjResult result = mResultList.get(position);
			Intent intent = new Intent(HistoryActivity.this, HistoryDetailActivity.class);
			intent.putExtra(MjResult.Col_StartTime, result.getStartTime());
			startActivityForResult(intent, Data_Result_Done);
		}	
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		if (isBatchMode) return false;
		final CommonDialog dialog = new CommonDialog(mContext, R.style.MyDialogStyle, true);
		dialog.addView(R.layout.layout_history_selection);
		dialog.setCanceledOnTouchOutside(true);
		dialog.titleTextView.setText(R.string.please_select_operation);
		View contentView = dialog.getContentView();
		TextView titleBtn = (TextView) contentView.findViewById(R.id.his_sel_title);
		TextView noteBtn = (TextView) contentView.findViewById(R.id.his_sel_note);
		TextView detailBtn = (TextView) contentView.findViewById(R.id.his_sel_detail);
		TextView deleteBtn = (TextView) contentView.findViewById(R.id.his_sel_delete);
		titleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				editTitle(mResultList.get(position));
				dialog.dismiss();
			}
		});
		noteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				editNote(mResultList.get(position));
				dialog.dismiss();
			}
		});
		detailBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				MjResult result = mResultList.get(position);
				Intent intent = new Intent(HistoryActivity.this, HistoryDetailActivity.class);
				intent.putExtra(MjResult.Col_StartTime, result.getStartTime());
				startActivity(intent);				
				dialog.dismiss();
			}
		});
		deleteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				try {
					MjResult result = mResultList.get(position);
					new Delete().from(MjResult.class)
							.where(MjResult.Col_StartTime + "=?", result.getStartTime())
							.execute();
					new Delete().from(MjDetail.class)
							.where(MjDetail.Col_StartTime + "=?", result.getStartTime())
							.execute();
					ToastTool.showToast(mContext, R.string.delete_success);
					searchResultList(true);
					ShareprefenceTool.getInstance().setBoolean(RankItem.IS_NEED_UPDATE, true, mContext);
				} catch (Exception e) {
					e.printStackTrace();
					ToastTool.showToast(mContext, R.string.delete_fail);
				} finally {
					dialog.dismiss();
				}		
			}
		});
		dialog.show();
		return true;
	}

	@Override
	public void onCheckedChanged(CompoundButton btn, boolean checked) {
		mAdapter.selectAll(checked);
	}
	
	private void editTitle(final MjResult result) {
		final CommonDialog editDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		editDialog.addView(R.layout.item_edittext);
		editDialog.setCanceledOnTouchOutside(true);
		editDialog.titleTextView.setText(getString(R.string.title));
		final EditText editText = (EditText) editDialog.getContentView().findViewById(R.id.edittext);
		editText.setText(result.getTitle() != null ? result.getTitle() : "");
		editDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String text = editText.getText().toString().trim();
				result.setTitle(text);
				result.save();
				mAdapter.notifyDataSetChanged();
				editDialog.dismiss();
			}
		});
		editDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				editDialog.dismiss();
			}
		});
		editDialog.show();
	}
	
	private void editNote(final MjResult result) {
		final CommonDialog editDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		editDialog.addView(R.layout.item_edittext_large);
		editDialog.setCanceledOnTouchOutside(true);
		editDialog.titleTextView.setText(getString(R.string.note));
		final EditText editText = (EditText) editDialog.getContentView().findViewById(R.id.edittext);
		editText.setText(result.getNote() != null ? result.getNote() : "");
		editText.setHorizontallyScrolling(true);
		editDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String text = editText.getText().toString().trim();
				result.setNote(text);
				result.save();
				editDialog.dismiss();
			}
		});
		editDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				editDialog.dismiss();
			}
		});
		editDialog.show();
	}
	
}

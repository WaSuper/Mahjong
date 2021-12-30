package com.mahjong.activity.jpn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.activeandroid.query.Select;
import com.mahjong.R;
import com.mahjong.adapter.HistoryDetailAdapter;
import com.mahjong.dialog.FinalRankDialog;
import com.mahjong.model.MjAction;
import com.mahjong.model.MjDetail;
import com.mahjong.model.MjResult;
import com.mahjong.model.Player;
import com.mahjong.tools.AnalysisTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.HorizontalPosition;
import com.mahjong.ui.LineChart;
import com.mahjong.ui.SmartPopupWindow;
import com.mahjong.ui.VerticalPosition;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryDetailActivity extends Activity implements OnClickListener {

	private Context mContext;
	private long mStartTime;
	private MjResult mResult;
	private List<MjDetail> mDetailList;
	
	private TextView mTitle;
	private ImageView mBack;
	private ImageView mMore;
	private ListView mHistoryList;
	private TextView mJuView;
	private TextView mTimeView;
	private HistoryDetailAdapter mAdapter;
	private LineChart mLineChart;
	
	private SmartPopupWindow popupWindow;	
	private AnalysisTool analysisTool;
	private FinalRankDialog frDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_history_detail);
		mContext = this;
		mStartTime = getIntent().getLongExtra(MjResult.Col_StartTime, 0);
		initUI();
	}
	
	private void initUI() {
		mTitle = (TextView) findViewById(R.id.history_detail_title);
		mBack = (ImageView) findViewById(R.id.history_detail_back);
		mMore = (ImageView) findViewById(R.id.history_detail_more);
		mHistoryList = (ListView) findViewById(R.id.history_detail_listview);
		mJuView = (TextView) findViewById(R.id.history_detail_ju);
		mTimeView = (TextView) findViewById(R.id.history_detail_cost_time);
		mLineChart = (LineChart) findViewById(R.id.history_detail_chart);
		
		mTitle.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mMore.setOnClickListener(this);
			
		mResult = new Select().from(MjResult.class)
				.where(MjResult.Col_StartTime + "=?", mStartTime).executeSingle();
		mDetailList = new Select().from(MjDetail.class)
				.where(MjDetail.Col_StartTime + "=?", mStartTime)
				.orderBy(MjDetail.Col_LogTime + " ASC")
				.execute();
		mAdapter = new HistoryDetailAdapter(mContext, mResult, mDetailList);
		mHistoryList.setAdapter(mAdapter);
		String type = "";
		switch (mResult.getGameType()) {
		case 0:
			type = mContext.getString(R.string.battle_one);
			break;
		case 1:
			type = mContext.getString(R.string.battle_two);
			break;
		case 3:
			type = mContext.getString(R.string.battle_four);
			break;
		default:
			break;
		}
		String title = mResult.getTitle();
		if (title == null || title.isEmpty()) {
			title = getString(R.string.detail_log);
		}
		mTitle.setText(title);
		mTitle.setSelected(true);
		mJuView.setText(type);
		String timeText = "";
		int cost_time = (int) ((mResult.getEndTime() - mResult.getStartTime()) / 1000);
		int count = cost_time / 86400; // day:60*60*24
		if (count > 0) timeText += (count + mContext.getString(R.string.day));
		cost_time = cost_time % 86400;
		count = cost_time / 3600; // hour:60*60
		if (count > 0) timeText += (count + mContext.getString(R.string.hour));
		cost_time = cost_time % 3600;
		count = cost_time / 60; // minute:60
		if (count > 0) timeText += (count + mContext.getString(R.string.minute));
		cost_time = cost_time % 60;
		if (cost_time > 0) timeText += (cost_time + mContext.getString(R.string.second));
		else timeText += ("0" + mContext.getString(R.string.second));
		mTimeView.setText(timeText);
		
		analysisTool = new AnalysisTool(mResult, mDetailList);
		mLineChart.setData(analysisTool.getBaseScore(), analysisTool.getDataLen(),
				analysisTool.getScore1st(), analysisTool.getScore2nd(), 
				analysisTool.getScore3rd(), analysisTool.getScore4th());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.history_detail_title:
			editTitle(mResult);
			break;
		case R.id.history_detail_back:
			finish();
			break;
		case R.id.history_detail_more:
			showPopupWindows();
			break;
		case R.id.his_detail_act_copy_to_clipboard:
			if (mDetailList == null || mDetailList.size() == 1) {
				ToastTool.showToast(mContext, R.string.no_data);
				return;
			}
			copyToClickboard();
			popupWindow.dismiss();
			break;
		case R.id.his_detail_act_statistics:
			showFinalRankDialog();
			popupWindow.dismiss();
			break;
		default:
			break;
		}
	}	
	
	private void showFinalRankDialog() {
		if (frDialog == null) {
			frDialog = new FinalRankDialog(mContext);
			List<Player> alls = Player.getAllPlayer();
			Player[] npcs = Player.getNPCPlayers();
			String[] ids = mResult.getIds();
			Player[] players = new Player[4];
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				for (Player p : alls) {
					if (p.getUuid().equals(id)) {
						players[i] = p;
						continue;
					}
				}
				for (Player p : npcs) {
					if (p.getUuid().equals(id)) {
						players[i] = p;
						continue;
					}
				}
			}
			frDialog.setData(players, mResult.getPoints(), 
					mResult.getMas(), mResult.getRanks(), analysisTool, null);
		}			
		frDialog.show();
	}
	
	private void showPopupWindows() {
		if (popupWindow == null) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.layout_history_detail_action, null);
			popupWindow = SmartPopupWindow.Builder
					.build(this, view)
					.setAlpha(0.8f)
					.createPopupWindow();
			Button[] actions = new Button[2];
			actions[0] = (Button) view.findViewById(R.id.his_detail_act_copy_to_clipboard);
			actions[1] = (Button) view.findViewById(R.id.his_detail_act_statistics);
			for (int i = 0; i < actions.length; i++) {
				actions[i].setOnClickListener(this);
			}			
		}
		popupWindow.showAtAnchorView(mMore, VerticalPosition.BELOW, HorizontalPosition.RIGHT);
	}

	private void copyToClickboard() {
		//获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        StringBuffer buffer = new StringBuffer();
		String[] mNames = mResult.getNames();
		String[] mIds = mResult.getIds();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
        String[] winds = new String[4];
    	String[] nums = new String[4];
    	String ju = mContext.getString(R.string.ju);
    	String round = mContext.getString(R.string.round);
		winds[0] = mContext.getString(R.string.east);
		winds[1] = mContext.getString(R.string.south);
		winds[2] = mContext.getString(R.string.west);
		winds[3] = mContext.getString(R.string.north);
		nums[0] = mContext.getString(R.string.one);
		nums[1] = mContext.getString(R.string.two);
		nums[2] = mContext.getString(R.string.three);
		nums[3] = mContext.getString(R.string.four);
        String divider = "----------------------------------------\n";
        String title = mResult.getTitle();
    	if (title != null && !title.isEmpty()) { // database ver.2
    		buffer.append(getString(R.string.title) + "：");
			buffer.append(title + "\n");
		}
		buffer.append(mContext.getString(R.string.battle_count) + "：");
		buffer.append(mJuView.getText().toString() + "\t\t\t\t");
		buffer.append(mContext.getString(R.string.cost_time) + "：");
		buffer.append(mTimeView.getText().toString() + "\n");
		for (MjDetail detail : mDetailList) {
			Date date = new Date(detail.getLogTime());
			buffer.append(simpleDateFormat.format(date) + " ");
			int juWind = detail.getJuCount() / 4;
			int juCount = detail.getJuCount() % 4;
			buffer.append(winds[juWind] + nums[juCount] + ju + " ");
			buffer.append(detail.getRoundCount() + round + "\n");
			int[] changeScores = detail.getChangeScores();
			int[] finalScores = detail.getFinalScores();
			boolean isShowScore = false;
			String content = MjDetail.getDetailText(mContext, detail, winds,
					mNames, mIds);
			switch (detail.getActionId()) {
			case MjAction.ACTION_ZIMO:
			case MjAction.ACTION_BOMB:
			case MjAction.ACTION_HUANGPAILIUJU:
			case MjAction.ACTION_LIUJUMANGUAN:
				isShowScore = true;
				break;
			case MjAction.ACTION_LIZHI:
				int index = MjDetail.findPlayerOrgIndexById(mIds,
						detail.getAction().id0);
				content += (" " + changeScores[index] + "→" + finalScores[index]);
				break;
			default:
				break;
			}
			buffer.append(content + "\n");
			if (isShowScore) {
				for (int i = 0; i < 4; i++) {
					String playerWind = winds[(i + 4 - juCount) % 4];
					buffer.append("(" + playerWind + ")" + mNames[i] + ":\t\t");
					buffer.append(changeScores[i] + "→" + finalScores[i] + "\n");
				}
			}
			if (detail.getLogTime() != mDetailList.get(mDetailList.size() - 1).getLogTime()) {
				buffer.append(divider);				
			}
		}		
        ClipData mClipData = ClipData.newPlainText("Mahjong", buffer.toString());
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastTool.showToast(mContext, R.string.success);
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
				mTitle.setText(text.isEmpty() ? getString(R.string.detail_log) : text);
				setResult(RESULT_OK);
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

package com.mahjong.activity.jpn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.activeandroid.query.Select;
import com.mahjong.R;
import com.mahjong.adapter.RankListAdapter;
import com.mahjong.model.MjResult;
import com.mahjong.model.Player;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.ui.HorizontalPosition;
import com.mahjong.ui.SmartPopupWindow;
import com.mahjong.ui.VerticalPosition;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RankListActivity extends Activity implements OnClickListener {

	private static final String LAST_SELECT_RANK = "LAST_SELECT_RANK";
	
	private Context mContext;
	
	private ImageView mBackView;
	private TextView mMoreView;
	private ListView mListView;
	private RankListAdapter mAdapter;
	private SmartPopupWindow popupWindow;
	
	private Map<String, RankListData> mResultList = new HashMap<String, RankListActivity.RankListData>();
	private int mLastSelectRank;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_ranklist);
		mContext = this;
		initUI();
		initData();
	}
	
	private void initUI() {
		mBackView = (ImageView) findViewById(R.id.ranklist_back);
		mMoreView = (TextView) findViewById(R.id.ranklist_more);
		mListView = (ListView) findViewById(R.id.ranklist_listview);
		mAdapter = new RankListAdapter(this);
		mListView.setAdapter(mAdapter);
		
		mBackView.setOnClickListener(this);
		mMoreView.setOnClickListener(this);
	}

	private void initData() {
		calculatePlayerRank();
		mLastSelectRank = ShareprefenceTool.getInstance().getInt(LAST_SELECT_RANK, mContext, 0);
		switch (mLastSelectRank) {
		case 0:
			showPointRankList();
			break;
		case 1:
			showFistRankList();
			break;
		default:
			break;
		}
	}
	
	private void calculatePlayerRank() {
		List<MjResult> results = new Select().from(MjResult.class).execute();
		List<Player> players = Player.getAllPlayer();
		Player[] npcs = Player.getNPCPlayers();
		Map<String, Player> playerMap = new HashMap<String, Player>();
		for (Player player : players) {
			playerMap.put(player.getUuid(), player);
		}
		for (Player player : npcs) {
			playerMap.put(player.getUuid(), player);
		}
		for (MjResult result : results) {
			String[] uuids = result.getIds();
			String[] names = result.getNames();
			float[] mapoints = result.getMas();
			int[] ranks = result.getRanks();
			for (int i = 0; i < uuids.length; i++) {
				String uuid = uuids[i];
				RankListData data = mResultList.get(uuid);
				if (data == null) {
					Player player = playerMap.get(uuid);
					if (player == null) {
						player = new Player(uuid, names[i], names[i], 'M', "", "");
					}
					data = new RankListData(player);
					mResultList.put(uuid, data);
				}
				data.pointData += mapoints[i];
				if (ranks[i] == 1) data.firstCount++;
				data.gameCount++;
			}
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ranklist_back:
			finish();
			break;
		case R.id.ranklist_more:
			showPopupWindows();
			break;
		case R.id.rank_act_rank_point:
			mLastSelectRank = 0;
			ShareprefenceTool.getInstance().setInt(LAST_SELECT_RANK, mLastSelectRank, mContext);
			showPointRankList();
			popupWindow.dismiss();
			break;
		case R.id.rank_act_rank_first:
			mLastSelectRank = 1;
			ShareprefenceTool.getInstance().setInt(LAST_SELECT_RANK, mLastSelectRank, mContext);
			showFistRankList();
			popupWindow.dismiss();
			break;
		default:
			break;
		}
	}
	
	private void showPopupWindows() {
		if (popupWindow == null) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.layout_rank_action, null);
			popupWindow = SmartPopupWindow.Builder
					.build(this, view)
					.setAlpha(0.8f)
					.createPopupWindow();
			Button[] actions = new Button[2];
			actions[0] = (Button) view.findViewById(R.id.rank_act_rank_point);
			actions[1] = (Button) view.findViewById(R.id.rank_act_rank_first);
			for (int i = 0; i < actions.length; i++) {
				actions[i].setOnClickListener(this);
			}			
		}
		popupWindow.showAtAnchorView(mMoreView, VerticalPosition.BELOW, HorizontalPosition.RIGHT);
	}
	
	private void showPointRankList() {
		mMoreView.setText(getString(R.string.rank_point));
		if (mResultList.size() == 0) return;
		List<RankListData> pointDatas = new ArrayList<RankListActivity.RankListData>();
		for (RankListData data : mResultList.values()) {
			if (pointDatas.size() == 0) {
				pointDatas.add(data);
				continue;
			}
			boolean isAdd = false;
			for (int i = 0; i < pointDatas.size(); i++) {
				RankListData cmpData = pointDatas.get(i);
				if (cmpData.pointData <= data.pointData) {
					pointDatas.add(i, data);
					isAdd = true;
					break;
				}
			}
			if (!isAdd) {
				pointDatas.add(data);
			}
		}
		int rank = 1;
		float lastPoint = pointDatas.get(0).pointData;
		for (RankListData data : pointDatas) {
			if (data.pointData == lastPoint) {
				data.rank = rank;
			} else if (data.pointData < lastPoint) {
				rank++;
				data.rank = rank;
				lastPoint = data.pointData;
			}
		}
		mAdapter.setData(mLastSelectRank, pointDatas);
	}
	
	private void showFistRankList() {
		mMoreView.setText(getString(R.string.rank_first));	
		if (mResultList.size() == 0) return;	
		List<RankListData> firstDatas = new ArrayList<RankListActivity.RankListData>();
		for (RankListData data : mResultList.values()) {
			if (firstDatas.size() == 0) {
				firstDatas.add(data);
				continue;
			}
			boolean isAdd = false;
			for (int i = 0; i < firstDatas.size(); i++) {
				RankListData cmpData = firstDatas.get(i);
				if (cmpData.firstCount * data.gameCount <= data.firstCount * cmpData.gameCount) {
					firstDatas.add(i, data);
					isAdd = true;
					break;
				}
			}
			if (!isAdd) {
				firstDatas.add(data);
			}
		}
		int rank = 1;
		float lastPercent = (float) firstDatas.get(0).firstCount / (float) firstDatas.get(0).gameCount;
		for (RankListData data : firstDatas) {
			float curPercent = (float) data.firstCount / (float) data.gameCount;
			if (curPercent == lastPercent) {
				data.rank = rank;
			} else if (curPercent < lastPercent) {
				rank++;
				data.rank = rank;
				lastPercent = curPercent;
			}
		}
		mAdapter.setData(mLastSelectRank, firstDatas);
	}
	
	public static class RankListData {
		public Player player;
		public int rank;
		public float pointData;
		public int firstCount;
		public int gameCount;
		
		public RankListData(Player player) {
			this.player = player;
			this.pointData = 0;
			this.firstCount = 0;
			this.gameCount = 0;
		}
	}
	
}

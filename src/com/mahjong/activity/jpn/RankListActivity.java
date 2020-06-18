package com.mahjong.activity.jpn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.mahjong.R;
import com.mahjong.adapter.RankListAdapter;
import com.mahjong.model.MjAction;
import com.mahjong.model.MjDetail;
import com.mahjong.model.MjResult;
import com.mahjong.model.Player;
import com.mahjong.model.RankItem;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.ui.HorizontalPosition;
import com.mahjong.ui.SmartPopupWindow;
import com.mahjong.ui.VerticalPosition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RankListActivity extends Activity 
		implements OnClickListener, OnItemClickListener {

	private static final String LAST_SELECT_RANK = "LAST_SELECT_RANK";
	
	private Context mContext;
	private Handler mHandler;
	
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
		mHandler = new Handler();
		initUI();
		checkUpdateData();
	}
	
	private void initUI() {
		mBackView = (ImageView) findViewById(R.id.ranklist_back);
		mMoreView = (TextView) findViewById(R.id.ranklist_more);
		mListView = (ListView) findViewById(R.id.ranklist_listview);
		mAdapter = new RankListAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		mBackView.setOnClickListener(this);
		mMoreView.setOnClickListener(this);
	}
	
	private void checkUpdateData() {
		boolean isNeedUpdate = ShareprefenceTool.getInstance()
				.getBoolean(RankItem.IS_NEED_UPDATE, mContext, true);
		if (isNeedUpdate) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					calcPlayerRankData();
				}
			}).start();
		} else {
			List<RankItem> items = RankItem.getAllRankItem();
			Map<String, Player> playerMap = getPlayersWithNPC();
			for (RankItem item : items) {
				RankListData data = new RankListData(playerMap.get(item.getPlayerId()), item);
				mResultList.put(item.getPlayerId(), data);
			}
			showData();
		}
	}

	private void calcPlayerRankData() {
		ActiveAndroid.beginTransaction();
		try {
			RankItem.resetTable();
			List<MjResult> results = new Select().from(MjResult.class)
					.orderBy(MjResult.Col_StartTime + " DESC").execute();
			Map<String, Player> playerMap = getPlayersWithNPC();
			for (MjResult result : results) {
				RankItem[] rankItems = new RankItem[4];
				String[] uuids = result.getIds();
				String[] names = result.getNames();
				float[] mapoints = result.getMas();
				int[] ranks = result.getRanks();
				int[] points = result.getPoints();
				for (int i = 0; i < uuids.length; i++) {
					String uuid = uuids[i];
					RankListData data = mResultList.get(uuid);
					if (data == null) {
						Player player = playerMap.get(uuid);
						if (player == null) {
							player = new Player(uuid, names[i], names[i], 'M', "", "");
						}
						RankItem rankItem = new RankItem(uuid);
						data = new RankListData(player, rankItem);
						mResultList.put(uuid, data);
						rankItems[i] = rankItem;
					} else {
						rankItems[i] = data.rankItem;
					}
					data.rankItem.addResult(mapoints[i], ranks[i], points[i]);
				}
				List<MjDetail> details = new Select().from(MjDetail.class)
						.where(MjDetail.Col_StartTime + "=?", result.getStartTime())
						.orderBy(MjDetail.Col_LogTime + " ASC").execute();
				int roundCount = 1;
				long lastJuCount = 0;
				long lastRoundCount = 0;
				int[] lizhiCounts = {0, 0, 0, 0};
				int[] hepaiCounts = {0, 0, 0, 0};
				int[] zimoCounts = {0, 0, 0, 0};
				int[] bombCounts = {0, 0, 0, 0};
				int[] bankerCounts = {0, 0, 0, 0};
				int[] maxFans = {0, 0, 0, 0};
				int[] maxFus = {0, 0, 0, 0};
				String[] maxSepctrums = {"", "", "", ""};
				long[] maxStartTimes = {0, 0, 0, 0};
				long[] maxLogTimes = {0, 0, 0, 0};
				if (details != null && details.size() > 0) {
					for (int i = 0; i < details.size(); i++) {
						MjDetail detail = details.get(i);
						MjAction action = detail.getAction();
						// 记录最大连庄数
						int ju = detail.getJuCount() % 4;
						int round = detail.getRoundCount();
						bankerCounts[ju] = Math.max(bankerCounts[ju], round);
						// 记录总场数（减除立直）
						if (detail.getJuCount() != lastJuCount || detail.getRoundCount() != lastRoundCount) {
							roundCount++;
							lastJuCount = detail.getJuCount();
							lastRoundCount = detail.getRoundCount();						
						}
						int index;
						switch (detail.getActionId()) {
						case MjAction.ACTION_LIZHI:
							index = searchId2Index(uuids, action.id0);
							if (index != -1) { lizhiCounts[index]++; } // 记录立直数
							break;
						case MjAction.ACTION_ZIMO:
							index = searchId2Index(uuids, action.id0);
							if (index != -1) {
								zimoCounts[index]++; // 记录自摸数
								hepaiCounts[index]++; // 记录和牌数
								// 记录最大牌谱
								checkLogMaxFan(action.fan0, action.fu0, action.spt0, 
										detail, index, maxFans, maxFus, maxSepctrums, 
										maxStartTimes, maxLogTimes);
							}
							break;
						case MjAction.ACTION_BOMB:
							index = searchId2Index(uuids, action.id3);
							if (index != -1) { bombCounts[index]++; } // 记录放铳数
							index = searchId2Index(uuids, action.id0);
							if (index != -1) {
								hepaiCounts[index]++; // 记录和牌数
								// 记录最大牌谱
								checkLogMaxFan(action.fan0, action.fu0, action.spt0, 
										detail, index, maxFans, maxFus, maxSepctrums, 
										maxStartTimes, maxLogTimes);
							}
							index = searchId2Index(uuids, action.id1);
							if (index != -1) {
								hepaiCounts[index]++; // 记录和牌数
								// 记录最大牌谱
								checkLogMaxFan(action.fan1, action.fu1, action.spt1, 
										detail, index, maxFans, maxFus, maxSepctrums, 
										maxStartTimes, maxLogTimes);
							}
							index = searchId2Index(uuids, action.id2);
							if (index != -1) {
								hepaiCounts[index]++; // 记录和牌数
								// 记录最大牌谱
								checkLogMaxFan(action.fan2, action.fu2, action.spt2, 
										detail, index, maxFans, maxFus, maxSepctrums, 
										maxStartTimes, maxLogTimes);
							}
							break;
						default:
							break;
						}
					}
				}				
				for (int i = 0; i < rankItems.length; i++) {
					rankItems[i].addDetail(roundCount, lizhiCounts[i], hepaiCounts[i], 
							zimoCounts[i], bombCounts[i], bankerCounts[i], 
							maxFans[i], maxFus[i], maxSepctrums[i], 
							maxStartTimes[i], maxLogTimes[i]);
				}
			}			
			for (RankListData data : mResultList.values()) {
				data.rankItem.save();
			}
			ActiveAndroid.setTransactionSuccessful();
			ShareprefenceTool.getInstance().setBoolean(RankItem.IS_NEED_UPDATE, false, mContext);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ActiveAndroid.endTransaction();
		}
		// update ui
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				showData();
			}
		});
	}
	
	private Map<String, Player> getPlayersWithNPC() {
		List<Player> players = Player.getAllPlayer();
		Player[] npcs = Player.getNPCPlayers();
		Map<String, Player> playerMap = new HashMap<String, Player>();
		for (Player player : players) {
			playerMap.put(player.getUuid(), player);
		}
		for (Player player : npcs) {
			playerMap.put(player.getUuid(), player);
		}
		return playerMap;
	}
	
	private void checkLogMaxFan(int fan, int fu, String spt, MjDetail detail,
			int index, int[] maxFans, int[] maxFus, String[] maxSepctrums,
			long[] maxStartTimes, long[] maxLogTimes) {
		boolean isLogData = RankItem.checkSaveMaxFan(fan, fu, 
				maxFans[index], maxFus[index]);
		if (isLogData) {
			maxFans[index] = fan;
			maxFus[index] = fu;
			maxSepctrums[index] = spt;
			maxStartTimes[index] = detail.getStartTime();
			maxLogTimes[index] = detail.getLogTime();
		}		
	}
	
	private int searchId2Index(String[] uuids, String id) {
		for (int i = 0; i < uuids.length; i++) {
			if (uuids[i].equals(id)) {
				return i;
			}
		}
		return -1;
	}
	
	private void showData() {
		mLastSelectRank = ShareprefenceTool.getInstance().getInt(LAST_SELECT_RANK, mContext, 0);
		switch (mLastSelectRank) {
		case 0:
			showPointRankList();
			break;
		case 1:
			showFirstRankList();
			break;
		default:
			break;
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
			showFirstRankList();
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
				if (cmpData.rankItem.getTotalPoint() <= data.rankItem.getTotalPoint()) {
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
		double lastPoint = pointDatas.get(0).rankItem.getTotalPoint();
		for (RankListData data : pointDatas) {
			if (data.rankItem.getTotalPoint() == lastPoint) {
				data.rank = rank;
			} else if (data.rankItem.getTotalPoint() < lastPoint) {
				rank++;
				data.rank = rank;
				lastPoint = data.rankItem.getTotalPoint();
			}
		}
		mAdapter.setData(mLastSelectRank, pointDatas);
	}
	
	private void showFirstRankList() {
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
				if (cmpData.rankItem.getRank1Count() * data.rankItem.getBattleCount() 
						<= data.rankItem.getRank1Count() * cmpData.rankItem.getBattleCount() ) {
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
		double lastPercent = firstDatas.get(0).rankItem.getRank1Percent();
		for (RankListData data : firstDatas) {
			double curPercent = data.rankItem.getRank1Percent();
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
		public RankItem rankItem;
		public int rank;
		
		public RankListData(Player player, RankItem rankItem) {
			this.player = player;
			this.rankItem = rankItem;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		RankListData item = (RankListData) mAdapter.getItem(position);
		if (item != null) {
			Intent intent = new Intent(RankListActivity.this, RankListDetailActivity.class);
			intent.putExtra(Player.Col_Uuid, item.player.getUuid());
			startActivity(intent);
		}	
	}
	
}

package com.mahjong.model;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.mahjong.R;

@Table(name = "MjDetail")
public class MjDetail extends Model {
	
	public static final String Name =  "MjDetail";

	public static final String Col_StartTime		= "StartTime";
	public static final String Col_LogTime 			= "LogTime";
	public static final String Col_JuCount 			= "JuCount";
	public static final String Col_RoundCount 		= "RoundCount";
	public static final String Col_LizhiCount 		= "LizhiCount";
	public static final String Col_PointChangeEast 	= "PointChangeEast";
	public static final String Col_PointFinalEast 	= "PointFinalEast";
	public static final String Col_PointChangeSouth = "PointChangeSouth";
	public static final String Col_PointFinalSouth 	= "PointFinalSouth";
	public static final String Col_PointChangeWest 	= "PointChangeWest";
	public static final String Col_PointFinalWest 	= "PointFinalWest";
	public static final String Col_PointChangeNorth = "PointChangeNorth";
	public static final String Col_PointFinalNorth 	= "PointFinalNorth";
	public static final String Col_ActionId 		= "ActionId";
	public static final String Col_ActionText 		= "ActionText";
	public static final String Col_DoraOut 			= "DoraOut";
	public static final String Col_DoraIn 			= "DoraIn";
	
	public static final String[] Columns = {Col_StartTime, Col_LogTime, Col_JuCount, Col_RoundCount, Col_LizhiCount,
		Col_PointChangeEast, Col_PointFinalEast, Col_PointChangeSouth, Col_PointFinalSouth,
		Col_PointChangeWest, Col_PointFinalWest, Col_PointChangeNorth, Col_PointFinalNorth,
		Col_ActionId, Col_ActionText, Col_DoraOut, Col_DoraIn};
	
	@Column(name = "StartTime")
	private long start_time;		// 总开始时间
	
	@Column(name = "LogTime")
	private long log_time;			// 当前操作记录时间
	
	@Column(name = "JuCount")
	private int ju_count;			// 局数
	
	@Column(name = "RoundCount")
	private int round_count;		// 本场数
	
	@Column(name = "LizhiCount")
	private int lizhi_count;		// 场上立直棒数
	
	@Column(name = "PointChangeEast")
	private int point_change_east;	// 东位玩家调整分值
	
	@Column(name = "PointFinalEast")
	private int point_final_east;	// 东位玩家最终分值
	
	@Column(name = "PointChangeSouth")
	private int point_change_south;	// 南位玩家调整分值
	
	@Column(name = "PointFinalSouth")
	private int point_final_south;	// 南位玩家最终分值
	
	@Column(name = "PointChangeWest")
	private int point_change_west;	// 西位玩家调整分值
	
	@Column(name = "PointFinalWest")
	private int point_final_west;	// 西位玩家最终分值
	
	@Column(name = "PointChangeNorth")
	private int point_change_north;	// 北位玩家调整分值
	
	@Column(name = "PointFinalNorth")
	private int point_final_north;	// 北位玩家最终分值

	@Column(name = "ActionId")
	private int action_id;			// 动作id
	
	@Column(name = "ActionText")
	private String action_text;		// 动作字符串
	
	@Column(name = "DoraOut")
	private String dora_out;		// 宝牌指示牌
	
	@Column(name = "DoraIn")
	private String dora_In;			// 里宝牌指示牌
	
	private MjAction mjAction;		// 动作对象
	
	public MjDetail() {
		super();
	}
	
	public MjDetail(long start_time, long logTime, int juCount, int roundCount, int lizhiCount,
			int changeEast, int finalEast, int changeSouth, int finalSouth,
			int changeWest, int finalWest, int changeNorth, int finalNorth,
			String doraOut, String doraIn, MjAction action) {
		super();
		this.start_time = start_time;
		this.log_time = logTime;
		this.ju_count = juCount;
		this.round_count = roundCount;
		this.lizhi_count = lizhiCount;
		this.point_change_east = changeEast;
		this.point_final_east = finalEast;
		this.point_change_south = changeSouth;
		this.point_final_south = finalSouth;
		this.point_change_west = changeWest;
		this.point_final_west = finalWest;
		this.point_change_north = changeNorth;
		this.point_final_north = finalNorth;
		this.dora_out = doraOut;
		this.dora_In = doraIn;
		this.action_id = action.getActionID();
		this.action_text = action.toJsonString();
		this.mjAction = action;
	}
	
	public MjDetail(long start_time, long logTime, int juCount, int roundCount, int lizhiCount,
			int changeEast, int finalEast, int changeSouth, int finalSouth,
			int changeWest, int finalWest, int changeNorth, int finalNorth,
			String doraOut, String doraIn, int actionId, String action) {
		super();
		this.start_time = start_time;
		this.log_time = logTime;
		this.ju_count = juCount;
		this.round_count = roundCount;
		this.lizhi_count = lizhiCount;
		this.point_change_east = changeEast;
		this.point_final_east = finalEast;
		this.point_change_south = changeSouth;
		this.point_final_south = finalSouth;
		this.point_change_west = changeWest;
		this.point_final_west = finalWest;
		this.point_change_north = changeNorth;
		this.point_final_north = finalNorth;
		this.dora_out = doraOut;
		this.dora_In = doraIn;
		this.action_id = actionId;
		this.action_text = action;
	}
		
	public void transAction() {
		mjAction = MjAction.fromJsonString(action_id, action_text);
	}
	
	public MjAction getAction() {
		if (mjAction == null) {
			transAction();
		}
		return mjAction;
	}
	
	public int getActionId() {
		return action_id;
	}
	
	public String getActionText() {
		if (action_text == null) {
			action_text = mjAction.toJsonString();
		}
		return action_text;
	}
	
	public int[] getChangeScores() {
		return new int[] {
				point_change_east, point_change_south, 
				point_change_west, point_change_north};
	}
	
	public int getChangeEast() {
		return point_change_east;
	}
	
	public int getChangeSouth() {
		return point_change_south;
	}
	
	public int getChangeWest() {
		return point_change_west;
	}
	
	public int getChangeNorth() {
		return point_change_north;
	}
	
	public int[] getFinalScores() {
		return new int[] {
				point_final_east, point_final_south, 
				point_final_west, point_final_north};
	}
	
	public int getFinalEast() {
		return point_final_east;
	}
	
	public int getFinalSouth() {
		return point_final_south;
	}
	
	public int getFinalWest() {
		return point_final_west;
	}
	
	public int getFinalNorth() {
		return point_final_north;
	}
	
	public int[] getOriginalScores() {
		return new int[] {
				point_final_east - point_change_east, 
				point_final_south - point_change_south, 
				point_final_west - point_change_west, 
				point_final_north - point_change_north};
	}
	
	public long getStartTime() {
		return start_time;
	}
	
	public long getLogTime() {
		return log_time;
	}
	
	public int getJuCount() {
		return ju_count;
	}
	
	public int getRoundCount() {
		return round_count;
	}
	
	public int getLizhiCount() {
		return lizhi_count;
	}
	
	public boolean isLizhi() {
		return (action_id == MjAction.ACTION_LIZHI);
	}
	
	public String getDoraOut() {
		return dora_out;
	}
	
	public String getDoraIn() {
		return dora_In;
	}
	
	public void changePlayer(String oldId, String newId) {
		MjAction action = getAction();
		action.changePlayer(oldId, newId);
		action_text = null;
		getActionText();
	}
	
	/**
	 * 将操作转为文本
	 * 
	 * @param context
	 * @param detail
	 * @param winds
	 * @return
	 */
	public static String getDetailText(Context context, MjDetail detail, String[] winds,
			String[] names, String[] ids) {
		int index;
		MjAction action = detail.getAction();
		StringBuilder builder = new StringBuilder();
		switch (detail.getActionId()) {
		case MjAction.ACTION_LIZHI:
			index = findPlayerOrgIndexById(ids, action.id0);
			builder.append(names[index] + " ");
			builder.append(action.tag1 == 0 ? context.getString(R.string.lizhi)
					: context.getString(R.string.double_lizhi));
			break;
		case MjAction.ACTION_ZIMO:
			index = findPlayerOrgIndexById(ids, action.id0);
			builder.append(names[index] + " ");
			builder.append(context.getString(R.string.zimo) + " ");
			builder.append(getFanString(context, action.fan0, action.fu0));
			break;
		case MjAction.ACTION_BOMB:
			index = findPlayerOrgIndexById(ids, action.id3);
			builder.append(names[index] + " ");
			builder.append(context.getString(R.string.bomb) + " ");
			int winCount = action.tag1;
			if (winCount > 0) {
				index = findPlayerOrgIndexById(ids, action.id0);
				builder.append(names[index]);
				builder.append("(" + getFanString(context, action.fan0, action.fu0) + ")");
			}
			if (winCount > 1) {
				index = findPlayerOrgIndexById(ids, action.id1);
				builder.append(", " + names[index]);
				builder.append("(" + getFanString(context, action.fan1, action.fu1) + ")");
			}
			if (winCount > 2) {
				index = findPlayerOrgIndexById(ids, action.id2);
				builder.append(", " + names[index]);
				builder.append("(" + getFanString(context, action.fan2, action.fu2) + ")");
			}
			break;
		case MjAction.ACTION_HUANGPAILIUJU:
			builder.append(context.getString(R.string.huangpailiuju));
			break;
		case MjAction.ACTION_SIFENGLIANDA:
			builder.append(context.getString(R.string.SiFengLianDa));
			builder.append(" " + winds[action.tag1]);
			break;
		case MjAction.ACTION_SIGANGSANLE:
			builder.append(context.getString(R.string.SiGangSanLe));
			break;
		case MjAction.ACTION_JIUPAIJIUZHONG:
			builder.append(context.getString(R.string.JiuPaiJiuZhong));
			index = findPlayerOrgIndexById(ids, action.id0);
			builder.append(" " +names[index]);
			break;
		case MjAction.ACTION_SIJIALIZHI:
			builder.append(context.getString(R.string.SiJiaLiZhi));
			break;
		case MjAction.ACTION_SANJIAHELIAO:
			builder.append(context.getString(R.string.SanJiaHeLiao));
			index = findPlayerOrgIndexById(ids, action.id0);
			builder.append(" " +names[index]);
			index = findPlayerOrgIndexById(ids, action.id1);
			builder.append(", " +names[index]);
			index = findPlayerOrgIndexById(ids, action.id2);
			builder.append(", " +names[index]);
			break;
		case MjAction.ACTION_LIUJUMANGUAN:
			builder.append(context.getString(R.string.LiuJuManGuan));
			int count = action.tag1;
			if (count > 0) {
				index = findPlayerOrgIndexById(ids, action.id0);
				builder.append(" " + names[index]);
			}
			if (count > 1) {
				index = findPlayerOrgIndexById(ids, action.id1);
				builder.append(", " + names[index]);
			}
			if (count > 2) {
				index = findPlayerOrgIndexById(ids, action.id2);
				builder.append(", " + names[index]);
			}
			if (count > 3) {
				index = findPlayerOrgIndexById(ids, action.id3);
				builder.append(", " + names[index]);
			}
			break;
		case MjAction.ACTION_CHANGESCORE:
			builder.append(context.getString(R.string.change_score));
			break;
		default:
			break;
		}
		return builder.toString();
	}
	
	/**
	 * 根据玩家id查找玩家原始风位
	 * 
	 * @param id
	 * @return
	 */
	public static int findPlayerOrgIndexById(String[] ids, String id) {
		int index  = 0;
		for (int i = 0; i < ids.length; i++) {
			if (ids[i].equals(id)) {
				index = i;
				break;
			}
		}		
		return index;
	}
	
	/**
	 * 将番转为文本
	 * 
	 * @param context
	 * @param fan
	 * @param fu
	 * @return
	 */	
	public static String getFanString(Context context, int fan, int fu) {
		if (fan < 0) {
			if (fan == -1) {
				return context.getString(R.string.fan_yiman);
			} else {
				return -fan + context.getString(R.string.yiman_times);
			}
		} else if (fan < 5) {
			return fan + context.getString(R.string.fan) 
					+ fu + context.getString(R.string.fu);
		} else if (fan == 5) {
			return context.getString(R.string.fan_manguan);
		} else if (fan == 6 || fan == 7) {
			return context.getString(R.string.tiaoman);
		} else if (fan > 7 && fan < 11) {
			return context.getString(R.string.beiman);
		} else if (fan == 11 || fan == 12) {
			return context.getString(R.string.sanbeiman);
		} else {
			int yiman = fan / 13;
			if (yiman == 1) {
				return context.getString(R.string.accumulate_yiman);
			} else {
				return yiman + context.getString(R.string.accumulate_yiman_times);
			}			
		}
	}
	
}

package com.mahjong.model;

import org.json.JSONException;
import org.json.JSONObject;


public class MjAction {
	
	public static final String Name =  "MjAction";
	
	public static final int ACTION_LIZHI 			= 1;	// 立直
	public static final int ACTION_ZIMO 			= 2;	// 自摸
	public static final int ACTION_BOMB 			= 3;	// 放铳
	public static final int ACTION_HUANGPAILIUJU 	= 4;	// 荒牌流局
	public static final int ACTION_SIFENGLIANDA 	= 5;	// 四风连打
	public static final int ACTION_SIGANGSANLE 		= 6;	// 四杠散了
	public static final int ACTION_JIUPAIJIUZHONG 	= 7;	// 九牌九种
	public static final int ACTION_SIJIALIZHI 		= 8;	// 四家立直
	public static final int ACTION_SANJIAHELIAO 	= 9;	// 三家和了
	public static final int ACTION_LIUJUMANGUAN 	= 10;	// 流局满贯
	public static final int ACTION_CHANGESCORE 		= 11;	// 分数调整
	public static final int ACTION_FINAL_LIZHI		= 12;	// 终局立直棒分配
	
	public int actionId;		// 动作id
	public String id0;			// 玩家0
	public String spt0;			// 玩家0牌谱
	public int fan0;			// 玩家0番数
	public int fu0;				// 玩家0符数
	public int env0;			// 玩家0环境
	public String id1;			// 玩家1
	public String spt1;			// 玩家1牌谱
	public int fan1;			// 玩家1番数
	public int fu1;				// 玩家1符数
	public int env1;			// 玩家1环境
	public String id2;			// 玩家2
	public String spt2;			// 玩家2牌谱
	public int fan2;			// 玩家2番数
	public int fu2;				// 玩家2符数
	public int env2;			// 玩家2环境
	public String id3;			// 玩家3
	public int tag1;			// 选项1
	public int tag2;			// 选项2
	public String baoId0;		// 包牌玩家id0
	public String baoId1;		// 包牌玩家id1
	public String baoId2;		// 包牌玩家id2
	public String spYakus;		// 特殊役
	
	public MjAction(int actionId) {
		this.actionId = actionId;
	}
	
	/**
	 * 立直
	 * 
	 * @param lizhiId 立直玩家id
	 * @return
	 */
	public static MjAction createLizhiAction(String lizhiId, boolean isWLizhi) {
		MjAction action = new MjAction(ACTION_LIZHI);
		action.id0 = lizhiId;
		action.tag1 = isWLizhi ? 1 : 0;
		return action;
	}
	
	/**
	 * 自摸
	 * 
	 * @param zimoId 自摸玩家id
	 * @param spectrum 牌谱
	 * @param fan 番数
	 * @param fu 符数
	 * @param env 环境变量
	 * @param isBao 是否包牌
	 * @param baoId 包牌玩家id
	 * @return
	 */
	public static MjAction createZimoAction(String zimoId, String spectrum, int fan, int fu, int env,
			boolean isBao, String baoId, String spYakus) {
		MjAction action = new MjAction(ACTION_ZIMO);
		action.id0 = zimoId;
		action.spt0 = spectrum;
		action.fan0 = fan;
		action.fu0 = fu;
		action.env0 = env;
		action.tag1 = isBao ? 1 : 0;
		if (isBao) action.baoId0 = baoId;
		action.spYakus = spYakus;
		return action;
	}
	
	/**
	 * 放铳
	 * 
	 * @param bombId 放铳玩家id
	 * @param winCount 荣和玩家数量
	 * @param id 荣和玩家id数组
	 * @param spectrum 荣和玩家牌谱数组
	 * @param fan 荣和玩家番数数组
	 * @param fu 荣和玩家符数数组
	 * @param env 环境变量数组
	 * @param baoCount 包牌玩家数量
	 * @param baoId 包牌玩家id数组
	 * @return
	 */
	public static MjAction createBombAction(String bombId, int winCount,
			String[] id, String[] spectrum, int[] fan, int[] fu, int[] envs,
			int baoCount, String[] baoId, String spYakus) {
		MjAction action = new MjAction(ACTION_BOMB);
		action.id3 = bombId;
		action.tag1 = winCount;
		action.tag2 = baoCount;
		action.spYakus = spYakus;
		if (winCount > 0) {
			action.id0 = id[0];
			action.spt0 = spectrum[0];
			action.fan0 = fan[0];
			action.fu0 = fu[0];
			action.env0 = envs[0];
			if (baoCount > 0) action.baoId0 = baoId[0];
		} 
		if (winCount > 1) {
			action.id1 = id[1];
			action.spt1 = spectrum[1];
			action.fan1 = fan[1];
			action.fu1 = fu[1];
			action.env1 = envs[1];
			if (baoCount > 0) action.baoId1 = baoId[1];
		} 
		if (winCount > 2) {
			action.id2 = id[2];
			action.spt2 = spectrum[2];
			action.fan2 = fan[2];
			action.fu2 = fu[2];
			action.env2 = envs[2];
			if (baoCount > 0) action.baoId2 = baoId[2];
		}
		return action;
	}
	
	/**
	 * 荒牌流局
	 * 
	 * @param tingCount 听牌玩家数量
	 * @param tingIds 听牌玩家id
	 * @param roundChange 本场数变化值
	 * @return
	 */
	public static MjAction createHPLJAction(int tingCount, String[] tingIds, int roundChange) {
		MjAction action = new MjAction(ACTION_HUANGPAILIUJU);
		action.tag1 = tingCount;
		action.tag2 = roundChange;
		if (tingCount > 0) action.id0 = tingIds[0];
		if (tingCount > 1) action.id1 = tingIds[1];
		if (tingCount > 2) action.id2 = tingIds[2];
		if (tingCount > 3) action.id3 = tingIds[3];
		return action;
	}
	
	/**
	 * 四风连打
	 * 
	 * @param wind 风牌
	 * @param roundChange 本场数变化值
	 * @return
	 */
	public static MjAction createSFLDAction(int wind, int roundChange) {
		MjAction action = new MjAction(ACTION_SIFENGLIANDA);
		action.tag1 = wind;
		action.tag2 = roundChange;
		return action;
	}
	
	/**
	 * 四杠散了
	 * 
	 * @param gangCount 杠牌玩家数量
	 * @param gangIds 杠牌玩家id
	 * @param roundChange 本场数变化值
	 * @return
	 */
	public static MjAction createSGSLAction(int gangCount, String[] gangIds, int roundChange) {
		MjAction action = new MjAction(ACTION_SIGANGSANLE);
		action.tag1 = gangCount;
		action.tag2 = roundChange;
		action.id0 = gangIds[0];
		action.id1 = gangIds[1];
		if (gangCount > 2) action.id2 = gangIds[2];
		if (gangCount > 3) action.id3 = gangIds[3];
		return action;
	}
	
	/**
	 * 九牌九种
	 * 
	 * @param liuId 流局玩家id
	 * @param roundChange 本场数变化值
	 * @return
	 */
	public static MjAction createJPJZAction(String liuId, int roundChange) {
		MjAction action = new MjAction(ACTION_JIUPAIJIUZHONG);
		action.id0 = liuId;
		action.tag2 = roundChange;
		return action;
	}
	
	/**
	 * 四家立直
	 * 
	 * @param lizhiIds 立直玩家id
	 * @param roundChange 本场数变化值
	 * @return
	 */
	public static MjAction createSJLZAction(String[] lizhiIds, int roundChange) {
		MjAction action = new MjAction(ACTION_SIJIALIZHI);
		action.tag1 = 4;
		action.tag2 = roundChange;
		action.id0 = lizhiIds[0];
		action.id1 = lizhiIds[1];
		action.id2 = lizhiIds[2];
		action.id3 = lizhiIds[3];
		return action;
	}
	
	/**
	 * 三家和了
	 * 
	 * @param heIds 荣和玩家id
	 * @param roundChange 本场数变化值
	 * @return
	 */
	public static MjAction createSJHLAction(String[] heIds, int roundChange) {
		MjAction action = new MjAction(ACTION_SANJIAHELIAO);
		action.tag1 = 3;
		action.tag2 = roundChange;
		action.id0 = heIds[0];
		action.id1 = heIds[1];
		action.id2 = heIds[2];
		return action;
	}
	
	/**
	 * 流局满贯
	 * 
	 * @param count 玩家数量
	 * @param ids 玩家id
	 * @param roundChange 本场数变化值
	 * @return
	 */
	public static MjAction createLJMGAction(int count, String[] ids, int roundChange) {
		MjAction action = new MjAction(ACTION_LIUJUMANGUAN);
		action.tag1 = count;
		action.tag2 = roundChange;
		action.id0 = ids[0];
		if (count > 1) action.id1 = ids[1];
		if (count > 2) action.id2 = ids[2];
		if (count > 3) action.id3 = ids[3];
		return action;
	}
	
	/**
	 * 分数调整
	 * 
	 * @return
	 */
	public static MjAction createChangeScoreAction() {
		MjAction action = new MjAction(ACTION_CHANGESCORE);
		return action;
	}
	
	/**
	 * 终局立直棒调整
	 * 
	 * @param lizhiCount 立直棒数量
	 * @param winId 一位玩家id
	 * @return
	 */
	public static MjAction createFinalLizhiAction(int lizhiCount, String winId) {
		MjAction action = new MjAction(ACTION_FINAL_LIZHI);
		action.id0 = winId;
		action.tag1 = lizhiCount;
		return action;
	}

	/**
	 * 动作字符串转化为动作
	 * 
	 * @param actionId
	 * @param content
	 * @return
	 */
	public static MjAction fromJsonString(int actionId, String content) {
		MjAction action = new MjAction(actionId);		
		try {
			JSONObject jb = new JSONObject(content);
			switch (actionId) {
			case ACTION_LIZHI:
				action.id0 = jb.optString("id0", "");
				break;
			case ACTION_ZIMO:
				action.id0 = jb.optString("id0", "");
				action.spt0 = jb.optString("spt0", "");
				action.fan0 = jb.optInt("fan0", 0);
				action.fu0 = jb.optInt("fu0", 0);
				action.env0 = jb.optInt("env0", 0);
				action.tag1 = jb.optInt("tag1", 0);
				if (action.tag1 > 0) {
					action.baoId0 = jb.optString("baoId0", "");
				}
				action.spYakus = jb.optString("spYakus", "");
				break;
			case ACTION_BOMB:
				action.id3 = jb.optString("id3", "");
				action.tag1 = jb.optInt("tag1", 0);
				action.tag2 = jb.optInt("tag2", 0);
				if (action.tag1 > 0) {
					action.id0 = jb.optString("id0", "");
					action.spt0 = jb.optString("spt0", "");
					action.fan0 = jb.optInt("fan0", 0);
					action.fu0 = jb.optInt("fu0", 0);
					action.env0 = jb.optInt("env0", 0);
					if (action.tag2 > 0) action.baoId0 = jb.optString("baoId0", "");
				} 
				if (action.tag1 > 1) {
					action.id1 = jb.optString("id1", "");
					action.spt1 = jb.optString("spt1", "");
					action.fan1 = jb.optInt("fan1", 0);
					action.fu1 = jb.optInt("fu1", 0);
					action.env1 = jb.optInt("env1", 0);
					if (action.tag2 > 0) action.baoId1 = jb.optString("baoId1", "");
				} 
				if (action.tag1 > 2) {
					action.id2 = jb.optString("id2", "");
					action.spt2 = jb.optString("spt2", "");
					action.fan2 = jb.optInt("fan2", 0);
					action.fu2 = jb.optInt("fu2", 0);
					action.env2 = jb.optInt("env2", 0);
					if (action.tag2 > 0) action.baoId2 = jb.optString("baoId2", "");
				}
				action.spYakus = jb.optString("spYakus", "");
				break;
			case ACTION_HUANGPAILIUJU:
				action.tag1 = jb.optInt("tag1", 0);
				action.tag2 = jb.optInt("tag2", 0);
				if (action.tag1 > 0) action.id0 = jb.optString("id0", "");
				if (action.tag1 > 1) action.id1 = jb.optString("id1", "");
				if (action.tag1 > 2) action.id2 = jb.optString("id2", "");
				if (action.tag1 > 3) action.id3 = jb.optString("id3", "");
				break;
			case ACTION_SIFENGLIANDA:
				action.tag1 = jb.optInt("tag1", 0);
				action.tag2 = jb.optInt("tag2", 0);
				break;
			case ACTION_SIGANGSANLE:
				action.tag1 = jb.optInt("tag1", 0);
				action.tag2 = jb.optInt("tag2", 0);
				action.id0 = jb.optString("id0", "");
				action.id1 = jb.optString("id1", "");
				if (action.tag1 > 2) action.id2 = jb.optString("id2", "");
				if (action.tag1 > 3) action.id3 = jb.optString("id3", "");
				break;
			case ACTION_JIUPAIJIUZHONG:
				action.id0 = jb.optString("id0", "");
				action.tag2 = jb.optInt("tag2", 0);
				break;
			case ACTION_SIJIALIZHI:
				action.tag1 = jb.optInt("tag1", 0);
				action.tag2 = jb.optInt("tag2", 0);
				action.id0 = jb.optString("id0", "");
				action.id1 = jb.optString("id1", "");
				action.id2 = jb.optString("id2", "");
				action.id3 = jb.optString("id3", "");
				break;
			case ACTION_SANJIAHELIAO:
				action.tag1 = jb.optInt("tag1", 0);
				action.tag2 = jb.optInt("tag2", 0);
				action.id0 = jb.optString("id0", "");
				action.id1 = jb.optString("id1", "");
				action.id2 = jb.optString("id2", "");
				break;
			case ACTION_LIUJUMANGUAN:
				action.tag1 = jb.optInt("tag1", 0);
				action.tag2 = jb.optInt("tag2", 0);
				action.id0 = jb.optString("id0", "");
				if (action.tag1 > 1) action.id1 = jb.optString("id1", "");
				if (action.tag1 > 2) action.id2 = jb.optString("id2", "");
				if (action.tag1 > 3) action.id3 = jb.optString("id3", "");
				break;
			case ACTION_CHANGESCORE:
				break;
			case ACTION_FINAL_LIZHI:
				action.tag1 = jb.optInt("tag1", 0);
				action.id0 = jb.optString("id0", "");
				break;
			default:
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return action;
	}
	
	/**
	 * 动作转化为字符串
	 * 
	 * @return
	 */
	public String toJsonString() {
		JSONObject jb = new JSONObject();
		try {
			switch (actionId) {
			case ACTION_LIZHI:
				jb.put("id0", id0);
				jb.put("tag1", tag1);
				break;
			case ACTION_ZIMO:
				jb.put("id0", id0);
				jb.put("spt0", spt0 != null ? spt0 : "");
				jb.put("fan0", fan0);
				jb.put("fu0", fu0);
				jb.put("env0", env0);
				jb.put("tag1", tag1);
				if (tag1 > 0) {
					jb.put("baoId0", baoId0);
				}
				jb.put("spYakus", spYakus != null ? spYakus : "");
				break;
			case ACTION_BOMB:
				jb.put("id3", id3);
				jb.put("tag1", tag1);
				jb.put("tag2", tag2);
				if (tag1 > 0) {
					jb.put("id0", id0);
					jb.put("spt0", spt0);
					jb.put("fan0", fan0);
					jb.put("fu0", fu0);
					jb.put("env0", env0);
					if (tag2 > 0) jb.put("baoId0", baoId0);
				} 
				if (tag1 > 1) {
					jb.put("id1", id1);
					jb.put("spt1", spt1);
					jb.put("fan1", fan1);
					jb.put("fu1", fu1);
					jb.put("env1", env1);
					if (tag2 > 0) jb.put("baoId1", baoId1);
				} 
				if (tag1 > 2) {
					jb.put("id2", id2);
					jb.put("spt2", spt2);
					jb.put("fan2", fan2);
					jb.put("fu2", fu2);
					jb.put("env2", env2);
					if (tag2 > 0) jb.put("baoId2", baoId2);
				}
				jb.put("spYakus", spYakus != null ? spYakus : "");
				break;
			case ACTION_HUANGPAILIUJU:
				jb.put("tag1", tag1);
				jb.put("tag2", tag2);
				if (tag1 > 0) jb.put("id0", id0);
				if (tag1 > 1) jb.put("id1", id1);
				if (tag1 > 2) jb.put("id2", id2);
				if (tag1 > 3) jb.put("id3", id3);
				break;
			case ACTION_SIFENGLIANDA:
				jb.put("tag1", tag1);
				jb.put("tag2", tag2);
				break;
			case ACTION_SIGANGSANLE:
				jb.put("tag1", tag1);
				jb.put("tag2", tag2);
				jb.put("id0", id0);
				jb.put("id1", id1);
				if (tag1 > 2) jb.put("id2", id2);
				if (tag1 > 3) jb.put("id3", id3);
				break;
			case ACTION_JIUPAIJIUZHONG:
				jb.put("id0", id0);
				jb.put("tag2", tag2);
				break;
			case ACTION_SIJIALIZHI:
				jb.put("tag1", tag1);
				jb.put("tag2", tag2);
				jb.put("id0", id0);
				jb.put("id1", id1);
				jb.put("id2", id2);
				jb.put("id3", id3);
				break;
			case ACTION_SANJIAHELIAO:
				jb.put("tag1", tag1);
				jb.put("tag2", tag2);
				jb.put("id0", id0);
				jb.put("id1", id1);
				jb.put("id2", id2);
				break;
			case ACTION_LIUJUMANGUAN:
				jb.put("tag1", tag1);
				jb.put("tag2", tag2);
				jb.put("id0", id0);
				if (tag1 > 1) jb.put("id1", id1);
				if (tag1 > 2) jb.put("id2", id2);
				if (tag1 > 3) jb.put("id3", id3);
				break;
			case ACTION_CHANGESCORE:
				break;
			case ACTION_FINAL_LIZHI:
				jb.put("tag1", tag1);
				jb.put("id0", id0);
				break;
			default:
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jb.toString();
	}
	
	public int getActionID() {
		return actionId;
	}
	
	public void changePlayer(String oldId, String newId) {
		if (id0 != null && id0.equals(oldId)) {
			id0 = newId;
		}
		if (id1 != null && id1.equals(oldId)) {
			id1 = newId;
		}
		if (id2 != null && id2.equals(oldId)) {
			id2 = newId;
		}
		if (id3 != null && id3.equals(oldId)) {
			id3 = newId;
		}
		if (baoId0 != null && baoId0.equals(oldId)) {
			baoId0 = newId;
		}
		if (baoId1 != null && baoId1.equals(oldId)) {
			baoId1 = newId;
		}
		if (baoId2 != null && baoId2.equals(oldId)) {
			baoId2 = newId;
		}
	}
	
}

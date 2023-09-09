package com.mahjong.model;

import java.util.List;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "RankItem")
public class RankItem extends Model {
	
	public static final String IS_UPDATE 		= "IS_UPDATE";
	public static final String IS_UPDATE_3P 	= "IS_UPDATE_3P";
	public static final String IS_UPDATE_17S 	= "IS_UPDATE_17S";
	public static final int MAX_RECENT_COUNT 	= 20;
	
	public static final String Col_PlayerId 		= "PlayerId";
	public static final String Col_Spectrum 		= "Spectrum";
	public static final String Col_Fan 				= "Fan";
	public static final String Col_Fu				= "Fu";
	public static final String Col_StartTime		= "StartTime";
	public static final String Col_LogTime			= "LogTime";
	public static final String Col_RecentRanks		= "RecentRanks";
	public static final String Col_RecentFlys		= "RecentFlys";
	public static final String Col_RecentChickens	= "RecentChickens";
	public static final String Col_BattleCount		= "BattleCount";
	public static final String Col_Rank1Count		= "Rank1Count";
	public static final String Col_Rank2Count		= "Rank2Count";
	public static final String Col_Rank3Count		= "Rank3Count";
	public static final String Col_Rank4Count		= "Rank4Count";
	public static final String Col_MaxBanker		= "MaxBanker";
	public static final String Col_TotalPoint		= "TotalPoint";
	public static final String Col_RoundCount 		= "RoundCount";
	public static final String Col_LizhiCount 		= "LizhiCount";
	public static final String Col_HepaiCount 		= "HepaiCount";
	public static final String Col_ZimoCount 		= "ZimoCount";
	public static final String Col_BombCount 		= "BombCount";
	public static final String Col_FlyCount 		= "FlyCount";
	public static final String Col_ChickenCount 	= "ChickenCount";
	public static final String Col_MainType			= "MainType";
	public static final String Col_AverageHepai		= "AverageHepai";
	public static final String Col_AverageBomb		= "AverageBomb";
	
	public static final String[] Columns = {
		Col_PlayerId, Col_Spectrum, Col_Fan, Col_Fu, Col_StartTime, Col_LogTime, 
		Col_RecentRanks, Col_RecentFlys, Col_RecentChickens,
		Col_BattleCount, Col_Rank1Count, Col_Rank2Count, Col_Rank3Count, Col_Rank4Count, 
		Col_MaxBanker, Col_TotalPoint, Col_RoundCount, Col_LizhiCount, Col_HepaiCount, 
		Col_ZimoCount, Col_BombCount, Col_FlyCount, Col_ChickenCount, Col_MainType,
		Col_AverageHepai, Col_AverageBomb
	};
	
	@Column(name = "PlayerId")
	private String player_id;		// 玩家id
	
	@Column(name = "Spectrum")
	private String spectrum; 		// 大牌牌谱
	
	@Column(name = "Fan")
	private int fan; 				// 番数
	
	@Column(name = "Fu")
	private int fu;					// 符数
	
	@Column(name = "StartTime")
	private long start_time;		// 大牌的对局开始时间
	
	@Column(name = "LogTime")
	private long log_time;			// 大牌的对局记录时间
	
	@Column(name = "RecentRanks")
	private String recent_ranks;	// 最近排位（1-4），例：1,4,3,2,3,1
	
	@Column(name = "RecentFlys")
	private String recent_flys;		// 最近起飞，1：起飞，0：正常（对应排位），例：0,1,0,0,0,0
	
	@Column(name = "RecentChickens")
	private String recent_chickens;	// 最近烧鸡，1：烧鸡，0：正常（对应排位），例：0,1,1,0,1,0
	
	@Column(name = "BattleCount")
	private int battle_count;		// 总对局数
	
	@Column(name = "Rank1Count")
	private int rank1_count;		// 排位1次数
	
	@Column(name = "Rank2Count")
	private int rank2_count;		// 排位2次数
	
	@Column(name = "Rank3Count")
	private int rank3_count;		// 排位3次数
	
	@Column(name = "Rank4Count")
	private int rank4_count;		// 排位4次数
	
	@Column(name = "MaxBanker")
	private int max_banker;			// 最大连庄数
	
	@Column(name = "TotalPoint")
	private double total_point;		// 总点数
	
	@Column(name = "RoundCount")
	private int round_count;		// 总对局场次数
	
	@Column(name = "LizhiCount")
	private int lizhi_count;		// 立直次数
	
	@Column(name = "HepaiCount")
	private int hepai_count;		// 和牌次数
	
	@Column(name = "ZimoCount")
	private int zimo_count;			// 自摸次数
	
	@Column(name = "BombCount")
	private int bomb_count;			// 放铳次数
	
	@Column(name = "FlyCount")
	private int fly_count;			// 起飞次数
	
	@Column(name = "ChickenCount")
	private int chicken_count;		// 烧鸡次数
	
	@Column(name = "MainType")
	private int main_type;			// 游戏主类型:0->四麻,1->三麻,2->17步
	
	@Column(name = "AverageHepai")
	private int average_hepai;		// 平均打点

	@Column(name = "AverageBomb")
	private int average_bomb;		// 平均铳点
	
	public RankItem() {
		super();
	}
	
	public RankItem(String playerId, int main_type) {
		super();
		this.player_id = playerId;
		this.spectrum = "";
		this.fan = 0;
		this.fu = 0;
		this.battle_count = 0;
		this.rank1_count = 0;
		this.rank2_count = 0;
		this.rank3_count = 0;
		this.rank4_count = 0;
		this.max_banker = 0;
		this.total_point = 0;
		this.round_count = 0;
		this.lizhi_count = 0;
		this.hepai_count = 0;
		this.zimo_count = 0;
		this.bomb_count = 0;
		this.fly_count = 0;
		this.chicken_count = 0;
		this.recent_ranks = "";
		this.recent_flys = "";
		this.recent_chickens = "";
		this.main_type = main_type;
		this.average_hepai = 0;
		this.average_bomb = 0;
	}
	
	public RankItem(String playerId, String spectrum, int fan, int fu, 
			int[] recentRanks, boolean[] recentFlys, boolean[] recentChickens,
			int battlecount, int rank1Count, int rank2Count, int rank3Count, int rank4Count,
			int maxBanker, double totalPoint, int roundCount, int lizhiCount, int hepaiCount,
			int zimoCount, int bombCount, int flyCount, int chickenCount, int main_type,
			int average_hepai, int average_bomb) {
		super();
		this.player_id = playerId;
		this.spectrum = spectrum;
		this.fan = fan;
		this.fu = fu;
		this.battle_count = battlecount;
		this.rank1_count = rank1Count;
		this.rank2_count = rank2Count;
		this.rank3_count = rank3Count;
		this.rank4_count = rank4Count;
		this.max_banker = maxBanker;
		this.total_point = totalPoint;
		this.round_count = roundCount;
		this.lizhi_count = lizhiCount;
		this.hepai_count = hepaiCount;
		this.zimo_count = zimoCount;
		this.bomb_count = bombCount;
		this.fly_count = flyCount;
		this.chicken_count = chickenCount;
		this.main_type = main_type;
		this.average_hepai = average_hepai;
		this.average_bomb = average_bomb;
		if (recentRanks != null) {
			this.recent_ranks = recentRanks[0] + "";
			for (int i = 1; i < recentRanks.length; i++) {
				this.recent_ranks += ("," + recentRanks[i]);
			}
		}
		if (recentFlys != null) {
			this.recent_flys = (recentFlys[0] ? "1" : "0");
			for (int i = 1; i < recentFlys.length; i++) {
				this.recent_flys += ("," + (recentFlys[i] ? "1" : "0"));
			}
		}
		if (recentChickens != null) {
			this.recent_chickens = (recentChickens[0] ? "1" : "0");
			for (int i = 1; i < recentChickens.length; i++) {
				this.recent_chickens += ("," + (recentChickens[i] ? "1" : "0"));
			}
		}
	}
	
	public static void resetTable(int type) {
		ActiveAndroid.execSQL("delete from RankItem where " + Col_MainType + "=" + type);
		//ActiveAndroid.execSQL("update sqlite_sequence SET seq = 0 where name ='RankItem'");
	}
	
	public static List<RankItem> getAllRankItemByType(int type) {
		List<RankItem> items = new Select().from(RankItem.class).where(Col_MainType + "=?", type).execute();
		return items;
	}
	
	public static RankItem getRankItem(String playerId, int type) {
		RankItem item = new Select().from(RankItem.class).where(Col_PlayerId + "=? AND " + Col_MainType + "=?", 
				playerId, type).executeSingle();
		return item;
	}
	
	public String getPlayerId() {
		return player_id;
	}
	
	public String getSpectrum() {
		return spectrum;
	}
	
	public String getFanString(Context context) {
		return MjDetail.getFanString(context, fan, fu);
	}
	
	public long getStartTime() {
		return start_time;
	}
	
	public long getLogTime() {
		return log_time;
	}
	
	public int[] getRecentRanks() {
		if (recent_ranks == null || recent_ranks.isEmpty()) {
			return new int[] {-1};
		}
		String[] rankStrings = recent_ranks.split(",");
		if (rankStrings == null || rankStrings.length == 0) {
			return new int[] {-1};
		}
		int[] ranks = new int[rankStrings.length];
		for (int i = 0; i < ranks.length; i++) {
			try {
				ranks[i] = Integer.parseInt(rankStrings[i]);
			} catch (Exception e) {
				ranks[i] = -1;
			}			
		}
		return ranks;
	}
	
	public boolean[] getRecentFlys() {
		if (recent_flys == null || recent_flys.isEmpty()) {
			return new boolean[] {false};
		}
		String[] flyStrings = recent_flys.split(",");
		if (flyStrings == null || flyStrings.length == 0) {
			return new boolean[] {false};
		}
		boolean[] flys = new boolean[flyStrings.length];
		for (int i = 0; i < flys.length; i++) {
			flys[i] = flyStrings[i].equals("1");		
		}
		return flys;
	}
	
	public boolean[] getRecentChickens() {
		if (recent_chickens == null || recent_chickens.isEmpty()) {
			return new boolean[] {false};
		}
		String[] chickenStrings = recent_chickens.split(",");
		if (chickenStrings == null || chickenStrings.length == 0) {
			return new boolean[] {false};
		}
		boolean[] chickens = new boolean[chickenStrings.length];
		for (int i = 0; i < chickens.length; i++) {
			chickens[i] = chickenStrings[i].equals("1");		
		}
		return chickens;
	}
	
	public int getBattleCount() {
		return battle_count;
	}
	
	public int getRank1Count() {
		return rank1_count;
	}
	
	public double getRank1Percent() {
		return (double) rank1_count / (double) battle_count;
	}
	
	public double getRank2Percent() {
		return (double) rank2_count / (double) battle_count;
	}
	
	public double getRank3Percent() {
		return (double) rank3_count / (double) battle_count;
	}
	
	public double getRank4Percent() {
		return (double) rank4_count / (double) battle_count;
	}
	
	public double getAverageRank() {
		// 求平均顺位：1*一位率+2*二位率+3*三位率+4*四位率
		return (double) (rank1_count + 2 * rank2_count + 3 * rank3_count + 4 * rank4_count) / (double) battle_count;
	}
	
	public int getMaxBanker() {
		return max_banker;
	}
	
	public double getTotalPoint() {
		 return total_point;
	}
	
	public double getLizhiPercent() {
		return (double) lizhi_count / (double) round_count;
	}
	
	public double getHepaiPercent() {
		return (double) hepai_count / (double) round_count;
	}
	
	public double getZimoPercent() {
		return (double) zimo_count / (double) round_count;
	}
	
	public double getBombPercent() {
		return (double) bomb_count / (double) round_count;
	}
	
	public double getFlyPercent() {
		return (double) fly_count / (double) battle_count;
	}
	
	public double getChickenPercent() {
		return (double) chicken_count / (double) battle_count;
	}
	
	public int getMainType() {
		return main_type;
	}
	
	public void setMainType(int type) {
		this.main_type = type;
	}
	
	public int getAverageHepai() {
		return average_hepai;
	}
	
	public int getAverageBomb() {
		return average_bomb;
	}
	
	public void addResult(float ma, int rank, int point) {
		total_point += ma;
		switch (rank) {
		case 1:
			rank1_count++;
			break;
		case 2:
			rank2_count++;
			break;
		case 3:
			rank3_count++;
			break;
		case 4:
			rank4_count++;
			break;
		default:
			break;
		}
		battle_count++;
		if (point < 0) fly_count++;
		if (battle_count <= MAX_RECENT_COUNT) {
			if (battle_count == 1) {
				recent_ranks = "" + rank;
				recent_flys = point < 0 ? "1" : "0";
			} else {
				recent_ranks = rank + "," + recent_ranks;
				recent_flys = (point < 0 ? "1" : "0") + "," + recent_flys;
			}
		}
	}
	
	public void addDetail(int roundCount, int lizhiCount, int hepaiCount, int zimoCount,
			int bombCount, int bankerCount, int maxFan, int maxFu, String maxSpectrum,
			long maxStartTime, long maxLogTime, int averageHepai, int averageBomb) {
		round_count += roundCount;
		lizhi_count += lizhiCount;
		hepai_count += hepaiCount;
		if (hepaiCount == 0) {
			chicken_count++;
		}
		zimo_count += zimoCount;
		bomb_count += bombCount;
		max_banker = Math.max(max_banker, bankerCount);
		boolean isSave = checkSaveMaxFan(maxFan, maxFu, fan, fu);
		if (isSave) {
			fan = maxFan;
			fu = maxFu;
			spectrum = maxSpectrum;
			start_time = maxStartTime;
			log_time = maxLogTime;
		}
		if (battle_count <= MAX_RECENT_COUNT) {
			if (battle_count == 1) {
				recent_chickens = hepaiCount == 0 ? "1" : "0";
			} else {
				recent_chickens = (hepaiCount == 0 ? "1" : "0") + "," + recent_chickens;
			}
		}
		if (hepai_count > 0) {
			average_hepai = (average_hepai * (hepai_count - hepaiCount)  + averageHepai * hepaiCount) / hepai_count;
		}
		if (bomb_count > 0) {
			average_bomb = (average_bomb * (bomb_count - bombCount) + averageBomb * bombCount) / bomb_count;
		}
		
	}
	
	public static boolean checkSaveMaxFan(int cmpFan, int cmpFu, int orgFan, int orgFu) {
		boolean isLogData = false;
		if (cmpFan == 0) {
			isLogData = false;
		} else if (orgFan == 0) {
			isLogData = true;
		} else if (cmpFan > 0 && orgFan > 0) {
			if (cmpFan > orgFan
					|| (cmpFan == orgFan && cmpFu > orgFu)) {
				isLogData = true;
			}
		} else if (cmpFan < 0 && orgFan < 0) {
			if (cmpFan < orgFan) {
				isLogData = true;
			}
		} else if (cmpFan < 0 && orgFan > 0) {
			int yiman = orgFan / 13;
			if (yiman < -cmpFan) {
				isLogData = true;
			}
		} else if (cmpFan > 0 && orgFan < 0) {
			int yiman = cmpFan / 13;
			if (yiman > -orgFan) {
				isLogData = true;
			}
		}
		return isLogData;
	}
	
}

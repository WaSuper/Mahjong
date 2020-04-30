package com.mahjong.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "MjResult")
public class MjResult extends Model {
	
	public static final String Name =  "MjResult";

	public static final String Col_GameType 	= "GameType";
	public static final String Col_BasePoint 	= "BasePoint";
	public static final String Col_MaPoint 		= "MaPoint";
	public static final String Col_StartTime 	= "StartTime";
	public static final String Col_EndTime 	 	= "EndTime";
	public static final String Col_EastId 		= "EastId";
	public static final String Col_EastName 	= "EastName";
	public static final String Col_EastPoint 	= "EastPoint";
	public static final String Col_EastRank 	= "EastRank";
	public static final String Col_EastRMa 		= "EastMa";	
	public static final String Col_SouthId 		= "SouthId";
	public static final String Col_SouthName 	= "SouthName";
	public static final String Col_SouthPoint 	= "SouthPoint";
	public static final String Col_SouthRank 	= "SouthRank";
	public static final String Col_SouthMa 		= "SouthMa";
	public static final String Col_WestId		= "WestId";
	public static final String Col_WestName 	= "WestName";
	public static final String Col_WestPoint 	= "WestPoint";
	public static final String Col_WestRank 	= "WestRank";
	public static final String Col_WestMa 		= "WestMa";
	public static final String Col_NorthId 		= "NorthId";
	public static final String Col_NorthName 	= "NorthName";
	public static final String Col_NorthPoint 	= "NorthPoint";
	public static final String Col_NorthRank 	= "NorthRank";
	public static final String Col_NorthMa 		= "NorthMa";
	public static final String Col_Title		= "Title";
	public static final String Col_Note			= "Note";
	
	public static final String[] Columns = {Col_GameType, Col_BasePoint, Col_MaPoint, Col_StartTime, Col_EndTime,
		Col_EastId, Col_EastName, Col_EastPoint, Col_EastRank, Col_EastRMa,
		Col_SouthId, Col_SouthName, Col_SouthPoint, Col_SouthRank, Col_SouthMa,
		Col_WestId, Col_WestName, Col_WestPoint, Col_WestRank, Col_WestMa,
		Col_NorthId, Col_NorthName, Col_NorthPoint, Col_NorthRank, Col_NorthMa,
		Col_Title, Col_Note};
	
	@Column(name = "GameType")
	private int game_type;			// 游戏类型:1->东风战，2->半庄战，4->全庄战
	
	@Column(name = "BasePoint")
	private int base_point;			// 基本点数
	
	@Column(name = "MaPoint")
	private String ma_point;		// 马点
	
	@Column(name = "StartTime", unique = true)
	private long start_time;		// 开始时间
	
	@Column(name = "EndTime")
	private long end_time;			// 结束时间
	
	@Column(name = "EastId")
	private String east_id;			// 东风的id

	@Column(name = "EastName")
	private String east_name;		// 东风的名字
	
	@Column(name = "EastPoint")
	private int east_point;			// 东风的点数
	
	@Column(name = "EastRank")
	private int east_rank;			// 东风的排位
	
	@Column(name = "EastMa")
	private float east_ma;			// 东风的马点
	
	@Column(name = "SouthId")
	private String south_id;		// 南风的id

	@Column(name = "SouthName")
	private String south_name;		// 南风的名字
	
	@Column(name = "SouthPoint")
	private int south_point;		// 南风的点数
	
	@Column(name = "SouthRank")
	private int south_rank;			// 南风的排位
	
	@Column(name = "SouthMa")
	private float south_ma;			// 南风的马点
	
	@Column(name = "WestId")
	private String west_id;			// 西风的id

	@Column(name = "WestName")
	private String west_name;		// 西风的名字
	
	@Column(name = "WestPoint")
	private int west_point;			// 西风的点数
	
	@Column(name = "WestRank")
	private int west_rank;			// 西风的排位
	
	@Column(name = "WestMa")
	private float west_ma;			// 西风的马点
	
	@Column(name = "NorthId")
	private String north_id;		// 北风的id

	@Column(name = "NorthName")
	private String north_name;		// 北风的名字
	
	@Column(name = "NorthPoint")
	private int north_point;		// 北风的点数
	
	@Column(name = "NorthRank")
	private int north_rank;			// 北风的排位
	
	@Column(name = "NorthMa")
	private float north_ma;			// 北风的马点
	
	@Column(name = "title")
	private String title;			// 标题
	
	@Column(name = "Note")
	private String note;			// 备注
	
	public MjResult() {
		super();
	}
	
	public MjResult(int game_type, int base_point, int[] ma_points, long start_time, 
			String east_id, String east_name, String south_id, String south_name,
			String west_id, String west_name, String north_id, String north_name) {
		super();
		this.game_type = game_type;
		this.base_point = base_point;
		this.start_time = start_time;
		this.ma_point = ma_points[0] + "," + ma_points[1] + "," + ma_points[2] + "," + ma_points[3];
		this.end_time = -1;
		this.east_id = east_id;
		this.east_name = east_name;
		this.east_point = base_point;
		this.east_rank = 1;
		this.south_id = south_id;
		this.south_name = south_name;
		this.south_point = base_point;
		this.south_rank = 2;
		this.west_id = west_id;
		this.west_name = west_name;
		this.west_point = base_point;
		this.west_rank = 3;
		this.north_id = north_id;
		this.north_name = north_name;
		this.north_point = base_point;
		this.north_rank = 4;
	}
	
	public void setEndGame(long end_time, int[] scores, int[] ranks, float[] mas) {
		this.end_time = end_time;
		this.east_point = scores[0];
		this.south_point = scores[1];
		this.west_point = scores[2];
		this.north_point = scores[3];
		this.east_rank = ranks[0];
		this.south_rank = ranks[1];
		this.west_rank = ranks[2];
		this.north_rank = ranks[3];
		this.east_ma = mas[0];
		this.south_ma = mas[1];
		this.west_ma = mas[2];
		this.north_ma = mas[3];
	}
	
	public int getGameType() {
		return game_type;
	}
	
	public int getBasePoint() {
		return base_point;
	}
	
	public String getMaPointString() {
		return ma_point;
	}
	
	public int[] getMaPoint() {
		int[] result = {15, 5, -5, -15};
		if (ma_point == null || ma_point.length() == 0) {
			return result;
		}
		String[] mas = ma_point.split(",");
		try {
			if (mas.length == 4) {
				for (int i = 0; i < mas.length; i++) {
					result[i] = Integer.parseInt(mas[i]);
				}
			}
		} catch (Exception e) {
			
		}		
		return result;
	}
	
	public long getStartTime() {
		return start_time;
	}
	
	public long getEndTime() {
		return end_time;
	}
	
	public String[] getIds() {
		return new String[] {east_id, south_id, west_id, north_id};
	}
	
	public String[] getNames() {
		return new String[] {east_name, south_name, west_name, north_name};
	}
	
	public int[] getPoints() {
		return new int[] {east_point, south_point, west_point, north_point}; 
	}
	
	public int[] getRanks() {
		return new int[] {east_rank, south_rank, west_rank, north_rank};
	}
	
	public float[] getMas() {
		return new float[] {east_ma, south_ma, west_ma, north_ma};
	}
	
	public String getEastId() {
		return east_id;
	}
	
	public String getEastName() {
		return east_name;
	}
	
	public int getEastPoint() {
		return east_point;
	}
	
	public int getEastRank() {
		return east_rank;
	}
	
	public float getEastMa() {
		return east_ma;
	}
	
	public String getSouthId() {
		return south_id;
	}
	
	public String getSouthName() {
		return south_name;
	}
	
	public int getSouthPoint() {
		return south_point;
	}
	
	public int getSouthRank() {
		return south_rank;
	}
	
	public float getSouthMa() {
		return south_ma;
	}
	
	public String getWestId() {
		return west_id;
	}
	
	public String getWestName() {
		return west_name;
	}
	
	public int getWestPoint() {
		return west_point;
	}
	
	public int getWestRank() {
		return west_rank;
	}
	
	public float getWestMa() {
		return west_ma;
	}
	
	public String getNorthId() {
		return north_id;
	}
	
	public String getNorthName() {
		return north_name;
	}
	
	public int getNorthPoint() {
		return north_point;
	}
	
	public int getNorthRank() {
		return north_rank;
	}
	
	public float getNorthMa() {
		return north_ma;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public void changePlayer(int index, String uuid, String name) {
		switch (index) {
		case 0:
			east_id = uuid;
			east_name = name;
			break;
		case 1:
			south_id = uuid;
			south_name = name;			
			break;
		case 2:
			west_id = uuid;
			west_name = name;			
			break;
		case 3:
			north_id = uuid;
			north_name = name;			
			break;
		default:
			break;
		}
	}
	
}

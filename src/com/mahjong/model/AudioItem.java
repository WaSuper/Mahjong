package com.mahjong.model;

import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "AudioItem")
public class AudioItem extends Model {

	public static final String Name =  "AudioItem";
	
	public static final String Col_PlayerId 	= "PlayerId";
	public static final String Col_Type 		= "Type";
	public static final String Col_FilePath 	= "FilePath";
	public static final String Col_Enable 		= "Enable";
	
	public static final String[] Columns = {
		Col_PlayerId, Col_Type, Col_FilePath, Col_Enable
	};
	
	@Column(name = "PlayerId")
	private String player_id;	// 玩家id
	
	@Column(name = "Type")
	private int type;			// 音效种类
	
	@Column(name = "FilePath")
	private String file_path;	// 文件路径
	
	@Column(name = "Enable")
	private boolean enable;		// 是否播放
	
	public AudioItem() {
		super();
	}
	
	public AudioItem(String playerId, int type, String filePath, boolean enable) {
		super();
		this.player_id = playerId;
		this.type = type;
		this.file_path = filePath;
		this.enable = enable;
	}
	
	public String getPlayerId() {
		return player_id;
	}
	
	public static List<AudioItem> getAllAudioItems() {
		List<AudioItem> list = new Select().from(AudioItem.class).execute();
		return list;
	}
	
	public static List<AudioItem> loadItemsById(String id) {
		List<AudioItem> list = new Select().from(AudioItem.class)
				.where(Col_PlayerId + "=?", id).execute();
		return list;
	}
	
	public int getType() {
		return type;
	}
	
	public String getFilePath() {
		return file_path;
	}
	
	public void setFilePath(String path) {
		this.file_path = path;
	}
	
	public boolean getEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
}

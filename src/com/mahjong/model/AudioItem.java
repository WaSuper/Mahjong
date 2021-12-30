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
	public static final String Col_SoundBoxId 	= "SoundBoxId";
	
	public static final String[] Columns = {
		Col_PlayerId, Col_Type, Col_FilePath, Col_Enable, Col_SoundBoxId
	};
	
	@Column(name = "PlayerId")
	private String player_id;	// 玩家id
	
	@Column(name = "Type")
	private int type;			// 音效种类（详见AudioTool定义）
	
	@Column(name = "FilePath")
	private String file_path;	// 文件路径
	
	@Column(name = "Enable")
	private boolean enable;		// 是否播放
	
	@Column(name = "SoundBoxId") 
	private long soundbox_id;	// 音频包id
	
	public AudioItem() {
		super();
	}
	
	/**
	 * 定义玩家立直BGM(type==1)
	 * 
	 * @param playerId
	 * @param type
	 * @param filePath
	 * @param enable
	 */
	public AudioItem(String playerId, int type, String filePath, boolean enable) {
		super();
		this.player_id = playerId;
		this.type = type;
		this.file_path = filePath;
		this.enable = enable;
		this.soundbox_id = -1;
	}
	
	/**
	 * 定义音频包子项(type>1)
	 * 
	 * @param soundboxId
	 * @param type
	 * @param filePath
	 * @param enable
	 */
	public AudioItem(long soundboxId,int type, String filePath, boolean enable) {
		super();
		this.soundbox_id = soundboxId;
		this.type = type;
		this.file_path = filePath;
		this.enable = enable;
		this.player_id = "";
	}
	
	/**
	 * 定义玩家音频包(type==0)
	 * 
	 * @param playerId
	 * @param soundboxId
	 * @param type
	 * @param name
	 * @param enable
	 */
	public AudioItem(String playerId, long soundboxId, int type, String name, boolean enable) {
		super();
		this.player_id = playerId;
		this.soundbox_id = soundboxId;
		this.type = type;
		this.file_path = name;
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
	
	public static List<AudioItem> loadItemsByType(int type) {
		List<AudioItem> list = new Select().from(AudioItem.class)
				.where(Col_Type + "=?", type).execute();
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
	
	public long getSoundBoxId() {
		return soundbox_id;
	}
	
	public void setSoundBoxId(long id) {
		this.soundbox_id = id;
	}
	
	public static List<AudioItem> loadItemsBySoundBoxId(long id) {
		List<AudioItem> list = new Select().from(AudioItem.class)
				.where(Col_SoundBoxId + "=? AND " + Col_Type + "!=?", id, 0).execute();
		return list;
	}
	
	public static AudioItem loadItemByIdAndType(String playId, int type) {
		AudioItem item = new Select().from(AudioItem.class)
				.where(Col_PlayerId + "=? AND " + Col_Type + "=?", playId, type)
				.executeSingle();
		return item;
	}
	
}

package com.mahjong.model;

import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

@Table(name = "SoundBox")
public class SoundBox extends Model {

	public static final String Col_Uuid 		= "Uuid";
	public static final String Col_Name 		= "Name";
	public static final String Col_DefaultIcon 	= "DefaultIcon";
	public static final String Col_SortIndex 	= "SortIndex";
	public static final String Col_Description 	= "Description";
	
	public static final String[] Columns = {
		Col_Uuid, Col_Name, Col_DefaultIcon, Col_SortIndex, Col_Description
	};
	
	@Column(name = "Uuid", unique = true)
	private long uuid;				// id
	
	@Column(name = "Name")
	private String name; 			// 名字
	
	@Column(name = "DefaultIcon")
	private String default_icon;	// 默认头像
	
	@Column(name = "SortIndex")
	private int sortIndex; 			// 排序索引
	
	@Column(name = "Description")
	private String description;		// 描述	
	
	public SoundBox() {
		super();
	}
	
	public SoundBox(long uuid, String name, String default_icon, int sortIndex) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.default_icon = default_icon;
		this.sortIndex = sortIndex;
	}
	
	public SoundBox(long uuid, String name, String default_icon, int sortIndex, String description) {
		this(uuid, name, default_icon, sortIndex);
		this.description = description;
	}
	
	public long getUuid() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDefaultIcon() {
		return default_icon;
	}
	
	public void setDefaultIcon(String icon) {
		this.default_icon = icon;
	}
	
	public int getIndex() {
		return sortIndex;
	}
	
	public void setIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	
	/**
	 * 获取全部音频包
	 * 
	 * @return
	 */
	public static List<SoundBox> getAllSoundBoxs() {
		List<SoundBox> list = new Select().from(SoundBox.class)
				.orderBy(Character.Col_SortIndex + " ASC").execute();
		return list;
	}

	
	/**
	 * 获取单个音频包
	 * 
	 * @param uuid
	 * @return
	 */
	public static SoundBox getSoundBox(long uuid) {
		SoundBox soundBox = new Select().from(SoundBox.class)
				.where(SoundBox.Col_Uuid + "=?", uuid).executeSingle();
		return soundBox;
	}
	
	/**
	 * 创建音频包
	 * 
	 * @param finalSoundBox 全部音频包的最后一个
	 * @param name 音频包名字
	 * @param default_icon 音频包默认头像
	 * @return
	 */
	public static SoundBox createSoundBox(String name, String default_icon,int sort_index) {
		SoundBox newSoundBox = new SoundBox(System.currentTimeMillis(), 
				name, default_icon, sort_index);
		try {
			newSoundBox.save();
		} catch (Exception e) {
			return null;
		}		
		return newSoundBox;
	}
	
	/**
	 * 删除音频包
	 * 
	 * @param character
	 * @return
	 */
	public static boolean deleteSoundBox(SoundBox soundBox) {
		if (soundBox == null || soundBox.getUuid() < 0) return false;
		boolean result = true;
		ActiveAndroid.beginTransaction();
		try {
			long uuid = soundBox.uuid;
			// 删除子音频包
			new Delete().from(AudioItem.class)
				.where(AudioItem.Col_SoundBoxId + "=?", uuid)
				.execute();
			// 删除音频包
			soundBox.delete();
			ActiveAndroid.setTransactionSuccessful();
		} catch (Exception e) {
			result = false;
		} finally {
			ActiveAndroid.endTransaction();
		}
		return result;
	}
	
	/**
	 * 修改音频包名字
	 * 
	 * @param soundBox
	 * @param name
	 * @return
	 */
	public static boolean modifySoundBoxName(SoundBox soundBox, String name) {
		if (soundBox == null || soundBox.getUuid() < 0) return false;
		soundBox.name = name;
		soundBox.save();
		return true;
	}
	
	/**
	 * 修改音频包头像
	 * 
	 * @param soundBox
	 * @param default_icon
	 * @return
	 */
	public static boolean modifySoundBoxIcon(SoundBox soundBox, String default_icon) {
		if (soundBox == null || soundBox.getUuid() < 0) return false;
		soundBox.default_icon = default_icon;
		soundBox.save();
		return true;
	}

}

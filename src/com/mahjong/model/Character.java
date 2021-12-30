package com.mahjong.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.mahjong.R;

@Table(name = "Character")
public class Character extends Model {

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
	
	public Character() {
		super();
	}
	
	public Character(long uuid, String name, String default_icon, int sortIndex) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.default_icon = default_icon;
		this.sortIndex = sortIndex;
	}
	
	public Character(long uuid, String name, String default_icon, int sortIndex, String description) {
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
	 * 获取全部角色（无默认角色）
	 * 
	 * @return
	 */
	public static List<Character> getAllCharacters() {
		List<Character> list = new Select().from(Character.class)
				.orderBy(Character.Col_SortIndex + " ASC").execute();
		return list;
	}
	
	/**
	 * 获取全部角色（第一个为默认角色）
	 * 
	 * @param context
	 * @return
	 */
	public static List<Character> getAllCharacters(Context context) {
		List<Character> allCharacters = new ArrayList<Character>();
		Character defaultCharacter = new Character(-1, context.getString(R.string.default_name), null, 0);
		allCharacters.add(defaultCharacter);
		List<Character> list = new Select().from(Character.class)
				.orderBy(Character.Col_SortIndex + " ASC").execute();
		allCharacters.addAll(list);
		return allCharacters;
	}
	
	/**
	 * 获取单个角色（第一个为默认角色）
	 * 
	 * @param context
	 * @param uuid
	 * @return
	 */
	public static Character getCharacter(Context context, long uuid) {
		if (uuid == -1) {
			return new Character(-1, context.getString(R.string.default_name), null, 0);
		} else {
			Character character = new Select().from(Character.class)
					.where(Character.Col_Uuid + "=?", uuid).executeSingle();
			if (character == null) {
				return new Character(-2, context.getString(R.string.unknown), null, 0);
			} else {
				return character;
			}
		}
	}
	
	/**
	 * 创建角色
	 * 
	 * @param finalCharacter 全部角色的最后一个
	 * @param name 角色名字
	 * @param default_icon 角色默认头像
	 * @return
	 */
	public static Character createCharacter(Character finalCharacter, String name, String default_icon) {
		Character newCharacter = new Character(System.currentTimeMillis(), 
				name, default_icon, finalCharacter.getIndex() + 1);
		try {
			newCharacter.save();
		} catch (Exception e) {
			return null;
		}		
		return newCharacter;
	}
	
	/**
	 * 删除角色
	 * 
	 * @param character
	 * @return
	 */
	public static boolean deleteCharacter(Character character) {
		if (character == null || character.getUuid() == -1) return false;
		boolean result = true;
		ActiveAndroid.beginTransaction();
		try {
			long uuid = character.uuid;
			// 删除子表情
			new Delete().from(CharacterIcon.class)
				.where(CharacterIcon.Col_CharacterId + "=?", uuid)
				.execute();
			// 删除表情包
			character.delete();
			// 修改相关表情包玩家
			new Update(Player.class).set(Player.Col_CharacterId + "=?", -1)
				.where(Player.Col_CharacterId + "=?", uuid)
				.execute();
			ActiveAndroid.setTransactionSuccessful();
		} catch (Exception e) {
			result = false;
		} finally {
			ActiveAndroid.endTransaction();
		}
		return result;
	}
	
	/**
	 * 修改角色名字
	 * 
	 * @param character
	 * @param name
	 * @return
	 */
	public static boolean modifyCharacterName(Character character, String name) {
		if (character == null || character.getUuid() == -1) return false;
		character.name = name;
		character.save();
		return true;
	}
	
	/**
	 * 修改角色头像
	 * 
	 * @param character
	 * @param default_icon
	 * @return
	 */
	public static boolean modifyCharacterIcon(Character character, String default_icon) {
		if (character == null || character.getUuid() == -1) return false;
		character.default_icon = default_icon;
		character.save();
		new Update(Player.class).set(Player.Col_Icon + "=?", default_icon)
			.where(Player.Col_CharacterId + "=?", character.getUuid())
			.execute();
		return true;
	}
}

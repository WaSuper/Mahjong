package com.mahjong.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

public class CharacterIcon extends Model {

	public static final String Col_CharacterId	= "CharacterId";
	public static final String Col_Path 		= "Path";
	public static final String Col_Rank 		= "Rank";
	public static final String Col_SortIndex 	= "SortIndex";
	public static final String Col_Name 		= "Name";	
	
	@Column(name = "CharacterId")
	private long character_id;		// id
	
	@Column(name = "Path")
	private String path;	// path
	
	@Column(name = "Rank")
	private int rank;		// rank
	
	@Column(name = "SortIndex")
	private int sortIndex;	// index
	
	@Column(name = "Name")
	private String name;	// name
	
	public CharacterIcon() {
		super();
	}
	
	public CharacterIcon(long character_id, String path, int rank, int sortIndex, String name) {
		super();
		this.character_id = character_id;
		this.path = path;
		this.rank = rank;
		this.sortIndex = sortIndex;
		this.name = name;
	}
	
	public long getCharacterId() {
		return character_id;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public int getRank() {
		return rank;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public int getIndex() {
		return sortIndex;
	}
	
	public void setIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public static List<List<CharacterIcon>> getAllCharacterIcons(long uuid) {
		List<CharacterIcon> iconList = new Select().from(CharacterIcon.class)
				.where(CharacterIcon.Col_CharacterId + "=?", uuid).execute();
		List<List<CharacterIcon>> rankLists = new ArrayList<List<CharacterIcon>>();
		List<CharacterIcon> rank1 = new ArrayList<CharacterIcon>();
		List<CharacterIcon> rank2 = new ArrayList<CharacterIcon>();
		List<CharacterIcon> rank3 = new ArrayList<CharacterIcon>();
		List<CharacterIcon> rank4 = new ArrayList<CharacterIcon>();
		for (CharacterIcon icon : iconList) {
			switch (icon.rank) {
			case 1:
				rank1.add(icon);
				break;
			case 2:
				rank2.add(icon);
				break;
			case 3:
				rank3.add(icon);
				break;
			case 4:
				rank4.add(icon);
				break;
			default:
				break;
			}
		}
		Collections.sort(rank1, comparator);
		Collections.sort(rank2, comparator);
		Collections.sort(rank3, comparator);
		Collections.sort(rank4, comparator);
		rankLists.add(rank1);
		rankLists.add(rank2);
		rankLists.add(rank3);
		rankLists.add(rank4);
		return rankLists;
	}
	
	public static List<CharacterIcon> getCharacterIcons(long uuid, int rank, boolean isSorted) {
		List<CharacterIcon> iconList = new Select().from(CharacterIcon.class)
				.where(CharacterIcon.Col_CharacterId + "=? and " + CharacterIcon.Col_Rank + "=?", uuid, rank)
				.execute();
		if (isSorted) {
			Collections.sort(iconList, comparator);
		}
		return iconList;
	}
	
	public static List<CharacterIcon> addCharacterIcon(long characterId, 
			List<CharacterIcon> rankList, int rank, String path) {
		int index = 1;
		List<CharacterIcon> newList = new ArrayList<CharacterIcon>();
		if (rankList != null && rankList.size() > 0) {
			index = rankList.get(rankList.size() - 1).getIndex() + 1;
			newList.addAll(rankList);
		}
		CharacterIcon characterIcon = new CharacterIcon(characterId, path, rank, index, null);
		characterIcon.save();
		newList.add(characterIcon);
		return newList;
	}
	
	public static boolean deleteCharacterIcon(CharacterIcon characterIcon) {
		try {
			characterIcon.delete();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	

	private static Comparator<CharacterIcon> comparator = new Comparator<CharacterIcon>() {
		
		@Override
		public int compare(CharacterIcon i0, CharacterIcon i1) {
			return i0.getIndex() - i1.getIndex();
		}
	};
	
}

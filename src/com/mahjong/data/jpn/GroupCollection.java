package com.mahjong.data.jpn;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.data.jpn.Groups.Group;
import com.mahjong.data.jpn.Groups.Pair;
import com.mahjong.data.jpn.Groups.Junko;

public class GroupCollection implements IGroups {

	private Group[] groups;
	
	private Pair pair;
	private Junko[] junkos;
	private Group[] pungs;
	
	private int[] kindCounts = {0, 0, 0, 0};
	private int[] kindCountsWithoutPair = {0, 0, 0, 0};
	
	public GroupCollection(List<Group> groups) {
		if (groups != null && groups.size() > 0) {
			this.groups = new Group[groups.size()];
			groups.toArray(this.groups);
			List<Junko> jList = new ArrayList<Junko>();
			List<Group> pList = new ArrayList<Group>();
			for (int i = 0; i < groups.size(); i++) {
				Group g = groups.get(i);
				if (g instanceof Pair) {
					this.pair = (Pair) g;
				} else if (g instanceof Junko) {
					jList.add((Junko) g);
				} else {
					pList.add(g);
				}
			}
			junkos = new Junko[jList.size()];
			jList.toArray(junkos);
			pungs = new Group[pList.size()];
			pList.toArray(pungs);

			for (Group g : groups) {
				kindCounts[g.sortedLevel()]++;
				if (g instanceof Pair) continue;
				kindCountsWithoutPair[g.sortedLevel()]++;
			}
		}		
	}
	
	@Override
	public int Count() {
		return groups != null ? groups.length : 0;
	}
	
	@Override
	public Group[] AllGroups() {
		return groups;
	}
	
	@Override
	public Pair Pair() {
		return pair;
	}

	@Override
	public Junko[] JunkoList() {
		return junkos;
	}

	@Override
	public Group[] PungList() {
		return pungs;
	}

	@Override
	public int JunkoCount() {
		return junkos != null ? junkos.length : 0;
	}

	@Override
	public int PungCount() {
		return pungs != null ? pungs.length : 0;
	}

	@Override
	public int[] KindCounts() {
		return kindCounts;
	}

	@Override
	public int[] KindCountsWithoutPair() {
		return kindCountsWithoutPair;
	}

}

package com.mahjong.common;

import java.util.List;

import com.mahjong.R;

public class MjSetting {

	public static final String PLAYER_NORTH 	= "PLAYER_NORTH";
	public static final String PLAYER_SOUTH 	= "PLAYER_SOUTH";
	public static final String PLAYER_EAST 		= "PLAYER_EAST";
	public static final String PLAYER_WEST 		= "PLAYER_WEST";
	
	public static final String MEMBER_COUNT 	= "MEMBER_COUNT";
	public static final String BATTLE_COUNT 	= "BATTLE_COUNT";
	public static final String BASE_POINT 		= "BASE_POINT";
	public static final String ENTER_SOUTNWEST	= "ENTER_SOUTNWEST";
	public static final String FANFU 			= "FANFU";
	public static final String LIZHI_BELONG		= "LIZHI_BELONG";
	public static final String MODE_NOVICE_EXPERT 	= "MODE_NOVICE_EXPERT";
	public static final String MA_POINT 		= "MA_POINT";
	public static final String RET_POINT 		= "RET_POINT";
	
	// 特殊役
	public static final String BALIANZHUANG 	= "BALIANZHUANG";
	
	public static int UUID = 0;
	
	public static final int _face_down = 0;
	
	public static final int _man1 = 1;
	public static final int _man2 = 2;
	public static final int _man3 = 3;
	public static final int _man4 = 4;
	public static final int _man5 = 5;
	public static final int _man6 = 6;
	public static final int _man7 = 7;
	public static final int _man8 = 8;
	public static final int _man9 = 9;
	public static final int _man5r = 10;
	
	public static final int _pin1 = 11;
	public static final int _pin2 = 12;
	public static final int _pin3 = 13;
	public static final int _pin4 = 14;
	public static final int _pin5 = 15;
	public static final int _pin6 = 16;
	public static final int _pin7 = 17;
	public static final int _pin8 = 18;
	public static final int _pin9 = 19;
	public static final int _pin5r = 20;
	
	public static final int _bamboo1 = 21;
	public static final int _bamboo2 = 22;
	public static final int _bamboo3 = 23;
	public static final int _bamboo4 = 24;
	public static final int _bamboo5 = 25;
	public static final int _bamboo6 = 26;
	public static final int _bamboo7 = 27;
	public static final int _bamboo8 = 28;
	public static final int _bamboo9 = 29;
	public static final int _bamboo5r = 30;
	
	public static final int _wind_east = 31;
	public static final int _wind_south = 32;
	public static final int _wind_west = 33;
	public static final int _wind_north = 34;
	public static final int _dragon_haku = 35;
	public static final int _dragon_green = 36;
	public static final int _dragon_chun = 37;
	
	public static final int _season_spring = 41;
	public static final int _season_summer = 42;
	public static final int _season_autumn = 43;
	public static final int _season_winter = 44;
	public static final int _flower_plum = 45;
	public static final int _flower_orchid = 46;
	public static final int _flower_bamboo = 47;
	public static final int _flower_chrysanthemum = 48;
	
	public static final int[] drawableMan = {
		R.drawable.b_man1, R.drawable.b_man2, R.drawable.b_man3, R.drawable.b_man4,
		R.drawable.b_man5, R.drawable.b_man6, R.drawable.b_man7, R.drawable.b_man8,
		R.drawable.b_man9, R.drawable.b_red_man5,
	};
	
	public static final int[] drawablePin = {
		R.drawable.b_pin1, R.drawable.b_pin2, R.drawable.b_pin3, R.drawable.b_pin4,
		R.drawable.b_pin5, R.drawable.b_pin6, R.drawable.b_pin7, R.drawable.b_pin8,
		R.drawable.b_pin9, R.drawable.b_red_pin5,
	};
	
	public static final int[] drawableBamboo = {
		R.drawable.b_bamboo1, R.drawable.b_bamboo2, R.drawable.b_bamboo3, R.drawable.b_bamboo4,
		R.drawable.b_bamboo5, R.drawable.b_bamboo6, R.drawable.b_bamboo7, R.drawable.b_bamboo8,
		R.drawable.b_bamboo9, R.drawable.b_red_bamboo5,
	};
	
	public static final int[] drawableW_D = {
		R.drawable.b_wind_east, R.drawable.b_wind_south, R.drawable.b_wind_west, R.drawable.b_wind_north,
		R.drawable.b_dragon_haku, R.drawable.b_dragon_green, R.drawable.b_dragon_chun,
	};
	
	public static final int[] drawableS_F = {
		R.drawable.b_season_spring, R.drawable.b_season_summer, R.drawable.b_season_autumn, R.drawable.b_season_winter,
		R.drawable.b_flower_plum, R.drawable.b_flower_orchid, R.drawable.b_flower_bamboo, R.drawable.b_flower_chrysanthemum,
	};
	
	public static int buildUuid() {
		return ++UUID;
	}
	
	public static int getMahjongBitmapRes(int num) {
		int resId = R.drawable.b_face_down;
		if (num >= _man1 && num <= _man5r) {
			resId = drawableMan[num - _man1];
		} else if (num >= _pin1 && num <= _pin5r) {
			resId = drawablePin[num - _pin1];
		} else if (num >= _bamboo1 && num <= _bamboo5r) {
			resId = drawableBamboo[num - _bamboo1];
		} else if (num >= _wind_east && num <= _dragon_chun) {
			resId = drawableW_D[num - _wind_east];
		} else if (num >= _season_spring && num <= _flower_chrysanthemum) {
			resId = drawableS_F[num - _season_spring];
		}
		return resId;
	}
	
	public static void sortMjCardList(List<MjCard> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			MjCard minCard = list.get(i);
			if (minCard.isBlank()) {
				break;
			} 
			for (int j = i + 1; j < list.size(); j++) {
				MjCard cmpCard = list.get(j);
				if (cmpCard.isBlank()) {
					break;
				}
				float cmp = cmpCard.num;
				if (cmp == _man5r || cmp == _pin5r || cmp == _bamboo5r) {
					cmp -= 4.5f;
				}
				float min = minCard.num;
				if (min == _man5r || min == _pin5r || min == _bamboo5r) {
					min -= 4.5f;
				}
				if (cmp < min) {
					int tmpNum = minCard.num;
					int tmpId = minCard.id;
					minCard.num = cmpCard.num;
					minCard.id = cmpCard.id;
					cmpCard.num = tmpNum;
					cmpCard.id = tmpId;
				}
			}
		}
	}
	
}

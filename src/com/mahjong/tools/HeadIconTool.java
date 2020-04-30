package com.mahjong.tools;

import com.mahjong.R;

/**
 * （弃用）早期工具类
 * 
 * @author Summer Lee
 *
 */
public class HeadIconTool {

	public static String  ICON_START_ID = "[id]";
	public static String  ICON_START_LOCAL = "[local]";
	
	public static int[][] IconFolder = {
		{R.drawable.head_none, R.string.mahjong_soul},
		{R.drawable.head_none, R.string.animal_square}	
//		{R.drawable.head_mjsoul_wang, R.string.mahjong_soul},
//		{R.drawable.head_rect_bear, R.string.animal_square}		
	};
		
	private static int[] IconSet0 = {
		R.drawable.head_none
//		R.drawable.head_mjsoul_wang, R.drawable.head_mjsoul_yiji, R.drawable.head_mjsoul_erjietang,
//		R.drawable.head_mjsoul_qianzhi, R.drawable.head_mjsoul_jiana, R.drawable.head_mjsoul_wu,
//		R.drawable.head_mjsoul_fuzi, R.drawable.head_mjsoul_wei, R.drawable.head_mjsoul_liyu,
//		R.drawable.head_mjsoul_niya, R.drawable.head_mjsoul_kawei, R.drawable.head_mjsoul_qingku,
//		R.drawable.head_mjsoul_shala, R.drawable.head_mjsoul_hua
	};
	
	private static int[] IconSet1 = {
		R.drawable.head_none
//		R.drawable.head_rect_bear, R.drawable.head_rect_bird, R.drawable.head_rect_elephant,
//		R.drawable.head_rect_monkey, R.drawable.head_rect_panda, R.drawable.head_rect_pig,
//		R.drawable.head_rect_puffer, R.drawable.head_rect_tiger, R.drawable.head_rect_whale
	};
	
	public static int[] iconSetSelect(int drawableId) {
		int[] set = null;
		switch (drawableId) {
		case R.drawable.head_none:
//		case R.drawable.head_mjsoul_wang:
			set = IconSet0;
			break;
//		case R.drawable.head_rect_bear:
//			set = IconSet1;
//			break;
		default:
			break;
		}
		return set;
	}
	
	public static String Id2String(int folder, int icon) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < IconFolder.length; i++) {
			if (IconFolder[i][0] == folder) {
				sb.append(ICON_START_ID);
				switch (folder) {
				case R.drawable.head_none:
//				case R.drawable.head_mjsoul_wang:
					sb.append("mjsoul-");
					sb.append(ConvertIconSet0ToString(icon));
					break;
//				case R.drawable.head_rect_bear:
//					sb.append("rect-");
//					sb.append(ConvertIconSet1ToString(icon));
//					break;
				default:
					break;
				}				
				break;
			}
		}		
		return sb.toString();
	}
	
	private static String ConvertIconSet0ToString(int id) {
		String s = null;
//		switch (id) {
//		case R.drawable.head_mjsoul_wang:
//			s = "head_mjsoul_wang";
//			break;
//		case R.drawable.head_mjsoul_yiji:
//			s = "head_mjsoul_yiji";
//			break;
//		case R.drawable.head_mjsoul_erjietang:
//			s = "head_mjsoul_erjietang";
//			break;
//		case R.drawable.head_mjsoul_qianzhi:
//			s = "head_mjsoul_qianzhi";
//			break;
//		case R.drawable.head_mjsoul_jiana:
//			s = "head_mjsoul_jiana";
//			break;
//		case R.drawable.head_mjsoul_wu:
//			s = "head_mjsoul_wu";
//			break;
//		case R.drawable.head_mjsoul_fuzi:
//			s = "head_mjsoul_fuzi";
//			break;
//		case R.drawable.head_mjsoul_wei:
//			s = "head_mjsoul_wei";
//			break;
//		case R.drawable.head_mjsoul_liyu:
//			s = "head_mjsoul_liyu";
//			break;
//		case R.drawable.head_mjsoul_niya:
//			s = "head_mjsoul_niya";
//			break;
//		case R.drawable.head_mjsoul_kawei:
//			s = "head_mjsoul_kawei";
//			break;
//		case R.drawable.head_mjsoul_qingku:
//			s = "head_mjsoul_qingku";
//			break;
//		case R.drawable.head_mjsoul_shala:
//			s = "head_mjsoul_shala";
//			break;
//		case R.drawable.head_mjsoul_hua:
//			s = "head_mjsoul_hua";
//			break;
//		default:
//			break;
//		}
		return s;
	}
	
	private static String ConvertIconSet1ToString(int id) {
		String s = null;
//		switch (id) {
//		case R.drawable.head_rect_bear:
//			s = "head_rect_bear";
//			break;
//		case R.drawable.head_rect_bird:
//			s = "head_rect_bird";
//			break;
//		case R.drawable.head_rect_elephant:
//			s = "head_rect_elephant";
//			break;
//		case R.drawable.head_rect_monkey:
//			s = "head_rect_monkey";
//			break;
//		case R.drawable.head_rect_panda:
//			s = "head_rect_panda";
//			break;
//		case R.drawable.head_rect_pig:
//			s = "head_rect_pig";
//			break;
//		case R.drawable.head_rect_puffer:
//			s = "head_rect_puffer";
//			break;
//		case R.drawable.head_rect_tiger:
//			s = "head_rect_tiger";
//			break;
//		case R.drawable.head_rect_whale:
//			s = "head_rect_whale";
//			break;
//		default:
//			break;
//		}
		return s;
	}
	
	public static int String2Id(String s) {
		if (s.startsWith(ICON_START_ID)) {
			s = s.substring(ICON_START_ID.length());
			String[] split = s.split("-");
			if (split == null || split.length != 2) {
				return -1;
			}
			if (split[0].equals("npc")) {
				return ConvertIconNPCToId(split[1]);
			} else if (split[0].equals("mjsoul")) {
				return ConvertIconSet0ToId(split[1]);
			} else if (split[0].equals("rect")) {
				return ConvertIconSet1ToId(split[1]);
			}
		}			
		return -1;
	}

	private static int ConvertIconNPCToId(String s) {
		int id = -1;
//		if (s.equals("head_pc_saki")) {
//			id = R.drawable.head_pc_saki;
//		} else if (s.equals("head_pc_nodoka")) {
//			id = R.drawable.head_pc_nodoka;
//		} else if (s.equals("head_pc_koromo")) {
//			id = R.drawable.head_pc_koromo;
//		} else if (s.equals("head_pc_komaki")) {
//			id = R.drawable.head_pc_komaki;
//		} else if (s.equals("head_pc_teru")) {
//			id = R.drawable.head_pc_teru;
//		} else if (s.equals("head_pc_shizuno")) {
//			id = R.drawable.head_pc_shizuno;
//		}
		return id;
	}
	
	private static int ConvertIconSet0ToId(String s) {
		int id = -1;
//		if (s.equals("head_mjsoul_wang")) {
//			id = R.drawable.head_mjsoul_wang;
//		} else if (s.equals("head_mjsoul_yiji")) {
//			id = R.drawable.head_mjsoul_yiji;
//		} else if (s.equals("head_mjsoul_erjietang")) {
//			id = R.drawable.head_mjsoul_erjietang;
//		} else if (s.equals("head_mjsoul_qianzhi")) {
//			id = R.drawable.head_mjsoul_qianzhi;
//		} else if (s.equals("head_mjsoul_jiana")) {
//			id = R.drawable.head_mjsoul_jiana;
//		} else if (s.equals("head_mjsoul_wu")) {
//			id = R.drawable.head_mjsoul_wu;
//		} else if (s.equals("head_mjsoul_fuzi")) {
//			id = R.drawable.head_mjsoul_fuzi;
//		} else if (s.equals("head_mjsoul_wei")) {
//			id = R.drawable.head_mjsoul_wei;
//		} else if (s.equals("head_mjsoul_liyu")) {
//			id = R.drawable.head_mjsoul_liyu;
//		} else if (s.equals("head_mjsoul_niya")) {
//			id = R.drawable.head_mjsoul_niya;
//		} else if (s.equals("head_mjsoul_kawei")) {
//			id = R.drawable.head_mjsoul_kawei;
//		} else if (s.equals("head_mjsoul_qingku")) {
//			id = R.drawable.head_mjsoul_qingku;
//		} else if (s.equals("head_mjsoul_shala")) {
//			id = R.drawable.head_mjsoul_shala;
//		} else if (s.equals("head_mjsoul_hua")) {
//			id = R.drawable.head_mjsoul_hua;
//		}
		return id;
	}

	private static int ConvertIconSet1ToId(String s) {
		int id = -1;
//		if (s.equals("head_rect_bear")) {
//			id = R.drawable.head_rect_bear;
//		} else if (s.equals("head_rect_bird")) {
//			id = R.drawable.head_rect_bird;
//		} else if (s.equals("head_rect_elephant")) {
//			id = R.drawable.head_rect_elephant;
//		} else if (s.equals("head_rect_monkey")) {
//			id = R.drawable.head_rect_monkey;
//		} else if (s.equals("head_rect_panda")) {
//			id = R.drawable.head_rect_panda;
//		} else if (s.equals("head_rect_pig")) {
//			id = R.drawable.head_rect_pig;
//		} else if (s.equals("head_rect_puffer")) {
//			id = R.drawable.head_rect_puffer;
//		} else if (s.equals("head_rect_tiger")) {
//			id = R.drawable.head_rect_tiger;
//		} else if (s.equals("head_rect_whale")) {
//			id = R.drawable.head_rect_whale;
//		}
		return id;
	}
}

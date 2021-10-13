package com.mahjong.item;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.mahjong.tools.PinyinUtil;

public class FileSortModel {

	public static int Type_UpperCase 	= 1;
	public static int Type_LowerCase 	= 2;
	public static int Type_Pinyin 		= 3;
	public static int Type_Number 		= 4;
	public static int Type_Other		= 5;
	
	private File file;
	private String pinyinName;
	private char firstChar;
	private int type;
	
	public FileSortModel(File file) {
		this.file = file;
		initData();
	}
	
	private void initData() {
		String fileName = file.getName();
		pinyinName = PinyinUtil.hanziToPinyin(fileName);
		firstChar = fileName.charAt(0);
		if (firstChar >= 0x41 && firstChar <= 0x5a) {
			type = Type_UpperCase;
		} else if (firstChar >= 0x61 && firstChar <= 0x7a) {
			type = Type_LowerCase;
			firstChar -= 0x20;
		} else if (firstChar >= 0x30 && firstChar <= 0x39) {
			type = Type_Number;
		} else if (firstChar >= 0x4e00 && firstChar <= 0x9fa5) {
			type = Type_Pinyin;
			try {
				String[] temp = PinyinHelper.toHanyuPinyinStringArray(firstChar, PinyinUtil.getDefaultFormat());
				if (temp != null && temp.length > 0) {
					firstChar = temp[0].charAt(0);
					firstChar -= 0x20;
				} else {
					type = Type_Other;
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		} else {
			type = Type_Other;
		}
	}
	
	public File getFile() {
		return file;
	}
	
	public String getPinyinName() {
		return pinyinName;
	}
	
	public char getFirstChar() {
		return firstChar;
	}
	
	public int getType() {
		return type;
	}
	
	public static List<File> sortFile(List<File> orgFiles) {
		List<FileSortModel> sortModels = new ArrayList<FileSortModel>();
		for (int i = 0; i < orgFiles.size(); i++) {
			sortModels.add(new FileSortModel(orgFiles.get(i)));
		}
		Collections.sort(sortModels, new Comparator<FileSortModel>() {

			@Override
			public int compare(FileSortModel f0, FileSortModel f1) {
				if (f0.getType() == f1.getType()) { // 类型相同
					if (f0.getFirstChar() == f1.getFirstChar()) { // 首字母相同，比较全部
						String name0 = f0.getPinyinName();
						String name1 = f1.getPinyinName();
						for (int i = 1; i < name0.length() && i < name1.length(); i++) {
							if (name0.charAt(i) == name1.charAt(i)) continue;
							return name0.charAt(i) - name1.charAt(i);
						}
					}
					return f0.getFirstChar() - f1.getFirstChar();										
				} else { // 类型不同
					if ((f0.getType() == Type_UpperCase || f0.getType() == Type_LowerCase || f0.getType() == Type_Pinyin)
							&& (f1.getType() == Type_UpperCase || f1.getType() == Type_LowerCase || f1.getType() == Type_Pinyin)) { // 两个文件名首字母为字母或汉字
						if (f0.getFirstChar() == f1.getFirstChar()) { // 首字母相同，比较类型							
							return f0.getType() - f1.getType();
						} else { // 首字母不同，比较字母
							return f0.getFirstChar() - f1.getFirstChar();
						}						
					}
					return f0.getType() - f1.getType();										
				}
			}
		});
		List<File> dstFiles = new ArrayList<File>();
		for (int i = 0; i < sortModels.size(); i++) {
			dstFiles.add(sortModels.get(i).getFile());
		}
		return dstFiles;
	}
	
}

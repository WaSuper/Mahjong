package com.mahjong.tools;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {
	
	private static HanyuPinyinOutputFormat pinyinFormat;
	
	public static HanyuPinyinOutputFormat getDefaultFormat() {
		if (pinyinFormat != null) return pinyinFormat;
		pinyinFormat = new HanyuPinyinOutputFormat();
		//设置声调格式方法参数HanyuPinyinToneType有以下常量对象：
		//	HanyuPinyinToneType.WITH_TONE_NUMBER 用数字表示声调，例如：liu2
		//	HanyuPinyinToneType.WITHOUT_TONE 无声调表示，例如：liu
		//	HanyuPinyinToneType.WITH_TONE_MARK 用声调符号表示，例如：liú
		pinyinFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		//设置特殊拼音ü的显示格式方法参数HanyuPinyinVCharType有以下常量对象：
		//	HanyuPinyinVCharType.WITH_U_AND_COLON 以U和一个冒号表示该拼音，例如：lu:
		//	HanyuPinyinVCharType.WITH_V 以V表示该字符，例如：lv
		//	HanyuPinyinVCharType.WITH_U_UNICODE 以ü表示
		pinyinFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		//设置大小写格式outputFormat.setCaseType(HanyuPinyinCaseType);
		//	HanyuPinyinCaseType.LOWERCASE 转换后以全小写方式输出
		//	HanyuPinyinCaseType.UPPERCASE 转换后以全大写方式输出
		pinyinFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		return pinyinFormat;
	}
	
	public static String hanziToPinyin(String hanzi) {
		HanyuPinyinOutputFormat format = getDefaultFormat();
		char[] input = hanzi.trim().toCharArray();
        StringBuffer output = new StringBuffer();
        try {
            for (char curchar : input) {
                if (java.lang.Character.toString(curchar).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, format);
                    output.append(temp[0]);
                } else
                    output.append(java.lang.Character.toString(curchar));
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
		return output.toString();
	}
}

package com.mahjong.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ShareprefenceTool {
	
	public Context mContext;
	public static ShareprefenceTool mst;
	public static String SETTING = "OPTION";

	static public ShareprefenceTool getInstance() {
		if (mst == null) {
			mst = new ShareprefenceTool();
			return mst;
		}
		return mst;
	}

	public void setStringSet(String key, Set<String> values, Context mContext) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);
		Editor et = sp.edit();
		et.putStringSet(key, values);
		et.commit();
	}

	public Set<String> getStringSet(String key, Context mContext) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);
		Set<String> mSet = sp.getStringSet(key, null);

		return mSet;
	}

	public String[] getSringArray(String key, Context mContext) {
		String regularEx = "#!#";
		String[] str = null;
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);
		String values;
		values = sp.getString(key, "");

		str = values.split(regularEx);

		return str;
	}

	public void setSringArray(String key, String[] values,
			Context mContext) {
		String regularEx = "#!#";
		String str = "";
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);
		if (values != null && values.length > 0) {
			for (String value : values) {
				str += value;
				str += regularEx;
			}
		}
		Editor et = sp.edit();
		et.putString(key, str);
		et.commit();
	}

	public void setSringArray2(String key, List<String> values, Context mContext) {
		String regularEx = "#!#";
		String str = "";
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);
		if (values != null && values.size() > 0) {
			for (String value : values) {
				str += value;
				str += regularEx;
			}
		}
		Editor et = sp.edit();
		et.putString(key, str);
		et.commit();
	}

	public List<String> getSringArray2(String key, Context mContext) {
		String regularEx = "#!#";
		List<String> lists = new ArrayList<String>();
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);
		String values;
		values = sp.getString(key, "");

		String[] str = values.split(regularEx);
		for (int i = 0; i < str.length; i++) {
			lists.add(str[i]);
		}

		return lists;
	}

	public boolean getBoolean(String key, Context mContext,
			boolean default_value) {
		boolean b;
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);

		b = sp.getBoolean(key, default_value);
		return b;
	}

	public String getString(String key, Context mContext,
			String default_value) {
		String b;
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);

		b = sp.getString(key, default_value);
		return b;
	}

	public int getInt(String key, Context mContext,
			int default_value) {
		int b;
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);

		b = sp.getInt(key, default_value);
		return b;
	}

	public void setBoolean(String key, boolean values,
			Context mContext) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);

		Editor et = sp.edit();
		et.putBoolean(key, values);
		et.commit();

	}

	public void setString(String key, String values,
			Context mContext) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);

		Editor et = sp.edit();
		et.putString(key, values);
		et.commit();

	}

	public void setInt(String key, int values, Context mContext) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);

		Editor et = sp.edit();
		et.putInt(key, values);
		et.commit();

	}
	public void setLong(String key, long values, Context mContext) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);

		Editor et = sp.edit();
		et.putLong(key, values);
		et.commit();

	}
	public long getLong(String key, Context mContext,
			long default_value) {
		long b;
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);

		b = sp.getLong(key, default_value);
		return b;
	}
	public float[] getfloat(String key, float[] values,
			Context mContext) {

		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);
		float[] f = new float[11];
		for (int i = 0; i < 11; i++) {

			if (values == null) {

				f[i] = sp.getFloat(key + i, (float) 0.0);

			} else {

				f[i] = sp.getFloat(key + i, values[i]);
			}
		}

		return f;
	}

	public void setfloat(String key, float[] values,
			Context mContext) {
		if (values != null) {
			SharedPreferences sp = mContext.getSharedPreferences(SETTING,
					Context.MODE_PRIVATE);

			Editor et = sp.edit();
			// et.putInt(key, values);
			for (int i = 0; i < 11; i++) {
				et.putFloat(key + i, values[i]);
				et.commit();
			}

		}

	}

	public int[] getIntArray(String key, Context mContext) {
		List<Integer> list = getIntegerArray(key, mContext);
		if (list.size() > 0) {
			int[] array = new int[list.size()];
			for (int i = 0; i < list.size(); i++) {
				array[i] = list.get(i);
			}			
			return array;
		} else {
			return null;
		}
	}
	
	public void setIntArray(String key, int[] values,
			Context mContext) {
		String regularEx = "#";
		String str = "";
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);
		if (values != null && values.length > 0) {
			for (int value : values) {
				str += value;
				str += regularEx;
			}
		}
		Editor et = sp.edit();
		et.putString(key, str);
		et.commit();
	}
	
	public List<Integer> getIntegerArray(String key, Context mContext) {

		String regularEx = "#";
		List<Integer> lists = new ArrayList<Integer>();
		if (mContext != null) {
			SharedPreferences sp = mContext.getSharedPreferences(SETTING,
					Context.MODE_PRIVATE);
			String values;
			values = sp.getString(key, "");

			String[] str = values.split(regularEx);
			for (int i = 0; i < str.length; i++) {
				if (!str[i].equals("")) {
					Integer decode = Integer.parseInt((str[i]));
					lists.add(decode);
				}
			}
		}
		return lists;

	}

	public void setIntegerArray(String key, List<Integer> values,
			Context mContext) {
		String regularEx = "#";
		String str = "";
		SharedPreferences sp = mContext.getSharedPreferences(SETTING,
				Context.MODE_PRIVATE);
		if (values != null && values.size() > 0) {
			for (Integer value : values) {
				str += value;
				str += regularEx;
			}
		}
		Editor et = sp.edit();
		et.putString(key, str);
		et.commit();
	}

}

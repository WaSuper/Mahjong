package com.mahjong.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.mahjong.R;
import com.mahjong.common.MjCalcTool;
import com.mahjong.data.jpn.Yaku;
import com.mahjong.item.YakuBean;

public class SpecialYakuTool {

	private static SpecialYakuTool instance;

	private String fileDir;
	private Map<String, Integer> name2IndexMap;
	private List<YakuBean> mYakuList;	
	
	public static SpecialYakuTool getInstance() {
		if (instance == null) {
			instance = new SpecialYakuTool();
		}
		return instance;
	}
	
	public SpecialYakuTool() {		
		mYakuList = new ArrayList<YakuBean>();
		name2IndexMap = new HashMap<String, Integer>();
		List<Yaku> spYakus = MjCalcTool.getSpecialYakus();
		for (int i = 0; i < spYakus.size(); i++) {
			Yaku yaku = spYakus.get(i);
			YakuBean bean = new YakuBean(yaku.name(), false);
			mYakuList.add(bean);
			name2IndexMap.put(yaku.name(), i);			
		}
	}
	
	public void init(Context context, String dir) {
		this.fileDir = dir;
		List<YakuBean> restoreList = restoreSpecialYaku(fileDir);
		for (YakuBean savebean : restoreList) {
			if (savebean.enable()) {
				Integer index = name2IndexMap.get(savebean.name());
				if (index != null && index < mYakuList.size()) {
					mYakuList.get(index).setEnable(true);
				}
			}
		}
		Map<String, String> transNameMap = new HashMap<String, String>();
		transNameMap.put("ShiErLuoTai", context.getString(R.string.ShiErLuoTai));
		transNameMap.put("SanLianKe", context.getString(R.string.SanLianKe));
		transNameMap.put("SanSeTongGuan", context.getString(R.string.SanSeTongGuan));
		transNameMap.put("SanSeLianKe", context.getString(R.string.SanSeLianKe));
		transNameMap.put("JingTongHe", context.getString(R.string.JingTongHe));
		transNameMap.put("ErTongKe", context.getString(R.string.ErTongKe));
		transNameMap.put("SanSeShuangLongHui", context.getString(R.string.SanSeShuangLongHui));
		transNameMap.put("RenHe", context.getString(R.string.RenHe));
		transNameMap.put("DaCheLun", context.getString(R.string.DaCheLun));
		transNameMap.put("DaZhuLin", context.getString(R.string.DaZhuLin));
		transNameMap.put("DaShuLin", context.getString(R.string.DaShuLin));
		transNameMap.put("BaLianZhuang", context.getString(R.string.BaLianZhuang));
		transNameMap.put("DaQiXing", context.getString(R.string.DaQiXing));
		for (YakuBean bean : mYakuList) {
			String showName = transNameMap.get(bean.name());
			bean.setShowName(showName);
		}
		MjCalcTool.setSpecialYakusEnable(name2IndexMap, mYakuList);
	}
	
	public List<YakuBean> getYakuList() {
		return mYakuList;
	}
	
	private List<YakuBean> restoreSpecialYaku(String dir) {
		List<YakuBean> mList = new ArrayList<YakuBean>();
		File tmpFile = new File(dir, "special_yakus.tmp");
		if (tmpFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(tmpFile));
				int len = -1;
		        char[] buffer = new char[1024];
		        StringBuffer sb = new StringBuffer();
		        while ((len = br.read(buffer)) != -1) {
		        	sb.append(buffer, 0, len);
		        }
				br.close();
				JSONArray jsonArray = new JSONArray(sb.toString());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = (JSONObject) jsonArray.get(i);
					String name = object.getString("name");
					boolean enable = object.getBoolean("enable");
					YakuBean yakuBean = new YakuBean(name, enable);
					mList.add(yakuBean);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return mList;
	}
	
	public void saveSpecialYaku() {
		saveSpecialYaku(fileDir, mYakuList);
		MjCalcTool.setSpecialYakusEnable(name2IndexMap, mYakuList);
	}
	
	private void saveSpecialYaku(String dir, List<YakuBean> list) {
		if (list == null || list.isEmpty()) return;
		try {
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				YakuBean yakuBean = list.get(i);
				JSONObject object = new JSONObject();
				object.put("name", yakuBean.name());
				object.put("enable", yakuBean.enable());
				jsonArray.put(object);
			}
			File saveFile = new File(dir, "special_yakus.tmp");
			if (!saveFile.exists()) saveFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile, false));
			bw.write(jsonArray.toString());
            bw.flush();
            bw.close();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}

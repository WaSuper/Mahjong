package com.mahjong.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.mahjong.model.AudioItem;
import com.mahjong.model.MjDetail;
import com.mahjong.model.MjResult;
import com.mahjong.model.Player;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtils {
		
	private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private static final String mainSheetName = "main";
	private static final String audioSheetName = "audio";
	
	/**
	 * 创建excel表（历史清单）
	 * 
	 * @param filePath 保存路径
	 * @param fileName 文件名
	 * @param resultList 保存数据
	 * @return 
	 */
	public static boolean createExcelFromResult(String filePath, String fileName, List<MjResult> resultList) {
		try {
			File savePath = new File(filePath);
			if (!savePath.exists()) {
				savePath.mkdir();
			}			
			String path = filePath + "/" + fileName + ".xls"; // 输出Excel的路径	
			File xlsFile = new File(path);
			OutputStream os = new FileOutputStream(xlsFile); // 新建一个文件			
			WritableWorkbook writableWorkbook = Workbook.createWorkbook(os); // 创建Excel工作簿			
			WritableSheet mainSheet = writableWorkbook.createSheet(mainSheetName, 0); // 创建Sheet表			
			addSheetTitle(mainSheet, MjResult.Columns); // 添加标题
			for (int i = 0; i < resultList.size(); i++) {
				MjResult result = resultList.get(i);
				// 写入result数据
				int row = i + 1;
				mainSheet.addCell(new Number(0, row, result.getGameType()));
				mainSheet.addCell(new Number(1, row, result.getBasePoint()));
				mainSheet.addCell(new Label(2, row, result.getMaPointString()));
				mainSheet.addCell(new Label(3, row, mDateFormat.format(new Date(result.getStartTime()))));
				mainSheet.addCell(new Label(4, row, mDateFormat.format(new Date(result.getEndTime()))));
				mainSheet.addCell(new Label(5, row, result.getEastId()));
				mainSheet.addCell(new Label(6, row, result.getEastName()));
				mainSheet.addCell(new Number(7, row, result.getEastPoint()));
				mainSheet.addCell(new Number(8, row, result.getEastRank()));
				mainSheet.addCell(new Number(9, row, result.getEastMa()));
				mainSheet.addCell(new Label(10, row, result.getSouthId()));
				mainSheet.addCell(new Label(11, row, result.getSouthName()));
				mainSheet.addCell(new Number(12, row, result.getSouthPoint()));
				mainSheet.addCell(new Number(13, row, result.getSouthRank()));
				mainSheet.addCell(new Number(14, row, result.getSouthMa()));
				mainSheet.addCell(new Label(15, row, result.getWestId()));
				mainSheet.addCell(new Label(16, row, result.getWestName()));
				mainSheet.addCell(new Number(17, row, result.getWestPoint()));
				mainSheet.addCell(new Number(18, row, result.getWestRank()));
				mainSheet.addCell(new Number(19, row, result.getWestMa()));
				mainSheet.addCell(new Label(20, row, result.getNorthId()));
				mainSheet.addCell(new Label(21, row, result.getNorthName()));
				mainSheet.addCell(new Number(22, row, result.getNorthPoint()));
				mainSheet.addCell(new Number(23, row, result.getNorthRank()));
				mainSheet.addCell(new Number(24, row, result.getNorthMa()));
				// database ver.2
				mainSheet.addCell(new Label(25, row, result.getTitle()));
				mainSheet.addCell(new Label(26, row, result.getNote()));
				// 写入detail数据
				List<MjDetail> detailList = new Select().from(MjDetail.class)
						.where(MjDetail.Col_StartTime + "=?", result.getStartTime())
						.orderBy(MjDetail.Col_LogTime + " ASC")
						.execute();
				String startTime = mDateFormat.format(new Date(result.getStartTime()));
				WritableSheet detailSheet = writableWorkbook.createSheet(startTime, i + 1);
				addSheetTitle(detailSheet, MjDetail.Columns);
				for (int j = 0; j < detailList.size(); j++) {
					MjDetail detail = detailList.get(j);
					int row2 = j + 1;
					detailSheet.addCell(new Label(0, row2, startTime));
					detailSheet.addCell(new Label(1, row2, mDateFormat.format(new Date(detail.getLogTime()))));
					detailSheet.addCell(new Number(2, row2, detail.getJuCount()));
					detailSheet.addCell(new Number(3, row2, detail.getRoundCount()));
					detailSheet.addCell(new Number(4, row2, detail.getLizhiCount()));
					detailSheet.addCell(new Number(5, row2, detail.getChangeEast()));
					detailSheet.addCell(new Number(6, row2, detail.getFinalEast()));
					detailSheet.addCell(new Number(7, row2, detail.getChangeSouth()));
					detailSheet.addCell(new Number(8, row2, detail.getFinalSouth()));
					detailSheet.addCell(new Number(9, row2, detail.getChangeWest()));
					detailSheet.addCell(new Number(10, row2, detail.getFinalWest()));
					detailSheet.addCell(new Number(11, row2, detail.getChangeNorth()));
					detailSheet.addCell(new Number(12, row2, detail.getFinalNorth()));
					detailSheet.addCell(new Number(13, row2, detail.getActionId()));
					detailSheet.addCell(new Label(14, row2, detail.getActionText()));
					detailSheet.addCell(new Label(15, row2, detail.getDoraOut()));
					detailSheet.addCell(new Label(16, row2, detail.getDoraIn()));
				}
			}						
			writableWorkbook.write(); // 写入数据			
			writableWorkbook.close(); // 关闭文件
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (WriteException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 创建excel表（玩家）
	 * 
	 * @param filePath 保存路径
	 * @param fileName 文件名
	 * @param playerList 保存数据
	 * @return 
	 */
	public static boolean createExcelFromPlayer(String filePath, String fileName, 
			List<Player> playerList, List<AudioItem> audioList) {
		try {
			File savePath = new File(filePath);
			if (!savePath.exists()) {
				savePath.mkdir();
			}			
			String path = filePath + "/" + fileName + ".xls"; // 输出Excel的路径	
			File xlsFile = new File(path);
			OutputStream os = new FileOutputStream(xlsFile); // 新建一个文件			
			WritableWorkbook writableWorkbook = Workbook.createWorkbook(os); // 创建Excel工作簿			
			WritableSheet mainSheet = writableWorkbook.createSheet(mainSheetName, 0); // 创建主表			
			addSheetTitle(mainSheet, Player.Columns); // 添加标题
			if (playerList != null && playerList.size() > 0) {
				for (int i = 0; i < playerList.size(); i++) {
					Player player = playerList.get(i);
					int row = i + 1;
					mainSheet.addCell(new Label(0, row, player.getUuid()));
					mainSheet.addCell(new Label(1, row, player.getName()));
					mainSheet.addCell(new Label(2, row, player.getNickName()));
					mainSheet.addCell(new Label(3, row, player.getSex() + ""));
					mainSheet.addCell(new Label(4, row, player.getSign()));
					mainSheet.addCell(new Label(5, row, player.getIcon()));
					mainSheet.addCell(new Number(6, row, player.getCharacterId()));
				}
			}				
			WritableSheet audioSheet = writableWorkbook.createSheet(audioSheetName, 1); // 创建音频表
			addSheetTitle(audioSheet, AudioItem.Columns); // 添加标题
			if (audioList != null && audioList.size() > 0) {
				for (int i = 0; i < audioList.size(); i++) {
					AudioItem item = audioList.get(i);
					int row = i + 1;
					audioSheet.addCell(new Label(0, row, item.getPlayerId()));
					audioSheet.addCell(new Number(1, row, item.getType()));
					audioSheet.addCell(new Label(2, row, item.getFilePath()));
					audioSheet.addCell(new Number(3, row, item.getEnable() ? 1 : 0));
				}
			}
			writableWorkbook.write(); // 写入数据			
			writableWorkbook.close(); // 关闭文件
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (WriteException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 添加标题栏
	 * 
	 * @param mainSheet
	 * @param titles
	 * @throws WriteException
	 */
	private static void addSheetTitle(WritableSheet mainSheet, String[] titles) throws WriteException {
		for (int i = 0; i < titles.length; i++) {
			Label label = new Label(i, 0, titles[i]);
			mainSheet.addCell(label);
		}
	}

	/**
	 * 读取Excel表（历史清单 ）
	 * 
	 * @param filePath
	 * @return
	 */
	public static int readExcelToResult(String filePath) {	        
		Workbook workbook = null;
		int count = 0;
        try {
            // 加载Excel文件
        	InputStream is = new FileInputStream(filePath);
            // 获取workbook
        	workbook = Workbook.getWorkbook(is);
        	// 获取sheet, 如果你的workbook里有多个sheet可以利用workbook.getSheets()方法来得到所有的
        	Map<String, Sheet> sheetMap = new HashMap<String, Sheet>();
        	Sheet mainSheet = null;
        	for (Sheet sheet : workbook.getSheets()) {
        		if (sheet.getName().equals(mainSheetName)) {
					if (sheet.getColumns() < 25 || sheet.getRows() < 2) {
						return closeExcel(workbook);
					} else {
						mainSheet = sheet;
					}
				} else {
					if (sheet.getColumns() >= 17 && sheet.getRows() >= 1) {
						sheetMap.put(sheet.getName(), sheet);
					}				
				}				
			}	        
        	if (mainSheet == null) return closeExcel(workbook);
        	List<MjResult> resultList = new ArrayList<MjResult>();
        	List<List<MjDetail>> detaiList = new ArrayList<List<MjDetail>>();
        	for (int i = 1; i < mainSheet.getRows(); i++) { // 逐行解析主表
        		int gameType = Integer.parseInt(mainSheet.getCell(0, i).getContents());
        		if (gameType > 4 || gameType < 0) continue;
        		int basePoint = Integer.parseInt(mainSheet.getCell(1, i).getContents());
        		if (basePoint < 0) continue;
        		String maPoint = mainSheet.getCell(2, i).getContents();
        		String[] maPoints = maPoint.split(",");
        		int[] ma_points = {0, 0, 0, 0};
        		if (maPoints == null || maPoints.length != 4) continue;
        		for (int j = 0; j < maPoints.length; j++) {
					ma_points[j] = Integer.parseInt(maPoints[j]);
				}
        		String startTime = mainSheet.getCell(3, i).getContents();
        		long start_time = mDateFormat.parse(startTime).getTime();
        		String endTime = mainSheet.getCell(4, i).getContents();
        		long end_time = mDateFormat.parse(endTime).getTime();
        		String eastId = mainSheet.getCell(5, i).getContents();
        		if (eastId == null || eastId.isEmpty()) continue;
        		String eastName = mainSheet.getCell(6, i).getContents();
        		if (eastName == null || eastName.isEmpty()) continue;
        		int eastPoint = Integer.parseInt(mainSheet.getCell(7, i).getContents());
        		int eastRank = Integer.parseInt(mainSheet.getCell(8, i).getContents());
        		if (eastRank < 1 || eastRank > 4) continue;
        		float eastMa = Float.parseFloat(mainSheet.getCell(9, i).getContents());
        		String southId = mainSheet.getCell(10, i).getContents();
        		if (southId == null || southId.isEmpty()) continue;
        		String southName = mainSheet.getCell(11, i).getContents();
        		if (southName == null || southName.isEmpty()) continue;
        		int southPoint = Integer.parseInt(mainSheet.getCell(12, i).getContents());
        		int southRank = Integer.parseInt(mainSheet.getCell(13, i).getContents());
        		if (southRank < 1 || southRank > 4) continue;
        		float southMa = Float.parseFloat(mainSheet.getCell(14, i).getContents());
        		String westId = mainSheet.getCell(15, i).getContents();
        		if (westId == null || westId.isEmpty()) continue;
        		String westName = mainSheet.getCell(16, i).getContents();
        		if (westName == null || westName.isEmpty()) continue;
        		int westPoint = Integer.parseInt(mainSheet.getCell(17, i).getContents());
        		int westRank = Integer.parseInt(mainSheet.getCell(18, i).getContents());
        		if (westRank < 1 || westRank > 4) continue;
        		float westMa = Float.parseFloat(mainSheet.getCell(19, i).getContents());
        		String northId = mainSheet.getCell(20, i).getContents();
        		if (northId == null || northId.isEmpty()) continue;
        		String northName = mainSheet.getCell(21, i).getContents();
        		if (northName == null || northName.isEmpty()) continue;
        		int northPoint = Integer.parseInt(mainSheet.getCell(22, i).getContents());
        		int northRank = Integer.parseInt(mainSheet.getCell(23, i).getContents());
        		if (northRank < 1 || northRank > 4) continue;
        		float northMa = Float.parseFloat(mainSheet.getCell(24, i).getContents());        		
        		MjResult result = new MjResult(gameType, basePoint, ma_points, start_time, 
        				eastId, eastName, southId, southName, westId, westName, northId, northName);
        		result.setEndGame(end_time, 
        				new int[] {eastPoint, southPoint, westPoint, northPoint}, 
        				new int[] {eastRank, southRank, westRank, northRank}, 
        				new float[] {eastMa, southMa, westMa, northMa});
        		if (mainSheet.getColumns() >= 27) { // database ver.2
        			String title = mainSheet.getCell(25, i).getContents();
        			String note = mainSheet.getCell(26, i).getContents();
        			result.setTitle(title);
            		result.setNote(note);
				}        		
        		Sheet timeSheet = sheetMap.get(startTime);
        		if (timeSheet == null) continue;
        		List<MjDetail> details = new ArrayList<MjDetail>();
        		for (int j = 1; j < timeSheet.getRows(); j++) { // 逐行解析分表
        			String logTime = timeSheet.getCell(1, j).getContents();
        			long log_time = mDateFormat.parse(logTime).getTime();
        			int juCount = Integer.parseInt(timeSheet.getCell(2, j).getContents());
        			int roundCount = Integer.parseInt(timeSheet.getCell(3, j).getContents());
        			int lizhiCount = Integer.parseInt(timeSheet.getCell(4, j).getContents());
        			int changeEast = Integer.parseInt(timeSheet.getCell(5, j).getContents());
        			int finalEast = Integer.parseInt(timeSheet.getCell(6, j).getContents());
        			int changeSouth = Integer.parseInt(timeSheet.getCell(7, j).getContents());
        			int finalSouth = Integer.parseInt(timeSheet.getCell(8, j).getContents());
        			int changeWest = Integer.parseInt(timeSheet.getCell(9, j).getContents());
        			int finalWest = Integer.parseInt(timeSheet.getCell(10, j).getContents());
        			int changeNorth = Integer.parseInt(timeSheet.getCell(11, j).getContents());
        			int finalNorth = Integer.parseInt(timeSheet.getCell(12, j).getContents());
        			int actionId = Integer.parseInt(timeSheet.getCell(13, j).getContents());
        			String actionText = timeSheet.getCell(14, j).getContents();
        			String doraOut = timeSheet.getCell(15, j).getContents();
        			String doraIn = timeSheet.getCell(16, j).getContents();
        			MjDetail detail = new MjDetail(start_time, log_time, juCount, roundCount, lizhiCount, 
        					changeEast, finalEast, changeSouth, finalSouth, 
        					changeWest, finalWest, changeNorth, finalNorth, 
        					doraOut, doraIn, actionId, actionText);
        			details.add(detail);
				}        	
        		detaiList.add(details);
				resultList.add(result);
			}        
	        ActiveAndroid.beginTransaction();
	        for (int i = 0; i < resultList.size(); i++) {
	        	MjResult result = resultList.get(i);
	        	MjResult tmp = new Select().from(MjResult.class)
		        	.where(MjResult.Col_StartTime + "=?", result.getStartTime())
		        	.executeSingle();
	        	if (tmp == null) {
	        		result.save();
					for (MjDetail detail : detaiList.get(i)) {
						detail.save();
					}
					count++;
				}				
			}	        
	        ActiveAndroid.setTransactionSuccessful();
	        ActiveAndroid.endTransaction();
	        workbook.close();// 关闭工作簿
        } catch (Exception e) {
        	e.printStackTrace();
        	if (workbook != null) {
				workbook.close();
			}
        	return -1;
        }           
        return count;
    }
	
	/**
	 * 读取Excel表（玩家 ）
	 * 
	 * @param filePath
	 * @return
	 */
	public static int readExcelToPlayer(String filePath) {	        
		Workbook workbook = null;
		int count = 0;
        try {
            // 加载Excel文件
        	InputStream is = new FileInputStream(filePath);
            // 获取workbook
        	workbook = Workbook.getWorkbook(is);
        	Sheet mainSheet = null;
        	Sheet audioSheet = null;
        	for (Sheet sheet : workbook.getSheets()) {
        		if (sheet.getName().equals(mainSheetName)) {
					if (sheet.getColumns() < 7 || sheet.getRows() < 2) {
						return closeExcel(workbook);
					} else {
						mainSheet = sheet;
					}
        		} else if (sheet.getName().equals(audioSheetName)) {
        			audioSheet = sheet;
        		}
        	}
        	if (mainSheet == null) return closeExcel(workbook);
        	List<Player> playerList = new ArrayList<Player>();
        	for (int i = 1; i < mainSheet.getRows(); i++) { // 逐行解析主表
        		String uuid = mainSheet.getCell(0, i).getContents();
        		if (uuid == null || uuid.isEmpty()) continue;
        		String name = mainSheet.getCell(1, i).getContents();
        		if (name == null || name.isEmpty()) continue;
        		String nickName = mainSheet.getCell(2, i).getContents();
        		if (nickName == null || nickName.isEmpty()) continue;
        		String sexString = mainSheet.getCell(3, i).getContents();
        		if (!sexString.equals("M") && !sexString.equals("F")) continue;
        		char sex = sexString.equals("M") ? 'M' : 'F';
        		String sign = mainSheet.getCell(4, i).getContents();
        		String icon = mainSheet.getCell(5, i).getContents();
        		long characterId = Long.parseLong(mainSheet.getCell(6, i).getContents());
        		Player player = new Player(uuid, name, nickName, sex, sign, icon, characterId);
        		playerList.add(player);
        	}
        	List<AudioItem> audioList = new ArrayList<AudioItem>();
        	if (audioSheet != null && audioSheet.getColumns() >= 4) {
				for (int i = 1; i < audioSheet.getRows(); i++) {
					String playerId = audioSheet.getCell(0, i).getContents();
					if (playerId == null || playerId.isEmpty()) continue;
					int type = Integer.parseInt(audioSheet.getCell(1, i).getContents());
					if (type <= 0) continue;
					String path = audioSheet.getCell(2, i).getContents();
					Boolean enable = Integer.parseInt(audioSheet.getCell(3, i).getContents()) == 0 ? false : true;
					AudioItem item = new AudioItem(playerId, type, path, enable);
					audioList.add(item);
				}
			}
        	ActiveAndroid.beginTransaction();
	        for (int i = 0; i < playerList.size(); i++) {
	        	Player player = playerList.get(i);
	        	Player tmp = new Select().from(Player.class)
	        			.where(Player.Col_Uuid + "=?", player.getUuid())
		        		.executeSingle();
	        	if (tmp == null) {
	        		player.save();
					count++;
				}				
			}	        
	        for (int i = 0; i < audioList.size(); i++) {
				AudioItem audio = audioList.get(i);
				AudioItem tmp = new Select().from(AudioItem.class)
						.where(AudioItem.Col_PlayerId + "=? and " + AudioItem.Col_Type + "=?", 
								audio.getPlayerId(), audio.getType())
						.executeSingle();
				if (tmp == null) {
					audio.save();
				}
			}
	        ActiveAndroid.setTransactionSuccessful();
	        ActiveAndroid.endTransaction();
	        workbook.close();// 关闭工作簿
        } catch (Exception e) {
        	e.printStackTrace();
        	if (workbook != null) {
				workbook.close();
			}
        	return -1;
        } 
        return count;
	}
	
	
	/**
	 * 关闭工作表
	 * 
	 * @param workbook
	 * @return
	 */
	private static int closeExcel(Workbook workbook) {
		workbook.close();
		return -1;
	}
	
}

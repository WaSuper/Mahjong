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
import com.mahjong.model.Character;
import com.mahjong.model.CharacterIcon;
import com.mahjong.model.MjDetail;
import com.mahjong.model.MjResult;
import com.mahjong.model.Player;
import com.mahjong.model.SoundBox;

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
	private static final String soundboxSheetName = "soundbox";
	private static final String characterSheetName = "character";
	private static final String iconSheetName = "icon";
	
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
				// database ver.6
				mainSheet.addCell(new Number(27, row, result.getRetPoint()));
				// database ver.7
				mainSheet.addCell(new Number(28, row, result.getMemberCount()));
				mainSheet.addCell(new Number(29, row, result.getMainType()));
				mainSheet.addCell(new Number(30, row, result.getExtraData()));
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
	 * @param playerList 玩家数据
	 * @param audioList 音频数据
	 * @param soundboxList 音频包数据
	 * @param characterList 表情包数据
	 * @param iconList 表情数据
	 * @return 
	 */
	public static boolean createExcelFromPlayer(String filePath, String fileName, 
			List<Player> playerList, List<AudioItem> audioList, List<SoundBox> soundboxList,
			List<Character> characterList, List<CharacterIcon> iconList) {
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
					audioSheet.addCell(new Number(4, row, item.getSoundBoxId()));
				}
			}
			WritableSheet soundboxSheet = writableWorkbook.createSheet(soundboxSheetName, 2); // 创建音频包表
			addSheetTitle(soundboxSheet, SoundBox.Columns); // 添加标题
			if (soundboxList != null && soundboxList.size() > 0) {
				for (int i = 0; i < soundboxList.size(); i++) {
					SoundBox soundbox = soundboxList.get(i);
					int row = i + 1;
					soundboxSheet.addCell(new Number(0, row, soundbox.getUuid()));
					soundboxSheet.addCell(new Label(1, row, soundbox.getName()));
					soundboxSheet.addCell(new Label(2, row, soundbox.getDefaultIcon()));
					soundboxSheet.addCell(new Number(3, row, soundbox.getIndex()));
					soundboxSheet.addCell(new Label(4, row, soundbox.getDescription()));
				}
			}
			WritableSheet characterSheet = writableWorkbook.createSheet(characterSheetName, 3); // 创建表情包表
			addSheetTitle(characterSheet, Character.Columns); // 添加标题
			if (characterList != null && characterList.size() > 0) {
				int row = 1;
				for (int i = 0; i < characterList.size(); i++) {
					Character character = characterList.get(i);
					if (character.getUuid() < 0) continue; // -1为默认表情包，不需要保存
					characterSheet.addCell(new Number(0, row, character.getUuid()));
					characterSheet.addCell(new Label(1, row, character.getName()));
					characterSheet.addCell(new Label(2, row, character.getDefaultIcon()));
					characterSheet.addCell(new Number(3, row, character.getIndex()));
					characterSheet.addCell(new Label(4, row, character.getDescription()));
					row++;
				}
			}
			WritableSheet iconSheet = writableWorkbook.createSheet(iconSheetName, 4); // 创建表情表
			addSheetTitle(iconSheet, CharacterIcon.Columns); // 添加标题
			if (iconList != null && iconList.size() > 0) {
				for (int i = 0; i < iconList.size(); i++) {
					CharacterIcon icon = iconList.get(i);
					int row = i + 1;
					iconSheet.addCell(new Number(0, row, icon.getCharacterId()));
					iconSheet.addCell(new Label(1, row, icon.getPath()));
					iconSheet.addCell(new Number(2, row, icon.getRank()));
					iconSheet.addCell(new Number(3, row, icon.getIndex()));
					iconSheet.addCell(new Label(4, row, icon.getName()));
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
	 * @param filePath 文件路径
	 * @param updateType 更新排位的种类（0：四麻，1：三麻， 2：17步）
	 * @return
	 */
	public static int readExcelToResult(String filePath, boolean[] updateType) {	        
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
        		if (gameType < 0) continue;
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
        		String eastName = mainSheet.getCell(6, i).getContents();
        		int eastPoint = Integer.parseInt(mainSheet.getCell(7, i).getContents());
        		int eastRank = Integer.parseInt(mainSheet.getCell(8, i).getContents());
        		float eastMa = Float.parseFloat(mainSheet.getCell(9, i).getContents());
        		String southId = mainSheet.getCell(10, i).getContents();
        		String southName = mainSheet.getCell(11, i).getContents();
        		int southPoint = Integer.parseInt(mainSheet.getCell(12, i).getContents());
        		int southRank = Integer.parseInt(mainSheet.getCell(13, i).getContents());
        		float southMa = Float.parseFloat(mainSheet.getCell(14, i).getContents());
        		String westId = mainSheet.getCell(15, i).getContents();
        		String westName = mainSheet.getCell(16, i).getContents();
        		int westPoint = Integer.parseInt(mainSheet.getCell(17, i).getContents());
        		int westRank = Integer.parseInt(mainSheet.getCell(18, i).getContents());
        		float westMa = Float.parseFloat(mainSheet.getCell(19, i).getContents());
        		String northId = mainSheet.getCell(20, i).getContents();
        		String northName = mainSheet.getCell(21, i).getContents();
        		int northPoint = Integer.parseInt(mainSheet.getCell(22, i).getContents());
        		int northRank = Integer.parseInt(mainSheet.getCell(23, i).getContents());
        		float northMa = Float.parseFloat(mainSheet.getCell(24, i).getContents());        		
        		MjResult result = new MjResult(gameType, basePoint, ma_points, start_time, 
        				eastId, eastName, southId, southName, westId, westName, northId, northName,
        				5000, 4, 0, 0);
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
        		if (mainSheet.getColumns() >= 28) { // database ver.6
        			int retPoint = Integer.parseInt(mainSheet.getCell(27, i).getContents());
        			result.setRetPoint(retPoint);
				}
        		int member = 4;
        		if (mainSheet.getColumns() >= 30) { // database ver.7
        			member = Integer.parseInt(mainSheet.getCell(28, i).getContents());
        			result.setMemberCount(member);
        			
        			int type = Integer.parseInt(mainSheet.getCell(29, i).getContents());
        			result.setMainType(type);
        			if (type >=0 && type <= 2) {
						updateType[type] = true; // 需要通知对应的排位表进行更新
					}
        			int extra = Integer.parseInt(mainSheet.getCell(30, i).getContents());
        			result.setExtraData(extra);
				} else {
					updateType[0] = true; // 默认更新四麻的排位表
				}
        		// 根据人数判断玩家数据是否正确
        		switch (member) {
				case 4:
					if (northId == null || northId.isEmpty()) continue;
					if (northName == null || northName.isEmpty()) continue;
					if (northRank < 1 || northRank > 4) continue;
				case 3:
					if (southId == null || southId.isEmpty()) continue;
					if (southName == null || southName.isEmpty()) continue;
					if (southRank < 1 || southRank > 4) continue;
				case 2:
					if (eastId == null || eastId.isEmpty()) continue;
					if (eastName == null || eastName.isEmpty()) continue;
					if (eastRank < 1 || eastRank > 4) continue;
					if (westId == null || westId.isEmpty()) continue;
					if (westName == null || westName.isEmpty()) continue;
					if (westRank < 1 || westRank > 4) continue;
				default:
					break;
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
        	Sheet soundboxSheet = null;
        	Sheet characterSheet = null;
        	Sheet iconSheet = null;
        	for (Sheet sheet : workbook.getSheets()) {
        		if (sheet.getName().equals(mainSheetName)) {
					if (sheet.getColumns() < 7 || sheet.getRows() < 2) {
						return closeExcel(workbook);
					} else {
						mainSheet = sheet;
					}
        		} else if (sheet.getName().equals(audioSheetName)) {
        			audioSheet = sheet;
        		} else if (sheet.getName().equals(soundboxSheetName)) {
        			soundboxSheet = sheet;
        		} else if (sheet.getName().equals(characterSheetName)) {
        			characterSheet = sheet;
        		} else if (sheet.getName().equals(iconSheetName)) {
        			iconSheet = sheet;
        		}
        	}
        	// 解析玩家表
        	if (mainSheet == null) return closeExcel(workbook);
        	List<Player> dbPlayers = Player.getAllPlayer();
        	Map<String, Player> allPlayerIds = new HashMap<String, Player>(); // 存储所有玩家id
        	for (Player player : dbPlayers) {
				allPlayerIds.put(player.getUuid(), player);
			}
        	List<Player> newPlayerList = readPlayerSheet(mainSheet, allPlayerIds);
        	// 解析音频包表
        	List<SoundBox> dbSoundBoxs = SoundBox.getAllSoundBoxs();
        	Map<Long, SoundBox> allSoundBoxIds = new HashMap<Long, SoundBox>(); // 存储所有音频包id
        	for (SoundBox soundbox : dbSoundBoxs) {
				allSoundBoxIds.put(soundbox.getUuid(), soundbox);
			}
        	List<SoundBox> newSoundboxList = readSoundBoxSheet(soundboxSheet, allSoundBoxIds);
        	// 解析音频表
        	List<AudioItem> newAudioList = readAudioItemSheet(audioSheet, allPlayerIds);
        	// 解析表情包表
        	List<Character> dbCharacters = Character.getAllCharacters();
        	Map<Long, Character> allCharacterIds = new HashMap<Long, Character>(); // 存储所有表情包id
        	for (Character character : dbCharacters) {
        		allCharacterIds.put(character.getUuid(), character);
        	}
        	List<Character> newCharacterList = readCharactersheet(characterSheet, allCharacterIds);
        	// 解析表情表
        	List<CharacterIcon> newIconList = readIconSheet(iconSheet, allCharacterIds);
        	// 保存数据
        	ActiveAndroid.beginTransaction();
	        for (int i = 0; i < newPlayerList.size(); i++) {
	        	newPlayerList.get(i).save();	
			}	 
	        for (int i = 0; i < newSoundboxList.size(); i++) {
	        	newSoundboxList.get(i).save();
			}       
	        for (int i = 0; i < newAudioList.size(); i++) {
				newAudioList.get(i).save();
			}
	        for (int i = 0; i < newCharacterList.size(); i++) {
	        	newCharacterList.get(i).save();
			}
	        for (int i = 0; i < newIconList.size(); i++) {
				newIconList.get(i).save();
			}
	        count = newPlayerList.size(); // 返回新增玩家数量
	        if (count == 0) {
	        	boolean isImportExtra = newSoundboxList.size() > 0 || newAudioList.size() > 0
	        			|| newCharacterList.size() > 0 || newIconList.size() > 0;
	        	if (isImportExtra) count = -2;
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
	 * 解析玩家表
	 * 
	 * @param mainSheet
	 * @param allPlayerIds
	 * @return
	 */
	private static List<Player> readPlayerSheet(Sheet mainSheet, Map<String, Player> allPlayerIds) {
		List<Player> newPlayerList = new ArrayList<Player>();
    	for (int i = 1; i < mainSheet.getRows(); i++) { // 逐行解析主表
    		String uuid = mainSheet.getCell(0, i).getContents();
    		if (uuid == null || uuid.isEmpty()) continue;
    		Player tmpPlayer = allPlayerIds.get(uuid);
    		if (tmpPlayer != null) continue; // 存在的玩家跳过
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
    		Player player = new Player(uuid, name, nickName, sex, sign, icon, characterId, -1);
    		newPlayerList.add(player);
    		allPlayerIds.put(uuid, player);    		
    	}
    	return newPlayerList;
	}
	
	/**
	 * 解析音频包表
	 * 
	 * @param soundboxSheet
	 * @param allSoundBoxIds
	 * @return
	 */
	private static List<SoundBox> readSoundBoxSheet(Sheet soundboxSheet, Map<Long, SoundBox> allSoundBoxIds) {
		List<SoundBox> newSoundboxList = new ArrayList<SoundBox>();
    	if (soundboxSheet != null && soundboxSheet.getColumns() >= 5) {
			for (int i = 1; i < soundboxSheet.getRows(); i++) {
				String soundboxString = soundboxSheet.getCell(0, i).getContents();
				long soundboxId = -1;
				if (soundboxString != null && !soundboxString.isEmpty()) {
					soundboxId = Long.parseLong(soundboxString);
				}
				if (soundboxId < 0) continue;
				SoundBox tmpBox = allSoundBoxIds.get(soundboxId);
				if (tmpBox != null) continue; // 存在的音频包跳过
				String name = soundboxSheet.getCell(1, i).getContents();
				String icon = soundboxSheet.getCell(2, i).getContents();
				int index = Integer.parseInt(soundboxSheet.getCell(3, i).getContents());
				String description = soundboxSheet.getCell(4, i).getContents();
				SoundBox soundbox = new SoundBox(soundboxId, name, icon, index, description);
				newSoundboxList.add(soundbox);
				allSoundBoxIds.put(soundboxId, soundbox);
			}
		}
    	return newSoundboxList;
	}
	
	/**
	 * 解析音频表
	 * 
	 * @param audioSheet
	 * @param allPlayerIds
	 * @return
	 */
	private static List<AudioItem> readAudioItemSheet(Sheet audioSheet, Map<String, Player> allPlayerIds) {
		List<AudioItem> newAudioList = new ArrayList<AudioItem>();
		if (audioSheet != null && audioSheet.getColumns() >= 5) {
        	List<AudioItem> dbAudioItems = AudioItem.getAllAudioItems();
        	Map<String, AudioItem> type0List = new HashMap<String, AudioItem>();
        	Map<String, AudioItem> type1List = new HashMap<String, AudioItem>();
        	List<AudioItem> typeElseList = new ArrayList<AudioItem>();
        	for (AudioItem item : dbAudioItems) {
				switch (item.getType()) {
				case AudioTool.Type_SoundBox:
					type0List.put(item.getPlayerId() ,item);
					break;
				case AudioTool.Type_Lizhi_BGM:
					type1List.put(item.getPlayerId() ,item);
					break;
				default:
					typeElseList.add(item);
					break;
				}
			}
			for (int i = 1; i < audioSheet.getRows(); i++) {
				String playerId = audioSheet.getCell(0, i).getContents();
				String soundboxString = audioSheet.getCell(4, i).getContents();
				long soundboxId = -1;
				if (soundboxString != null && !soundboxString.isEmpty()) {
					soundboxId = Long.parseLong(soundboxString);
				}
				int type = Integer.parseInt(audioSheet.getCell(1, i).getContents());
				if (type < 0) continue;
				switch (type) {
				case AudioTool.Type_SoundBox:
					if (playerId == null || playerId.isEmpty()) continue;
					if (allPlayerIds.get(playerId) == null) continue;
					if (type0List.get(playerId) != null) continue;
					break;
				case AudioTool.Type_Lizhi_BGM:
					if (playerId == null || playerId.isEmpty()) continue;
					if (allPlayerIds.get(playerId) == null) continue;
					if (type1List.get(playerId) != null) continue;
					break;
				default:
					boolean isExist = false;
					for (AudioItem tmpItem : typeElseList) {
						if (tmpItem.getType() == type && tmpItem.getSoundBoxId() == soundboxId) {
							isExist = true;
							break;
						}
					}
					if (isExist) continue;
					break;
				}
				String path = audioSheet.getCell(2, i).getContents();
				Boolean enable = Integer.parseInt(audioSheet.getCell(3, i).getContents()) == 0 ? false : true;
				AudioItem item = new AudioItem(playerId, soundboxId, type, path, enable);
				newAudioList.add(item);
				switch (type) {
				case AudioTool.Type_SoundBox:
					type0List.put(item.getPlayerId(), item);
					break;
				case AudioTool.Type_Lizhi_BGM:
					type1List.put(item.getPlayerId(), item);
					break;
				default:
					typeElseList.add(item);
					break;
				}
			}
		}
		return newAudioList;
	}
	
	/**
	 * 解析表情包表
	 * 
	 * @param characterSheet
	 * @param allCharacterIds
	 * @return
	 */
	private static List<Character> readCharactersheet(Sheet characterSheet, Map<Long, Character> allCharacterIds) {
		List<Character> newCharacterList = new ArrayList<Character>();
    	if (characterSheet != null && characterSheet.getColumns() >= 5) {
			for (int i = 1; i < characterSheet.getRows(); i++) {
				String characterString = characterSheet.getCell(0, i).getContents();
				long characterId = -1;
				if (characterString != null && !characterString.isEmpty()) {
					characterId = Long.parseLong(characterString);
				}
				if (characterId < 0) continue;
				Character tmpCharacter = allCharacterIds.get(characterId);
				if (tmpCharacter != null) continue; // 存在的表情包跳过
				String name = characterSheet.getCell(1, i).getContents();
				String icon = characterSheet.getCell(2, i).getContents();
				int index = Integer.parseInt(characterSheet.getCell(3, i).getContents());
				String description = characterSheet.getCell(4, i).getContents();
				Character character = new Character(characterId, name, icon, index, description);
				newCharacterList.add(character);
				allCharacterIds.put(characterId, character);
			}
		}
    	return newCharacterList;
	}
	
	/**
	 * 解析表情表
	 * 
	 * @param iconSheet
	 * @return
	 */
	private static List<CharacterIcon> readIconSheet(Sheet iconSheet, Map<Long, Character> allCharacterIds) {
		List<CharacterIcon> newIconList = new ArrayList<CharacterIcon>();
    	if (iconSheet != null && iconSheet.getColumns() >= 5) {
    		List<CharacterIcon> dbCharacterIcons = CharacterIcon.getAllCharacterIcons();
    		List<List<CharacterIcon>> rankList = new ArrayList<List<CharacterIcon>>();
    		// 分成4个等级的列表
    		for (int i = 0; i < 4; i++) {
				rankList.add(new ArrayList<CharacterIcon>());
			}
    		for (CharacterIcon icon : dbCharacterIcons) {
    			int rank = icon.getRank();
    			rankList.get(rank - 1).add(icon);
    		}
			for (int i = 1; i < iconSheet.getRows(); i++) {
				long id = Long.parseLong(iconSheet.getCell(0, i).getContents());
				if (id < 0) continue;
				if (allCharacterIds.get(id) == null) continue;
				String path = iconSheet.getCell(1, i).getContents();
				if (path == null || path.isEmpty()) continue;
				int rank = Integer.parseInt(iconSheet.getCell(2, i).getContents());
				if (rank < 0 || rank > 4) continue;
				List<CharacterIcon> curIconList = rankList.get(rank - 1);
				boolean isExist = false;
				for (CharacterIcon icon : curIconList) {
					if (id == icon.getCharacterId() && path.equals(icon.getPath())) {
						isExist = true;
						break;
					}
				}
				if (isExist) continue;
				int index = Integer.parseInt(iconSheet.getCell(3, i).getContents());
				String name = iconSheet.getCell(4, i).getContents();
				CharacterIcon characterIcon = new CharacterIcon(id, path, rank, index, name);
				newIconList.add(characterIcon);
				curIconList.add(characterIcon);
			}
		}
    	return newIconList;
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

package com.mahjong.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

public class FileTools {
	private static FileTools mFileTools;
	final public static String MEMORY_FILE_PATH = "MEMORY_FILE_PATH";
	final public static String MEMORY_LISTVIEW_POS = "MEMORY_LISTVIEW_POS";
	final public static String MEMORY_LISTVIE_TOP = "MEMORY_LISTVIE_TOP";
	public static final String[] Music_supportTypeArray = { "mp3", "wav", "ogg",
			"ape", "flac", "m4a", "aac"};
	public static final String[] mimeType = { "audio/x-mpeg", "audio/x-wav", "audio/ogg",
		"audio/ape", "audio/x-ms-wma", "audio/x-aiff", "audio/x-aiff", "audio/dsf", "audio/dff",
		"audio/flac", "audio/mp4a-latm", "audio/aac", "audio/x-mpeg", "audio/x-mpeg",
		"audio/oga", "audio/iso", "text/cue"
	};

	public static final String[] Music_SupportTypeArray = { "mp3", "wav", "ogg",
			"ape", "wma", "aif", "aiff", "dsf", "dff", "flac", "m4a", "aac",
			"mp1", "mp2", "oga", "iso", "cue", "m3u", "m3u8"};
	
	public static final String[] Picture_SupportTypeArray = { 
		"jpg", "jpeg", "png", "webp"};
	
	private FileFitVersionTool fitVersionTool;
	
	public static FileTools getInstance() {
		if (mFileTools == null)
			mFileTools = new FileTools();
		return mFileTools;
	}
	
	public void setFitVersionTool(FileFitVersionTool tool) {
		fitVersionTool = tool;
	}

	public String getUriPath(Context context, Uri uri) {

		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection,
						null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		}

		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public File createFile(String path) {
		File file = new File(path);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}

	public void createFileDir(String path) {
		File songDir = new File(path);
		if (!songDir.exists())
			songDir.mkdirs();

	}

	public boolean isFileExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	public boolean deleteDirectory(String filePath) {
		boolean flag = false;
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		File dirFile = new File(filePath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		boolean isDeleteSucess = dirFile.delete();
		if (!isDeleteSucess) {
			// check the new delete method
			if (fitVersionTool.checkErrorVersion()) {
				Uri uri = fitVersionTool.getDocumentUri(filePath);
				isDeleteSucess = fitVersionTool.deleteFile(uri);
			}	
		}
		return isDeleteSucess;
	}
	
	public boolean copyFile(String oldFile, String newFile) {
		File srcFile = new File(oldFile);
		File destFile = new File(newFile);
		// 复制文件  
        int byteread = 0; // 读取的字节数  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(srcFile);  
            out = new FileOutputStream(destFile);  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
            return true;  
        } catch (FileNotFoundException e) {  
        	if (in != null && out == null) {
        		if (fitVersionTool.checkErrorVersion()) {
        			Uri uri = fitVersionTool.getDocumentUri(destFile.getParentFile().getAbsolutePath());
        			out = fitVersionTool.createFile(uri, destFile.getName());
        			if (out == null) {
        				return false;
        			}			
        			try {
        				byte[] byt = new byte[1024];
        				int c;
        				while ((c = in.read(byt)) > 0) {
        					out.write(byt, 0, c);
        				}        				
        				return true;
        			} catch (IOException e1) {
        				e1.printStackTrace();
        				return false;
        			}
        		}
        	}
            return false;  
        } catch (IOException e) {  
            return false;  
        } finally {  
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
	}

	public boolean deleteFile(String filePath) {
		File file = new File(filePath);

		if (file.isFile() && file.exists()) {
			boolean isDeleteSucess = file.delete();
			if (!isDeleteSucess) {
				// check the new delete method
				if (fitVersionTool.checkErrorVersion()) {
					Uri uri = fitVersionTool.getDocumentUri(filePath);
					isDeleteSucess = fitVersionTool.deleteFile(uri);
				}	
			}			
			return isDeleteSucess;
		}
		return false;
	}

	public String getFileName(String path) {
		String nameString = null;
		if (path != null && path.contains("/")) {
			nameString = path.substring(path.lastIndexOf("/") + 1);
		} else {
			nameString = path;
		}
		return nameString;
	}


	public boolean renameFile(String file, String toFile) {

		File toBeRenamed = new File(file);
		if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
//			System.out.println("File does not exist: " + file);
			return false;
		}

		File newFile = new File(toFile);
		if (newFile.exists()) {
			return false;
		}

		boolean isReName = false;
		isReName = toBeRenamed.renameTo(newFile);
		if (!isReName) {
			if (fitVersionTool.checkErrorVersion()) {
				Uri uri = fitVersionTool.getDocumentUri(toBeRenamed.getAbsolutePath());
				isReName = fitVersionTool.renameTo(uri, newFile.getName());
			}
		}
		if (isReName) {
			System.out.println("File has been renamed.");
		} else {
			System.out.println("Error renmaing file");
		}
		return isReName;
	}

	
	static public List<File> initSDcardList(Context mActivity) {
		StorageManager sm = (StorageManager) mActivity.getSystemService(Context.STORAGE_SERVICE);  
		// 获取sdcard的路径：外置和内置  
		List<File> SDcardfileList = new ArrayList<File>();
		try {
			String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", new Class[0]).invoke(sm, new Object[]{});
			for (int i = 0; i < paths.length; i++) {
				File file = new File(paths[i]);
				String invoke = (String) sm.getClass().getMethod("getVolumeState", String.class).invoke(sm, file.getAbsolutePath());
				if(file!=null && "mounted".equals(invoke))
					SDcardfileList.add(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SDcardfileList;
	}
	
	static public boolean getstate(Context mActivity, String path){
		try {
			StorageManager sm = (StorageManager) mActivity.getSystemService(Context.STORAGE_SERVICE);  
			String invoke = (String) sm.getClass().getMethod("getVolumeState", String.class).invoke(sm, path);
			if("mounted".equals(invoke)){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean isInitRootPath2(Context mActivity, String path){
		
		if(TextUtils.isEmpty(path))
			return true;
		
		StorageManager sm = (StorageManager) mActivity.getSystemService(Context.STORAGE_SERVICE);  
		// 获取sdcard的路径：外置和内置  
		
			try {
				String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", new Class[0]).invoke(sm, new Object[]{});
				for(String s : paths){
					if(s.equals(path)){
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		return false;
	}
	
	public static String getExtension(String absPath) {
		int index = -1;
		if (absPath.contains("."))
			index = absPath.lastIndexOf(".");
		if (index == -1) {
			return "";
		}
		return absPath.substring(index + 1);
	}

	public static String getFileNameNoEx(String filename) {
		if (filename != null && filename.length() > 0) {
			String name = filename;
			if (name.contains("/")) {
				name = name.substring(name.lastIndexOf("/") + 1);
			}
			int dot = name.lastIndexOf('.');
			if (dot > -1 && dot < name.length()) {
				return name.substring(0, dot);
			}			
		}
		return filename;
	}
	
	public static void getDatabaseFiles(Context context){
        //找到文件的路径  /data/data/包名/databases/数据库名称
        File dbFile = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/"
        		+ context.getPackageName() + "/databases/mahjong.db");
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            //将数据库文件复制到sd卡的根目录
            fis = new FileInputStream(dbFile);
            fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/mahjong.db");
            int len = 0;
            byte[] buffer = new byte[2048];
            while(-1!=(len=fis.read(buffer))){
                fos.write(buffer, 0, len);
            }
            fos.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //完成之后关闭输入输出流
            try {
                if (fos != null) fos.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

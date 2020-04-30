package com.mahjong.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Document;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.List;

import com.mahjong.R;
import com.mahjong.ui.CommonDialog;

public class FileFitVersionTool {

	public final static String TAG = "FileFitVersionTool";
	
	public final static String DOCUMENT_TREE_STRING = "DOCUMENT_TREE_STRING";
	public final static String DOCUMENT_ROOT_TREE_STRING = "DOCUMENT_ROOT_TREE_STRING";
	
	public final static int RequestCode = 33;
	
	public final static String StartUri = "content://com.android.externalstorage.documents/tree/";
	public final static String InnerStorageUri = "/tree/primary";	
	
	enum AndroidVersion {
		Above_6_0, Above_5_0, Above_4_4, Below_4_4
	}
	
//	private static FileFitVersionTool instance;
	private Activity mActivity;
	private Context mContext;
	private Handler mHandler;
	
	private AndroidVersion version;
	
	private Uri mainUri;
	public String rootUri = "";
//	private DocumentFile documentFile;
	
	private List<File> roots;
	private int index = 0;

	private CommonDialog mTipDialog;
	
//	public static FileFitVersionTool getInstance() {
//		return instance;
//	}
//
//	public static void initTool(Context context, Handler handler) {
//		if (instance == null) {
//			instance = new FileFitVersionTool(context, handler);
//		}		
//	}
	
	public FileFitVersionTool(Activity activity, Handler handler) {
		mActivity = activity;
		mContext = activity;
		mHandler = handler;
		if (Build.VERSION.SDK_INT >= 23) {	
			version = AndroidVersion.Above_6_0;
		} else if (Build.VERSION.SDK_INT >= 21) {	
			version = AndroidVersion.Above_5_0;
		} else if (Build.VERSION.SDK_INT >= 19) {	
			version = AndroidVersion.Above_4_4;
		} else {
			version = AndroidVersion.Below_4_4;
		}
		roots = FileTools.initSDcardList(mContext);
		String innerSDcard = Environment.getExternalStorageDirectory().toString();
		for (int i = 0; i < roots.size(); i++) {
			File file = roots.get(i);
			if (file.getAbsolutePath().contains(innerSDcard)) {
				index = i;
				break;
			}
		}
	}	
	
	public void requestContentProviderCallback(Intent data) {
		mainUri = data.getData();				
		System.out.println(mainUri.getPath());
		System.out.println(mainUri.getAuthority());
		// Set directory as default in preferences
		String uriString = mainUri.toString();
		int index = uriString.indexOf("%3A");				
		if (index > 0) { 
			// save the sdcard root uri
			rootUri = uriString.substring(0, index) + ":";
			ShareprefenceTool.getInstance().setString(
					DOCUMENT_ROOT_TREE_STRING, rootUri, mContext);	
		}
		System.out.println(uriString);
		ShareprefenceTool.getInstance().setString(
				DOCUMENT_TREE_STRING, uriString, mContext);				
		// grant write permissions				
        mContext.getContentResolver().takePersistableUriPermission(mainUri, 
        		Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        documentFile = DocumentFile.fromSingleUri(mContext, mainUri);	
	}
	
	public boolean checkErrorVersion() {
		if (version == AndroidVersion.Above_5_0 || version == AndroidVersion.Above_6_0) {
			String tree = ShareprefenceTool.getInstance()
					.getString(DOCUMENT_TREE_STRING, mContext, "");
			if (!tree.equals("")) {
				System.out.println(DOCUMENT_TREE_STRING + " : " + tree);
				mainUri = Uri.parse(tree);
				// grant write permissions
	            mContext.getContentResolver().takePersistableUriPermission(mainUri, 
	            		Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//	            documentFile = DocumentFile.fromSingleUri(mContext, mainUri);
	            rootUri = ShareprefenceTool.getInstance()
							.getString(DOCUMENT_ROOT_TREE_STRING, mContext, "");
	            return true;
			} else {
				showChooseDialog(true);
				return false;
			}		
		}
		return false;
	}
	
	public boolean setDocumentTree() {
		if (mActivity != null && mActivity.isTaskRoot()) {
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
			mActivity.startActivityForResult(intent, RequestCode);
			return true;
		}
		return false;
	}
	
	public void showChooseDialog(final boolean isFirst) {		
		if (mTipDialog != null && mTipDialog.isShowing()) {
			return;
		}
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				if (mTipDialog == null) {
					mTipDialog = new CommonDialog(
							mContext, R.style.MyDialogStyle);
					mTipDialog.setCanceledOnTouchOutside(true);
					mTipDialog.addView(R.layout.item_text);
					TextView okTextView = mTipDialog.ok;
					TextView cancelView = mTipDialog.cancel;
					TextView titleTextView = mTipDialog.titleTextView;		
					titleTextView.setText(mContext.getString(R.string.tip));					
					okTextView.setText(mContext.getString(R.string.select));
					okTextView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							setDocumentTree();
							mTipDialog.cancel();				
						}
					});
					cancelView.setText(mContext.getString(R.string.cancel));
					cancelView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							mTipDialog.cancel();				
						}
					});
				}
				TextView contentText = (TextView) mTipDialog.getContentView().findViewById(R.id.item_text);
				String text = mContext.getString(R.string.grant_authorization);
				if (!isFirst) {
					text = mContext.getString(R.string.no_authorization) + "\n" + text;
				}
				contentText.setText(text);						
				mTipDialog.show();	
			}
		});
		
	}
	
	/*********************************** file function ********************************/
	public Uri getDocumentUri(String path) {
		String innerSDcard = roots.get(index).getPath();
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		if (file.isDirectory() && !path.endsWith("/")) {
			path += "/";
		}
		if (path.contains(innerSDcard)) {
			return null;
		}
		for (int i = 0; i < roots.size(); i++) {
			if (i == index) {
				continue;
			}
			String sdPath = roots.get(i).getPath() + "/";
			if (path.contains(sdPath)) {
				int index = path.indexOf(sdPath);
				if (index != -1) {
					path = path.substring(index + sdPath.length());
					if (!rootUri.equals("")) {
						path = rootUri + path;
						if (checkIsChildPath(path)) {
							path = path.substring(StartUri.length());
							Uri curUri = DocumentsContract.buildDocumentUriUsingTree(mainUri, path);
							return curUri;
						} else {
							showChooseDialog(false);
							return null;
						}						
					} else {
						return null;
					}					
				}
				break;
			}
		}		
		return null;
	}
	
	public boolean checkIsChildPath(String path) {
		String temp = mainUri.toString().replaceFirst("%3A", ":").replace("%2F", "/");
		return path.startsWith(temp);
	}
	
	public boolean createDirectory(Uri uri, String displayName) {
		if (uri != null && DocumentsContract.isDocumentUri(mContext, uri)) {
			if (DocumentsContract.createDocument(
					mContext.getContentResolver(), uri, Document.MIME_TYPE_DIR, displayName) != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public OutputStream createFile(Uri uri, String displayName) {
		if (uri != null && DocumentsContract.isDocumentUri(mContext, uri)) {
			try {
				String ext = FileTools.getExtension(displayName);
				String mimeType = "audio/*";
				if (ext == null) {
					mimeType = "audio/*";
				} else {
					for (int i = 0; i < FileTools.supportTypeArray.length; i++) {
						if (ext.equals(FileTools.supportTypeArray[i])) {
							mimeType = FileTools.mimeType[i];
							break;
						}
					}
				}				
				Uri fileUri = DocumentsContract.createDocument(
						mContext.getContentResolver(), uri, mimeType, displayName);
				if (fileUri != null) {
					OutputStream out = mContext.getContentResolver().openOutputStream(fileUri);
					return out;
				}
				return null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
	public boolean deleteFile(Uri uri) {
		if (uri != null && DocumentsContract.isDocumentUri(mContext, uri)) {
			if (DocumentsContract.deleteDocument(
					mContext.getContentResolver(), uri)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean renameTo(Uri uri, String displayName) {
		if (uri != null && DocumentsContract.isDocumentUri(mContext, uri)) {
			if (DocumentsContract.renameDocument(
					mContext.getContentResolver(), uri, displayName) != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	
	/*********************************** other ****************************************/
	public AndroidVersion getVersion() {
		return version;
	}
	
}

package com.mahjong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.adapter.PlayerSimpleAdapter;
import com.mahjong.item.FileSortModel;
import com.mahjong.model.AudioItem;
import com.mahjong.model.Player;
import com.mahjong.tools.AudioTool;
import com.mahjong.tools.FileTools;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.HorizontalPosition;
import com.mahjong.ui.SmartPopupWindow;
import com.mahjong.ui.VerticalPosition;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FileActivity extends BaseActivity implements View.OnClickListener {

	public static final String FILE_SORT_MODE = "FILE_SORT_MODE";
	public static final int File_Sort_By_letter = 0;
	public static final int File_Sort_By_date 	= 1;
	
	public static final String FileType = "FileType";
	public static final String FileDir 	= "FileDir";
	public static final String FileSelects 	= "FileSelects";
	public static final String FileShowBottom = "FileShowBottom";
	public static final String LastSecletPath = "LastSecletPath";
	public static final String GlobalSetting = "GlobalSetting";
	public static final String AudioType = "AudioType";
	
	public static final int File_All 			= 0x2001;
	public static final int File_Music_Only 	= 0x2002;
	public static final int File_Excel_Only 	= 0x2003;
	public static final int File_Picture_Only 	= 0x2004;
	
	private TextView mBackView;
    private ListView mFileView;
    private TextView mTitleView;
    private ImageView mCancelView;
    private FileAdapter mFileAdapter;
    private LinearLayout mBottomView;
    private Button mChooseAllBtn;
    private Button mOkBtn;
    private ImageView mMoreView;
    private SmartPopupWindow popupWindow;
    private Button[] popupActions;

    private int mFileLevel = 0;
    private List<File> mRootFiles;
    private File mParentFile = null;
    private int mFirstItemPosition = 0;
    private int mFirstItemFromTop = 0;
    private List<Integer> mPositionList = new LinkedList<Integer>();
    private List<Integer> mFromTopList = new LinkedList<Integer>();
    
    private Context mContext;
    
    private int fileType = 1;
    private String lastSelectPath;
    private boolean isShowBottom = false;
    private boolean isGlobalSetting;
    private int mAudioType;
    private List<AudioItem> mAudioList;
    private int mSortMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        mContext = this;
        fileType = getIntent().getIntExtra(FileType, File_All);
        lastSelectPath = getIntent().getStringExtra(FileDir);
        isShowBottom = getIntent().getBooleanExtra(FileShowBottom, false);
        isGlobalSetting = getIntent().getBooleanExtra(GlobalSetting, false);
        mAudioType = getIntent().getIntExtra(AudioType, 0);
        if (mAudioType != 0) {
        	mAudioList = AudioItem.loadItemsByType(mAudioType);
		}
        mSortMode = ShareprefenceTool.getInstance()
        		.getInt(FILE_SORT_MODE, mContext, File_Sort_By_letter);
        initUI();
    }

    private void initUI() {
        mBackView = (TextView) findViewById(R.id.file_back);
        mCancelView = (ImageView) findViewById(R.id.file_exit);
        mFileView = (ListView) findViewById(R.id.file_list);
        mTitleView = (TextView) findViewById(R.id.file_title);
        mBottomView = (LinearLayout) findViewById(R.id.file_bottom);
        mChooseAllBtn = (Button) findViewById(R.id.file_choose_all);
        mOkBtn = (Button) findViewById(R.id.file_ok);
        mMoreView = (ImageView) findViewById(R.id.file_more);

        mRootFiles = FileTools.initSDcardList(mContext);
        mFileAdapter = new FileAdapter(mRootFiles);
        mFileView.setAdapter(mFileAdapter);

        mBackView.setOnClickListener(this);
        mCancelView.setOnClickListener(this);
        mMoreView.setOnClickListener(this);
        mFileView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	goToNextLevel(position);
            }
        });
        mFileView.setOnScrollListener(new OnScrollListenerImple());  
        
        checkLastSelectPath();
        
        if (isShowBottom) {
			mBottomView.setVisibility(View.VISIBLE);
			mChooseAllBtn.setOnClickListener(this);
			mOkBtn.setOnClickListener(this);
		}
    }
    
    private void checkLastSelectPath() {
    	if (lastSelectPath != null && !lastSelectPath.isEmpty()) {
			File lastFile = new File(lastSelectPath);
			File rootFile = null;
			for (File file : mRootFiles) {
				if (lastFile.getPath().startsWith(file.getPath())) {
					rootFile = file;
					break;
				}
			}
			if (rootFile != null) {
				mFileLevel = 1;
				File tmpFile = lastFile;
				while (!tmpFile.getPath().equals(rootFile.getPath())) {
					tmpFile = tmpFile.getParentFile();
					mFileLevel++;
					mPositionList.add(0);
					mFromTopList.add(0);
				}
				mFileAdapter.setData(lastFile);
				mTitleView.setText(lastSelectPath);
			}
		}
    }
    
    @Override
    public void onBackPressed() {
    	if (!backToLastLevel()) {
    		setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_exit:
            	setResult(RESULT_CANCELED);
                this.finish();
                break;
            case R.id.file_back:
                backToLastLevel();
                break;
            case R.id.file_more:
            	showMoreDialog();
            	break;
            case R.id.file_act_sort_by_letter:
            	ShareprefenceTool.getInstance()
            		.setInt(FILE_SORT_MODE, File_Sort_By_letter, mContext);
            	mFileAdapter.sortData(File_Sort_By_letter, false);
            	popupWindow.dismiss();
            	break;
            case R.id.file_act_sort_by_date:
            	ShareprefenceTool.getInstance()
            		.setInt(FILE_SORT_MODE, File_Sort_By_date, mContext);
            	mFileAdapter.sortData(File_Sort_By_date, false);
            	popupWindow.dismiss();
            	break;
            case R.id.file_choose_all: // isShowBottom==true
            	mFileAdapter.selectAllorNot();
            	break;
            case R.id.file_ok: // isShowBottom==true
            	doSelectDir();
            	break;
            default:
                break;
        }
    }
    
    private void showMoreDialog() {
    	if (popupWindow == null) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.layout_file_action, null);
			popupWindow = SmartPopupWindow.Builder
					.build(this, view)
					.setAlpha(0.8f)
					.createPopupWindow();
			popupActions = new Button[2];
			popupActions[0] = (Button) view.findViewById(R.id.file_act_sort_by_letter);
			popupActions[1] = (Button) view.findViewById(R.id.file_act_sort_by_date);
			for (int i = 0; i < popupActions.length; i++) {
				popupActions[i].setOnClickListener(this);
			}			
		}
    	for (int i = 0; i < popupActions.length; i++) {
    		if (i == mSortMode) {
    			popupActions[i].setTextColor(getResources().getColor(R.color.blue));
			} else {
				popupActions[i].setTextColor(getResources().getColor(R.color.black));
			}
    	}
		popupWindow.showAtAnchorView(mMoreView, VerticalPosition.BELOW, HorizontalPosition.RIGHT);
    }
    
    public void doSelectDir() {
    	if (mFileLevel == 0) {
			ToastTool.showToast(mContext, R.string.direction_forbidden);
		} else if (mFileLevel > 0 && mParentFile != null) {
			List<File> selectFiles = mFileAdapter.getSelectList();
			if (selectFiles.size() > 0) {
				Intent intent = new Intent();
	            intent.putExtra(FileDir, mParentFile.getAbsolutePath());
	            String regularEx = "#!#";
	            StringBuffer nameBuffer = new StringBuffer();
	            for (File file : selectFiles) {
					nameBuffer.append(file.getName());
					nameBuffer.append(regularEx);
				}
	            intent.putExtra(FileSelects, nameBuffer.toString());
	            setResult(RESULT_OK, intent);
	            finish();
			} else {
				ToastTool.showToast(mContext, R.string.please_choose_at_least_one);
			}			
		}
    }
    
    private void goToNextLevel(int position) {
    	File file = mFileAdapter.getFileList().get(position);
    	if (file.isDirectory()) {
            mFileAdapter.setData(file);
            mTitleView.setText(file.getPath());
            mFileLevel++;
            mPositionList.add(mFirstItemPosition);
            mFromTopList.add(mFirstItemFromTop);
            mFirstItemPosition = 0;
            mFirstItemFromTop = 0;
        } else {
        	if (!isShowBottom) {
        		if (isGlobalSetting) {
        			showPlayerSelectDialog(file);
				} else {
					Intent intent = new Intent();
	                intent.putExtra(FileDir, file.getAbsolutePath());
	                intent.putExtra(AudioType, mAudioType);
	                setResult(RESULT_OK, intent);
					ShareprefenceTool.getInstance().setString(
							FileActivity.LastSecletPath, file.getParent(), mContext);
	                finish();
				}                			
			}
        }
    }
    
    private void showPlayerSelectDialog(final File file) {
    	final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getString(R.string.please_choose_player));
		mDialog.ok.setText(getResources().getString(R.string.ok));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		ListView listView = (ListView) mDialog.getContentView();
		PlayerSimpleAdapter mAdapter = new PlayerSimpleAdapter(mContext);
		listView.setAdapter(mAdapter);
		final List<Player> list = Player.getAllPlayer();
		mAdapter.setData(list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Player player = list.get(position);
				if (mAudioType != 0) {
					AudioItem audioItem = getAudioItem(player.getUuid());
					audioItem.setFilePath(file.getAbsolutePath());
					audioItem.save();
					ShareprefenceTool.getInstance().setString(
							FileActivity.LastSecletPath, file.getParent(), mContext);
					AudioTool.setAudioHistory(mContext, mAudioType, file.getAbsolutePath());
					ToastTool.showToast(mContext, R.string.success);
				}
				mDialog.dismiss();
			}
		});
		mDialog.show();
    }    
	
	private AudioItem getAudioItem(String playerId) {
		if (mAudioList != null && mAudioList.size() > 0) {
			for (AudioItem audioItem : mAudioList) {
				if (audioItem.getPlayerId().equals(playerId)) {
					return audioItem;
				}
			}
		}
		AudioItem item = new AudioItem(playerId, mAudioType, "", false);	
		mAudioList.add(item);
		return item;		
	}
    
    private boolean backToLastLevel() {
    	if (mFileLevel != 0) {
            if (mFileLevel == 1) {
                mFileAdapter.setRootFiles(mRootFiles);
                mTitleView.setText(getString(R.string.please_select_file));
                mFileLevel--;
                try {
                	mFirstItemPosition = mPositionList.remove(mPositionList.size() - 1);
                    mFirstItemFromTop = mFromTopList.remove(mFromTopList.size() - 1);
				} catch (Exception e) {
					mFirstItemPosition = 0;
					mFirstItemFromTop = 0;
				}                
                mFileView.setSelectionFromTop(mFirstItemPosition, mFirstItemFromTop);
            } else {
                if (mFileAdapter.back()) {
                	mTitleView.setText(mParentFile.getPath());
                	mFileLevel--;
                	try {
                    	mFirstItemPosition = mPositionList.remove(mPositionList.size() - 1);
                        mFirstItemFromTop = mFromTopList.remove(mFromTopList.size() - 1);
    				} catch (Exception e) {
    					mFirstItemPosition = 0;
    					mFirstItemFromTop = 0;
    				} 
                    mFileView.setSelectionFromTop(mFirstItemPosition, mFirstItemFromTop);
                }
            }
            return true;
        } else {
        	return false;
        }
    }

    private class OnScrollListenerImple implements OnScrollListener {
    	
		@Override
		public void onScroll(AbsListView listView, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			
		}
       
		@Override
		public void onScrollStateChanged(AbsListView listview, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				mFirstItemPosition = mFileView.getFirstVisiblePosition();
				View itemView = mFileView.getChildAt(0);
				mFirstItemFromTop = (itemView == null) ? 0 : itemView.getTop();
			}
		}
 
	}
    
    class FileAdapter extends BaseAdapter implements OnCheckedChangeListener {

        private List<File> mFileList = new ArrayList<File>();
        private boolean[] mSelectList;
        private boolean isSelectAll = false;
        
        private List<File> mTmpFiles = new ArrayList<File>();
        private List<File> mTmpDirs = new ArrayList<File>();

        public FileAdapter(List<File> list) {
            for (File file : list) {
                mFileList.add(file);
            }
            if (mFileList != null && mFileList.size() != 0) {
    			mSelectList = new boolean[mFileList.size()];
    			Arrays.fill(mSelectList, false);
    		} else {
    			mSelectList = null;
    		}
            isSelectAll = false;
        }

        public void setData(File parent) {
            mParentFile = parent;
            File[] list = parent.listFiles();
            mTmpDirs.clear();
            mTmpFiles.clear();
            if (list != null) {
            	for (File file : list) {
                    if (file.isDirectory()) {
                    	if (!file.getName().startsWith(".")) {
                    		mTmpDirs.add(file);
    					}                    
                    } else {
                    	switch (fileType) {
    					case File_All:
    						mTmpFiles.add(file);
    						break;
    					case File_Music_Only:
    						for (String s : FileTools.Music_SupportTypeArray) {
    	                        String extm = FileTools.getExtension(file.getName());
    	                        if (extm != null && extm.equalsIgnoreCase(s)) {
    	                        	mTmpFiles.add(file);
                                    break;
    	                        }
    	                    }
    						break;
    					case File_Excel_Only:
    						String ext = FileTools.getExtension(file.getName());
                            if (ext != null && ext.equalsIgnoreCase("xls")) {
                            	mTmpFiles.add(file);
                                break;
                            }
    						break;
    					case File_Picture_Only:
    						for (String s : FileTools.Picture_SupportTypeArray) {
    	                        String extp = FileTools.getExtension(file.getName());
    	                        if (extp != null && extp.equalsIgnoreCase(s)) {
    	                        	mTmpFiles.add(file);
                                    break;
    	                        }
    	                    }
    						break;
    					default:
    						break;
    					}                    
                    }
                }
			}            
            sortData(mSortMode, true);
        }
        
        public void sortData(int mode, boolean isReset) {
        	if (mode != mSortMode || isReset) {
				mSortMode = mode;				
				switch (mSortMode) {
				case File_Sort_By_letter:
					sortByLetter();
					break;
				case File_Sort_By_date:
					sortByDate();
					break;
				default:
					break;
				}
				if (mFileList != null && mFileList.size() != 0) {
	    			mSelectList = new boolean[mFileList.size()];
	    			Arrays.fill(mSelectList, false);
	    		} else {
	    			mSelectList = null;
	    		}
				isSelectAll = false;
				notifyDataSetInvalidated();
			}
        }
        
        private void sortByLetter() {
            mFileList.clear();
        	mFileList.addAll(FileSortModel.sortFile(mTmpDirs));
            mFileList.addAll(FileSortModel.sortFile(mTmpFiles));
        }
        
        private void sortByDate() {
        	mFileList.clear();
        	mFileList.addAll(mTmpDirs);
        	mFileList.addAll(mTmpFiles);
        	Collections.sort(mFileList, new Comparator<File>() {

				@Override
				public int compare(File f0, File f1) {
					return (int)(f1.lastModified() - f0.lastModified());
				}
			});
        }

        public void setRootFiles(List<File> list) {
            mParentFile = null;
            mFileList.clear();
            for (File file : list) {
                mFileList.add(file);
            }
            notifyDataSetChanged();
        }

        public boolean back() {
            if (mParentFile != null) {
                File file = mParentFile.getParentFile();
                setData(file);
                return true;
            }
            return false;
        }

        public List<File> getFileList() {
            return mFileList;
        }
        
        /**
    	 * 全选或取消全选
    	 * 
    	 * @return 是否全选状态
    	 */
    	public boolean selectAllorNot() {
    		if (mSelectList != null) {
    			Arrays.fill(mSelectList, !isSelectAll);	
    			isSelectAll = !isSelectAll;
				notifyDataSetChanged();
				return isSelectAll;
    		} else {
    			return false;
    		}		
    	}
        
        /**
    	 * 获取所有选择项
    	 * 
    	 * @return
    	 */
    	public List<File> getSelectList() {
    		List<File> list = new ArrayList<File>();
    		if (isShowBottom) {
    			for (int i = 0; i < mSelectList.length; i++) {
    				if (mSelectList[i]) {
    					list.add(mFileList.get(i));
    				}
    			}
    		}
    		return list;
    	}

        @Override
        public int getCount() {
            return mFileList != null ? mFileList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mFileList != null ? mFileList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.file_item, null);
                holder = new ViewHolder();
                holder.mImage = (ImageView) convertView.findViewById(R.id.file_image);
                holder.mName = (TextView) convertView.findViewById(R.id.file_text);
                holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.file_checked);  
                holder.mCheckBox.setTag(position);
                holder.mCheckBox.setOnCheckedChangeListener(this);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.mCheckBox.setTag(position);
            }
            File file = mFileList.get(position);
            int drawableId = R.drawable.list_ic_folder;
            if (isShowBottom && file.isDirectory()) {
            	holder.mCheckBox.setVisibility(View.VISIBLE );
            	if (mSelectList != null && position < mSelectList.length) {
            		holder.mCheckBox.setChecked(mSelectList[position]);
				}            	
			} else {
				holder.mCheckBox.setVisibility(View.GONE);
			}
            if (!file.isDirectory()) {
				switch (fileType) {
				case File_Music_Only:
					drawableId = R.drawable.list_ic_music;
					break;
				case File_Excel_Only:
					drawableId = R.drawable.list_ic_excel;
					break;
				case File_Picture_Only:
					drawableId = R.drawable.list_ic_picture;
					break;
				default:
					drawableId = R.drawable.list_ic_file;
					break;
				}
			}
            if (drawableId == R.drawable.list_ic_picture) {
            	ImageLoader.getInstance().displayImage("file://" + file.getPath(), holder.mImage);
			} else {
				ImageLoader.getInstance().displayImage("drawable://" + drawableId, holder.mImage);
			}            
//            holder.mImage.setBackgroundResource(drawableId);
            holder.mName.setText(file.getName());
            return convertView;
        }

        class ViewHolder {
            ImageView mImage;
            TextView mName;
            CheckBox mCheckBox;
        }

		@Override
		public void onCheckedChanged(CompoundButton cbox, boolean checked) {
			int position = (Integer) cbox.getTag();
			if (mSelectList != null && position < mSelectList.length) {
				mSelectList[position] = checked;
			}
		}
    }

}

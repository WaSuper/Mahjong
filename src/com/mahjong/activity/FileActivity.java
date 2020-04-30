package com.mahjong.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.tools.FileTools;
import com.mahjong.tools.ToastTool;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FileActivity extends Activity implements View.OnClickListener {

	public static final String FileType = "FileType";
	public static final String FileDir 	= "FileDir";
	public static final String FileShowBottom = "FileShowBottom";
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
    private Button mCancelBtn;
    private Button mOkBtn;

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
    boolean isShowBottom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        mContext = this;
        fileType = getIntent().getIntExtra(FileType, File_All);
        lastSelectPath = getIntent().getStringExtra(FileDir);
        isShowBottom = getIntent().getBooleanExtra(FileShowBottom, false);
        initUI();
    }

    private void initUI() {
        mBackView = (TextView) findViewById(R.id.file_back);
        mCancelView = (ImageView) findViewById(R.id.file_exit);
        mFileView = (ListView) findViewById(R.id.file_list);
        mTitleView = (TextView) findViewById(R.id.file_title);
        mBottomView = (LinearLayout) findViewById(R.id.file_bottom);
        mCancelBtn = (Button) findViewById(R.id.file_cancel);
        mOkBtn = (Button) findViewById(R.id.file_ok);

        mRootFiles = FileTools.initSDcardList(mContext);
        mFileAdapter = new FileAdapter(mRootFiles);
        mFileView.setAdapter(mFileAdapter);

        mBackView.setOnClickListener(this);
        mCancelView.setOnClickListener(this);
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
			mCancelBtn.setOnClickListener(this);
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
            case R.id.file_cancel:
            	setResult(RESULT_CANCELED);
                this.finish();
                break;
            case R.id.file_back:
                backToLastLevel();
                break;
            case R.id.file_ok:
            	doSelectDir();
            	break;
            default:
                break;
        }
    }
    
    public void doSelectDir() {
    	if (mFileLevel == 0) {
			ToastTool.showToast(mContext, R.string.direction_forbidden);
		} else if (mFileLevel > 0 && mParentFile != null) {
			Intent intent = new Intent();
            intent.putExtra(FileDir, mParentFile.getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();
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
                Intent intent = new Intent();
                intent.putExtra(FileDir, file.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();				
			}
        }
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
    
    class FileAdapter extends BaseAdapter {

        private List<File> mFileList = new ArrayList<File>();

        public FileAdapter(List<File> list) {
            for (File file : list) {
                mFileList.add(file);
            }
        }

        public void setData(File parent) {
            mParentFile = parent;
            File[] list = parent.listFiles();
            mFileList.clear();
            List<File> dirs = new ArrayList<File>();
            List<File> files = new ArrayList<File>();
            for (File file : list) {
                if (file.isDirectory()) {
                	if (!file.getName().startsWith(".")) {
                		dirs.add(file);
					}                    
                } else {
                	switch (fileType) {
					case File_All:
						files.add(file);
						break;
					case File_Music_Only:
						for (String s : FileTools.Music_SupportTypeArray) {
	                        String extm = FileTools.getExtension(file.getName());
	                        if (extm != null && extm.equalsIgnoreCase(s)) {
	                        	files.add(file);
                                break;
	                        }
	                    }
						break;
					case File_Excel_Only:
						String ext = FileTools.getExtension(file.getName());
                        if (ext != null && ext.equalsIgnoreCase("xls")) {
                        	files.add(file);
                            break;
                        }
						break;
					case File_Picture_Only:
						for (String s : FileTools.Picture_SupportTypeArray) {
	                        String extp = FileTools.getExtension(file.getName());
	                        if (extp != null && extp.equalsIgnoreCase(s)) {
	                        	files.add(file);
                                break;
	                        }
	                    }
						break;
					default:
						break;
					}                    
                }
            }
            Collections.sort(dirs);
            Collections.sort(files);
            mFileList.addAll(dirs);
            mFileList.addAll(files);
            notifyDataSetInvalidated();
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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            File file = mFileList.get(position);
            int drawableId = R.drawable.list_ic_folder;
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
        }
    }

}

package com.mahjong.activity;

import com.mahjong.R;
import com.mahjong.tools.HeadIconTool;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.PopWinDownUtil;
import com.mahjong.tools.ToastTool;
import com.mahjong.tools.ValueTool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * （弃用）早期界面
 * 
 * @author Summer Lee
 *
 */
public class HeadIconSelectActivity extends BaseActivity implements OnClickListener {

	public static final int REQUEST_ICON = 1111;
	public static final String ID_ICON = "ID_ICON";
	public static final String ID_FOLDER = "ID_FOLDER";
	
	private Context mContext;
	
	private ImageView mBack;
	private Button mOkBtn;
	private TextView mPreview;
	private LinearLayout mSelectMenu;
	private TextView mMenuText;
	private GridView mGridList;
	private HeadIconAdapter mHeadIconAdapter;
	private RelativeLayout mBottomView;

	private PopWinDownUtil popWinDownUtil;
	private IconFolderAdapter mIconFolderAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_head_select);
		mContext = this;
		initUI();
		initPopUpWin();
	}
	
	private void initUI() {
		mBack = (ImageView) findViewById(R.id.head_select_back);
		mOkBtn = (Button) findViewById(R.id.head_select_ok);
		mPreview = (TextView) findViewById(R.id.head_select_preview);
		mSelectMenu = (LinearLayout) findViewById(R.id.head_select_menu);
		mMenuText = (TextView) findViewById(R.id.head_select_menu_text);
		mGridList = (GridView) findViewById(R.id.head_select_list);
		mBottomView = (RelativeLayout) findViewById(R.id.head_select_rl_bottom);
		
		mMenuText.setText(getString(HeadIconTool.IconFolder[0][1]));
	 	int[] IconSet = HeadIconTool.iconSetSelect(HeadIconTool.IconFolder[0][0]);
		mHeadIconAdapter = new HeadIconAdapter(this, IconSet);
		mGridList.setAdapter(mHeadIconAdapter);
		mGridList.setOnItemClickListener(new OnHeadIconClickListener());
		
		mBack.setOnClickListener(this);
		mOkBtn.setOnClickListener(this);
		mPreview.setOnClickListener(this);
		mSelectMenu.setOnClickListener(this);
	}
	
	private void initPopUpWin() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dropdown_content, null);
		view.findViewById(R.id.dropdown_content).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						popWinDownUtil.hide();
					}
				});
		ListView listView = (ListView) view.findViewById(R.id.dropdown_listview);
		listView.setOnItemClickListener(new OnIconFolderSelectListener());
		mIconFolderAdapter = new IconFolderAdapter(mContext, HeadIconTool.IconFolder);
		listView.setAdapter(mIconFolderAdapter);
		popWinDownUtil = new PopWinDownUtil(mContext, view, mBottomView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_select_back:
			this.finish();
			break;
		case R.id.head_select_ok:
			checkSelectIcon();
			break;
		case R.id.head_select_preview:
			break;
		case R.id.head_select_menu:
			popWinDownUtil.showAsPopUp();
			break;
		default:
			break;
		}
	}
	
	private void checkSelectIcon() {
		int pos = mHeadIconAdapter.getCheckPosition();
		if (pos < 0) {
			ToastTool.showToast(mContext, R.string.please_choose_one);
		} else {
			int curFolder = mIconFolderAdapter.getCurFolder();
			int curIcon = mHeadIconAdapter.getCheckId();
			Intent intent = getIntent();
			intent.putExtra(ID_FOLDER, curFolder);
			intent.putExtra(ID_ICON, curIcon);
			setResult(RESULT_OK, intent);
			this.finish();
		}
	}
	
	private class OnHeadIconClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			mHeadIconAdapter.setCheckPosition(view, position);
		}
		
	}
	
	private class OnIconFolderSelectListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			int[] idArray = mIconFolderAdapter.selectCurFolder(position);
			mHeadIconAdapter.setData(idArray);
			mMenuText.setText(mIconFolderAdapter.selectCurName(position));
			popWinDownUtil.hide();
		}
		
	}
	
	private class HeadIconAdapter extends BaseAdapter {
		 
        private Context mContext;
        private int[] idArray;
        private int checkPos;
        private float radius;
 
        public HeadIconAdapter(Context context, int[] idArray) {
            this.mContext = context;
            this.idArray = idArray;
            this.checkPos = -1;
            this.radius = ValueTool.dp2px(context, 10);
        }
 
        public void setData(int[] idArray) {
            this.idArray = idArray;
            this.checkPos = -1;
            this.notifyDataSetChanged();
        }
        
        public void setCheckPosition(View view, int pos) {
        	int firstIndex = mGridList.getFirstVisiblePosition();
        	if (checkPos == pos) {
				ViewHolder holder = (ViewHolder) mGridList.getChildAt(pos - firstIndex).getTag();
				holder.checkBox.setChecked(false);				
				checkPos = -1;
			} else {
				ViewHolder holder;
				if (checkPos != -1) {
					holder = (ViewHolder) mGridList.getChildAt(checkPos - firstIndex).getTag();
					holder.checkBox.setChecked(false);					
				}
				holder = (ViewHolder) mGridList.getChildAt(pos - firstIndex).getTag();
				holder.checkBox.setChecked(true);
				checkPos = pos;
			}
//        	this.notifyDataSetChanged();
        }
        
        public int getCheckPosition() {
        	return checkPos;
        }
        
        public int getCheckId() {
        	return idArray[checkPos];
        }
        
        @Override
        public int getCount() {
            return idArray.length;
        }
 
        @Override
        public Object getItem(int position) {
            return null;
        }
 
        @Override
        public long getItemId(int position) {
            return position;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null; 
 
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_headicon, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.itemImg = (ImageView) convertView.findViewById(R.id.item_headicon_img); 
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.item_headicon_checkbox);
                convertView.setTag(viewHolder); 
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Bitmap bitmap = ImageTool.createRoundConerImage(
            		BitmapFactory.decodeResource(getResources(), idArray[position]), radius);
            viewHolder.itemImg.setImageBitmap(bitmap);
            if (position == checkPos) {
				viewHolder.checkBox.setChecked(true);
			} else {
				viewHolder.checkBox.setChecked(false);
			}
 
            return convertView;
        } 
 
        class ViewHolder {
            ImageView itemImg;
            CheckBox checkBox;
        }
    }
	
	private class IconFolderAdapter extends BaseAdapter {
		 
        private Context mContext;
        private int[][] folderArray;
        private int curFolder;
 
        public IconFolderAdapter(Context context, int[][] folderArray) {
            this.mContext = context;
            this.folderArray = folderArray;
            this.curFolder = folderArray[0][0];
        }
 
        public int[] selectCurFolder(int position) {
            this.curFolder = folderArray[position][0];
            this.notifyDataSetChanged();
            return HeadIconTool.iconSetSelect(curFolder);
        }
        
        public String selectCurName(int position) {
        	return getResources().getString(folderArray[position][1]);
        }
        
        public int getCurFolder() {
        	return curFolder;
        }
        
        @Override
        public int getCount() {
            return folderArray.length;
        }
 
        @Override
        public Object getItem(int position) {
            return null;
        }
 
        @Override
        public long getItemId(int position) {
            return position;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null; 
 
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_menu_select_icon, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.img = (ImageView) convertView.findViewById(R.id.icon_menu_item_icon); 
                viewHolder.cbox = (ImageView) convertView.findViewById(R.id.icon_menu_item_cbox);
                viewHolder.name = (TextView) convertView.findViewById(R.id.icon_menu_item_name);
                viewHolder.count = (TextView) convertView.findViewById(R.id.icon_menu_item_count);
                convertView.setTag(viewHolder); 
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            viewHolder.img.setImageResource(folderArray[position][0]);
            viewHolder.name.setText(getResources().getString(folderArray[position][1]));
            viewHolder.count.setText(getResources().getString(R.string.total_count, 
            		HeadIconTool.iconSetSelect(folderArray[position][0]).length));
            viewHolder.cbox.setVisibility(curFolder == folderArray[position][0] ? View.VISIBLE : View.INVISIBLE);
            
            return convertView;
        } 
 
        class ViewHolder {
            ImageView img;
            TextView name;
            TextView count;
            ImageView cbox;
        }
    }
	
}

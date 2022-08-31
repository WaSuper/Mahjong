package com.mahjong.activity.jpn;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.activity.BaseActivity;
import com.mahjong.adapter.SpecialYakuCheckAdapter;
import com.mahjong.common.MjFanType;
import com.mahjong.item.MjFanBean;
import com.mahjong.tools.ValueTool;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.MahjongSpectrum;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FanTypeActivity extends BaseActivity implements OnClickListener {

	private ImageView mBack;
	private ListView mListView;
	private MjFanItemAdapter mAdapter;
	private TextView mFanOne;
	private TextView mFanTwo;
	private TextView mFanThree;
	private TextView mFanSix;
	private TextView mFanManGuan;
	private TextView mFanYiMan;
	private TextView mFanYiManDouble;
	private TextView mFanLeave;
	private ImageView mInfoView;
	private TextView mSpYakuView;
	
	private TextView mSelectedView;
	
	private int mSelectColor;
	private int mNormalColor;
	
	private List<MjFanBean> FanOneList = new ArrayList<MjFanBean>();
	private List<MjFanBean> FanTwoList = new ArrayList<MjFanBean>();
	private List<MjFanBean> FanThreeList = new ArrayList<MjFanBean>();
	private List<MjFanBean> FanSixList = new ArrayList<MjFanBean>();
	private List<MjFanBean> FanManGuanList = new ArrayList<MjFanBean>();
	private List<MjFanBean> FanYiManList = new ArrayList<MjFanBean>();
	private List<MjFanBean> FanYiManDoubleList = new ArrayList<MjFanBean>();
	private List<MjFanBean> FanLeaveList = new ArrayList<MjFanBean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_fan_type);
		initUI();
		initData();
	}
	
	private void initUI() {
		mBack = (ImageView) findViewById(R.id.fan_type_back);
		mListView = (ListView) findViewById(R.id.fan_type_listview);
		mFanOne = (TextView) findViewById(R.id.fan_type_tv_one);
		mFanTwo = (TextView) findViewById(R.id.fan_type_tv_two);
		mFanThree = (TextView) findViewById(R.id.fan_type_tv_three);
		mFanSix = (TextView) findViewById(R.id.fan_type_tv_six);
		mFanManGuan = (TextView) findViewById(R.id.fan_type_tv_manguan);
		mFanYiMan = (TextView) findViewById(R.id.fan_type_tv_yiman);
		mFanYiManDouble = (TextView) findViewById(R.id.fan_type_tv_yiman_double);
		mFanLeave = (TextView) findViewById(R.id.fan_type_tv_leave);
		mListView = (ListView) findViewById(R.id.fan_type_listview);
		mAdapter = new MjFanItemAdapter(this);
		mListView.setAdapter(mAdapter);
		mInfoView = (ImageView) findViewById(R.id.fan_type_info);
		mSpYakuView = (TextView) findViewById(R.id.fan_type_specialyaku);
		
		mBack.setOnClickListener(this);
		mFanOne.setOnClickListener(this);
		mFanTwo.setOnClickListener(this);
		mFanThree.setOnClickListener(this);
		mFanSix.setOnClickListener(this);
		mFanManGuan.setOnClickListener(this);
		mFanYiMan.setOnClickListener(this);
		mFanYiManDouble.setOnClickListener(this);
		mFanLeave.setOnClickListener(this);
		mInfoView.setOnClickListener(this);
		mSpYakuView.setOnClickListener(this);
	}
	
	private void initData() {
		mSelectColor = getResources().getColor(R.color.fan_bg);
		mNormalColor = getResources().getColor(R.color.fan_item_bg);
		mSelectedView = mFanOne;
		mSelectedView.setBackgroundColor(mSelectColor);
		showFanOne();
	}
	
	private void showInformation() {
		final CommonDialog infoDialog = new CommonDialog(this, R.style.MyDialogStyle, 0);
		infoDialog.addView(R.layout.item_text);
		infoDialog.setCanceledOnTouchOutside(true);
		infoDialog.titleTextView.setText(getString(R.string.rule_to_calc_fu));
		TextView textView = (TextView) infoDialog.getContentView().findViewById(R.id.item_text);
		textView.setText(getString(R.string.rule_to_calc_fu_detail));
		textView.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		textView.setTextSize(ValueTool.sp2px(this, 6));
		infoDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {	
				infoDialog.dismiss();
			}
		});
		infoDialog.show();
	}
	
	private void showSpecialYakuDialog() {
		final CommonDialog mDialog = new CommonDialog(this, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getResources().getString(R.string.special_yaku));
		mDialog.ok.setText(getResources().getString(R.string.ok));
		ListView listView = (ListView) mDialog.getContentView();
		final SpecialYakuCheckAdapter mAdapter = new SpecialYakuCheckAdapter(this);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAdapter.clickItem(position);
			}
		});
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
	private void showFanOne() {
		if (FanOneList.isEmpty()) {
			addFan2List(FanOneList, MjFanType.LiZhiText);
			addFan2List(FanOneList, MjFanType.YiFaText);
			addFan2List(FanOneList, MjFanType.ZiMoText);
			addFan2List(FanOneList, MjFanType.PingHeText);
			addFan2List(FanOneList, MjFanType.QiangKongText);
			addFan2List(FanOneList, MjFanType.YiPaiSelfText);
			addFan2List(FanOneList, MjFanType.YiPaiGroundText);
			addFan2List(FanOneList, MjFanType.YiPaiSanYuanText);
			addFan2List(FanOneList, MjFanType.DuanYaoJiuText);
			addFan2List(FanOneList, MjFanType.YiBeiKouText);
			addFan2List(FanOneList, MjFanType.LingShangKaiHuaText);
			addFan2List(FanOneList, MjFanType.HaiDiLaoYueText);
			addFan2List(FanOneList, MjFanType.HeDiMoYuText);
			addFan2List(FanOneList, MjFanType.DoraText);
			addFan2List(FanOneList, MjFanType.DoraInText);
			addFan2List(FanOneList, MjFanType.DoraRedText);
			addFan2List(FanOneList, MjFanType.ShiErLuoTaiText);
		}		
		mAdapter.setData(FanOneList);
		mListView.setSelection(0);
	}
	
	private void showFanTwo() {
		if (FanTwoList.isEmpty()) {
			addFan2List(FanTwoList, MjFanType.DoubleLiZhiText);
			addFan2List(FanTwoList, MjFanType.QiDuiZiText);
			addFan2List(FanTwoList, MjFanType.HunQuanDaiYaoText);
			addFan2List(FanTwoList, MjFanType.YiQiTongGuanText);
			addFan2List(FanTwoList, MjFanType.SanSeTongShunText);
			addFan2List(FanTwoList, MjFanType.SanSeTongKeText);
			addFan2List(FanTwoList, MjFanType.SanGangZiText);
			addFan2List(FanTwoList, MjFanType.DuiDuiHeText);
			addFan2List(FanTwoList, MjFanType.SanAnKeText);
			addFan2List(FanTwoList, MjFanType.XiaoSanYuanText);
			addFan2List(FanTwoList, MjFanType.HunLaoTouText);
			addFan2List(FanTwoList, MjFanType.SanLianKeText);
			addFan2List(FanTwoList, MjFanType.SanSeTongGuanText);
			addFan2List(FanTwoList, MjFanType.SanSeLianKeText);
			addFan2List(FanTwoList, MjFanType.JingTongHeText);
			addFan2List(FanTwoList, MjFanType.ErTongKeText);
		}
		mAdapter.setData(FanTwoList);
		mListView.setSelection(0);
	}
	
	private void showFanThree() {
		if (FanThreeList.isEmpty()) {
			addFan2List(FanThreeList, MjFanType.ErBeiKouText);
			addFan2List(FanThreeList, MjFanType.ChunQuanDaiYaoJiuText);
			addFan2List(FanThreeList, MjFanType.HunYiSeText);
		}
		mAdapter.setData(FanThreeList);
		mListView.setSelection(0);
	}
	
	private void showFanSix() {
		if (FanSixList.isEmpty()) {
			addFan2List(FanSixList, MjFanType.QingYiSeText);
			addFan2List(FanSixList, MjFanType.SanSeShuangLongHuiText);
		}
		mAdapter.setData(FanSixList);
		mListView.setSelection(0);
	}
	
	private void showFanManGuan() {
		if (FanManGuanList.isEmpty()) {
			addFan2List(FanManGuanList, MjFanType.LiuJuManGuanText);
		}
		mAdapter.setData(FanManGuanList);
		mListView.setSelection(0);
	}
	
	private void showFanYiMan() {
		if (FanYiManList.isEmpty()) {
			addFan2List(FanYiManList, MjFanType.TianHeText);
			addFan2List(FanYiManList, MjFanType.DiHeText);
			addFan2List(FanYiManList, MjFanType.GuoShiWuShuangText);
			addFan2List(FanYiManList, MjFanType.JiuLianBaoDengText);
			addFan2List(FanYiManList, MjFanType.SiAnKeText);
			addFan2List(FanYiManList, MjFanType.DaSanYuanText);
			addFan2List(FanYiManList, MjFanType.SiGangZiText);
			addFan2List(FanYiManList, MjFanType.LvYiSeText);
			addFan2List(FanYiManList, MjFanType.ZiYiSeText);
			addFan2List(FanYiManList, MjFanType.QingLaoTouText);
			addFan2List(FanYiManList, MjFanType.XiaoSiXiText);
			addFan2List(FanYiManList, MjFanType.RenHeText);
			addFan2List(FanYiManList, MjFanType.DaCheLunText);
			addFan2List(FanYiManList, MjFanType.DaZhuLinText);
			addFan2List(FanYiManList, MjFanType.DaShuLinText);
			addFan2List(FanYiManList, MjFanType.BaLianZhuangText);
		}
		mAdapter.setData(FanYiManList);
		mListView.setSelection(0);
	}
	
	private void showFanYIManDouble() {
		if (FanYiManDoubleList.isEmpty()) {
			addFan2List(FanYiManDoubleList, MjFanType.SiAnKe_SingleText);
			addFan2List(FanYiManDoubleList, MjFanType.GuoShiWuShuang_ThirteenText);
			addFan2List(FanYiManDoubleList, MjFanType.JiuLianBaoDeng_PureText);
			addFan2List(FanYiManDoubleList, MjFanType.DaSiXiText);
			addFan2List(FanYiManDoubleList, MjFanType.DaQiXingText);
		}
		mAdapter.setData(FanYiManDoubleList);
		mListView.setSelection(0);
	}
	
	private void showFanLeave() {
		if (FanLeaveList.isEmpty()) {
			addFan2List(FanLeaveList, MjFanType.SiFengLianDaText);
			addFan2List(FanLeaveList, MjFanType.SiGangSanLeText);
			addFan2List(FanLeaveList, MjFanType.JiuPaiJiuZhongText);
			addFan2List(FanLeaveList, MjFanType.SiJiaLiZhiText);
			addFan2List(FanLeaveList, MjFanType.SanJiaHeLiaoText);
		}
		mAdapter.setData(FanLeaveList);
		mListView.setSelection(0);
	}
	
	private void addFan2List(List<MjFanBean> list, String[] type) {
		list.add(new MjFanBean(type[0], type[1], type[2], type[3]));
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.fan_type_back:
			finish();
			break;
		case R.id.fan_type_info:
			showInformation();
			break;
		case R.id.fan_type_specialyaku:
			showSpecialYakuDialog();
			break;
		case R.id.fan_type_tv_one:
			if (mSelectedView != null) {
				if (mSelectedView.getId() == mFanOne.getId()) {
					return;
				}
				mSelectedView.setBackgroundColor(mNormalColor);
			}				
			mFanOne.setBackgroundColor(mSelectColor);
			mSelectedView = mFanOne;
			showFanOne();
			break;
		case R.id.fan_type_tv_two:
			if (mSelectedView != null) {
				if (mSelectedView.getId() == mFanTwo.getId()) {
					return;
				}
				mSelectedView.setBackgroundColor(mNormalColor);
			}
			mFanTwo.setBackgroundColor(mSelectColor);
			mSelectedView = mFanTwo;
			showFanTwo();
			break;
		case R.id.fan_type_tv_three:
			if (mSelectedView != null) {
				if (mSelectedView.getId() == mFanThree.getId()) {
					return;
				}
				mSelectedView.setBackgroundColor(mNormalColor);
			}
			mFanThree.setBackgroundColor(mSelectColor);
			mSelectedView = mFanThree;
			showFanThree();
			break;
		case R.id.fan_type_tv_six:
			if (mSelectedView != null) {
				if (mSelectedView.getId() == mFanSix.getId()) {
					return;
				}
				mSelectedView.setBackgroundColor(mNormalColor);
			}
			mFanSix.setBackgroundColor(mSelectColor);
			mSelectedView = mFanSix;
			showFanSix();
			break;
		case R.id.fan_type_tv_manguan:
			if (mSelectedView != null) {
				if (mSelectedView.getId() == mFanManGuan.getId()) {
					return;
				}
				mSelectedView.setBackgroundColor(mNormalColor);
			}
			mFanManGuan.setBackgroundColor(mSelectColor);
			mSelectedView = mFanManGuan;
			showFanManGuan();
			break;
		case R.id.fan_type_tv_yiman:
			if (mSelectedView != null) {
				if (mSelectedView.getId() == mFanYiMan.getId()) {
					return;
				}
				mSelectedView.setBackgroundColor(mNormalColor);
			}
			mFanYiMan.setBackgroundColor(mSelectColor);
			mSelectedView = mFanYiMan;
			showFanYiMan();
			break;
		case R.id.fan_type_tv_yiman_double:
			if (mSelectedView != null) {
				if (mSelectedView.getId() == mFanYiManDouble.getId()) {
					return;
				}
				mSelectedView.setBackgroundColor(mNormalColor);
			}
			mFanYiManDouble.setBackgroundColor(mSelectColor);
			mSelectedView = mFanYiManDouble;
			showFanYIManDouble();
			break;
		case R.id.fan_type_tv_leave:
			if (mSelectedView != null) {
				if (mSelectedView.getId() == mFanLeave.getId()) {
					return;
				}
				mSelectedView.setBackgroundColor(mNormalColor);
			}
			mFanLeave.setBackgroundColor(mSelectColor);
			mSelectedView = mFanLeave;
			showFanLeave();
			break;
		default:
			break;
		}
	}
	
	class MjFanItemAdapter extends BaseAdapter {

		private Context mContext;
		private List<MjFanBean> mjList;
		
		public MjFanItemAdapter(Context context) {
			this.mContext = context;
		}
		
		public void setData(List<MjFanBean> list) {
			this.mjList = list;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return mjList != null ? mjList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return mjList != null ? mjList.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
	            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mjspactrum, null);
	            holder.leftText = (TextView) convertView.findViewById(R.id.item_mjs_tv_left);
	            holder.centerText = (TextView) convertView.findViewById(R.id.item_mjs_tv_center);
	            holder.rightText = (TextView) convertView.findViewById(R.id.item_mjs_tv_right);
	            holder.mjSpectrum = (MahjongSpectrum) convertView.findViewById(R.id.item_mjs_mjspectrum);
	            convertView.setTag(holder);
	        } else {
	        	holder = (ViewHolder) convertView.getTag();
	        }
			MjFanBean bean = mjList.get(position);
			holder.leftText.setText(bean.getLeftText());
			holder.centerText.setText(bean.getCenterText());
			holder.rightText.setText(bean.getRightText());
			holder.mjSpectrum.setData(bean.getCardList(), bean.getPairsList(), bean.getWinCard());
			return convertView;
		}
		
		private class ViewHolder {
			TextView leftText;
			TextView centerText;
			TextView rightText;
			MahjongSpectrum mjSpectrum;
		}
		
	}
	
}

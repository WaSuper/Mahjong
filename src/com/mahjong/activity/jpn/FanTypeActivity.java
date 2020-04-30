package com.mahjong.activity.jpn;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.R;
import com.mahjong.common.MjFanType;
import com.mahjong.item.MjFanBean;
import com.mahjong.tools.ValueTool;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.MahjongSpectrum;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FanTypeActivity extends Activity implements OnClickListener {

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
	private void showFanOne() {
		if (FanOneList.isEmpty()) {
			FanOneList.add(new MjFanBean(MjFanType.LiZhiText[0], MjFanType.LiZhiText[1], 
					MjFanType.LiZhiText[2], MjFanType.LiZhiText[3]));
			FanOneList.add(new MjFanBean(MjFanType.YiFaText[0], MjFanType.YiFaText[1], 
					MjFanType.YiFaText[2], MjFanType.YiFaText[3]));
			FanOneList.add(new MjFanBean(MjFanType.ZiMoText[0], MjFanType.ZiMoText[1], 
					MjFanType.ZiMoText[2], MjFanType.ZiMoText[3]));
			FanOneList.add(new MjFanBean(MjFanType.PingHeText[0], MjFanType.PingHeText[1], 
					MjFanType.PingHeText[2], MjFanType.PingHeText[3]));
			FanOneList.add(new MjFanBean(MjFanType.QiangKongText[0], MjFanType.QiangKongText[1], 
					MjFanType.QiangKongText[2], MjFanType.QiangKongText[3]));
			FanOneList.add(new MjFanBean(MjFanType.YiPaiSelfText[0], MjFanType.YiPaiSelfText[1], 
					MjFanType.YiPaiSelfText[2], MjFanType.YiPaiSelfText[3]));
			FanOneList.add(new MjFanBean(MjFanType.YiPaiGroundText[0], MjFanType.YiPaiGroundText[1], 
					MjFanType.YiPaiGroundText[2], MjFanType.YiPaiGroundText[3]));
			FanOneList.add(new MjFanBean(MjFanType.YiPaiSanYuanText[0], MjFanType.YiPaiSanYuanText[1], 
					MjFanType.YiPaiSanYuanText[2], MjFanType.YiPaiSanYuanText[3]));
			FanOneList.add(new MjFanBean(MjFanType.DuanYaoJiuText[0], MjFanType.DuanYaoJiuText[1], 
					MjFanType.DuanYaoJiuText[2], MjFanType.DuanYaoJiuText[3]));
			FanOneList.add(new MjFanBean(MjFanType.YiBeiKouText[0], MjFanType.YiBeiKouText[1], 
					MjFanType.YiBeiKouText[2], MjFanType.YiBeiKouText[3]));
			FanOneList.add(new MjFanBean(MjFanType.LingShangKaiHuaText[0], MjFanType.LingShangKaiHuaText[1], 
					MjFanType.LingShangKaiHuaText[2], MjFanType.LingShangKaiHuaText[3]));
			FanOneList.add(new MjFanBean(MjFanType.HaiDiLaoYueText[0], MjFanType.HaiDiLaoYueText[1], 
					MjFanType.HaiDiLaoYueText[2], MjFanType.HaiDiLaoYueText[3]));
			FanOneList.add(new MjFanBean(MjFanType.HeDiMoYuText[0], MjFanType.HeDiMoYuText[1], 
					MjFanType.HeDiMoYuText[2], MjFanType.HeDiMoYuText[3]));
			FanOneList.add(new MjFanBean(MjFanType.DoraText[0], MjFanType.DoraText[1], 
					MjFanType.DoraText[2], MjFanType.DoraText[3]));
			FanOneList.add(new MjFanBean(MjFanType.DoraInText[0], MjFanType.DoraInText[1], 
					MjFanType.DoraInText[2], MjFanType.DoraInText[3]));
			FanOneList.add(new MjFanBean(MjFanType.DoraRedText[0], MjFanType.DoraRedText[1], 
					MjFanType.DoraRedText[2], MjFanType.DoraRedText[3]));
		}		
		mAdapter.setData(FanOneList);
		mListView.setSelection(0);
	}
	
	private void showFanTwo() {
		if (FanTwoList.isEmpty()) {
			FanTwoList.add(new MjFanBean(MjFanType.DoubleLiZhiText[0], MjFanType.DoubleLiZhiText[1], 
					MjFanType.DoubleLiZhiText[2], MjFanType.DoubleLiZhiText[3]));
			FanTwoList.add(new MjFanBean(MjFanType.QiDuiZiText[0], MjFanType.QiDuiZiText[1], 
					MjFanType.QiDuiZiText[2], MjFanType.QiDuiZiText[3]));
			FanTwoList.add(new MjFanBean(MjFanType.HunQuanDaiYaoText[0], MjFanType.HunQuanDaiYaoText[1], 
					MjFanType.HunQuanDaiYaoText[2], MjFanType.HunQuanDaiYaoText[3]));
			FanTwoList.add(new MjFanBean(MjFanType.YiQiTongGuanText[0], MjFanType.YiQiTongGuanText[1], 
					MjFanType.YiQiTongGuanText[2], MjFanType.YiQiTongGuanText[3]));
			FanTwoList.add(new MjFanBean(MjFanType.SanSeTongShunText[0], MjFanType.SanSeTongShunText[1], 
					MjFanType.SanSeTongShunText[2], MjFanType.SanSeTongShunText[3]));
			FanTwoList.add(new MjFanBean(MjFanType.SanSeTongKeText[0], MjFanType.SanSeTongKeText[1], 
					MjFanType.SanSeTongKeText[2], MjFanType.SanSeTongKeText[3]));
			FanTwoList.add(new MjFanBean(MjFanType.SanGangZiText[0], MjFanType.SanGangZiText[1], 
					MjFanType.SanGangZiText[2], MjFanType.SanGangZiText[3]));
			FanTwoList.add(new MjFanBean(MjFanType.DuiDuiHeText[0], MjFanType.DuiDuiHeText[1], 
					MjFanType.DuiDuiHeText[2], MjFanType.DuiDuiHeText[3]));
			FanTwoList.add(new MjFanBean(MjFanType.SanAnKeText[0], MjFanType.SanAnKeText[1], 
					MjFanType.SanAnKeText[2], MjFanType.SanAnKeText[3]));
			FanTwoList.add(new MjFanBean(MjFanType.XiaoSanYuanText[0], MjFanType.XiaoSanYuanText[1], 
					MjFanType.XiaoSanYuanText[2], MjFanType.XiaoSanYuanText[3]));
			FanTwoList.add(new MjFanBean(MjFanType.HunLaoTouText[0], MjFanType.HunLaoTouText[1], 
					MjFanType.HunLaoTouText[2], MjFanType.HunLaoTouText[3]));
		}
		mAdapter.setData(FanTwoList);
		mListView.setSelection(0);
	}
	
	private void showFanThree() {
		if (FanThreeList.isEmpty()) {
			FanThreeList.add(new MjFanBean(MjFanType.ErBeiKouText[0], MjFanType.ErBeiKouText[1], 
					MjFanType.ErBeiKouText[2], MjFanType.ErBeiKouText[3]));
			FanThreeList.add(new MjFanBean(MjFanType.ChunQuanDaiYaoJiuText[0], MjFanType.ChunQuanDaiYaoJiuText[1], 
					MjFanType.ChunQuanDaiYaoJiuText[2], MjFanType.ChunQuanDaiYaoJiuText[3]));
			FanThreeList.add(new MjFanBean(MjFanType.HunYiSeText[0], MjFanType.HunYiSeText[1], 
					MjFanType.HunYiSeText[2], MjFanType.HunYiSeText[3]));
		}
		mAdapter.setData(FanThreeList);
		mListView.setSelection(0);
	}
	
	private void showFanSix() {
		if (FanSixList.isEmpty()) {
			FanSixList.add(new MjFanBean(MjFanType.QingYiSeText[0], MjFanType.QingYiSeText[1], 
					MjFanType.QingYiSeText[2], MjFanType.QingYiSeText[3]));
		}
		mAdapter.setData(FanSixList);
		mListView.setSelection(0);
	}
	
	private void showFanManGuan() {
		if (FanManGuanList.isEmpty()) {
			FanManGuanList.add(new MjFanBean(MjFanType.LiuJuManGuanText[0], MjFanType.LiuJuManGuanText[1], 
					MjFanType.LiuJuManGuanText[2], MjFanType.LiuJuManGuanText[3]));
		}
		mAdapter.setData(FanManGuanList);
		mListView.setSelection(0);
	}
	
	private void showFanYiMan() {
		if (FanYiManList.isEmpty()) {
			FanYiManList.add(new MjFanBean(MjFanType.TianHeText[0], MjFanType.TianHeText[1], 
					MjFanType.TianHeText[2], MjFanType.TianHeText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.DiHeText[0], MjFanType.DiHeText[1], 
					MjFanType.DiHeText[2], MjFanType.DiHeText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.RenHeText[0], MjFanType.RenHeText[1], 
					MjFanType.RenHeText[2], MjFanType.RenHeText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.DaCheLunText[0], MjFanType.DaCheLunText[1], 
					MjFanType.DaCheLunText[2], MjFanType.DaCheLunText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.GuoShiWuShuangText[0], MjFanType.GuoShiWuShuangText[1], 
					MjFanType.GuoShiWuShuangText[2], MjFanType.GuoShiWuShuangText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.JiuLianBaoDengText[0], MjFanType.JiuLianBaoDengText[1], 
					MjFanType.JiuLianBaoDengText[2], MjFanType.JiuLianBaoDengText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.SiAnKeText[0], MjFanType.SiAnKeText[1], 
					MjFanType.SiAnKeText[2], MjFanType.SiAnKeText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.DaSanYuanText[0], MjFanType.DaSanYuanText[1], 
					MjFanType.DaSanYuanText[2], MjFanType.DaSanYuanText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.SiGangZiText[0], MjFanType.SiGangZiText[1], 
					MjFanType.SiGangZiText[2], MjFanType.SiGangZiText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.LvYiSeText[0], MjFanType.LvYiSeText[1], 
					MjFanType.LvYiSeText[2], MjFanType.LvYiSeText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.ZiYiSeText[0], MjFanType.ZiYiSeText[1], 
					MjFanType.ZiYiSeText[2], MjFanType.ZiYiSeText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.QingLaoTouText[0], MjFanType.QingLaoTouText[1], 
					MjFanType.QingLaoTouText[2], MjFanType.QingLaoTouText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.XiaoSiXiText[0], MjFanType.XiaoSiXiText[1], 
					MjFanType.XiaoSiXiText[2], MjFanType.XiaoSiXiText[3]));
			FanYiManList.add(new MjFanBean(MjFanType.BaLianZhuangText[0], MjFanType.BaLianZhuangText[1], 
					MjFanType.BaLianZhuangText[2], MjFanType.BaLianZhuangText[3]));
		}
		mAdapter.setData(FanYiManList);
		mListView.setSelection(0);
	}
	
	private void showFanYIManDouble() {
		if (FanYiManDoubleList.isEmpty()) {
			FanYiManDoubleList.add(new MjFanBean(MjFanType.SiAnKe_SingleText[0], MjFanType.SiAnKe_SingleText[1], 
					MjFanType.SiAnKe_SingleText[2], MjFanType.SiAnKe_SingleText[3]));
			FanYiManDoubleList.add(new MjFanBean(MjFanType.GuoShiWuShuang_ThirteenText[0], MjFanType.GuoShiWuShuang_ThirteenText[1], 
					MjFanType.GuoShiWuShuang_ThirteenText[2], MjFanType.GuoShiWuShuang_ThirteenText[3]));
			FanYiManDoubleList.add(new MjFanBean(MjFanType.JiuLianBaoDeng_PureText[0], MjFanType.JiuLianBaoDeng_PureText[1], 
					MjFanType.JiuLianBaoDeng_PureText[2], MjFanType.JiuLianBaoDeng_PureText[3]));
			FanYiManDoubleList.add(new MjFanBean(MjFanType.DaSiXiText[0], MjFanType.DaSiXiText[1], 
					MjFanType.DaSiXiText[2], MjFanType.DaSiXiText[3]));
		}
		mAdapter.setData(FanYiManDoubleList);
		mListView.setSelection(0);
	}
	
	private void showFanLeave() {
		if (FanLeaveList.isEmpty()) {
			FanLeaveList.add(new MjFanBean(MjFanType.SiFengLianDaText[0], MjFanType.SiFengLianDaText[1], 
					MjFanType.SiFengLianDaText[2], MjFanType.SiFengLianDaText[3]));
			FanLeaveList.add(new MjFanBean(MjFanType.SiGangSanLeText[0], MjFanType.SiGangSanLeText[1], 
					MjFanType.SiGangSanLeText[2], MjFanType.SiGangSanLeText[3]));
			FanLeaveList.add(new MjFanBean(MjFanType.JiuPaiJiuZhongText[0], MjFanType.JiuPaiJiuZhongText[1], 
					MjFanType.JiuPaiJiuZhongText[2], MjFanType.JiuPaiJiuZhongText[3]));
			FanLeaveList.add(new MjFanBean(MjFanType.SiJiaLiZhiText[0], MjFanType.SiJiaLiZhiText[1], 
					MjFanType.SiJiaLiZhiText[2], MjFanType.SiJiaLiZhiText[3]));
			FanLeaveList.add(new MjFanBean(MjFanType.SanJiaHeLiaoText[0], MjFanType.SanJiaHeLiaoText[1], 
					MjFanType.SanJiaHeLiaoText[2], MjFanType.SanJiaHeLiaoText[3]));
		}
		mAdapter.setData(FanLeaveList);
		mListView.setSelection(0);
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

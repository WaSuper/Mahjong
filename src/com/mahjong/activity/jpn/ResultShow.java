package com.mahjong.activity.jpn;

import com.mahjong.R;
import com.mahjong.activity.BaseActivity;
import com.mahjong.common.MjSetting;
import com.mahjong.control.BaseManager;
import com.mahjong.control.ManagerTool;
import com.mahjong.item.ResultList;
import com.mahjong.model.MjAction;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.LightTool;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.ui.ResultListView;
import com.mahjong.ui.ResultView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultShow extends BaseActivity implements OnClickListener {

	private ResultView[] mResultViews; // 结果视图
	private ResultListView[] mResultListViews; // 分数列表视图
	private TextView[] mNameViews; // 名字视图
	private TextView[] mWindViews; // 风位视图
	private Button mOkBtn; // 确定按钮
	private Button mRevokeBtn; // 撤销按钮
//	private TextView mJuText; // 局文字
//	private TextView mRoundText; // 本场数量
//	private TextView mLizhiText; // 立直数量
	private ImageView[] mJuViews; // 局数文字
	private ImageView[] mRoundViews; // 本场文字
	private ImageView[] mLizhiViews; // 立直文字
	private ImageView[] mArrowToViews; // 箭头：得分
	private ImageView[] mArrowFromViews; // 箭头：失分
	
	private String[] mWindStrings; // 东、南、西、北
//	private String[] mNumStrings; // 一、二、三、四 
//	private String mJuString; // 局
	
	private int mActionType; // 动作类型
	private int mMainVision; // 主视觉
	private int[] mIndexs; // 四个方位玩家对应的初始索引
	private int[] changeScores = {0, 0, 0, 0}; // 四个玩家的变化分值
	private boolean isDealerTing = false; // 庄家是否听牌
	private boolean[] contactIds; // 相关联的id数组
	private int mMainPlayer; // 主操作玩家
	private int[] mBombIndexs; // 多家和玩家
	private ResultList[] mResultLists = new ResultList[4]; // 四个玩家分值变化详情
	
	private BaseManager mTool = ManagerTool.getInstance().getManager();
	private LightTool mLightTool; // 控制屏幕亮度
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean landscapeMode = ShareprefenceTool.getInstance().getBoolean(MjSetting.LANDSCAPE_MODE, this, false);
		boolean isPortscapeMode = getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		if (isPortscapeMode && landscapeMode) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setContentView(R.layout.activity_blank);
		} else if (!isPortscapeMode && !landscapeMode) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setContentView(R.layout.activity_blank);
		} else {
			onCreateAction();
		}
	}
	
	private void onCreateAction() {
		boolean squareMode = ShareprefenceTool.getInstance().getBoolean(MjSetting.SQUARE_MODE, this, false);
		if (squareMode) {
			setContentView(R.layout.activity_square_jpn_show_result);
		} else {
			setContentView(R.layout.activity_jpn_show_result);
		}
		mActionType = getIntent().getIntExtra(MjAction.Name, 0);
		mMainVision = getIntent().getIntExtra(GameSimpleActivity.MAIN_VISION, 0);
		mLightTool = new LightTool(this);
		initUI();
		showAction();
	}
	
	private void initUI() {
		mResultViews = new ResultView[4];
		mResultViews[0] = (ResultView) findViewById(R.id.show_result_point_bottom);
		mResultViews[1] = (ResultView) findViewById(R.id.show_result_point_right);
		mResultViews[2] = (ResultView) findViewById(R.id.show_result_point_top);
		mResultViews[3] = (ResultView) findViewById(R.id.show_result_point_left);
		mResultListViews = new ResultListView[4];
		mResultListViews[0] = (ResultListView) findViewById(R.id.show_result_list_bottom);
		mResultListViews[1] = (ResultListView) findViewById(R.id.show_result_list_right);
		mResultListViews[2] = (ResultListView) findViewById(R.id.show_result_list_top);
		mResultListViews[3] = (ResultListView) findViewById(R.id.show_result_list_left);
		mNameViews = new TextView[4];
		mNameViews[0] = (TextView) findViewById(R.id.show_result_name_bottom);
		mNameViews[1] = (TextView) findViewById(R.id.show_result_name_right);
		mNameViews[2] = (TextView) findViewById(R.id.show_result_name_top);
		mNameViews[3] = (TextView) findViewById(R.id.show_result_name_left);
		mWindViews = new TextView[4];
		mWindViews[0] = (TextView) findViewById(R.id.show_result_wind_bottom);
		mWindViews[1] = (TextView) findViewById(R.id.show_result_wind_right);
		mWindViews[2] = (TextView) findViewById(R.id.show_result_wind_top);
		mWindViews[3] = (TextView) findViewById(R.id.show_result_wind_left);		
		mOkBtn = (Button) findViewById(R.id.show_result_btn_ok);
		mRevokeBtn = (Button) findViewById(R.id.show_result_btn_revoke);
//		mJuText = (TextView) findViewById(R.id.show_result_tv_ju);
//		mRoundText = (TextView) findViewById(R.id.show_result_tv_round);
//		mLizhiText = (TextView) findViewById(R.id.show_result_tv_lizhi);
		mJuViews = new ImageView[3];
		mJuViews[0] = (ImageView) findViewById(R.id.show_result_ju_img1);
		mJuViews[1] = (ImageView) findViewById(R.id.show_result_ju_img2);
		mJuViews[2] = (ImageView) findViewById(R.id.show_result_ju_img3);
		mRoundViews = new ImageView[4];
		mRoundViews[0] = (ImageView) findViewById(R.id.show_result_round_img0);
		mRoundViews[1] = (ImageView) findViewById(R.id.show_result_round_img1);
		mRoundViews[2] = (ImageView) findViewById(R.id.show_result_round_img2);
		mRoundViews[3] = (ImageView) findViewById(R.id.show_result_round_img3);
		mLizhiViews = new ImageView[4];
		mLizhiViews[0] = (ImageView) findViewById(R.id.show_result_lizhi_img1); 
		mLizhiViews[1] = (ImageView) findViewById(R.id.show_result_lizhi_img2);
		mLizhiViews[2] = (ImageView) findViewById(R.id.show_result_lizhi_img3);
		mLizhiViews[3] = (ImageView) findViewById(R.id.show_result_lizhi_img4);	
		mArrowToViews = new ImageView[4];
		mArrowToViews[0] = (ImageView) findViewById(R.id.show_result_arrow_bottom_r);
		mArrowToViews[1] = (ImageView) findViewById(R.id.show_result_arrow_right_t);
		mArrowToViews[2] = (ImageView) findViewById(R.id.show_result_arrow_top_l);
		mArrowToViews[3] = (ImageView) findViewById(R.id.show_result_arrow_left_b);
		mArrowFromViews = new ImageView[4];
		mArrowFromViews[0] = (ImageView) findViewById(R.id.show_result_arrow_bottom_l);
		mArrowFromViews[1] = (ImageView) findViewById(R.id.show_result_arrow_right_b);
		mArrowFromViews[2] = (ImageView) findViewById(R.id.show_result_arrow_top_r);
		mArrowFromViews[3] = (ImageView) findViewById(R.id.show_result_arrow_left_t);
		
		mWindStrings = new String[4];
		mWindStrings[0] = getResources().getString(R.string.east);
		mWindStrings[1] = getResources().getString(R.string.south);
		mWindStrings[2] = getResources().getString(R.string.west);
		mWindStrings[3] = getResources().getString(R.string.north);
//		mNumStrings = new String[4];
//		mNumStrings[0] = getResources().getString(R.string.one);
//		mNumStrings[1] = getResources().getString(R.string.two);
//		mNumStrings[2] = getResources().getString(R.string.three);
//		mNumStrings[3] = getResources().getString(R.string.four);
//		mJuString = getResources().getString(R.string.ju);
		
		// 设置中心文字
//		mJuText.setText(mWindStrings[mTool.getFengCount()] 
//				+ mNumStrings[mTool.getJuCount()] + mJuString);
//		mRoundText.setText(mTool.getRoundCount() + "");
//		mLizhiText.setText(mTool.getLizhiCount() + "");
		
		Bitmap windBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text_ju_wind);
		Bitmap[] mImgWinds = ImageTool.split(windBitmap, 4, 1);
		Bitmap juNumBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text_ju_number);
		Bitmap[] mImgJuNums = ImageTool.split(juNumBitmap, 4, 1);
		Bitmap roundNumBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text_count_number);
		Bitmap[] mImgRoundNums = ImageTool.split(roundNumBitmap, 11, 1);
		Bitmap extraBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text_extra);
		Bitmap[] extras = ImageTool.split(extraBitmap, 3, 1);
		Bitmap mImgJu = extras[0];
		Bitmap mImgBen = extras[1];
		Bitmap mImgChang = extras[2];
		// 局数
		mJuViews[0].setImageBitmap(mImgWinds[mTool.getFengCount()]);
		mJuViews[1].setImageBitmap(mImgJuNums[mTool.getJuCount()]);
		mJuViews[2].setImageBitmap(mImgJu);
		if (mTool.is17Step()) {
			int tmpIndex = 0;
			if (mTool.getFengType() == 0) {
				tmpIndex = 0;
			} else {
				switch (mTool.getMemberCount()) {
				case 2:
					if (mTool.getFengCount() % 2 == 0) tmpIndex = 0;
					else tmpIndex = 2;
					break;
				case 3:
					tmpIndex = mTool.getFengCount() % 3;
					break;
				case 4:
					tmpIndex = mTool.getFengCount() % 4;
					break;
				default:
					break;
				}
			}
			mJuViews[0].setImageBitmap(mImgWinds[tmpIndex]);
		} else {
			mJuViews[0].setImageBitmap(mImgWinds[mTool.getFengCount()]);
		}
		// 本场数
		mRoundViews[2].setImageBitmap(mImgBen);
		mRoundViews[3].setImageBitmap(mImgChang);
		int mRoundCount = mTool.getRoundCount();
		int hNum, lNum;
		if (mRoundCount > 9) {
			hNum = mRoundCount / 10;
			if (mRoundCount > 99) hNum = 9;
			mRoundViews[0].setVisibility(View.VISIBLE);
			mRoundViews[0].setImageBitmap(mImgRoundNums[hNum]);
		}
		lNum = mRoundCount % 10;
		if (mRoundCount > 99) lNum = 9;
		mRoundViews[1].setImageBitmap(mImgRoundNums[lNum]);
		// 立直数
		mLizhiViews[1].setImageBitmap(mImgRoundNums[10]);
		int mLizhiCount = mTool.getLizhiCount();
		if (mLizhiCount > 9) {
			hNum = mLizhiCount / 10;
			lNum = mLizhiCount % 10;
			if (mLizhiCount > 99) {hNum = 9; lNum = 9;}
			mLizhiViews[2].setImageBitmap(mImgRoundNums[hNum]);
			mLizhiViews[3].setVisibility(View.VISIBLE);
			mLizhiViews[3].setImageBitmap(mImgRoundNums[lNum]);			
		} else {
			mLizhiViews[2].setImageBitmap(mImgRoundNums[mLizhiCount]);
		}
		
		// 设置四个玩家的名字、风位
		mIndexs = new int[4];
		for (int i = 0; i < 4; i++) {
			mIndexs[i] = mTool.getPlayerIndexByPosition(mMainVision, i);
			mNameViews[i].setText(mTool.getPlayer(mIndexs[i]).getNickName());
			mWindViews[i].setText(mWindStrings[mTool.getPlayerWind(mIndexs[i])]);
			if (mTool.getMemberCount() < 4) {
				if (mIndexs[i] == 3) {
					mNameViews[i].setVisibility(View.INVISIBLE);
					mWindViews[i].setVisibility(View.INVISIBLE);
					mResultViews[i].setVisibility(View.INVISIBLE);
					mResultListViews[i].setVisibility(View.INVISIBLE);
				}
				if (mTool.getMemberCount() < 3) {
					if (mIndexs[i] == 1) {
						mNameViews[i].setVisibility(View.INVISIBLE);
						mWindViews[i].setVisibility(View.INVISIBLE);
						mResultViews[i].setVisibility(View.INVISIBLE);
						mResultListViews[i].setVisibility(View.INVISIBLE);
					}
				}
			}
		}		
		
		mOkBtn.setOnClickListener(this);
		mRevokeBtn.setOnClickListener(this);
	}
	
	private void showAction() {
		int[] orgScores = mTool.getPlayerScores(); // 四个玩家的原本分值
		for (int i = 0; i < mResultLists.length; i++) { // 初始化分值详情表
			mResultLists[i] = new ResultList(); 
		}
		switch (mActionType) {
		case MjAction.ACTION_ZIMO:	
			mMainPlayer = getIntent().getIntExtra(BaseManager.PLAYER_ORIGINAL_INDEX, 0);
			changeScores = mTool.analysisZimo(mMainPlayer, mResultLists);
			break;
		case MjAction.ACTION_BOMB:			
			mMainPlayer = getIntent().getIntExtra(BaseManager.PLAYER_ORIGINAL_INDEX, 0);
			mBombIndexs = getIntent().getIntArrayExtra(BaseManager.RESULT_BOMB_INDEX);
			changeScores = mTool.analysisBomb(mMainPlayer, mBombIndexs, mResultLists);
			break;
		case MjAction.ACTION_HUANGPAILIUJU:	
			changeScores = mTool.analysisTingpaiResult(mResultLists);
			break;
		case MjAction.ACTION_LIUJUMANGUAN:	
			contactIds = getIntent().getBooleanArrayExtra(GameSimpleActivity.CONTACT_IDS);
			isDealerTing = getIntent().getBooleanExtra(GameSimpleActivity.CONTACT_CHECK, false);
			changeScores = mTool.analysisLiujumanguan(contactIds, mResultLists);
			break;
		case MjAction.ACTION_CHANGESCORE:
			changeScores = getIntent().getIntArrayExtra(ChangeScoreActivity.ChangeScores);
			for (int i = 0; i < changeScores.length; i++) {
				mResultLists[i].addModify(changeScores[i]);
			}
			break;
		default:
			break;
		}
		int[] ranks = mTool.analysisPlayerRanks(orgScores, changeScores);
		// 更新视图
		for (int i = 0; i < 4; i++) {
//			mResultViews[i].setOnClickListener(this);
			mResultViews[i].setData(orgScores[mIndexs[i]], 
					changeScores[mIndexs[i]], ranks[mIndexs[i]],
					0, 0);
			mResultListViews[i].setData(mResultLists[mIndexs[i]]);
			if (changeScores[mIndexs[i]] > 0) {
				mArrowToViews[i].setVisibility(View.VISIBLE);
			} else if (changeScores[mIndexs[i]] < 0) {
				mArrowFromViews[i].setVisibility(View.VISIBLE);				
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.show_result_btn_ok:		
			doAction();
//			broadcastToInvalidate();
			finish();
			break;
		case R.id.show_result_btn_revoke:
			finish();
			break;
//		case R.id.show_result_point_bottom:
//			ToastTool.showToast(this, mResultLists[mIndexs[0]].getResultString(this));
//			break;
//		case R.id.show_result_point_right:
//			ToastTool.showToast(this, mResultLists[mIndexs[1]].getResultString(this));
//			break;
//		case R.id.show_result_point_top:
//			ToastTool.showToast(this, mResultLists[mIndexs[2]].getResultString(this));
//			break;
//		case R.id.show_result_point_left:
//			ToastTool.showToast(this, mResultLists[mIndexs[3]].getResultString(this));
//			break;
		default:
			break;
		}
	}
	
	private void doAction() {
		Intent intent = new Intent(GameSimpleActivity.BROADCAST_RESULT);
		intent.putExtra(GameSimpleActivity.BROADCAST_RESULT, 
				GameSimpleActivity.REQUEST_ACTION_DONE);
		intent.putExtra(GameSimpleActivity.ACTION_TYPE, mActionType);
		intent.putExtra(GameSimpleActivity.CHANGE_SCORES, changeScores);
		switch (mActionType) {
		case MjAction.ACTION_ZIMO:			
//			mTool.setZimo(changeScores, mMainPlayer);
			intent.putExtra(GameSimpleActivity.MAIN_PLAYER, mMainPlayer);
			break;
		case MjAction.ACTION_BOMB:			
//			mTool.setBomb(changeScores, mMainPlayer, mBombIndexs);
			intent.putExtra(GameSimpleActivity.MAIN_PLAYER, mMainPlayer);
			intent.putExtra(GameSimpleActivity.BOMB_INDEXS, mBombIndexs);
			break;
		case MjAction.ACTION_HUANGPAILIUJU:	
//			mTool.setHuangpailiuju(changeScores);
			break;
		case MjAction.ACTION_LIUJUMANGUAN:	
//			mTool.setLiujumanguan(changeScores, contactIds, isDealerTing);
			intent.putExtra(GameSimpleActivity.CONTACT_IDS, contactIds);
			intent.putExtra(GameSimpleActivity.IS_DEALER_TING, isDealerTing);
			break;
		case MjAction.ACTION_CHANGESCORE:
//			mTool.setChangeScore(changeScores);
			break;
		default:
			break;
		}
		sendBroadcast(intent);
	}
	
	private void broadcastToInvalidate() {
		Intent intent = new Intent(GameSimpleActivity.BROADCAST_RESULT);
		intent.putExtra(GameSimpleActivity.BROADCAST_RESULT, 
				GameSimpleActivity.REQUEST_INVALIDATE);
		sendBroadcast(intent);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
//		onCreateAction();
		super.onConfigurationChanged(newConfig);
	}
	
}

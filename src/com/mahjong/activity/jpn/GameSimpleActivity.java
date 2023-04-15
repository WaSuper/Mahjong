package com.mahjong.activity.jpn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mahjong.R;
import com.mahjong.activity.BaseActivity;
import com.mahjong.activity.PlayerSettingActivity;
import com.mahjong.activity.jpn.game17s.Game17sCalculateActivity;
import com.mahjong.adapter.PlayerSimpleAdapter;
import com.mahjong.common.MjSetting;
import com.mahjong.control.BaseManager;
import com.mahjong.control.BaseManager.OnManageToolListener;
import com.mahjong.control.Game4pManager;
import com.mahjong.control.ManagerTool;
import com.mahjong.dialog.FinalRankDialog;
import com.mahjong.dialog.RoundMenuDialog;
import com.mahjong.dialog.FinalRankDialog.OnCancelListener;
import com.mahjong.dialog.RoundMenuDialog.OnRoundMenuDialogListener;
import com.mahjong.dialog.MjHistoryDialog;
import com.mahjong.model.MjAction;
import com.mahjong.model.MjDetail;
import com.mahjong.model.Player;
import com.mahjong.model.RankItem;
import com.mahjong.tools.AnalysisTool;
import com.mahjong.tools.AudioTool;
import com.mahjong.tools.LightTool;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.CommonDialog.OnAnimationlistener;
import com.mahjong.ui.MjPanelView;
import com.mahjong.ui.MjPanelView.OnMjPanelViewListener;
import com.mahjong.ui.PlayerFuncItem;
import com.mahjong.ui.PlayerFuncItem.OnPlayerFuncItemListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class GameSimpleActivity extends BaseActivity implements
		OnClickListener {

	public static final String MAIN_VISION 		= "MAIN_VISION";
	public static final String BROADCAST_RESULT = "BROADCAST_RESULT";
	public static final String BROADCAST_TEXT 	= "BROADCAST_TEXT";
	public static final String CONTACT_IDS 		= "CONTACT_IDS";
	public static final String CONTACT_CHECK 	= "CONTACT_CHECK";
	public static final String ACTION_TYPE		= "ACTION_TYPE";
	public static final String CHANGE_SCORES	= "CHANGE_SCORES";
	public static final String MAIN_PLAYER		= "MAIN_PLAYER";
	public static final String BOMB_INDEXS		= "BOMB_INDEXS";
	public static final String IS_DEALER_TING	= "IS_DEALER_TING";
	
	public static final int REQUEST_INVALIDATE 				 = 0x1000;
	public static final int REQUEST_CODE_PLAYER_SETTING 	 = 0x1001;
	public static final int REQUEST_CODE_PLAYER_CHANGE 		 = 0x1002;
	public static final int REQUEST_GAME_END 		 		 = 0x1003;
	public static final int REQUEST_ACTION_DONE		 		 = 0x1004;
	
	private Context mContext;

	private PlayerFuncItem mBottomItem;
	private PlayerFuncItem mRightItem;
	private PlayerFuncItem mTopItem;
	private PlayerFuncItem mLeftItem;
	private List<PlayerFuncItem> mPlayerItemList;
	private MjPanelView mPanelView;
	private Button mLiujuBtn;
	private Button mBaopaiBtn;
	private CheckBox mModeBox;
	private TextView mModeText;
	private ImageView mNoviceView;
	private ImageView mExpertView;
	
	private BaseManager mManageTool;
	private int mMainVision = 0; // 主视觉玩家
	
	private CommonDialog mLiujuDialog;
	private Button[] mLiujuButtons = new Button[7];
	private CommonDialog mPlayerChooseDialog;
	private CheckBox[] mPlayerBoxs = new CheckBox[4];
	private CheckBox[] mTingpaiBoxs = new CheckBox[4];
	private LinearLayout mTingpaiLayout;
	private LinearLayout mAdditionLayout;
	private CheckBox mAdditionBox;
	private int mCurLiujuType; // 当前流局种类
	private int mCheckedCount = 0; // mPlayerBoxs的选中数量
	private int[] mPlayerIndexes = {0, 0, 0, 0}; // mPlayerBoxs的选中顺序
	
	private CommonDialog mBaopaiDialog;
	private LinearLayout[] mBaopaiLayout = new LinearLayout[2];
	private CheckBox[] mBaopaiBoxs = new CheckBox[4];
	private TextView[] mBaopaiPlayers = new TextView[4];
	private TextView[] mBaopaiInPlayers = new TextView[3];
	private int[] mBaopaiIndexs = {-1, -1, -1, -1};
	private int mBaopaiSelectIndex;
	private boolean mBaopaiState = false; // 包牌状态
	
	private boolean mResultMode; // 结果模式：true：行家，false：新手
	
	private ResultBroadcast mResultBroadcast;
	
	private String[] winds = new String[4]; // 四风文字
	
	private LightTool mLightTool; // 控制屏幕亮度
	
	private AudioTool mAudioTool; // 音效工具
	
	private ImageView[] mRankImageViews = new ImageView[4]; // 排位控件
	private int[] rankDrawables = {0, R.drawable.rank1_num, R.drawable.rank2_num, 
			R.drawable.rank3_num, R.drawable.rank4_num }; // 排位图片
	
	private Handler mHandler = new Handler();
	
	private boolean landscapeMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		landscapeMode = ShareprefenceTool.getInstance().getBoolean(MjSetting.LANDSCAPE_MODE, this, false);
		if (landscapeMode) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_jpn_game_simple);
		mContext = this;
		mLightTool = new LightTool(this);
		mManageTool = ManagerTool.getInstance().getManager();
		winds[0] = mContext.getString(R.string.east);
		winds[1] = mContext.getString(R.string.south);
		winds[2] = mContext.getString(R.string.west);
		winds[3] = mContext.getString(R.string.north);
		initUI();
		mManageTool.setOnManageToolListener(mManageToolListener);
		mResultBroadcast = new ResultBroadcast();
		IntentFilter intentFilter = new IntentFilter(BROADCAST_RESULT);
		registerReceiver(mResultBroadcast, intentFilter);
		broadcastToInvalidate();
		mAudioTool = new AudioTool();
		mAudioTool.loadAsyn(mManageTool.getAllPlayer());
	}

	@Override
	protected void onPause() {
		super.onPause();
		//mLightTool.stopSleepTask();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//mLightTool.startSleepTask();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mResultBroadcast);
		//mLightTool.onDestroy();
		mAudioTool.onDestory();
	}
	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		return mLightTool.dispatchTouchEvent(super.dispatchTouchEvent(ev));
//	}
	
	private void initUI() {
		mBottomItem = (PlayerFuncItem) findViewById(R.id.game_player_bottom);
		mRightItem = (PlayerFuncItem) findViewById(R.id.game_player_right);
		mTopItem = (PlayerFuncItem) findViewById(R.id.game_player_top);
		mLeftItem = (PlayerFuncItem) findViewById(R.id.game_player_left);
		mPanelView = (MjPanelView) findViewById(R.id.game_panel_center);
		mLiujuBtn = (Button) findViewById(R.id.game_btn_liuju);
		mBaopaiBtn = (Button) findViewById(R.id.game_btn_baopai);
		mModeBox = (CheckBox) findViewById(R.id.game_switch_mode);
		mModeText = (TextView) findViewById(R.id.game_tv_mode);
		mNoviceView = (ImageView) findViewById(R.id.game_img_novice);
		mExpertView = (ImageView) findViewById(R.id.game_img_expert);
		mRankImageViews[0] = (ImageView) findViewById(R.id.game_player_rank_bottom);
		mRankImageViews[1] = (ImageView) findViewById(R.id.game_player_rank_right);
		mRankImageViews[2] = (ImageView) findViewById(R.id.game_player_rank_top);
		mRankImageViews[3] = (ImageView) findViewById(R.id.game_player_rank_left);
		
		mTopItem.setOnPlayerFuncItemListener(mPlayerFuncItemListener);
		mLeftItem.setOnPlayerFuncItemListener(mPlayerFuncItemListener);
		mBottomItem.setOnPlayerFuncItemListener(mPlayerFuncItemListener);
		mRightItem.setOnPlayerFuncItemListener(mPlayerFuncItemListener);
		mPanelView.setOnMjPanelViewListener(mMjPanelViewListener);
		mLiujuBtn.setOnClickListener(this);
		mBaopaiBtn.setOnClickListener(this);	
		if (mManageTool.is17Step()) {
			mBaopaiBtn.setVisibility(View.INVISIBLE);
			mBottomItem.setEnableZimo(false);
			mRightItem.setEnableZimo(false);
			mTopItem.setEnableZimo(false);
			mLeftItem.setEnableZimo(false);
		}
		
		mPlayerItemList = new ArrayList<PlayerFuncItem>();
		mPlayerItemList.add(mBottomItem);
		mPlayerItemList.add(mRightItem);
		mPlayerItemList.add(mTopItem);
		mPlayerItemList.add(mLeftItem);
		mBottomItem.setPlayer(mManageTool.getPlayer(0), 0, 0);
		mRightItem.setPlayer(mManageTool.getPlayer(1), 1, 1);
		mTopItem.setPlayer(mManageTool.getPlayer(2), 2, 2);
		mLeftItem.setPlayer(mManageTool.getPlayer(3), 3, 3);
		int memberCount = mManageTool.getMemberCount();
		mPanelView.setBaseData(mManageTool.getBaseScore(), memberCount, true);
		mPanelView.set17StepPanel(mManageTool.is17Step(), mManageTool.getFengType());
		// 根据人数隐藏控件
		if (memberCount < 4) {
			mLeftItem.setVisibility(View.INVISIBLE);
			mRankImageViews[3].setVisibility(View.INVISIBLE);
			if (memberCount < 3) {
				mRightItem.setVisibility(View.INVISIBLE);
				mRankImageViews[1].setVisibility(View.INVISIBLE);
			}
		}
		
		mResultMode = ShareprefenceTool.getInstance()
				.getBoolean(MjSetting.MODE_NOVICE_EXPERT, mContext, false);
		showModeText(mResultMode);
		mModeBox.setChecked(mResultMode);
		mModeBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton button, boolean checked) {
				showModeText(checked);
			}
		});		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			
	}
	
	@Override
	public void onBackPressed() {
		showGameEndDialog();
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.game_btn_liuju:
			showLiuJuDialog();
			break;
		case R.id.game_btn_baopai:
			showBaopaiDialog();
			break;
		default:
			break;
		}
	}

	private void showModeText(boolean checked) {
		mResultMode = checked;
		if (checked) {
			mModeText.setText(getString(R.string.expert));
			mExpertView.setVisibility(View.VISIBLE);
			mNoviceView.setVisibility(View.INVISIBLE);
		} else {
			mModeText.setText(getString(R.string.novice));
			mExpertView.setVisibility(View.INVISIBLE);
			mNoviceView.setVisibility(View.VISIBLE);
		}
		ShareprefenceTool.getInstance().setBoolean(MjSetting.MODE_NOVICE_EXPERT, checked, mContext);
	}
	
	private void showBaopaiDialog() {
		if (mBaopaiDialog == null) {
			mBaopaiDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
			mBaopaiDialog.addView(R.layout.layout_baopai);
			mBaopaiDialog.setCanceledOnTouchOutside(true);
			mBaopaiDialog.titleTextView.setText(R.string.baopai);
			View contentView = mBaopaiDialog.getContentView();
			mBaopaiLayout[0] = (LinearLayout) contentView.findViewById(R.id.baopai_tab1);
			mBaopaiLayout[1] = (LinearLayout) contentView.findViewById(R.id.baopai_tab2);
			mBaopaiBoxs[0] = (CheckBox) contentView.findViewById(R.id.baopai_player_left1);
			mBaopaiBoxs[1] = (CheckBox) contentView.findViewById(R.id.baopai_player_left2);
			mBaopaiBoxs[2] = (CheckBox) contentView.findViewById(R.id.baopai_player_left3);
			mBaopaiBoxs[3] = (CheckBox) contentView.findViewById(R.id.baopai_player_left4);
			mBaopaiPlayers[0] = (TextView) contentView.findViewById(R.id.baopai_player_right1);
			mBaopaiPlayers[1] = (TextView) contentView.findViewById(R.id.baopai_player_right2);
			mBaopaiPlayers[2] = (TextView) contentView.findViewById(R.id.baopai_player_right3);
			mBaopaiPlayers[3] = (TextView) contentView.findViewById(R.id.baopai_player_right4);
			mBaopaiInPlayers[0] = (TextView) contentView.findViewById(R.id.baopai_tab2_player1);
			mBaopaiInPlayers[1] = (TextView) contentView.findViewById(R.id.baopai_tab2_player2);
			mBaopaiInPlayers[2] = (TextView) contentView.findViewById(R.id.baopai_tab2_player3);
			final int memberCount = mManageTool.getMemberCount();
			if (memberCount < 4) {
				mBaopaiBoxs[3].setVisibility(View.GONE);
				mBaopaiPlayers[3].setVisibility(View.GONE);
				mBaopaiInPlayers[2].setVisibility(View.GONE);
			}
			for (int i = 0; i < memberCount; i++) {
				mBaopaiBoxs[i].setText(mManageTool.getPlayer(i).getNickName());
				mBaopaiBoxs[i].setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton btn, boolean check) {
						int index = 0;
						switch (btn.getId()) {
						case R.id.baopai_player_left1:
							index = 0;
							break;
						case R.id.baopai_player_left2:
							index = 1;
							break;
						case R.id.baopai_player_left3:
							index = 2;
							break;
						case R.id.baopai_player_left4:
							index = 3;
							break;
						default:
							break;
						}
						mManageTool.setEnableBaopai(index, check);
					}
				});
			}		
			for (int i = 0; i < memberCount; i++) {
				mBaopaiPlayers[i].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						switch (v.getId()) {
						case R.id.baopai_player_right1:	
							mBaopaiSelectIndex = 0;
							break;
						case R.id.baopai_player_right2:
							mBaopaiSelectIndex = 1;
							break;
						case R.id.baopai_player_right3:
							mBaopaiSelectIndex = 2;
							break;
						case R.id.baopai_player_right4:
							mBaopaiSelectIndex = 3;
							break;
						default:
							break;
						}
						Player[] players = mManageTool.getAllPlayer();
						int baoIndex = 0;
						for (int j = 0; j < memberCount; j++) {
							if (j == mBaopaiSelectIndex) continue;
							mBaopaiInPlayers[baoIndex++].setText(players[j].getNickName());
						}
						mBaopaiDialog.closeAnimation();
					}
				});
			}
			for (int i = 0; i < mBaopaiInPlayers.length; i++) {
				mBaopaiInPlayers[i].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int[][] table = {{1, 2, 3},{0, 2, 3},{0, 1, 3},{0, 1, 2}};
						switch (v.getId()) {
						case R.id.baopai_tab2_player1:
							mBaopaiIndexs[mBaopaiSelectIndex] = table[mBaopaiSelectIndex][0];
							break;
						case R.id.baopai_tab2_player2:
							mBaopaiIndexs[mBaopaiSelectIndex] = table[mBaopaiSelectIndex][1];
							break;
						case R.id.baopai_tab2_player3:
							mBaopaiIndexs[mBaopaiSelectIndex] = table[mBaopaiSelectIndex][2];
							break;
						default:
							break;
						}
						mBaopaiPlayers[mBaopaiSelectIndex].setText(
								mManageTool.getPlayer(mBaopaiIndexs[mBaopaiSelectIndex]).getNickName());
						mManageTool.setBaopaiPlayer(mBaopaiSelectIndex, mBaopaiIndexs[mBaopaiSelectIndex]);
						mBaopaiDialog.openAnimation();
					}
				});
			}
			mBaopaiDialog.setOnAnimationListener(new OnAnimationlistener() {
				
				@Override
				public void onOpenEnd() {
					mBaopaiLayout[0].setVisibility(View.VISIBLE);
					mBaopaiLayout[1].setVisibility(View.GONE);	
					mBaopaiDialog.ok.setVisibility(View.VISIBLE);			
				}
				
				@Override
				public void onCloseEnd() {
					mBaopaiLayout[0].setVisibility(View.GONE);
					mBaopaiLayout[1].setVisibility(View.VISIBLE);
					mBaopaiDialog.ok.setVisibility(View.INVISIBLE);
				}
			});
			mBaopaiDialog.ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					boolean isChecked = false;
					for (int i = 0; i < mBaopaiBoxs.length; i++) {
						if (mBaopaiBoxs[i].isChecked()) {
							showBaopaiState(true);
							isChecked = true;
							break;
						}
					}
					if (!isChecked) showBaopaiState(false);
					mBaopaiDialog.dismiss();
				}
			});
			mBaopaiDialog.cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mBaopaiDialog.ok.getVisibility() == View.VISIBLE) {
						mBaopaiDialog.dismiss();
					} else {
						mBaopaiDialog.openAnimation();
					}					
				}
			});
		}			
		mBaopaiLayout[0].setVisibility(View.VISIBLE);
		mBaopaiLayout[1].setVisibility(View.GONE);	
		mBaopaiDialog.ok.setVisibility(View.VISIBLE);
		mBaopaiDialog.show();
	}
	
	private void showBaopaiState(boolean state) {
		mBaopaiState = state;
		if (state) {
			mBaopaiBtn.setBackgroundResource(R.drawable.ic_game_btn_right);			
		} else {
			mBaopaiBtn.setBackgroundResource(R.drawable.selector_game_btn_right);
		}
	}
	
	private void showLiuJuDialog() {
		if (mLiujuDialog == null) {
			mLiujuDialog = new CommonDialog(mContext, R.style.MyDialogStyle, true);
			mLiujuDialog.addView(R.layout.layout_liuju);
			mLiujuDialog.setCanceledOnTouchOutside(true);
			mLiujuDialog.titleTextView.setText(getString(R.string.please_choose_type_liuju));
			View contentView = mLiujuDialog.getContentView();
			mLiujuButtons[0] = (Button) contentView.findViewById(R.id.liuju_huangpai);
			mLiujuButtons[1] = (Button) contentView.findViewById(R.id.liuju_sifenglianda);
			mLiujuButtons[2] = (Button) contentView.findViewById(R.id.liuju_sigangsanle);
			mLiujuButtons[3] = (Button) contentView.findViewById(R.id.liuju_jiupaijiuzhong);
			mLiujuButtons[4] = (Button) contentView.findViewById(R.id.liuju_sijializhi);
			mLiujuButtons[5] = (Button) contentView.findViewById(R.id.liuju_sanjiaheliao);
			mLiujuButtons[6] = (Button) contentView.findViewById(R.id.liuju_liujumanguan);
			for (int i = 0; i < mLiujuButtons.length; i++) {
				mLiujuButtons[i].setOnClickListener(mLiujuListener);
			}
			switch (mManageTool.getMemberCount()) {
			case 3:
			case 2:
				contentView.findViewById(R.id.liuju_layout_sifenglianda).setVisibility(View.GONE);
				contentView.findViewById(R.id.liuju_layout_sijializhi).setVisibility(View.GONE);
				contentView.findViewById(R.id.liuju_layout_sanjiaheliao).setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
		mLiujuDialog.show();
		mAudioTool.stopPlayer();
	}
	
	private OnClickListener mLiujuListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.liuju_huangpai:
				showPlayerChooseDialog(R.id.liuju_huangpai, 
						getString(R.string.please_ensure_tingpai_player),
						mManageTool.getPlayerTingpai(0), mManageTool.getPlayerTingpai(1),
						mManageTool.getPlayerTingpai(2), mManageTool.getPlayerTingpai(3));
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_sifenglianda:
				showPlayerChooseDialog(R.id.liuju_sifenglianda, 
						getString(R.string.please_choose_wind),
						false, false, false, false);
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_sigangsanle:
				showPlayerChooseDialog(R.id.liuju_sigangsanle, 
						getString(R.string.please_choose_gang_player),
						false, false, false, false);
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_jiupaijiuzhong:
				showPlayerChooseDialog(R.id.liuju_jiupaijiuzhong, 
						getString(R.string.please_choose_jpjz_player),
						false, false, false, false);
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_sijializhi:
				showPlayerChooseDialog(R.id.liuju_sijializhi, 
						getString(R.string.please_ensure_lizhi_player),
						mManageTool.getPlayerIsLizhi(0), mManageTool.getPlayerIsLizhi(1),
						mManageTool.getPlayerIsLizhi(2), mManageTool.getPlayerIsLizhi(3));
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_sanjiaheliao:
				showPlayerChooseDialog(R.id.liuju_sanjiaheliao, 
						getString(R.string.please_choose_hele_player),
						false, false, false, false);
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_liujumanguan:
				showPlayerChooseDialog(R.id.liuju_liujumanguan, 
						getString(R.string.please_choose_manguan_player),
						false, false, false, false);
				mLiujuDialog.dismiss();
				break;
			default:
				break;
			}
		}
	};
	
	private void showPlayerChooseDialog(int id, String title, 
			 boolean player0, boolean player1, boolean player2, boolean player3) {
		if (mPlayerChooseDialog == null) {
			mPlayerChooseDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
			mPlayerChooseDialog.addView(R.layout.layout_choose_players);
			mPlayerChooseDialog.setCanceledOnTouchOutside(true);
			mPlayerChooseDialog.ok.setText(getString(R.string.ok));
			mPlayerChooseDialog.ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					if (doLiujuAction())
						mPlayerChooseDialog.dismiss();
				}
			});			
			View contentView = mPlayerChooseDialog.getContentView();
			mPlayerBoxs[0] = (CheckBox) contentView.findViewById(R.id.choose_player1);
			mPlayerBoxs[1] = (CheckBox) contentView.findViewById(R.id.choose_player2);
			mPlayerBoxs[2] = (CheckBox) contentView.findViewById(R.id.choose_player3);
			mPlayerBoxs[3] = (CheckBox) contentView.findViewById(R.id.choose_player4);
			mTingpaiBoxs[0] = (CheckBox) contentView.findViewById(R.id.choose_tingpai1);
			mTingpaiBoxs[1] = (CheckBox) contentView.findViewById(R.id.choose_tingpai2);
			mTingpaiBoxs[2] = (CheckBox) contentView.findViewById(R.id.choose_tingpai3);
			mTingpaiBoxs[3] = (CheckBox) contentView.findViewById(R.id.choose_tingpai4);
			mTingpaiLayout = (LinearLayout) contentView.findViewById(R.id.choose_ll_tingpai);
			mAdditionLayout = (LinearLayout) contentView.findViewById(R.id.choose_ll_bottom);
			mAdditionBox = (CheckBox) contentView.findViewById(R.id.choose_additon);
			for (CheckBox cBox : mPlayerBoxs) {
				cBox.setOnCheckedChangeListener(mLiuJuDetailListener);
			}
		}
		mCurLiujuType = id;
		mCheckedCount = 0;
		Arrays.fill(mPlayerIndexes, 0);
		if (id == R.id.liuju_sifenglianda) {
			mPlayerBoxs[0].setText(getString(R.string.east));
			mPlayerBoxs[1].setText(getString(R.string.south));
			mPlayerBoxs[2].setText(getString(R.string.west));
			mPlayerBoxs[3].setText(getString(R.string.north));
		} else {
			mPlayerBoxs[0].setText(mManageTool.getPlayerNameWithWind(0, winds));
			mPlayerBoxs[1].setText(mManageTool.getPlayerNameWithWind(1, winds));
			mPlayerBoxs[2].setText(mManageTool.getPlayerNameWithWind(2, winds));
			mPlayerBoxs[3].setText(mManageTool.getPlayerNameWithWind(3, winds));			
		}
		if (id == R.id.liuju_huangpai || id == R.id.liuju_liujumanguan) {
			mAdditionLayout.setVisibility(View.GONE);
			if (id == R.id.liuju_liujumanguan) {
				mTingpaiLayout.setVisibility(View.VISIBLE);
				mTingpaiBoxs[0].setChecked(mManageTool.getPlayerTingpai(0));
				mTingpaiBoxs[1].setChecked(mManageTool.getPlayerTingpai(1));
				mTingpaiBoxs[2].setChecked(mManageTool.getPlayerTingpai(2));
				mTingpaiBoxs[3].setChecked(mManageTool.getPlayerTingpai(3));
			} else {
				mTingpaiLayout.setVisibility(View.INVISIBLE);
			}
		} else {			
			mAdditionLayout.setVisibility(View.VISIBLE);
			mAdditionBox.setText(getString(R.string.rollcount_plus_one));
			mAdditionBox.setChecked(true);
			mAdditionBox.setEnabled(false);	
			mTingpaiLayout.setVisibility(View.INVISIBLE);
		}		
		mPlayerChooseDialog.titleTextView.setText(title);
		mPlayerBoxs[0].setChecked(player0);
		mPlayerBoxs[1].setChecked(player1);
		mPlayerBoxs[2].setChecked(player2);
		mPlayerBoxs[3].setChecked(player3);
		switch (mManageTool.getMemberCount()) { // 根据人数隐藏控件
		case 2:
			mPlayerBoxs[1].setVisibility(View.GONE);
			mTingpaiBoxs[1].setVisibility(View.GONE);
			mPlayerBoxs[3].setVisibility(View.GONE);
			mTingpaiBoxs[3].setVisibility(View.GONE);
			break;
		case 3:
			mPlayerBoxs[3].setVisibility(View.GONE);
			mTingpaiBoxs[3].setVisibility(View.GONE);
			break;
		default:
			break;
		}
		mPlayerChooseDialog.show();
	}
	
	private OnCheckedChangeListener mLiuJuDetailListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton btn, boolean isCheck) {
			if (mCurLiujuType == R.id.liuju_sigangsanle) {
				int index = 0;
				switch (btn.getId()) {
				case R.id.choose_player1:
					index = 0;
					break;
				case R.id.choose_player2:
					index = 1;
					break;
				case R.id.choose_player3:
					index = 2;
					break;
				case R.id.choose_player4:
					index = 3;
					break;
				default:
					break;
				}
				if (isCheck) {
					if (mCheckedCount >= 0 && mCheckedCount < 4) {
						mCheckedCount++;
						mPlayerIndexes[index] = mCheckedCount;
						mPlayerBoxs[index].setText("(" + mPlayerIndexes[index] + ")" 
								+ mManageTool.getPlayerNameWithWind(index, winds));
					}
				} else {
					if (mCheckedCount > 0 && mCheckedCount <= 4) {
						int curIndex = mPlayerIndexes[index];
						for (int i = 0; i < mPlayerBoxs.length; i++) {
							if (i == index) {
								mPlayerBoxs[i].setText(mManageTool.getPlayerNameWithWind(i, winds));
								mPlayerIndexes[i] = 0;
							} else {
								if (mPlayerIndexes[i] < curIndex) {
									continue;
								} else {
									mPlayerIndexes[i]--;
									mPlayerBoxs[i].setText("(" + mPlayerIndexes[i] + ")" 
											+ mManageTool.getPlayerNameWithWind(i, winds));
								}
							}
						}
						mCheckedCount--;
					}
				}
			}
		}
	};
	
	private boolean doLiujuAction() {
		int count = 0;
		for (int i = 0; i < mPlayerBoxs.length; i++) {
			if (mPlayerBoxs[i].isChecked()) count++;
		}
		Intent intent;
		switch (mCurLiujuType) {
		case R.id.liuju_huangpai:
			// 庄家听牌：连庄，本场+1；庄家无听牌，流庄，本场+1
			mManageTool.setPlayerTingPai(mPlayerBoxs[0].isChecked(), mPlayerBoxs[1].isChecked(), 
					mPlayerBoxs[2].isChecked(), mPlayerBoxs[3].isChecked());
			if (landscapeMode) {
				intent = new Intent(GameSimpleActivity.this, ResultShowForLand.class);
			} else {
				intent = new Intent(GameSimpleActivity.this, ResultShow.class);
			}
			intent.putExtra(MjAction.Name, MjAction.ACTION_HUANGPAILIUJU);
			intent.putExtra(MAIN_VISION, mMainVision);
			startActivity(intent);
			break;
		case R.id.liuju_sifenglianda:
			// 连庄，判断是否本场+1
			if (count == 1) {
				int wind = 0;
				for (int i = 0; i < mPlayerBoxs.length; i++) {
					if (mPlayerBoxs[i].isChecked()) {
						wind = i;
						break;
					}
				}
				mManageTool.setSifenglianda(wind, mAdditionBox.isChecked());
				broadcastToInvalidate();
			} else {
				ToastTool.showToast(mContext, R.string.please_choose_only_one);
				return false;
			}
			break;
		case R.id.liuju_sigangsanle:
			// 连庄，判断是否本场+1
			if (count > 1) {
				String[] gangIds = new String[count];
//				int index = 0;
				for (int i = 0; i < mPlayerBoxs.length; i++) {
					if (mPlayerBoxs[i].isChecked() && mPlayerIndexes[i] > 0) {
						gangIds[--mPlayerIndexes[i]] = mManageTool.getPlayer(i).getUuid();
//						gangIds[index++] = mManageTool.getPlayer(i).getUuid();
					}
				}
				mManageTool.setSigangsanle(count, gangIds, mAdditionBox.isChecked());
				broadcastToInvalidate();
			} else {
				ToastTool.showToast(mContext, R.string.please_choose_at_least_two);
				return false;
			}
			break;
		case R.id.liuju_jiupaijiuzhong:
			// 连庄，判断是否本场+1
			if (count == 1) {
				String liuId = "";
				for (int i = 0; i < mPlayerBoxs.length; i++) {
					if (mPlayerBoxs[i].isChecked()) {
						liuId = mManageTool.getPlayer(i).getUuid();
						break;
					}
				}
				mManageTool.setJiupaijiuzhong(liuId, mAdditionBox.isChecked());
				broadcastToInvalidate();
			} else {
				ToastTool.showToast(mContext, R.string.please_choose_only_one);
				return false;
			}
			break;
		case R.id.liuju_sijializhi:
			// 连庄，判断是否本场+1
			count = 0;
			for (int i = 0; i < 4; i++) {
				if (mManageTool.getPlayerIsLizhi(i)) count++;
			}
			if (count == 4) {
				mManageTool.setSijializhi(mAdditionBox.isChecked());
				broadcastToInvalidate();
			} else {
				ToastTool.showToast(mContext, R.string.please_back_to_lizhi);
				return false;
			}
			break;
		case R.id.liuju_sanjiaheliao:
			// 判断规则是否允许，连庄，判断是否本场+1
			if (count == 3) {
				String[] heIds = new String[count];
				int index = 0;
				for (int i = 0; i < mPlayerBoxs.length; i++) {
					if (mPlayerBoxs[i].isChecked()) {
						heIds[index++] = mManageTool.getPlayer(i).getUuid();
					}
				}
				mManageTool.setSanjiahele(heIds, mAdditionBox.isChecked());
				broadcastToInvalidate();
			} else {
				ToastTool.showToast(mContext, R.string.please_choose_only_three);
				return false;
			}
			break;
		case R.id.liuju_liujumanguan:
			// 从庄家开始按顺序结算，若没人被飞，则庄家查听判断是否连庄，本场+1
			if (count > 0) {
				mManageTool.setPlayerTingPai(mTingpaiBoxs[0].isChecked(), mTingpaiBoxs[1].isChecked(), 
						mTingpaiBoxs[2].isChecked(), mTingpaiBoxs[3].isChecked());
				boolean[] isManGuans = new boolean[4];
				for (int i = 0; i < mPlayerBoxs.length; i++) {
					isManGuans[i] = mPlayerBoxs[i].isChecked();
				}
				intent = new Intent(GameSimpleActivity.this, ResultShow.class);
				intent.putExtra(MjAction.Name, MjAction.ACTION_LIUJUMANGUAN);
				intent.putExtra(MAIN_VISION, mMainVision);
				intent.putExtra(CONTACT_IDS, isManGuans);
				intent.putExtra(CONTACT_CHECK, mManageTool.getPlayerTingpai(mManageTool.getDealer()));
				startActivity(intent);
			} else {
				ToastTool.showToast(mContext, R.string.please_choose_at_least_one);
				return false;
			}
			break;
		default:
			break;
		}
		return true;
	}
	
	private void broadcastToInvalidate() {
		Intent intent = new Intent(BROADCAST_RESULT);
		intent.putExtra(BROADCAST_RESULT, REQUEST_INVALIDATE);
		sendBroadcast(intent);
	}
	
	private OnPlayerFuncItemListener mPlayerFuncItemListener = new OnPlayerFuncItemListener() {
		
		private int[] port_orientations = {0, -1, 2, 1};
		private int[] land_orientations = {1, 0, -1, 2};
		
		private Intent getResultSetComplexActivity(int pos) {
			Intent intent;
			if ((landscapeMode && land_orientations[pos] == 1)
					|| (!landscapeMode && port_orientations[pos] == 1)) {
				intent = new Intent(GameSimpleActivity.this, ResultSetComplexActivityForLand.class);
			} else if ((landscapeMode && land_orientations[pos] == -1)
					|| (!landscapeMode && port_orientations[pos] == -1)) {
				intent = new Intent(GameSimpleActivity.this, ResultSetComplexActivityForReLand.class);
			} else if ((landscapeMode && land_orientations[pos] == 2)
					|| (!landscapeMode && port_orientations[pos] == 2)) {
				intent = new Intent(GameSimpleActivity.this, ResultSetComplexActivityForRePort.class);
			} else {
				intent = new Intent(GameSimpleActivity.this, ResultSetComplexActivity.class);
			}
			return intent;
		}
		
		@Override
		public void onClickZimo(Player player, int orgIndex, int pos) {
			Intent intent;
			if (mResultMode) {
				intent = new Intent(GameSimpleActivity.this, ResultSetSimpleActivity.class);
			} else {
				intent = getResultSetComplexActivity(pos);
			}
			intent.putExtra(Game4pManager.PLAYER_ITEM_POSITION, pos);
			intent.putExtra(Game4pManager.PLAYER_IS_ZIMO, true);
			intent.putExtra(Game4pManager.PLAYER_ORIGINAL_INDEX, orgIndex);
			intent.putExtra(MAIN_VISION, mMainVision);
			startActivity(intent);
			mAudioTool.playZimo(orgIndex);
			mAudioTool.stopPlayer();
		}
		
		@Override
		public void onClickRonghe(Player player, int orgIndex, int pos) {
			Intent intent;
			if (mResultMode) {
				intent = new Intent(GameSimpleActivity.this, ResultSetSimpleActivity.class);
			} else {
				intent = getResultSetComplexActivity(pos);
			}
			intent.putExtra(Game4pManager.PLAYER_ITEM_POSITION, pos);
			intent.putExtra(Game4pManager.PLAYER_IS_ZIMO, false);
			intent.putExtra(Game4pManager.PLAYER_ORIGINAL_INDEX, orgIndex);
			intent.putExtra(MAIN_VISION, mMainVision);
			startActivity(intent);
			mAudioTool.playRonghe(orgIndex);
			mAudioTool.stopPlayer();
		}

		@Override
		public void onClickBomb(Player player, int orgIndex, int pos) {
			Intent intent;
			if (mResultMode) {
				intent = new Intent(GameSimpleActivity.this, ResultSetBombSimpleActivity.class);
			} else {
				intent = getResultSetComplexActivity(pos);
			}
			intent.putExtra(Game4pManager.PLAYER_ITEM_POSITION, pos);
			intent.putExtra(Game4pManager.PLAYER_ORIGINAL_INDEX, orgIndex);
			intent.putExtra(MAIN_VISION, mMainVision);
			intent.putExtra(Game4pManager.PLAYER_IS_BOMB, true);
			startActivity(intent);
			mAudioTool.stopPlayer();
		}	
		
		@Override
		public void onClickFeng(Player player, int orgIndex, int pos) {

		}
		
		@Override
		public int onClickLizhi(Player player, int orgIndex, int pos, int lizhi) {
			if (lizhi > 0 || mManageTool.getPlayerScore(orgIndex) < 1000) {
				ToastTool.showToast(mContext, R.string.lizhi_fail);
				return 0;
			}
			int result = 1;			
			int lizhiCount = mManageTool.setPlayerLizhi(orgIndex, result);
			// 更新ui
			mPanelView.setPlayerLizhi(orgIndex, result, lizhiCount);
			mPanelView.setPlayerScore(mManageTool.getPlayerScores());
			// 播放音效
			mAudioTool.playLizhi(orgIndex);
			mAudioTool.playBgm(orgIndex);
			return result;
		}
		
		@Override
		public void onClickIcon(final Player player, final int orgIndex, final int pos) {
			final CommonDialog mIconDialog = new CommonDialog(mContext, R.style.MyDialogStyle, true);
			mIconDialog.addView(R.layout.layout_player_selection);
			mIconDialog.setCanceledOnTouchOutside(true);
			mIconDialog.titleTextView.setText(R.string.select);
			View view = mIconDialog.getContentView();
			TextView mSettingView = (TextView) view.findViewById(R.id.player_sel_setting);
			TextView mChangeView = (TextView) view.findViewById(R.id.player_sel_change);
			OnClickListener listener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.player_sel_setting:
						Intent intent = new Intent(
								GameSimpleActivity.this, PlayerSettingActivity.class);
						intent.putExtra(Player.Col_Uuid, player.getUuid());
						intent.putExtra("", orgIndex);
						startActivity(intent);
						break;
					case R.id.player_sel_change:
						showChangePlayerDialog(player, orgIndex, pos);
						break;
					default:
						break;
					}
					mIconDialog.dismiss();
				}
			};
			mSettingView.setOnClickListener(listener);
			mChangeView.setOnClickListener(listener);
			mIconDialog.show();
		}	
		
	};
	
	private void showChangePlayerDialog(final Player player, final int orgIndex, final int pos) {
		final CommonDialog mPlayerDialog = new CommonDialog(mContext, R.style.MyDialogStyle, true);
		mPlayerDialog.addView(R.layout.listview);
		mPlayerDialog.setCanceledOnTouchOutside(true);
		mPlayerDialog.titleTextView.setText(getString(R.string.please_choose_player));
		ListView listView = (ListView) mPlayerDialog.getContentView();
		PlayerSimpleAdapter mAdapter = new PlayerSimpleAdapter(mContext);
		listView.setAdapter(mAdapter);
		String[] ids = new String[4];
		Player[] players = mManageTool.getAllPlayer();
		for (int i = 0; i < players.length; i++) {
			ids[i] = players[i].getUuid();
		}
		final List<Player> list = PlayerSimpleAdapter.getPlayersExceptUesd(ids);
		mAdapter.setData(list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Player selPlayer = list.get(position);
				mManageTool.changePlayer(orgIndex, selPlayer);	
				mPlayerItemList.get(pos).resetPlayer(selPlayer);	
				mAudioTool.loadAsynByPlayer(selPlayer.getUuid(), orgIndex);
				switch (orgIndex) {
				case 0:
					ShareprefenceTool.getInstance().setString(
							MjSetting.PLAYER_EAST, selPlayer.getUuid(), mContext);
					break;
				case 1:
					ShareprefenceTool.getInstance().setString(
							MjSetting.PLAYER_SOUTH, selPlayer.getUuid(), mContext);						
					break;
				case 2:
					ShareprefenceTool.getInstance().setString(
							MjSetting.PLAYER_WEST, selPlayer.getUuid(), mContext);						
					break;
				case 3:
					ShareprefenceTool.getInstance().setString(
							MjSetting.PLAYER_NORTH, selPlayer.getUuid(), mContext);						
					break;
				default:
					break;
				}
				mPlayerDialog.dismiss();
			}
		});
		mPlayerDialog.show();
	}
	
	private OnMjPanelViewListener mMjPanelViewListener = new OnMjPanelViewListener() {
		
		@Override
		public void onClickRevoke() {			
			revokeLastRound(true);
		}
		
		@Override
		public void onClickHistory() {
			MjHistoryDialog dialog = new MjHistoryDialog(mContext, 
					mManageTool.getAllDetails(), mManageTool.getAllPlayer(),
					mManageTool.getResult());
			dialog.show();
		}
		
		@Override
		public void onClickHelp() {
			showHelpDialog();
		}
		
		@Override
		public void onClickEixt() {
			showGameEndDialog();
		}
		
		@Override
		public void onClickCenter() {
			Intent intent = new Intent(GameSimpleActivity.this, DiceActivity.class);
			int dealer = mManageTool.getDealer();
			for (int i = 0; i < mPlayerItemList.size(); i++) {
				PlayerFuncItem item = mPlayerItemList.get(i);
				if (item.getOriginalIndex() == dealer) {
					intent.putExtra(Game4pManager.PLAYER_ITEM_POSITION, item.getItemPosition());
					break;
				}
			}
			startActivity(intent);
		}
		
		@Override
		public void onClickPoint(int pos) {
			switch (pos) {
			case 0: // 下方				
				break;
			case 1: // 右方
				break;
			case 2: // 上方
				break;
			case 3: // 左方
				break;
			default:
				break;
			}
		};
	};
	
	private void revokeLastRound(boolean isShowDialog) {
		if (!mManageTool.hasLastDetail()) {
			if (isShowDialog) {
				ToastTool.showToast(mContext, R.string.revoke_fail);
			}
		} else {
			MjDetail detail = mManageTool.getLastDetail();
			if (detail.isLizhi()) {
				String playerId = detail.getAction().id0;
				int playerIndex = mManageTool.findPlayerOrgIndexById(playerId);
				int lizhiCount = mManageTool.revokePlayerLizhi(playerIndex);
				// 更新ui
				int result = 0;
				mPanelView.setPlayerLizhi(playerIndex, result, lizhiCount);
				mPanelView.setPlayerScore(mManageTool.getPlayerScores());
				for (PlayerFuncItem item : mPlayerItemList) {
					if (item.getOriginalIndex() == playerIndex) {
						item.setLizhiState(result);
						break;
					}
				}
				mAudioTool.stopPlayer();
			} else {
				if (isShowDialog) {
					final CommonDialog dialog = new CommonDialog(mContext, R.style.MyDialogStyle);
					dialog.addView(R.layout.item_text);
					TextView text = (TextView) dialog.getContentView().findViewById(R.id.item_text);
					text.setText(mManageTool.getDetailText(mContext, detail, winds));
					dialog.titleTextView.setText(R.string.ensure_revoke_last_round);
					dialog.ok.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View view) {
							revokeLastRoundEx();
							dialog.dismiss();
							mAudioTool.stopPlayer();
						}
					});
					dialog.cancel.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View view) {
							dialog.dismiss();
						}
					});
					dialog.show();
				} else {
					revokeLastRoundEx();
					mAudioTool.stopPlayer();
				}				
			}
		}
	}
	
	private void revokeLastRoundEx() {
		int[] lizhis = mManageTool.revokeLastRound();	
		mPanelView.setPlayersLizhi(lizhis, mManageTool.getLizhiCount());
		broadcastToInvalidate();
	}
	
	private void showHelpDialog() {
		final CommonDialog mHelpDialog = new CommonDialog(mContext, R.style.MyDialogStyle, true);
		mHelpDialog.addView(R.layout.layout_help);
		mHelpDialog.setCanceledOnTouchOutside(true);
		mHelpDialog.titleTextView.setText(R.string.help);
		View view = mHelpDialog.getContentView();
		TextView calcView = (TextView) view.findViewById(R.id.help_calculator);
		TextView visionView = (TextView) view.findViewById(R.id.help_main_vision);
		LinearLayout audioView = (LinearLayout) view.findViewById(R.id.help_audio);
		TextView switchView = (TextView) view.findViewById(R.id.help_audio_switch);
		switchView.setText(mAudioTool.getAudioEnable() ? 
				getResources().getString(R.string.switch_on) : getResources().getString(R.string.switch_off));
		TextView scoreView = (TextView) view.findViewById(R.id.help_change_score);
		TextView noteView = (TextView) view.findViewById(R.id.help_note);
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.help_calculator:
					switch (mManageTool.getMainType()) {
					case BaseManager.MainType_4p:
						startActivity(new Intent(GameSimpleActivity.this, CalculateActivity.class));
						break;
					case BaseManager.MainType_3p:
						startActivity(new Intent(GameSimpleActivity.this, CalculateActivity.class));
						break;
					case BaseManager.MainType_17s:
						startActivity(new Intent(GameSimpleActivity.this, Game17sCalculateActivity.class));
						break;
					default:
						break;
					}
					break;
				case R.id.help_main_vision:
					showRoundViewDialog();
					break;
				case R.id.help_audio:
					mAudioTool.setAudioEnable(!mAudioTool.getAudioEnable());
					break;
				case R.id.help_change_score:
					Intent intent = new Intent(GameSimpleActivity.this, ChangeScoreActivity.class);
					intent.putExtra(MAIN_VISION, mMainVision);
					startActivity(intent);
					break;
				case R.id.help_note:
					editNote();
					break;
				default:
					break;
				}
				mHelpDialog.dismiss();
			}
		};
		calcView.setOnClickListener(listener);
		visionView.setOnClickListener(listener);
		audioView.setOnClickListener(listener);
		scoreView.setOnClickListener(listener);
		noteView.setOnClickListener(listener);
		mHelpDialog.show();
	}
	
	private void editNote() {
		final CommonDialog editDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		editDialog.addView(R.layout.item_edittext_large);
		editDialog.setCanceledOnTouchOutside(true);
		editDialog.titleTextView.setText(getString(R.string.note));
		final EditText editText = (EditText) editDialog.getContentView().findViewById(R.id.edittext);
		editText.setText(mManageTool.getNote());
		editText.setHorizontallyScrolling(true);
		editDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String text = editText.getText().toString().trim();
				mManageTool.setNote(text);
				editDialog.dismiss();
			}
		});
		editDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				editDialog.dismiss();
			}
		});
		editDialog.show();
	}
	
	private void showRoundViewDialog() {
		RoundMenuDialog mRoundMenuDialog = new RoundMenuDialog(mContext);
		mRoundMenuDialog.setOnRoundMenuDialogListener(new OnRoundMenuDialogListener() {
			
			@Override
			public void onFinish(int dir) {
				if (dir == 0) return;
				mMainVision = mPlayerItemList.get(dir).getOriginalIndex();
				mPanelView.setCurPlayer(mMainVision);
				int orgIndex = mMainVision;
				int memberCount = mManageTool.getMemberCount();
				int[] ranks = mManageTool.analysisPlayerRanks(mManageTool.getPlayerScores());
				for (int i = 0; i < mPlayerItemList.size(); i++) {
					PlayerFuncItem item = mPlayerItemList.get(i);
					item.setPlayer(mManageTool.getPlayer(orgIndex), orgIndex, i);
					refreshPlayerFuncItem(item, orgIndex);
					item.setPlayerRank(ranks[orgIndex]);
					mRankImageViews[i].setImageResource(rankDrawables[ranks[orgIndex]]);
					// 根据人数隐藏控件
					if (orgIndex == 1 && memberCount < 3) {
						item.setVisibility(View.INVISIBLE);
						mRankImageViews[i].setVisibility(View.INVISIBLE);
					} else if (orgIndex == 3 && memberCount < 4) {
						item.setVisibility(View.INVISIBLE);
						mRankImageViews[i].setVisibility(View.INVISIBLE);
					} else {
						item.setVisibility(View.VISIBLE);
						mRankImageViews[i].setVisibility(View.VISIBLE);
					}
					// 递增索引
					orgIndex = (orgIndex + 1) % 4;
				}
			}
		});
		mRoundMenuDialog.resetDir();
		mRoundMenuDialog.show();
	}
	
	private OnManageToolListener mManageToolListener = new OnManageToolListener() {
		
		@Override
		public void onFinishByPlayerFly() {
			showGameEndDialog(getString(R.string.player_fly_game_end), true);
//			Intent intent = new Intent(BROADCAST_RESULT);
//			intent.putExtra(BROADCAST_RESULT, REQUEST_GAME_END);
//			intent.putExtra(BROADCAST_TEXT, getString(R.string.player_fly_game_end));
//			sendBroadcast(intent);
		}
		
		@Override
		public void onEnterSouthOrWest(boolean isNan) {
			
		}
		
		@Override
		public void onFinishAll() {
			showGameEndDialog(getString(R.string.game_end), true);
//			Intent intent = new Intent(BROADCAST_RESULT);
//			intent.putExtra(BROADCAST_RESULT, REQUEST_GAME_END);
//			intent.putExtra(BROADCAST_TEXT, getString(R.string.game_end));
//			sendBroadcast(intent);
		}
		
		@Override
		public void onResult(Player[] players, int[] scores, int[] ranks, float[] mas) {
			showRankDialog(players, scores, ranks, mas);
			switch (mManageTool.getMainType()) {
			case BaseManager.MainType_4p:
				ShareprefenceTool.getInstance().setBoolean(RankItem.IS_UPDATE, true, mContext);
				break;
			case BaseManager.MainType_3p:
				ShareprefenceTool.getInstance().setBoolean(RankItem.IS_UPDATE_3P, true, mContext);
				break;
			case BaseManager.MainType_17s:
				ShareprefenceTool.getInstance().setBoolean(RankItem.IS_UPDATE_17S, true, mContext);
				break;
			default:
				break;
			}
		}
		
	};
	
	private void showGameEndDialog(String content, final boolean canRevoke) {
		final CommonDialog endDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		endDialog.addView(R.layout.item_text);
		endDialog.titleTextView.setText(R.string.tip);
		endDialog.setCanceledOnTouchOutside(false);
		endDialog.setCancelable(false);
		View contentView = endDialog.getContentView();
		TextView text = (TextView) contentView.findViewById(R.id.item_text);
		text.setText(content);
		endDialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				endDialog.dismiss();
				mManageTool.doGameFinish();
			}
		});
		if (canRevoke) {
			endDialog.cancel.setText(R.string.revoke);
		} else {
			endDialog.cancel.setText(R.string.cancel);
		}		
		endDialog.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (canRevoke) {
					revokeLastRound(false);
				} 			
				endDialog.dismiss();
			}
		});
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				endDialog.show();
			}
		});		
	}
	
	private void showGameEndDialog() {
		final CommonDialog endDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		endDialog.addView(R.layout.layout_exit_game);
		endDialog.titleTextView.setText(R.string.move_up_game_end);
		endDialog.setCanceledOnTouchOutside(true);
		View contentView = endDialog.getContentView();
		TextView interruptView = (TextView) contentView.findViewById(R.id.exit_interrupt_save_temp);
		TextView endGameView = (TextView) contentView.findViewById(R.id.exit_end_game_save_db);
		TextView giveUpView = (TextView) contentView.findViewById(R.id.exit_exit_give_up_data);
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.cancel:
					endDialog.dismiss();					
					break;
				case R.id.exit_interrupt_save_temp:
					endDialog.dismiss();
					finish();
					break;
				case R.id.exit_end_game_save_db:
					endDialog.dismiss();
					mManageTool.doGameFinish();
					break;
				case R.id.exit_exit_give_up_data:
					mManageTool.clearStatesForTmp();
					endDialog.dismiss();
					finish();
					break;
				default:
					break;
				}
			}
		};
		interruptView.setOnClickListener(listener);
		endGameView.setOnClickListener(listener);
		giveUpView.setOnClickListener(listener);
		endDialog.ok.setOnClickListener(listener);
		endDialog.show();
	}	
	
	private void showRankDialog(Player[] players, int[] scores, int[] ranks, float[] mas) {
		final FinalRankDialog frDialog = new FinalRankDialog(mContext);
		AnalysisTool aTool = new AnalysisTool(mManageTool.getResult(), mManageTool.getAllDetails());
		frDialog.setData(players, scores, mas, ranks, aTool, mAudioTool, mManageTool.getMemberCount());
		frDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel() {
				finish();
			}
		});
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				frDialog.show();
			}
		});				
	}
	
	private class ResultBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int action = intent.getIntExtra(BROADCAST_RESULT, 0);
			switch (action) {
			case REQUEST_INVALIDATE:
				refreshAll();				
				break;
			case REQUEST_CODE_PLAYER_SETTING:
			case REQUEST_CODE_PLAYER_CHANGE:
				String playerId = intent.getStringExtra(Player.Col_Uuid);
				int index = mManageTool.findPlayerOrgIndexById(playerId);				
				mAudioTool.loadAsynByPlayer(playerId, index);
				break;
//			case REQUEST_GAME_END:
//				String text = intent.getStringExtra(BROADCAST_TEXT);
//				showGameEndDialog(text, true);
//				break;
			case REQUEST_ACTION_DONE:
				actionDone(intent);
				refreshAll();
				break;
			default:
				break;
			}
		}
		
	}
	
	private void refreshAll() {
		// 更新面板
		mPanelView.setPlayersLizhi(mManageTool.getPlayerLizhis(), mManageTool.getLizhiCount());
		mPanelView.setCounter(mManageTool.getFengCount(), mManageTool.getJuCount(), 
				mManageTool.getLizhiCount(), mManageTool.getRoundCount(), false);
		mPanelView.setPlayerScore(mManageTool.getPlayerScores());
		// 更新四个玩家
		int[] ranks = mManageTool.analysisPlayerRanks(mManageTool.getPlayerScores());
		for (int i = 0; i < mPlayerItemList.size(); i++) {
			PlayerFuncItem item = mPlayerItemList.get(i);
			refreshPlayerFuncItem(item, item.getOriginalIndex());
			int rank = ranks[item.getOriginalIndex()];
			item.setPlayerRank(rank);
			mRankImageViews[i].setImageResource(rankDrawables[rank]);
		}
		// 更新包牌
		if (mBaopaiDialog != null) {
			showBaopaiState(false);
			String baopaiText = getResources().getString(R.string.click_to_select);
			for (int i = 0; i < 4; i++) {
				mBaopaiBoxs[i].setChecked(false);
				mBaopaiPlayers[i].setText(baopaiText);
				mBaopaiIndexs[i] = -1;
			} 
		}	
	}
	
	private void refreshPlayerFuncItem(PlayerFuncItem item, int orgIndex) {
		item.setCurrentIndex(mManageTool.getPlayerWind(orgIndex));
		item.setLizhiState(mManageTool.getPlayerLizhi(orgIndex));
		item.setChickenState(mManageTool.getChickenState(orgIndex));
	}

	private void actionDone(Intent intent) {
		int actionType = intent.getIntExtra(ACTION_TYPE, -1);
		int[] changeScores = intent.getIntArrayExtra(CHANGE_SCORES);
		switch (actionType) {
		case MjAction.ACTION_ZIMO:			
			int zimoPlayer = intent.getIntExtra(MAIN_PLAYER, 0);
			mManageTool.setZimo(changeScores, zimoPlayer);
			break;
		case MjAction.ACTION_BOMB:			
			int bombPlayer = intent.getIntExtra(MAIN_PLAYER, 0);
			int[] bombIndexs = intent.getIntArrayExtra(BOMB_INDEXS);
			mManageTool.setBomb(changeScores, bombPlayer, bombIndexs);
			break;
		case MjAction.ACTION_HUANGPAILIUJU:	
			mManageTool.setHuangpailiuju(changeScores);
			break;
		case MjAction.ACTION_LIUJUMANGUAN:	
			boolean[] contactIds = intent.getBooleanArrayExtra(CONTACT_IDS);
			boolean isDealerTing = intent.getBooleanExtra(IS_DEALER_TING, false);
			mManageTool.setLiujumanguan(changeScores, contactIds, isDealerTing);
			break;
		case MjAction.ACTION_CHANGESCORE:
			mManageTool.setChangeScore(changeScores);
			break;
		default:
			break;
		}
	}
	
}

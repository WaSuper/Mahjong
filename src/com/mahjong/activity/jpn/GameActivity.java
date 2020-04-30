package com.mahjong.activity.jpn;

import java.util.List;

import com.mahjong.R;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;
import com.mahjong.dialog.DoraDialog;
import com.mahjong.dialog.DoraDialog.OnDoraDialogListener;
import com.mahjong.dialog.RoundMenuDialog;
import com.mahjong.dialog.RoundMenuDialog.OnRoundMenuDialogListener;
import com.mahjong.dialog.SpectrumDialog;
import com.mahjong.dialog.SpectrumDialog.OnSpectrumDialogListener;
import com.mahjong.tools.ManageTool;
import com.mahjong.tools.ManageTool.SpectrumData;
import com.mahjong.ui.CommonDialog;
import com.mahjong.ui.MahjongMainDora;
import com.mahjong.ui.MahjongMainDora.MahjongMainDoraListener;
import com.mahjong.ui.MahjongMainPanel;
import com.mahjong.ui.MahjongMainPanel.MahjongMainPanelListener;
import com.mahjong.ui.MahjongSpectrum;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * （弃用）早期界面
 * 
 * @author Summer Lee
 *
 */
public class GameActivity extends Activity 
	implements OnClickListener, OnLongClickListener {

	private Context mContext;
	
	private MahjongMainPanel mPanelView;
	private MahjongMainDora mDoraView;
	private MahjongSpectrum mSpectrumView;
	private Button mExitBtn;
	private Button mSettingBtn;
	private Button mHistoryBtn;
	private ImageButton mMoreBtn;
	private ImageButton mCalculateBtn;
	
	private ManageTool mManageTool = ManageTool.getInstance();
	private DoraDialog mDoraDialog;
	private SpectrumDialog mSpectrumDialog;
	private RoundMenuDialog mRoundMenuDialog;
	private CommonDialog mLiujuDialog;
	private Button[] mLiujuButtons = new Button[7];
	private CommonDialog mPlayerChooseDialog;
	private CheckBox[] mPlayerBoxs = new CheckBox[4];
	private LinearLayout mDealerLayout;
	private CheckBox mDealerBox;
	private int mCurLiujuType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jpn_game_complex);
		mContext = this;
		initUI();
	}
	
	private void initUI() {
		mExitBtn = (Button) findViewById(R.id.game_exit);
		mSettingBtn = (Button) findViewById(R.id.game_setting);
		mHistoryBtn = (Button) findViewById(R.id.game_history);
		mMoreBtn = (ImageButton) findViewById(R.id.game_more);
		mCalculateBtn = (ImageButton) findViewById(R.id.game_calculate_start);
		
		mExitBtn.setOnClickListener(this);
		mSettingBtn.setOnClickListener(this);
		mHistoryBtn.setOnClickListener(this);
		mMoreBtn.setOnClickListener(this);		
		mCalculateBtn.setOnClickListener(this);
		
		mPanelView = (MahjongMainPanel) findViewById(R.id.game_panel);
		mPanelView.setPlayers(mManageTool.getPlayer(0), mManageTool.getPlayer(1), 
				mManageTool.getPlayer(2), mManageTool.getPlayer(3));
		mPanelView.setOnMahjongMainPanelListener(mPanelListener);
		mPanelView.setOnLongClickListener(this);
		
		mDoraView = (MahjongMainDora) findViewById(R.id.game_dora);	
		mDoraView.setOnMahjongMainDoraListener(mDoraListener);
		mSpectrumView = (MahjongSpectrum) findViewById(R.id.game_spectrum);
		mSpectrumView.setOnClickListener(this);
	}
	
	
	private MahjongMainPanelListener mPanelListener = new MahjongMainPanelListener() {
		
		@Override
		public void onLiZhiState(int east, int south, int west, int north) {
			int sum = east + south + west + north;
			mDoraView.setDoarIn(sum != 0);
			mManageTool.setPlayerLizhi(east, south, west, north);
		}
		
		@Override
		public void onTingPaiState(boolean east, boolean south, boolean west, boolean north) {
			mManageTool.setPlayerTingPai(east, south, west, north);
		}
		
		@Override
		public void onClickSpectrum(int index) {
			SpectrumData sData = mManageTool.getSpectrumData(index);
			showSpectrumDialog(index, sData);
		}
	};
	 
	
	private MahjongMainDoraListener mDoraListener = new MahjongMainDoraListener() {
		
		@Override
		public void onClickRound(int round) {
			
		}
		
		@Override
		public void onClickLiZhi(int lizhi) {
			
		}
		
		@Override
		public void onClickDora(List<MjCard> doras, List<MjCard> doraIns) {
			if (mDoraDialog == null) {
				mDoraDialog = new DoraDialog(mContext);
				mDoraDialog.setOnDoraDialogListener(new OnDoraDialogListener() {
					
					@Override
					public void onFinish(List<MjCard> doras, List<MjCard> doraIns) {
						mDoraView.setDoraList(doras);
						mDoraView.setDoraInList(doraIns);
					}
				});
			}			
			mDoraDialog.setDoraIndicator(mDoraView.getDoraIn(), doras, doraIns);
			mDoraDialog.show();
		}
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.game_exit:
			
			break;
		case R.id.game_setting:
			
			break;
		case R.id.game_history:
			
			break;
		case R.id.game_more:
			showLiuJuDialog();
			break;
		case R.id.game_spectrum:
			showSpectrumDialog(mPanelView.getCurrentPlayer(), 
					mManageTool.getSpectrumData(mPanelView.getCurrentPlayer()));
		case R.id.game_calculate_start:
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onLongClick(View view) {
		switch (view.getId()) {
		case R.id.game_panel:
			showRoundViewDialog();
			return true;
		default:
			break;
		}
		return false;
	}
	
	private void showSpectrumDialog(int index, SpectrumData sData) {
		if (mSpectrumDialog == null) {
			mSpectrumDialog = new SpectrumDialog(mContext);
			mSpectrumDialog.setOnSpectrumDialogListener(new OnSpectrumDialogListener() {
				
				@Override
				public void onComplete(int index, List<MjCard> darkCards,
						List<MjCardPairs> brightCardPairs, MjCard winCard,
						boolean lizhi, boolean doublelizhi, boolean yifa, boolean zimo,
						boolean firstround, boolean finalpick, boolean qianggang, boolean lingshang) {
//					SpectrumData sData = mManageTool.getSpectrumData(index);
//					sData.setData(darkCards, brightCardPairs, winCard, 
//							lizhi, doublelizhi, yifa, zimo, 
//							firstround, finalpick, qianggang, lingshang);
//					mPanelView.setPlayerLiZhi(index, lizhi, doublelizhi);
//					mDoraView.setDoarIn(lizhi || doublelizhi);
//					if (index == mPanelView.getCurrentPlayer()) {
//						mSpectrumView.setData(sData.mDarkCards, sData.mBrightPairs, sData.mWinCard);
//					}					
				}
			});
		}
//		mSpectrumDialog.setData(index, sData.mDarkCards, 
//				sData.mBrightPairs, sData.mWinCard);
//		mSpectrumDialog.setEnvironment(sData.mLiZhi, sData.mYiFa, sData.mZiMo, 
//				sData.mFirstRound, sData.mFinalPick, sData.mQiangGang, sData.mLingShang);
		mSpectrumDialog.show();
	}
	
	private void showRoundViewDialog() {
		if (mRoundMenuDialog == null) {
			mRoundMenuDialog = new RoundMenuDialog(mContext);
			mRoundMenuDialog.setOnRoundMenuDialogListener(new OnRoundMenuDialogListener() {
				
				@Override
				public void onFinish(int dir) {
//					if (dir == 0) return;
//					int index = (mPanelView.getCurrentPlayer() + dir) % 4;
//					mPanelView.setCurrentPlayer(index);
//					SpectrumData sData = mManageTool.getSpectrumData(index);
//					mSpectrumView.setData(sData.mDarkCards, sData.mBrightPairs, sData.mWinCard);
				}
			});
		}
		mRoundMenuDialog.resetDir();
		mRoundMenuDialog.show();
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
		}
		mLiujuDialog.show();
	}
	
	private OnClickListener mLiujuListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.liuju_huangpai:
				showPlayerchooseDialog(R.id.liuju_huangpai, 
						getString(R.string.please_ensure_tingpai_player),
						mManageTool.getPlayerTingpai(0), mManageTool.getPlayerTingpai(1),
						mManageTool.getPlayerTingpai(2), mManageTool.getPlayerTingpai(3));
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_sifenglianda:
				showPlayerchooseDialog(R.id.liuju_sifenglianda, 
						getString(R.string.please_choose_wind),
						false, false, false, false);
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_sigangsanle:
				showPlayerchooseDialog(R.id.liuju_sigangsanle, 
						getString(R.string.please_choose_gang_player),
						false, false, false, false);
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_jiupaijiuzhong:
				showPlayerchooseDialog(R.id.liuju_jiupaijiuzhong, 
						getString(R.string.please_choose_jpjz_player),
						false, false, false, false);
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_sijializhi:
				showPlayerchooseDialog(R.id.liuju_sijializhi, 
						getString(R.string.please_ensure_lizhi_player),
						mManageTool.getPlayerIsLizhi(0), mManageTool.getPlayerIsLizhi(1),
						mManageTool.getPlayerIsLizhi(2), mManageTool.getPlayerIsLizhi(3));
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_sanjiaheliao:
				showPlayerchooseDialog(R.id.liuju_sanjiaheliao, 
						getString(R.string.please_choose_hele_player),
						false, false, false, false);
				mLiujuDialog.dismiss();
				break;
			case R.id.liuju_liujumanguan:
				showPlayerchooseDialog(R.id.liuju_liujumanguan, 
						getString(R.string.please_choose_manguan_player),
						false, false, false, false);
				mLiujuDialog.dismiss();
				break;
			default:
				break;
			}
		}
	};
	
	private void showPlayerchooseDialog(int id, String title, 
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
			mDealerLayout = (LinearLayout) contentView.findViewById(R.id.choose_ll_bottom);
			mDealerBox = (CheckBox) contentView.findViewById(R.id.choose_additon);
		}
		mCurLiujuType = id;
		if (id == R.id.liuju_sifenglianda) {
			mPlayerBoxs[0].setText(getString(R.string.east));
			mPlayerBoxs[1].setText(getString(R.string.south));
			mPlayerBoxs[2].setText(getString(R.string.west));
			mPlayerBoxs[3].setText(getString(R.string.north));
		} else {
			mPlayerBoxs[0].setText(mManageTool.getPlayer(0).getNickName());
			mPlayerBoxs[1].setText(mManageTool.getPlayer(1).getNickName());
			mPlayerBoxs[2].setText(mManageTool.getPlayer(2).getNickName());
			mPlayerBoxs[3].setText(mManageTool.getPlayer(3).getNickName());			
		}
		if (id == R.id.liuju_liujumanguan) {
			mDealerLayout.setVisibility(View.VISIBLE);
			mDealerBox.setChecked(mManageTool.getPlayerTingpai(mManageTool.getDealer()));
		} else {
			mDealerLayout.setVisibility(View.GONE);
		}		
		mPlayerChooseDialog.titleTextView.setText(title);
		mPlayerBoxs[0].setChecked(player0);
		mPlayerBoxs[1].setChecked(player1);
		mPlayerBoxs[2].setChecked(player2);
		mPlayerBoxs[3].setChecked(player3);
		mPlayerChooseDialog.show();
	}
	
	private boolean doLiujuAction() {
		switch (mCurLiujuType) {
		case R.id.liuju_huangpai:
			// 庄家听牌：连庄，本场+1；庄家无听牌，流庄，本场+1
			
			break;
		case R.id.liuju_sifenglianda:
			// 连庄，本场+1
			break;
		case R.id.liuju_sigangsanle:
			// 连庄，本场+1
			break;
		case R.id.liuju_jiupaijiuzhong:
			// 连庄，本场+1
			break;
		case R.id.liuju_sijializhi:
			// 连庄，本场+1
			break;
		case R.id.liuju_sanjiaheliao:
			// 判断规则是否允许，连庄，本场+1
			break;
		case R.id.liuju_liujumanguan:
			// 从庄家开始按顺序结算，若没人被飞，则庄家查听判断是否连庄
			break;
		default:
			break;
		}
		return true;
	}
}

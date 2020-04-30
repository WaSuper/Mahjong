package com.mahjong.activity.jpn;

import java.util.List;

import com.mahjong.R;
import com.mahjong.common.MjCalcTool;
import com.mahjong.common.MjCard;
import com.mahjong.common.MjCalcTool.GameResult;
import com.mahjong.data.jpn.Score;
import com.mahjong.data.jpn.YakuValue;
import com.mahjong.dialog.DoraDialog;
import com.mahjong.dialog.DoraDialog.OnDoraDialogListener;
import com.mahjong.fragment.ResultComplexFragment;
import com.mahjong.fragment.ResultComplexFragment.OnResultComplexListener;
import com.mahjong.model.MjAction;
import com.mahjong.tools.ManageTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.MahjongMainDora;
import com.mahjong.ui.MahjongMainDora.MahjongMainDoraListener;
import com.mahjong.ui.MahjongSpectrum;
import com.mahjong.ui.PlayerFuncItem;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultSetComplexActivity extends FragmentActivity 
		implements OnClickListener, OnResultComplexListener {	
	
	private Context mContext;
	
	private RelativeLayout mZimoLayout;
	private RelativeLayout mRongheLayout;
	private Button mOpppsiterBtn;
	private Button mUpperBtn;
	private Button mDownerBtn;
	private Button mOkBtn;
	private Button mCancelBtn;
	private LinearLayout mPointsLayout;
	private ImageView[] mPointViews;
	private MahjongMainDora mDoraView;
	private TextView mResultView;
	
	private ViewPager mSpectrumLayout;
	private ResultComplexFragment[] mResultFragment;
	private int mCurPage = 0;
	
	private DoraDialog mDoraDialog;
	
	private boolean isZimo; // 判断自摸布局
	private boolean isBomb; // 判断荣和或点炮
	private int mOrgPlayer; // 当前玩家初始索引
	private int mPosition = 0;	// 荣和视图下：点炮玩家位置：1：下家，2：对家，3：上家
	private boolean[] mPositions = {false, false, false}; // 点炮视图下：荣和玩家位置：0：上家，1：对家，2：下家
	private int mHuCount = 0; // 点炮视图下胡牌玩家数量
	private int mMainVision; // 主视觉
	
	private int mFanCount = 1; // 番数
	private int mFuCount = 0; // 符数
	
	private ManageTool mManageTool = ManageTool.getInstance();
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_jpn_result_set_complex);
		mResultFragment = null;
		initUI();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		// 设定方向
		Intent intent = getIntent();
		int pos = intent.getIntExtra(ManageTool.PLAYER_ITEM_POSITION, PlayerFuncItem.POS_BOTTOM);
		switch (pos) {
		case PlayerFuncItem.POS_LEFT:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case PlayerFuncItem.POS_RIGHT:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			break;
		case PlayerFuncItem.POS_TOP:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
			break;
		case PlayerFuncItem.POS_BOTTOM:
		default:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}
		setContentView(R.layout.activity_jpn_result_set_complex);
		mContext = this;
		// 设定内容
		isZimo = intent.getBooleanExtra(ManageTool.PLAYER_IS_ZIMO, false);
		isBomb = intent.getBooleanExtra(ManageTool.PLAYER_IS_BOMB, false);
		mOrgPlayer = intent.getIntExtra(ManageTool.PLAYER_ORIGINAL_INDEX, 0);
		mMainVision = intent.getIntExtra(GameSimpleActivity.MAIN_VISION, 0);
		initUI();
	}
	
	@SuppressWarnings("deprecation")
	private void initUI() {
		mZimoLayout = (RelativeLayout) findViewById(R.id.result_set_complex_top_zimo);
		mRongheLayout = (RelativeLayout) findViewById(R.id.result_set_complex_top_ronghe);
		mOpppsiterBtn = (Button) findViewById(R.id.result_set_complex_oppositer);
		mUpperBtn = (Button) findViewById(R.id.result_set_complex_upper);
		mDownerBtn = (Button) findViewById(R.id.result_set_complex_downer);
		mOkBtn = (Button) findViewById(R.id.result_set_complex_ok);
		mCancelBtn = (Button) findViewById(R.id.result_set_complex_cancel);
		mPointsLayout = (LinearLayout) findViewById(R.id.result_set_complex_ll_points);
		mPointViews = new ImageView[3];
		mPointViews[0] = (ImageView) findViewById(R.id.result_set_complex_point1);
		mPointViews[1] = (ImageView) findViewById(R.id.result_set_complex_point2);
		mPointViews[2] = (ImageView) findViewById(R.id.result_set_complex_point3);
		mDoraView = (MahjongMainDora) findViewById(R.id.result_set_complex_dora);
		mResultView = (TextView) findViewById(R.id.result_set_complex_tv_result);
		
		if (isZimo) {
			mZimoLayout.setVisibility(View.VISIBLE);
			mRongheLayout.setVisibility(View.GONE);
			mPointsLayout.setVisibility(View.INVISIBLE);
			mResultFragment = new ResultComplexFragment[1];
			mResultFragment[0] = new ResultComplexFragment();	
			mResultFragment[0].setPlayerIndex(0, mOrgPlayer, isZimo, mOrgPlayer);
		} else {
			mZimoLayout.setVisibility(View.GONE);
			mRongheLayout.setVisibility(View.VISIBLE);		
			if (isBomb) {
				mPointsLayout.setVisibility(View.VISIBLE);	
				mResultFragment = new ResultComplexFragment[3];
				mResultFragment[0] = new ResultComplexFragment();
				mResultFragment[0].setPlayerIndex(0, (mOrgPlayer + 3) % 4, isZimo, mOrgPlayer);
				mResultFragment[1] = new ResultComplexFragment();
				mResultFragment[1].setPlayerIndex(1, (mOrgPlayer + 2) % 4, isZimo, mOrgPlayer);
				mResultFragment[2] = new ResultComplexFragment();	
				mResultFragment[2].setPlayerIndex(2, (mOrgPlayer + 1) % 4, isZimo, mOrgPlayer);
				for (int i = 0; i < mPositions.length; i++) {
					setRonghePlayer(i, false);
				}				
			} else {
				mPointsLayout.setVisibility(View.INVISIBLE);	
				mResultFragment = new ResultComplexFragment[1];
				mResultFragment[0] = new ResultComplexFragment();	
				mResultFragment[0].setPlayerIndex(0, mOrgPlayer, isZimo, (mOrgPlayer + 2) % 4);	
				setDianPaoPlayer(mPosition);
			}
		}
		for (int i = 0; i < mResultFragment.length; i++) {
			mResultFragment[i].setOnResultComplexListener(this);
		}
		
		mSpectrumLayout = (ViewPager) findViewById(R.id.result_set_complex_viewpager);
		mSpectrumLayout.setOffscreenPageLimit(3);
		mSpectrumLayout.setAdapter(new SpectrumAdapter(getSupportFragmentManager(), mResultFragment));
		mSpectrumLayout.setOnPageChangeListener(new SpectrumListener());
		mSpectrumLayout.setCurrentItem(0);
		
		mOpppsiterBtn.setOnClickListener(this);
		mUpperBtn.setOnClickListener(this);
		mDownerBtn.setOnClickListener(this);
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		mDoraView.setOnClickListener(this);
		mDoraView.setLizhiAndRound(mManageTool.getLizhiCount(), 
				mManageTool.getRoundCount());
		if (mManageTool.checkHasLizhi()) {
			mDoraView.setDoarIn(true);
		}
		mDoraView.setOnMahjongMainDoraListener(new MahjongMainDoraListener() {
			
			@Override
			public void onClickRound(int round) {
				
			}
			
			@Override
			public void onClickLiZhi(int lizhi) {
				
			}
			
			@Override
			public void onClickDora(List<MjCard> doras, List<MjCard> doraIns) {
				showDoraDialog(doras, doraIns);
			}
		});
	}
	
	@Override
	public void onResult(GameResult gameResult, int page, int index) {
		setResultText1(gameResult);
	}
	
	@Override
	public void onEnvironmentChange(GameResult gameResult, int page, int index) {
		setResultText2(gameResult);
	}
	
	private void setResultText1(GameResult gameResult) {
		MahjongSpectrum spectrum = gameResult.spectrum;
		if (spectrum.checkIsComplete()) {
			boolean canHupai = MjCalcTool.checkCanHupai(gameResult, 
					spectrum.getDarkNums(), spectrum.getBrightNums(), spectrum.getWinNum(), 
					mDoraView.getDoraList(), mDoraView.getDoraInList());
			if (canHupai) {
				setResultText2(gameResult);
			} else {
				mResultView.setText(R.string.result_none);
			}
		} else {
			mResultView.setText(R.string.result_none);
		}
		
	}
	
	private void setResultText2(GameResult gameResult) {
		if (gameResult.canHupai) {
			boolean hasFan = MjCalcTool.calcToScore(gameResult);
			if (hasFan) {
				Score score = gameResult.score;
				StringBuffer sBuffer = new StringBuffer();
				sBuffer.append(score.toString());
				sBuffer.append("\n番种：");
				for (YakuValue value : score.YakuValues()) {
					sBuffer.append("\n\t\t" + value.toString());
				}
				mResultView.setText(sBuffer.toString());
			} else {
				mResultView.setText(R.string.result_none);
			}
		} else {
			mResultView.setText(R.string.result_none);
		}		
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.result_set_complex_oppositer:
			if (isBomb) {
				setRonghePlayer(1, true);
			} else {
				setDianPaoPlayer(2);
			}
			break;
		case R.id.result_set_complex_upper:
			if (isBomb) {
				setRonghePlayer(0, true);
			} else {
				setDianPaoPlayer(3);
			}
			break;
		case R.id.result_set_complex_downer:
			if (isBomb) {
				setRonghePlayer(2, true);
			} else {
				setDianPaoPlayer(1);
			}
			break;
		case R.id.result_set_complex_ok:
			if (!isZimo) {
				if (isBomb) {
					mHuCount = 0;
					for (int i = 0; i < mPositions.length; i++) {
						if (mPositions[i]) mHuCount++;						
					}
					if (mHuCount == 0) {
						ToastTool.showToast(mContext, R.string.player_ronghe_no_choose);
						return;
					}
				} else {
					if (mPosition == 0) {
						ToastTool.showToast(mContext, R.string.player_bomb_no_choose);
						return;
					}
				}				
			}
			boolean isEnableFanfu = mManageTool.getEnableFanFu();
			int roundCount = mManageTool.getRoundCount();
			if (isEnableFanfu && roundCount >= 5) {
				if (checkFanfu(roundCount)) {
					sendData();
				} 
			} else {
				sendData();
			}
			break;
		case R.id.result_set_complex_cancel:
			this.finish();
			break;		
		default:
			break;
		}
	}
	
	private boolean checkFanfu(int roundCount) {
		if (!isBomb) {
			GameResult gameResult = mResultFragment[0].getGameResult();
			if (gameResult.canHupai && gameResult.score.hasYaku()) {
				boolean result = gameResult.score.checkFanfu(roundCount);
				if (!result) {
					showFanfuError(gameResult.score, roundCount);
				}
				return result;
			}
		} else {
			for (int i = 0; i < mPositions.length; i++) {
				if (mPositions[i]) {
					GameResult gameResult = mResultFragment[i].getGameResult();
					if (gameResult.canHupai && gameResult.score.hasYaku()) {
						boolean result = gameResult.score.checkFanfu(roundCount);
						if (!result) {
							showFanfuError(gameResult.score, roundCount);
							mSpectrumLayout.setCurrentItem(i, true);
							return false;
						}
					}
				}
			}
			return true;
		}		
		return false;
	}
	
	private void showFanfuError(Score score, int roundCount) {
		ToastTool.showToast(mContext, getString(R.string.fanfu_invalid, 
				score.getFanfu(roundCount), score.getFanfuValidYaku()));
	}
	
	private void sendData() {
		Intent data = new Intent(ResultSetComplexActivity.this, ResultShow.class);
		data.putExtra(GameSimpleActivity.MAIN_VISION, mMainVision);
		if (isBomb) {
			data.putExtra(MjAction.Name, MjAction.ACTION_BOMB);
			data.putExtra(ManageTool.PLAYER_ORIGINAL_INDEX, mOrgPlayer);
			int[] bombIndexs = new int[mHuCount];
			int index = 0;
			for (int i = 0; i < mPositions.length; i++) {
				if (mPositions[i]) {
					GameResult result = mResultFragment[i].getGameResult();
					if (!result.hasYaku) {
						ToastTool.showToast(mContext, R.string.no_yaku);
						return;
					}
					int fan = result.score.AllFanValue();
					if (result.score.FullYaku() > 0) fan = -result.score.FullYaku();
					int fu = result.score.Fu();
					String spectrum = result.spectrum.toString();
					int env = result.env;
					bombIndexs[index] = (mOrgPlayer + 3 - i) % 4;	
					mManageTool.setResult(bombIndexs[index], 
							fan, fu, spectrum, env);
					index++;
				}
			}
			data.putExtra(ManageTool.RESULT_BOMB_INDEX, bombIndexs);
		} else {
			GameResult result = mResultFragment[0].getGameResult();
			if (!result.hasYaku) {
				ToastTool.showToast(mContext, R.string.no_yaku);
				return;
			}
			mManageTool.setResult(mOrgPlayer, mFanCount, mFuCount);
			if (isZimo) {
				data.putExtra(MjAction.Name, MjAction.ACTION_ZIMO);
				data.putExtra(ManageTool.PLAYER_ORIGINAL_INDEX, mOrgPlayer);
			} else {
				data.putExtra(MjAction.Name, MjAction.ACTION_BOMB);
				data.putExtra(ManageTool.PLAYER_ORIGINAL_INDEX, (mOrgPlayer + mPosition) % 4);
				data.putExtra(ManageTool.RESULT_BOMB_INDEX, new int[] {mOrgPlayer});
			}
			int fan = result.score.AllFanValue();
			if (result.score.FullYaku() > 0) fan = -result.score.FullYaku();
			int fu = result.score.Fu();
			String spectrum = result.spectrum.toString();
			int env = result.env;
			mManageTool.setResult(mOrgPlayer, fan, fu, spectrum, env);
		}		
		startActivity(data);
		this.finish();		
	}
	
	private void showDoraDialog(List<MjCard> doras, List<MjCard> doraIns) {
		if (mDoraDialog == null) {
			mDoraDialog = new DoraDialog(mContext);
			Display display = getWindowManager().getDefaultDisplay();
			LayoutParams params = mDoraDialog.getWindow().getAttributes();
			params.width = display.getWidth();
			mDoraDialog.getWindow().setAttributes(params);
			mDoraDialog.setOnDoraDialogListener(new OnDoraDialogListener() {
				
				@Override
				public void onFinish(List<MjCard> doras, List<MjCard> doraIns) {
					mDoraView.setDoraList(doras);
					mDoraView.setDoraInList(doraIns);
					GameResult gameResult = mResultFragment[mCurPage].getGameResult();
					gameResult.reCalcDora(mDoraView.getDoraList(), mDoraView.getDoraInList());
					setResultText2(gameResult);
				}
			});
		}
		mDoraDialog.setDoraIndicator(mDoraView.getDoraIn(), doras, doraIns);
		mDoraDialog.show();
	}
	
	private void setDianPaoPlayer(int pos) {
		mPosition = pos;
		switch (mPosition) {
		case 1: // 下家
			mDownerBtn.setBackgroundResource(R.drawable.ic_btn_player_dir);
			mOpppsiterBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			mUpperBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			break;
		case 2: // 对家
			mOpppsiterBtn.setBackgroundResource(R.drawable.ic_btn_player_dir);
			mDownerBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			mUpperBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			break;
		case 3: // 上家
			mUpperBtn.setBackgroundResource(R.drawable.ic_btn_player_dir);
			mOpppsiterBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			mDownerBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			break;
		default:
			break;
		}
	}
	
	private void setRonghePlayer(int pos, boolean isClick) {
		if (isClick) mPositions[pos] = !mPositions[pos];
		switch (pos) {
		case 0: // 上家
			if (mPositions[0]) {
				mUpperBtn.setBackgroundResource(R.drawable.ic_btn_player_dir);
			} else {
				mUpperBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			}
			break;
		case 1: // 对家
			if (mPositions[1]) {
				mOpppsiterBtn.setBackgroundResource(R.drawable.ic_btn_player_dir);
			} else {
				mOpppsiterBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			}
			break;
		case 2: // 下家
			if (mPositions[2]) {
				mDownerBtn.setBackgroundResource(R.drawable.ic_btn_player_dir);
			} else {
				mDownerBtn.setBackgroundResource(R.drawable.selector_btn_player_dir);
			}
			break;
		default:
			break;
		}
	}
	
	class SpectrumAdapter extends FragmentStatePagerAdapter {

		Fragment[] mList;

		public SpectrumAdapter(FragmentManager fm) {
			super(fm);
		}

		public SpectrumAdapter(FragmentManager fm, Fragment[] list) {
			super(fm);
			this.mList = list;
		}

		@Override
		public Fragment getItem(int pos) {
			return mList != null && mList.length > pos ? mList[pos] : null;
		}

		@Override
		public int getCount() {
			return mList != null ? mList.length : 0;
		}
		
		@Override  
		public int getItemPosition(Object object) {  
			return POSITION_NONE;  
		}
	}

	class SpectrumListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int state) {
			switch (state) {
			case ViewPager.SCROLL_STATE_IDLE: // 空闲状态
				break;

			case ViewPager.SCROLL_STATE_DRAGGING: // 正在滑动
				break;

			case ViewPager.SCROLL_STATE_SETTLING: // 自然沉降
				break;

			}
		}

		public void onPageScrolled(int pos, float percent, int arg2) {

		}

		public void onPageSelected(int pos) {
			mCurPage = pos;
			switch (mCurPage) {
			case 0:
				mPointViews[0].setImageResource(R.drawable.ic_indicator_sel);
				mPointViews[1].setImageResource(R.drawable.ic_indicator_nor);
				mPointViews[2].setImageResource(R.drawable.ic_indicator_nor);
				break;
			case 1:
				mPointViews[0].setImageResource(R.drawable.ic_indicator_nor);
				mPointViews[1].setImageResource(R.drawable.ic_indicator_sel);
				mPointViews[2].setImageResource(R.drawable.ic_indicator_nor);				
				break;
			case 2:
				mPointViews[0].setImageResource(R.drawable.ic_indicator_nor);
				mPointViews[1].setImageResource(R.drawable.ic_indicator_nor);
				mPointViews[2].setImageResource(R.drawable.ic_indicator_sel);					
				break;
			default:
				break;
			}
			setResultText2(mResultFragment[mCurPage].getGameResult());
		}

	}
	
}

package com.mahjong.dialog;

import java.util.Arrays;
import java.util.List;

import com.mahjong.R;
import com.mahjong.adapter.PlayerSimpleAdapter;
import com.mahjong.model.Character;
import com.mahjong.model.Player;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.GetSize;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.Rotatable;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CardExtractDialog extends Dialog implements OnClickListener {

	private Context mContext;
	private Handler mHandler;
	
	private RelativeLayout[] mLayout;
	private ImageView[] mBackView;
	private ImageView[] mFrontView;
	private ImageView[] mClickView;
	private TextView[] mNameViews;
	private ImageView mBackBtn;
	private ImageView mRefreshBtn;
	private ImageView mOkBtn;
	
	private RelativeLayout mPlayerDialog;
	private LinearLayout dContentLayout;
	public TextView dOk, dTitleTextView;
	private ListView dListView;
	private PlayerSimpleAdapter mAdapter;
	
	private int[] cardImgs = {R.drawable.card_front_east, R.drawable.card_front_south, 
			R.drawable.card_front_west, R.drawable.card_front_north};
	private int[] cardPos = {0, 1, 2, 3};
	private List<Player> playerList;	
	private Player[] mPlayers = new Player[4];
	private int mIndex;
	
	private CardExtractListener mListener;
	
	public CardExtractDialog(Context context) {
		super(context, R.style.MyDialogStyle);
		setContentView(R.layout.dialog_card_extract);
		this.mContext = context;
		this.mHandler = new Handler();
		initUI();
	}

	private void initUI() {
		mLayout = new RelativeLayout[4];
		mLayout[0] = (RelativeLayout) findViewById(R.id.card_rl_root1);
		mLayout[1] = (RelativeLayout) findViewById(R.id.card_rl_root2);
		mLayout[2] = (RelativeLayout) findViewById(R.id.card_rl_root3);
		mLayout[3] = (RelativeLayout) findViewById(R.id.card_rl_root4);
		mBackView = new ImageView[4];
		mBackView[0] = (ImageView) findViewById(R.id.card_img_back1);
		mBackView[1] = (ImageView) findViewById(R.id.card_img_back2);
		mBackView[2] = (ImageView) findViewById(R.id.card_img_back3);
		mBackView[3] = (ImageView) findViewById(R.id.card_img_back4);
		mFrontView = new ImageView[4];
		mFrontView[0] = (ImageView) findViewById(R.id.card_img_front1);
		mFrontView[1] = (ImageView) findViewById(R.id.card_img_front2);
		mFrontView[2] = (ImageView) findViewById(R.id.card_img_front3);
		mFrontView[3] = (ImageView) findViewById(R.id.card_img_front4);
		mClickView = new ImageView[4];
		mClickView[0] = (ImageView) findViewById(R.id.card_click1);
		mClickView[1] = (ImageView) findViewById(R.id.card_click2);
		mClickView[2] = (ImageView) findViewById(R.id.card_click3);
		mClickView[3] = (ImageView) findViewById(R.id.card_click4);
		mNameViews = new TextView[4];
		mNameViews[0] = (TextView) findViewById(R.id.card_player_name1);
		mNameViews[1] = (TextView) findViewById(R.id.card_player_name2);
		mNameViews[2] = (TextView) findViewById(R.id.card_player_name3);
		mNameViews[3] = (TextView) findViewById(R.id.card_player_name4);
		mBackBtn = (ImageView) findViewById(R.id.card_back);
		mRefreshBtn = (ImageView) findViewById(R.id.card_refresh);
		mOkBtn = (ImageView) findViewById(R.id.card_ok);
		
		mPlayerDialog = (RelativeLayout) findViewById(R.id.card_player_dialog);
		dContentLayout = (LinearLayout) findViewById(R.id.contentlayout);
		dTitleTextView = (TextView) findViewById(R.id.title);
		dOk = (TextView) findViewById(R.id.l_ok);
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		dListView = (ListView) layoutInflater.inflate(R.layout.listview, null);
		((ViewGroup) dContentLayout).addView(dListView);
		mAdapter = new PlayerSimpleAdapter(mContext);
		dListView.setAdapter(mAdapter);
		dListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Player player = playerList.get(position);
				setPlayer(player, mIndex, true);
				mPlayerDialog.setVisibility(View.INVISIBLE);
			}
		});
		
		for (int i = 0; i < 4; i++) {
			mBackView[i].setOnClickListener(this);
			mFrontView[i].setOnClickListener(this);
			mClickView[i].setOnClickListener(this);
			setCameraDistance(mLayout[i]);
		}
		mBackBtn.setOnClickListener(this);
		mRefreshBtn.setOnClickListener(this);
		mOkBtn.setOnClickListener(this);
		
		dTitleTextView.setText(mContext.getString(R.string.please_choose_player));
		dOk.setOnClickListener(this);
		
		randomCards();
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.card_img_back1:
			cardTurnover(mBackView[0], mFrontView[0], mLayout[0]);
			mHandler.postDelayed(new ClickShowRunnable(0), 1200);
			break;
		case R.id.card_img_back2:
			cardTurnover(mBackView[1], mFrontView[1], mLayout[1]);	
			mHandler.postDelayed(new ClickShowRunnable(1), 1200);		
			break;
		case R.id.card_img_back3:
			cardTurnover(mBackView[2], mFrontView[2], mLayout[2]);
			mHandler.postDelayed(new ClickShowRunnable(2), 1200);
			break;
		case R.id.card_img_back4:
			cardTurnover(mBackView[3], mFrontView[3], mLayout[3]);
			mHandler.postDelayed(new ClickShowRunnable(3), 1200);
			break;
		case R.id.card_img_front1:
		case R.id.card_click1:
			showPlayerDialog(0);
			break;
		case R.id.card_img_front2:
		case R.id.card_click2:
			showPlayerDialog(1);
			break;
		case R.id.card_img_front3:
		case R.id.card_click3:
			showPlayerDialog(2);
			break;
		case R.id.card_img_front4:
		case R.id.card_click4:
			showPlayerDialog(3);
			break;
		case R.id.card_back:
			dismiss();
			break;
		case R.id.card_ok:
			dismiss();
			if (mListener != null) mListener.onFinish(mPlayers, cardPos);
			break;
		case R.id.card_refresh:
			turnBack();
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					randomCards();
				}
			}, 1500);
			break;
		case R.id.l_ok:
			mPlayerDialog.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
	}
	
	private class ClickShowRunnable implements Runnable {
		
		private int index;
		
		public ClickShowRunnable(int index) {
			this.index = index;
		}
		
		@Override
		public void run() {
			mClickView[index].setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 乱序卡牌
	 */
	private void randomCards() {
		boolean[] used = new boolean[4];
		Arrays.fill(used, false);
		int index;
		for (int i = 0; i < 4; i++) {
			do {
				index = (int)(Math.random() * 4);
			} while (used[index]);
			cardPos[i] = index;		
			used[index] = true;
		}
		for (int i = 0; i < 4; i++) {
			mFrontView[i].setBackgroundResource(cardImgs[cardPos[i]]);
		}
	}
	
	/**
	 * 返回背面
	 */
	public void turnBack() {
		for (int i = 0; i < 4; i++) {
			if (mFrontView[i].getVisibility() == View.VISIBLE) {
				cardTurnover(mBackView[i], mFrontView[i], mLayout[i]);
			}
			if (mClickView[i].getVisibility() == View.VISIBLE) {
				mClickView[i].setVisibility(View.GONE);
				mClickView[i].setImageResource(R.drawable.selector_action_click);
			}
			mNameViews[i].setText("");
			mPlayers[i] = null;
		}
	}
	
	 /**
     * 翻牌
     */
    public void cardTurnover(ImageView backView, ImageView frontView,
    		RelativeLayout rootView) {
        if (View.VISIBLE == backView.getVisibility()) {
        	frontView.setRotationY(180f); //先翻转180，转回来时就不是反转的了
            Rotatable rotatable = new Rotatable.Builder(rootView)
                    .sides(backView.getId(), frontView.getId())
                    .direction(Rotatable.ROTATE_Y)
                    .rotationCount(1)
                    .build();
            rotatable.setTouchEnable(false);
            rotatable.rotate(Rotatable.ROTATE_Y, -180, 1200);
        } else if (View.VISIBLE == frontView.getVisibility()) {
            Rotatable rotatable = new Rotatable.Builder(rootView)
                    .sides(backView.getId(), frontView.getId())
                    .direction(Rotatable.ROTATE_Y)
                    .rotationCount(1)
                    .build();
            rotatable.setTouchEnable(false);
            rotatable.rotate(Rotatable.ROTATE_Y, 0, 1200);
        }
    }

    /**
     * 改变视角距离, 贴近屏幕
     */
    private void setCameraDistance(RelativeLayout rootView) {
        int distance = 10000;
        float scale = mContext.getResources().getDisplayMetrics().density * distance;
        rootView.setCameraDistance(scale);
    }
    
    /**
     * 显示玩家选择框
     */
    private void showPlayerDialog(int index) {
    	mIndex = index;
    	mPlayerDialog.setVisibility(View.VISIBLE);
    	playerList = getPlayersExceptUesd();
    	mAdapter.setData(playerList);
		final ViewTreeObserver vto = dContentLayout.getViewTreeObserver();
		vto.addOnPreDrawListener(new OnPreDrawListener() {

			public boolean onPreDraw() {
				dContentLayout.getViewTreeObserver().removeOnPreDrawListener(this);
				mHandler.post(new Runnable() {

					public void run() {
						LayoutParams layoutParams = dContentLayout.getLayoutParams();
						if (playerList != null && playerList.size() > 5) {
							layoutParams.height = GetSize.dip2px(mContext, 170);
						} else {
							layoutParams.height = LayoutParams.WRAP_CONTENT;
						}
						dContentLayout.setLayoutParams(layoutParams);
					}
				});
				return true;
			}
		});
    }
    
    private List<Player> getPlayersExceptUesd() {
		String[] ids = new String[4];
		for (int i = 0; i < mPlayers.length; i++) {
			Player player = mPlayers[i];
			if (player != null) {
				ids[i] = player.getUuid();
			}
		}
		List<Player> list = PlayerSimpleAdapter.getPlayersExceptUesd(ids);
		return list;
	}
    
    private void setPlayer(Player player, int dir, boolean isSave) {
		boolean isReset = (player == null);
//		int icon = R.drawable.player_nor;
//		if (!isReset) {
//			icon = HeadIconTool.String2Id(player.getIcon());
//			if (icon == -1) icon = R.drawable.head_none;
//		}		
		Bitmap bitmap;
		if (!isReset) {
			bitmap = EmoticonTool.getEmoticon(Character.getCharacter(mContext, player.getCharacterId()));
			bitmap = ImageTool.createCircleImage(bitmap, bitmap.getWidth());
		} else {
			bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.player_nor);
		}
		String nickname = isReset ? "" : player.getNickName();
		mPlayers[dir] = player;
		mClickView[dir].setImageBitmap(bitmap);
		mNameViews[dir].setText(nickname);
	}
    
    public void setOnCardExtractListener(CardExtractListener listener) {
    	this.mListener = listener;
    }
    
    public interface CardExtractListener {
    	public void onFinish(Player[] players, int[] dirs);
    }
    
}

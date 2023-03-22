package com.mahjong.fragment;

import java.util.List;
import java.util.Random;

import com.mahjong.R;
import com.mahjong.activity.CreatePlayerActivity;
import com.mahjong.adapter.PlayerCheckAdapter;
import com.mahjong.adapter.PlayerSimpleAdapter;
import com.mahjong.control.ManagerTool;
import com.mahjong.dialog.CardExtractDialog;
import com.mahjong.dialog.CardExtractDialog.CardExtractListener;
import com.mahjong.model.Character;
import com.mahjong.model.Player;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.ShareprefenceTool;
import com.mahjong.tools.ToastTool;
import com.mahjong.ui.CommonDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public abstract class BaseMemberFragment extends Fragment 
			implements OnClickListener, OnLongClickListener {

	enum Direction {
		east, south, west, north
	}
	
	protected View mView;
	protected Context mContext;

	protected ImageView mEastView;
	protected ImageView mSouthView;
	protected ImageView mWestView;
	protected ImageView mNorthView;

	protected TextView mEastText;
	protected TextView mSouthText;
	protected TextView mWestText;
	protected TextView mNorthText;

	protected Player mEastPlayer;
	protected Player mSouthPlayer;
	protected Player mWestPlayer;
	protected Player mNorthPlayer;
	
	protected ImageView mCardBtn;
	protected ImageView mRandomBtn;
	protected ImageView mResetBtn;
	
	protected CardExtractDialog mCardDialog;
	protected Random random = new Random(System.currentTimeMillis());
	
	protected int mMemberCount;
	protected String KEY_MEMBER_COUNT;
	protected String KEY_EAST;
	protected String KEY_SOUTH;
	protected String KEY_WEST;
	protected String KEY_NORTH;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_member, container, false);
		initUI();
		initData(false);
		return mView;
	}

	/**
	 * 进入游戏前调用
	 */
	public void initGameStart() {
		ManagerTool.getInstance().getManager().setPlayers(mMemberCount, 
				mEastPlayer, mSouthPlayer, mWestPlayer, mNorthPlayer);
	}

	private void initUI() {
		mEastView = (ImageView) mView.findViewById(R.id.member_img_east);
		mSouthView = (ImageView) mView.findViewById(R.id.member_img_south);
		mWestView = (ImageView) mView.findViewById(R.id.member_img_west);
		mNorthView = (ImageView) mView.findViewById(R.id.member_img_north);
		
		mEastText = (TextView) mView.findViewById(R.id.member_tv_east);
		mSouthText = (TextView) mView.findViewById(R.id.member_tv_south);
		mWestText = (TextView) mView.findViewById(R.id.member_tv_west);
		mNorthText = (TextView) mView.findViewById(R.id.member_tv_north);
		
		mCardBtn = (ImageView) mView.findViewById(R.id.member_btn_card);
		mRandomBtn = (ImageView) mView.findViewById(R.id.member_btn_random);
		mResetBtn = (ImageView) mView.findViewById(R.id.member_btn_reset);
		
		mEastView.setOnClickListener(this);
		mSouthView.setOnClickListener(this);
		mWestView.setOnClickListener(this);
		mNorthView.setOnClickListener(this);
		
		mEastView.setOnLongClickListener(this);
		mSouthView.setOnLongClickListener(this);
		mWestView.setOnLongClickListener(this);
		mNorthView.setOnLongClickListener(this);
		
		mCardBtn.setOnClickListener(this);
		mRandomBtn.setOnClickListener(this);
		mResetBtn.setOnClickListener(this);
	}
	
	public void initData(boolean isReload) {
		if (isReload) {
			mMemberCount = ShareprefenceTool.getInstance()
					.getInt(KEY_MEMBER_COUNT, mContext, mMemberCount);
		}
		List<Player> list = Player.getAllPlayer();
		String eastId = ShareprefenceTool.getInstance()
				.getString(KEY_EAST, mContext, null);
		if (eastId != null) {
			for (Player player : list) {
				if (player.getUuid().equals(eastId)) {
					setPlayer(player, Direction.east, false);
					break;
				}
			}
		}
		if (mMemberCount > 2) {
			mSouthView.setVisibility(View.VISIBLE);
			mSouthText.setVisibility(View.VISIBLE);
			String southId = ShareprefenceTool.getInstance()
					.getString(KEY_SOUTH, mContext, null);
			if (southId != null) {
				for (Player player : list) {
					if (player.getUuid().equals(southId)) {
						setPlayer(player, Direction.south, false);
						break;
					}
				}
			}
		} else {
			mSouthView.setVisibility(View.INVISIBLE);
			mSouthText.setVisibility(View.INVISIBLE);
			setPlayer(null, Direction.south, false);
		}
		String westId = ShareprefenceTool.getInstance()
				.getString(KEY_WEST, mContext, null);
		if (westId != null) {
			for (Player player : list) {
				if (player.getUuid().equals(westId)) {
					setPlayer(player, Direction.west, false);
					break;
				}
			}
		}
		if (mMemberCount > 3) {
			mNorthView.setVisibility(View.VISIBLE);
			mNorthText.setVisibility(View.VISIBLE);
			String northId = ShareprefenceTool.getInstance()
					.getString(KEY_NORTH, mContext, null);
			if (northId != null) {
				for (Player player : list) {
					if (player.getUuid().equals(northId)) {
						setPlayer(player, Direction.north, false);
						break;
					}
				}
			}
		} else {
			mNorthView.setVisibility(View.INVISIBLE);
			mNorthText.setVisibility(View.INVISIBLE);
			setPlayer(null, Direction.north, false);
		}
	}
	

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.member_img_east:
			showChoosePlayerDialog(Direction.east);
			break;
		case R.id.member_img_south:
			showChoosePlayerDialog(Direction.south);
			break;
		case R.id.member_img_west:
			showChoosePlayerDialog(Direction.west);
			break;
		case R.id.member_img_north:
			showChoosePlayerDialog(Direction.north);
			break;
		case R.id.member_btn_card:
			showCardDialog();
			break;
		case R.id.member_btn_random:
			showRandomDialog();
			break;
		case R.id.member_btn_reset:
			setPlayer(null, Direction.east, true);
			setPlayer(null, Direction.south, true);
			setPlayer(null, Direction.west, true);
			setPlayer(null, Direction.north, true);
		default:
			break;
		}
	}

	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.member_img_east:
			if (mEastPlayer == null) return false;
			showCancelPlayerDialog(Direction.east);
			break;
		case R.id.member_img_south:
			if (mSouthPlayer == null) return false;
			showCancelPlayerDialog(Direction.south);
			break;
		case R.id.member_img_west:
			if (mWestPlayer == null) return false;
			showCancelPlayerDialog(Direction.west);
			break;
		case R.id.member_img_north:
			if (mNorthPlayer == null) return false;
			showCancelPlayerDialog(Direction.north);
			break;
		default:
			break;
		}
		return true;
	}
	
	private void showRandomDialog() {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getString(R.string.please_choose_player));
		mDialog.ok.setText(getResources().getString(R.string.ok));
		ListView listView = (ListView) mDialog.getContentView();
		final PlayerCheckAdapter mAdapter = new PlayerCheckAdapter(mContext);
		listView.setAdapter(mAdapter);
		final List<Player> list = Player.getAllPlayer();
		Player[] curPlayers = {mEastPlayer, mSouthPlayer, mWestPlayer, mNorthPlayer};
		mAdapter.setData(list, curPlayers);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAdapter.setCheck(position);
			}
		});
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				List<Player> checkList = mAdapter.getCheckPlayers();
				if (checkList != null && checkList.size() == mMemberCount) {
					int count = mMemberCount;
					int tmp = random.nextInt(count);
					for (int i = 0; i < 5; i++) { // 多循环几次选定随机数
						tmp = random.nextInt(count);
					}
					setPlayer(checkList.remove(tmp), Direction.east, true);
					if (mMemberCount > 2) {
						tmp = random.nextInt(--count);
						setPlayer(checkList.remove(tmp), Direction.south, true);
					}
					tmp = random.nextInt(--count);
					setPlayer(checkList.remove(tmp), Direction.west, true);
					if (mMemberCount > 3) {
						setPlayer(checkList.get(0), Direction.north, true);
					}
					mDialog.dismiss();
				} else {
					ToastTool.showToast(mContext, getString(R.string.please_select_count_players, mMemberCount));
				}
			}
		});
		mDialog.show();
	}
	
	private void showCardDialog() {
		if (mCardDialog == null) {
			mCardDialog = new CardExtractDialog(mContext);
			mCardDialog.setOnCardExtractListener(new CardExtractListener() {
				
				@Override
				public void onFinish(Player[] players, int[] dirs) {
					for (int i = 0; i < dirs.length; i++) {
						switch (dirs[i]) {
						case 0:
							setPlayer(players[i], Direction.east, true);
							break;
						case 1:
							setPlayer(players[i], Direction.south, true);
							break;
						case 2:
							setPlayer(players[i], Direction.west, true);
							break;
						case 3:
							setPlayer(players[i], Direction.north, true);
							break;
						default:
							break;
						}
					}
				}
			});
		}
		mCardDialog.setMemberCount(mMemberCount);
		mCardDialog.show();
	}
	
	private void showChoosePlayerDialog(final Direction dir) {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle, 0);
		mDialog.addView(R.layout.listview);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getString(R.string.please_choose_player));
		mDialog.ok.setText(getResources().getString(R.string.create));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), CreatePlayerActivity.class));
				mDialog.dismiss();
			}
		});
		ListView listView = (ListView) mDialog.getContentView();
		PlayerSimpleAdapter mAdapter = new PlayerSimpleAdapter(mContext);
		listView.setAdapter(mAdapter);
		final List<Player> list = getPlayersExceptUesd();
		mAdapter.setData(list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Player player = list.get(position);
				setPlayer(player, dir, true);
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
	private void showCancelPlayerDialog(final Direction dir) {
		final CommonDialog mDialog = new CommonDialog(mContext, R.style.MyDialogStyle);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.titleTextView.setText(getString(R.string.is_cancel_player));
		mDialog.ok.setText(getResources().getString(R.string.ok));
		mDialog.ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				setPlayer(null, dir, true);
				mDialog.dismiss();
			}
		});
		mDialog.cancel.setText(getResources().getString(R.string.cancel));
		mDialog.cancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {				
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
	private List<Player> getPlayersExceptUesd() {
		String[] ids = new String[4];
		if (mEastPlayer != null) {
			ids[0] = mEastPlayer.getUuid();
		}
		if (mSouthPlayer != null) {
			ids[1] = mSouthPlayer.getUuid();
		}		
		if (mWestPlayer != null) {
			ids[2] = mWestPlayer.getUuid();
		}		
		if (mNorthPlayer != null) {
			ids[3] = mNorthPlayer.getUuid();
		}
		List<Player> list = PlayerSimpleAdapter.getPlayersExceptUesd(ids);
		return list;
	}
	
	private void setPlayer(Player player, Direction dir, boolean isSave) {
		boolean isReset = (player == null);
		Bitmap bitmap;
		if (!isReset) {
			bitmap = EmoticonTool.getEmoticon(Character.getCharacter(mContext, player.getCharacterId()));
			bitmap = ImageTool.createCircleImage(bitmap, bitmap.getWidth());
		} else {
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.emo_unknown);
		}
		String nickname = isReset ? "" : player.getNickName();
		String uuid = isReset ? "" : player.getUuid();
		String text;
		switch (dir) {
		case east:
			mEastPlayer = player;
			text = isReset ? getString(R.string.east) : nickname;
			mEastText.setText(text);
			mEastView.setImageBitmap(bitmap);
			if (isSave) {
				ShareprefenceTool.getInstance().setString(KEY_EAST, uuid, mContext);
			}
			break;
		case south:
			mSouthPlayer = player;
			text = isReset ? getString(R.string.south) : nickname;
			mSouthText.setText(text);
			mSouthView.setImageBitmap(bitmap);
			if (isSave) {
				ShareprefenceTool.getInstance().setString(KEY_SOUTH, uuid, mContext);
			}
			break;
		case west:
			mWestPlayer = player;
			text = isReset ? getString(R.string.west): nickname;
			mWestText.setText(text);
			mWestView.setImageBitmap(bitmap);
			if (isSave) {
				ShareprefenceTool.getInstance().setString(KEY_WEST, uuid, mContext);
			}
			break;
		case north:
			mNorthPlayer = player;
			text = isReset ? getString(R.string.north) : nickname;
			mNorthText.setText(text);
			mNorthView.setImageBitmap(bitmap);
			if (isSave) {
				ShareprefenceTool.getInstance().setString(KEY_NORTH, uuid, mContext);
			}
			break;
		default:
			break;
		}
	}

}

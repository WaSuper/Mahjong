package com.mahjong.fragment;

import java.util.List;
import java.util.Random;

import com.mahjong.R;
import com.mahjong.activity.CreatePlayerActivity;
import com.mahjong.adapter.PlayerCheckAdapter;
import com.mahjong.adapter.PlayerSimpleAdapter;
import com.mahjong.common.MjSetting;
import com.mahjong.dialog.CardExtractDialog;
import com.mahjong.dialog.CardExtractDialog.CardExtractListener;
import com.mahjong.model.Character;
import com.mahjong.model.Player;
import com.mahjong.tools.EmoticonTool;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.ManageTool;
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
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MemberFragment extends Fragment 
			implements OnClickListener, OnLongClickListener {

	enum Direction {
		east, south, west, north
	}
	
	private View mView;
	private Context mContext;

	private ImageView mEastView;
	private ImageView mSouthView;
	private ImageView mWestView;
	private ImageView mNorthView;

	private TextView mEastText;
	private TextView mSouthText;
	private TextView mWestText;
	private TextView mNorthText;

	private Player mEastPlayer;
	private Player mSouthPlayer;
	private Player mWestPlayer;
	private Player mNorthPlayer;
	
	private ImageView mCardBtn;
	private ImageView mRandomBtn;
	private ImageView mResetBtn;
	
	private CardExtractDialog mCardDialog;
	private Random random = new Random(System.currentTimeMillis());
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_member, container, false);
		mContext = getActivity();
		initUI();
		initData();
		return mView;
	}
	
	public void initGameStart() {
		ManageTool.getInstance().setPlayers(mEastPlayer, mSouthPlayer, mWestPlayer, mNorthPlayer);
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
	
	private void initData() {
		List<Player> list = Player.getAllPlayer();
		String eastId = ShareprefenceTool.getInstance()
				.getString(MjSetting.PLAYER_EAST, mContext, null);
		if (eastId != null) {
			for (Player player : list) {
				if (player.getUuid().equals(eastId)) {
					setPlayer(player, Direction.east, false);
					break;
				}
			}
		}
		String southId = ShareprefenceTool.getInstance()
				.getString(MjSetting.PLAYER_SOUTH, mContext, null);
		if (southId != null) {
			for (Player player : list) {
				if (player.getUuid().equals(southId)) {
					setPlayer(player, Direction.south, false);
					break;
				}
			}
		}
		String westId = ShareprefenceTool.getInstance()
				.getString(MjSetting.PLAYER_WEST, mContext, null);
		if (westId != null) {
			for (Player player : list) {
				if (player.getUuid().equals(westId)) {
					setPlayer(player, Direction.west, false);
					break;
				}
			}
		}
		String northId = ShareprefenceTool.getInstance()
				.getString(MjSetting.PLAYER_NORTH, mContext, null);
		if (northId != null) {
			for (Player player : list) {
				if (player.getUuid().equals(northId)) {
					setPlayer(player, Direction.north, false);
					break;
				}
			}
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
				if (checkList != null && checkList.size() == 4) {
					int tmp = random.nextInt(4);
					for (int i = 0; i < 5; i++) {
						tmp = random.nextInt(4);
					}
					setPlayer(checkList.remove(tmp), Direction.east, true);
					tmp = random.nextInt(3);
					setPlayer(checkList.remove(tmp), Direction.south, true);
					tmp = random.nextInt(2);
					setPlayer(checkList.remove(tmp), Direction.west, true);
					setPlayer(checkList.get(0), Direction.north, true);
					mDialog.dismiss();
				} else {
					ToastTool.showToast(mContext, R.string.please_select_four_players);
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
//		int icon = R.drawable.player_nor;
//		if (!isReset) {
//			icon = HeadIconTool.String2Id(player.getIcon());
//			if (icon == -1) icon = R.drawable.head_none;			
//		}		
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icon);
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
				ShareprefenceTool.getInstance().setString(
						MjSetting.PLAYER_EAST, uuid, mContext);
			}
			break;
		case south:
			mSouthPlayer = player;
			text = isReset ? getString(R.string.south) : nickname;
			mSouthText.setText(text);
			mSouthView.setImageBitmap(bitmap);
			if (isSave) {
				ShareprefenceTool.getInstance().setString(
						MjSetting.PLAYER_SOUTH, uuid, mContext);
			}
			break;
		case west:
			mWestPlayer = player;
			text = isReset ? getString(R.string.west): nickname;
			mWestText.setText(text);
			mWestView.setImageBitmap(bitmap);
			if (isSave) {
				ShareprefenceTool.getInstance().setString(
						MjSetting.PLAYER_WEST, uuid, mContext);
			}
			break;
		case north:
			mNorthPlayer = player;
			text = isReset ? getString(R.string.north) : nickname;
			mNorthText.setText(text);
			mNorthView.setImageBitmap(bitmap);
			if (isSave) {
				ShareprefenceTool.getInstance().setString(
						MjSetting.PLAYER_NORTH, uuid, mContext);
			}
			break;
		default:
			break;
		}
	}

}

package com.mahjong.tools;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.mahjong.model.AudioItem;
import com.mahjong.model.Player;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class AudioTool {

    public static final String RequestCode 	= "RequestCode";
    
    public static final int Type_SoundBox 	= 0x00;
    
    public static final int MAX_TYPE 		= 7;
    public static final int Type_Lizhi_BGM 	= 0x01;
	public static final int Type_Lizhi 		= 0x02;
	public static final int Type_DoubleLizhi= 0x03;
	public static final int Type_Zimo 		= 0x04;
	public static final int Type_Ronghe 	= 0x05;
	public static final int Type_GameTop	= 0x06;
	
	public static final String Histrory_Lizhi_BGM 	= "Histrory_Lizhi_BGM";
	public static final String Histrory_Lizhi 		= "Histrory_Lizhi";
	public static final String Histrory_DoubleLizhi	= "Histrory_DoubleLizhi";
	public static final String Histrory_Zimo 		= "Histrory_Zimo";
	public static final String Histrory_Ronghe 		= "Histrory_Ronghe";
	public static final String Histrory_GameTop		= "Histrory_GameTop";
	
	private boolean enableAudio = true;
	
	private SoundPool mSoundPool;
	private String[] mBgmPaths = {"", "", "", ""};
	private boolean[] mBgmEnables = {false, false, false, false};
	private int[] mLizhiIds = {-1, -1, -1, -1};
	private boolean[] mLizhiEnables = {false, false, false, false};
	private int[] mDoubleLizhiIds = {-1, -1, -1, -1};
	private boolean[] mDoubleLizhiEnables = {false, false, false, false};
	private int[] mZimoIds = {-1, -1, -1, -1};
	private boolean[] mZimoEnables = {false, false, false, false};
	private int[] mRongheIds = {-1, -1, -1, -1};
	private boolean[] mRongheEnables = {false, false, false, false};
	private int[] mGameTopIds = {-1, -1, -1, -1};
	private boolean[] mGameTopEnables = {false, false, false, false};
	
	private MediaPlayer mMediaPlayer;
	
	public AudioTool() {
		initSoundPool();
		initBgmPlayer();
	}
	
	public boolean getAudioEnable() {
		return enableAudio;
	}
	
	public void setAudioEnable(boolean enable) {
		enableAudio = enable;
		if (!enable) {
			stopPlayer();
		}
	}
	
	public void onDestory() {
		onReleaseAudio(mLizhiIds, mLizhiEnables);
		onReleaseAudio(mDoubleLizhiIds, mDoubleLizhiEnables);
		onReleaseAudio(mZimoIds, mZimoEnables);
		onReleaseAudio(mRongheIds, mRongheEnables);
		onReleaseAudio(mGameTopIds, mGameTopEnables);
		
		mSoundPool.release();
		mSoundPool = null;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
	}
	
	private void onReleaseAudio(int[] ids, boolean[] enables) {
		if (ids != null && enables != null) {
			for (int i = 0; i < ids.length; i++) {
	            if (enables[i]) {
	            	mSoundPool.unload(ids[i]);
	            }
	        }
		}
	}
	
	@SuppressWarnings("deprecation")
	private void initSoundPool() {
        // 5.0 及 之后
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = null;
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(4)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else { // 5.0 以前
        	mSoundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);  // 创建SoundPool
        }
    }	
	
	private void initBgmPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setLooping(false);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.seekTo(0);
                mp.start();
            }
        });
    }
	
	public void loadAsyn(final Player[] players) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				for (int i = 0; i < players.length; i++) {
					Player player = players[i];
					loadByPlayer(player, i);
				}
			}
		}).start();		
	}
	
	public void loadAsynByPlayer(final String playerId, final int index) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				loadByPlayer(playerId, index);
			}
		}).start();	
	}
	
	public void loadByPlayer(Player player, int index) {
		loadByPlayer(player.getUuid(), index);
	}
	
	public void loadByPlayer(String playerId, int index) {
		List<AudioItem> audioItems = AudioItem.loadItemsById(playerId);
		if (audioItems != null && audioItems.size() > 0) {
			for (AudioItem audio : audioItems) {
				switch (audio.getType()) {
				case Type_Lizhi_BGM:
					if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
					mBgmEnables[index] = audio.getEnable();
					mBgmPaths[index] = audio.getFilePath();
					break;
				case Type_SoundBox:
					List<AudioItem> soundItems = AudioItem.loadItemsBySoundBoxId(audio.getSoundBoxId());
					boolean enable = audio.getEnable();
					if (soundItems != null && soundItems.size() > 0) {
						for (AudioItem sound : soundItems) {
							switch (sound.getType()) {
							case Type_Lizhi:
								doLoadAudio(mLizhiIds, mLizhiEnables, sound, index, enable);
								break;
							case Type_DoubleLizhi:
								doLoadAudio(mDoubleLizhiIds, mDoubleLizhiEnables, sound, index, enable);
								break;
							case Type_Zimo:
								doLoadAudio(mZimoIds, mZimoEnables, sound, index, enable);
								break;
							case Type_Ronghe:
								doLoadAudio(mRongheIds, mRongheEnables, sound, index, enable);
								break;
							case Type_GameTop:
								doLoadAudio(mGameTopIds, mGameTopEnables, sound, index, enable);
							default:
								break;
							}
						}
					}
					break;
				default:
					break;
				}
			}
		}
	}
	
	private void doLoadAudio(int[] ids, boolean[] enables, 
			AudioItem audio, int index, boolean enable) {
		enables[index] = enable;
		if (ids[index] != -1) mSoundPool.unload(ids[index]);
		if (audio.getFilePath() != null && audio.getFilePath() != "") {
			ids[index] = mSoundPool.load(audio.getFilePath(), 1);
		} else {
			ids[index] = -1;
		}
	}
	
	public void playBgm(int index) {
		if (!enableAudio) return;
		if (mBgmEnables[index]) {
			mMediaPlayer.stop();
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(mBgmPaths[index]);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
	}
	
	public void playLizhi(int index) {
		if (!enableAudio) return;
		if (mLizhiEnables[index]) {
			mSoundPool.play(mLizhiIds[index],1,1,1,0,1);
		}
	}
	
	public void playDoubleLizhi(int index) {
		if (!enableAudio) return;
		if (mDoubleLizhiEnables[index]) {
			mSoundPool.play(mDoubleLizhiIds[index],1,1,1,0,1);
		}
	}
	
	public void playZimo(int index) {
		if (!enableAudio) return;
		if (mZimoEnables[index]) {
			mSoundPool.play(mZimoIds[index],1,1,1,0,1);
		}
	}
	
	public void playRonghe(int index) {
		if (!enableAudio) return;
		if (mRongheEnables[index]) {
			mSoundPool.play(mRongheIds[index],1,1,1,0,1);
		}
	}	
	
	public void playGameTop(int index) {
		if (!enableAudio) return;
		if (mRongheEnables[index]) {
			mSoundPool.play(mGameTopIds[index],1,1,1,0,1);
		}
	}	
	
	public void stopPlayer() {
		mMediaPlayer.stop();
	}
	
	public static boolean setAudioHistory(Context context, int type, String path) {
		if (path == null || path.isEmpty()) return false;
		String historyKey = "";
		switch (type) {
		case Type_Lizhi_BGM:
			historyKey = Histrory_Lizhi_BGM;
			break;
		case Type_Lizhi:
			historyKey = Histrory_Lizhi;
			break;
		case Type_DoubleLizhi:
			historyKey = Histrory_DoubleLizhi;
			break;
		case Type_Zimo:
			historyKey = Histrory_Zimo;
			break;
		case Type_Ronghe:
			historyKey = Histrory_Ronghe;
			break;
		case Type_GameTop:
			historyKey = Histrory_GameTop;
			break;
		default:
			break;
		}
		List<String> historyList = ShareprefenceTool.getInstance()
				.getSringArray2(historyKey, context);
		// 寻找相同的文件移除，再将其放到第一
		Iterator<String> iterator = historyList.iterator();
		while (iterator.hasNext()) {
			String tmp = iterator.next();
			if (tmp.equals(path)) {
				iterator.remove();
				break;
			}
		}
		historyList.add(0, path);
		int maxSize = 10; // 设置最多保存10条历史
		if (historyList.size() > maxSize) {
			for (int i = 10; i < historyList.size();) {
				historyList.remove(i);
			}
		}
		ShareprefenceTool.getInstance().setSringArray2(historyKey, historyList, context);
		return true;
	}
	
	public static List<String> getAudioHistory(Context context, int type) {
		String historyKey = "";
		switch (type) {
		case Type_Lizhi_BGM:
			historyKey = Histrory_Lizhi_BGM;
			break;
		case Type_Lizhi:
			historyKey = Histrory_Lizhi;
			break;
		case Type_DoubleLizhi:
			historyKey = Histrory_DoubleLizhi;
			break;
		case Type_Zimo:
			historyKey = Histrory_Zimo;
			break;
		case Type_Ronghe:
			historyKey = Histrory_Ronghe;
			break;
		case Type_GameTop:
			historyKey = Histrory_GameTop;
			break;
		default:
			break;
		}
		List<String> historyList = ShareprefenceTool.getInstance()
				.getSringArray2(historyKey, context);
		return historyList;
	}
	
	
}

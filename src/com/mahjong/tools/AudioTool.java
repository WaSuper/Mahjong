package com.mahjong.tools;

import java.io.IOException;
import java.util.List;

import com.mahjong.model.AudioItem;
import com.mahjong.model.Player;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class AudioTool {

    public static final String RequestCode 	= "RequestCode";
    public static final int MAX_TYPE 		= 4;
    public static final int Type_Lizhi_BGM 	= 0x01;
	public static final int Type_Lizhi 		= 0x02;
	public static final int Type_Zimo 		= 0x03;
	public static final int Type_Ronghe 	= 0x04;
	
	private boolean enableAudio = true;
	
	private SoundPool mSoundPool;
	private String[] mBgmPaths = {"", "", "", ""};
	private boolean[] mBgmEnables = {false, false, false, false};
	private int[] mLizhiIds = {-1, -1, -1, -1};
	private boolean[] mLizhiEnables = {false, false, false, false};
	private int[] mZimoIds = {-1, -1, -1, -1};
	private boolean[] mZimoEnables = {false, false, false, false};
	private int[] mRongheIds = {-1, -1, -1, -1};
	private boolean[] mRongheEnables = {false, false, false, false};
	
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
		if (mLizhiIds != null && mLizhiEnables != null) {
			for (int i = 0; i < mLizhiIds.length; i++) {
	            if (mLizhiEnables[i]) {
	            	mSoundPool.unload(mLizhiIds[i]);
	            }
	        }
		}
		if (mZimoIds != null && mZimoEnables != null) {
			for (int i = 0; i < mZimoIds.length; i++) {
	            if (mZimoEnables[i]) {
	            	mSoundPool.unload(mZimoIds[i]);
	            }
	        }
		}
		if (mRongheIds != null && mRongheEnables != null) {
			for (int i = 0; i < mRongheIds.length; i++) {
	            if (mRongheEnables[i]) {
	            	mSoundPool.unload(mRongheIds[i]);
	            }
	        }
		}
		mSoundPool.release();
		mSoundPool = null;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
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
		List<AudioItem> audios = AudioItem.loadItemsById(playerId);
		if (audios != null && audios.size() > 0) {
			for (AudioItem audio : audios) {
				switch (audio.getType()) {
				case Type_Lizhi_BGM:
					if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
					mBgmEnables[index] = audio.getEnable();
					mBgmPaths[index] = audio.getFilePath();
					break;
				case Type_Lizhi:
					mLizhiEnables[index] = audio.getEnable();
					if (mLizhiIds[index] != -1) mSoundPool.unload(mLizhiIds[index]);
					if (audio.getFilePath() != null && audio.getFilePath() != "") {
						mLizhiIds[index] = mSoundPool.load(audio.getFilePath(), 1);
					} else {
						mLizhiIds[index] = -1;
					}
					break;
				case Type_Zimo:
					mZimoEnables[index] = audio.getEnable();
					if (mZimoIds[index] != -1) mSoundPool.unload(mZimoIds[index]);
					if (audio.getFilePath() != null && audio.getFilePath() != "") {
						mZimoIds[index] = mSoundPool.load(audio.getFilePath(), 1);
					} else {
						mZimoIds[index] = -1;
					}
					break;
				case Type_Ronghe:
					mRongheEnables[index] = audio.getEnable();
					if (mRongheIds[index] != -1) mSoundPool.unload(mRongheIds[index]);
					if (audio.getFilePath() != null && audio.getFilePath() != "") {
						mRongheIds[index] = mSoundPool.load(audio.getFilePath(), 1);
					} else {
						mRongheIds[index] = -1;
					}
					break;
				default:
					break;
				}
			}
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
	
	public void stopPlayer() {
		mMediaPlayer.stop();
	}
}

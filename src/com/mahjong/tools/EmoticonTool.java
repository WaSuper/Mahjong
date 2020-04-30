package com.mahjong.tools;

import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.mahjong.R;
import com.mahjong.model.Character;
import com.mahjong.model.CharacterIcon;
import com.mahjong.model.Player;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EmoticonTool {
	
	public static DisplayImageOptions mDefaultOptions  = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.emo_unknown) 	//设置图片uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.emo_unknown) 		//设置图片加载或解码过程中发生错误显示的图片
		.build();

	private static Random random = new Random(System.currentTimeMillis());

	public static Bitmap getEmoticon(Character character) {
		if (character != null) {
			String path = character.getDefaultIcon();
			if (character.getUuid() == -1) { // default
				return ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_0_1);
			} else if (character.getUuid() == -2) { // error
				return ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_unknown);
			} else if (character.getUuid() == 0) { // npc
				return ImageLoader.getInstance().loadImageSync(path, mDefaultOptions);
			} else { // other
				if (path == null || path.isEmpty()) {
					return ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_unknown);
				} else {
					return ImageLoader.getInstance().loadImageSync("file://" + path, mDefaultOptions);				
				}
			}			
		} else {
			return ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_unknown);
		}
	}
	
	public static Bitmap getEmoticonForRank(Player player, int rank) {
		Bitmap bitmap = null;
		if (player != null) {
			if (player.getCharacterId() == -1) { // default
				switch (rank) {
				case 1:
					bitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_1_1);
					break;
				case 2:
					bitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_2_1);
					break;
				case 3:
					bitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_3_1);
					break;
				case 4:
					bitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_4_1);
					break;
				default:
					bitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_0_1);
					break;
				}
			} else if (player.getCharacterId() == 0) { // npc
				bitmap = ImageLoader.getInstance().loadImageSync(player.getIcon(), mDefaultOptions);
			} else if (player.getCharacterId() > 0) { // player
				List<CharacterIcon> icons = CharacterIcon.getCharacterIcons(player.getCharacterId(), rank, false);
				if (icons != null && icons.size() > 0) {
					int pos = random.nextInt(icons.size());
					bitmap = ImageLoader.getInstance().loadImageSync("file://" + icons.get(pos).getPath(), mDefaultOptions);
				} else {
					if (player.getIcon() != null && !player.getIcon().isEmpty()) {
						bitmap = ImageLoader.getInstance().loadImageSync("file://" + player.getIcon(), mDefaultOptions);
					} else {
						bitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_unknown);
					}					
				}
			} else { // unknown
				bitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_unknown);
			}
		} else {
			bitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.emo_unknown);
		}
		return bitmap;
	}
	
	public static void showEmoticon(Character character, ImageView icon, DisplayImageOptions options) {
		if (character != null) {
			String path = character.getDefaultIcon();
			if (character.getUuid() == -1) { // default
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_0_1, icon, options);
			} else if (character.getUuid() == -2) { // error
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_unknown, icon, options);
			} else if (character.getUuid() == 0) { // npc
				ImageLoader.getInstance().displayImage(path, icon, options);
			} else { // other
				if (path == null || path.isEmpty()) {
					ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_unknown, icon, options);
				} else {
					ImageLoader.getInstance().displayImage("file://" + path, icon, options);				
				}
			}			
		} else {
			ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_unknown, icon, options);
		}
	}
	
	public static void showEmoticon(Player player, ImageView icon, DisplayImageOptions options) {
		if (player != null) {
			String path = player.getIcon();
			if (player.getCharacterId() == -1) { // default
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_0_1, icon, options);
			} else if (player.getCharacterId() == -2) { // error
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_unknown, icon, options);
			} else if (player.getCharacterId() == 0) { // npc
				ImageLoader.getInstance().displayImage(path, icon, options);
			} else { // other
				if (path == null || path.isEmpty()) {
					ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_unknown, icon, options);
				} else {
					ImageLoader.getInstance().displayImage("file://" + path, icon, options);				
				}
			}			
		} else {
			ImageLoader.getInstance().displayImage("drawable://" + R.drawable.emo_unknown, icon, options);
		}
	}
	
}

package com.mahjong.adapter;

import java.util.List;

import com.mahjong.R;
import com.mahjong.tools.ImageTool;
import com.mahjong.tools.ViewHolder;
import com.mahjong.model.SoundBox;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SoundEffectAdapter extends BaseAdapter {

	private Context mContext;
	private List<SoundBox> mSoundBoxs;
	private DisplayImageOptions options;
	
	private int mTextColor = Color.WHITE;
	
	public SoundEffectAdapter(Context context) {
		this.mContext = context;
		this.options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.soundbox_default) //设置图片uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.soundbox_default) //设置图片加载或解码过程中发生错误显示的图片
			.build();
	}
	
	public void setData(List<SoundBox> soundBoxs) {
		this.mSoundBoxs = soundBoxs;
		this.notifyDataSetChanged();
	}
	
	public void setTextColor(int color) {
		mTextColor = color;
	}
	
	@Override
	public int getCount() {
		return mSoundBoxs != null ? mSoundBoxs.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mSoundBoxs != null ? mSoundBoxs.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_soundbox, null);
        }
		final ImageView iconInner = ViewHolder.get(convertView, R.id.soundbox_item_icon_inner);
		final ImageView iconOuter = ViewHolder.get(convertView, R.id.soundbox_item_icon_outer);
		TextView name = ViewHolder.get(convertView, R.id.soundbox_item_name);
		SoundBox soundbox = mSoundBoxs.get(position);
		ImageLoader.getInstance().loadImage("file://" + soundbox.getDefaultIcon(),
				new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						
					}
					
					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						iconInner.setImageBitmap(null);
						iconOuter.setImageResource(R.drawable.soundbox_default);
					}
					
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						loadedImage = ImageTool.createCircleImage(loadedImage, loadedImage.getWidth());
						iconInner.setImageBitmap(loadedImage);
						iconOuter.setImageResource(R.drawable.soundbox_bg);
					}
					
					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						iconInner.setImageBitmap(null);
						iconOuter.setImageResource(R.drawable.soundbox_default);
					}
				});
		name.setText(soundbox != null ? soundbox.getName() : mContext.getString(R.string.unknown));
		name.setTextColor(mTextColor);
		return convertView;
	}

}

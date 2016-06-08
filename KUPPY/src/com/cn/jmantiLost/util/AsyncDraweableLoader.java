package com.cn.jmantiLost.util;


import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;

import com.cn.jmantiLost.provider.ImageUtils;
import com.cn.jmantiLost.provider.VideoUtils;

public class AsyncDraweableLoader {

	private HashMap<String, SoftReference<Drawable>> imageCache;

	public AsyncDraweableLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback,final int type) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl,type);
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}
	/**
	 * 加载内存卡图片
	 */
	public static Drawable loadImageFromUrl(String url,int type) {		
		Drawable drawable = null ;
		if(type == 1){
			Bitmap bitmap = ImageUtils.getImageThumbnail(url, 80,80);
			drawable = new BitmapDrawable(bitmap);
		}else{
			Bitmap bitmap = VideoUtils.getVideoThumbnail(url, 80, 80, Thumbnails.MICRO_KIND);
			drawable = new BitmapDrawable(bitmap);
		}
		return drawable;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);

	}

}

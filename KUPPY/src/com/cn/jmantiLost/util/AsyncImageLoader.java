package com.cn.jmantiLost.util;


import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {

	private HashMap<String, SoftReference<Bitmap>> imageCache;

	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	public Bitmap loadDrawable(final String imageUrl,final ImageCallback imageCallback, final int type,final int width ,final int height) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			Bitmap Bitmap = softReference.get();
			if (Bitmap != null) {
				return Bitmap;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
			}
		};
		new Thread() {
			@Override
			public void run() {
				Bitmap drawable = loadImageFromUrl(imageUrl, type,width ,height);
				imageCache.put(imageUrl, new SoftReference<Bitmap>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	/**
	 * 加载内存卡图片
	 */
	public static Bitmap loadImageFromUrl(String url, int type,int mItemwidth,int mItemHerght) {
		Bitmap bitmap = null;
		if (type == 1) {
			bitmap = getDrawable(url,1,mItemwidth,mItemHerght);
		} /*else {
			bitmap = VideoUtils.getVideoThumbnail(url, 80, 80,Thumbnails.MICRO_KIND);
		}*/
		return bitmap;
	}

	private static Bitmap getDrawable(String filePath, int zoom,int mItemwidth,int mItemHerght) {
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		int mWidth = options.outWidth;
		int mHeight = options.outHeight;
		int s = 1;
		while ((mWidth / s > mItemwidth * 2 * zoom)
				|| (mHeight / s > mItemHerght * 2 * zoom)) {
			s *= 2;
		}

		options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inSampleSize = s;
		Bitmap bm = BitmapFactory.decodeFile(filePath, options);

		if (bm != null) {
			int h = bm.getHeight();
			int w = bm.getWidth();

			float ft = (float) ((float) w / (float) h);
			float fs = (float) ((float) mItemwidth / (float) mItemHerght);

			int neww = ft >= fs ? mItemwidth * zoom
					: (int) (mItemHerght * zoom * ft);
			int newh = ft >= fs ? (int) (mItemwidth * zoom / ft) : mItemHerght
					* zoom;

			float scaleWidth = ((float) neww) / w;
			float scaleHeight = ((float) newh) / h;

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			bm = Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
			return bm;
		}
		return null;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);

	}

}

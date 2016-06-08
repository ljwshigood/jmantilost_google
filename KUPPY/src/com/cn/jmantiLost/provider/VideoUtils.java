package com.cn.jmantiLost.provider;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.cn.jmantiLost.bean.ImageFolder;

public class VideoUtils {

	private static Cursor getVideoCursor(Context context) {
		String[] projection = new String[] { MediaStore.Video.Media._ID,
				MediaStore.Video.Media.BUCKET_ID, // 直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
				MediaStore.Video.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
				MediaStore.Video.Media.DISPLAY_NAME, // 图片文件名
				MediaStore.Video.Media.DATA };
		String selection = " 0==0) ORDER BY (bucket_display_name";
		ContentResolver cr = context.getContentResolver();

		Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				projection, selection, null, "");
		return cursor;
	}

	public static ArrayList<ImageFolder> getVideoFolderList(Context context) {
		ArrayList<ImageFolder> list = new ArrayList<ImageFolder>();

		if (context != null) {
			Cursor cursor = getVideoCursor(context);
			ImageFolder pf = null;
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
				String bucketName = cursor
						.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
				String displayName = cursor
						.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
				String path = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

				if (pf == null || !pf.getName().equals(bucketName)) {
					pf = new ImageFolder(String.valueOf(id), path, bucketName,
							id, 2);
					list.add(pf);
				}
				pf.getFilePathes().add(path);
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return list;
	}

	public static Bitmap getVideoThumbnail(String videoPath, int width,
			int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

}

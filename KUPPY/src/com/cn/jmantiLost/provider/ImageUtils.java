package com.cn.jmantiLost.provider;


import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.cn.jmantiLost.bean.ImageFolder;

public class ImageUtils {
	
	private Context context;
	
	private static ImageUtils mInstance ;

	private ImageUtils(Context context) {
		this.context = context;
	}
	
	private static  Cursor getImageCursor(Context context){
		String[] projection = new String[] { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.BUCKET_ID, // 直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
				MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
				MediaStore.Images.Media.DATA };
		String selection = " 0==0) ORDER BY (bucket_display_name";
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				projection, selection, null, "");
		return cursor ;
	}
	public static ArrayList<ImageFolder> getImageFolderList(Context context) {

		ArrayList<ImageFolder> folderList = new ArrayList<ImageFolder>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = getImageCursor(context);
		ImageFolder imageFolder = null;
		
		while (cursor.moveToNext()) {
			
			String folderId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
			String folder = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
			int fileId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
			String finaName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
			String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			
			if (imageFolder == null || !imageFolder.getName().equals(folder)) {
				imageFolder = new ImageFolder(folderId,path, folder,fileId,1);
				folderList.add(imageFolder);
			}
			imageFolder.getFilePathes().add(path);
		}
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
		}
		return folderList;
	}
	
	public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				 ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		
		return bitmap;
	}

}

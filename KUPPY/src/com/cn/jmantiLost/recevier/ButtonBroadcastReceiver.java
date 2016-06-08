package com.cn.jmantiLost.recevier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Video.VideoColumns;

public class ButtonBroadcastReceiver extends BroadcastReceiver {

	public final static String ACTION_BUTTON = "com.notifications.intent.action";
	
	public final static String INTENT_BUTTONID_TAG = "ButtonId";
	
	public final static int BUTTON_PRIEW_ID = 1;
	
	private static String mFileDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
										+File.separator+"Camera";
	
	private ArrayList<String> mPicFiles = new ArrayList<String>() ;
	
	public NotificationManager mNotificationManager;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		if(action.equals(ACTION_BUTTON)){
			int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
			switch (buttonId) {
			case BUTTON_PRIEW_ID:
				
				/*intent = new Intent(context,ImagePreviewActivity.class);
				try {
					mPicFiles = FileUtil.getFiles(mFileDirPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				intent.putStringArrayListExtra("filePathList",mPicFiles);
				intent.putExtra("position", 0);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);*/
				clickGallery(context);
				mNotificationManager.cancel(200);
				break;
			default:
				break;
			}
		}
	}
	
	 class Media {
	    	public long id;
	    	public boolean video;
	    	public Uri uri;
	    	public long date;
	    	public int orientation;

	    	Media(long id, boolean video, Uri uri, long date, int orientation) {
	    		this.id = id;
	    		this.video = video;
	    		this.uri = uri;
	    		this.date = date;
	    		this.orientation = orientation;
	    	}
	  }
	
	private Media getLatestMedia(Context context,boolean video) {
    	Media media = null;
		Uri baseUri = video ? Video.Media.EXTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Uri query = baseUri.buildUpon().appendQueryParameter("limit", "1").build();
		String [] projection = video ? new String[] {VideoColumns._ID, VideoColumns.DATE_TAKEN} : new String[] {ImageColumns._ID, ImageColumns.DATE_TAKEN, ImageColumns.ORIENTATION};
		String selection = video ? "" : ImageColumns.MIME_TYPE + "='image/jpeg'";
		String order = video ? VideoColumns.DATE_TAKEN + " DESC," + VideoColumns._ID + " DESC" : ImageColumns.DATE_TAKEN + " DESC," + ImageColumns._ID + " DESC";
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(query, projection, selection, null, order);
			if( cursor != null && cursor.moveToFirst() ) {
				long id = cursor.getLong(0);
				long date = cursor.getLong(1);
				int orientation = video ? 0 : cursor.getInt(2);
				Uri uri = ContentUris.withAppendedId(baseUri, id);
				media = new Media(id, video, uri, date, orientation);
			}
		}
		finally {
			if( cursor != null ) {
				cursor.close();
			}
		}
		return media;
    }
    
	
	 private Media getLatestMedia(Context context) {
			Media image_media = getLatestMedia(context,false);
			Media video_media = getLatestMedia(context,true);
			Media media = null;
			if( image_media != null && video_media == null ) {
				media = image_media;
			}
			else if( image_media == null && video_media != null ) {
				media = video_media;
			}
			else if( image_media != null && video_media != null ) {
				if( image_media.date >= video_media.date ) {
					media = image_media;
				}
				else {
					media = video_media;
				}
			}
			return media;
	    }


	private void clickGallery(Context context){
		Uri uri = null;
		Media media = getLatestMedia(context);
		if( media != null ) {
			uri = media.uri;
		}

		if( uri != null ) {
			try {
				ContentResolver cr = context.getContentResolver();
				ParcelFileDescriptor pfd = cr.openFileDescriptor(uri, "r");
				if( pfd == null ) {
					uri = null;
				}
				pfd.close();
			}
			catch(IOException e) {
				uri = null;
			}
		}
		if( uri == null ) {
			uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		}
		final String REVIEW_ACTION = "com.android.camera.action.REVIEW";
		try {
			
			Intent intent = new Intent(REVIEW_ACTION, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		catch(ActivityNotFoundException e) {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if( intent.resolveActivity(context.getPackageManager()) != null ) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		}
	}
	
}

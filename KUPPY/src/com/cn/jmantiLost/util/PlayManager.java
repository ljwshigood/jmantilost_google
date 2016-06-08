package com.cn.jmantiLost.util;


import java.io.IOException;

import android.media.MediaPlayer;

public class PlayManager {

	private static MediaPlayer mp = new MediaPlayer();

	public static void playRecord(String path) {
		try {
			mp.setDataSource(path);
			mp.prepare();
			mp.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void pauseRecord() {
		if (mp != null) {
			mp.pause();
		}
	}

	public static void releasePlayRecord() {
		if (mp != null) {
			mp.release();
		}
	}
	
	public static long getMusicDuration(String filePath){
		return mp.getDuration();
	}
	
}

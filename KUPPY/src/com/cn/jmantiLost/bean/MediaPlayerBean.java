package com.cn.jmantiLost.bean;

import android.media.MediaPlayer;

public class MediaPlayerBean {
	
	private MediaPlayer mediaPlayer ;
	
	private int count = 0 ;

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void increase(){
		count++ ;
	}
	
	
	
}

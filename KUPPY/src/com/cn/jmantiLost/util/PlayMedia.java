package com.cn.jmantiLost.util;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class PlayMedia {
	private Context mContext;
	private static PlayMedia mPlayMedia;

	private MediaPlayer mMediaPlayer;

	public PlayMedia(Context mContext) {
		this.mContext = mContext;
	}

	public static PlayMedia getInstance(Context mContext) {
		if (null == mPlayMedia) {
			mPlayMedia = new PlayMedia(mContext);
		}
		return mPlayMedia;
	}

	@SuppressWarnings("static-access")
	public void playMusicMedia(int id) {

		try {
			if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
			mMediaPlayer = MediaPlayer.create(mContext, id);
			mMediaPlayer.setVolume(2.0f, 2.0f);
			mMediaPlayer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				release();
			}
		});
	}
	
	public void playMusicMedia(int id,float volume) {

		try {
			if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
			mMediaPlayer = MediaPlayer.create(mContext, id);
			mMediaPlayer.setVolume(volume, volume);
			mMediaPlayer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				release();
			}
		});
	}

	public void release() {
		if(mMediaPlayer != null){
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
}

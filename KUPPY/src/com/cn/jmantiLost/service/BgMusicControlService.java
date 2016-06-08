package com.cn.jmantiLost.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.os.Vibrator;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.bean.DisturbInfo;
import com.cn.jmantiLost.bean.MediaPlayerBean;
import com.cn.jmantiLost.bean.SoundInfo;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.util.FomatTimeUtil;

public class BgMusicControlService extends Service {
	
	public static final String HELP_SOUND_FILE = "help_sound_setting";
	public static final String CTL_ACTION = "com.android.iwit.IWITARTIS.CTL_ACTION";
	MyReceiver serviceReceiver ;
	AudioManager mAudioManager ;
	private DatabaseManager mDatabaseManger;


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	public MediaPlayerBean iteratorMediaPlayer(
			HashMap<String, MediaPlayerBean> hashMapMediaPlayer, String address) {
		Iterator iter = hashMapMediaPlayer.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (key.toString().equals(address)) {
				return (MediaPlayerBean) val;
			}
		}
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mDatabaseManger = DatabaseManager.getInstance(BgMusicControlService.this);
		serviceReceiver = new MyReceiver();
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		IntentFilter filter = new IntentFilter();
		filter.addAction(CTL_ACTION);
		registerReceiver(serviceReceiver, filter);
	}

	private MediaPlayerBean createMediaPlayer(int id, float volume,double duration, final String address) {
		
		MediaPlayer mediaPlayer = null;
		mediaPlayer = MediaPlayer.create(getBaseContext(), id);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) ;
		mediaPlayer.setVolume(current,current);
		mediaPlayer.start();
		
		long formatDuration = (long) duration * 1000;
		final int playCount = (int) formatDuration / mediaPlayer.getDuration();
		final MediaPlayerBean bean = new MediaPlayerBean();
		bean.setMediaPlayer(mediaPlayer);

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				if (bean != null) {
					bean.increase();
					if (playCount <= bean.getCount()) {
						if (mp != null) {
							mp.release();
							mp = null;
							if(vibrator != null){
								vibrator.cancel();
							}
						}
					} else {
						mp.seekTo(0);
						mp.start();
					}
				}
			}
		});
		return bean;
	}

	private Vibrator vibrator ;
	public static boolean isPause = true ;
	
	private static MediaPlayerBean mMediaPlayer = null  ;

	public static MediaPlayerBean getmMediaPlayer() {
		return mMediaPlayer;
	}

	public static void setmMediaPlayer(MediaPlayerBean mMediaPlayer) {
		BgMusicControlService.mMediaPlayer = mMediaPlayer;
	}

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(final Context context, Intent intent) {
			int control = intent.getIntExtra("control", -1);
			String address = intent.getStringExtra("address");
			int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) ;
			switch (control) {
			case 1:
				
				if(mMediaPlayer != null){
					releaseMusic(mMediaPlayer) ;
				}
				
				ArrayList<SoundInfo> soundList = mDatabaseManger.selectSoundInfo(address);
				ArrayList<DisturbInfo> disturbList = mDatabaseManger.selectDisturbInfo(address);
				
				if(disturbList == null || disturbList.size() == 0){
					return ;
				}
				
				boolean isDisturb = disturbList.get(0).isDisturb();
				
				if (soundList.size() > 0) {
					SoundInfo info = soundList.get(0);
					if(isDisturb){
						mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);    
					}else{
						mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
					}
					
					mMediaPlayer = createMediaPlayer(info.getRingId(), info.getRingVolume(),info.getDurationTime(), address) ;
					
					if (!mMediaPlayer.getMediaPlayer().isPlaying()) {
						mMediaPlayer.getMediaPlayer().start();
					}
				}
				
				break;
			case 2:
				releaseMusic(mMediaPlayer);
				break;
			case 3:
				
				if(mMediaPlayer != null){
					releaseMusic(mMediaPlayer) ;
				}
				
				ArrayList<SoundInfo> soundListDisconnect = mDatabaseManger.selectSoundInfo(address);
				if (soundListDisconnect.size() > 0) {
					SoundInfo info = soundListDisconnect.get(0);
					
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
					mMediaPlayer = createMediaPlayer(R.raw.linkloss, info.getRingVolume(),info.getDurationTime(), address) ;
					
					if (!mMediaPlayer.getMediaPlayer().isPlaying()) {
						mMediaPlayer.getMediaPlayer().start();
					}
				}
				
				break;
			}
		}
	}

	private void releaseMusic(MediaPlayerBean mediaBean){
		if(mediaBean != null){
			MediaPlayer mediaPlayer = mediaBean.getMediaPlayer();
			if (mediaPlayer != null) {
				mediaPlayer.release();
				mediaPlayer = null;
			}	
		}
		mMediaPlayer = null ;
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(serviceReceiver);
	}

}

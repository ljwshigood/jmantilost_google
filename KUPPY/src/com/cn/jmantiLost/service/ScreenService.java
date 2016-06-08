package com.cn.jmantiLost.service;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

import com.cn.jmantiLost.util.ScreenObserver;
import com.cn.jmantiLost.util.ScreenObserver.ScreenStateListener;

public class ScreenService extends Service {

	private KeyguardManager mKeyguardManager = null;
	
	private KeyguardLock mKeyguardLock = null;
	
	private PowerManager pm;
	
	private PowerManager.WakeLock wakeLock;

	private ScreenObserver mScreenObserver;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		mScreenObserver = new ScreenObserver(this);
		mKeyguardLock = mKeyguardManager.newKeyguardLock("un_lock");
		mScreenObserver.requestScreenStateUpdate(new ScreenStateListener() {
			@Override
			public void onScreenOn() {
				mKeyguardLock.disableKeyguard();
			}

			@Override
			public void onScreenOff() {
				mKeyguardLock.reenableKeyguard();
			}

			@Override
			public void onUserPresent() {
			}
		});
	}

	@Override
	public void onStart(Intent intent, int startId) {
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		wakeLock.release();
		mKeyguardLock.reenableKeyguard();
		mScreenObserver.stopScreenStateUpdate();
	}

}

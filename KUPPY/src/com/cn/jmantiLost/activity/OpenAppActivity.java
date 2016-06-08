package com.cn.jmantiLost.activity;

import java.util.List;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.bean.AppInfo;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.util.ScreenObserver;
import com.cn.jmantiLost.util.ScreenObserver.ScreenStateListener;

public class OpenAppActivity extends Activity {
	
	private DatabaseManager mDatabaseManager;
	
	private PowerManager pm;
	
	private PowerManager.WakeLock wakeLock;
	
	private Handler handler = new Handler();
	
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			 AppInfo bean = mDatabaseManager.selectAppInfo();
			 openApp(OpenAppActivity.this,bean.getPackageName());
			//finish();
		}
	};
	
	private ScreenObserver mScreenObserver;
	
	private KeyguardManager keyguardManager ;
	
	private KeyguardLock keyguardLock  ;
	
	private Vibrator vibrator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_call_people);
		
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    	vibrator.vibrate(200);
    	
		mDatabaseManager = DatabaseManager.getInstance(OpenAppActivity.this);
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		
		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "un_lock");  
		wakeLock.acquire();  
		
		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		{
	     WindowManager.LayoutParams layout = getWindow().getAttributes();
		    layout.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
	     getWindow().setAttributes(layout); 
		}
		
		keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
		keyguardLock = keyguardManager.newKeyguardLock("un_lock");
		
		keyguardLock.disableKeyguard();
		mScreenObserver = new ScreenObserver(this);
		mScreenObserver.requestScreenStateUpdate(new ScreenStateListener() {
			@Override
			public void onScreenOn() {
				keyguardLock.disableKeyguard();
			}

			@Override
			public void onScreenOff() {
				keyguardLock.reenableKeyguard();
			}

			@Override
			public void onUserPresent() {
			}
		});
		
		AppInfo bean = mDatabaseManager.selectAppInfo();
		openApp(OpenAppActivity.this,bean.getPackageName());
		
	}
	
	@Override
	public void onAttachedToWindow() {
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(wakeLock != null){
			wakeLock.release();
		}
		Log.e("OpenAppActivity","##########################onDestroy");
		Log.e("OpenAppActivity","##########################onDestroy");
		
		/*getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
	                  | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
	                  | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
	                  | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/
		mScreenObserver.stopScreenStateUpdate();
		keyguardLock.reenableKeyguard();
		
		if(vibrator != null){
			vibrator.cancel();	
		}
	}

	private void openApp(Context context, String packageName) {
		PackageInfo pi;
		try {
			pi = context.getPackageManager().getPackageInfo(packageName, 0);

			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);
			List<ResolveInfo> apps = context.getPackageManager()
					.queryIntentActivities(resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String t_packageName = ri.activityInfo.packageName;
				String t_className = ri.activityInfo.name;
				Intent intent = new Intent();  
				//Intent intent = new Intent(Intent.ACTION_MAIN);
				//intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ComponentName cn = new ComponentName(t_packageName, t_className);
				intent.setComponent(cn);
				startActivityForResult(intent, 123);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 123){
			Log.e("liujw","#########################onActivityResult ");
			Log.e("liujw","#########################onActivityResult ");
			Log.e("liujw","#########################onActivityResult ");
			Log.e("liujw","#########################onActivityResult ");
			finish();
		}
		
	}

}

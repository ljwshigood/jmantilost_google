package com.cn.jmantiLost.util;


import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;

import com.cn.jmantiLost.activity.AntilostCameraActivity;
import com.cn.jmantiLost.activity.BackgroundCameraActivity;
import com.cn.jmantiLost.activity.CallActivity;
import com.cn.jmantiLost.activity.FlashActivity;
import com.cn.jmantiLost.activity.OpenAppActivity;
import com.cn.jmantiLost.activity.RecordActivity;
import com.cn.jmantiLost.activity.SosActivity;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.ContactBean;
import com.cn.jmantiLost.db.DatabaseManager;

public class KeyFunctionUtil {

	private Context mContext;

	private static KeyFunctionUtil mInstance;

	private DatabaseManager mDatabaseManager;

	private KeyFunctionUtil(Context context) {
		this.mContext = context;
		mDatabaseManager = DatabaseManager.getInstance(context);
		
		pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);  
		
		mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE); 
	}

	public static KeyFunctionUtil getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new KeyFunctionUtil(context);
		}
		return mInstance;
	}
	
	private boolean islight = true ;
	
	private PowerManager pm;  
	
	private PowerManager.WakeLock wakeLock;  
		
	private KeyguardManager mKeyguardManager = null;    
	
	private KeyguardLock mKeyguardLock = null;    
	
	
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			mKeyguardLock = mKeyguardManager.newKeyguardLock("");    
			mKeyguardLock.disableKeyguard();  
		}
	};
	
	private Handler handler = new Handler();
	
	public void actionKeyFunction(Context context, int action) {
		Intent intent = null;
		
		switch (action) {
		case 0: // camera
			context.sendBroadcast(new Intent(Constant.FINISH));
			if(AppContext.isStart){
				intent = new Intent(context, BackgroundCameraActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				AppContext.isStart = false ;
			}
			
			break;
		case 1: // light
			if (islight) {
				intent = new Intent(context, FlashActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				islight = false;
			}else{
				context.sendBroadcast(new Intent(Constant.FINISH));
				islight = true;
			}
			
			/*Camera mCamera = Camera.open();
			if (islight) {
				Parameters mParameters = mCamera.getParameters();
				mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(mParameters);
				islight = false;
			} else {
				Parameters mParameters = mCamera.getParameters();
				mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(mParameters);
				islight = true;
			}*/
			break;
		case 2: // start app
			/*if(wakeLock != null){
				wakeLock.release();  
			}
			wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");  
			wakeLock.acquire();  
			
			mKeyguardLock = mKeyguardManager.newKeyguardLock("");    
			mKeyguardLock.disableKeyguard();  
			*/
			context.sendBroadcast(new Intent(Constant.FINISH));
			intent = new Intent(context, OpenAppActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			//AppInfo bean = mDatabaseManager.selectAppInfo();
			//openApp(context,bean.getPackageName());
			break;
		case 3: // anti_call
			context.sendBroadcast(new Intent(Constant.FINISH));
			intent = new Intent(context, CallActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			break;
		case 4: // sos
			context.sendBroadcast(new Intent(Constant.FINISH));
			if(AppContext.isStart){
				intent = new Intent(context,SosActivity.class);
				intent.putExtra("action", 4);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				AppContext.isStart = false ;
			}
			break;
		case 5: // call
			context.sendBroadcast(new Intent(Constant.FINISH));
			ContactBean contactBean = mDatabaseManager.selectContact();
			startCallActivity(contactBean.getNumber());
			break ;
		case 6:
			context.sendBroadcast(new Intent(Constant.FINISH));
			intent = new Intent(context, AntilostCameraActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			break;
		case 7:
			context.sendBroadcast(new Intent(Constant.FINISH));
			intent = new Intent(context,RecordActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("flag", 1);
			context.startActivity(intent);
			break ;
		default:
			break;
		}
	}
	
	public void releaseWake(){
		if(wakeLock != null){
			wakeLock.release();
		}
	}
	
	public void startCallActivity(String number){
		  Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+number));  
		  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          mContext.startActivity(intent);  
	}

}

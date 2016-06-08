package com.cn.jmantiLost.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.util.Constant;
import com.cn.jmantiLost.view.FlashlightSurface;

public class FlashActivity extends Activity {
	
	private FlashlightSurface mSurface;  
	
	private finishRecevier mFinishReceiver ;
	
    private ImageView mImageView;  
    private boolean isFlashlightOn = false;  
    
    private Handler mHandler = new Handler();
    
    Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			 mSurface.setFlashlightSwitch(true); 
		}
	};
	
	private Vibrator vibrator;
	
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
    	
    	if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
    	
    	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_flash);  
        mSurface = (FlashlightSurface) findViewById(R.id.surfaceview);  
        mImageView = (ImageView) findViewById(R.id.image);  
      //  changeFlash(AppContext.isFlash);
        mHandler.postDelayed(runnable, 2000);
        mFinishReceiver = new finishRecevier();
        registerOneClickBroadcast();
        
     	vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    	vibrator.vibrate(200);
        
    }  
  
    private void registerOneClickBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.FINISH);
		registerReceiver(mFinishReceiver, filter);
	};

    
    private void changeFlash(boolean flag){
    /*	if(flag){  
            mSurface.setFlashlightSwitch(false);  
            flag = false;  
        }else{  */
            mSurface.setFlashlightSwitch(true);  
          /*  flag = true;  
        }  */
    }
    
    private class finishRecevier extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			vibrator.vibrate(200);
			finish();
		}
	}
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	unregisterReceiver(mFinishReceiver);
    	if(vibrator != null){
    		vibrator.cancel();
    	}
    }
    
    
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        if(MotionEvent.ACTION_UP == event.getAction()){ 
        	 if(isFlashlightOn){  
                 mSurface.setFlashlightSwitch(false);  
                 isFlashlightOn = false;  
                 mImageView.setImageResource(R.drawable.ic_launcher);  
             }else{  
                 mSurface.setFlashlightSwitch(true);  
                 isFlashlightOn = true;  
                 mImageView.setImageResource(R.drawable.ic_launcher);  
             }  
        }  
        return super.onTouchEvent(event);  
    }  
}

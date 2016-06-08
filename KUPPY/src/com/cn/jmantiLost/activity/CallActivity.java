package com.cn.jmantiLost.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.bean.ContactBean;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.util.ScreenObserver;
import com.cn.jmantiLost.util.WindowsTools;
import com.cn.jmantiLost.view.SliderRelativeLayout;
import com.cn.jmantiLost.view.SliderRelativeLayout.IJieTingCall;

public class CallActivity extends Activity implements OnClickListener,IJieTingCall{
	
	private MediaPlayer mMediaPlayer;

	private Vibrator vibrator;
	
	private TextView mTvTime ;
	
	private TextView mTvNumber ;
	
	private TextView mTvContact ;
	
	private void playerDefaultRing() {
		try {
			Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(CallActivity.this, alert);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
			mMediaPlayer.setLooping(true);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 800, 150, 400, 130 }; // OFF/ON/OFF/ON...
			vibrator.vibrate(pattern, 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeRing() {
		try {
			if (mMediaPlayer != null) {
				if (mMediaPlayer.isPlaying()) {
					this.mMediaPlayer.stop();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (null != vibrator) {
				vibrator.cancel();
				vibrator = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private DatabaseManager mDatabaseManager ;
	
	private ContactBean mContactBean ;
	
	private KeyguardLock mKeyguardLock = null;  
	
	private PowerManager pm;
	
	private PowerManager.WakeLock wakeLock;

	KeyguardManager mKeyguardManager = null;  
	
	private LinearLayout mLLJietingCall ;
	
	private LinearLayout mLLCancelCall ;

	private int second = 0;

	private int minute = 0;

	private Timer timer;
	
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			String strMinute = String.valueOf(minute);
			String strSecond = String.valueOf(second);
			if (minute < 10) {
				strMinute = "0" + strMinute;
			}
			if (second < 10) {
				strSecond = "0" + strSecond;
			}
			mTvTime.setText(strMinute + ":" + strSecond);
		};
	};
	
	private void startTimeRecord() {

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				second++;
				if (second >= 60) {
					second = 0;
					minute++;
				}
				mHandler.sendEmptyMessage(1);
			}
		};
		timer = new Timer();
		timer.schedule(timerTask, 1000, 1000);
	}
	
	
	private SliderRelativeLayout mSliderRelativeLayout ;
	
	private int mMiddleWidth ;
	
	private ScreenObserver mScreenObserver;
	
	private KeyguardManager keyguardManager ;
	
	private KeyguardLock keyguardLock  ;
	
	private ImageView mIvJieTing ;
	
	private ImageView mIvCancel ;
	
	private void initImageView(){
		mIvJieTing = (ImageView)findViewById(R.id.iv_jieting_call);
		mIvCancel = (ImageView)findViewById(R.id.iv_cancel_call);
		mIvCancel.setImageResource(R.anim.ic_jieting_call_anim);  
		AnimationDrawable  animationDrawable = (AnimationDrawable) mIvCancel.getDrawable();  
        animationDrawable.start();  
        
        mIvJieTing.setImageResource(R.anim.ic_cancel_call_anim);  
		AnimationDrawable  animationDrawable1 = (AnimationDrawable) mIvJieTing.getDrawable();  
        animationDrawable1.start(); 
        
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_anti_call_people);
		
		mMiddleWidth = WindowsTools.getWindowsWidth(CallActivity.this) / 2;
		
		initImageView();
		
		mSliderRelativeLayout = (SliderRelativeLayout)findViewById(R.id.slider_layout);
		mDatabaseManager = DatabaseManager.getInstance(CallActivity.this);
		mTvContact = (TextView)findViewById(R.id.tv_contact);
		mTvTime = (TextView)findViewById(R.id.tv_time);
		mTvNumber = (TextView)findViewById(R.id.tv_number);
		mContactBean = mDatabaseManager.selectAntiContact();
		mTvNumber.setText(mContactBean.getNumber());
		mTvContact.setText(mContactBean.getContact());
		
		mSliderRelativeLayout.setmIJIeTingCall(this);
		//mLLJietingCall = (LinearLayout)findViewById(R.id.ll_jieting_call);
		//mLLCancelCall = (LinearLayout)findViewById(R.id.ll_cancel_call);
		//mLLJietingCall.setOnClickListener(this);
		//mLLCancelCall.setOnClickListener(this);
		
		
		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");  
		wakeLock.acquire();  
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        // keep Open Camera on top of screen-lock (will still need to unlock when going to gallery or settings)
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        // set screen to max brightness - see http://stackoverflow.com/questions/11978042/android-screen-brightness-max-value
		// done here rather than onCreate, so that changing it in preferences takes effect without restarting app
		{
	        WindowManager.LayoutParams layout = getWindow().getAttributes();
		        layout.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
	        getWindow().setAttributes(layout); 
		}
		
		mSliderRelativeLayout.setmMiddleWidth(mMiddleWidth);
		
		playerDefaultRing();
		
		//startTimeRecord();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeRing() ;
		if(wakeLock != null){
			wakeLock.release();  
		}
		//mScreenObserver.stopScreenStateUpdate();
		//keyguardLock.reenableKeyguard();
	}
	
	@Override
	public void onClick(View v) {
		/*switch (v.getId()) {
		case R.id.ll_jieting_call:
			Intent intent = new Intent(mContext,DialActivity.class);
			mContext.startActivity(intent);
			finish();
			break;
		case R.id.ll_cancel_call:
			finish();
			break;
		default:
			break;
		}*/
	}

	@Override
	public void jietingCall(int isLeft) {
		if(isLeft == 0){
			Intent intent = new Intent(CallActivity.this,DialActivity.class);
			startActivity(intent);
			finish();
		}else{
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}

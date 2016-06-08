package com.cn.jmantiLost.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.bean.ContactBean;
import com.cn.jmantiLost.db.DatabaseManager;

public class DialActivity extends Activity implements OnClickListener{

	private DatabaseManager mDatabaseManager;

	private ContactBean mContactBean;

	private TextView mTvNumber;

	private TextView mTvTimer;

	private int second = 0;

	private int minute = 0;

	private Timer timer;
	
	private TextView mTvContact ;
	
	private LinearLayout mLLStopDial ;

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
			mTvTimer.setText(strMinute + ":" + strSecond);
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
	

	private PowerManager pm;
	
	private PowerManager.WakeLock wakeLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dail);
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		
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
		
		mLLStopDial = (LinearLayout)findViewById(R.id.ll_stop_communcation);
		mLLStopDial.setOnClickListener(this);
		mDatabaseManager = DatabaseManager.getInstance(DialActivity.this);
		mContactBean = mDatabaseManager.selectAntiContact();
		initView();
		initData();
		startTimeRecord();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(wakeLock != null){
			wakeLock.release();  
		}
	}
	
	private void initView() {
		mTvContact = (TextView)findViewById(R.id.tv_contact);
		mTvNumber = (TextView) findViewById(R.id.tv_number);
		mTvTimer = (TextView) findViewById(R.id.tv_time);
	}

	private void initData() {
		mTvNumber.setText(mContactBean.getNumber());
		mTvContact.setText(mContactBean.getContact());
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_stop_communcation:
			setResult(321);
			finish();
			break;

		default:
			break;
		}
	}

}

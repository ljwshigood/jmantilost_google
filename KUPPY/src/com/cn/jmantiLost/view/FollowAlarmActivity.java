package com.cn.jmantiLost.view;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.DeviceSetInfo;
import com.cn.jmantiLost.impl.ICallBack;
import com.cn.jmantiLost.service.BgMusicControlService;
import com.cn.jmantiLost.util.AlarmManager;
import com.cn.jmantiLost.util.Constant;

public class FollowAlarmActivity extends Activity {

	private Context mContext;

	private DeviceSetInfo mInfo;

	private ImageView mIvDeviceIcon;

	private TextView mTvName;

	private TextView mTvContent;
	
	private LinearLayout mLlOK ;
	
	private AlarmManager mAlarmManager ;
	
	private String mAlarmInfo ;
	
	private ICallBack mICallback ;
	
	private int mType ;
	
	private void initView(){
		mIvDeviceIcon = (ImageView)findViewById(R.id.iv_alarm);
		mTvName = (TextView)findViewById(R.id.tv_device_name);
		mTvContent = (TextView)findViewById(R.id.tv_alarm_info);
		mLlOK = (LinearLayout)findViewById(R.id.ll_ok);
		mTvName.setText(mInfo.getmDeviceName());
		mTvContent.setText(mAlarmInfo);
		mIvDeviceIcon.setImageBitmap(mAlarmManager.getDeviceBitmap(mInfo,mContext));
		mLlOK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentDistance = new Intent(BgMusicControlService.CTL_ACTION);
				intentDistance.putExtra("control", 2);
				intentDistance.putExtra("address", mInfo.getmDeviceAddress());
				mContext.sendBroadcast(intentDistance);
				if(mType == Constant.DISTANCE){
					AppContext.mDeviceStatus[0] = 0 ;
					AppContext.mDeviceStatus[1] = 0 ;
				}else if(mType == Constant.DISCONNECT){
					Intent intent = new Intent("blue_disconnect");
					sendBroadcast(intent);
				}
				AppContext.isShow = true ;
				finish();
			}
		});
	}
	
	private void getIntentData(){
		Intent intent = getIntent();
		mInfo = (DeviceSetInfo) intent.getSerializableExtra("deviceinfo");
		mType = intent.getIntExtra("type", -1);
		mAlarmInfo = intent.getStringExtra("alarm_info");
	}
	
	private DestoryBroadcast mDestoryBroadcast ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_dialog);
		mContext = FollowAlarmActivity.this;
		mAlarmManager = AlarmManager.getInstance(mContext);
		getIntentData();
		initView();
		mDestoryBroadcast = new DestoryBroadcast();
	}
	
	private class DestoryBroadcast  extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	}
	
	private void registerReconnectBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.DIALOG_FINISH);
		registerReceiver(mDestoryBroadcast, filter);
	};
	
	@Override
	protected void onResume() {
		registerReconnectBroadcast();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(mDestoryBroadcast);
		super.onPause();
	}
	
	
}

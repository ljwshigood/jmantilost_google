package com.cn.jmantiLost.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.service.BluetoothLeService;

public class AboutMeActivity extends BaseActivity implements OnClickListener{
	
	private ImageView mIvBack ;
	
	private TextView mTvMainInfo ;
	
	private TextView mTvSoftVersion ;
	
	private TextView mTvFireVersion ;
	
	private void initView(){
		mTvSoftVersion = (TextView)findViewById(R.id.tv_soft_version) ;
		mTvFireVersion = (TextView)findViewById(R.id.tv_fire_version) ;
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mTvMainInfo  =(TextView)findViewById(R.id.tv_title_info);
		mIvBack.setOnClickListener(this) ;
		String softString = String.format(mContext.getString(R.string.soft_version_info), "1.0.0") ;
		String fireString = String.format(mContext.getString(R.string.fire_version_info), "1.0.0") ;
		mTvSoftVersion.setText(softString) ;
		mTvFireVersion.setText(fireString) ;
		
		if(AppContext.mBluetoothLeService != null){
			AppContext.mBluetoothLeService.readFireWare() ;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			 	final String action = intent.getAction();
	            String extra = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
	            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
	            	
	            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
	            	
	            	
	            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
	            	
	            	
	            } else if (BluetoothLeService.ACTION_READ_DATA_AVAILABLE.equals(action)) {
	            	
	            	String softString = String.format(mContext.getString(R.string.fire_version_info), extra) ;
	            	mTvFireVersion.setText(softString) ;
	            }
			
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_READ_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSI);
		return intentFilter;
	}

	private void initData(){
		mTvMainInfo.setText(mContext.getString(R.string.about_me)) ;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mGattUpdateReceiver) ;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_me) ;
		initView();
		initData();
		
		if(AppContext.mBluetoothLeService != null){
			AppContext.mBluetoothLeService.readFireWare() ;
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish() ;
			break;

		default:
			break;
		}
	}

}

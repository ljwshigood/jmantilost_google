package com.cn.jmantiLost.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.DeviceSetInfo;
import com.cn.jmantiLost.bean.NotificationBean;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.impl.ICallBack;
import com.cn.jmantiLost.service.BluetoothLeService;
import com.cn.jmantiLost.util.AlarmManager;

/**
 * @author liujw
 * @description:
 *
 */
public class BaseActivity extends FragmentActivity implements ICallBack{

	public Context mContext ;
	
	private AlarmManager mAlarmManager ;
	
	private DatabaseManager mDatabaseManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		super.onCreate(savedInstanceState);
		
		
		mContext = BaseActivity.this ;
		mAlarmManager = AlarmManager.getInstance(mContext);
		mDatabaseManager = DatabaseManager.getInstance(mContext);
		mNotificationMnager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
	}
	
	protected NotificationManager mNotificationMnager ;
	
	protected void  disconnectStatus() {
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
		NotificationBean bean = AppContext.mNotificationBean;
		mNotificationMnager.cancel(bean.getNotificationID());
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSI);
		return intentFilter;
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				
			} else if (BluetoothLeService.ACTION_GATT_RSSI.equals(action)) { // 超距离报警
				
			} else if (BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE.equals(action)) { // 设备寻找手机报警
				
			}else if(BluetoothLeService.ACTION_READ_DATA_AVAILABLE.equals(action)){
				
			}else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				
				if(mIUpdateConnectStatus != null){
					mIUpdateConnectStatus.updateConnectStatus(0);
				}
				
			}
		}
	};
	
	@Override
	public void setFollowDialog(DeviceSetInfo info ,int type) {
		
	}
	
	private IUpdateConnectStatus mIUpdateConnectStatus ;
	
	public IUpdateConnectStatus getmIUpdateConnectStatus() {
		return mIUpdateConnectStatus;
	}

	public void setmIUpdateConnectStatus(IUpdateConnectStatus mIUpdateConnectStatus) {
		this.mIUpdateConnectStatus = mIUpdateConnectStatus;
	}

	public interface IUpdateConnectStatus{
		
		public void updateConnectStatus(int status);
		
	}
	
}

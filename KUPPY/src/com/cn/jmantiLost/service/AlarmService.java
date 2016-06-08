package com.cn.jmantiLost.service;


import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.activity.MainActivity;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.DeviceSetInfo;
import com.cn.jmantiLost.bean.DisturbInfo;
import com.cn.jmantiLost.bean.SoundInfo;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.util.AlarmManager;
import com.cn.jmantiLost.util.Constant;
import com.cn.jmantiLost.util.EncriptyUtils;
import com.cn.jmantiLost.util.KeyFunctionUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;

public class AlarmService extends Service implements ConnectionCallbacks,
													OnConnectionFailedListener,
													OnMyLocationButtonClickListener, 
													com.google.android.gms.location.LocationListener{

	
	
	private AlarmManager mAlarmManager;

	private DatabaseManager mDatabaseManager ;

	private Context mContext;

	private BluetoothAdapter mBluetoothAdapter;

	private void dismissBleActivity() {
		Intent intent = new Intent(Constant.DIALOG_FINISH);
		sendBroadcast(intent);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName componentName,IBinder service) {
				AppContext.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
				if (!AppContext.mBluetoothLeService.initialize()) {
					stopSelf();
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {
				AppContext.mBluetoothLeService = null;
			}
	};
		
	private boolean isBind ;
			
	@Override
	public void onCreate() {
		
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		isBind = this.getApplicationContext().bindService(gattServiceIntent,mServiceConnection, BIND_AUTO_CREATE);
		mContext = AlarmService.this;
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mAlarmManager = AlarmManager.getInstance(mContext);
		mDatabaseManager = DatabaseManager.getInstance(mContext);
		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	
	};

	private Handler handler = new Handler();
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//scanLeDevice(true);
		return super.onStartCommand(intent, flags, startId);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(isBind){
			getApplicationContext().unbindService(mServiceConnection);	
		}
		
		KeyFunctionUtil.getInstance(mContext).releaseWake();
		
		unregisterReceiver(mGattUpdateReceiver);
		
		if (mNotificationManager != null) {
			mNotificationManager.cancel(NOTICE_ID);
		}
		scanLeDevice(false);
		
		mHandler.removeCallbacks(mScanRunnable) ;
	}

	private ArrayList<DeviceSetInfo> mDeviceList = new ArrayList<DeviceSetInfo>();
	
	private final static String TAG = "AlarmService" ;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == 0){
				scanLeDevice(true);
			}else if(msg.what == 1){
				mHandler.removeCallbacks(mScanRunnable) ;
			}
		};
	};
	
	
	private boolean mScanning  ;
	
	private static final long SCAN_PERIOD = 5000;
	
	Runnable mScanRunnable = new Runnable() {
		
		@Override
		public void run() {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			Message msg = new Message() ;
			msg.what = 0 ;
			mHandler.sendMessageDelayed(msg, SCAN_PERIOD) ;
		}
	};
	
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			mHandler.postDelayed(mScanRunnable, SCAN_PERIOD);
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
	
	
	private BluetoothDevice mDevice ;
	
	public  String byte2hex(byte[] b) {

		String hs = "";

		String stmp = "";

		for (int n = 0; n < b.length; n++) {

			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {

				hs = hs + "0" + stmp;

			} else {

				hs = hs + stmp;
			}
		}

		return hs;
	}
	
	private BluetoothDevice compareDBBlueDevice(){
		BluetoothDevice mConnectBlueDevice = null ;
		ArrayList<DeviceSetInfo> deviceList = DatabaseManager.getInstance(mContext).selectDeviceInfo()  ;
		for(int i = 0 ;i < mLeDevices.size() ;i++){
			BluetoothDevice device = mLeDevices.get(i);
			for(int j = 0 ;j < deviceList.size() ;j++){
				if(device.getAddress().equals(deviceList.get(j).getmDeviceAddress())){
					mConnectBlueDevice = device ;
					break ;
				}
			}
		}
		
		return mConnectBlueDevice ;
	}
	
	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,final byte[] scanRecord) {
			new Thread() {
				public void run() {
					
					Log.e("liujw","##########################AlarmService device : "+device.getName());
					
					String advertiseData = byte2hex(scanRecord) ; 
					if(isMatch(advertiseData)){
						addDevice(device) ;	
						mDevice = compareDBBlueDevice() ;
						if(mDevice != null){
							if(AppContext.mBluetoothLeService != null && !AppContext.mBluetoothLeService.isConnect()){
								AppContext.mBluetoothLeService.connect(mDevice.getAddress());	
							}
						}
					}
					
				};
			}.start();
		}
	};
	
	private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>() ;
	
	public void addDevice(BluetoothDevice device) {
		if (!mLeDevices.contains(device)) {
			mLeDevices.add(device);
		}
	}
	
	private boolean isMatch(String string){
		String reg=".*9400.*";  
		return string.matches(reg);
	}
	
	
	
	private void progressTopTaskDeviceDisconnect(String address) {
		DeviceSetInfo info = null ;
		DatabaseManager manager = DatabaseManager.getInstance(mContext);
		manager.updateDeviceLatLogDisconnect(String.valueOf(AppContext.mLatitude), String.valueOf(AppContext.mLongitude), address,System.currentTimeMillis());
		ArrayList<DeviceSetInfo>  list = manager.selectDeviceInfo(address);
		if(list.size() > 0){
			info  = list.get(0);
		}
		AppContext.mBluetoothLeService.close();
		if(info != null && info.isActive()){
			mAlarmManager.DeviceDisconnectAlarm(info, address,mContext.getString(R.string.device_disconnect));
		}
	}
	
	
	private Intent mIntent ;
	
	private void disconnectProgam(String address) {
		
		if(AppContext.mBluetoothLeService != null){
			AppContext.mBluetoothLeService.disconnect() ;
			AppContext.mBluetoothLeService.close() ;
		}
		
		if(mIntent == null){
			return ;
		}
		
		if (!mAlarmManager.isApplicationBroughtToBackground(mContext)) {
			progressTopTaskDeviceDisconnect(address);
		}else if (mAlarmManager.isApplicationBroughtToBackground(mContext)) {
			progressDeviceDisconnect(address,mContext.getString(R.string.device_disconnect));
		}
	}
	
	private void reconnectProgam(){
		if(mNotificationManager != null){
			mNotificationManager.cancel(NOTICE_ID) ;
		}
		
		AppContext.mNotificationBean.setShowNotificationDialog(false) ;
		Intent intentDistance = new Intent(BgMusicControlService.CTL_ACTION);
		intentDistance.putExtra("control",2);
		mContext.sendBroadcast(intentDistance);
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			final String action = intent.getAction();
			mIntent = intent ;
			BluetoothDevice blueDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA) ;
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				disconnectProgam(blueDevice.getAddress());
				scanLeDevice(true);
				
			} else if (BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE.equals(action)) { //设备寻找手机报警
				if(AppContext.isAlarm == false && !mAlarmManager.isApplicationBroughtToBackground(mContext)){
					return ;
				}
				AppContext.mTag[1] = 1 ;
				
				if(AppContext.mTag[0] == 1 && AppContext.mTag[1] == 1){
					return ;
				}
				
				//b124 fcde
				
				String descryString = EncriptyUtils.decryption(data);
				if(descryString.startsWith("756c")){
					if(BgMusicControlService.getmMediaPlayer() != null){
						Intent intentDistance = new Intent(BgMusicControlService.CTL_ACTION);
						intentDistance.putExtra("control", 2);
						intentDistance.putExtra("address", blueDevice.getAddress());
						sendBroadcast(intentDistance);
					}else{
						Intent intentDistance = new Intent(BgMusicControlService.CTL_ACTION);
						intentDistance.putExtra("control", 1);
						intentDistance.putExtra("address", blueDevice.getAddress());
						sendBroadcast(intentDistance);
					}
					
					if (mAlarmManager.isApplicationBroughtToBackground(mContext)) {
						progressDeviceNotification(mIntent,mContext.getString(R.string.device_found_mobile));
					}
					
				}
				
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				
				mHandler.removeCallbacks(mScanRunnable) ;
				reconnectProgam();
				if (AppContext.mBluetoothLeService != null) {
					displayGattServices(AppContext.mBluetoothLeService.getSupportedGattServices(), blueDevice.getAddress());
				}
				
				DatabaseManager.getInstance(mContext).updateDeviceConnect(blueDevice.getAddress());
				
				dismissBleActivity();
				Intent intentDistance = new Intent(BgMusicControlService.CTL_ACTION);
				intentDistance.putExtra("control", 2);
				intentDistance.putExtra("address", blueDevice.getAddress());
				sendBroadcast(intentDistance);
				
				
				Intent intentVisiable = new Intent();
				intentVisiable.setAction(Constant.VISIABLE);
				sendBroadcast(intentVisiable) ;
				
				
				AppContext.mNotificationBean.setShowNotificationDialog(false);
				
			}else if(BluetoothLeService.ACTION_READ_DATA_AVAILABLE.equals(action)){
				byte[] msg = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				if (msg != null) {
					String message = msg.toString();
					if(Integer.parseInt(message) < 30){
						notifycationAlarm(mContext, blueDevice.getAddress(),
								mContext.getString(R.string.battery),
								Constant.READBATTERY);
					}
				}
			}
		}
	};

	@SuppressLint("NewApi")
	private void displayGattServices(List<BluetoothGattService> gattServices,String address) {
		if (gattServices == null) {
			return;
		}
		for (BluetoothGattService gattService : gattServices) {
			if (gattService.getUuid().toString().startsWith("00001802")) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					if (gattCharacteristic.getUuid().toString().startsWith("00002a06")) {

					}
				}
			} else if (gattService.getUuid().toString().startsWith("0000ffe0")) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService
						.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					if (gattCharacteristic.getUuid().toString().startsWith("0000ffe1")) {
						AppContext.mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
					}
				}
			}
		}
		
		saveDatabaseAndStartActivity();
	}
	
	private void saveDatabaseAndStartActivity() {
		if(mDevice == null){
			return ;
		}
		DatabaseManager.getInstance(mContext).deleteAllDeviceInfo();
		ArrayList<DeviceSetInfo> deviceList = DatabaseManager.getInstance(mContext).selectDeviceInfo(mDevice.getAddress());
		if (deviceList.size() == 0) {
			DeviceSetInfo info = new DeviceSetInfo();
			info.setDistanceType(2);
			info.setDisturb(false);
			info.setFilePath(null);
			info.setLocation(true);
			info.setmDeviceAddress(mDevice.getAddress());
			info.setmDeviceName(mDevice.getName());
			info.setConnected(true);
			info.setVisible(false);
			info.setActive(true);
			info.setLat(String.valueOf(AppContext.mLatitude));
			info.setLng(String.valueOf(AppContext.mLongitude));
			DisturbInfo disturbInfo = new DisturbInfo();
			disturbInfo.setDisturb(false);
			disturbInfo.setEndTime("23:59");
			disturbInfo.setStartTime("00:00");
			SoundInfo soundInfo = new SoundInfo();
			soundInfo.setDurationTime(180);
			soundInfo.setRingId(R.raw.findlong);
			soundInfo.setRingName(mContext.getString(R.string.ringset_qsmusic));
			soundInfo.setRingVolume(15);
			soundInfo.setShock(true);
			DatabaseManager.getInstance(mContext).insertDeviceInfo(mDevice.getAddress(), info);
			DatabaseManager.getInstance(mContext).insertDisurbInfo(mDevice.getAddress(), disturbInfo);
			DatabaseManager.getInstance(mContext).insertSoundInfo(mDevice.getAddress(), soundInfo);
		}
		
	}

	private void progressDeviceDisconnect(String address,String infomation) {
		DeviceSetInfo info = null;
		DatabaseManager manager = DatabaseManager.getInstance(mContext);
		manager.updateDeviceLatLogDisconnect(String.valueOf(AppContext.mLatitude), String.valueOf(AppContext.mLongitude), address,System.currentTimeMillis());
		ArrayList<DeviceSetInfo> list = manager.selectDeviceInfo(address);
		if (list.size() > 0) {
			info = list.get(0);
		}
		if (info != null && info.isActive()) {
			boolean isAlarm = mAlarmManager.DeviceDisconnectAlarm(info,address, mContext.getString(R.string.device_disconnect));
			if (isAlarm) {
				notifycationAlarm(mContext, address,infomation,Constant.DISCONNECT);
			}
			AppContext.mHashMapConnectGatt.remove(address);
		}
	}
	
	private void progressDeviceNotification(Intent intent,String infomation) {
		DeviceSetInfo info = null;
		BluetoothDevice blueDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		DatabaseManager manager = DatabaseManager.getInstance(mContext);
		manager.updateDeviceLatLogDisconnect(String.valueOf(AppContext.mLatitude), String.valueOf(AppContext.mLongitude), blueDevice.getAddress(),System.currentTimeMillis());
		ArrayList<DeviceSetInfo> list = manager.selectDeviceInfo(blueDevice.getAddress());
		if (list.size() > 0) {
			info = list.get(0);
		}
		if (info != null && info.isActive()) {
			notifycationAlarm(mContext, blueDevice.getAddress(),infomation,Constant.SENDDATA);
		}
	}



	private static final int NOTICE_ID = 1222;

	private NotificationManager mNotificationManager;

	public void notifycationAlarm(Context context, String address,String string, int type) {

		if (address == null) {
			Intent intent = new Intent(context, MainActivity.class);
			Notification notification = new Notification(R.drawable.ic_launcher,mContext.getString(R.string.notify_alarm),System.currentTimeMillis());
			PendingIntent pendIntent = PendingIntent.getActivity(context, 0,intent, 0);
			notification.setLatestEventInfo(context, mContext.getString(R.string.app_name), string,pendIntent);
			mNotificationManager.notify(NOTICE_ID, notification);
			AppContext.mNotificationBean.setShowNotificationDialog(false);
			AppContext.mNotificationBean.setNotificationID(NOTICE_ID);
		} else {
			Intent intent = new Intent(context, MainActivity.class);
			Notification notification = new Notification(R.drawable.ic_launcher,mContext.getString(R.string.notify_alarm),System.currentTimeMillis());
			PendingIntent pendIntent = PendingIntent.getActivity(context, 0,intent, 0);
			notification.setLatestEventInfo(context, mContext.getString(R.string.app_name), string,pendIntent);
			mNotificationManager.notify(NOTICE_ID, notification);
			AppContext.mNotificationBean.setAddress(address);
			AppContext.mNotificationBean.setShowNotificationDialog(true);
			AppContext.mNotificationBean.setAlarmInfo(string);
			AppContext.mNotificationBean.setNotificationID(NOTICE_ID);
			AppContext.mNotificationBean.setAlarmType(type);
		}
	}
	
	private LocationClient mLocationClient;

	private static final LocationRequest REQUEST = LocationRequest.create()
        .setInterval(5000)       
        .setFastestInterval(16)  
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	 private void setUpLocationClientIfNeeded() {
	        if (mLocationClient == null) {
	            mLocationClient = new LocationClient(
	                    getApplicationContext(),
	                    this,  
	                    this); 
	        }
	    }

	@Override
	public void onLocationChanged(Location location) {
		AppContext.mLatitude = location.getLatitude();
		AppContext.mLongitude = location.getLongitude();
		
		Log.e("liujw","#################latitude : "+location.getLatitude() +"longitude : "+location.getLongitude());
		
		DatabaseManager.getInstance(mContext).updateDeviceLatLogIsConnect(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
	}

	@Override
	public boolean onMyLocationButtonClick() {
		return false;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		  mLocationClient.requestLocationUpdates(
	                REQUEST,
	                this); 
	}

	@Override
	public void onDisconnected() {
		
	}

}

package com.cn.jmantiLost.application;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.bluetooth.BluetoothGatt;

import com.baidu.batsdk.BatSDK;
import com.cn.jmantiLost.R;
import com.cn.jmantiLost.bean.DeviceSetInfo;
import com.cn.jmantiLost.bean.NotificationBean;
import com.cn.jmantiLost.bean.alarmInfo;
import com.cn.jmantiLost.service.BluetoothLeService;

public class AppContext extends Application {

	public static String mDeviceAddress;

	public static  boolean isAlarm = true ;
	
	public static boolean isShow = true ;
	
	public static  HashMap<String, BluetoothGatt> mHashMapConnectGatt = new HashMap<String, BluetoothGatt>();
	
	public static ArrayList<DeviceSetInfo> mDeviceList = new ArrayList<DeviceSetInfo>();
	
	public static int mCurrentTab = 0 ;
	
	public static BluetoothLeService mBluetoothLeService;
	
	public static boolean isEndMusic ;
	
	public int mBatteryValues = 90 ;
	
	public static NotificationBean mNotificationBean = new NotificationBean();
	
	public static int[] mDeviceStatus = {0,0};
	
	public boolean isExistDeviceConnected = false;
	
	public static double mLongitude = 0.0 ;
	
	public static double mLatitude = 0.0;
	
	public static boolean isStart = true ;
	
	public static boolean isFlash = true;
	
	public static String SESSION_KEY ;
	
	public static int[] mTag = new int[]{0,0};
	
	@Override
	public void onCreate() {
		super.onCreate();
		BatSDK.init(this, "e601ac3ecd6dc06b");
		BatSDK.setCollectScreenshot(true);
		BatSDK.setSendPrivacyInformation(true) ;
		
		/*CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		*/
		
	}

}

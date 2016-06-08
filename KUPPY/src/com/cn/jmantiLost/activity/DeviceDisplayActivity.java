package com.cn.jmantiLost.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.adapter.DeviceAdapter;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.DeviceSetInfo;
import com.cn.jmantiLost.bean.DisturbInfo;
import com.cn.jmantiLost.bean.NotificationBean;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.service.BgMusicControlService;
import com.cn.jmantiLost.service.BluetoothLeService;
import com.cn.jmantiLost.util.Constant;
import com.cn.jmantiLost.util.EncriptyUtils;
import com.cn.jmantiLost.view.FollowEditDialog.ICallbackUpdateView;

public class DeviceDisplayActivity extends BaseActivity implements OnClickListener, ICallbackUpdateView {

	private Context mContext;

	private DeviceAdapter mDeviceAdapter;

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";

	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	public final static int RESULT_ADRESS = 1000;

	private static String TAG = "DeviceDisplayActivity";

	private BluetoothAdapter mBluetoothAdapter;

	private ArrayList<DeviceSetInfo> mDeviceList = new ArrayList<DeviceSetInfo>();

	private DatabaseManager mDatabaseManager;

	public void updateDeviceAdapter(String address) {
		for (int i = 0; i < mDeviceList.size(); i++) {
			DeviceSetInfo info = mDeviceList.get(i);
			if (info.getmDeviceAddress().equals(address)) {
				info.setActive(true);
				info.setConnected(true);
				info.setVisible(false);
				mDatabaseManager.updateDeviceActiveStatus(info.getmDeviceAddress(), info);
			}
		}
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

				BluetoothDevice blueDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (AppContext.mBluetoothLeService != null) {
					displayGattServices(AppContext.mBluetoothLeService.getSupportedGattServices(),device.getAddress());
				}
				
				updateDeviceAdapter(blueDevice.getAddress());
				
				mIvMobileSingle.setVisibility(View.INVISIBLE) ;
				mIvDeviceSingle.setVisibility(View.INVISIBLE) ;		
				
				mLlMobileDevice.setVisibility(View.VISIBLE);
				mLLDeviceMobile.setVisibility(View.GONE) ;
				
				mTvMainInfo.setText(device.getName()) ;

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				
				mIvMobileSingle.setVisibility(View.INVISIBLE) ;
				mIvDeviceSingle.setVisibility(View.INVISIBLE) ;	
				
				mLlMobileDevice.setVisibility(View.GONE);
				mLLDeviceMobile.setVisibility(View.GONE) ;
				
				mIvDisconnect.setVisibility(View.VISIBLE);
				
				initDisconnectStatus() ;
				initRedBolangAnim() ;
				
				mllCall.setBackgroundResource(R.drawable.btn_red_selector)  ;
				mTvCall.setTextColor(mContext.getResources().getColor(R.color.red));
				mTvCall.setText(mContext.getString(R.string.stop)) ;
				
			}else if (BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE.equals(action)){
				mNotifyData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
				handler.postDelayed(updateUIRunnable, 200);
			}else if(Constant.VISIABLE.equals(action)){
				
				mIvBolang.setVisibility(View.INVISIBLE) ;
				mIvDeviceSingle.setVisibility(View.GONE) ;
				mIvMobileSingle.setVisibility(View.GONE) ;
				
				mLLDeviceMobile.setVisibility(View.GONE);
				mLlMobileDevice.setVisibility(View.VISIBLE);
				
				mllCall.setBackgroundResource(R.drawable.btn_blue_selector)  ;
				mTvCall.setTextColor(mContext.getResources().getColor(R.color.blue_light));
				mTvCall.setText(mContext.getString(R.string.call)) ;
				mIvDisconnect.setVisibility(View.INVISIBLE) ;
			}
		}
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			if(AppContext.mTag[0] == 1 && AppContext.mTag[1] == 1){
				AppContext.mTag[0] = 0 ;
				AppContext.mTag[1] = 0 ;
				
				mIvBolang.setVisibility(View.INVISIBLE) ;
				mIvDeviceSingle.setVisibility(View.GONE) ;
				mIvMobileSingle.setVisibility(View.GONE) ;
				
				mLLDeviceMobile.setVisibility(View.GONE);
				mLlMobileDevice.setVisibility(View.VISIBLE);
				
				mllCall.setBackgroundResource(R.drawable.btn_blue_selector)  ;
				mTvCall.setTextColor(mContext.getResources().getColor(R.color.blue_light));
				mTvCall.setText(mContext.getString(R.string.call)) ;
				mIvDisconnect.setVisibility(View.INVISIBLE) ;
				
				return ;
			}
			
			String descryString = EncriptyUtils.decryption(mNotifyData);
			if(descryString.startsWith("756c")){
				if(BgMusicControlService.getmMediaPlayer() != null){
					initRedBolangAnim();
					mLlMobileDevice.setVisibility(View.GONE);
					mLLDeviceMobile.setVisibility(View.VISIBLE) ;
					
					mIvDeviceSingle.setVisibility(View.VISIBLE);
					mIvMobileSingle.setVisibility(View.GONE) ;
					initDeviceSingleStatus();
					
					mllCall.setBackgroundResource(R.drawable.btn_red_selector)  ;
					mTvCall.setTextColor(mContext.getResources().getColor(R.color.red));
					mTvCall.setText(mContext.getString(R.string.stop)) ;
				}else{
					
					if(AppContext.mBluetoothLeService != null){
						AppContext.mBluetoothLeService.writeCharacterOFF("") ;
					}
					
					mIvBolang.setVisibility(View.INVISIBLE) ;
					mIvDeviceSingle.setVisibility(View.GONE) ;
					mIvMobileSingle.setVisibility(View.GONE) ;
					
					mLLDeviceMobile.setVisibility(View.GONE);
					mLlMobileDevice.setVisibility(View.VISIBLE);
					
					mllCall.setBackgroundResource(R.drawable.btn_blue_selector)  ;
					mTvCall.setTextColor(mContext.getResources().getColor(R.color.blue_light));
					mTvCall.setText(mContext.getString(R.string.call)) ;
					
					Intent intentDistance = new Intent(BgMusicControlService.CTL_ACTION);
					intentDistance.putExtra("control", 2);
					intentDistance.putExtra("address", AppContext.mBluetoothLeService.getGattAddress());
					sendBroadcast(intentDistance);
				}
				
			}
		};
	};
	
	
	private String mNotifyData ;
	
	Runnable updateUIRunnable = new Runnable() {
		
		@Override
		public void run() {
			handler.sendEmptyMessage(0) ;
		}
	};
	

	Runnable mRunable = new Runnable() {
		@Override
		public void run() {

			for (int i = 0; i < mDeviceList.size(); i++) {
				DeviceSetInfo info = mDeviceList.get(i);
				info.setVisible(false);
			}
			mDeviceAdapter.notifyDataSetChanged();
		}
	};

	private DeviceSetInfo mDeviceSetInfo;

	private String mDeviceAddress;
	
	private ImageView mIvBolang ;

	private void initRedBolangAnim(){
		mIvBolang.setVisibility(View.VISIBLE);
		mIvBolang.setImageResource(R.anim.boliang_red_anim);  
		AnimationDrawable  animationDrawable = (AnimationDrawable) mIvBolang.getDrawable();  
        animationDrawable.start();  
	}
	
	
	private ImageView mIvDisconnect ;
	
	private ImageView mIvMobileSingle ;
	
	private void initMobileSingleStatus(){
		mIvMobileSingle.setImageResource(R.anim.singe_anim);  
		AnimationDrawable  animationDrawable = (AnimationDrawable) mIvMobileSingle.getDrawable();  
        animationDrawable.start(); 
	}
	
	private void initDeviceSingleStatus(){
		mIvDeviceSingle.setImageResource(R.anim.singe_anim);  
		AnimationDrawable  animationDrawable = (AnimationDrawable) mIvDeviceSingle.getDrawable();  
        animationDrawable.start(); 
	}
	private void initDisconnectStatus(){
		mIvDisconnect.setImageResource(R.anim.break_anim);  
		AnimationDrawable  animationDrawable = (AnimationDrawable) mIvDisconnect.getDrawable();  
        animationDrawable.start(); 
	}
	
	private MainActivity mParentActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		mDatabaseManager = DatabaseManager.getInstance(mContext);
		mParentActivity = (MainActivity) getParent() ;
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported,Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		mContext = DeviceDisplayActivity.this;
		mDeviceSetInfo = mDatabaseManager.selectSingleDeviceInfo();

		initView();

		if (mDeviceSetInfo != null) {
			mDeviceAddress = mDeviceSetInfo.getmDeviceAddress();
			AppContext.mDeviceAddress = mDeviceAddress;
			initData();
			initDeviceListInfo();
		}
		mLlMobileDevice.setVisibility(View.VISIBLE);
		mLLDeviceMobile.setVisibility(View.GONE) ;
		
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

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
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					if (gattCharacteristic.getUuid().toString().startsWith("0000ffe1")) {
						AppContext.mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
					}
				}
			}
		}
	}

	public void initDeviceListInfo() {

		mDeviceList = mDatabaseManager.selectDeviceInfo();
		
		if(AppContext.mBluetoothLeService != null && AppContext.mBluetoothLeService.isConnect()){
			if(mDeviceList != null && mDeviceList.size() > 0){
				mDeviceSetInfo =  mDeviceList.get(0);
				mDeviceSetInfo.setConnected(true);
				mDeviceSetInfo.setVisible(false);
			}
		}
	}

	private void initData() {
		
		ArrayList<DeviceSetInfo> list = mDatabaseManager.selectDeviceInfo(mDeviceAddress);
		
		if (list.size() > 0) {
			mDeviceSetInfo = mDatabaseManager.selectDeviceInfo(mDeviceAddress).get(0);
		}
		
		ArrayList<DisturbInfo> disturbList = mDatabaseManager.selectDisturbInfo(mDeviceAddress);
		if (disturbList == null) {
			return;
		}

	}

	private TextView mTvMainInfo ;
	
	private ImageView mIvLeft ;
	
	private ImageView mIvDeviceSingle; 
	
	private LinearLayout mLLDeviceMobile  ;
	
	private LinearLayout mLlMobileDevice   ;
	
	private LinearLayout mllCall ;

	private TextView mTvCall ;
	
	private void initView() {
		mTvCall = (TextView)findViewById(R.id.tv_call) ;
		mllCall = (LinearLayout)findViewById(R.id.ll_call) ;
		mLLDeviceMobile = (LinearLayout)findViewById(R.id.ll_device_mobile);
		mLlMobileDevice = (LinearLayout)findViewById(R.id.ll_mobile_device);
		mIvDeviceSingle = (ImageView)findViewById(R.id.iv_single_device) ;
		mIvMobileSingle = (ImageView)findViewById(R.id.iv_singe_mobile);
		mIvDisconnect = (ImageView)findViewById(R.id.iv_disconnect);
		mIvBolang = (ImageView)findViewById(R.id.iv_bolang) ;
		mTvMainInfo = (TextView)findViewById(R.id.tv_title_info);
		mIvLeft = (ImageView)findViewById(R.id.iv_back);
		mIvLeft.setVisibility(View.INVISIBLE);
		mTvMainInfo.setText(mContext.getString(R.string.new_info));
		mllCall.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ArrayList<DeviceSetInfo> deviceList = mDatabaseManager.selectDeviceInfo(mDeviceAddress);
		if (deviceList.size() > 0) {
			mDatabaseManager.updateDeviceInfo(mDeviceAddress, mDeviceSetInfo);
		}
		
		unregisterReceiver(mGattUpdateReceiver);
	}

	private static final int REQUEST_ENABLE_BT = 1;

	@Override
	protected void onResume() {
		super.onResume();
		AppContext.isAlarm = true ;
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
		
		NotificationBean bean = AppContext.mNotificationBean;
		if(bean != null && bean.isShowNotificationDialog()){
			Message msg = new Message() ;
			msg.what = 1 ;
			mParentActivity.mRedirectHandler.sendMessage(msg);
			
			ArrayList<DeviceSetInfo> list = mDatabaseManager.selectDeviceInfo(bean.getAddress());
			DeviceSetInfo info = null;
			if (list.size() > 0) {
				info = list.get(0);
			}
			
			mIvDeviceSingle.setVisibility(View.GONE);
			mIvMobileSingle.setVisibility(View.VISIBLE) ;
			initMobileSingleStatus() ;
			initRedBolangAnim() ;
			
			mllCall.setBackgroundResource(R.drawable.btn_red_selector)  ;
			mTvCall.setTextColor(mContext.getResources().getColor(R.color.red));
			mTvCall.setText(mContext.getString(R.string.stop)) ;
			bean.setShowNotificationDialog(false);
			
		}
		
		mNotificationMnager.cancel(bean.getNotificationID());
		
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSI);
		intentFilter.addAction(Constant.VISIABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_READ_DATA_AVAILABLE);
		return intentFilter;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(DeviceDisplayActivity.this);
		builder.setMessage(mContext.getString(R.string.non_device_connected));
		builder.setTitle(mContext.getString(R.string.tip));
		builder.setPositiveButton(mContext.getString(R.string.ok),new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Message msg = new Message();
				msg.what = 0  ;
				mParentActivity.mRedirectHandler.sendMessage(msg);
			}
		}) ;
		
		builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}) ;
		
		builder.create().show();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_call:
			
			
			if(mTvCall.getText().toString().equals(mContext.getString(R.string.call))){
				
				if(AppContext.mBluetoothLeService != null && !AppContext.mBluetoothLeService.isConnect()){
					dialog();
					return ;
				}else if(AppContext.mBluetoothLeService != null && AppContext.mBluetoothLeService.isConnect()){
					
					mIvDeviceSingle.setVisibility(View.GONE);
					mIvMobileSingle.setVisibility(View.VISIBLE) ;
					initMobileSingleStatus() ;
					initRedBolangAnim() ;
					
					mllCall.setBackgroundResource(R.drawable.btn_red_selector)  ;
					mTvCall.setTextColor(mContext.getResources().getColor(R.color.red));
					mTvCall.setText(mContext.getString(R.string.stop)) ;
					
					if(AppContext.mBluetoothLeService != null){
						AppContext.mBluetoothLeService.writeCharacterOn("") ;
						AppContext.mTag[0] = 1 ;
					}
				}
				
			}else if(mTvCall.getText().toString().equals(mContext.getString(R.string.stop))){
				
				if(AppContext.mBluetoothLeService != null){
					AppContext.mBluetoothLeService.writeCharacterOFF("") ;
				}
				
				mIvBolang.setVisibility(View.INVISIBLE) ;
				mIvDeviceSingle.setVisibility(View.GONE) ;
				mIvMobileSingle.setVisibility(View.GONE) ;
				
				mLLDeviceMobile.setVisibility(View.GONE);
				mLlMobileDevice.setVisibility(View.VISIBLE);
				
				mllCall.setBackgroundResource(R.drawable.btn_blue_selector)  ;
				mTvCall.setTextColor(mContext.getResources().getColor(R.color.blue_light));
				mTvCall.setText(mContext.getString(R.string.call)) ;
				mIvDisconnect.setVisibility(View.INVISIBLE) ;
				
				
				Intent intentDistance = new Intent(BgMusicControlService.CTL_ACTION);
				intentDistance.putExtra("control", 2);
				sendBroadcast(intentDistance);
				
				
			}
			
			
			break;

		default:
			break;
		}
		
	}

	@Override
	public void updateView() {
		initData();
		initView();
	};
}

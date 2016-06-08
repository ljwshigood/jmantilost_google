package com.cn.jmantiLost.activity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.app.TabActivity;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.DeviceSetInfo;
import com.cn.jmantiLost.bean.DisturbInfo;
import com.cn.jmantiLost.bean.NotificationBean;
import com.cn.jmantiLost.bean.SoundInfo;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.impl.IDismissListener;
import com.cn.jmantiLost.service.AlarmService;
import com.cn.jmantiLost.service.BgMusicControlService;
import com.cn.jmantiLost.service.BluetoothLeService;
import com.cn.jmantiLost.util.Constant;
import com.cn.jmantiLost.util.EncriptyUtils;

public class MainActivity extends TabActivity implements OnClickListener,OnTabChangeListener,IDismissListener{

	private Context mContext;
	
	private TabHost mTabHost;
	
	private ImageView mIvBack ;  
	
	private LinearLayout mLLMain ;
	
	private LinearLayout mLLContent ;
	
	public Handler mRedirectHandler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				mLLMain.setVisibility(View.GONE) ;
				mLLContent.setVisibility(View.VISIBLE) ;
			}else{
				mLLMain.setVisibility(View.VISIBLE) ;
				mLLContent.setVisibility(View.GONE) ;
			}
		}
	};

	
    private  IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(Constant.VISIABLE);
        return intentFilter;
    }
	
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
    	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
    		Log.e("liujw","#######dispatchKeyEvent ");
    		AlertDialog.Builder builder = new Builder(mContext);
    		builder.setMessage(mContext.getString(R.string.exit_all_application));
    		builder.setTitle(mContext.getString(R.string.tip));
    		builder.setPositiveButton(mContext.getString(R.string.ok),new DialogInterface.OnClickListener() {
    			
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				android.os.Process.killProcess(android.os.Process.myPid());  
    				System.exit(0) ;
    				dialog.dismiss();
    			}
    		}) ;
    		
    		builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
    		});
    		builder.create().show();
		}
    	return super.dispatchKeyEvent(event);
    }
    
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mGattUpdateReceiver);
		scanLeDevice(false) ;
	}
	
	private BluetoothAdapter mBluetoothAdapter ;
 	
	private static final long SCAN_PERIOD = 5000;
	
	private Handler mHandler;
	
	private boolean mScanning;
	
	private AudioManager mAudioManager;
	
	private TextView mTvConnectStatus ;
	
	public BluetoothLeService mBluetoothLeService;
	
	public String randomGenerate(int count){
		StringBuffer sb = new StringBuffer() ;
		String str = "0123456789" ;
		Random r = new Random() ;
		for(int i = 0 ;i < count ;i++){
			int num = r.nextInt(str.length());
			sb.append(str.charAt(num)) ;
			str = str.replace((str.charAt(num)+""), "") ;
		}
		return sb.toString() ;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_follow);
		
		mContext = MainActivity.this;
		
		mNotificationMnager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
		Intent intent = new Intent(mContext, BgMusicControlService.class);
		startService(intent);
		
		Intent intentAlarm = new Intent(mContext, AlarmService.class);
		startService(intentAlarm);
		
		initView();
		setTabBackGroundSelect(0);
		
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		mHandler = new Handler();
		NotificationBean bean = AppContext.mNotificationBean;
		if(bean != null && !bean.isShowNotificationDialog()){
			scanLeDevice(true);
		}
		
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	private BluetoothDevice mDevice ;
	
	private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>() ;
	
	public void addDevice(BluetoothDevice device) {
		if (!mLeDevices.contains(device)) {
			mLeDevices.add(device);
		}
	}
	
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
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					String advertiseData = byte2hex(scanRecord) ; 
					Log.e("liujw","###################onLeScan : "+byte2hex(scanRecord));
					Log.e("liujw","###################onLeScan : "+isMatch(byte2hex(scanRecord)));
					if(isMatch(advertiseData)){
						addDevice(device) ;	
						mDevice = compareDBBlueDevice() ;
						if(mDevice != null){
							if(AppContext.mBluetoothLeService != null && !AppContext.mBluetoothLeService.isConnect()){
								AppContext.mBluetoothLeService.connect(mDevice.getAddress());	
							}
						}
					}
				}
			});
		}
	};
	
	private boolean isMatch(String string){
		String reg=".*94001122.*";  
		//
		return string.matches(reg);
	}
	
	
	
	public boolean  parseData(byte[] adv_data) {
		  boolean isRet = false ;
		  ByteBuffer buffer = ByteBuffer.wrap(adv_data).order(ByteOrder.LITTLE_ENDIAN);
		  while (buffer.remaining() > 2) {
		    byte length = buffer.get();
		    if (length == 0)
		      break;
		    byte type = buffer.get();
		    length -= 1;
		    switch (type) {
		      case 0x01: // Flags
		       // parsedAd.flags = buffer.get();
		    	  Log.e("liujw","####################flags : "+buffer.get());
		        length--;
		        break;
		      case 0x02: // Partial list of 16-bit UUIDs
		      case 0x03: // Complete list of 16-bit UUIDs
		      case 0x14: // List of 16-bit Service Solicitation UUIDs
		        while (length >= 2) {
		        
		        Log.e("liujw","####################Solicitation UUIDs : "+UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
		          length -= 2;
		        }
		        break;
		      case 0x04: // Partial list of 32 bit service UUIDs
		      case 0x05: // Complete list of 32 bit service UUIDs
		        while (length >= 4) {
		         /* parsedAd.uuids.add(UUID.fromString(String.format(
		              "%08x-0000-1000-8000-00805f9b34fb", buffer.getInt())));*/
		          Log.e("liujw","####################service UUIDs : "+UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", buffer.getInt())));
		          length -= 4;
		        }
		        break;
		      case 0x06: // Partial list of 128-bit UUIDs
		      case 0x07: // Complete list of 128-bit UUIDs
		      case 0x15: // List of 128-bit Service Solicitation UUIDs
		        while (length >= 16) {
		          long lsb = buffer.getLong();
		          long msb = buffer.getLong();
		          Log.e("liujw","####################Solicitation UUIDs : "+new UUID(msb, lsb));
		       //   parsedAd.uuids.add(new UUID(msb, lsb));
		          length -= 16;
		        }
		        break;
		      case 0x08: // Short local device name
		      case 0x09: // Complete local device name
		        byte sb[] = new byte[length];
		        buffer.get(sb, 0, length);
		        length = 0;
		        Log.e("liujw","####################localName : "+new String(sb).trim());
		        //parsedAd.localName = new String(sb).trim();
		        break;				
		      case (byte) 0xFF: // Manufacturer Specific Data
		        //parsedAd.manufacturer = buffer.getShort();
		        length -= 2;
		      	byte[] manufacture = shortToByte(buffer.getShort());
		      	String manufactureString = new String(manufacture).trim();
		    	Log.e("liujw","####################buffer.getShort() : "+buffer.getShort());
		      	if(manufactureString.startsWith("94002211")){
		      		isRet = true ;
		      	}
		        
		        break;
		      default: // skip
		        break;
		    }
		    if (length > 0) {
		      buffer.position(buffer.position() + length);
		    }
		  }
		  return isRet ;
		}
	
	  public byte[] shortToByte(short number) { 
	        int temp = number; 
	        byte[] b = new byte[2]; 
	        for (int i = 0; i < b.length; i++) { 
	            b[i] = new Integer(temp & 0xff).byteValue();//将最低位保存在最低位 
	            temp = temp >> 8; // 向右移8位 
	        } 
	        return b; 
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
	
	
	private BluetoothDevice selectBlueDevice(){
		BluetoothDevice mConnectBlueDevice = null ;
		boolean isExist = false ;
		ArrayList<DeviceSetInfo> deviceList = DatabaseManager.getInstance(mContext).selectDeviceInfo()  ;
		for(int i = 0 ;i < mLeDevices.size() ;i++){
			BluetoothDevice device = mLeDevices.get(i);
			for(int j = 0 ;j < deviceList.size() ;j++){
				if(device.getAddress().equals(deviceList.get(j).getmDeviceAddress())){
					isExist = true ;
					mConnectBlueDevice = device ;
					break ;
				}
			}
		}
		
		if(!isExist && mLeDevices.size() > 0){
			mConnectBlueDevice = mLeDevices.get(0) ;
		}
		return mConnectBlueDevice ;
	}
	
	private Handler mScanHandler = new Handler(){
		
		public void handleMessage(Message msg) {
			dialog();
			mPbBar.setVisibility(View.GONE);
			mTvScanneStatus.setText(mContext.getString(R.string.non_device)) ;
			mTvConnectStatus.setText(mContext.getString(R.string.again_scan)) ;
		};
		
	} ;
	
	Runnable mScanRunnable = new Runnable() {
		@Override
		public void run() {
			
			if(mLeDevices == null || mLeDevices.size() == 0){
				mScanHandler.sendEmptyMessage(0) ;
				return ;
			}
			
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mDevice = selectBlueDevice();
			
			if(mDevice == null){
				return ;
			}
			
			if(AppContext.mBluetoothLeService != null && !AppContext.mBluetoothLeService.isConnect()){
				AppContext.mBluetoothLeService.connect(mDevice.getAddress());	
			}
			
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
	
	
	protected void dialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setMessage(mContext.getString(R.string.no_scanning_device));
		builder.setTitle(mContext.getString(R.string.tip));
		builder.setPositiveButton(mContext.getString(R.string.ok),new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}) ;
		
		builder.create().show();
	}
	private TabWidget mTabWidget;  
	
	private void setupTab(Class<?> ccls, String name, int label, Integer iconId) {
		Intent intent = new Intent().setClass(this, ccls);
		View view = LayoutInflater.from(this).inflate(R.layout.tab_item_view,null);
		ImageView image = (ImageView) view.findViewById(R.id.ic_icon);
		TextView text = (TextView) view.findViewById(R.id.tv_info);
		image.setImageResource(iconId);
		text.setText(label);
		TabSpec spec = mTabHost.newTabSpec(name).setIndicator(view).setContent(intent);
		
		mTabTextViewList.add(text);
		mImageViewList.add(image);
		mLLTabItemViewList.add((LinearLayout)view) ;
		
		mTabHost.addTab(spec);
		
	}
	
	private List<LinearLayout> mLLTabItemViewList = new ArrayList<LinearLayout>();
	
	private LinearLayout mLLConnectStatus ;
	
	private TextView mTvScanneStatus ;
	
	private ProgressBar mPbBar ;
	
	private void initView() {
		mPbBar = (ProgressBar)findViewById(R.id.pb_search) ;
		mTvScanneStatus = (TextView)findViewById(R.id.tv_scan_status);
		mLLConnectStatus = (LinearLayout)findViewById(R.id.ll_connect_status) ;
		mTvConnectStatus = (TextView)findViewById(R.id.tv_connect_status) ;
		mLLMain = (LinearLayout)findViewById(R.id.ll_main) ;
		mLLContent = (LinearLayout)findViewById(R.id.ll_scan) ;
		mIvBack = (ImageView)findViewById(R.id.iv_back) ;
		mLLConnectStatus.setOnClickListener(this) ;
		mIvBack.setOnClickListener(this) ;
		mLLMain.setVisibility(View.GONE) ;
		mLLContent.setVisibility(View.VISIBLE) ;
		mTabHost = this.getTabHost();
		mTabWidget = mTabHost.getTabWidget();  
		
		setupTab(DeviceDisplayActivity.class, "tab_device", R.string.antilost,R.drawable.ic_device_nomal);
		setupTab(CameraActivity.class, "tab_camera", R.string.camera,R.drawable.ic_camera_big_nomal);
		setupTab(DeviceLocationActivity.class, "tab_location", R.string.location,R.drawable.ic_location_nomal);
		
		//setupTab(MyLocationDemoActivity.class, "tab_location", R.string.location,R.drawable.ic_location_nomal);
		setupTab(DeviceSetActivity.class, "tab_record", R.string.setting,R.drawable.ic_set_press);
		
		mTabHost.setOnTabChangedListener(this);
		
		
		mTabWidget.getChildAt(1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AntilostCameraActivity.class);
				startActivity(intent);
			}
			
		});
	
	}
	
	private List<TextView> mTabTextViewList = new ArrayList<TextView>();
	
	private List<ImageView> mImageViewList = new ArrayList<ImageView>();
	

	private int[] mTabImageNomalList = {R.drawable.ic_device_nomal,
										R.drawable.ic_camera_big_nomal, 
										R.drawable.ic_location_nomal,
										R.drawable.ic_set_nomal};

	private int[] mTabImagePressList = {R.drawable.ic_device_selected,
										R.drawable.ic_camera_big_press, 
										R.drawable.ic_location_selected,
										R.drawable.ic_set_press};;
	
	private void setTabBackGroundSelect(int position) {
		for (int i = 0; i < mTabTextViewList.size(); i++) {
			if (i == position) {
				mImageViewList.get(i).setImageResource(mTabImagePressList[i]);
				mTabTextViewList.get(i).setTextColor(mContext.getResources().getColor(R.color.blue_light));
			} else {
				mImageViewList.get(i).setImageResource(mTabImageNomalList[i]);
				mTabTextViewList.get(i).setTextColor(mContext.getResources().getColor(R.color.grey));
			}
		}
	}
	
	protected NotificationManager mNotificationMnager ;
	
	@Override
	protected void onResume() {
		super.onResume();
		initViewScanStatus() ;
		
		NotificationBean bean = AppContext.mNotificationBean;
		if(bean != null && bean.isShowNotificationDialog()){
			Message msg = new Message() ;
			msg.what = 1 ;
			mNotificationMnager.cancel(bean.getNotificationID());
			bean.setShowNotificationDialog(false) ;
		}
		
	}
	
	private void initViewScanStatus(){
		if(AppContext.mBluetoothLeService != null && AppContext.mBluetoothLeService.isConnect()){
			mLLMain.setVisibility(View.VISIBLE) ;
    		mLLContent.setVisibility(View.GONE) ;
		}else{
			mLLMain.setVisibility(View.GONE) ;
    		mLLContent.setVisibility(View.VISIBLE) ;
		}
	}
	

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            	
            	displayGattServices(AppContext.mBluetoothLeService.getSupportedGattServices(),device.getAddress());
            	Log.e("liujw","###################### MainActivity=====displayGattServices");
            	mDelayHandler.removeCallbacks(runnable) ;
            	mDelayHandler.postDelayed(runnable, 500);
            }else if(Constant.VISIABLE.equals(action)){
            	mLLMain.setVisibility(View.VISIBLE) ;
        		mLLContent.setVisibility(View.GONE) ;
            }
            
        }
	};
	
	private Handler mDelayHandler = new Handler(){
			public void handleMessage(Message msg) {
				if (AppContext.mBluetoothLeService != null) {
					AppContext.SESSION_KEY = randomGenerate(4) ;
					Log.e("liujw","################AppContext.SESSION_KEY : "+AppContext.SESSION_KEY);
					String orgData = EncriptyUtils.encripty(Constant.AUTH_ID+AppContext.SESSION_KEY, Constant.Key) ;
					AppContext.mBluetoothLeService.sendMsg(mAddress,EncriptyUtils.zhuanHuan(orgData));
				}
			};
	};
	
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			mDelayHandler.sendEmptyMessage(0);
		}
	};
	
	private String mAddress ;
	
	private void displayGattServices(List<BluetoothGattService> gattServices,String address) {
		if (gattServices == null)
			return;
		for (BluetoothGattService gattService : gattServices) {
			if (gattService.getUuid().toString().startsWith("0000ffe0")) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					if (gattCharacteristic.getUuid().toString().startsWith("0000ffe1")) {
						AppContext.mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
					}else if(gattCharacteristic.getUuid().toString().startsWith("0000ffe2")){
						mAddress = address ;
					}
				}
			}
		}
		
		saveDatabaseAndStartActivity();
		mLLMain.setVisibility(View.VISIBLE) ;
		mLLContent.setVisibility(View.GONE) ;
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
			soundInfo.setRingVolume(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
			soundInfo.setShock(true);
			DatabaseManager.getInstance(mContext).insertDeviceInfo(mDevice.getAddress(), info);
			DatabaseManager.getInstance(mContext).insertDisurbInfo(mDevice.getAddress(), disturbInfo);
			DatabaseManager.getInstance(mContext).insertSoundInfo(mDevice.getAddress(), soundInfo);
		}
		
		if (mScanning) {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mScanning = false;
		}
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.iv_back:
			mHandler.removeCallbacks(mScanRunnable);
			mLLMain.setVisibility(View.VISIBLE) ;
			mLLContent.setVisibility(View.GONE) ;
			break;
		case R.id.ll_connect_status:
			mPbBar.setVisibility(View.VISIBLE);
			mTvScanneStatus.setText(mContext.getString(R.string.scanning)) ;
			scanLeDevice(true);
			break ;
		default:
			break;
		}
		
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		setTabBackGroundSelect(position);
	}


	@Override
	public void dismiss() {
		
	}
	
}

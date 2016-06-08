package com.cn.jmantiLost.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.DeviceSetInfo;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.service.BluetoothLeService;
import com.cn.jmantiLost.util.LocationUtils;
import com.cn.jmantiLost.util.MapUtility;
import com.cn.jmantiLost.view.FollowInfoDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DeviceLocationActivity extends FragmentActivity implements OnClickListener,
																		ConnectionCallbacks,
																		OnConnectionFailedListener, 
																		LocationListener{

	private DatabaseManager mDatabaseManager;

	private Context mContext;

	private ArrayList<DeviceSetInfo> mDeviceList = new ArrayList<DeviceSetInfo>();

	private TextView mTvPlace ;
	
	private TextView mTvTime ;
	
	private ImageView mIvLocate ;
	
	private RelativeLayout mRlDeviceInfo ;
	
	private GoogleMap mMap;

	private LocationClient mLocationClient;
	
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) 
			.setFastestInterval(16)
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	
	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				mMap.setMyLocationEnabled(false);
			}
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this,this); 
		}
	}
	
	private void initView(){
		
		mRlDeviceInfo = (RelativeLayout)findViewById(R.id.rl_device_info);
		mIvLocate = (ImageView)findViewById(R.id.iv_locate);
		mTvPlace = (TextView)findViewById(R.id.tv_place) ;
		mTvTime = (TextView)findViewById(R.id.tv_time) ;
		mIvLocate.setOnClickListener(this) ;
		mRlDeviceInfo.setVisibility(View.GONE);
		mRlDeviceInfo.setOnClickListener(this) ;
	}


	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSI);
		intentFilter.addAction(BluetoothLeService.ACTION_READ_DATA_AVAILABLE);
		return intentFilter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = DeviceLocationActivity.this;
		mDatabaseManager = DatabaseManager.getInstance(mContext);
		initDeviceList();
		setContentView(R.layout.activity_location);
		if (!LocationUtils.isOPen(mContext)) {
			
			FollowInfoDialog dialogLocation = new FollowInfoDialog(
					mContext, R.style.MyDialog, null,
					mContext.getString(R.string.open_gps), 1);
			dialogLocation.show();
		}
		
		initView() ;
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				initDeviceList() ;
				mRlDeviceInfo.setVisibility(View.VISIBLE) ;
				initDisconnectLocation() ;
			}
		}
	};
	
	private void initDeviceList() {
		mDeviceList = mDatabaseManager.selectDeviceInfoByLocation();
	}
	
	private Dialog mDialog ;
	
	@Override
	protected void onResume() {
		AppContext.isAlarm = true ;
		super.onResume();
		
		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
		if (result == ConnectionResult.SUCCESS) {
			setUpMapIfNeeded();
			setUpLocationClientIfNeeded();
			mLocationClient.connect();
			initDeviceList();
			

			if(AppContext.mBluetoothLeService != null && !AppContext.mBluetoothLeService.isConnect() && isExistLocationRecord()){
				initDisconnectLocation() ;
				mRlDeviceInfo.setVisibility(View.VISIBLE) ;
			}else{
				mMap.clear() ;
				initLocationMark();
				if(mMark != null){
					mMark.showInfoWindow();
				}
				mRlDeviceInfo.setVisibility(View.GONE) ;
			}
			
		} else {
			if(mDialog == null){
				mDialog = GooglePlayServicesUtil.getErrorDialog(result,DeviceLocationActivity.this, 255);				
			}
			mDialog.show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mGattUpdateReceiver) ;
	}
	
	private Marker mMark;
	
	
	public void getAddress(final LatLng latLonPoint) {
		
		Thread thrd = new Thread() {
			@Override
			public void run() {
				try {
					
					JSONObject jo = MapUtility.getLocationInfo(latLonPoint.latitude,latLonPoint.longitude);
					String address = MapUtility.getGeoAddress(jo);
					Message message = uiCallback.obtainMessage();
					message.obj = address;
					message.sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thrd.start();
	}
	
	private Handler uiCallback = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String pt = (String) msg.obj;
			if (pt != null) {
				mTvPlace.setText(pt) ;
			} else {
				mTvPlace.setText(mContext.getString(R.string.unkown_address)) ;
			}
		};
	};
	
	public String formatHourAndMinute(long time) {
		
		String timer ; 
		SimpleDateFormat formatHourMin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date duraTime = new Date(time);
		timer = formatHourMin.format(duraTime) ; 
		return timer;
		
	}
	
	private void initDisconnectLocation(){
		
		if(mDeviceList != null && mDeviceList.size() > 0){
			DeviceSetInfo deviceSetInfo = mDeviceList.get(0) ;
			getAddress(new LatLng(Double.valueOf(deviceSetInfo.getLat()), Double.valueOf(deviceSetInfo.getLng())));
			
			mTvTime.setText(formatHourAndMinute(deviceSetInfo.getTime())) ;
			double lat = Double.valueOf(deviceSetInfo.getLat()) ;
			double lng = Double.valueOf(deviceSetInfo.getLng()) ;
			
			mMark = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
					.position(new LatLng(lat,lng))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
					.draggable(true));

			CameraUpdate update = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lat,lng), 15, 30, 0));
			mMap.animateCamera(update, 500, null);

			mMark.showInfoWindow();
			
			mMap.addCircle(new CircleOptions().center(new LatLng(lat,lng))
					.radius(500)
					.strokeColor(Color.argb(255, 19, 167, 72))
					.fillColor(Color.argb(50, 203, 240, 143)).strokeWidth(1));
		}
		
	}


	private void initLocationMark() {
		
		if(mGoogleLocation == null){
			return ;
		}
		mMark = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(new LatLng(mGoogleLocation.getLatitude(),mGoogleLocation.getLongitude()))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
				.draggable(true));
		
		CameraUpdate update = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(mGoogleLocation.getLatitude(),mGoogleLocation.getLongitude()), 15, 30, 0));
		mMap.animateCamera(update, 500, null);
	}
	
	private void initLocationPlace(Location aLocation) {
		CameraUpdate update = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(aLocation.getLatitude(),aLocation.getLongitude()), 15, 30, 0));
		mMap.animateCamera(update, 500, null);
		//mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_locate :
			if(mGoogleLocation != null){
				initLocationPlace(mGoogleLocation) ;
			}
			
			break ;
		case R.id.rl_device_info :
			initDisconnectLocation() ;
			break ;
		}
	}

	
	private boolean isExistLocationRecord(){
		boolean ret = true ;
		if(mDeviceList == null || mDeviceList.size() == 0){
			ret = false ;
		}
		return ret ;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		
	}


	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(REQUEST, this);
	}


	@Override
	public void onDisconnected() {
		
	}

	private Location mGoogleLocation ;
	
	@Override
	public void onLocationChanged(Location location) {
		
		Log.e("DeviceLocationActivity","##################onLocationChanged");
		
		if (location != null) {
			
			mGoogleLocation = location ;
			if(AppContext.mBluetoothLeService != null && AppContext.mBluetoothLeService.isConnect()){
				mMap.clear() ;
				initLocationMark() ;
				mMark.showInfoWindow();
			}else if(!isExistLocationRecord()){
				mMap.clear() ;
				initLocationMark();
				mMark.showInfoWindow();
			}
		}
		
	}


}

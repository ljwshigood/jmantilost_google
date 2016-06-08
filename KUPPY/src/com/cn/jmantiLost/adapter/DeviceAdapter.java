package com.cn.jmantiLost.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.DeviceSetInfo;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.service.BluetoothLeService;
import com.cn.jmantiLost.util.AlarmManager;
import com.cn.jmantiLost.util.BlueGattManager;

public class DeviceAdapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<DeviceSetInfo> mDeviceList;

	private LayoutInflater mInflator;
	
	private Handler mHandler ;
	
	private AlarmManager mAlarmManager ;
	
	private DatabaseManager mDatabaseManager ;
	
	public DeviceAdapter(Context context, ArrayList<DeviceSetInfo> leDevices,
						 BluetoothLeService bleService,Handler handler) {
		this.mContext = context;
		this.mDeviceList = leDevices;
		mHandler = handler ;
		mInflator = LayoutInflater.from(context);
		mAlarmManager = AlarmManager.getInstance(context);
		mDatabaseManager = DatabaseManager.getInstance(context);
	}

	@Override
	public int getCount() {
		return mDeviceList == null ? 0 : mDeviceList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		final int tempPosition = position;
		final DeviceSetInfo  info = mDeviceList.get(position);
		if (convertView == null) {
			convertView = mInflator.inflate(R.layout.item_add_device, null);
			viewHolder = new ViewHolder();
			viewHolder.mIvSet = (ImageView) convertView
					.findViewById(R.id.ic_device_set);
			viewHolder.mCbAlarm = (CheckBox) convertView
					.findViewById(R.id.cb_switch);
			viewHolder.mTvDeviceInfo = (TextView) convertView
					.findViewById(R.id.tv_device_info);
			viewHolder.mIvDevice = (ImageView) convertView
					.findViewById(R.id.iv_device);
			viewHolder.mIvAlarm = (ImageView)convertView.findViewById(R.id.iv_alarm);
			viewHolder.mPbbar = (ProgressBar)convertView.findViewById(R.id.pb_connect_device);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(info.isActive()){ //已经激活
			viewHolder.mCbAlarm.setChecked(true);
			if(info.isConnected()){
				viewHolder.mIvAlarm.setBackgroundResource(R.drawable.ic_alarm_enable);
			}else{
				viewHolder.mIvAlarm.setBackgroundResource(R.drawable.ic_alarm_enable);
			}
		}else if(!info.isActive()){
			viewHolder.mCbAlarm.setChecked(false);
			viewHolder.mIvAlarm.setBackgroundResource(R.drawable.ic_alarm_enable);
		}
		
		if(info.isVisible()){
			viewHolder.mPbbar.setVisibility(View.VISIBLE);
		}else{
			viewHolder.mPbbar.setVisibility(View.GONE);
		}
		viewHolder.mTvDeviceInfo.setText(info.getmDeviceName());
		viewHolder.mIvAlarm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(info.isActive() && info.isConnected()){
				}
				
			}
		});
		
		viewHolder.mIvSet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			/*	Intent intent = new Intent(mContext, DeviceSetActivity.class);
				String address = mDeviceList.get(tempPosition).getmDeviceAddress();
				intent.putExtra("address", address);
				mContext.startActivity(intent);*/
			}
		});
		viewHolder.mCbAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (viewHolder.mCbAlarm.isChecked()) {
					boolean isConnect = BlueGattManager.iteratorGattHashMap(AppContext.mHashMapConnectGatt, info.getmDeviceAddress());
					//没有连接，开始搜索设备连接
					if(!isConnect){
						info.setActive(true);
						info.setVisible(true);
						info.setConnected(false);
						Message msg = new Message();
						msg.what = 0 ;
						msg.obj= info ;
						mHandler.sendMessage(msg);
					}else{
						//已经连接了，直接按钮变色
						info.setActive(true);
						info.setConnected(true);
						viewHolder.mIvAlarm.setBackgroundResource(R.drawable.ic_alarm_enable);
					}
				}else{
					info.setActive(false);
					info.setVisible(false);
					info.setConnected(false);
					viewHolder.mIvAlarm.setBackgroundResource(R.drawable.ic_alarm_enable);
					mDatabaseManager.updateDeviceInfoActivie(viewHolder.mCbAlarm.isChecked(),info.getmDeviceAddress());
					if(AppContext.mBluetoothLeService != null){
						AppContext.mBluetoothLeService.close();
					}
					BlueGattManager.removeGattHashMap(AppContext.mHashMapConnectGatt,AppContext.mDeviceAddress);
				}
				notifyDataSetChanged();
			}
		});
		
		viewHolder.mTvDeviceInfo.setText(mDeviceList.get(position).getmDeviceName());
		return convertView;
	}

	static class ViewHolder {
		ImageView mIvSet;
		CheckBox mCbAlarm;
		ImageView mIvDevice;
		TextView mTvDeviceInfo;
		ImageView  mIvAlarm ;
		ProgressBar mPbbar ;
		
	}

	public void notifyDeviceDataSetChange(ArrayList<DeviceSetInfo> list){
		this.mDeviceList = list;
		notifyDataSetChanged();
	}
	
}

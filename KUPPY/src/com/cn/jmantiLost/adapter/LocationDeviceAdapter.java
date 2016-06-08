package com.cn.jmantiLost.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.bean.DeviceSetInfo;
import com.cn.jmantiLost.util.AlarmManager;

public class LocationDeviceAdapter  extends BaseAdapter{
	private Context mContext;

	private HashMap<String, BluetoothGatt> mDeviceGattList ;
	
	private ArrayList<BluetoothGatt>  _gattList = new ArrayList<BluetoothGatt>();
 	
	private LayoutInflater mInflator;
			
	private ArrayList<DeviceSetInfo> mDeviceList ;
	
	private AlarmManager mAlarmManager ;
	
	public LocationDeviceAdapter(Context context, ArrayList<DeviceSetInfo> leDevices) {
		this.mContext = context;
		this.mDeviceList = leDevices;
		mInflator = LayoutInflater.from(context);
		mAlarmManager = AlarmManager.getInstance(mContext);
	}
	
	private void generationBleList() {
		Iterator iter = mDeviceGattList.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			_gattList.add((BluetoothGatt)val);
		}
	}
	@Override
	public int getCount() {
		return mDeviceList == null ? 0 : mDeviceList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDeviceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		DeviceSetInfo info = mDeviceList.get(position);
		if (convertView == null) {
			convertView = mInflator.inflate(R.layout.list_item_find, null);
			viewHolder = new ViewHolder();
			viewHolder.iv_device = (ImageView) convertView.findViewById(R.id.iv_device);
			viewHolder.tv_device_info = (TextView) convertView.findViewById(R.id.tv_device_info);
					
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.iv_device.setImageBitmap(mAlarmManager.getDeviceBitmap(info, mContext));
		viewHolder.tv_device_info.setText(mDeviceList.get(position).getmDeviceName());
		return convertView;
	}
	
	public Bitmap getRes(String name) {
		ApplicationInfo appInfo = mContext.getApplicationInfo();
		int resID = mContext.getResources().getIdentifier(name, "drawable",
				appInfo.packageName);
		return BitmapFactory.decodeResource(mContext.getResources(), resID);
	}
	static class ViewHolder {
		ImageView iv_device ;
		TextView tv_device_info;
	}
}

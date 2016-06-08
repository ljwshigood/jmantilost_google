package com.cn.jmantiLost.util;


import java.util.Iterator;
import java.util.Map;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.cn.jmantiLost.application.AppContext;

public class BlueGattManager {
	
	public static String getBlueGattMac(Map map, int column) {
		Iterator iter = map.entrySet().iterator();
		int _column = 0;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			_column++;
			if (_column == column) {
				Object key = entry.getKey();
				return key.toString();
			}
		}
		return null;
	}
	
	public static boolean iteratorGattHashMap(Map map, String address) {
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (key.toString().equals(address)) {
				boolean flag = ((BluetoothGatt)val).connect();
				Log.e("liujw","#################(BluetoothGatt)val).connect()) : "+flag);
				if(!((BluetoothGatt)val).connect()){
					AppContext.mHashMapConnectGatt.remove(key);
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	public static BluetoothGatt getBlueGattHashMap(Map map, String address) {
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (String.valueOf(key).equals(address)) {
				return (BluetoothGatt)val;
			}
		}
		return null;
	}
	
	public static void removeGattHashMap(Map map, String address) {
		
		Object object = null ;
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (key.toString().equals(address)) {
				object = key ;
				break ;
			}
		}
		if(object != null){
			map.remove(object);
		}
	}
	

}

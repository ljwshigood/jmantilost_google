package com.cn.jmantiLost.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cn.jmantiLost.bean.AppInfo;
import com.cn.jmantiLost.bean.CameraInfo;
import com.cn.jmantiLost.bean.ContactBean;
import com.cn.jmantiLost.bean.DeviceSetInfo;
import com.cn.jmantiLost.bean.DisturbInfo;
import com.cn.jmantiLost.bean.KeySetBean;
import com.cn.jmantiLost.bean.SosBean;
import com.cn.jmantiLost.bean.SoundInfo;

public class DatabaseManager {

	private Context mContext;

	private static DatabaseManager mInstance;

	private DBHelper mDBHelp = null;

	private SQLiteDatabase mSQLiteDatabase;

	private DatabaseManager(Context context) {
		this.mContext = context;
		mDBHelp = new DBHelper(context);
		mSQLiteDatabase = mDBHelp.getWritableDatabase();
	}

	public static DatabaseManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DatabaseManager(context);
		}
		return mInstance;
	}

	public SosBean selectSOSInfo() {
		String sql = "select * from sos";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		SosBean bean = null;
		while (cursor != null && cursor.moveToNext()) {
			bean = new SosBean();
			bean.setContact(cursor.getString(cursor.getColumnIndex("number")));
			bean.setMessage(cursor.getString(cursor.getColumnIndex("message")));
		}
		return bean;
	}

	public void updateKeySet(int count) {
		deleteKeySet(count);
		switch (count) {
		case 2:
			String insert_two = "insert into key_set values(null,null,null,2,0,-1)";
			mSQLiteDatabase.execSQL(insert_two);
			break;
		case 3:
			String insert_three = "insert into key_set values(null,null,null,3,0,-1)";
			mSQLiteDatabase.execSQL(insert_three);
			break;
		case 4:
			String insert_four = "insert into key_set values(null,null,null,4,0,-1)";
			mSQLiteDatabase.execSQL(insert_four);
			break;
		default:
			break;
		}
	}

	public void insertSOSInfo(String phoneName, String message) {
		String deleteSQL = "delete from sos";
		String insertQL = "insert into sos values(null ,\"%s\",\"%s\")";
		insertQL = String.format(insertQL, phoneName, message);
		mSQLiteDatabase.execSQL(deleteSQL);
		mSQLiteDatabase.execSQL(insertQL);
	}

	public void insertAppInfo(AppInfo info) {
		String deleteSQL = "delete from app";
		String insertQL = "insert into app values(null ,\"%s\",\"%s\")";
		insertQL = String.format(insertQL, info.getPackageName(),
				info.getAppName());
		mSQLiteDatabase.execSQL(deleteSQL);
		mSQLiteDatabase.execSQL(insertQL);
	}

	public AppInfo selectAppInfo() {
		AppInfo info = new AppInfo();
		String sql = "select * from app";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		while (cursor != null && cursor.moveToNext()) {
			info.setPackageName(cursor.getString(cursor
					.getColumnIndex("package_name")));
			info.setAppName(cursor.getString(cursor.getColumnIndex("app_name")));
		}
		return info;
	}

	public void insertContact(String contactName, String phoneNumber) {
		String deleteSQL = "delete from contact";
		String insertSQL = "insert into contact values(null,\"%s\",\"%s\")";
		insertSQL = String.format(insertSQL, contactName, phoneNumber);
		mSQLiteDatabase.execSQL(deleteSQL);
		mSQLiteDatabase.execSQL(insertSQL);
	}

	public ContactBean selectContact() {
		String sql = "select * from contact";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		ContactBean bean = new ContactBean();
		while (cursor != null && cursor.moveToNext()) {
			bean.setContact(cursor.getString(cursor.getColumnIndex("name")));
			bean.setNumber(cursor.getString(cursor.getColumnIndex("number")));
		}
		return bean;
	}

	public void insertCameraInfo(CameraInfo info) {
		String deleteSQL = "delete from camera";
		String insertQL = "insert into camera values(null ,%d,%d)";
		insertQL = String.format(insertQL, info.getFront(), info.getFocus());
		mSQLiteDatabase.execSQL(deleteSQL);
		mSQLiteDatabase.execSQL(insertQL);
	}

	public CameraInfo selectCameraInfo() {
		CameraInfo info = new CameraInfo();
		String sql = "select * from camera";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		while (cursor != null && cursor.moveToNext()) {
			info.setFocus(cursor.getInt(cursor.getColumnIndex("isFocus")));
			info.setFront(cursor.getInt(cursor.getColumnIndex("isFront")));
		}
		return info;
	}

	public void insertKeySet(KeySetBean bean) {
		String insertSQL = "insert into key_set values(null,\"%s\",\"%s\",%d,%d,%d)";
		insertSQL = String.format(insertSQL, bean.getBitmapString(),
				bean.getKeySetDetail(), bean.getCount(), bean.getType(),
				bean.getAction());
		mSQLiteDatabase.execSQL(insertSQL);
	}

	public void insertAntiContact(String contactName, String number) {
		String deleteSQL = "delete from anti_contact";
		String insertSQL = "insert into anti_contact values(null,\"%s\",\"%s\")";
		insertSQL = String.format(insertSQL, contactName, number);
		mSQLiteDatabase.execSQL(deleteSQL);
		mSQLiteDatabase.execSQL(insertSQL);
	}

	public ContactBean selectAntiContact() {
		String sql = "select * from anti_contact";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		ContactBean bean = new ContactBean();
		while (cursor != null && cursor.moveToNext()) {
			bean.setContact(cursor.getString(cursor.getColumnIndex("name")));
			bean.setNumber(cursor.getString(cursor.getColumnIndex("number")));
		}
		return bean;
	}

	public void editorKeySet(KeySetBean bean) {
		if (!existKeySet(bean.getCount())) {
			insertKeySet(bean);
		} else {
			updateKeySet(bean);
		}
	}

	private boolean existKeySet(int count) {
		boolean isExist = false;
		String sql = "select * from key_set where count = %d";
		sql = String.format(sql, count);
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if (cursor != null && cursor.getCount() > 0) {
			isExist = true;
		}
		return isExist;
	}

	public void updateKeySet(KeySetBean bean) {
		String updateSQL = "update key_set set bitmap = \"%s\",detail = \"%s\" ,type = %d ,action = %d where count = %d";
		updateSQL = String.format(updateSQL, bean.getBitmapString(),
				bean.getKeySetDetail(), bean.getType(), bean.getAction(),
				bean.getCount());
		mSQLiteDatabase.execSQL(updateSQL);
	}

	public void deleteKeySet(int count) {
		String sql = "delete from key_set where count = %d";
		sql = String.format(sql, count);
		mSQLiteDatabase.execSQL(sql);
	}

	public List<KeySetBean> selectKeySet() {
		List<KeySetBean> list = new ArrayList<KeySetBean>();
		String sql = "select * from key_set";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		while (cursor != null && cursor.moveToNext()) {
			KeySetBean bean = new KeySetBean();
			bean.setBitmapString(cursor.getString(cursor
					.getColumnIndex("bitmap")));
			bean.setCount(cursor.getInt(cursor.getColumnIndex("count")));
			bean.setKeySetDetail(cursor.getString(cursor
					.getColumnIndex("detail")));
			bean.setAction(cursor.getInt(cursor.getColumnIndex("action")));
			bean.setType(cursor.getInt(cursor.getColumnIndex("type")));
			bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
			list.add(bean);
		}
		return list;

	}

	public void deleteKeySetById(KeySetBean bean) {
		String deleteSQL = "delete from key_set where id = %d";
		deleteSQL = String.format(deleteSQL, bean.getId());
		mSQLiteDatabase.execSQL(deleteSQL);
	}

	public KeySetBean selectKeySetByCount(int count) {
		String sql = "select * from key_set where count = %d";
		sql = String.format(sql, count);
		KeySetBean bean = new KeySetBean();
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		while (cursor != null && cursor.moveToNext()) {
			bean.setAction(cursor.getInt(cursor.getColumnIndex("action")));
		}
		return bean;
	}

	public void updateDeviceInfoActivie(boolean isActive, String address) {
		int flag = isActive == true ? 1 : 0;
		String sql = "update  device_set set deviceIsActive = %d where unique_id = \"%s\" ";
		sql = String.format(sql, flag, address);
		mSQLiteDatabase.execSQL(sql);
	}

	public ArrayList<DeviceSetInfo> selectDeviceInfoByLocation() {
		ArrayList<DeviceSetInfo> deviceInfoList = null;
		String sql = "select * from device_set";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			deviceInfoList = new ArrayList<DeviceSetInfo>();
			while (cursor.moveToNext()) {
				DeviceSetInfo deviceInfo = new DeviceSetInfo();
				deviceInfo.setDeviceId(cursor.getString(cursor.getColumnIndexOrThrow("unique_id")));
				deviceInfo.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow("imagepath")));
				deviceInfo.setDistanceType(cursor.getInt(cursor.getColumnIndexOrThrow("distance")));
				boolean isDisturb = cursor.getInt(cursor.getColumnIndexOrThrow("isdisturb")) == 1 ? true
						: false;
				boolean isLocation = cursor.getInt(cursor
						.getColumnIndexOrThrow("islocation")) == 1 ? true
						: false;
				boolean isActive = cursor.getInt(cursor
						.getColumnIndexOrThrow("deviceIsActive")) == 1 ? true
						: false;
				deviceInfo.setmDeviceAddress(cursor.getString(cursor
						.getColumnIndexOrThrow("unique_id")));
				deviceInfo.setmDeviceName(cursor.getString(cursor
						.getColumnIndexOrThrow("deviceName")));
				deviceInfo.setDisturb(isDisturb);
				deviceInfo.setLat(cursor.getString(cursor
						.getColumnIndexOrThrow("latitude")));
				deviceInfo.setLng(cursor.getString(cursor
						.getColumnIndexOrThrow("longitude")));
				deviceInfo.setTime(cursor.getLong(cursor.getColumnIndexOrThrow("anti_time"))) ;
				deviceInfo.setLocation(isLocation);
				deviceInfo.setActive(isActive);
				deviceInfoList.add(deviceInfo);
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
		return deviceInfoList;
	}
	
	public DeviceSetInfo selectSingleDeviceInfo() {
		DeviceSetInfo deviceInfo = null ;
		String sql = "select * from device_set";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				deviceInfo = new DeviceSetInfo();
				deviceInfo.setmDeviceName(cursor.getString(cursor
						.getColumnIndexOrThrow("deviceName")));
				deviceInfo.setmDeviceAddress(cursor.getString(cursor
						.getColumnIndexOrThrow("deviceAddress")));
				deviceInfo.setDeviceId(cursor.getString(cursor
						.getColumnIndexOrThrow("unique_id")));
				deviceInfo.setFilePath(cursor.getString(cursor
						.getColumnIndexOrThrow("imagepath")));
				deviceInfo.setDistanceType(cursor.getInt(cursor
						.getColumnIndexOrThrow("distance")));
				boolean isDisturb = cursor.getInt(cursor
						.getColumnIndexOrThrow("isdisturb")) == 1 ? true
						: false;
				boolean isLocation = cursor.getInt(cursor
						.getColumnIndexOrThrow("islocation")) == 1 ? true
						: false;
				boolean isActivite = cursor.getInt(cursor
						.getColumnIndexOrThrow("deviceIsActive")) == 1 ? true
						: false;
				deviceInfo.setDisturb(isDisturb);
				deviceInfo.setActive(isActivite);
				deviceInfo.setLocation(isLocation);
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
		return deviceInfo ;
	}

	public ArrayList<DeviceSetInfo> selectDeviceInfo(String deviceID) {
		ArrayList<DeviceSetInfo> deviceInfoList = null;
		String sql = "select * from device_set where unique_id = \"%s\" ";
		sql = String.format(sql, deviceID);
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			deviceInfoList = new ArrayList<DeviceSetInfo>();
			while (cursor.moveToNext()) {
				DeviceSetInfo deviceInfo = new DeviceSetInfo();
				deviceInfo.setmDeviceName(cursor.getString(cursor
						.getColumnIndexOrThrow("deviceName")));
				deviceInfo.setmDeviceAddress(cursor.getString(cursor
						.getColumnIndexOrThrow("deviceAddress")));
				deviceInfo.setDeviceId(cursor.getString(cursor
						.getColumnIndexOrThrow("unique_id")));
				deviceInfo.setFilePath(cursor.getString(cursor
						.getColumnIndexOrThrow("imagepath")));
				deviceInfo.setDistanceType(cursor.getInt(cursor
						.getColumnIndexOrThrow("distance")));
				boolean isDisturb = cursor.getInt(cursor
						.getColumnIndexOrThrow("isdisturb")) == 1 ? true
						: false;
				boolean isLocation = cursor.getInt(cursor
						.getColumnIndexOrThrow("islocation")) == 1 ? true
						: false;
				boolean isActivite = cursor.getInt(cursor
						.getColumnIndexOrThrow("deviceIsActive")) == 1 ? true
						: false;
				deviceInfo.setDisturb(isDisturb);
				deviceInfo.setActive(isActivite);
				deviceInfo.setLocation(isLocation);
				deviceInfoList.add(deviceInfo);
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
		return deviceInfoList;
	}

	public ArrayList<DeviceSetInfo> selectDeviceInfo() {
		ArrayList<DeviceSetInfo> deviceInfoList = null;
		String sql = "select * from device_set";
		sql = String.format(sql);
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			deviceInfoList = new ArrayList<DeviceSetInfo>();
			while (cursor.moveToNext()) {
				DeviceSetInfo deviceInfo = new DeviceSetInfo();
				deviceInfo.setDeviceId(cursor.getString(cursor
						.getColumnIndexOrThrow("unique_id")));
				deviceInfo.setFilePath(cursor.getString(cursor
						.getColumnIndexOrThrow("imagepath")));
				deviceInfo.setDistanceType(cursor.getInt(cursor
						.getColumnIndexOrThrow("distance")));
				boolean isDisturb = cursor.getInt(cursor
						.getColumnIndexOrThrow("isdisturb")) == 1 ? true
						: false;
				boolean isLocation = cursor.getInt(cursor
						.getColumnIndexOrThrow("islocation")) == 1 ? true
						: false;
				boolean isActive = cursor.getInt(cursor
						.getColumnIndexOrThrow("deviceIsActive")) == 1 ? true
						: false;
				deviceInfo.setmDeviceAddress(cursor.getString(cursor
						.getColumnIndexOrThrow("unique_id")));
				deviceInfo.setmDeviceName(cursor.getString(cursor
						.getColumnIndexOrThrow("deviceName")));
				deviceInfo.setDisturb(isDisturb);
				deviceInfo.setLocation(isLocation);
				deviceInfo.setActive(isActive); // 获取激活状态
				if (isActive) {
					deviceInfo.setVisible(true);// 默认要显示
				} else {
					deviceInfo.setVisible(false);// 默认要显示
				}

				deviceInfoList.add(deviceInfo);
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
		return deviceInfoList;
	}

	public ArrayList<DisturbInfo> selectDisturbInfo(String deviceID) {
		ArrayList<DisturbInfo> disturbInfoList = null;
		String sql = "select * from device_disturb where unique_id = \"%s\"";
		sql = String.format(sql, deviceID);
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			disturbInfoList = new ArrayList<DisturbInfo>();
			while (cursor.moveToNext()) {
				DisturbInfo disturbInfo = new DisturbInfo();
				disturbInfo.setDeviceID(cursor.getString(cursor
						.getColumnIndexOrThrow("unique_id")));
				disturbInfo.setStartTime(cursor.getString(cursor
						.getColumnIndexOrThrow("disturb_start")));
				disturbInfo.setEndTime(cursor.getString(cursor
						.getColumnIndexOrThrow("disturb_end")));
				boolean isDisturb = cursor.getInt(cursor
						.getColumnIndexOrThrow("isdisturb")) == 1 ? true
						: false;

				disturbInfo.setDisturb(isDisturb);
				disturbInfoList.add(disturbInfo);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
		return disturbInfoList;

	}

	public ArrayList<SoundInfo> selectSoundInfo(String deviceID) {
		ArrayList<SoundInfo> soundInfoList = null;
		String sql = "select * from device_ring,device_disturb where device_ring.unique_id = \"%s\"";
		sql = String.format(sql, deviceID);
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			soundInfoList = new ArrayList<SoundInfo>();
			while (cursor.moveToNext()) {
				SoundInfo soundInfo = new SoundInfo();
				soundInfo.setDeviceID(cursor.getString(cursor
						.getColumnIndexOrThrow("unique_id")));
				soundInfo.setRingId(cursor.getInt(cursor
						.getColumnIndexOrThrow("ring_id")));
				soundInfo.setRingVolume(cursor.getInt(cursor
						.getColumnIndexOrThrow("ringvolume")));
				soundInfo.setDurationTime(cursor.getDouble(cursor
						.getColumnIndexOrThrow("duration_time")));
				boolean isShock = cursor.getInt(cursor
						.getColumnIndexOrThrow("isshock")) == 1 ? true : false;

				soundInfo.setShock(isShock);
				soundInfoList.add(soundInfo);
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
		return soundInfoList;
	}

	public void updateDeviceLatLogDisconnect(String lat,String lng,String deviceID,long currentTime) {
		String sql = "update device_set set latitude = \"%s\",longitude =\"%s\", anti_time = %d ,connect = 0 where unique_id = \"%s\" and islocation = 1";
		sql = String.format(sql, lat, lng,currentTime,deviceID);
		Log.e("liujw", "####################sql " + sql);
		mSQLiteDatabase.execSQL(sql);
	}
	
	public void updateDeviceConnect(String deviceID) {
		String sql = "update device_set set connect = 1 where unique_id = \"%s\"";
		sql = String.format(sql, deviceID);
		Log.e("liujw", "####################sql " + sql);
		mSQLiteDatabase.execSQL(sql);
	}
	
	
	public void updateDeviceLatLogIsConnect(String lat,String lng) {
		String sql = "update device_set set latitude = \"%s\",longitude =\"%s\" where islocation = 1 and connect = 1";
		sql = String.format(sql, lat, lng);
		Log.e("liujw", "####################sql " + sql);
		mSQLiteDatabase.execSQL(sql);
	}
	
	public void updateDeviceInfo(String deviceID, DeviceSetInfo info) {
		String sql = "update device_set set imagepath = \"%s\",isdisturb = %d,"
				+ "distance = %d ,islocation= %d , deviceName = \"%s\" where unique_id = \"%s\"";
		int isDisturb = info.isDisturb() == true ? 1 : 0;

		Log.e("liujw", "###############isDisturb " + isDisturb);
		int isLocation = info.isLocation() == true ? 1 : 0;
		Log.e("liujw", "###############isLocation " + isLocation);
		sql = String.format(sql, info.getFilePath(), isDisturb,
				info.getDistanceType(), isLocation, info.getmDeviceName(),
				deviceID);

		Log.e("liujw", "####################sql " + sql);
		mSQLiteDatabase.execSQL(sql);
	}

	public void updateDeviceActiveStatus(String deviceID, DeviceSetInfo info) {
		String sql = "update device_set set  deviceIsActive = 1 where unique_id = \"%s\" ";
		sql = String.format(sql, deviceID);
		mSQLiteDatabase.execSQL(sql);
	}

	public void updateDisturbInfo(String deviceID, DisturbInfo info) {
		String sql = "update device_disturb set isdisturb =%d,disturb_start= \"%s\", disturb_end = \"%s\" "
				+ "where unique_id = \"%s\"";
		int isDisturb = info.isDisturb() == true ? 1 : 0;
		sql = String.format(sql, isDisturb, info.getStartTime(),
				info.getEndTime(), deviceID);
		mSQLiteDatabase.execSQL(sql);
	}
	
	public void updateDisturbInfo(DisturbInfo info) {
		String sql = "update device_set set isdisturb =%d ";
		int isDisturb = info.isDisturb() == true ? 1 : 0;
		sql = String.format(sql, isDisturb);
		mSQLiteDatabase.execSQL(sql);
	}

	public void updateDisturbIsDisturbInfo(String deviceID, DisturbInfo info) {
		String sql = "update device_disturb set isdisturb =%d "
				+ "where unique_id = \"%s\"";
		int isDisturb = info.isDisturb() == true ? 1 : 0;
		sql = String.format(sql, isDisturb, deviceID);
		mSQLiteDatabase.execSQL(sql);
	}

	public void updateSoundInfo(String deviceID, SoundInfo info) {
		String sql = "update device_ring set isshock = %d ,ring_id = %d ,"
				+ "ringvolume = %d,duration_time = \"%s\" "
				+ "where unique_id = \"%s\"";

		int isShock = info.isShock() == true ? 1 : 0;
		sql = String.format(sql, isShock, info.getRingId(),
				info.getRingVolume(), info.getDurationTime(), deviceID);
		mSQLiteDatabase.execSQL(sql);
	}

	public void updateSoundInfoId(String deviceID, int id) {
		String sql = "update device_ring set ring_id = %d  "
				+ "where unique_id = \"%s\"";

		sql = String.format(sql, id, deviceID);
		mSQLiteDatabase.execSQL(sql);

	}

	public void insertDeviceInfo(String deviceID, DeviceSetInfo info) {
		String sql = "insert into device_set values(null,\"%s\",\"%s\",%d,%d,\"%s\",\"%s\",%d,%d,null,null,%d,1)";
		int isDisturb = info.isDisturb() == true ? 1 : 0;
		int isLocation = info.isLocation() == true ? 1 : 0;
		int isActivie = info.isActive() == true ? 1 : 0;
		sql = String.format(sql, deviceID, info.getFilePath(), isDisturb,
				info.getDistanceType(), info.getmDeviceName(),
				info.getmDeviceAddress(), isActivie, isLocation,System.currentTimeMillis());
		mSQLiteDatabase.execSQL(sql);
	}

	public boolean isExistDeviceInfo(String deviceId) {
		boolean ret = false;
		String sql = "select * from device_set where unique_id = \"%s\" ";
		sql = String.format(sql, deviceId);
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if (cursor != null && cursor.moveToNext()) {
			ret = true;
		}
		return ret;
	}

	public void insertDisurbInfo(String deviceID, DisturbInfo info) {
		String sql = "insert into device_disturb values(null,\"%s\",%d,\"%s\",\"%s\")";
		int isDisturb = info.isDisturb() == true ? 1 : 0;
		sql = String.format(sql, deviceID, isDisturb, info.getStartTime(),
				info.getEndTime());
		mSQLiteDatabase.execSQL(sql);
	}

	public boolean isExistDisturbInfo(String deviceId) {
		boolean ret = false;
		String sql = "select * from device_disturb where unique_id = \"%s\" ";
		sql = String.format(sql, deviceId);
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if (cursor != null && cursor.moveToNext()) {
			ret = true;
		}
		return ret;
	}

	public void insertSoundInfo(String deviceID, SoundInfo info) {
		String sql = "insert into device_ring values(null,\"%s\",%d,%d,%d,\"%s\")";
		int isShock = info.isShock() == true ? 1 : 0;
		sql = String.format(sql, deviceID, isShock, info.getRingId(),
				info.getRingVolume(), info.getDurationTime());
		mSQLiteDatabase.execSQL(sql);
	}

	public boolean isExistSoundInfo(String deviceId) {
		boolean ret = false;
		String sql = "select * from device_ring where unique_id = \"%s\" ";
		sql = String.format(sql, deviceId);
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if (cursor != null && cursor.moveToNext()) {
			ret = true;
		}
		return ret;
	}

	public void deleteAllDeviceInfo(String deviceID) {
		String sql1 = "delete from device_disturb where unique_id = \"%s\"";
		String sql2 = "delete from device_ring where unique_id = \"%s\"";
		String sql3 = "delete from device_set where unique_id = \"%s\"";
		sql1 = String.format(sql1, deviceID);
		sql2 = String.format(sql2, deviceID);
		sql3 = String.format(sql3, deviceID);
		mSQLiteDatabase.execSQL(sql1);
		mSQLiteDatabase.execSQL(sql2);
		mSQLiteDatabase.execSQL(sql3);
	}
	
	public void deleteAllDeviceInfo() {
		String sql1 = "delete from device_disturb ";
		String sql2 = "delete from device_ring ";
		String sql3 = "delete from device_set ";
		mSQLiteDatabase.execSQL(sql1);
		mSQLiteDatabase.execSQL(sql2);
		mSQLiteDatabase.execSQL(sql3);
	}

}

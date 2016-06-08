package com.cn.jmantiLost.bean;

import java.io.Serializable;

public class DeviceSetInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String deviceId ;

	private String filePath;
	
	private long time ;
	
	//private double distance;
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	private int distanceType ;
	
	private String lat ;
	
	private String lng ;
	
	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	private boolean isLocation;
	
	private boolean isDisturb ;
	
	private String mDeviceName ;
	
	private String mDeviceAddress ;
	
	private boolean isActive ;
	
	private boolean isConnected ;
	
	private boolean isVisible ;
	
	public int getDistanceType() {
		return distanceType;
	}

	public void setDistanceType(int distanceType) {
		this.distanceType = distanceType;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getmDeviceName() {
		return mDeviceName;
	}

	public void setmDeviceName(String mDeviceName) {
		this.mDeviceName = mDeviceName;
	}

	public String getmDeviceAddress() {
		return mDeviceAddress;
	}

	public void setmDeviceAddress(String mDeviceAddress) {
		this.mDeviceAddress = mDeviceAddress;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isLocation() {
		return isLocation;
	}

	public void setLocation(boolean isLocation) {
		this.isLocation = isLocation;
	}

	public boolean isDisturb() {
		return isDisturb;
	}

	public void setDisturb(boolean isDisturb) {
		this.isDisturb = isDisturb;
	}

}

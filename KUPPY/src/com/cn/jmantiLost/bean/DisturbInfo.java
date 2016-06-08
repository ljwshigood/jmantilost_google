package com.cn.jmantiLost.bean;

public class DisturbInfo {
	
	private String deviceID ;
	
	private boolean isDisturb ;
	
	private String startTime ;
	
	private String endTime ;
	
	
	public boolean isDisturb() {
		return isDisturb;
	}

	public void setDisturb(boolean isDisturb) {
		this.isDisturb = isDisturb;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
}

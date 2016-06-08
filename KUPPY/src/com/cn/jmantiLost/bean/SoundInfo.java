package com.cn.jmantiLost.bean;
public class SoundInfo {

	private String deviceID ;
	
	private boolean isShock;

	private int ringId;

	private int ringVolume;
	
	private String ringName ;

	private double durationTime;
	
	public String getRingName() {
		return ringName;
	}

	public void setRingName(String ringName) {
		this.ringName = ringName;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	
	public boolean isShock() {
		return isShock;
	}

	public void setShock(boolean isShock) {
		this.isShock = isShock;
	}

	public int getRingId() {
		return ringId;
	}

	public void setRingId(int ringId) {
		this.ringId = ringId;
	}

	public int getRingVolume() {
		return ringVolume;
	}

	public void setRingVolume(int ringVolume) {
		this.ringVolume = ringVolume;
	}

	public double getDurationTime() {
		return durationTime;
	}

	public void setDurationTime(double durationTime) {
		this.durationTime = durationTime;
	}
}

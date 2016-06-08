package com.cn.jmantiLost.bean;

public class NotificationBean {

	private String address ;
	
	private boolean isShowNotificationDialog ;
	
	private String alarmInfo ;
	
	private int alarmType ;
	
	private int notificationID ;
	
	public int getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}

	public int getNotificationID() {
		return notificationID;
	}

	public void setNotificationID(int notificationID) {
		this.notificationID = notificationID;
	}

	public String getAlarmInfo() {
		return alarmInfo;
	}

	public void setAlarmInfo(String alarmInfo) {
		this.alarmInfo = alarmInfo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isShowNotificationDialog() {
		return isShowNotificationDialog;
	}

	public void setShowNotificationDialog(boolean isShowNotificationDialog) {
		this.isShowNotificationDialog = isShowNotificationDialog;
	}
	
	
	
}

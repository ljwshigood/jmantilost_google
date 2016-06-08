package com.cn.jmantiLost.bean;

import java.io.Serializable;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String appName = "";
	public String packageName = "";
	public String versionName = "";
	public int versionCode = 0;
	public Drawable appIcon = null;
	
	public boolean isSelect ;
	
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		Log.e("liujw","##########################position : "+isSelect);
		this.isSelect = isSelect;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	
}

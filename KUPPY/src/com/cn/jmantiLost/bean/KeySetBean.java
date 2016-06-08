package com.cn.jmantiLost.bean;

import java.io.Serializable;

import android.graphics.Bitmap;

public class KeySetBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String keySetDetail ;
	
	private String bitmapString ;
	
	private int count ;
	
	private int type ;
	
	private int action = -1;
	
	private int id ;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getBitmapString() {
		return bitmapString;
	}

	public void setBitmapString(String bitmapString) {
		this.bitmapString = bitmapString;
	}

	public String getKeySetDetail() {
		return keySetDetail;
	}

	public void setKeySetDetail(String keySetDetail) {
		this.keySetDetail = keySetDetail;
	}


	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}

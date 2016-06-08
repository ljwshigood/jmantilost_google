package com.cn.jmantiLost.bean;


public class alarmInfo {

	private int res ;
	private String name ;
	private int position ;
	
	private boolean isSelect ;
	
	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public alarmInfo(int res, String name, int position,boolean isSelect) {
		super();
		this.res = res;
		this.name = name;
		this.position = position;
		this.isSelect= isSelect;
	}

	public int getRes() {
		return res;
	}

	public void setRes(int res) {
		this.res = res;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	
	
}

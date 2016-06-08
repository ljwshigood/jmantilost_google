package com.cn.jmantiLost.bean;

import java.io.Serializable;

public class Server implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String title ;
	
	private String time ;
	
	private String ip ;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}

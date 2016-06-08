package com.cn.jmantiLost.bean;

import java.io.Serializable;

public class IPBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ip ;
	
	private String msg ;
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	private boolean result;
	
	private Server serv ;
	
	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Server getServ() {
		return serv;
	}

	public void setServ(Server serv) {
		this.serv = serv;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	
	
}

package com.cn.jmantiLost.bean;

import java.io.Serializable;

public class CameraInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int focus ;
	
	private int front ;

	public int getFocus() {
		return focus;
	}

	public void setFocus(int focus) {
		this.focus = focus;
	}

	public int getFront() {
		return front;
	}

	public void setFront(int front) {
		this.front = front;
	}

}

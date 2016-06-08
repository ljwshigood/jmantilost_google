package com.cn.jmantiLost.bean;

import java.io.Serializable;

public class ContactBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id ;
	
	private String contact ;
	
	private String number ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
}

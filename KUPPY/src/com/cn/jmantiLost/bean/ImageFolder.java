package com.cn.jmantiLost.bean;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;

public class ImageFolder implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String id ;
	public String path;
	public String name ;
	public int pisNum = 0;
	public ArrayList<String> filePathes = new ArrayList<String>();
	
	public int fileId ;
	
	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}


	public int type ;
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ImageFolder(){
		super();
	}
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPisNum() {
		return pisNum;
	}

	public void setPisNum(int pisNum) {
		this.pisNum = pisNum;
	}


	public ArrayList<String> getFilePathes() {
		return filePathes;
	}


	public void setFilePathes(ArrayList<String> filePathes) {
		this.filePathes = filePathes;
	}


	public ImageFolder(String id, String path, String name,
			int fieldId,int type) {
		super();
		this.id = id;
		this.path = path;
		this.name = name;
		this.fileId = fieldId;
		this.type= type ;
	}
	
	
}

package com.cn.jmantiLost.bean;

public class RecordInfo {

	private boolean isSelect ;
	private boolean isVisible ;
	private String filePath ;
	
	public RecordInfo(boolean isSelect, boolean isVisible, String filePath) {
		super();
		this.isSelect = isSelect;
		this.isVisible = isVisible;
		this.filePath = filePath;
	}
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	public boolean isVisible() {
		return isVisible;
	}
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}

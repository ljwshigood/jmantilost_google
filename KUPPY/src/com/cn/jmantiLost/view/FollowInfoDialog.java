package com.cn.jmantiLost.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.util.LocationUtils;

public class FollowInfoDialog extends Dialog implements android.view.View.OnClickListener{

	private Context mContext ;
	
	private String title ;
	
	private String content ;
	
	private TextView mTvTitle ;
	
	private TextView mTvContent ;
	
	private LinearLayout mLLOK ;
	
	private void initView(){
		mTvTitle = (TextView)findViewById(R.id.tv_title);
		mLLOK = (LinearLayout)findViewById(R.id.ll_ok);
		mTvContent = (TextView)findViewById(R.id.tv_content);
		mLLOK.setOnClickListener(this);
		mTvTitle.setText(title);
		mTvContent.setText(content);
	}
	
	public FollowInfoDialog(Context context) {
		super(context);
	}

	public FollowInfoDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}
	
	private int mFlag = 0 ;
	
	private IOpenGps mIOpenGps ;
	
	public IOpenGps getmIOpenGps() {
		return mIOpenGps;
	}

	public void setmIOpenGps(IOpenGps mIOpenGps) {
		this.mIOpenGps = mIOpenGps;
	}

	public interface IOpenGps{
		
		public void openGps();
	}
	
	
	public FollowInfoDialog(Context context, int theme,String title,String content,int flag) {
		super(context, theme);
		this.mContext = context ;
		this.title = title ;
		this.content = content ;
		this.mFlag = flag ;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 this.setContentView(R.layout.dialog);
		 initView();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_ok:
			if(mFlag == 1){
				if(mIOpenGps != null){
					mIOpenGps.openGps();
				}
				LocationUtils.toggleGPS(mContext);
			}
			if(mIUpdateUI != null){
				mIUpdateUI.updateUI();
			}
			dismiss();
			break;

		default:
			break;
		}	
	}
	
	private IUpdateUI mIUpdateUI ;
	
	public IUpdateUI getmIUpdateUI() {
		return mIUpdateUI;
	}

	public void setmIUpdateUI(IUpdateUI mIUpdateUI) {
		this.mIUpdateUI = mIUpdateUI;
	}

	public interface IUpdateUI{
		public void updateUI();
	}
	
}

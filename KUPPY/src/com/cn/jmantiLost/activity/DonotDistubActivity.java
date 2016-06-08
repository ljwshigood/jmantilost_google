package com.cn.jmantiLost.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.DisturbInfo;
import com.cn.jmantiLost.db.DatabaseManager;

public class DonotDistubActivity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private LinearLayout mLLQuiteDuration;

	private ImageView mIvBack;

	private DatabaseManager mDatabaseManager;

	private ArrayList<DisturbInfo> mDisturbList;
	
	private DisturbInfo mDisturbInfo;

	private String mDeviceAddress ;
	
	private ImageView mIvLine ;
	
	private CheckBox mCbDisturb ;
	
	private TextView mTvDisturbDuration ;
	
	/*private boolean getNotDisturbStatus(){
		SharedPreferences settings = mContext.getSharedPreferences("config", 0);
		return settings.getBoolean("click", false);
	}*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_not_disturb);
		mContext = DonotDistubActivity.this;
		mDeviceAddress = AppContext.mDeviceAddress;
		initData();
		initView();
		setTitle(mContext.getString(R.string.not_disturb));
	}
	
	private View mView;

	private TextView mTvTitleInfo;
	
	private void setTitle(String info) {
		mView = (View) findViewById(R.id.include_head);
		mTvTitleInfo = (TextView) mView.findViewById(R.id.tv_title_info);
		mTvTitleInfo.setText(info);
	}

	private void initData() {
		mDatabaseManager = DatabaseManager.getInstance(mContext);
		mDisturbList = mDatabaseManager.selectDisturbInfo(mDeviceAddress);
		
		for(int i = 0;i < mDisturbList.size();i++){
			mDisturbInfo = mDisturbList.get(i);
		}
	}

	private void initView() {
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mLLQuiteDuration = (LinearLayout) findViewById(R.id.ll_quite_duration);
		mTvDisturbDuration = (TextView)findViewById(R.id.tv_time_duration);
		mCbDisturb = (CheckBox)findViewById(R.id.cb_maunal);
		mIvLine = (ImageView)findViewById(R.id.iv_slice_fromto);
		mCbDisturb.setOnClickListener(this);
		mLLQuiteDuration.setOnClickListener(this);
		mIvBack.setOnClickListener(this);
		mCbDisturb.setChecked(mDisturbInfo.isDisturb());
		
		/*if(!getNotDisturbStatus()){
			if(mDisturbInfo.isDisturb()){
				mLLQuiteDuration.setVisibility(View.VISIBLE);
				mIvLine.setVisibility(View.VISIBLE);
			}
		}else {
			mLLQuiteDuration.setVisibility(View.VISIBLE);
			mIvLine.setVisibility(View.VISIBLE);
		}*/
		
		mLLQuiteDuration.setVisibility(View.VISIBLE);
		mIvLine.setVisibility(View.VISIBLE);
		
		if(!mDisturbInfo.getStartTime().equals("null") && !mDisturbInfo.getEndTime().equals("null")){
			mTvDisturbDuration.setText(mDisturbInfo.getStartTime()+"\n"+mDisturbInfo.getEndTime());	
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_quite_duration:
			Intent intent = new Intent(mContext, QuiteTimeActivity.class);
			startActivity(intent);
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.cb_maunal:
			//setNotDisturbStatus();			
			mDisturbInfo.setDisturb(mCbDisturb.isChecked());
			if(mCbDisturb.isChecked()){
				mLLQuiteDuration.setVisibility(View.VISIBLE);
				mIvLine.setVisibility(View.VISIBLE);
			}
			updateDatabase();
			break ;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
		initView();
	}
	

	private void setNotDisturbStatus() {
		SharedPreferences settings = mContext.getSharedPreferences("config", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("click", true);
		editor.commit();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//updateDatabase();
	}

	private void updateDatabase() {
		ArrayList<DisturbInfo>  list = mDatabaseManager.selectDisturbInfo(mDeviceAddress);
		if(list.size() > 0){
			mDatabaseManager.updateDisturbIsDisturbInfo(mDeviceAddress, mDisturbInfo);
		}
	}
	
	
}

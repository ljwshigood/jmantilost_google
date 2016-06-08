package com.cn.jmantiLost.activity;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.DisturbInfo;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.util.FomatTimeUtil;
import com.cn.jmantiLost.wheelview.NumericWheelAdapter;
import com.cn.jmantiLost.wheelview.OnWheelChangedListener;
import com.cn.jmantiLost.wheelview.WheelView;

public class QuiteTimeActivity extends BaseActivity implements OnClickListener {

	private ImageView mIvBack;

	private TextView mTvStartTimeHour;

	private TextView mTvEndTimeHour;

	private TextView mTvStartTimeMinute;

	private TextView mTvEndTimeMinute;

	private DisturbInfo mDisturInfo;

	private DatabaseManager mDatabaseManager;

	private Context mContext;

	private String mAddress;
	
	private String[] mStartTime  = new String[2];
	
	private String[] mEndTime  = new String[2];
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_quite_mode);
		mContext = QuiteTimeActivity.this;
		mAddress = AppContext.mDeviceAddress;
		mDatabaseManager = DatabaseManager.getInstance(mContext);
		initView();
		ArrayList<DisturbInfo> infoList = mDatabaseManager.selectDisturbInfo(mAddress);
		if (infoList.size() > 0) {
			mDisturInfo = infoList.get(0);
			mStartTime = mDisturInfo.getStartTime().split(":");
			mEndTime= mDisturInfo.getEndTime().split(":");
		}
		initDateTimePicker();
		initDateData();
		setTitle(mContext.getString(R.string.quite_mode_duration));
	}

	private View mView;

	private TextView mTvTitleInfo;
	
	private void setTitle(String info) {
		mView = (View) findViewById(R.id.include_head);
		mTvTitleInfo = (TextView) mView.findViewById(R.id.tv_title_info);
		mTvTitleInfo.setText(info);
	}
	
	private void initView() {
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mTvStartTimeHour = (TextView) findViewById(R.id.tv_from_hour);
		mTvStartTimeMinute = (TextView) findViewById(R.id.tv_from_mimute);

		mTvEndTimeHour = (TextView) findViewById(R.id.tv_to_hour);
		mTvEndTimeMinute = (TextView) findViewById(R.id.tv_to_mimute);

		mIvBack.setOnClickListener(this);
	}
	
	private void initDateData(){
		String startTime[]  = spitString(mDisturInfo.getStartTime());
		mTvStartTimeHour.setText(startTime[0]);
		mTvStartTimeMinute.setText(startTime[1]);
		String endTime[]=  spitString(mDisturInfo.getEndTime());
		mTvEndTimeHour.setText(endTime[0]);
		mTvEndTimeMinute.setText(endTime[1]);
	}

	private void initDateTimePicker() {
		// //////////////////////////
		final WheelView wvFromHours = (WheelView) findViewById(R.id.from_hour);
		wvFromHours.setAdapter(new NumericWheelAdapter(0, 23));
		wvFromHours.setCyclic(true);
		wvFromHours.setCurrentItem(Integer.valueOf(mStartTime[0]));

		final WheelView wvFromMins = (WheelView) findViewById(R.id.from_mins);
		wvFromMins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		wvFromMins.setCyclic(true);
		wvFromMins.setCurrentItem(Integer.valueOf(mStartTime[1]));
		// //////////////////////////////////////////
		final WheelView wvToHours = (WheelView) findViewById(R.id.to_hour);
		wvToHours.setAdapter(new NumericWheelAdapter(0, 23));
		wvToHours.setCyclic(true);
		wvToHours.setCurrentItem(Integer.valueOf(mEndTime[0]));

		final WheelView wvToMins = (WheelView) findViewById(R.id.to_mins);
		wvToMins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		wvToMins.setCyclic(true);
		wvToMins.setCurrentItem(Integer.valueOf(mEndTime[1]));

		int textSize = 30;
		wvFromHours.TEXT_SIZE = textSize;
		wvFromMins.TEXT_SIZE = textSize;
		wvToMins.TEXT_SIZE = textSize;
		wvToHours.TEXT_SIZE = textSize;
		// from hour minutor
		wvFromHours.addChangingListener(wheelFromHour);
		wvFromMins.addChangingListener(wheelFromMinute);
		// to hour minutor ;

		wvToHours.addChangingListener(wheelToHour);
		wvToMins.addChangingListener(wheelToMinutor);

	}
	
	private String[] spitString(String string){
		String [] spiltString = string.split(":");
		return spiltString;
	}
	
	OnWheelChangedListener wheelToHour = new OnWheelChangedListener() {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (newValue < 10) {
				mTvEndTimeHour.setText("0" + newValue);
			} else {
				mTvEndTimeHour.setText(String.valueOf(newValue));
			}
			updateDatabase();
		}
	};

	OnWheelChangedListener wheelToMinutor = new OnWheelChangedListener() {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (newValue < 10) {
				mTvEndTimeMinute.setText("0" + String.valueOf(newValue));
			} else {
				mTvEndTimeMinute.setText(String.valueOf(newValue));
			}
			updateDatabase();
		}
	};

	OnWheelChangedListener wheelFromHour = new OnWheelChangedListener() {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (newValue < 10) {
				mTvStartTimeHour.setText("0" + newValue);
			} else {
				mTvStartTimeHour.setText(String.valueOf(newValue));
			}
			updateDatabase();
		}
	};

	OnWheelChangedListener wheelFromMinute = new OnWheelChangedListener() {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (newValue < 10) {
				mTvStartTimeMinute.setText(String.valueOf("0" + newValue));
			} else {
				mTvStartTimeMinute.setText(String.valueOf(newValue));
			}
			updateDatabase();
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		default:
			break;
		}
	}

	private void updateDatabase() {
		mDisturInfo.setDisturb(true);
		mDisturInfo.setStartTime(String.valueOf(mTvStartTimeHour.getText()
				+ ":" + mTvStartTimeMinute.getText()));
		mDisturInfo.setEndTime(String.valueOf(mTvEndTimeHour.getText() + ":"
				+ mTvEndTimeMinute.getText()));
		Date dateStart = FomatTimeUtil.String2Date(mDisturInfo.getStartTime());
		Date dateEnd = FomatTimeUtil.String2Date(mDisturInfo.getEndTime());
		if(!dateStart.before(dateEnd)){
			Toast.makeText(mContext, mContext.getString(R.string.time_check), 1).show();
		}else{
			mDatabaseManager.updateDisturbInfo(mAddress, mDisturInfo);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

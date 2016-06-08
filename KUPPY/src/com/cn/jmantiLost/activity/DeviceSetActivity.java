package com.cn.jmantiLost.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.service.BluetoothLeService;
import com.cn.jmantiLost.util.SharePerfenceUtil;
import com.cn.jmantiLost.view.FollowEditDialog.ICallbackUpdateView;

public class DeviceSetActivity extends BaseActivity implements OnClickListener,
		ICallbackUpdateView {

	private Context mContext;

	private LinearLayout mLLRecord;

	private Intent mIntent;

	private ImageView mIvBack;

	private String mDeviceAddress;

	private LinearLayout mLLAboutMe;
	
	private CheckBox mCbDisturb ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_set);
		this.mContext = DeviceSetActivity.this;
		getIntentExtra();
		initView();
		setTitle(mContext.getString(R.string.device_setting));
		boolean flag = (Boolean) SharePerfenceUtil.getParam(mContext, "disturb", false) ;
		mCbDisturb.setChecked(flag) ;
	}

	private View mView;

	private TextView mTvTitleInfo;

	private void setTitle(String info) {
		mView = (View) findViewById(R.id.include_head);
		mTvTitleInfo = (TextView) mView.findViewById(R.id.tv_title_info);
		mTvTitleInfo.setText(info);
	}

	@Override
	protected void onResume() {
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		AppContext.isAlarm = true ;
		super.onResume();
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_READ_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSI);
		return intentFilter;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void getIntentExtra() {
		Intent intent = getIntent();
		mDeviceAddress = intent.getStringExtra("address");
		AppContext.mDeviceAddress = mDeviceAddress;
	}

	private void initView() {
		mCbDisturb = (CheckBox)findViewById(R.id.cb_disturb) ;
		mLLAboutMe = (LinearLayout) findViewById(R.id.ll_about_me);
		mLLRecord = (LinearLayout) findViewById(R.id.ll_record);
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBack.setVisibility(View.INVISIBLE);
		mLLRecord.setOnClickListener(this);
		mLLAboutMe.setOnClickListener(this);
		mCbDisturb.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cb_disturb :
			
			SharePerfenceUtil.setParam(mContext, "disturb", mCbDisturb.isChecked());
			
			/*DisturbInfo info = new DisturbInfo() ;
			info.setDisturb(mCbDisturb.isChecked()) ;
			DatabaseManager.getInstance(mContext).updateDisturbInfo(info) ;*/
			break ;
		case R.id.ll_about_me:
			mIntent = new Intent(mContext, AboutMeActivity.class);
			startActivity(mIntent);
			break;
		case R.id.ll_record:
			mIntent = new Intent(mContext, RecordActivity.class);
			startActivity(mIntent);
			break;
		case R.id.iv_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mGattUpdateReceiver);
		super.onDestroy();
	}

	@Override
	public void updateView() {
		initView();
	}
}

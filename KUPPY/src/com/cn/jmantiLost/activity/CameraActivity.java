package com.cn.jmantiLost.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.adapter.GalleryAdapter;
import com.cn.jmantiLost.bean.ImageFolder;
import com.cn.jmantiLost.provider.ImageUtils;
import com.cn.jmantiLost.provider.VideoUtils;
import com.cn.jmantiLost.service.BluetoothLeService;

public class CameraActivity extends Activity implements OnClickListener {

	private Context mContext;

	private ListView mLvCamera;

	private GalleryAdapter mGalleryAdapter;

	private String filePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	private ImageView mIvCamera;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				mGalleryAdapter.notifyGalleryDataSet((ArrayList<ImageFolder>) msg.obj);
			}
		}
	};

	private class ScanThread extends Thread {
		@Override
		public void run() {
			ArrayList<ImageFolder> videolist = VideoUtils
					.getVideoFolderList(CameraActivity.this);
			ArrayList<ImageFolder> imagelist = ImageUtils.getImageFolderList(mContext);
			ArrayList<ImageFolder> list = new ArrayList<ImageFolder>();

			list.addAll(imagelist);
			list.addAll(videolist);
			Message msg = new Message();
			msg.what = 1;
			msg.obj = list;
			mHandler.sendMessage(msg);
		}
	}

	private ImageView mIvBack ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_camera);
		mContext = CameraActivity.this;
		mGalleryAdapter = new GalleryAdapter(mContext, null);
		initView();
		setTitle(mContext.getString(R.string.camera));
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
		super.onResume();
		new ScanThread().start();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSI);
		return intentFilter;
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE.equals(action)) {

			}
		}
	};

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_back);
		mIvBack.setVisibility(View.INVISIBLE);
		mLvCamera = (ListView) findViewById(R.id.lv_gallery);
		mLvCamera.setAdapter(mGalleryAdapter);
		mLvCamera.setOnItemClickListener(mGalleryAdapter);
		mIvCamera = (ImageView) findViewById(R.id.iv_camera);
		mIvCamera.setVisibility(View.VISIBLE);
		mIvCamera.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_camera:
			Intent intent = new Intent(mContext, AntilostCameraActivity.class);
			startActivity(intent);
			break;
		case R.id.iv_back:
			finish();
			break ;
		default:
			break;
		}

	}

}

package com.cn.jmantiLost.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.adapter.FunctionAdapter;
import com.cn.jmantiLost.bean.KeySetBean;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.util.Constant;
import com.cn.jmantiLost.view.LinearGridView;
import com.cn.jmantiLost.view.SelectAppWindow;
import com.cn.jmantiLost.view.SelectAppWindow.ISelectSOSContact;

public class FunctionDetailActivity extends BaseActivity implements
		OnClickListener, ISelectSOSContact, OnItemClickListener {

	private RelativeLayout mRlAntiloast;

	private RelativeLayout mRlCapture;

	private RelativeLayout mRlFlash;

	private RelativeLayout mRlOpenApp;

	private RelativeLayout mRlSos;

	private RelativeLayout mRlAntiCall;

	private RelativeLayout mRlCall;

	private Context mContext;

	private ImageView mIvBack;

	private DatabaseManager mDatabaseManager;

	private LinearGridView mGvFunction;

	private TextView mTvHintInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_function_detail);
		mContext = FunctionDetailActivity.this;
		mDatabaseManager = DatabaseManager.getInstance(mContext);
		initView();
		getIntentData();
		initData();
	}

	private int[] res = new int[] { R.drawable.camera,
			R.drawable.ic_light_press, R.drawable.ic_open_app_press,
			R.drawable.ic_sos_press, R.drawable.ic_anti_call_nomal,
			R.drawable.ic_call_press,R.drawable.ic_capture_large_press,R.drawable.recording};

	private String[] info = null;

	private void initData() {
		info = new String[] { mContext.getString(R.string.capture),
				mContext.getString(R.string.light),
				mContext.getString(R.string.open_app),
				mContext.getString(R.string.sos_detail),
				mContext.getString(R.string.anti_call),
				mContext.getString(R.string.call),
				mContext.getString(R.string.camera),
				mContext.getString(R.string.recording)};

		mFunctionAdapter = new FunctionAdapter(mContext, res, info);
		mGvFunction.setAdapter(mFunctionAdapter);
		mGvFunction.setOnItemClickListener(this);
	}

	private FunctionAdapter mFunctionAdapter;

	private void initView() {
		
		mTvHintInfo = (TextView)findViewById(R.id.tv_title_info);
		mTvHintInfo.setText(mContext.getString(R.string.app_name));
		mGvFunction = (LinearGridView) findViewById(R.id.gv_function);
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBack.setOnClickListener(this);
		mRlAntiloast = (RelativeLayout) findViewById(R.id.rl_antilost);
		mRlCapture = (RelativeLayout) findViewById(R.id.rl_capture);
		mRlFlash = (RelativeLayout) findViewById(R.id.rl_flash);
		mRlOpenApp = (RelativeLayout) findViewById(R.id.rl_open_app);
		mRlSos = (RelativeLayout) findViewById(R.id.rl_sos);
		mRlAntiCall = (RelativeLayout) findViewById(R.id.rl_anti_call);
		mRlCall = (RelativeLayout) findViewById(R.id.rl_call);

		mRlAntiloast.setOnClickListener(this);
		mRlCapture.setOnClickListener(this);
		mRlFlash.setOnClickListener(this);
		mRlOpenApp.setOnClickListener(this);
		mRlSos.setOnClickListener(this);
		mRlAntiCall.setOnClickListener(this);
		mRlCall.setOnClickListener(this);
	}

	private int mCount;

	private void getIntentData() {
		Intent intent = getIntent();
		mCount = intent.getIntExtra("count", 0);
		setTitleInfo();
	}

	private void setTitleInfo() {
		mTvHintInfo.setTextSize(15);
		switch (mCount) {
		case 2:
			mTvHintInfo.setText(mContext.getString(R.string.swit_two_click));
			break;
		case 3:
			mTvHintInfo.setText(mContext.getString(R.string.swit_three_click));
			break;
		case 4:
			mTvHintInfo.setText(mContext.getString(R.string.swit_four_click));
			break;

		default:
			break;
		}
	}

	private KeySetBean mKeySetBean;

	private SelectAppWindow menuWindow;

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {

			default:
				break;
			}
		}
	};

	private void showWindows(int type) {

		menuWindow = new SelectAppWindow(FunctionDetailActivity.this,
				itemsOnClick, type);
		menuWindow.setmCount(mCount);
		menuWindow.showAtLocation(this.findViewById(R.id.main), Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
		menuWindow.setmISelectSOSContact(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_antilost:
			Intent intent = new Intent(mContext, DeviceDisplayActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_flash:
			mKeySetBean = new KeySetBean();
			mKeySetBean.setCount(mCount);
			mKeySetBean.setType(0);
			mKeySetBean.setKeySetDetail(mContext.getString(R.string.light));
			mKeySetBean.setAction(1);
			mKeySetBean.setBitmapString(String.valueOf(R.drawable.ic_key_light));
			mDatabaseManager.editorKeySet(mKeySetBean);
			finish();
			break;
		case R.id.rl_capture:
			showWindows(3);
			break;
		case R.id.rl_open_app:
			showWindows(0);
			break;
		case R.id.rl_sos:
			showWindows(1);
			break;

		case R.id.rl_anti_call:
			showWindows(2);
			break;
		case R.id.rl_call:
			showWindows(4);
			break;

		default:
			break;
		}
	}

	private int mType;

	@Override
	public void selectSOSContact(int type) {
		mType = type;
		Uri uri= ContactsContract.Contacts.CONTENT_URI;
        Intent intent=new Intent(Intent.ACTION_PICK, uri);
        intent.setType(Phone.CONTENT_TYPE);
		/*Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("vnd.android.cursor.dir/phone");*/
		startActivityForResult(intent, Constant.SEND_SMS_TYPE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (Constant.SEND_SMS_TYPE == requestCode) {
			if (!(resultCode == RESULT_OK)) {
				return;
			}
			if (data == null) {
				return;
			}
			Uri uri = data.getData();
			Cursor cursor = getContentResolver().query(uri, null, null, null,null);
			cursor.moveToFirst();
			int indexPeopleName = cursor.getColumnIndex(Phone.DISPLAY_NAME);
			String strPeopleName = cursor.getString(indexPeopleName);
			String number = cursor.getString(cursor.getColumnIndexOrThrow(Phone.NUMBER));
			if (mType == 1) {
				menuWindow.updateContact(number);
			} else if (mType == 2) {
				menuWindow.updateAntiCallContact(number, strPeopleName);
			} else if (mType == 4) {
				menuWindow.updateCallContact(number, strPeopleName);
			}

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			showWindows(3);
			break;
		case 1:
			mKeySetBean = new KeySetBean();
			mKeySetBean.setCount(mCount);
			mKeySetBean.setType(0);
			mKeySetBean.setKeySetDetail(mContext.getString(R.string.light));
			mKeySetBean.setAction(1);
			mKeySetBean.setBitmapString(String.valueOf(R.drawable.ic_light_press));
			mDatabaseManager.editorKeySet(mKeySetBean);
			finish();
			break;
		case 2:
			showWindows(0);
			break;
		case 3:
			showWindows(1);
			break;
		case 4:
			showWindows(2);
			break;
		case 5:
			showWindows(4);
			break;
		case 6:
			mKeySetBean = new KeySetBean();
			mKeySetBean.setCount(mCount);
			mKeySetBean.setType(0);
			mKeySetBean.setKeySetDetail(mContext.getString(R.string.camera));
			mKeySetBean.setAction(6);
			mKeySetBean.setBitmapString(String.valueOf(R.drawable.ic_capture_large_press));
			mDatabaseManager.editorKeySet(mKeySetBean);
			finish();
			break ;
		case 7:
			mKeySetBean = new KeySetBean();
			mKeySetBean.setCount(mCount);
			mKeySetBean.setType(0);
			mKeySetBean.setKeySetDetail(mContext.getString(R.string.record_info));
			mKeySetBean.setAction(7);
			mKeySetBean.setBitmapString(String.valueOf(R.drawable.recording));
			mDatabaseManager.editorKeySet(mKeySetBean);
			finish();
			break ;
		default:
			break;
		}
	}

	@Override
	public void okSelect() {
		finish();
	}

}

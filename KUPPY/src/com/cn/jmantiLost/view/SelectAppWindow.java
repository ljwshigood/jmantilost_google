package com.cn.jmantiLost.view;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.adapter.AppAdapter;
import com.cn.jmantiLost.adapter.AppAdapter.ISelectApp;
import com.cn.jmantiLost.bean.AppInfo;
import com.cn.jmantiLost.bean.CameraInfo;
import com.cn.jmantiLost.bean.ContactBean;
import com.cn.jmantiLost.bean.KeySetBean;
import com.cn.jmantiLost.bean.SosBean;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.util.AppManager;
import com.cn.jmantiLost.util.LocationUtils;
import com.cn.jmantiLost.view.FollowInfoDialog.IOpenGps;

public class SelectAppWindow extends PopupWindow implements ISelectApp,IOpenGps {

	private View mMenuView;

	private RelativeLayout mRLOpenApp;

	private LinearLayout mLLSosCall;

	private LinearLayout mLLAntiCall;

	private LinearLayout mLLOk;

	private LinearLayout mLLCancel;

	private int mType;

	private EditText mEtSosContact;

	private EditText mEtContact;

	private EditText mEtNumber;

	private Context mContext;

	private ListView mLvApp;

	private TextView mTvTitle;
	
	private EditText mEtSosMessage ;

	private int mCount;

	public void updateContact(String number) {
		mEtSosContact.setText(number);
	}
	
	public void updateAntiCallContact(String number,String name){
		mEtContact.setText(name);
		mEtNumber.setText(number);
	}
	
	public void updateCallContact(String number,String name){
		mEtCallPhone.setText(number);
	}
	
	public int getmCount() {
		return mCount;
	}

	public void setmCount(int mCount) {
		this.mCount = mCount;
	}

	private ProgressBar mProgressBar;

	private ArrayList<AppInfo> mAppInfoList;

	private AppAdapter mAppAdapter;

	private ImageView mIvContact;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				if (mProgressBar != null) {
					mProgressBar.setVisibility(View.GONE);
				}
				mAppAdapter.notifyAppDataSetChange(mAppInfoList);
			}
		};
	};

	private LinearLayout mLlCameraSet;

	public ISelectSOSContact mISelectSOSContact;

	public ISelectSOSContact getmISelectSOSContact() {
		return mISelectSOSContact;
	}

	public void setmISelectSOSContact(ISelectSOSContact mISelectSOSContact) {
		this.mISelectSOSContact = mISelectSOSContact;
	}

	private DatabaseManager mDatabaseManger;

	private CheckBox mCbBackCamera;

	private CheckBox mCbFrontCamera;

	private CheckBox mCbSwitch;

	private LinearLayout mLLContactPhone;
	
	private EditText mEtCallPhone ;
	
	private ImageView mIvAntiCallContact ;
	
	private ImageView mIvCallPhoneContact ;
	
	

	public SelectAppWindow(Activity context, OnClickListener itemsOnClick,
			int type) {
		super(context);
		this.mContext = context;
		mDatabaseManger = DatabaseManager.getInstance(mContext);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.pop_select_app, null);
		this.mType = type;
		mEtSosMessage = (EditText)mMenuView.findViewById(R.id.et_sos_message);
		mIvCallPhoneContact = (ImageView)mMenuView.findViewById(R.id.iv_call_contact);
		mIvAntiCallContact = (ImageView)mMenuView.findViewById(R.id.iv_anti_call);
		mEtContact = (EditText) mMenuView.findViewById(R.id.contact_value);
		mEtNumber = (EditText) mMenuView.findViewById(R.id.et_mobile_value);
		mRLOpenApp = (RelativeLayout) mMenuView.findViewById(R.id.ll_select_app);
		mLLSosCall = (LinearLayout) mMenuView.findViewById(R.id.ll_sos_info);
		mLLAntiCall = (LinearLayout) mMenuView.findViewById(R.id.ll_contact_info);
		mTvTitle = (TextView) mMenuView.findViewById(R.id.tv_title);
		mProgressBar = (ProgressBar) mMenuView.findViewById(R.id.pb_progress);
		mIvContact = (ImageView) mMenuView.findViewById(R.id.iv_contact);
		mEtSosContact = (EditText) mMenuView.findViewById(R.id.et_sos_mobile);
		mCbSwitch = (CheckBox) mMenuView.findViewById(R.id.cb_switch);
		mLlCameraSet = (LinearLayout) mMenuView.findViewById(R.id.ll_select_camera);
		mCbBackCamera = (CheckBox) mMenuView.findViewById(R.id.cb_back);
		mCbFrontCamera = (CheckBox) mMenuView.findViewById(R.id.cb_front);
		mLLContactPhone = (LinearLayout) mMenuView.findViewById(R.id.ll_call_contact);
		mEtCallPhone = (EditText)mMenuView.findViewById(R.id.et_contact_mobile_value);
		mIvAntiCallContact.setVisibility(View.INVISIBLE);
		mIvCallPhoneContact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mISelectSOSContact.selectSOSContact(mType);
			}
		});
		
		mIvAntiCallContact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mISelectSOSContact.selectSOSContact(mType);
			}
		});
		
		mCbBackCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCbBackCamera.isChecked()) {
					mCbFrontCamera.setChecked(false);
				} else {
					mCbFrontCamera.setChecked(true);
				}
			}
		});

		mCbFrontCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCbFrontCamera.isChecked()) {
					mCbBackCamera.setChecked(false);
				} else {
					mCbBackCamera.setChecked(true);
				}
			}
		});

		if (type == 0) { // open app
			mRLOpenApp.setVisibility(View.VISIBLE);
			mLLSosCall.setVisibility(View.GONE);
			mLLAntiCall.setVisibility(View.GONE);
			mLlCameraSet.setVisibility(View.GONE);
			mLLContactPhone.setVisibility(View.GONE);
			mLvApp = (ListView) mMenuView.findViewById(R.id.lv_select_app);
			mAppAdapter = new AppAdapter(mContext, mAppInfoList);
			mAppAdapter.setmISelectApp(this);
			mAppAdapter.setmCount(mCount);
			mLvApp.setAdapter(mAppAdapter);
			mLvApp.setOnItemClickListener(mAppAdapter);
			new Thread() {

				@Override
				public void run() {
					mAppInfoList = AppManager.getAllApplicationInfo(mContext);
					handler.sendEmptyMessage(0);
				};
			}.start();

		} else if (type == 1) { // jinji call
			
			SosBean bean = mDatabaseManger.selectSOSInfo();
			if(bean != null){
				mEtSosContact.setText(bean.getContact());
				mEtSosMessage.setText(bean.getMessage());	
			}
			
			mRLOpenApp.setVisibility(View.GONE);
			mLLSosCall.setVisibility(View.VISIBLE);
			mLLAntiCall.setVisibility(View.GONE);
			mLlCameraSet.setVisibility(View.GONE);
			mLLContactPhone.setVisibility(View.GONE);
			mTvTitle.setText(mContext.getString(R.string.sos));
			mIvContact.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mISelectSOSContact.selectSOSContact(mType);
				}
			});
		} else if (type == 2) { // anti call
			
			ContactBean contact = mDatabaseManger.selectAntiContact();
			if(contact != null){
				mEtNumber.setText(contact.getNumber());
				mEtContact.setText(contact.getContact());
			}
			
			
			mRLOpenApp.setVisibility(View.GONE);
			mLLSosCall.setVisibility(View.GONE);
			mLLAntiCall.setVisibility(View.VISIBLE);
			mLlCameraSet.setVisibility(View.GONE);
			mLLContactPhone.setVisibility(View.GONE);
			mTvTitle.setText(mContext.getString(R.string.contact));
		} else if (type == 3) {
			CameraInfo info = mDatabaseManger.selectCameraInfo();
			if(info.getFront() == 1){
				mCbBackCamera.setChecked(false);
				mCbFrontCamera.setChecked(true);
			}else{
				mCbBackCamera.setChecked(true);
				mCbFrontCamera.setChecked(false);
			}
			
			if(info.getFocus() == 0){
				mCbSwitch.setChecked(false);
			}else{
				mCbSwitch.setChecked(true);
			}
			
			mRLOpenApp.setVisibility(View.GONE);
			mLLSosCall.setVisibility(View.GONE);
			mLLAntiCall.setVisibility(View.GONE);
			mLLContactPhone.setVisibility(View.GONE);
			mLlCameraSet.setVisibility(View.VISIBLE);
			mTvTitle.setText(mContext.getString(R.string.camear_set));
		} else if (type == 4) {
			
			ContactBean bean = mDatabaseManger.selectContact();
			if(bean != null){
				mEtCallPhone.setText(bean.getNumber());
			}
			
			mRLOpenApp.setVisibility(View.GONE);
			mLLSosCall.setVisibility(View.GONE);
			mLLAntiCall.setVisibility(View.GONE);
			mLlCameraSet.setVisibility(View.GONE);
			mLLContactPhone.setVisibility(View.VISIBLE);
			mTvTitle.setText(mContext.getString(R.string.call_number));
		}

		mLLOk = (LinearLayout) mMenuView.findViewById(R.id.ll_ok);
		mLLCancel = (LinearLayout) mMenuView.findViewById(R.id.ll_cancel);

		mLLOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mType == 0) { // insert into app info

					mDatabaseManger.insertAppInfo(mCurrentApp);

					KeySetBean bean = new KeySetBean();
					bean.setBitmapString(mCurrentApp.getPackageName());
					bean.setKeySetDetail(mCurrentApp.getAppName());
					bean.setCount(mCount);
					bean.setAction(2);
					bean.setType(1);
					mDatabaseManger.editorKeySet(bean);
					dismiss();
					mISelectSOSContact.okSelect();
				} else if (mType == 1) { // sos
					
					if (!LocationUtils.isOPen(mContext)) {
						FollowInfoDialog dialogLocation = new FollowInfoDialog(
								mContext, R.style.MyDialog, null,mContext.getString(R.string.open_gps), 1);
						dialogLocation.setmIOpenGps(SelectAppWindow.this);
						dialogLocation.show();
					}else{
						if(TextUtils.isEmpty(mEtSosContact.getText().toString())){
							Toast.makeText(mContext, mContext.getString(R.string.contact_empty), 1).show();
							return ;
						}
					
						if(TextUtils.isEmpty(mEtSosMessage.getText().toString())){
							Toast.makeText(mContext, mContext.getString(R.string.sms_empty), 1).show();
							return ;
						}
		
						DatabaseManager.getInstance(mContext).insertSOSInfo(
								mEtSosContact.getText().toString(),
								mEtSosMessage.getText().toString());
	
						KeySetBean bean = new KeySetBean();
						bean.setBitmapString(String
								.valueOf(R.drawable.ic_sos_press));
						bean.setKeySetDetail(mContext.getString(R.string.jinji_sos));
						bean.setCount(mCount);
						bean.setAction(4);
						bean.setType(0);
						mDatabaseManger.editorKeySet(bean);
						dismiss();
						mISelectSOSContact.okSelect();
					}
				} else if (mType == 2) {// anti call
					DatabaseManager.getInstance(mContext).insertAntiContact(
							mEtContact.getText().toString(),
							mEtNumber.getText().toString());

					KeySetBean bean = new KeySetBean();
					bean.setBitmapString(String.valueOf(R.drawable.ic_anti_call_nomal));
					bean.setKeySetDetail(mContext.getString(R.string.anti_call));
					bean.setCount(mCount);
					bean.setAction(3);
					bean.setType(0);
					mDatabaseManger.editorKeySet(bean);
					dismiss();
					mISelectSOSContact.okSelect();
				} else if (mType == 3) { // camera capture ;
					CameraInfo info = new CameraInfo();
					if (mCbFrontCamera.isChecked()) {
						info.setFront(1);
					} else {
						info.setFront(0);
					}

					if (mCbSwitch.isChecked()) {
						info.setFocus(1);
					} else {
						info.setFocus(0);
					}
					DatabaseManager.getInstance(mContext).insertCameraInfo(info);

					KeySetBean bean = new KeySetBean();
					//
					bean.setBitmapString(String.valueOf(R.drawable.camera));
					bean.setKeySetDetail(mContext.getString(R.string.zipai));
					bean.setCount(mCount);
					bean.setAction(0);
					bean.setType(0);
					DatabaseManager.getInstance(mContext).editorKeySet(bean);
					dismiss();
					mISelectSOSContact.okSelect();
				} else if (mType == 4) {// call people
					DatabaseManager.getInstance(mContext).insertContact("",mEtCallPhone.getText().toString());

					KeySetBean bean = new KeySetBean();
					bean.setBitmapString(String.valueOf(R.drawable.ic_call_press));
					bean.setKeySetDetail(mContext.getString(R.string.call_people));
					bean.setCount(mCount);
					bean.setAction(5);
					bean.setType(0);
					mDatabaseManger.editorKeySet(bean);
					dismiss();
					mISelectSOSContact.okSelect();
				}
			}
		});

		mLLCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dismiss();
			}
		});
		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.select_app).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}
	
	public interface ISelectSOSContact {
		
		public void selectSOSContact(int type);
		
		public void okSelect();
	}

	private AppInfo mCurrentApp;

	@Override
	public void selectApp(AppInfo appUnit) {
		mCurrentApp = appUnit;
	}

	@Override
	public void openGps() {
		DatabaseManager.getInstance(mContext).insertSOSInfo(
				mEtSosContact.getText().toString(),
				mEtSosMessage.getText().toString());

		KeySetBean bean = new KeySetBean();
		bean.setBitmapString(String
				.valueOf(R.drawable.ic_sos_press));
		bean.setKeySetDetail(mContext.getString(R.string.jinji_sos));
		bean.setCount(mCount);
		bean.setAction(4);
		bean.setType(0);
		mDatabaseManger.editorKeySet(bean);
		dismiss();
		mISelectSOSContact.okSelect();
	}

}

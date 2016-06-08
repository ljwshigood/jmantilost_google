package com.cn.jmantiLost.view;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.bean.DeviceSetInfo;
import com.cn.jmantiLost.db.DatabaseManager;

public class FollowEditDialog extends Dialog implements android.view.View.OnClickListener{


	private Context mContext ;
	
	private EditText mEtText;
	
	private String title ;
	
	private String content ;
	
	private TextView mTvTitle ;
	
	private LinearLayout mLLOK ;
	
	private EditText mEtContent ;
	
	private void initView(){
		mTvTitle = (TextView)findViewById(R.id.tv_title);
		mLLOK = (LinearLayout)findViewById(R.id.ll_ok);
		mEtContent = (EditText)findViewById(R.id.et_content);
		mLLOK.setOnClickListener(this);
		mTvTitle.setText(title);
	}
	
	
	public FollowEditDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public FollowEditDialog(Context context, int theme) {
		super(context, theme);
	}

	public FollowEditDialog(Context context) {
		super(context);
	}
	
	private DeviceSetInfo mInfo ;
	
	private String mAddress ;
	
	private ICallbackUpdateView mCallback ;
	
	public FollowEditDialog(Context context, int theme,String title,DeviceSetInfo info,String address,ICallbackUpdateView callback) {
		super(context, theme);
		setContentView(R.layout.edit_device_name_dialog);
		this.mContext = context ;
		this.title = title ;
		this.mInfo = info ;
		this.mAddress = address ;
		this.mCallback = callback;
		initView();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_ok:
			if(mEtContent.getText().toString().equals("")){
				Toast.makeText(mContext, mContext.getString(R.string.name_is_null), 1).show();
				return ;
			}
			mInfo.setmDeviceName(mEtContent.getText().toString());
			mEtContent.setText(mEtContent.getText().toString());
			
			DatabaseManager mDatabaseManager = DatabaseManager.getInstance(mContext);
			mDatabaseManager.updateDeviceInfo(mAddress, mInfo);
			mCallback.updateView();
			dismiss();
			break;
		default:
			break;
		}
	}
	
	public interface ICallbackUpdateView{
		void updateView();
	}
	
	
}

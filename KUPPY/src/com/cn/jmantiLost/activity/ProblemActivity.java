package com.cn.jmantiLost.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.jmantiLost.R;

public class ProblemActivity extends BaseActivity implements OnClickListener{

	private ImageView mIvBack;
	
	private TextView mTvMainInfo ;

	private void initView() {
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mTvMainInfo  =(TextView)findViewById(R.id.tv_title_info);
		mIvBack.setOnClickListener(this);
	}
	
	private void initData(){
		mTvMainInfo.setText(mContext.getString(R.string.problem)) ;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_problem);
		initView();
		initData(); 
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish() ;
			break;

		default:
			break;
		}
	}

}

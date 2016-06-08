package com.cn.jmantiLost.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.adapter.ImageAdapter;
import com.cn.jmantiLost.bean.ImageFolder;

public class GalleryActivity extends BaseActivity implements OnClickListener{

	private GridView mGvGallery;

	private ImageAdapter mImageAdapter;

	private Context mContext;

	private ArrayList<String> mFileList = null;

	private ImageFolder mImageFolder;

	private TextView mTvTitleMainInfo ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		mContext = GalleryActivity.this;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mImageFolder = (ImageFolder) bundle.get("imagefolder");
		mImageAdapter = new ImageAdapter(mContext, mImageFolder);
		initView();

	}
	
	private ImageView mIvBack ;

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_back);
		mIvBack.setOnClickListener(this);
		mTvTitleMainInfo = (TextView)findViewById(R.id.tv_title_info);
		mTvTitleMainInfo.setText(mContext.getString(R.string.gallery));
		mGvGallery = (GridView) findViewById(R.id.gv_camera);
		mGvGallery.setAdapter(mImageAdapter);
		mGvGallery.setOnItemClickListener(mImageAdapter);
	}

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
}

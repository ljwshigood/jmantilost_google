package com.cn.jmantiLost.adapter;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.activity.ImagePreviewActivity;
import com.cn.jmantiLost.bean.ImageFolder;
import com.cn.jmantiLost.util.AsyncDraweableLoader;
import com.cn.jmantiLost.util.AsyncDraweableLoader.ImageCallback;

public class ImageAdapter extends BaseAdapter implements OnItemClickListener {

	private Context mContext;

	private ArrayList<String> mFilePathes;

	private LayoutInflater mLayoutInflater;

	private AsyncDraweableLoader imageLoader = new AsyncDraweableLoader();
	
	private ImageFolder mImageFolder ;

	public ImageAdapter(Context context,ImageFolder imageFolder) {
		this.mContext = context;
		mImageFolder = imageFolder;
		this.mFilePathes = imageFolder.getFilePathes();
		this.mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {

		return mFilePathes == null ? 0 : mFilePathes.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		final String filePath = mFilePathes.get(position);
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.gv_image_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mIvImage = (ImageView) convertView.findViewById(R.id.iv_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mIvImage.setTag(filePath);
		final View view = convertView ;
		Drawable drawable = imageLoader.loadDrawable(filePath, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView imageViewByTag = (ImageView)view.findViewWithTag(filePath);
				 if (imageViewByTag!=null) {
                     imageViewByTag.setBackgroundDrawable(imageDrawable);
                 }
			}
		}, mImageFolder.getType());
		
		 if(drawable==null){
			 Resources resources = mContext.getResources();
			 Drawable db = resources.getDrawable(R.drawable.ic_launcher);
			 viewHolder.mIvImage.setBackgroundDrawable(db);
         }else{
        	 viewHolder.mIvImage.setBackgroundDrawable(drawable);
         }
		return convertView;
	}

	class ViewHolder {
		ImageView mIvImage;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3) {
		String filePath = mFilePathes.get(position);
		if(mImageFolder.getType() == 1){
			Intent intent = new Intent(mContext,ImagePreviewActivity.class);
			intent.putStringArrayListExtra("filePathList", mFilePathes);
			intent.putExtra("position", position);
			mContext.startActivity(intent);
		}else{
			File newFile = new File(filePath);
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(newFile), "video/*");
			mContext.startActivity(intent);
		}
	}

}

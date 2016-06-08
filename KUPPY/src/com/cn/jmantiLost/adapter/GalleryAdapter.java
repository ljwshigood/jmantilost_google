package com.cn.jmantiLost.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.activity.GalleryActivity;
import com.cn.jmantiLost.bean.ImageFolder;
import com.cn.jmantiLost.util.AsyncDraweableLoader;
import com.cn.jmantiLost.util.AsyncDraweableLoader.ImageCallback;

public class GalleryAdapter extends BaseAdapter implements OnItemClickListener {

	private Context mContext;

	private ArrayList<ImageFolder> mImageFolderList;

	private LayoutInflater mLayoutInflater;

	private AsyncDraweableLoader imageLoader = new AsyncDraweableLoader();

	public GalleryAdapter(Context context,
			ArrayList<ImageFolder> imageFolderList) {
		this.mContext = context;
		this.mImageFolderList = imageFolderList;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mImageFolderList == null ? 0 : mImageFolderList.size();
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

		ImageFolder imageFolder = mImageFolderList.get(position);
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.list_item_gallery,
					null);
			viewHolder = new ViewHolder();
			viewHolder.mIvIcon = (ImageView) convertView
					.findViewById(R.id.iv_icon);
			viewHolder.mTvPath = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.mTvName = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.mTvPicCount = (TextView) convertView
					.findViewById(R.id.tv_picturecount);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final View view = convertView ;
		final String filePath = imageFolder.getPath() ;
		viewHolder.mIvIcon.setTag(filePath);
		Drawable drawable = imageLoader.loadDrawable(imageFolder.getPath(), new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				  ImageView imageViewByTag = (ImageView)view.findViewWithTag(filePath);
				  if (imageViewByTag!=null) {
                      imageViewByTag.setImageDrawable(imageDrawable);
                  }
			}
		}, imageFolder.getType());
		
		 if(drawable==null){
			 Resources resources = mContext.getResources();
			 Drawable db = resources.getDrawable(R.drawable.ic_launcher);
			 viewHolder.mIvIcon.setImageDrawable(db);
         }else{
        	 viewHolder.mIvIcon.setImageDrawable(drawable);
         }
		viewHolder.mTvPicCount.setText(mContext.getString(R.string.pic_count) + imageFolder.getFilePathes().size());
		viewHolder.mTvName.setText(imageFolder.getName());
		return convertView;
	}

	static class ViewHolder {
		ImageView mIvIcon;
		TextView mTvPath;
		TextView mTvName;
		TextView mTvPicCount;
	}

	public void notifyGalleryDataSet(ArrayList<ImageFolder> list) {
		this.mImageFolderList = list;
		notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3) {
		ImageFolder folder = mImageFolderList.get(position);
		Intent intent = new Intent(mContext, GalleryActivity.class);
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("imagefolder", folder);
		intent.putExtras(mBundle);
		mContext.startActivity(intent);
	}

}

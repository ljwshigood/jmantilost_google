package com.cn.jmantiLost.activity;

import java.io.IOException;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.util.AsyncImageLoader;
import com.cn.jmantiLost.util.AsyncImageLoader.ImageCallback;

public class ImagePreviewActivity extends Activity implements OnClickListener {

	private ViewPager mViewPager;

	private Context mContext;

	private ArrayList<String> mPicFiles;

	private int mItemwidth;

	private int mItemHerght;
	
	private NotificationManager mNotificationManager ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_preview);
		
		mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.cancel(200);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mItemwidth = dm.widthPixels;
		mItemHerght = dm.heightPixels;
		mContext = ImagePreviewActivity.this;
		getIntentExtra();
		initView();
		initData();
	}
	
	private int mPosition = 0 ;
	
	private void getIntentExtra() {
		Intent intent = getIntent();
		mPicFiles = intent.getStringArrayListExtra("filePathList");
		mPosition = intent.getIntExtra("position", 0);
	}

	
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.pager);
	}

	private void initData() {
		ImageAdapter adapter = new ImageAdapter(mContext, mPicFiles);
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(mPosition);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private AsyncImageLoader mImageLoader;
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	private class ImageAdapter extends PagerAdapter {

		private LayoutInflater inflater;

		private ArrayList<String> mFileList;

		public ImageAdapter(Context context, ArrayList<String> fileList) {
			inflater = LayoutInflater.from(context);
			this.mFileList = fileList;
			mImageLoader = new AsyncImageLoader();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return mFileList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_image, view,
					false);
			assert imageLayout != null;

			final PhotoView imageView = (PhotoView) imageLayout
					.findViewById(R.id.iv);
			final String filePath = mFileList.get(position);
			imageView.setTag(filePath);
			
			Bitmap bitmap = mImageLoader.loadDrawable(filePath, new ImageCallback() {
				
				@Override
				public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
					updateImageView(imageView, imageDrawable, filePath);
				}
			}, 1, mItemwidth, mItemHerght);
			
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			}
			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		
	}

	public void updateImageView(ImageView iv,Bitmap bitmap,String picFilePath) {
		
		String exif_orientation_s = null;
		int thumbnail_rotation = 0;
		try {
			if( exif_orientation_s == null ) {
            	ExifInterface exif = new ExifInterface(picFilePath);
            	exif_orientation_s = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
			}
			int exif_orientation = 0;
			//from http://jpegclub.org/exif_orientation.html
			if( exif_orientation_s.equals("0") || exif_orientation_s.equals("1") ) {
				// leave at 0
			}
			else if( exif_orientation_s.equals("3") ) {
				exif_orientation = 180;
			}
			else if( exif_orientation_s.equals("6") ) {
				exif_orientation = 90;
			}
			else if( exif_orientation_s.equals("8") ) {
				exif_orientation = 270;
			}			
			thumbnail_rotation = (thumbnail_rotation + exif_orientation) % 360;
		}
		catch(IOException exception) {
			exception.printStackTrace();
		}
		if( thumbnail_rotation != 0 ) {
			Matrix m = new Matrix();
			m.setRotate(thumbnail_rotation, bitmap.getWidth() * 0.5f, bitmap.getHeight() * 0.5f);
			Bitmap rotated_thumbnail = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), m, true);
			if( rotated_thumbnail != bitmap ) {
				bitmap.recycle();
				bitmap = rotated_thumbnail;
			}
		}
		if (bitmap != null) {
			iv.setImageBitmap(bitmap);
		}
	}
}

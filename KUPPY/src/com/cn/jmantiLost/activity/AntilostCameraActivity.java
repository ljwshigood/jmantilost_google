package com.cn.jmantiLost.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Video.VideoColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.service.BluetoothLeService;
import com.cn.jmantiLost.util.EncriptyUtils;
import com.cn.jmantiLost.view.AntilostPreview;


public class AntilostCameraActivity extends Activity {
	private static final String TAG = "MainActivity";
	private SensorManager mSensorManager = null;
	private Sensor mSensorAccelerometer = null;
	private Sensor mSensorMagnetic = null;
	private LocationManager mLocationManager = null;
	private LocationListener locationListener = null;
	private AntilostPreview preview = null;
	private int current_orientation = 0;
	private OrientationEventListener orientationEventListener = null;
	private boolean supports_auto_stabilise = false;
	private boolean supports_force_video_4k = false;

	public Bitmap gallery_bitmap = null;
	
	
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
			String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA) ;
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

			} else if (BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE.equals(action)) {
				String jiemi = EncriptyUtils.decryption(data);
				if(jiemi.startsWith("134a")){
					takePicture();
				}
			
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mGattUpdateReceiver) ;
	};
	
	private Context mContext ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antilost_camera);
		mContext = AntilostCameraActivity.this ;
		
		Toast.makeText(mContext,mContext.getString(R.string.initial_camera),Toast.LENGTH_SHORT).show();
		
		AppContext.isAlarm = false ;
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		if( activityManager.getLargeMemoryClass() >= 128 ) {
			supports_auto_stabilise = true;
		}
		
		if( activityManager.getLargeMemoryClass() >= 512 ) {
			supports_force_video_4k = true;
		}
		
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		if( mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null ) {
			mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		
		if( mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null ) {
			mSensorMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		}

		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		preview = (AntilostPreview)findViewById(R.id.ap_preview) ;

		
        orientationEventListener = new OrientationEventListener(this) {
			@Override
			public void onOrientationChanged(int orientation) {
				AntilostCameraActivity.this.onOrientationChanged(orientation);
			}
        };
        
        layoutUI() ;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
        return super.onKeyDown(keyCode, event); 
    }

	private SensorEventListener accelerometerListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			preview.onAccelerometerSensorChanged(event);
		}
	};
	
	private SensorEventListener magneticListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			preview.onMagneticSensorChanged(event);
		}
	};
	
    @Override
    protected void onResume() {
        super.onResume();
        
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mSensorManager.registerListener(accelerometerListener, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(magneticListener, mSensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        orientationEventListener.enable();

		boolean store_location = sharedPreferences.getBoolean("preference_location", false);
		if( store_location ) {
			locationListener = new LocationListener() {
			    public void onLocationChanged(Location location) {
			    	preview.locationChanged(location);
			    }

			    public void onStatusChanged(String provider, int status, Bundle extras) {
			    }

			    public void onProviderEnabled(String provider) {
			    }

			    public void onProviderDisabled(String provider) {
			    }
			};
			
			if( mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER) ) {
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			}
			if( mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER) ) {
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			}
		}

		mHandler.postDelayed(runnable, 50) ;
    }
    
    Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			mHandler.sendEmptyMessage(0) ;
		}
	};
    
    private Handler mHandler = new Handler(){
    		public void handleMessage(android.os.Message msg) {
    			layoutUI();
    			updateGalleryIcon(); 
    			preview.onResume();
    		};
    } ;
    
    
    @Override
    protected void onPause() {
        super.onPause() ;
        mSensorManager.unregisterListener(accelerometerListener);
        mSensorManager.unregisterListener(magneticListener);
        orientationEventListener.disable();
        if( this.locationListener != null ) {
            mLocationManager.removeUpdates(locationListener);
        }
		preview.onPause();
		
    }

    public void layoutUI() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String ui_placement = sharedPreferences.getString("preference_ui_placement", "ui_right");
		boolean ui_placement_right = ui_placement.equals("ui_right");
	    int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
	    int degrees = 0;
	    switch (rotation) {
	    	case Surface.ROTATION_0: degrees = 0; break;
	        case Surface.ROTATION_90: degrees = 90; break;
	        case Surface.ROTATION_180: degrees = 180; break;
	        case Surface.ROTATION_270: degrees = 270; break;
	    }
    	int relative_orientation = (current_orientation + degrees) % 360;
    	
		int ui_rotation = (360 - relative_orientation) % 360;
		preview.setUIRotation(ui_rotation);
		int left_of = RelativeLayout.LEFT_OF;
		int right_of = RelativeLayout.RIGHT_OF;
		int align_parent_left = RelativeLayout.ALIGN_PARENT_LEFT ;
		int align_parent_right = RelativeLayout.ALIGN_PARENT_RIGHT ;
		int align_parent_top = RelativeLayout.ALIGN_PARENT_TOP ;
		int align_parent_bottom = RelativeLayout.ALIGN_PARENT_BOTTOM ;
		if( ( relative_orientation == 0 && ui_placement_right ) || ( relative_orientation == 180 && ui_placement_right ) || relative_orientation == 90 || relative_orientation == 270) {
			if( !ui_placement_right && ( relative_orientation == 90 || relative_orientation == 270 ) ) {
				align_parent_top = RelativeLayout.ALIGN_PARENT_BOTTOM;
				align_parent_bottom = RelativeLayout.ALIGN_PARENT_TOP;
			}
			
			View view = findViewById(R.id.gallery);
			view.setVisibility(View.VISIBLE);			
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
			layoutParams.addRule(align_parent_left, 0);
			layoutParams.addRule(align_parent_right, RelativeLayout.TRUE);
			layoutParams.addRule(align_parent_top, RelativeLayout.TRUE);
			layoutParams.addRule(align_parent_bottom, 0);
			layoutParams.addRule(left_of, 0);
			layoutParams.addRule(right_of, 0);
			view.setLayoutParams(layoutParams);
			view.setRotation(ui_rotation);



			view = findViewById(R.id.focus_mode);
			view.setVisibility(View.VISIBLE);
			layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
			layoutParams.addRule(align_parent_top, RelativeLayout.TRUE);
			layoutParams.addRule(align_parent_bottom, 0);
			layoutParams.addRule(left_of, R.id.gallery);
			layoutParams.addRule(right_of, 0);
			view.setLayoutParams(layoutParams);
			view.setRotation(ui_rotation);

			view = findViewById(R.id.flash);
			view.setVisibility(View.VISIBLE);
			layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
			layoutParams.addRule(align_parent_top, RelativeLayout.TRUE);
			layoutParams.addRule(align_parent_bottom, 0);
			layoutParams.addRule(left_of, R.id.focus_mode);
			layoutParams.addRule(right_of, 0);
			view.setLayoutParams(layoutParams);
			view.setRotation(ui_rotation);

			view = findViewById(R.id.switch_camera);
			view.setVisibility(View.VISIBLE);
			layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
			layoutParams.addRule(align_parent_left, 0);
			layoutParams.addRule(align_parent_right, 0);
			layoutParams.addRule(align_parent_top, RelativeLayout.TRUE);
			layoutParams.addRule(align_parent_bottom, 0);
			layoutParams.addRule(left_of, R.id.flash);
			layoutParams.addRule(right_of, 0);
			view.setLayoutParams(layoutParams);
			view.setRotation(ui_rotation);

			view = findViewById(R.id.take_photo);
			view.setVisibility(View.VISIBLE);
			layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
			layoutParams.addRule(align_parent_left, 0);
			layoutParams.addRule(align_parent_right, RelativeLayout.TRUE);
			view.setLayoutParams(layoutParams);
			view.setRotation(ui_rotation);


		}
		else {
			View view = findViewById(R.id.switch_camera);
			view.setVisibility(View.VISIBLE) ;
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
			layoutParams.addRule(align_parent_left, RelativeLayout.TRUE);
			layoutParams.addRule(align_parent_right, 0);
			layoutParams.addRule(align_parent_top, RelativeLayout.TRUE);
			layoutParams.addRule(align_parent_bottom, 0);
			layoutParams.addRule(left_of, 0);
			layoutParams.addRule(right_of, 0);
			view.setLayoutParams(layoutParams);
			view.setRotation(ui_rotation);

			view = findViewById(R.id.flash);
			view.setVisibility(View.VISIBLE) ;
			layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
			layoutParams.addRule(align_parent_top, RelativeLayout.TRUE);
			layoutParams.addRule(align_parent_bottom, 0);
			layoutParams.addRule(left_of, 0);
			layoutParams.addRule(right_of, R.id.switch_camera);
			view.setLayoutParams(layoutParams);
			view.setRotation(ui_rotation);

			view = findViewById(R.id.focus_mode);
			view.setVisibility(View.VISIBLE) ;
			layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
			layoutParams.addRule(align_parent_top, RelativeLayout.TRUE);
			layoutParams.addRule(align_parent_bottom, 0);
			layoutParams.addRule(left_of, 0);
			layoutParams.addRule(right_of, R.id.flash);
			view.setLayoutParams(layoutParams);
			view.setRotation(ui_rotation);


			view = findViewById(R.id.gallery);
			view.setVisibility(View.VISIBLE) ;
			layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
			layoutParams.addRule(align_parent_top, RelativeLayout.TRUE);
			layoutParams.addRule(align_parent_bottom, 0);
			layoutParams.addRule(left_of, 0);
			layoutParams.addRule(right_of, R.id.focus_mode);
			view.setLayoutParams(layoutParams);
			view.setRotation(ui_rotation);
			
			view = findViewById(R.id.take_photo);
			view.setVisibility(View.VISIBLE) ;
			layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
			layoutParams.addRule(align_parent_left, RelativeLayout.TRUE);
			layoutParams.addRule(align_parent_right, 0);
			view.setLayoutParams(layoutParams);
			view.setRotation(ui_rotation);

		}
		
		{
			ImageView view = (ImageView)findViewById(R.id.take_photo);
			if( preview != null ) {
				view.setImageResource(preview.isVideo() ? R.drawable.take_video : R.drawable.take_photo);
			}
		}
    }

    private void onOrientationChanged(int orientation) {
    	
		if( orientation == OrientationEventListener.ORIENTATION_UNKNOWN )
			return;
		int diff = Math.abs(orientation - current_orientation);
		if( diff > 180 )
			diff = 360 - diff;
		if( diff > 60 ) {
		    orientation = (orientation + 45) / 90 * 90;
		    orientation = orientation % 360;
		    if( orientation != current_orientation ) {
			    this.current_orientation = orientation;
				layoutUI();
		    }
		}
	}

    public void clickedTakePhoto(View view) {
    	this.takePicture();
    }

    public void clickedSwitchCamera(View view) {
		if( MyDebug.LOG )
			Log.d(TAG, "clickedSwitchCamera");
		this.preview.switchCamera();
    }

    public void clickedSwitchVideo(View view) {
		this.preview.switchVideo(true, true);
    }

    public void clickedFlash(View view) {
    	this.preview.cycleFlash();
    }
    
    public void clickedFocusMode(View view) {
    	this.preview.cycleFocusMode();
    }

    
    class Media {
    	public long id;
    	public boolean video;
    	public Uri uri;
    	public long date;
    	public int orientation;

    	Media(long id, boolean video, Uri uri, long date, int orientation) {
    		this.id = id;
    		this.video = video;
    		this.uri = uri;
    		this.date = date;
    		this.orientation = orientation;
    	}
    }
    
    private Media getLatestMedia(boolean video) {
    	Media media = null;
		Uri baseUri = video ? Video.Media.EXTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Uri query = baseUri.buildUpon().appendQueryParameter("limit", "1").build();
		String [] projection = video ? new String[] {VideoColumns._ID, VideoColumns.DATE_TAKEN} : new String[] {ImageColumns._ID, ImageColumns.DATE_TAKEN, ImageColumns.ORIENTATION};
		String selection = video ? "" : ImageColumns.MIME_TYPE + "='image/jpeg'";
		String order = video ? VideoColumns.DATE_TAKEN + " DESC," + VideoColumns._ID + " DESC" : ImageColumns.DATE_TAKEN + " DESC," + ImageColumns._ID + " DESC";
		Cursor cursor = null;
		try {
			cursor = getContentResolver().query(query, projection, selection, null, order);
			if( cursor != null && cursor.moveToFirst() ) {
				long id = cursor.getLong(0);
				long date = cursor.getLong(1);
				int orientation = video ? 0 : cursor.getInt(2);
				Uri uri = ContentUris.withAppendedId(baseUri, id);
				if( MyDebug.LOG )
					Log.d(TAG, "found most recent uri for " + (video ? "video" : "images") + ": " + uri);
				media = new Media(id, video, uri, date, orientation);
			}
		}
		finally {
			if( cursor != null ) {
				cursor.close();
			}
		}
		return media;
    }
    
    private Media getLatestMedia() {
		Media image_media = getLatestMedia(false);
		Media video_media = getLatestMedia(true);
		Media media = null;
		if( image_media != null && video_media == null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "only found images");
			media = image_media;
		}else if( image_media == null && video_media != null ) {
			media = video_media;
		}
		else if( image_media != null && video_media != null ) {
			if( image_media.date >= video_media.date ) {
				media = image_media;
			}else {
				media = video_media;
			}
		}
		return media;
    }

    public void updateGalleryIconToBlank() {
		ImageView galleryButton = (ImageView) this.findViewById(R.id.gallery);
	    int bottom = galleryButton.getPaddingBottom();
	    int top = galleryButton.getPaddingTop();
	    int right = galleryButton.getPaddingRight();
	    int left = galleryButton.getPaddingLeft();
	    galleryButton.setImageBitmap(null);
		galleryButton.setImageResource(R.drawable.gallery);
		galleryButton.setPadding(left, top, right, bottom);
		gallery_bitmap = null;
    }
    
    public void updateGalleryIconToBitmap(Bitmap bitmap) {
		ImageView galleryButton = (ImageView) this.findViewById(R.id.gallery);
		galleryButton.setImageBitmap(bitmap);
		gallery_bitmap = bitmap;
    }
    
    public void updateGalleryIcon() {
    	long time_s = System.currentTimeMillis();
    	Media media = getLatestMedia();
		Bitmap thumbnail = null;
    	if( media != null ) {
    		if( media.video ) {
    			  thumbnail = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), media.id, MediaStore.Video.Thumbnails.MINI_KIND, null);
    		}
    		else {
    			  thumbnail = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), media.id, MediaStore.Images.Thumbnails.MINI_KIND, null);
    		}
    		if( thumbnail != null ) {
	    		if( media.orientation != 0 ) {
	    			
	    			Matrix matrix = new Matrix();
	    			matrix.setRotate(media.orientation, thumbnail.getWidth() * 0.5f, thumbnail.getHeight() * 0.5f);
	    			try {
	    				Bitmap rotated_thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
	        		    if( rotated_thumbnail != thumbnail ) {
	        		    	thumbnail.recycle();
	        		    	thumbnail = rotated_thumbnail;
	        		    }
	    			}
	    			catch(Throwable t) {
		    			if( MyDebug.LOG )
		    				Log.d(TAG, "failed to rotate thumbnail");
	    			}
	    		}
    		}
    	}
    	
    	if( thumbnail != null ) {
			updateGalleryIconToBitmap(thumbnail);
    	}
    	else {
			updateGalleryIconToBlank();
    	}
    }
    
    public void clickedGallery(View view) {
		Uri uri = null;
		Media media = getLatestMedia();
		if( media != null ) {
			uri = media.uri;
		}

		if( uri != null ) {
			try {
				ContentResolver cr = getContentResolver();
				ParcelFileDescriptor pfd = cr.openFileDescriptor(uri, "r");
				if( pfd == null ) {
					uri = null;
				}
				pfd.close();
			}
			catch(IOException e) {
				uri = null;
			}
		}
		if( uri == null ) {
			uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		}
		
		final String REVIEW_ACTION = "com.android.camera.action.REVIEW";
		try {
			Intent intent = new Intent(REVIEW_ACTION, uri);
			this.startActivity(intent);
		}catch(ActivityNotFoundException e) {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if( intent.resolveActivity(getPackageManager()) != null ) {
				this.startActivity(intent);
			}else{
				preview.showToast(null, "No Gallery app available");
			}
		}
    }



    public void clickedTrash(View view) {
    	final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
		    	updateGalleryIcon();
			}
		}, 500);
    }

    private void takePicture() {
    	this.preview.takePicturePressed();
    }

	@Override
	protected void onSaveInstanceState(Bundle state) {
	    super.onSaveInstanceState(state);
	}

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if( event.getAction() == KeyEvent.ACTION_DOWN ) {
            int keyCode = event.getKeyCode();
            switch( keyCode ) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
        		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        		String volume_keys = sharedPreferences.getString("preference_volume_keys", "volume_take_photo");
        		if( volume_keys.equals("volume_take_photo") ) {
                	takePicture();
                    return true;
        		}
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void broadcastFile(File file) {
    	if( file.isDirectory() ) {
    		
    	}else {
        	MediaScannerConnection.scanFile(this, new String[] { file.getAbsolutePath() }, null,
        			new MediaScannerConnection.OnScanCompletedListener() {
    		 		public void onScanCompleted(String path, Uri uri) {
    		 			if( MyDebug.LOG ) {
    		 				Log.d("ExternalStorage", "Scanned " + path + ":");
    		 				Log.d("ExternalStorage", "-> uri=" + uri);
    		 			}
    		 		}
    			}
    		);
    	}
	}
    
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public File getImageFolder() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String folder_name = sharedPreferences.getString("preference_save_location", "OpenCamera");
		File file = null;
		if( folder_name.length() > 0 && folder_name.lastIndexOf('/') == folder_name.length()-1 ) {
			folder_name = folder_name.substring(0, folder_name.length()-1);
		}
		if( folder_name.startsWith("/") ) {
			file = new File(folder_name);
		}
		else {
	        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), folder_name);
		}
        return file;
    }
    
    @SuppressLint("SimpleDateFormat")
	public File getOutputMediaFile(int type){

    	File mediaStorageDir = getImageFolder();
        if( !mediaStorageDir.exists() ) {
            if( !mediaStorageDir.mkdirs() ) {
        		if( MyDebug.LOG )
        			Log.e(TAG, "failed to create directory");
                return null;
            }
            broadcastFile(mediaStorageDir);
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String index = "";
        File mediaFile = null;
        for(int count=1;count<=100;count++) {
            if( type == MEDIA_TYPE_IMAGE ) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + index + ".jpg");
            }
            else if( type == MEDIA_TYPE_VIDEO ) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "VID_"+ timeStamp + index + ".mp4");
            }
            else {
                return null;
            }
            if( !mediaFile.exists() ) {
            	break;
            }
            index = "_" + count; // try to find a unique filename
        }
        return mediaFile;
    }
    
    public boolean supportsAutoStabilise() {
    	return this.supports_auto_stabilise;
    }

    public boolean supportsForceVideo4K() {
    	return this.supports_force_video_4k;
    }

    @SuppressWarnings("deprecation")
	public long freeMemory() { // return free memory in MB
    	try {
    		File image_folder = this.getImageFolder();
	        StatFs statFs = new StatFs(image_folder.getAbsolutePath());
	        long blocks = statFs.getAvailableBlocks();
	        long size = statFs.getBlockSize();
	        long free  = (blocks*size) / 1048576;
	        return free;
    	}
    	catch(IllegalArgumentException e) {
    		return -1;
    	}
    }

}

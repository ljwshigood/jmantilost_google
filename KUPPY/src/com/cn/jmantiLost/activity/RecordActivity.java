package com.cn.jmantiLost.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cn.jmantiLost.R;
import com.cn.jmantiLost.adapter.RecordMenuAdapter;
import com.cn.jmantiLost.adapter.RecordMenuAdapter.IMediaListener;
import com.cn.jmantiLost.adapter.RecordMenuAdapter.IShowDialog;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.RecordInfo;
import com.cn.jmantiLost.service.BluetoothLeService;
import com.cn.jmantiLost.util.EncriptyUtils;
import com.cn.jmantiLost.util.FileUtil;
import com.cn.jmantiLost.util.RecordManager;

public class RecordActivity extends Activity implements OnClickListener ,IMediaListener,IShowDialog{

	private Context mContext;

	private CheckBox mCbRecord;

	private RecordManager mRecordManger;

	private ImageView mRvRecord;
	
	private ImageView mIvBack ;
	
	private void getIntentData(){
		Intent intent = getIntent();
	}
	
	public Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
    		mMediaStatusCode = mRecordManger.startRecord();
    		mCbRecord.setBackgroundResource(R.drawable.ic_record_pause);
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		mContext = RecordActivity.this;
		mRecordManger = RecordManager.getInstance(mContext);
		mMediaPlayer = new MediaPlayer();
		getIntentData();
		initView();
		makeGattUpdateIntentFilter();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		initRecordFiles();		
		mRecordManger.setmRvRecord(mRvRecord);
		
		AppContext.isAlarm = false ;
	}
	
	private Vibrator vibrator;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(mGattUpdateReceiver);
		
		if(vibrator != null){
    		vibrator.cancel();
    	}
		
		AppContext.isAlarm = true ;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveRecord();
	}
	
	private SwipeMenuListView mSwipeList ;

	private void initView() {
		mSwipeList = (SwipeMenuListView)findViewById(R.id.lv_record_list) ;
		mIvBack  =  (ImageView)findViewById(R.id.iv_back);
		mRvRecord = (ImageView) findViewById(R.id.rv_record);
		mCbRecord = (CheckBox) findViewById(R.id.cb_record);
		mCbRecord.setOnClickListener(this);
		mIvBack.setOnClickListener(this);
	}

	private boolean isSave = false ;
	
	private int mMediaStatusCode = -1 ;

	private ArrayList<RecordInfo> mRecordList ;
	
	private RecordMenuAdapter mMenuAdapter ;
	
	SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(mContext.getResources().getDrawable(R.drawable.ic_delete_press));
            deleteItem.setWidth(200);
            menu.addMenuItem(deleteItem);
        }
    };
	
	private void initRecordFiles() {
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "BKYY") ;
		String scanFilePath = file.getAbsolutePath() ;
		mRecordList = FileUtil.getRecordFiles(scanFilePath);
		mMenuAdapter = new RecordMenuAdapter(mContext, mRecordList, this,null);
		mMenuAdapter.setmIShowDialog(this);
		mSwipeList.setAdapter(mMenuAdapter);
		mSwipeList.setOnItemClickListener(mMenuAdapter);
		
		mSwipeList.setMenuCreator(creator);
		mSwipeList.setAdapter(mMenuAdapter);
		
		mSwipeList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            	RecordInfo info = mRecordList.get(index);
				FileUtil.deleteFile(new File(info.getFilePath()));
				mRecordList.remove(info);
				mMenuAdapter.notifyDataSetChanged() ;
                return false;
            }
        });
		 
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.iv_record_menu:
			if(mMediaStatusCode == -1){
				Intent intent = new Intent(mContext, RecordMenuActivity.class);
				startActivity(intent);
				return ;
			}else if(mMediaStatusCode != 2 && !isSave){
				saveRecord();
				mMediaStatusCode = -1 ;
				isSave = true ;
				return ;
			}else if(isSave){
				Intent intent = new Intent(mContext, RecordMenuActivity.class);
				startActivity(intent);
				return ;
			}
			break;
		case R.id.cb_record :
			
			if(mCbRecord.isChecked()){
				mRecordManger.startRecord() ;
			}else{
				mRecordManger.saveRecord() ;
				initRecordFiles() ;
			}
			
			break ;
		case R.id.iv_back:
			finish();
			break ;
		default:
			break;
		}
	}

	private int saveRecord() {
		int ret = 0 ;
		ret = mRecordManger.saveRecord();
		isSave = true;
		if (timer != null) {
			timer.cancel();
		}
		return ret ;
	}


	private Timer timer;

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
					if(mMediaStatusCode == -1){
			    		mCbRecord.setChecked(true);
			    		mMediaStatusCode = mRecordManger.startRecord();
			    	}else{
			    		mCbRecord.setChecked(false);
			    		saveRecord();
			    		//mRecordManger.saveRecord();
			    		initRecordFiles() ;
			    		mMediaStatusCode = -1 ;
			    		isSave = true ;
			    	}
				}
		    
		    	
			}
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_RSSI);
		return intentFilter;
	}

	@Override
	public void play(int position, String path) {
		String filePath = mRecordList.get(position).getFilePath();
		playMusic(filePath);
	}
	
	/* MediaPlayer对象 */
	public MediaPlayer mMediaPlayer = null;
	private void playMusic(String path) {
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
			mMediaPlayer.start();

			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer arg0) {
					
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showDialog(final File file,final RecordInfo info) {
		String fileName = file.getName() ;
		String fileNameNon = fileName.substring(0,fileName.lastIndexOf("."));
		final EditText et = new EditText(this);
		et.setText(fileNameNon);
		new AlertDialog.Builder(this).setTitle(mContext.getString(R.string.file_name)).setIcon(android.R.drawable.ic_dialog_info).setView(et).setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				File newFile = FileUtil.renameFile(file, et.getText().toString());
				info.setFilePath(newFile.getAbsolutePath()) ;
				mMenuAdapter.notifyDataSetChanged() ;
			}
		}).setNegativeButton(mContext.getString(R.string.cancel), null).show();	
	}

}

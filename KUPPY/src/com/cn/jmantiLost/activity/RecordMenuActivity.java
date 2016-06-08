package com.cn.jmantiLost.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cn.jmantiLost.R;
import com.cn.jmantiLost.adapter.RecordMenuAdapter;
import com.cn.jmantiLost.adapter.RecordMenuAdapter.IMediaListener;
import com.cn.jmantiLost.adapter.RecordMenuAdapter.ITransferMediaItemList;
import com.cn.jmantiLost.bean.RecordInfo;
import com.cn.jmantiLost.util.FileUtil;

public class RecordMenuActivity extends BaseActivity implements
		OnClickListener, IMediaListener,ITransferMediaItemList {

	private ImageView mIvBack;

	private CheckBox mCbQuite;

	private ImageView mIvDelete;

	private SeekBar mSbPlayer;

	private SwipeMenuListView mLvRecordPlay;

	private RecordMenuAdapter mMenuAdapter;

	Handler mHandler = new Handler();

	private Context mContext;

	AudioManager audioManager = null;

	private CheckBox mCbPlayStatus;

	/* MediaPlayer对象 */
	public MediaPlayer mMediaPlayer = null;

	/* 播放列表 */
	private ArrayList<RecordInfo> mMusicList = new ArrayList<RecordInfo>();

	private TextView mTvTotalTime;

	private TextView mTvStartTime;

	private TextView mTvEndTime;

	private TextView mTvEdit ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_play);
		mContext = RecordMenuActivity.this;
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMediaPlayer = new MediaPlayer();
		initView();
		initData();
	}

	private Boolean mIsComplete = false ;
	

	private void playMusic(String path) {
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
			mMediaPlayer.start();

			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer arg0) {
					mHandler.removeCallbacks(updatesb);
					mSbPlayer.setProgress(0);
					mCbPlayStatus.setChecked(false);
					mIsComplete = true ;
					isPlayer = true ;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initData() {
		String scanFilePath = mContext.getCacheDir().getAbsolutePath() + "/YYT";
		mMusicList = FileUtil.getRecordFiles(scanFilePath);
		mMenuAdapter = new RecordMenuAdapter(mContext, mMusicList, this,this);
		mLvRecordPlay.setAdapter(mMenuAdapter);
		mLvRecordPlay.setOnItemClickListener(mMenuAdapter);
		
		mLvRecordPlay.setMenuCreator(creator);
		mLvRecordPlay.setAdapter(mMenuAdapter);
		
		mLvRecordPlay.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        		/*DatabaseManager.getInstance(mContext).updateKeySet(position+1);
        		mDataBaseManager = DatabaseManager.getInstance(mContext);
        		sortKeySetList();
        		mKeySetAdapter.notifyKeyDataSetChange(mListKeySet);*/
            	
            	RecordInfo info = mMusicList.get(index);
				FileUtil.deleteFile(new File(info.getFilePath()));
				mMusicList.remove(info);
				mMenuAdapter.notifyDataSetChanged() ;
                return false;
            }
        });
		 
	}

	SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setIcon(mContext.getResources().getDrawable(R.drawable.ic_delete_press));
            // set item width
            deleteItem.setWidth(100);
            // set a icon
          //  deleteItem.setIcon(R.drawable.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };
	
	private void initView() {
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mCbQuite = (CheckBox) findViewById(R.id.iv_sperker);
		mIvDelete = (ImageView) findViewById(R.id.iv_delete);
		mSbPlayer = (SeekBar) findViewById(R.id.sb_record_seek);
		mLvRecordPlay = (SwipeMenuListView) findViewById(R.id.lv_record_list);
		mCbPlayStatus = (CheckBox) findViewById(R.id.cb_play_status);
		mSbPlayer.setOnSeekBarChangeListener(sbPlayer);

		mTvTotalTime = (TextView) findViewById(R.id.tv_total);

		mTvStartTime = (TextView) findViewById(R.id.tv_start_time);
		mTvEndTime = (TextView) findViewById(R.id.tv_end_time);

		mTvEdit = (TextView)findViewById(R.id.tv_edit);
		mCbQuite.setChecked(true);
		mTvEdit.setOnClickListener(this);
		mCbPlayStatus.setOnClickListener(this);
		mCbQuite.setOnClickListener(this);
		mIvDelete.setOnClickListener(this);
		mIvBack.setOnClickListener(this);
	}

	private OnSeekBarChangeListener sbPlayer = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mMediaPlayer.seekTo(mSbPlayer.getProgress());
		}

	};

	Runnable start = new Runnable() {

		@Override
		public void run() {
			mMediaPlayer.start();
			mHandler.post(updatesb);
		}

	};
	Runnable updatesb = new Runnable() {

		@Override
		public void run() {
			mTvStartTime.setText(formatSecondTime(mMediaPlayer
					.getCurrentPosition()));
			mSbPlayer.setProgress(mMediaPlayer.getCurrentPosition());
			mTvEndTime.setText(formatSecondTime(getMusicDuration(mPath)- mMediaPlayer.getCurrentPosition()));
			mHandler.postDelayed(updatesb, 1000);
		}
	};

	private String mPath;

	private boolean isSprker = false;

	private boolean isPlayer =false ;
	
	private boolean isEdit = false ;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_delete:
			for(int i = mMusicList.size() -1  ;i >= 0 ;i--){
				if(mMusicList.get(i).isSelect()){
					RecordInfo info = mMusicList.get(i);
					FileUtil.deleteFile(new File(info.getFilePath()));
					mMusicList.remove(info);
				}
			}
			
			for(int i =  0 ;i < mMusicList.size();i++){
				mMusicList.get(i).setVisible(false);
				mMusicList.get(i).setSelect(false);
			}
			mMenuAdapter.notifyDataSetChanged();
			mTvEdit.setText(mContext.getString(R.string.edit));
			break;
		case R.id.iv_sperker:
			isSprker = !isSprker;
			setSpeakerphoneOn(isSprker);
			mCbQuite.setChecked(isSprker);
			break;
		case R.id.cb_play_status:
			isPlayer = !isPlayer;
			if(isPlayer){
				mMediaPlayer.pause();
			}else{
				if(mIsComplete){
					if(mCurrentFilePath == null){
						return ;
					}
					playMusic(mCurrentFilePath);
					initPlayStatus(mCurrentFilePath);
					mIsComplete = false ;
				}
				mMediaPlayer.start();
			}
			break;
		case R.id.tv_edit:
			mMediaPlayer.pause();
			
			isEdit = !isEdit;
			if(isEdit){
				mTvEdit.setText(mContext.getString(R.string.un_edit));
				for(int i =  0 ;i < mMusicList.size();i++){
					mMusicList.get(i).setVisible(true);
					mMusicList.get(i).setSelect(false);
				}
				mMenuAdapter.notifyDataSetChanged();
			}else{
				mTvEdit.setText(mContext.getString(R.string.edit));
				for(int i =  0 ;i < mMusicList.size();i++){
					mMusicList.get(i).setVisible(false);
				}
				mMenuAdapter.notifyDataSetChanged();
			}
			
			break;
		default:
			break;
		}
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(updatesb);
		if(mMediaPlayer != null){
			mMediaPlayer.release();
			mMediaPlayer = null ;
		}
	}
	private void setSpeakerphoneOn(boolean on) {
		if (on) {
			audioManager.setSpeakerphoneOn(true);
		} else {
			audioManager.setSpeakerphoneOn(false);// 关闭扬声器
			audioManager.setRouting(AudioManager.MODE_NORMAL,
					AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
			setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
			audioManager.setMode(AudioManager.MODE_IN_CALL);
		}
	}

	public int getMusicDuration(String filePath) {
		return mMediaPlayer.getDuration();
	}

	public static String formatSecondTime(int millisecond) {
		if (millisecond == 0) {
			return "00:00";
		}
		millisecond = millisecond / 1000;
		int m = millisecond / 60 % 60;
		int s = millisecond % 60;
		return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
	}

	private String mCurrentFilePath ;
	
	@Override
	public void play(int position, String path) {
		mCurrentFilePath = mMusicList.get(position).getFilePath();
		playMusic(mCurrentFilePath);
		initPlayStatus(path);
	}

	private void initPlayStatus(String path) {
		mTvTotalTime.setText(formatSecondTime(getMusicDuration(mPath)));
		mTvEndTime.setText(formatSecondTime(getMusicDuration(mPath)));
		mHandler.post(updatesb);
		mCbPlayStatus.setChecked(true);
		isPlayer =false ;
		mSbPlayer.setMax(getMusicDuration(path));
	}
	
	@Override
	public void transferMediaItemList(ArrayList<RecordInfo> mediaList) {
		this.mMusicList = mediaList ;
	}
}

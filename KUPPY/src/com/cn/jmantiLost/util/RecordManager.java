package com.cn.jmantiLost.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaRecorder.OutputFormat;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.view.RecordingView;

public class RecordManager {

	private static RecordManager mInstance;

	private Context mContext;

	private File myRecAudioFile;

	private File myRecAudioDir;

	private MediaRecorder mMediaRecorder;

	private ArrayAdapter<String> adapter;

	private boolean sdcardExit;

	private String length1 = null;

	private final String SUFFIX = ".amr";

	Button buttonpause;

	private ArrayList<String> list;

	private boolean isPause;

	private boolean inThePause;

	public boolean isInThePause() {
		return inThePause;
	}

	public void setInThePause(boolean inThePause) {
		this.inThePause = inThePause;
	}

	private RecordManager(Context context) {
		this.mContext = context;
		isPause = false;
		inThePause = false;
		list = new ArrayList<String>();
		list.clear();

		sdcardExit = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExit) {
			String pathStr = mContext.getCacheDir().getAbsolutePath() + "/YYT";
			//myRecAudioDir = new File(pathStr);
			myRecAudioDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "BKYY");
			if (!myRecAudioDir.exists()) {
				myRecAudioDir.mkdirs();
			}
		}
	}
	
	private ImageView mRvRecord;
	
	public ImageView getmRvRecord() {
		return mRvRecord;
	}

	public void setmRvRecord(ImageView mRvRecord) {
		this.mRvRecord = mRvRecord;
	}

	public static RecordManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new RecordManager(context);
		}
		return mInstance;
	}

	private void start() {

		try {
			if (!sdcardExit) {
				Toast.makeText(mContext, "请插入SD card", Toast.LENGTH_LONG)
						.show();
				return;
			}
			String mMinute1 = getTime();
			myRecAudioFile = new File(myRecAudioDir, mMinute1 + SUFFIX);
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setOutputFormat(OutputFormat.RAW_AMR);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			if (!myRecAudioFile.exists()) {
				try {
					myRecAudioFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			mMediaRecorder.setOutputFile(myRecAudioFile.getAbsolutePath());
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			
			handler.post(mUpdateMicStatusTimer);

			mMediaRecorder.setOnInfoListener(new OnInfoListener() {

				@Override
				public void onInfo(MediaRecorder mr, int what, int extra) {
					int m = mr.getMaxAmplitude();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getMyRecAudioFile() {
		return myRecAudioFile;
	}

	public void setMyRecAudioFile(File myRecAudioFile) {
		this.myRecAudioFile = myRecAudioFile;
	}

	private final Handler handler = new Handler();
	
	private Runnable mUpdateMicStatusTimer = new Runnable() {
		public void run() {
			updateMicStatus();
			handler.postDelayed(mUpdateMicStatusTimer, 100);
		}
	};

	private void updateMicStatus() {
		if(mMediaRecorder == null){
			return ;
		}
		if(mRvRecord == null){
			return ;
		}
		int m = mMediaRecorder.getMaxAmplitude();
		int x = (6 * m / 32768);// 通过这种算法计算出的音量大小在1-7之间
		switch (x) {
		case 1:
			mRvRecord.setImageResource(R.drawable.record_animate_14) ;
			mRvRecord.invalidate();
			break;
		case 2:
			mRvRecord.setImageResource(R.drawable.record_animate_13) ;
			mRvRecord.invalidate();
			break;
		case 3:
			mRvRecord.setImageResource(R.drawable.record_animate_12) ;
			mRvRecord.invalidate();
			break;
		case 4:
			mRvRecord.setImageResource(R.drawable.record_animate_11) ;
			mRvRecord.invalidate();
			break;
		case 5:
			mRvRecord.setImageResource(R.drawable.record_animate_10) ;
			mRvRecord.invalidate();
			break;
		case 6:
			mRvRecord.setImageResource(R.drawable.record_animate_09) ;
			mRvRecord.invalidate();
			break;
		case 7:
			mRvRecord.setImageResource(R.drawable.record_animate_08) ;
			mRvRecord.invalidate();
			break;
		default:
			mRvRecord.setImageResource(R.drawable.record_animate_07) ;
			mRvRecord.invalidate();
			break;
		}
	}
	

	private String getTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH：mm：ss");
		Date curDate = new Date(System.currentTimeMillis());
		String time = formatter.format(curDate);
		return time;
	}
	
	private File mFileFinal ;
	
	public File getmFileFinal() {
		return mFileFinal;
	}

	public void setmFileFinal(File mFileFinal) {
		this.mFileFinal = mFileFinal;
	}

	private void getInputCollection(List list, boolean isAddLastRecord) {

		String mMinute1 = getTime();
		mFileFinal = new File(myRecAudioDir, mMinute1 + SUFFIX);
		FileOutputStream fileOutputStream = null;

		if (!mFileFinal.exists()) {
			try {
				mFileFinal.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fileOutputStream = new FileOutputStream(mFileFinal);

		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < list.size(); i++) {
			File file = new File((String) list.get(i));
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				byte[] myByte = new byte[fileInputStream.available()];
				int length = myByte.length;

				if (i == 0) {
					while (fileInputStream.read(myByte) != -1) {
						fileOutputStream.write(myByte, 0, length);
					}
				}

				else {
					while (fileInputStream.read(myByte) != -1) {

						fileOutputStream.write(myByte, 6, length - 6);
					}
				}
				fileOutputStream.flush();
				fileInputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		deleteListRecord(isAddLastRecord);
	}

	private void deleteListRecord(boolean isAddLastRecord) {
		for (int i = 0; i < list.size(); i++) {
			File file = new File((String) list.get(i));
			if (file.exists()) {
				file.delete();
			}
		}
		if (isAddLastRecord) {
			myRecAudioFile.delete();
		}
	}

	protected int  recodeStop() {
		if (mMediaRecorder != null) {
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
		return 1 ;
	}

	private boolean isSave;

	public boolean isSave() {
		return isSave;
	}

	public void setSave(boolean isSave) {
		this.isSave = isSave;
	}

	/**
	 * @description: 负责暂停和恢复录音
	 * 
	 */
	public int pauseRecord() {
		int ret = 0 ;
		isPause = true ;
		if (inThePause) {
			ret = startRecord();
			inThePause = false;
			Log.e("pauserRecord","##################start");
			
		} else {
			list.add(myRecAudioFile.getPath());
			inThePause = true;
			ret = recodeStop();
			Log.e("pauserRecord","##################recodeStop");
		}
		return ret ;
	}

	/**
	 * @description：保存录音文件
	 * 
	 */
	public int saveRecord() {
		if (isPause) {
			if (inThePause) { // 如果是在终止状态下
				recodeStop();
				getInputCollection(list, false);
			} else {
				list.add(myRecAudioFile.getPath());
				recodeStop();
				getInputCollection(list, true);
			}
			isPause = false;
			inThePause = false;
		} else {
			if (myRecAudioFile != null && mMediaRecorder != null) {
				mMediaRecorder.stop();
				mMediaRecorder.release();
				mMediaRecorder = null;
			}
		}
		
		handler.removeCallbacks(mUpdateMicStatusTimer);
		
		isSave = true;
		
		return 2 ;
	}

	/**
	 * @return code : 0 start 1: pause : 2 : save
	 * @descripton: 只调用一次
	 */
	public int startRecord() {
		start();
		return 0 ;
	}
}

package com.cn.jmantiLost.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RemoteViews;

import com.cn.jmantiLost.R;

public class TestActivity extends BaseActivity {
	
	private Button mBtn ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		mBtn = (Button)findViewById(R.id.btn);
		mBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showButtonNotify();
			}
		});
		
	}
	
	public final static String ACTION_BUTTON = "com.notifications.intent.action";
	
	public final static String INTENT_BUTTONID_TAG = "ButtonId";
	
	
	public PendingIntent getDefalutIntent(int flags){
		PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
		return pendingIntent;
	}
	
	public NotificationManager mNotificationManager;
	
	public final static int BUTTON_PRIEW_ID = 1;
	
	public void showButtonNotify(){
		
		NotificationCompat.Builder mBuilder = new Builder(this);
		RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_custom_button);
	
		Intent buttonIntent = new Intent(ACTION_BUTTON);

		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PRIEW_ID);

		PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.iv_notify, intent_prev);
		
		mRemoteViews.setImageViewBitmap(R.id.iv_notify,BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher));
		
		
		mBuilder.setContent(mRemoteViews)
		.setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
		.setWhen(System.currentTimeMillis())
		.setTicker("测试通知")
		.setPriority(Notification.PRIORITY_DEFAULT)
		.setOngoing(true)
		.setAutoCancel(true)
		.setSmallIcon(R.drawable.ic_launcher);
		
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		
		mNotificationManager.notify(200, notify);
	}
	
	
	
}
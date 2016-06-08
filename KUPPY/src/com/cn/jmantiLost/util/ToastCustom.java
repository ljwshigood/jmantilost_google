package com.cn.jmantiLost.util;



import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.jmantiLost.R;

public class ToastCustom {
	public static Toast toast;
	static TextView title = null ;
	static View layout = null ;
	static LayoutInflater inflater;
	/** 
	 * Description	:
	 * @param mActivity
	 * @param context 
	 * @param text 
	 * @param time
	 * @see 		:[class/class#field/class#method]
	 */
	public static void toastCustom(Activity mActivity,Context context, String text, int time) {
		
		if (toast == null) {
			inflater = mActivity.getLayoutInflater();
			layout = inflater.inflate(R.layout.toast, null);
			title = (TextView) layout.findViewById(R.id.tv_toast);
			title.setText(text);
			toast = new Toast(context);
			toast.setView(layout);
			toast.setDuration(time);
			int hight = mActivity.getWindowManager().getDefaultDisplay().getHeight()/4;
			toast.setGravity(Gravity.CENTER, 0, hight);
			
		} else {
			 title.setText(text);
			 toast.setView(layout);
			 toast.setDuration(time);
		}
		toast.show();
	}
}

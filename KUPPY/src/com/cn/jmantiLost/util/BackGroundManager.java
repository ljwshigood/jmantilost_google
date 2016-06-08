package com.cn.jmantiLost.util;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

public class BackGroundManager {

	public static boolean isCurrentActivity(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		RunningTaskInfo info = manager.getRunningTasks(1).get(0);
		String shortClassName = info.topActivity.getShortClassName(); // 类名
		String className = info.topActivity.getClassName(); // 完整类名
		String packageName = info.topActivity.getPackageName(); // 包名
		if (packageName.equals("com.iwit.antilost")) {
			return false;
		}
		return true;
	}
}

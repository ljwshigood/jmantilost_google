package com.cn.jmantiLost.util;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.cn.jmantiLost.bean.AppInfo;

public class AppManager {
	// 使用列表存储已安装的非系统应用程序
	private static ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
	// 获取手机已安装的所有应用package的信息(其中包括用户自己安装的，还有系统自带的)
	public static ArrayList<AppInfo> getInstallAppInfo(Context context){
		
		List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			AppInfo tmpInfo = new AppInfo();
			tmpInfo.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
			tmpInfo.packageName = packageInfo.packageName;
			tmpInfo.versionName = packageInfo.versionName;
			tmpInfo.versionCode = packageInfo.versionCode;
			tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
			// 如果属于非系统程序，则添加到列表显示
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appList.add(tmpInfo);
			}

		}
		return appList ;

	}
	
	public static ArrayList<AppInfo> getAllApplicationInfo(Context context) {
		ArrayList<AppInfo> items = new ArrayList<AppInfo>();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager manager = context.getPackageManager();
		List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
		for (ResolveInfo app : apps) {
			//过滤自己应用
			if(!app.activityInfo.packageName.equals("com.publicnumber.satellite")){
				AppInfo tmpInfo = new AppInfo();
				tmpInfo.appName = app.activityInfo.loadLabel(manager).toString();
				tmpInfo.packageName = app.activityInfo.packageName;
				tmpInfo.appIcon = app.loadIcon(manager);
				items.add(tmpInfo);
			}
				

		}
		return items;
	}
	
	 public static String getProgramNameByPackageName(Context context,String packageName) {
         PackageManager pm = context.getPackageManager();
         String name = null;
         try {
             name = pm.getApplicationLabel(pm.getApplicationInfo(packageName,PackageManager.GET_META_DATA)).toString();
             pm.getApplicationIcon(packageName);
         } catch (NameNotFoundException e) {
             e.printStackTrace();
         }
         return name;
	}
	 
	public static Drawable getProgramBitmapByPackageName(Context context,String packageName) {
         PackageManager pm = context.getPackageManager();
         Drawable draweable = null ;
         try {
        	 draweable = pm.getApplicationIcon(packageName);
         } catch (NameNotFoundException e) {
             e.printStackTrace();
         }
         return draweable;
	}

}

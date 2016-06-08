package com.cn.jmantiLost.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FomatTimeUtil {

	public static String getFormatTime(long time) {
		long millisecond = time % 1000;
		long second = (time / 1000) % 60;
		long minute = time / 1000 / 60;

		String strMillisecond = "" + (millisecond / 100);
		String strSecond = ("00" + second)
				.substring(("00" + second).length() - 2);
		String strMinute = ("00" + minute)
				.substring(("00" + minute).length() - 2);

		return strMinute + ":" + strSecond;
	}

	public static boolean isInDisturb(String strBeforTime,String strEndTime) {
		boolean flag = false;
		Date time = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
		Date stringFormatTime = StringToDate(sd.format(time));
		Date beforTime = StringToDate(strBeforTime);
		Date endTime  = StringToDate(strEndTime);
		if(stringFormatTime.after(beforTime) && stringFormatTime.before(endTime)){
			return true ;
		}
		return flag;
	}

	public static Date StringToDate(String s) {
		Date time = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
		try {
			time = sd.parse(s);
		} catch (ParseException e) {
			System.out.println("输入的日期格式有误！");
		}
		return time;
	}
	
	public static Date String2Date(String s) {
		Date time = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
		try {
			time = sd.parse(s);
		} catch (ParseException e) {
			System.out.println("输入的日期格式有误！");
		}
		return time;
	}

}

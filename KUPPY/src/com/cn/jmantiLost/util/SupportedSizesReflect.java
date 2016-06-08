package com.cn.jmantiLost.util;

/**
 * SupportedSizesReflect.java
 * 版权所有(C) 2013 
 * 创建:cuiran 2013-7-22 下午4:54:22
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.Surface;
import android.view.WindowManager;

/**
 * 
 * 
 */
public class SupportedSizesReflect {

	private static Method Parameters_getSupportedPreviewSizes = null;
	private static Method Parameters_getSupportedPictureSizes = null;

	static {
		initCompatibility();
	};

	private static void initCompatibility() {
		try {
			Parameters_getSupportedPreviewSizes = Camera.Parameters.class.getMethod(
					"getSupportedPreviewSizes", new Class[] {});

			Parameters_getSupportedPictureSizes = Camera.Parameters.class.getMethod(
					"getSupportedPictureSizes", new Class[] {});

		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
			Parameters_getSupportedPreviewSizes = Parameters_getSupportedPictureSizes = null;
		}
	}

	/**
	 * Android 2.1之后有效
	 * @param p
	 * @return Android1.x返回null
	 */
	public static List<Size> getSupportedPreviewSizes(Camera.Parameters p) {
		return getSupportedSizes(p, Parameters_getSupportedPreviewSizes);
	}

	public static List<Size> getSupportedPictureSizes(Camera.Parameters p){
		return getSupportedSizes(p, Parameters_getSupportedPictureSizes);
	}	

	@SuppressWarnings("unchecked")
	private static List<Size> getSupportedSizes(Camera.Parameters p, Method method){
		try {
			if (method != null) {
				return (List<Size>) method.invoke(p);
			} else {
				return null;
			}
		} catch (InvocationTargetException ite) {
			Throwable cause = ite.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else if (cause instanceof Error) {
				throw (Error) cause;
			} else {
				throw new RuntimeException(ite);
			}
		} catch (IllegalAccessException ie) {
			return null;
		}
	}
	
	public static Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}
	
	
	public static Size getOptimalPictureSize(List<Size> sizes) {
		int height = sizes.get(0).height;
		Size optimalSize = sizes.get(0);
		if (null != sizes && 0 < sizes.size()) { 
			
		    for (int i = 0; i < sizes.size(); i++) { 
		        Camera.Size size = (Camera.Size) sizes.get(i); 
		        int sizeheight = size.height; 
		        int sizewidth = size.width; 
		        if(height < sizes.get(i).height){
		        	height = sizes.get(i).height ;
		        	optimalSize = sizes.get(i);
		        }
		    } 
		}
		return optimalSize ;
	}
	
	// 获取显示方向
	public static int getDisplayRotation(Context context) {

		int rotation = 0;
		rotation = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getRotation();
		switch (rotation) {
		case Surface.ROTATION_0:
			return 0;
		case Surface.ROTATION_90:
			return 90;
		case Surface.ROTATION_180:
			return 180;
		case Surface.ROTATION_270:
			return 270;
		}
		return 0;
	}

}

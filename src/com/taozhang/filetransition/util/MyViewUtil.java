package com.taozhang.filetransition.util;

import android.app.Activity;
import android.util.DisplayMetrics;

public class MyViewUtil {
	/**
	 * 获取手机分辨率
	 * 
	 * @param mActivity
	 * @return
	 */
	public static int getScreenDPI(Activity mActivity) {
		DisplayMetrics metric = getScreenMetrics(mActivity);
		int dpi = metric.densityDpi;
		return dpi;
	}

	public static int getScreenWidthPixels(Activity mActivity) {
		DisplayMetrics metric = getScreenMetrics(mActivity);
		int widthPixels = metric.widthPixels; // 屏幕宽度（像素）
		return widthPixels;
	}

	public static int getScreenHeightPixels(Activity mActivity) {
		DisplayMetrics metric = getScreenMetrics(mActivity);
		int heightPixels = metric.heightPixels; // 屏幕高度（像素）
		return heightPixels;
	}

	/**
	 * 由Activity获取屏幕度量
	 * 
	 * @param mActivity
	 * @return
	 */
	private static DisplayMetrics getScreenMetrics(Activity mActivity) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics;
	}
}

package com.taozhang.filetransition.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.ScanSystemFile;

public class App extends Application {

	public static App instance;
	public static Context context;
	public static Map<String, FileInfo> map_file_application;
	public static boolean isCopy = false;
	public static boolean isMove = false;
	public static String cutPath;
	public static int cutPosition = -1;
	public static FileInfo fileInfo = new FileInfo();
	public static ContentResolver cr;
	public static int SCREEN_WIDTH;
	/**
	 * 系统上所有的音乐文件
	 */
	public static ArrayList<FileInfo> sysMusics;
	public static ArrayList<FileInfo> sysVideo;
	public static ArrayList<FileInfo> sysOffice;
	/**
	 * 标记是否正在加载
	 */
	public static boolean isMusicScaning = true;
	public static boolean isVideoScaning = true;
	public static boolean isOfficeScaning = true;
	public static SharedPreferences mPreferences;

	public SharedPreferences getMySharedPreferences() {
		mPreferences = context.getSharedPreferences("fileTransitionsp", MODE_PRIVATE);
		return mPreferences;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		SCREEN_WIDTH = Math.min(point.x, point.y);

		context = getApplicationContext();
		getMySharedPreferences();

		cr = context.getContentResolver();
		// 创建文件夹
		// File dir = new File(Environment.getExternalStorageDirectory(),
		// "fileTransition");
		File dir = new File(Constant.PROJECTBASE_PATH);
		if (!dir.exists()) {
			boolean mkdirs = dir.mkdirs();
			Log.e("mkdirs", mkdirs + "");
		}
		dir = new File(Constant.BASEPATH_MUSIC);
		if (!dir.exists())
			dir.mkdirs();

		dir = new File(Constant.BASEPATH_APK);
		if (!dir.exists())
			dir.mkdirs();

		dir = new File(Constant.BASEPATH_OFFICE);
		if (!dir.exists())
			dir.mkdirs();

		dir = new File(Constant.BASEPATH_PIC);
		if (!dir.exists())
			dir.mkdirs();

		dir = new File(Constant.BASEPATH_VIDEO);
		if (!dir.exists())
			dir.mkdirs();

		dir = new File(Constant.BASEPATH_ELSE);
		if (!dir.exists())
			dir.mkdirs();

		dir = new File(Constant.DATABASE_PATH);
		if (!dir.exists())
			dir.mkdirs();

		new Thread() {
			public void run() {
				// video
				sysVideo = ScanSystemFile.scanVideoFile();
				isVideoScaning = false;
			};

		}.start();

		new Thread() {
			public void run() {
				// word pdf
				String[] pdf = new String[] { ScanSystemFile.PDF };
				sysOffice = ScanSystemFile.scanAllFile(pdf);
				String[] word = new String[] { ScanSystemFile.WORD };
				sysOffice.addAll(ScanSystemFile.scanAllFile(word));
				isOfficeScaning = false;
			};

		}.start();
		// 事先把文件扫描一遍
		new Thread() {
			@Override
			public void run() {
				super.run();
				// music
				sysMusics = ScanSystemFile.scanMusicFile();
				isMusicScaning = false;
			}
		}.start();
		
		final ScanSystemFile scan = new ScanSystemFile();
		// 掃描圖片
			new Thread() {
			

				@Override
				public void run() {
					super.run();
					// PICTURE
					scan.initImage();
					folders = scan.getFolders();
				}
			}.start();

	}
	public static  Map<String, List<FileInfo>> folders;
	public static App getInstance() {
		return instance;
	}

}

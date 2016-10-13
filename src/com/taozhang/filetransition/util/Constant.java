package com.taozhang.filetransition.util;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.base.App;

import android.os.Environment;

public class Constant {

	// "pdf"
	// "jpg", "jpeg", "gif", "png", "bmp", "wbmp"
	// "mp4", "wmv", "mpeg", "m4v", "3gp", "3gpp", "3g2", "3gpp2", "asf"
	// "txt", "log", "xml", "ini", "lrc"
	// "doc", "ppt", "docx", "pptx", "xsl", "xslx",
	/**
	 * 端口號
	 */
	public static final int PORT = 10010;
	public static final int PCPORT = 10088;
	public static int FILE_SELECT_CODE = 50001;
	public static String PROJECTBASE_PATH = Environment
			.getExternalStorageDirectory() + "/fileTransition";

	public static final String REQUESTFILES = "100";// 请求发送目录
	// public static String PROJECTBASE_PATH =
	// "/storage/sdcard0/fileTransition";

	public static String DATABASE_PATH = Environment
			.getExternalStorageDirectory() + "/fileTransition/data";
	public static String DATABASE_NAME = "fileTransition.db";
	public static String TABLENAME = "userInfo";
	public static final String ROOT = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	// 历史文件路径
	public static String BASEPATH_MUSIC = PROJECTBASE_PATH + "/music";
	public static String BASEPATH_APK = PROJECTBASE_PATH + "/apk";
	public static String BASEPATH_VIDEO = PROJECTBASE_PATH + "/video";
	public static String BASEPATH_ELSE = PROJECTBASE_PATH + "/else";
	public static String BASEPATH_PIC = PROJECTBASE_PATH + "/pic";
	public static String BASEPATH_OFFICE = PROJECTBASE_PATH + "/office";

	public static String ALERT_ROOTPATH = "已经到达根目录";

	// WIFI AP
	public static final String WIFI_HOT_SPOT_SSID_PREFIX = App.getInstance()
			.getString(R.string.app_name);

	public static final String FREE_SERVER = "192.168.43.1";
	public static final String WIFI_CODE = "w"; // 使用的是wifi网络
	public static final String AP = "a"; // 使用的是wifiap

	public interface MSG {
		public static final int PICTURE_OK = 0;
		public static final int APP_OK = 1;
	}

	// bluetooth
	public static final int BLUETOOTH_CONNECTSUCCESS = 101;
	public static final int HANDLER_DATAFROMIO = 201;

	public static final int REQUEST_ENABLE_BT = 5100;
	public static final int DISCOVERDEVICE = 5200;

	// fileTransition
	public static final String FILEPATH = "filePath";
	// request send files to pc
	public static final String REQUEST_SEND_FEILS_TO_PC = "233";
	public static final String REQUEST_DOWNLOAD_FROM_PC = "234";
	public static final String EXIST = "235";
	public static String ERROR = "ERROR";
}

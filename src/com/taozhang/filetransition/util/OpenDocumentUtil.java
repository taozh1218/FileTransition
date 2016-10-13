package com.taozhang.filetransition.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Parcelable;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public class OpenDocumentUtil {
	public static Intent getopenFileIntent(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 获取文件file的MIME类型
		String type = getMimeType(file);
		// 设置intent的data和Type属性。
		intent.setDataAndType(Uri.fromFile(file), type);
		return intent;
	}

	/**
	 * 
	 * @param file
	 */
	public static void openFile(File file, Context context) {
		// String type = "*/*";
		// String fName = file.getName();
		// // 获取后缀名前的分隔符"."在fName中的位置。
		// int dotIndex = fName.lastIndexOf(".");
		// if (dotIndex < 0) {
		// return type;
		// }
		// /* 获取文件的后缀名 */
		// String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		// if (end == "")
		// return type;
		// // 在MIME和文件类型的匹配表中找到对应的MIME类型。
		// for (int i = 0; i < MIME_MapTable.length; i++) { //
		// MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
		// if (end.equals(MIME_MapTable[i][0]))
		// type = MIME_MapTable[i][1];
		// }
		// return type;

		String type = getMimeType(file);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), type);
		List<ResolveInfo> resInfo = context.getPackageManager()
				.queryIntentActivities(intent, 0);
		if (!resInfo.isEmpty()) {
			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			for (ResolveInfo info : resInfo) {
				Intent targeted = new Intent(Intent.ACTION_VIEW);
				targeted.setDataAndType(Uri.fromFile(file), type);
				ActivityInfo activityInfo = info.activityInfo;
				// judgments : activityInfo.packageName, activityInfo.name, etc.
				if (activityInfo.packageName.contains("com.tencent.mobileqq")) {
					continue;
				}
				targeted.setPackage(activityInfo.packageName);
				targetedShareIntents.add(targeted);
			}
			if (targetedShareIntents.size() != 0) {
				Intent chooserIntent = Intent.createChooser(
						targetedShareIntents.remove(0), "Select app to Open");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						targetedShareIntents.toArray(new Parcelable[] {}));
				chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(chooserIntent);
			} else {
				Toast.makeText(context, "没有可选程序", Toast.LENGTH_SHORT).show();
			}
		} else {
			System.out.println("resInfo.isEmpty()");
			Toast.makeText(context, "resInfo.isEmpty()", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static String getMimeType(File file) {
		String suffix = getSuffix(file);
		if (suffix == null) {
			return "file/*";
		}
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				suffix);
		if (type != null ) {
			return type;
		}
		return "file/*";
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	private static String getSuffix(File file) {
		if (file == null || !file.exists() || file.isDirectory()) {
			return null;
		}
		String fileName = file.getName();
		if (fileName.equals("") || fileName.endsWith(".")) {
			return null;
		}
		int index = fileName.lastIndexOf(".");
		if (index != -1) {
			return fileName.substring(index + 1).toLowerCase(Locale.US);
		} else {
			return null;
		}
	}

	/**
	 * 根据文件路径获得对应的MIME类型。
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getMIMETypeByPath(String filePath) {
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		String mime = "text/plain";
		if (filePath != null) {
			try {
				mmr.setDataSource(filePath);
				mime = mmr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
			} catch (IllegalStateException e) {
				return mime;
			} catch (IllegalArgumentException e) {
				return mime;
			} catch (RuntimeException e) {
				return mime;
			}
		}
		return mime;
	}

	// MIME_MapTable是所有文件的后缀名所对应的MIME类型的一个String数组
	// private static final String[][] MIME_MapTable = {.....各种mime类型}

	// 打开文件的方法
	private boolean openDocs(String path, Context context) {
		File file = new File(path);
		if (file == null || !file.exists()) {
			return false;
		} else {
			Intent intent = OpenDocumentUtil.getopenFileIntent(file);
			try {
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}

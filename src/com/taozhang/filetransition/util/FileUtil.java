package com.taozhang.filetransition.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

/**
 * File operation util class
 * 
 * dat=file:// + /storage/sdcard0/学习/计算机书籍控/android/Android驱动开发与移植实战详解.pdf
 * 
 * @author taozhang
 * 
 */
public class FileUtil {
	public static String getPath(Context context, Uri uri) {
		String path = null;

		return path;
	}

	// @SuppressLint("NewApi")
	// public static String getRealPathFromURI_API19(Context context, Uri uri){
	// String filePath = "";
	// String wholeID = DocumentsContract.getDocumentId(uri);
	//
	// // Split at colon, use second item in the array
	// String id = wholeID.split(":")[1];
	//
	// String[] column = { MediaStore.Images.Media.DATA };
	//
	// // where id is equal to
	// String sel = MediaStore.Images.Media._ID + "=?";
	//
	// Cursor cursor =
	// context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	// column, sel, new String[]{ id }, null);
	//
	// int columnIndex = cursor.getColumnIndex(column[0]);
	//
	// if (cursor.moveToFirst()) {
	// filePath = cursor.getString(columnIndex);
	// }
	// cursor.close();
	// return filePath;
	// }

	@SuppressLint("NewApi")
	public static String getRealPathFromURI_API11to18(Context context,
			Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		String result = null;

		CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj,
				null, null, null);
		Cursor cursor = cursorLoader.loadInBackground();

		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			result = cursor.getString(column_index);
		}
		return result;
	}

	public static String getRealPathFromURI_BelowAPI11(Context context,
			Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(contentUri, proj,
				null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * Try to return the absolute file path from the given Uri
	 * 
	 * @param context
	 * @param uri
	 * @return the file path or null
	 */
	public static String getRealFilePath(final Context context, final Uri uri) {
		if (null == uri)
			return null;
		final String scheme = uri.getScheme();
		String data = null;
		if (scheme == null)
			data = uri.getPath();
		else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { ImageColumns.DATA }, null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(ImageColumns.DATA);
					if (index > -1) {
						data = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return data;
	}

	public static String getFilePath(Context context, Uri uri) {
		String filepath = "";
		String uriPath = uri.toString();

		// Handle local file and remove url encoding
		if (uriPath.startsWith("file://")) {
			filepath = uriPath.replace("file://", "");
			try {
				return URLDecoder.decode(filepath, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = context.getContentResolver().query(uri, projection,
					null, null, null);
			if (cursor != null && cursor.getCount() != 0) {
				int column_index = cursor
						.getColumnIndex(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				filepath = cursor.getString(column_index);
			}
		} catch (Exception e) {
			Log.e("Path Error", e.toString());
		}
		return filepath;
	}

	public static String getFileAbsouutePath(Context context, String fileName) {
		// 首先保存图片
		File appDir = new File(Environment.getExternalStorageDirectory(),
				"fileTransition");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String absolutePath = appDir.getAbsolutePath() + "/" + fileName;
		System.out.println("absolutePath:"+absolutePath);
		return absolutePath;
	}
}

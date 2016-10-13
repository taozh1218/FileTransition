package com.taozhang.filetransition.util.simplifymi;

import com.taozhang.filetransition.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class IconHelper {
	public Bitmap getFileBitmapByType(String type, Context context) {
		Bitmap bitmap;
		if (type.equals("apk")) {// apk
			bitmap = getBitmap(R.drawable.app_blue, context);
		} else if (type.equals("mp3")) {// 音乐
			bitmap = getBitmap(R.drawable.music_red, context);
		} else if (type.equals("png") || type.equals("jpg")
				|| type.equals("jpeg") || type.equals("gif")
				|| type.equals("bmp") || type.equals("wbmp")) {// 图片
			bitmap = getBitmap(R.drawable.pic_grey, context);
		} else if (type.equals("mp4") || type.equals("wmv")
				|| type.equals("mpeg") || type.equals("3gp")
				|| type.equals("3gpp") || type.equals("asf")) {// 视频
			bitmap = getBitmap(R.drawable.audio_red, context);
		} else if (type.equals("docx") || type.equals("xlsx")
				|| type.equals("pptx") || type.equals("accdb")
				|| type.equals("doc") || type.equals("xls")
				|| type.equals("ppt")) {// office
			bitmap = getBitmap(R.drawable.office_red, context);
		} else {// else
			bitmap = getBitmap(R.drawable.doc_blue_32, context);
		}
		return bitmap;

	}

	/**
	 * 根据resource id获取Bitmap
	 * 
	 * @param id
	 * @return
	 */
	public static Bitmap getBitmap(int id, Context context) {
		Bitmap bitmap = BitmapFactory
				.decodeResource(context.getResources(), id);
		return bitmap;
	}
}

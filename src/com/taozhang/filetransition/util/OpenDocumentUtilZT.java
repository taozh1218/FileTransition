package com.taozhang.filetransition.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Intent;
import android.net.Uri;

public class OpenDocumentUtilZT {
	// 打开文件
	public static Intent openFile(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		return intent;
	}

	// 获取文件mimetype
	static String getMIMEType(File file) {
		String type = "";
		String name = file.getName();
		// 文件扩展名
		String end = name.substring(name.lastIndexOf(".") + 1, name.length())
				.toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("mp4") || end.equals("3gp")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg")
				|| end.equals("bmp") || end.equals("gif")) {
			type = "image";
		} else {
			// 如果无法直接打开，跳出列表由用户选择
			type = "*";
		}
		type += "/*";
		return type;
	}
	
	public static void fileChannelCopy(File s, File t) {

        FileInputStream fi = null;

        FileOutputStream fo = null;

        FileChannel in = null;

        FileChannel out = null;

        try {

            fi = new FileInputStream(s);

            fo = new FileOutputStream(t);

            in = fi.getChannel();//得到对应的文件通道

            out = fo.getChannel();//得到对应的文件通道

            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                fi.close();

                in.close();

                fo.close();

                out.close();

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

    }

}

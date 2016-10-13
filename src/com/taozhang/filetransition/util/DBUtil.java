package com.taozhang.filetransition.util;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.taozhang.filetransition.bean.FileDetail;
import com.taozhang.filetransition.helper.MySQLiteOpenHelper;

/**
 * 数据库模块
 * 
 * 只能插入不带/的文件名以及文件类型
 * 
 * @author taozhang
 * 
 */
@SuppressLint("SdCardPath")
public class DBUtil {

	private static List<FileDetail> list;

	public static List<FileDetail> queryAll(Context context) {
		list = new ArrayList<FileDetail>();// 初始化list
		FileDetail file;
		String name;
		String type;

		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase readDB = helper.getReadableDatabase();// 获取阅读数据库对象
		// 查询结果只包括name:new String []{"name"} ,null则表示查询所有
		// 查询条件: "name=\"校长\"" 为了防止sql注入攻击，可以这样，"name = ?"，然后在下一个参数 new String
		// []{"小张"}
		Cursor cursor = readDB.query(Constant.TABLENAME, null, null, null,
				null, null, null);
		while (cursor.moveToNext()) {
			file = new FileDetail();
			name = cursor.getString(cursor.getColumnIndex("name"));
			file.setName(name);
			type = cursor.getString(cursor.getColumnIndex("type"));
			file.setType(type);
			System.out.println(String.format("name=%s,type=%s", name, type));
			list.add(file);
		}
		return list;
	}

	public static List<FileDetail> queryByType(Context context, String type) {
		list = new ArrayList<FileDetail>();// 初始化list
		FileDetail file;
		String name;

		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase readDB = helper.getReadableDatabase();// 获取阅读数据库对象
		Cursor cursor = readDB.query(Constant.TABLENAME, null, "name = ?",
				new String[] { type }, null, null, null);
		while (cursor.moveToNext()) {
			file = new FileDetail();
			name = cursor.getString(cursor.getColumnIndex("name"));
			file.setName(name);
			type = cursor.getString(cursor.getColumnIndex("type"));
			file.setType(type);
			System.out.println(String.format("name=%s,type=%s", name, type));
			list.add(file);
		}
		return list;
	}
}

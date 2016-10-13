package com.taozhang.filetransition.util;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.taozhang.filetransition.bean.UserInfo;
import com.taozhang.filetransition.bean.UserInfo;
import com.taozhang.filetransition.helper.MySQLiteOpenHelper;

/**
 * 数据库的userInfo表的增删查改
 * 
 * @author taozhang
 * 
 */
@SuppressLint("SdCardPath")
public class TbUserUtil {

	private static List<UserInfo> list;

	public static List<UserInfo> queryAll(Context context) {
		list = new ArrayList<UserInfo>();// 初始化list
		UserInfo user;
		String account;
		String psw;
		String username;
		String sign;

		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase readDB = helper.getReadableDatabase();// 获取阅读数据库对象
		// 查询结果只包括name:new String []{"name"} ,null则表示查询所有
		// 查询条件: "name=\"校长\"" 为了防止sql注入攻击，可以这样，"name = ?"，然后在下一个参数 new String
		// []{"小张"}
		Cursor cursor = readDB.query(Constant.TABLENAME, null, null, null,
				null, null, null);
		while (cursor.moveToNext()) {
			user = new UserInfo();
			account = cursor.getString(cursor.getColumnIndex("uid"));
			user.uid = account;
			psw = cursor.getString(cursor.getColumnIndex("psw"));
			user.psw = psw;
			username = cursor.getString(cursor.getColumnIndex("username"));
			user.username = username;
			sign = cursor.getString(cursor.getColumnIndex("sign"));
			user.sign = sign;
			System.out.println(String.format(
					"account=%s,psw=%s,username=%s,sign=%s", account, psw,
					username, sign));
			list.add(user);
		}
		return list;
	}

	/**
	 * 根据用户账号（手机号）查询信息
	 * 
	 * @param context
	 * @param uid
	 * @return
	 */
	public static List<UserInfo> queryByAccount(Context context, String uid) {
		list = new ArrayList<UserInfo>();// 初始化list
		UserInfo user;
		String psw;
		String username;
		String sign;

		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase readDB = helper.getReadableDatabase();// 获取阅读数据库对象
		Cursor cursor = readDB.query(Constant.TABLENAME, null, "uid = ?",
				new String[] { uid }, null, null, null);
		while (cursor.moveToNext()) {
			user = new UserInfo();
			user.uid = uid;
			psw = cursor.getString(cursor.getColumnIndex("psw"));
			user.psw = psw;
			username = cursor.getString(cursor.getColumnIndex("username"));
			user.username = username;
			sign = cursor.getString(cursor.getColumnIndex("sign"));
			user.sign = sign;

			System.out.println(String.format(
					"account=%s,psw=%s,username=%s,sign=%s", uid, psw,
					username, sign));
			list.add(user);
		}
		return list;
	}

	/**
	 * 
	 * @param context
	 * @param user
	 */
	public static long insertIntoTB(Context context, UserInfo user) {
		// 获取数据库对象
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase writableDatabase = helper.getWritableDatabase();
		// 写入数据4
		ContentValues values = new ContentValues();
		values.put("uid", user.uid);
		values.put("psw", user.psw);
		values.put("username", user.username);
		values.put("sign", user.sign);
		long insert = writableDatabase.insert(Constant.TABLENAME, null, values);
		return insert;
	}

	/**
	 * 更新某个用户信息
	 * 
	 * @param context
	 * @param user
	 * @return
	 */
	public static int updateTb(Context context, UserInfo user) {
		// 创建一个DatabaseHelper对象
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase writableDatabase = helper.getWritableDatabase();
		// 创建一个ContentValues对象
		ContentValues values = new ContentValues();
		values.put("uid", user.uid);
		values.put("psw", user.psw);
		values.put("username", user.username);
		values.put("sign", user.sign);
		// 调用update方法
		// 第一个参数String：表名
		// 第二个参数ContentValues：ContentValues对象
		// 第三个参数String：where字句，相当于sql语句where后面的语句，？号是占位符
		// 第四个参数String[]：占位符的值
		int update = writableDatabase.update(Constant.TABLENAME, values,
				"uid=?", new String[] { user.uid });
		return update;
	}

	/**
	 * 
	 * @param context
	 * @param uid
	 *            用户账号
	 * @return
	 */
	public static int deletedatafromTb(Context context, String uid) {
		// 创建一个DatabaseHelper对象
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase writableDatabase = helper.getWritableDatabase();
		// 调用SQLiteDatabase对象的delete方法进行删除操作
		// 第一个参数String：表名
		// 第二个参数String：条件语句
		// 第三个参数String[]：条件值
		int delete = writableDatabase.delete(Constant.TABLENAME, "uid=?",
				new String[] { uid });
		return delete;
	}
}

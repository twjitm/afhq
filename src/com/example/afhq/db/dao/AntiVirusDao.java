package com.example.afhq.db.dao;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

//select desc from datable where md5='a2bd62c89207956348986bf1357dea01'

public class AntiVirusDao {
	public AntiVirusDao() {
		
	}
@SuppressLint("SdCardPath")
private static String dbURL="/sdcard/db/antivirus.db";//病毒数据库
	/**
	 * 检查某个md5是否是病毒
	 * 
	 * @param md5
	 * @return null 代表扫描安全
	 */
	public static String checkVirus(String md5) {
		String desc = null;
		// 打开病毒数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				dbURL, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select desc from datable where md5=?",
				new String[] { md5 });
		if (cursor.moveToNext()) {
			desc = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return desc;
	}

	/**
	 * 判断数据库文件是否存在
	 * 
	 * @return
	 */
	public  static boolean isDBExit() {
		File file = new File(
				dbURL);
		return file.exists() && file.length() > 0;
	}

	/**
	 * 获取数据库版本号
	 * 
	 * @return
	 */
	public static String getDBVersionNum() {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				dbURL, null,
				SQLiteDatabase.OPEN_READONLY);
		String versionnumber = "0";
		Cursor cursor = db.rawQuery("select  subcnt from version", null);
		if (cursor.moveToNext()) {
			versionnumber = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return versionnumber;
	}
	/**
	 * 更新数据库版本号的操作
	 * @param newversion
	 */
	public static void updateDBVersion(int newversion){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"", null,
				SQLiteDatabase.OPEN_READWRITE);
		String versionnumber = "0";
		ContentValues values = new ContentValues();
		values.put("subcnt", newversion);
		db.update("version", values, null, null);
		db.close();
	}
	/**
	 * 更新病毒数据库的API
	 * @param desc
	 * @param md5
	 */
	public static void add(String desc,String md5){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dbURL, null,SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("desc", desc);
		values.put("type", 6);
		values.put("name", "Android.Hack.i22hkt.a");
		db.insert("datable", null, values);
		db.close();
	}
	
	
	/**
	 * 删除病毒api
	 * @param md5
	 */
	public static void delete(String md5){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dbURL, null,SQLiteDatabase.OPEN_READWRITE);
		String[] values={md5};
		db.delete("datable", "md5=?", values);
		db.close();
	}
	
	
	//public static void main(String[] args) {
		//add("超级木马病毒", "3a0c9fdcf83c8c039dd8838a614a2cf0");
	//}
}

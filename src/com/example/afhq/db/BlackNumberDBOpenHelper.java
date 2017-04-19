package com.example.afhq.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 数据库创建的构造方法
	 * 
	 * @param context
	 */
	public BlackNumberDBOpenHelper(Context context) {
		super(context, "callsafe.db", null, 1);
	}

	// 数据库表结构的初始化
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table blackinfo (_id integer primary key autoincrement,number varchar(20),mode varchar(2)) ");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}

package com.example.afhq.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 流量短信数据库
 * @author 文江
 *
 */
public class TrafficMessageDBOOpenHelper extends SQLiteOpenHelper{

	public TrafficMessageDBOOpenHelper(Context context) {
		super(context, "trafficMessage.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
		"create table trafficMessage ("
		+ "_id integer primary key autoincrement,"
		+ "typeContext varchar(200),applyed varchar(10),"
		+ "surplus varchar(11),alled varchar(11))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}

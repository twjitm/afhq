package com.example.afhq.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.afhq.db.ApplockDBOpenHelper;
import com.example.afhq.enums.AppLockType;

public class ApplockDao {
	private ApplockDBOpenHelper helper;
	private Context context;

	public ApplockDao(Context context) {
		this.context = context;
		helper = new ApplockDBOpenHelper(context);
	}
	/**
	 * 查询某个包名是否需要别锁定
	 * @param packname
	 * @return
	 */
	public boolean find(String packname){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("info", null, "packname=?", new String[]{packname}, null, null, null);
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * 查询全部的锁定的包名
	 * @return
	 */
	public List<String> findAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("info", new String[]{"packname"}, null, null, null, null, null);
		List<String> packnames = new ArrayList<String>();
		while(cursor.moveToNext()){
			packnames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return packnames;
	}
	
	public String  getStateBypackageName(String packageName){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("info", new String[]{"state"}, "packname=?", new String[]{packageName}, null, null, null);
		while(cursor.moveToNext()){
			return cursor.getString(0);
		}
		cursor.close();
		db.close();
		return null;
	}
	
	
	/**
	 * 添加一个包名到程序锁数据库
	 * @param packname
	 */
	public void add(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		values.put("state", AppLockType.UNLOCK);
		db.insert("info", null, values);
		db.close();
		//通知内容观察者数据变化了。
		context.getContentResolver().notifyChange(Uri.parse("content://com.itheima.mobileguard.applock"), null);
	}
	
	/**
	 * 删除一个包名，从程序锁数据库删除
	 * @param packname
	 */
	public void delete(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("info", "packname=?", new String[]{packname});
		db.close();
		//通知内容观察者数据变化了。
		context.getContentResolver().notifyChange(Uri.parse("content://com.itheima.mobileguard.applock"), null);
	}
	
	/**
	 * 加锁或者解锁软件
	 * @param packageName
	 * @param state 0加密，1解密
	 */
	public void lockapp(String packageName,Integer state){
		
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		System.out.println("更新状态："+packageName+"------------->"+state);
		values.put("state", state);
		db.update("info", values, "packname=?", new String[]{packageName});
		context.getContentResolver().notifyChange(Uri.parse("content://com.itheima.mobileguard.applock"), null);
	}
	

	
}

package com.example.afhq.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.example.afhq.db.BlackNumberDBOpenHelper;
import com.example.afhq.domain.BlackNumberInfo;

/**
 * 黑名单数据库的增删改查工具类
 * 
 * @author Administrator
 * 
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}

	/**
	 * 添加黑名单号码
	 * 
	 * @param number
	 *            号码
	 * @param mode
	 *            拦截模式
	 * @return 是否添加成功
	 */
	public boolean add(String number, String mode) {
		// 获取到可写的数据库
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		Cursor cursor = db.query("blackinfo", new String[]{"number"}, "number=?", new String[]{number}, null, null, null);
		int context=cursor.getCount();
		if(context>0){
			return changeBlockMode(number, mode);
		}
		long rowid = db.insert("blackinfo", null, values);
		if (rowid == -1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 删除黑名单号码
	 * 
	 * @param number
	 *            号码
	 * @return 是否删除成功
	 */
	public boolean delete(String number) {
		// 获取到可写的数据库
		SQLiteDatabase db = helper.getWritableDatabase();
		int rownumber = db.delete("blackinfo", "number=?",
				new String[] { number });
		if (rownumber == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 修改黑名单号码的拦截模式
	 * 
	 * @param number
	 *            号码
	 * @param newmode
	 *            新的拦截模式
	 * @return 是否修改成功
	 */
	public boolean changeBlockMode(String number, String newmode) {
		// 获取到可写的数据库
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		int rownumber =db.update("blackinfo", values, "number=?", new String[]{number});
		if (rownumber == 0) {
			return false;
		} else {
			return true;
		}
	}
	/**
	 * 返回一个黑名单号码拦截模式
	 * @param number 要查询的黑名单号码
	 * @return 0不是黑名单号码不拦截 1全部拦截 2短信拦截 3电话拦截 
	 */
	public String findBlockMode(String number){
		String mode = "0";
		// 获取到可读的数据库
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blackinfo", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
		if(cursor.moveToNext()){
			mode = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return mode;
	}
	
	
	/**
	 * 查询全部的黑名单号码
	 */
	public List<BlackNumberInfo> findAll(){
		// 得到可读的数据库
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blackinfo", new String[]{"number","mode"}, null, null, null, null, null);
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setMode(mode);
			info.setNumber(number);
			blackNumberInfos.add(info);
		}
		cursor.close();
		db.close();
		SystemClock.sleep(3000);
		return blackNumberInfos;
	}
	
	/**
	 * 分页查询数据库的记录
	 * @param pagenumber 第几页，页码 从第0页开始
	 * @param pagesize   每一个页面的大小
	 */
	public List<BlackNumberInfo> findPart(int pagenumber,int pagesize){
		// 得到可读的数据库
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blackinfo limit ? offset ?", new String[]{String.valueOf(pagesize),
				String.valueOf(pagesize*pagenumber)
		});
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setMode(mode);
			info.setNumber(number);
			blackNumberInfos.add(info);
		}
		cursor.close();
		db.close();
		SystemClock.sleep(30);
		return blackNumberInfos;
	}
	
	
	/**
	 * 分批加载数据
	 * @param startIndex 从哪个位置开始加载数据
	 * @param maxCount   最多加载几条数据
	 */
	public List<BlackNumberInfo> findPart2(int startIndex,int maxCount){
		// 得到可读的数据库
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blackinfo order by _id desc limit ? offset ?", new String[]{String.valueOf(maxCount),
				String.valueOf(startIndex)
		});
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setMode(mode);
			info.setNumber(number);
			blackNumberInfos.add(info);
		}
		cursor.close();
		db.close();
		SystemClock.sleep(30);
		return blackNumberInfos;
	}
	
	
	/**
	 * 获取数据库的总条目个数
	 * @param pagenumber 第几页，页码 从第0页开始
	 * @param pagesize   每一个页面的大小
	 */
	public int getTotalNumber(){
		// 得到可读的数据库
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blackinfo",null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		db.close();
		return count;
	}
}

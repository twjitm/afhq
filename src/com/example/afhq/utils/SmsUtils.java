package com.example.afhq.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Xml;

/**
 * 短信的工具类 提供短信备份和还原的API
 * 
 * @author Administrator
 * 
 */
public class SmsUtils {
	//1.定义一个接口。
	//2.接口里面提供一些回调函数
	//3.调用方法的时候 传递接口对象。
	//4.在开始和备份过程中调用接口里面的方法。
	
	
	/**
	 * 定义的一个接口，用作回调。
	 * @author Administrator
	 *
	 */
	public interface BackupStatusCallback{
		/**
		 * 在短信备份之前调用的方法
		 * @param size 总的短信的个数
		 */
		public void beforeSmsBackup(int size);
		
		/**
		 * 当sms短信备份过程中调用的方法
		 * @param process 当前的进度
		 */
		public void onSmsBackup(int process);
	}
	/**
	 * 备份短信
	 * 
	 * @param context
	 *            上下文
	 * @param callback 接口
	 * @return
	 * @throws FileNotFoundException
	 */
	public static boolean backUpSms(Context context,BackupStatusCallback callback)
			throws FileNotFoundException, IllegalStateException, IOException {
		XmlSerializer serializer = Xml.newSerializer();
		File sdDir = Environment.getExternalStorageDirectory();
		long freesize = sdDir.getFreeSpace();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				&& freesize > 1024l * 1024l) {
			File file = new File(Environment.getExternalStorageDirectory(),
					"backup.xml");
			FileOutputStream os = new FileOutputStream(file);
			// 初始化xml文件的序列化器
			serializer.setOutput(os, "utf-8");
			// 写xml文件的头
			serializer.startDocument("utf-8", true);
			// 写根节点
			ContentResolver resolver = context.getContentResolver();
			Uri uri = Uri.parse("content://sms/");
			Cursor cursor = resolver.query(uri, new String[] { "address",
					"body", "type", "date" }, null, null, null);
			// 得到总的条目的个数
			int size = cursor.getCount();
			//设置进度的总大小
//			pb.setMax(size);
//			pd.setMax(size);
			callback.beforeSmsBackup(size);
			serializer.startTag(null, "smss");
			serializer.attribute(null, "size", String.valueOf(size));
			int process  = 0;
			while (cursor.moveToNext()) {
				serializer.startTag(null, "sms");
				serializer.startTag(null, "body");
				//:-)
				//可能会有乱码问题需要处理，如果出现乱码会导致备份师表。
				try {
					String bodyencpyt = Crypto.encrypt("123", cursor.getString(1));
					serializer.text(bodyencpyt);
				} catch (Exception e1) {
					e1.printStackTrace();
					serializer.text("短信读取失败");
				}
				serializer.endTag(null, "body");
				serializer.startTag(null, "address");
				serializer.text(cursor.getString(0));
				serializer.endTag(null, "address");
				serializer.startTag(null, "type");
				serializer.text(cursor.getString(2));
				serializer.endTag(null, "type");
				serializer.startTag(null, "date");
				serializer.text(cursor.getString(3));
				serializer.endTag(null, "date");
				serializer.endTag(null, "sms");
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//设置进度条对话框的进度
				process++;
//				pb.setProgress(process);
//				pd.setProgress(process);
				callback.onSmsBackup(process);
			}
			cursor.close();
			serializer.endTag(null, "smss");
			serializer.endDocument();
			os.flush();
			os.close();
			return true;
		} else {
			throw new IllegalStateException("sd卡不存在或者空间不足");
		}
	}
	
	
	public interface RestoreSmsCallBack{
		public void beforeSmsRestore(int size);
		public void onSmsRestore(int progress);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean restoreSms(Context context,RestoreSmsCallBack callback){
		//判断 是否备份文件存在 读取sd卡的 文件
		//解析xml文件。
		//1. 创建pull解析器
		//2.初始化pull解析器，设置编码 inputstream
		//3.解析xml文件 while(文档末尾）
		//{
			//读取属性 size 总个数据. 调用接口的方法 beforeSmsRestore
			//每读取到一条短信 就把这个短信 body（解密） address date type获取出来
			//利用内容提供者  resolver.insert(Uri.parse("content://sms/"),contentValue);
			//每还原条 count++ 调用onSmsRestore(count);
		//}
		return false;
	}
	
	/**
	 * 发送短信
	 * @param content
	 * @param destinationAddress 发送到
	 */
	public static void sendMsg(String content,String destinationAddress ){
	  SmsManager smsManager = SmsManager.getDefault();
	  List<String> divideContents = smsManager.divideMessage(content);
	  for (String text : divideContents) {  
	    smsManager.sendTextMessage(destinationAddress, null, text, null, null);  
	  } 
	}
	
	
}

package com.example.afhq.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {
	/**
	 * md5加密的算法
	 * @param text
	 * @return
	 */
	public static String encode(String text){
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] result = digest.digest(text.getBytes());
			StringBuilder sb  =new StringBuilder();
			for(byte b : result){
				int number = b&0xff; // 加盐 +1 ;
				String hex = Integer.toHexString(number);
				if(hex.length()==1){
					sb.append("0"+hex);
				}else{
					sb.append(hex);
				}
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			//can't reach
			return "";
		}
	}
	
	/**
	 * 获取文件的md5值， 
	 * @param path 文件的路径
	 * @return null文件不存在
	 */
	public static String getFileMd5(String path ){
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer,0,len);
			}
			byte[] result = digest.digest();
			StringBuilder sb  =new StringBuilder();
			for(byte b : result){
				int number = b&0xff;
				String hex = Integer.toHexString(number);
				if(hex.length()==1){
					sb.append("0"+hex);
				}else{
					sb.append(hex);
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

package com.example.afhq.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
	/**
	 * 把一个流里面的内容 转化成一个字符串
	 * @param is 流里面的内容
	 * @return null解析失败
	 */
	public static String readStream(InputStream is){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len = is.read(buffer))!=-1){
				baos.write(buffer, 0, len);
			}
			is.close();
			return new String(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}

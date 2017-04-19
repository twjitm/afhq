package com.example.afhq.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.afhq.entity.TrafficMessage;

public class TextFormater
{
	public static String dataSizeFormat(long size)
	{
		DecimalFormat formater = new DecimalFormat("####.00");
		if(size < 1024)
		{
			return size + "byte";
		}
		else if(size < (1 << 20)) //左移20位，相当于1024 * 1024
		{
			float kSize = size >> 10; //右移10位，相当于除以1024
			return formater.format(kSize) + "KB";
		}
		else if(size < (1 << 30)) //左移30位，相当于1024 * 1024 * 1024
		{
			float mSize = size >> 20; //右移20位，相当于除以1024再除以1024
			return formater.format(mSize) + "MB";
		}
		else if(size < (1 << 40))
		{
			float gSize = size >> 30;
			return formater.format(gSize) + "GB";
		}
		else
		{
			return "size : error";
		}
	}
	
	public static String getSizeFromKB(long kSize)
	{
		return dataSizeFormat(kSize << 10);
	}

	static String message="您好！您目前所用的上网套餐有5个，截止到02月27日21时29分使用情况如下:1、赠送100M流量免套餐费,已使用51.1MB,剩余48.9MB;2、赠送300M全国流量,已使用300MB,剩余0MB;3、4G飞享套餐38元_300M国内通用流量,已使用300MB,剩余0MB;4、仅限于观看(咪咕视频)APP的免费省内流量,已使用0MB,剩余2000MB;5、免费赠送500M省内流量,已使用259.72MB,剩余240.28MB;您已使用的流量总和为910.82M,剩余流量总和为2289.18M(部分流量可能有使用限制).所查信息仅供参考。啥？流量不够？来手机营业厅捡便宜啊~手机营业厅签到送流量！每月最高可获得160M！2017年1月3日至3月31日初次下载并登录还可免费领取500M省内4G专属流量哦！别犹豫了，快点击下载： http://dx.10086.cn/zvqbrn 2017年1月4日至2月28日，关注“河北移动”微信公众号参与每日挖宝，1-100元电子券等您拿~【中国移动】";
	/**
	 * 格式化短信数据
	 * @param contntext
	 * @return
	 */
	public   List<TrafficMessage> formatTraffic(String contntext){
		List<TrafficMessage> list=new ArrayList<TrafficMessage>();
		String format=contntext.split(":")[1].split("。")[0];
		String[] entitys= format.split(";");
	   System.out.println(format);
	   for(int i=0;i<entitys.length-1;i++){
		 String[]colomns=  entitys[i].split(",");
		 TrafficMessage trafficMessage=new TrafficMessage();
			 trafficMessage.setTypeContext(colomns[0]);
			 trafficMessage.setApplyed(colomns[1].replace("已使用", "").replace("MB", ""));
			 trafficMessage.setSurplus(colomns[2].replace("剩余", "").replace("MB", ""));
			 double all=Double.parseDouble(colomns[1].replace("已使用", "").replace("MB", ""))+
					 Double.parseDouble(colomns[2].replace("剩余", "").replace("MB", ""));
			 trafficMessage.setAll(all+"");
			 list.add(trafficMessage);      
	   }
		 return list;
	   }
	

	   
}

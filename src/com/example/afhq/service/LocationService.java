package com.example.afhq.service;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
/**
 * 地理位置服务
 * @author 文江
 *
 */
public class LocationService extends Service {
	private LocationManager lm;
	private MyListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		listener = new MyListener();
		//criteria 查询条件
		//true只返回可用的位置提供者 
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//获取准确的位置。
		criteria.setCostAllowed(true);//允许产生开销
		String  name = lm.getBestProvider(criteria, true);
		System.out.println("最好的位置提供者："+name);
		lm.requestLocationUpdates(name, 0, 0, listener);
		super.onCreate();
	}
	
	private class MyListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			StringBuilder sb = new StringBuilder();
			System.out.println("精确度："+location.getAccuracy());
			System.out.println("移动的速度："+location.getSpeed());
			System.out.println("纬度："+location.getLatitude());
			System.out.println("经度："+location.getLongitude());
			System.out.println("海拔："+location.getAltitude());
			sb.append("accuracy:"+location.getAccuracy()+"\n");
			sb.append("speed:"+location.getSpeed()+"\n");
			sb.append("weidu:"+location.getLatitude()+"\n");
			sb.append("jingdu:"+location.getLongitude()+"\n");
			String result = sb.toString();
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, result, null, null);
			stopSelf();
		}
		//当位置提供者 状态发生变化的时候调用的方法。
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		//当某个位置提供者 可用的时候调用的方法。
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		//当某个位置提供者 不可用的时候调用的方法。
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(listener);
		listener = null;
	}

}

package com.example.afhq.activity;

import java.util.List;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.afhq.R;
import com.example.afhq.adapter.TrafficAppAdapter;
import com.example.afhq.db.dao.TrafficMessageDao;
import com.example.afhq.domain.AppInfo;
import com.example.afhq.engine.AppInfoParser;
import com.example.afhq.entity.StorageSize;
import com.example.afhq.entity.TrafficMessage;
import com.example.afhq.observer.SmsDatabaseChaneObserver;
import com.example.afhq.receiver.InnerSmsReceiver;
import com.example.afhq.utils.SmsUtils;
import com.example.afhq.utils.StorageUtil;
import com.example.afhq.utils.TextFormater;
import com.example.afhq.widget.ArcProgress;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class TrafficManagerActivity extends Activity{
	@ViewInject(R.id.arc_store)
	private ArcProgress arc_stare;
	@ViewInject(R.id.traafic_list)
	private ListView traafic_list;
	@ViewInject(R.id.traff_correct)
	private TextView traff_correct;
	@ViewInject(R.id.capacity)
	private TextView capacity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		VpnService vpnService=new VpnService();
		
		PackageManager pm = getPackageManager();
		InnerSmsReceiver receiver=new InnerSmsReceiver();
		String action ="android.provider.Telephony.SMS_RECEIVED";
		IntentFilter filter=new IntentFilter(action);
		String broadcastPermission="android.permission.READ_SMS";
		registerReceiver(receiver, filter, broadcastPermission, hander);
		
		
	// List<AppInfo>apps=	AppInfoParser.getUserAppInfos(getApplicationContext());
//	 for (AppInfo appInfo : apps) {
//		 System.out.println(appInfo);
//		 boolean permission = (PackageManager.PERMISSION_GRANTED == 
//				  pm.checkPermission("android.permission.INTERNET", appInfo.getPackname())); 
//				if (permission) {
//				 System.out.println("you");
//				}else {
//			    System.out.println("wu");
//				}
//	 }	
	
		ViewUtils.inject(this);
		//traffice_round.setDefaultStr(50+"/100M");
		//		traffice_round.set
		//rx receive 接收 下载
		//手机的2g/3g/4g 产生流量
		long mobileRx = TrafficStats.getMobileRxBytes();//接收
		//transfer 发送  上传
		StorageSize storageSize=StorageUtil.convertStorageSize(mobileRx);
		long mobileTx = TrafficStats.getMobileTxBytes();
		StorageSize storageSize1=StorageUtil.convertStorageSize(mobileTx);
		System.out.println(storageSize1.value+storageSize1.suffix);
		
		//全部的网络信息  wifi + 手机卡
		long totalRx = TrafficStats.getTotalRxBytes();
		long totalTx = TrafficStats.getTotalTxBytes();
		SmsManager manager = SmsManager.getDefault();
		//
		//uid 用户id
		int uid = 0;
		
		List<AppInfo>list=AppInfoParser.getUserAppInfos(getApplicationContext());
		
		///proc/uid_stat/10041/tcp_rcv存储的就是下载的流量
		//proc/uid_stat/10041/tcp_snd 上传的流量
	  ConnectivityManager  cm = 	(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo netinfor=cm.getActiveNetworkInfo();
	   TrafficMessageDao trafficMessageDao = new TrafficMessageDao(this);
		/*	if(netinfor!=null){
			System.out.println(netinfor.getTypeName());
		}*/
		List<TrafficMessage> all=trafficMessageDao.getTrafficMessageAll();
		double alltraffic=0;
		double used=0;
		for(TrafficMessage trafficMessage:all){
			used=used+Double.parseDouble(trafficMessage.getApplyed());
			alltraffic+=Double.parseDouble(trafficMessage.getAll());
		}
		capacity.setText(used+"/"+alltraffic+"M");
		arc_stare.setSuffixText("M");
		arc_stare.setProgress((float) alltraffic);
		
		traafic_list.setAdapter(new TrafficAppAdapter(getApplicationContext(), list));
         onClickListener();
	}
	
	private void onClickListener() {
		traff_correct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SmsUtils.sendMsg("5011", "10086");
				Uri smsUri = Uri.parse("content://sms");  
				SmsDatabaseChaneObserver smsDatabaseChaneObserver=
						new SmsDatabaseChaneObserver(getContentResolver(), hander,getApplicationContext());
				 getContentResolver().registerContentObserver(smsUri, true,smsDatabaseChaneObserver);  
			}
		});
	}
	
	//接收到短信的回调
		Handler hander=new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case SmsDatabaseChaneObserver.MESSAGE:
					String context=(String) msg.obj;
					TextFormater textFormater=new TextFormater();
					List<TrafficMessage> traffentity = textFormater.formatTraffic(context);
					TrafficMessageDao trafficMessageDao=new TrafficMessageDao(getApplicationContext());
				    trafficMessageDao.addMsg(traffentity);
					
				   /*for(TrafficMessage trafficMessage:traffentity){
						trafficMessageDao.delete(trafficMessage.getTypeContext());
					}*/
					break;

				default:
					break;
				}
			};
			
		};

}

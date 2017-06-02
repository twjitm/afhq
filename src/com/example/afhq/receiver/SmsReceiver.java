package com.example.afhq.receiver;


import com.example.afhq.R;
import com.example.afhq.service.LocationService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * 短信广播广播监听
 * @author 文江
 *
 */
public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		 System.out.println(TAG+"短信到来了");
		Log.i(TAG,"短信到来了");
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		
		//获取超级管理员
		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		
		for(Object obj:objs){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String sender = smsMessage.getOriginatingAddress();
			String body = smsMessage.getMessageBody();
			if("#*location*#".equals(body)){
				Log.i(TAG,"返回位置信息.");
				//获取位置 放在服务里面去实现。
				Intent service = new Intent(context,LocationService.class);
				context.startService(service);
				abortBroadcast();
			}else if("#*alarm*#".equals(body)){
				Log.i(TAG,"播放报警音乐.");
				MediaPlayer player = MediaPlayer.create(context, null);
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();
			}else if("#*wipedata*#".equals(body)){
				Log.i(TAG,"远程清除数据.");
				dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
				abortBroadcast();
			}else if("#*lockscreen*#".equals(body)){
				Log.i(TAG,"远程锁屏.");
				dpm.resetPassword("123", 0);
				dpm.lockNow();
				abortBroadcast();
			}
		}
	}

}

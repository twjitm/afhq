package com.example.afhq.receiver;

import com.example.afhq.db.dao.BlackNumberDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class InnerSmsReceiver extends BroadcastReceiver {
	private BlackNumberDao dao;
	@Override
	public void onReceive(Context context, Intent intent) {
		dao=new BlackNumberDao(context);
		Log.i("InnerSmsReceiver","短信到来了。");
		Toast.makeText(context, "短信到来了", Toast.LENGTH_SHORT).show();
		//判断短信的发件人是否在黑名单列表里面，
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for(Object obj :objs){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String sender = smsMessage.getOriginatingAddress();
			
			String mode = dao.findBlockMode(sender);
			if("1".equals(mode)||"2".equals(mode)){
				Log.i("InnerSmsReceiver","黑名单短信被拦截。");
				abortBroadcast();//终止短信的广播 ，短信就被拦截 
			}
			//智能拦截。
			String body = smsMessage.getMessageBody();
			if(body.contains("发票")){ //你的头发票亮极了。
				Log.i("InnerSmsReceiver","拦截到垃圾发票短信，终止");
				abortBroadcast();//终止短信的广播 ，短信就被拦截 
			}
		}
	}

}

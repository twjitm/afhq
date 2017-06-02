package com.example.afhq.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.afhq.R;
import com.example.afhq.activity.MessageInterceptActivity.CallSmsSafeAdapter;
import com.example.afhq.adapter.AppLockAdapter;
import com.example.afhq.db.dao.ApplockDao;
import com.example.afhq.domain.AppInfo;
import com.example.afhq.domain.BlackNumberInfo;
import com.example.afhq.enums.AppLockType;
import com.example.afhq.staticdo.AppController;
import com.example.afhq.utils.AppUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class LockAppActivity extends Activity{

	private SharedPreferences shared;
	private AppLockAdapter appadapter;
	@ViewInject(R.id.lockapp_list)
	ListView lockapp_list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		ViewUtils.inject(this);
		shared=getSharedPreferences("isset",MODE_PRIVATE);
		String psd = shared.getString("psd", "0");
		 ApplockDao dao=new ApplockDao(getApplicationContext());
		 
	       List<String> list = dao.findAll();
	      System.out.println(list.size()+"size()++++++++++++++++++==");
	       for(String str:list){
	    	   System.out.println(str);
	       }
	       
		if(psd.equals("0")){
			initLocakPsd();//设置密码
		}else{
			into();
		}
	    List<AppInfo> applist = AppUtil.getUserAppInfos(getApplicationContext());//获取用户安装程序
	    List<AppInfo> viewapplist=new ArrayList<AppInfo>();
	    
	    for(int i=0;i<applist.size();i++){
	    	 boolean exist = dao.find(applist.get(i).getPackname());
	    	 if(!exist){//若是数据库中不存在
	    		 dao.add(applist.get(i).getPackname());
	    		AppInfo app= applist.get(i);
	    		app.setState(AppLockType.UNLOCK);
	    		 viewapplist.add(app);
	    	 }else{
	    		String state = dao.getStateBypackageName(applist.get(i).getPackname());
	    		System.out.println("状态-----》"+state);
	    		AppInfo app= applist.get(i);
	    		    // applist.get(i).getState();
	    		if(state==null){
	    			app.setState(AppLockType.UNLOCK);
	    		}else{
	    			app.setState(Integer.parseInt(state));
	    		}
		    		viewapplist.add(app);
	    	 }
	     } 
	    
	    
	    if(appadapter==null){
			appadapter=new AppLockAdapter(this, viewapplist, handler);
		}
	    lockapp_list.setAdapter(appadapter);
	    lockapp_list.setOnItemClickListener(appadapter);
	}
	
	
	//--------------消息出来器
	Handler handler=new Handler(){
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			System.out.println("-------handler--------");
			appadapter.notifyDataSetChanged();
		};
	};
	
	
	//-----------------------------------------弹框中的业务逻辑
	/**
	 * 进入锁界面
	 */
	private void into() {
		AlertDialog.Builder builder = new Builder(this);
		View dialogView = View.inflate(this, R.layout.dialog_lock_psd,
				null);
		final AlertDialog dialog = builder.create();
		final EditText edit_init_psd = (EditText) dialogView
				.findViewById(R.id.edit_init_psd);//密码。
		dialog.setCancelable(false);
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode==KeyEvent.KEYCODE_BACK){
				}
				return keyCode==KeyEvent.KEYCODE_BACK;
			}
		});
		
		
		/**
		 * 取消按钮
		 */
		dialogView.findViewById(R.id.initpsd_cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						finish();
					}
				});

		
		/**
		 * 确定按钮
		 */
		dialogView.findViewById(R.id.initpsd_ok).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
					    String psd=	shared.getString("psd", "0");
						if(edit_init_psd.getText().toString().equals(psd)){
							dialog.dismiss();
						}else{
							Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
							return;
						}
						
					}
				});
		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();
	}
		

	/**
	 * 第一次使用初始化密码
	 * 
	 * @param view
	 */
	public void initLocakPsd() {
		AlertDialog.Builder builder = new Builder(this);
		View dialogView = View.inflate(this, R.layout.dialog_initlock_psd,
				null);
		final AlertDialog dialog = builder.create();
	
		final EditText edit_init_psd = (EditText) dialogView
				.findViewById(R.id.edit_init_psd);//密码。
		final EditText edit_init_repsd = (EditText) dialogView
				.findViewById(R.id.edit_init_repsd);//确定密码
		dialog.setCancelable(false);
		dialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode==KeyEvent.KEYCODE_BACK){
				}
				return keyCode==KeyEvent.KEYCODE_BACK;
			}
		});
		
		/**
		 * 取消按钮
		 */
		dialogView.findViewById(R.id.initpsd_cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						finish();
					}
				});

		
		/**
		 * 确定按钮
		 */
		dialogView.findViewById(R.id.initpsd_ok).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
					
						String psd=edit_init_psd.getText().toString().trim();
						String repsd=edit_init_repsd.getText().toString().trim();
						System.out.println(psd+"|"+repsd);
						if(psd==null||repsd==null){
							Toast.makeText(getApplicationContext(), "密码不能为空！", Toast.LENGTH_SHORT).show();
							return;
						}
						if(psd.equals(repsd)&&psd.length()>6){
							 shared.edit().clear();
							 Editor editor = shared.edit();
							 editor.putString("psd", edit_init_psd.getText().toString());
							 editor.commit();
							 dialog.dismiss();
						}else{
							Toast.makeText(getApplicationContext(), "密码过于简单或者不一致！", Toast.LENGTH_SHORT).show();
						}
					}
				});
		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println(keyCode+"------www-----------");
            if(KeyEvent.KEYCODE_BACK==keyCode){
            	finish();
            	
            }
            return super.onKeyDown(keyCode, event);
	}
}

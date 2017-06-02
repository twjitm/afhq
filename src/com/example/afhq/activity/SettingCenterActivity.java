package com.example.afhq.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afhq.R;
import com.example.afhq.db.dao.AntiVirusDao;
import com.example.afhq.service.CallSmsSafeService;
import com.example.afhq.staticdo.AppController;
import com.example.afhq.utils.SystemInfoUtils;
import com.example.afhq.view.SettingView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;



public class SettingCenterActivity extends Activity {
	
	private static final String[] items ={"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
	
	private SharedPreferences sp;
	/**O
	 * 黑名单
	 */
	private Intent callSmsSafeIntent;
	/**
	 * 归属地显示
	 */
	private Intent showAddressIntent;
	
	/**
	 * 看门狗设置
	 */
	private Intent watchDogIntent;
	
	private String s;
	
	
	@ViewInject(R.id.desc)
	private EditText desc;
	@ViewInject(R.id.mdcode)
	private EditText mdcode;
	@ViewInject(R.id.savemdcode)
	private Button savemdcode;
	@ViewInject(R.id.delete_sowf)
	private Button delete_sowf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ViewUtils.inject(this);
		final AntiVirusDao dao=new AntiVirusDao();
		savemdcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String descStr=desc.getText().toString();
				String mdcodeStr=mdcode.getText().toString();
				
				dao.add(descStr, mdcodeStr);
			}
		});
		findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String mdcodeStr=mdcode.getText().toString();
				dao.delete(mdcodeStr);
			}
		});
		
		delete_sowf.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			boolean isinstall=AppController.clientUninstall("com.example.test");
		      if(isinstall){
		     Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
		      }
			}
		});
		
		
	}
	
}

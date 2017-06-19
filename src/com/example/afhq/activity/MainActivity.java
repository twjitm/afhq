package com.example.afhq.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.afhq.R;
import com.example.afhq.R.id;
import com.example.afhq.R.layout;
import com.example.afhq.fragment.HomeFragment;
import com.example.afhq.fragment.ScanningListFragment;
import com.example.afhq.utils.TextFormater;
import com.example.afhq.widget.RoundProgressBar;

public class MainActivity extends FragmentActivity {
	private RoundProgressBar mRoundProgressBar5;
	private int progress = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		ImageView im =(ImageView) findViewById(R.id.titleBarRightImage_setting);
		im.setVisibility(View.VISIBLE);
		//设置界面
		im.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), SettingCenterActivity.class);
                startActivity(intent);
			}
		});
		
		mRoundProgressBar5 = (RoundProgressBar) findViewById(R.id.roundProgressBar2);
		mRoundProgressBar5.setDefaultStr("一键体检");
		mRoundProgressBar5.setLogoStr("");
		mRoundProgressBar5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 startScanningListFragment();
				new Thread(new Runnable() {

					@Override
					public void run() {
						while(progress >= 78){
							progress -= 3;
							System.out.println(progress);
							mRoundProgressBar5.setProgress(progress);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
				Date date=	new Date();
				SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String da=s.format(date);
				((TextView)findViewById(R.id.textView1)).setText(da);
			}
		});
		((Button)findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((Button)findViewById(R.id.button1)).setText("正在体检");
				new Thread(new Runnable() {
					@Override
					public void run() {
						while(progress >= 78){
							progress -= 3;
							System.out.println(progress);		
							mRoundProgressBar5.setProgress(progress);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						((Button)findViewById(R.id.button1)).setText("体检完成");
						
					}
				}).start();
			}
		});
	        
		HomeFragment homeFragment=new HomeFragment();
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.activity_test_frag, homeFragment, "").commit();
	         
	
	}

	public void startScanningListFragment(){
		   ScanningListFragment scanningListFragment=new ScanningListFragment();
		   getSupportFragmentManager().beginTransaction().
		   replace(R.id.activity_test_frag, scanningListFragment).commit();
	   }
	
	/**
	 * 
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode==KeyEvent.KEYCODE_BACK){
			return super.onKeyDown(keyCode, event);
		}
		
		return super.onKeyDown(keyCode, event);
	}
}

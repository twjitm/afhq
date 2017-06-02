package com.example.afhq.fragment;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.afhq.R;
import com.example.afhq.activity.AntiVirusActivity;
import com.example.afhq.activity.LockAppActivity;
import com.example.afhq.activity.MessageInterceptActivity;
import com.example.afhq.activity.RublishcleanActivity;
import com.example.afhq.activity.TasksActivity;
import com.example.afhq.activity.TrafficManagerActivity;
import com.example.afhq.base.BaseFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class HomeFragment extends BaseFragment {
	@ViewInject(R.id.fragment_main_trojan)
	private LinearLayout fragment_main_trojan;//木马查杀
	@ViewInject(R.id.messagee_linearlayout)//短信拦截
	private LinearLayout messagee_linearlayout;
	@ViewInject(R.id.traffice_manager)
	private LinearLayout traffice_manager;//流量监控
	@ViewInject(R.id.ll_rubbish)
	private LinearLayout ll_rubbish;
	@ViewInject(R.id.task_insert_layer)//软件管理
	private LinearLayout task_insert_layer;
	@ViewInject(R.id.main_layout_applock)
	private LinearLayout main_layout_applock;
	
	@Override
	public void initData() {
		fragment_main_trojan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =new Intent();
				intent.setClass(getActivity(), AntiVirusActivity.class);
				startActivity(intent);
			}
		});
		messagee_linearlayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent();
				intent.setClass(getActivity(), MessageInterceptActivity.class);
				startActivity(intent);
			}
		});
		//流量监控
		traffice_manager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				TrafficManagerActivity 
				Intent intent=new Intent();
				intent.setClass(context, TrafficManagerActivity.class);
				startActivity(intent);
				
			}
		});
		/**
		 * 
		 */
		ll_rubbish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(context, RublishcleanActivity.class);
				startActivity(intent);				
			}
		});
		
		
		task_insert_layer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(context, TasksActivity.class);
				startActivity(intent);						
			}
		});
		
		main_layout_applock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(context, LockAppActivity.class);
				startActivity(intent);					
			}
		});
		
		
	}
	@Override
	public View initView() {
		view= View.inflate(getActivity(), R.layout.fragment_main, null);
		ViewUtils.inject(this,view);
		return view;
	}

}

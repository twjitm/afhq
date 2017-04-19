package com.example.afhq.activity;


import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.afhq.R;
import com.example.afhq.db.dao.BlackNumberDao;
import com.example.afhq.domain.BlackNumberInfo;
import com.example.afhq.domain.ContactInfo;
import com.example.afhq.engine.ContactInfoParser;
import com.example.afhq.fragment.MessageinterceptFragment;
import com.example.afhq.service.CallSmsSafeService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MessageInterceptActivity extends FragmentActivity{
	@ViewInject(R.id.bt_inpeople)
	private Button bt_inpeople;//从联系人导入
	@ViewInject(R.id.titleBarRightImage_add)//添加拦截
	private  ImageView  titleBarRightImage_add;
	@ViewInject(R.id.titleBarLeftImage_break)//添加拦截
	private ImageView titleBarLeftImage_break;
	@ViewInject(R.id.message_intercept_list)
	private ListView message_intercept_list;
	@ViewInject(R.id.ll_add_number_tips)
	private LinearLayout ll_add_number_tips;
	@ViewInject(R.id.ll_loading)
	private LinearLayout ll_loading;
	private BlackNumberDao dao;
	private List<BlackNumberInfo> infos=new ArrayList<BlackNumberInfo>(); // 代表就是当前界面的集合。

	private CallSmsSafeAdapter adapter;

	/**
	 * 开始获取数据的位置
	 */
	private int startIndex = 0;

	/**
	 * 一次最多获取几条数据
	 */
	private int maxCount = 20;
	private int totalCount = 0;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_message_intercept);
		ViewUtils.inject(this);
		titleBarRightImage_add.setVisibility(View.VISIBLE);
		titleBarLeftImage_break.setVisibility(View.VISIBLE);
		dao = new BlackNumberDao(this);
		addIntercept();
		fillData();
		importPerple();
	}
	
	@Override
	protected void onStart() {
		CallSmsSafeService c=new CallSmsSafeService();
		Intent intent=new Intent();
		intent.setClass(this, CallSmsSafeService.class);
		startService(intent);
		super.onStart();
	}
	/**
	 * 带有返回数据的
	 */
	 @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 0:
			Toast.makeText(getApplicationContext(), "导入黑名单成功",Toast.LENGTH_SHORT).show();
		String json=data.getStringExtra("import");
		List<ContactInfo>contactInfos=JSON.parseArray(json, ContactInfo.class);
		for(ContactInfo ifo:contactInfos){
			 boolean result=	dao.add(ifo.getPhone(), "3");
				// 刷新界面。 把数据加入到infos集合里面。
				if (result) {
					BlackNumberInfo info = new BlackNumberInfo();
					info.setMode("3");
					info.setNumber(ifo.getPhone());
					infos.add(0, info);// 界面的数据集合发生了变化。
					// 通知界面刷新。
					if (adapter != null) {
						adapter.notifyDataSetChanged();
					} else {
						adapter = new CallSmsSafeAdapter(getApplicationContext(), infos,handler);
						message_intercept_list.setAdapter(adapter);
					}
				}     
		     }
		
			break;
		default:
			break;
		}
	}
	
	private void importPerple() {
		bt_inpeople.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), ImportPerpleActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}
	/**
	 * 添加短信拦截
	 * 
	 */
	private void addIntercept() {
		titleBarRightImage_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addBlackNumber(v);
			}
		});

	}

	/**
	 * 添加黑名单号码
	 * 
	 * @param view
	 */
	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		View dialogView = View.inflate(this, R.layout.dialog_add_blacknumber,
				null);
		final AlertDialog dialog = builder.create();
		final EditText et_black_number = (EditText) dialogView
				.findViewById(R.id.et_black_number);
		final CheckBox cb_phone = (CheckBox) dialogView
				.findViewById(R.id.cb_phone);
		final CheckBox cb_sms = (CheckBox) dialogView.findViewById(R.id.cb_sms);
		dialogView.findViewById(R.id.bt_cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

		/**
		 * 弹框
		 */
		dialogView.findViewById(R.id.bt_ok).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						String blackNumber = et_black_number.getText()
								.toString().trim();
						if (TextUtils.isEmpty(blackNumber)) {
							Toast.makeText(getApplicationContext(), "号码不能为空", 1)
							.show();
							return;
						}
						String mode = "0";
						// 1 全部拦截 2 短信拦截 3 电话拦截
						if (cb_phone.isChecked() && cb_sms.isChecked()) {
							mode = "1";
						} else if (cb_phone.isChecked()) {
							mode = "3";
						} else if (cb_sms.isChecked()) {
							mode = "2";
						} else {
							Toast.makeText(getApplicationContext(), "请选择拦截模式",
									1).show();
							return;
						}
						// 把数据添加到数据库
						boolean result = dao.add(blackNumber, mode);
						// 刷新界面。 把数据加入到infos集合里面。
						if (result) {
							BlackNumberInfo info = new BlackNumberInfo();
							info.setMode(mode);
							info.setNumber(blackNumber);
							infos.add(0, info);// 界面的数据集合发生了变化。
							// 通知界面刷新。
							if (adapter != null) {
								adapter.notifyDataSetChanged();
							} else {
								adapter = new CallSmsSafeAdapter(getApplicationContext(), infos,handler);
								message_intercept_list.setAdapter(adapter);
							}
						}
						dialog.dismiss();
					}
				});
		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();
	}

	private void fillData(){
		dao = new BlackNumberDao(this);
		totalCount = dao.getTotalNumber();
		if(totalCount==0){
			//没有黑单号码
			Toast.makeText(getApplicationContext(), "暂无记录", Toast.LENGTH_SHORT).show();
		}else{
			// 数据库的总条目个数 / 每个页面最多显示多少条数据
			// 耗时的操作 逻辑应该放在子线程里面执行。
			new Thread() {
				public void run() {
					if (infos == null) {
						infos = dao.findPart2(startIndex, maxCount);
					} else {
						// 集合里面原来有数据,新的数据应该放在旧的集合的后面。
						infos.addAll(dao.findPart2(startIndex, maxCount));
					}
					Message message=Message.obtain();
					message.what=010;
					message.obj=infos;
					handler.sendMessage(message);
				};
			}.start();
		}
	}
	/**
	 * 消息处理器
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what==010){
				if (infos.size() == 0){
					// 没有数据，设置添加数据的提醒
				} else {
					//if (adapter == null) {
					adapter = new CallSmsSafeAdapter(getApplicationContext(), (List<BlackNumberInfo>) msg.obj,handler);
					message_intercept_list.setAdapter(adapter);
					//} else {// 数据适配器是已经存在的。
					// 因为数据适配器里面的数据 已经变化。刷新界面。
					adapter.notifyDataSetChanged();
					//}
				}
				if (msg.what==011) {
					adapter.notifyDataSetChanged();
				}
			};
		}
		
		
	};
	
	
	
	
	/**
	 * 黑名单适配器
	 * @author 文江
	 *
	 */
	class CallSmsSafeAdapter  extends BaseAdapter {
		Context context;
		List<BlackNumberInfo> infos;
		Handler handler;
		 BlackNumberDao dao;
		public CallSmsSafeAdapter(Context context,List<BlackNumberInfo> infos,Handler handler) {
		 this.context=context;
		 this.infos=infos;
		 this.handler=handler;
		  dao = new BlackNumberDao(context);
		}
		@Override
		public int getCount() {
			
			return infos.size();
		}
		// 这个方法要被执行很多次， 有多个条目 就要执行多少次
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(context,
						R.layout.item_callsms, null);
				holder = new ViewHolder(); // 减少子孩子查询的次数
				holder.tv_phone = (TextView) view
						.findViewById(R.id.tv_item_phone);
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_item_mode);
				holder.iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);
				// 把孩子id的引用 存放在holder里面，设置给父亲 view
				view.setTag(holder);
			} else {
				view = convertView; // 使用历史缓存view对象， 减少view对象被创建的次数
				holder = (ViewHolder) view.getTag();
			}
			final BlackNumberInfo info = infos.get(position);
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String number = info.getNumber();
					// 从数据库删除黑名单号码
					boolean result = dao.delete(number);
					if (result) {
						Toast.makeText(context, "删除成功", 0).show();
						// 从界面ui里面删除信息
						infos.remove(info);
						// 通知界面刷新
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(context, "删除失败", 0).show();
					}
				}
			});
			
			holder.tv_phone.setText(info.getNumber());
			// 1 全部拦截 2 短信拦截 3 电话拦截
			String mode = info.getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("全部拦截");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("短信拦截 ");
			} else if ("3".equals(mode)) {
				holder.tv_mode.setText("电话拦截 ");
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		class ViewHolder {
			TextView tv_phone;
			TextView tv_mode;
			ImageView iv_delete;
		}

	}

	
}

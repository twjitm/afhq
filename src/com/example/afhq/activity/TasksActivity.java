package com.example.afhq.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.afhq.R;
import com.example.afhq.adapter.TaskProcessAdapter;
import com.example.afhq.domain.AppInfo;
import com.example.afhq.engine.AppInfoParser;
import com.example.afhq.entity.CacheListItem;
import com.example.afhq.entity.StorageSize;
import com.example.afhq.service.CleanerService;
import com.example.afhq.service.CleanerService.OnActionListener;
import com.example.afhq.utils.AppUtil;
import com.example.afhq.utils.StorageUtil;
import com.example.afhq.widget.ArcProgress;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
/**
 * 软件管理
 * @author 文江
 *
 */
public class TasksActivity extends Activity implements OnActionListener {
	protected static final int SCAN_FINIFSH = 6;
	protected static final int PROCESS_MAX = 8;
	protected static final int PROCESS_PROCESS = 9;
	private static final int READMORM=0x0;
	
@ViewInject(R.id.task_arc_store)
private ArcProgress task_arc_store;
@ViewInject(R.id.task_capacity)
private TextView task_capacity;

@ViewInject(R.id.task_mangerlist)
private ListView task_mangerlist;
@ViewInject(R.id.task_kill)
private Button task_kill;
private CleanerService mCleanerService;
protected Context mContext;
private ActivityManager activitymanager;
List<AppInfo>adapter=new ArrayList<AppInfo>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		ViewUtils.inject(this);
		mContext=this;
		activitymanager=(ActivityManager) getSystemService(ACTIVITY_SERVICE);
		init();
		task_arc_store.setBottomText("可用内存");	
	}

	/**
 * 初始化界面
 */
	private void init() {

	List<AppInfo> list = AppInfoParser.getUserAppInfos(mContext);
	  
		List<AndroidAppProcess> list1 = ProcessManager.getRunningForegroundApps(mContext);
		/**
		 * 筛选，用于适配器中显示而已没有什么太大的意义
		 */
		for(int i=0;i<list1.size();i++){
			for(int j=0;j<list.size();j++){
				if(list1.get(i).getPackageName().equals(list.get(j).getPackname())){
					adapter.add(list.get(j));
				}
			}
		}
		
		System.out.println("runing--------"+adapter.size());
		TaskProcessAdapter taskProcessAdapter=new TaskProcessAdapter(mContext, adapter);     
		 task_mangerlist.setAdapter(taskProcessAdapter);
		 task_mangerlist.setOnItemClickListener(taskProcessAdapter);
 Thread thread=new Thread(new Runnable() {
				//获取运行内存大小
				@Override
				public void run() {
					long alMemory = AppUtil.getTotalMemory(mContext);
					long availMemory = AppUtil.getAvailMemory(mContext);
					StorageSize alMemorySize = StorageUtil.convertStorageSize(alMemory);
					StorageSize availMemorySize = StorageUtil.convertStorageSize(availMemory);
	                   Message msMessage=Message.obtain();
	                   msMessage.what=READMORM;
	                   String value = (alMemorySize.value+"").substring(0,4);
	                   String valueall = (availMemorySize.value+"").substring(0,4);
	                   msMessage.obj=valueall+alMemorySize.suffix+"/"
	                   +value+availMemorySize.suffix;
	                  
	                   handler.sendMessage(msMessage);
				}
			});
		    thread.start();
			applyKitKatTranslucency();
	
			/**
			  * 结束进程
			  */
			task_kill.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					for(int i=0;i<adapter.size();i++){
						if(!adapter.get(i).getPackname().equals("com.example.afhq")){
							activitymanager.killBackgroundProcesses(adapter.get(i).getPackname());   
							adapter.remove(i);
						}
					}
					TaskProcessAdapter newtaskProcessAdapter = new TaskProcessAdapter(mContext, adapter);
					task_mangerlist.setAdapter(newtaskProcessAdapter);
					
				}
			});
			
			
	
	}
	
	  Handler handler=new Handler(){
		   public void handleMessage(android.os.Message msg) {
			   switch (msg.what) {
			case READMORM:
				String message=(String) msg.obj;
				task_capacity.setText(message);
				String valhaveg=message.split("/")[0];
				valhaveg=valhaveg.replace("GB", "");
				System.out.println("---------------"+valhaveg);
				task_arc_store.setProgress( Float.parseFloat(valhaveg));
				task_arc_store.setSuffixText("G");
				break;

			default:
				break;
			}
		   };
	   };
		private void applyKitKatTranslucency() {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
				setTranslucentStatus(true);
			}

		}
		@TargetApi(19)
		private void setTranslucentStatus(boolean on) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING;
			if (on) {
				winParams.flags |= bits;
			} else {
				winParams.flags &= ~bits;
			}
			win.setAttributes(winParams);
		}

		
		@Override
		public void onScanStarted(Context context) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onScanProgressUpdated(Context context, int current, int max) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onScanCompleted(Context context, List<CacheListItem> apps) {
			long medMemory = mCleanerService != null ? mCleanerService.getCacheSize() : 0;
			System.out.println(medMemory);
			float cachsize= 0;
			for (CacheListItem cacheListItem : apps) {
				cachsize+=cachsize+cacheListItem.getCacheSize();
			}
			StorageSize mStorageSize = StorageUtil.convertStorageSize(medMemory);
			System.out.println("mStorageSize.value"+mStorageSize.value);
			//if(mStorageSize.value==0.0){
				/*rubbish_arc_store.setSuffixText("");
				rubbish_arc_store.setBottomText("暂无垃圾");
				rubbish_capacity.setText("");*/
		//	}else{
				
		//	}
			 
		}

		@Override
		public void onCleanStarted(Context context) {
		}

		@Override
		public void onCleanCompleted(Context context, long cacheSize) {
			
		}

}

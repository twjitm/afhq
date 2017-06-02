package com.example.afhq.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.afhq.R;
import com.example.afhq.db.dao.ApplockDao;
import com.example.afhq.domain.AppInfo;
import com.example.afhq.entity.CacheListItem;
import com.example.afhq.enums.AppLockType;

public class AppLockAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

	public List<AppInfo> mlistAppInfo;
	LayoutInflater infater = null;
	private Context mContext;
	public static List<Integer> clearIds;
	private Handler handler;
	ApplockDao dao;

	public AppLockAdapter(Context context, List<AppInfo> apps, Handler handler) {
		infater = LayoutInflater.from(context);
		mContext = context;
		clearIds = new ArrayList<Integer>();
		this.mlistAppInfo = apps;
		this.handler=handler;
		dao=new ApplockDao(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlistAppInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mlistAppInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = infater.inflate(R.layout.item_locked,
					parent, false);
			holder = new ViewHolder();
			holder.appIcon = (ImageView) convertView
					.findViewById(R.id.iv_app_icon);
			holder.appName = (TextView) convertView
					.findViewById(R.id.tv_app_name);
			holder.lock = (ImageView) convertView
					.findViewById(R.id.iv_app_lock);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AppInfo item = (AppInfo) getItem(position);
		if (item != null) {
			holder.appIcon.setImageDrawable(item.getIcon());
			holder.appName.setText(item.getName());
			Integer state = item.getState();
			System.out.println("state="+state);
			if(state==AppLockType.UNLOCK){//加锁
				holder.lock.setBackgroundResource(R.drawable.unlock);
			}else{
				holder.lock.setBackgroundResource(R.drawable.lock);
			}
			holder.lock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.out.println("--------------");
					Integer state = item.getState();
					//加锁业务
					if(state==1){
						dao.lockapp(item.getPackname(), 1);
					}else{
						dao.lockapp(item.getPackname(), 0);
					}
					//Message message=Message.obtain();
					 handler.sendEmptyMessage(0X00);

				}
			});
			// holder.lock.setBackgroundResource(R.id.);
			/*holder.size.setText(Formatter.formatShortFileSize(mContext, item.getCacheSize()));
            holder.packageName = item.getPackageName();*/
		}
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		if (viewHolder != null && viewHolder.packageName != null) {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + viewHolder.packageName));
			mContext.startActivity(intent);
		}
	}

	class ViewHolder {
		ImageView appIcon;
		TextView appName;
		ImageView lock;


		String packageName;
	}



}

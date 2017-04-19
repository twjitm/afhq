package com.example.afhq.adapter;

import java.util.List;

import com.example.afhq.R;
import com.example.afhq.domain.AppInfo;
import com.example.afhq.domain.TaskInfo;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScanListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<AppInfo> appinfos; 
	public ScanListAdapter(Context context,List<AppInfo> appinfos) {
		this.mInflater=LayoutInflater.from(context);
		this.appinfos=appinfos;
	}
	

	@Override
	public int getCount() {
		return appinfos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return appinfos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			 convertView = mInflater.inflate(R.layout.adapter_scan_item,null);
		 }
		ImageView app_image = (ImageView) convertView.findViewById(R.id.app_image);
		TextView app_name = (TextView) convertView.findViewById(R.id.app_name);
		TextView app_start = (TextView) convertView.findViewById(R.id.app_start);
		app_image.setBackgroundDrawable(appinfos.get(position).getIcon());
		app_name.setText(appinfos.get(position).getName());
		app_start.setText(appinfos.get(position).getAppSize()+"");
		return convertView;
	}

}

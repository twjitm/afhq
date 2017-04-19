package com.example.afhq.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.afhq.R;
import com.example.afhq.domain.Malice;

public class MaliceAppAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Malice>data;
	
	public MaliceAppAdapter(Context context,List<Malice> data) {
		mInflater = LayoutInflater.from(context);
		this.data=data;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		 if(convertView==null){
			 convertView = mInflater.inflate(R.layout.adapter_malice_item,null);
		 }
		 View hander = convertView.findViewById(R.id.view_1);
		 View footer = convertView.findViewById(R.id.view_2);
		 if(position==0){
			 hander.setVisibility(View.GONE);
		 }
		 if(position==data.size()-1){
			 footer.setVisibility(View.GONE);
		 }
		TextView saf_type_name= (TextView) convertView.findViewById(R.id.saf_type_name);
		TextView safe_start= (TextView) convertView.findViewById(R.id.safe_start);
		       Malice m = data.get(position);
		   saf_type_name.setText(m.getName());
		   safe_start.setText(m.getStart());
		return convertView;
	}

}

package com.example.afhq.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.alibaba.fastjson.JSON;
import com.example.afhq.R;
import com.example.afhq.domain.ContactInfo;
import com.example.afhq.engine.ContactInfoParser;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ImportPerpleActivity extends Activity {
	@ViewInject(R.id.import_intercept_list)
	ListView import_intercept_list;//联系人列表
	List<ContactInfo> list ;
	@ViewInject(R.id.save)
	Button save;
	Map<String, ContactInfo>importPeop=new HashMap<String, ContactInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_intercept);
		ViewUtils.inject(this);
		ContactInfoParser cont=new ContactInfoParser();
		list = cont.findAll(getApplicationContext());
		list = new ArrayList<ContactInfo>(new HashSet<ContactInfo>(list));
		ImportPerpleAdapter adapter=new ImportPerpleAdapter();
		import_intercept_list.setAdapter(adapter);
		add();
	}

	private void add() {
    save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			Toast.makeText(getApplicationContext(), "--->"+importPeop.size(), Toast.LENGTH_SHORT).show();
			Intent intent=new Intent();
			List<ContactInfo>list=new ArrayList<ContactInfo>();
			for(Map.Entry<String, ContactInfo>ifo:importPeop.entrySet()){
				list.add(ifo.getValue());
			}
			String json=JSON.toJSONString(list);
			intent.putExtra("import", json);
			setResult(0,intent);
			finish();
			}
		});
	}

	static class ViewHolder{
		CheckBox import_check;
		TextView people_name;
		TextView photo_num1;
	}

	class  ImportPerpleAdapter  extends BaseAdapter{
		Map<Integer,Boolean> mChecked=new HashMap<Integer, Boolean>();
		LayoutInflater layoutflater=LayoutInflater.from(getApplicationContext());
		HashMap<Integer,View> map = new HashMap<Integer,View>(); 
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (map.get(position) == null) {
				view = layoutflater.inflate(R.layout.item_import_people,null);
				holder=new ViewHolder();
				holder.people_name=(TextView) view.findViewById(R.id.people_name);
				holder.photo_num1=(TextView) view.findViewById(R.id.photo_num1);
				holder.import_check=(CheckBox) view.findViewById(R.id.import_check);
				map.put(position, view);
				final int p = position;
				holder.import_check.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {//获取选中项
						CheckBox checkBox=(CheckBox) v;
						if(checkBox.isChecked()){
							importPeop.put(list.get(position).getPhone(), list.get(position));
						}else{
							importPeop.remove(list.get(position).getPhone());
						}
						mChecked.put(p, checkBox.isChecked());
					}
				});
				view.setTag(holder);
			}else{
				view = map.get(position);
				holder = (ViewHolder) view.getTag();
			}
			holder.people_name.setText(list.get(position).getName());
			holder.photo_num1.setText(list.get(position).getPhone());
			return view;
		}
	}
}

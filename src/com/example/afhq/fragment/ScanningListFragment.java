package com.example.afhq.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;

import com.example.afhq.R;
import com.example.afhq.adapter.ScanListAdapter;
import com.example.afhq.base.BaseFragment;
import com.example.afhq.db.dao.AntiVirusDao;
import com.example.afhq.domain.AppInfo;
import com.example.afhq.engine.AppInfoParser;
import com.example.afhq.engine.TaskInfoParser;
import com.example.afhq.entity.ScanInfo;
import com.example.afhq.utils.Md5Utils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
/**
 * 主页扫描列表
 * @author 文江
 *
 */
public class ScanningListFragment extends BaseFragment {

	@ViewInject(R.id.scan_list)
	private ListView scan_list;
	
	@Override
	public void initData() {
		AppInfoParser  t=new AppInfoParser();
		ScanListAdapter scanListAdapter=new ScanListAdapter(getActivity(), t.getUserAppInfos(context));
		scan_list.setAdapter(scanListAdapter);
	}
	@Override
	public View initView() {
	view=View.inflate(getActivity(), R.layout.fragment_main_scan, null);
	ViewUtils.inject(this,view);
	return view;
	}
	   
}

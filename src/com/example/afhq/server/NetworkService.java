package com.example.afhq.server;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.os.Bundle;


public class NetworkService extends Activity{
@SuppressLint("ServiceCast")
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	ActivityManager m=(ActivityManager) getSystemService(ACTIVITY_SERVICE);
	//m.killBackgroundProcesses(packageName);
}

}

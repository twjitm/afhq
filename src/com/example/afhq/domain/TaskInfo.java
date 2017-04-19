package com.example.afhq.domain;

import android.graphics.drawable.Drawable;

/**
 * 进程信息
 * @author Administrator
 *
 */
public class TaskInfo {
	private Drawable icon;
	private String appname;
	private String packname;
	private boolean usertask;
	private long memsize;
	private boolean checked;
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public boolean isUsertask() {
		return usertask;
	}
	public void setUsertask(boolean usertask) {
		this.usertask = usertask;
	}
	public long getMemsize() {
		return memsize;
	}
	public void setMemsize(long memsize) {
		this.memsize = memsize;
	}
	@Override
	public String toString() {
		return "TaskInfo [appname=" + appname + ", packname=" + packname
				+ ", usertask=" + usertask + ", memsize=" + memsize + "]";
	}
}

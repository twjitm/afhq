package com.example.afhq.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.afhq.R;
import com.example.afhq.domain.AppInfo;
import com.example.afhq.entity.CacheListItem;
/**
 * 程序管理
 * @author 文江
 *
 */
public class TaskProcessAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    public List<AppInfo> mlistAppInfo;
    LayoutInflater infater = null;
    private Context mContext;
    public static List<Integer> clearIds;

    public TaskProcessAdapter(Context context, List<AppInfo> adapter) {
        infater = LayoutInflater.from(context);
        mContext = context;
        clearIds = new ArrayList<Integer>();
        this.mlistAppInfo = adapter;
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
            convertView = infater.inflate(R.layout.adapter_rubbish_item,
                    parent, false);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.appName = (TextView) convertView
                    .findViewById(R.id.app_name);
            holder.size = (TextView) convertView
                    .findViewById(R.id.app_size);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AppInfo item = (AppInfo) getItem(position);
        if (item != null) {
            holder.appIcon.setImageDrawable(item.getIcon());
            holder.appName.setText(item.getName());
             //Formatter.formatShortFileSize(mContext, item.getUid())
            holder.size.setText("物理地址:"+item.getUid());
            holder.packageName = item.getPackname();
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
        TextView size;
        String packageName;
    }

}

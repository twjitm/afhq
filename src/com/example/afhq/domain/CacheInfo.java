package com.example.afhq.domain;

import android.graphics.drawable.Drawable;
/**
 * 垃圾实体
 * @author 文江
 *
 */
public class CacheInfo {
    private String name;
    private String packageName;
    private Drawable icon;
    //应用大小
    private String codeSize;
    //数据大小
    private String dataSize;
    //缓存大小
    private String cacheSize;

    public String getName() {
        return name;
    }

    public String getDataSize() {
        return dataSize;
    }

    public String getCacheSize() {
        return cacheSize;
    }

    public String getCodeSize() {
        return codeSize;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    public void setCodeSize(String codeSize) {
        this.codeSize = codeSize;
    }

    public String getPackageName() {
        return packageName;
    }
}

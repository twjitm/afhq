package com.example.afhq.staticdo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AppController {
	/**
     * 描述: 安装
     */
    public static boolean install(String apkPath,Context context){
        // 先判断手机是否有root权限
        if(hasRootPerssion()){
            // 有root权限，利用静默安装实现
            return clientInstall(apkPath);
        }else{
            // 没有root权限，利用意图进行安装
            File file = new File(apkPath);
            if(!file.exists())
                return false; 
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
            context.startActivity(intent);
            return true;
        }
    }
     
    /**
     * 描述: 卸载
     *                                        
     * 
     */
    public static boolean uninstall(String packageName,Context context){
        if(hasRootPerssion()){
            // 有root权限，利用静默卸载实现
        	System.out.println("--------------------有root权限");
            return clientUninstall(packageName);
        }else{
        	System.out.println("--------------------没有root权限");
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,packageURI);
            uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(uninstallIntent);
            return true;
        }
    }
     
    /**
     * 判断手机是否有root权限
     */
    private static boolean hasRootPerssion(){
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }
     
    /**
     * 静默安装
     */
    private static boolean clientInstall(String apkPath){
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 "+apkPath);
            PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r "+apkPath);
//          PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }
     
    /**
     * 静默卸载
     */
    public static boolean clientUninstall(String packageName){
        PrintWriter PrintWriter = null;
        Process process = null;
      //  try {
            try {
				process = Runtime.getRuntime().exec("su");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            PrintWriter.println("pm uninstall "+packageName);
            PrintWriter.flush();
            PrintWriter.close();
            int value = 0;
			try {
				value = process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
            return returnResult(value); 
        //} catch (Exception e) {
          //  e.printStackTrace();
        //}
   // finally{
            //if(process!=null){
            //    process.destroy();
          //  }
       // }
        //return false;
    }
     
    /**
     * 启动app
     * com.exmaple.client/.MainActivity
     * com.exmaple.client/com.exmaple.client.MainActivity
     */
    public static boolean startApp(String packageName,String activityName){
        boolean isSuccess = false;
     //  packageName + "/" + activityName + " \n";
       String cmd = "am start -n " + new  ComponentName(packageName,activityName).flattenToShortString();
        System.out.println(cmd);
        Process process = null;
        try {
           process = Runtime.getRuntime().exec(cmd);
           int value = process.waitFor();  
           System.out.println(value);
           
           return returnResult(value);
        } catch (Exception e) {
          e.printStackTrace();
        } finally{
            if(process!=null){
                process.destroy();
            }
        }
        return isSuccess;
    }
     
     
    private static boolean returnResult(int value){
        // 代表成功  
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }  
    }
}

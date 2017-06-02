/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.afhq.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.example.afhq.domain.AppInfo;
import com.example.afhq.entity.AppProcessInfo;
import com.example.afhq.entity.ProcessInfo;


// TODO: Auto-generated Javadoc

/**

 */
public class AppUtil {

    public static List<String[]> mProcessList = null;

    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 描述：打开并安装文件.
     *
     * @param context the context
     * @param file    apk文件路径
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 描述：卸载程序.
     *
     * @param context     the context
     * @param packageName 包名
     */
    public static void uninstallApk(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri packageURI = Uri.parse("package:" + packageName);
        intent.setData(packageURI);
        context.startActivity(intent);
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param context   the context
     * @param className 判断的服务名字 "com.xxx.xx..XXXService"
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> servicesList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        Iterator<RunningServiceInfo> l = servicesList.iterator();
        while (l.hasNext()) {
            RunningServiceInfo si = (RunningServiceInfo) l.next();
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * 停止服务.
     *
     * @param context   the context
     * @param className the class name
     * @return true, if successful
     */
    public static boolean stopRunningService(Context context, String className) {
        Intent intent_service = null;
        boolean ret = false;
        try {
            intent_service = new Intent(context, Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent_service != null) {
            ret = context.stopService(intent_service);
        }
        return ret;
    }

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or 1 if failed to get result
     */
    public static int getNumCores() {
        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    // Check if filename is "cpu", followed by a single digit
                    // number
                    if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                        return true;
                    }
                    return false;
                }

            });
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 描述：判断网络是否有效.
     *
     * @param context the context
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Gps是否打开 需要<uses-permission
     * android:name="android.permission.ACCESS_FINE_LOCATION" />权限
     *
     * @param context the context
     * @return true, if is gps enabled
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断当前网络是否是移动数据网络.
     *
     * @param context the context
     * @return boolean
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 导入数据库.
     *
     * @param context the context
     * @param dbName  the db name
     * @param rawRes  the raw res
     * @return true, if successful
     */
    public static boolean importDatabase(Context context, String dbName,
                                         int rawRes) {
        int buffer_size = 1024;
        InputStream is = null;
        FileOutputStream fos = null;
        boolean flag = false;

        try {
            String dbPath = "/data/data/" + context.getPackageName()
                    + "/databases/" + dbName;
            File dbfile = new File(dbPath);
            // 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
            if (!dbfile.exists()) {
                // 欲导入的数据库
                if (!dbfile.getParentFile().exists()) {
                    dbfile.getParentFile().mkdirs();
                }
                dbfile.createNewFile();
                is = context.getResources().openRawResource(rawRes);
                fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[buffer_size];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return flag;
    }

    /**
     * 获取屏幕尺寸与密度.
     *
     * @param context the context
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null) {
            mResources = Resources.getSystem();

        } else {
            mResources = context.getResources();
        }
        // DisplayMetrics{density=1.5, width=480, height=854, scaledDensity=1.5,
        // xdpi=160.421, ydpi=159.497}
        // DisplayMetrics{density=2.0, width=720, height=1280,
        // scaledDensity=2.0, xdpi=160.42105, ydpi=160.15764}
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }

    /**
     * 打开键盘.
     *
     * @param context the context
     */
    public static void showSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 关闭键盘事件.
     *
     * @param context the context
     */
    public static void closeSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null
                && ((Activity) context).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(((Activity) context)
                            .getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 获取包信息.
     *
     * @param context the context
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            String packageName = context.getPackageName();
            info = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    public static String getPackage(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 描述：获取运行的进程列表.
     *
     * @param context
     * @return
     */
    public static List<AppProcessInfo> getRunningAppProcesses(Context context) {
        ActivityManager activityManager = null;
        List<AppProcessInfo> list = null;
        PackageManager packageManager = null;
        try {
            activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            list = new ArrayList<AppProcessInfo>();
            // 所有运行的进程
            List<RunningAppProcessInfo> appProcessList = activityManager
                    .getRunningAppProcesses();
            ApplicationInfo appInfo = null;
            AppProcessInfo abAppProcessInfo = null;
            PackageInfo packageInfo = getPackageInfo(context);

            if (mProcessList != null) {
                mProcessList.clear();
            }
            mProcessList = getProcessRunningInfo();

            for (RunningAppProcessInfo appProcessInfo : appProcessList) {
                abAppProcessInfo = new AppProcessInfo(
                        appProcessInfo.processName, appProcessInfo.pid,
                        appProcessInfo.uid);
                appInfo = getApplicationInfo(context,
                        appProcessInfo.processName);
                // appInfo.flags;

                if (appInfo != null) {

                    if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        abAppProcessInfo.isSystem = true;
                    } else {
                        abAppProcessInfo.isSystem = false;
                    }
                    Drawable icon = appInfo.loadIcon(packageManager);
                    String appName = appInfo.loadLabel(packageManager)
                            .toString();
                    abAppProcessInfo.icon = icon;
                    abAppProcessInfo.appName = appName;
                } else {
                    // :服务的命名
                    if (appProcessInfo.processName.indexOf(":") != -1) {
                        appInfo = getApplicationInfo(context,
                                appProcessInfo.processName.split(":")[0]);
                        Drawable icon = appInfo.loadIcon(packageManager);
                        abAppProcessInfo.icon = icon;
                    }
                    abAppProcessInfo.isSystem = true;
                    abAppProcessInfo.appName = appProcessInfo.processName;
                }

				/*
                 * AbPsRow psRow = getPsRow(appProcessInfo.processName);
				 * if(psRow!=null){ abAppProcessInfo.memory = psRow.mem; }
				 */

                ProcessInfo processInfo = getMemInfo(appProcessInfo.processName);
                abAppProcessInfo.memory = processInfo.memory;
                abAppProcessInfo.cpu = processInfo.cpu;
                abAppProcessInfo.status = processInfo.status;
                abAppProcessInfo.threadsCount = processInfo.threadsCount;
                list.add(abAppProcessInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // 查询所有正在运行的应用程序信息： 包括他们所在的进程id和进程名  
    // 这儿我直接获取了系统里安装的所有应用程序，然后根据报名pkgname过滤获取所有真正运行的应用程序  
    private static PackageManager pm; 
    public static List<ApplicationInfo> queryAllRunningAppInfo(Context context) {  
        pm = context.getPackageManager(); 
        // 查询所有已经安装的应用程序  
        List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);  
        Collections.sort(listAppcations,new ApplicationInfo.DisplayNameComparator(pm));// 排序  
  
        // 保存所有正在运行的包名 以及它所在的进程信息  
        Map<String, ActivityManager.RunningAppProcessInfo> pgkProcessAppMap = new HashMap<String, ActivityManager.RunningAppProcessInfo>();  
  
        ActivityManager mActivityManager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);  
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程  
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager  
                .getRunningAppProcesses();  
  
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {  
            int pid = appProcess.pid; // pid  
            String processName = appProcess.processName; // 进程名  
  
            String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的所有应用程序包  
  
            // 输出所有应用程序的包名  
            for (int i = 0; i < pkgNameList.length; i++) {  
                String pkgName = pkgNameList[i];  
                System.out.println( "packageName " + pkgName + " at index " + i+ " in process " + pid);  
                // 加入至map对象里  
                pgkProcessAppMap.put(pkgName, appProcess);  
            }  
        }
        // 保存所有正在运行的应用程序信息  
        List<ApplicationInfo> runningAppInfos = new ArrayList<ApplicationInfo>(); // 保存过滤查到的AppInfo  
  
        for (ApplicationInfo app : listAppcations) {  
            // 如果该包名存在 则构造一个RunningAppInfo对象  
            if (pgkProcessAppMap.containsKey(app.packageName)) {  
                // 获得该packageName的 pid 和 processName  
                int pid = pgkProcessAppMap.get(app.packageName).pid;  
                String processName = pgkProcessAppMap.get(app.packageName).processName;  
               // runningAppInfos.add(getAppInfo(app, pid, processName));  
            }  
        }
		return runningAppInfos;  
    }

    /**
     * 描述：根据进程名返回应用程序.
     *
     * @param context
     * @param processName
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context,
                                                     String processName) {
        if (processName == null) {
            return null;
        }

        PackageManager packageManager = context.getApplicationContext()
                .getPackageManager();
        List<ApplicationInfo> appList = packageManager
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }

    /**
     * 描述：kill进程.
     *
     * @param context
     * @param pid
     */
    public static void killProcesses(Context context, int pid,
                                     String processName) {

        String cmd = "kill -9 " + pid;
        String Command = "am force-stop " + processName + "\n";
        Process sh = null;
        DataOutputStream os = null;
        try {
            sh = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(sh.getOutputStream());
            os.writeBytes(Command + "\n");
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            sh.waitFor();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // AbLogUtil.d(AbAppUtil.class, "#kill -9 "+pid);
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = null;
        try {
            if (processName.indexOf(":") == -1) {
                packageName = processName;
            } else {
                packageName = processName.split(":")[0];
            }

            activityManager.killBackgroundProcesses(packageName);

            //
            Method forceStopPackage = activityManager.getClass()
                    .getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // /**
    // *
    // * 描述：执行PS.
    // */
    // public static List<AbPsRow> ps() {
    // List<AbPsRow> psRowlist = new ArrayList<AbPsRow>();
    // String ps = runScript("ps");
    // String[] lines = ps.split("\n");
    // psRowlist = new ArrayList<AbPsRow>();
    // for (String line : lines) {
    // AbPsRow row = new AbPsRow(line);
    // if (row.pid != null) psRowlist.add(row);
    // }
    // return psRowlist;
    // }
    //
    // /**
    // *
    // * 描述：获得这个进程的ps信息.
    // * @param processName
    // * @return
    // */
    // public static AbPsRow getPsRow(String processName) {
    // List<AbPsRow> psRowlist = ps();
    // for (AbPsRow row : psRowlist) {
    // if (processName.equals(row.cmd)) {
    // return row;
    // }
    // }
    // return null;
    // }

    /**
     * 描述：根据进程名获取CPU和内存信息.
     *
     * @param processName
     * @return
     */
    public static ProcessInfo getMemInfo(String processName) {
        ProcessInfo process = new ProcessInfo();
        if (mProcessList == null) {
            mProcessList = getProcessRunningInfo();
        }
        String processNameTemp = "";

        for (Iterator<String[]> iterator = mProcessList.iterator(); iterator
                .hasNext(); ) {
            String[] item = (String[]) iterator.next();
            processNameTemp = item[9];
            // AbLogUtil.d(AbAppUtil.class,
            // "##"+item[9]+",NAME:"+processNameTemp);
            if (processNameTemp != null && processNameTemp.equals(processName)) {
                // AbLogUtil.d(AbAppUtil.class,
                // "##"+item[9]+","+process.memory);
                // Process ID
                process.pid = Integer.parseInt(item[0]);
                // CPU
                process.cpu = item[2];
                // S
                process.status = item[3];
                // thread
                process.threadsCount = item[4];
                // Mem
                long mem = 0;
                if (item[6].indexOf("M") != -1) {
                    mem = Long.parseLong(item[6].replace("M", "")) * 1000 * 1024;
                } else if (item[6].indexOf("K") != -1) {
                    mem = Long.parseLong(item[6].replace("K", "")) * 1000;
                } else if (item[6].indexOf("G") != -1) {
                    mem = Long.parseLong(item[6].replace("G", "")) * 1000 * 1024 * 1024;
                }
                process.memory = mem;
                // UID
                process.uid = item[8];
                // Process Name
                process.processName = item[9];
                break;
            }
        }
        if (process.memory == 0) {
        }
        return process;
    }

    /**
     * 描述：根据进程ID获取CPU和内存信息.
     *
     * @param pid
     * @return
     */
    public static ProcessInfo getMemInfo(int pid) {
        ProcessInfo process = new ProcessInfo();
        if (mProcessList == null) {
            mProcessList = getProcessRunningInfo();
        }
        String tempPidString = "";
        int tempPid = 0;
        int count = mProcessList.size();
        for (int i = 0; i < count; i++) {
            String[] item = mProcessList.get(i);
            tempPidString = item[0];
            if (tempPidString == null) {
                continue;
            }
            // AbLogUtil.d(AbAppUtil.class, "##"+item[9]+",PID:"+tempPid);
            tempPid = Integer.parseInt(tempPidString);
            if (tempPid == pid) {
                // AbLogUtil.d(AbAppUtil.class,
                // "##"+item[9]+","+process.memory);
                // Process ID
                process.pid = Integer.parseInt(item[0]);
                // CPU
                process.cpu = item[2];
                // S
                process.status = item[3];
                // thread
                process.threadsCount = item[4];
                // Mem
                long mem = 0;
                if (item[6].indexOf("M") != -1) {
                    mem = Long.parseLong(item[6].replace("M", "")) * 1000 * 1024;
                } else if (item[6].indexOf("K") != -1) {
                    mem = Long.parseLong(item[6].replace("K", "")) * 1000;
                } else if (item[6].indexOf("G") != -1) {
                    mem = Long.parseLong(item[6].replace("G", "")) * 1000 * 1024 * 1024;
                }
                process.memory = mem;
                // UID
                process.uid = item[8];
                // Process Name
                process.processName = item[9];
                break;
            }
        }
        return process;
    }

    /**
     * 描述：执行命令.
     *
     * @param command
     * @param workdirectory
     * @return
     */
    public static String runCommand(String[] command, String workdirectory) {
        String result = "";
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            // set working directory
            if (workdirectory != null) {
                builder.directory(new File(workdirectory));
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream in = process.getInputStream();
            byte[] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                String str = new String(buffer);
                result = result + str;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 描述：运行脚本.
     *
     * @param script
     * @return
     */
    public static String runScript(String script) {
        String sRet = "";
        try {
            final Process m_process = Runtime.getRuntime().exec(script);
            final StringBuilder sbread = new StringBuilder();
            Thread tout = new Thread(new Runnable() {
                public void run() {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(m_process.getInputStream()),
                            8192);
                    String ls_1 = null;
                    try {
                        while ((ls_1 = bufferedReader.readLine()) != null) {
                            sbread.append(ls_1).append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            tout.start();

            final StringBuilder sberr = new StringBuilder();
            Thread terr = new Thread(new Runnable() {
                public void run() {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(m_process.getErrorStream()),
                            8192);
                    String ls_1 = null;
                    try {
                        while ((ls_1 = bufferedReader.readLine()) != null) {
                            sberr.append(ls_1).append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            terr.start();

            int retvalue = m_process.waitFor();
            while (tout.isAlive()) {
                Thread.sleep(50);
            }
            if (terr.isAlive())
                terr.interrupt();
            String stdout = sbread.toString();
            String stderr = sberr.toString();
            sRet = stdout + stderr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sRet;
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean getRootPermission(Context context) {
        String packageCodePath = context.getPackageCodePath();
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + packageCodePath;
            // 切换到root帐号
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 描述：获取进程运行的信息.
     *
     * @return
     */
    public static List<String[]> getProcessRunningInfo() {
        List<String[]> processList = null;
        try {
            String result = runCommandTopN1();
            processList = parseProcessRunningInfo(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processList;
    }

    /**
     * 描述：top -n 1.
     *
     * @return
     */
    public static String runCommandTopN1() {
        String result = null;
        try {
            String[] args = {"/system/bin/top", "-n", "1"};
            result = runCommand(args, "/system/bin/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // /**
    // *
    // * 描述：获取进程运行的信息.
    // * @return
    // */
    // public static AbCPUInfo getCPUInfo() {
    // AbCPUInfo CPUInfo = null;
    // try {
    // String result = runCommandTopN1();
    // CPUInfo = parseCPUInfo(result);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return CPUInfo;
    // }

    /**
     * 描述：解析数据.
     *
     * @param info User 39%, System 17%, IOW 3%, IRQ 0% PID PR CPU% S #THR VSS
     *             RSS PCY UID Name 31587 0 39% S 14 542288K 42272K fg u0_a162
     *             cn.amsoft.process 313 1 17% S 12 68620K 11328K fg system
     *             /system/bin/surfaceflinger 32076 1 2% R 1 1304K 604K bg
     *             u0_a162 /system/bin/top
     * @return
     */
    public static List<String[]> parseProcessRunningInfo(String info) {
        List<String[]> processList = new ArrayList<String[]>();
        int Length_ProcStat = 10;
        String tempString = "";
        boolean bIsProcInfo = false;
        String[] rows = null;
        String[] columns = null;
        rows = info.split("[\n]+");
        // 使用正则表达式分割字符串
        for (int i = 0; i < rows.length; i++) {
            tempString = rows[i];
            // AbLogUtil.d(AbAppUtil.class, tempString);
            if (tempString.indexOf("PID") == -1) {
                if (bIsProcInfo == true) {
                    tempString = tempString.trim();
                    columns = tempString.split("[ ]+");
                    if (columns.length == Length_ProcStat) {
                        // 把/system/bin/的去掉
                        if (columns[9].startsWith("/system/bin/")) {
                            continue;
                        }
                        // AbLogUtil.d(AbAppUtil.class,
                        // "#"+columns[9]+",PID:"+columns[0]);
                        processList.add(columns);
                    }
                }
            } else {
                bIsProcInfo = true;
            }
        }
        return processList;
    }

    /**
     *
     * 描述：解析数据.
     *
     * @param info
     *            User 39%, System 17%, IOW 3%, IRQ 0%
     * @return
     */
    // public static AbCPUInfo parseCPUInfo(String info) {
    // AbCPUInfo CPUInfo = new AbCPUInfo();
    // String tempString = "";
    // String[] rows = null;
    // String[] columns = null;
    // rows = info.split("[\n]+");
    // // 使用正则表达式分割字符串
    // for (int i = 0; i < rows.length; i++) {
    // tempString = rows[i];
    // //AbLogUtil.d(AbAppUtil.class, tempString);
    // if (tempString.indexOf("User") != -1 && tempString.indexOf("System") !=
    // -1) {
    // tempString = tempString.trim();
    // columns = tempString.split(",");
    // for(int j = 0; j < columns.length; j++){
    // String col = columns[j].trim();
    // String[] cpu = col.split(" ");
    // if(j == 0){
    // CPUInfo.User = cpu[1];
    // }else if(j == 1){
    // CPUInfo.System = cpu[1];
    // }else if(j == 2){
    // CPUInfo.IOW = cpu[1];
    // }else if(j == 3){
    // CPUInfo.IRQ = cpu[1];
    // }
    // }
    // }
    // }
    // return CPUInfo;
    // }

    /**
     * 描述：获取可用内存.
     *
     * @param context
     * @return
     */
    public static long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        // 当前系统可用内存 ,将获得的内存大小规格化

        return memoryInfo.availMem;
    }

    /**
     * 描述：总内存.
     *
     * @param context
     * @return
     */
    public static long getTotalMemory(Context context) {
        // 系统内存信息文件
        String file = "/proc/meminfo";
        String memInfo;
        String[] strs;
        long memory = 0;

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            // 读取meminfo第一行，系统内存大小
            memInfo = bufferedReader.readLine();
            strs = memInfo.split("\\s+");
            // 获得系统总内存，单位KB
            memory = Integer.valueOf(strs[1]).intValue() ;
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Byte转位KB或MB
        return memory*1024;
    }
    
    
    //--------------------
    public static final int AID_APP = 10000;
    /** offset for uid ranges for each user */
    public static final int AID_USER = 100000;
    public static String getForegroundApp() {
      File[] files = new File("/proc").listFiles();
      int lowestOomScore = Integer.MAX_VALUE;
      String foregroundProcess = null;
      for (File file : files) {
        if (!file.isDirectory()) {
          continue;
        }
        int pid;
        try {
          pid = Integer.parseInt(file.getName());
        } catch (NumberFormatException e) {
          continue;
        }
        try {
          String cgroup = read(String.format("/proc/%d/cgroup", pid));
          String[] lines = cgroup.split("\n");
          String cpuSubsystem;
          String cpuaccctSubsystem;
          
          if (lines.length == 2) {//有的手机里cgroup包含2行或者3行，我们取cpu和cpuacct两行数据
          cpuSubsystem = lines[0];
          cpuaccctSubsystem = lines[1];
          }else if(lines.length==3){
          cpuSubsystem = lines[0];
          cpuaccctSubsystem = lines[2];
          }else {
    continue;
    }
          if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
            // not an application process
            continue;
          }
          if (cpuSubsystem.endsWith("bg_non_interactive")) {
            // background policy
            continue;
          }
          String cmdline = read(String.format("/proc/%d/cmdline", pid));
          if (cmdline.contains("com.android.systemui")) {
            continue;
          }
          int uid = Integer.parseInt(
              cpuaccctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
          if (uid >= 1000 && uid <= 1038) {
            // system process
            continue;
          }
          int appId = uid - AID_APP;
          int userId = 0;
          // loop until we get the correct user id.
          // 100000 is the offset for each user.
          while (appId > AID_USER) {
            appId -= AID_USER;
            userId++;
          }
          if (appId < 0) {
            continue;
          }
          // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
          // String uidName = String.format("u%d_a%d", userId, appId);
          File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
          if (oomScoreAdj.canRead()) {
            int oomAdj = Integer.parseInt(read(oomScoreAdj.getAbsolutePath()));
            if (oomAdj != 0) {
              continue;
            }
          }
          int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", pid)));
          if (oomscore < lowestOomScore) {
            lowestOomScore = oomscore;
            foregroundProcess = cmdline;
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return foregroundProcess;
    }
    private static String read(String path) throws IOException {
      StringBuilder output = new StringBuilder();
      BufferedReader reader = new BufferedReader(new FileReader(path));
      output.append(reader.readLine());
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        output.append('\n').append(line);
      }
      reader.close();
      return output.toString().trim();//不调用trim()，包名后面会带有乱码
    }
//--------------------
    
    @SuppressLint("NewApi")
	public static String printForegroundTask(Context context) {
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        Log.e("adapter", "Current App in foreground is: " + currentApp);
        return currentApp;
    }
    
    /**
	 * 获取手机里面的所有的应用程序
	 * @param context 上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		//得到一个java保证的 包管理器。
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		for(PackageInfo packInfo:packInfos){
			AppInfo appinfo = new AppInfo();
			String packname = packInfo.packageName;
			appinfo.setPackname(packname);
			Drawable icon = packInfo.applicationInfo.loadIcon(pm);
			appinfo.setIcon(icon);
			String appname = packInfo.applicationInfo.loadLabel(pm).toString();
			appinfo.setName(appname);
			//应用程序apk包的路径
			String apkpath = packInfo.applicationInfo.sourceDir;
			appinfo.setApkpath(apkpath);
			File file = new File(apkpath);
			long appSize = file.length();
			appinfo.setAppSize(appSize);
			//应用程序安装的位置。
			int flags = packInfo.applicationInfo.flags; //二进制映射  大bit-map
			int uid=packInfo.applicationInfo.uid;
			appinfo.setUid(uid);
			if((ApplicationInfo.FLAG_EXTERNAL_STORAGE&flags)!=0){
				//外部存储
				appinfo.setInRom(false);
			}else{
				//手机内存
				appinfo.setInRom(true);
			}
			if((ApplicationInfo.FLAG_SYSTEM&flags)!=0){
				//系统应用
				appinfo.setUserApp(false);
			}else{
				//用户应用
				appinfo.setUserApp(true);
			}
			appinfos.add(appinfo);
			appinfo = null;
		}
		return appinfos;
	}
	
	public static List<AppInfo> getUserAppInfos(Context context){
		//得到一个java保证的 包管理器。
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		for(PackageInfo packInfo:packInfos){
			AppInfo appinfo = new AppInfo();
			String packname = packInfo.packageName;
			appinfo.setPackname(packname);
			Drawable icon = packInfo.applicationInfo.loadIcon(pm);
			appinfo.setIcon(icon);
			String appname = packInfo.applicationInfo.loadLabel(pm).toString();
			appinfo.setName(appname);
			//应用程序apk包的路径
			String apkpath = packInfo.applicationInfo.sourceDir;
			appinfo.setApkpath(apkpath);
			File file = new File(apkpath);
			long appSize = file.length();
			appinfo.setAppSize(appSize);
			//应用程序安装的位置。
			int flags = packInfo.applicationInfo.flags; //二进制映射  大bit-map
			int uid =packInfo.applicationInfo.uid;
			appinfo.setUid(uid);
			if((ApplicationInfo.FLAG_EXTERNAL_STORAGE&flags)!=0){
				//外部存储
				appinfo.setInRom(false);
			}else{
				//手机内存
				appinfo.setInRom(true);
			}
			if((ApplicationInfo.FLAG_SYSTEM&flags)!=0){
				//系统应用
				//appinfo.setUserApp(false);
			}else{
				appinfos.add(appinfo);
				//用户应用
				appinfo.setUserApp(true);
			}
			appinfo = null;
		}
		return appinfos;
	}
	
    
    
    
}

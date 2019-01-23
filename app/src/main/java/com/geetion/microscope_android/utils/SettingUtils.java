package com.geetion.microscope_android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.geetion.coreOneUtil.UIUtil;
import com.geetion.coreTwoUtil.GAndroidUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by virgilyan on 15/11/5.
 */
public class SettingUtils {
    /**
     * 获取本机IP地址
     *
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16) & 0xFF) + "." + (ipAddress >> 24 & 0xFF);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentSSID(Context context) {
        WifiManager wifiMan = (WifiManager) (context
                .getSystemService(Context.WIFI_SERVICE));
        WifiInfo wifiInfo = wifiMan.getConnectionInfo();

        if (wifiInfo != null)
            return wifiInfo.getSSID();
        else
            return null;
    }

    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static boolean isWifiOpen(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isAvailable();
    }

    private static List<File> searchFile(Context context, String keyword) {
        List<File> result = new ArrayList<>();

        ExternalSD externalSD = new ExternalSD(context);
        ExternalHDD externalHDD = new ExternalHDD(context);
        File sdcard = externalSD.getSDCardDir();
        File usbCard = externalHDD.getUSBCardPath();
        if(sdcard == null || usbCard == null) {
            return null;
        }
        if (sdcard.length() == 0 && usbCard.length() == 0) {
            return result;
        }
        File[] files = null;
        if (sdcard.length() > 0) {
            files = new File(sdcard.getPath()).listFiles();
        }
        if (usbCard.length() > 0 && (files == null || files.length == 0)) {
            files = new File(usbCard.getPath()).listFiles();
        }

        if (files != null) {
            for (File file : files) {
                if (file.getName().contains(keyword) && file.getName().contains(".apk")) {
                    result.add(file);
                }
            }
        }
        return result;
    }

    public static void upgrade(Context context) {
        String version = GAndroidUtil.getApplicationVersion(context);
        if (version != null && !version.equals("")) {
            float versionNum = Float.parseFloat(version);
            if(searchFile(context, "setup_v") == null) {
                UIUtil.toast(context, "找不到文件");
                return;
            }
            List<File> files = searchFile(context, "setup_v");
            if (files.size() == 0) UIUtil.toast(context, "目录下找不到需要安装的更新安装文件");
            else {
                for (File file : files) {
                    float updateVersion = Float.parseFloat(file.getName().substring(7, 10));
                    if (updateVersion > versionNum) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        context.startActivity(intent);
                        return;
                    }
                }
                UIUtil.toast(context, "当前目录下找不到比当前应用的更新版本的安装文件");
            }
        }
    }
}

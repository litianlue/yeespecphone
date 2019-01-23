package com.geetion.microscope_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.util.Log;

/**
 * Created by yuchunrong on 2017-09-19.
 */

public class HttpUtil {
    private static long lastTotalRxBytes = 0;
    private static long lastTimeStamp = 0;

    private static long getTotalRxBytes(int uid) {
        return TrafficStats.getUidRxBytes(uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
    }
    public static long showNetSpeed(int uid) {

        long nowTotalRxBytes = getTotalRxBytes(uid);
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return speed;
    }
    //wifi网络不可用
    public static boolean isWifionnected(Context context){
        if(context!=null){
            ConnectivityManager connectivityManager  = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mwifinetworkinfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(mwifinetworkinfo!=null){
                return  mwifinetworkinfo.isAvailable();
            }
        }
        return false;
    }
}

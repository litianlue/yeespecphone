package com.geetion.microscope_android.service;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.geetion.log.Logger;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.activity.BatteryAlertActivity;

import java.util.List;

/**
 * Created by WongzYe on 16/1/22.
 */
public class CheckBatteryService extends Service {

    public static final String ACTION = "com.geetion.microscope_android.service.CheckBatteryService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Logger.e("Aye", "service battery " + DataDepot.battery);
        if (DataDepot.battery > 0 && DataDepot.battery <= 30 && !isForeground(this, BatteryAlertActivity.class.getName())) {
            Logger.e("Aye", "service battery " + "启动activity");
            Intent i = new Intent(this, BatteryAlertActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else {
            Logger.e("Aye", "service battery " + "已启动activity");
        }
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     */
    private boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }


        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }

}

package com.geetion.xUtil;

import org.xutils.common.Callback;

import java.io.File;

/**
 * Created by Beary on 15/11/26.
 */
public class ActionDownLoad {
    public void onStarted() {
    }

    public void onWaiting() {
    }

    public void onLoading(long total, long current, boolean isDownloading) {
    }

    public void onSuccessed(File result) {
    }

    public void onError(Throwable ex, boolean isOnCallback) {
    }

    public void onFinished() {
    }

    public void onCancelled(Callback.CancelledException cex) {
    }
}

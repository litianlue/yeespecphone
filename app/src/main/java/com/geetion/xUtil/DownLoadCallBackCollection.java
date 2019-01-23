package com.geetion.xUtil;

import org.xutils.common.Callback;

import java.io.File;

/**
 * Created by Beary on 15/11/26.
 */
public class DownLoadCallBackCollection implements
        Callback.CommonCallback<File>,
        Callback.ProgressCallback<File>,
        Callback.Cancelable {

    public interface CallBack {
        void onStarted();

        void onWaiting();

        void onLoading(long total, long current, boolean isDownloading);

        void onSuccessed(File result);

        void onError(Throwable ex, boolean isOnCallback);

        void onFinished();

        void onCancelled(CancelledException cex);
    }

    private CallBack myCallBack;

    public DownLoadCallBackCollection(CallBack callBack) {
        myCallBack = callBack;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void onWaiting() {
        myCallBack.onWaiting();
    }

    @Override
    public void onStarted() {
        myCallBack.onStarted();
    }

    @Override
    public void onLoading(long total, long current, boolean isDownloading) {
        myCallBack.onLoading(total, current, isDownloading);
    }

    @Override
    public void onSuccess(File result) {
        myCallBack.onSuccessed(result);
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        myCallBack.onError(ex, isOnCallback);
    }

    @Override
    public void onCancelled(CancelledException cex) {
        myCallBack.onCancelled(cex);
    }

    @Override
    public void onFinished() {
        myCallBack.onFinished();
    }
}

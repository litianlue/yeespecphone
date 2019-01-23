package com.geetion.xUtil;

import org.xutils.common.Callback;

import java.util.List;

/**
 * Created by Beary on 15/11/20.
 */
public abstract class ActionCallback<T extends GBaseHttpResult> extends BaseActionCallBack {

    //回调对象
    public void callBackWithObject(T object) {
    }

    //回调对象列表
    public void callBackWithList(List<T> dataList) {
    }

    //回调当用户自行取消接口访问后
    public void onCanceled(Callback.CancelledException cex) {
    }

    //当完成接口访问后的回调
    public void onFinished() {
    }

}


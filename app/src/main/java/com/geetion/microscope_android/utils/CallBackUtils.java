package com.geetion.microscope_android.utils;

import com.geetion.microscope_android.callback.LoginCallBack;

/**
 * Created by yuchunrong on 2017-09-19.
 */

public class CallBackUtils {

    private static LoginCallBack mCallBack;

    public static void setCallBack(LoginCallBack callBack) {
        mCallBack = callBack;
    }

    public static void doCallBackMethod(boolean issuccess){

        mCallBack.LogindResult(issuccess);
    }
}

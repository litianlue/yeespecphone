package com.geetion.xUtil;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.geetion.coreTwoUtil.GDeviceUtils;
import com.geetion.coreTwoUtil.GJSONUtil;
import com.geetion.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Beary on 15/11/19.
 */
public class GXHttpManager {

    public static final int METHOD_GET = 0;
    public static final int METHOD_POST = 1;

    private static final int API_OBJECT = 1000;
    private static final int API_OBJECT_LIST = 1001;
    private static final int API_EXTRA_STRING = 1002;
    private static final int API_JSON_STRING = 1003;

    private static final int MAX_DOWNLOAD_THREAD = 3; // 有效的值范围[1, 3]
    private static final Executor executor = new PriorityExecutor(MAX_DOWNLOAD_THREAD,false);


    /**
     * 过滤服务器回来的信息，只有200code才是正确的，其他的code都代表异常，回调onFailure
     *
     * @param object
     * @param actionCallback
     * @return
     */
    private static boolean isParse(JSONObject object, ActionCallback actionCallback) {
        if (object != null && actionCallback != null) {
            int code = object.optInt("code");
            if (code == 200) {
                actionCallback.onSuccess(object.optString("message"));
                return true;
            } else {
                actionCallback.onFailure(null, object.optString("message"));
                return false;
            }
        } else {
            throw new IllegalArgumentException("jsonObject or actionBack is null");
        }
    }


    private static boolean isParse(JSONObject object, ActionCallBackString actionCallback) {
        if (object != null && actionCallback != null) {
            int code = object.optInt("code");
            if (code != 0) {
                if (code == 200) {
                    actionCallback.onSuccess(object.optString("message"));
                    return true;
                } else {
                    actionCallback.onFailure(null, object.optString("message"));
                    return false;
                }
            } else {
                actionCallback.callBackWithJSON(object.toString());
                return false;
            }
        } else {
            throw new IllegalArgumentException("jsonObject or actionBack is null");
        }
    }

    /**
     * 访问接口并解析结果成对象
     *
     * @param mContext
     * @param params
     * @param callback
     * @return
     */
    public static Callback.Cancelable getObject(Context mContext, int method, GBaseHttpParams params, final ActionCallback callback) {
        //妈妈再也不用担心我需要浪费时间在每个接口里面判断是否有网络，这里一次性判断
        if (GDeviceUtils.isNetworkAvailable(mContext)) {
            switch (method) {
                case METHOD_GET:
                    return switchAPI(false, API_OBJECT, params, callback, null, null);
                case METHOD_POST:
                    return switchAPI(true, API_OBJECT, params, callback, null, null);
            }
        } else {
            callback.onNetWorkError();
            return null;
        }
        return null;
    }


    /**
     * 访问接口并解析结果成对象列表
     *
     * @param mContext
     * @param params
     * @param callback
     * @return
     */
    public static Callback.Cancelable getObjectList(Context mContext, int method, GBaseHttpParams params, final ActionCallback callback) {
        if (GDeviceUtils.isNetworkAvailable(mContext)) {

            switch (method) {
                case METHOD_GET:
                    return switchAPI(false, API_OBJECT_LIST, params, callback, null, null);
                case METHOD_POST:
                    return switchAPI(true, API_OBJECT_LIST, params, callback, null, null);
            }
        } else {
            callback.onNetWorkError();
            return null;
        }
        return null;
    }


    /**
     * 访问接口,跟住传入的list里面的关键字，提取关键字对应字段并封装成map回调出去
     *
     * @param mContext
     * @param params
     * @param strKey
     * @param callback
     * @return
     */
    public static Callback.Cancelable getWithExtraString(Context mContext, int method, GBaseHttpParams params, final List<String> strKey, final ActionCallBackString callback) {
        if (GDeviceUtils.isNetworkAvailable(mContext)) {
            switch (method) {
                case METHOD_GET:
                    return switchAPI(false, API_EXTRA_STRING, params, null, callback, strKey);
                case METHOD_POST:
                    return switchAPI(true, API_EXTRA_STRING, params, null, callback, strKey);
            }
        } else {
            callback.onNetWorkError();
            return null;
        }
        return null;
    }


    /**
     * get方法访问接口,直接将结果json返回，不做处理
     *
     * @param mContext
     * @param params
     * @param callback
     * @return
     */
    public static Callback.Cancelable getWithJSON(Context mContext, int method, GBaseHttpParams params, final ActionCallBackString callback) {
        if (GDeviceUtils.isNetworkAvailable(mContext)) {
            switch (method) {
                case METHOD_GET:
                    return switchAPI(false, API_JSON_STRING, params, null, callback, null);
                case METHOD_POST:
                    return switchAPI(true, API_JSON_STRING, params, null, callback, null);
            }
        } else {
            callback.onNetWorkError();
            return null;
        }
        return null;
    }

    /**
     * 处理网络异常（什么鬼timeout之类的那种）
     *
     * @param ex
     * @param ob
     * @param str
     */
    private static void analyseNetError(Throwable ex, ActionCallback ob, ActionCallBackString str) {
        if (ex instanceof HttpException) { // 网络错误
            HttpException httpEx = (HttpException) ex;
            int responseCode = httpEx.getCode();
            String responseMsg = httpEx.getMessage();
            String errorResult = httpEx.getResult();
            // ...
            if (ob != null)
                ob.onFailure(httpEx, responseMsg);
            if (str != null)
                str.onFailure(httpEx, responseMsg);
        } else { // 其他错误
            if (ob != null)
                ob.onFailure(null, "其他服务器异常!");
            if (str != null) {
               // str.onFailure(null, "其他服务器异常!");
            }
            // ...
        }
    }

    /**
     * 处理json和对象或者对象列表转换
     *
     * @param result
     * @param isList
     * @param callback
     */
    private static void analyseResultObject(String result, boolean isList, ActionCallback callback) {
        if (!GJSONUtil.isJson(result)) {
            //判断后台返回的字符串是不是json
            throw new IllegalArgumentException("the result string is not json!!!");
        }


        if (!isList) {
            try {
                JSONObject object = new JSONObject(result);
                Class<?> clazz;
                if (isParse(object, callback)) {
                    Type genType = callback.getClass().getGenericSuperclass();
                    Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                    clazz = (Class<? extends GBaseHttpResult>) params[0];
                    Object resultObject = GJSONUtil.parseModel(object.optString("object"), clazz);
                    callback.callBackWithObject((GBaseHttpResult) resultObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject object = new JSONObject(result);

                Class<?> clazz;
                if (isParse(object, callback)) {
                    Type genType = callback.getClass().getGenericSuperclass();
                    Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                    clazz = (Class<? extends GBaseHttpResult>) params[0];
                    Object jsonArray = object.get("list");
                    List<?> resultList = JSON.parseArray(jsonArray.toString(), clazz);
                    callback.callBackWithList(resultList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理json转换到特定字段map或者仅仅返回json
     *
     * @param result
     * @param callBack
     * @param strKey
     */
    private static void analyseResultString(boolean isJsonBack, String result, ActionCallBackString callBack, List<String> strKey) {
        if (!GJSONUtil.isJson(result)) {
            //判断后台返回的字符串是不是json
            throw new IllegalArgumentException("the result string is not json!!!");
        }
        JSONObject object = null;
        try {
            object = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //过滤code是否200
        if (isParse(object, callBack)) {
            if (!isJsonBack) {
                Map<String, String> resultMap = new HashMap();
                if (strKey != null && strKey.size() > 0) {
                    for (String key : strKey) {
                        resultMap.put(key, object.optString(key));
                    }
                    callBack.callBackWithExtra(resultMap);
                }
            } else {
                callBack.callBackWithJSON(result);
            }
        }
    }

    private static Callback.Cancelable switchAPI(boolean isPost, final int type, GBaseHttpParams params,
                                                 final ActionCallback ob, final ActionCallBackString str,
                                                 final List<String> strKey) {
        if (ob == null && str == null) {
            throw new IllegalArgumentException("the call back is null");
        } else {
            if (!isPost) {//处理get请求
                return x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        String s = result.toLowerCase();
                        switch (type) {
                            case API_OBJECT:
                                analyseResultObject(s, false, ob);
                                break;
                            case API_OBJECT_LIST:
                                analyseResultObject(s, true, ob);
                                break;
                            case API_EXTRA_STRING:
                                analyseResultString(false, s, str, strKey);
                                break;
                            case API_JSON_STRING:
                                analyseResultString(true, s, str, null);
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (ob != null)
                            analyseNetError(ex, ob, null);
                        if (str != null)
                            analyseNetError(ex, null, str);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        if (ob != null)
                            ob.onCanceled(cex);
                        if (str != null)
                            str.onCanceled(cex);
                    }

                    @Override
                    public void onFinished() {
                        if (ob != null)
                            ob.onFinished();
                        if (str != null)
                            str.onFinished();
                    }
                });
            } else {//处理post请求
                return x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        String s = result.toLowerCase();
                        switch (type) {
                            case API_OBJECT:
                                analyseResultObject(s, false, ob);
                                break;
                            case API_OBJECT_LIST:
                                analyseResultObject(s, true, ob);
                                break;
                            case API_EXTRA_STRING:
                                analyseResultString(false, s, str, strKey);
                                break;
                            case API_JSON_STRING:
                                analyseResultString(true, s, str, null);
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (ob != null)
                            analyseNetError(ex, ob, null);
                        if (str != null)
                            analyseNetError(ex, null, str);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        if (ob != null)
                            ob.onCanceled(cex);
                        if (str != null)
                            str.onCanceled(cex);
                    }

                    @Override
                    public void onFinished() {
                        if (ob != null)
                            ob.onFinished();
                        if (str != null)
                            str.onFinished();
                    }
                });
            }
        }
    }

    //=====================================这是华丽丽的分割线=========================================


    public static Callback.Cancelable downLoadFile(GBaseHttpParams params, final ActionDownLoad download) {
        params.setExecutor(executor);
        return x.http().get(params, new DownLoadCallBackCollection(new DownLoadCallBackCollection.CallBack() {
            @Override
            public void onStarted() {
                // Logger.e("onStarted");
                download.onStarted();
            }

            @Override
            public void onWaiting() {
                // Logger.e("onWaiting");
                download.onWaiting();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                //Logger.e("total: " + total + "  " + "current:  " + current + "  isDownLoading:  " + isDownloading);
                download.onLoading(total, current, isDownloading);
            }

            @Override
            public void onSuccessed(File result) {
                //Logger.e("onSuccessed");
                download.onSuccessed(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                // Logger.e("onError");
                download.onError(ex, isOnCallback);
            }

            @Override
            public void onFinished() {
                //Logger.e("onFinished");
                download.onFinished();
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
                //Logger.e("onCancelled");
                download.onCancelled(cex);
            }
        }));


    }
}

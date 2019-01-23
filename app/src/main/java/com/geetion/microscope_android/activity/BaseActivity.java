package com.geetion.microscope_android.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.geetion.coreTwoUtil.GActivityManager;
import com.geetion.log.Logger;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.application.BaseApplication;
import com.geetion.microscope_android.service.Constant;
import com.geetion.microscope_android.service.DataDepot;
import com.geetion.microscope_android.service.webSocket.BaseWebSocketCallback;
import com.geetion.microscope_android.utils.ConstanUtil;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by virgilyan on 15/11/3.
 */
public class BaseActivity extends Activity {

    public Activity activity;
    protected TextView returnView;
    private boolean isPause;
//    private BatteryInfo batteryInfo;

    private Future<WebSocket> controlEnbelScoket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //BaseApplication.setContext(this);
        GActivityManager.getActivityManager().addActivity(this);
//        Logger.e("Aye", "add activity " + String.valueOf(GActivityManager.getActivityManager().getActivity(this.getClass().getName()) == null) + " size " + GActivityManager.getActivityManager().getActivityStackSize());
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        if(ConstanUtil.loginUser!=null&&!ConstanUtil.loginUser.equals(""))
            openService(ConstanUtil.loginUser);
        super.onResume();
        isPause = false;
        initProhibitDialog();
        getControlEnble();
        if(ConstanUtil.remoteLogin){

        }else
        getPadInfoByWebSocket();
    }

    @Override
    protected void onPause() {
        if(ConstanUtil.loginUser!=null&&!ConstanUtil.loginUser.equals(""))
        closeService(ConstanUtil.loginUser);
        super.onPause();
        isPause = true;
        closeBaseActivityWebScoket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * @param usernama
     */
    private void closeService(final String usernama) {
        AVQuery<AVObject> query = new AVQuery<>("ConnectState");
        query.whereEqualTo("user",usernama);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("flage",false);
                    jsonObject.put("date",object.getUpdatedAt().getTime());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                object.put("phonestate", jsonObject);
                AVSaveOption option = new AVSaveOption();
                option.query(new AVQuery<>("ConnectState").whereEqualTo("user", usernama));
                option.setFetchWhenSave(true);
                object.saveInBackground(option, new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {

                        } else {

                        }
                    }
                });
            }
        });
    }
    /**
     * @param usernama
     */
    private void openService(final String usernama) {
        AVQuery<AVObject> query = new AVQuery<>("ConnectState");
        query.whereEqualTo("user",usernama);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("flage",true);
                    jsonObject.put("date",object.getUpdatedAt().getTime());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                object.put("phonestate", jsonObject);
                AVSaveOption option = new AVSaveOption();
                option.query(new AVQuery<>("ConnectState").whereEqualTo("user", usernama));
                option.setFetchWhenSave(true);
                object.saveInBackground(option, new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {

                        } else {

                        }
                    }
                });
            }
        });
    }
    private void initListener() {
        if (returnView != null) {
            returnView.setOnClickListener(onClickListener);
        }
    }

    private void initView() {
        returnView = (TextView) findViewById(R.id.btn_return);
    }


    public void getControlEnble() {
//        BaseWebSocketCallback controlEnbleCallback = new BaseWebSocketCallback(this, controlEnbelScoket) {
//            @Override
//            public void onCompleted(Exception ex, WebSocket webSocket) {
//                super.onCompleted(ex, webSocket);
//                if (webSocket == null) return;
//                webSocket.setStringCallback(new WebSocket.StringCallback() {
//                    @Override
//                    public void onStringAvailable(String s) {
//
//                        try {
//                            JSONObject object = new JSONObject(s);
//                            DataDepot.enable = object.getBoolean("enable");
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        if (isForeground(activity, activity.getClass().getName())) {
//                            if (DataDepot.enable) {
//                                if (prohibitDialog != null && prohibitDialog.isShowing()) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            prohibitDialog.dismiss();
//                                        }
//                                    });
//
//                                }
//                            } else {
//                                if (prohibitDialog != null && !prohibitDialog.isShowing()) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (isForeground(activity, activity.getClass().getName())) {
//                                                if (!isPause) {
//                                                    prohibitDialog.show();
//                                                }
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                        }
//                    }
//                });
//            }
//        };
//        controlEnbelScoket = AsyncHttpClient.getDefaultInstance().websocket(Constant.WS_PROTOCOL + Constant.WS_URI + Constant.WS_PORT_DEVICE_STATUS + Constant.DEVICES_STATUS_CHANNEL, null, controlEnbleCallback);
    }

    private Dialog prohibitDialog;

    public void initProhibitDialog() {
        prohibitDialog = new Dialog(this, R.style.Dialog_Prohibit);
        prohibitDialog.setContentView(R.layout.dialog_prohibit);
        prohibitDialog.setCancelable(false);
        prohibitDialog.setCanceledOnTouchOutside(false);
    }

    private Future<WebSocket> padInfoWebSocket;

    private void getPadInfoByWebSocket() {

        BaseWebSocketCallback padInfoCallback = new BaseWebSocketCallback(this, padInfoWebSocket) {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                super.onCompleted(ex, webSocket);
                if (webSocket == null) return;
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        Log.e("BaseActivity", s);
                        try {
                            JSONObject object = new JSONObject(s);
                            DataDepot.battery = Integer.valueOf(object.getString("battery").replace("%", ""));
                            DataDepot.cameraConnect = object.getString("cameraConnect");
                            DataDepot.connectCount = object.getString("connectCount");
                            DataDepot.deviceIP = object.getString("deviceIP");
                            DataDepot.deviceMac = object.getString("deviceMac");
                            DataDepot.employMemory = object.getString("employMemoery");
                            DataDepot.totalMemory = object.getString("totalMemoery");
                            DataDepot.wifi = object.getString("wifi");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(ConstanUtil.upDataAction);
                        sendBroadcast(intent);
                       /* Log.e("BaseActivity", ">>>>>>>>>1111");
                        if (mOnPadInfoWebScoket != null) {
                            Log.e("BaseActivity", ">>>>>>>>>22222");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mOnPadInfoWebScoket.doInPadInfoWebSocket();
                                }
                            });
                        }*/
                    }
                });
            }
        };
        padInfoWebSocket = AsyncHttpClient.getDefaultInstance().websocket(Constant.WS_PROTOCOL + Constant.WS_URI + Constant.WS_PORT_PAD_STATUS + Constant.PAD_STATUS_CHANNEL, null, padInfoCallback);
    }


    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_return:
                    GActivityManager.getActivityManager().finishActivity();
                    break;
            }
        }
    };

    /*public OnPadInfoWebScoket mOnPadInfoWebScoket;

    public interface OnPadInfoWebScoket {
        void doInPadInfoWebSocket();
    }*/

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

//        Logger.e("Aye", "className " + className);

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
//            Logger.e("Aye", "ComponentName " + cpn.getClassName());
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }

    protected void closeBaseActivityWebScoket() {
        if (controlEnbelScoket != null) {
            if (controlEnbelScoket.tryGet() != null) {
                BaseApplication.closeBySelf = true;
                controlEnbelScoket.tryGet().close();
            }
            controlEnbelScoket = null;
        }

        if (prohibitDialog != null) {
            prohibitDialog.dismiss();
            prohibitDialog = null;
        }

        if (padInfoWebSocket != null) {
            if (padInfoWebSocket.tryGet() != null) {
                BaseApplication.closeBySelf = true;
                padInfoWebSocket.tryGet().close();
            }
            padInfoWebSocket = null;
        }
    }

}

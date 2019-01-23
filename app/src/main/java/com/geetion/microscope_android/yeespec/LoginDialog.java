package com.geetion.microscope_android.yeespec;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geetion.coreOneUtil.UIUtil;
import com.geetion.coreTwoUtil.GActivityManager;
import com.geetion.log.Logger;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.activity.MasterActivity;
import com.geetion.microscope_android.activity.SelectConnectActivity;
import com.geetion.microscope_android.service.Constant;
import com.geetion.microscope_android.service.http.CommonActionCallBackString;
import com.geetion.microscope_android.utils.CallBackUtils;
import com.geetion.microscope_android.utils.ConstanUtil;
import com.geetion.microscope_android.utils.SPUtils;
import com.geetion.xUtil.GBaseHttpParams;
import com.geetion.xUtil.GXHttpManager;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/8/3.
 *
 * @author Mr.Wen
 * @version $Rev$
 * @company YeeSpec
 * @time 2016/8/3 15:48
 * @desc ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class LoginDialog {

    private static Dialog loginDealyDialog;
    private static Dialog mLoginDialog = null;
    private static Button mConfirmBtn = null;

    private static Context mContext = null;

    private static EditText mFirstEt;
    private static EditText mSecondEt;
    private static EditText mThirdEt;
    private static EditText mFourthEt;
    private static TextView tv;
    private static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2);
    private static ScheduledExecutorService isloginDelay = new ScheduledThreadPoolExecutor(2);//超时释放登录标记位

    //用于测试连接是否成功
    private static Callback.Cancelable mCallback;

    //private static  MyThread myThread;
    public static Dialog getLoginDialog(Context context) {


        mContext = context;

        // 初始化对话框
        if (mLoginDialog == null)
            mLoginDialog = new Dialog(context, R.style.Dialog_Radio);
        initView();
        initListener();


        return mLoginDialog;
    }
    public interface CallBack {

        void doSomeThing(String string);

    }

    private static void initView() {

        // 初始化对话框
        mLoginDialog.setContentView(R.layout.dialog_login);
        mLoginDialog.setCancelable(true);
        mLoginDialog.setCanceledOnTouchOutside(false);
        mLoginDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ConstanUtil.firstLogin = true;
                Toast.makeText(mContext, "正在退出", Toast.LENGTH_SHORT).show();
                GActivityManager.getActivityManager().appExit(mContext);
                //Intent intent = new Intent(mContext, SelectConnectActivity.class);
                //mContext.startActivity(intent);
            }
        });
        mFirstEt = (EditText) mLoginDialog.findViewById(R.id.et_ip1);
        mSecondEt = (EditText) mLoginDialog.findViewById(R.id.et_ip2);
        mThirdEt = (EditText) mLoginDialog.findViewById(R.id.et_ip3);
        mFourthEt = (EditText) mLoginDialog.findViewById(R.id.et_ip4);
        mConfirmBtn = (Button) mLoginDialog.findViewById(R.id.btn_confirm);
        tv = (TextView) mLoginDialog.findViewById(R.id.textView);
        String ip = (String) SPUtils.get(mContext, "ip", "");
        if (ip != null) {
            String[] sArray = ip.split("\\.");
            for (int i = 0; i < sArray.length; i++) {
                Logger.e("Aye", "ip " + sArray[i]);
                switch (i) {
                    case 0:
                        mFirstEt.setText(sArray[i].trim());
                        break;
                    case 1:
                        mSecondEt.setText(sArray[i].trim());
                        break;
                    case 2:
                        mThirdEt.setText(sArray[i].trim());
                        break;
                    case 3:
                        mFourthEt.setText(sArray[i].trim());
                        break;
                }
            }
        }
    }

    private static void initListener() {
        mConfirmBtn.setOnClickListener(btnConfirmListener);
    }

    private static View.OnClickListener btnConfirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String mFirstIP;
            final String mSecondIP;
            final String mThridIP;
            final String mFourthIP;

            mFirstIP = mFirstEt.getText().toString();
            mSecondIP = mSecondEt.getText().toString();
            mThridIP = mThirdEt.getText().toString();
            mFourthIP = mFourthEt.getText().toString();

            loginMethods(mFirstIP, mSecondIP, mThridIP, mFourthIP);

        }
    };

    public static void reConnectService(final String ip) {
        tryWakeUpServer();
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                connectHttp(ip);
            }
        }, 2000, TimeUnit.MILLISECONDS);
    }

    private static void loginMethods(final String mFirstIP, final String mSecondIP, final String mThridIP, final String mFourthIP) {
        if (mFourthIP.equals("") || mSecondIP.equals("") || mThridIP.equals("") || mFourthIP.equals("")) {

            UIUtil.toast(mContext, "请填写完整");
            return;
        }
        tryWakeUpServer();
        tv.setText("正在登陆中请稍等...");
        mConfirmBtn.setBackgroundColor(Color.parseColor("#626264"));
        mConfirmBtn.setEnabled(false);
        ConstanUtil.isSuccessLogin = false;
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                // if (!mFourthIP.equals("") || !mSecondIP.equals("") || !mThridIP.equals("") || !mFourthIP.equals("")) {
                connectHttp(mFirstIP + "." + mSecondIP + "." + mThridIP + "." + mFourthIP);
                // }
            }
        }, 2000, TimeUnit.MILLISECONDS);
    }



    public static void connectHttp(final String ip) {
        ConstanUtil.connectip = ip;
        Logger.e("Aye", Constant.HTTP_PROTOCOL + ip + Constant.HTTP_PORT + Constant.CAMERA_GETPHOTOLIST);
        GBaseHttpParams params = new GBaseHttpParams(Constant.HTTP_PROTOCOL + ip + Constant.HTTP_PORT + Constant.CAMERA_GETPHOTOLIST);
        Map<String, String> map = new HashMap<>();
        map.put("page", "1");
        params.putGETParams(map);
        isloginDelay.schedule(new Runnable() {
            @Override
            public void run() {
                Log.e("MasterActivvty", "登录超时="+ConstanUtil.islogin  );
                if (mLoginDialog!=null) {
                    if (mLoginDialog != null) {
                        mLoginDialog.dismiss();
                        mLoginDialog = null;
                    }
                    CallBackUtils.doCallBackMethod(false);
                }
            }
        }, 15000, TimeUnit.MILLISECONDS);
        mCallback = GXHttpManager.getWithJSON(mContext, GXHttpManager.METHOD_GET, params, new CommonActionCallBackString(mContext) {
            @Override
            public void onSuccess(String msg) {
                super.onSuccess(msg);
                Log.e("MasterActivvty", "onSuccess=" + msg);
                if(mLoginDialog!=null)
                mLoginDialog.dismiss();
                mLoginDialog = null;
                ConstanUtil.islogin = true;

                isloginDelay.schedule(new Runnable() {
                    @Override
                    public void run() {
                        if (ConstanUtil.islogin)
                            ConstanUtil.islogin = false;
                    }
                }, 6000, TimeUnit.MILLISECONDS);
                SPUtils.put(mContext, "ip", ip);
                Constant.WS_URI = ip;
                Constant.HTTP_URI = ip + Constant.HTTP_PORT;

                Intent i = new Intent(mContext, MasterActivity.class);
                if (!ConstanUtil.isSuccessLogin) {
                    //i.setFlags(I)
                }
                mContext.startActivity(i);

                if (mCallback != null) {
                    mCallback.cancel();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Log.e("MasterActivvty", "onFailure=" + msg);
                if (mLoginDialog != null) {
                    mLoginDialog.dismiss();
                    mLoginDialog = null;
                }
                if (!ConstanUtil.islogin)
                    getLoginDialog(mContext).show();
                if (!ConstanUtil.islogin)
                    UIUtil.toast(mContext, "IP地址有误或" + msg);

            }

        });
    }


    //    DatagramSocket socket = null;
    //    DatagramPacket packet = null;
    //    byte buf[] = new byte[]{'5'};

    private static void tryWakeUpServer() {

        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte buf[] = new byte[]{'5'};

        try {
            if (socket == null) {
                socket = new DatagramSocket();
            }
            if (packet == null) {
                packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("255.255.255.255"), 3451);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final DatagramSocket finalSocket = socket;
        final DatagramPacket finalPacket = packet;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    finalSocket.send(finalPacket);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

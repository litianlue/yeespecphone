package com.geetion.microscope_android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geetion.coreOneUtil.UIUtil;
import com.geetion.log.Logger;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.service.Constant;
import com.geetion.microscope_android.service.http.CommonActionCallBackString;
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

/**
 * Created by WongzYe on 15/12/28.
 */

public class LoginActivity extends Activity {

    private EditText mFirstEt;
    private EditText mSecondEt;
    private EditText mThirdEt;
    private EditText mFourthEt;
    private Button mConfirmBtn;
    private String mFirstIP;
    private String mSecondIP;
    private String mThridIP;
    private String mFourthIP;

    //用于测试连接是否成功
    private Callback.Cancelable mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //2016.07.29 : 添加输入法弹出与界面兼容显示设置 :
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initView();
        initListener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCallback != null) {
            mCallback.cancel();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_login);
        mFirstEt = (EditText) findViewById(R.id.et_ip1);
        mSecondEt = (EditText) findViewById(R.id.et_ip2);
        mThirdEt = (EditText) findViewById(R.id.et_ip3);
        mFourthEt = (EditText) findViewById(R.id.et_ip4);
        mConfirmBtn = (Button) findViewById(R.id.btn_confirm);

        String ip = (String) SPUtils.get(this, "ip", "");
        if (ip != null) {
            String[] sArray = ip.split("\\.");
            for (int i = 0; i < sArray.length; i++) {
                Logger.e("Aye", "ip " + sArray[i]);
                switch (i) {
                    case 0:
                        mFirstEt.setText(sArray[i]);
                        break;
                    case 1:
                        mSecondEt.setText(sArray[i]);
                        break;
                    case 2:
                        mThirdEt.setText(sArray[i]);
                        break;
                    case 3:
                        mFourthEt.setText(sArray[i]);
                        break;
                }
            }
        }
    }

    private void initListener() {
        mConfirmBtn.setOnClickListener(btnConfirmListener);
    }

    private View.OnClickListener btnConfirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            mFirstIP = mFirstEt.getText().toString();
            mSecondIP = mSecondEt.getText().toString();
            mThridIP = mThirdEt.getText().toString();
            mFourthIP = mFourthEt.getText().toString();

            tryWakeUpServer();

            if (!mFourthIP.equals("") || !mSecondIP.equals("") || !mThridIP.equals("") || !mFourthIP.equals("")) {
               connectHttp(mFirstIP + "." + mSecondIP + "." + mThridIP + "." + mFourthIP);

            } else {
                UIUtil.toast(LoginActivity.this, "请填写完整");
            }
        }
    };

    private void connectHttp(final String ip) {
        Logger.e("Aye", Constant.HTTP_PROTOCOL + ip + Constant.HTTP_PORT + Constant.CAMERA_GETPHOTOLIST);
        GBaseHttpParams params = new GBaseHttpParams(Constant.HTTP_PROTOCOL + ip + Constant.HTTP_PORT + Constant.CAMERA_GETPHOTOLIST);
        Map<String, String> map = new HashMap<>();
        map.put("page", "1");
        params.putGETParams(map);
        mCallback = GXHttpManager.getWithJSON(LoginActivity.this, GXHttpManager.METHOD_GET, params, new CommonActionCallBackString(LoginActivity.this) {

            @Override
            public void onSuccess(String msg) {
                super.onSuccess(msg);
                SPUtils.put(LoginActivity.this, "ip", ip);
                Constant.WS_URI = ip;
                Constant.HTTP_URI = ip + Constant.HTTP_PORT;
                Logger.e("Aye", " Constant.WS_URI " + Constant.WS_URI + "  Constant.HTTP_URI " + Constant.HTTP_URI);
                Intent i = new Intent(LoginActivity.this, MasterActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                UIUtil.toast(LoginActivity.this, "IP地址有误或" + msg);

            }
        });
    }


    DatagramSocket socket = null;
    DatagramPacket packet = null;
    byte buf[] = new byte[]{'5'};

    private void tryWakeUpServer() {
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.send(packet);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

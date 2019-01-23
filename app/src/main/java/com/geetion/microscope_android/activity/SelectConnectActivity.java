package com.geetion.microscope_android.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.geetion.coreOneUtil.UIUtil;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.service.DataDepot;
import com.geetion.microscope_android.utils.ConstanUtil;
import com.geetion.microscope_android.utils.SPUtils;
import com.geetion.xUtil.ResponeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/12/18.
 */

public class SelectConnectActivity extends Activity {
    private EditText user;
    private EditText password;
    private Button to_login;
    private TextView login;
    private ScheduledExecutorService DELAY_EXECUTORSERVICE = Executors.newScheduledThreadPool(2);//定时线程
    private ProgressBar mProgressbar;
    private long mloginTimer;
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if(mProgressbar.getVisibility()==View.VISIBLE) {
                        UIUtil.toast(SelectConnectActivity.this, "登录超时");
                        mProgressbar.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    private TextView configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_activity);
        user = ((EditText) findViewById(R.id.user_text));
        password = ((EditText) findViewById(R.id.possword_text));
        configuration = ((TextView) findViewById(R.id.configuration_wifi));
        to_login = ((Button) findViewById(R.id.login_bnt));
        login = ((TextView) findViewById(R.id.login_text));
        mProgressbar = ((ProgressBar) findViewById(R.id.progressbar));
        String userstr = (String) SPUtils.get(SelectConnectActivity.this, "user","yeespec");
        String passwordstr = (String) SPUtils.get(SelectConnectActivity.this, "password","123456");

        configuration.setVisibility(View.VISIBLE);

        if(user!=null)
            user.setText(userstr);
        if(password!=null)
            password.setText(passwordstr);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectConnectActivity.this,MasterActivity.class);
                startActivity(intent);
                ConstanUtil.remoteLogin= false;
                finish();

            }
        });

        configuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mWifi.isConnected()) {
                    Intent intent  = new Intent(SelectConnectActivity.this,ServiceConnectWifiAcitivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(SelectConnectActivity.this, "你手机还没链接到wifi,请先链接wifi!", Toast.LENGTH_SHORT).show();
                    Intent intentWifi =  new Intent(Settings.ACTION_WIFI_SETTINGS);//WIFI网络
                    intentWifi.putExtra("extra_prefs_show_button_bar", true);
                    intentWifi.putExtra("extra_prefs_set_back_text","返回");
                    intentWifi.putExtra("extra_prefs_set_next_text","");
                    startActivity(intentWifi);
                }

            }
        });
        checkPermission();
        if (!isLocationEnable(this)) {
            setLocationService();
        }
        to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remoteLogin();
            }
        });
    }
    public  final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider) return true;
        return false;
    }
    private  final int REQUEST_CODE_LOCATION_SETTINGS = 2;
    //跳转到打开定位页面
    private void setLocationService() {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // showToast("自Android 6.0开始需要打开位置权限才可以搜索到Ble设备");
                    Toast.makeText(this, "拒绝权限可能导致部分功能不正常", Toast.LENGTH_SHORT).show();
                }
              ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }

        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户允许改权限，0表示允许，-1表示拒绝 PERMISSION_GRANTED = 0， PERMISSION_DENIED = -1
                //这里进行授权被允许的处理
                Toast.makeText(this, "用户允许授权", Toast.LENGTH_SHORT).show();
            } else {
                //这里进行权限被拒绝的处理

               // finish();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    private boolean isloginAgain =true;
    private void remoteLogin() {
        final String muser = SelectConnectActivity.this.user.getText().toString().trim();
        final String mpassword = password.getText().toString().trim();
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0,8000);
        if(!mpassword.equals("")&&!muser.equals("")){
            mProgressbar.setVisibility(View.VISIBLE);
            mloginTimer = 0;
            AVUser.logInInBackground(muser, mpassword, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if(e==null){
                        //通知服务器手机端登录
                        AVQuery<AVObject> timer = new AVQuery<>("ConnectState");
                        timer.whereEqualTo("user",muser);
                        timer.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                AVObject avObject = list.get(0);
                                JSONObject padstate = avObject.getJSONObject("padstate");

                                try {
                                    long date = padstate.getLong("date");
                                    mloginTimer = date;
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                            }
                        });

                        ResponeUtils.phoneLogin(muser);
                        SPUtils.put(SelectConnectActivity.this,"user",muser);
                        SPUtils.put(SelectConnectActivity.this,"password",mpassword);
                        DELAY_EXECUTORSERVICE.schedule(new Runnable() {
                            @Override
                            public void run() {
                                AVQuery<AVObject> query = new AVQuery<>("ConnectState");
                                query.whereEqualTo("user",muser);
                                query.findInBackground(new FindCallback<AVObject>() {
                                    @Override
                                    public void done(List<AVObject> list, AVException e) {
                                        if(e==null) {
                                            AVObject avObject = list.get(0);
                                            JSONObject padstate1 = avObject.getJSONObject("padstate");
                                            try {
                                                boolean flage = padstate1.getBoolean("flage");
                                                long data = padstate1.getLong("date");
                                                Log.i("minfo","flage="+flage+"  data ="+data+"  mlogintiamer ="+mloginTimer);
                                                if (flage&&(data>mloginTimer)) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Intent intent = new Intent(SelectConnectActivity.this,SelectClientActivity.class);
                                                            startActivity(intent);
                                                            ConstanUtil.remoteLogin = true;
                                                            ConstanUtil.padConnect = true;
                                                            ConstanUtil.loginUser = muser;
                                                            mProgressbar.setVisibility(View.GONE);
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                 /*   if(isloginAgain){
                                                        isloginAgain = flage;
                                                        remoteLogin();
                                                        UIUtil.toast(SelectConnectActivity.this,"login again!");
                                                        return;
                                                    }*/
                                                    isloginAgain = true;
                                                    mProgressbar.setVisibility(View.GONE);
                                                    UIUtil.toast(SelectConnectActivity.this,"登录超时，请检查网络或者平板是否开启");
                                                }
                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            }


                                        }else {
                                            isloginAgain = true;
                                            mProgressbar.setVisibility(View.GONE);
                                            UIUtil.toast(SelectConnectActivity.this,"链接错位，请检测网络");
                                        }
                                    }
                                });
                            }
                        },2000, TimeUnit.MILLISECONDS);
                    }else {
                        isloginAgain = true;
                        mProgressbar.setVisibility(View.GONE);
                        Toast.makeText(SelectConnectActivity.this, "登录失败！！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(SelectConnectActivity.this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
        }
    }


}

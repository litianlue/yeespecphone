package com.geetion.microscope_android.activity;import android.app.Activity;import android.content.Intent;import android.os.Bundle;import android.os.Handler;import android.view.MotionEvent;import com.geetion.microscope_android.R;import com.geetion.microscope_android.utils.ConstanUtil;import com.geetion.microscope_android.utils.SPUtils;public class SplashActivity extends Activity {    private Handler handler = new Handler() {        //2016.08.03 : 修改启动页后直接跳转到主界面 :        public void handleMessage(android.os.Message msg) {            if(ConstanUtil.isAddLogin){                Intent intent = new Intent(SplashActivity.this, SelectConnectActivity.class);                startActivity(intent);                finish();                handler.removeMessages(-1);            }else {                ConstanUtil.remoteLogin= false;                Intent intent = new Intent(SplashActivity.this, MasterActivity.class);                startActivity(intent);                finish();                handler.removeMessages(-1);            }        }        //        public void handleMessage(android.os.Message msg) {        //            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);        //            startActivity(intent);        //            finish();        //            handler.removeMessages(-1);        //        }    };    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_splash);        handler.sendMessageDelayed(handler.obtainMessage(-1), 3000);        SPUtils.put(getBaseContext(), "autoPhoto_views", 1);    }    @Override    public boolean onTouchEvent(MotionEvent event) {        if (event.getAction() == MotionEvent.ACTION_UP) {            handler.sendMessage(handler.obtainMessage(-1));            finish();        }        return super.onTouchEvent(event);    }}
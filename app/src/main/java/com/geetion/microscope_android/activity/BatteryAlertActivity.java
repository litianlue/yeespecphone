package com.geetion.microscope_android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geetion.coreTwoUtil.GActivityManager;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.service.DataDepot;

/**
 * Created by WongzYe on 16/1/25.
 */

public class BatteryAlertActivity extends Activity implements View.OnClickListener{

    private TextView mToastMessage;
    private TextView mAction;
    private Button mConfirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GActivityManager.getActivityManager().addActivity(this);
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        setMessage();
        super.onResume();
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    private void initView() {
        setContentView(R.layout.activity_battery_alert);
        mToastMessage = (TextView) findViewById(R.id.toast_msg);
        mAction = (TextView) findViewById(R.id.tv_action);
        mConfirmBtn = (Button) findViewById(R.id.electricity_ok_btn);
    }

    private void initListener() {
        mConfirmBtn.setOnClickListener(this);
    }

    private void setMessage() {
        mToastMessage.setText("中控端电池只剩" + DataDepot.battery + "%");
        mAction.setText(DataDepot.battery <= 10 ? "准备关机" : "电量低");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.electricity_ok_btn:
                GActivityManager.getActivityManager().finishActivity();
                break;
        }
    }
}

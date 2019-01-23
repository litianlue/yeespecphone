package com.geetion.microscope_android.activity;

import android.os.Bundle;

import com.geetion.microscope_android.R;


public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
        super.onCreate(savedInstanceState);
        activity = this;
    }

}

package com.geetion.microscope_android.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.geetion.microscope_android.R;
import com.geetion.microscope_android.custom.MoveImageview;

/**
 * Created by yuchunrong on 2017-06-22.
 */

public class MytestActivity  extends BaseActivity{
    private MoveImageview moveimag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mytest_activity);
        moveimag = ((MoveImageview) findViewById(R.id.moveimageview));
    }
}

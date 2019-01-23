package com.geetion.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public interface Receive {
        void callBack(Context context, Intent intent);
    }

    Receive myReceive;

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        myReceive.callBack(arg0, arg1);

    }

    public void setOnReceive(Receive listener) {
        myReceive = listener;
    }

}

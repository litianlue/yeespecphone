package com.geetion.microscope_android.model;

import com.geetion.xUtil.GBaseHttpResult;

/**
 * Created by WongzYe on 15/12/16.
 */
public class WifiModel extends GBaseHttpResult {

    private String wifiName;
    private String securityMode;
    private int level;

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getSecurityMode() {
        return securityMode;
    }

    public int getLevel() {
        return level;
    }

    public void setSecurityMode(String securityMode) {
        this.securityMode = securityMode;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

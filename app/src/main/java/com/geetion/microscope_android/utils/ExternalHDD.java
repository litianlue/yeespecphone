package com.geetion.microscope_android.utils;

import android.content.Context;

import java.io.File;

/**
 * For detection and access to external HDD connected via USB OTG
 * 检测和访问外部硬盘通过USB OTG连接
 */
public class ExternalHDD {

    private final String storageDir;
    private final String externalHddDir;

    private final File externalHddPath;

    /**
     * ...
     */
    public ExternalHDD(Context context) {

        // TODO detect main storage dir
        storageDir = "/storage/";

        // TODO detect external HDD subdirectory
        externalHddDir = "usbcard1";

        externalHddPath = new File(storageDir, externalHddDir);
    }

    /**
     * Access to external HDD connected to device
     *
     * @return File pointing to HDD
     */
    public File getUSBCardPath() {
        return externalHddPath;
    }
}

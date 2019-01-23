package com.geetion.coreOneUtil;

import android.os.Environment;

public class DefaultURL {


    public static final boolean IS_DEBUG = false;
    public static final String DEFAULT_FOLDER = "/Geetion";

    /**
     * HTTP下载，默认缓存地址，文件下载地址，文件下载名字
     */
    public static final String DEFAULT_CACHEDIRNAME = Environment.getExternalStorageDirectory().getPath() + DEFAULT_FOLDER + "/netCache/";
    public static final String DEFAULT_SAVEFILEPATH = Environment.getExternalStorageDirectory().getPath() + DEFAULT_FOLDER + "/download/";
    public static final String DEFAULT_FILENAME = "httpDownLoadFile.temp";

    /**
     * 相机拍照默认照片存储地址
     */
    public static final String DEFAULT_SAVEPHOTOPATH = Environment.getExternalStorageDirectory().getPath() + DEFAULT_FOLDER + "/photo";

    /**
     * 录音默认文件名与存储地址
     */
    public final static String DEFAULT_AUDIO_NAME = "recording.mp3";
    public static final String DEFAULT_SAVEAUDIOPAHT = Environment.getExternalStorageDirectory().getPath() + DEFAULT_FOLDER + "/audio";
}

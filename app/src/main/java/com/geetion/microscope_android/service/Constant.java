package com.geetion.microscope_android.service;

/**
 * Created by WongzYe on 15/12/18.
 */
public class Constant {

    public static final boolean DEBUG = false;
    public static final String DIR_NAME = "USBCameraTest";

    /**
     * WebSocket服务端口
     */
    public static final String WS_PORT_PIC = ":8121";
    public static final String WS_PORT_CAMERA_OPTIONS = ":8122";
    public static final String WS_PORT_PAD_STATUS = ":8123";
    public static final String WS_PORT_DEVICE_STATUS = ":8124";

    /**
     * 地址相关
     */
    public static final String WS_PROTOCOL = "ws://";
    public static final String HTTP_PROTOCOL = "http://";
    public static final String HTTP_PORT = ":5000";
    public static String WS_URI;// = "192.168.31.106:";
    public static String HTTP_URI;// = "192.168.31.106:" + HTTP_PORT;

    /**
     * WebSocket通道
     */
    public static final String PICTURE_CHANNEL = "/picture";
    public static final String CAMERA_CHANNEL = "/camera/options";
    public static final String PAD_STATUS_CHANNEL = "/pad/status";
    public static final String DEVICES_STATUS_CHANNEL = "/device/operation/status";

    /**
     * 拍照相关
     */
    public static final String CAMERA_TAKEPHOTO = "/camera/takephoto";
    public static final String CAMERA_GETPHOTOLIST = "/camera/getphotolist";
    public static final String CAMERA_GETPHOTO = "/camera/getphoto";
    public static final String CAMERA_DOWNLOAD_PHOTO = "/camera/download_photo";
//    public static String GET_PHOTO_URL = Constant.HTTP_PROTOCOL + Constant.HTTP_URI + Constant.CAMERA_GETPHOTO + "?picname=";

    /**
     * 录像相关
     */
    public static final String VIDEO_ACTION = "/video/action";
    public static final String VIDEO_GET_VIDEO = "/video/get_video";
    public static final String VIDEO_GET_VIDEO_LIST = "/video/get_video_list";
    public static final String VIDEO_DOWNLOAD_VIDEO = "/video/download_video";

    /**
     * 取色，着色
     */
    public static final String Get_Color = "/color/get";
    public static final String Set_Color = "/color/set";
    //着色名称
    public static final String Get_ColosString = "/colorstring/get";
    public static final String Set_ColorString = "/colorstring/set";
    //激发快和物镜参数
    public static final String Get_Configuration = "/configuration/get";
    public static final String Set_Configuration = "/configuration/set";
    /**
     * wifi相关
     */
    public static final String Get_WIFI_List = "/wifi/get";
    public static final String Set_WIFI = "/wifi/set";

    /**
     * 关机
     */
    public static final String SHUT_DOWN = "/pad/shutdown";
    /**
     * 删除
     */
    public static final String DEL = "/pad/del";

}

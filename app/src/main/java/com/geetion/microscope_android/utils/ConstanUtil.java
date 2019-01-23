package com.geetion.microscope_android.utils;

/**
 * Created by yuchunrong on 2017-06-13.
 */

public class ConstanUtil {

    //configuration
    public static boolean LONZA = true;//是否为LONZA 客户端 无物镜切换 无摇杆
    public static boolean IS_SELF_CHECK = true;//是否开启自检  false 自检按键被屏蔽
    public static boolean ROCKER = false;//是否有摇杆



    public static String  ConvergenceNumber = "50";
    public static  String PARAM_TYPE_PIC_LOAD= "param_type_picture";//图片
    public static String CLIENTCLASS = "ClientInstruction";
    public static String PRIVIEW_PHOTO_CLASS = "PriviewPhoto";
    public  static  boolean padConnect = false;//平板是否在链接
    public  static  boolean remoteLogin = false;//是否远程登录
    public  static  String  loginUser = "";//登录用户

    public  static  boolean  isAddLogin = true;//是否开启远程登录功能
    public static  int lastsaturation=100;
    public static  int lastmultiple=100;
    public static  int  record = 3;//1 :recoid 2 stoprecored
    public static  int  lastrecord = 3;//1 :recoid 2 stoprecored
    public static  boolean  isrecycnCameraView =false;
    public static  String   lastautofocuview ="";

    public static int  processBarAutoPhoto = 0;//自动拍照进度/张
    public static int  autoPhotoCount = 0;//自动拍照总数/张
    public static String  stopAutoPhotoStr = "";//自动拍照停止时间
    public static String ThumbnaiPicterName="";
    public static String LIGHTTYPE ="1";//   “,”为英文输入法
    public static int LIGHTNUMBER =1;
    public static final int OBJECTIVE_VALUE_A = 5;//物镜A倍数
    public static final int OBJECTIVE_VALUE_B = 20;//物镜B倍数
    public static final int OBJECTIVE_VALUE_C = 50;//物镜C倍数
    public static int[] OBJECTIVES = {OBJECTIVE_VALUE_A, OBJECTIVE_VALUE_B};
    public static  boolean isAutophoto = false;//是否准备自动拍照
    public static  boolean isAutophotoGoing = false;//是否正在自动拍照

    public static int currentRecolorPosition;

    public static  boolean isOperationISO = true;//是否可以增加ISO
    public static  boolean isOperationBrigthness = true;//
    // 是否可以增加亮度
    public static  boolean isOperationGamma = true;//是否可以增加焦点值

    public static  String  connectip = "";//连接IP
    public static  boolean  islogin = false;//是否登陆中
    public static  boolean  isSuccessLogin = false;//是否登陆成功
    public static  boolean  firstLogin = true;//第一次登录
    public static String upDataAction= "upadataaction";



    public static long lastClickTime;
    public static boolean isFastDoubleClick(int milliseconds) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD <milliseconds) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}

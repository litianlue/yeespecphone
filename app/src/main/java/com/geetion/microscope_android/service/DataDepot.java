package com.geetion.microscope_android.service;

/**
 * Created by WongzYe on 15/12/18.
 */
public class DataDepot {
    public static String intevalAndPercentage;     //
    //摄像头参数
    public static String brightness = "50";     //
    public static String gain = "0";            //
    public static String recolor = "0";         //
    public static String recolorstring = "No-Color";
    public static String saturation = "0";      //
    public static String contrast = "10";       //
    public static String gamma = "0";
    //
    public static int maxiso = 100;//20170612新增加最大增益值参数
    public static int maxbrightness=0;//20170612新增加最大亮度值参数
    public static String vessels="0";//20170612新增加自动对焦选择器皿参数 1：载玻片 2：培养皿 3：培养板
    public static boolean isAutofocus=false;
    public static int autophoto_view=1;//拍照状态 1 可拍照 2 准备自动拍照  3 为自动拍照
    //2016.08.02 : 新增 :用于接收从平板端传输过来的当前物镜实际倍数 显示 :
    public static String currentContrast = "10";           //

    public static boolean is_record;
    public static boolean autophotdelay=false;
    //平板信息
    public static boolean isexport=false;//是否正在导出
    public static String voltagelevel="100";  ///20170615增加电压百分比
    public static String autophotoview="0";  ///20170615增加自动拍照状态
    public static int battery;
    public static String wifi;
    public static String cameraConnect;
    public static String connectCount;
    public static String deviceIP;
    public static String deviceMac;
    public static String employMemory;
    public static String totalMemory;
    public static String recodigtimer;
    public static int padWidth;         //
    public static int padHeight;        //
    public static int cameraWidth=1360;      //
    public static int cameraHeight=1040;     //

    public static int pngsize;      //
    public static int moviessize;     //
    //屏蔽状态
    public static boolean enable;
    public static boolean uartisbusy;

    public static long startdatetimer;
    public static long enddatetimer;//自动拍照的开始和结束时间

    public static int autophotocount;
    public static String autophotostoptimer;
    public static String autophotocNumber;
    public static int autophotoprocess;

    //2016.07.27 : 添加打印Json数据的方法 :
    public static String
    toStringMethod() {

        return "DataDepot{\n" +
                "intevalAndPercentage =  " + intevalAndPercentage + " ; \n" +
                "autophotocount =  " + autophotocount + " ; \n" +
                "autophotostoptimer =  " + autophotostoptimer + " ; \n" +
                "autophotostcNumber =  " + autophotocNumber + " ; \n" +
                "autophotoprocess =  " + autophotoprocess + " ; \n" +

                "recodigtimer =  " + isexport + " ; \n" +
                "recodigtimer =  " + recodigtimer + " ; \n" +
                "recolorstring =  " + recolorstring + " ; \n" +
                "startdatetimer =  " + startdatetimer + " ; \n" +
                "enddatetimer =  " + enddatetimer + " ; \n" +
                "uartisbusy =  " + uartisbusy + " ; \n" +
                "brightness =  " + brightness + " ; \n" +
                "gain =  " + gain + " ; \n" +
                "voltagelevel =  " + voltagelevel + " ; \n" +
                "autophotoview =  " + autophotoview + " ; \n" +
                "isAutofocus =  " + isAutofocus + " ; \n" +
                "maxbrightness =  " + maxbrightness + " ; \n" +
                "maxbrightness =  " + maxiso + " ; \n" +
                "vessels =  " + vessels + " ; \n" +
                "recolor =  " + recolor + " ; \n" +
                "saturation =  " + saturation + " ; \n" +
                "contrast =  " + contrast + " ; \n" +
                "gamma =  " + gamma + " ; \n" +
                "currentContrast =  " + currentContrast + " ; \n" +
                "is_record =  " + is_record + " ; \n" +
                "padWidth =  " + padWidth + " ; \n" +
                "padHeight =  " + padHeight + " ; \n" +
                "cameraWidth =  " + cameraWidth + " ; \n" +
                "cameraHeight =  " + cameraHeight + " ; \n" +
                "pngsize =  " + pngsize + " ; \n" +
                "moviessize =  " + moviessize + " ; \n" +
                "autophotdelay =  " + autophotdelay + " ; \n" +
                "}";

    }

}

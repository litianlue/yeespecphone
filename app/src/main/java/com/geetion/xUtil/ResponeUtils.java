package com.geetion.xUtil;


import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.GetCallback;
import com.geetion.microscope_android.utils.ConstanUtil;


import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Administrator on 2017/12/23.
 */

public class ResponeUtils {
    public static final String CONNECT_STATE="ConnectState";
    public static final String CLIENT_INSTURCTION="ClientInstruction";
    public static final String PRIVIEW_PHOTO="PriviewPhoto";
    public static final String EQUIPMENT_CONNECT_STATE="EquipmentConnectState";
    public static final String PARAMETER="Parameter";
    public static final String CONFIGURATION="Configuration";
    public static final String CAPTURE = "capture";
    public static final int CAPTURE_TYPE=0;
    private static final String CAPTURE_STILL = "capture_still";
    public static final int CAPTURE_STILL_TYPE=1;
    private static final String PARAM_TYPE_SATURATION = "param_type_saturation";
    public static final int PARAM_TYPE_SATURATION_TYPE=2;
    private static final String PARAM_TYPE_CONTRAST = "param_type_contrast";
    public static final int PARAM_TYPE_CONTRAST_TYPE=3;
    private static final String COLORS = "colors";
    public static final int COLORS_TYPE=4;
    private static final String RECOLORS_STRING = "recolors_string";
    public static final int RECOLORS_STRING_TYPE=5;
    private static final String PARAM_TYPE_ISO = "param_type_iso";
    public static final int PARAM_TYPE_ISO_TYPE = 6;
    private static final String PARAM_TYPE_BRIGHTNESS = "param_type_brightness";
    public static final int PARAM_TYPE_BRIGHTNESS_TYPE=7;
    private static final String SHUT_DOWN = "shut_down";
    private static final int SHUT_DOWN_TYPE=8;
    public static final String PARAM_TYPE_MANUAL_FOCUS = "param_type_manual_focus";
    public static final int PARAM_TYPE_MANUAL_FOCUS_TYPE=9;
    private static final String PARAM_TYPE_GAMMA = "param_type_gamma";
    public static final int PARAM_TYPE_GAMMA_TYPE=10;
    public static final String PARAM_TYPE_GAMMA_DOMN = "param_type_gamma_domn";
    public static final int PARAM_TYPE_GAMMA_DOMN_TYPE=11;
    public static final String PARAM_TYPE_MANUAL_FOCUS_STOP = "param_type_manual_focus_stop";
    public static final int PARAM_TYPE_MANUAL_FOCUS_STOP_TYPE=12;
    private static final String PARAM_TYPE_VESSELS = "param_type_vessels";
    public static final int PARAM_TYPE_VESSELS_TYPE=13;
    public static final String PARAM_TYPE_UpDATE_PICTER = "param_type_update_picter";//更新右下角缩略图
    public static final int PARAM_TYPE_UpDATE_PICTER_TYPE=14;
    private static final String PARAM_TYPE_AUTOPHOTO = "param_type_autophoto";//自动拍照
    public static final int PARAM_TYPE_AUTOPHOTO_TYPE=15;
    private static final String PARAM_TYPE_STOPAUTOPHOTO = "param_type_stopautophoto";//停止自动拍照
    public static final int PARAM_TYPE_STOPAUTOPHOTO_TYPE=16;
    private static final String PARAM_TYPE_AUTOPHOTOPREPARE = "param_type_autophotoprepare";//准备自动拍照
    public static final int PARAM_TYPE_AUTOPHOTOPREPARE_TYPE=17;
    private static final String PARAM_TYPE_CONSTAST = "param_type_constast";//对照组定位设定
    public static final int PARAM_TYPE_CONSTAST_TYPE=18;
    private static final String PARAM_TYPE_ROCKERVIEW = "param_type_rockerview";//摇杆控制
    public static final int PARAM_TYPE_ROCKERVIEW_TYPE=19;



    public static final String PARAM_TYPE_PIC_LOAD= "param_type_picture";//图片
    public static final String PARAM_TYPE_PIC_NAME_LOAD= "param_type_picture_name";//图片名称
    public static final int PARAM_TYPE_PIC_LOAD_TYPE=20;
    public static final String PARAM_TYPE_LOAD_PIC_NAME= "param_type_load_pic_name";//加载图片名
    public static final int PARAM_TYPE_LOAD_PIC_NAME_TYPE=21;
    public static final String PARAM_TYPE_SCAL_PIC_LOAD= "param_type_scal_picture";//缩略图片
    public static final String PARAM_TYPE_SCAL_PIC_NAME_LOAD= "param_type_scal_picture_name";//缩略图片名称
    public static final int PARAM_TYPE_SCAL_PIC_LOAD_TYPE=22;
    public static final String PARAM_TYPE_LOAD_SCAL_PIC_NAME= "param_type_load_scal_pic_name";//加载缩略图图片名
    public static final int PARAM_TYPE_LOAD_SCAL_PIC_NAME_TYPE=23;
    public static final String PARAM_TYPE_OFF = "param_type_off";//关机
    public static final int PARAM_TYPE_OFF_TYPE=24;
    private static final String PARAM_TYPE_EXPORT = "param_type_export";//导出
    public static final int PARAM_TYPE_EXPORT_TYPE=25;
    public static void sendParamInstruction(final int type, final int param, final String user){
        AVQuery<AVObject> query = new AVQuery<>("ClientInstruction");
        query.whereEqualTo("user", user);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject account, AVException e) {
                Log.e("ResponeUtils","user="+user+" account="+account+"e="+e);
                if(e==null&&account!=null) {
                    switch (type){
                        case PARAM_TYPE_SATURATION_TYPE://激发快
                            account.put(PARAM_TYPE_SATURATION, param);
                            break;
                        case PARAM_TYPE_CONTRAST_TYPE://物镜
                            account.put(PARAM_TYPE_CONTRAST, param);
                            break;
                        case PARAM_TYPE_VESSELS_TYPE://器皿
                            account.put(PARAM_TYPE_VESSELS, param);
                            break;

                        case PARAM_TYPE_ISO_TYPE://ISO
                            account.put(PARAM_TYPE_ISO, param);
                            break;
                        case PARAM_TYPE_BRIGHTNESS_TYPE://brightness
                            account.put(PARAM_TYPE_BRIGHTNESS, param);
                            break;
                        case PARAM_TYPE_GAMMA_TYPE://gamma +
                            account.put(PARAM_TYPE_GAMMA, param);
                            break;
                        case PARAM_TYPE_GAMMA_DOMN_TYPE://gamma -
                            account.put(PARAM_TYPE_GAMMA_DOMN, param);
                            break;
                        case CAPTURE_TYPE://拍照
                            account.put(CAPTURE, true);
                            break;
                        case PARAM_TYPE_MANUAL_FOCUS_STOP_TYPE://停止自动对焦
                            account.put(PARAM_TYPE_MANUAL_FOCUS_STOP, true);
                            break;
                        case PARAM_TYPE_STOPAUTOPHOTO_TYPE://停止自动拍照
                            account.put(PARAM_TYPE_STOPAUTOPHOTO, true);
                            break;
                        case CAPTURE_STILL_TYPE://录制视频
                            account.put(CAPTURE_STILL, true);
                            break;
                        case PARAM_TYPE_OFF_TYPE:
                            account.put(PARAM_TYPE_OFF, true);
                            break;

                    }
                    AVSaveOption option = new AVSaveOption();
                    option.query(new AVQuery<>("ClientInstruction").whereEqualTo("user", user));
                    option.setFetchWhenSave(true);
                    account.saveInBackground();
                }
            }
        });
    }
    public static void sendStringInstruction(final int type, final String param,final  String user){
        AVQuery<AVObject> query = new AVQuery<>("ClientInstruction");
        query.whereEqualTo("user", user);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject account, AVException e) {
                if(e==null) {
                    switch (type){
                        case PARAM_TYPE_LOAD_PIC_NAME_TYPE://激发快
                            account.put(PARAM_TYPE_LOAD_PIC_NAME, param);
                            break;

                    }
                    AVSaveOption option = new AVSaveOption();
                    option.query(new AVQuery<>("ClientInstruction").whereEqualTo("user", user));
                    option.setFetchWhenSave(true);
                    account.saveInBackground();
                }
            }
        });
    }
   public static void  sendObjectInstruction(final int type, final JSONObject object,final String user){
       AVQuery<AVObject> query = new AVQuery<>("ClientInstruction");
       query.whereEqualTo("user", user);
       query.getFirstInBackground(new GetCallback<AVObject>() {
           @Override
           public void done(final AVObject account, AVException e) {
               if(e==null) {
                   switch (type){

                       case PARAM_TYPE_CONSTAST_TYPE://对照组
                           account.put(PARAM_TYPE_CONSTAST, object);
                           break;

                       case PARAM_TYPE_MANUAL_FOCUS_TYPE://自动对焦
                           account.put(PARAM_TYPE_MANUAL_FOCUS, object);
                           break;
                       case PARAM_TYPE_AUTOPHOTO_TYPE://自动拍照
                           account.put(PARAM_TYPE_AUTOPHOTO, object);
                           break;
                       case PARAM_TYPE_ROCKERVIEW_TYPE://摇杆状态
                           account.put(PARAM_TYPE_ROCKERVIEW, object);
                           break;
                       case PARAM_TYPE_UpDATE_PICTER_TYPE://更新缩略图文件名
                           account.put(PARAM_TYPE_UpDATE_PICTER, object);
                           break;
                       case PARAM_TYPE_EXPORT_TYPE://导出文件
                           account.put(PARAM_TYPE_EXPORT, object);
                           break;
                   }
                   AVSaveOption option = new AVSaveOption();
                   option.query(new AVQuery<>("ClientInstruction").whereEqualTo("user", user));
                   option.setFetchWhenSave(true);
                   account.saveInBackground();
               }
           }
       });
   }

    public static  void phoneLogin(final String user){
        AVQuery<AVObject> query = new AVQuery<>(CONNECT_STATE);
        query.whereEqualTo("user",user);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject account, AVException e) {
                if(e==null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("flage",true);
                        jsonObject.put("date",System.currentTimeMillis());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    account.put("phonestate", jsonObject);
                    AVSaveOption option = new AVSaveOption();
                    option.query(new AVQuery<>(CONNECT_STATE).whereGreaterThanOrEqualTo("user",user));
                    option.setFetchWhenSave(true);
                    account.saveInBackground();
                }
            }
        });
    }
    public static void phoneUnRegist(final String user){
        //通知服务器平板端退出
        AVQuery<AVObject> query = new AVQuery<>(CONNECT_STATE);
        query.whereEqualTo("user", user);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject account, AVException e) {
                if(e==null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("flage",false);
                        jsonObject.put("date",System.currentTimeMillis());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    account.put("phonestate", jsonObject);
                    AVSaveOption option = new AVSaveOption();
                    option.query(new AVQuery<>(CONNECT_STATE).whereEqualTo("user", user));
                    option.setFetchWhenSave(true);
                    account.saveInBackground();
                }
            }
        });
    }
}

package com.geetion.microscope_android.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.YuvImage;
import android.media.MediaCodec;
import android.nfc.Tag;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVLiveQuery;
import com.avos.avoscloud.AVLiveQueryEventHandler;
import com.avos.avoscloud.AVLiveQuerySubscribeCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.geetion.coreOneUtil.UIUtil;

import com.geetion.log.Logger;

import com.geetion.microscope_android.activity.SelectConnectActivity;
import com.geetion.microscope_android.application.BaseApplication;
import com.geetion.microscope_android.service.http.CommonActionCallBackString;
import com.geetion.microscope_android.service.webSocket.BaseWebSocketCallback;
import com.geetion.microscope_android.utils.Bmp2YUV;
import com.geetion.microscope_android.utils.ByteZip;
import com.geetion.microscope_android.utils.ConstanUtil;
import com.geetion.microscope_android.utils.DataUtil;
import com.geetion.microscope_android.utils.SPUtils;
import com.geetion.microscope_android.widget.surfaceview.MediaCodecSurfaceView;
import com.geetion.xUtil.GBaseHttpParams;
import com.geetion.xUtil.GXHttpManager;
import com.geetion.xUtil.ResponeUtils;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import example.sszpf.x264.x264sdk;

import static com.geetion.microscope_android.activity.MasterActivity.DISMISSAUTOFOCUS_GONE_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.DISMISSAUTOFOCUS_VIEW_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.DISMISSTIMER_VIEW_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.DISMISS_DELAY_DIALOG_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.DISMISS_RECORD_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.DISMISS_STUBITMAP_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.HANDLE_MESSAGE_DISMISS_PROGRESS;
import static com.geetion.microscope_android.activity.MasterActivity.HANDLE_MESSAGE_LISTENING;
import static com.geetion.microscope_android.activity.MasterActivity.PARAM_TYPE_CONSTAST;
import static com.geetion.microscope_android.activity.MasterActivity.PARAM_TYPE_EXPORT;
import static com.geetion.microscope_android.activity.MasterActivity.PARAM_TYPE_MANUAL_FOCUS;
import static com.geetion.microscope_android.activity.MasterActivity.PARAM_TYPE_MANUAL_FOCUS_STOP;
import static com.geetion.microscope_android.activity.MasterActivity.PARAM_TYPE_ROCKERVIEW;
import static com.geetion.microscope_android.activity.MasterActivity.PARAM_TYPE_UPDAATE_PICTER;
import static com.geetion.microscope_android.activity.MasterActivity.SHOW_AUTOPHOTO_DIALOG_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.SHOW_EXPORT_DIALOG_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.UDATAAUTOFOCUS_PER_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.UDATAAUTOFOCUS_VIEW_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.UDATAAUTOFOCUS_VISSBLE_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.UDATAPROCESSBAR_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.UDATAREMENUCOLOR_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.UDATATIMER_VIEW_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.UDATAUI_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.UDATA_MULTIPLE_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.UDATA_RECORD_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.UPADAT_GROUTH_RATE;
import static com.geetion.microscope_android.activity.MasterActivity.UPADAT_IAMGE_ALBUM;
import static com.geetion.microscope_android.activity.MasterActivity.UPADAT_IMAGE_MESSAGE;
import static com.geetion.microscope_android.activity.MasterActivity.UPADAT_MESSAGE_LISTENING;
import static com.geetion.microscope_android.activity.MasterActivity.VIEW_MESSAGEII_UPADA;
import static com.geetion.microscope_android.activity.MasterActivity.VIEW_MESSAGEI_UPADA;

/**
 * Created by Administrator on 2018/7/14.
 */

public class MasterService extends Service {
    private final  String TAG = "MasterService";
    private Future<WebSocket> picAsyncScoket;
    private Future<WebSocket> cameraInfoScoket;
    private WebSocket cameraCMDScoket;
    private RequestQueue mQueue;
    private Map<String, String> webScoketResult = new HashMap<>();
    private Callback.Cancelable mPicNameCallback;
    private Object synchronos = new Object();
    private MyBinder binder  = new MyBinder();
    private Handler handler;
    private Handler dismissHandler;
    private Activity activity;
    private String userName;
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(1);
    private ExecutorService workExecutorService = Executors.newFixedThreadPool(1);
    private ScheduledExecutorService DELAY_EXECUTORSERVICE = Executors.newScheduledThreadPool(2);//定时线程

    private AVLiveQuery doingLiveQuery;
    private int loginNumber = 2;//登录次数；
    private boolean isListnessConnect = false;//是否监听数据更新
    private Timer requestTimer;
    private TimerTask requestTask;
    private boolean updataConfiguration = true;
    private MediaCodecSurfaceView mSurfaceView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraInfoScoket != null) {
            if (cameraInfoScoket.tryGet() != null) {
                BaseApplication.closeBySelf = true;
                cameraInfoScoket.tryGet().close();
            }
            cameraInfoScoket = null;
        }
        if (mPicNameCallback != null) {
            mPicNameCallback.cancel();
            mPicNameCallback = null;
        }
        if (mQueue != null) {

            mQueue.stop();
            mQueue.getCache().clear();
            mQueue.cancelAll("btn_photo_album");
            mQueue = null;

        }

        if (ConstanUtil.remoteLogin) {
            dismissHandler.removeMessages(HANDLE_MESSAGE_LISTENING);
            dismissHandler.removeMessages(HANDLE_MESSAGE_DISMISS_PROGRESS);
            isListnessConnect = false;
            if (doingLiveQuery != null) {
                doingLiveQuery.unsubscribeInBackground(new AVLiveQuerySubscribeCallback() {
                    @Override
                    public void done(AVException e) {

                    }
                });
            }
            final AVQuery<AVObject> query = new AVQuery<>("ConnectState");
            query.whereEqualTo("user", userName);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject account, AVException e) {
                    if (e == null && account != null) {
                        account.put("phonestate", false);
                        AVSaveOption option = new AVSaveOption();
                        option.query(new AVQuery<>("ConnectState").whereEqualTo("user", userName));
                        option.setFetchWhenSave(true);
                        account.saveInBackground();
                    }
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return  START_NOT_STICKY;
    }
    //接收平板电脑传输数据
    private void cameraInfoWebSocket() {

        BaseWebSocketCallback cameraInfoCallback = new BaseWebSocketCallback(activity, cameraInfoScoket) {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                super.onCompleted(ex, webSocket);
                if (webSocket == null)
                    return;
                if (cameraCMDScoket == null) {
                    cameraCMDScoket = webSocket;
                }
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {

                        JSONObject json = null;
                        try {
                            json = new JSONObject(s);
                            String filename = json.optString("filename");
                            String mgamma = json.optString("mgamma");
                            if (filename.length() > 0) {
                                String mFilename = filename.substring(filename.lastIndexOf("/") + 1);

                                if (!ConstanUtil.ThumbnaiPicterName.equals(mFilename.trim())) {
                                    ConstanUtil.ThumbnaiPicterName = mFilename.trim();

                                    handler.removeMessages(UDATAPROCESSBAR_MESSAGE);
                                    handler.sendEmptyMessage(UDATAPROCESSBAR_MESSAGE);
                                    handler.removeMessages(3);
                                    handler.sendEmptyMessageDelayed(3, 5000);
                                    getPicture();
                                }

                                return;
                            }
                            if (mgamma.length() > 0) {
                                DataDepot.gamma = mgamma;
                                return;
                            }

                            DataDepot.recodigtimer = json.optString("recondingtimer");
                            DataDepot.brightness = json.optString("brightness");
                            DataDepot.gain = json.optString("gain");
                            DataDepot.recolor = json.optString("recolor");
                            DataDepot.recolorstring = json.optString("recolorstring");
                            DataDepot.autophotoview = json.optString("autophotoview");
                            DataDepot.contrast = json.optString("contrast");
                            String voltagelevel = json.optString("voltagelevel");
                            int voltage = Integer.parseInt(voltagelevel);
                            int curentvaltage = Integer.parseInt(DataDepot.voltagelevel);
                            int countvaltage = Math.abs(curentvaltage - voltage);
                            if (countvaltage > 5) {
                                DataDepot.voltagelevel = voltagelevel;
                            }


                            DataDepot.gamma = json.optString("gamma");
                            // DataDepot.maxbrightness = json.optString("maxbrightness");
                            DataDepot.vessels = json.optString("vessels");
                            DataDepot.isAutofocus = json.optBoolean("isAutofocus");
                            DataDepot.isexport = json.optBoolean("isexport");

                            DataDepot.saturation = json.optString("saturation");
                            DataDepot.is_record = json.optBoolean("is_record");
                            DataDepot.uartisbusy = json.optBoolean("isswitch");
                            DataDepot.padWidth = json.optInt("pad_width");
                            DataDepot.padHeight = json.getInt("pad_height");
                            // DataDepot.cameraHeight = json.getInt("camera_height");
                            // DataDepot.cameraWidth = json.getInt("camera_width");
                            DataDepot.pngsize = json.getInt("pngfiles");
                            DataDepot.moviessize = json.getInt("moviesfiles");
                            DataDepot.startdatetimer = json.optLong("startdatetimer");
                            DataDepot.enddatetimer = json.optLong("enddatetimer");
                            DataDepot.autophotdelay = json.optBoolean("stopautophotodelay");

                            DataDepot.autophotocount = json.getInt("autophotocount");
                            DataDepot.autophotoprocess = json.getInt("autophotoprocess");
                            DataDepot.autophotostoptimer = json.optString("autophototimer");
                            DataDepot.autophotocNumber = json.optString("convergence");
                            //2016.08.02 : 新增  : 用于接收从平板端传输过来的当前物镜实际倍数 显示 :
                            DataDepot.currentContrast = json.getString("current_contrast");
                            DataDepot.intevalAndPercentage = json.getString("percentage");
                            SPUtils.put(activity, "percentage", DataDepot.intevalAndPercentage);
                            String contrastkey = json.optString("contrastkey");
                            Log.w("contrast", "contrastkey= " + contrastkey);
                            Log.w("contrast", " DataDepot.intevalAndPercentage= " + DataDepot.intevalAndPercentage);
                            String[] checks = contrastkey.substring(1, contrastkey.length() - 1).split(",");


                            if (checks.length > 1) {
                                for (int i = 0; i < checks.length; i++) {
                                    boolean ischeck = Boolean.parseBoolean(checks[i]);
                                    DataUtil.CheckNums[i] = ischeck;
                                    Log.w("contrast", "DataUtil.CheckNums= " + DataUtil.CheckNums[i]);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("AyeI", "DATA= " + DataDepot.toStringMethod());

                        handler.sendEmptyMessage(UDATAUI_MESSAGE);

                        if (DataDepot.is_record) {
                            handler.sendEmptyMessage(UDATATIMER_VIEW_MESSAGE);

                        } else if (!DataDepot.is_record) {
                            handler.sendEmptyMessage(DISMISSTIMER_VIEW_MESSAGE);
                        }

                        if (DataDepot.isAutofocus) {
                            handler.sendEmptyMessage(UDATAAUTOFOCUS_VIEW_MESSAGE);

                        } else if (!DataDepot.isAutofocus && !DataDepot.autophotoview.trim().equals("visible")) {
                            handler.sendEmptyMessage(DISMISSAUTOFOCUS_VIEW_MESSAGE);

                        }

                        ConstanUtil.lastautofocuview = DataDepot.autophotoview.trim();
                        if (DataDepot.autophotoview.trim().equals("visible")) {

                            handler.sendEmptyMessage(UDATAAUTOFOCUS_VISSBLE_MESSAGE);

                            DataDepot.autophoto_view = 3;
                            ConstanUtil.isAutophoto = false;
                            ConstanUtil.isAutophotoGoing = true;

                        } else if (DataDepot.autophotoview.equals("gone")) {
                            handler.sendEmptyMessage(DISMISSAUTOFOCUS_GONE_MESSAGE);

                            DataDepot.autophotoprocess = 0;
                            DataDepot.autophotoprocess = 0;
                            if (!DataDepot.autophotdelay) {
                                ConstanUtil.isAutophotoGoing = false;
                            }
                            DataDepot.autophoto_view = 1;

                        } else if (DataDepot.autophotoview.equals("prepare")) {
                            handler.sendEmptyMessage(UDATAAUTOFOCUS_PER_MESSAGE);

                            DataDepot.autophoto_view = 2;

                        }

                        if (!DataDepot.saturation.equals("")) {
                            final int saturation = Integer.valueOf(DataDepot.saturation);
                            if (saturation != ConstanUtil.lastsaturation) {
                                ConstanUtil.lastsaturation = saturation;
                                Message message = new Message();
                                message.what = UDATAREMENUCOLOR_MESSAGE;
                                message.arg1 = saturation;
                                handler.sendMessage(message);
                                if (saturation == 0) {
                                    handler.sendEmptyMessage(UPADAT_GROUTH_RATE);
                                }
                            }

                        }


                        //2016.08.03 新增 :　根据选择的物镜倍数更新　标尺的单位数值　：
                        int multiple = Integer.valueOf(DataDepot.contrast);
                        if (multiple != ConstanUtil.lastmultiple) {
                            ConstanUtil.lastmultiple = multiple;
                            Message message = new Message();
                            message.what = UDATA_MULTIPLE_MESSAGE;
                            switch (multiple) {
                                case 5:
                                    message.obj = "100 um";
                                    break;
                                case 10:
                                    message.obj = "50 um";
                                    break;
                                case 20:
                                    message.obj = "25 um";
                                    break;
                                case 50:
                                    message.obj = "1 um";
                                    break;
                            }
                            handler.sendMessage(message);
                        }
                        if (DataDepot.is_record) {
                            ConstanUtil.record = 1;
                        } else {
                            ConstanUtil.record = 2;
                        }
                        if (ConstanUtil.lastrecord != ConstanUtil.record) {
                            if (DataDepot.is_record) {
                                handler.sendEmptyMessage(UDATA_RECORD_MESSAGE);

                            } else {
                                handler.sendEmptyMessage(DISMISS_RECORD_MESSAGE);

                            }
                        }

                        int msaturation = Integer.valueOf(DataDepot.saturation);

                        if (msaturation == 0) {
                            //摄像头关闭 清除btm
                            handlerSendMessage(VIEW_MESSAGEI_UPADA,2);
                        }
                        if (DataDepot.pngsize == 0) {
                            handler.sendEmptyMessage(3);

                        }

                        if (DataDepot.autophotdelay && Integer.valueOf(DataDepot.saturation) != 0) {

                            handler.sendEmptyMessage(SHOW_AUTOPHOTO_DIALOG_MESSAGE);
                        } else if (!DataDepot.autophotdelay && !DataDepot.isexport) {

                            handlerSendMessage(VIEW_MESSAGEI_UPADA,3);
                        }

                        if (DataDepot.isexport) {

                            handler.sendEmptyMessage(SHOW_EXPORT_DIALOG_MESSAGE);

                        } else if (!DataDepot.autophotdelay && !DataDepot.isexport) {
/*
                            if (delayAutoPhoto != null) {

                            }*/
                        }
                    }
                });

            }
        };
        Logger.e("Aye", Constant.WS_PROTOCOL + Constant.WS_URI + Constant.WS_PORT_CAMERA_OPTIONS + Constant.CAMERA_CHANNEL);
        cameraInfoScoket = AsyncHttpClient.getDefaultInstance().websocket(Constant.WS_PROTOCOL + Constant.WS_URI + Constant.WS_PORT_CAMERA_OPTIONS + Constant.CAMERA_CHANNEL, null, cameraInfoCallback);
    }
    public void getPicture() {

        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                GBaseHttpParams picParams = new GBaseHttpParams(Constant.HTTP_PROTOCOL + Constant.HTTP_URI + Constant.CAMERA_GETPHOTOLIST);
                Map<String, String> getPicMap = new HashMap<>();
                getPicMap.put("page", String.valueOf("1"));
                picParams.putGETParams(getPicMap);
                mPicNameCallback = GXHttpManager.getWithJSON(activity, GXHttpManager.METHOD_GET, picParams, new CommonActionCallBackString(activity) {
                    @Override
                    public void callBackWithJSON(String json) {
                        com.alibaba.fastjson.JSONObject object = JSON.parseObject(json);
                        com.alibaba.fastjson.JSONArray array = object.getJSONArray("list");
                        List<String> strings = Arrays.asList(array.toArray(new String[array.size()]));

                        for (int i = 0; i < strings.size(); i++) {
                            String medianame = strings.get(i);
                            if (medianame.contains("scaled") || medianame.contains("mp4")) {
                                boolean isPic = medianame.contains(".bmp");
                                if (isPic) {
                                    handlerSendMessage(VIEW_MESSAGEI_UPADA,1);
                                    handlerSendMessageString(UPADAT_IAMGE_ALBUM,medianame);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {


                    }
                });
            }
        });


    }
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    /**
     * 模拟数据发送
     */
    private void startDecodeH264() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        byte[] bytes = new byte[1024 * 8];
        int readSize = 0;
        int count = 0;

        while (count < 449) {
            try {
                inputStream = getAssets().open(count + ".h264");
                while ((readSize = inputStream.read(bytes)) != -1) {
                    byteArrayOutputStream.write(bytes, 0, readSize);
                }
                byteArrayOutputStream.flush();

                mSurfaceView.onReceived(byteArrayOutputStream.toByteArray());

                byteArrayOutputStream.reset();
                inputStream.close();
                inputStream = null;
                count++;
                SystemClock.sleep(100);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (byteArrayOutputStream != null) {
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void picAsyncWetSocket() {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                BaseWebSocketCallback picAsyncCallback = new BaseWebSocketCallback(activity, picAsyncScoket) {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        super.onCompleted(ex, webSocket);
                        if (webSocket == null)
                            return;
                        webSocket.setDataCallback(new DataCallback() {
                            @Override
                            public void onDataAvailable(DataEmitter emitter, final ByteBufferList bb) {
                                synchronized (synchronos) {
                                    //handler.removeMessages(UPADAT_MESSAGE_LISTENING);
                                   // handler.sendEmptyMessageDelayed(UPADAT_MESSAGE_LISTENING, 20 * 1000);//destruction app at six seconds no update
                                   // handler.removeMessages(UPADAT_IMAGE_MESSAGE);

                                    ByteBuffer buffer = ByteBuffer.wrap(ByteZip.unGZip(bb.getAllByteArray()));
                                    Log.w("Aye", "mbuffer= " + buffer.limit());
                                    byte[] mbyte = new byte[buffer.capacity()];
                                    buffer.get(mbyte, 0, mbyte.length);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(mbyte, 0, mbyte.length);
                                    final SoftReference<Bitmap> sbitmap = new SoftReference<Bitmap>(bitmap);
                                    buffer.rewind();
                                    buffer.clear();
                                    System.gc();
                                    bb.recycle();

                                    if (sbitmap.get() != null) {
                                        Message message = new Message();
                                        message.obj = sbitmap.get();
                                        message.what = UPADAT_IMAGE_MESSAGE;
                                        handler.sendMessage(message);
                                        Log.e("mMasterActivity", "sbitmap= " + bitmap.getHeight() + ";;" + bitmap.getWidth());

                                    }
                                }

                            }
                        });

                    }
                };

                picAsyncScoket = AsyncHttpClient.getDefaultInstance().websocket(Constant.WS_PROTOCOL + Constant.WS_URI + Constant.WS_PORT_PIC + Constant.PICTURE_CHANNEL, null, picAsyncCallback);
            }
        });

    }


    private void requesObjectLight() {

        GBaseHttpParams httpParamsstr = new GBaseHttpParams(Constant.HTTP_PROTOCOL + Constant.HTTP_URI + Constant.Set_Configuration);
        Map<String, String> mapParamsstr = new HashMap<>();
        mapParamsstr.put("configuration", "请求配置参数");
        httpParamsstr.putGETParams(mapParamsstr);
        mPicNameCallback = GXHttpManager.getWithJSON(activity, GXHttpManager.METHOD_GET, httpParamsstr, new CommonActionCallBackString(activity) {
            @Override
            public void onSuccess(String msg) {
                super.onSuccess(msg);
                handlerSendMessage(VIEW_MESSAGEI_UPADA,4);
                String[] split = msg.split("&");
                Log.w(TAG,"msg="+msg);
                if (split.length > 1) {

                    handlerSendMessage(VIEW_MESSAGEI_UPADA,5);
                    ConstanUtil.LIGHTTYPE = split[1];
                    ConstanUtil.LIGHTNUMBER = split[1].split(",").length;
                    String[] objects = split[0].split(",");
                    ConstanUtil.OBJECTIVES = new int[objects.length];
                    for (int i = 0; i < objects.length; i++) {
                        int object = Integer.valueOf(objects[i]);
                        ConstanUtil.OBJECTIVES[i] = object;
                    }
                    if (split.length > 2) {
                        if (split[2] != null) {
                            DataDepot.maxbrightness = Integer.valueOf(split[2]);
                        }
                    }
                    if (split.length > 3) {
                        if (split[2] != null) {
                            DataDepot.maxiso = Integer.valueOf(split[3]);
                        }
                    }
                    if (split.length > 4) {
                        if (split[3] != null) {
                            ConstanUtil.LONZA = Boolean.valueOf(split[4]);
                        }

                    }
                    if (split.length > 5) {
                        if (split[4] != null) {
                            if (split[5].trim().equals("yes"))
                                ConstanUtil.ROCKER = true;
                            else
                                ConstanUtil.ROCKER = false;
                        }
                    }
                }
            }
        });
    }
    //通过webScoket发送摄像头参数

    //
    private void handlerSendMessage(int what,int type) {
        Message message = new Message();
        message.what  = what;
        message.arg1 = type;
        handler.sendMessage(message);
    }
    private void handlerSendMessageString(int what,String type) {
        Message message = new Message();
        message.what  = what;
        message.obj = type;
        handler.sendMessage(message);
    }
    private void handlerSednMessageOBJ(int what,Object obj){
        Message message  = new Message();
        message.what = what;
        message.obj = obj;
        handler.sendMessage(message);
    }
    public class  MyBinder extends Binder{

        public MasterService getService(){
            return MasterService.this;
        }
    }

    //在Service中暴露出去的方法，供client调用

    public void sendMessageToRockerView(String command) {
        if (ConstanUtil.remoteLogin) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("rocketview", true);
                jsonObject.put("command", command);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ResponeUtils.sendObjectInstruction(ResponeUtils.PARAM_TYPE_ROCKERVIEW_TYPE, jsonObject, userName);
        } else {
            if (cameraCMDScoket != null) {
                webScoketResult.clear();
                webScoketResult.put("type", String.valueOf(PARAM_TYPE_ROCKERVIEW));
                webScoketResult.put("param", String.valueOf(1));
                webScoketResult.put("command", String.valueOf(command));
                cameraCMDScoket.send(JSON.toJSONString(webScoketResult));

            }
        }
    }
    public void sendCameraParam(int paramType, int param) {
        if (cameraCMDScoket != null) {
            webScoketResult.clear();
            webScoketResult.put("type", String.valueOf(paramType));
            webScoketResult.put("param", String.valueOf(param));
            cameraCMDScoket.send(JSON.toJSONString(webScoketResult));
            Log.e("mytest", "sendcameraparm");
        }
    }

    //发送自动拍照
    public void sendAutoPhoto(int paramType, String lights, String isautofucs, String issynthetic
            , String startTimer, String tTimer, String endTimer) {
        if (ConstanUtil.remoteLogin) {
            JSONObject autophoto = new JSONObject();
            try {
                autophoto.put("autophoto", true);
                autophoto.put("autophotolights", lights);
                autophoto.put("autofocus", isautofucs);
                autophoto.put("synthetic", issynthetic);
                autophoto.put("autophoto_starttimer", startTimer);
                autophoto.put("autophoto_ttimer", tTimer);
                autophoto.put("autophoto_endtimer", endTimer);
                autophoto.put("convergence", ConstanUtil.ConvergenceNumber);
                autophoto.put("autophoto_stopstr", ConstanUtil.stopAutoPhotoStr);
                autophoto.put("autophoto_count", ConstanUtil.autoPhotoCount + "");
                ResponeUtils.sendObjectInstruction(ResponeUtils.PARAM_TYPE_AUTOPHOTO_TYPE, autophoto, userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            if (cameraCMDScoket != null) {
                webScoketResult.clear();
                webScoketResult.put("type", String.valueOf(paramType));
                webScoketResult.put("param", String.valueOf(101));

                webScoketResult.put("autophotolights", lights);
                webScoketResult.put("autofocus", isautofucs);
                webScoketResult.put("synthetic", issynthetic);
                webScoketResult.put("convergence", ConstanUtil.ConvergenceNumber);
                webScoketResult.put("autophoto_starttimer", startTimer);
                webScoketResult.put("autophoto_ttimer", tTimer);
                webScoketResult.put("autophoto_endtimer", endTimer);

                //webScoketResult.put("autophoto_process", ConstanUtil.autoPhotoCount);
                webScoketResult.put("autophoto_count", ConstanUtil.autoPhotoCount + "");
                webScoketResult.put("autophoto_stopstr", ConstanUtil.stopAutoPhotoStr);

                cameraCMDScoket.send(JSON.toJSONString(webScoketResult));

            }
        }

    }
    public void sendStopAutophoto(int type) {
        if (ConstanUtil.remoteLogin) {
            ResponeUtils.sendParamInstruction(ResponeUtils.PARAM_TYPE_STOPAUTOPHOTO_TYPE, 0, userName);
        } else {
            if (cameraCMDScoket != null) {
                webScoketResult.clear();
                webScoketResult.put("type", String.valueOf(type));
                webScoketResult.put("param", String.valueOf(101));
                cameraCMDScoket.send(JSON.toJSONString(webScoketResult));
            }
        }
    }
    public void setBtnPhoto() {

        handler.sendEmptyMessage(UDATAPROCESSBAR_MESSAGE);
        handler.removeMessages(3);
        handler.sendEmptyMessageDelayed(3, 7000);
        DELAY_EXECUTORSERVICE.schedule(new Runnable() {
            @Override
            public void run() {
                sendCameraParam(PARAM_TYPE_UPDAATE_PICTER, 1);
            }
        }, 1000, TimeUnit.MILLISECONDS);

    }
    public int getRandomNumber(){
        return 1;
    }
    public void  setHandler(Handler handler){
       this.handler = handler;
    }

    public void setDismissHandler(Handler dismissHandler){
        this.dismissHandler = dismissHandler;
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }
    public void setSurfaceview(MediaCodecSurfaceView surfaceview){
        this.mSurfaceView = surfaceview;
    }

    public void init(){
        workExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG,"init.......");
                if (ConstanUtil.remoteLogin) {
                    loginNumber = 3;
                    dismissHandler.removeMessages(HANDLE_MESSAGE_LISTENING);
                    dismissHandler.removeMessages(HANDLE_MESSAGE_DISMISS_PROGRESS);
                    dismissHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_DISMISS_PROGRESS, 5000);
                    isListnessConnect = true;

                    remoteLoginMethod();
                    Log.w(TAG, "userName=" + userName);

                } else {
                    if (!ConstanUtil.isSuccessLogin) {
                        Log.w(TAG, "ConstanUtil.isSuccessLogin=" + ConstanUtil.isSuccessLogin);
                        cameraInfoWebSocket();
                        picAsyncWetSocket();
                        requesObjectLight();//请求配置参数
                        mQueue = Volley.newRequestQueue(activity);
                    }
                    if (ConstanUtil.isSuccessLogin) {
                        setBtnPhoto();
                    }
                }
            }
        });

    }
    public void setUserName(String userName){
       this.userName = userName;
    }
    public void remoteLoginMethod() {

        handlerSendMessage(VIEW_MESSAGEI_UPADA,4);
        AVQuery<AVObject> cfg = new AVQuery<>("Configuration");
        cfg.whereEqualTo("user", userName);
        cfg.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> list, AVException e) {

                if (e == null && list.size() > 0) {

                    AVObject avObject = list.get(0);
                    Number maxbrightness = avObject.getNumber("maxbrightness");
                    Number maxiso = avObject.getNumber("maxiso");

                    boolean lonza = avObject.getBoolean("lonza");
                    if (lonza)
                        ConstanUtil.LONZA = true;
                    else
                        ConstanUtil.LONZA = false;


                    String rockerstate = avObject.getString("rockerstate");
                    if (rockerstate != null) {
                        if (rockerstate.equals("yes"))
                            ConstanUtil.ROCKER = true;
                        else
                            ConstanUtil.ROCKER = false;
                    }
                    DataDepot.maxbrightness = maxbrightness.intValue();
                    DataDepot.maxiso = maxiso.intValue();

                    if (DataDepot.maxbrightness == 0) {
                        DataDepot.maxbrightness = 32767;
                    }
                    String light_number = avObject.getString("light_number");
                    ConstanUtil.LIGHTTYPE = avObject.getString("light_type");
                    ConstanUtil.LIGHTNUMBER = Integer.valueOf(light_number);

                    String constans = avObject.getString("constans");
                    String objects[] = constans.split(",");
                    ConstanUtil.OBJECTIVES = new int[objects.length];
                    for (int i = 0; i < objects.length; i++) {
                        ConstanUtil.OBJECTIVES[i] = Integer.valueOf(objects[i]);
                        Log.i("SelectConnectActivity", "ConstanUtil.OBJECTIVES=" + ConstanUtil.OBJECTIVES[i]);
                    }
                    dismissHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_LISTENING, 10 * 1000);
                    updataParameter();
                    requesTimer(userName);
                    handlerSendMessage(VIEW_MESSAGEI_UPADA,5);

                } else {

                    if(updataConfiguration){
                        UIUtil.toast(activity, "请求配置失败,正在重新请求");
                        updataConfiguration = false;
                        remoteLoginMethod();
                    }else{
                        UIUtil.toast(activity, "请求配置失败,请检测网络");
                        Intent intent  = new Intent(activity,SelectConnectActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }

                }
            }
        });
    }

    public void requesTimer(final String user) {
        if (requestTimer != null) {
            requestTimer.cancel();
            requestTimer = null;
        }
        requestTask = new TimerTask() {
            @Override
            public void run() {
                AVQuery<AVObject> query = new AVQuery<>("ConnectState");
                query.whereEqualTo("user", user);
                query.getFirstInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject object, AVException e) {
                        object.put("heartbeat", true);
                        AVSaveOption option = new AVSaveOption();
                        option.query(new AVQuery<>("ConnectState").whereEqualTo("user", user));
                        option.setFetchWhenSave(true);
                        object.saveInBackground(option, new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {

                                } else {

                                }
                            }
                        });
                    }
                });
            }
        };
        requestTimer = new Timer();
        requestTimer.schedule(requestTask, 10000, 60 * 1000);//10秒后启动 每60秒查询一次
    }
    public void updataParameter() {
        if (doingLiveQuery != null) {
            doingLiveQuery.unsubscribeInBackground(new AVLiveQuerySubscribeCallback() {
                @Override
                public void done(AVException e) {

                }
            });
        }
        AVQuery<AVObject> doingQuery = new AVQuery<>("Parameter");
        doingQuery.whereEqualTo("user", userName);
        doingQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> parseObjects, AVException parseException) {
                // 符合查询条件的 Todo
                Log.w(TAG, "符合查询条件的=" + userName);
            }
        });

        doingLiveQuery = AVLiveQuery.initWithQuery(doingQuery);

        doingLiveQuery.setEventHandler(new AVLiveQueryEventHandler() {
            @Override
            public void done(AVLiveQuery.EventType eventType, AVObject avObject, List<String> updateKeyList) {
                // 事件回调，有更新后会调用此回调函数

            }
        });
        doingLiveQuery.subscribeInBackground(new AVLiveQuerySubscribeCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                    // 订阅成功
                    Log.w(TAG, "订阅成功");
                }
            }
        });
        doingLiveQuery.setEventHandler(new AVLiveQueryEventHandler() {
            @Override
            public void onObjectUpdated(AVObject avObject, List<String> updateKeyList) {
                Log.w(TAG, "updateKeyList=" + updateKeyList);
                handlerSendMessage(VIEW_MESSAGEI_UPADA,7);
                dismissHandler.removeMessages(HANDLE_MESSAGE_DISMISS_PROGRESS);
                dismissHandler.removeMessages(HANDLE_MESSAGE_LISTENING);
                dismissHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_LISTENING, 10 * 1000);//destruction app at six seconds no update
                Log.w(TAG, "avObject=" + avObject);
                readAVObject(avObject);
            }

        });

    }
    public void readAVObject(AVObject avObject) {

        DataDepot.recodigtimer = avObject.getString("recondingtimer");
        DataDepot.brightness = avObject.getString("brightness");
        DataDepot.gain = avObject.getString("gain");
        DataDepot.recolor = avObject.getString("recolor");
        DataDepot.recolorstring = avObject.getString("recolorstring");
        DataDepot.autophotoview = avObject.getString("autophotoview");
        DataDepot.contrast = avObject.getString("contrast");

        String voltagelevel = avObject.getString("voltagelevel");
        DataDepot.voltagelevel = voltagelevel;
        int voltage = Integer.parseInt(voltagelevel);
        int curentvaltage = Integer.parseInt(DataDepot.voltagelevel);
        int countvaltage = Math.abs(curentvaltage - voltage);
        if (countvaltage > 5) {
            DataDepot.voltagelevel = voltagelevel;
        }
        DataDepot.gamma = avObject.getString("gamma");
        DataDepot.vessels = avObject.getString("vessels");
        DataDepot.isAutofocus = avObject.getBoolean("isAutofocus");
        DataDepot.isexport = avObject.getBoolean("isexport");
        DataDepot.saturation = avObject.getString("saturation");
        DataDepot.is_record = avObject.getBoolean("is_record");
        DataDepot.uartisbusy = avObject.getBoolean("isswitch");
        DataDepot.padWidth = avObject.getNumber("pad_width").intValue();
        DataDepot.padHeight = avObject.getNumber("pad_height").intValue();
        DataDepot.pngsize = avObject.getNumber("pngfiles").intValue();
        DataDepot.moviessize = avObject.getNumber("moviesfiles").intValue();
        DataDepot.startdatetimer = avObject.getNumber("startdatetimer").longValue();
        DataDepot.enddatetimer = avObject.getNumber("enddatetimer").longValue();
        DataDepot.autophotdelay = avObject.getBoolean("stopautophotodelay");

        DataDepot.autophotocount = avObject.getNumber("autophotocount").intValue();
        DataDepot.autophotoprocess = avObject.getNumber("autophotoprocess").intValue();
        DataDepot.autophotostoptimer = avObject.getString("autophototimer");
        DataDepot.autophotocNumber = avObject.getString("convergence");
        //2016.08.02 : 新增  : 用于接收从平板端传输过来的当前物镜实际倍数 显示 :
        DataDepot.currentContrast = avObject.getString("current_contrast");
        DataDepot.employMemory = avObject.getString("employMemoery");
        DataDepot.totalMemory = avObject.getString("totalMemoery");
        String intevalAndPercentage = avObject.getString("percentage");
        Log.w("contrast", "intevalAndPercentage= " + intevalAndPercentage);
        if (intevalAndPercentage != null) {
            DataDepot.intevalAndPercentage = intevalAndPercentage;
            SPUtils.put(this, "percentage", DataDepot.intevalAndPercentage);
        }
        Log.w("contrast", "intevalAndPercentage1= " + intevalAndPercentage);
        String contrastkey = avObject.getString("contrastkey");
        String[] checks = contrastkey.substring(0, contrastkey.length()).split(",");
        // Log.w("contrast", "contrastkey= " + contrastkey);
        if (checks.length > 1) {
            for (int i = 0; i < checks.length; i++) {
                boolean ischeck = Boolean.parseBoolean(checks[i]);
                DataUtil.CheckNums[i] = ischeck;

            }
        }
        handler.sendEmptyMessage(UDATAUI_MESSAGE);

        if (DataDepot.is_record) {
            handler.sendEmptyMessage(UDATATIMER_VIEW_MESSAGE);

        } else if (!DataDepot.is_record) {
            handler.sendEmptyMessage(DISMISSTIMER_VIEW_MESSAGE);
        }

        if (DataDepot.isAutofocus) {
            handler.sendEmptyMessage(UDATAAUTOFOCUS_VIEW_MESSAGE);

        } else if (!DataDepot.isAutofocus && !DataDepot.autophotoview.trim().equals("visible")) {
            handler.sendEmptyMessage(DISMISSAUTOFOCUS_VIEW_MESSAGE);
        }

        ConstanUtil.lastautofocuview = DataDepot.autophotoview.trim();
        if (DataDepot.autophotoview.trim().equals("visible")) {

            handler.sendEmptyMessage(UDATAAUTOFOCUS_VISSBLE_MESSAGE);

            DataDepot.autophoto_view = 3;
            ConstanUtil.isAutophoto = false;
            ConstanUtil.isAutophotoGoing = true;

        } else if (DataDepot.autophotoview.equals("gone")) {
            handler.sendEmptyMessage(DISMISSAUTOFOCUS_GONE_MESSAGE);

            DataDepot.autophotoprocess = 0;
            DataDepot.autophotoprocess = 0;
            if (!DataDepot.autophotdelay) {
                ConstanUtil.isAutophotoGoing = false;
            }
            DataDepot.autophoto_view = 1;

        } else if (DataDepot.autophotoview.equals("prepare")) {
            handler.sendEmptyMessage(UDATAAUTOFOCUS_PER_MESSAGE);

            DataDepot.autophoto_view = 2;
        }
        if (!DataDepot.saturation.equals("")) {
            final int saturation = Integer.valueOf(DataDepot.saturation);
            Log.w(TAG, "saturation=" + saturation);
            if (saturation != ConstanUtil.lastsaturation) {
                ConstanUtil.lastsaturation = saturation;
                if (saturation == 0) {
                    handler.sendEmptyMessage(UPADAT_GROUTH_RATE);
                }
                Message message = new Message();
                message.what = UDATAREMENUCOLOR_MESSAGE;
                message.arg1 = saturation;
                handler.sendMessage(message);
            }
        }

        //2016.08.03 新增 :　根据选择的物镜倍数更新　标尺的单位数值　：
        int multiple = Integer.valueOf(DataDepot.contrast);
        if (multiple != ConstanUtil.lastmultiple) {
            ConstanUtil.lastmultiple = multiple;
            Message message = new Message();
            message.what = UDATA_MULTIPLE_MESSAGE;
            switch (multiple) {
                case 5:
                    message.obj = "100 um";
                    break;
                case 10:
                    message.obj = "50 um";
                    break;
                case 20:
                    message.obj = "25 um";
                    break;
                case 50:
                    message.obj = "1 um";
                    break;
            }
            handler.sendMessage(message);
        }
        if (DataDepot.is_record) {
            ConstanUtil.record = 1;
        } else {
            ConstanUtil.record = 2;
        }
        if (ConstanUtil.lastrecord != ConstanUtil.record) {
            if (DataDepot.is_record) {
                handler.sendEmptyMessage(UDATA_RECORD_MESSAGE);

            } else {
                handler.sendEmptyMessage(DISMISS_RECORD_MESSAGE);

            }
        }

        int msaturation = Integer.valueOf(DataDepot.saturation);

        if (msaturation == 0) {
            handlerSendMessage(VIEW_MESSAGEI_UPADA,6);


        }

        if (DataDepot.autophotdelay && Integer.valueOf(DataDepot.saturation) != 0) {

            handler.sendEmptyMessage(SHOW_AUTOPHOTO_DIALOG_MESSAGE);
        } else if (!DataDepot.autophotdelay && !DataDepot.isexport) {
            handlerSendMessage(VIEW_MESSAGEI_UPADA,8);

        }

        if (DataDepot.isexport) {

            handler.sendEmptyMessage(SHOW_EXPORT_DIALOG_MESSAGE);

        } else if (!DataDepot.autophotdelay && !DataDepot.isexport) {

          /*  if (delayAutoPhoto != null) {

            }*/
        }
        Log.w(TAG, "image0=");
        byte[] images = avObject.getBytes("image");

        Log.w(TAG, "image=" + images);

        if (images != null) {
            final byte[] finalData = images;

            handlerSednMessageOBJ(VIEW_MESSAGEII_UPADA,finalData);

        }
    }
    public void sendExportParam(boolean delete) {
        int param = 0;

        if (delete) {
            param = 1;
        }
        if (ConstanUtil.remoteLogin) {
            JSONObject export = new JSONObject();
            try {
                export.put("param", param);
                ResponeUtils.sendObjectInstruction(ResponeUtils.PARAM_TYPE_EXPORT_TYPE, export, userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            if (cameraCMDScoket != null) {
                webScoketResult.clear();
                webScoketResult.put("type", String.valueOf(PARAM_TYPE_EXPORT));
                webScoketResult.put("param", String.valueOf(param));
                cameraCMDScoket.send(JSON.toJSONString(webScoketResult));
            }
        }
    }
    public void sendState(int position, boolean b) {
        if (ConstanUtil.remoteLogin) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("postion", position);
                jsonObject.put("result", b);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ResponeUtils.sendObjectInstruction(ResponeUtils.PARAM_TYPE_CONSTAST_TYPE, jsonObject, userName);
        } else {
            if (cameraCMDScoket != null) {
                webScoketResult.clear();
                webScoketResult.put("type", String.valueOf(PARAM_TYPE_CONSTAST));
                webScoketResult.put("param", String.valueOf(position));
                webScoketResult.put("result", String.valueOf(b));
                cameraCMDScoket.send(JSON.toJSONString(webScoketResult));

            }
        }
    }
    public void sendAutoFocus(int mcameraX, int mcameraY, int isoParam) {
        if (ConstanUtil.remoteLogin) {
            JSONObject js = new JSONObject();
            try {
                js.put("autofocus", true);
                js.put("x", mcameraX);
                js.put("y", mcameraY);
            } catch (JSONException e3) {
                e3.printStackTrace();
            }
            ResponeUtils.sendObjectInstruction(ResponeUtils.PARAM_TYPE_MANUAL_FOCUS_TYPE, js, userName);

        } else {
            if (cameraCMDScoket != null) {
                webScoketResult.clear();
                webScoketResult.put("type", String.valueOf(PARAM_TYPE_MANUAL_FOCUS));
                webScoketResult.put("param", String.valueOf(isoParam));
                //   Logger.e("Aye", String.valueOf(padX) + " " + String.valueOf(padY));
                webScoketResult.put("x", String.valueOf(mcameraX));
                webScoketResult.put("y", String.valueOf(mcameraY));
                //  Log.e("autofucus", "x=" + mcameraX);
                //  Log.e("autofucus", "y=" + mcameraY);
                cameraCMDScoket.send(JSON.toJSONString(webScoketResult));
            }
        }
    }
    public void senStopAutoFocus() {
        if (ConstanUtil.remoteLogin) {
            ResponeUtils.sendParamInstruction(ResponeUtils.PARAM_TYPE_MANUAL_FOCUS_STOP_TYPE, -1, userName);
        } else {
            if (cameraCMDScoket != null) {
                webScoketResult.clear();
                webScoketResult.put("type", String.valueOf(PARAM_TYPE_MANUAL_FOCUS_STOP));
                webScoketResult.put("param", String.valueOf(101));      //发送一个无意义的数值101 ;
                cameraCMDScoket.send(JSON.toJSONString(webScoketResult));
            }
        }
    }

}

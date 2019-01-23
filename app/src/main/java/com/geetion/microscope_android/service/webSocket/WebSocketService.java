package com.geetion.microscope_android.service.webSocket;

import com.geetion.log.Logger;
import com.geetion.microscope_android.service.Constant;
import com.geetion.microscope_android.service.DataDepot;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WongzYe on 15/12/18.
 */
//public class WebSocketService {
//
//    private static WebSocketService webSocketService;
//    private String picture = "/picture";
//    private String cameraOptions = "/camera/options";
//    private String padStatus = "/pad/status";
//    private String deviceStatus = "/device/operation/status";
//
//    private AsyncHttpClient.WebSocketConnectCallback getPicCallback;
//    private AsyncHttpClient.WebSocketConnectCallback cameraOptionsCallback;
//    private AsyncHttpClient.WebSocketConnectCallback padStatusCallback;
//    private AsyncHttpClient.WebSocketConnectCallback deviceStatusCallback;
//
//    private Future<WebSocket> getPicScoket;
//    private Future<WebSocket> cameraOptionsScoket;
//    private Future<WebSocket> padStatusScoket;
//    private Future<WebSocket> deviceStatusScoket;
//
//    private WebSocket cameraWebScoket;
//
//    public static WebSocketService getInstance() {
//        if (webSocketService == null) {
//            webSocketService = new WebSocketService();
//        }
//        return webSocketService;
//    }
//
//    private WebSocketService() {
//        init();
//    }
//
//    public void init() {
//
//        //屏幕同步回调接口
////        getPicCallback = new BaseWebSocketCallback(picture, getPicScoket) {
////            @Override
////            public void onCompleted(Exception ex, WebSocket webSocket) {
////                super.onCompleted(ex, webSocket);
////                if (webSocket == null) return;
////                webSocket.setDataCallback(new DataCallback() {
////                    @Override
////                    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
//////                        Logger.e(picture, "ws" + picture + "onDataAvailable");
////                        if (webSocketServiceListener != null)
////                            webSocketServiceListener.onPictureOk(emitter, bb);
////                    }
////                });
////            }
////        };
//
//        //相机状态回调接口
////        cameraOptionsCallback = new BaseWebSocketCallback(cameraOptions, cameraOptionsScoket) {
////            @Override
////            public void onCompleted(Exception ex, WebSocket webSocket) {
////                super.onCompleted(ex, webSocket);
////                if (webSocket == null) return;
////                if (cameraWebScoket == null)
////                    cameraWebScoket = webSocket;
////
////                webSocket.setStringCallback(new WebSocket.StringCallback() {
////                    @Override
////                    public void onStringAvailable(String s) {
////                        Logger.e(cameraOptions, "ws" + s);
////
////                        try {
////                            JSONObject object = new JSONObject(s);
////                            DataDepot.brightness = object.optString("brightness");
////                            DataDepot.gain = object.optString("gain");
////                            DataDepot.recolor = object.optString("recolor");
////                            DataDepot.contrast = object.optString("contrast");
////                            DataDepot.saturation = object.optString("saturation");
////
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////                        if (webSocketServiceListener != null)
////                            webSocketServiceListener.onCameraOptionsOk(s);
////                    }
////                });
////            }
////
////
////        };
//
//        //平板设备状态回调接口
////        padStatusCallback = new BaseWebSocketCallback(padStatus, padStatusScoket) {
////            @Override
////            public void onCompleted(Exception ex, WebSocket webSocket) {
////                super.onCompleted(ex, webSocket);
////                if (webSocket == null) return;
////                webSocket.setStringCallback(new WebSocket.StringCallback() {
////                    @Override
////                    public void onStringAvailable(String s) {
//////                        Logger.e(padStatus, "ws" + s);
////
////                        try {
////                            JSONObject object = new JSONObject(s);
////                            DataDepot.battery = object.getString("battery");
////                            DataDepot.cameraConnect = object.getString("cameraConnect");
////                            DataDepot.connectCount = object.getString("connectCount");
////                            DataDepot.deviceIP = object.getString("deviceIP");
////                            DataDepot.deviceMac = object.getString("deviceMac");
////                            DataDepot.memoery = object.getString("memoery");
////                            DataDepot.wifi = object.getString("wifi");
////
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////
////                        if (webSocketServiceListener != null)
////                            webSocketServiceListener.onPadStatusOk(s);
////                    }
////                });
////            }
////        };
//
//        //屏蔽状态回调接口
////        deviceStatusCallback = new BaseWebSocketCallback(deviceStatus, deviceStatusScoket) {
////            @Override
////            public void onCompleted(Exception ex, WebSocket webSocket) {
////                super.onCompleted(ex, webSocket);
////                if (webSocket == null) return;
////                webSocket.setStringCallback(new WebSocket.StringCallback() {
////                    @Override
////                    public void onStringAvailable(String s) {
//////                        Logger.e(deviceStatus, s);
////
////                        try {
////                            JSONObject object = new JSONObject(s);
////                            DataDepot.enable = object.getBoolean("enable");
//////                            Logger.e(deviceStatus, String.valueOf(DataDepot.enable));
////
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////
////                        if (webSocketServiceListener != null)
////                            webSocketServiceListener.onDeviceStatusOk(s);
////                    }
////                });
////            }
////        };
//    }
//
//    //连接屏幕同步接口
//    public void connectPicWetSocket() {
////        if (getPicCallback == null) {
//            BaseWebSocketCallback getPicCallback1 = new BaseWebSocketCallback(picture, getPicScoket) {
//                @Override
//                public void onCompleted(Exception ex, WebSocket webSocket) {
////                    super.onCompleted(ex, webSocket);
//                    if (webSocket == null) return;
//                    if (ex != null) {
//                        System.out.println("getPicture got an error");
//                        ex.printStackTrace();
//                        getPicScoket = null;
//                        connectPicWetSocket();
//                        return;
//                    }
//                    webSocket.setDataCallback(new DataCallback() {
//                        @Override
//                        public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
//                            Logger.e(picture, "ws" + picture + "onDataAvailable");
////                            if (webSocketServiceListener != null)
////                                webSocketServiceListener.onPictureOk(emitter, bb);
//                        }
//                    });
//
//                    webSocket.setClosedCallback(new CompletedCallback() {
//                        @Override
//                        public void onCompleted(Exception ex) {
//                            Logger.e("Aye", picture + "I got an error ");
//                            if (ex != null) {
//                                ex.printStackTrace();
//                                getPicScoket = null;
//                                connectPicWetSocket();
//                                return;
//                            }
//                        }
//                    });
//                }
//            };
//
////        }
//
//        getPicScoket = AsyncHttpClient.getDefaultInstance().websocket(Constant.WS_PROTOCOL + Constant.WS_URI + Constant.WS_PORT_PIC + Constant.PICTURE_CHANNEL, null, getPicCallback1);
//    }
//
//    //连接获取相机状态接口
//    public void connectCameraSocket() {
//        if (cameraOptionsCallback == null) {
//            cameraOptionsCallback = new BaseWebSocketCallback(cameraOptions, cameraOptionsScoket) {
//                @Override
//                public void onCompleted(Exception ex, WebSocket webSocket) {
//                    super.onCompleted(ex, webSocket);
//                    if (webSocket == null) return;
//                    if (cameraWebScoket == null)
//                        cameraWebScoket = webSocket;
//
//                    webSocket.setStringCallback(new WebSocket.StringCallback() {
//                        @Override
//                        public void onStringAvailable(String s) {
//                            Logger.e(cameraOptions, "ws" + s);
//
//                            try {
//                                JSONObject object = new JSONObject(s);
//                                DataDepot.brightness = object.optString("brightness");
//                                DataDepot.gain = object.optString("gain");
//                                DataDepot.recolor = object.optString("recolor");
//                                DataDepot.contrast = object.optString("contrast");
//                                DataDepot.saturation = object.optString("saturation");
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            if (webSocketServiceListener != null)
//                                webSocketServiceListener.onCameraOptionsOk(s);
//                        }
//                    });
//                }
//
//
//            };
//            cameraOptionsScoket = AsyncHttpClient.getDefaultInstance().websocket(Constant.WS_PROTOCOL + Constant.WS_URI + Constant.WS_PORT_CAMERA_OPTIONS + Constant.CAMERA_CHANNEL, null, cameraOptionsCallback);
//        }
//    }
//
//    //连接获取平板设备信息接口
//    public void connectPadStatusScoket() {
//        if (padStatusScoket == null) {
//            padStatusCallback = new BaseWebSocketCallback(padStatus, padStatusScoket) {
//                @Override
//                public void onCompleted(Exception ex, WebSocket webSocket) {
//                    super.onCompleted(ex, webSocket);
//                    if (webSocket == null) return;
//                    webSocket.setStringCallback(new WebSocket.StringCallback() {
//                        @Override
//                        public void onStringAvailable(String s) {
////                        Logger.e(padStatus, "ws" + s);
//
//                            try {
//                                JSONObject object = new JSONObject(s);
//                                DataDepot.battery = object.getString("battery");
//                                DataDepot.cameraConnect = object.getString("cameraConnect");
//                                DataDepot.connectCount = object.getString("connectCount");
//                                DataDepot.deviceIP = object.getString("deviceIP");
//                                DataDepot.deviceMac = object.getString("deviceMac");
//                                DataDepot.memoery = object.getString("memoery");
//                                DataDepot.wifi = object.getString("wifi");
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            if (webSocketServiceListener != null)
//                                webSocketServiceListener.onPadStatusOk(s);
//                        }
//                    });
//                }
//            };
//        }
//        padStatusScoket = AsyncHttpClient.getDefaultInstance().websocket(Constant.WS_PROTOCOL + Constant.WS_URI + Constant.WS_PORT_PAD_STATUS + Constant.PAD_STATUS_CHANNEL, null, padStatusCallback);
//    }
//
//    //连接获取平板控制状态接口
//    public void connectDeviceStatusScoket() {
//        if (deviceStatusScoket == null) {
//            deviceStatusCallback = new BaseWebSocketCallback(deviceStatus, deviceStatusScoket) {
//                @Override
//                public void onCompleted(Exception ex, WebSocket webSocket) {
//                    super.onCompleted(ex, webSocket);
//                    if (webSocket == null) return;
//                    webSocket.setStringCallback(new WebSocket.StringCallback() {
//                        @Override
//                        public void onStringAvailable(String s) {
////                        Logger.e(deviceStatus, s);
//
//                            try {
//                                JSONObject object = new JSONObject(s);
//                                DataDepot.enable = object.getBoolean("enable");
////                            Logger.e(deviceStatus, String.valueOf(DataDepot.enable));
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            if (webSocketServiceListener != null)
//                                webSocketServiceListener.onDeviceStatusOk(s);
//                        }
//                    });
//                }
//            };
//        }
//        deviceStatusScoket = AsyncHttpClient.getDefaultInstance().websocket(Constant.WS_PROTOCOL + Constant.WS_URI + Constant.WS_PORT_DEVICE_STATUS + Constant.DEVICES_STATUS_CHANNEL, null, deviceStatusCallback);
//    }
//
//    //断开所有WebScoket连接
//    public void disconnectAllScoket() {
//        if (getPicScoket != null) {
//            if (getPicScoket.tryGet() != null)
//                getPicScoket.tryGet().close();
//            getPicScoket = null;
//        }
//
//        if (cameraOptionsScoket != null) {
//            if (cameraOptionsScoket.tryGet() != null)
//                cameraOptionsScoket.tryGet().close();
//            cameraOptionsScoket = null;
//        }
//
//        if (padStatusScoket != null) {
//            if (padStatusScoket.tryGet() != null)
//                padStatusScoket.tryGet().close();
//            padStatusScoket = null;
//        }
//
//        if (deviceStatusScoket != null) {
//            if (deviceStatusScoket.tryGet() != null)
//                deviceStatusScoket.tryGet().close();
//            deviceStatusScoket = null;
//        }
//    }
//
//    public WebSocket getCameraWebScoket() {
//        return cameraWebScoket;
//    }
//
//    private WebSocketServiceListener webSocketServiceListener;
//
//    public void setWebSocketServiceListener(WebSocketServiceListener listener) {
//        webSocketServiceListener = listener;
//    }
//
//    public void removeListener() {
//        webSocketServiceListener = null;
//    }
//
//    public interface WebSocketServiceListener {
//
//        void onPictureOk(DataEmitter emitter, ByteBufferList bb);
//
//        void onCameraOptionsOk(String s);
//
//        void onPadStatusOk(String s);
//
//        void onDeviceStatusOk(String s);
//
//    }
//}

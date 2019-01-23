package com.geetion.microscope_android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.avos.avoscloud.AVException;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * Created by Administrator on 2017/12/26.
 */

public class LeanCloudRequesUtil {
    private static  ExecutorService executorService = Executors.newFixedThreadPool(5);
    RequestListenerCallback requestListener;

    public  interface RequestListenerCallback{
         void sListenerCallback(AVObject object);
          void pictureCallback(Bitmap bitmap);
    }
    public static void requesObject(final String username, final RequestListenerCallback requestListener, final String classString){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AVQuery<AVObject> query = new AVQuery<>(classString);
                query.whereEqualTo("user", username);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(final List<AVObject> list, AVException e) {
                        if (e == null && list.size() > 0) {
                            requestListener.sListenerCallback(list.get(0));
                        }else {
                            requestListener.sListenerCallback(null);
                        }
                    }
                });
            }
        });

    }
    public static void  requestImage(final String username, final RequestListenerCallback requestListener, final String classString){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AVQuery<AVObject> query = new AVQuery<>(classString);
                query.whereGreaterThanOrEqualTo("user", username);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(final List<AVObject> list, AVException e) {
                        if (e == null && list.size() > 0) {
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    AVObject avObject = list.get(0);
                                    byte[] pictures =null;
                                    try {
                                        AVFile avFile = avObject.getAVFile(ConstanUtil.PARAM_TYPE_PIC_LOAD);
                                        if(avFile!=null) {
                                            pictures = avFile.getData();
                                            requestListener.pictureCallback(BitmapFactory.decodeByteArray(pictures, 0, pictures.length));
                                        }
                                    } catch (AVException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });


                        }else {
                            requestListener.pictureCallback(null);
                        }
                    }
                });
            }
        });

    }

    /**
     * @param usernama
     */
    public static void openService(final String usernama) {
        AVQuery<AVObject> query = new AVQuery<>("ConnectState");
        query.whereEqualTo("user",usernama);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                object.put("phonestate", true);
                AVSaveOption option = new AVSaveOption();
                option.query(new AVQuery<>("ConnectState").whereEqualTo("user", usernama));
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
}

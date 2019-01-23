package com.geetion.microscope_android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.geetion.coreOneUtil.UIUtil;
import com.geetion.coreTwoUtil.GAndroidUtil;
import com.geetion.coreTwoUtil.GScreenUtils;
import com.geetion.fresco.tool.FrescoTool;
import com.geetion.log.Logger;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.application.BaseApplication;
import com.geetion.microscope_android.custom.ImageControl;
import com.geetion.microscope_android.service.Constant;
import com.geetion.microscope_android.utils.AsyncImageLoader;
import com.geetion.microscope_android.utils.ConstanUtil;
import com.geetion.microscope_android.utils.FileUtil;
import com.geetion.xUtil.ResponeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ImageActivity extends BaseActivity implements View.OnClickListener{

    private ImageControl imageView;
    private TextView titleView;
    private SimpleDraweeView simpleDraweeView;
    private ImageRequest mImageRequest;
    private RequestQueue mQueue;
    private RelativeLayout mProgressBar;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private AsyncImageLoader imageLoader;
    private long get_before_timer;
    private String userName;
    private TextView mRemove;
    private String mrequestName;
    private List<String> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activity = this;
        setContentView(R.layout.activity_image);
        super.onCreate(savedInstanceState);
        titleView = (TextView) findViewById(R.id.title);
        mRemove = ((TextView) findViewById(R.id.remove));
        mProgressBar = (RelativeLayout) findViewById(R.id.mprogressbar);
        imageView = (ImageControl) findViewById(R.id.image_view);
        get_before_timer =0;
        mRemove.setOnClickListener(this);
        mRemove.setVisibility(View.GONE);
        if(ConstanUtil.remoteLogin){
            mRemove.setVisibility(View.VISIBLE);
            imageLoader = new AsyncImageLoader(this);
            mProgressBar.setVisibility(View.VISIBLE);
            final String filename = getIntent().getStringExtra("name");
            userName = getIntent().getStringExtra("username");
            final String requestName = filename.replace("/scale/","/request/").replace(".bmp",".jpg");
            String finnamestr = requestName.substring(requestName.lastIndexOf("/"));
            titleView.setText(finnamestr.substring(8,finnamestr.length()));
            mrequestName = requestName;
            Bitmap bitmapFromMem = imageLoader.getBitmap(requestName);
            if(bitmapFromMem!=null){
                mProgressBar.setVisibility(View.GONE);
                imageView.setImageBitmap(bitmapFromMem);
                return;
            }
            AVQuery<AVObject> query1 = new AVQuery<>(ConstanUtil.CLIENTCLASS);
            query1.whereEqualTo("user", ConstanUtil.loginUser);
            query1.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject account, AVException e) {
                    if(e==null) {
                        account.put(ResponeUtils.PARAM_TYPE_LOAD_PIC_NAME, requestName);
                        AVSaveOption option = new AVSaveOption();
                        option.query(new AVQuery<>(ConstanUtil.CLIENTCLASS).whereEqualTo("user", ConstanUtil.loginUser));
                        option.setFetchWhenSave(true);
                        account.saveInBackground();
                        get_before_timer = System.currentTimeMillis();
                        executorService.schedule(new Runnable() {
                            @Override
                            public void run() {
                                AVQuery<AVObject> query = new AVQuery<>(ConstanUtil.PRIVIEW_PHOTO_CLASS);
                                query.whereEqualTo("user", ConstanUtil.loginUser);
                                query.findInBackground(new FindCallback<AVObject>() {
                                    @Override
                                    public void done(List<AVObject> list, AVException e) {
                                        if(e==null){
                                            AVObject avObject = list.get(0);
                                            long time = avObject.getUpdatedAt().getTime();
                                            String string = avObject.getString(ResponeUtils.PARAM_TYPE_PIC_NAME_LOAD);
                                            if(!string.equals(requestName)){
                                                mProgressBar.setVisibility(View.GONE);
                                                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.requesagain);
                                                imageView.setImageBitmap(bitmap1);
                                                UIUtil.toast(ImageActivity.this,"请求超时,请重新刷新");
                                                return;
                                            }
                                            byte[] bytes = avObject.getBytes(ResponeUtils.PARAM_TYPE_PIC_LOAD);
                                            if(bytes!=null){
                                                mProgressBar.setVisibility(View.GONE);
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                imageLoader.savaBitmap(requestName,bitmap);
                                                imageView.setImageBitmap(bitmap);
                                            }
                                        }else{
                                            mProgressBar.setVisibility(View.GONE);
                                            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.requesagain);
                                            imageView.setImageBitmap(bitmap1);
                                            UIUtil.toast(ImageActivity.this,"请求错误,请重新刷新");
                                        }

                                    }
                                });
                            }
                        },4000, TimeUnit.MILLISECONDS);

                    }
                }
            });
            //String finnamestr = filename.substring(filename.lastIndexOf("/"));
            //titleView.setText(finnamestr.substring(8,finnamestr.length()));


        }else {
            mQueue = Volley.newRequestQueue(activity);
            String filename = getIntent().getStringExtra("name");

            filename = filename.substring(0, filename.lastIndexOf("scaled") - 1) + ".scaled.jpg";




            titleView.setText(FileUtil.replaceFileName(filename.substring(0, filename.indexOf("."))));

            requestThumbnailPicter(filename);
        }


    }
    private void requestThumbnailPicter(String mFirstPicName) {
        mProgressBar.setVisibility(View.VISIBLE);
        mImageRequest = new ImageRequest(Constant.HTTP_PROTOCOL + Constant.HTTP_URI + Constant.CAMERA_GETPHOTO + "?picname=" + mFirstPicName,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mProgressBar.setVisibility(View.GONE);
                        imageView.setImageBitmap(bitmap);
                       // Toast.makeText(activity, "图片请求完成", Toast.LENGTH_SHORT).show();
                    }
                }, 0, 0, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(activity, "图片请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
        mImageRequest.setTag("btn_photo_album");
        mQueue.add(mImageRequest);
    }

    @Override
    protected void onPause() {
        if(ConstanUtil.remoteLogin){

        }else {
            if (mQueue != null) {
                mQueue.stop();
                mQueue.getCache().clear();
                mQueue.cancelAll("btn_photo_album");
                mQueue = null;
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        imageView.setImageBitmap(null);
//        bitmap.recycle();
        System.gc();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.remove:
                if (imageLoader != null) {
                    list.clear();
                    list.add(mrequestName);
                    imageLoader.removeAll(list);
                    UIUtil.toast(this, "已清除缓存");
                    Intent intent = new Intent(this,RemotePhotoAlbumActivity.class);
                    intent.putExtra("username",userName);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }
}

package com.geetion.microscope_android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.geetion.coreOneUtil.UIUtil;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.adapter.PageViewAdapter;
import com.geetion.microscope_android.utils.AsyncImageLoader;
import com.geetion.microscope_android.utils.ConstanUtil;
import com.geetion.microscope_android.utils.DateTimePickDialogUtil;
import com.geetion.microscope_android.utils.LeanCloudRequesUtil;
import com.geetion.microscope_android.widget.TouchImageView;
import com.geetion.xUtil.ResponeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import io.netty.channel.MessageSizeEstimator;

/**
 * Created by Administrator on 2017/12/26.
 */

public class RemotePhotoAlbumActivity extends BaseActivity implements View.OnClickListener {
    ViewPager pager = null;
    PagerTabStrip tabStrip = null;
    List<TouchImageView> viewContainter = new ArrayList<>();
    List<String> titleContainer = new ArrayList<String>();
    List<String> listTitles = new ArrayList<String>();
    public String TAG = "RemotePhotoAlbumActivity";
    private FrameLayout mProgressbar;
    private PageViewAdapter adapter;
    private TextView mReture;
    private TextView mtimer;
    private String initFinishTime;
    private ImageView mImage;
    private AsyncImageLoader imageLoader;
    private String userName;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    public static final int UPDATEIMAG = 0;
    public static final int UPDAIMAGE = 1;
    public static final int CLEANIMG = 2;
    public static final int NULLIMAGE = 3;
    Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATEIMAG:

                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (bitmap != null)
                        mImage.setImageBitmap(bitmap);
                    break;
                case UPDAIMAGE:
                    int position = msg.arg1;
                    updateImage(position);
                    break;
                case CLEANIMG:
                    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.requesagain);
                    mImage.setImageBitmap(bitmap1);
                    break;
                case NULLIMAGE:
                    Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.null_image);
                    mImage.setImageBitmap(bitmap2);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private long get_before_timer = 0;
    private TextView removeLRC;

    private void updateImage(final int position) {

        mProgressbar.setVisibility(View.VISIBLE);
        Log.w("Adapter", "updateImage=" + position);
        final Bitmap bitmapFromMem = imageLoader.getBitmap(titleContainer.get(position));
        if (bitmapFromMem != null) {
            Log.w("Adapter", "(bitmapFromMem!=null");
            mProgressbar.setVisibility(View.GONE);
            Message message = new Message();
            message.obj = bitmapFromMem;
            myHandler.sendMessage(message);
            return;
        }
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                AVQuery<AVObject> query1 = new AVQuery<>(ConstanUtil.CLIENTCLASS);
                query1.whereEqualTo("user", userName);
                query1.getFirstInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(final AVObject account, AVException e) {
                        if (e == null) {
                            account.put(ResponeUtils.PARAM_TYPE_LOAD_SCAL_PIC_NAME, titleContainer.get(position));
                            AVSaveOption option = new AVSaveOption();
                            option.query(new AVQuery<>(ConstanUtil.CLIENTCLASS).whereEqualTo("user", userName));
                            option.setFetchWhenSave(true);
                            account.saveInBackground();
                            get_before_timer = System.currentTimeMillis();
                            executorService.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    AVQuery<AVObject> query = new AVQuery<>(ConstanUtil.PRIVIEW_PHOTO_CLASS);
                                    query.whereEqualTo("user", userName);
                                    query.findInBackground(new FindCallback<AVObject>() {
                                        @Override
                                        public void done(List<AVObject> list, AVException e) {
                                            if (e == null) {
                                                AVObject avObject = list.get(0);
                                                long time = avObject.getUpdatedAt().getTime();
                                                String string = avObject.getString(ResponeUtils.PARAM_TYPE_SCAL_PIC_NAME_LOAD);
                                                if (!string.equals(titleContainer.get(position))) {
                                                    mProgressbar.setVisibility(View.GONE);
                                                    myHandler.sendEmptyMessage(CLEANIMG);
                                                    UIUtil.toast(RemotePhotoAlbumActivity.this, "请求超时,请重新刷新");
                                                    return;
                                                }
                                                byte[] bytes = avObject.getBytes(ResponeUtils.PARAM_TYPE_SCAL_PIC_LOAD);
                                                if (bytes != null) {

                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                    imageLoader.savaBitmap(titleContainer.get(position), bitmap);
                                                    Message message = new Message();
                                                    message.obj = bitmap;
                                                    myHandler.sendMessage(message);
                                                } else {

                                                }
                                            } else {
                                                Log.w("Adapter", "error");
                                            }
                                            mProgressbar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }, 4000, TimeUnit.MILLISECONDS);
                        } else {
                            UIUtil.toast(RemotePhotoAlbumActivity.this, "请求出错,请重新刷新");
                        }
                    }
                });
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remotephoto_activity);
        pager = (ViewPager) findViewById(R.id.viewpager);
        removeLRC = ((TextView) findViewById(R.id.remove));
        removeLRC.setOnClickListener(this);
        mReture = ((TextView) findViewById(R.id.mbtn_return));
        mReture.setOnClickListener(this);
        mtimer = ((TextView) findViewById(R.id.priview_timer));
        mtimer.setOnClickListener(this);
        mtimer.setText("");
        mImage = ((ImageView) findViewById(R.id.imageview));
        mProgressbar = ((FrameLayout) findViewById(R.id.m_progressbar));
        myHandler.sendEmptyMessage(NULLIMAGE);
        imageLoader = new AsyncImageLoader(this);
        userName = getIntent().getStringExtra("username");
      /*  tabStrip = (PagerTabStrip) this.findViewById(R.id.tabstrip);
        //取消tab下面的长横线
        tabStrip.setDrawFullUnderline(false);
        //设置tab的背景色
        tabStrip.setBackgroundColor(this.getResources().getColor(R.color.bg));
        //设置当前tab页签的下划线颜色
        tabStrip.setTabIndicatorColor(this.getResources().getColor(R.color.red));
        tabStrip.setTextSpacing(200);*/
        for (int i = 0; i < 4; i++) {
            TouchImageView view = new TouchImageView(RemotePhotoAlbumActivity.this);
            viewContainter.add(view);
        }
        setRequest();
    }

    private int requestNum = 3;

    private void setRequest() {

        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                LeanCloudRequesUtil.requesObject(userName, new LeanCloudRequesUtil.RequestListenerCallback() {
                    @Override
                    public void sListenerCallback(AVObject object) {
                        if (object != null) {

                            JSONObject jsonObject = object.getJSONObject(ResponeUtils.PARAM_TYPE_UpDATE_PICTER);
                            try {
                                String filename = jsonObject.getString("filename");
                                if (filename.equals("null") && requestNum > 0) {
                                    Log.w("Adapter", "filename=1" + filename + "  " + requestNum);
                                    requestNum--;
                                    setRequest();
                                    return;
                                }
                                Log.w("Adapter", "filename=" + filename);
                                String names[] = filename.split("&");
                                for (int i = 0; i < names.length; i++) {
                                    titleContainer.add(names[i]);
                                    Log.w("Adapter", "names[i]=" + names[i]);
                                    // String substring = names[i].substring(names[i].lastIndexOf("/"));
                                    // listTitles.add(substring.substring(8,substring.length()));
                                }
                                String substring = titleContainer.get(0).substring(titleContainer.get(0).lastIndexOf("/"));
                                mtimer.setText(substring.substring(8, substring.length()));
                                pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                    }

                                    @Override
                                    public void onPageSelected(final int position) {

                                        pager.setCurrentItem(position);
                                        String substring = titleContainer.get(position).substring(titleContainer.get(position).lastIndexOf("/"));
                                        mtimer.setText(substring.substring(8, substring.length()));
                                        myHandler.sendEmptyMessage(NULLIMAGE);
                                        myHandler.removeMessages(UPDAIMAGE);
                                        Message message = new Message();
                                        message.what = UPDAIMAGE;
                                        message.arg1 = position;
                                        //停留一秒才加载图片
                                        myHandler.sendMessageDelayed(message, 1000);

                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {

                                    }
                                });
                                Bitmap bitmapFromMem = imageLoader.getBitmap(titleContainer.get(0));
                                if (bitmapFromMem != null) {
                                    mProgressbar.setVisibility(View.GONE);
                                    Message message = new Message();
                                    message.obj = bitmapFromMem;
                                    myHandler.sendMessage(message);

                                } else {

                                    AVQuery<AVObject> query1 = new AVQuery<>(ConstanUtil.CLIENTCLASS);
                                    query1.whereEqualTo("user", userName);
                                    query1.getFirstInBackground(new GetCallback<AVObject>() {
                                        @Override
                                        public void done(final AVObject account, AVException e) {
                                            if (e == null) {

                                                account.put(ResponeUtils.PARAM_TYPE_LOAD_SCAL_PIC_NAME, titleContainer.get(0));
                                                AVSaveOption option = new AVSaveOption();
                                                option.query(new AVQuery<>(ConstanUtil.CLIENTCLASS).whereEqualTo("user", userName));
                                                option.setFetchWhenSave(true);
                                                account.saveInBackground();

                                                executorService.schedule(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        AVQuery<AVObject> query = new AVQuery<>(ConstanUtil.PRIVIEW_PHOTO_CLASS);
                                                        query.whereEqualTo("user", userName);
                                                        query.findInBackground(new FindCallback<AVObject>() {
                                                            @Override
                                                            public void done(List<AVObject> list, AVException e) {
                                                                if (e == null) {
                                                                    AVObject avObject = list.get(0);
                                                                    long time = avObject.getUpdatedAt().getTime();
                                                                    String string = avObject.getString(ResponeUtils.PARAM_TYPE_SCAL_PIC_NAME_LOAD);
                                                                    if (!string.equals(titleContainer.get(0))) {
                                                                        mProgressbar.setVisibility(View.GONE);
                                                                        myHandler.sendEmptyMessage(CLEANIMG);
                                                                        UIUtil.toast(RemotePhotoAlbumActivity.this, "请求超时,请重新刷新");
                                                                        return;
                                                                    }


                                                                    byte[] bytes = avObject.getBytes(ResponeUtils.PARAM_TYPE_SCAL_PIC_LOAD);
                                                                    if (bytes != null) {

                                                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                        imageLoader.savaBitmap(titleContainer.get(0), bitmap);
                                                                        Message message = new Message();
                                                                        message.obj = bitmap;
                                                                        myHandler.sendMessage(message);
                                                                    } else {

                                                                    }
                                                                }
                                                                mProgressbar.setVisibility(View.GONE);
                                                            }
                                                        });
                                                    }
                                                }, 3000, TimeUnit.MILLISECONDS);
                                            } else {

                                                UIUtil.toast(RemotePhotoAlbumActivity.this, "请求出错,请重新刷新");
                                            }
                                        }
                                    });
                                }
                                adapter = new PageViewAdapter(titleContainer, viewContainter, RemotePhotoAlbumActivity.this, userName);
                                pager.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {

                        }
                    }

                    @Override
                    public void pictureCallback(Bitmap bitmap) {

                    }
                }, ConstanUtil.PRIVIEW_PHOTO_CLASS);
            }
        }, 2500, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remove:
                if (imageLoader != null) {
                    imageLoader.removeAll(titleContainer);
                    UIUtil.toast(this, "已清除缓存");
                    Intent intent = new Intent(this,MasterActivity.class);
                    intent.putExtra("username",userName);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.mbtn_return:
              //  UIUtil.toast(this, "返回");
                Intent intent = new Intent(this,MasterActivity.class);
                intent.putExtra("username",userName);
                startActivity(intent);
                finish();
                break;
            case R.id.priview_timer:
              /*  DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        RemotePhotoAlbumActivity.this, initFinishTime);
                dateTimePicKDialog.mdateTimePicKDialog(mtimer);*/
                break;
        }
    }
}


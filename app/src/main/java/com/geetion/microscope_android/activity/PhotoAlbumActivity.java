package com.geetion.microscope_android.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.geetion.coreTwoUtil.GAndroidUtil;
import com.geetion.coreTwoUtil.GScreenUtils;
import com.geetion.fresco.tool.FrescoTool;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.adapter.GalleryAdapter;
import com.geetion.microscope_android.application.BaseApplication;
import com.geetion.microscope_android.custom.MyRecyclerView;
import com.geetion.microscope_android.service.Constant;
import com.geetion.microscope_android.service.http.CommonActionCallBackString;
import com.geetion.microscope_android.utils.ConstanUtil;
import com.geetion.microscope_android.utils.FileUtil;
import com.geetion.xUtil.ActionCallback;
import com.geetion.xUtil.GBaseHttpParams;
import com.geetion.xUtil.GXHttpManager;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 相册页面
 */
public class PhotoAlbumActivity extends BaseActivity {

    private List<String> mMediaList = new ArrayList<>();
    private MyRecyclerView mRecyclerView;
    private ImageView mPlayButton;
    private TextView tvdel;
    private GalleryAdapter mAdapter;
    public MyAdapter mViewPagerAdapter;
    private Callback.Cancelable mPicNameCallback;
    private boolean mFetchingPicName;
    private FrameLayout.LayoutParams vp_layout;
    private int vp_Height;
    private int vp_width;
    private String mCurrentUrl = "";
    private Context context;

    private TextView mfileName;
    private int currentPage=1;
    private boolean isRequest=false;//是否在请求中
    private boolean isLastPage = false;//是否是最后一页
    private ProgressBar requestProgressBar;
   //定义定时线程池
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    /**
     * ViewPager
     */
    private ViewPager viewPager;

    public List<ImageView> mImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        context = this;
        setContentView(R.layout.activity_photo_album);
        super.onCreate(savedInstanceState);
        //BaseApplication.setContext(this);

        initView();
        initListener();
        initData();
    }

    @Override
    protected void onResume() {

        getPictures(currentPage);
        super.onResume();
    }


    @Override
    protected void onPause() {
        mAdapter.videoThumPool.shutdownNow();
        if (mPicNameCallback != null) {
            mPicNameCallback.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        System.gc();


        super.onDestroy();
    }

    private void initView() {
        currentPage = 1;
        isRequest =false;
        isLastPage = false;
        mPlayButton = (ImageView) findViewById(R.id.iv_play);
        mfileName = (TextView) findViewById(R.id.mfile_name);
        tvdel = (TextView) findViewById(R.id.tv_del);
        requestProgressBar = (ProgressBar) findViewById(R.id.request_progressbar);
        mRecyclerView = (MyRecyclerView) findViewById(R.id.id_recyclerview_horizontal);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new GalleryAdapter(this, mMediaList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.getLayoutParams().height = GScreenUtils.getScreenWidth(this) / 10;
        mRecyclerView.setOnBottomCallback(new MyRecyclerView.OnBottomCallback() {
            @Override
            public void onBottom() {
                currentPage++;
                getPictures(currentPage);
                Log.d("PhotoAlbumActivity","已经到底部 ：currentPage="+currentPage);
            }
        });
        //设置ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPagerAdapter = new MyAdapter();
        viewPager.setAdapter(mViewPagerAdapter);
    }

    public void getPictures(final int page) {
        //如果是最后一页则返回
        if(isLastPage)
            return;
        isRequest = true;
        if(isRequest){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    requestProgressBar.setVisibility(View.VISIBLE);
                }
            });
            //最多5秒后清除请求显示
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(requestProgressBar.getVisibility()==View.VISIBLE)
                                requestProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            },5000, TimeUnit.MILLISECONDS);
        }
        //设置状态过滤adapter重复调用
        if (!mFetchingPicName) {
            mFetchingPicName = true;
            GBaseHttpParams picParams = new GBaseHttpParams(Constant.HTTP_PROTOCOL + Constant.HTTP_URI + Constant.CAMERA_GETPHOTOLIST);
            Map<String, String> getPicMap = new HashMap<>();
            getPicMap.put("page", String.valueOf(page));
            picParams.putGETParams(getPicMap);
            mPicNameCallback = GXHttpManager.getWithJSON(this, GXHttpManager.METHOD_GET, picParams, new CommonActionCallBackString(PhotoAlbumActivity.this) {
                @Override
                public void callBackWithJSON(String json) {
                    isRequest = false;
                    requestProgressBar.setVisibility(View.GONE);
                   JSONObject object = JSON.parseObject(json);
                 //   String mjson = new String(Base64.decode(json.getBytes(), Base64.DEFAULT));
                 //   JSONObject object = JSON.parseObject(mjson);
                    com.alibaba.fastjson.JSONArray array = object.getJSONArray("list");
                    List<String> strings = Arrays.asList(array.toArray(new String[array.size()]));

                    for (int i = 0; i < strings.size(); i++) {
                        String medianame = strings.get(i);
                        Log.d("PhotoAlbumActivity","medianame="+medianame);
                        if (!mMediaList.contains(medianame)) {
                            if (medianame.contains("scaled")||medianame.contains("mp4")) {
                                mMediaList.add(medianame);
                                addVPView(mMediaList.indexOf(medianame), medianame);
                            }
                        }
                        if(page==1){
                            if(mMediaList.size()>1) {
                                String fn = mMediaList.get(0);
                                mfileName.setText(FileUtil.replaceFileName(fn.substring(0,fn.indexOf("."))));
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        mFetchingPicName = false;
                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    isLastPage =true;
                    requestProgressBar.setVisibility(View.GONE);
                    Toast.makeText(activity, msg + "", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean isViewPagerControlRecycleView = true;
    public boolean isRecycleViewControlViewPager = true;

    private void initListener() {
        //设置监听，主要是设置点点的背景
        viewPager.setOnPageChangeListener(onPageChangeListener);

        mRecyclerView.setOnItemScrollChangeListener(new MyRecyclerView.OnItemScrollChangeListener() {
            @Override
            public void onChange(View view, int position) {

                if (isRecycleViewControlViewPager) {
                    isViewPagerControlRecycleView = false;
                    mRecyclerView.setCurrentPosition(position);
                    viewPager.setCurrentItem(position);
                    String filename = mMediaList.get(position).toString();
                    mfileName.setText(FileUtil.replaceFileName(filename.substring(0,filename.indexOf("."))));
                    mAdapter.setCurrentPosition(position);
                    mAdapter.notifyDataSetChanged();
                    setPalyBtnVisibility(position);
                    isViewPagerControlRecycleView = true;
                }
            }
        });

        mAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //                Logger.e("onItemClick-position = " + position);
                if (isRecycleViewControlViewPager) {
                    isViewPagerControlRecycleView = false;
                    mRecyclerView.setCurrentPosition(position);
                    viewPager.setCurrentItem(position);
                    String filename = mMediaList.get(position).toString();
                    mfileName.setText(FileUtil.replaceFileName(filename.substring(0,filename.indexOf("."))));
                   // mfileName.setText(mMediaList.get(position).toString());
                    mAdapter.setCurrentPosition(position);
                    mAdapter.notifyDataSetChanged();
                    setPalyBtnVisibility(position);
                    isViewPagerControlRecycleView = true;
                }
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //使用封装了google-media-framework的播放器的WebViewActivity来播放服务器上相册里的视频 ;
                Intent i = new Intent(activity, WebViewActivity.class);
                i.putExtra("name", mCurrentUrl);
                startActivity(i);
            }
        });

        tvdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.delete);
                builder.setInverseBackgroundForced(true);
                builder.setMessage("你确认删除全部图片和视频吗？");
                builder.setTitle("全部删除");//设置对话框题目
                AlertDialog.Builder builder1 = builder.setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                if (mPicNameCallback != null) {
                                    mPicNameCallback.cancel();
                                }
                                finish();
                                return;
                                /*GBaseHttpParams params = new GBaseHttpParams(Constant.HTTP_PROTOCOL + Constant.HTTP_URI + Constant.DEL);
                                GXHttpManager.getObject(PhotoAlbumActivity.this, GXHttpManager.METHOD_GET, params, new ActionCallback() {
                                    @Override
                                    public void onSuccess(String msg) {

                                    }
                                    @Override
                                    public void onFailure(HttpException error, String msg) {

                                    }

                                    @Override
                                    public void onNetWorkError() {

                                    }
                                });*/
                            }
                        }
                );

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
                builder.create().
                        show();
            }
        });
    }

    private void initData() {
        vp_Height = GScreenUtils.getScreenHeight(this) - GAndroidUtil.dpToPx(150, this) - GAndroidUtil.dpToPx(48, this);
        vp_width = vp_Height * BaseApplication.DEFAULT_WIDTH / BaseApplication.DEFAULT_HEIGHT;
        vp_layout = new FrameLayout.LayoutParams(vp_width, vp_Height);

        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        viewPager.setCurrentItem(0);

    }

    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageList.get(position));
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewGroup parent = (ViewGroup) mImageList.get(position).getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            container.addView(mImageList.get(position));


            return mImageList.get(position);
        }
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //            Logger.e("onPageScrolled-position = " + position + ",positionOffset = " + positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            //            Logger.e("onPageSelected-position = " + position);
            if (isViewPagerControlRecycleView) {
                // 底部HorizontalScrollView需要选择
                isRecycleViewControlViewPager = false;
                mRecyclerView.setCurrentPosition(position);
                mRecyclerView.scrollToPosition(position);
                String filename = mMediaList.get(position).toString();
                mfileName.setText(FileUtil.replaceFileName(filename.substring(0,filename.indexOf("."))));
                //mfileName.setText(mMediaList.get(position).toString());
                setPalyBtnVisibility(position);
                mAdapter.setCurrentPosition(position);
                mAdapter.notifyDataSetChanged();
                isRecycleViewControlViewPager = true;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //            Logger.e("onPageScrollStateChanged-state = " + state);
        }
    };

    private void setPalyBtnVisibility(int position) {
        mCurrentUrl = (String) mImageList.get(position).getTag();
        //        Logger.e("Aye", "currentUrl " + mCurrentUrl);
        if (mCurrentUrl.contains(".mp4")) {
            mPlayButton.setVisibility(View.VISIBLE);
        } else {
            mPlayButton.setVisibility(View.GONE);
        }
    }

    public void addVPView(final int pos, final String name) {
        final SimpleDraweeView imageView = new SimpleDraweeView(this);
        imageView.setLayoutParams(vp_layout);
        imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        boolean isPic = name.contains(".bmp");
        if (isPic) {
            FrescoTool.displayImage(Constant.HTTP_PROTOCOL + Constant.HTTP_URI + Constant.CAMERA_GETPHOTO + "?picname=" + name, imageView);
        }
        imageView.setTag(name);
        imageView.setOnClickListener(makeVPListener());

        mImageList.add(pos, imageView);
        if (mImageList.size() == 1) {
            setPalyBtnVisibility(0);
        }
        mViewPagerAdapter.notifyDataSetChanged();
    }

    public View.OnClickListener makeVPListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = (String) v.getTag();
                boolean isMp4 = name.contains(".mp4");
                //使用封装了google-media-framework的播放器的WebViewActivity来播放服务器上相册里的视频 ;
                Intent intent = new Intent(PhotoAlbumActivity.this, isMp4 ? WebViewActivity.class : ImageActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        };
    }

}

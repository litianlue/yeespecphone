package com.geetion.microscope_android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.FindCallback;

import com.avos.avoscloud.GetCallback;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.activity.ImageActivity;
import com.geetion.microscope_android.activity.RemotePhotoAlbumActivity;
import com.geetion.microscope_android.utils.AsyncImageLoader;
import com.geetion.microscope_android.utils.ConstanUtil;


import com.geetion.microscope_android.utils.MD5EncryptorUtils;
import com.geetion.microscope_android.widget.TouchImageView;
import com.geetion.xUtil.ResponeUtils;


import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.geetion.microscope_android.R.id.container;

/**
 * Created by Administrator on 2017/12/26.
 */

public class PageViewAdapter extends PagerAdapter {

    private int priview_position = 0;
    private List<TouchImageView> mViewList;
    private List<String> titleList;
    private Activity context;
    private AsyncImageLoader asyncImageLoader;
    private String userName;
    private String mCacheRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/viewpager_cache/";

    public PageViewAdapter(List<String> titleList, List<TouchImageView> mViewList, Activity context,String userName) {
        this.mViewList = mViewList;
        this.titleList = titleList;
        this.context = context;
        this.userName = userName;
        asyncImageLoader  = new AsyncImageLoader(context);
    }


    @Override
    public int getCount() {//必须实现
        return titleList.size();
    }

    public void setPriview_position(int position) {
        priview_position = position;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {//必须实现
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {//必须实现，实例化

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        int i = position % 4;
        final TouchImageView imageView = mViewList.get(i);
        imageView.setLayoutParams(params);
        final String url = titleList.get(position);
        imageView.setTag(url);
      //  Log.w("Adapter", "instantiateItem.position=" + position);
      //  Log.w("Adapter", "titleList。url=" + url);
       /* String cacheExists = cacheExists(url);
        if (TextUtils.isEmpty(cacheExists)) {//没缓存 需要压缩(压缩耗时 异步)
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    AVQuery<AVObject> query = new AVQuery<>(ConstanUtil.PRIVIEW_PHOTO_CLASS);
                    query.whereGreaterThanOrEqualTo("user", ConstanUtil.loginUser);
                    query.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            if (e == null) {
                                AVObject avObject = list.get(0);
                                byte[] bytes = avObject.getBytes(ResponeUtils.PARAM_TYPE_SCAL_PIC_LOAD);
                                if (bytes != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    if(imageView.getTag()!=null&&imageView.getTag().equals(url))
                                    imageView.setImageBitmap(bitmap);
                                    notifyDataSetChanged();
                                } else {
                                    imageView.setImageBitmap(null);

                                    notifyDataSetChanged();
                                }
                            } else {
                                imageView.setImageBitmap(null);

                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            }, 2000, TimeUnit.MILLISECONDS);

        } else {//有缓存 直接显示
            Log.w("Adapter", "url=" + url);
            Bitmap bitmap = BitmapFactory.decodeFile(cacheExists);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                Log.w("Adapter", "url=" + url);
            }

        }*/
        //asyncImageLoader.loadImage(imageView,url);
       // Bitmap bitmap = BitmapFactory.decodeResource(container.getResources(), R.mipmap.ic_launcher);
       // imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra("name", titleList.get(position));
                intent.putExtra("username",userName);
                context.startActivity(intent);
            }
        });
        container.addView(imageView);
        return imageView;

    }
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {//必须实现，销毁
        int i = position % 4;
        TouchImageView imageView = mViewList.get(i);
        if (imageView != null) {
            // imageView = null;
        }

        container.removeView(imageView);

    }

    @Override
    public int getItemPosition(Object object) {

        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String substring = titleList.get(position).substring(titleList.get(position).lastIndexOf("/"));
    //    Log.w("Adapter", "getPageTitle。position=" + position);
        return substring.substring(8, substring.length());

    }

    private String cacheExists(String url) {

        try {
            File fileDir = new File(mCacheRootPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            File file = new File(mCacheRootPath, new StringBuffer().append(MD5EncryptorUtils.md5Encryption(url)).toString());
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getCacheNoExistsPath(String url) {
        File fileDir = new File(mCacheRootPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }


        return new StringBuffer().append(mCacheRootPath)
                .append(MD5EncryptorUtils.md5Encryption(url)).toString();
    }
}

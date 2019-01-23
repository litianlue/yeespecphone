package com.geetion.microscope_android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.geetion.microscope_android.widget.DiskLruCache;
import com.geetion.xUtil.ResponeUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/12/29.
 */

public class AsyncImageLoader {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Context context;
    // 内存缓存默认 5M
    static final int MEM_CACHE_DEFAULT_SIZE = 5 * 1024 * 1024;
    // 文件缓存默认 10M
    static final int DISK_CACHE_DEFAULT_SIZE = 10 * 1024 * 1024;
    // 一级内存缓存基于 LruCache
    private LruCache<String, Bitmap> memCache;
    // 二级文件缓存基于 DiskLruCache
    private DiskLruCache diskCache;

    public AsyncImageLoader(Context context) {
        this.context = context;
        initMemCache();
        initDiskLruCache();
    }

    /**
     * 初始化内存缓存
     */
    private void initMemCache() {
        memCache = new LruCache<String, Bitmap>(MEM_CACHE_DEFAULT_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }
    public void savaBitmap(String url,Bitmap bmp){
        putBitmapToMem(url,bmp);
        try {

            String key = hashKeyForDisk(url);
            // 下载成功后直接将图片流写入文件缓存
            DiskLruCache.Editor editor = null;
            editor = diskCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();
                outputStream.write(datas);
                editor.commit();
            }
            diskCache.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Bitmap getBitmap(String url){

        Bitmap bitmapFromMem = getBitmapFromMem(url);
        if(bitmapFromMem!=null){
            return bitmapFromMem;
        }
        bitmapFromMem = getBitmapFromDisk(url);
        if (bitmapFromMem != null) {
            // 重新缓存到内存中
            putBitmapToMem(url, bitmapFromMem);
            return bitmapFromMem;
        }

        return null;
    }
    /**
     * 初始化文件缓存
     */
    private void initDiskLruCache() {
        try {
            File cacheDir = getDiskCacheDir(context, "bitmap");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            diskCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, DISK_CACHE_DEFAULT_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从内存缓存中拿
     *
     * @param url
     */
    public Bitmap getBitmapFromMem(String url) {
        return memCache.get(url);
    }

    /**
     * 加入到内存缓存中
     *
     * @param url
     * @param bitmap
     */
    public void putBitmapToMem(String url, Bitmap bitmap) {
        memCache.put(url, bitmap);

    }

    /**
     * 从文件缓存中拿
     *
     * @param url
     */
    public Bitmap getBitmapFromDisk(String url) {
        try {
            String key = hashKeyForDisk(url);
            DiskLruCache.Snapshot snapShot = diskCache.get(key);
            if (snapShot != null) {
                InputStream is =  snapShot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public void removeAll(final List<String> keys){
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < keys.size(); i++) {
                    String key = hashKeyForDisk(keys.get(i));
                    if(memCache!=null)
                        memCache.remove(key);
                    try {
                        if(diskCache!=null)
                            diskCache.remove(key);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        },0, TimeUnit.MILLISECONDS);


    }
    /**
     * 从 url 加载图片
     *
     * @param imageView
     * @param imageUrl
     */
    public Bitmap loadImage(ImageView imageView, String imageUrl) {
        // 先从内存中拿
        Bitmap bitmap = getBitmapFromMem(imageUrl);

        if (bitmap != null) {
            Log.i("leslie", "image exists in memory");
            return bitmap;
        }

        // 再从文件中找
        bitmap = getBitmapFromDisk(imageUrl);
        if (bitmap != null) {
            Log.i("leslie", "image exists in file");
            // 重新缓存到内存中
            putBitmapToMem(imageUrl, bitmap);
            return bitmap;
        }

        // 内存和文件中都没有再从网络下载
        if (!TextUtils.isEmpty(imageUrl)) {
            new ImageDownloadTask(imageView).execute(imageUrl);
        }

        return null;
    }

    class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {
        private String imageUrl;
        private ImageView imageView;

        public ImageDownloadTask(ImageView imageView) {
            this.imageView = imageView;
        }
        private int connectCount=1;
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                imageUrl = params[0];
                String key = hashKeyForDisk(imageUrl);
                // 下载成功后直接将图片流写入文件缓存
                DiskLruCache.Editor editor = diskCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    connectCount =1;
                    if (downloadUrlToStream(imageUrl, outputStream)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
                diskCache.flush();

                Bitmap bitmap = getBitmapFromDisk(imageUrl);
                if (bitmap != null) {
                    // 将图片加入到内存缓存中
                    putBitmapToMem(imageUrl, bitmap);
                }

                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                // 通过 tag 来防止图片错位
                if (imageView.getTag() != null && imageView.getTag().equals(imageUrl)) {
                    imageView.setImageBitmap(result);
                }
            }
        }

        private boolean downloadUrlToStream(final String urlString, final OutputStream outputStream) {
            final boolean[] flage = {false};

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

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] datas = baos.toByteArray();
                            try {
                                outputStream.write(datas);
                                flage[0] =true;
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            flage[0] =false;
                        }
                    }
                }
            });
            Log.w("test","flage[]="+flage[0]);
          //  flage[0] =true;
            if(flage[0]==false&&connectCount==1){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connectCount=0;
                downloadUrlToStream(urlString,outputStream);

            }

          /*
            HttpURLConnection urlConnection = null;
            BufferedOutputStream out = null;
            BufferedInputStream in = null;
            try {
                final URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
                out = new BufferedOutputStream(outputStream, 8 * 1024);
                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                return true;
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }*/
            return flage[0];
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}

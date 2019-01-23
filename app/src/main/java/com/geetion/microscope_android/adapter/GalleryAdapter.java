package com.geetion.microscope_android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.geetion.coreTwoUtil.GScreenUtils;
import com.geetion.fresco.tool.FrescoTool;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.activity.PhotoAlbumActivity;
import com.geetion.microscope_android.application.BaseApplication;
import com.geetion.microscope_android.frameGrabber.media.FrameGrabber;
import com.geetion.microscope_android.service.Constant;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private LayoutInflater mInflater;
    private List<String> mDatas;
    private PhotoAlbumActivity activity;
    private int height;
    private int width;
    private boolean isPic;
    public ExecutorService videoThumPool;
    private int currentPosition;
    private FrameGrabber mFrameGrabber;
    private LruCache<String, Bitmap> mLruCache;

    public GalleryAdapter(Context context, List<String> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
        activity = (PhotoAlbumActivity) context;
        mLruCache = new LruCache<>((int) Runtime.getRuntime().maxMemory() / 8);
        height = GScreenUtils.getScreenWidth(context) / 10;
        width = height * BaseApplication.DEFAULT_WIDTH / BaseApplication.DEFAULT_HEIGHT;
        videoThumPool = Executors.newFixedThreadPool(2);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        SimpleDraweeView img;
        ImageView videoTag;
        View itemBgView;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_index_gallery, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.videoTag = (ImageView) view.findViewById(R.id.type);
        viewHolder.img = (SimpleDraweeView) view.findViewById(R.id.id_index_gallery_item_image);
        viewHolder.img.getLayoutParams().width = width;
        viewHolder.img.getLayoutParams().height = height;
        viewHolder.itemBgView = view.findViewById(R.id.content_bg);
        viewHolder.itemBgView.getLayoutParams().width = width;
        viewHolder.itemBgView.getLayoutParams().height = height;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final String name = mDatas.get(i);
        String picUrl = Constant.HTTP_PROTOCOL + Constant.HTTP_URI + Constant.CAMERA_GETPHOTO + "?picname=" + name;
        final String videoUrl = Constant.HTTP_PROTOCOL + Constant.HTTP_URI + Constant.VIDEO_GET_VIDEO + "?video_name=" + name;
/*
//        Logger.e("Aye", videoUrl);
        //如果不是最后一页则向服务器申请下一页数据
        if (i + 3 >= mDatas.size() && mDatas.size() % 10 == 0) {
//            Logger.e("Aye", "Aye onBindViewHolder page " + " size " + mDatas.size() + " " + (mDatas.size() / 10 + 1) + " pos " + i);
            activity.getPictures(mDatas.size() / 10 + 1);
        }
*/

        isPic = name.contains(".bmp");
        isPic |= name.contains(".png");
        isPic |= name.contains(".jpg");

        if (isPic) {
//            Logger.e("Aye", "onBindViewHolder " + picUrl);
            FrescoTool.displayImage(picUrl, viewHolder.img);
            viewHolder.videoTag.setImageDrawable(null);
        } else {
            viewHolder.videoTag.setImageDrawable(activity.getResources().getDrawable(R.mipmap.luxiang_s));
            if (mLruCache.get(videoUrl) != null) {
                viewHolder.img.setImageBitmap(mLruCache.get(videoUrl));
            } else {
                viewHolder.img.setImageBitmap(null);
                try {
                    videoThumPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (!videoThumPool.isShutdown()) {
                                final Bitmap bitmap = getFrameAtTimeByFrameGrabber(Constant.HTTP_PROTOCOL + Constant.HTTP_URI + Constant.VIDEO_GET_VIDEO + "?video_name=" + name, 3333 * 5, 80, 60);
                                releaseFrameGrabber();
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (bitmap != null) {
                                            mLruCache.put(videoUrl, bitmap);
                                        }
                                        viewHolder.img.setImageBitmap(bitmap);
                                        activity.mImageList.get(i).setImageBitmap(bitmap);
                                        notifyDataSetChanged();
                                        activity.mViewPagerAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    });
                } catch (RejectedExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        if (i == currentPosition) {
            viewHolder.itemBgView.setBackground(activity.getResources().getDrawable(R.drawable.photo_content_bg_red));
        } else {
            viewHolder.itemBgView.setBackground(activity.getResources().getDrawable(R.drawable.photo_content_bg_white));
        }

        if (mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(viewHolder.itemView, i);
                }
            });

        }
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Bitmap getFrameAtTimeByFrameGrabber(String path, long time, int width, int height) {
        mFrameGrabber = new FrameGrabber();
        mFrameGrabber.setDataSource(path);
        mFrameGrabber.setTargetSize(width, height);
        mFrameGrabber.init();
        return mFrameGrabber.getFrameAtTime(time);
    }

    public void releaseFrameGrabber() {
        if (mFrameGrabber != null) {
            mFrameGrabber.release();
            mFrameGrabber = null;
        }
    }

}

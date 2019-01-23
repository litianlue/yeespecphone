package com.geetion.microscope_android.frameGrabber.media;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import com.geetion.microscope_android.frameGrabber.gl.GLHelper;


public class FrameGrabber {        //图像采集卡
    final static String TAG = "FrameGrabber";

    private HandlerThread mGLThread = null;
    private Handler mGLHandler = null;
    private GLHelper mGLHelper = null;

    private int mDefaultTextureID = 10001;

    private int mWidth = 1920;
    private int mHeight = 1080;

    private String mPath = null;

    public FrameGrabber() {
        mGLHelper = new GLHelper();
        mGLThread = new HandlerThread("FrameGrabber");

        mGLThread.start();
        mGLHandler = new Handler(mGLThread.getLooper());
    }

    public void setDataSource(String path) {
        mPath = path;
    }

    public void setTargetSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void init() {
        try {
            mGLHandler.post(new Runnable() {
                @Override
                public void run() {
                    SurfaceTexture st = new SurfaceTexture(mDefaultTextureID);
                    st.setDefaultBufferSize(mWidth, mHeight);
                    mGLHelper.init(st);
                }
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        mGLHandler.post(new Runnable() {
            @Override
            public void run() {
                mGLHelper.release();
                mGLThread.quit();
            }
        });
    }

    private Object mWaitBitmap = new Object();
    private Bitmap mBitmap = null;

    public Bitmap getFrameAtTime(final long frameTime) {
        if (null == mPath || mPath.isEmpty()) {
            throw new RuntimeException("Illegal State");
        }

        mGLHandler.post(new Runnable() {
            @Override
            public void run() {
                getFrameAtTimeImpl(frameTime);
            }
        });

        synchronized (mWaitBitmap) {
            try {
                mWaitBitmap.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return mBitmap;
    }

    @SuppressLint("SdCardPath")
    public void getFrameAtTimeImpl(long frameTime) {
        try {
            final int textureID = mGLHelper.createOESTexture();
            final SurfaceTexture st = new SurfaceTexture(textureID);
            final Surface surface = new Surface(st);
            final VideoDecoder vd = new VideoDecoder(mPath, surface);
            st.setOnFrameAvailableListener(new OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    Log.i(TAG, "onFrameAvailable");
                    try {
                        mGLHelper.drawFrame(st, textureID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mBitmap = mGLHelper.readPixels(mWidth, mHeight);
                    synchronized (mWaitBitmap) {
                        mWaitBitmap.notify();
                    }

                    vd.release();
                    st.release();
                    surface.release();
                }
            });

            if (!vd.prepare(frameTime)) {
                mBitmap = null;
                synchronized (mWaitBitmap) {
                    mWaitBitmap.notify();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

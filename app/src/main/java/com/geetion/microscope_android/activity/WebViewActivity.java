package com.geetion.microscope_android.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.geetion.microscope_android.R;
import com.geetion.microscope_android.adapter.ImaPlayer;
import com.geetion.microscope_android.custom.VideoPlayerContainer;
import com.geetion.microscope_android.service.Constant;
import com.geetion.microscope_android.utils.FileUtil;
import com.google.android.libraries.mediaframework.exoplayerextensions.Video;


/**
 * Created by WongzYe on 16/1/9.
 */

/**
 * WebViewActivity此Activity用于手机端相册播放服务器上拍摄的视频 :
 */
public class WebViewActivity extends BaseActivity {

    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;

    private ImaPlayer mImaPlayer;
    private VideoPlayerContainer mPlayerContainer;
    private String mName;

    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;

    private int mMaxVolume;
    private int mVolume = -1;
    private float mBrightness = -1f;
    private TextView mbtnReturn;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = WebViewActivity.this;
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
        startPlay();
    }

    @Override
    protected void onDestroy() {
        if (mImaPlayer != null) {
            mImaPlayer.release();
        }
        super.onDestroy();
    }

    private void initView() {
        setContentView(R.layout.activity_webview);
        mPlayerContainer = (VideoPlayerContainer) findViewById(R.id.video_frame);
        mbtnReturn = ((TextView) findViewById(R.id.btn_return));
        mTitle = ((TextView) findViewById(R.id.title));
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
    }

    private void initListener() {
        mPlayerContainer.setListenr(new VideoPlayerContainer.ScrollviewListener() {
            @Override
            public void onScroll(MotionEvent e1, MotionEvent e2) {
                mDismissHandler.removeMessages(0);
                float mOldX = e1.getX(), mOldY = e1.getY();
                int y = (int) e2.getRawY();
                Display disp = getWindowManager().getDefaultDisplay();
                int windowWidth = disp.getWidth();
                int windowHeight = disp.getHeight();

                if (mOldX > windowWidth * 3.0 / 5)// 右边滑动
                    onVolumeSlide((mOldY - y) / windowHeight);
                else if (mOldX < windowWidth / 3.0)// 左边滑动
                    onBrightnessSlide((mOldY - y) / windowHeight);

            }

            @Override
            public void onEndGesture() {
                endGesture();
            }
        });
        mbtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImaPlayer != null) {
                    mPlayerContainer.removeAllViews();
                    mImaPlayer.pause();
                    mImaPlayer.release();
                    mImaPlayer = null;
                }
                finish();
            }
        });
    }

    private void initData() {
        mName = getIntent().getStringExtra("name");
        mTitle.setText(FileUtil.replaceFileName(mName.toString()));
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void startPlay() {

        if (mImaPlayer != null) {
            mImaPlayer.release();
        }

        mPlayerContainer.removeAllViews();

        // TODO: 2016/7/29  : 此处调用了google-media-framework外联库里的video :
        Video video = new Video(Constant.HTTP_PROTOCOL + Constant.HTTP_URI + Constant.VIDEO_GET_VIDEO + "?video_name=" + mName, Video.VideoType.MP4);

        //使用自己封装了google-media-framework的ImaPlayer播放器来播放 :
        mImaPlayer = new ImaPlayer(this, mPlayerContainer, video, mName, null);
        mImaPlayer.play();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mPlayerContainer.onTouchEvent(event);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

        // 隐藏
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 定时隐藏
     */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mVolumeBrightnessLayout.setVisibility(View.GONE);
        }
    };


    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // 显示
            mOperationBg.setImageResource(R.mipmap.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            mOperationBg.setImageResource(R.mipmap.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }

}

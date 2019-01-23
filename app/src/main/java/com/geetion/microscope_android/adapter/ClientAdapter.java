package com.geetion.microscope_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.geetion.microscope_android.activity.MasterActivity;
import com.geetion.microscope_android.activity.SelectClientActivity;
import com.geetion.microscope_android.utils.ConstanUtil;
import com.geetion.microscope_android.utils.LeanCloudRequesUtil;
import com.geetion.microscope_android.widget.TouchImageView;

import java.util.List;

/**
 * Created by Administrator on 2018/1/3.
 */

public class ClientAdapter extends PagerAdapter {
    OnClienListenerCallback requestListener;

    public  interface OnClienListenerCallback{
        void mListenerCallback(int position);
    }
    public void setListenerCallback(OnClienListenerCallback requestListener){
        this.requestListener = requestListener;
    }
    private List<String> list ;
    private List<View> views;
    private Context context;
    private List<String> password;
    public  ClientAdapter(SelectClientActivity selectClientActivity,
                          List<String> lists, List<View> views, List<String> password){
        this.list = lists;
        this.views = views;
        this.context  = selectClientActivity;
        this.password = password;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        int i = position%4;
        View view = views.get(i);
        view.setLayoutParams(params);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestListener.mListenerCallback(position);
            }
        });
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        int i = position % 4;
        View view = views.get(i);
        container.removeView(view);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position);
    }
}

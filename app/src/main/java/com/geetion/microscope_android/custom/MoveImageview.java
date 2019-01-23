package com.geetion.microscope_android.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yuchunrong on 2017-06-22.
 */

public class MoveImageview extends ImageView {
    public MoveImageview(Context context) {
        super(context);
    }

    public MoveImageview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public MoveImageview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setLocation(int x,int y){
        //this.setFrame(x,y-this.getHeight(),x+this.getWidth(),y);
        this.setFrame(y,x,y,x);
    }
}

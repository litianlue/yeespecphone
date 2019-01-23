package com.geetion.microscope_android.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.geetion.microscope_android.R;


/**
 * Created by aye on 2015/7/7.
 */

public class PasswordDialog extends Dialog implements View.OnClickListener{

    private EditText mEditText;
    private Button mConfirmButton;
    private String mPassword;

    public PasswordDialog(Context context, OnConfirmListener listener) {
        super(context, R.style.Dialog_Radio);
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        initView();
    }

   private void initView() {
       setContentView(R.layout.dialog_password);
       mEditText = (EditText) findViewById(R.id.password_enter);
       mConfirmButton = (Button) findViewById(R.id.confirm_butotn);
       mEditText.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               mPassword = s.toString();
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });
       mConfirmButton.setOnClickListener(this);
   }

    private OnConfirmListener mListener;//确定按钮接口

    public interface OnConfirmListener {

        void onClick(String password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_butotn:
                mListener.onClick(mPassword);
                break;
        }
    }
}

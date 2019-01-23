package com.geetion.microscope_android.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geetion.coreOneUtil.UIUtil;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.service.DataDepot;
import com.geetion.microscope_android.utils.ConstanUtil;
import com.geetion.microscope_android.utils.DataUtil;
import com.geetion.microscope_android.utils.DateTimePickDialogUtil;
import com.geetion.microscope_android.utils.NumberUtil;
import com.geetion.microscope_android.utils.SPUtils;
import com.geetion.microscope_android.yeespec.LoginDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.geetion.microscope_android.utils.ConstanUtil.ConvergenceNumber;

/**
 * Created by Administrator on 2016/3/21.
 */

public class AutoPhotoActivity extends BaseActivity {
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    /**
     * Called when the activity is first created.
     */
    private EditText inTheEndTimer;
    private EditText startDateTimer, photoNumber, finishTime,convergenceNumber;
    private TextView btnChecked;
    private TextView btnReturn;

    private String initStartDateTime;// 初始化开始时间
    private String initEndDateTime; //="2016年03月20日 16:25"; // 初始化结束时间
    private String initFinishTime;

    private String mTime2, mTime3, mTime1, mTime4;
    private Date date1 = null;
    private Date date2 = null;
    private Context context;
    private long now, Nextday;
    private static long oneDay = 24 * 60 * 60 * 1000;
    private CheckBox whiteCheckBox;
    private CheckBox blueCheckBox;
    private CheckBox greenCheckBox;
    private CheckBox purpleCheckBox;
    private CheckBox mAutofocus;
    private CheckBox mSynthetic;
    private LinearLayout writell, bluell, greenll, purplell, synchronousll;
    private Set<String> checboxs = new HashSet<>();
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.auto_photo);
        super.onCreate(savedInstanceState);

        activity = this;
        //获取当时时间
        userName = getIntent().getStringExtra("username");
        Date date = new Date(DataDepot.startdatetimer);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String datestr = sdf.format(date);
        String datestr2 = sdf.format(new Date(DataDepot.enddatetimer));

        initStartDateTime = datestr;
        initEndDateTime = datestr;
        initFinishTime = datestr2;
        initView();
        // 两个输入框
      //  Log.w("AutoPhotoActivity", "initStartDateTime=" + initStartDateTime);
     //   Log.w("AutoPhotoActivity", "initFinishTime=" + initFinishTime);
        inTheEndTimer.setText(initStartDateTime);
        startDateTimer.setText(initEndDateTime);
        finishTime.setText(initFinishTime);
        photoNumber.setText("5");
        convergenceNumber.setText("50");
        initListener();//监听两个输入宽
        listenerExitText();//设置输入款不能为0
        initCheckedListener();
        initCheckBoxListenes();

        startDateTimer.addTextChangedListener(textWatcher);
        finishTime.addTextChangedListener(textWatcher);
        photoNumber.addTextChangedListener(textWatcher);
        String timerString = stopTimerString();
        if(timerString.contains("!")){
            inTheEndTimer.setTextColor(Color.rgb(255,32,32));
        }else {
            inTheEndTimer.setTextColor(Color.rgb(75,77,77));
        }
        inTheEndTimer.setText(timerString);

    }

    private void initView() {
        convergenceNumber = (EditText) findViewById(R.id.c_number);
        inTheEndTimer = (EditText) findViewById(R.id.inputDate);
        startDateTimer = (EditText) findViewById(R.id.inputDate2);
        finishTime = (EditText) findViewById(R.id.inputDate3);
        photoNumber = (EditText) findViewById(R.id.photo_number);
        btnChecked = (TextView) findViewById(R.id.check);
        whiteCheckBox = ((CheckBox) findViewById(R.id.whitelight));
        blueCheckBox = ((CheckBox) findViewById(R.id.bluelight));
        greenCheckBox = ((CheckBox) findViewById(R.id.greenlight));
        purpleCheckBox = ((CheckBox) findViewById(R.id.purplelight));
        mAutofocus = ((CheckBox) findViewById(R.id.isauto_focus));
        mSynthetic = ((CheckBox) findViewById(R.id.issynthetic));
        writell = ((LinearLayout) findViewById(R.id.writell));
        bluell = ((LinearLayout) findViewById(R.id.bluell));
        greenll = ((LinearLayout) findViewById(R.id.greenll));
        purplell = ((LinearLayout) findViewById(R.id.purplell));
        writell.setVisibility(View.GONE);
        bluell.setVisibility(View.GONE);
        greenll.setVisibility(View.GONE);
        purplell.setVisibility(View.GONE);
        synchronousll = ((LinearLayout) findViewById(R.id.synchrnousll));
        if (ConstanUtil.LIGHTNUMBER > 1)
            synchronousll.setVisibility(View.VISIBLE);
        String[] strings = ConstanUtil.LIGHTTYPE.split(",");
        if (strings.length > 0) {
            for (int i = 0; i < strings.length; i++) {
                int vs = Integer.valueOf(strings[i]);
                switch (vs) {
                    case 1:
                        writell.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        bluell.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        greenll.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        purplell.setVisibility(View.VISIBLE);
                        break;

                }
            }
        }

       // btnReturn = (TextView) findViewById(R.id.btn_return);

        // TODO: 2016/7/10 : 添加对话框手动设置默认参数配置 :
        Button settingDialog = (Button) findViewById(R.id.setting_dialog);
        settingDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //        Toast.makeText(getApplicationContext(), "弹出对话框 ! ", Toast.LENGTH_SHORT).show();

                //2016.08.03 : 添加连接平板的弹窗 :
                Dialog loginDialog = LoginDialog.getLoginDialog(AutoPhotoActivity.this);
                //                if (!loginDialog.isShowing()) {
                //                    loginDialog.show();
                //                }

                loginDialog.show();

            }
        });


    }

    private void listenerExitText() {
        photoNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = s.toString();
                int len = s.toString().length();
                if (len == 1 && text.equals("0")) {
                    s.clear();
                }

            }
        });

    }

    //监听开始时间和，结束时间
    private void initListener() {
      /*  startDateTimer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        AutoPhotoActivity.this, initEndDateTime);

                dateTimePicKDialog.dateTimePicKDialog(startDateTimer);
            }
        });
*/
        finishTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        AutoPhotoActivity.this, initFinishTime);
                dateTimePicKDialog.dateTimePicKDialog(finishTime);

            }
        });
    }


    public static long getSecondes(String date1, String date2) {
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;

        DateFormat myFormatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date date = null;
        Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
        }
        long secondes = (date.getTime() - mydate.getTime()) / 1000;
        return secondes;
    }

    private String stopTimerString(){
        String startTimer=startDateTimer.getText().toString().trim();
        String stopTimer =finishTime.getText().toString().trim();
        String timerNumber= photoNumber.getText().toString().trim();
        Set<String> Checkboxs = checboxs;
        if(Checkboxs==null||Checkboxs.size()<1||startTimer==null||stopTimer==null||timerNumber==null||timerNumber.equals("")){
            return "正在计算中。。。";
        }
        int checks=0;
        //如果开启对照组
        if (DataUtil.getContracts()!=null) {
            int[][] contracts = DataUtil.getContracts();
            if (mSynthetic.isChecked()) {
                checks = contracts.length * Checkboxs.size() + contracts.length;
            } else {
                checks = contracts.length * Checkboxs.size();
            }
        } else {
            if(mSynthetic.isChecked()){
                checks =Checkboxs.size()+1;
            }else {
                checks =Checkboxs.size();
            }
        }

        String timerString=stopTimer;
        int onePhotoSize= (1024*4+60)*checks;//一轮之后使用空间：kb
        int timerSpacing;
        if(timerNumber.equals("")||timerNumber==null){
            timerSpacing =0;
        }else {
            timerSpacing = Integer.valueOf(timerNumber);

        }

        //可用内存大小 /一轮使用空间 * 时间间隔= 可用时间段(分
        //如果（结束时间-开始时间）<可用时间段   停止时间 = 结束时间
        //如果 （结束时间-开始时间）> 可用时间段  停止时间 = 开始时间 +可用时间段
        long emportmemory=0;
        if(DataDepot.employMemory.contains("GB")){
            String employmemorystr= DataDepot.employMemory.replace("GB","");
            Log.e("SettingActivity","employmemorystr="+employmemorystr);
            emportmemory =(long) (Float.valueOf(employmemorystr.trim())*1024*1024*1024);
        }else  if(DataDepot.employMemory.contains("MB")){

            String employmemorystr= DataDepot.employMemory.replace("MB","");
            emportmemory = (long) (Float.valueOf(employmemorystr.trim())*1024*1024);
        }

        Log.e("SettingActivity","emportmemory="+emportmemory);
        int availableTimer = (int)(((emportmemory/1024-500*1024)/onePhotoSize)*timerSpacing);
        long secondes = getSecondes(stopTimer, startTimer);

        if(secondes-availableTimer*60<0){
            if(timerSpacing!=0)
                ConstanUtil.autoPhotoCount = (int)((secondes/timerSpacing/60+1)*checks);
            ConstanUtil.stopAutoPhotoStr = "停止时间："+stopTimer;
            return stopTimer;
        }else {

            DateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

            try {


                Date startt = format.parse(startTimer);
                long seconde = startt.getTime()/1000+availableTimer*60;

                Date mdate = new Date(seconde*1000);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                String stoptimer = dateFormat.format(mdate);

                long secondes2 = getSecondes(stoptimer, startTimer);
                if(timerSpacing!=0)
                    ConstanUtil.autoPhotoCount = (int)((secondes2/timerSpacing/60+1)*checks);
                ConstanUtil.stopAutoPhotoStr =  "停止时间："+stoptimer;

                return stoptimer+"  !!!";
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return timerString;
    }
    //监听Editext内容变化方法
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            Log.d("TAG","afterTextChanged--------------->");
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
            Log.d("TAG","beforeTextChanged--------------->");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            Log.d("TAG","onTextChanged--------------->");
            String timerString = stopTimerString();
            if(timerString.contains("!")){
                inTheEndTimer.setTextColor(Color.rgb(255,32,32));
            }else {
                inTheEndTimer.setTextColor(Color.rgb(75,77,77));
            }
            inTheEndTimer.setText(timerString);

        }
    };

    private void initCheckedListener() {
        btnChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTime2 = startDateTimer.getText().toString();
                mTime3 = photoNumber.getText().toString();
                mTime4 = finishTime.getText().toString();
                String cNumber = convergenceNumber.getText().toString();
                if (cNumber == null || cNumber.equals("")) {
                    UIUtil.toast(AutoPhotoActivity.this, "细胞汇合率不能为空");
                    return;
                }
                if (!NumberUtil.isNummeric(cNumber)) {
                    UIUtil.toast(AutoPhotoActivity.this, "细胞汇合率必须是整数!");
                    return;
                }
                int mCNumber = Integer.valueOf(cNumber);
                if(mCNumber<0||mCNumber>100){
                    UIUtil.toast(AutoPhotoActivity.this, "细胞汇合率必须是0-100");
                    return;
                }
                if (!NumberUtil.isNummeric(mTime3)) {
                    UIUtil.toast(AutoPhotoActivity.this, "时间间隔必须为整数!");
                    return;
                }
                Date DTime2 = null;
                Date DTime3 = null;
                Date DTime4 = null;
                DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                try {
                    DTime2 = dateFormat.parse(mTime2);
                    DTime3 = dateFormat.parse(initEndDateTime);
                    DTime4 = dateFormat.parse(mTime4);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Long LTime2 = DTime2.getTime();
                Long LTime3 = DTime3.getTime();
                Long LTime4 = DTime4.getTime();
                if (LTime3 - LTime2 > 0) {
                    UIUtil.toast(AutoPhotoActivity.this, "输入时间小于开始时间，请重新输入!");
                    //Toast.makeText(AutoPhotoActivity.this, "输入时间小于当前时间，请重新输入！", Toast.LENGTH_LONG).show();
                    return;
                } else if (LTime2 - LTime4 >= 0) {
                    UIUtil.toast(AutoPhotoActivity.this, "输入时间小于或者等于开始时间，请重新输入！");
                    //Toast.makeText(AutoPhotoActivity.this, "输入时间小于或者等于开始时间，请重新输入！", Toast.LENGTH_LONG).show();
                    return;
                }

                SPUtils.put(getBaseContext(), "autoPhoto_startTime", mTime2);
                SPUtils.put(getBaseContext(), "autoPhoto_tTime", mTime3);
                SPUtils.put(getBaseContext(), "autoPhoto_finishTime", mTime4);
                Set<String> autoPhoto_light = SPUtils.getset(getApplicationContext(), "autoPhoto_light");
                int lights = autoPhoto_light == null ? 0 : autoPhoto_light.size();
                int mt = Integer.valueOf(mTime3);

                if (lights < 1) {
                    UIUtil.toast(AutoPhotoActivity.this, "请选择自动拍照色灯");
                    //Toast.makeText(AutoPhotoActivity.this, "请选择自动拍照色灯", Toast.LENGTH_LONG).show();
                    return;
                }
                int[][] contracts = DataUtil.getContracts();
                if (DataUtil.getContracts()!=null) {
                    double minute = (double) (contracts.length * autoPhoto_light.size() * 8.0 / 60.0) + 1;
                    int result = (int) Math.ceil(minute);
                    if (mt < result) {
                        UIUtil.toast(AutoPhotoActivity.this, "自动拍照时间间隔必须大于或等于" + result + "分钟");

                        return;
                    }
                } else {

                    if (autoPhoto_light.size() > 1 && mt < 2) {
                        UIUtil.toast(AutoPhotoActivity.this, "多个激发快时间间隔不能小于2分钟");
                        // Toast.makeText(AutoPhotoActivity.this, "多个激发快时间间隔不能小于2分钟", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                ConvergenceNumber  = mCNumber+"";
                DataDepot.autophoto_view = 2;
                ConstanUtil.isAutophoto = true;
              //  UIUtil.toast(AutoPhotoActivity.this, "点拍照，进入自动拍照");
               // Toast.makeText(AutoPhotoActivity.this, "点拍照，进入自动拍照", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(AutoPhotoActivity.this, MasterActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.putExtra("autophotoview","prepare");
                intent.putExtra("username",userName);
                startActivity(intent);
                finish();


            }
        });

    }

    private void initCheckBoxListenes() {
        boolean autofocus = false;
        Object autofocus1 = SPUtils.get(getApplicationContext(), "autofocus", false);
        if (autofocus1 != null) {
            autofocus = (boolean) autofocus1;
        }
        boolean synthetic = false;
        Object synthetic1 = SPUtils.get(getApplicationContext(), "synthetic", false);
        if (synthetic1 != null) {
            synthetic = (boolean) synthetic1;
        }
        mAutofocus.setChecked(autofocus);
        mSynthetic.setChecked(synthetic);

        whiteCheckBox.setChecked(false);
        blueCheckBox.setChecked(false);
        greenCheckBox.setChecked(false);
        purpleCheckBox.setChecked(false);
        Set<String> autoPhoto_light = SPUtils.getset(getApplicationContext(), "autoPhoto_light");
        if (autoPhoto_light != null) {
            checboxs = autoPhoto_light;
            Iterator iterator = autoPhoto_light.iterator();

            while (iterator.hasNext()) {
                String next = (String) iterator.next();
                switch (Integer.valueOf(next)) {
                    case 1:
                        whiteCheckBox.setChecked(true);
                        break;
                    case 2:
                        blueCheckBox.setChecked(true);
                        break;
                    case 3:
                        greenCheckBox.setChecked(true);
                        break;
                    case 4:
                        purpleCheckBox.setChecked(true);
                        break;
                }
            }
        }

        whiteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checboxs.add("1");
                    SPUtils.putset(getApplicationContext(), "autoPhoto_light", checboxs);
                } else {
                    checboxs.remove("1");
                    SPUtils.putset(getApplicationContext(), "autoPhoto_light", checboxs);
                }
                String timerString = stopTimerString();
                if(timerString.contains("!")){
                    inTheEndTimer.setTextColor(Color.rgb(255,32,32));
                }else {
                    inTheEndTimer.setTextColor(Color.rgb(75,77,77));
                }
                inTheEndTimer.setText(timerString);
            }
        });
        blueCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checboxs.add("2");
                    SPUtils.putset(getApplicationContext(), "autoPhoto_light", checboxs);
                } else {
                    checboxs.remove("2");
                    SPUtils.putset(getApplicationContext(), "autoPhoto_light", checboxs);
                }
                String timerString = stopTimerString();
                if(timerString.contains("!")){
                    inTheEndTimer.setTextColor(Color.rgb(255,32,32));
                }else {
                    inTheEndTimer.setTextColor(Color.rgb(75,77,77));
                }
                inTheEndTimer.setText(timerString);
            }
        });
        greenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checboxs.add("3");
                    SPUtils.putset(getApplicationContext(), "autoPhoto_light", checboxs);
                } else {
                    checboxs.remove("3");
                    SPUtils.putset(getApplicationContext(), "autoPhoto_light", checboxs);
                }
                String timerString = stopTimerString();
                if(timerString.contains("!")){
                    inTheEndTimer.setTextColor(Color.rgb(255,32,32));
                }else {
                    inTheEndTimer.setTextColor(Color.rgb(75,77,77));
                }
                inTheEndTimer.setText(timerString);
            }
        });
        purpleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checboxs.add("4");
                    SPUtils.putset(getApplicationContext(), "autoPhoto_light", checboxs);
                } else {
                    checboxs.remove("4");
                    SPUtils.putset(getApplicationContext(), "autoPhoto_light", checboxs);
                }
                String timerString = stopTimerString();
                if(timerString.contains("!")){
                    inTheEndTimer.setTextColor(Color.rgb(255,32,32));
                }else {
                    inTheEndTimer.setTextColor(Color.rgb(75,77,77));
                }
                inTheEndTimer.setText(timerString);
            }
        });
        mAutofocus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPUtils.put(getApplicationContext(), "autofocus", true);
                } else {
                    SPUtils.put(getApplicationContext(), "autofocus", false);
                }
            }
        });
        mSynthetic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPUtils.put(getApplicationContext(), "synthetic", true);
                } else {
                    SPUtils.put(getApplicationContext(), "synthetic", false);
                }
                String timerString = stopTimerString();
                if(timerString.contains("!")){
                    inTheEndTimer.setTextColor(Color.rgb(255,32,32));
                }else {
                    inTheEndTimer.setTextColor(Color.rgb(75,77,77));
                }
                inTheEndTimer.setText(timerString);
            }
        });

    }

}

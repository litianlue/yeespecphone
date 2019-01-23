package com.geetion.microscope_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVLiveQuery;
import com.avos.avoscloud.AVLiveQueryEventHandler;
import com.avos.avoscloud.AVLiveQuerySubscribeCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.geetion.coreOneUtil.UIUtil;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.adapter.ClientAdapter;
import com.geetion.microscope_android.utils.ConstanUtil;
import com.geetion.microscope_android.widget.TouchImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 2018/1/2.
 */

public class SelectClientActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager mViewPage;
    private List<String> user = new ArrayList<>();
    private List<String> password = new ArrayList<>();
    private List<View> views = new ArrayList<>();
    private ClientAdapter adapter;
    private TextView add_tv;
    private String TAG = "SelectClientActivity";
    private TextView mtest;
    private TextView mtitle;
    private ImageView mImage;
    private AVLiveQuery doingLiveQuery;
    private ProgressBar mProgressBar;
    private final int UPDATEIMAGE=1;
    private final int DISS_PG=2;
    private final int SETEMPTY_IMAGE=3;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATEIMAGE:
                    if(msg.obj!=null)
                    mImage.setImageBitmap((Bitmap) msg.obj);
                    break;
                case DISS_PG:
                    mProgressBar.setVisibility(View.GONE);
                    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.requesagain);
                    mImage.setImageBitmap(bitmap1);
                    break;
                case SETEMPTY_IMAGE:
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.null_image);
                    mImage.setImageBitmap(bitmap);
                    break;
            }
        }
    };
    private Timer requestTimer;
    private TimerTask mTimerTask;
    private void requesTimer(final String user){
        if(requestTimer!=null){
            requestTimer.cancel();
            requestTimer =null;
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                AVQuery<AVObject> query = new AVQuery<>("ConnectState");
                query.whereEqualTo("user",user);
                query.getFirstInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject object, AVException e) {
                        object.put("heartbeat", true);
                        AVSaveOption option = new AVSaveOption();
                        option.query(new AVQuery<>("ConnectState").whereEqualTo("user", user));
                        option.setFetchWhenSave(true);
                        object.saveInBackground(option, new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {

                                } else {

                                }
                            }
                        });
                    }
                });
            }
        };
        requestTimer = new Timer();
        requestTimer.schedule(mTimerTask,0, 60*1000);//1秒后启动 每5秒查询一次
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_client_activity);
        mtest = ((TextView) findViewById(R.id.mbtn_return));
        add_tv = ((TextView) findViewById(R.id.adduser_tv));
        mtitle = ((TextView) findViewById(R.id.priview_timer));
        mImage = ((ImageView) findViewById(R.id.imageview));
        mProgressBar = ((ProgressBar) findViewById(R.id.loading));
        handler.removeMessages(DISS_PG);
        handler.sendEmptyMessageDelayed(DISS_PG,10000);
        mtest.setOnClickListener(this);
        add_tv.setOnClickListener(this);
        mViewPage = ((ViewPager) findViewById(R.id.mviewpage));
        for (int i = 0; i < 4; i++) {
            TouchImageView view = new TouchImageView(SelectClientActivity.this);
            views.add(view);
        }
        AVQuery<AVObject> query = new AVQuery<>("EquipmentConnectState");
        query.whereEqualTo("user", ConstanUtil.loginUser);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> list, AVException e) {

                if (e == null) {
                    try {
                        AVObject avObject = list.get(0);
                        JSONObject equipment = avObject.getJSONObject("equipment");
                        JSONObject equipment_one = avObject.getJSONObject("equipment_one");
                        JSONObject equipment_two = avObject.getJSONObject("equipment_two");
                        JSONObject equipment_tree = avObject.getJSONObject("equipment_tree");
                        JSONObject equipment_four = avObject.getJSONObject("equipment_four");
                        JSONObject equipment_five = avObject.getJSONObject("equipment_five");
                        if (equipment != null) {
                            user.add(equipment.getString("user"));
                            password.add(equipment.getString("password"));
                        }
                        if (equipment_one != null) {
                            user.add(equipment_one.getString("user"));
                            password.add(equipment_one.getString("password"));
                        }
                        if (equipment_two != null) {
                            user.add(equipment_two.getString("user"));
                            password.add(equipment_two.getString("password"));
                        }
                        if (equipment_tree != null) {
                            user.add(equipment_tree.getString("user"));
                            password.add(equipment_tree.getString("password"));
                        }
                        if (equipment_four != null) {
                            user.add(equipment_four.getString("user"));
                            password.add(equipment_four.getString("password"));
                        }
                        if (equipment_five != null) {
                            user.add(equipment_five.getString("user"));
                            password.add(equipment_five.getString("password"));
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    for (int i = 0; i < user.size(); i++) {

                    }
                    adapter = new ClientAdapter(SelectClientActivity.this, user, views, password);
                    adapter.setListenerCallback(new ClientAdapter.OnClienListenerCallback() {
                        @Override
                        public void mListenerCallback(final int position) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            AVUser.logInInBackground(user.get(position).trim(), password.get(position).trim(), new LogInCallback<AVUser>() {
                                @Override
                                public void done(AVUser avUser, AVException e) {
                                    Log.w(TAG,"e="+e);
                                    if (e == null) {
                                        AVQuery<AVObject> query = new AVQuery<>("ConnectState");
                                        query.whereEqualTo("user",user.get(position).trim());
                                        query.getFirstInBackground(new GetCallback<AVObject>() {
                                            @Override
                                            public void done(AVObject object, AVException e) {
                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("flage",true);
                                                    jsonObject.put("date",System.currentTimeMillis());
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                                object.put("phonestate", jsonObject);
                                                AVSaveOption option = new AVSaveOption();
                                                option.query(new AVQuery<>("ConnectState").whereEqualTo("user", user.get(position).trim()));
                                                option.setFetchWhenSave(true);
                                                object.saveInBackground(option, new SaveCallback() {
                                                    @Override
                                                    public void done(AVException e) {
                                                        if (e == null) {
                                                            mProgressBar.setVisibility(View.GONE);
                                                            if(requestTimer!=null){
                                                                requestTimer.cancel();
                                                                requestTimer =null;
                                                            }
                                                            Intent intent = new Intent(SelectClientActivity.this, MasterActivity.class);
                                                            intent.putExtra("username",user.get(position).trim());
                                                            startActivity(intent);
                                                            finish();

                                                        } else {
                                                            mProgressBar.setVisibility(View.GONE);
                                                            UIUtil.toast(SelectClientActivity.this,"登录失败");
                                                        }
                                                    }
                                                });
                                            }
                                        });

                                    } else {
                                        mProgressBar.setVisibility(View.GONE);
                                        UIUtil.toast(SelectClientActivity.this,"不存在该用户"+user.get(position));
                                    }
                                }
                            });
                        }
                    });
                    mViewPage.setAdapter(adapter);
                    mtitle.setText(user.get(0));
                    ConstanUtil.loginUser = mtitle.getText().toString().trim();
                    synchronousImage(user.get(0));
                    requesTimer(user.get(0));

                    mViewPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            Log.w(TAG, "position=" + position + "   positionOffset=" + positionOffset + "   positionOffsetPixels=" + positionOffsetPixels);
                        }

                        @Override
                        public void onPageSelected(int position) {
                            handler.removeMessages(DISS_PG);
                            handler.sendEmptyMessageDelayed(DISS_PG,7000);
                            mProgressBar.setVisibility(View.VISIBLE);
                            if (mtitle.getText().toString() != null)
                                closeService(mtitle.getText().toString());
                            mtitle.setText(user.get(position));
                            ConstanUtil.loginUser = mtitle.getText().toString().trim();
                            requesTimer(user.get(position));
                            openService(user.get(position));
                            synchronousImage(user.get(position));
                        }
                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * @param usernama
     */
    private void closeService(final String usernama) {
        AVQuery<AVObject> query = new AVQuery<>("ConnectState");
        query.whereEqualTo("user",usernama);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("flage",false);
                    jsonObject.put("date",System.currentTimeMillis());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                object.put("phonestate", jsonObject);
                AVSaveOption option = new AVSaveOption();
                option.query(new AVQuery<>("ConnectState").whereEqualTo("user", usernama));
                option.setFetchWhenSave(true);
                object.saveInBackground(option, new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {

                        } else {

                        }
                    }
                });
            }
        });
    }

    /**
     * @param usernama
     */
    private void openService(final String usernama) {
        AVQuery<AVObject> query = new AVQuery<>("ConnectState");
        query.whereEqualTo("user",usernama);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("flage",true);
                    jsonObject.put("date",System.currentTimeMillis());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                object.put("phonestate", jsonObject);
                AVSaveOption option = new AVSaveOption();
                option.query(new AVQuery<>("ConnectState").whereEqualTo("user", usernama));
                option.setFetchWhenSave(true);
                object.saveInBackground(option, new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {

                        } else {

                        }
                    }
                });
            }
        });
    }

    /**
     * @param username
     */
    private void synchronousImage(String username) {
        if (doingLiveQuery != null) {
            doingLiveQuery.unsubscribeInBackground(new AVLiveQuerySubscribeCallback() {
                @Override
                public void done(AVException e) {

                }
            });
        }
        handler.removeMessages(UPDATEIMAGE);
        handler.sendEmptyMessage(SETEMPTY_IMAGE);
        Log.w(TAG, "username=" + username);
        AVQuery<AVObject> doingQuery = new AVQuery<>("Parameter");

        doingQuery.whereEqualTo("user", username);
        doingQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> parseObjects, AVException parseException) {
                // 符合查询条件的 Todo
                Log.w(TAG, "符合查询条件的");
            }
        });

        doingLiveQuery = AVLiveQuery.initWithQuery(doingQuery);

        doingLiveQuery.setEventHandler(new AVLiveQueryEventHandler() {
            @Override
            public void done(AVLiveQuery.EventType eventType, AVObject avObject, List<String> updateKeyList) {
                // 事件回调，有更新后会调用此回调函数

            }
        });
        doingLiveQuery.subscribeInBackground(new AVLiveQuerySubscribeCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                    // 订阅成功
                    Log.w(TAG, "订阅成功");
                }
            }
        });
        doingLiveQuery.setEventHandler(new AVLiveQueryEventHandler() {
            @Override
            public void onObjectUpdated(AVObject avObject, List<String> updateKeyList) {
                Log.w(TAG, "updateKeyList=" + updateKeyList);
                final byte[] images = avObject.getBytes("image");
                mProgressBar.setVisibility(View.GONE);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.removeMessages(DISS_PG);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(images, 0, images.length);
                        Message message = new Message();
                        message.what = UPDATEIMAGE;
                        message.obj = bitmap;
                        handler.sendMessage(message);
                    }
                });


            }
        });


    }

    private PopupWindow popupWindow;

    /**
     * @param view
     */
    private void showAddUser(View view) {
        View contentview = LayoutInflater.from(this).inflate(R.layout.adduser_ppw, null);

        final EditText password = ((EditText) contentview.findViewById(R.id.password));
        final EditText username = ((EditText) contentview.findViewById(R.id.user_name));

        Button add_user = ((Button) contentview.findViewById(R.id.add_btn));
        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int user_size = user.size();
                if (user_size > 5) {
                    UIUtil.toast(SelectClientActivity.this, "添加设备已经达到极限");
                    return;
                }
                final String ps = password.getText().toString();
                final String un = username.getText().toString();
                for (int i = 0; i < user.size(); i++) {
                    String name = user.get(i);
                    if (un.equals(name)) {
                        UIUtil.toast(SelectClientActivity.this, "添加失败，已经添加该用户");
                        return;
                    }
                }
                AVUser avUser = new AVUser();
                avUser.logInInBackground(un, ps, new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if (e == null) {
                            updateEquipmentConnect(user_size, un, ps);

                        } else {
                            UIUtil.toast(SelectClientActivity.this, "添加失败");
                            popupWindow.dismiss();
                        }
                    }
                });
            }
        });
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int xPos = windowManager.getDefaultDisplay().getWidth() / 12 * 4;
        int yPos = windowManager.getDefaultDisplay().getHeight() / 2;
        popupWindow = new PopupWindow(contentview,
                xPos, yPos, true);
        popupWindow.setTouchable(true);

        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.toast_background));
        popupWindow.showAsDropDown(view);
    }

    /**
     * @param user_size
     * @param name
     */
    private void updateEquipmentConnect(final int user_size, final String name, final String password) {
        AVQuery<AVObject> query = new AVQuery<>("EquipmentConnectState");
        query.whereEqualTo("user",ConstanUtil.loginUser);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                if (e == null && object != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("user", name);
                        jsonObject.put("password", password);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    switch (user_size) {
                        case 1:
                            object.put("equipment_one", jsonObject);
                            break;
                        case 2:
                            object.put("equipment_two", jsonObject);
                            break;
                        case 3:
                            object.put("equipment_tree", jsonObject);
                            break;
                        case 4:
                            object.put("equipment_four", jsonObject);
                            break;
                        case 5:
                            object.put("equipment_five", jsonObject);
                            break;
                    }
                    AVSaveOption option = new AVSaveOption();
                    option.query(new AVQuery<>("EquipmentConnectState").whereEqualTo("user", ConstanUtil.loginUser));
                    option.setFetchWhenSave(true);
                    object.saveInBackground(option, new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                UIUtil.toast(SelectClientActivity.this, "添加成功,请重新登录");
                                Intent intent = new Intent(SelectClientActivity.this, SelectConnectActivity.class);
                                startActivity(intent);
                                finish();
                                popupWindow.dismiss();
                            } else {
                                UIUtil.toast(SelectClientActivity.this, "添加失败!");
                                popupWindow.dismiss();
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adduser_tv:
                showAddUser(v);
                break;
            case R.id.mbtn_return:
                Intent intent = new Intent(this, SelectConnectActivity.class);
                startActivity(intent);

                if (doingLiveQuery != null) {
                    doingLiveQuery.unsubscribeInBackground(new AVLiveQuerySubscribeCallback() {
                        @Override
                        public void done(AVException e) {

                        }
                    });
                }
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (doingLiveQuery != null) {
            doingLiveQuery.unsubscribeInBackground(new AVLiveQuerySubscribeCallback() {
                @Override
                public void done(AVException e) {

                }
            });
        }
        if(requestTimer!=null){
            requestTimer.cancel();
            requestTimer =null;
        }
        super.onDestroy();
    }
}

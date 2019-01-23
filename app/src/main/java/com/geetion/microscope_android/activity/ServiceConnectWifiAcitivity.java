package com.geetion.microscope_android.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geetion.coreOneUtil.UIUtil;
import com.geetion.microscope_android.R;
import com.geetion.microscope_android.adapter.MRecyclerviewAdapter;
import com.geetion.microscope_android.utils.DataThread;
import com.geetion.microscope_android.utils.SPUtils;
import com.geetion.microscope_android.utils.SocketNetUtils;
import com.geetion.receiver.WifiDirectBroadcastReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/1/22.
 */

public class ServiceConnectWifiAcitivity extends Activity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private MRecyclerviewAdapter mAdapter;
    private List peers = new ArrayList();
    private List<HashMap<String, String>> peersshow = new ArrayList();
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mFilter;
    private WifiP2pInfo info;
    private Button dis_cover;
    private Button un_discover;
    private Button stop_connect;
    private EditText mUser;
    private EditText mPassword;
    private Button mSend;

    private static ScheduledExecutorService connectDelay = Executors.newScheduledThreadPool(3);
    public static ScheduledExecutorService DiscoverDelay = Executors.newScheduledThreadPool(1);

    private OutputStream stream = null;
    private final String MESSAGE_HEAD = "message_head";
    public static final int READ_DATA = 1;
    public static final int READ_SOCKET_DATA = 2;
    public static final int LONGIN_OUT_TITMER = 3;
    public static final int DISCOVER_OUT_TITMER = 4;
    public static final int RE_DISCOVER = 5;//搜索查询
    public static boolean isConnectWifi = false;

    private int PORT = 8500;
    private String IP = "192.168.2.112";
    private boolean IsServer = false;
    private DataThread dataThread;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case READ_DATA:
                    String str = (String) msg.obj;
                    //Toast.makeText(ServiceConnectWifiAcitivity.this, "" + str, Toast.LENGTH_SHORT).show();
                    if (!TextUtils.isEmpty(str)) {
                        if (str.equals("已经链接上服务端")) {
                            IsServer = true;
                            SetButtonVisible();
                            UIUtil.toast(ServiceConnectWifiAcitivity.this, "请输入wifi密码进行链接");
                        }
                        if (str.indexOf("成功") != -1) {
                            handler.removeMessages(LONGIN_OUT_TITMER);
                            //UIUtil.toast(ServiceConnectWifiAcitivity.this, "已经配置成功！");
                            Toast.makeText(ServiceConnectWifiAcitivity.this, "已经配置成功！自动跳转登录页" , Toast.LENGTH_SHORT).show();
                            String ip = str.split("&")[1].trim();
                            if (ip != null)
                                SPUtils.put(ServiceConnectWifiAcitivity.this, "ip", ip);
                            Log.i("xyz","ip=="+ip);
                            Intent intent = new Intent(ServiceConnectWifiAcitivity.this, SelectConnectActivity.class);
                            startActivity(intent);
                            if(dataThread!=null) {
                                dataThread.closeService();
                                dataThread = null;
                            }
                            finish();

                        } else {
                            UIUtil.toast(ServiceConnectWifiAcitivity.this, "" + str);
                        }
                    }
                    break;
                case READ_SOCKET_DATA:

                    String connectinfo = (String) msg.obj;
                    handler.removeMessages(LONGIN_OUT_TITMER);
                    sendProgressBar.setVisibility(View.GONE);
                    if (connectinfo.indexOf("登录成功") != -1) {
                       // UIUtil.toast(ServiceConnectWifiAcitivity.this, "已经配置成功！");
                        Toast.makeText(ServiceConnectWifiAcitivity.this, "已经配置成功！自动跳转登录页！", Toast.LENGTH_SHORT).show();
                        String ip = connectinfo.split("&")[1].trim();
                        if (ip != null)
                            SPUtils.put(ServiceConnectWifiAcitivity.this, "ip", ip);
                        Log.i("xyz","ip=="+ip);
                        Intent intent = new Intent(ServiceConnectWifiAcitivity.this, SelectConnectActivity.class);
                        startActivity(intent);
                        if(dataThread!=null) {
                            dataThread.closeService();
                            dataThread = null;
                        }
                        finish();
                    } else {
                        UIUtil.toast(ServiceConnectWifiAcitivity.this, "" + connectinfo);
                    }
                    break;
                case LONGIN_OUT_TITMER:
                    SetButtonGone();
                    ReconnecStopDiscoverVISIBLE();
                    mProgressBar.setVisibility(View.GONE);
                    sendProgressBar.setVisibility(View.GONE);
                    UIUtil.toast(ServiceConnectWifiAcitivity.this, "账号或密码不正确？请重启服务端链接！");
                    break;
                case DISCOVER_OUT_TITMER:
                    mProgressBar.setVisibility(View.GONE);
                    ReconnecStopDiscoverVISIBLE();
                    UIUtil.toast(ServiceConnectWifiAcitivity.this, "搜索失败，重新搜索试试");
                    StopDiscoverPeers();
                    break;
                case RE_DISCOVER:
                    if(peersshow.size()<1) {
                        handler.sendEmptyMessageDelayed(RE_DISCOVER,10000);
                        DiscoverPeers();
                    }
                    break;
            }
        }
    };
    private LinearLayout mProgressBar;
    private ProgressBar sendProgressBar;
    private TextView reTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servie_connect_wifi);

        initView();
        initIntentFilter();
        initReceiver();
        DiscoverDelay.schedule(new Runnable() {
            @Override
            public void run() {
                DiscoverPeers();
            }
        },2000,TimeUnit.MILLISECONDS);
        handler.sendEmptyMessageDelayed(RE_DISCOVER,10000);
    }

    private void initView() {

        dis_cover = ((Button) findViewById(R.id.bt_discover));
        un_discover = ((Button) findViewById(R.id.bt_stopdiscover));
        stop_connect = ((Button) findViewById(R.id.bt_stopconnect));
        mUser = ((EditText) findViewById(R.id.user_text));
        mPassword = ((EditText) findViewById(R.id.possword_text));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mProgressBar = ((LinearLayout) findViewById(R.id.dicoverlayout));
        sendProgressBar = ((ProgressBar) findViewById(R.id.send_pb));
        reTurn = ((TextView) findViewById(R.id.btn_return));
        mSend = ((Button) findViewById(R.id.send_btn));
        dis_cover.setOnClickListener(this);
        un_discover.setOnClickListener(this);
        stop_connect.setOnClickListener(this);
        reTurn.setOnClickListener(this);
        mSend.setOnClickListener(this);


        mAdapter = new MRecyclerviewAdapter(peersshow);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager
                (this.getApplicationContext()));

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiManager.isWifiEnabled()) {
            String ssid = wifiInfo.getSSID();
            String substring = ssid.substring(1, ssid.length() - 1);
            mUser.setText(substring);
        } else {
            Toast.makeText(this, "清先链接wifi", Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }

    private void initIntentFilter() {

        mFilter = new IntentFilter();
        mFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void initReceiver() {

        mManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);

        mChannel = mManager.initialize(this, Looper.myLooper(), null);

        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
        WifiP2pManager.PeerListListener mPeerListListerner = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peersList) {

                peers.clear();
                peersshow.clear();
                Collection<WifiP2pDevice> aList = peersList.getDeviceList();
                peers.addAll(aList);
                for (int i = 0; i < aList.size(); i++) {

                    WifiP2pDevice a = (WifiP2pDevice) peers.get(i);

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", a.deviceName);
                    map.put("address", a.deviceAddress);
                    peersshow.add(map);
                }
                mAdapter = new MRecyclerviewAdapter(peersshow);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager
                        (ServiceConnectWifiAcitivity.this));
                mAdapter.SetOnItemClickListener(new MRecyclerviewAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                      //  CreateConnect(peersshow.get(position).get("address"),
                        //        peersshow.get(position).get("name"));

                    }

                    @Override
                    public void OnItemLongClick(View view, int position) {

                    }
                });
            }
        };

        WifiP2pManager.ConnectionInfoListener mInfoListener = new WifiP2pManager.ConnectionInfoListener() {

            @Override
            public void onConnectionInfoAvailable(final WifiP2pInfo minfo) {

                info = minfo;

                if (info.groupFormed && info.isGroupOwner) {
                    Log.i("xyz","isGroupOwner=");
                   // Toast.makeText(ServiceConnectWifiAcitivity.this, "isGroupOwner", Toast.LENGTH_SHORT).show();
                    dataThread = new DataThread(ServiceConnectWifiAcitivity.this, handler);
                    dataThread.start();

                } else if (info.groupFormed) {
                    handler.removeMessages(DISCOVER_OUT_TITMER);
                   // sendString("已经链接上服务端");
                    connectDelay.schedule(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("xyz","run="+"已经链接上服务端");
                            if (socket == null) {
                                try {
                                    host = info.groupOwnerAddress.getHostAddress();
                                    socket = new Socket(host, port);
                                    socket.setSoTimeout(120*10000);
                                } catch (IOException e) {
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                }
                            }

                            try {
                                BufferedReader in;
                                BufferedReader input;
                                PrintWriter out;
                                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                out = new PrintWriter(socket.getOutputStream());
                                String line = "已经链接上服务端";
                                Log.i("xyz","line="+line);
                                if (!"bye".equals(line)) {
                                    out.println(line);
                                    out.flush();
                                    String echo = in.readLine();
                                    Message message = new Message();
                                    message.obj = echo;
                                    message.what = READ_DATA;
                                    handler.sendMessage(message);


                                }
                            } catch (Exception e) {
                            }

                        }
                    },500,TimeUnit.MILLISECONDS);
                    SetButtonVisible();
                    ServiceConnectWifiAcitivity.isConnectWifi = true;
                    mProgressBar.setVisibility(View.GONE);
                    handler.removeMessages(LONGIN_OUT_TITMER);
                    Toast.makeText(ServiceConnectWifiAcitivity.this, "请输入wifi密码进行链接", Toast.LENGTH_SHORT).show();
                }
            }
        };
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this, mPeerListListerner, mInfoListener);
    }

    private void CreateConnect(String address, final String name) {


        WifiP2pDevice device;
        WifiP2pConfig config = new WifiP2pConfig();
        Log.i("xyz", address);

        config.deviceAddress = address;
        /*mac地址*/

        config.wps.setup = WpsInfo.PBC;
        Log.i("address", "MAC IS " + address);
        if (address.equals("9a:ff:d0:23:85:97")) {
            config.groupOwnerIntent = 0;
            Log.i("address", "lingyige shisun");
        }
        if (address.equals("36:80:b3:e8:69:a6")) {
            config.groupOwnerIntent = 15;
            Log.i("address", "lingyigeshiwo");

        }

        Log.i("address", "lingyige youxianji" + String.valueOf(config.groupOwnerIntent));

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {


            }
        });
    }

    private void SetButtonVisible() {
        handler.removeMessages(DISCOVER_OUT_TITMER);
        handler.removeMessages(LONGIN_OUT_TITMER);
        sendProgressBar.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        ReconnecStopDiscoverVISIBLE();
        if (mSend.getVisibility() == View.GONE)
            mSend.setVisibility(View.VISIBLE);
        mSend.setTextColor(getResources().getColor(R.color.BLACK));
        mSend.setEnabled(true);

    }
    private void ReconnecStopDiscoverGONE(){
        stop_connect.setTextColor(getResources().getColor(R.color.gone_color));
        stop_connect.setEnabled(false);
        un_discover.setTextColor(getResources().getColor(R.color.gone_color));
        un_discover.setEnabled(false);
    }
    private void ReconnecStopDiscoverVISIBLE(){
        stop_connect.setTextColor(getResources().getColor(R.color.BLACK));
        stop_connect.setEnabled(true);
        un_discover.setTextColor(getResources().getColor(R.color.BLACK));
        un_discover.setEnabled(true);
    }
    private void SetButtonGone() {
        if (mSend.getVisibility() == View.GONE)
            mSend.setVisibility(View.VISIBLE);
        mSend.setTextColor(getResources().getColor(R.color.gone_color));
        mSend.setEnabled(false);

    }

    private void DiscoverPeers() {
        Log.i("xyz","DiscoverPeers");
        SetButtonGone();
        //ReconnecStopDiscoverGONE();
        handler.removeMessages(DISCOVER_OUT_TITMER);
        handler.sendEmptyMessageDelayed(DISCOVER_OUT_TITMER, 60 * 1000);
        mProgressBar.setVisibility(View.VISIBLE);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    private void StopDiscoverPeers() {
        mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {


            }
        });
    }

    private int reConnectCount = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_discover:

                DiscoverPeers();
                break;
            case R.id.bt_stopdiscover:
                mProgressBar.setVisibility(View.GONE);
                ReconnecStopDiscoverVISIBLE();
                StopDiscoverPeers();
                break;
            case R.id.bt_stopconnect:
                if(socket!=null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
              /*  if (reConnectCount > 3) {
                    UIUtil.toast(this, "你已经重试多次，请关闭服务端和手机端重试一下");
                    return;
                }*/
                isConnectWifi = false;
                StopConnect();
                DiscoverPeers();
               /* connectDelay.schedule(new Runnable() {
                    @Override
                    public void run() {
                        DiscoverPeers();
                    }
                }, 2000, TimeUnit.MILLISECONDS);*/
                reConnectCount++;
                break;
            case R.id.send_btn:
                final String user = mUser.getText().toString();
                final String password = mPassword.getText().toString();
                if (TextUtils.isEmpty(user) || TextUtils.isEmpty(password)) {
                    UIUtil.toast(this, "账号或密码不能为空");
                    return;
                }
                if (IsServer) {
                    connectDelay.schedule(new Runnable() {
                        @Override
                        public void run() {
                            dataThread.serverSendString(MESSAGE_HEAD + "&" + user + "&" + password + "&" + SocketNetUtils.getIPAddress(ServiceConnectWifiAcitivity.this) + "&" + SocketNetUtils.getWifiCipherType(ServiceConnectWifiAcitivity.this));
                        }
                    },0,TimeUnit.MILLISECONDS);
                    //dataThread.serverSendString(MESSAGE_HEAD + "&" + user + "&" + password + "&" + SocketNetUtils.getIPAddress(ServiceConnectWifiAcitivity.this) + "&" + SocketNetUtils.getWifiCipherType(ServiceConnectWifiAcitivity.this));
                } else {
                    sendString(MESSAGE_HEAD + "&" + user + "&" + password + "&" + SocketNetUtils.getIPAddress(this) + "&" + SocketNetUtils.getWifiCipherType(this));
                }
                SetButtonGone();
                sendProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                ReconnecStopDiscoverVISIBLE();
                mSend.setVisibility(View.GONE);
                handler.sendEmptyMessageDelayed(LONGIN_OUT_TITMER, 80 * 1000);
                break;
            case R.id.btn_return:
                Intent intent = new Intent(ServiceConnectWifiAcitivity.this, SelectConnectActivity.class);
                startActivity(intent);
                StopConnect();
                if(dataThread!=null) {
                    dataThread.closeService();
                    dataThread = null;
                }
                finish();
                break;

        }
    }

    private Socket socket;
    private int port = 8888;
    private String host;

    private void sendString(final String str) {
        Log.i("xyz","str="+str);

        connectDelay.schedule(new Runnable() {
            @Override
            public void run() {
                Log.i("xyz","run="+str);
                if(!socket.isConnected()){
                    Log.i("xyz","!socket.isConnected()");
                    try {
                        socket.close();
                        socket =null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                if (socket == null) {
                    try {
                        host = info.groupOwnerAddress.getHostAddress();
                        socket = new Socket(host, port);
                        socket.setSoTimeout(120*10000);
                    } catch (IOException e) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }

                try {
                    BufferedReader in;
                    BufferedReader input;
                    PrintWriter out;
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream());
                    String line = str;
                    Log.i("xyz","line="+line);
                    if (!"bye".equals(line)) {
                        out.println(line);
                        out.flush();
                        String echo = in.readLine();
                        Message message = new Message();
                        message.obj = echo;
                        message.what = READ_DATA;
                        handler.sendMessage(message);

                    }
                } catch (Exception e) {
                }

            }
        },500,TimeUnit.MILLISECONDS);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mFilter);
        dis_cover.setVisibility(View.GONE);
        //  un_discover.setVisibility(View.GONE);
        DiscoverDelay.schedule(new Runnable() {
            @Override
            public void run() {
                SocketNetUtils.ServerReceviedByTcp(PORT, handler);//开启socket服务监听链接成功后返回ip
            }
        },0,TimeUnit.MILLISECONDS);
     /*  new Thread(new Runnable() {
           @Override
           public void run() {

           }
       }).start();*/
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void setGroup() {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    private void StopConnect() {

        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(socket!=null){
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        handler.removeCallbacksAndMessages(null);

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        StopConnect();
        if(dataThread!=null) {

            dataThread.closeService();
            dataThread = null;
        }
    }


}

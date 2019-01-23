package com.geetion.microscope_android.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.geetion.microscope_android.activity.ServiceConnectWifiAcitivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static com.geetion.microscope_android.activity.ServiceConnectWifiAcitivity.DiscoverDelay;

/**
 * Created by Administrator on 2018/1/22.
 */

public class DataThread extends Thread {
    private Activity activity;
    private Handler handler;
    private  Socket client;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private boolean READ = true;
    private  ServerSocket serverSocket = null;

    public DataThread(Activity activity, Handler handler){
        this.activity = activity;
        this.handler = handler;
        if(serverSocket!=null){
            serverSocket = null;

        }

    }

    @Override
    public void run() {

        Log.i("xyz", "data doinback");


        try {
            serverSocket = new ServerSocket(8888);
            serverSocket.setSoTimeout(120*1000);
            Log.i("xyz","串口创建完成");
             client = serverSocket.accept();

            out = new PrintWriter(client.getOutputStream());
            Log.i("xyz","串口创建完成=");
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line = in.readLine();
            Log.i("xyz","串口创建完成=="+line);
            while(!"bye".equals(line)){

                System.out.println("client:"+line);
               // out.println("服务端应答");
               // out.flush();

                Log.i("xyz", "line="+line);
                sendMessage(line);
                if(line.indexOf("登录成功")!=-1)
                    break;
                if(client.isConnected())
                line = in.readLine();
                Log.i("xyz", "line1="+line);

            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendMessage(final String line) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str =line;
                Message message = new Message();
                message.what = ServiceConnectWifiAcitivity.READ_DATA;
                message.obj = str;
                handler.sendMessage(message);
            }
        }).start();

    }
    public void closeService(){
        if(serverSocket!=null){
            try {
                serverSocket.close();
                in.close();
                out.close();
                serverSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void serverSendString(String str){
        if(!client.isConnected()){
            Log.w("xyz", "!client.isConnected()="+str);
        }
        Log.i("xyz", "client="+client);
        Log.i("xyz", "str="+str);
        if(client!=null){

            try {
                out = new PrintWriter(client.getOutputStream());
                out.println(str);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

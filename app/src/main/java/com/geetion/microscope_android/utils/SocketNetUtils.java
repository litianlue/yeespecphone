package com.geetion.microscope_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Administrator on 2018/1/23.
 */

public class SocketNetUtils {

    public static void connectServerWithTCPSocket(String ip,int port) {
        Socket socket;
        try {// 创建一个Socket对象，并指定服务端的IP及端口号
            socket = new Socket(ip, port);
            /*// 创建一个InputStream用户读取要发送的文件。
            InputStream inputStream = new FileInputStream("e://a.txt");
            // 获取Socket的OutputStream对象用于发送数据。
            OutputStream outputStream = socket.getOutputStream();
            // 创建一个byte类型的buffer字节数组，用于存放读取的本地文件
            byte buffer[] = new byte[4 * 1024];
            int temp = 0;
            // 循环读取文件
            while ((temp = inputStream.read(buffer)) != -1) {
                // 把数据写入到OuputStream对象中
                outputStream.write(buffer, 0, temp);
            }
            // 发送读取的数据到服务端
            outputStream.flush();*/
            /** 或创建一个报文，使用BufferedWriter写入,看你的需求 **/
            String socketData = "[sldfhjwohfieowajpfsjad;l]";
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream()));
            writer.write(socketData.replace("\n", " ") + "\n");
            writer.flush();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void ServerReceviedByTcp(int port,Handler handler) {
        // 声明一个ServerSocket对象
        ServerSocket serverSocket = null;
        try {
            // 创建一个ServerSocket对象，并让这个Socket在1989端口监听
            serverSocket = new ServerSocket(port);
            // 调用ServerSocket的accept()方法，接受客户端所发送的请求，
            // 如果客户端没有发送数据，那么该线程就停滞不继续
            Socket socket = serverSocket.accept();
            // 从Socket当中得到InputStream对象
            InputStream inputStream = socket.getInputStream();
            byte buffer[] = new byte[1024 * 4];
            int temp = 0;
            // 从InputStream当中读取客户端所发送的数据
            while ((temp = inputStream.read(buffer)) != -1) {
                String s = new String(buffer, 0, temp);
                Message message = new Message();
                message.what = 2;
                message.obj = s;
                handler.sendMessage(message);
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }
    public static int getWifiCipherType(Context context){
        List<ScanResult> scanResults = new ArrayList<ScanResult>();
        WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = mWifiManager.getConnectionInfo();
        final List<ScanResult> results = mWifiManager.getScanResults();

        scanResults.addAll(results);
        int position=0;
        if(scanResults.size()<1)
            return 0;
        Log.w("type","info.getSSID()="+scanResults.get(position).SSID);
        for (int i = 0; i < scanResults.size(); i++) {
            String currentSSid =info.getSSID();
            if(scanResults.get(i).SSID.equals(currentSSid)){
                position = i;
                break;
            }
        }
        Log.w("type","scanResults.get(position)="+scanResults.get(position).SSID);
        ScanResult item = scanResults.get(position);
        if (item.capabilities.contains("WPA2") || item.capabilities.contains("WPA-PSK")) {
            return 0;
           // wiFiUtil.addWiFiNetwork(item.SSID, "12345678", WiFiUtil.Data.WIFI_CIPHER_WPA2);
        } else if (item.capabilities.contains("WPA")) {
            return 1;
            //wiFiUtil.addWiFiNetwork(item.SSID, "12345678", WiFiUtil.Data.WIFI_CIPHER_WPA);
        } else if (item.capabilities.contains("WEP")) {
            return 2;
                    /* WIFICIPHER_WEP 加密 */
           // wiFiUtil.addWiFiNetwork(item.SSID, "12345678", WiFiUtil.Data.WIFI_CIPHER_WEP);
        } else {
            return 3;
                    /* WIFICIPHER_OPEN NOPASSWORD 开放无加密 */
           // wiFiUtil.addWiFiNetwork(item.SSID, "", WiFiUtil.Data.WIFI_CIPHER_NOPASS);
        }


    }
    static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;

    static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }
    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}

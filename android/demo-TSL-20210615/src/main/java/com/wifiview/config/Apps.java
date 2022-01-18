package com.wifiview.config;

import android.app.Application;

import com.wifiview.nativelibs.CmdSocket;

/**
 * Created by tony on 2017/6/19.
 */

public class Apps extends Application {

    /**
     * 设备为AP模式时，ip地址为以下的默认ip，当变成station模式连接路由，则是路由器分配ip地址，需要搜索局域网获得
     */
    public static String defaultIpAddr = "192.168.10.123";
    public static CmdSocket cmdSocket;

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();

        cmdSocket = new CmdSocket(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        cmdSocket.stopSocket();
        cmdSocket = null;
    }
}

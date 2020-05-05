package com.deepanshu.whatsappdemo;

import android.app.Application;

import com.deepanshu.whatsappdemo.ConnectivityReceiver;

public class Myapplication extends Application {

    private static Myapplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized Myapplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
package com.deepanshu.whatsappdemo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.deepanshu.whatsappdemo.extraUtil.ConnectivityReceiver;
import com.deepanshu.whatsappdemo.interfaces.BaseInterface;
import com.deepanshu.whatsappdemo.R;

public class BaseActivity extends AppCompatActivity implements BaseInterface {

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                boolean isConnected = ConnectivityReceiver.isConnected();
                CheckConnectivity(isConnected);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    public void showdialog() {

    }

    @Override
    public void hideDialog() {

    }

    @Override
    public void CheckConnectivity(boolean isConnected) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        //Fail Safe
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


}

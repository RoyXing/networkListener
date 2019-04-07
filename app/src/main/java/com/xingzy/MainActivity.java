package com.xingzy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.xingzy.network.NetworkManger;
import com.xingzy.network.annotation.Network;
import com.xingzy.network.type.NetType;
import com.xingzy.network.utils.Constants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NetworkManger.getDefault().registerObserver(this);
    }

    @Network(netType = NetType.CMWAP)
    public void network(NetType netType) {
        switch (netType) {
            case WIFI:
                Toast.makeText(this, Constants.LOG_TAG + "onConnect" + netType.name(), Toast.LENGTH_SHORT).show();
                break;
            case CMNET:
            case CMWAP:
                Toast.makeText(this, Constants.LOG_TAG + "onConnect" + netType.name(), Toast.LENGTH_SHORT).show();
                break;
            case NONE:
                Toast.makeText(this, Constants.LOG_TAG + "onConnect" + netType.name(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManger.getDefault().unRegisterObserver(this);
//        NetworkManger.getDefault().unRegisterAllObserver();
    }

//    @Override
//    public void onConnect(NetType netType) {
//        Toast.makeText(this, Constants.LOG_TAG + "onConnect" + netType.name(), Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onDisConnect() {
//        Toast.makeText(this, Constants.LOG_TAG + "onDisConnect", Toast.LENGTH_SHORT).show();
//    }
}

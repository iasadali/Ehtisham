package com.ehtisham.bytesbank;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class WithdrawFragmentMain extends Fragment
{

    private ListView wifiList;
    SmsSender smsMgr;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;
    String attemptedSSID;
    AppDatabase db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        db=new AppDatabase(getActivity());
        smsMgr=new SmsSender();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        return inflater.inflate(R.layout.activity_withdraw, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        wifiList =getView().findViewById(R.id.wifiList);
        Button buttonScan = getView().findViewById(R.id.scanBtn);
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getActivity(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        CreateReceiver();
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                wifiManager.startScan();
            }
        });
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                attemptedSSID=String.valueOf(parent.getItemAtPosition(position));
                RequestKey();
                Toast.makeText(getActivity(), String.valueOf(parent.getItemAtPosition(position)), Toast.LENGTH_LONG).show();
            }
        });
        SmsReceiver.bindListener(new SmsListener()
        {
            @Override
            public void messageReceived(final String messageText, String contact) {
                Log.e("Message",messageText);
                if(messageText.contains(attemptedSSID))
                {
                    Log.d("KEY RECEIVED",messageText);
                    TryConnect(messageText);
                }
            }
        });
    }

    public void TryConnect(String content)
    {
        Log.d(">>>>>>>>>>>>>","Trying connect");
        db.SetRxPackets();
        db.SetSSID(attemptedSSID);
        String key=content.replace(attemptedSSID,"");
        Log.d(">>>>>>>>>>>>>","KEY: "+key);
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", attemptedSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);
        WifiManager  wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }


    public void RequestKey()
    {
        Toast.makeText(getActivity(),"Trying to connect: "+attemptedSSID , Toast.LENGTH_LONG).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                smsMgr.SendSMS(db.GetServerContact(),"BYTESBANK REQUEST "+attemptedSSID);
            }
        }, 2000);
    }

    public  void CreateReceiver()
    {
        receiverWifi = new WifiReceiver(wifiManager, wifiList,getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getActivity().registerReceiver(receiverWifi, intentFilter);
    }


}
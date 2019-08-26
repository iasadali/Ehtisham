package com.ehtisham.bytesbank;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

class WifiReceiver extends BroadcastReceiver {
    WifiManager wifiManager;
    StringBuilder sb;
    ListView wifiDeviceList;
    ArrayList<String> wifis;
    Context ctx;
    Boolean startedByService;


    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList,Context _ctx)
    {
        startedByService=false;
        ctx=_ctx;
        wifis=new ArrayList<String>();
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
    }

    public WifiReceiver(WifiManager wifiManager,Context _ctx)
    {
        startedByService=true;
        ctx=_ctx;
        wifis=new ArrayList<String>();
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<String> deviceList = new ArrayList<>();
            RefreshAps(wifiList);
            deviceList.clear();
            deviceList.addAll(wifis);
            if(!startedByService)
            {
                ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList.toArray());
                wifiDeviceList.setAdapter(arrayAdapter);
            }
        }
    }


    public void RefreshAps(List<ScanResult> wifiList)
    {
        for (ScanResult scanResult : wifiList)
        {
            if(scanResult.SSID.contains("BYTESBANK"))
            if(wifis.contains(scanResult.SSID)==false)
            {
                Notify(scanResult.SSID);
            }
        }
        wifis.clear();
        for (ScanResult scanResult : wifiList)
        {
            if(scanResult.SSID.contains("BYTESBANK"))
                wifis.add(scanResult.SSID);
        }
    }

    public  void Notify(String content)
    {
        Notification notification = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.key_icon)
                .setContentTitle(content+" is Available")
                .setContentText("free wifi is waiting...")
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
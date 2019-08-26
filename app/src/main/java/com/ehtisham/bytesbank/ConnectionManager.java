package com.ehtisham.bytesbank;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


public class ConnectionManager
{
    Context ctx;
    ConnectivityManager connManager;
    WifiManager mgr;
    NetworkInfo mWifi;
    WifiInfo info;

    public ConnectionManager(Context _ctx)
    {
        ctx=_ctx;
        connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mgr= (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
    }

    public  Boolean isConnected()
    {
        if (mWifi.isConnected())
        {
            return true;
        }
        return false;
    }

    public String GetConnectionSSID()
    {
        return  mgr.getConnectionInfo().getSSID().toString();
    }
}

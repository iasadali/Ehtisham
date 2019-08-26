package com.ehtisham.bytesbank;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BytesBankGuard extends Service
{
    public static final long NOTIFY_INTERVAL = 10 * 2000; // 2 seconds
    private Handler mHandler = new Handler();

    // timer handling
    AppDatabase db;
    private Timer mTimer = null;
    public  int counter=0;
    public Context mContext;
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;
    ConnectionManager mgr;


    @Override
    public  void onCreate()
    {
        super.onCreate();
        db=new AppDatabase(this);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        mgr=new ConnectionManager(this);
        CreateReceiver();
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    Log.d("<<<<<<<<<<","TIMER TASK");
                }
            }, 4000);

        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        // startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        startTimer();

        // Let it continue running until it is stopped.
        Toast.makeText(this, "BytesBank Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        Toast.makeText(this, "Server Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return  null;
    }
    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Count", "=========  "+ (counter++));
                if(mgr.isConnected())
                {
                    if(mgr.GetConnectionSSID().contains(db.GetSSID()))
                    {
                        db.Consume();
                        Log.d("<<<<<<<<<",mgr.GetConnectionSSID()+"--"+db.GetSSID());
                    }
                }
                wifiManager.startScan();
            }
        };
        timer.schedule(timerTask, 5000, 3000); //
    }

    public void stoptimertask()
    {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public  void CreateReceiver()
    {
        receiverWifi = new WifiReceiver(wifiManager, this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(receiverWifi, intentFilter);

    }


    public void UpdateConnections()
    {

    }
}

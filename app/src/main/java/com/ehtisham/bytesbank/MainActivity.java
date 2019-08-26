package com.ehtisham.bytesbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    AppDatabase db;
    boolean camera,location,storage,sms;
    Intent activityIntent;
    int PERMISSION_ALL = 1;
    Handler handler;
    String[] PERMISSIONS =
            {
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.WRITE_APN_SETTINGS,
                    Manifest.permission.INTERNET
            };


    void CheckPermissions()
    {

        camera=true;location=true;storage=true;sms=true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            camera=false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            storage=false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            sms=false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            location=false;
        }
        if(camera && location && storage && sms)
        {
            db.TryGetServerContact();
            handler.postDelayed(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(db.GetServerContact()!="")
                            {
                                if(db.IsLoggedIn())
                                {
                                    StartApplicationActivity();
                                }
                                else
                                {
                                    StartHomeActivity();
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"No Connection!\nTry again later." , Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    }, 1000);
        }
        else
        {
            StartPermissionActivity();
        }
    }


    protected  void StartPermissionActivity()
    {
        activityIntent=new Intent(this,PermissonActivity.class);
        this.startActivity(activityIntent);
    }


    protected  void StartHomeActivity()
    {
        activityIntent=new Intent(this,HomeActivity.class);
        this.startActivity(activityIntent);
    }

    protected  void StartApplicationActivity()
    {
        activityIntent=new Intent(this,ApplicationActivity.class);
        this.startActivity(activityIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db= new AppDatabase(getApplicationContext());

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CheckPermissions();
                }
            }, 1400);
    }
}

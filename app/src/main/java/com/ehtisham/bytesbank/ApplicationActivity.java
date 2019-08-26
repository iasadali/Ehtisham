package com.ehtisham.bytesbank;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class ApplicationActivity extends AppCompatActivity
{

    Intent mServiceIntent;
    private BytesBankGuard bytesbankService;
    AppDatabase db;
    Fragment fragmentWithdraw,fragmentDeposit,fragmentAccount;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return loadFragment(fragmentWithdraw);
                case R.id.navigation_dashboard:
                    return loadFragment(fragmentDeposit);
                case R.id.navigation_notifications:
                    return loadFragment(fragmentAccount);
            }


            return loadFragment(null);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        fragmentWithdraw=new WithdrawFragmentMain();
        fragmentDeposit=new DepositFragmentMain();
        fragmentAccount=new AccountFragmentMain();;
        loadFragment(new WithdrawFragmentMain());
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        StartService();

    }

    public void StartService()
    {
        bytesbankService = new BytesBankGuard();
        mServiceIntent = new Intent(this, bytesbankService.getClass());
        if (!isMyServiceRunning(bytesbankService.getClass())) {
            startService(mServiceIntent);
        }
    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        super.onDestroy();
    }



}

package com.ehtisham.bytesbank;

import android.telephony.SmsManager;

public class SmsSender
{
    SmsManager smsManager;
    public SmsSender()
    {
        smsManager = SmsManager.getDefault();
    }

    public  void SendSMS(String contact,String text)
    {
        smsManager.sendTextMessage(contact, null, text, null, null);
    }
}

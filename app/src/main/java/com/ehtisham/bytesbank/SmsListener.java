package com.ehtisham.bytesbank;

public interface SmsListener
{
    public void messageReceived(String messageText, String contact);
}

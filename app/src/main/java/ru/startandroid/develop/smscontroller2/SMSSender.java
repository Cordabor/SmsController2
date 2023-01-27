package ru.startandroid.develop.smscontroller2;

import android.telephony.SmsManager;

public class SMSSender {
    private SmsManager smsManager;

    public SMSSender() {
        smsManager = SmsManager.getDefault();
    }

    public void sendSMS(String phoneNumber, String message) {
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
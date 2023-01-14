package ru.startandroid.develop.smscontroller2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SMSReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Log.d("SMSReceiver", "onReceive method called");
        Bundle bundle= intent.getExtras();
        if (bundle != null) {
            Log.d("SMSReceiver", "bundle is not null");
// Получить объект PDU (Protocol Data Unit)
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
// Создать SmsMessage из PDU
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
// Получить текст сообщения
                String message = messages[i].getMessageBody();
                Log.d("SMSReceiver", "New SMS Received: " + message);
// Получить номер отправителя
                String sender = messages[i].getOriginatingAddress();
                Log.d("SMSReceiver", "Sender: " + sender);
// Получить текущую дату и время
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()).format(new Date());
                Log.d("SMSReceiver", "Timestamp: " + timestamp);
// Обновить UI
                updateUI(context, message, sender, timestamp);
            }
        }
    }
    private void updateUI(final Context context, String message, String sender, String timestamp){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("message", message);
        intent.putExtra("sender", sender);
        intent.putExtra("timestamp", timestamp);
        context.startActivity(intent);
    }
}
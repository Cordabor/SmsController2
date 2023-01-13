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

import java.util.ArrayList;

public class SMSReceiver extends BroadcastReceiver {
    ArrayList<String> smsMessages = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ListView smsListView;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Получить данные из intent
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages;
        String str = "";
        if (bundle != null) {
            // Получить pdus из bundle
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus.length];
            // Заполнить массив SmsMessage
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += messages[i].getMessageBody();
                smsMessages.add(str);
            }
            updateUI(context);
        }
    }

    private void updateUI(final Context context){
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                smsListView = (ListView) ((Activity) context).findViewById(R.id.sms_list_view);
                adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, smsMessages);
                smsListView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
        });

    }

}
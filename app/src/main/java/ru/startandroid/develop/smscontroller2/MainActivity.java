package ru.startandroid.develop.smscontroller2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> smsList = new ArrayList<>();
    private ListView smsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        smsListView = findViewById(R.id.sms_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsList);
        smsListView.setAdapter(adapter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSMSEvent(SMSEvent event) {
        // Обновляем UI
        updateSMSList(event.getMessage(), event.getSender(), event.getTimestamp());
    }

    private void updateSMSList(String message, String sender, String timestamp) {
        // Add new SMS to the list
        smsList.add(sender + ": " + message + " (" + timestamp + ")");
        // Create ArrayAdapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsList);
        // Set adapter to ListView
        smsListView.setAdapter(adapter);
    }
}
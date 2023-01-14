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

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    public Button sendButton;
    public EditText commandEditText;
    public ListView smsListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> smsData;

    private ServiceConnection serviceConnection;
    private CommandService commandService;

    // private ResponseReceiver responseReceiver;
    private SMSReceiver smsReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent.hasExtra("message")) {
            String message = intent.getStringExtra("message");
            String sender = intent.getStringExtra("sender");
            String timestamp = intent.getStringExtra("timestamp");
            updateSMSList(message, sender, timestamp);
        }
        smsListView = findViewById(R.id.sms_list_view);
        smsData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsData);
        smsListView.setAdapter(adapter);

        sendButton = findViewById(R.id.send_button);
        commandEditText = findViewById(R.id.command_edit_text);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String command = commandEditText.getText().toString();
                Intent serviceIntent = new Intent(MainActivity.this, CommandService.class);
                serviceIntent.putExtra("command", command);
                startService(serviceIntent);
            }
        });

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                CommandService.CommandBinder binder = (CommandService.CommandBinder) iBinder;
                commandService = binder.getService();
                commandService.setMainActivity(MainActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                commandService = null;
            }
        };

        intentFilter = new IntentFilter();
        // intentFilter.addAction(ResponseReceiver.ACTION_RESP);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        //  responseReceiver = new ResponseReceiver();
        //registerReceiver(responseReceiver, intentFilter);

        smsReceiver = new SMSReceiver();
        IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, smsFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(responseReceiver, intentFilter);
        if (smsReceiver != null) {
            registerReceiver(smsReceiver, intentFilter);
        }
        intentFilter.setPriority(100);
        bindService(new Intent(this, CommandService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(responseReceiver);
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
        unbindService(serviceConnection);
    }

    // add new method to update UI with new incoming SMS
    public void updateSMSList(String message, String sender, String timestamp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                smsData.add("Message: " + message + "\n" + "Sender: " + sender + "\n" + "Timestamp: " + timestamp);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
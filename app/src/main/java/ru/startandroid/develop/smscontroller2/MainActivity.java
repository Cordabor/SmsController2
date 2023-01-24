package ru.startandroid.develop.smscontroller2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ru.startandroid.develop.smscontroller2.CommandService.CommandCallback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity implements CommandCallback {

    private CommandCallback commandCallback;
    private ArrayAdapter<String> adapter;
    public ArrayList<String> smsList = new ArrayList<>();
    private ListView smsListView;
    private int selectedPosition = -1;


   public ArrayList<String> commandList;
    public ArrayAdapter<String> commandListAdapter;
    private ListView command_list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commandCallback = new CommandCallbackImpl();
        setContentView(R.layout.activity_main);
        smsListView = findViewById(R.id.sms_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsList);
        smsListView.setAdapter(adapter);
        EventBus.getDefault().register(this);
        CommandService.setMainActivity(this);
        CommandService commandService = new CommandService();
        commandService.setCommandCallback(this);

        commandList = new ArrayList<>();
        commandListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, commandList);
        command_list_view = findViewById(R.id.command_list_view);
        command_list_view.setAdapter(commandListAdapter);

        TCPServer server = new TCPServer(this, commandList, commandListAdapter);
        server.startListening();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSMSEvent(SMSEvent event) {
        String sms = event.getSender() + ": " + event.getMessage() + " " + event.getTimestamp();
        smsList.add(sms);
        adapter.notifyDataSetChanged();
        if (smsList.size() > 0) {
            sendCommand(sms);
        }
    }

    private void sendCommand(String sms) {
        selectedPosition++;
        if (selectedPosition >= smsList.size()) {
            selectedPosition = 0;
        }
        Intent intent = new Intent(MainActivity.this, CommandService.class);
        intent.putExtra("selected_position", selectedPosition);
        startService(intent);
    }

    private class CommandCallbackImpl implements CommandCallback {
        @Override
        public void onCommandSent() {

        }

        @Override
        public void onCommandSent(boolean isSuccess) {
            if (commandCallback != null) {
                commandCallback.onCommandSent(isSuccess);
            }
            if (isSuccess) {
                Log.d("CommandService", "Command sent successfully");
                Toast.makeText(MainActivity.this, "Command sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("CommandService", "Failed to send command");
                Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_SHORT).show();
            }
        }
    }
 //   @Subscribe(threadMode = ThreadMode.MAIN)
  //  public void onSMSEvent(SMSEvent event) {
       // String sms = event.getSender() + ": " + event.getMessage() + " [" + event.getTimestamp() + "]";
       // smsList.add(sms);
       // adapter.notifyDataSetChanged();
       // sendCommand(sms);
 //   }

  //  public void sendCommand(String sms) {
     //   Intent intent = new Intent(this, CommandService.class);
     //   intent.putExtra("sms", sms);
     //   startService(intent);
  //  }
    @Override
    public void onCommandSent() {

    }

    @Override
    public void onCommandSent(boolean isSuccess) {
        if (isSuccess) {
            Log.d("CommandService", "Command sent successfully");
            Toast.makeText(MainActivity.this, "Command sent successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("CommandService", "Failed to send command");
            Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_SHORT).show();
        }
    }



    public void updateUI(String message) {
        MainActivity.this.runOnUiThread(() -> {
            commandList.add(message);
            commandListAdapter.notifyDataSetChanged();
        });
    }


}
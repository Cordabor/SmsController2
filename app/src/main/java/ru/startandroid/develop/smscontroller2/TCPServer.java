package ru.startandroid.develop.smscontroller2;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


public class TCPServer {

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private boolean isListening = false;

    private MainActivity mainActivity;
    private ServerSocket serverSocket;
    private ArrayList<String> commandList;
    private ArrayAdapter<String> commandListAdapter;
    private int port = 59830;
  //int port = 8554;5555 24800
   // int port = 1194;
    // private String IP =  "192.168.1.23";
    //private String IP = "10.0.2.15";
  //  String IP = "100.96.1.3";

    public TCPServer(MainActivity mainActivity, ArrayList<String> commandList, ArrayAdapter<String> commandListAdapter) {
        this.mainActivity = mainActivity;
        this.commandList = commandList;
        this.commandListAdapter = commandListAdapter;
    }


    public void startListening() {
        new ListenTask(mainActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void stopListening() {
        try {
            serverSocket.close();
            isListening = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onMessageReceived(String message) {
        mainActivity.updateUI(message);
       // mainActivity.runOnUiThread(() -> {
         //   mainActivity.commandList.add(message);
          //  mainActivity.commandListAdapter.notifyDataSetChanged();
 //   });
    }

}

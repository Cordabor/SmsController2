package ru.startandroid.develop.smscontroller2;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;

import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
public class CommandService extends Service {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private static MainActivity mainActivity;

    private static final String MICROCONTROLLER_IP = "192.168.1.100";
    private static final int PORT = 1234;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public static void setMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getStringExtra("command");
        ConnectTask connectTask = new ConnectTask();
        connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, command);
        return START_STICKY;
    }

    public class CommandBinder extends Binder {
        public CommandService getService() {
            return CommandService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new CommandBinder();
    }

    private class ConnectTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... command) {
            String response = "";
            try {
                socket = new Socket(MICROCONTROLLER_IP, PORT);
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out.println(command[0]);
                response = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        // Метод onPostExecute выполняется после завершения doInBackground
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                socket.close();
                sendResult(response);
                updateUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendResult(String result) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra("result", result);
            sendBroadcast(broadcastIntent);
        }

        private void updateUI() {
            ListView smsListView = mainActivity.findViewById(R.id.sms_list_view);
            List<String> smsData = getSmsData();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, smsData);
            smsListView.setAdapter(adapter);
        }

        private List<String> getSmsData() {
            // return the data you want to display in the ListView
            List<String> smsData = new ArrayList<>();
            // Code to query the SMS content provider and retrieve the data
            Uri uri = Uri.parse("content://sms/inbox");
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            // Once you have the data, add it to the list
            if (cursor.moveToFirst()) {
                do {
                    String body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
                    smsData.add(body);
                } while (cursor.moveToNext());
            }
            cursor.close();
            // return the list of SMS data
            return smsData;
        }
    }

}
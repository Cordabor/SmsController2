package ru.startandroid.develop.smscontroller2;

import static android.content.ContentValues.TAG;

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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
public class CommandService extends Service{

    public interface CommandCallback {
       void onCommandSent();
       void onCommandSent(boolean isSuccess);
    }
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private static MainActivity mainActivity;


    private static final String MICROCONTROLLER_IP = "192.168.1.23";
    private static final int PORT = 59830;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public static void setMainActivity(MainActivity activity) {
        mainActivity = activity;
    }
    private CommandCallback commandCallback;

    public void setCommandCallback(CommandCallback callback) {
        this.commandCallback = callback;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int selectedPosition = intent.getIntExtra("selected_position", -1);
        if(selectedPosition != -1) {
            String command = mainActivity.smsList.get(selectedPosition);
            ConnectTask connectTask = new ConnectTask();
            connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, command);
            //Notify TCPServer that command has been sent
            mainActivity.server.startListening();
        }
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

    private class ConnectTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... strings) {
            String command = strings[0];
            try {
                Log.d("CommandService", "Connecting to server at " + MICROCONTROLLER_IP + ":" + PORT);
                socket = new Socket();
                socket.connect(new InetSocketAddress(MICROCONTROLLER_IP, PORT), 3000);
                Log.d("CommandService", "Connected to server");
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.d("CommandService", "Sending command: " + command);
                out.println(command);
                String response = in.readLine();
                Log.d("CommandService", "Received response: " + response);
                if (response != null && response.equals("OK")) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if (commandCallback != null) {
                commandCallback.onCommandSent(isSuccess);
            }
        }
    }


        // Метод onPostExecute выполняется после завершения doInBackground
      //  @Override
       // protected void onPostExecute(Boolean isConnected) {
          //  if (isConnected) {
            //    if (commandCallback != null) {
               //     commandCallback.onCommandSent();
              //  }
          //  } else {
                // Handle connection error
           // }
      //  }


        private void sendResult(String result) {
            Intent broadcastIntent = new Intent();
            // broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
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


    public void onCommandSent(boolean isSuccess) {
//TODO: Implement this method
        if (isSuccess) {
            Log.d("CommandService", "Command sent successfully to IP address");
        } else {
            Log.d("CommandService", "Command sent failed to IP address");
        }
    }

    }


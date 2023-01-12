package ru.startandroid.develop.smscontroller2;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;

import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStreamWriter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class CommandService extends Service {
   EditText command_edit_text;
    Button send_button;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private static final String MICROCONTROLLER_IP = "192.168.1.100";
    private static final int PORT = 1234;

    private final Handler handler = new Handler(Looper.getMainLooper());



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

    private class ConnectTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... command) {
            try {
                socket = new Socket(MICROCONTROLLER_IP, PORT);
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out.println(command[0]);
                final String response = in.readLine();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sendResult(response);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // Метод onPostExecute выполняется после завершения doInBackground
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                socket.close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Код для обновления UI
                        command_edit_text.setText("");
                        send_button.setEnabled(true);
                    }
                });
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
    }
}
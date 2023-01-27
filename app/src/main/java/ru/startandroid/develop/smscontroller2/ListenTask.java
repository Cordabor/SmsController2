package ru.startandroid.develop.smscontroller2;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenTask extends AsyncTask<Void, Void, Void> {
    private boolean isListening = false;
     int port = 1194;
     //String IP = "192.168.245.101";
     String IP = "100.96.1.3";
    private ServerSocket serverSocket;
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;

    public ListenTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private void onMessageReceived(String message) {
        mainActivity.updateUI(message);
        // mainActivity.runOnUiThread(() -> {
        //   mainActivity.commandList.add(message);
        //  mainActivity.commandListAdapter.notifyDataSetChanged();
        //   });
    }

    @Override
    public Void doInBackground(Void... voids) {
        if (!isListening) {
            isListening = true;
            // Код для прослушивания сообщений
            new Thread(() -> {
                try {
                    Log.d("TCPServer", "Starting server socket initialization");
                    serverSocket = new ServerSocket(port, 0, InetAddress.getByName(IP));
                    Log.d("TCPServer", "Server socket initialized and bound to " + IP + ":" + port);
                    while (true) {
                        Log.d("TCPServer", "Waiting for client connection");
                        Socket clientSocket = serverSocket.accept();
                        Log.d("TCPServer", "Client connected");
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String message;
                        while ((message = in.readLine()) != null) {
                            onMessageReceived(message);
                        }
                    }
                } catch (IOException e) {
                    Log.e("TCPServer", "IOException: " + e.getMessage());
                }
            }).start();
            isListening = false;
        }
        return null;
    }

}
package ru.startandroid.develop.smscontroller2;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.Socket;

public class ConnectTask extends AsyncTask<String, Void, Void> {
    // Переопределяем метод doInBackground, который будет выполняться
    // в отдельном потоке
    private Socket socket;
    private static final String MICROCONTROLLER_IP = "192.168.1.100";
    private static final int PORT = 80;
    @Override
    protected Void doInBackground(String... strings) {
        String command = strings[0];
        try {
            socket = new Socket(MICROCONTROLLER_IP, PORT);
            // другой код
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
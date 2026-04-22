package com.example.multithreading;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

/**
 * HandlerExample - демонстрирует использование Handler для общения между потоками
 * Handler позволяет безопасно обновлять UI из фоновых потоков
 */
public class HandlerExample {

    private TextView textView;
    private Handler handler;

    public HandlerExample(TextView tv) {
        this.textView = tv;
        // Создаем Handler в главном потоке
        this.handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    textView.append((String) msg.obj);
                }
            }
        };
    }

    public void executeTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 10; i++) {
                    String message = "Handler итерация " + i + "\n";

                    // Создаем Message и отправляем его в Handler
                    Message msg = handler.obtainMessage(1, message);
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                handler.sendMessage(handler.obtainMessage(1, "Готово!\n"));
            }
        }).start();
    }
}

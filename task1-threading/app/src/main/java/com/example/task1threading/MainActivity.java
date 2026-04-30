package com.example.task1threading;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView counterText;
    private TextView statusText;
    private TextView executorResultText;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile boolean workerRunning;
    private Thread counterThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counterText = findViewById(R.id.counterText);
        statusText = findViewById(R.id.statusText);
        executorResultText = findViewById(R.id.executorResultText);
        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button executorButton = findViewById(R.id.executorButton);

        startButton.setOnClickListener(view -> startCounterThread());
        stopButton.setOnClickListener(view -> stopCounterThread());
        executorButton.setOnClickListener(view -> runExecutorDemo());

        /*
         * Плохой вариант для демонстрации ANR:
         * нельзя блокировать UI-поток долгим циклом или Thread.sleep().
         *
         * while (true) {
         *     Thread.sleep(1000);
         * }
         */
    }

    private void startCounterThread() {
        if (workerRunning) {
            return;
        }
        workerRunning = true;
        statusText.setText("Thread is running");

        counterThread = new Thread(() -> {
            int counter = 0;
            while (workerRunning && !Thread.currentThread().isInterrupted()) {
                counter++;
                int value = counter;

                // Способ 1: Activity.runOnUiThread().
                runOnUiThread(() -> counterText.setText("Counter: " + value));

                // Способ 2: View.post() безопасно ставит Runnable в очередь UI-потока.
                counterText.post(() -> statusText.setText("Updated through View.post()"));

                // Способ 3: Handler, привязанный к Looper главного потока.
                mainHandler.postDelayed(
                        () -> executorResultText.setText("Handler tick: " + value),
                        100
                );

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "CounterThread");
        counterThread.start();
    }

    private void runExecutorDemo() {
        executorResultText.setText("ExecutorService is working...");
        executorService.execute(() -> {
            long sum = 0;
            for (int number = 1; number <= 2_000_000; number++) {
                sum += number;
            }
            long result = sum;
            mainHandler.post(() -> executorResultText.setText("Executor sum: " + result));
        });
    }

    private void stopCounterThread() {
        workerRunning = false;
        if (counterThread != null) {
            counterThread.interrupt();
            counterThread = null;
        }
        mainHandler.removeCallbacksAndMessages(null);
        statusText.setText("Thread is stopped");
    }

    @Override
    protected void onDestroy() {
        stopCounterThread();
        executorService.shutdownNow();
        super.onDestroy();
    }
}

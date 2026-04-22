package com.example.multithreading;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * MainActivity - главное окно приложения
 * Демонстрирует основные подходы к многопоточности в Android
 */
public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    private Button btnSimpleThread;
    private Button btnRunnable;
    private Button btnHandler;
    private Button btnThreadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация UI элементов
        resultTextView = findViewById(R.id.resultTextView);
        btnSimpleThread = findViewById(R.id.btnSimpleThread);
        btnRunnable = findViewById(R.id.btnRunnable);
        btnHandler = findViewById(R.id.btnHandler);
        btnThreadPool = findViewById(R.id.btnThreadPool);

        // Обработчик для простого потока (наследование Thread)
        btnSimpleThread.setOnClickListener(v -> startSimpleThread());

        // Обработчик для Runnable
        btnRunnable.setOnClickListener(v -> startRunnableThread());

        // Обработчик для Handler
        btnHandler.setOnClickListener(v -> startHandlerExample());

        // Обработчик для ThreadPool
        btnThreadPool.setOnClickListener(v -> startThreadPoolExample());
    }

    /**
     * Пример 1: Наследование класса Thread
     */
    private void startSimpleThread() {
        resultTextView.setText("Запущен простой поток...\n");

        CountingThread thread = new CountingThread(resultTextView);
        thread.start();
    }

    /**
     * Пример 2: Реализация интерфейса Runnable
     */
    private void startRunnableThread() {
        resultTextView.setText("Запущен поток через Runnable...\n");

        Thread thread = new Thread(new CountingRunnable(resultTextView));
        thread.start();
    }

    /**
     * Пример 3: Использование Handler для обновления UI
     */
    private void startHandlerExample() {
        resultTextView.setText("Используется Handler для обновления UI...\n");

        new HandlerExample(resultTextView).executeTask();
    }

    /**
     * Пример 4: ExecutorService (ThreadPool)
     */
    private void startThreadPoolExample() {
        resultTextView.setText("Запущен ThreadPool...\n");

        new ThreadPoolExample(resultTextView).runExample();
    }

    /**
     * Вспомогательный класс: Thread через наследование
     */
    private static class CountingThread extends Thread {
        private TextView textView;

        public CountingThread(TextView tv) {
            this.textView = tv;
        }

        @Override
        public void run() {
            StringBuilder text = new StringBuilder();
            for (int i = 1; i <= 10; i++) {
                text.append("Итерация ").append(i).append(" (Thread ID: ")
                    .append(Thread.currentThread().getId()).append(")\n");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Обновляем UI в главном потоке
            textView.post(() -> textView.append(text.toString()));
        }
    }

    /**
     * Вспомогательный класс: Runnable
     */
    private static class CountingRunnable implements Runnable {
        private TextView textView;

        public CountingRunnable(TextView tv) {
            this.textView = tv;
        }

        @Override
        public void run() {
            StringBuilder text = new StringBuilder();
            for (int i = 1; i <= 10; i++) {
                text.append("Runnable итерация ").append(i).append("\n");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            textView.post(() -> textView.append(text.toString()));
        }
    }
}

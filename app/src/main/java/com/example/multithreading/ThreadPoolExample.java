package com.example.multithreading;

import android.widget.TextView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ThreadPoolExample - демонстрирует использование ExecutorService (ThreadPool)
 * Позволяет эффективно управлять несколькими потоками
 */
public class ThreadPoolExample {

    private TextView textView;

    public ThreadPoolExample(TextView tv) {
        this.textView = tv;
    }

    public void runExample() {
        // Создаем пул из 3 потоков
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Запускаем 6 задач в пуле
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    long threadId = Thread.currentThread().getId();
                    String message = "Задача " + taskId + " выполняется в потоке " + threadId + "\n";

                    textView.post(() -> textView.append(message));

                    try {
                        Thread.sleep(2000); // Имитация работы
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    String completed = "Задача " + taskId + " завершена\n";
                    textView.post(() -> textView.append(completed));
                }
            });
        }

        // Завершаем пул
        executor.shutdown();
        textView.post(() -> textView.append("Все задачи запущены в ThreadPool\n"));
    }
}

package com.example.multithreading;

/**
 * SynchronizationExample - демонстрирует синхронизацию потоков
 * Включает примеры использования synchronized и классических паттернов
 */
public class SynchronizationExample {

    /**
     * Простой счетчик БЕЗ синхронизации (небезопасно)
     */
    public static class UnsafeCounter {
        private int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    /**
     * Безопасный счетчик СО ВЗАИМНОЙ ИСКЛЮЧЕНИЕМ (synchronized)
     */
    public static class SynchronizedCounter {
        private int count = 0;

        public synchronized void increment() {
            count++;
        }

        public synchronized int getCount() {
            return count;
        }
    }

    /**
     * Пример Producer-Consumer паттерна (производитель-потребитель)
     */
    public static class ProducerConsumerQueue {
        private int[] queue = new int[10];
        private int head = 0;
        private int tail = 0;
        private int count = 0;

        public synchronized void produce(int item) throws InterruptedException {
            while (count == queue.length) {
                wait(); // Ждем, пока потребитель освободит место
            }

            queue[tail] = item;
            tail = (tail + 1) % queue.length;
            count++;

            notifyAll(); // Уведомляем потребителя
        }

        public synchronized int consume() throws InterruptedException {
            while (count == 0) {
                wait(); // Ждем, пока производитель положит элемент
            }

            int item = queue[head];
            head = (head + 1) % queue.length;
            count--;

            notifyAll(); // Уведомляем производителя
            return item;
        }
    }

    /**
     * Пример использования synchronized блока
     */
    public static class BlockSynchronizationExample {
        private static final Object lock = new Object();
        private static int sharedData = 0;

        public void updateSharedData() {
            synchronized (lock) {
                // Это критический раздел - только один поток в раз
                sharedData++;
                System.out.println("Обновлено: " + sharedData);
            }
            // Здесь поток может быть прерван
            doOtherWork();
        }

        private void doOtherWork() {
            // Некритическая работа
        }
    }

    /**
     * Демонстрация race condition (состояние гонки)
     */
    public static class RaceConditionDemo {
        private static volatile int counter = 0;

        public static void demonstrateRaceCondition() {
            // Запускаем несколько потоков, каждый увеличивает счетчик
            for (int i = 0; i < 10; i++) {
                new Thread(() -> {
                    for (int j = 0; j < 1000; j++) {
                        counter++; // Несинхронизированный доступ!
                    }
                }).start();
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Ожидаемо 10000, но из-за race condition будет меньше
            System.out.println("Результат: " + counter);
        }
    }

    /**
     * Пример использования volatile для видимости
     */
    public static class VolatileExample {
        private volatile boolean shouldStop = false;

        public void worker() throws InterruptedException {
            while (!shouldStop) {
                // Выполняем работу
                Thread.sleep(100);
            }
            System.out.println("Поток завершен");
        }

        public void stopWorker() {
            shouldStop = true; // Сигнал для остановки
        }
    }
}

# Практические примеры многопоточности

## Пример 1: Простой поток (Thread)

```java
class CountThread extends Thread {
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Итерация: " + i);
            try {
                Thread.sleep(1000); // Пауза 1 сек
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// Использование:
CountThread thread = new CountThread();
thread.start(); // Запуск в отдельном потоке
```

## Пример 2: Runnable

```java
class CountRunnable implements Runnable {
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Runnable: " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// Использование:
Thread thread = new Thread(new CountRunnable());
thread.start();

// Или через lambda (Java 8+):
new Thread(() -> {
    for (int i = 0; i < 10; i++) {
        System.out.println(i);
    }
}).start();
```

## Пример 3: Handler для обновления UI

```java
public class MyActivity extends AppCompatActivity {
    private Handler handler = new Handler(Looper.getMainLooper());
    
    private void loadDataInBackground() {
        new Thread(() -> {
            // Долгая операция
            String result = fetchDataFromServer();
            
            // Обновляем UI в главном потоке
            handler.post(() -> {
                textView.setText(result);
            });
        }).start();
    }
}
```

## Пример 4: ExecutorService (ThreadPool)

```java
// Создаем пул из 4 потоков
ExecutorService executor = Executors.newFixedThreadPool(4);

// Запускаем задачи
for (int i = 0; i < 10; i++) {
    final int taskId = i;
    executor.execute(() -> {
        System.out.println("Задача " + taskId + " выполняется");
        // Выполняем работу
    });
}

// Завершаем пул (ждет завершения всех задач)
executor.shutdown();

// Или если нужно прервать:
executor.shutdownNow();
```

## Пример 5: Синхронизированный доступ

```java
public class Counter {
    private int count = 0;
    
    // Синхронизированный метод
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}

// Или с блоком:
public class SharedResource {
    private static final Object lock = new Object();
    
    public void update() {
        synchronized(lock) {
            // Критический раздел
            // Только один поток в раз
        }
    }
}
```

## Пример 6: Producer-Consumer

```java
public class ProducerConsumer {
    private Queue<Integer> queue = new LinkedList<>();
    private final int MAX_SIZE = 10;
    
    public synchronized void produce(int item) throws InterruptedException {
        while (queue.size() == MAX_SIZE) {
            wait(); // Ждем, пока очередь не освободится
        }
        queue.add(item);
        System.out.println("Произведено: " + item);
        notifyAll();
    }
    
    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait(); // Ждем, пока в очереди появится элемент
        }
        int item = queue.poll();
        System.out.println("Потреблено: " + item);
        notifyAll();
        return item;
    }
}
```

## Пример 7: Volatile переменная

```java
public class Worker {
    private volatile boolean shouldStop = false;
    
    public void work() throws InterruptedException {
        while (!shouldStop) {
            // Выполняем работу
            Thread.sleep(100);
        }
        System.out.println("Работа завершена");
    }
    
    public void stop() {
        shouldStop = true; // Безопасна видимость в других потоках
    }
}
```

## Пример 8: CountDownLatch

```java
import java.util.concurrent.CountDownLatch;

public class LatchExample {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        
        // Запускаем 3 потока
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                System.out.println("Поток начал работу");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Поток завершен");
                latch.countDown(); // Уменьшаем счетчик
            }).start();
        }
        
        latch.await(); // Ждем, пока счетчик не станет 0
        System.out.println("Все потоки завершены");
    }
}
```

## Пример 9: Barrier (Барьер)

```java
import java.util.concurrent.CyclicBarrier;

public class BarrierExample {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3, () -> {
            System.out.println("Все потоки достигли барьера!");
        });
        
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                System.out.println("Поток " + Thread.currentThread().getId() + " ждет");
                try {
                    barrier.await(); // Ожидание на барьере
                    System.out.println("Поток " + Thread.currentThread().getId() + " прошел");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
```

## Пример 10: Semaphore (Семафор)

```java
import java.util.concurrent.Semaphore;

public class SemaphoreExample {
    private static Semaphore semaphore = new Semaphore(2); // 2 разрешения
    
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire(); // Получить разрешение
                    System.out.println("Поток вошел в критический раздел");
                    Thread.sleep(2000);
                    System.out.println("Поток выходит");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release(); // Освободить разрешение
                }
            }).start();
        }
    }
}
```

## Типичные ошибки

### ❌ Ошибка 1: Race Condition
```java
// Неправильно - race condition
private int counter = 0;
counter++; // Не атомарная операция!

// Правильно
private synchronized void increment() {
    counter++;
}
```

### ❌ Ошибка 2: Deadlock
```java
// Потоки блокируют друг друга
Thread 1: lock(A) -> wait for lock(B)
Thread 2: lock(B) -> wait for lock(A)
```

### ❌ Ошибка 3: Обновление UI из фонового потока
```java
// Неправильно
new Thread(() -> {
    textView.setText("Данные"); // ❌ Будет исключение!
}).start();

// Правильно
new Thread(() -> {
    runOnUiThread(() -> {
        textView.setText("Данные"); // ✅ Безопасно
    });
}).start();
```

---

**Дополнительные методы Thread:**
- `Thread.sleep(ms)` - Приостановить текущий поток
- `thread.join()` - Ждать завершения потока
- `thread.interrupt()` - Прервать поток
- `Thread.currentThread()` - Получить текущий поток
- `thread.getPriority()` / `setPriority(1-10)` - Приоритет потока

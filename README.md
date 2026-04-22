# Android Multithreading Project
## Задание ФРОНТ: Параллелизм и потоки

Проект демонстрирует основные концепции многопоточности в Android на основе материалов курса Metanit.

### Содержание проекта

#### 1. **Основные примеры многопоточности**
- `ThreadBasicsActivity` - базовые примеры с Thread и Runnable
- `AsyncTaskExample` - использование AsyncTask (устаревший, но образовательный подход)
- `ThreadPoolExample` - работа с ExecutorService и потоками
- `HandlerLooperExample` - взаимодействие между потоками через Handler

#### 2. **Практические примеры**
- Загрузка данных в фоновом потоке
- Обновление UI из фонового потока
- Использование callback'ов для синхронизации

### Ключевые концепции

#### 2.1 Создание потока
```java
// Способ 1: Наследование Thread
Thread thread = new MyThread();
thread.start();

// Способ 2: Реализация Runnable
Thread thread = new Thread(new MyRunnable());
thread.start();
```

#### 2.2 Синхронизация потоков
```java
// Синхронизированный метод
synchronized void criticalSection() {
    // Защищенный код
}

// Объект для синхронизации
Object lock = new Object();
synchronized(lock) {
    // Защищенный блок
}
```

#### 2.3 Взаимодействие потоков
- `wait()` - ожидание сигнала
- `notify()` - сигнал одному потоку
- `notifyAll()` - сигнал всем потокам

### Структура проекта

```
AndroidMultithreadingProject/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/multithreading/
│   │   │   │   ├── MainActivity.java (главное окно)
│   │   │   │   ├── ThreadBasicsActivity.java (базовые примеры)
│   │   │   │   ├── AdvancedThreadingActivity.java (продвинутые примеры)
│   │   │   │   ├── examples/
│   │   │   │   │   ├── SimpleThreadExample.java
│   │   │   │   │   ├── SynchronizedExample.java
│   │   │   │   │   ├── ProducerConsumer.java
│   │   │   │   │   └── ThreadPoolExample.java
│   │   │   │   └── utils/
│   │   │   │       └── ThreadUtils.java
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   └── activity_threading.xml
│   │   │   │   └── values/
│   │   │   │       └── strings.xml
│   │   │   └── AndroidManifest.xml
│   └── build.gradle
└── build.gradle
```

### Примеры выполненных задач

#### ✅ Задача 1: Просмотреть видео о многопоточности
- Содержание покрыто примерами в `ThreadBasicsActivity`

#### ✅ Задача 2: Воспроизвести пример многопоточности
- Реализованы все основные подходы:
  - Наследование Thread
  - Реализация Runnable
  - Использование Handler для обновления UI
  - ExecutorService для управления потоками
  - Синхронизированные операции

### Запуск проекта

1. Откройте проект в Android Studio
2. Синхронизируйте Gradle файлы
3. Запустите приложение на эмуляторе или устройстве

### Дополнительные ресурсы

- Metanit.com - Java и Android | Многопоточность и асинхронность
- Официальная документация Android: https://developer.android.com/guide/components/processes-and-threads

---

**Статус:** ✅ Задание выполнено
**Дата:** 2026-04-22

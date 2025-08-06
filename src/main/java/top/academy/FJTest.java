package top.academy; // Указывает пакет, в котором находится класс, для организации кода в проекте

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList; // Импорт класса ArrayList для работы с динамическими списками
import java.util.List; // Импорт интерфейса List для работы с коллекциями
//import java.util.Map; // Импорт интерфейса Map для работы с отображениями
import java.util.concurrent.ForkJoinPool; // Импорт ForkJoinPool для параллельного выполнения задач
import java.util.concurrent.RecursiveTask; // Импорт RecursiveTask для создания рекурсивных задач Fork/Join
import java.util.stream.Collectors; // Импорт Collectors для операций с потоками (Stream API)

public class FJTest { // Определяет основной класс FJTest, содержащий внутренние классы и публичные методы
    private static final int THRESHOLD = 10_000; // Константа, определяющая порог размера данных (10_000) для прекращения деления задач
    private static final int MAX_SPLITS = Math.min(Runtime.getRuntime().availableProcessors(), 8); // Константа для числа подзадач, ограниченная числом логических ядер (максимум 8)
    private static final ForkJoinPool pool = ForkJoinPool.commonPool(); // Создает общий пул ForkJoinPool для всех задач, чтобы избежать создания новых пулов

    // Внутренний класс для задач, возвращающих List<Integer> (для методов 3or5 и prime)
    private static class FJTestList extends RecursiveTask<List<Integer>> { // Определяет класс FJTestList, наследующий RecursiveTask с типом результата List<Integer>
        private final List<Integer> data; // Поле для хранения входного списка чисел
        private final String method; // Поле для хранения имени метода ("3or5" или "prime")

        public FJTestList(List<Integer> data, String method) { // Конструктор класса FJTestList
            this.data = data; // Инициализирует поле data входным списком
            this.method = method; // Инициализирует поле method именем метода
        }

        @Override
        protected List<Integer> compute() { // Переопределяет метод compute для выполнения задачи
            if (data.size() <= THRESHOLD) { // Проверяет, если размер данных меньше или равен порогу, обрабатывает задачу последовательно
                return switch (method) { // Использует switch-выражение для выбора метода обработки
                    case "3or5" -> data.stream() // Для метода "3or5" создает поток из данных
                            .filter(CommonMethods::isDivisibleBy3Or5) // Фильтрует числа, кратные 3 или 5
                            .collect(Collectors.toList()); // Собирает результаты в список List<Integer>
                    case "prime" -> data.stream() // Для метода "prime" создает поток из данных
                            .filter(CommonMethods::isPrime) // Фильтрует простые числа
                            .collect(Collectors.toList()); // Собирает результаты в список List<Integer>
                    default -> throw new IllegalArgumentException("Неизвестный метод: " + method); // Выбрасывает исключение для неизвестного метода
                }; // Завершает switch-выражение
            } else { // Если размер данных превышает порог, делит задачу на подзадачи
                int size = data.size(); // Сохраняет размер входных данных
                int chunkSize = Math.max(size / MAX_SPLITS, 1); // Вычисляет размер каждой подзадачи, минимум 1
                List<FJTestList> tasks = new ArrayList<>(); // Создает список для хранения подзадач
                for (int i = 0; i < size; i += chunkSize) { // Цикл для деления данных на части
                    int end = Math.min(i + chunkSize, size); // Определяет конец текущей части
                    FJTestList task = new FJTestList(data.subList(i, end), method); // Создает новую подзадачу для подсписка
                    tasks.add(task); // Добавляет подзадачу в список
                    task.fork(); // Запускает подзадачу в пуле Fork/Join
                }

                List<Integer> result = new ArrayList<>(); // Создает список для объединения результатов
                for (FJTestList task : tasks) { // Цикл по всем подзадачам
                    result.addAll(task.join()); // Объединяет результаты подзадач в общий список
                }
                return result; // Возвращает объединенный список
            }
        }
    }

    // Внутренний класс для задачи, возвращающей Double (для метода average)
    private static class FJTestDouble extends RecursiveTask<Double> { // Определяет класс FJTestDouble, наследующий RecursiveTask с типом результата Double
        private final List<Integer> data; // Поле для хранения входного списка чисел

        public FJTestDouble(List<Integer> data) { // Конструктор класса FJTestDouble
            this.data = data; // Инициализирует поле data входным списком
        }

        @NotNull
        @Override
        protected Double compute() { // Переопределяет метод compute для выполнения задачи
            if (data.size() <= THRESHOLD) { // Проверяет, если размер данных меньше или равен порогу, обрабатывает задачу последовательно
                return data.stream() // Создает поток из данных
                        .mapToDouble(Integer::doubleValue) // Преобразует числа в double
                        .average() // Вычисляет среднее арифметическое
                        .orElse(0.0); // Возвращает 0.0, если поток пустой
            } else { // Если размер данных превышает порог, делит задачу на подзадачи
                int size = data.size(); // Сохраняет размер входных данных
                int chunkSize = Math.max(size / MAX_SPLITS, 1); // Вычисляет размер каждой подзадачи, минимум 1
                List<FJTestDouble> tasks = new ArrayList<>(); // Создает список для хранения подзадач
                for (int i = 0; i < size; i += chunkSize) { // Цикл для деления данных на части
                    int end = Math.min(i + chunkSize, size); // Определяет конец текущей части
                    FJTestDouble task = new FJTestDouble(data.subList(i, end)); // Создает новую подзадачу для подсписка
                    tasks.add(task); // Добавляет подзадачу в список
                    task.fork(); // Запускает подзадачу в пуле Fork/Join
                }

                double sum = 0.0; // Переменная для накопления взвешенной суммы
                int totalSize = 0; // Переменная для подсчета общего размера подсписков
                for (FJTestDouble task : tasks) { // Цикл по всем подзадачам
                    double subAvg = task.join(); // Получает среднее значение подзадачи
                    int subSize = Math.min(chunkSize, size - totalSize); // Вычисляет размер текущего подсписка
                    sum += subAvg * subSize; // Добавляет взвешенное среднее к сумме
                    totalSize += subSize; // Увеличивает общий размер
                }
                return totalSize > 0 ? sum / totalSize : 0.0; // Возвращает среднее или 0.0, если данные пусты
            }
        }
    }

    // Внутренний класс для задачи, возвращающей Long (для метода same)
    private static class FJTestLong extends RecursiveTask<Long> { // Определяет класс FJTestLong, наследующий RecursiveTask с типом результата Long
        private final List<Integer> data; // Поле для хранения входного списка чисел

        public FJTestLong(List<Integer> data) { // Конструктор класса FJTestLong
            this.data = data; // Инициализирует поле data входным списком
        }

        @Override
        protected Long compute() { // Переопределяет метод compute для выполнения задачи
            if (data.size() <= THRESHOLD) { // Проверяет, если размер данных меньше или равен порогу, обрабатывает задачу последовательно
                return CollectionTest.sameCollectionStream(data); // Возвращает 0, если данные пусты
            } else { // Если размер данных превышает порог, делит задачу на подзадачи
                int size = data.size(); // Сохраняет размер входных данных
                int chunkSize = Math.max(size / MAX_SPLITS, 1); // Вычисляет размер каждой подзадачи, минимум 1
                List<FJTestLong> tasks = new ArrayList<>(); // Создает список для хранения подзадач
                for (int i = 0; i < size; i += chunkSize) { // Цикл для деления данных на части
                    int end = Math.min(i + chunkSize, size); // Определяет конец текущей части
                    FJTestLong task = new FJTestLong(data.subList(i, end)); // Создает новую подзадачу для подсписка
                    tasks.add(task); // Добавляет подзадачу в список
                    task.fork(); // Запускает подзадачу в пуле Fork/Join
                }

                long maxCount = 0; // Переменная для хранения максимальной частоты
                for (FJTestLong task : tasks) { // Цикл по всем подзадачам
                    maxCount = Math.max(maxCount, task.join()); // Находит максимум среди результатов подзадач
                }
                return maxCount; // Возвращает максимальную частоту
            }
        }
    }

    // Публичные методы для вызова задач
    public static List<Integer> dividers3or5FJ(@NotNull List<Integer> data) { // Метод для фильтрации чисел, кратных 3 или 5
        return pool.invoke(new FJTestList(data, "3or5")); // Вызывает задачу FJTestList с методом "3or5"
    }

    public static List<Integer> primeFJ(@NotNull List<Integer> data) { // Метод для фильтрации простых чисел
        return pool.invoke(new FJTestList(data, "prime")); // Вызывает задачу FJTestList с методом "prime"
    }

    public static double averageFJ(@NotNull List<Integer> data) { // Метод для вычисления среднего арифметического
        return pool.invoke(new FJTestDouble(data)); // Вызывает задачу FJTestDouble
    }

    public static long sameFJ(@NotNull List<Integer> data) { // Метод для нахождения максимального числа повторений
        return pool.invoke(new FJTestLong(data)); // Вызывает задачу FJTestLong
    }
} // Завершает определение класса FJTest
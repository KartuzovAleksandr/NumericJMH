package top.academy;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1, jvmArgs = {"-Xms4G", "-Xmx4G"})
@Warmup(iterations = 1)
@Measurement(iterations = 3)
public class BenchmarkNumeric {
    @Param({"1000000"})
    private int size;
    private List<Integer> dataList;
    private int[] dataArray;

    @Setup(Level.Iteration)
    public void setup() {
        Random r = new Random();
        int max = 1000;
        dataList = r.ints(size, 1, max).boxed().toList();
        dataArray = r.ints(size, 1, max).toArray();
        // Создаем ExecutorService с количеством потоков, равным числу логических ядер
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CFTest.setExecutor(executor);
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        // Закрываем ExecutorService после каждой итерации
        CFTest.shutdownExecutor();
    }

    //region числа, делящиеся на 3 или 5
    @Benchmark
    public int[] dividers3or5Array() { return ArrayTest.dividers3or5Array(dataArray); }
    @Benchmark
    public List<Integer> dividers3or5Collection() { return CollectionTest.dividers3or5Collection(dataList); }
    @Benchmark
    public List<Integer> dividers3or5PStream() { return PStreamTest.dividers3or5PStream(dataList); }
    @Benchmark
    public List<Integer> dividers3or5CF() throws Exception { return CFTest.dividers3or5CF(dataList); }
    @Benchmark
    public List<Integer> dividers3or5FJ() { return FJTest.dividers3or5FJ(dataList); }
    //endregion

    //region простые числа
    @Benchmark
    public int[] primeArray() { return ArrayTest.primeArray(dataArray); }
    @Benchmark
    public List<Integer> primeCollection() { return CollectionTest.primeCollection(dataList); }
    @Benchmark
    public List<Integer> primePStream() { return PStreamTest.primePStream(dataList); }
    @Benchmark
    public List<Integer> primeCF() throws Exception { return CFTest.primeCF(dataList); }
    @Benchmark
    public List<Integer> primeFJ() { return FJTest.primeFJ(dataList); }
    //endregion

    //region среднее значение
    @Benchmark
    public double averageArray() { return ArrayTest.averageArray(dataArray); }
    @Benchmark
    public double averageCollection() { return CollectionTest.averageCollection(dataList); }
    @Benchmark
    public double averagePStream() { return PStreamTest.averagePStream(dataList); }
    @Benchmark
    public double averageCF() throws Exception { return CFTest.averageCF(dataList); }
    @Benchmark
    public double averageFJ() { return FJTest.averageFJ(dataList); }
    //endregion

    //region кол-во одинаковых чисел
    @Benchmark
    public long sameArray() { return ArrayTest.sameArray(dataArray); }
    @Benchmark
    public long sameCollection() { return CollectionTest.sameCollection(dataList); }
    @Benchmark
    public long samePStream() { return PStreamTest.samePStream(dataList); }
    @Benchmark
    public long sameCF() throws Exception { return CFTest.sameCF(dataList); }
    @Benchmark
    public long sameFJ() {
        return FJTest.sameFJ(dataList);
    }
    //endregion

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkNumeric.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
package top.academy;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class CFTest {
    private static ExecutorService executor;

    // Метод для установки ExecutorService из бенчмарка
    public static void setExecutor(ExecutorService exec) {
        executor = exec;
    }

    // Метод для закрытия ExecutorService (вызывается из @TearDown)
    public static void shutdownExecutor() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    public static List<Integer> dividers3or5CF(@NotNull List<Integer> collection) throws Exception {
        CompletableFuture<List<Integer>> future = CompletableFuture.supplyAsync(() ->
                        collection.stream()
                                .filter(CommonMethods::isDivisibleBy3Or5)
                                .collect(Collectors.toList()),
                executor);
        return future.get();
    }

    public static List<Integer> primeCF(@NotNull List<Integer> collection) throws Exception {
        CompletableFuture<List<Integer>> future = CompletableFuture.supplyAsync(() ->
                        collection.stream()
                                .filter(CommonMethods::isPrime)
                                .collect(Collectors.toList()),
                executor);
        return future.get();
    }

    public static double averageCF(@NotNull List<Integer> collection) throws Exception {
        CompletableFuture<Double> future = CompletableFuture.supplyAsync(() ->
                        collection.stream()
                                .mapToDouble(Integer::doubleValue)
                                .average()
                                .orElse(0.0),
                executor);
        return future.get();
    }

    public static long sameCF(@NotNull List<Integer> collection) throws Exception {
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            return CollectionTest.sameCollectionStream(collection);
        }, executor);
        return future.get();
    }
}
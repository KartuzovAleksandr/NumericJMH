package top.academy;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionTest {
    public static List<Integer> dividers3or5Collection(@NotNull List<Integer> collection) {
        List<Integer> result = new ArrayList<>(collection);
        result.removeIf(num -> !CommonMethods.isDivisibleBy3Or5(num));
        return result;
    }

    public static List<Integer> primeCollection(@NotNull List<Integer> collection) {
        List<Integer> result = new ArrayList<>(collection);
        result.removeIf(num -> !CommonMethods.isPrime(num));
        return result;
    }

    public static double averageCollection(@NotNull List<Integer> collection) {
        if (collection.isEmpty()) return 0.0;
        int sum = 0;
        for (int num : collection) {
            sum += num;
        }
        return (double) sum / collection.size();
    }

    // Использование Map.merge
    public static long sameCollection(@NotNull List<Integer> collection) {
        Map<Integer, Long> frequencyMap = new HashMap<>();
        for (Integer num : collection) {
            frequencyMap.merge(num, 1L, Long::sum);
        }

        long maxCount = 0;
        for (Long count : frequencyMap.values()) {
            maxCount = Math.max(maxCount, count);
        }
        return maxCount;
    }

    // Реализация со Stream API
    public static long sameCollectionStream(@NotNull List<Integer> collection) {
        Map<Integer, Long> frequencyMap = collection.stream()
                .collect(Collectors.groupingBy(num -> num, Collectors.counting()));
        return frequencyMap.values().stream()
                .max(Long::compare)
                .orElse(0L);
    }

    // Использование массива (для чисел в диапазоне [1, 1000])
    public static long sameCollectionArray(@NotNull List<Integer> collection) {
        int maxValue = 1000;
        long[] frequency = new long[maxValue + 1];

        for (Integer num : collection) {
            frequency[num]++;
        }

        long maxCount = 0;
        for (long count : frequency) {
            if (count > maxCount) {
                maxCount = count;
            }
        }
        return maxCount;
    }
}
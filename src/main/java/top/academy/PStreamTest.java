package top.academy;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PStreamTest {
    public static List<Integer> dividers3or5PStream(@NotNull List<Integer> collection) {
        return collection.parallelStream()
                .filter(CommonMethods::isDivisibleBy3Or5) // (x) -> CommonMethods.isDivisibleBy3Or5(x)
                .collect(Collectors.toList());
    }
    public static List<Integer> primePStream(@NotNull List<Integer> collection) {
        return collection.parallelStream()
                .filter(CommonMethods::isPrime)
                .collect(Collectors.toList());
    }
    public static double averagePStream(@NotNull List<Integer> collection) {
        return collection.parallelStream()
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);
    }
    public static long samePStream(@NotNull List<Integer> collection) {
        Map<Integer, Long> frequencyMap = collection.parallelStream()
                .collect(Collectors.groupingBy(num -> num, Collectors.counting()));
        return frequencyMap.values().parallelStream()
                .max(Long::compare)
                .orElse(0L);
    }
}
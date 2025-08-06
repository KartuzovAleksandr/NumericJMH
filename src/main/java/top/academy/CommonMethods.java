package top.academy;

public class CommonMethods {
    public static boolean isDivisibleBy3Or5(int number) {
        return number % 3 == 0 || number % 5 == 0;
    }
    public static boolean isPrime(int number) {
        if (number <= 1) return false;
        if (number == 2) return true;
        if (number % 2 == 0) return false;
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }
}

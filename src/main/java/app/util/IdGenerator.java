package app.util;

import java.util.UUID;

public class IdGenerator {
    public static String generateUserId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12);
    }

    public static String generateAccountNumber() {
        long number = (long) (Math.random() * 9_000_000_000L) + 1_000_000_000L;
        return String.valueOf(number);
    }
}

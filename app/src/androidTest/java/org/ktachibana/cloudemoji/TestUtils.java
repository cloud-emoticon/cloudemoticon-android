package org.ktachibana.cloudemoji;

import java.util.Random;

public class TestUtils {
    private static final int DEFAULT_RANDOM_STRING_COUNT = 5;

    public static String generateRandomString(int count) {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            char c = alphabet[random.nextInt(alphabet.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static String generateRandomString() {
        return generateRandomString(DEFAULT_RANDOM_STRING_COUNT);
    }

    public static long generateRandomLong() {
        return new Random().nextLong();
    }
}

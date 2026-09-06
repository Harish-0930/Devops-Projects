package com.nexusbank.customer.util;

import java.security.SecureRandom;

public class CustomerUniqueIdGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateUniqueId() {
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}

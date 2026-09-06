package com.nexusbank.customer.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountNumberGenerator {
    private static final AtomicInteger counter = new AtomicInteger(1000);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String generateAccountNumber() {
        String datePrefix = LocalDateTime.now().format(formatter);
        int seq = counter.incrementAndGet();
        return "NB" + datePrefix + String.format("%06d", seq);
    }
}
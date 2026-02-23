package com.mcms.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MonthlyReportTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MonthlyReport getMonthlyReportSample1() {
        return new MonthlyReport().id(1L).year(1).month(1).totalContaminationEvents(1).totalMissingFields(1);
    }

    public static MonthlyReport getMonthlyReportSample2() {
        return new MonthlyReport().id(2L).year(2).month(2).totalContaminationEvents(2).totalMissingFields(2);
    }

    public static MonthlyReport getMonthlyReportRandomSampleGenerator() {
        return new MonthlyReport()
            .id(longCount.incrementAndGet())
            .year(intCount.incrementAndGet())
            .month(intCount.incrementAndGet())
            .totalContaminationEvents(intCount.incrementAndGet())
            .totalMissingFields(intCount.incrementAndGet());
    }
}

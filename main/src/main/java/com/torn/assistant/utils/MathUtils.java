package com.torn.assistant.utils;

public class MathUtils {
    private MathUtils() {

    }

    public static Long calculateDifference(Long end, Long start) {
        if (start == null) {
            if (end != null) {
                return end;
            }
        }
        if (end == null) {
            return 0L;
        }
        return end - start;
    }

    public static Long sum(Long... stats) {
        long total = 0L;
        for (Long stat : stats) {
            if (stat != null) {
                total += stat;
            }
        }
        return total;
    }
}

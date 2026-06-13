package com.ecrea.maxint2147483647.util;

import com.ecrea.maxint2147483647.config.MaxIntConfig;

/**
 * アイテムスタック数のGUI表示形式を切り替えるユーティリティ。
 * Config (stackCountDisplayMode) に応じて、そのままの数字 もしくは
 * K/M/B/T... のような省略表記を返す。
 */
public final class NumberFormatUtil {

    private static final String[] SUFFIXES = {
            "", "K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No"
    };

    private NumberFormatUtil() {
    }

    public static String format(int count) {
        if (MaxIntConfig.countDisplayMode() == MaxIntConfig.CountDisplayMode.FULL_NUMBER) {
            return String.valueOf(count);
        }
        return abbreviate(count);
    }

    private static String abbreviate(long count) {
        if (count < 1000L) {
            return String.valueOf(count);
        }

        double value = count;
        int suffixIndex = 0;
        while (value >= 1000.0 && suffixIndex < SUFFIXES.length - 1) {
            value /= 1000.0;
            suffixIndex++;
        }

        if (value == Math.floor(value)) {
            return String.format("%.0f%s", value, SUFFIXES[suffixIndex]);
        }
        return String.format("%.1f%s", value, SUFFIXES[suffixIndex]);
    }
}

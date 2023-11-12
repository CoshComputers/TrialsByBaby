package com.dsd.tbb.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");

    /**
     * Returns the current timestamp in a standardized format.
     *
     * @return A string representing the current timestamp.
     */
    public static String getCurrentTimestamp() {
        return dateFormat.format(new Date());
    }
}

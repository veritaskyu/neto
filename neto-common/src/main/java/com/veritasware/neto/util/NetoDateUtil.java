package com.veritasware.neto.util;

import java.text.SimpleDateFormat;

/**
 * Created by chacker on 2016-11-04.
 */
public class NetoDateUtil {
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(System.currentTimeMillis());
    }
}

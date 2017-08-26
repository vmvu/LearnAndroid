package com.minhvu.proandroid.sqlite.database.Utils;

import android.icu.text.SimpleDateFormat;

import java.sql.Date;

/**
 * Created by vomin on 8/25/2017.
 */

public class DateTimeUtils {
    public static String longToStringDate(long timeMillis){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(timeMillis);
    }

}

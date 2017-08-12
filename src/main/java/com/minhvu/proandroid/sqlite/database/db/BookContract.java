package com.minhvu.proandroid.sqlite.database.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vomin on 8/1/2017.
 */

public class BookContract  {
    public static final String AUTHORITY = "com.minhvu.proandroid.sqlite.database";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY );

    private BookContract(){}
    public static final String path_book ="books";
    public static final class BookEntry implements BaseColumns{

        public static final String DATABASE_TABLE = "t_note";
        public static final String COLS_TITLE = "title";
        public static final String COLS_CONTENT = "content";
        public static final String COLS_CREATION_ON = "create_on";
        public static final String COLS_LASTUPDATE_ON = "lastupdate_on";
        public static final String COLS_COLOR = "color";
        public static final String COLS_COLOR_BACKGROUND = "color_background";
        public static final String COLS_REMINDER = "reminder_clock";
        public static final String COLS_PIN = "pin";
        public static final String COLS_PASSWORD = "password";
        public static final String COLS_PASSWORD_SALT = "p_salt";

        public static final String DEFAULT_SORT_ORDER = COLS_TITLE + " DESC";
        private BookEntry(){}

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(path_book).build();

        public static String[] getColumnNames(){
            return new String[]{_ID, COLS_TITLE, COLS_CONTENT, COLS_CREATION_ON, COLS_LASTUPDATE_ON,
                    COLS_COLOR, COLS_REMINDER, COLS_PIN, COLS_COLOR_BACKGROUND};
        }

        public static long getCurrentTime(){
            return System.currentTimeMillis();
        }

        public static Date getDate(long times){
            return new Date(times);
        }

        public static long fromDate(Date date){
            return date.getTime();
        }

        public static String dateToString(Date date){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            return dateFormat.format(date);
        }

        public static Date stringToDate(String sTime)  {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            Date date;
            try{
                date = format.parse(sTime);
            }catch (ParseException e){
                date =  null;
            }
            return date;
        }

    }
}

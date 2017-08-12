package com.minhvu.proandroid.sqlite.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vomin on 8/4/2017.
 */

public class BookDBHelper extends SQLiteOpenHelper {
    private static final String LOGTAG = "BookDBHelper";
    private static final String DATABASE_NAME = "bookDB.db";
    private static int DATABASE_VERSION = 1;


    public BookDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOGTAG, "onCreate");
        final String CREATE_DATABASE = "CREATE TABLE IF NOT EXISTS " + BookContract.BookEntry.DATABASE_TABLE + " ("
                + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.COLS_TITLE + " TEXT, "
                + BookContract.BookEntry.COLS_CONTENT + " TEXT, "
                + BookContract.BookEntry.COLS_CREATION_ON + " TEXT, "
                + BookContract.BookEntry.COLS_LASTUPDATE_ON + " TEXT, "
                + BookContract.BookEntry.COLS_COLOR + " INTEGER, "
                + BookContract.BookEntry.COLS_COLOR_BACKGROUND + " INTEGER, "
                + BookContract.BookEntry.COLS_REMINDER + " TEXT, "
                + BookContract.BookEntry.COLS_PIN + " INTEGER, "
                + BookContract.BookEntry.COLS_PASSWORD + " TEXT, "
                + BookContract.BookEntry.COLS_PASSWORD_SALT + " TEXT)";
        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOGTAG, "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + BookContract.BookEntry.DATABASE_TABLE);
        onCreate(db);
    }
}

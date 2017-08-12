package com.minhvu.proandroid.sqlite.database.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by vomin on 8/1/2017.
 */

public class BookProvider extends ContentProvider {
    public static final int INCOMING_BOOK_COLLECTION_URI_INDICATOR = 1;
    public static final int INCOMING_SIGNLE_BOOK_URI_INDICATOR = 100;
    private BookDBHelper mOpenHelper = null;

    private static UriMatcher uriMatcher = null;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BookContract.AUTHORITY, BookContract.path_book,
                INCOMING_BOOK_COLLECTION_URI_INDICATOR);
        uriMatcher.addURI(BookContract.AUTHORITY, BookContract.path_book + "/#",
                INCOMING_SIGNLE_BOOK_URI_INDICATOR);
    }
    private static HashMap<String, String> sBookProjectMap;
    static{
        sBookProjectMap = new HashMap<>();
        sBookProjectMap.put(BookContract.BookEntry._ID, BookContract.BookEntry._ID);
        sBookProjectMap.put(BookContract.BookEntry.COLS_TITLE , BookContract.BookEntry.COLS_TITLE  );
        sBookProjectMap.put(BookContract.BookEntry.COLS_CONTENT, BookContract.BookEntry.COLS_CONTENT);
        sBookProjectMap.put(BookContract.BookEntry.COLS_CREATION_ON, BookContract.BookEntry.COLS_CREATION_ON);
        sBookProjectMap.put(BookContract.BookEntry.COLS_LASTUPDATE_ON, BookContract.BookEntry.COLS_LASTUPDATE_ON);
        sBookProjectMap.put(BookContract.BookEntry.COLS_COLOR, BookContract.BookEntry.COLS_COLOR);
        sBookProjectMap.put(BookContract.BookEntry.COLS_COLOR_BACKGROUND, BookContract.BookEntry.COLS_COLOR_BACKGROUND);
        sBookProjectMap.put(BookContract.BookEntry.COLS_REMINDER, BookContract.BookEntry.COLS_REMINDER);
        sBookProjectMap.put(BookContract.BookEntry.COLS_PIN, BookContract.BookEntry.COLS_PIN);
        sBookProjectMap.put(BookContract.BookEntry.COLS_PASSWORD, BookContract.BookEntry.COLS_PASSWORD);
        sBookProjectMap.put(BookContract.BookEntry.COLS_PASSWORD_SALT, BookContract.BookEntry.COLS_PASSWORD_SALT);
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new BookDBHelper(getContext());

        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb =new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)){
            case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
                qb.setTables(BookContract.BookEntry.DATABASE_TABLE);
                qb.setProjectionMap(sBookProjectMap);
                break;
            case INCOMING_SIGNLE_BOOK_URI_INDICATOR:
                qb.setTables(BookContract.BookEntry.DATABASE_TABLE);
                qb.setProjectionMap(sBookProjectMap);
                qb.appendWhere(BookContract.BookEntry._ID + "=" + uri.getPathSegments().get(1));
                break;
        }
        String orderBy ;
        if(TextUtils.isEmpty(sortOrder)){
            orderBy = BookContract.BookEntry.DEFAULT_SORT_ORDER;
        }else{
            orderBy = sortOrder;
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
                return ContentResolver.CURSOR_DIR_BASE_TYPE +"/" + BookContract.AUTHORITY + "."+ BookContract.BookEntry.DATABASE_TABLE;
            case INCOMING_SIGNLE_BOOK_URI_INDICATOR:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"+ BookContract.AUTHORITY + "."+ BookContract.BookEntry.DATABASE_TABLE;
            default:
                throw new IllegalArgumentException("Unknown Uri"+ uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if(uriMatcher.match(uri) != INCOMING_BOOK_COLLECTION_URI_INDICATOR){
            throw new IllegalArgumentException("Unknown URI:"+uri);
        }
        ContentValues cv ;
        // xu ly du lieu nguon khong bi anh huong
        if(values != null){
            cv = new ContentValues(values);
        }else {
            cv = new ContentValues();
        }

        if(cv.containsKey(BookContract.BookEntry.COLS_TITLE) == false){
            throw new SQLException("Failed to insert row because Book Name is needed " + uri);
        }


        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        // parameter 2: khong the null cot nay
        long rowID = db.insert(BookContract.BookEntry.DATABASE_TABLE, BookContract.BookEntry.COLS_TITLE, cv);
        if(rowID < 0){
            throw  new SQLException("Failed to insert row into "+ uri);
        }
        Uri insertBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(insertBookUri, null);
        return insertBookUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String where ;
        switch (uriMatcher.match(uri)){
            case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
                where = selection;
                break;
            case INCOMING_SIGNLE_BOOK_URI_INDICATOR:
                String rowID = uri.getPathSegments().get(1);
                where = BookContract.BookEntry._ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ")" : "");
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri:"+ uri);
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int deletes = db.delete(BookContract.BookEntry.DATABASE_TABLE, where, selectionArgs);
        if(deletes < 0){
            throw new SQLException("Failed to delete row into "+ uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return deletes;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String where;
        switch (uriMatcher.match(uri)){
            case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
                where = selection;
                break;
            case  INCOMING_SIGNLE_BOOK_URI_INDICATOR:
                String rowID = uri.getPathSegments().get(1);
                where = BookContract.BookEntry._ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ")" : "");
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri);
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int updates = db.update(BookContract.BookEntry.DATABASE_TABLE, values, where, selectionArgs);
        if(updates < 0){
            throw new SQLException("Failed to update into " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updates;
    }


}

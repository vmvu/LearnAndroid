package com.minhvu.proandroid.sqlite.database.db;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vomin on 8/1/2017.
 */

public class Book implements Parcelable {
    public int id;
    public String title;
    public String content;
    public String lastUpdate;
    public String creationOn;
    public String backgroundColor;
    public String reminder_clock;
    public String password;
    public String password_salt;

    public Book(){}

    public Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        lastUpdate = in.readString();
        creationOn = in.readString();
        backgroundColor = in.readString();
        reminder_clock = in.readString();
        password = in.readString();
        password_salt = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(lastUpdate);
        dest.writeString(creationOn);
        dest.writeString(backgroundColor);
        dest.writeString(reminder_clock);
        dest.writeString(password);
        dest.writeString(password_salt);
    }
}

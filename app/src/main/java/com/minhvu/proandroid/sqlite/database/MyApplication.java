package com.minhvu.proandroid.sqlite.database;

import android.app.Application;
import android.content.Context;

/**
 * Created by vomin on 8/23/2017.
 */

public class MyApplication extends Application {
    public static volatile Context m_appContext  =null;
    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.m_appContext = this.getApplicationContext();
    }
}

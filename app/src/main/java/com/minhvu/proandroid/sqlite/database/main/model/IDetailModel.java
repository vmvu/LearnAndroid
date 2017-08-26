package com.minhvu.proandroid.sqlite.database.main.model;

/**
 * Created by vomin on 8/26/2017.
 */

public interface IDetailModel {
    void onDestroy(boolean isChangingConfiguration);
    void setDataSharePreference(String key, String content);
    String getDataSharePreference(String key);
}

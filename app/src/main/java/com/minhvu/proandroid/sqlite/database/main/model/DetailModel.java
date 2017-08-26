package com.minhvu.proandroid.sqlite.database.main.model;

import android.content.SharedPreferences;

import com.minhvu.proandroid.sqlite.database.main.presenter.IDetailPresenter;

/**
 * Created by vomin on 8/26/2017.
 */

public class DetailModel implements IDetailModel {
    IDetailPresenter mPresenter;
    SharedPreferences mPreferences = null;

    public DetailModel(SharedPreferences preferences){
        this.mPreferences = preferences;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if(!isChangingConfiguration){
            mPresenter = null;
            mPreferences = null;
        }
    }

    @Override
    public void setDataSharePreference(String key, String content) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, content);
        editor.apply();
    }

    @Override
    public String getDataSharePreference( String key) {
        return mPreferences.getString(key, null);
    }
}

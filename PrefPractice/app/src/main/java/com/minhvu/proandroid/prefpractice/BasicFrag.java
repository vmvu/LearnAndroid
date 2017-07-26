package com.minhvu.proandroid.prefpractice;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

/**
 * Created by vomin on 7/24/2017.
 */

public class BasicFrag extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.nested_screen_basicfrag);
    }
}

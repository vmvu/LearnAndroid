package com.minhvu.proandroid.prefpractice;

import android.content.res.Configuration;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

/**
 * Created by vomin on 7/24/2017.
 */

public class MainPreferenceActivity extends PreferenceActivity {
    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }
    public ActionBar getSupportActionBar(){
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar){
        getDelegate().setSupportActionBar(toolbar);
    }

    @NonNull
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu(){
        getDelegate().invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private AppCompatDelegate getDelegate(){
        if(mDelegate == null){
            mDelegate = AppCompatDelegate.create(this,null);
        }
        return mDelegate;
    }















    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    public static class Frag1 extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener{
        private static final String LOGTAG = Frag1.class.getSimpleName();
        private EditTextPreference pkgPref;
        private EditTextPreference emailPref;
        private ListPreference listPref;
        private MultiSelectListPreference pizzaPref;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(LOGTAG, "onCreate");
            addPreferencesFromResource(R.xml.main);
            listPref = (ListPreference)
                    findPreference(getResources().getString(R.string.flight_sort_option_key));
            pkgPref = (EditTextPreference)
                    findPreference(getResources().getString(R.string.package_name_preference_key));
            pkgPref.setSummary(pkgPref.getText());
            emailPref = (EditTextPreference)
                    findPreference(getResources().getString(R.string.alert_email_address_key));
            emailPref.setSummary(emailPref.getText());
            pizzaPref = (MultiSelectListPreference)
                    findPreference(getResources().getString(R.string.pizza_toppings_key));

        }


        public void onResume() {
            super.onResume();
            Log.d(LOGTAG, "onResume");
            setFlightOptionSummary(null);
            pizzaPref.setOnPreferenceChangeListener(this);
           // pkgPref.setOnPreferenceChangeListener(this);
            pkgPref.setSummary(pkgPref.getText());
            //emailPref.setOnPreferenceChangeListener(this);
            emailPref.setSummary(emailPref.getText());
            //listPref.setOnPreferenceChangeListener(this);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d(LOGTAG, "onPause");
            //listPref.setOnPreferenceChangeListener(null);
           // pkgPref.setOnPreferenceChangeListener(null);
            //emailPref.setOnPreferenceChangeListener(null);
            pizzaPref.setOnPreferenceChangeListener(null);
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.d(LOGTAG, "onPreferenceChange");
            String key = preference.getKey();
            if(getString(R.string.pizza_toppings_key).equals(key)){
                if(((Set<String>) newValue).size() > 4){
                    Toast.makeText(getActivity(),
                            "Too many toppings. No more than 4 please", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            return true;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(LOGTAG, "onSharedPreferenceChanged");
            if(key.equals(getString(R.string.package_name_preference_key))){
                pkgPref.setSummary(sharedPreferences.getString(key, ""));
            }
            if(key.equals(getString(R.string.alert_email_address_key))){
                emailPref.setSummary(sharedPreferences.getString(key, ""));
            }
            if(key.equals(getString(R.string.flight_sort_option_key))){
                setFlightOptionSummary(listPref.getValue());
            }
        }

        private void setFlightOptionSummary(String newValue){
            String setTo = newValue;
            if(setTo == null){
                setTo = listPref.getValue();
                String[] optionEntries = getResources().getStringArray(R.array.flight_sort_option);
                try{
                    listPref.setSummary("Currently set to " + optionEntries[listPref.findIndexOfValue(setTo)]);
                }catch (Exception e){
                    listPref.setSummary("Preference error: unknown value of listPref: "+ setTo);
                }
            }
        }
    }

    public static class Frag2 extends PreferenceFragment {
        private static String LOGTAG = Frag2.class.getSimpleName();
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.frag2);
        }
    }
}

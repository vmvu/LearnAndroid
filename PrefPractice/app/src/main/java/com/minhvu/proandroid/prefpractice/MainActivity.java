package com.minhvu.proandroid.prefpractice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Resources resources;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this,R.xml.main, false);

        resources = getResources();

        tv = (TextView) findViewById(R.id.text1);
        setOptionText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_prefs){
            Intent intent = new Intent(this, MainPreferenceActivity.class);
            this.startActivityForResult(intent, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setOptionText();
    }

    private void setOptionText(){
        String valueText;
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);

        String flight_option = sPref.getString(
                resources.getString(R.string.flight_sort_option_key),
                resources.getString(R.string.flight_sort_option_default_value));


        String[] optionEntries = resources.getStringArray(R.array.flight_sort_option);

        valueText = "option value is " + flight_option + " (" + optionEntries[Integer.parseInt(flight_option)] + ")";

        String[] optionValues = resources.getStringArray(R.array.flight_sort_options_values);
        int index = 0;
        for(; index < optionValues.length; index++){
            if(optionValues[index].equals(flight_option))
                break;
        }
        if(index < optionValues.length){
            valueText += "\n   ...or the other way to get it ("+optionEntries[index]+")";
        }
        valueText += "\nShow Airline: " +
                sPref.getBoolean("show_airline_column_pref", false);

        valueText += "\nAlert email address: " +
                sPref.getString("alert_email_address", "");

        valueText += "\nFavorite pizza toppings: " +
                sPref.getStringSet("pizza_toppings", null);

        // Now that we've built a long text message, display it
        tv.setText(valueText);
    }
}

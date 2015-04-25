package com.sunshine.nitin.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(LOG_TAG, "On stop!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "On Pause!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "On Resume!");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "On Create!");
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "On start!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "On Destroy!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        if (R.id.action_preferred_location == id) {
            String preferredLocation = getPreferences().getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri geoLocation = Uri.parse(String.format(Locale.ENGLISH, "geo:0,0?q=%s", preferredLocation));
            intent.setData(geoLocation);

            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);
            else
                Log.e(LOG_TAG, "Couldn't find activity ");

        }

        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

}

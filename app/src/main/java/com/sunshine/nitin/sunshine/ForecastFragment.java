package com.sunshine.nitin.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sunshine.nitin.enums.Metric;

import java.util.ArrayList;
import java.util.Locale;


/**
 * Fragment that deals with getting forecast information
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> adapter;

    private SharedPreferences preferences;


    public ForecastFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    public SharedPreferences getPreferences() {

        if(preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        return preferences;
    }

    /**
     * Calls the weather api to get the weather details
     */
    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(adapter);

        String postalCode = getPreferences().getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        String metric = getPreferences().getString(getString(R.string.pref_metric_key), Metric.CELCIUS.getCode());

        weatherTask.execute(postalCode, metric);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast,
                R.id.list_item_forecast_textview, new ArrayList<String>());

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = adapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }
}

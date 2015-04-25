package com.sunshine.nitin.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.sunshine.nitin.enums.Metric;
import com.sunshine.nitin.utils.WeatherDataParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nitin on 4/19/15.
 *
 * This task fetches forecast information using the open weather api
 */
public class FetchWeatherTask  extends AsyncTask<String, Void, String[]> {

    private static final String LOG_TAG = FetchWeatherTask.class.getName();

    private static final String urlBase = "api.openweathermap.org";

    private static final String REQUEST_METHOD = "GET";

    private static final String numDays = "7";

    private ArrayAdapter<String> adapter;

    public FetchWeatherTask(ArrayAdapter<String> adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if(result != null && result.length > 0) {
            Log.v(LOG_TAG, "forecast details fetched!");
            adapter.clear();
            adapter.addAll(Arrays.asList(result));
        }
    }

    private String[] getForecastDetails(String postalCode, boolean isFarenheit) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        String[] forecastDetails = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").authority(urlBase)
                    .appendPath("data").appendPath("2.5").appendPath("forecast").appendPath("daily")
                    .appendQueryParameter("mode", "json").appendQueryParameter("units", "metric")
                    .appendQueryParameter("cnt", numDays).appendQueryParameter("q", postalCode);

            Log.v(LOG_TAG, builder.build().toString());

            URL url = new URL(builder.build().toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n ");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            forecastDetails = WeatherDataParser.getWeatherDataFromJson(forecastJsonStr, Integer.parseInt(numDays), isFarenheit);
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error in networking " + e.getMessage());

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Parsing exception " + e.getMessage());
        }

        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("FetchWeatherTask", "Error closing stream", e);
                }
            }
        }

       return forecastDetails;
    }

    @Override
    protected String[] doInBackground(String... params) {
        String postalCode = params[0];
        String metric = params[1];
        boolean isFarenheit = false;
        try {
            if(Metric.FARENHEIT == Metric.getByCode(metric)) {
                isFarenheit = true;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Invalid metric " + metric);
            e.printStackTrace();
        }

        String[] weatherDetails = getForecastDetails(postalCode, isFarenheit);

        for(String s : weatherDetails) {
            Log.v(LOG_TAG, s);
        }

        return weatherDetails;
    }
}

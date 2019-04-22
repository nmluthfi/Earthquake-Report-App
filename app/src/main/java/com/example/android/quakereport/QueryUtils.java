package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Sample JSON response for a USGS query
     */

    // Tag for the log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /*
     *   Query the USGS dataset and return an {@link List<Earthquake>} object to represent
     *   a list of earthquake
     * */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {
        Log.v(LOG_TAG, "fetchEarthquakeData()");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }


        List<Earthquake> earthquakes = extractEarthquakes(jsonResponse);
        return earthquakes;

    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /*
     *  Make an HTTP request to the give URL and return a String as a the response
     * */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*in milisecond */);
            urlConnection.setConnectTimeout(15000 /*in milisecond */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            /*
             * If the request was succesfull (response code = 200)
             * then read the input  strwam and parsw the response
             */

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON result " + e);
        } finally {
            if (urlConnection != null)
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                urlConnection.disconnect();
            if (inputStream != null)
                inputStream.close();
        }

        return jsonResponse;
    }

    /*
     * Convert the {@link InputStream} into a String which contain the whole JSON response from
     * the server
     * */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Earthquake> extractEarthquakes(String earthquakeJSON) {

        // If the JSON String is empty or null, then return null
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        List<Earthquake> earthquakes = new ArrayList<>();
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.

            // Covert SAMPLE_JSON_RESPONSE into JSONObject
            JSONObject jsonObjectRoot = new JSONObject(earthquakeJSON);

            // Extract JSONArray features
            JSONArray features = jsonObjectRoot.getJSONArray("features");

            // Iterate through all features data
            for (int i = 0; i < features.length(); i++) {

                // get the data from features and store it in the variables f
                JSONObject currentEarthquake = features.getJSONObject(i);

                // get the node properties
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                // extract data inside JSONObject properties
                Double magnitude = properties.getDouble("mag");
                String place = properties.getString("place");
                Long time = properties.getLong("time");

                // Extract the value for the key called "url"
                String url = properties.getString("url");

                // Casting the all variable except String
                // String magnitudeInString = String.valueOf(magnitude);
                // String timeInString = String.valueOf(time);

                Earthquake earthquake = new Earthquake(magnitude, place, time, url);

                earthquakes.add(earthquake);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}

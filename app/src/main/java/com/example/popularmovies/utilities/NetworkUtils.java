package com.example.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIES_DB_URL = "http://api.themoviedb.org/3/movie/";

    final static String most_popular = "popular";
    final static String highest_rated = "top_rated";

    private static final String API_PARAM = "api_key";

    public enum Endpoint {
        POPULAR,
        TOP_RATED
    }

    public static URL buildURL(Endpoint endpoint, String api_key){

        String base_url = MOVIES_DB_URL;

        if(endpoint == Endpoint.POPULAR){
            base_url += most_popular;
        } else if (endpoint == Endpoint.TOP_RATED){
            base_url += highest_rated;
        }

        Uri builtUri = Uri.parse(base_url).buildUpon()
                .appendQueryParameter(API_PARAM, api_key)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch(MalformedURLException e){
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}

package com.example.popularmovies.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String MOVIES_DB_URL = "http://api.themoviedb.org/3/movie/";

    final static String most_popular = "popular";
    final static String highest_rated = "top_rated";
    final static String favorites = "favorites";
    final static String reviews = "reviews";
    final static String videos = "videos";

    private static final String API_PARAM = "api_key";

    public enum Endpoint {
        POPULAR,
        TOP_RATED,
        REVIEWS,
        VIDEOS;
    }
    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param endpoint The endpoint that will be used as part of the url
     * @param api_key The user's api key to be used as a query parameter (to have access to the data)
     * @return The URL to use to query the API server.
     */
    public static URL buildURL(Endpoint endpoint, String api_key, int movie_id){

        // we will build the url from the base url
        String base_url = MOVIES_DB_URL;

        // add the endpoint to the base url
        if(endpoint == Endpoint.POPULAR){
            base_url += most_popular;
        } else if (endpoint == Endpoint.TOP_RATED){
            base_url += highest_rated;
        } else if (endpoint == Endpoint.REVIEWS){
            base_url += movie_id + "/" + reviews;
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

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
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

    /**
     * This method checks if the network is online (if phone has internet access)
     * Uses code from: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     * @return returns true or false depending on if there is access to the internet or not
     */
    public static boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

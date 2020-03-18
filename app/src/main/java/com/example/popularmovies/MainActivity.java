package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        new FetchMovieTask().execute();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            URL moviesRequestUrl = NetworkUtils
                    .buildURL(NetworkUtils.Endpoint.TOP_RATED, getString(R.string.api_key));

            try {

                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                Log.d("JSON RESPONSE:", jsonMovieResponse);

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

            return null;
        }
    }
}

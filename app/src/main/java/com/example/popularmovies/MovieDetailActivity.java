package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.popularmovies.model.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Get the movie
        Intent intent = getIntent();
        Movie movie = (Movie) intent.getParcelableExtra(MOVIE);

        Log.d("MOVIE DETAIL TITLE: ", movie.getTitle());
    }
}

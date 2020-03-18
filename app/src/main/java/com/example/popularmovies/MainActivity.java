package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utilities.MovieDBJsonUtils;
import com.example.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private List<Movie> movieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 3);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        new FetchMovieTask().execute();
    }

    @Override
    public void onClick(Movie movie) {
        Context context = getApplicationContext();
        Toast.makeText(context, movie.getTitle(), Toast.LENGTH_SHORT).show();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            URL moviesRequestUrl = NetworkUtils
                    .buildURL(NetworkUtils.Endpoint.POPULAR, getString(R.string.api_key));

            try {

                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                Log.d("JSON RESPONSE:", jsonMovieResponse);

                // Parse JSON into a list of movies...
                movieData = MovieDBJsonUtils
                        .getMoviesFromJson(jsonMovieResponse);

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (movieData != null){
                mMovieAdapter.setMovieData(movieData);
            }
        }
    }
}

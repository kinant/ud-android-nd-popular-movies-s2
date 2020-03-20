package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

        // load default endopoint movie grid (most popular movies)
        new FetchMovieTask().execute("");
    }

    private void loadMovies(NetworkUtils.Endpoint endpoint){

        if(endpoint == NetworkUtils.Endpoint.POPULAR){
            setTitle("Most Popular Movies");
            new FetchMovieTask().execute("popular");
        }

        if(endpoint == NetworkUtils.Endpoint.TOP_RATED){
            setTitle("Highest Rated Movies");
            new FetchMovieTask().execute("highest_rated");
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_most_popular){
            // load most popular
            loadMovies(NetworkUtils.Endpoint.POPULAR);
        }

        if(id == R.id.action_highest_rated){
            // load highest rated
            loadMovies(NetworkUtils.Endpoint.TOP_RATED);
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            String endpointString = params[0];
            NetworkUtils.Endpoint endpointSelected = NetworkUtils.Endpoint.POPULAR;

            if(endpointString == "popular"){
                endpointSelected = NetworkUtils.Endpoint.POPULAR;
            } else if(endpointString == "highest_rated"){
                endpointSelected = NetworkUtils.Endpoint.TOP_RATED;
            }

            URL moviesRequestUrl = NetworkUtils
                    .buildURL(endpointSelected, getString(R.string.api_key));

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

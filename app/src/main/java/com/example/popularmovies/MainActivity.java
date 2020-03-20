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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utilities.MovieDBJsonUtils;
import com.example.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mNoInternet;

    private ProgressBar mLoadingIndicator;

    private List<Movie> movieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        mNoInternet = (TextView) findViewById(R.id.no_internet_tv);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 3);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        // load default endopoint movie grid (most popular movies)
        loadMovies(NetworkUtils.Endpoint.POPULAR);
    }

    private void loadMovies(NetworkUtils.Endpoint endpoint){

        new FetchMovieTask().execute(endpoint);

        if(endpoint == NetworkUtils.Endpoint.POPULAR){
            setTitle("Most Popular Movies");
        }

        if(endpoint == NetworkUtils.Endpoint.TOP_RATED){
            setTitle("Highest Rated Movies");
        }
    }

    private void showGrid(){
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideGrid(){
        mRecyclerView.setVisibility(View.GONE);
    }

    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.GONE);
        mNoInternet.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage(){
        mNoInternet.setVisibility(View.GONE);
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

    public class FetchMovieTask extends AsyncTask<NetworkUtils.Endpoint, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hideGrid();
            hideErrorMessage();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(NetworkUtils.Endpoint... endpoints) {

            if(NetworkUtils.isOnline()) {

                NetworkUtils.Endpoint endpointSelected = endpoints[0];

                URL moviesRequestUrl = NetworkUtils
                        .buildURL(endpointSelected, getString(R.string.api_key));

                try {

                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(moviesRequestUrl);

                    Log.d("JSON RESPONSE:", jsonMovieResponse);

                    // Parse JSON into a list of movies...
                    movieData = MovieDBJsonUtils
                            .getMoviesFromJson(jsonMovieResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                movieData = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            mLoadingIndicator.setVisibility(View.GONE);

            if (movieData != null){
                mMovieAdapter.setMovieData(movieData);
                showGrid();
            } else {
                showErrorMessage();
            }
        }
    }
}

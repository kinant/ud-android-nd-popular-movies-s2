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

import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utilities.AppExecutors;
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

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        mNoInternet = (TextView) findViewById(R.id.no_internet_tv);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // create the GridLayoutManager with a spanCount of 3 (to show 3 columns)
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 3);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        /*
         * The MovieAdapter is responsible for linking our movie data with the Views that
         * will end up displaying the movie data.
         */
        mMovieAdapter = new MovieAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieAdapter);

        // load default endopoint movie grid (most popular movies)
        loadMovies(NetworkUtils.Endpoint.POPULAR);

        // database
        mDb = AppDatabase.getInstance(this);

        // checkFavoriteMovies();
    }

    private void checkFavoriteMovies() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Movie> favoriteMovies = mDb.favoriteMovieDao().loadAllFavoriteMovies();
                Log.d("LOADING FAVORITES: ", "size is " + favoriteMovies.size());

                if(favoriteMovies.size() > 0){
                    hideErrorMessage();
                    showGrid();
                }

                for(int i = 0; i < favoriteMovies.size(); i++){
                    Log.d("FAV MOVIE: ", "id = " + favoriteMovies.get(i).getMovie_id());
                }

            }
        });
    }

    /**
     * This method is used to load the movies based on the endpoint the user
     * has selected
     *
     * @param endpoint  the endpoint selected by the user
     */
    private void loadMovies(NetworkUtils.Endpoint endpoint){

        // execute the FetchMovieTask Async task, passing along the endpoing
        new FetchMovieTask().execute(endpoint);

        // set the title for the UI
        if(endpoint == NetworkUtils.Endpoint.POPULAR){
            setTitle("Most Popular Movies");
        }

        if(endpoint == NetworkUtils.Endpoint.TOP_RATED){
            setTitle("Highest Rated Movies");
        }
    }

    private void loadFavoriteMovies() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieData = mDb.favoriteMovieDao().loadAllFavoriteMovies();
                mMovieAdapter.setMovieData(movieData);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showGrid();
                        hideErrorMessage();
                    }
                });
            }
        });
    }

    /**
     * This method is used to show the grid (once movies are successfully loaded)
     */
    private void showGrid(){
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method is used to hide the grid, for instance, when the user
     * selects another endpoint and the movies are refreshed or if
     * there is a network error and the movies are not loaded.
     */
    private void hideGrid(){
        mRecyclerView.setVisibility(View.GONE);
    }

    /**
     * This method is used to show the error message and hide the grid
     */
    private void showErrorMessage(){
        hideGrid();
        mNoInternet.setVisibility(View.VISIBLE);
    }

    /**
     * This method is used to hide the error message
     */
    private void hideErrorMessage(){
        mNoInternet.setVisibility(View.GONE);
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks. Will start an activity to show the detail view of the movie that was clicked.
     *
     * @param movie The movie that was clicked (poster is clicked but we pass along the whole movie
     *              object.
     */
    @Override
    public void onClick(Movie movie) {

        // create the intent to show the MovieDetailActivity
        Intent intent = new Intent(this, MovieDetailActivity.class);

        // pass along the movie as an extra (the Movie object is parelable, so we are able to do this)
        intent.putExtra(MovieDetailActivity.MOVIE, movie);

        // start the MovieDetailActivity
        startActivity(intent);
    }

    /** This method is used to show the options/settings menu so the user
     * Can select between most popular and highest rated movies
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Use AppCompactActivity's method getMenuInflater to get a handle of the menu inflater
        MenuInflater inflater = getMenuInflater();

        // use the inflater's inflate method to inflate our menu to this menu
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    /** This method handles the selection of a menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* Check which menu item was selected and load the movies for the
            appropriate endpoint
         */
        if(id == R.id.action_most_popular){
            // load most popular
            loadMovies(NetworkUtils.Endpoint.POPULAR);
        }

        if(id == R.id.action_highest_rated){
            // load highest rated
            loadMovies(NetworkUtils.Endpoint.TOP_RATED);
        }
        if( id == R.id.action_favorites){
            setTitle("Favorite Movies");
            loadFavoriteMovies();
        }

        return super.onOptionsItemSelected(item);
    }

    /** Async Task that is used for checking the network status and then loading the
     * movies from the API.
     */
    public class FetchMovieTask extends AsyncTask<NetworkUtils.Endpoint, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // we hide the grid and error and show the progress indicator
            hideGrid();
            hideErrorMessage();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(NetworkUtils.Endpoint... endpoints) {

            // first we check if the network is online
            if(NetworkUtils.isOnline()) {

                // if the network is online, then we proceed to get the endpoint
                NetworkUtils.Endpoint endpointSelected = endpoints[0];

                // build the url
                URL moviesRequestUrl = NetworkUtils
                        .buildURL(endpointSelected, getString(R.string.api_key), 0);

                try {

                    // get the JSON results from the network (The Movie Database API)
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(moviesRequestUrl);

                    // Parse JSON into a list of movies...
                    movieData = MovieDBJsonUtils
                            .getMoviesFromJson(jsonMovieResponse);

                    for(int i = 0; i < movieData.size(); i++){
                        movieData.get(i).printMovie();
                    }

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

            // hide the progress indicator
            mLoadingIndicator.setVisibility(View.GONE);

            // check that we have movie data and set them in the adapter and then show the grid
            if (movieData != null){
                mMovieAdapter.setMovieData(movieData);
                showGrid();
            } else {
                showErrorMessage();
            }
        }
    }
}

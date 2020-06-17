package com.example.popularmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utilities.AppExecutors;
import com.example.popularmovies.utilities.ImageSaver;
import com.example.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>{

    public static final String MOVIE = "movie";
    private static final int TMDB_API_LOADER = 22;

    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mVoterAvg;
    private TextView mPlot;

    private ImageView mPoster;
    private ImageView mFavoriteIcon;
    private Drawable mDrawable;

    private AppDatabase mDb;

    private Movie mMovie;

    // for bitmap saving
    private Target mTarget;
    private Bitmap mPosterBitmap;

    private boolean isFavorite = false;
    private boolean isFavoriteDB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Get references
        mTitle = (TextView) findViewById(R.id.title_tv);
        mReleaseDate = (TextView) findViewById(R.id.release_date_tv);
        mVoterAvg = (TextView) findViewById(R.id.vote_avg_tv);
        mPlot = (TextView) findViewById(R.id.plot_tv);
        mPoster = (ImageView) findViewById(R.id.d_poster_iv);
        mFavoriteIcon = (ImageView) findViewById(R.id.iv_favorite);

        // get drawable for favorite icon
        // https://www.semicolonworld.com/question/45887/change-drawable-color-programmatically
        mDrawable = ContextCompat.getDrawable(this,R.drawable.ic_heart);

        // Get the movie
        Intent intent = getIntent();
        mMovie = (Movie) intent.getParcelableExtra(MOVIE);

        // Set the image using Picasso
        // https://guides.codepath.com/android/Displaying-Images-with-the-Picasso-Library
        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mPoster.setImageBitmap(bitmap);
                mPosterBitmap = bitmap;
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.get()
                .load(mMovie.getPoster())
                .into(mTarget);

        // Set the text views
        mTitle.setText(mMovie.getTitle());
        mReleaseDate.setText(mMovie.getReleaseDate());
        mVoterAvg.setText(String.valueOf(mMovie.getVote_average()));
        mPlot.setText(mMovie.getPlot());

        // Database
        mDb = AppDatabase.getInstance(this);

        // set the title
        setTitle(mMovie.getTitle());

        // initialize the loader
        getSupportLoaderManager().initLoader(TMDB_API_LOADER, null, this);

        // check if movie is favorite
        checkIsFavorite();

        // load reviews and videos
        requestAPIReviewsAndVideos();
    }

    private void checkIsFavorite() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int isFavoriteInt = mDb.favoriteMovieDao().isMovieFavorite(mMovie.getMovie_id());

                isFavorite = (isFavoriteInt == 1) ? true : false;
                isFavoriteDB = isFavorite;
                changeFavoriteIconColor();
            }
        });
    }

    private void changeFavoriteIconColor(){
        if(isFavorite){
            mDrawable.setColorFilter(new
                    PorterDuffColorFilter(0xffF44336, PorterDuff.Mode.SRC_IN));
        } else {
            mDrawable.setColorFilter(new
                    PorterDuffColorFilter(0xffE91E63, PorterDuff.Mode.SRC_IN));
        }

        mFavoriteIcon.setImageDrawable(mDrawable);
    }

    public void toggleFavorite(View view) {
        isFavorite = !isFavorite;
        changeFavoriteIconColor();

        if(isFavorite){
            saveFavorite();
        } else {
            deleteFavorite();
        }
    }

    private void saveFavorite(){
        Log.d("ACT: ", "saving step 1");
        if(isFavorite && !isFavoriteDB){
            Log.d("ACT: ", "saving step 2");
            if(mPosterBitmap != null) {
                Log.d("ACT: ", "saving step 3");
                // save image
                new ImageSaver(getApplicationContext())
                        .setFileName(mMovie.getMovie_id() + ".png")
                        .setDirectoryName("favorite_movies")
                        .save(mPosterBitmap);
            }
            // save move information
            Log.d("ACT: ", "saving step 4");
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteMovieDao().insertFavoriteMovie(mMovie);
                }
            });
        }
    }

    private void deleteFavorite(){
        if(isFavoriteDB && !isFavorite) {
            // delete from db
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteMovieDao().deleteFavoriteMovie(mMovie.getMovie_id());
                }
            });

            Log.d("DETAIL: ", "should be attempting to delete!");
            // delete file
            new ImageSaver(getApplicationContext())
                    .setFileName(mMovie.getMovie_id() + ".png")
                    .setDirectoryName("favorite_movies")
                    .delete();
        }
    }

    private void requestAPIReviewsAndVideos(){
        URL videosQueryURL = NetworkUtils.buildURL(NetworkUtils.Endpoint.REVIEWS, getString(R.string.api_key), mMovie.getMovie_id());

        Bundle queryBundle = new Bundle();
        queryBundle.putString("reviews_url", videosQueryURL.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> reviewQueryLoader = loaderManager.getLoader(TMDB_API_LOADER);

        if(reviewQueryLoader == null){
            loaderManager.initLoader(TMDB_API_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(TMDB_API_LOADER, queryBundle, this);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                if(args == null){
                    return;
                }

                forceLoad();
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String queryURLString = args.getString("reviews_url");

                try {
                    URL tmdbAPIUrl = new URL(queryURLString);
                    String apiReviewResults = NetworkUtils.getResponseFromHttpUrl(tmdbAPIUrl);
                    Log.d("REV API: ", apiReviewResults);
                    return apiReviewResults;
                } catch (IOException e){
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if(data == null){
            // show error
        } else {
            Log.d("DATA: ", data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}

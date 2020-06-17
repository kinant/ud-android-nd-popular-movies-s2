package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utilities.AppExecutors;
import com.example.popularmovies.utilities.ImageSaver;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE = "movie";

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

        // check if movie is favorite
        checkIsFavorite();
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
    }

    // Use the on destroy activity lifecycle event to save or delete if a movie is favorited
    @Override
    protected void onDestroy() {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(isFavorite && !isFavoriteDB){

                    if(mPosterBitmap != null) {
                        new ImageSaver(getApplicationContext())
                                .setFileName(mMovie.getMovie_id() + ".png")
                                .setDirectoryName("favorite_movies")
                                .save(mPosterBitmap);
                        mDb.favoriteMovieDao().insertFavoriteMovie(mMovie);
                    }
                } else if(isFavoriteDB && !isFavorite) {
                    mDb.favoriteMovieDao().deleteFavoriteMovie(mMovie.getMovie_id());
                }
            }
        });

        super.onDestroy();
    }
}

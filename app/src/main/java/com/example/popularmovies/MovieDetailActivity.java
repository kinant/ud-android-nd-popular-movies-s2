package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE = "movie";

    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mVoterAvg;
    private TextView mPlot;

    private ImageView mPoster;
    private ImageView mFavoriteIcon;
    private Drawable mDrawable;

    private boolean isFavorite = false;

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

        // Get the movie
        Intent intent = getIntent();
        Movie movie = (Movie) intent.getParcelableExtra(MOVIE);

        // Set the image using Picasso
        // https://guides.codepath.com/android/Displaying-Images-with-the-Picasso-Library
        Picasso.get()
                .load(movie.getPoster())
                .into(mPoster);

        // Set the text views
        mTitle.setText(movie.getTitle());
        mReleaseDate.setText(movie.getReleaseDate());
        mVoterAvg.setText(String.valueOf(movie.getVote_average()));
        mPlot.setText(movie.getPlot());

        // set the title
        setTitle(movie.getTitle());

        // check if movie is favorite
        checkIsFavorite();
        // https://www.semicolonworld.com/question/45887/change-drawable-color-programmatically
        mDrawable = ContextCompat.getDrawable(this,R.drawable.ic_heart);
    }



    private void checkIsFavorite() {
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
        Log.d("FAVORITE: ", "toggling favorite!" + isFavorite);
        checkIsFavorite();
    }
}

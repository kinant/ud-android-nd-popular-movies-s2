package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

        // Get the movie
        Intent intent = getIntent();
        Movie movie = (Movie) intent.getParcelableExtra(MOVIE);

        // Set the image
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
    }
}

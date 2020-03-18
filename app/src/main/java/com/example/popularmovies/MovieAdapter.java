package com.example.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Movie> mMovieData;

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mMoviePoster;

        public MovieAdapterViewHolder(View view){
            super(view);
            mMoviePoster = (ImageView) view.findViewById(R.id.image_iv);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForGridItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForGridItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        // Set the image

        Picasso.get()
                .load(mMovieData.get(position).getPoster())
                .into(movieAdapterViewHolder.mMoviePoster);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null){
            return 0;
        }

        return mMovieData.size();
    }

    public void setMovieData(List<Movie> movies){
        mMovieData = movies;
        notifyDataSetChanged();
    }
}

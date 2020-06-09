package com.example.popularmovies.database;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_movies")
public class FavoriteMovie {
    @PrimaryKey(autoGenerate = false)
    private int movie_id;

    @Ignore()
    public FavoriteMovie(int movie_id){
        Log.d("CREATING NEW MOVIE: ", " id: " + movie_id);
        this.movie_id = movie_id;
    }

    public FavoriteMovie(){ }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }
}

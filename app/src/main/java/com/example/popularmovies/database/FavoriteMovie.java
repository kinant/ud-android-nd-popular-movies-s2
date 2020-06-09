package com.example.popularmovies.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_movies")
public class FavoriteMovie {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int movie_id;

    @Ignore()
    public FavoriteMovie(int movie_id){
        movie_id = movie_id;
    }
}

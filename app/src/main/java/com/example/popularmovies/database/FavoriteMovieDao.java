package com.example.popularmovies.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.popularmovies.model.Movie;

import java.util.List;

@Dao
public interface FavoriteMovieDao {

    @Query("SELECT * FROM favorite_movies")
    List<Movie> loadAllFavoriteMovies();

    @Query("SELECT COUNT(1) FROM favorite_movies WHERE movie_id = :movie_id")
    int isMovieFavorite(int movie_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavoriteMovie(Movie favoriteMovie);

    @Query("DELETE FROM favorite_movies WHERE movie_id = :movie_id")
    void deleteFavoriteMovie(int movie_id);
}

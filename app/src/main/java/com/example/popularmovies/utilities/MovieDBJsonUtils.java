package com.example.popularmovies.utilities;

import android.util.Log;

import com.example.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class MovieDBJsonUtils {

    final static String MDB_RESULTS = "results";
    final static String MDB_TITLE = "original_title";
    final static String MDB_RELEASE_DATE = "release_date";
    final static String MDB_POSTER = "poster_path";
    final static String MDB_VOTE_AVG = "vote_average";
    final static String MDB_PLOT = "overview";

    public static List<Movie> getMoviesFromJson(String jsonString){

        List<Movie> movies = new ArrayList<Movie>();

        try {
            // get the entire response as an object
            JSONObject response = new JSONObject(jsonString);

            // The movies are contained in the "results" array
            JSONArray results = response.getJSONArray(MDB_RESULTS);

            // now we iterate over each result to get each movie
            for(int i = 0; i < results.length();  i++){
                // create a new movie
                JSONObject result = results.getJSONObject(i);
                // get the data
                String title = result.getString(MDB_TITLE);
                String releaseDate = result.getString(MDB_RELEASE_DATE);
                String poster = "http://image.tmdb.org/t/p/w185/" + result.getString(MDB_POSTER);
                int vote_avg = result.getInt(MDB_VOTE_AVG);
                String plot = result.getString(MDB_PLOT);

                Log.d("Movie" + i + "", title + ", " + releaseDate + ", " + poster + ", " + String.valueOf(vote_avg) + ", " + plot);

                Movie newMovie = new Movie(
                        title,
                        releaseDate,
                        poster,
                        vote_avg,
                        plot);

                movies.add(newMovie);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }

        return movies;
    }
}

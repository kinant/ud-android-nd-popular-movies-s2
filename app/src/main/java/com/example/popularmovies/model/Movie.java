package com.example.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * This class is used to represent a Movie Object. It implements Parcelable, so that it can
 * be passed from one activity to another. For more information and reference on the code, look at
 * the following sources:
 * https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
 * http://sohailaziz05.blogspot.com/2012/04/passing-custom-objects-between-android.html
 */
public class Movie implements Parcelable {
    private int movie_id;
    private String title;
    private String releaseDate;
    private String poster;
    private int vote_average;
    private String plot;

    public Movie(int movie_id, String title, String releaseDate, String poster, int vote_avg, String plot){
        this.movie_id = movie_id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.poster = poster;
        this.vote_average = vote_avg;
        this.plot = plot;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public String getTitle() { return title; }

    public String getPoster() {
        return poster;
    }

    public String getReleaseDate() { return releaseDate; }

    public String getPlot() { return plot; }

    public int getVote_average() { return vote_average; }

    public Movie(Parcel in){
        readFromParcel( in );
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie( in );
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movie_id);
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(releaseDate);
        dest.writeString(plot);
        dest.writeInt(vote_average);
    }

    private void readFromParcel(Parcel in){
        movie_id = in.readInt();
        title = in.readString();
        poster = in.readString();
        releaseDate = in.readString();
        plot = in.readString();
        vote_average = in.readInt();
    }

    public void printMovie(){
        Log.d("MOVIE: ", movie_id +", " + title);
    }
}

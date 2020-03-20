package com.example.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

// https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
// http://sohailaziz05.blogspot.com/2012/04/passing-custom-objects-between-android.html
public class Movie implements Parcelable {
    private String title;
    private String releaseDate;
    private String poster;
    private int vote_average;
    private String plot;

    public Movie(String title, String releaseDate, String poster, int vote_avg, String plot){
        this.title = title;
        this.releaseDate = releaseDate;
        this.poster = poster;
        this.vote_average = vote_avg;
        this.plot = plot;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

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
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(releaseDate);
        dest.writeString(plot);
        dest.writeInt(vote_average);
    }

    private void readFromParcel(Parcel in){
        title = in.readString();
        poster = in.readString();
        releaseDate = in.readString();
        plot = in.readString();
        vote_average = in.readInt();
    }
}

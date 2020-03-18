package com.example.popularmovies.model;

public class Movie {
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

    public String getPoster() {
        return poster;
    }
}

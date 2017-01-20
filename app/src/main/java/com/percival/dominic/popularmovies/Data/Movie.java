/*
 * Copyright (C) 2013 The Android Open Source Project
 */
package com.percival.dominic.popularmovies.Data;

import android.os.Bundle;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class: Movie
 * Created on: 18-01-2017 23:34
 * Created by: Dominic PD
 * Provides an Object modelled around a Movie entity
 */

public class Movie{
    private String mTitle;
    private String mPoster;
    private String mOverview;
    private double mRating;
    private Date mReleaseDate;
    private static DateFormat mDateFormat;

    public static final String TITLE = "title";
    public static final String POSTER = "poster";
    public static final String OVERVIEW = "overview";
    public static final String RATING = "rating";
    public static final String RELEASEDATE = "releasedate";

    /**
     * Creates a movie object.
     * @param title Sting literal that holds the title of the movie.
     * @param poster An URL which is points to the address of the movie's poster from the API source[See NetworkUtils].
     * @param overview A string literal that holds the synopsis of the movie.
     * @param rating A double value which is the average rating of the movie out of 5 stars.
     * @param releaseDate A date value which the release date of the movie.
     */
    public Movie(String title, String poster, String overview, double rating, Date releaseDate){
        this.mTitle = title;;
        this.mPoster = poster;
        this.mOverview = overview;
        this.mRating = rating;
        this.mReleaseDate = releaseDate;
    }

    /**
     * Creates a Movie object from the bundle attached to an Intent.
     * @param movieBundle A movie bundle containing, 4 strings and a double.
     */
    public Movie(Bundle movieBundle){
        this.mTitle = movieBundle.getString(TITLE);
        this.mPoster = movieBundle.getString(POSTER);
        this.mOverview = movieBundle.getString(OVERVIEW);
        this.mRating = movieBundle.getDouble(RATING);
        try {
            this.mReleaseDate = mDateFormat.parse(movieBundle.getString(RELEASEDATE));
        }
        catch (ParseException e){
            e.printStackTrace();
        }
    }

    static{
        mDateFormat = new SimpleDateFormat("yyyy-mm-dd");
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPoster() {
        return mPoster;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getRating() {
        return mRating;
    }

    public Date getReleaseDate(){
        return mReleaseDate;
    }

    public static DateFormat getDateFormat() { return mDateFormat; }

}

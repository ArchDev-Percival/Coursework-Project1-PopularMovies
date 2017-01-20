/*
 * Copyright (C) 2013 The Android Open Source Project
 */
package com.percival.dominic.popularmovies.MiscUtilities;

import com.percival.dominic.popularmovies.Data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import static com.percival.dominic.popularmovies.Data.Movie.getDateFormat;
import static com.percival.dominic.popularmovies.MiscUtilities.NetworkUtils.buildUrlForImage;


/**
 * Encapsulates all the JSON Parsing in this Application.
 */

public class JSONUtils {

    private static final String LOG_TAG = JSONUtils.class.getSimpleName();

    //JSON Input - Level 1 Page - Variables for Values of Field Names.
    public static int sJsonPageNumber;
    private static JSONArray sJsonResults;
    private static int sJsonTotalResults;
    private static int sJsonTotalPages;

    //JSON Input - Level 1 Page - Field Names
    private static final String JSON_PAGE_FIELD_NAME = "page";
    private static final String JSON_RESULTS_FIELD_NAME = "results";
    private static final String JSON_TOTAL_RESULTS_FIELD_NAME = "total_results";
    private static final String JSON_TOTAL_PAGES_FIELD_NAME = "total_pages";

    //Json Input - Level 2 Individual Movie - Detail movie Field Names
    private static final String JSON_INDIVIDUAL_MOVIE_POSTER_PATH = "poster_path";
    private static final String JSON_INDIVIDUAL_MOVIE_IS_ADULT = "adult";
    private static final String JSON_INDIVIDUAL_MOVIE_OVERVIEW = "overview";
    private static final String JSON_INDIVIDUAL_MOVIE_RELEASE_DATE = "release_date";
    private static final String JSON_INDIVIDUAL_MOVIE_GENRE_IDS = "genre_ids";
    private static final String JSON_INDIVIDUAL_MOVIE_ID = "id";
    private static final String JSON_INDIVIDUAL_MOVIE_ORIGINAL_TITLE = "original_title";
    private static final String JSON_INDIVIDUAL_MOVIE_ORIGINAL_LANGUAGE = "original_language";
    private static final String JSON_INDIVIDUAL_MOVIE_TITLE = "title";
    private static final String JSON_INDIVIDUAL_MOVIE_BACKDROP_PATH = "backdrop_path";
    private static final String JSON_INDIVIDUAL_MOVIE_POPULARITY = "popularity";
    private static final String JSON_INDIVIDUAL_MOVIE_VOTE_COUNT = "vote_count";
    private static final String JSON_INDIVIDUAL_MOVIE_VIDEO = "video";
    private static final String JSON_INDIVIDUAL_MOVIE_VOTE_AVERAGE = "vote_average";

    /**
     * Performs the JSON parsing on an input JSON String.
     * @param jsonResponseString
     * @return ArrayList<Movie> object, populated with all the movie details after the successful JSON parsing.
     * @throws JSONException notifies the caller to handle possible JSON parsing issues.
     * @throws IOException notifies the caller to handle possible IO issues.
     * @throws ParseException notifies the caller to handle possible date parse issues.
     */
    public static ArrayList<Movie> getPopularMovies(String jsonResponseString) throws JSONException,IOException,ParseException{

        ArrayList<Movie> MasterMoviesList = new ArrayList<Movie>();
        JSONObject jsonResponse = null;
        jsonResponse = new JSONObject(jsonResponseString);

        //Parse Level 1 of the JSON Response
        sJsonPageNumber = jsonResponse.getInt(JSON_PAGE_FIELD_NAME);
        sJsonResults = jsonResponse.getJSONArray(JSON_RESULTS_FIELD_NAME);
        sJsonTotalResults = jsonResponse.getInt(JSON_TOTAL_RESULTS_FIELD_NAME);
        sJsonTotalPages = jsonResponse.getInt(JSON_TOTAL_PAGES_FIELD_NAME);

        //Parse Level 2 of the JSON Response
        for (int i = 0; i < sJsonResults.length(); i++) {
            JSONObject specificMovie = (JSONObject) sJsonResults.get(i);

            String jsonMoviePosterPath = specificMovie.getString(JSON_INDIVIDUAL_MOVIE_POSTER_PATH);
            jsonMoviePosterPath = buildUrlForImage(jsonMoviePosterPath);

            boolean jsonMovieIsAdult = specificMovie.getBoolean(JSON_INDIVIDUAL_MOVIE_IS_ADULT);

            String jsonMovieOverview = specificMovie.getString(JSON_INDIVIDUAL_MOVIE_OVERVIEW);

            String jsonMovieReleaseDateAsString = specificMovie.getString(JSON_INDIVIDUAL_MOVIE_RELEASE_DATE);
            Date jsonMovieReleaseDate = getDateFormat().parse(jsonMovieReleaseDateAsString);

            JSONArray jsonMovieGenres = specificMovie.getJSONArray(JSON_INDIVIDUAL_MOVIE_GENRE_IDS);
            ArrayList<Integer> jsonMovieGenreIds = new ArrayList<Integer>();
            for (int j = 0; j < jsonMovieGenres.length(); j++) {
                jsonMovieGenreIds.add(jsonMovieGenres.getInt(j));
            }

            int jsonMovieID = specificMovie.getInt(JSON_INDIVIDUAL_MOVIE_ID);

            String jsonMovieOriginalTitle = specificMovie.getString(JSON_INDIVIDUAL_MOVIE_ORIGINAL_TITLE);

            String jsonMovieOriginalLanguage = specificMovie.getString(JSON_INDIVIDUAL_MOVIE_ORIGINAL_LANGUAGE);

            String jsonMovieTitle = specificMovie.getString(JSON_INDIVIDUAL_MOVIE_TITLE);

            String jsonMovieBackdropPath = specificMovie.getString(JSON_INDIVIDUAL_MOVIE_BACKDROP_PATH);
            jsonMovieBackdropPath = buildUrlForImage(jsonMovieBackdropPath);

            double jsonMoviePopularity = specificMovie.getDouble(JSON_INDIVIDUAL_MOVIE_POPULARITY);

            int jsonMovieVoteCount = specificMovie.getInt(JSON_INDIVIDUAL_MOVIE_VOTE_COUNT);

            boolean jsonMovieVideo = specificMovie.getBoolean(JSON_INDIVIDUAL_MOVIE_VIDEO);

            double jsonMovieVoteAverage = specificMovie.getDouble(JSON_INDIVIDUAL_MOVIE_VOTE_AVERAGE);

            //Add the required details to a new Movie and add the movie to the master list of movies
            MasterMoviesList.add(new Movie(jsonMovieOriginalTitle, jsonMoviePosterPath,
                    jsonMovieOverview, jsonMovieVoteAverage, jsonMovieReleaseDate));
        }
        return MasterMoviesList;
    }
}

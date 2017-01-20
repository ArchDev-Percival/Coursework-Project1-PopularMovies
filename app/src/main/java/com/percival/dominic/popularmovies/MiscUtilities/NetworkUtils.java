/*
 * Copyright (C) 2013 The Android Open Source Project
 */
package com.percival.dominic.popularmovies.MiscUtilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Encapsulates all network operations in this Application.
 */

public class NetworkUtils {

    //LOGTAG.
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    //Base URL of the API.
    private static final String API_URL_BASE = "https://api.themoviedb.org/3/movie";

    //URL Sort Categories.
    public static final String API_URL_CATEGORY_POPULAR = "popular";
    public static final String API_URL_CATEGORY_TOPRATED = "top_rated";
    //URL Sort Categories - Top Rating is the default. Updated in the Options Menu (master_docket_menu.xml)
    public static String sApiUrlCategoryChosen = API_URL_CATEGORY_TOPRATED;

    //Query Parameters Key/Name.
    private static final String API_QPARAM_APIKEY_KEY_M = "api_key";
    private static final String API_QPARAM_LANGUAGE_KEY_O = "language";
    private static final String API_QPARAM_PAGE_KEY_O = "page";
    private static final String API_QPARAM_REGION_KEY_O = "region";

    //Query Parameter Values.
    //TODO: Reviewer TODO Please fill in your App ID
    private static final String API_QPARAM_APIKEY_VALUE = "";
    private static final String API_QPARAM_LANGUAGE_VALUE = "en";
    private static String sApiQParamPageValue = "1";

    //Image URL Building.
    private static final String API_IMG_BASE_URL = "http://image.tmdb.org/t/p";

    //Image URL Path fragment, based on type of Image Size.
    private static final String API_IMG_SIZE_OPTION_W92 = "/w92/";
    private static final String API_IMG_SIZE_OPTION_W154 = "/w154/";
    private static final String API_IMG_SIZE_OPTION_W185 = "/w185/";
    private static final String API_IMG_SIZE_OPTION_W342 = "/w342/";
    private static final String API_IMG_SIZE_OPTION_W500 = "/w500/";
    private static final String API_IMG_SIZE_OPTION_W780 = "/w780/";
    private static final String API_IMG_SIZE_OPTION_ORIGINAL = "/original/";
    private static String sApiImgSizeSelected = API_IMG_SIZE_OPTION_W342;

    /**
     * Creates and returns the
     * @param chosenCategoryID The chosen sortCategory indicating sorting by top rating or popularity.
     * @return URL object, that is the network url that provides the JSON response.
     */
    private static URL buildUrl(String chosenCategoryID) throws MalformedURLException,IOException{
        switch (chosenCategoryID){
            case API_URL_CATEGORY_POPULAR:
                sApiUrlCategoryChosen = API_URL_CATEGORY_POPULAR;
                break;
            case API_URL_CATEGORY_TOPRATED:
            default:
                sApiUrlCategoryChosen = API_URL_CATEGORY_TOPRATED;
                break;
        }

        Uri.Builder uriBuilder = Uri.parse(API_URL_BASE).buildUpon()
                .appendPath(sApiUrlCategoryChosen)
                .query("")
                .appendQueryParameter(API_QPARAM_APIKEY_KEY_M,API_QPARAM_APIKEY_VALUE)
                .appendQueryParameter(API_QPARAM_LANGUAGE_KEY_O,API_QPARAM_LANGUAGE_VALUE)
                .appendQueryParameter(API_QPARAM_PAGE_KEY_O,sApiQParamPageValue)
                ;
        Uri fetchUri = uriBuilder.build();

        URL fetchUrl = null;
        fetchUrl = new URL(fetchUri.toString());
        return fetchUrl;
    }

    /**
     * Obtains and returns a JSON response as a String by querying the provided URL.
     * @param chosenCategoryID int, representing The chosen ID, which will result in sorting by Popularity or in sorting by: Top Rating
     * @return String literal, which is the JSON Response String containing movies and their details.
     */
    public static String getJsonString(String chosenCategoryID) throws MalformedURLException,IOException{

        URL fetchUrl = buildUrl(chosenCategoryID);
        if(fetchUrl==null) {
            return null;
        }
        HttpURLConnection httpUrlConnection = (HttpURLConnection) fetchUrl.openConnection();
            httpUrlConnection.setRequestMethod("GET");

        int responseCode = httpUrlConnection.getResponseCode();
        switch(responseCode) {
            case 200:
                httpUrlConnection.connect();
                InputStream responseStream = httpUrlConnection.getInputStream();
                Scanner responseStreamScanner = new Scanner(responseStream).useDelimiter("\\A");
                return responseStreamScanner.hasNext() ? responseStreamScanner.next() : "";
            default:
                Log.e(LOG_TAG, " ");
                return null;
        }
    }

    /**
     * Method to build the complete URL to load Images
     * @param urlFragment String literal, the /*.jpg filename provided by the JSON response.
     * @return String literal, the complete URL that can be used to fetch a movie poster.
     */
    static String buildUrlForImage(String urlFragment){

        Uri imageUri = Uri.parse( API_IMG_BASE_URL + sApiImgSizeSelected + urlFragment )
                .buildUpon()
                .build();

        return imageUri.toString();
    }

    /**
     * Checks if the device is connected to some form of network
     */
    public static boolean isNetworkConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo!=null){
            if(networkInfo.getState() == NetworkInfo.State.CONNECTED){
                return true;
            } else {
                return false;
            }
        }
        else{
            return false;
        }
    }
}

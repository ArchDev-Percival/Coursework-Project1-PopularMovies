/*
 * Copyright (C) 2013 The Android Open Source Project
 * Copyright 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.percival.dominic.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.percival.dominic.popularmovies.Data.Movie;
import com.squareup.picasso.Picasso;

/**
 * Encapsulates the data and functions of a Detail Pane, in a Discovery-Detail Pattern.
 */
public class DetailActivity extends AppCompatActivity {
    //UI View Reference Variables corresponding to details required on the pane.
    Movie mMovie;
    TextView mTitle;
    WebView mSummary;
    TextView mReleaseDate;
    RatingBar mRatingBar;
    ImageView mPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Assigning references to the variables
        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mSummary = (WebView) findViewById(R.id.wv_summary);
        mReleaseDate  = (TextView) findViewById(R.id.tv_movie_releasedate);
        mRatingBar  = (RatingBar) findViewById(R.id.rb_movie_rating);
        mPoster  = (ImageView) findViewById(R.id.iv_poster);

        getIntentDetails();
    }

    /**
     * Extracts a bundle from the intent to recreate the required Movie Object and update the UI.
     */
    private void getIntentDetails(){
        Intent incomingIntent = getIntent();
        if(incomingIntent!=null){
            mMovie = new Movie(incomingIntent.getExtras());

            /*Used in conjunction with the web view to enable justifying text.

            */
            String webViewFormatter = "<html><body><p align=\"justify\">"+ mMovie.getOverview()
                    + "</p></body></html>";

            //Update the UI based on
            mTitle.setText(mMovie.getTitle());
            mSummary.loadData(webViewFormatter,"text/html","utf-8");
            mReleaseDate.setText(Movie.getDateFormat().format(mMovie.getReleaseDate()));
            mRatingBar.setRating((float)mMovie.getRating()/2);
            Picasso.with(this).load(mMovie.getPoster()).into(mPoster);
        }
        else {
            Toast.makeText(this, getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
        }
    }
}

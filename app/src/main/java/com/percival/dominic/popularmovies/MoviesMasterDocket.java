/*
 * Copyright (C) 2013 The Android Open Source Project
 */
package com.percival.dominic.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.percival.dominic.popularmovies.Data.Movie;
import com.percival.dominic.popularmovies.MiscUtilities.JSONUtils;
import com.percival.dominic.popularmovies.MiscUtilities.NetworkUtils;
import com.percival.dominic.popularmovies.RecyclerViewUtils.MoviesAdapter;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;

import static com.percival.dominic.popularmovies.MiscUtilities.NetworkUtils.isNetworkConnected;

/**
 * Encapsulates the data and function of a Discovery Pane, in a Discovery-Detail Pattern.
 */
public class MoviesMasterDocket extends AppCompatActivity implements MoviesAdapter.ListItemClickListener {

    //LOG_TAG
    public static final String LOG_TAG = MoviesMasterDocket.class.getSimpleName();

    //Primary Data Source for the Recycler View
    private ArrayList<Movie> mMasterMovieList;

    //RecyclerView & Associated Types
    private RecyclerView mMoviesMasterListRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView mErrorTextView;
    ProgressBar mNetworkLoadProgressBar;

    //Single, application-wide Toast Reference
    Toast mToast;

    @Override
    protected void onStart() {
        //Cancels the application wide mToast so that messages don't persist when swapping screens.
        if(mToast!=null){
            mToast.cancel();
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Implement the savedInstanceState Functionality. Does this help with rotate as well?
        setContentView(R.layout.activity_movies_masterlist);


        mMasterMovieList = new ArrayList<Movie>();
        mMoviesMasterListRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_master_list);
        mErrorTextView = (TextView) findViewById(R.id.tv_errorTextView);
        mNetworkLoadProgressBar = (ProgressBar) findViewById(R.id.pb_networkload) ;

        //Creation of the Layout Manager to have 2 columns and n/2 number of rows as there are listitems.
        layoutManager= new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL,false);
        mMoviesMasterListRecyclerView.setLayoutManager(layoutManager);

        mMoviesAdapter = new MoviesAdapter(this);

        //Call the AsyncMovieNetworkLoader to perform Network tasks on a parallel thread.
        (new AsyncMovieNetworkLoader()).execute(NetworkUtils.sApiUrlCategoryChosen);
    }

    /**
     * The method launches the individual movie detail screen with the relevant data.
     *     [Implemented method of the ListItemClickListener Interface]
     * @param clickedMovie Movie object, corresponding to the HolderView/View that was clicked.
     */
    @Override
    public void onListItemClick(Movie clickedMovie) {
        //Intent to move to the detailed screen.
        Intent moveToDetail = new Intent(MoviesMasterDocket.this,DetailActivity.class);

        //Adds bundle information to the Intent, which can be consumed to recreate an object in the detail screen.
        Bundle movieBundle = new Bundle();
            movieBundle.putString(Movie.TITLE,clickedMovie.getTitle());
            movieBundle.putString(Movie.RELEASEDATE,Movie.getDateFormat().format(clickedMovie.getReleaseDate()));
            movieBundle.putString(Movie.OVERVIEW,clickedMovie.getOverview());
            movieBundle.putDouble(Movie.RATING,clickedMovie.getRating());
            movieBundle.putString(Movie.POSTER,clickedMovie.getPoster());
            moveToDetail.putExtras(movieBundle);

        //Make sure that there is an application to handle the intent.
        if(moveToDetail.resolveActivity(getPackageManager())!=null){
            startActivity(moveToDetail);
        }
    }

    /**
     * An Asynchronous Tasks implementation to perform across-network data fetches over a parallel[to the UI] thread
     */
    public class AsyncMovieNetworkLoader extends AsyncTask<String,Void,String> {
        @Override
        /**
         * Performs network query on a provided url asynchronously.
         * @chosenSortCategory String varargs, that stores the single URL passed to the execute function.
         */
        protected String doInBackground(String... chosenSortCategory) {
            try {
                if(!isNetworkConnected(getApplicationContext())){
                    //handleError(null,getString(R.string.no_network_available));
                    return null;
                }
                return NetworkUtils.getJsonString(chosenSortCategory[0]);
            }
            catch (MalformedURLException e){
                handleError(e,getString(R.string.invalid_url));
            }
            catch (IOException e){
                handleError(e,getString(R.string.unable_to_connect));
            }
            return null;
        }

        /**
         * Updates the UI to show an infinite progressbar while the network query is performed.
         */
        @Override
        protected void onPreExecute() {
            mNetworkLoadProgressBar.setVisibility(View.VISIBLE);
            mErrorTextView.setVisibility(View.INVISIBLE);
            mMoviesMasterListRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            mNetworkLoadProgressBar.setVisibility(View.INVISIBLE);
            try{
                if(jsonResponse==null){
                    Log.e(LOG_TAG,getString(R.string.invalid_json_string));
                    handleError(null,null);
                    return;
                }
                mMasterMovieList = JSONUtils.getPopularMovies(jsonResponse);
                updateUiOnSuccess();
            }
            catch (JSONException e) {
                handleError(e,getString(R.string.invalid_json_string));
            }
            catch (ParseException e){
                handleError(e,null);
            }
            catch (IOException e){
                handleError(e,null);
            }

            mMoviesAdapter.setMovieList(mMasterMovieList);
            mMoviesMasterListRecyclerView.setAdapter(mMoviesAdapter);
        }
    }

    /**
     * Inflates the menu, on creation
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.master_docket_menu,menu);
        return true;
    }

    /**
     * Handles the selection of a item from the Options Menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();
        switch (menuItemSelected){
            case R.id.action_sortby_popularity:
                updateSortCategory(NetworkUtils.API_URL_CATEGORY_POPULAR);
                break;
            case R.id.action_sortby_rating:
                updateSortCategory(NetworkUtils.API_URL_CATEGORY_TOPRATED);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the default category in response to Option Item Selection
     * @param chosenCategory String literal, one of the contant values as described by NetworkUtils.java
     */
    private void updateSortCategory(String chosenCategory){
        if(!isNetworkConnected(this)){
            mToast = Toast.makeText(this,getString(R.string.no_network_available),Toast.LENGTH_LONG);
            mToast.show();
        }
        if(NetworkUtils.sApiUrlCategoryChosen.equals(chosenCategory))
        {
            mToast = Toast.makeText(getApplicationContext(),getString(R.string.already_sorted),Toast.LENGTH_LONG);
            mToast.show();
        } else {
            (new AsyncMovieNetworkLoader()).execute(chosenCategory);
        }
    }

    /**
     * Handles errors by: updating UI / printing Error Messages and printing the error stack Trace.
     * @param e Exception object, so that the error stacktrace can be printed.
     * @param logMessages String literal, that holds the message to be printed to the logcat.
     */
    private void handleError(Exception e,String logMessages){
        mErrorTextView.setText(getString(R.string.no_network_available));
        mErrorTextView.setVisibility(View.VISIBLE);
        mMoviesMasterListRecyclerView.setVisibility(View.INVISIBLE);
        mNetworkLoadProgressBar.setVisibility(View.INVISIBLE);

        if(e!=null){
            e.printStackTrace();
        }
        if(logMessages!=null){
            Log.e(LOG_TAG,logMessages);
        }
    }
    //Updates the UI by hiding error display and progressbar.
    private void updateUiOnSuccess(){
        mErrorTextView.setVisibility(View.INVISIBLE);
        mMoviesMasterListRecyclerView.setVisibility(View.VISIBLE);
        mNetworkLoadProgressBar.setVisibility(View.INVISIBLE);

    }
}

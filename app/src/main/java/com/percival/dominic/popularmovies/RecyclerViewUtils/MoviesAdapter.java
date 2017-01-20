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
package com.percival.dominic.popularmovies.RecyclerViewUtils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.percival.dominic.popularmovies.Data.Movie;
import com.percival.dominic.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Class: Movie
 * Created on: 19-01-2017 14:15
 * Created by: Dominic PD
 * Creates MovieViewHolders for each visible view and manages the binding of a large collection of data to these views.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    //Source of Data
    private ArrayList<Movie> mMasterMovieList;
    private ListItemClickListener mListItemClickListener;

    /**
     * Binds the click handling to the MoviesMasterDocket class
     */
    public interface ListItemClickListener{
        void onListItemClick(Movie clickedMovie);
    }

    /**
     * Creates an object of MoviesAdapter
     */
    public MoviesAdapter(ListItemClickListener listItemClickListener){
        this.mListItemClickListener = listItemClickListener;
    }

    /**
     * Updates the mMasterMovieList when asynchronous load from network is completed.
     * @param masterMovieList ArrayList<Movie> which has been populated from the JSON response.
     */
    public void setMovieList(ArrayList<Movie> masterMovieList){
        mMasterMovieList = masterMovieList;
    }

    /**
     * Inflates view, creates MovieViewHolders and assigns them views.
     * @return MovieViewHolder object, that contains the view [inflated listitem layout]
     */
    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View viewForNewViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_masterlist_item,parent,false);
        return new MovieViewHolder(viewForNewViewHolder);
    }

    /**
     * Describes what needs to be done, when a view is bound.
     * @param holder MovieViewHolder reference from a pool on which data will be bound and placed displayed in the RecyclerView.
     * @param position int value that describes the position in the RecyclerView and also the index in the data source.
     */
    @Override
    public void onBindViewHolder(MoviesAdapter.MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mMasterMovieList ==null){
            return 0;
        } else {
            return mMasterMovieList.size();
        }
    }

    /**
     * Stores and caches the references of individual dataviews in the inflated itemview.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mListItemPoster;

        private MovieViewHolder(View view){
            super(view);
            mListItemPoster = (ImageView) view.findViewById(R.id.iv_listitem_poster);
            view.setOnClickListener(this);

        }

        /**
         * Performs loading of a particular movie's poster into the ImageView reference in the MovieViewHolder
         * @param position int value, that provides the index of the movie whose poster needs to be loaded
         */
        void bind(int position){
            Picasso.with(itemView.getContext()).load(mMasterMovieList.get(position).getPoster()).into(mListItemPoster);
        }

        @Override
        public void onClick(View view) {
            mListItemClickListener.onListItemClick(mMasterMovieList.get(getAdapterPosition()));
        }
    }
}

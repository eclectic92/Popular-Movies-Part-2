package com.natalieryanudacity.android.popularmovies.moviedetails;

import android.content.Context;
import android.os.AsyncTask;

import com.natalieryanudacity.android.popularmovies.data.TmdbMovieContract;
import com.natalieryanudacity.android.popularmovies.model.TmdbMovie;

/**
 * Created by natalier258 on 6/21/17.'
 * Async task to remove favorite from tmdbmovies.db
 */

class RemoveFavoriteAsyncTask extends AsyncTask<TmdbMovie, Void, Void> {

    private final Context mContext;
    private final RemoveFavoriteListener mFavoriteListenter;

    public RemoveFavoriteAsyncTask (Context context, RemoveFavoriteListener removeFavoriteListener){
        this.mContext = context;
        this.mFavoriteListenter = removeFavoriteListener;
    }

    /**
     * Listener for async task completion
     * Must be implemented by hosting class
     */
    interface RemoveFavoriteListener {
        void onFavoriteRemoved();
    }

    @Override
    protected Void doInBackground(TmdbMovie... params) {

        if (params.length == 0) {
            return null;
        }

        TmdbMovie tmdbMovie = params[0];

        if(mContext != null && mContext.getContentResolver() != null){
            mContext.getContentResolver().delete(
                    TmdbMovieContract.TmdbMovieEntry.CONTENT_URI,
                    TmdbMovieContract.TmdbMovieEntry.COLUMN_TMDB_ID + " = " + tmdbMovie.getId(),
                    null
            );

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mFavoriteListenter.onFavoriteRemoved();
    }
}

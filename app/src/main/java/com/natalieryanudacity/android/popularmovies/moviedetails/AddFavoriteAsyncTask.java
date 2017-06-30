package com.natalieryanudacity.android.popularmovies.moviedetails;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.natalieryanudacity.android.popularmovies.data.TmdbMovieContract;
import com.natalieryanudacity.android.popularmovies.model.TmdbMovie;

/**
 * Created by natalier258 on 6/20/17.
 *
 * Async task to add favorite to tmdbmovies.db
 */

class AddFavoriteAsyncTask  extends AsyncTask<TmdbMovie, Void, Void> {

    private final Context mContext;
    private final AddFavoriteListener mFavoriteListener;

    public AddFavoriteAsyncTask(Context context, AddFavoriteListener addFavoriteListener){
        this.mContext = context;
        this.mFavoriteListener= addFavoriteListener;
    }

    /**
     * Listener for async task completion
     * Must be implemented by hosting class
     */
    interface AddFavoriteListener {
        void onFavoriteAdded();
    }

    @Override
    protected Void doInBackground(TmdbMovie... params) {

        if (params.length == 0) {
            return null;
        }

        // if we don't have a movie, nothing to do

        TmdbMovie tmdbMovie = params[0];
        ContentValues favoriteMovieValues = new ContentValues();

        // some movie detail info might be null, so let's extract it here for
        // easier evaluation later
        String posterPath = tmdbMovie.getPosterPath();
        String bannerPath = tmdbMovie.getBannerPath();
        String overview = tmdbMovie.getOverview();
        String tagline = tmdbMovie.getTagline();
        String runningTime = tmdbMovie.getRunningTime();
        String genres = tmdbMovie.getGenres();
        String certification = tmdbMovie.getCertification();
        String posterImagePath = tmdbMovie.getPosterImagePath();
        String bannerImagePath = tmdbMovie.getBannerImagePath();
        String castList = tmdbMovie.getCastList();

        favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_TMDB_ID,
                tmdbMovie.getId());

        favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_TITLE,
                tmdbMovie.getMovieTitle());

        favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_RELEASE_DATE,
                tmdbMovie.getRawReleaseDate());

        if(posterPath != null){
            favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_POSTER_PATH,
                    tmdbMovie.getPosterPath());
        }else{
            favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_POSTER_PATH);
        }

        if(bannerPath !=null){
            favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_BANNER_PATH,
                    tmdbMovie.getBannerPath());
        }else{
            favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_BANNER_PATH);

        }

        if(overview != null){
            favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_OVERVIEW, overview);
        }else{
            favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_OVERVIEW);
        }

        if(tagline != null){
            favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_TAGLINE, tagline);
        }else{
            favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_TAGLINE);
        }

        favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_VOTE_AVERAGE,
                tmdbMovie.getVoteAverage());

        if(runningTime != null){
            favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry
                    .COLUMN_RUNNING_TIME, runningTime);
        }else{
            favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_RUNNING_TIME);
        }

        if(genres != null){
            favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_GENRES, genres);
        }else{
            favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_GENRES);
        }

        if(certification !=null){
            favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry
                    .COLUMN_CERTIFICATION, certification);
        }else{
            favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_CERTIFICATION);
        }

        if(posterImagePath != null){
            favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry
                    .COLUMN_POSTER_FILE_PATH, posterImagePath);
        }else{
            favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_POSTER_FILE_PATH);
        }

        if(bannerImagePath != null){
            favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry
                    .COLUMN_BANNER_FILE_PATH, bannerImagePath);
        }else{
            favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_BANNER_FILE_PATH);
        }

        if(castList != null){
            favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry
                    .COLUMN_CAST_LIST, castList);
        }else{
            favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_CAST_LIST);
        }

        if(mContext != null && mContext.getContentResolver() != null){
             mContext.getContentResolver().insert(
                    TmdbMovieContract.TmdbMovieEntry.CONTENT_URI,
                    favoriteMovieValues
            );
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mFavoriteListener.onFavoriteAdded();
    }
}

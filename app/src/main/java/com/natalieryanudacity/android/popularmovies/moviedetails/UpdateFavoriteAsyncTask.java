package com.natalieryanudacity.android.popularmovies.moviedetails;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.natalieryanudacity.android.popularmovies.data.TmdbMovieContract;
import com.natalieryanudacity.android.popularmovies.model.TmdbMovie;

/**
 * Created by natalier258 on 6/22/17.
 * Updates entries in tmdbmovies.db
 */

class UpdateFavoriteAsyncTask extends AsyncTask<TmdbMovie, Void, Integer>
{

	private final Context mContext;
	private final UpdateFavoriteListener mFavoriteListenter;


	UpdateFavoriteAsyncTask(Context context,
							UpdateFavoriteListener updateFavoriteListener)
	{
		this.mContext=context;
		this.mFavoriteListenter=updateFavoriteListener;
	}


	/**
	 * Listener for async task completion
	 * Must be implemented by hosting class
	 */
	interface UpdateFavoriteListener
	{
		void onFavoriteUpdated(int rowsUpdated);
	}


	@Override
	protected Integer doInBackground(TmdbMovie... params)
	{

		// if we don't have a movie, nothing to do
		if (params.length==0)
		{
			return 0;
		}

		TmdbMovie tmdbMovie=params[0];
		ContentValues favoriteMovieValues=new ContentValues();

		// extended movie detail info might be null, so let's extract it here for
		// easier evaluation later
		String posterPath=tmdbMovie.getPosterPath();
		String bannerPath=tmdbMovie.getBannerPath();
		String overview=tmdbMovie.getOverview();
		String tagline=tmdbMovie.getTagline();
		String runningTime=tmdbMovie.getRunningTime();
		String genres=tmdbMovie.getGenres();
		String certification=tmdbMovie.getCertification();
		String posterImagePath=tmdbMovie.getPosterImagePath();
		String bannerImagePath=tmdbMovie.getBannerImagePath();
		String castList=tmdbMovie.getCastList();

		int rowsUpdated=0;

		favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_TMDB_ID,
				tmdbMovie.getId());

		favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_TITLE,
				tmdbMovie.getMovieTitle());

		favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_RELEASE_DATE,
				tmdbMovie.getRawReleaseDate());

		if (posterPath!=null)
		{
			favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_POSTER_PATH,
					tmdbMovie.getPosterPath());
		}
		else
		{
			favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_POSTER_PATH);
		}

		if (bannerPath!=null)
		{
			favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_BANNER_PATH,
					tmdbMovie.getBannerPath());
		}
		else
		{
			favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_BANNER_PATH);

		}

		if (overview!=null)
		{
			favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_OVERVIEW, overview);
		}
		else
		{
			favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_OVERVIEW);
		}

		if (tagline!=null)
		{
			favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_TAGLINE, tagline);
		}
		else
		{
			favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_TAGLINE);
		}

		favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_VOTE_AVERAGE,
				tmdbMovie.getVoteAverage());

		if (runningTime!=null)
		{
			favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry
					.COLUMN_RUNNING_TIME, runningTime);
		}
		else
		{
			favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_RUNNING_TIME);
		}

		if (genres!=null)
		{
			favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry.COLUMN_GENRES, genres);
		}
		else
		{
			favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_GENRES);
		}

		if (certification!=null)
		{
			favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry
					.COLUMN_CERTIFICATION, certification);
		}
		else
		{
			favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_CERTIFICATION);
		}

		if (posterImagePath!=null)
		{
			favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry
					.COLUMN_POSTER_FILE_PATH, posterImagePath);
		}
		else
		{
			favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_POSTER_FILE_PATH);
		}

		if (bannerImagePath!=null)
		{
			favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry
					.COLUMN_BANNER_FILE_PATH, bannerImagePath);
		}
		else
		{
			favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_BANNER_FILE_PATH);
		}

		if (castList!=null)
		{
			favoriteMovieValues.put(TmdbMovieContract.TmdbMovieEntry
					.COLUMN_CAST_LIST, castList);
		}
		else
		{
			favoriteMovieValues.putNull(TmdbMovieContract.TmdbMovieEntry.COLUMN_CAST_LIST);
		}

		if (mContext!=null && mContext.getContentResolver()!=null)
		{
			rowsUpdated=mContext.getContentResolver().update(
					TmdbMovieContract.TmdbMovieEntry.CONTENT_URI,
					favoriteMovieValues,
					TmdbMovieContract.TmdbMovieEntry.COLUMN_TMDB_ID+" = "+tmdbMovie.getId(),
					null
			);
		}
		return rowsUpdated;
	}


	@Override
	protected void onPostExecute(Integer rowsUpdatedCount)
	{
		mFavoriteListenter.onFavoriteUpdated(rowsUpdatedCount);
	}
}

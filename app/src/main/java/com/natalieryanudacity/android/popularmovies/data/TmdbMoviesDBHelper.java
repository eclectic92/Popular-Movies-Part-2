package com.natalieryanudacity.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.natalieryanudacity.android.popularmovies.data.TmdbMovieContract.TmdbMovieEntry;

/**
 * Created by natalier258 on 6/20/17.
 *
 * database helper for tmdbmovies.db
 */

class TmdbMoviesDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tmdbmovies.db";

    private static final int DATABASE_VERSION = 8;

    public TmdbMoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TMDB_MOVIES_TABLE =
                "CREATE TABLE " + TmdbMovieEntry.TABLE_NAME + " (" +
                        TmdbMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TmdbMovieEntry.COLUMN_TMDB_ID + " TEXT NOT NULL, " +
                        TmdbMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        TmdbMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        TmdbMovieEntry.COLUMN_POSTER_PATH + " TEXT , " +
                        TmdbMovieEntry.COLUMN_BANNER_PATH + " TEXT , " +
                        TmdbMovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                        TmdbMovieEntry.COLUMN_TAGLINE + " TEXT, " +
                        TmdbMovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                        TmdbMovieEntry.COLUMN_RUNNING_TIME + " TEXT, " +
                        TmdbMovieEntry.COLUMN_GENRES + " TEXT, " +
                        TmdbMovieEntry.COLUMN_CERTIFICATION + " TEXT, " +
                        TmdbMovieEntry.COLUMN_POSTER_FILE_PATH + " TEXT, " +
                        TmdbMovieEntry.COLUMN_BANNER_FILE_PATH + " TEXT, " +
                        TmdbMovieEntry.COLUMN_CAST_LIST + " TEXT, " +
                        " UNIQUE (" + TmdbMovieEntry.COLUMN_TMDB_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_TMDB_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TmdbMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

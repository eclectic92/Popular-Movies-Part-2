package com.natalieryanudacity.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by natalier258 on 6/20/17.
 *
 * provider for tmdbmovies.db
 */

public class TmdbMoviesProvider extends ContentProvider {

    private static final int CODE_MOVIES = 100;
    private static final int CODE_MOVIE_BY_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TmdbMoviesDBHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TmdbMovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TmdbMovieContract.PATH_TMDB_MOVIE, CODE_MOVIES);
        matcher.addURI(authority, TmdbMovieContract.PATH_TMDB_MOVIE + "/#", CODE_MOVIE_BY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TmdbMoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_BY_ID: {
                String movieId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieId};
                cursor = mOpenHelper.getReadableDatabase().query(
                        TmdbMovieContract.TmdbMovieEntry.TABLE_NAME,
                        projection,
                        TmdbMovieContract.TmdbMovieEntry.COLUMN_TMDB_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_MOVIES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        TmdbMovieContract.TmdbMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES: {
                long id = mOpenHelper.getWritableDatabase().insert(
                        TmdbMovieContract.TmdbMovieEntry.TABLE_NAME,
                        null,
                        values
                );

                if (id > 0) {
                    returnUri = TmdbMovieContract.TmdbMovieEntry.buildTmdbMovieUriWithId(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int deletedDrowsCount;

        if (null == selection){
            selection = "1";
        }

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIES:
                deletedDrowsCount = mOpenHelper.getWritableDatabase().delete(
                        TmdbMovieContract.TmdbMovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deletedDrowsCount != 0) {
            if(getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return deletedDrowsCount;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        int updatedRowsCount;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES: {
                updatedRowsCount = mOpenHelper.getWritableDatabase().update(
                        TmdbMovieContract.TmdbMovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (updatedRowsCount != 0) {
            if(getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return updatedRowsCount;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:{
                return TmdbMovieContract.TmdbMovieEntry.CONTENT_TYPE;
            }
            case CODE_MOVIE_BY_ID:{
                return TmdbMovieContract.TmdbMovieEntry.CONTENT_ITEM_TYPE;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}

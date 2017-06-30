package com.natalieryanudacity.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by natalier258 on 6/19/17.
 *
 * SQLite contract for storing movie info from tmdb
 */

@SuppressWarnings("unused")
public class TmdbMovieContract {

    public static final String CONTENT_AUTHORITY = "com.natalieryanudacity.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TMDB_MOVIE = "tmdb_movie";

    public static final class TmdbMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TMDB_MOVIE)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY + "/" + PATH_TMDB_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY  + "/" + PATH_TMDB_MOVIE;

        public static final String TABLE_NAME = "tmdb_movies";
        public static final String COLUMN_TMDB_ID = "tmdb_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BANNER_PATH = "backdrop_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RUNNING_TIME = "running_time";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_CERTIFICATION = "certification";
        public static final String COLUMN_POSTER_FILE_PATH = "poster_file_path";
        public static final String COLUMN_BANNER_FILE_PATH = "banner_file_path";
        public static final String COLUMN_CAST_LIST = "cast_list";

        public static Uri buildTmdbMovieUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}

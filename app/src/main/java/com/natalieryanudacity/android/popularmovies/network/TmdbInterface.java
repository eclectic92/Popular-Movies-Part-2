package com.natalieryanudacity.android.popularmovies.network;

import com.natalieryanudacity.android.popularmovies.model.TmdbMovieDetails;
import com.natalieryanudacity.android.popularmovies.model.TmdbMovies;
import com.natalieryanudacity.android.popularmovies.model.TmdbReviews;
import com.natalieryanudacity.android.popularmovies.model.TmdbTrailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by natalier258 on 6/14/17.
 *
 * Retrofit interface for tmdb
 */

@SuppressWarnings({"SameParameterValue", "unused"})
public interface TmdbInterface {
    String SORT_POPULAR = "popular";
    String SORT_TOP_RATED = "top_rated";
    String SORT_NOW_PLAYING = "now_playing";
    String SORT_UPCOMING = "upcoming";
    String SORT_FAVORITES = "favorites";
    String RELEASE_DATES = "release_dates";
    String CREDITS= "credits";

    @GET("{search_type}")
    Call<TmdbMovies> getMovieList(@Path("search_type") String searchType,
                                  @Query("api_key") String apiKey,
                                  @Query("page") int pageIndex);

    @GET("{search_type}")
    Call<TmdbMovies> getMovieList(@Path("search_type") String searchType,
                                  @Query("api_key") String apiKey,
                                  @Query("region") String region,
                                  @Query("page") int pageIndex);

    @GET("{id}")
    Call<TmdbMovieDetails> getMovieDetails(@Path("id") long id,
                                           @Query("api_key") String apiKey);

    @GET("{id}")
    Call<TmdbMovieDetails> getMovieDetails(@Path("id") long id,
                                            @Query("append_to_response") String extraInfoType,
                                            @Query("api_key") String apiKey);
    @GET("{id}/reviews")
    Call<TmdbReviews> getMovieReviews(@Path("id") long id,
                                      @Query("api_key") String apiKey);

    @GET("{id}/videos")
    Call<TmdbTrailers> getMovieTrailers(@Path("id") long id,
                                        @Query("api_key") String apiKey);
}

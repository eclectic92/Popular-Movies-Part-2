package com.natalieryanudacity.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by natalier258 on 6/15/17.
 *
 * Pojo for root element of reviews from tmdb
 */
@SuppressWarnings("unused")
public class TmdbReviews {
    @SerializedName("results")
    private final ArrayList<TmdbReview> reviews = new ArrayList<>();

    public ArrayList<TmdbReview> getReviews() {
        return reviews;
    }
}

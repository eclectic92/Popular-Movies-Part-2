package com.natalieryanudacity.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by natalier258 on 6/16/17.
 *
 * Pojo for tmdb regional release
 */

@SuppressWarnings("unused")
class TmdbRegionalRelease {
    @SerializedName("iso_3166_1")
    private String mCountryCode;
    @SerializedName("release_dates")
    private final ArrayList<TmdbRelease> mSingleReleases = new ArrayList<>();

    void setCountryCode(String countryCode){
        this.mCountryCode = countryCode;
    }

    String getCountryCode(){
        return mCountryCode;
    }

    ArrayList<TmdbRelease> getReleaseDates(){
        return mSingleReleases;
    }
}
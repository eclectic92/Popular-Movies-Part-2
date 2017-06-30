package com.natalieryanudacity.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by natalier258 on 6/14/17.
 *
 * Pojo for tmdb genre details
 */

@SuppressWarnings("unused")
class TmdbGenre {

    @SerializedName("name")
    private String mName;

    public void setName (String name){
        this.mName = name;
    }

    public String getName(){
        return mName;
    }
}
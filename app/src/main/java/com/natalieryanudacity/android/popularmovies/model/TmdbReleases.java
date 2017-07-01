package com.natalieryanudacity.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by natalier258 on 6/14/17.
 * <p>
 * Pojo for tmdb reegional releases data
 */

@SuppressWarnings("unused")

public class TmdbReleases
{

	@SerializedName("results")
	private final ArrayList<TmdbRegionalRelease> mResults=new ArrayList<>();


	ArrayList<TmdbRegionalRelease> getRegionalReleases()
	{
		return mResults;
	}
}

package com.natalieryanudacity.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by natalier258 on 6/14/17.
 * <p>
 * Pojo for tmdb credits info
 */

@SuppressWarnings("unused")

public class TmdbCast
{

	@SerializedName("cast")
	private final ArrayList<TmdbCastMember> mCastList=new ArrayList<>();


	ArrayList<TmdbCastMember> getCastList()
	{
		return mCastList;
	}
}
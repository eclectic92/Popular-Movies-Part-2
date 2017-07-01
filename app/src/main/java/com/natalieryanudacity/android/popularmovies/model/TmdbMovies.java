package com.natalieryanudacity.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by natalier258 on 6/14/17.
 * Simple class to store movie "results" response from tmdb
 */

@SuppressWarnings("unused")
public class TmdbMovies
{
	@SerializedName("results")
	private final ArrayList<TmdbMovie> mMovieList=new ArrayList<>();
	@SerializedName("page")
	private int mPage;
	@SerializedName("total_pages")
	private int mTotalPages;


	public ArrayList<TmdbMovie> getMovieList()
	{
		return mMovieList;
	}


	public void setPage(int page)
	{
		this.mPage=page;
	}


	public int getPage()
	{
		return mPage;
	}


	public void setTotalPages(int totalPages)
	{
		this.mTotalPages=totalPages;
	}


	public int getTotalPages()
	{
		return mTotalPages;
	}
}
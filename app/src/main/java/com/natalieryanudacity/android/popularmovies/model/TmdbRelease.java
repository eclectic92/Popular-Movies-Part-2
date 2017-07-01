package com.natalieryanudacity.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by natalier258 on 6/16/17.
 * <p>
 * Pojo for individual release from tmdb
 */

@SuppressWarnings("unused")
public class TmdbRelease
{

	@SerializedName("certification")
	private String mCertification;
	@SerializedName("release_date")
	private String mReleaseDate;
	@SerializedName("type")
	private int mReleaseType;


	public void setCertification(String certification)
	{
		this.mCertification=certification;
	}


	public String getCertification()
	{
		return mCertification;
	}


	public void setReleaseDate(String releaseDate)
	{
		this.mReleaseDate=releaseDate;
	}


	public String getReleaseDate()
	{
		return mReleaseDate;
	}


	public void setReleaseType(int releaseType)
	{
		this.mReleaseType=releaseType;
	}


	int getReleaseType()
	{
		return mReleaseType;
	}
}
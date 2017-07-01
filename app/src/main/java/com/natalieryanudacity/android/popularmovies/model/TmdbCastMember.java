package com.natalieryanudacity.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by natalier258 on 6/30/17.
 * <p>
 * simple pojo to hold cast member name
 */

@SuppressWarnings("unused")
class TmdbCastMember
{
	@SerializedName("name")
	private String mName;


	public void setName(String name)
	{
		this.mName=name;
	}


	public String getName()
	{
		return mName;
	}
}

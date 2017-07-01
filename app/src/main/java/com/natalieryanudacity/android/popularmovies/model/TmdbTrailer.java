package com.natalieryanudacity.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by natalier258 on 6/16/17.
 * <p>
 * pojo to hold trailer data from tmdb
 */

@SuppressWarnings("unused")
public class TmdbTrailer implements Parcelable
{
	@SerializedName("id")
	private String mTrailerId;
	@SerializedName("key")
	private String mTrailerKey;
	@SerializedName("name")
	private String mTrailerName;
	@SerializedName("site")
	private String mTrailerSite;
	@SerializedName("type")
	private String mTrailerType;


	//empty constructor
	public TmdbTrailer()
	{
	}


	//baseline constructor
	public TmdbTrailer(String id, String key, String name, String site, String type)
	{
		this.mTrailerId=id;
		this.mTrailerKey=key;
		this.mTrailerName=name;
		this.mTrailerSite=site;
		this.mTrailerType=type;
	}


	//constructor for parceler
	private TmdbTrailer(Parcel in)
	{
		mTrailerId=in.readString();
		mTrailerKey=in.readString();
		mTrailerName=in.readString();
		mTrailerSite=in.readString();
		mTrailerType=in.readString();
	}


	@Override
	public int describeContents()
	{
		return 0;
	}


	public static final Parcelable.Creator<TmdbTrailer> CREATOR=
			new Parcelable.Creator<TmdbTrailer>()
			{
				public TmdbTrailer createFromParcel(Parcel parcel)
				{
					return new TmdbTrailer(parcel);
				}


				public TmdbTrailer[] newArray(int size)
				{
					return new TmdbTrailer[size];
				}
			};


	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(mTrailerId);
		out.writeString(mTrailerKey);
		out.writeString(mTrailerName);
		out.writeString(mTrailerSite);
		out.writeString(mTrailerType);
	}


	// public getters -----------------------------
	public String getTrailerId()
	{
		return mTrailerId;
	}


	public String getTrailerKey()
	{
		return mTrailerKey;
	}


	public String getTrailerName()
	{
		return mTrailerName;
	}


	public String getTrailerSite()
	{
		return mTrailerSite;
	}


	public String getTrailerType()
	{
		return mTrailerType;
	}
}

package com.natalieryanudacity.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by natalier258 on 6/16/17.
 * <p>
 * Pojo to hold and parse trailer results from tmdb
 */

@SuppressWarnings("unused")
public class TmdbTrailers
{
	@SerializedName("results")
	private final ArrayList<TmdbTrailer> mTrailers=new ArrayList<>();


	public ArrayList<TmdbTrailer> getTrailers()
	{
		return mTrailers;
	}


	public ArrayList<TmdbTrailer> getTrailersBySite(String siteName)
	{

		ArrayList<TmdbTrailer> siteTrailers=new ArrayList<>();

		if (!mTrailers.isEmpty())
		{
			for (TmdbTrailer trailer : mTrailers)
			{
				if (trailer.getTrailerSite().equalsIgnoreCase(siteName))
				{
					siteTrailers.add(trailer);
				}
			}
		}

		return siteTrailers;
	}


	public ArrayList<TmdbTrailer> getTrailersBySiteAndType(String siteName, String[] trailerTypes)
	{

		ArrayList<TmdbTrailer> siteTrailers=new ArrayList<>();

		if (!mTrailers.isEmpty())
		{
			for (TmdbTrailer trailer : mTrailers)
			{
				if (trailer.getTrailerSite().equalsIgnoreCase(siteName))
				{
					List<String> allowedTypes=Arrays.asList(trailerTypes);
					if (allowedTypes.contains(trailer.getTrailerType()))
					{
						siteTrailers.add(trailer);
					}
				}
			}
		}
		return siteTrailers;
	}
}

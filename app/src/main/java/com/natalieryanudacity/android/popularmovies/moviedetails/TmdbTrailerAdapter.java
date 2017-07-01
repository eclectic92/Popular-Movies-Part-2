package com.natalieryanudacity.android.popularmovies.moviedetails;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.natalieryanudacity.android.popularmovies.R;
import com.natalieryanudacity.android.popularmovies.databinding.TrailerItemBinding;
import com.natalieryanudacity.android.popularmovies.model.TmdbTrailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by natalier258 on 6/16/17.
 * <p>
 * Adapter to manage trailers from tmdb
 */

@SuppressWarnings("unused")
public class TmdbTrailerAdapter extends RecyclerView.Adapter<TmdbTrailerAdapter.TmdbTrailerAdapterViewHolder>
{
	private ArrayList<TmdbTrailer> mTrailerList;
	private TrailerClickListener clickListener;
	private TrailerItemBinding mBinder;


	/**
	 * Default Constructor
	 */
	TmdbTrailerAdapter()
	{

	}


	interface TrailerClickListener
	{
		void onTrailerClick(View view, int position);
	}


	/**
	 * View holder for recycler view item
	 */
	class TmdbTrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{

		TmdbTrailerAdapterViewHolder(View view)
		{
			super(view);
			view.setOnClickListener(this);
		}


		@Override
		public void onClick(View view)
		{
			if (clickListener!=null)
			{
				clickListener.onTrailerClick(view, getAdapterPosition());
			}
		}

	}


	@Override
	public TmdbTrailerAdapter.TmdbTrailerAdapterViewHolder onCreateViewHolder(
			ViewGroup viewGroup, int viewType)
	{

		//set our bound layout
		Context context=viewGroup.getContext();
		int layoutIdForListItem=R.layout.trailer_item;
		LayoutInflater inflater=LayoutInflater.from(context);
		mBinder=DataBindingUtil.inflate(inflater, layoutIdForListItem, viewGroup, false);
		View view=mBinder.getRoot();
		return new TmdbTrailerAdapter.TmdbTrailerAdapterViewHolder(view);
	}


	@Override
	public void onBindViewHolder(TmdbTrailerAdapter.TmdbTrailerAdapterViewHolder viewHolder,
								 int position)
	{
		final Context context=mBinder.youtubeTrailerThumbnail.getContext();
		TmdbTrailer trailer=mTrailerList.get(position);
		mBinder.setTrailer(trailer);
		String thumbnailPath=context.getString(R.string.youtube_thumbnail_path)
				.replace("{0}", trailer.getTrailerKey());

		Picasso.with(context)
				.load(thumbnailPath)
				.into(mBinder.youtubeTrailerThumbnail);
	}


	@Override
	public int getItemCount()
	{
		if (null==mTrailerList)
		{
			return 0;
		}
		return mTrailerList.size();
	}


	TmdbTrailer getItem(int position)
	{
		if (mTrailerList!=null)
		{
			return mTrailerList.get(position);
		}
		else
		{
			return new TmdbTrailer();
		}
	}


	public ArrayList<TmdbTrailer> getTrailerList()
	{
		return mTrailerList;
	}


	void setTrailerData(ArrayList<TmdbTrailer> reviewData)
	{
		mTrailerList=reviewData;
		notifyDataSetChanged();
	}


	void setClickListener(TmdbTrailerAdapter.TrailerClickListener itemClickListener)
	{
		this.clickListener=itemClickListener;
	}
}

package com.natalieryanudacity.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.natalieryanudacity.android.popularmovies.model.TmdbMovie;
import com.natalieryanudacity.android.popularmovies.utils.storage.BitmapIOUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Natalie Ryan on 6/3/17.
 *
 * Adapted for recycler view that holds movie information for the main activity's movie poster grid
 */
@SuppressWarnings("unused")
public class TmdbMovieAdapter extends RecyclerView.Adapter<TmdbMovieAdapter.TmdbMovieAdapterViewHolder> {

    private ArrayList<TmdbMovie> mMovieList = new ArrayList<>();
    private ItemClickListener clickListener;
    private boolean isLoadingAdded = false;
    private static final int LOADING = 1;

    /**
     * Default Constructor
     */
    public TmdbMovieAdapter() {
    }

    /**
     * Click listener for individual item in movie grid. MUST be implemented by calling class
     */
    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    /**
     * View holder for recycler view item
     */
    public class TmdbMovieAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final ImageView mMoviePosterImageView;
        public final TextView mMoviePosterTitleView;
        public TmdbMovie mSingleMovie;

        public TmdbMovieAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mMoviePosterImageView = (ImageView) view.findViewById(R.id.movie_poster_image);
            mMoviePosterTitleView = (TextView) view.findViewById(R.id.movie_poster_title);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }

    }

    @Override
    public TmdbMovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_poster_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TmdbMovieAdapterViewHolder(view);
    }

    @Override
    public void onViewRecycled(TmdbMovieAdapterViewHolder tmdbMovieAdapterViewHolder) {
        super.onViewRecycled(tmdbMovieAdapterViewHolder);
        tmdbMovieAdapterViewHolder.mMoviePosterImageView.setImageDrawable(null);
        tmdbMovieAdapterViewHolder.mMoviePosterImageView.setImageBitmap(null);
        tmdbMovieAdapterViewHolder.mMoviePosterTitleView.setText(null);
        tmdbMovieAdapterViewHolder.mMoviePosterTitleView.setVisibility(View.GONE);
    }

    @Override
    public void onBindViewHolder(final TmdbMovieAdapterViewHolder tmdbMovieAdapterViewHolder,
                                 int position) {

        final Context context = tmdbMovieAdapterViewHolder.mMoviePosterImageView.getContext();
        final TmdbMovie singleMovie = mMovieList.get(position);
        //tmdbMovieAdapterViewHolder.mSingleMovie = singleMovie;
        int favoriteStatus = singleMovie.getFavoriteStatus();
        final String moviePosterPath = singleMovie.getPosterPath(context);
        boolean bitmapLoaded = false;

        if(favoriteStatus == TmdbMovie.FAVORITE_STATUS_TRUE){
            //try to load the saved poster from file system
            String posterImagePath = singleMovie.getPosterImagePath();
            if(posterImagePath != null && !posterImagePath.isEmpty()){
                Bitmap posterImage = BitmapIOUtil.readBitmap(posterImagePath,
                        tmdbMovieAdapterViewHolder.mMoviePosterImageView.getContext());
                if(posterImage != null){
                    tmdbMovieAdapterViewHolder.mMoviePosterImageView.setImageBitmap(posterImage);
                    bitmapLoaded = true;
                }
            }
        }

        if(!bitmapLoaded) {
            if(moviePosterPath == null){
                Drawable emptyFrame = context.getDrawable(R.drawable.ic_empty_poster);
                tmdbMovieAdapterViewHolder.mMoviePosterImageView.setImageDrawable(emptyFrame);
                tmdbMovieAdapterViewHolder.mMoviePosterTitleView.
                        setText(singleMovie.getMovieTitle());
                tmdbMovieAdapterViewHolder.mMoviePosterTitleView
                        .setVisibility(View.VISIBLE);
            }else{
                loadImageFromPicasso(moviePosterPath, tmdbMovieAdapterViewHolder);
            }

        }
    }

    private void loadImageFromPicasso(final String imagePath,
                                      final TmdbMovieAdapterViewHolder tmdbMovieAdapterViewHolder){
        Picasso.with(tmdbMovieAdapterViewHolder.mMoviePosterImageView.getContext())
                .load(imagePath)
                .into(tmdbMovieAdapterViewHolder.mMoviePosterImageView,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                //nothing special to do here
                            }

                            @Override
                            public void onError() {
                                tmdbMovieAdapterViewHolder.mMoviePosterTitleView
                                        .setVisibility(View.VISIBLE);
                            }
                        }
                );
    }

    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mMovieList.size() - 1 && isLoadingAdded) ? LOADING : position;

    }

    public TmdbMovie getItem(int position) {
        if(mMovieList != null) {
            return mMovieList.get(position);
        }else{
            return new TmdbMovie();
        }
    }

    public ArrayList<TmdbMovie> getMovieList() {
        return mMovieList;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    protected void setMovieData(ArrayList<TmdbMovie> movieData) {
        mMovieList = movieData;
        notifyDataSetChanged();
    }

    protected void add(TmdbMovie tmdbMovie) {
        mMovieList.add(tmdbMovie);
        notifyItemInserted(mMovieList.size() - 1);
    }

    protected void addAll(ArrayList<TmdbMovie> movieList) {
        mMovieList.addAll(movieList);
        notifyDataSetChanged();
    }

    protected void addAll(Cursor cursor) {
        mMovieList.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(MovieGridFragment.INDEX_TMDB_TMDB_ID);
                String title = cursor.getString(MovieGridFragment.INDEX_TMDB_TITLE);
                String releaseDate = cursor.getString(MovieGridFragment.INDEX_TMDB_RELEASE_DATE);
                String posterPath = cursor.getString(MovieGridFragment.INDEX_TMDB_POSTER_PATH);
                String backdropPath = cursor.getString(MovieGridFragment.INDEX_TMDB_BACKDROP_PATH);
                String overview = cursor.getString(MovieGridFragment.INDEX_TMDB_OVERVIEW);
                String voteAverage = cursor.getString(MovieGridFragment.INDEX_TMDB_VOTE_AVERAGE);
                String runningTime = cursor.getString(MovieGridFragment.INDEX_TMDB_RUNNING_TIME);
                String genres = cursor.getString(MovieGridFragment.INDEX_TMDB_GENRES);
                String certification = cursor.getString(MovieGridFragment.INDEX_TMDB_CERTIFICATION);
                String tagline = cursor.getString(MovieGridFragment.INDEX_TMDB_TAGLINE);
                String posterImagePath = cursor.getString(MovieGridFragment.INDEX_TMDB_POSTER_FILE_PATH);
                String bannerImagePath = cursor.getString(MovieGridFragment.INDEX_TMDB_BANNER_FILE_PATH);
                String castList = cursor.getString(MovieGridFragment.INDEX_TMDB_CAST_LIST);
                int favoriteStatus = TmdbMovie.FAVORITE_STATUS_TRUE;

                TmdbMovie singleMovie = new TmdbMovie(id, title, releaseDate, posterPath,
                        backdropPath, overview, voteAverage, runningTime,  genres,
                        certification, tagline, favoriteStatus, posterImagePath, bannerImagePath, castList);
                mMovieList.add(singleMovie);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    protected void remove(TmdbMovie tmdbMovie) {
        int position = mMovieList.indexOf(tmdbMovie);
        if (position > -1) {
            mMovieList.remove(position);
            notifyItemRemoved(position);
        }
    }

    protected void clear() {
        isLoadingAdded = false;
        mMovieList.clear();
        notifyDataSetChanged();
    }


    protected boolean isEmpty() {
        return getItemCount() == 0;
    }

}
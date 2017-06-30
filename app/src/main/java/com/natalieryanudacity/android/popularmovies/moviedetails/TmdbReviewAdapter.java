package com.natalieryanudacity.android.popularmovies.moviedetails;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.natalieryanudacity.android.popularmovies.R;
import com.natalieryanudacity.android.popularmovies.databinding.ReviewItemBinding;
import com.natalieryanudacity.android.popularmovies.model.TmdbReview;

import java.util.ArrayList;

/**
 * Created by natalier258 on 6/15/17.
 *
 * Adapter for recycler view that holds tmdb review information

 */

@SuppressWarnings("unused")
public class TmdbReviewAdapter extends RecyclerView.Adapter<TmdbReviewAdapter.TmdbReviewAdapterViewHolder> {

    private ArrayList<TmdbReview> mReviewList;
    private ReviewClickListener clickListener;
    private ReviewItemBinding mBinder;

    /**
     * Default Constructor
     */
    TmdbReviewAdapter() {
    }

    public interface ReviewClickListener {
        void onReviewClick(View view, int position);
    }

    /**
     * View holder for recycler view item
     */
    class TmdbReviewAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TmdbReviewAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onReviewClick(view, getAdapterPosition());
        }

    }

    @Override
    public TmdbReviewAdapter.TmdbReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
		mBinder = DataBindingUtil.inflate(inflater,layoutIdForListItem,viewGroup,false);
        View view = mBinder.getRoot();
        return new TmdbReviewAdapter.TmdbReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TmdbReviewAdapter.TmdbReviewAdapterViewHolder viewHolder,
                                 int position) {
        String reviewAuthor = mBinder.reviewAuthorTv.getContext()
                .getString(R.string.details_review_author) + " " + mReviewList.get(position).getReviewAuthor() + ":";
		mBinder.reviewAuthorTv.setText(reviewAuthor);

        String reviewContent = mReviewList.get(position).getReviewContent().replaceAll("(\\r|\\n)", " ");
        mBinder.reviewContentTv.setText(reviewContent);
    }

    @Override
    public int getItemCount() {
        if (null == mReviewList) return 0;
        return mReviewList.size();
    }

    public TmdbReview getItem(int position) {
        if(mReviewList != null) {
            return mReviewList.get(position);
        }else{
            return new TmdbReview();
        }
    }

    public ArrayList<TmdbReview> getMovieList() {
        return mReviewList;
    }

    void setReviewData(ArrayList<TmdbReview> reviewData) {
        mReviewList = reviewData;
        notifyDataSetChanged();
    }
    public void setClickListener(ReviewClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
}
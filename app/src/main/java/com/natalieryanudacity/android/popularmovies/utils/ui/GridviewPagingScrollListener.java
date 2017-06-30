

package com.natalieryanudacity.android.popularmovies.utils.ui;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by natalier258 on 6/17/17.
 *
 * Work in progress - scroller listener
 */

@SuppressWarnings("unused")
public abstract class GridviewPagingScrollListener extends RecyclerView.OnScrollListener {

    private final GridLayoutManager layoutManager;
    private boolean userScrolled;

    public GridviewPagingScrollListener(GridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        userScrolled = false;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        userScrolled = (newState != RecyclerView.SCROLL_STATE_IDLE);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition > 0 && userScrolled) {
                loadMoreItems();
            }
        }
    }

    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

}

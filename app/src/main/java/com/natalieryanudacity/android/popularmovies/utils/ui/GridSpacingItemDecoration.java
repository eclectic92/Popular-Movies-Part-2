package com.natalieryanudacity.android.popularmovies.utils.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Put even spacing around all of the items in the movie grid view
 * <p>
 * Note: After looking for half a day for a better/built-in way to achieve this, this remains
 * the best single solution I have found, so all credit goes to the original author of this
 * function!
 * <p>
 * Code originally from, and taken as-is
 * https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing/30701422#30701422
 */

@SuppressWarnings("SameParameterValue")
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration
{

	private final int spanCount;
	private final int spacing;
	private final boolean includeEdge;


	/**
	 * Adds spacing between items on grid view
	 *
	 * @param spanCount   count of colums in grid view
	 * @param spacing     spacing between columns
	 * @param includeEdge include spacings around edges
	 */
	public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge)
	{
		this.spanCount=spanCount;
		this.spacing=spacing;
		this.includeEdge=includeEdge;
	}


	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
	{
		int position=parent.getChildAdapterPosition(view); // item position
		int column=position%spanCount; // item column

		if (includeEdge)
		{
			outRect.left=spacing-column*spacing/spanCount; // spacing - column * ((1f / spanCount) * spacing)
			outRect.right=(column+1)*spacing/spanCount; // (column + 1) * ((1f / spanCount) * spacing)

			if (position<spanCount)
			{ // top edge
				outRect.top=spacing;
			}
			outRect.bottom=spacing; // item bottom
		}
		else
		{
			outRect.left=column*spacing/spanCount; // column * ((1f / spanCount) * spacing)
			outRect.right=spacing-(column+1)*spacing/spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
			if (position>=spanCount)
			{
				outRect.top=spacing; // item top
			}
		}
	}
}
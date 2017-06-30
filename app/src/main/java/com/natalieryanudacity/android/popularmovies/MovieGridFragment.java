package com.natalieryanudacity.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.natalieryanudacity.android.popularmovies.data.TmdbMovieContract;
import com.natalieryanudacity.android.popularmovies.databinding.FragmentMovieGridBinding;
import com.natalieryanudacity.android.popularmovies.model.TmdbMovie;
import com.natalieryanudacity.android.popularmovies.model.TmdbMovies;
import com.natalieryanudacity.android.popularmovies.network.ConnectionChecker;
import com.natalieryanudacity.android.popularmovies.network.TmdbClient;
import com.natalieryanudacity.android.popularmovies.network.TmdbInterface;
import com.natalieryanudacity.android.popularmovies.utils.ui.GridSpacingItemDecoration;
import com.natalieryanudacity.android.popularmovies.utils.ui.GridviewPagingScrollListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment to render movie posters in a grid based on movile list data returned from
 * searching the popular or top_rated API endpoints at tmdb.org
 *
 * Will render in a two-column grid in portait and a three-column grid in landscape
 * Will show an error text view in failed load/no movies found/no internet connnection states
 * Saves user's state on rotation or clicking the "home" button so APIs are not repeatedly called
 * and will save place in scrolled view on rotation
 *
 * Will load additional movies from tmdb on hitting bottom of the scroll window if not in favorites view
 * Maximum pages to fetch and scroll through set in static member TOTAL_PAGES
 */
public class MovieGridFragment extends Fragment implements TmdbMovieAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = MovieGridFragment.class.getSimpleName();

    private static final String MOVIES = "MOVIES";
    private static final String SORT = "SORT";
    private static final String GRID_LAYOUT = "GRID_LAYOUT";
    private static final String CURRENT_PAGE = "CURRENT_PAGE";
    private static final int START_PAGE = 1;
    private static final int TOTAL_PAGES = 10;
    private static final int FAVORITES_LOADER = 20;

    private static final String[] MAIN_TMDB_PROJECTION = {
            TmdbMovieContract.TmdbMovieEntry.COLUMN_TMDB_ID,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_TITLE,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_RELEASE_DATE,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_POSTER_PATH,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_BANNER_PATH,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_OVERVIEW,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_TAGLINE,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_VOTE_AVERAGE,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_RUNNING_TIME,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_GENRES,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_CERTIFICATION,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_POSTER_FILE_PATH,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_BANNER_FILE_PATH,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_CAST_LIST
    };

    public static final int INDEX_TMDB_TMDB_ID = 0;
    public static final int INDEX_TMDB_TITLE = 1;
    public static final int INDEX_TMDB_RELEASE_DATE = 2;
    public static final int INDEX_TMDB_POSTER_PATH = 3;
    public static final int INDEX_TMDB_BACKDROP_PATH = 4;
    public static final int INDEX_TMDB_OVERVIEW = 5;
    public static final int INDEX_TMDB_TAGLINE = 6;
    public static final int INDEX_TMDB_VOTE_AVERAGE = 7;
    public static final int INDEX_TMDB_RUNNING_TIME = 8;
    public static final int INDEX_TMDB_GENRES = 9;
    public static final int INDEX_TMDB_CERTIFICATION = 10;
    public static final int INDEX_TMDB_POSTER_FILE_PATH = 11;
    public static final int INDEX_TMDB_BANNER_FILE_PATH = 12;
	public static final int INDEX_TMDB_CAST_LIST = 13;

	private GridLayoutManager mLayoutManager;
    private GridviewPagingScrollListener mScrollListener;
    private boolean mIsLoading = false;
    private boolean mIsLastPage = false;
    private int mCurrentPage = START_PAGE;

	private FragmentMovieGridBinding mBinder;

    private TmdbMovieAdapter mTmdbMovieAdapter;
    private String mSortType;

    private TmdbInterface mTmdbService;

    private OnMovieSelected mCallback;

    /**
     * Default constructor
     */
    public MovieGridFragment() {
    }

    public interface OnMovieSelected{
        void onMovieSelected(TmdbMovie singleMovie);
    }
    /**
     * Turn on option menu on create
     *
     * @param savedInstanceState saved instance state bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            //let's see if we have a saved preference for sort options
            SharedPreferences sharedPref = getActivity()
                    .getPreferences(HomeScreenActivity.MODE_PRIVATE);
            this.mSortType = sharedPref.getString(SORT, TmdbInterface.SORT_POPULAR);
        }
        setHasOptionsMenu(true);
    }


	/**
	 * Ensure that callback to movie selected click in implemented
	 *
	 * @param context context
	 */
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		// This makes sure that the host activity has implemented the callback interface
		// If not, it throws an exception
		try {
			mCallback = (OnMovieSelected) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnMovieSelected");
		}
	}

    /**
     * Creates the main movie grid view
     * will restore user's previous state and selected menu options if available
     * otherwise will call tmdb.org to get list of most popular movies
     *
     * @param inflater              view inflater
     * @param container             view container
     * @param savedInstanceState    saved instance as bundle
     * @return                      inflated view of movie grid
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		//set our bound layout
		mBinder = DataBindingUtil.inflate(inflater,R.layout.fragment_movie_grid, container, false);
		View rootView = mBinder.getRoot();

        // create a new layout manager with the right number of columns for device's orientation
        int colCount = getColumnCount();
        mLayoutManager = new GridLayoutManager(getActivity(),
                colCount, GridLayoutManager.VERTICAL, false);

        // set all the display properties on the grid layout
        mBinder.recyclerviewMovielist.addItemDecoration(new GridSpacingItemDecoration(colCount,
                getResources().getDimensionPixelSize(R.dimen.movie_grid_margin_spacing), true));

		//set the color for the spinner
		if(mBinder.gridLoaderPb != null){
			mBinder.gridLoaderPb.getIndeterminateDrawable().setColorFilter(
					ContextCompat.getColor(getContext(), R.color.colorAccent),
					PorterDuff.Mode.MULTIPLY);
		}

		mBinder.recyclerviewMovielist.setLayoutManager(mLayoutManager);

        mScrollListener = createScrollingListener();

		mBinder.recyclerviewMovielist.addOnScrollListener(mScrollListener);

        // create the adapter and bind it to the grid
        mTmdbMovieAdapter = new TmdbMovieAdapter();
        mTmdbMovieAdapter.setClickListener(this);
		mBinder.recyclerviewMovielist.setAdapter(mTmdbMovieAdapter);

        mBinder.errorRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsLoading = false;
                mCurrentPage = START_PAGE;
                mTmdbMovieAdapter.clear();
                loadMovieSearchData();
            }
        });

        mBinder.errorViewFavoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFavorites();
                getActivity().invalidateOptionsMenu();
            }
        });

        //call tmdb api to fetch the movie list if there's no saved state
        mTmdbService = TmdbClient.getClient().create(TmdbInterface.class);

        if (savedInstanceState != null) {
            mSortType = savedInstanceState.getString(SORT);
            if(savedInstanceState.containsKey(MOVIES)){
                ArrayList<TmdbMovie> movieList = savedInstanceState.getParcelableArrayList(MOVIES);
                mTmdbMovieAdapter.setMovieData(movieList);
            }
            if(savedInstanceState.containsKey(GRID_LAYOUT)){
                Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(GRID_LAYOUT);
				mBinder.recyclerviewMovielist.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            }
            if(savedInstanceState.containsKey(CURRENT_PAGE)){
                mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE);
                mIsLastPage = isOnLastPage();
            }
            updateUi();
        } else{
            if(mSortType == null || mSortType.isEmpty()) {
                mSortType = TmdbInterface.SORT_POPULAR;
            }
            if(mSortType.equals(TmdbInterface.SORT_FAVORITES)){
                viewFavorites();
            }else{
                loadMovieSearchData();
            }
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<TmdbMovie> movieList = mTmdbMovieAdapter.getMovieList();

        if (movieList != null && !movieList.isEmpty()) {
            outState.putParcelable(GRID_LAYOUT, mBinder.recyclerviewMovielist.getLayoutManager().onSaveInstanceState());
            outState.putParcelableArrayList(MOVIES, movieList);
        }
        outState.putString(SORT, mSortType);
        outState.putInt(CURRENT_PAGE,mCurrentPage);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_grid, menu);
        super.onCreateOptionsMenu(menu, inflater);
        if(mSortType != null) {
            switch (mSortType){
                case TmdbInterface.SORT_POPULAR:
                    menu.findItem(R.id.menu_item_popular).setChecked(true);
                    break;
                case TmdbInterface.SORT_TOP_RATED:
                    menu.findItem(R.id.menu_item_top_rated).setChecked(true);
                    break;
                case TmdbInterface.SORT_NOW_PLAYING:
                    menu.findItem(R.id.menu_item_now_playing).setChecked(true);
                    break;
                case TmdbInterface.SORT_UPCOMING:
                    menu.findItem(R.id.menu_item_upcoming).setChecked(true);
                    break;
                case TmdbInterface.SORT_FAVORITES:
                    menu.findItem(R.id.menu_item_favorites).setChecked(true);
                default:
                    //do nothing
            }
        }else {
            menu.findItem(R.id.menu_item_popular).setChecked(true);
        }
    }

    /**
     * Allows user to switch between viewing popular, top rated, upcoming, now playing,
	 * or saved favorites in grid
     *
     * @param item  menu item clicked
     * @return      boolean that click handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //detstroy the favories loader if we're not going to view them right noe
        if(mSortType.equals(TmdbInterface.SORT_FAVORITES)) {
            getActivity().getSupportLoaderManager().destroyLoader(FAVORITES_LOADER);
			mBinder.recyclerviewMovielist.addOnScrollListener(mScrollListener);
        }

        switch (item.getItemId()) {
            case R.id.menu_item_popular:
                mSortType = TmdbInterface.SORT_POPULAR;
                storeSortStatus();
                clearPosterView();
                loadMovieSearchData();
                item.setChecked(true);
                return true;
            case R.id.menu_item_top_rated:
                mSortType = TmdbInterface.SORT_TOP_RATED;
                storeSortStatus();
                clearPosterView();
                loadMovieSearchData();
                item.setChecked(true);
                return true;
            case R.id.menu_item_now_playing:
                mSortType = TmdbInterface.SORT_NOW_PLAYING;
                storeSortStatus();
                clearPosterView();
                loadMovieSearchData();
                item.setChecked(true);
                return true;
            case R.id.menu_item_upcoming:
                mSortType = TmdbInterface.SORT_UPCOMING;
                storeSortStatus();
                clearPosterView();
                loadMovieSearchData();
                item.setChecked(true);
                return true;
            case R.id.menu_item_favorites:
                mSortType = TmdbInterface.SORT_FAVORITES;
                storeSortStatus();
                viewFavorites();
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


	/**
	 * switches to favorites view
	 */
	private void viewFavorites(){
		mBinder.recyclerviewMovielist.removeOnScrollListener(mScrollListener);
        mSortType = TmdbInterface.SORT_FAVORITES;
        clearPosterView();
        getActivity().getSupportLoaderManager().initLoader(FAVORITES_LOADER, null, this);
    }

    /**
     * Initiates retrofit call to get data from tmdb.org
     * Sets adapter's list of movies and shows the movie grid
     *
     */
    private void loadMovieSearchData() {
        //if something happened to our internet connection, time to pop the error screen
        if(!ConnectionChecker.isOnline(getActivity())){
            showErrorMessage();
            return;
        }

        mBinder.gridErrorLayout.setVisibility(View.GONE);
		mBinder.gridLoaderPb.setVisibility(View.VISIBLE);

        if(mTmdbService != null){
            callMovieListApi().enqueue(new Callback<TmdbMovies>() {
                @Override
                public void onResponse(Call<TmdbMovies>call, Response<TmdbMovies> response) {
                    Log.d(TAG, "loaded results page " + mCurrentPage);
                    if(response.body() != null){
                        if(mCurrentPage > START_PAGE){
                            mIsLoading = false;
                        }
                        ArrayList<TmdbMovie> movieArrayList = response.body().getMovieList();
                        mTmdbMovieAdapter.addAll(movieArrayList);
                        mIsLastPage = isOnLastPage();
                    }
                    updateUi();
                }

                @Override
                public void onFailure(Call<TmdbMovies>call, Throwable t) {
                    showErrorMessage();
                }
            });
        } else {
            updateUi();
        }

    }

    /**
     * Utility function to show movie grid or error based on state
     */
    private void updateUi(){
        if(mTmdbMovieAdapter != null){
            if (mTmdbMovieAdapter.getItemCount() != 0){
                showMovieGrid();
            }else{
                showErrorMessage();
            }
        }else{
            showErrorMessage();
        }
    }

    /**
     * Utility function to hide loading spinner
     */
    private void showMovieGrid(){
		mBinder.gridLoaderPb.setVisibility(View.GONE);
		mBinder.recyclerviewMovielist.setVisibility(View.VISIBLE);
		mBinder.gridErrorLayout.setVisibility(View.GONE);
    }

    /**
     * Utility function to show appropriate error message based on state
     */
    private void showErrorMessage(){
        String errMessage;
		mBinder.gridLoaderPb.setVisibility(View.GONE);
		mBinder.recyclerviewMovielist.setVisibility(View.GONE);
        if(mSortType.equals(TmdbInterface.SORT_FAVORITES)){
            errMessage = getString(R.string.grid_no_favorites_added);
            mBinder.gridFavoritesAvailable.setVisibility(View.GONE);
			mBinder.errorRetryBtn.setVisibility(View.GONE);
			mBinder.errorViewFavoritesBtn.setVisibility(View.GONE);
        }else{
            if(ConnectionChecker.isOnline(getActivity())){
                errMessage = getString(R.string.grid_fetch_error);
            }else{
                errMessage = getString(R.string.grid_no_connection);
            }
			mBinder.gridFavoritesAvailable.setVisibility(View.VISIBLE);
			mBinder.errorRetryBtn.setVisibility(View.VISIBLE);
			mBinder.errorViewFavoritesBtn.setVisibility(View.VISIBLE);
        }
        mBinder.gridErrorTv.setText(errMessage);
		mBinder.gridErrorLayout.setVisibility(View.VISIBLE);
    }


	/**
	 * stores the last selected sort type in shared preferences
	 */
	private void storeSortStatus(){
        SharedPreferences sharedPref = getActivity().getPreferences(HomeScreenActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SORT, mSortType);
        editor.apply();
    }

    /**
     * Utility function get number of columns to display
     *
     * @return number of columns to display
     */
    private int getColumnCount() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return 2;
        }else{
            return 3;
        }
    }
    /**
     * sends intent to movie detail view when user clicks on a movie poster
     * will send all basic movie details as a parcelable
     *
     * @param view      calling view
     * @param position  recycler view position of clicked poster
     */
    @Override
    public void onClick(View view, int position) {
        final TmdbMovie singleMovie = mTmdbMovieAdapter.getItem(position);
		mCallback.onMovieSelected(singleMovie);
    }


	/**
	 * Retrofit call to get movie list from tmdb
	 *
	 * @return array of movies fetched from tmdb
	 */
	private Call<TmdbMovies> callMovieListApi() {
        if(mSortType.equals(TmdbInterface.SORT_UPCOMING) ||
                mSortType.equals(TmdbInterface.SORT_NOW_PLAYING)){
            return mTmdbService.getMovieList(
                    mSortType,
                    BuildConfig.TMDB_API_KEY,
                    getString(R.string.tmdb_region),
                    mCurrentPage
            );
        }else {
            return mTmdbService.getMovieList(
                    mSortType,
                    BuildConfig.TMDB_API_KEY,
                    mCurrentPage
            );
        }
    }


	/**
	 * clears out the current gridview
	 */
	private void clearPosterView(){
        mCurrentPage = START_PAGE;
        mIsLastPage = isOnLastPage();
        mTmdbMovieAdapter.clear();

    }


	/**
	 * See if user has reached last page of movies to display
	 *
	 * @return is user on last page of data to be fetched
	 */
	private boolean isOnLastPage(){
        return mCurrentPage >= TOTAL_PAGES;
    }


	/**
	 * Creates scrolling listener for the gridview to enable paging
	 *
	 * @return scrolling listener
	 */
	private GridviewPagingScrollListener createScrollingListener(){

            return new GridviewPagingScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                mIsLoading = true;
                mCurrentPage += 1;
                loadMovieSearchData();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return mIsLastPage;
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }
        };
    }

    // cursor loader to handle favorites data---------------------------------
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs) {

        switch (loaderId) {
            case FAVORITES_LOADER:
                    Uri movieUri = TmdbMovieContract.TmdbMovieEntry.CONTENT_URI;
                    return new CursorLoader(
                            getContext(),
                            movieUri,
                            MAIN_TMDB_PROJECTION,
                            null,
                            null,
                            null
                    );
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            mTmdbMovieAdapter.addAll(data);
            updateUi();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing to do here
    }
}
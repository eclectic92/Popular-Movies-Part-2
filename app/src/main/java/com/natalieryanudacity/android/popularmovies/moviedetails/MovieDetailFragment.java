package com.natalieryanudacity.android.popularmovies.moviedetails;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.natalieryanudacity.android.popularmovies.BuildConfig;
import com.natalieryanudacity.android.popularmovies.R;
import com.natalieryanudacity.android.popularmovies.data.TmdbMovieContract;
import com.natalieryanudacity.android.popularmovies.databinding.FragmentMovieDetailBinding;
import com.natalieryanudacity.android.popularmovies.model.TmdbMovie;
import com.natalieryanudacity.android.popularmovies.model.TmdbMovieDetails;
import com.natalieryanudacity.android.popularmovies.model.TmdbRelease;
import com.natalieryanudacity.android.popularmovies.model.TmdbReview;
import com.natalieryanudacity.android.popularmovies.model.TmdbReviews;
import com.natalieryanudacity.android.popularmovies.model.TmdbTrailer;
import com.natalieryanudacity.android.popularmovies.model.TmdbTrailers;
import com.natalieryanudacity.android.popularmovies.network.ConnectionChecker;
import com.natalieryanudacity.android.popularmovies.network.TmdbClient;
import com.natalieryanudacity.android.popularmovies.network.TmdbInterface;
import com.natalieryanudacity.android.popularmovies.utils.storage.BitmapIOUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Natalie Ryan on 6/7/17
 *
 * Fragment to display all of the details for a particular movie selected on the
 * main activity. All of the basic movie info such as title, overview, image paths, and vote
 * average should be passed to this fragment in the calling intent, and only "extended details"
 * such as the precise theatrical release date, MPAA rating, and running time will need to be
 * fetched from a new call to the /movie/[movie_id] endpoint at tmdb.org
 * <p>
 * Since basic movie info is passed in, the "bare bones" info will still render even if
 * internet connectivity is lost between main activity loading and the details
 * display being created.
 *
 * Also saves poster an banner images to local filesystem for "favorited" movies and calls
 * tmdb for any associated trailers and reviews
 */
public class MovieDetailFragment extends Fragment implements TmdbReviewAdapter.ReviewClickListener,
        TmdbTrailerAdapter.TrailerClickListener, AddFavoriteAsyncTask.AddFavoriteListener,
        RemoveFavoriteAsyncTask.RemoveFavoriteListener, LoaderManager.LoaderCallbacks<Cursor>,
        UpdateFavoriteAsyncTask.UpdateFavoriteListener {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();

    private static final int FAVORITE_LOADER = 11;
    private static final int FAVORITE_DETAILS_LOADER = 33;

    private static final String[] TMDB_EXT_DATA_PROJECTION = {
            TmdbMovieContract.TmdbMovieEntry.COLUMN_RELEASE_DATE,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_TAGLINE,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_RUNNING_TIME,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_GENRES,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_CERTIFICATION,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_POSTER_FILE_PATH,
            TmdbMovieContract.TmdbMovieEntry.COLUMN_BANNER_FILE_PATH,
			TmdbMovieContract.TmdbMovieEntry.COLUMN_CAST_LIST
	};

    private static final String[] TMDB_FAVORITE_PROJECTION = {
            TmdbMovieContract.TmdbMovieEntry.COLUMN_TMDB_ID
    };

    private static final int INDEX_TMDB_RELEASE_DATE = 0;
    private static final int INDEX_TMDB_TAGLINE = 1;
    private static final int INDEX_TMDB_RUNNING_TIME = 2;
    private static final int INDEX_TMDB_GENRES = 3;
    private static final int INDEX_TMDB_CERTIFICATION = 4;
    private static final int INDEX_TMDB_POSTER_FILE_PATH = 5;
    private static final int INDEX_TMDB_BANNER_FILE_PATH = 6;
	private static final int INDEX_TMDB_CAST_LIST = 7;

	public static final String MOVIE = "MOVIE";
    private static final String REVIEWS = "REVIEWS";
    private static final String TRAILERS = "TRAILERS";

    private static final String POSTER_FILE_SUFFIX = "_poster.jpg";
    private static final String BANNER_FILE_SUFFIX = "_banner.jpg";

    private TmdbMovie mSingleMovie;

    //layouts and views that aren't bound to the fragmnet
    private ImageView mMovieBannerImageView;

    // network
    private TmdbInterface mTmdbService;

    // for reviews
    private ArrayList<TmdbReview> mReviewArrayList;
    private TmdbReviewAdapter mReviewAdapter;

    // for trailers
    private ArrayList<TmdbTrailer> mTrailersArrayList;
    private TmdbTrailerAdapter mTrailerAdapter;
    private ShareActionProvider mTrailerShareProvider;
    private boolean mHasTrailers = false;

    //uri
    private Uri mFavoriteUri;

    //binder
    private FragmentMovieDetailBinding mBinder;

	//retrofit calls
	private Call<TmdbMovieDetails> mMovieDetailsCall;
	private Call<TmdbReviews> mReviewsCall;
	private Call<TmdbTrailers> mTrailersCall;

    //Default empty constructor
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(MOVIE)){
                mSingleMovie = savedInstanceState.getParcelable(MOVIE);
            }
            if(savedInstanceState.containsKey(REVIEWS)){
                mReviewArrayList = savedInstanceState.getParcelableArrayList(REVIEWS);
            }
            if(savedInstanceState.containsKey(TRAILERS)){
                mTrailersArrayList = savedInstanceState.getParcelableArrayList(TRAILERS);
            }
        } else {
			Bundle args = getArguments();
			if(args !=null){
				if (args.containsKey(MOVIE)) {
					mSingleMovie=getArguments().getParcelable(MOVIE);
				}
			}
			else {
				Intent intent=getActivity().getIntent();
				if (intent!=null)
				{
					mSingleMovie=intent.getParcelableExtra(MOVIE);
				}
			}
		}
        setHasOptionsMenu(true);
    }


	/**
	 * Loads the movie banner image into either the collapsing toolbar (if we're in MovieDetailActivity)
	 * or into the banner image view that's part of the main scroller if we're not in that specific activity
	 *
	 * Will load the banner image saved to the file system if we're coming in from the Favorites grid
	 * If not, we'll let picasso handle loading it fresh
	 *
	 * @param savedInstanceState saved state
	 */
	@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity detailActivity = getActivity();

		CollapsingToolbarLayout collapsingToolbar=
				(CollapsingToolbarLayout)detailActivity.findViewById(R.id.collapsing_toolbar);

		if(detailActivity instanceof MovieDetailActivity) {
			if (collapsingToolbar!=null && mSingleMovie!=null) {
				collapsingToolbar.setTitle(mSingleMovie.getMovieTitle());
				collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
				collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
				mMovieBannerImageView=(ImageView)detailActivity.findViewById(R.id.details_activity_movie_banner);
			}
		}else {
			mMovieBannerImageView=(ImageView)detailActivity.findViewById(R.id.details_movie_banner);
		}

		if (mMovieBannerImageView !=null && mSingleMovie!=null) {

			boolean bitmapLoaded=false;

			if (mSingleMovie.getFavoriteStatus()==TmdbMovie.FAVORITE_STATUS_TRUE) {
				//try to load the saved banner from file system
				String bannerImagePath=mSingleMovie.getBannerImagePath();
				if (bannerImagePath!=null && !bannerImagePath.isEmpty()) {
					Bitmap bannerImage=BitmapIOUtil
							.readBitmap(bannerImagePath, getActivity());
					if (bannerImage!=null) {
						mMovieBannerImageView.setImageBitmap(bannerImage);
						bitmapLoaded=true;
					}
				}
			}

			if (!bitmapLoaded) {
				if (mSingleMovie.getBannerPath()!=null &&
						!mSingleMovie.getBannerPath().isEmpty()) {
					Picasso.with(detailActivity)
							.load(mSingleMovie.getBannerPath(detailActivity))
							.into(mMovieBannerImageView);
				}
				else {
					AppBarLayout appBarLayout=(AppBarLayout)getActivity()
							.findViewById(R.id.details_appbar);
					if (appBarLayout!=null) {
						appBarLayout.setExpanded(false, false);
					}
					if(detailActivity.findViewById(R.id.details_movie_banner) != null){
						mBinder.detailsMovieBanner.setVisibility(View.GONE);
					}
				}
			}
		}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		//set our bound layout
        mBinder = DataBindingUtil.inflate(inflater,R.layout.fragment_movie_detail, container, false);
        View rootView = mBinder.getRoot();

        // Make sure we have a movie object before we try to do anything with it
        if (null == mSingleMovie){
            showErrorMessage();
        }else {

			mBinder.setMovie(mSingleMovie);

			//set the color for the spinner
			if(mBinder.detailsLoader.detailLoaderPb != null){
				mBinder.detailsLoader.detailLoaderPb.getIndeterminateDrawable().setColorFilter(
						ContextCompat.getColor(getContext(), R.color.colorAccent),
						PorterDuff.Mode.MULTIPLY);
			}

			//give the favorite buttons their click handlers
			if(mBinder.addToFavoritesBtn !=null){
				mBinder.addToFavoritesBtn.setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								addAsFavorite();
							}
						});
			}

			if(mBinder.removeFromFavoritesBtn != null){
				mBinder.removeFromFavoritesBtn.setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								RemoveAsFavorite();
							}
						});
			}

            // instantiate the Retrofit client we'll be using
            mTmdbService = TmdbClient.getClient().create(TmdbInterface.class);

            //build our Uri
            mFavoriteUri = TmdbMovieContract.TmdbMovieEntry
                    .buildTmdbMovieUriWithId(mSingleMovie.getId());

            // Load the basic data that came in with the intent into display views
            renderMoviePoster();
            if(savedInstanceState != null) {
                showMovieDetails();
                toggleFavoriteButtons();
                renderTrailers();
                renderReviews();
            }else{
                // see if movie is in the favorites databse
                getActivity().getSupportLoaderManager().initLoader(FAVORITE_LOADER, null, this);
                // Make the call to tmdb.org to get addition movie details we need to display
                // Note: we first need to make sure we're online and we have a valid REST service
                if(ConnectionChecker.isOnline(getActivity()) && mTmdbService !=null) {
                   	mBinder.detailsLoader.detailLoaderLayout.setVisibility(View.VISIBLE);
                    loadMovieDetailData();
                    loadTrailers();
                    loadReviews();
                }else{
                    //always show the basic details that were passed from the grid
                    showMovieDetails();
                }
            }
        }
        // Return the view we just built to the fragment host
        return rootView;
    }


	/**
	 * Creates a menu item to share the first trailer if the movie we loaded has one or more of theh
	 *
	 * @param menu		options menu
	 * @param inflater 	inflater
	 */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_details, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share_trailer);
        mTrailerShareProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        item.setVisible(mHasTrailers);
    }


	/**
	 * Saves all of our movie data, trailers, and reviews on stat save
	 *
	 * @param outState	bundle to save
	 */
	@Override
    public void onSaveInstanceState(Bundle outState) {
        if(mSingleMovie != null){
            outState.putParcelable(MOVIE, mSingleMovie);
        }

        if (mReviewArrayList != null){
            outState.putParcelableArrayList(REVIEWS, mReviewArrayList);
        }

        if(mTrailersArrayList != null){
            outState.putParcelableArrayList(TRAILERS, mTrailersArrayList);
        }
    }

    /**
     * Render the movie poster into the layout
	 *
	 * if we're dealing with a Favorite, load it from the file system if it exists
     */
    private void renderMoviePoster() {
        // load the poster image
        boolean bitmapLoaded = false;

        if(mSingleMovie.getFavoriteStatus() == TmdbMovie.FAVORITE_STATUS_TRUE){
            //try to load the saved poster from file system
            String posterImagePath = mSingleMovie.getPosterImagePath();
            if(posterImagePath != null && !posterImagePath.isEmpty()){
                Bitmap posterImage = BitmapIOUtil
                        .readBitmap(posterImagePath, getActivity());
                if(posterImage != null){
                    mBinder.detailPosterImage.setImageBitmap(posterImage);
                    bitmapLoaded = true;
                }
            }
        }

        if(!bitmapLoaded) {
            final Context context = mBinder.detailPosterImage.getContext();
            final String moviePosterPath = mSingleMovie.getPosterPath(context);

            if(moviePosterPath == null){
                Drawable emptyFrame = context.getDrawable(R.drawable.ic_empty_poster);
                mBinder.detailPosterImage.setImageDrawable(emptyFrame);
                mBinder.detailPosterTitle.setVisibility(View.VISIBLE);
            }else{
                Picasso.with(context)
                        .load(moviePosterPath)
                        .into(mBinder.detailPosterImage,
                                new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        //nothing special to do here
                                    }

                                    @Override
                                    public void onError() {
                                        mBinder.detailPosterTitle.setVisibility(View.VISIBLE);
                                    }
                                }
                        );
            }

        }
    }


	/**
	 * render the MPAA "rated" logo when filed is bound
	 *
	 * @param view				image view to load
	 * @param certification		movie certification
	 */
    @BindingAdapter("certificationLogo")
	public static void loadMpaaIcon(ImageView view, String certification){
		if(certification != null && !certification.isEmpty()){
			certification = certification.toUpperCase(Locale.ROOT);
			switch (certification){
				case "G":
					view.setImageDrawable(
							view.getContext().getDrawable(R.drawable.rated_g));
					break;
				case "PG":
					view.setImageDrawable(
							view.getContext().getDrawable(R.drawable.rated_pg));
					break;
				case "PG-13":
					view.setImageDrawable(
							view.getContext().getDrawable(R.drawable.rated_pg_13));
					break;
				case "R":
					view.setImageDrawable(
							view.getContext().getDrawable(R.drawable.rated_r));
					break;
				case "NC-17":
					view.setImageDrawable(
							view.getContext().getDrawable(R.drawable.rated_nc_17));
					break;
				default:
					//no image to load
			}
		}
	}


	/**
	 * render all of the reviews from tmdb
	 */
    private void renderReviews(){
        if(mReviewArrayList != null && !mReviewArrayList.isEmpty()){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
			mBinder.reviewListRecyclerview.setLayoutManager(layoutManager);
            mReviewAdapter = new TmdbReviewAdapter();
            mReviewAdapter.setClickListener(this);
			mBinder.reviewListRecyclerview.setAdapter(mReviewAdapter);
            mReviewAdapter.setReviewData(mReviewArrayList);
            mBinder.reviewListContainer.setVisibility(View.VISIBLE);
        }
    }

	/**
	 * render all of the youtube trailers from tmdb
	 */
    private void renderTrailers(){
        if(mTrailersArrayList != null && !mTrailersArrayList.isEmpty()){
            mHasTrailers = true;
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mBinder.trailersListRecyclerview.setLayoutManager(layoutManager);
            mTrailerAdapter = new TmdbTrailerAdapter();
            mTrailerAdapter.setClickListener(this);
			mBinder.trailersListRecyclerview.setAdapter(mTrailerAdapter);
            mTrailerAdapter.setTrailerData(mTrailersArrayList);
            mBinder.trailersListContainer.setVisibility(View.VISIBLE);
            getActivity().invalidateOptionsMenu();
            setTrailerShareAction(mTrailersArrayList.get(0));
        }else{
            mHasTrailers=false;
        }
    }

    /**
     * Initiates call to tmdb.org's /movie/[movie_id] API to fetch extended
     * movie metadata. Puts the new metadata into corresponding views in
     * the details layout
     */
    private void loadMovieDetailData() {

		String extraInfo = TmdbInterface.RELEASE_DATES + "," + TmdbInterface.CREDITS;
        mMovieDetailsCall = mTmdbService.getMovieDetails(mSingleMovie.getId(),
				extraInfo, BuildConfig.TMDB_API_KEY);

        mMovieDetailsCall.enqueue(new Callback<TmdbMovieDetails>() {
            @Override
            public void onResponse(Call<TmdbMovieDetails>call,
                                   Response<TmdbMovieDetails> response) {
                if(response.body() != null){
                    addMovieDetails(response.body());
                }
                showMovieDetails();
                if(mSingleMovie.getFavoriteStatus() == TmdbMovie.FAVORITE_STATUS_TRUE) {
                    updateFavoriteData();
                }
            }
            @Override
            public void onFailure(Call<TmdbMovieDetails>call, Throwable t) {
                showMovieDetails();
            }
        });
    }

	/**
	 * Initiates call to tmdb.org's /movie/[movie_id]/reviews API
	 */
    private void loadReviews() {
        mReviewsCall = mTmdbService.getMovieReviews(mSingleMovie.getId(),
                BuildConfig.TMDB_API_KEY);
		mReviewsCall.enqueue(new Callback<TmdbReviews>() {
            @Override
            public void onResponse(Call<TmdbReviews>call, Response<TmdbReviews> response) {
                if(response.body() != null){
                    mReviewArrayList = response.body().getReviews();
                    renderReviews();
                }
            }

            @Override
            public void onFailure(Call<TmdbReviews>call, Throwable t) {

            }
        });
    }

	/**
	 * Initiates call to tmdb.org's /movie/[movie_id]/videos API
	 */
    private void loadTrailers() {
		mTrailersCall = mTmdbService.getMovieTrailers(mSingleMovie.getId(),
                BuildConfig.TMDB_API_KEY);
		mTrailersCall.enqueue(new Callback<TmdbTrailers>() {
            @Override
            public void onResponse(Call<TmdbTrailers>call, Response<TmdbTrailers> response) {
                if(response.body() != null){
                    String trailerSite = getString(R.string.youtube_site_name);
                    String[] trailerTypes = getResources()
                            .getStringArray(R.array.allowed_trailer_types);

                    mTrailersArrayList = response.body()
                            .getTrailersBySiteAndType(trailerSite,trailerTypes);

                    renderTrailers();
                }
            }

            @Override
            public void onFailure(Call<TmdbTrailers>call, Throwable t) {

            }
        });
    }

    /**
     * Utility function to add extra details to existing movie
     */
    private void addMovieDetails(TmdbMovieDetails details){

        String runtime = details.getRuntime();
        if(runtime != null && !runtime.isEmpty()){
            mSingleMovie.setRunningTime(runtime);
        }

        String genres = details.getGenres();
        if(genres != null && !genres.isEmpty()){
            mSingleMovie.setGenres(genres);
        }

        TmdbRelease theatricalRelease =
                details.getTheatricalRelease(getString(R.string.tmdb_region));
        if(theatricalRelease != null){
            mSingleMovie.setCertification(theatricalRelease.getCertification());
            mSingleMovie.setReleaseDate(theatricalRelease.getReleaseDate());
        }

        String tagline = details.getTagline();
        if(tagline != null && !tagline.isEmpty()){
            mSingleMovie.setTagline(tagline);
        }

        String castList = details.getCastList();
		if(castList !=null && !castList.isEmpty()){
			mSingleMovie.setCastList(castList);
		}

    }

    /**
     * Utility function to hide loading spinner
     */
    private void showMovieDetails(){
        mBinder.detailMovieLayout.setVisibility(View.VISIBLE);
		mBinder.detailsLoader.detailLoaderLayout.setVisibility(View.GONE);

    }

    /**
     * Utility function to show error message
     */
    private void showErrorMessage() {
		mBinder.detailMovieLayout.setVisibility(View.GONE);
        mBinder.detailsError.detailErrorLayout.setVisibility(View.VISIBLE);
    }


	/**
	 * click handler for individual review. Takes user to review on tmdb web site
	 *
	 * @param view		review view
	 * @param position	review position
	 */
    @Override
    public void onReviewClick(View view, int position) {
        final TmdbReview review = mReviewAdapter.getItem(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getReviewUrl()));
        startActivity(intent);
    }


	/**
	 * click handler for individual trailers. Launches in youtube app or fallback to web
	 * if youtube app not installed
	 *
	 * @param view 		trailer view
	 * @param position	trailer position
	 */
 	@Override
    public void onTrailerClick(View view, int position) {
        final TmdbTrailer trailer = mTrailerAdapter.getItem(position);

        String webDestination = getString(R.string.youtube_web_path) + trailer.getTrailerKey();

        String appDestination = getString(R.string.youtube_app_path) + trailer.getTrailerKey();

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appDestination));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webDestination));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }


	/**
	 * add movie to local favorites database. Will also store images for poster
	 * and banner to filesystem if they have been loaded by picasso
	 */
	private void addAsFavorite() {
        saveMovieImages();
        AddFavoriteAsyncTask addFavorite = new AddFavoriteAsyncTask(getActivity(), this);
        addFavorite.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSingleMovie);
    }

	/**
	 * toggle the add/remove favorite buttons, show user a toast
	 */
    @Override
    public void onFavoriteAdded(){
        mSingleMovie.setFavoriteStatus(TmdbMovie.FAVORITE_STATUS_TRUE);
        toggleFavoriteButtons();
        String logString = mSingleMovie.getMovieTitle() + " added to favorites";
        Log.d(TAG, logString);
        Toast.makeText(getActivity(), getString(R.string.details_favorite_added),
                Toast.LENGTH_SHORT).show();
    }

	/**
	 * removie movie from local favorites database. Will also removed any image files stored locally
	 */
    private void RemoveAsFavorite(){
        BitmapIOUtil.deleteBitmap(mSingleMovie.getPosterImagePath(), getActivity());
        BitmapIOUtil.deleteBitmap(mSingleMovie.getBannerImagePath(), getActivity());
        RemoveFavoriteAsyncTask removeFavorite = new RemoveFavoriteAsyncTask(getActivity(), this);
        removeFavorite.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSingleMovie);
    }

	/**
	 * toggle the add/remove favorite buttons, show user a toast
	 */
    @Override
    public void onFavoriteRemoved(){
        mSingleMovie.setFavoriteStatus(TmdbMovie.FAVORITE_STATUS_FALSE);
        toggleFavoriteButtons();
        String logString = mSingleMovie.getMovieTitle() + " removed from favorites";
        Log.d(TAG, logString);
        Toast.makeText(getActivity(), getString(R.string.details_favorite_removed),
                Toast.LENGTH_SHORT).show();
    }

	/**
	 * update the stored favorite data if we got a good load from tmdb. Ensures any new data
	 * like release date, rating, etc is updated in the local db
	 */
    private void updateFavoriteData(){
        UpdateFavoriteAsyncTask updateFavorite =
                new UpdateFavoriteAsyncTask(getActivity(), this);
        updateFavorite.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSingleMovie);
    }

	/**
	 * log info when we successfully updated the loacl favorites db
	 */
    @Override
    public void onFavoriteUpdated(int rowsUpdated){
        String logString;
        //nothing needs shown to the user, but let's log it
        switch (rowsUpdated){
            case 0:
                logString = mSingleMovie.getMovieTitle() + " not updated";
                break;
            case 1:
                logString = String.valueOf(rowsUpdated) + " row of data updated for " +
                        mSingleMovie.getMovieTitle();
                break;
            default:
                logString = "Something went wrong updating record for " +
                        mSingleMovie.getMovieTitle();
        }

        Log.d(TAG, logString);
    }

	/**
	 * sets state of favorite buttons based on favorite status
	 */
    private void toggleFavoriteButtons(){
        if(mSingleMovie.getFavoriteStatus() == TmdbMovie.FAVORITE_STATUS_TRUE) {
            mBinder.addToFavoritesBtn.setVisibility(View.GONE);
            mBinder.removeFromFavoritesBtn.setVisibility(View.VISIBLE);
        }else{
			mBinder.addToFavoritesBtn.setVisibility(View.VISIBLE);
			mBinder.removeFromFavoritesBtn.setVisibility(View.GONE);
        }
    }


	/**
	 * Popualates the data for the first trailer into the share provider menu option
	 *
	 * @param tmdbTrailer trailer to share
	 */
	private void setTrailerShareAction(TmdbTrailer tmdbTrailer){
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        String shareSubject = mSingleMovie.getMovieTitle() + " " + mSingleMovie.getReleaseYear();
        String shareText = tmdbTrailer.getTrailerName() + ": " +
                getString(R.string.youtube_web_path) + tmdbTrailer.getTrailerKey();
        shareIntent.setType(getString(R.string.menu_share_content_type));
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        if(mTrailerShareProvider != null) {
            mTrailerShareProvider.setShareIntent(shareIntent);
        }
    }


	/**
	 * saves poster and banner images to local filesystem
	 */
	private void saveMovieImages(){

        if(mSingleMovie.getPosterPath() != null && !mSingleMovie.getPosterPath().isEmpty()){
            String posterFilename = String.valueOf(mSingleMovie.getId()) + POSTER_FILE_SUFFIX;
            if(saveBitmapFromImageView(mBinder.detailPosterImage, posterFilename)){
                mSingleMovie.setPosterImagePath(posterFilename);
            }
        }

        if(mSingleMovie.getBannerPath() != null && !mSingleMovie.getBannerPath().isEmpty()) {
            String bannerFilename = String.valueOf(mSingleMovie.getId()) + BANNER_FILE_SUFFIX;
            if(mMovieBannerImageView != null){
				if (saveBitmapFromImageView(mMovieBannerImageView, bannerFilename)) {
					mSingleMovie.setBannerImagePath(bannerFilename);
				}
			}
        }
    }

    /**
	 * helper function to save a single image to a local file
     */
    private boolean saveBitmapFromImageView(ImageView imageView, String fileName){
        boolean success = false;

        BitmapDrawable imageViewDrawable =
                (BitmapDrawable) imageView.getDrawable();
        if (imageViewDrawable != null){
            Bitmap bitmap = imageViewDrawable .getBitmap();
            if(bitmap != null){
                success = BitmapIOUtil.saveBitmap(bitmap, fileName, getActivity());
            }
        }
        return success;
    }


	/**
	 * cleanup - if we've got a retrofit call active when view is destroyed, we need
	 * to cancel it to avoid a "Fragment not attached to view" error
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mMovieDetailsCall != null) mMovieDetailsCall.cancel();
		if (mReviewsCall != null) mReviewsCall.cancel();
		if (mTrailersCall != null) mTrailersCall.cancel();
	}

	// loader for favories db ------------------------------------
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs) {
        switch (loaderId) {
            case FAVORITE_LOADER:
                return new CursorLoader(
                        getContext(),
                        mFavoriteUri,
                        TMDB_FAVORITE_PROJECTION,
                        null,
                        null,
                        null
                );

            case FAVORITE_DETAILS_LOADER:
                return new CursorLoader(
                        getContext(),
                        mFavoriteUri,
                        TMDB_EXT_DATA_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case FAVORITE_LOADER:
                if (data != null && data.moveToFirst()) {
                    mSingleMovie.setFavoriteStatus(TmdbMovie.FAVORITE_STATUS_TRUE);
					data.close();
                } else {
                    mSingleMovie.setFavoriteStatus(TmdbMovie.FAVORITE_STATUS_FALSE);
                }
                toggleFavoriteButtons();
				getActivity().getSupportLoaderManager().destroyLoader(FAVORITE_LOADER);

				//if we're in offline mode, go ahead and fetch all of the extended
                //details for the movie and render them now if we've got a favorite
                if(mSingleMovie.getFavoriteStatus() == TmdbMovie.FAVORITE_STATUS_TRUE
						&& !ConnectionChecker.isOnline(getContext())){
                    getActivity().getSupportLoaderManager()
                            .initLoader(FAVORITE_DETAILS_LOADER, null, this);
                }
                break;
            case FAVORITE_DETAILS_LOADER:
                if (data != null && data.moveToFirst()) {
                    if (!data.isNull(INDEX_TMDB_RELEASE_DATE)) {
                        String releaseDate = data.getString(INDEX_TMDB_RELEASE_DATE);
                        if (releaseDate != null && !releaseDate.isEmpty()) {
                            mSingleMovie.setReleaseDate(releaseDate);
                        }
                    }
                    if (!data.isNull(INDEX_TMDB_RUNNING_TIME)) {
                        String runtime = data.getString(INDEX_TMDB_RUNNING_TIME);
                        if (runtime != null && !runtime.isEmpty()) {
                            mSingleMovie.setRunningTime(runtime);
                        }
                    }
                    if (!data.isNull(INDEX_TMDB_GENRES)) {
                        String genres = data.getString(INDEX_TMDB_GENRES);
                        if (genres != null && !genres.isEmpty()) {
                            mSingleMovie.setGenres(genres);
                        }
                    }
                    if (!data.isNull(INDEX_TMDB_CERTIFICATION)) {
                        String certification = data.getString(INDEX_TMDB_CERTIFICATION);
                        if (certification != null && !certification.isEmpty()) {
                            mSingleMovie.setCertification(certification);
                        }
                    }
                    if (!data.isNull(INDEX_TMDB_TAGLINE)) {
                        String tagline = data.getString(INDEX_TMDB_TAGLINE);
                        if (tagline != null && !tagline.isEmpty()) {
                            mSingleMovie.setTagline(tagline);
                        }
                    }

                    if (!data.isNull(INDEX_TMDB_POSTER_FILE_PATH)) {
                        String posterFilePath = data.getString(INDEX_TMDB_POSTER_FILE_PATH);
                        if (posterFilePath != null && !posterFilePath.isEmpty()) {
                            mSingleMovie.setPosterImagePath(posterFilePath);
                        }
                    }
                    if (!data.isNull(INDEX_TMDB_BANNER_FILE_PATH)) {
                        String bannerFilePath = data.getString(INDEX_TMDB_BANNER_FILE_PATH);
                        if (bannerFilePath != null && !bannerFilePath.isEmpty()) {
                            mSingleMovie.setBannerImagePath(bannerFilePath);
                        }
                    }
					if (!data.isNull(INDEX_TMDB_CAST_LIST)) {
						String castList = data.getString(INDEX_TMDB_CAST_LIST);
						if (castList != null && !castList.isEmpty()) {
							mSingleMovie.setCastList(castList);
						}
					}
					data.close();
                }
				getActivity().getSupportLoaderManager().destroyLoader(FAVORITE_DETAILS_LOADER);
				break;

            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case FAVORITE_LOADER:
                mSingleMovie.setFavoriteStatus(TmdbMovie.FAVORITE_STATUS_NOT_SET);
                toggleFavoriteButtons();
                break;
            case FAVORITE_DETAILS_LOADER:
                //nothing to do
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }
}
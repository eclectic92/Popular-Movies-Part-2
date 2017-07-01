package com.natalieryanudacity.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.natalieryanudacity.android.popularmovies.model.TmdbMovie;
import com.natalieryanudacity.android.popularmovies.moviedetails.MovieDetailActivity;
import com.natalieryanudacity.android.popularmovies.moviedetails.MovieDetailFragment;

/**
 * Created by Natalie Ryan on 6/3/17
 * <p>
 * Android activity for displaying grid of movie posters
 * or two pane view if movie posters and movie details
 * Actual rendering is done by the fragment MovieGridFragment
 * <p>
 * NOTE: Devices with smallest dimension of 600dp and up will see two pane view
 */
public class HomeScreenActivity extends AppCompatActivity implements MovieGridFragment.OnMovieSelected
{

	private static final String MOVIE="MOVIE";
	private boolean mTwoPane;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);

		if (findViewById(R.id.details_container)!=null)
		{
			mTwoPane=true;
			if (savedInstanceState==null)
			{
				FragmentManager fragmentManager=getSupportFragmentManager();
				MovieDetailFragment movieDetailFragment=new MovieDetailFragment();
				fragmentManager.beginTransaction()
						.add(R.id.details_container, movieDetailFragment)
						.commit();
			}
		}
		else
		{
			mTwoPane=false;
		}
	}


	/**
	 * sends intent to movie detail activity, or sends movie details to the right-hand
	 * details pane (depending on device size) view when user clicks on a movie poster.
	 * Will send all basic movie details as a parcelable
	 */
	@Override
	public void onMovieSelected(TmdbMovie singleMovie)
	{
		if (mTwoPane)
		{
			Bundle args=new Bundle();
			args.putParcelable(MovieDetailFragment.MOVIE, singleMovie);
			MovieDetailFragment movieDetailFragment=new MovieDetailFragment();
			movieDetailFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.details_container, movieDetailFragment)
					.commit();
		}
		else
		{
			Intent intent=new Intent(this, MovieDetailActivity.class);
			intent.putExtra(MOVIE, singleMovie);
			startActivity(intent);
		}
	}
}



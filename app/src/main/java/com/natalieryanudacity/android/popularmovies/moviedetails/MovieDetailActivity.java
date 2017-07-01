package com.natalieryanudacity.android.popularmovies.moviedetails;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.natalieryanudacity.android.popularmovies.R;

/**
 * Created by Natalie Ryan on 6/7/17
 * <p>
 * Skeleton Android activity for displaying movie details
 * Actual rendering is done by the fragment MovieDetalFragment
 */
public class MovieDetailActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_detail);
		Toolbar toolbar=(Toolbar)findViewById(R.id.details_toolbar);
		setSupportActionBar(toolbar);

		//if we're on Lollipop or above, let's make sure the toolbar is tranparent
		if (Build.VERSION.SDK_INT>=21)
		{
			Window window=getWindow();
			window.getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
			window.setStatusBarColor(Color.TRANSPARENT);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		}

		if (getSupportActionBar()!=null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId()==android.R.id.home)
		{
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
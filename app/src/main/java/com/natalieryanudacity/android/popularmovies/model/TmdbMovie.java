package com.natalieryanudacity.android.popularmovies.model;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.SerializedName;
import com.natalieryanudacity.android.popularmovies.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Natalie Ryan on 6/3/17.
 * Simple class to store single movie details from theMovieDatabase
 */

@SuppressWarnings("unused")
public class TmdbMovie extends BaseObservable implements Parcelable{

    private final static String FULL_DATE_OUTPUT_FORMAT = "MMMM d, yyyy";
    private final static String YEAR_ONLY_DATE_OUTPUT_FORMAT = "(yyyy)";
    public static final int FAVORITE_STATUS_NOT_SET = -1;
    public static final int FAVORITE_STATUS_FALSE = 0;
    public static final int FAVORITE_STATUS_TRUE = 1;

	@Bindable
    @SerializedName("id")
    private long mId;

	@Bindable
    @SerializedName("title")
    private String mMovieTitle;

	@Bindable
	@SerializedName("release_date")
    private String mReleaseDate;

	@SerializedName("poster_path")
    private String mPosterPath;

	@SerializedName("backdrop_path")
    private String mBannerPath;

	@Bindable
    @SerializedName("overview")
    private String mOverview;

	@Bindable
    @SerializedName("vote_average")
    private String mVoteAverage;

	@Bindable
	private String mRunningTime;

	@Bindable
	private String mGenres;

	@Bindable
	private String mCertification;

	@Bindable
	private String mTagline;

    @Bindable
    private String mCastList;

	private String mPosterImagePath;
    private String mBannerImagePath;
    private int mFavoriteStatus = FAVORITE_STATUS_NOT_SET;

    //empty constructor
    public TmdbMovie() {
    }

    //baseline constructor
    public TmdbMovie(long id, String title, String releaseDate, String posterPath,
                     String backdropPath, String overview, String voteAverage,
                     String runningTime, String genres, String certification,
                     String tagline, int favoriteStatus, String posterImagePath,
                     String bannerImagePath, String castList){

        this.mId = id;
        this.mMovieTitle = title;
        this.mReleaseDate = releaseDate;
        this.mPosterPath = posterPath;
        this.mBannerPath = backdropPath;
        this.mOverview = overview;
        this.mVoteAverage = voteAverage;
        this.mRunningTime = runningTime;
        this.mGenres = genres;
        this.mCertification = certification;
        this.mTagline = tagline;
        this.mFavoriteStatus = favoriteStatus;
        this.mPosterImagePath = posterImagePath;
        this.mBannerImagePath = bannerImagePath;
		this.mCastList = castList;
    }

    //constructor for parceler
    private TmdbMovie(Parcel in){
        mId = in.readLong();
        mMovieTitle = in.readString();
        mReleaseDate = in.readString();
        mPosterPath = in.readString();
        mBannerPath = in.readString();
        mOverview = in.readString();
        mVoteAverage = in.readString();
        mRunningTime = in.readString();
        mGenres = in.readString();
        mCertification = in.readString();
        mTagline = in.readString();
        mFavoriteStatus = in.readInt();
        mPosterImagePath = in.readString();
        mBannerImagePath = in.readString();
		mCastList = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<TmdbMovie> CREATOR = new Parcelable.Creator<TmdbMovie>() {
        public TmdbMovie createFromParcel(Parcel parcel) {
            return new TmdbMovie(parcel);
        }
        public TmdbMovie[] newArray(int size) {
            return new TmdbMovie[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mId);
        out.writeString(mMovieTitle);
        out.writeString(mReleaseDate);
        out.writeString(mPosterPath);
        out.writeString(mBannerPath);
        out.writeString(mOverview);
        out.writeString(mVoteAverage);
        out.writeString(mRunningTime);
        out.writeString(mGenres);
        out.writeString(mCertification);
        out.writeString(mTagline);
        out.writeInt(mFavoriteStatus);
        out.writeString(mPosterImagePath);
        out.writeString(mBannerImagePath);
		out.writeString(mCastList);
    }

    //Setters and Getters---------------------------
    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
		notifyPropertyChanged(BR.id);
	}

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.mMovieTitle = movieTitle;
		notifyPropertyChanged(BR.movieTitle);
	}

    public String getReleaseDate() {
        return formatDateString(mReleaseDate, FULL_DATE_OUTPUT_FORMAT);
    }

    public String getRawReleaseDate(){
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
		notifyPropertyChanged(BR.releaseDate);
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    //Overload to get full path since converting app to use GSON
    public String getPosterPath(Context context) {
        if(mPosterPath != null && !mPosterPath.isEmpty()) {
            return context.getString(R.string.tmdb_poster_base_url) + mPosterPath;
        }else{
            return null;
        }
    }

    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
    }

    public String getBannerPath() {
        return mBannerPath != null ? mBannerPath : mPosterPath;
    }

    //Overload to get full path since converting app to use GSON
    public String getBannerPath(Context context) {
        if(mBannerPath != null && !mBannerPath.isEmpty()) {
            return context.getString(R.string.tmdb_banner_base_url) + mBannerPath;
        }else{
            if(mPosterPath !=null){
                return context.getString(R.string.tmdb_banner_base_url) + mPosterPath;
            }else{
                return null;
            }
        }
    }

    public void setBannerPath(String bannerPath) {
        this.mBannerPath = bannerPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(@Nullable String overview) {
        this.mOverview = overview;
		notifyPropertyChanged(BR.overview);
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String voteAverage){
        this.mVoteAverage = voteAverage;
    }

    public String getRunningTime() {
        return mRunningTime;
    }

    public void setRunningTime(String runningTime) {
        this.mRunningTime = runningTime;
		notifyPropertyChanged(BR.runningTime);
    }

    public String getGenres() {
        return mGenres;
    }

    public void setGenres(@Nullable String genres) {
        this.mGenres = genres;
		notifyPropertyChanged(BR.genres);
    }

    public String getCertification() {
        return mCertification;
    }

    public void setCertification(@Nullable String certification) {
        this.mCertification = certification;
		notifyPropertyChanged(BR.certification);

	}

    public String getTagline(){
        return mTagline;
    }

    public void setTagline(String tagline){
        this.mTagline = tagline;
		notifyPropertyChanged(BR.tagline);
    }

    public String getCastList(){
		return mCastList;
	}

	public void setCastList(String castList){
		this.mCastList = castList;
		this.notifyPropertyChanged(BR.castList);
	}

    public int getFavoriteStatus(){
        return mFavoriteStatus;
    }

    public void setFavoriteStatus(int favoriteStatus){
        this.mFavoriteStatus = favoriteStatus;
    }

    public String getPosterImagePath() {
        return mPosterImagePath;
    }

    public void setPosterImagePath(String posterImagePath){
        this.mPosterImagePath = posterImagePath;
    }

    public String getBannerImagePath() {
        return mBannerImagePath;
    }

    public void setBannerImagePath (String bannerImagePath){
        this.mBannerImagePath = bannerImagePath;
    }

    public String getReleaseYear() {
        return formatDateString(mReleaseDate, YEAR_ONLY_DATE_OUTPUT_FORMAT);
    }

    private static String formatDateString(String rawDateString, String outputFormat){
        SimpleDateFormat formatterIn = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat formatterOut = new SimpleDateFormat(outputFormat, Locale.US);
        String formattedDate = null;

        try{
            Date parsedDate = formatterIn.parse(rawDateString);
            formattedDate = formatterOut.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}
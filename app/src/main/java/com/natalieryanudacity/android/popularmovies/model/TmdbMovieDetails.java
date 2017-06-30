package com.natalieryanudacity.android.popularmovies.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by natalier258 on 6/14/17.
 * Simple class to store and parse extended movie details from theMovieDatabase
 */

@SuppressWarnings("unused")
public class TmdbMovieDetails {
    private static final String RELEASE_REGION = "US";
    private static final int THEATRICAL_RELEASE = 3;
	private static final int MAX_CAST_SIZE = 5;

    @SerializedName("id")
    private int mId;
    @SerializedName("runtime")
    private int mRuntime;
    @SerializedName("genres")
    private ArrayList<TmdbGenre> mGenres;
    @SerializedName("release_dates")
    private TmdbReleases mReleases;
    @SerializedName("tagline")
    private String mTagline;
    @SerializedName("credits")
    private TmdbCast mCast;

    public void setId(int id){
        this.mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setGenres(ArrayList<TmdbGenre> genres){
        this.mGenres = genres;
    }

    public String getGenres(){
        return parseGenres(mGenres);
    }

    public void setRuntime(int runtime){
        this.mRuntime = runtime;
    }

    public String getRuntime(){
        if(mRuntime > 0) {
            return parseRunningTime(mRuntime);
        }else{
            return "";
        }
    }

    public void setReleases(TmdbReleases releases){
        this.mReleases = releases;
    }

    public TmdbReleases getReleases(){
        return mReleases;
    }

    public void setTagline(String tagline){
        this.mTagline = tagline;
    }

    public String getTagline(){
        return mTagline;
    }

    public void setmCastList(TmdbCast cast){
        this.mCast = cast;
    }

    public String getCastList(){
        return parseCastList(this.mCast);
    }

    public TmdbRelease getTheatricalRelease(){
        return getSpecificRelease(mReleases.getRegionalReleases(),
                RELEASE_REGION, THEATRICAL_RELEASE);
    }

    public TmdbRelease getTheatricalRelease(String regionCode){
        return getSpecificRelease(mReleases.getRegionalReleases(),
                regionCode, THEATRICAL_RELEASE);
    }

    @Nullable
    private static String parseCastList(TmdbCast cast){
		ArrayList<TmdbCastMember> castList = cast.getCastList();

		if(castList !=null && !castList.isEmpty()){
			StringBuilder sb = new StringBuilder();

			int castCounter = castList.size() > MAX_CAST_SIZE ? MAX_CAST_SIZE : castList.size();

			for (int i = 0; i < castCounter; i++) {
				TmdbCastMember castMember = castList.get(i);
				sb.append(castMember.getName());
				if(i < castCounter -1){
					sb.append(", ");
				}
			}
			return sb.toString();
		}else{
			return null;
		}
	}

    private static String parseRunningTime(int rawMinutes) {
        String minuteKey = "min";
        String minutesKey = "mins";
        String hoursKey = "h";

        String minutes;
        int hours = rawMinutes/60;
        int mins = rawMinutes%60;

        //do we need single or plural for our "minutes" text?
        if(mins < 2){
            minutes = String.valueOf(mins) + minuteKey;
        }else{
            minutes = String.valueOf(mins) + minutesKey;
        }

        //do we need to show hours?
        if(hours < 1){
            return minutes;
        }else{
            return String.valueOf(hours) + hoursKey + " " + minutes;
        }
    }

    @Nullable
    private static String parseGenres(ArrayList<TmdbGenre> genres){
        if(genres == null || genres.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int genreCount = genres.size();

        for (int i = 0; i < genreCount; i++) {
            TmdbGenre singleGenre = genres.get(i);
            sb.append(singleGenre.getName());
            if(i < genreCount - 1 ){
                sb.append(", ");
            }
        }
        return sb.toString();
    }


    private static TmdbRelease getSpecificRelease(
                                       ArrayList<TmdbRegionalRelease> releases,
                                       String regionCode,
                                       int releaseType){

        TmdbRelease singleRelease = null;

        if(releases != null && !releases.isEmpty()){
            for (TmdbRegionalRelease regionalRelease : releases){
                if(regionalRelease.getCountryCode().equalsIgnoreCase(regionCode)){
                    ArrayList<TmdbRelease> regionalSingleReleases =
                            regionalRelease.getReleaseDates();
                    for(TmdbRelease release : regionalSingleReleases){
                        if (release.getReleaseType() == 3){
                            singleRelease = release;
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return singleRelease;
    }
}
package com.natalieryanudacity.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Pojo for movie reviews  from tmdb
 * Created by natalier258 on 6/15/17.
 */

@SuppressWarnings("unused")
public class TmdbReview implements Parcelable {
    @SerializedName("id")
    private String mReviewId;
    @SerializedName("author")
    private String mReviewAuthor;
    @SerializedName("content")
    private String mReviewContent;
    @SerializedName("url")
    private String mReviewUrl;

    //constructor for parceler
    public TmdbReview(){

    }

    //baseline constructor
    public TmdbReview(String id, String author, String content, String url){
        this.mReviewId = id;
        this.mReviewAuthor = author;
        this.mReviewContent = content;
        this.mReviewUrl = url;
    }

    //constructor for parceler
    private TmdbReview(Parcel in){
        mReviewId = in.readString();
        mReviewAuthor = in.readString();
        mReviewContent = in.readString();
        mReviewUrl = in.readString();
    }

    public static final Parcelable.Creator<TmdbReview> CREATOR = new Parcelable.Creator<TmdbReview>() {
        public TmdbReview createFromParcel(Parcel parcel) {
            return new TmdbReview(parcel);
        }
        public TmdbReview[] newArray(int size) {
            return new TmdbReview[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mReviewId);
        out.writeString(mReviewAuthor);
        out.writeString(mReviewContent);
        out.writeString(mReviewUrl);
    }

    // public getters ---------------------------------------
    public String getReviewId(){
        return mReviewId;
    }

    public String getReviewAuthor(){
        return mReviewAuthor;
    }

    public String getReviewContent(){
        return mReviewContent;
    }

    public String getReviewUrl(){
        return mReviewUrl;
    }
}

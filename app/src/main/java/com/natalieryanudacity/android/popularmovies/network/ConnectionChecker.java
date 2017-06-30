package com.natalieryanudacity.android.popularmovies.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Natalie Ryan on 6/7/17.
 *
 * Utility class
 * Checks to see if the device has a network connection
 *
 * Re-implemnted from
 * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
 * as suggested in udacity project guidelines
 *
 */
public class ConnectionChecker {
    /**
     *
     * @param context context of the calling activity
     * @return        boolean indicating if the device has a network connection
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
package br.com.soulblighter.popularmoviesapp.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import br.com.soulblighter.popularmoviesapp.BuildConfig;

public class NetworkUtils {

    private static final String IMAGE_TMDB_URL = "http://image.tmdb.org/t/p/w185/";
    private static final String YOUTUBE_URL = "vnd.youtube:";

    // https://stackoverflow.com/questions/1560788/how-to-check-internet
    // -access-on-android-inetaddress-never-times-out
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String buildImageUrl(String image) {
        Uri.Builder builder = Uri.parse(IMAGE_TMDB_URL).buildUpon();
        builder.appendEncodedPath(image);
        return builder.build().toString();
    }

    public static String buildYoutubeUrl(String key) {
        Uri.Builder builder = Uri.parse(YOUTUBE_URL + key).buildUpon();
        return builder.build().toString();
    }
}

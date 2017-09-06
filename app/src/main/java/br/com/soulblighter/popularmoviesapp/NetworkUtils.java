package br.com.soulblighter.popularmoviesapp;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static final String POPULAR_URL = "http://api.themoviedb.org/3/movie/popular";
    public static final String TOP_RATED_URL = "http://api.themoviedb.org/3/movie/top_rated";
    public static final String IMAGE_TMDB_URL = "http://image.tmdb.org/t/p/w185/";

    final static String API_KEY_PARAM = "api_key";
    final static String API_KEY_VALUE = "";

    public static URL buildPopularUrl() throws MalformedURLException {
        Uri.Builder builder = Uri.parse(POPULAR_URL).buildUpon();
        builder.appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE);
        return new URL(builder.build().toString());
    }

    public static URL buildTopRatedUrl() throws MalformedURLException {
        Uri.Builder builder = Uri.parse(TOP_RATED_URL).buildUpon();
        builder.appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE);
        return new URL(builder.build().toString());
    }

    public static URL buildImageUrl(String image) throws MalformedURLException {
        Uri.Builder builder = Uri.parse(IMAGE_TMDB_URL).buildUpon();
        builder.appendPath(image);
        return new URL(builder.build().toString());
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}

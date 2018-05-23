package br.com.soulblighter.popularmoviesapp.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import br.com.soulblighter.popularmoviesapp.BuildConfig;

public class NetworkUtils {

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH = "top_rated";
    private static final String REVIEWS_PATH = "reviews";
    private static final String TRAILERS_PATH = "videos";
    private static final String IMAGE_TMDB_URL = "http://image.tmdb.org/t/p/w185/";

    private static final String YOUTUBE_URL = "vnd.youtube:";

    private final static String API_KEY_PARAM = "api_key";
    private final static String API_KEY_VALUE = BuildConfig.API_KEY;

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

/*
    public static String buildPopularUrl() {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(POPULAR_PATH);
        builder.appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE);
        return builder.build().toString();
    }

    public static String buildTopRatedUrl() {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(TOP_RATED_PATH);
        builder.appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE);
        return builder.build().toString();
    }

    public static String buildTrailersUrl(long movieId) {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(String.valueOf(movieId));
        builder.appendPath(TRAILERS_PATH);
        builder.appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE);
        return builder.build().toString();
    }

    public static String buildReviewsUrl(long movieId) {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(String.valueOf(movieId));
        builder.appendPath(REVIEWS_PATH);
        builder.appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE);
        return builder.build().toString();
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url
                .openConnection();
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
*/
}

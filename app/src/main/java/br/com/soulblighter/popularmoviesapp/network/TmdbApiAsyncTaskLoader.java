package br.com.soulblighter.popularmoviesapp.network;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import java.io.IOException;
import java.net.URL;


public class TmdbApiAsyncTaskLoader extends AsyncTaskLoader<String> {


    /* This String will contain the raw JSON */
    String mJson;
    String mUrl;

    public TmdbApiAsyncTaskLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {

        /* If no arguments were passed, we don't have a query to perform. Simply return. */
        if (mUrl == null) {
            return;
        }

        /*
         * When we initially begin loading in the background, we want to display the
         * loading indicator to the user
         */

        /*
         * If we already have cached results, just deliver them now. If we don't have any
         * cached results, force a load.
         */
        if (mJson != null) {
            deliverResult(mJson);
        } else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        /* If the user didn't enter anything, there's nothing to search for */
        if (mUrl == null || TextUtils.isEmpty(mUrl)) {
            return null;
        }

        /* Parse the URL from the passed in String and perform the search */
        try {
            URL taskUrl = new URL(mUrl);
            String jsonResult = NetworkUtils.getResponseFromHttpUrl(taskUrl);
            return jsonResult;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(String json) {
        mJson = json;
        super.deliverResult(json);
    }
}


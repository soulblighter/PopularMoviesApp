package br.com.soulblighter.popularmoviesapp;

import android.os.AsyncTask;

import java.net.URL;
import java.util.List;

class NetworkRequestTask extends AsyncTask<URL, Void, List<TmdbMovie>> {

    private NetworkRequestTaskListener mNetworkTaskListener;

    public NetworkRequestTask(NetworkRequestTaskListener listener) {
        mNetworkTaskListener = listener;
    }

    public interface NetworkRequestTaskListener {
        void onNetworkTaskComplete(List<TmdbMovie> parsedData);
    }

    @Override
    protected List<TmdbMovie> doInBackground(URL... params) {
        if (params.length == 0) {
            return null;
        }

        try {
            URL url = params[0];

            String jsonResponse = NetworkUtils
                    .getResponseFromHttpUrl(url);

            List<TmdbMovie> parsedData = TMDBJsonUtils
                    .getImagesFromJson(jsonResponse);

            return parsedData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<TmdbMovie> parsedData) {
        if(mNetworkTaskListener != null) {
            mNetworkTaskListener.onNetworkTaskComplete(parsedData);
        }
    }
}

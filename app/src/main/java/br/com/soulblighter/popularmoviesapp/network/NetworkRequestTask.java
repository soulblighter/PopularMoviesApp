package br.com.soulblighter.popularmoviesapp.network;

import android.os.AsyncTask;

import java.net.URL;

public class NetworkRequestTask extends AsyncTask<URL, Void, String> {

    private NetworkRequestTaskListener mNetworkTaskListener;

    public NetworkRequestTask(NetworkRequestTaskListener listener) {
        mNetworkTaskListener = listener;
    }

    public interface NetworkRequestTaskListener {
        void onNetworkTaskComplete(String data);
    }

    @Override
    protected String doInBackground(URL... params) {
        if (params.length == 0) {
            return null;
        }

        try {
            URL url = params[0];

            String jsonResponse = NetworkUtils
                    .getResponseFromHttpUrl(url);

            return jsonResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String data) {
        if(mNetworkTaskListener != null) {
            mNetworkTaskListener.onNetworkTaskComplete(data);
        }
    }
}

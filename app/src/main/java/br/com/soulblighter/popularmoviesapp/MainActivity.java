package br.com.soulblighter.popularmoviesapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PicassoGridViewAdapter.onClickHandler{

    private RecyclerView gridview;
    private PicassoGridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new PicassoGridViewAdapter(this, null);

        gridview = (RecyclerView) findViewById(R.id.gridview);
        gridview.setLayoutManager(new GridLayoutManager(this, 2));
        gridview.setAdapter(adapter);

        loadData();
    }

    public void PicassoAdapterOnClick(int id) {
        Toast.makeText(MainActivity.this, "" + id,
                Toast.LENGTH_SHORT).show();
    }

    void loadData() {
        try {
            new NetworkRequestTask().execute(NetworkUtils.buildPopularUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_sort_popular) {
            Toast.makeText(MainActivity.this, "menu_sort_popular",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.menu_sort_rate) {
            Toast.makeText(MainActivity.this, "menu_sort_rate",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class NetworkRequestTask extends AsyncTask<URL, Void, Map<Integer,String>> {

        @Override
        protected Map<Integer,String> doInBackground(URL... params) {
            if (params.length == 0) {
                return null;
            }

            try {
                //URL url = params[0];
                URL url = NetworkUtils.buildPopularUrl();

                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(url);

                Map<Integer,String> parsedData = TMDBJsonUtils
                        .getImagesFromJson(MainActivity.this, jsonResponse);

                return parsedData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Map<Integer,String> parsedData) {
            if (parsedData != null) {
                //showWeatherDataView();
                adapter.setData(parsedData);
            } else {
                //showErrorMessage();
                Log.e(Utils.TAG, "onPostExecute: parsedData is null");
            }
        }
    }

}

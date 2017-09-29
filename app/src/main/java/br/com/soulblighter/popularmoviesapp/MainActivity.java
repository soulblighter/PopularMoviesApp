package br.com.soulblighter.popularmoviesapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import static br.com.soulblighter.popularmoviesapp.MainActivity.ErrorType.NO_CONNECTION;
import static br.com.soulblighter.popularmoviesapp.MainActivity.ErrorType.PARSE_JSON;

public class MainActivity extends AppCompatActivity implements PicassoGridViewAdapter.PicassoClickListener, NetworkRequestTask.NetworkRequestTaskListener{

    public enum SortMethod {
        SORT_POPULAR,
        SORT_RATING
    }

    public enum ErrorType {
        NO_CONNECTION,
        PARSE_JSON
    }

    private SortMethod sortMethodSelected = SortMethod.SORT_POPULAR;
    private static final String EXTRA_SORT_METHOD = "sort_method";

    private RecyclerView rv_gridview;
    private TextView tv_error_box;
    private PicassoGridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_SORT_METHOD)) {
            sortMethodSelected = SortMethod.values()[savedInstanceState.getInt(EXTRA_SORT_METHOD)];
        }

        adapter = new PicassoGridViewAdapter(this, null);
        adapter.setClickListener(this);

        rv_gridview = (RecyclerView) findViewById(R.id.rv_gridview);
        tv_error_box = (TextView) findViewById(R.id.tv_error_box);

        int display_mode = getResources().getConfiguration().orientation;

        if (display_mode == Configuration.ORIENTATION_PORTRAIT) {
            rv_gridview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            rv_gridview.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        }

        rv_gridview.setAdapter(adapter);

        loadData(sortMethodSelected);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_SORT_METHOD, sortMethodSelected.ordinal());
        super.onSaveInstanceState(outState);
    }

    public void onPicassoItemClick(View view, int position) {
        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        TmdbMovie movie = adapter.getItem(position);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, (Parcelable)movie);
        startActivity(intent);
    }

    private void loadData(SortMethod method) {

        if(!NetworkUtils.isOnline(this)) {
            //Toast.makeText(this, getString(R.string.error_no_network), Toast.LENGTH_SHORT).show();
            showError(NO_CONNECTION);
            return;
        }

        try {
            if( method == SortMethod.SORT_POPULAR) {
                new NetworkRequestTask(this).execute(NetworkUtils.buildPopularUrl());
            } else if( method == SortMethod.SORT_RATING) {
                new NetworkRequestTask(this).execute(NetworkUtils.buildTopRatedUrl());
            }
        } catch (Exception e) {
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
            sortMethodSelected = SortMethod.SORT_POPULAR;
            loadData(sortMethodSelected);
            return true;
        }

        if (id == R.id.menu_sort_rate) {
            sortMethodSelected = SortMethod.SORT_RATING;
            loadData(sortMethodSelected);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDataGrid() {
        rv_gridview.setVisibility(View.VISIBLE);
        tv_error_box.setVisibility(View.GONE);
    }

    private void showError(ErrorType error) {
        rv_gridview.setVisibility(View.GONE);
        tv_error_box.setVisibility(View.VISIBLE);
        switch (error) {
            case NO_CONNECTION:
                tv_error_box.setText(R.string.error_no_network);
                break;
            case PARSE_JSON:
            default:
                tv_error_box.setText(R.string.error_parse_json);
                break;
        }
    }

    @Override
    public void onNetworkTaskComplete(List<TmdbMovie> parsedData) {
        if (parsedData != null) {
            showDataGrid();
            adapter.setData(parsedData);
            adapter.notifyDataSetChanged();
        } else {
            showError(PARSE_JSON);
            //Toast.makeText(MainActivity.this, R.string.error_parse_json, Toast.LENGTH_SHORT).show();
            Log.e(this.getClass().getSimpleName(), "onPostExecute: parsedData is null");
        }
    }

}

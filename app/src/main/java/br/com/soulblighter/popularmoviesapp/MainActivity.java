package br.com.soulblighter.popularmoviesapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.soulblighter.popularmoviesapp.databinding.ActivityMainBinding;
import br.com.soulblighter.popularmoviesapp.json.TmdbJsonUtils;
import br.com.soulblighter.popularmoviesapp.json.TmdbMovie;
import br.com.soulblighter.popularmoviesapp.network.NetworkUtils;
import br.com.soulblighter.popularmoviesapp.network.TmdbApiAsyncTaskLoader;
import br.com.soulblighter.popularmoviesapp.provider.TmdbMovieContract;

import static br.com.soulblighter.popularmoviesapp.MainActivity.ErrorType.NO_CONNECTION;
import static br.com.soulblighter.popularmoviesapp.MainActivity.ErrorType.PARSE_JSON;

public class MainActivity extends AppCompatActivity implements
    PicassoGridViewAdapter.PicassoClickListener, LoaderManager.LoaderCallbacks {

    public enum TmdbDisplayType {
        SORT_POPULAR, SORT_RATING, LOCAL_FAVORITES
    }

    public enum ErrorType {
        NO_CONNECTION, PARSE_JSON
    }

    public static final String[] MAIN_TMDB_PROJECTION = {
        TmdbMovieContract.Entry.MOVIE_ID,
        TmdbMovieContract.Entry.TITLE,
        TmdbMovieContract.Entry.POSTER_PATH,
        TmdbMovieContract.Entry.RELEASE_DATE,
        TmdbMovieContract.Entry.VOTE_AVERAGE,
        TmdbMovieContract.Entry.OVERVIEW
    };

    public static final int INDEX_TMDB_MOVIE_ID = 0;
    public static final int INDEX_TMDB_TITLE = 1;
    public static final int INDEX_TMDB_POSTER_PATH = 2;
    public static final int INDEX_TMDB_RELEASE_DATE = 3;
    public static final int INDEX_TMDB_VOTE_AVERAGE = 4;
    public static final int INDEX_TMDB_OVERVIEW = 5;

    private static final int ID_LOADER_MAIN = 101;
    private static final int ID_LOADER_FAVORITES = 102;

    private TmdbDisplayType mTmdbDisplayTypeSelected = TmdbDisplayType
        .SORT_POPULAR;
    private static final String EXTRA_DISPLAY_TYPE = "display_type";
    private static final String EXTRA_GRID_SATE = "gridstate";

    private ActivityMainBinding mBinding;
    private PicassoGridViewAdapter mAdapter;
    StaggeredGridLayoutManager mLayoutManager;
    Parcelable mGridState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (savedInstanceState != null && savedInstanceState.containsKey
            (EXTRA_DISPLAY_TYPE)) {
            mTmdbDisplayTypeSelected = TmdbDisplayType.values()
                [savedInstanceState.getInt(EXTRA_DISPLAY_TYPE)];
        }

        mAdapter = new PicassoGridViewAdapter(this, null);
        mAdapter.setClickListener(this);

        int display_mode = getResources().getConfiguration().orientation;

        if (display_mode == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager
                .VERTICAL);
            mBinding.recyclerGridView.setLayoutManager(mLayoutManager);
        } else {
            mLayoutManager = new
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager
                .VERTICAL);
            mBinding.recyclerGridView.setLayoutManager(mLayoutManager);
        }

        mBinding.recyclerGridView.setAdapter(mAdapter);
        loadData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mGridState = mBinding.recyclerGridView.getLayoutManager()
            .onSaveInstanceState();
        outState.putParcelable(EXTRA_GRID_SATE, mGridState);

        outState.putInt(EXTRA_DISPLAY_TYPE, mTmdbDisplayTypeSelected.ordinal());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mGridState = savedInstanceState.getParcelable(EXTRA_GRID_SATE);
    }

    public void onPicassoItemClick(View view, int position) {
        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        TmdbMovie movie = mAdapter.getItem(position);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, (Parcelable) movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_popular:
                mTmdbDisplayTypeSelected = TmdbDisplayType.SORT_POPULAR;
                loadData();
                break;

            case R.id.menu_sort_rate:
                mTmdbDisplayTypeSelected = TmdbDisplayType.SORT_RATING;
                loadData();
                break;

            case R.id.menu_show_favorites:
                mTmdbDisplayTypeSelected = TmdbDisplayType.LOCAL_FAVORITES;
                loadData();
                break;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDataGrid() {
        mBinding.recyclerGridView.setVisibility(View.VISIBLE);
        mBinding.errorBox.setVisibility(View.GONE);
    }

    private void showError(ErrorType error) {
        mBinding.recyclerGridView.setVisibility(View.GONE);
        mBinding.errorBox.setVisibility(View.VISIBLE);
        switch (error) {
            case NO_CONNECTION:
                mBinding.errorBox.setText(R.string.error_no_network);
                break;
            case PARSE_JSON:
            default:
                mBinding.errorBox.setText(R.string.error_parse_json);
                break;
        }
    }

    @Override
    public Loader onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {
            case ID_LOADER_MAIN:
                TmdbDisplayType method = TmdbDisplayType.values()[bundle
                    .getInt(EXTRA_DISPLAY_TYPE)];

                String url = null;
                switch (method) {
                    case SORT_POPULAR:
                        url = NetworkUtils.buildPopularUrl();
                        break;
                    case SORT_RATING:
                        url = NetworkUtils.buildTopRatedUrl();
                        break;
                }
                return new TmdbApiAsyncTaskLoader(this, url);

            case ID_LOADER_FAVORITES:
                /* URI for all rows of weather data in our weather table */
                Uri queryUri = TmdbMovieContract.Entry.CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = TmdbMovieContract.Entry.MOVIE_ID +
                    " DESC";

                return new CursorLoader(this, queryUri, MAIN_TMDB_PROJECTION,
                    null, null, sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " +
                    loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        int loaderId = loader.getId();

        switch (loaderId) {
            case ID_LOADER_MAIN:
                if (data != null) {
                    List<TmdbMovie> movies;
                    try {
                        movies = TmdbJsonUtils.getTmdbMoviesFromJson(
                            (String) data);
                        showDataGrid();
                        updateData(movies);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showError(PARSE_JSON);
                    }
                } else {
                    showError(PARSE_JSON);
                    //Toast.makeText(MainActivity.this, R.string
                    // .error_parse_json, Toast.LENGTH_SHORT).show();
                    Log.e(this.getClass().getSimpleName(), "onPostExecute: " +
                        "parsedData is null");
                }
                break;

            case ID_LOADER_FAVORITES:
                Cursor cursor = (Cursor) data;
                if (cursor != null) {
                    List<TmdbMovie> movies = new ArrayList<>();
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                        .moveToNext()) {
                        TmdbMovie movie = new TmdbMovie();
                        movie.id = cursor.getInt(INDEX_TMDB_MOVIE_ID);
                        movie.title = cursor.getString(INDEX_TMDB_TITLE);
                        movie.posterPath = cursor.getString
                            (INDEX_TMDB_POSTER_PATH);
                        Date date = new Date(cursor.getLong
                            (INDEX_TMDB_RELEASE_DATE));
                        SimpleDateFormat sdf = new SimpleDateFormat
                            ("yyyy-MM-dd");
                        movie.releaseDate = sdf.format(date);
                        movie.voteAverage = cursor.getDouble
                            (INDEX_TMDB_VOTE_AVERAGE);
                        movie.overview = cursor.getString(INDEX_TMDB_OVERVIEW);
                        movies.add(movie);
                    }

                    showDataGrid();
                    updateData(movies);

                    cursor.close();
                } else {
                    showError(PARSE_JSON);
                    Log.e(this.getClass().getSimpleName(), "onPostExecute: " +
                        "parsedData is null");
                }

                break;

            default:
                throw new RuntimeException("Loader Not Implemented: " +
                    loaderId);
        }


    }


    private void updateData(List<TmdbMovie> data) {
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
        if (mGridState != null) {
            mBinding.recyclerGridView.getLayoutManager()
                .onRestoreInstanceState(mGridState);
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {
    }

    private void loadData() {
        LoaderManager loaderManager = getSupportLoaderManager();
        switch (mTmdbDisplayTypeSelected) {
            case SORT_POPULAR:
            case SORT_RATING:

                if (!NetworkUtils.isOnline(this)) {
                    showError(NO_CONNECTION);
                    return;
                }

                Bundle loaderBundle = new Bundle();
                loaderBundle.putInt(EXTRA_DISPLAY_TYPE,
                    mTmdbDisplayTypeSelected.ordinal());

                Loader mainLoader = loaderManager.getLoader(ID_LOADER_MAIN);
                if (mainLoader == null) {
                    loaderManager.initLoader(ID_LOADER_MAIN, loaderBundle,
                        this);
                } else {
                    loaderManager.restartLoader(ID_LOADER_MAIN, loaderBundle,
                        this);
                }
                break;

            case LOCAL_FAVORITES:
                Loader favoritesLoader = loaderManager.getLoader
                    (ID_LOADER_FAVORITES);
                if (favoritesLoader == null) {
                    loaderManager.initLoader(ID_LOADER_FAVORITES, null, this);
                } else {
                    loaderManager.restartLoader(ID_LOADER_FAVORITES, null,
                        this);
                }
        }
    }
}

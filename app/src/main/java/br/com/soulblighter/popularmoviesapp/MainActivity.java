package br.com.soulblighter.popularmoviesapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.soulblighter.popularmoviesapp.json.TmdbJsonUtils;
import br.com.soulblighter.popularmoviesapp.json.TmdbMovie;
import br.com.soulblighter.popularmoviesapp.network.NetworkUtils;
import br.com.soulblighter.popularmoviesapp.network.TmdbApiAsyncTaskLoader;
import br.com.soulblighter.popularmoviesapp.provider.TmdbMovieContract;

import static br.com.soulblighter.popularmoviesapp.MainActivity.ErrorType.NO_CONNECTION;
import static br.com.soulblighter.popularmoviesapp.MainActivity.ErrorType.PARSE_JSON;

public class MainActivity extends AppCompatActivity implements
        PicassoGridViewAdapter.PicassoClickListener,
        LoaderManager.LoaderCallbacks {

    public enum SortMethod {
        SORT_POPULAR,
        SORT_RATING,
        LOCAL_FAVORITES
    }

    public enum ErrorType {
        NO_CONNECTION,
        PARSE_JSON
    }


    public static final String[] MAIN_TMDB_PROJECTION = {
            TmdbMovieContract.Entry. _ID,
            TmdbMovieContract.Entry.COLUMN_MOVIE_ID,
            TmdbMovieContract.Entry.COLUMN_TITLE,
            TmdbMovieContract.Entry.COLUMN_POSTER_PATH,
            TmdbMovieContract.Entry.column_RELEASE_DATE,
            TmdbMovieContract.Entry.column_VOTE_AVERAGE,
    };

    public static final int INDEX_TMDB_ID = 0;
    public static final int INDEX_TMDB_MOVIE_ID = 1;
    public static final int INDEX_TMDB_TITLE = 2;
    public static final int INDEX_TMDB_POSTER_PATH = 3;
    public static final int INDEX_TMDB_RELEASE_DATE = 4;
    public static final int INDEX_TMDB_VOTE_AVERAGE = 5;




    private static final int ID_LOADER_MAIN = 101;
    private static final int ID_LOADER_FAVORITES = 102;


    private SortMethod sortMethodSelected = SortMethod.SORT_POPULAR;
    private static final String EXTRA_SORT_METHOD = "sort_method";

    private RecyclerView rv_gridview;
    private TextView tv_error_box;
    private PicassoGridViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_SORT_METHOD)) {
            sortMethodSelected = SortMethod.values()[savedInstanceState.getInt(EXTRA_SORT_METHOD)];
        }

        mAdapter = new PicassoGridViewAdapter(this, null);
        mAdapter.setClickListener(this);

        rv_gridview = (RecyclerView) findViewById(R.id.rv_gridview);
        tv_error_box = (TextView) findViewById(R.id.tv_error_box);

        int display_mode = getResources().getConfiguration().orientation;

        if (display_mode == Configuration.ORIENTATION_PORTRAIT) {
            rv_gridview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            rv_gridview.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        }

        rv_gridview.setAdapter(mAdapter);


        loadData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_SORT_METHOD, sortMethodSelected.ordinal());
        super.onSaveInstanceState(outState);
    }

    public void onPicassoItemClick(View view, int position) {
        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        TmdbMovie movie = mAdapter.getItem(position);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, (Parcelable)movie);
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
                sortMethodSelected = SortMethod.SORT_POPULAR;
                loadData();
                break;

            case R.id.menu_sort_rate:
                sortMethodSelected = SortMethod.SORT_RATING;
                loadData();
                break;

            case R.id.menu_show_favorites:
                sortMethodSelected = SortMethod.LOCAL_FAVORITES;
                loadData();
                break;

            case android.R.id.home:
                onBackPressed();
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
    public Loader onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {
            case ID_LOADER_MAIN:
                SortMethod method = SortMethod.values()[bundle.getInt(EXTRA_SORT_METHOD)];

                URL url = null;
                switch (method) {
                    case SORT_POPULAR:
                        try {
                            url = NetworkUtils.buildPopularUrl();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case SORT_RATING:
                        try {
                            url = NetworkUtils.buildTopRatedUrl();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        break;
                }

                return new TmdbApiAsyncTaskLoader(this, url.toString());

            case ID_LOADER_FAVORITES:
                Uri queryUri = null;
                String sortOrder = null;
                String selection = null;
                String[] selectionArgs = null;
                /* URI for all rows of weather data in our weather table */
                queryUri = TmdbMovieContract.Entry.CONTENT_URI;
                /* Sort order: Ascending by date */
                sortOrder = TmdbMovieContract.Entry.COLUMN_MOVIE_ID + " DESC";

                return new CursorLoader(this,
                        queryUri,
                        MAIN_TMDB_PROJECTION,
                        selection,
                        selectionArgs,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        int loaderId = loader.getId();

        switch (loaderId) {
            case ID_LOADER_MAIN:
                if (data != null) {
                    showDataGrid();
                    try {
                        mAdapter.setData(TmdbJsonUtils.getTmdbMoviesFromJson((String)data));
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError(PARSE_JSON);
                    }
                } else {
                    showError(PARSE_JSON);
                    //Toast.makeText(MainActivity.this, R.string.error_parse_json, Toast.LENGTH_SHORT).show();
                    Log.e(this.getClass().getSimpleName(), "onPostExecute: parsedData is null");
                }
                break;

            case ID_LOADER_FAVORITES:
                Cursor cursor = (Cursor)data;
                List< TmdbMovie > movies = new ArrayList<>();
                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    TmdbMovie movie = new TmdbMovie();
                    movie.id = cursor.getInt(INDEX_TMDB_MOVIE_ID);
                    movie.title = cursor.getString(INDEX_TMDB_TITLE);
                    movie.posterPath = cursor.getString(INDEX_TMDB_POSTER_PATH);
                    Date date = new Date(cursor.getLong(INDEX_TMDB_RELEASE_DATE));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    movie.releaseDate = sdf.format(date);
                    movie.voteAverage = cursor.getDouble(INDEX_TMDB_VOTE_AVERAGE);
                    movies.add(movie);
                }

                if (data != null) {
                    showDataGrid();
                    try {
                        mAdapter.setData(movies);
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError(PARSE_JSON);
                    }
                } else {
                    showError(PARSE_JSON);
                    Log.e(this.getClass().getSimpleName(), "onPostExecute: parsedData is null");
                }


                break;

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }


    private void loadData() {
        LoaderManager loaderManager = getSupportLoaderManager();
        switch (sortMethodSelected) {
            case SORT_POPULAR:
            case SORT_RATING:

                if(!NetworkUtils.isOnline(this)) {
                    showError(NO_CONNECTION);
                    return;
                }

                Bundle loaderBundle = new Bundle();
                loaderBundle.putInt(EXTRA_SORT_METHOD, sortMethodSelected.ordinal());

                Loader mainLoader = loaderManager.getLoader(ID_LOADER_MAIN);
                if (mainLoader == null) {
                    loaderManager.initLoader(ID_LOADER_MAIN, loaderBundle, this);
                } else {
                    loaderManager.restartLoader(ID_LOADER_MAIN, loaderBundle, this);
                }
                break;

            case LOCAL_FAVORITES:
                Loader favoritesLoader = loaderManager.getLoader(ID_LOADER_FAVORITES);
                if (favoritesLoader == null) {
                    loaderManager.initLoader(ID_LOADER_FAVORITES, null, this);
                } else {
                    loaderManager.restartLoader(ID_LOADER_FAVORITES, null, this);
                }
        }

    }

}

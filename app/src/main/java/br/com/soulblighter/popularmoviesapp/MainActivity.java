package br.com.soulblighter.popularmoviesapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import br.com.soulblighter.popularmoviesapp.databinding.ActivityMainBinding;
import br.com.soulblighter.popularmoviesapp.helper.PicassoGridViewAdapter;
import br.com.soulblighter.popularmoviesapp.json.TmdbMovie;
import br.com.soulblighter.popularmoviesapp.json.TmdbMovieResp;
import br.com.soulblighter.popularmoviesapp.helper.NetworkUtils;
import br.com.soulblighter.popularmoviesapp.provider.TmdbMovieContract;
import br.com.soulblighter.popularmoviesapp.retrofit.RetrofitComponent;
import br.com.soulblighter.popularmoviesapp.retrofit.TmdbService;
import dagger.android.AndroidInjection;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static br.com.soulblighter.popularmoviesapp.MainActivity.ErrorType.NO_CONNECTION;

public class MainActivity extends AppCompatActivity
        implements PicassoGridViewAdapter.PicassoClickListener {

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

    private TmdbDisplayType mTmdbDisplayTypeSelected = TmdbDisplayType
            .SORT_POPULAR;
    private static final String EXTRA_DISPLAY_TYPE = "display_type";
    private static final String EXTRA_GRID_SATE = "gridstate";

    private ActivityMainBinding mBinding;
    private PicassoGridViewAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private Parcelable mGridState;

    @Inject
    public TmdbService mTmdbService;

    private RetrofitComponent component;

    private DisposableSingleObserver<TmdbMovieResp> mFavoritesDisposable = null;
    private DisposableSingleObserver<TmdbMovieResp> mPopularDisposable = null;
    private DisposableSingleObserver<TmdbMovieResp> mTopRatedDisposable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((PopularMoviesApp) getApplication())
                .getMyComponent()
                .inject(this);

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
    protected void onDestroy() {
        super.onDestroy();
        if(mFavoritesDisposable != null && !mFavoritesDisposable.isDisposed()) {
            mFavoritesDisposable.dispose();
        }
        if(mPopularDisposable != null && !mPopularDisposable.isDisposed()) {
            mPopularDisposable.dispose();
        }
        if(mTopRatedDisposable != null && !mTopRatedDisposable.isDisposed()) {
            mTopRatedDisposable.dispose();
        }
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

    private void showError(String error) {
        mBinding.recyclerGridView.setVisibility(View.GONE);
        mBinding.errorBox.setVisibility(View.VISIBLE);
        mBinding.errorBox.setText(error);
    }

    private void updateData(List<TmdbMovie> data) {
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
        if (mGridState != null) {
            mBinding.recyclerGridView.getLayoutManager()
                    .onRestoreInstanceState(mGridState);
        }
    }

    private void loadData() {
        switch (mTmdbDisplayTypeSelected) {
            case SORT_RATING:
                if (!NetworkUtils.isOnline(this)) {
                    showError(NO_CONNECTION);
                    return;
                }

                if(mTopRatedDisposable != null && !mTopRatedDisposable.isDisposed()) {
                    mTopRatedDisposable.dispose();
                }
                mTopRatedDisposable = getMovieObserver();
                mTmdbService.getMovie(TmdbService.TOP_RATED_PATH, BuildConfig.API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getMovieObserver());
                break;

            case SORT_POPULAR:
                if (!NetworkUtils.isOnline(this)) {
                    showError(NO_CONNECTION);
                    return;
                }

                if(mPopularDisposable != null && !mPopularDisposable.isDisposed()) {
                    mPopularDisposable.dispose();
                }
                mPopularDisposable = getMovieObserver();
                mTmdbService.getMovie(TmdbService.POPULAR_PATH, BuildConfig.API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mPopularDisposable);
                break;

            case LOCAL_FAVORITES:

                if(mFavoritesDisposable != null && !mFavoritesDisposable.isDisposed()) {
                    mFavoritesDisposable.dispose();
                }
                mFavoritesDisposable = getMovieObserver();
                Single.fromCallable(() -> {
                    // URI for all rows of weather data in our weather table
                    Uri queryUri = TmdbMovieContract.Entry.CONTENT_URI;
                    // Sort order: Ascending by date
                    String sortOrder = TmdbMovieContract.Entry.MOVIE_ID +
                            " DESC";

                    Cursor cursor = getContentResolver().query( queryUri,
                            MAIN_TMDB_PROJECTION,
                            null,
                            null,
                            sortOrder);

                    TmdbMovieResp tmdbMovieResp = new TmdbMovieResp();
                    List<TmdbMovie> movies = null;
                    if (cursor != null) {
                        movies = new ArrayList<>();
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
                                    ("yyyy-MM-dd", Locale.ENGLISH);
                            movie.releaseDate = sdf.format(date);
                            movie.voteAverage = cursor.getDouble
                                    (INDEX_TMDB_VOTE_AVERAGE);
                            movie.overview = cursor.getString(INDEX_TMDB_OVERVIEW);
                            movies.add(movie);
                        }
                        tmdbMovieResp.results = movies;
                    }
                    return tmdbMovieResp;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mFavoritesDisposable);
        }
    }

    DisposableSingleObserver<TmdbMovieResp> getMovieObserver() {
        return new DisposableSingleObserver<TmdbMovieResp>() {
            @Override
            public void onSuccess(TmdbMovieResp movies) {
                showDataGrid();
                updateData(movies.results);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                showError(e.getLocalizedMessage());
            }
        };
    }

}

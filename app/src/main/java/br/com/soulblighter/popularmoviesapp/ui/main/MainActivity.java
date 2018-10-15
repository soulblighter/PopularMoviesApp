package br.com.soulblighter.popularmoviesapp.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
//import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import br.com.soulblighter.popularmoviesapp.BuildConfig;
import br.com.soulblighter.popularmoviesapp.PopularMoviesApp;
import br.com.soulblighter.popularmoviesapp.R;
import br.com.soulblighter.popularmoviesapp.model.TmdbMovie;
import br.com.soulblighter.popularmoviesapp.retrofit.rest.TmdbMovieResp;
//import br.com.soulblighter.popularmoviesapp.databinding.ActivityMainBinding;
import br.com.soulblighter.popularmoviesapp.ui.detail.DetailActivity;
import br.com.soulblighter.popularmoviesapp.helper.NetworkUtils;
import br.com.soulblighter.popularmoviesapp.retrofit.TmdbService;
//import butterknife.BindView;
//import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static br.com.soulblighter.popularmoviesapp.ui.main.MainActivity.ErrorType.NO_CONNECTION;

public class MainActivity extends AppCompatActivity
        implements PicassoGridViewAdapter.PicassoClickListener {

    public enum TmdbDisplayType {
        SORT_POPULAR, SORT_RATING, LOCAL_FAVORITES
    }

    public enum ErrorType {
        NO_CONNECTION, PARSE_JSON
    }

    private TmdbDisplayType mTmdbDisplayTypeSelected = TmdbDisplayType.SORT_POPULAR;
    private static final String EXTRA_DISPLAY_TYPE = "display_type";
    private static final String EXTRA_GRID_SATE = "gridstate";

    // Workaround for removing databinding
    static class ActivityMainBinding {
        //@BindView(R.id.recyclerGridView)
        RecyclerView recyclerGridView;

        //@BindView(R.id.errorBox)
        TextView errorBox;

        public ActivityMainBinding(Activity activity) {
            recyclerGridView = activity.findViewById(R.id.recyclerGridView);
            errorBox = activity.findViewById(R.id.errorBox);
        }
    }

    private ActivityMainBinding mBinding;
    private PicassoGridViewAdapter mAdapter;
    private Parcelable mGridState;

    @Inject
    public TmdbService mTmdbService;

    private MainViewModel mMainViewModel;
    Observer<List<TmdbMovie>> mLiveDataObserver;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApp) getApplication())
                .getDaggerRetrofitComponent()
                .inject(this);
        //mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setContentView(R.layout.activity_main);
        mBinding = new ActivityMainBinding(this);
        //ButterKnife.bind(mBinding, this);

        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mLiveDataObserver = getLiveDataObserver();


        if (savedInstanceState != null && savedInstanceState.containsKey
                (EXTRA_DISPLAY_TYPE)) {
            mTmdbDisplayTypeSelected = TmdbDisplayType.values()
                    [savedInstanceState.getInt(EXTRA_DISPLAY_TYPE)];
        }

        mAdapter = new PicassoGridViewAdapter(this, null);
        mAdapter.setClickListener(this);

        int display_mode = getResources().getConfiguration().orientation;

        StaggeredGridLayoutManager mLayoutManager;
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
        if(!mDisposables.isDisposed()) {
            mDisposables.dispose();
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

        //if( mMainViewModel.getAllMovies().hasObservers() ) {
        //    mMainViewModel.getAllMovies().removeObservers(this);
            //mMainViewModel.getAllMovies().removeObserver(mLiveDataObserver);
        //}

        switch (mTmdbDisplayTypeSelected) {
            case SORT_RATING:
                if (!NetworkUtils.isOnline(this)) {
                    showError(NO_CONNECTION);
                    return;
                }

                DisposableSingleObserver<TmdbMovieResp> mTopRatedDisposable =
                        getMovieObserver();
                mTmdbService.getMovie(TmdbService.TOP_RATED_PATH, BuildConfig.API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getMovieObserver());
                mDisposables.add(mTopRatedDisposable);
                break;

            case SORT_POPULAR:
                if (!NetworkUtils.isOnline(this)) {
                    showError(NO_CONNECTION);
                    return;
                }

                DisposableSingleObserver<TmdbMovieResp> mPopularDisposable =
                        getMovieObserver();
                mTmdbService.getMovie(TmdbService.POPULAR_PATH, BuildConfig.API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mPopularDisposable);
                mDisposables.add(mPopularDisposable);
                break;

            //case LOCAL_FAVORITES:
            //    mMainViewModel.getAllMovies().observe(this, mLiveDataObserver);
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

    Observer<List<TmdbMovie>> getLiveDataObserver() {
        // Update the cached copy of the words in the adapter.
        return this::updateData;
    }

}

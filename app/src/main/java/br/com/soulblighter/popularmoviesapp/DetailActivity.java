package br.com.soulblighter.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.soulblighter.popularmoviesapp.databinding.ActivityDetailBinding;
import br.com.soulblighter.popularmoviesapp.json.TmdbJsonUtils;
import br.com.soulblighter.popularmoviesapp.json.TmdbMovie;
import br.com.soulblighter.popularmoviesapp.json.TmdbReview;
import br.com.soulblighter.popularmoviesapp.json.TmdbTrailer;
import br.com.soulblighter.popularmoviesapp.network.NetworkUtils;
import br.com.soulblighter.popularmoviesapp.network.TmdbApiAsyncTaskLoader;
import br.com.soulblighter.popularmoviesapp.provider.TmdbMovieContract;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String> {

    public static final String EXTRA_MOVIE = "movie";

    public static final String LOADER_PARAM_MOVIE_ID = "movie_id";

    private static final int TRAILERS_LOADER = 22;
    private static final int REVIEWS_LOADER = 33;

    ActivityDetailBinding mBinding;

    // Screen rotation
    boolean mTrailersLoaded = false;
    boolean mReviewsLoaded = false;
    private static final String EXTRA_SCROLL_POS = "SCROLL_POSITION";
    private int[] mScrollPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        final TmdbMovie movie = i.getParcelableExtra(EXTRA_MOVIE);

        mBinding = DataBindingUtil.setContentView(this, R.layout
                .activity_detail);

        mBinding.scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    mBinding.fab.show();
                } else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mBinding.fab.hide();
                }
                return false;
            }
        });

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = df.parse(movie.releaseDate);
            mBinding.tvDate.setText(this.getString(R.string.release_date) + "" +
                    " " + String.valueOf(new SimpleDateFormat("dd/MM/yyyy")
                    .format(date)));
        } catch (ParseException pe) {
            pe.printStackTrace();
            mBinding.tvDate.setText(this.getString(R.string.release_date) + "" +
                    " " + String.valueOf(movie.releaseDate));
        }

        mBinding.tvRating.setText(this.getString(R.string.rating) + " " +
                String.valueOf(movie.voteAverage) + " / 10.0");
        mBinding.tvSummary.setText(String.valueOf(movie.overview));
        mBinding.tvName.setText(String.valueOf(movie.title));

        Picasso.with(this).load(NetworkUtils.buildImageUrl(movie.posterPath))
                .placeholder(R.color.colorPrimary).into(mBinding.ivPoster);

        if(TmdbMovieContract.isFavorite(this, movie)) {
            mBinding.fab.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            mBinding.fab.setImageResource(android.R.drawable.btn_star_big_off);
        }
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TmdbMovieContract.isFavorite(DetailActivity.this, movie)) {
                    TmdbMovieContract.unmarkAsFavorite(DetailActivity.this,
                            movie);
                    mBinding.fab.setImageResource(android.R.drawable.btn_star_big_off);
                    Snackbar.make(mBinding.scrollview,
                            String.format(getString(R.string.unmark_as_favorite),
                                    movie.title),
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    TmdbMovieContract.markAsFavorite(DetailActivity.this,
                            movie);
                    mBinding.fab.setImageResource(android.R.drawable.btn_star_big_on);
                    Snackbar.make(mBinding.scrollview,
                            String.format(getString(R.string.mark_as_favorite),
                                    movie.title),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        Bundle loaderBundle = new Bundle();
        loaderBundle.putLong(LOADER_PARAM_MOVIE_ID, movie.id);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> trailersLoader = loaderManager.getLoader
                (TRAILERS_LOADER);
        if (trailersLoader == null) {
            loaderManager.initLoader(TRAILERS_LOADER, loaderBundle, this);
        } else {
            loaderManager.restartLoader(TRAILERS_LOADER, loaderBundle, this);
        }
        getSupportLoaderManager().initLoader(TRAILERS_LOADER, null, this);


        Loader<String> reviewsLoader = loaderManager.getLoader(REVIEWS_LOADER);
        if (reviewsLoader == null) {
            loaderManager.initLoader(REVIEWS_LOADER, loaderBundle, this);
        } else {
            loaderManager.restartLoader(REVIEWS_LOADER, loaderBundle, this);
        }
        getSupportLoaderManager().initLoader(REVIEWS_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(EXTRA_SCROLL_POS,
            new int[]{ mBinding.scrollview.getScrollX(), mBinding.scrollview
                .getScrollY()});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScrollPosition = savedInstanceState.getIntArray(EXTRA_SCROLL_POS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public Loader<String> onCreateLoader(int loaderId, final Bundle args) {

        long movie_id = args.getLong(LOADER_PARAM_MOVIE_ID);
        switch (loaderId) {
            case TRAILERS_LOADER:

                mBinding.pbTrailers.setVisibility(View.VISIBLE);
                return new TmdbApiAsyncTaskLoader(this, NetworkUtils
                        .buildTrailersUrl(movie_id));

            case REVIEWS_LOADER:

                mBinding.pbReview.setVisibility(View.VISIBLE);
                return new TmdbApiAsyncTaskLoader(this, NetworkUtils
                        .buildReviewsUrl(movie_id));


            default:
                throw new RuntimeException("Loader Not Implemented: " +
                        loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        LayoutInflater vi = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (loader.getId()) {
            case TRAILERS_LOADER:
                mTrailersLoaded = true;

            /* When we finish loading, we want to hide the loading indicator
            from the user. */
                mBinding.pbTrailers.setVisibility(View.GONE);
            /*
             * If the results are null, we assume an error has occurred.
             * There are much more robust
             * methods for checking errors, but we wanted to keep this
             * particular example simple.
             */
                if (null == data) {
                    if (NetworkUtils.isOnline(this)) {
                        showError(getString(R.string.error_parse_json));
                    }
                    mBinding.layoutTrailerList.setVisibility(View.GONE);
                } else {
                    try {
                        List<TmdbTrailer> trailers = TmdbJsonUtils
                                .getTmdbTrailersFromJson(data);
                        if (trailers.size() == 0) {
                            mBinding.layoutTrailerList.setVisibility(View.GONE);
                        }
                        for (final TmdbTrailer trailer : trailers) {
                            if ("YouTube".equals(trailer.site)) {
                                View v = vi.inflate(R.layout.trailer_item,
                                        null);
                                TextView tv_trailer = v.findViewById(R.id
                                        .tv_trailer);
                                tv_trailer.setText(trailer.name);

                                v.setOnClickListener(new View.OnClickListener
                                        () {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent
                                                .ACTION_VIEW, Uri.parse
                                                (NetworkUtils.buildYoutubeUrl
                                                        (trailer.key)));
                                        startActivity(intent);
                                    }
                                });

                                mBinding.layoutTrailerList.addView(v);
                            }
                        }
                    } catch (JSONException e) {
                        if (NetworkUtils.isOnline(this)) {
                            e.printStackTrace();
                            showError(getString(R.string.error_parse_json));
                        }
                        mBinding.layoutTrailerList.setVisibility(View.GONE);
                    }
                }

                break;

            case REVIEWS_LOADER:
                mReviewsLoaded = true;

                mBinding.pbReview.setVisibility(View.GONE);
                if (null == data) {
                    if (NetworkUtils.isOnline(this)) {
                        showError(getString(R.string.error_parse_json));
                    }
                    mBinding.layoutReviewList.setVisibility(View.GONE);
                } else {
                    try {
                        List<TmdbReview> reviews = TmdbJsonUtils
                                .getTmdbReviewsFromJson(data);
                        if (reviews.size() == 0) {
                            mBinding.layoutReviewList.setVisibility(View.GONE);
                        }
                        for (TmdbReview review : reviews) {

                            View v = vi.inflate(R.layout.review_item, null);
                            TextView tv_author = v.findViewById(R.id.tv_author);
                            tv_author.setText(review.author);
                            TextView tv_review_text = v.findViewById(R.id
                                    .tv_review_text);
                            tv_review_text.setText(review.content);

                            mBinding.layoutReviewList.addView(v);
                        }
                    } catch (JSONException e) {
                        if (NetworkUtils.isOnline(this)) {
                            e.printStackTrace();
                            showError(getString(R.string.error_parse_json));
                        }
                        mBinding.layoutReviewList.setVisibility(View.GONE);
                    }
                }

                break;
        }

        if(mTrailersLoaded && mReviewsLoaded && mScrollPosition != null &&
            mScrollPosition.length == 2) {
            mBinding.scrollview.post(new Runnable() {
                public void run() {
                    mBinding.scrollview.scrollTo(mScrollPosition[0], mScrollPosition[1]);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

}

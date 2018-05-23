package br.com.soulblighter.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import br.com.soulblighter.popularmoviesapp.databinding.ActivityDetailBinding;
import br.com.soulblighter.popularmoviesapp.json.TmdbMovie;
import br.com.soulblighter.popularmoviesapp.json.TmdbReview;
import br.com.soulblighter.popularmoviesapp.json.TmdbReviewResp;
import br.com.soulblighter.popularmoviesapp.json.TmdbTrailer;
import br.com.soulblighter.popularmoviesapp.json.TmdbTrailerResp;
import br.com.soulblighter.popularmoviesapp.helper.NetworkUtils;
import br.com.soulblighter.popularmoviesapp.provider.TmdbMovieContract;
import br.com.soulblighter.popularmoviesapp.retrofit.TmdbService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "movie";

    // Screen rotation
    boolean mTrailersLoaded = false;
    boolean mReviewsLoaded = false;
    private static final String EXTRA_SCROLL_POS = "SCROLL_POSITION";
    private int[] mScrollPosition;

    ActivityDetailBinding mBinding;

    @Inject
    public TmdbService mTmdbService;

    private DisposableSingleObserver<TmdbReviewResp> mReviewsDisposable = null;
    private DisposableSingleObserver<TmdbTrailerResp> mTrailersDisposable = null;

    LayoutInflater vi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ((PopularMoviesApp) getApplication())
                .getMyComponent()
                .inject(this);

        Intent i = getIntent();
        final TmdbMovie movie = i.getParcelableExtra(EXTRA_MOVIE);

        mBinding = DataBindingUtil.setContentView(this, R.layout
                .activity_detail);

        mBinding.scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mBinding.fab.show();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
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

        if (TmdbMovieContract.isFavorite(this, movie)) {
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

        vi = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBinding.pbReview.setVisibility(View.VISIBLE);

        if(mReviewsDisposable != null && !mReviewsDisposable.isDisposed()) {
            mReviewsDisposable.dispose();
        }
        mReviewsDisposable = getReviewObserver();
        mTmdbService.getReviews(String.valueOf(movie.id), BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mReviewsDisposable);

        mBinding.pbTrailers.setVisibility(View.VISIBLE);

        if(mTrailersDisposable != null && !mTrailersDisposable.isDisposed()) {
            mTrailersDisposable.dispose();
        }
        mTrailersDisposable = getTrailersObserver();
        mTmdbService.getTrailers(String.valueOf(movie.id), BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mTrailersDisposable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTrailersDisposable != null && !mTrailersDisposable.isDisposed()) {
            mTrailersDisposable.dispose();
        }
        if(mReviewsDisposable != null && !mReviewsDisposable.isDisposed()) {
            mReviewsDisposable.dispose();
        }
    }

    DisposableSingleObserver<TmdbTrailerResp> getTrailersObserver() {
        return new DisposableSingleObserver<TmdbTrailerResp>() {
            @Override
            public void onSuccess(TmdbTrailerResp trailers) {
                mBinding.pbTrailers.setVisibility(View.GONE);
                if (trailers.results.size() == 0) {
                    mBinding.layoutTrailerList.setVisibility(View.GONE);
                }
                for (final TmdbTrailer trailer : trailers.results) {
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
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                showError(e.getLocalizedMessage());
                mBinding.layoutTrailerList.setVisibility(View.GONE);
            }
        };
    }

    DisposableSingleObserver<TmdbReviewResp> getReviewObserver() {
        return new DisposableSingleObserver<TmdbReviewResp>() {
            @Override
            public void onSuccess(TmdbReviewResp reviews) {
                mBinding.pbReview.setVisibility(View.GONE);
                if (reviews.results.size() == 0) {
                    mBinding.layoutReviewList.setVisibility(View.GONE);
                }
                for (TmdbReview review : reviews.results) {

                    View v = vi.inflate(R.layout.review_item, null);
                    TextView tv_author = v.findViewById(R.id.tv_author);
                    tv_author.setText(review.author);
                    TextView tv_review_text = v.findViewById(R.id
                            .tv_review_text);
                    tv_review_text.setText(review.content);

                    mBinding.layoutReviewList.addView(v);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                showError(e.getLocalizedMessage());
                mBinding.layoutTrailerList.setVisibility(View.GONE);
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(EXTRA_SCROLL_POS,
                new int[]{mBinding.scrollview.getScrollX(), mBinding.scrollview
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

}

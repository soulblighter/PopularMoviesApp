package br.com.soulblighter.popularmoviesapp.ui.detail;

import android.annotation.SuppressLint;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import br.com.soulblighter.popularmoviesapp.BuildConfig;
import br.com.soulblighter.popularmoviesapp.PopularMoviesApp;
import br.com.soulblighter.popularmoviesapp.R;
//import br.com.soulblighter.popularmoviesapp.databinding.ActivityDetailBinding;
import br.com.soulblighter.popularmoviesapp.helper.NetworkUtils;
import br.com.soulblighter.popularmoviesapp.model.TmdbMovie;
import br.com.soulblighter.popularmoviesapp.model.TmdbReview;
import br.com.soulblighter.popularmoviesapp.model.TmdbTrailer;
import br.com.soulblighter.popularmoviesapp.retrofit.TmdbService;
import br.com.soulblighter.popularmoviesapp.retrofit.rest.TmdbReviewResp;
import br.com.soulblighter.popularmoviesapp.retrofit.rest.TmdbTrailerResp;
import br.com.soulblighter.popularmoviesapp.ui.main.MainActivity;
import br.com.soulblighter.popularmoviesapp.ui.main.MainViewModel;
//import butterknife.BindView;
//import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "movie";

    // Screen rotation
    //boolean mTrailersLoaded = false;
    //boolean mReviewsLoaded = false;
    //private static final String EXTRA_SCROLL_POS = "SCROLL_POSITION";
    //private int[] mScrollPosition;

    // Workaround for swapping databinding to butterknife
    static class ActivityDetailBinding {
        //@BindView(R.id.scrollview)
        NestedScrollView scrollview;

        //@BindView(R.id.fab)
        FloatingActionButton fab;

        //@BindView(R.id.tv_date)
        TextView tvDate;

        //@BindView(R.id.tv_rating)
        TextView tvRating;

        //@BindView(R.id.tv_summary)
        TextView tvSummary;

        //@BindView(R.id.tv_name)
        TextView tvName;

        //@BindView(R.id.iv_poster)
        ImageView ivPoster;

        //@BindView(R.id.pb_trailers)
        ProgressBar pbTrailers;

        //@BindView(R.id.pb_review)
        ProgressBar pbReview;

        //@BindView(R.id.layout_trailer_list)
        LinearLayout layoutTrailerList;

        //@BindView(R.id.layout_review_list)
        LinearLayout layoutReviewList;

        public ActivityDetailBinding(Activity activity) {
            scrollview = activity.findViewById(R.id.scrollview);
            fab = activity.findViewById(R.id.fab);
            tvDate = activity.findViewById(R.id.tv_date);
            tvRating = activity.findViewById(R.id.tv_rating);
            tvSummary = activity.findViewById(R.id.tv_summary);
            tvName = activity.findViewById(R.id.tv_name);
            ivPoster = activity.findViewById(R.id.iv_poster);
            pbTrailers = activity.findViewById(R.id.pb_trailers);
            pbReview = activity.findViewById(R.id.pb_review);
            layoutTrailerList = activity.findViewById(R.id.layout_trailer_list);
            layoutReviewList = activity.findViewById(R.id.layout_review_list);
        }
    }
    ActivityDetailBinding mBinding;

    @Inject
    public TmdbService mTmdbService;

    private MainViewModel mMainViewModel;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    LayoutInflater vi;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mBinding = new ActivityDetailBinding(this);
        //ButterKnife.bind(mBinding, this);

        ((PopularMoviesApp) getApplication())
                .getDaggerRetrofitComponent()
                .inject(this);

        Intent i = getIntent();
        final TmdbMovie movie = i.getParcelableExtra(EXTRA_MOVIE);

        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        mBinding.scrollview.setOnTouchListener((view, motionEvent) -> {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        mBinding.fab.show();
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        mBinding.fab.hide();
                    }
                    return false;
                }
        );

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = df.parse(movie.releaseDate);
            mBinding.tvDate.setText(this.getString(R.string.release_date,
                    String.valueOf(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                            .format(date))));
        } catch (ParseException pe) {
            pe.printStackTrace();
            mBinding.tvDate.setText(this.getString(R.string.release_date,
                    String.valueOf(movie.releaseDate)));
        }

        mBinding.tvRating.setText(this.getString(R.string.rating,
                String.valueOf(movie.voteAverage)));
        mBinding.tvSummary.setText(String.valueOf(movie.overview));
        mBinding.tvName.setText(String.valueOf(movie.title));

        Picasso.with(this).load(NetworkUtils.buildImageUrl(movie.posterPath))
                .placeholder(R.color.colorPrimary).into(mBinding.ivPoster);

        mDisposables.add(mMainViewModel.isFavorite(movie.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        isFavorite -> mBinding.fab.setImageResource(android.R.drawable.btn_star_big_on),
                        Throwable::printStackTrace,
                        () -> mBinding.fab.setImageResource(android.R.drawable.btn_star_big_off)));

        mBinding.fab.setOnClickListener(view -> {

            mDisposables.add(mMainViewModel.isFavorite(movie.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(isFavorite -> {
                                mMainViewModel.unmarkAsFavorite(movie);
                                mBinding.fab.setImageResource(android.R.drawable
                                        .btn_star_big_off);
                                Snackbar.make(mBinding.scrollview,
                                        String.format(getString(R.string.unmark_as_favorite), movie.title),
                                        Snackbar.LENGTH_SHORT).show();
                            },

                            Throwable::printStackTrace,

                            () -> {
                                mMainViewModel.markAsFavorite(movie);
                                mBinding.fab.setImageResource(android.R.drawable.btn_star_big_on);
                                Snackbar.make(mBinding.scrollview,
                                        String.format(getString(R.string.mark_as_favorite), movie.title),
                                        Snackbar.LENGTH_SHORT).show();
                            }));
        });

        vi = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBinding.pbReview.setVisibility(View.VISIBLE);

        DisposableSingleObserver<TmdbReviewResp> reviewsDisposable = getReviewObserver();
        mTmdbService.getReviews(String.valueOf(movie.id), BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviewsDisposable);
        mDisposables.add(reviewsDisposable);

        mBinding.pbTrailers.setVisibility(View.VISIBLE);

        DisposableSingleObserver<TmdbTrailerResp>
                trailersDisposable = getTrailersObserver();
        mTmdbService.getTrailers(String.valueOf(movie.id), BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trailersDisposable);
        mDisposables.add(trailersDisposable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mDisposables.isDisposed()) {
            mDisposables.dispose();
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
                        View v = vi.inflate(R.layout.trailer_item, null);
                        TextView tv_trailer = v.findViewById(R.id.tv_trailer);
                        tv_trailer.setText(trailer.name);

                        v.setOnClickListener(view -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse
                                    (NetworkUtils.buildYoutubeUrl(trailer.key)));
                            startActivity(intent);
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

    /*
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
    */
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

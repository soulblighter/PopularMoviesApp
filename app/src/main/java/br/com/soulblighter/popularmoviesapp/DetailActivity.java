package br.com.soulblighter.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.MalformedURLException;
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


    ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        final TmdbMovie movie = i.getParcelableExtra(EXTRA_MOVIE);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = df.parse(movie.releaseDate);
            binding.tvDate.setText(this.getString(R.string.release_date) + " " + String.valueOf(new SimpleDateFormat("dd/MM/yyyy").format(date)));
        } catch (ParseException pe) {
            pe.printStackTrace();
            binding.tvDate.setText(this.getString(R.string.release_date) + " " + String.valueOf(movie.releaseDate));
        }

        binding.tvRating.setText(this.getString(R.string.rating) + " " + String.valueOf(movie.voteAverage) + " / 10.0");
        binding.tvSummary.setText(String.valueOf(movie.overview));
        binding.tvName.setText(String.valueOf(movie.title));

        String url;
        try {
            url = NetworkUtils.buildImageUrl(movie.posterPath).toString();
            Picasso.with(this)
                    .load(url)
                    .placeholder(R.color.colorPrimary)
                    .into(binding.ivPoster);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        binding.cbStar.setChecked(TmdbMovieContract.isFavorite(this, movie));
        binding.cbStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !binding.cbStar.isChecked() ) {
                    boolean c = TmdbMovieContract.unmarkAsFavorite(DetailActivity.this, movie);
                    Log.d("julio", "Delete: "+c);
                } else {
                    Uri uri = TmdbMovieContract.markAsFavorite(DetailActivity.this, movie);
                    Log.d("julio", "Inseted: "+uri);
                }
            }
        });


        Bundle loaderBundle = new Bundle();
        loaderBundle.putLong(LOADER_PARAM_MOVIE_ID, movie.id);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> trailersLoader = loaderManager.getLoader(TRAILERS_LOADER);
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

        String url;
        long movie_id = args.getLong(LOADER_PARAM_MOVIE_ID);
        switch (loaderId) {
            case TRAILERS_LOADER:

                binding.pbTrailers.setVisibility(View.VISIBLE);
                try {
                    url = NetworkUtils.buildTrailersUrl(movie_id).toString();
                    return new TmdbApiAsyncTaskLoader(this, url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


            break;
            case REVIEWS_LOADER:

                binding.pbReview.setVisibility(View.VISIBLE);
                try {
                    url = NetworkUtils.buildReviewsUrl(movie_id).toString();
                    return new TmdbApiAsyncTaskLoader(this, url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                break;

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (loader.getId()) {
            case TRAILERS_LOADER:
            /* When we finish loading, we want to hide the loading indicator from the user. */
                binding.pbTrailers.setVisibility(View.GONE);
            /*
             * If the results are null, we assume an error has occurred. There are much more robust
             * methods for checking errors, but we wanted to keep this particular example simple.
             */
                if (null == data) {
                    showError(getString(R.string.error_parse_json));
                    binding.layoutTrailerList.setVisibility(View.GONE);
                } else {
                    try {
                        List<TmdbTrailer> trailers = TmdbJsonUtils.getTmdbTrailersFromJson(data);
                        if(trailers.size() == 0) {
                            binding.layoutTrailerList.setVisibility(View.GONE);
                        }
                        for(final TmdbTrailer trailer : trailers ) {
                            if("YouTube".equals(trailer.site)) {
                                View v = vi.inflate(R.layout.trailer_item, null);
                                TextView tv_trailer = v.findViewById(R.id.tv_trailer);
                                tv_trailer.setText(trailer.name);

                                v.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+trailer.key));
                                        startActivity(intent);
                                    }
                                });

                                binding.layoutTrailerList.addView(v);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showError(getString(R.string.error_parse_json));
                        binding.layoutTrailerList.setVisibility(View.GONE);
                    }
                }

                break;

            case REVIEWS_LOADER:
                binding.pbReview.setVisibility(View.GONE);
                if (null == data) {
                    showError(getString(R.string.error_parse_json));
                    binding.layoutReviewList.setVisibility(View.GONE);
                } else {
                    try {
                        List<TmdbReview> reviews = TmdbJsonUtils.getTmdbReviewsFromJson(data);
                        if(reviews.size() == 0) {
                            binding.layoutReviewList.setVisibility(View.GONE);
                        }
                        for(TmdbReview review  : reviews ) {

                            View v = vi.inflate(R.layout.review_item, null);
                            TextView tv_author = v.findViewById(R.id.tv_author);
                            tv_author.setText(review.author);
                            TextView tv_review_text = v.findViewById(R.id.tv_review_text);
                            tv_review_text.setText(review.content);

                            /*
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+trailer.key));
                                    startActivity(intent);
                                }
                            });*/

                            binding.layoutReviewList.addView(v);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showError(getString(R.string.error_parse_json));
                        binding.layoutReviewList.setVisibility(View.GONE);
                    }
                }

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

}

package br.com.soulblighter.popularmoviesapp.retrofit;

import br.com.soulblighter.popularmoviesapp.data.TmdbMovieResp;
import br.com.soulblighter.popularmoviesapp.data.TmdbReviewResp;
import br.com.soulblighter.popularmoviesapp.data.TmdbTrailerResp;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbService {

    String POPULAR_PATH = "popular";
    String TOP_RATED_PATH = "top_rated";

    // type can be "popular" or "top_rated"
    @GET("movie/{type}")
    Single<TmdbMovieResp> getMovie(@Path("type") String type,
                                   @Query("api_key") String api_key);

    @GET("movie/{movieId}/reviews")
    Single<TmdbReviewResp> getReviews(@Path("movieId") String movieId,
                                      @Query("api_key") String api_key);

    @GET("movie/{movieId}/videos")
    Single<TmdbTrailerResp> getTrailers(@Path("movieId") String movieId,
                                        @Query("api_key") String api_key);
}

package br.com.soulblighter.popularmoviesapp.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.soulblighter.popularmoviesapp.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TmdbRetrofitConfig {

    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String IMAGE_TMDB_URL = "http://image.tmdb.org/t/p/w185/";
    public static final String API_KEY_VALUE = BuildConfig.API_KEY;

    private final Retrofit retrofit;
    private final Gson gson;

    public TmdbRetrofitConfig() {
        gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public TmdbService getTmdbService() {
        return retrofit.create(TmdbService.class);
    }

}

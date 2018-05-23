package br.com.soulblighter.popularmoviesapp.retrofit;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RetrofitModule {

    @Provides
    @Singleton
    TmdbService provideTmdbService() {
        return new TmdbRetrofitConfig().getTmdbService();
    }
}

package br.com.soulblighter.popularmoviesapp;

import android.app.Application;

import br.com.soulblighter.popularmoviesapp.dagger2.AppComponent;
import br.com.soulblighter.popularmoviesapp.dagger2.DaggerAppComponent;
import br.com.soulblighter.popularmoviesapp.dagger2.AppModule;
import br.com.soulblighter.popularmoviesapp.dagger2.NetModule;

public class PopularMoviesApp extends Application {

    private AppComponent mAppComponent;

    public static final String BASE_URL = "http://api.themoviedb.org/3/";

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = createRetrofitComponent();
    }

    public AppComponent getDaggerRetrofitComponent() {
        return mAppComponent;
    }

    private AppComponent createRetrofitComponent() {
        return DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(BASE_URL))
                .build();
    }
}

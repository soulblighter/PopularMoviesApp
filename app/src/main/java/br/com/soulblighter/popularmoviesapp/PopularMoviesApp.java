package br.com.soulblighter.popularmoviesapp;

import android.app.Application;

import br.com.soulblighter.popularmoviesapp.dagger2.AppModule;
import br.com.soulblighter.popularmoviesapp.dagger2.DaggerNetComponent;
import br.com.soulblighter.popularmoviesapp.dagger2.NetComponent;
import br.com.soulblighter.popularmoviesapp.dagger2.NetModule;

public class PopularMoviesApp extends Application {

    private NetComponent mNetComponent;

    public static final String BASE_URL = "http://api.themoviedb.org/3/";

    @Override
    public void onCreate() {
        super.onCreate();
        mNetComponent = createRetrofitComponent();
    }

    public NetComponent getDaggerRetrofitComponent() {
        return mNetComponent;
    }

    private NetComponent createRetrofitComponent() {
        return DaggerNetComponent
                .builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(BASE_URL))
                .build();
    }
}

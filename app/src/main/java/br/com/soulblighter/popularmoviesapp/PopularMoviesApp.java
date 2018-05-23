package br.com.soulblighter.popularmoviesapp;

import android.app.Application;

import br.com.soulblighter.popularmoviesapp.retrofit.DaggerRetrofitComponent;
import br.com.soulblighter.popularmoviesapp.retrofit.RetrofitComponent;
import br.com.soulblighter.popularmoviesapp.retrofit.RetrofitModule;

public class PopularMoviesApp extends Application {
    private RetrofitComponent mMyComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mMyComponent = createMyComponent();
    }

    RetrofitComponent getMyComponent() {
        return mMyComponent;
    }

    private RetrofitComponent createMyComponent() {
        return DaggerRetrofitComponent
                .builder()
                .retrofitModule(new RetrofitModule())
                .build();
    }
}

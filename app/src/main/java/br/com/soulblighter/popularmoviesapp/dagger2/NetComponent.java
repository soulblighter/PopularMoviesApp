package br.com.soulblighter.popularmoviesapp.dagger2;

import android.app.Application;

import javax.inject.Singleton;

import br.com.soulblighter.popularmoviesapp.ui.detail.DetailActivity;
import br.com.soulblighter.popularmoviesapp.ui.main.MainActivity;
import dagger.Component;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    // downstream components need these exposed with the return type
    // method name does not really matter
    Retrofit retrofit();
    Application app();

    void inject(MainActivity main);
    void inject(DetailActivity detail);
}

package br.com.soulblighter.popularmoviesapp.dagger2;

import android.app.Application;

import javax.inject.Singleton;

import br.com.soulblighter.popularmoviesapp.ui.detail.DetailActivity;
import br.com.soulblighter.popularmoviesapp.ui.main.MainActivity;
import dagger.Component;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface AppComponent {
    void inject(MainActivity main);
    void inject(DetailActivity detail);
}

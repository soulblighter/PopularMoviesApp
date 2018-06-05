package br.com.soulblighter.popularmoviesapp.retrofit;

import javax.inject.Singleton;

import br.com.soulblighter.popularmoviesapp.detail.DetailActivity;
import br.com.soulblighter.popularmoviesapp.main.MainActivity;
import dagger.Component;

@Singleton
@Component(modules = RetrofitModule.class)
public interface RetrofitComponent {
    void inject(MainActivity main);
    void inject(DetailActivity detail);
}

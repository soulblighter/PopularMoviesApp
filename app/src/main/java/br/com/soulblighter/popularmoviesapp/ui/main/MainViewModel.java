package br.com.soulblighter.popularmoviesapp.ui.main;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import br.com.soulblighter.popularmoviesapp.model.TmdbMovie;
import br.com.soulblighter.popularmoviesapp.room.TmdbRepository;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class MainViewModel extends AndroidViewModel {

    private TmdbRepository mRepository;

    private Maybe<List<TmdbMovie>> mAllWords;

    public MainViewModel(Application application) {
        super(application);
        mRepository = new TmdbRepository(application);
        mAllWords = mRepository.getAllMovies();
    }

    Maybe<List<TmdbMovie>> getAllMovies() { return mAllWords; }

    public Maybe<TmdbMovie> isFavorite(long id) {
        return mRepository.getMovie(id);
    }

    public void markAsFavorite(TmdbMovie word) { mRepository.insert(word); }

    public void unmarkAsFavorite(TmdbMovie word) { mRepository.delete(word); }
}

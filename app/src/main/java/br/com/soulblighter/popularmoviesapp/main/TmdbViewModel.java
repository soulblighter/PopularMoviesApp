package br.com.soulblighter.popularmoviesapp.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import br.com.soulblighter.popularmoviesapp.data.TmdbMovie;
import br.com.soulblighter.popularmoviesapp.data.TmdbRepository;
import io.reactivex.Maybe;

public class TmdbViewModel extends AndroidViewModel {

    private TmdbRepository mRepository;

    private LiveData<List<TmdbMovie>> mAllWords;

    public TmdbViewModel(Application application) {
        super(application);
        mRepository = new TmdbRepository(application);
        mAllWords = mRepository.getAllMovies();
    }

    LiveData<List<TmdbMovie>> getAllMovies() { return mAllWords; }

    public Maybe<TmdbMovie> isFavorite(long id) {
        return mRepository.getMovie(id);
    }

    public void markAsFavorite(TmdbMovie word) { mRepository.insert(word); }

    public void unmarkAsFavorite(TmdbMovie word) { mRepository.delete(word); }
}

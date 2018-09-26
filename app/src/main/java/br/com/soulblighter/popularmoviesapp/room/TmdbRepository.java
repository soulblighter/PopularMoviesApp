package br.com.soulblighter.popularmoviesapp.room;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import br.com.soulblighter.popularmoviesapp.model.TmdbMovie;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class TmdbRepository {
    private TmdbMovieDao mTmdbMovieDao;
    private Maybe<List<TmdbMovie>> mAllMovies;

    public TmdbRepository(Application application) {
        TmdbDatabase db = TmdbDatabase.getDatabase(application);
        mTmdbMovieDao = db.tmdbMovieDao();
        mAllMovies = mTmdbMovieDao.getAllMovies();
    }

    public Maybe<List<TmdbMovie>> getAllMovies() {
        return mAllMovies;
    }

    public void insert (final TmdbMovie movie) {
        new insertAsyncTask(mTmdbMovieDao).execute(movie);
    }

    public void delete (final TmdbMovie movie) {
        new deleteAsyncTask(mTmdbMovieDao).execute(movie);
    }

    public Maybe<TmdbMovie> getMovie(long id) {
        return mTmdbMovieDao.getMovie(id);
    }

    private static class insertAsyncTask extends AsyncTask<TmdbMovie, Void, Void> {

        private TmdbMovieDao mAsyncTaskDao;

        insertAsyncTask(TmdbMovieDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final TmdbMovie... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<TmdbMovie, Void, Void> {

        private TmdbMovieDao mAsyncTaskDao;

        deleteAsyncTask(TmdbMovieDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final TmdbMovie... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }
}

package br.com.soulblighter.popularmoviesapp.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import br.com.soulblighter.popularmoviesapp.model.TmdbMovie;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface TmdbMovieDao {
    @Query("SELECT * FROM movie ORDER BY title ASC")
    Maybe<List<TmdbMovie>> getAllMovies();

    //@Query("SELECT * FROM movie WHERE uid IN (:userIds)")
    //List<User> loadAllByUserId(int... userIds);

    @Query("SELECT * FROM movie where id = :id LIMIT 1")
    Maybe<TmdbMovie> getMovie(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TmdbMovie movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(TmdbMovie... movies);

    @Delete
    void delete(TmdbMovie movie);
}

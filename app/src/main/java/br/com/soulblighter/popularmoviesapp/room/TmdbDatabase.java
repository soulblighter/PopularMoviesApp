package br.com.soulblighter.popularmoviesapp.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import br.com.soulblighter.popularmoviesapp.model.TmdbMovie;

@Database(entities = {TmdbMovie.class}, version = 2)
@TypeConverters({RoomConverters.class})
public abstract class TmdbDatabase extends RoomDatabase {

    public abstract TmdbMovieDao tmdbMovieDao();


    private static TmdbDatabase INSTANCE;

    public static TmdbDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TmdbDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        TmdbDatabase.class, "tmdb_database")
                        .build();
                }
            }
        }
        return INSTANCE;
    }
}

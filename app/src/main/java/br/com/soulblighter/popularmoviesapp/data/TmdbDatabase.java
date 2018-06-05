package br.com.soulblighter.popularmoviesapp.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {TmdbMovie.class}, version = 2)
@TypeConverters({Converters.class})
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

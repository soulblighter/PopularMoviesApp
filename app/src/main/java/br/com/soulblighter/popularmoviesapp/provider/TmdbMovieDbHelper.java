package br.com.soulblighter.popularmoviesapp.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class TmdbMovieDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "tmdbDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 2;

    private static TmdbMovieDbHelper sInstance;


    // Constructor
    private TmdbMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static TmdbMovieDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static TmdbMovieDbHelper newInstance(Context context) {
        return new TmdbMovieDbHelper(context);
    }



    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "         + TmdbMovieContract.Entry.TABLE_NAME + " (" +
                TmdbMovieContract.Entry._ID                 + " INTEGER PRIMARY KEY, " +
                TmdbMovieContract.Entry.COLUMN_MOVIE_ID     + " LONG NOT NULL UNIQUE ON CONFLICT IGNORE, " +
                TmdbMovieContract.Entry.COLUMN_TITLE        + " TEXT NOT NULL, " +
                TmdbMovieContract.Entry.COLUMN_POSTER_PATH  + " TEXT NOT NULL, " +
                TmdbMovieContract.Entry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                TmdbMovieContract.Entry.COLUMN_RELEASE_DATE + " LONG NOT NULL, " +
                TmdbMovieContract.Entry.COLUMN_OVERVIEW     + " TEXT NOT NULL );";

        db.execSQL(CREATE_TABLE);
    }

    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TmdbMovieContract.Entry.TABLE_NAME);
        onCreate(db);
    }

}

package br.com.soulblighter.popularmoviesapp.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import br.com.soulblighter.popularmoviesapp.json.TmdbMovie;


public class TmdbMovieContract {

    public static final String AUTHORITY = "br.com.soulblighter" +
            ".popularmoviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" +
            AUTHORITY);

    public static final String PATH_ARTICLES = "movie";

    public static final class Entry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ARTICLES).build();

        public static final String TABLE_NAME = "movie";

        public static final String MOVIE_ID = "movie_id";
        public static final String TITLE = "title";
        public static final String POSTER_PATH = "poster_path";
        public static final String BACKDROP_PATH = "backdrop_path";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String RELEASE_DATE = "release_Date";
        public static final String OVERVIEW = "overview";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        MOVIE_ID + " LONG NOT NULL UNIQUE ON CONFLICT IGNORE, " +
                        TITLE + " TEXT NOT NULL, " +
                        POSTER_PATH + " TEXT NOT NULL, " +
                        BACKDROP_PATH + " TEXT, " +
                        VOTE_AVERAGE + " REAL NOT NULL, " +
                        RELEASE_DATE + " LONG NOT NULL, " +
                        OVERVIEW + " TEXT NOT NULL );";

        public static Uri buildUriWithTableId(long id) {
            return CONTENT_URI.buildUpon().appendPath(_ID).appendPath(Long
                    .toString(id)).build();
        }

        public static Uri buildUriWithMovieId(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id))
                    .build();
        }
    }

    static public Uri markAsFavorite(Context context, TmdbMovie movie) {
        ContentValues values = new ContentValues();
        values.put(Entry.MOVIE_ID, movie.id);
        values.put(Entry.TITLE, movie.title);
        values.put(Entry.POSTER_PATH, movie.posterPath);
        values.put(Entry.BACKDROP_PATH, movie.backdropPath);
        values.put(Entry.VOTE_AVERAGE, movie.voteAverage);
        values.put(Entry.RELEASE_DATE, movie.releaseDate);
        values.put(Entry.OVERVIEW, movie.overview);

        return context.getContentResolver().insert(Entry.CONTENT_URI, values);
    }

    static public boolean unmarkAsFavorite(Context context, TmdbMovie movie) {
        return context.getContentResolver().delete(Entry.buildUriWithMovieId
                (movie.id), null, null) > 0;
    }

    static public boolean isFavorite(Context context, TmdbMovie movie) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(Entry.buildUriWithMovieId
                    (movie.id), null, null, null, null);
            return c.getCount() > 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
}

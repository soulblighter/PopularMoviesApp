package br.com.soulblighter.popularmoviesapp.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import br.com.soulblighter.popularmoviesapp.json.TmdbMovie;


public class TmdbMovieContract {

    public static final String AUTHORITY = "br.com.soulblighter.popularmoviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_ARTICLES = "movie";


    public static final class Entry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTICLES).build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String column_VOTE_AVERAGE = "vote_average";
        public static final String column_RELEASE_DATE = "release_Date";

        public static Uri buildUriWithTableId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(_ID)
                    .appendPath(Long.toString(id))
                    .build();
        }

        public static Uri buildUriWithMovieId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }

    static public Uri markAsFavorite(Context context, TmdbMovie movie) {
        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_MOVIE_ID, movie.id);
        values.put(Entry.COLUMN_TITLE, movie.title);
        values.put(Entry.COLUMN_POSTER_PATH, movie.posterPath);
        values.put(Entry.column_VOTE_AVERAGE, movie.voteAverage);
        values.put(Entry.column_RELEASE_DATE, movie.releaseDate);

        return context.getContentResolver().insert(Entry.CONTENT_URI, values);
    }

    static public boolean unmarkAsFavorite(Context context, TmdbMovie movie) {
        return context.getContentResolver().delete(Entry.buildUriWithMovieId(movie.id), null, null) > 0;
    }

    static public boolean isFavorite(Context context, TmdbMovie movie) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(Entry.buildUriWithMovieId(movie.id), null, null, null, null);
            return c.getCount() > 0;
        } finally {
            if( c != null ) {
                c.close();
            }
        }
    }

/*
        public static final String COLUMN_VOTE_COUNT = "voteCount";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "originalLanguage";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
            instance.video = ((boolean) in.readValue((boolean.class.getClassLoader())));
            instance.originalLanguage = ((String) in.readValue((String.class.getClassLoader())));
            instance.originalTitle = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.genreIds, (java.lang.Integer.class.getClassLoader()));
            instance.backdropPath = ((String) in.readValue((String.class.getClassLoader())));
            instance.adult = ((boolean) in.readValue((boolean.class.getClassLoader())));
            instance.overview = ((String) in.readValue((String.class.getClassLoader())));
 */


}

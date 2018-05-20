package br.com.soulblighter.popularmoviesapp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class TmdbMovieContentProvider extends ContentProvider {
    public static final int URI_MATCH_ARTICLE = 100;
    public static final int URI_MATCH_ARTICLE_WITH_ID = 101;
    public static final int URI_MATCH_ARTICLE_WITH_MOVIE_ID = 102;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to
        // the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding
          match with addURI.
          The two calls below add matches for the Article directory and a
          single item by ID.
         */
        uriMatcher.addURI(TmdbMovieContract.AUTHORITY, TmdbMovieContract
                .PATH_ARTICLES, URI_MATCH_ARTICLE);
        uriMatcher.addURI(TmdbMovieContract.AUTHORITY, TmdbMovieContract
                        .PATH_ARTICLES + "/" + TmdbMovieContract.Entry._ID + "/#",
                URI_MATCH_ARTICLE_WITH_ID);
        uriMatcher.addURI(TmdbMovieContract.AUTHORITY, TmdbMovieContract
                .PATH_ARTICLES + "/#", URI_MATCH_ARTICLE_WITH_MOVIE_ID);

        return uriMatcher;
    }

    private TmdbMovieDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        // Complete onCreate() and initialize a ArticleDbhelper on startup
        // [Hint] Declare the DbHelper as a global variable

        Context context = getContext();
        mDbHelper = TmdbMovieDbHelper.getInstance(context);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Get access to the Article database (to write new data to)
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the Articles
        // directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case URI_MATCH_ARTICLE:
                // Insert new values into the database
                // Inserting values into Articles table
                long id = db.insert(TmdbMovieContract.Entry.TABLE_NAME, null,
                        values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TmdbMovieContract
                            .Entry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert" +
                            " row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case
            // for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the
        // newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of
        // data)
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case URI_MATCH_ARTICLE:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TmdbMovieContract.Entry
                                .TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable
            String[] strings) {

        // Get access to the database and write URI matching code to
        // recognize a single item
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in
            // the URI path
            case URI_MATCH_ARTICLE_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getLastPathSegment();
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(TmdbMovieContract.Entry.TABLE_NAME,
                        TmdbMovieContract.Entry._ID + "=?", new String[]{id});
                break;

            case URI_MATCH_ARTICLE_WITH_MOVIE_ID:
                // Get the task ID from the URI path
                String movie_id = uri.getLastPathSegment();
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(TmdbMovieContract.Entry.TABLE_NAME,
                        TmdbMovieContract.Entry.MOVIE_ID + "=?", new
                                String[]{movie_id});
                break;

            case URI_MATCH_ARTICLE:
                // Delete all
                tasksDeleted = db.delete(TmdbMovieContract.Entry.TABLE_NAME,
                        null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items
        // deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return tasksDeleted;
    }

    // Implement query to handle requests for data by URI
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String
            selection, String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        String[] selectionArguments;

        // Query for the Articles directory and write a default case
        switch (match) {
            // Query for the Articles directory
            case URI_MATCH_ARTICLE:
                retCursor = db.query(TmdbMovieContract.Entry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;

            case URI_MATCH_ARTICLE_WITH_ID:
                String id = uri.getLastPathSegment();
                selectionArguments = new String[]{id};

                retCursor = db.query(
                        /* Table we are going to query */
                        TmdbMovieContract.Entry.TABLE_NAME, projection,
                        TmdbMovieContract.Entry._ID + " = ? ",
                        selectionArguments, null, null, sortOrder);
                break;

            case URI_MATCH_ARTICLE_WITH_MOVIE_ID:
                String movie_id = uri.getLastPathSegment();
                selectionArguments = new String[]{movie_id};

                retCursor = db.query(
                        /* Table we are going to query */
                        TmdbMovieContract.Entry.TABLE_NAME, projection,
                        TmdbMovieContract.Entry.MOVIE_ID + " = ? ",
                        selectionArguments, null, null, sortOrder);
                break;

            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String
            selection, String[] selectionArgs) {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        int tasksUpdated;

        // Query for the Articles directory and write a default case
        switch (match) {
            // Query for the Articles directory
            case URI_MATCH_ARTICLE_WITH_MOVIE_ID:
                String movie_id = uri.getLastPathSegment();
                // Use selections/selectionArgs to filter for this ID
                tasksUpdated = db.update(TmdbMovieContract.Entry.TABLE_NAME,
                        values, TmdbMovieContract.Entry.MOVIE_ID + "=?",
                        new String[]{movie_id});
                break;

            case URI_MATCH_ARTICLE_WITH_ID:
                String id = uri.getLastPathSegment();
                // Use selections/selectionArgs to filter for this ID
                tasksUpdated = db.update(TmdbMovieContract.Entry.TABLE_NAME,
                        values, TmdbMovieContract.Entry._ID + "=?", new
                                String[]{id});
                break;

            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items
        // deleted
        if (tasksUpdated != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the desired Cursor
        return tasksUpdated;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}

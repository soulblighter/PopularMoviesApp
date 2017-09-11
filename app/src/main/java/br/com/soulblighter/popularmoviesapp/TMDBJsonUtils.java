package br.com.soulblighter.popularmoviesapp;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TMDBJsonUtils {

    public final static String TMDB_RESULT = "results";

    public static List< TmdbMovie > getImagesFromJson(Context context, String jsonStr)
            throws JSONException {

        JSONObject json = new JSONObject(jsonStr);
        JSONArray resultsArray = json.getJSONArray(TMDB_RESULT);

        Gson gson = new Gson();
        List< TmdbMovie > movies = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            TmdbMovie movie = gson.fromJson(String.valueOf(resultsArray.getJSONObject(i)), TmdbMovie.class);
            movies.add(movie);
        }

        return movies;
    }

}

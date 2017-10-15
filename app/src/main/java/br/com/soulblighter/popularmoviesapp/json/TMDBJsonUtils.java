package br.com.soulblighter.popularmoviesapp.json;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TmdbJsonUtils {

    public final static String TMDB_RESULT = "results";

    public static List<TmdbMovie> getTmdbMoviesFromJson(String jsonStr)
            throws JSONException {

        JSONObject json = new JSONObject(jsonStr);
        JSONArray resultsArray = json.getJSONArray(TMDB_RESULT);

        Gson gson = new Gson();
        List< TmdbMovie > itens = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            TmdbMovie item = gson.fromJson(String.valueOf(resultsArray.getJSONObject(i)), TmdbMovie.class);
            itens.add(item);
        }

        return itens;
    }

    public static List<TmdbReview> getTmdbReviewsFromJson(String jsonStr)
            throws JSONException {

        JSONObject json = new JSONObject(jsonStr);
        JSONArray resultsArray = json.getJSONArray(TMDB_RESULT);

        Gson gson = new Gson();
        List< TmdbReview > itens = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            TmdbReview item = gson.fromJson(String.valueOf(resultsArray.getJSONObject(i)), TmdbReview.class);
            itens.add(item);
        }

        return itens;
    }

    public static List<TmdbTrailer> getTmdbTrailersFromJson(String jsonStr)
            throws JSONException {

        JSONObject json = new JSONObject(jsonStr);
        JSONArray resultsArray = json.getJSONArray(TMDB_RESULT);

        Gson gson = new Gson();
        List< TmdbTrailer > itens = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            TmdbTrailer item = gson.fromJson(String.valueOf(resultsArray.getJSONObject(i)), TmdbTrailer.class);
            itens.add(item);
        }

        return itens;
    }

}

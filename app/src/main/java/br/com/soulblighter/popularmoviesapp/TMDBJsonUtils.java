package br.com.soulblighter.popularmoviesapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TMDBJsonUtils {

    public final static String TMDB_RESULT = "results";
    public final static String TMDB_POSTER = "poster_path";

    public static Map<Integer,String> getImagesFromJson(Context context, String jsonStr)
            throws JSONException {

        Map<Integer,String> parsedData = new HashMap<>();
        JSONObject json = new JSONObject(jsonStr);
        JSONArray resultsArray = json.getJSONArray(TMDB_RESULT);

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject result = resultsArray.getJSONObject(i);
            parsedData.put(i, result.getString(TMDB_POSTER));
        }

        return parsedData;
    }

}

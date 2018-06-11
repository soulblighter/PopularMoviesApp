package br.com.soulblighter.popularmoviesapp.room;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RoomConverters {

    // Instead of creating a new table to store this data array,
    // we convert the String array to a JSON String using GSON
    // and store it as a simple String
    @TypeConverter
    public static ArrayList<Integer> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Integer> list) {
        return new Gson().toJson(list);
    }
}
